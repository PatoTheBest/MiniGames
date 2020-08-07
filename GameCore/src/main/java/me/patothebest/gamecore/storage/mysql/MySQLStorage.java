package me.patothebest.gamecore.storage.mysql;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.lang.CoreLocaleManager;
import me.patothebest.gamecore.timings.TimingsData;
import me.patothebest.gamecore.timings.TimingsManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.KitFactory;
import me.patothebest.gamecore.kit.WrappedPotionEffect;
import me.patothebest.gamecore.logger.InjectParentLogger;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.PlayerFactory;
import me.patothebest.gamecore.player.PlayerIdentity;
import me.patothebest.gamecore.player.modifiers.GeneralModifier;
import me.patothebest.gamecore.player.modifiers.PlayerModifier;
import me.patothebest.gamecore.storage.StorageManager;
import me.patothebest.gamecore.util.Callback;
import me.patothebest.gamecore.util.PlayerObserver;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.PlayerInventory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public final class MySQLStorage implements IMySQLStorage, PlayerObserver {

    private final CorePlugin plugin;
    private final MySQLConnectionHandler connectionHandler;
    private final List<MySQLEntity> syncCoreSQLEntities;
    private final List<MySQLEntity> asyncCoreSQLEntities;
    private final PlayerFactory playerFactory;
    private final StorageManager storageManager;
    private final KitFactory kitFactory;
    private volatile boolean async = false;
    @InjectParentLogger(parent = StorageManager.class) private Logger logger;

    @Inject private MySQLStorage(CorePlugin plugin, CoreConfig coreConfig, StorageManager storageManager, Set<MySQLEntity> coreSQLEntities, PlayerFactory playerFactory, StorageManager storageManager1, KitFactory kitFactory) {
        this.plugin = plugin;
        this.playerFactory = playerFactory;
        this.storageManager = storageManager1;
        this.kitFactory = kitFactory;
        this.connectionHandler = new MySQLConnectionHandler(plugin, coreConfig, storageManager, this);

        syncCoreSQLEntities = Collections.unmodifiableList(coreSQLEntities.stream().filter(entity -> entity.getClass().isAnnotationPresent(LoadSynchronously.class)).collect(Collectors.toList()));
        asyncCoreSQLEntities = Collections.unmodifiableList(coreSQLEntities.stream().filter(entity -> !entity.getClass().isAnnotationPresent(LoadSynchronously.class)).collect(Collectors.toList()));
    }

    @Override
    public void loadPlayers(Callback<CorePlayer> playerCallback) {
        connectionHandler.executeSQLQuery(connection -> {
            PreparedStatement selectUser = connection.prepareStatement(PlayerQueries.SELECT_ALL);
            ResultSet resultSet = selectUser.executeQuery();

            int loadedPlayers = 0;

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                UUID uuid = UUID.fromString(resultSet.getString("UUID"));
                PlayerIdentity playerIdentity = new PlayerIdentity(name, uuid);
                CorePlayer player = playerFactory.create(CoreLocaleManager.DEFAULT_LOCALE);
                player.setPlayerIdentity(playerIdentity);
                load(player, false);
                playerCallback.call(player);
                loadedPlayers++;

                if(loadedPlayers % 1000 == 0) {
                    logger.info("Converted " + loadedPlayers + " players so far");
                }

                if(loadedPlayers % 200 == 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            logger.info("Converted " + loadedPlayers + " players!");
            resultSet.close();
            selectUser.close();
        }, false);
    }

    @Override
    public final void load(CorePlayer player, boolean async) {
        boolean lockThread = !async && plugin.isEnabled() && this.async;
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        connectionHandler.executeSQLQuery(connection -> {
            TimingsData timings = TimingsManager.create("Load " + player.getName());
            PreparedStatement selectUser = connection.prepareStatement(storageManager.isUseUUIDs() ? PlayerQueries.SELECT_UUID : PlayerQueries.SELECT_NAME);
            selectUser.setString(1, storageManager.isUseUUIDs() ? player.getUniqueId().toString() : player.getName());

            ResultSet resultSet = selectUser.executeQuery();
            timings.trackAction("Select user from database");

            if (!resultSet.next()) {
                PreparedStatement createUser = connection.prepareStatement(PlayerQueries.INSERT_RECORD);
                createUser.setString(1, player.getName());
                createUser.setString(2, player.getUniqueId().toString());
                createUser.setString(3, player.getLocale().getName());
                createUser.executeUpdate();
                createUser.close();
                resultSet.close();
                timings.trackAction("Create new user");

                // re-query database to obtain player id
                resultSet = selectUser.executeQuery();
                resultSet.next();
                timings.trackAction("Re-select user from database");
            }

            player.setPlayerId(resultSet.getInt("id"));
            player.setLocale(CoreLocaleManager.getOrDefault(resultSet.getString("locale"), player.getLocale()));

            if (storageManager.isUseUUIDs() && !resultSet.getString("name").equalsIgnoreCase(player.getName())) {
                updatePlayerName(player);
            }

            resultSet.close();
            selectUser.close();

            for (MySQLEntity coreSQLEntity : syncCoreSQLEntities) {
                coreSQLEntity.loadPlayer(player, connection);
                timings.trackAction("Load data from " + coreSQLEntity.getClass().getSimpleName());
            }

            if(lockThread) {
                countDownLatch.countDown();
            }

            for (MySQLEntity coreSQLEntity : asyncCoreSQLEntities) {
                coreSQLEntity.loadPlayer(player, connection);
                timings.trackAction("Load data from " + coreSQLEntity.getClass().getSimpleName());
            }

            player.addObserver(this);
            player.setAllDataLoaded(true);
            timings.stop();
        }, error -> {
            if(countDownLatch.getCount() == 1) {
                countDownLatch.countDown();
            }
        }, plugin.isEnabled() && this.async);

        if(lockThread) {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(CorePlayer player, PlayerModifier modifiedValue, Object... args) {
        connectionHandler.executeSQLQuery(connection -> {
            if(modifiedValue == GeneralModifier.LOCALE) {
                PreparedStatement preparedStatement = connection.prepareStatement(PlayerQueries.UPDATE_LOCALE);
                preparedStatement.setString(1, player.getLocale().getName());
                preparedStatement.setInt(2, player.getPlayerId());
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }

            for (MySQLEntity coreSQLEntity : syncCoreSQLEntities) {
                coreSQLEntity.updatePlayer(player, connection, modifiedValue, args);
            }

            for (MySQLEntity coreSQLEntity : asyncCoreSQLEntities) {
                coreSQLEntity.updatePlayer(player, connection, modifiedValue, args);
            }
        }, plugin.isEnabled() && async);
    }

    @Override
    public final void save(CorePlayer player) {
        connectionHandler.executeSQLQuery(connection -> {
            PreparedStatement updateUser = connection.prepareStatement(PlayerQueries.UPDATE);
            updateUser.setString(1, player.getName());
            updateUser.setString(2, player.getLocale().getName());
            updateUser.setInt(3, player.getPlayerId());

            updateUser.executeUpdate();
            updateUser.close();

            for (MySQLEntity coreSQLEntity : syncCoreSQLEntities) {
                coreSQLEntity.savePlayer(player, connection);
            }

            for (MySQLEntity coreSQLEntity : asyncCoreSQLEntities) {
                coreSQLEntity.savePlayer(player, connection);
            }
        }, plugin.isEnabled() && async);
    }

    private void updatePlayerName(CorePlayer player) {
        executeUpdate(PlayerQueries.UPDATE, preparedStatement -> {
            preparedStatement.setString(1, player.getName());
            preparedStatement.setInt(2, player.getPlayerId());
        });
    }

    private void executeUpdate(String update, SQLCallBack<PreparedStatement> preparedStatementCallback) {
        connectionHandler.executeSQLQuery(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(update);
            preparedStatementCallback.call(preparedStatement);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        }, plugin.isEnabled() && async);
    }

    @Override
    public void unCache(CorePlayer player) {
        player.deleteObservers();
    }

    @Override
    public void delete(CorePlayer player) {
        // TODO: delete player

        unCache(player);
    }

    @Override
    public void loadKits(Map<String, Kit> kitMap) {
        connectionHandler.executeSQLQuery(connection -> {
            PreparedStatement updateUser = connection.prepareStatement(KitQueries.SELECT);
            ResultSet resultSet = updateUser.executeQuery();

            while (resultSet.next()) {
                try {
                    Kit kit = kitFactory.createKit(resultSet);
                    kitMap.put(kit.getKitName(), kit);
                    logger.config("Loaded kit " + kit.getKitName());
                } catch (Throwable t) {
                    logger.log(Level.SEVERE, ChatColor.RED + "Could not load kit " + resultSet.getString("name"), t);
                }
            }
            updateUser.close();
        }, plugin.isEnabled() && async);
    }

    @Override
    public void saveKits(Map<String, Kit> kitMap) {
        if(kitMap.isEmpty()) {
            return;
        }

        connectionHandler.executeSQLQuery(connection -> {
            PreparedStatement insertKit = connection.prepareStatement(KitQueries.INSERT);

            for (Kit kit : kitMap.values()) {
                insertKit.setString(1, kit.getKitName());
                insertKit.setString(2, StringUtils.join(kit.getDescription(), "%delimiter%"));
                insertKit.setString(3, Utils.itemStackToString(kit.getDisplayItem()));
                insertKit.setString(4, Utils.itemStackArrayToBase64(kit.getInventoryItems()));
                insertKit.setString(5, Utils.itemStackArrayToBase64(kit.getArmorItems()));
                insertKit.setString(6, Utils.potionEffectArrayToBase64(kit.getPotionEffects().toArray(new WrappedPotionEffect[kit.getPotionEffects().size()])));
                insertKit.setString(7, kit.getPermissionGroup().getName());
                insertKit.setInt(8, kit.isOneTimeKit() ? 1 : 0);
                insertKit.setDouble(9, kit.getCost());
                insertKit.setInt(10, kit.isEnabled() ? 1 : 0);
                insertKit.addBatch();
            }

            insertKit.executeBatch();
        }, plugin.isEnabled() && async);

    }

    @Override
    public Kit createKit(String name, PlayerInventory playerInventory) {
        Kit kit = kitFactory.createKit(name, playerInventory);
        connectionHandler.executeSQLQuery(connection -> {
            PreparedStatement insertKit = connection.prepareStatement(KitQueries.INSERT);
            insertKit.setString(1, name);
            insertKit.setString(2, StringUtils.join(kit.getDescription(), "%delimiter%"));
            insertKit.setString(3, Utils.itemStackToString(kit.getDisplayItem()));
            insertKit.setString(4, Utils.itemStackArrayToBase64(kit.getInventoryItems()));
            insertKit.setString(5, Utils.itemStackArrayToBase64(kit.getArmorItems()));
            insertKit.setString(6, Utils.potionEffectArrayToBase64(kit.getPotionEffects().toArray(new WrappedPotionEffect[kit.getPotionEffects().size()])));
            insertKit.setString(7, kit.getPermissionGroup().getName());
            insertKit.setInt(8, kit.isOneTimeKit() ? 1 : 0);
            insertKit.setDouble(9, kit.getCost());
            insertKit.setInt(10, kit.isEnabled() ? 1 : 0);
            insertKit.executeUpdate();
        }, plugin.isEnabled() && async);
        return kit;
    }

    @Override
    public void saveKit(Kit kit) {
        connectionHandler.executeSQLQuery(connection -> {
            PreparedStatement insertKit = connection.prepareStatement(KitQueries.UPDATE);
            insertKit.setString(1, StringUtils.join(kit.getDescription(), "%delimiter%"));
            insertKit.setString(2, Utils.itemStackToString(kit.getDisplayItem()));
            insertKit.setString(3, Utils.itemStackArrayToBase64(kit.getInventoryItems()));
            insertKit.setString(4, Utils.itemStackArrayToBase64(kit.getArmorItems()));
            insertKit.setString(5, Utils.potionEffectArrayToBase64(kit.getPotionEffects().toArray(new WrappedPotionEffect[kit.getPotionEffects().size()])));
            insertKit.setString(6, kit.getPermissionGroup().getName());
            insertKit.setInt(7, kit.isOneTimeKit() ? 1 : 0);
            insertKit.setDouble(8, kit.getCost());
            insertKit.setInt(9, kit.isEnabled() ? 1 : 0);
            insertKit.setString(10, kit.getKitName());
            insertKit.executeUpdate();
        }, plugin.isEnabled() && async);
    }

    @Override
    public void deleteKit(Kit kit) {
        connectionHandler.executeSQLQuery(connection -> {
            PreparedStatement insertKit = connection.prepareStatement(KitQueries.DELETE);
            insertKit.setString(1, kit.getKitName());
            insertKit.executeUpdate();
        }, plugin.isEnabled() && async);
    }

    @Override
    public final void enableStorage() {
        connectionHandler.setupHikari();
    }

    @Override
    public void postEnable() {
        async = true;
    }

    @Override
    public final void preDisableStorage() {
        async = false;
    }

    @Override
    public final void disableStorage() {
        connectionHandler.closeHikari();
    }

    @Override
    public List<String> getCreateTableQueries() {
        List<String> statements = new ArrayList<>();
        Consumer<MySQLEntity> consumer = sqlEntity -> {
            statements.addAll(Arrays.asList(sqlEntity.getCreateTableStatements()));
        };

        syncCoreSQLEntities.forEach(consumer);
        asyncCoreSQLEntities.forEach(consumer);
        statements.add(PlayerQueries.CREATE_TABLE);
        statements.add(KitQueries.CREATE_TABLE);
        return statements;
    }

    final CorePlugin getPlugin() {
        return plugin;
    }

    public final MySQLConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    Logger getLogger() {
        return logger;
    }
}
