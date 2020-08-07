package me.patothebest.gamecore.kit.entities;

import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.file.ParserException;
import me.patothebest.gamecore.kit.KitLayout;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.KitManager;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.modifiers.KitModifier;
import me.patothebest.gamecore.player.modifiers.PlayerModifier;
import me.patothebest.gamecore.util.ThrowableBiConsumer;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KitMySQLEntity implements MySQLEntity {

    private final KitManager kitManager;

    @Inject private KitMySQLEntity(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @Override
    public void loadPlayer(CorePlayer player, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(Queries.SELECT_KIT);
        preparedStatement.setInt(1, player.getPlayerId());
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String kitName = resultSet.getString("kit_name");
            Kit kit = kitManager.getKits().get(kitName);
            int kitUses = resultSet.getInt("uses");

            if(kit != null) {
                player.addKitUses(kit, kitUses);
            }
        }

        resultSet.close();
        preparedStatement.close();

        PreparedStatement selectDefaultKit = connection.prepareStatement(Queries.SELECT_DEFAULT_KIT);
        selectDefaultKit.setString(1, PluginConfig.PLUGIN_NAME);
        selectDefaultKit.setInt(2, player.getPlayerId());
        ResultSet selectDefaultKitResultSet = selectDefaultKit.executeQuery();

        if(selectDefaultKitResultSet.next()) {
            player.setKit(kitManager.getKits().get(selectDefaultKitResultSet.getString("kit_name")));
        } else {
            PreparedStatement insertRecord = connection.prepareStatement(Queries.INSERT_DEFAULT_KIT);
            insertRecord.setString(1, PluginConfig.PLUGIN_NAME);
            insertRecord.setInt(2, player.getPlayerId());
            insertRecord.setString(3, "default");
            insertRecord.executeUpdate();
        }

        selectDefaultKitResultSet.close();
        selectDefaultKit.close();

        PreparedStatement selectLayout = connection.prepareCall(Queries.SELECT_LAYOUT);
        selectLayout.setInt(1, player.getPlayerId());
        ResultSet layouts = selectLayout.executeQuery();
        while(layouts.next()) {
            String kitName = layouts.getString("kit_name");
            Kit kit = kitManager.getKits().get(kitName);
            String kitLayout = layouts.getString("layout");
            KitLayout layout = new KitLayout();

            try {
                layout.parseLayout(kitLayout);
            } catch (ParserException e) {
                Utils.printError("Could not load layout!",
                        "PlayerID: " + player.getPlayerId(),
                        "Player: " + player.getName(),
                        "Kit: " + kitName,
                        "Layout: " + layout,
                        e);
            }

            if(kit != null) {
                player.modifyKitLayout(kit, layout);
            }
        }

        layouts.close();
        selectLayout.close();
    }

    @Override
    public void savePlayer(CorePlayer player, Connection connection) throws SQLException {
        PreparedStatement deleteAll = connection.prepareStatement(Queries.DELETE_ALL);
        deleteAll.setInt(1, player.getPlayerId());
        deleteAll.executeUpdate();

        if(!player.getKitUses().isEmpty()) {
            PreparedStatement preparedStatement = connection.prepareStatement(Queries.INSERT_KIT);
            player.getKitUses().forEach((ThrowableBiConsumer<Kit, Integer>) (kit, uses) -> {
                preparedStatement.setInt(1, player.getPlayerId());
                preparedStatement.setString(2, kit.getKitName());
                preparedStatement.setInt(3, uses);
                preparedStatement.executeUpdate();
                preparedStatement.addBatch();
            });

            preparedStatement.executeBatch();
        }
    }

    @Override
    public void updatePlayer(CorePlayer player, Connection connection, PlayerModifier updatedType, Object... args) throws SQLException {
        if(!(updatedType instanceof KitModifier)) {
            return;
        }

        Kit kit = (Kit) args[0];
        switch (((KitModifier)updatedType)) {
            case ADD_KIT: {
                PreparedStatement preparedStatement = connection.prepareStatement(Queries.INSERT_KIT);
                preparedStatement.setInt(1, player.getPlayerId());
                preparedStatement.setString(2, kit.getKitName());
                preparedStatement.setInt(3, (Integer) args[1]);
                preparedStatement.executeUpdate();
                break;
            } case REMOVE_KIT: {
                PreparedStatement preparedStatement = connection.prepareStatement(Queries.DELETE_KIT);
                preparedStatement.setInt(1, player.getPlayerId());
                preparedStatement.setString(2, kit.getKitName());
                preparedStatement.executeUpdate();
                break;
            } case MODIFY_KIT: {
                PreparedStatement preparedStatement = connection.prepareStatement(Queries.UPDATE_KIT);
                preparedStatement.setInt(1, (Integer) args[1]);
                preparedStatement.setInt(2, player.getPlayerId());
                preparedStatement.setString(3, kit.getKitName());
                preparedStatement.executeUpdate();
                break;
            } case SET_LAYOUT: {
                PreparedStatement preparedStatement = connection.prepareStatement(Queries.INSERT_LAYOUT);
                preparedStatement.setInt(1, player.getPlayerId());
                preparedStatement.setString(2, kit.getKitName());
                preparedStatement.setString(3, args[1].toString());
                preparedStatement.executeUpdate();
                break;
            } case MODIFY_LAYOUT: {
                PreparedStatement preparedStatement = connection.prepareStatement(Queries.UPDATE_LAYOUT);
                preparedStatement.setString(1, args[1].toString());
                preparedStatement.setInt(2, player.getPlayerId());
                preparedStatement.setString(3, kit.getKitName());
                preparedStatement.executeUpdate();
                break;
            } case SET_DEFAULT: {
                PreparedStatement preparedStatement = connection.prepareStatement(Queries.UPDATE_DEFAULT_KIT);
                preparedStatement.setString(1, kit.getKitName());
                preparedStatement.setString(2, PluginConfig.PLUGIN_NAME);
                preparedStatement.setInt(3, player.getPlayerId());
                preparedStatement.executeUpdate();
                break;
            }
        }
    }

    @Override
    public String[] getCreateTableStatements() {
        return new String[] {Queries.CREATE_TABLE, Queries.CREATE_DEFAULT_KIT_TABLEE, Queries.CREATE_KITS_LAYOUT_TABLE};
    }
}
