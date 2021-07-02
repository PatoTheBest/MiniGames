package me.patothebest.gamecore.player;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.cosmetics.shop.ShopItem;
import me.patothebest.gamecore.event.player.PlayerDeSelectItemEvent;
import me.patothebest.gamecore.event.player.PlayerJoinPrepareEvent;
import me.patothebest.gamecore.event.player.PlayerLoginPrepareEvent;
import me.patothebest.gamecore.event.player.PlayerSelectItemEvent;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.KitLayout;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.Locale;
import me.patothebest.gamecore.player.modifiers.ExperienceModifier;
import me.patothebest.gamecore.player.modifiers.GeneralModifier;
import me.patothebest.gamecore.player.modifiers.KitModifier;
import me.patothebest.gamecore.player.modifiers.PointsModifier;
import me.patothebest.gamecore.player.modifiers.ShopModifier;
import me.patothebest.gamecore.player.modifiers.TreasureModifier;
import me.patothebest.gamecore.quests.ActiveQuest;
import me.patothebest.gamecore.scoreboard.CoreScoreboardType;
import me.patothebest.gamecore.scoreboard.CustomScoreboard;
import me.patothebest.gamecore.scoreboard.ScoreboardFile;
import me.patothebest.gamecore.scoreboard.ScoreboardType;
import me.patothebest.gamecore.stats.Statistic;
import me.patothebest.gamecore.stats.TrackedStatistic;
import me.patothebest.gamecore.treasure.type.TreasureType;
import me.patothebest.gamecore.util.Callback;
import me.patothebest.gamecore.util.ObservablePlayerImpl;
import me.patothebest.gamecore.util.Sounds;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class CorePlayer extends ObservablePlayerImpl implements IPlayer {

    private final Map<ScoreboardType, CustomScoreboard> scoreboards = new HashMap<>();
    private final ScoreboardFile scoreboardFile;

    // Guice inject
    @Inject private Provider<Economy> economyProvider;
    @Inject private CorePlugin plugin;

    private PlayerIdentity playerIdentity;
    private PlayerInventory playerInventory;
    private Player player;
    protected Locale locale;
    private AbstractArena currentArena;
    private AbstractGameTeam gameTeam;
    private Kit kit;
    private boolean fullyJoined = false;
    private boolean allDataLoaded = false;
    private Location joinLocation;

    private final Map<Class<? extends Statistic>, TrackedStatistic> statistics = new HashMap<>();
    private final Queue<Callback<IPlayer>> callbackQueue = new ConcurrentLinkedQueue<>();

    /**
     * Shop stuff
     * To save resources we unify all the shoppable items (except kits) in
     * a big table, so when we need to add a new shop type (particles, cages,
     * etc.) we don't need to create a new table
     */
    private final Map<Class<? extends ShopItem>, Map<ShopItem, Integer>> ownedItems = new HashMap<>();
    private final Map<Class<? extends ShopItem>, ShopItem> selectedShopItems = new HashMap<>();

    /**
     * The experience from our internal system
     */
    private long experience;

    private int points;

    private int gameKills;
    private int gameDeaths;

    /**
     * Field representing the player id inside the database. This
     * field will not be assigned if a database storage type is not
     * selected.
     */
    private int playerId = -1;

    /**
     * Field representing how many kit uses does a player have. If the kit
     * is permanent, the amount will be -1
     */
    private final Map<Kit, Integer> kitUses = new HashMap<>();

    private final Map<Kit, KitLayout> kitLayouts = new HashMap<>();

    /**
     * Treasure keys map, doesn't matter if the treasure type is disabled or
     * not, it will be loaded into memory
     */
    private final Map<TreasureType, Integer> treasureKeys = new HashMap<>();

    /**
     * Map of all the current quests
     */
    private final Map<String, ActiveQuest> quests = new HashMap<>();

    /**
     * Scoreboard to show on prepare, used in case storage is being reloaded
     * while player is still inside an arena or in the waiting lobby.
     */
    private ScoreboardType scoreboardToShow = CoreScoreboardType.LOBBY;

    @Inject public CorePlayer(ScoreboardFile scoreboardFile, @Assisted Locale defaultLocale) {
        this.locale = defaultLocale;
        this.scoreboardFile = scoreboardFile;
    }

    @Override
    public void loginPrepare() {
        if(player == null) {
            return;
        }

        plugin.getServer().getPluginManager().callEvent(new PlayerLoginPrepareEvent(this));
    }

    @Override
    public void joinPrepare() {
        if(player == null) {
            return;
        }

        plugin.getServer().getPluginManager().callEvent(new PlayerJoinPrepareEvent(this));
        callbackQueue.forEach(corePlayerCallback -> corePlayerCallback.call(this));
        callbackQueue.clear();
        fullyJoined = true;
    }

    @Override
    public void executeWhenFullyLoaded(Callback<IPlayer> corePlayerCallback) {
        if(fullyJoined) {
            corePlayerCallback.call(this);
            return;
        }

        callbackQueue.add(corePlayerCallback);
    }

    @Override
    public boolean isFullyJoined() {
        return fullyJoined;
    }

    @Override
    public boolean isAllDataLoaded() {
        return allDataLoaded;
    }

    public void setAllDataLoaded(boolean allDataLoaded) {
        this.allDataLoaded = allDataLoaded;
    }

    @Override
    public void reinitializeAfterReload() {
        if(player == null || !player.isOnline()) {
            return;
        }

        if (currentArena.isInGame()) {
            getScoreboards().put(CoreScoreboardType.GAME, new CustomScoreboard(plugin, getPlayer(), "game", scoreboardFile.getConfigurationSection("game-scoreboard")));
            setScoreboardToShow(CoreScoreboardType.GAME);
        } else {
            setScoreboardToShow(CoreScoreboardType.WAITING);
        }
    }

    @Override
    public void destroy() {
        scoreboards.forEach((scoreboardType, customScoreboard) -> {
            customScoreboard.cancel();
            customScoreboard.destroy();
        });

        scoreboards.clear();
    }

    private void resetGameStats() {
        gameKills = 0;
        gameDeaths = 0;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;

        notifyObservers(GeneralModifier.LOCALE);
    }

    @Override
    public AbstractArena getCurrentArena() {
        return currentArena;
    }

    @Override
    public void setCurrentArena(AbstractArena currentArena) {
        this.currentArena = currentArena;
        resetGameStats();
    }

    @Override
    public AbstractGameTeam getGameTeam() {
        return gameTeam;
    }

    @Override
    public void setGameTeam(AbstractGameTeam gameTeam) {
        this.gameTeam = gameTeam;
    }

    @Override
    public boolean isInArena() {
        return currentArena != null;
    }

    @Override
    public int getGameKills() {
        return gameKills;
    }

    @Override
    public int getGameDeaths() {
        return gameDeaths;
    }

    public void addGameKills(int gameKills) {
        this.gameKills += gameKills;
    }

    public void addGameDeaths(int gameDeaths) {
        this.gameDeaths += gameDeaths;
    }

    @Override
    public PlayerInventory getPlayerInventory() {
        return playerInventory;
    }

    @Override
    public Kit getKit() {
        return kit;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isPlaying() {
        return isInArena();
    }

    @Override
    public void setKit(Kit kit) {
        this.kit = kit;
    }

    @Override
    public int getPlayerId() {
        return playerId;
    }

    @Override
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public void show(ScoreboardType scoreboardToShow) {
        scoreboards.forEach((scoreboardType, customScoreboard) -> {
            if (!customScoreboard.hasBeenScheduled()) {
                return;
            }

            customScoreboard.cancel();
        });

        if (!scoreboards.containsKey(scoreboardToShow)) {
            return;
        }

        if (!scoreboardToShow.isEnabled()) {
            return;
        }

        scoreboards.get(scoreboardToShow).show();
    }

    @Override
    public void destroy(ScoreboardType scoreboardToShow) {
        scoreboards.get(scoreboardToShow).destroy();
        scoreboards.remove(scoreboardToShow);
    }

    @Override
    public Map<ScoreboardType, CustomScoreboard> getScoreboards() {
        return scoreboards;
    }

    @Override
    public ScoreboardType getScoreboardToShow() {
        return scoreboardToShow;
    }

    @Override
    public void setScoreboardToShow(ScoreboardType scoreboardToShow) {
        this.scoreboardToShow = scoreboardToShow;
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    @Override
    public boolean canUseKit(Kit kit) {
        return kit.isEnabled() && kit.getPermissionGroup().hasPermission(this) && (kit.getCost() < 1.0 || (kitUses.containsKey(kit) && (kitUses.get(kit) > 0 || kitUses.get(kit) == -1)));
    }

    @Override
    public boolean useKit(Kit kit) {
        if (!canUseKit(kit)) {
            return false;
        }

        if(isPermanentKit(kit)) {
            return true;
        }

        return removeKit(kit, 1);
    }

    @Override
    public boolean removeKit(Kit kit) {
        if (!canUseKit(kit)) {
            return false;
        }

        kitUses.remove(kit);
        notifyObservers(KitModifier.REMOVE_KIT, kit);
        return true;
    }

    @Override
    public boolean removeKit(Kit kit, int amount) {
        if (!canUseKit(kit)) {
            return false;
        }

        if(isPermanentKit(kit)) {
            return false;
        }

        int currAmount = kitUses.remove(kit);

        if(currAmount <= 0) {
            return false;
        }

        currAmount-= amount;

        if(currAmount > 0) {
            kitUses.put(kit, currAmount);
            notifyObservers(KitModifier.MODIFY_KIT, kit, currAmount);
        } else {
            notifyObservers(KitModifier.REMOVE_KIT, kit);
        }

        return false;
    }

    @Override
    public Map<Kit, Integer> getKitUses() {
        return ImmutableMap.copyOf(kitUses);
    }

    @Override
    public void buyPermanentKit(Kit kit) {
        if(!kitUses.containsKey(kit)) {
            kitUses.put(kit, -1);
            notifyObservers(KitModifier.ADD_KIT, kit, -1);
            return;
        }

        if(kitUses.get(kit) == -1) {
            return;
        }

        kitUses.remove(kit);
        kitUses.put(kit, -1);
        notifyObservers(KitModifier.MODIFY_KIT, kit, -1);
    }

    @Override
    public void buyKitSingleKitUse(Kit kit) {
        addKitUses(kit, 1);
    }

    @Override
    public void addKitUses(Kit kit, int amount) {
        if(isPermanentKit(kit) || kit.isFree()) {
            return;
        }

        if(kitUses.containsKey(kit)) {
            int newAmount = kitUses.remove(kit) + amount;
            kitUses.put(kit, newAmount);
            notifyObservers(KitModifier.MODIFY_KIT, kit, newAmount);
            return;
        }

        kitUses.put(kit, amount);
        notifyObservers(KitModifier.ADD_KIT, kit, amount);
    }

    @Override
    public void setKitUses(Kit kit, int amount) {
        if (isPermanentKit(kit) || kit.isFree()) {
            return;
        }

        if (kitUses.containsKey(kit)) {
            if (kitUses.get(kit) == amount) {
                return;
            }

            kitUses.put(kit, amount);
            notifyObservers(KitModifier.MODIFY_KIT, kit, amount);
            return;
        }

        kitUses.put(kit, amount);
        notifyObservers(KitModifier.ADD_KIT, kit, amount);
    }

    @Override
    public void modifyKitLayout(Kit kit, KitLayout kitLayout) {
        if (kitLayouts.containsKey(kit)) {
            kitLayouts.put(kit, kitLayout);
            notifyObservers(KitModifier.MODIFY_LAYOUT, kit, kitLayout);
        } else {
            kitLayouts.put(kit, kitLayout);
            notifyObservers(KitModifier.SET_LAYOUT, kit, kitLayout);
        }
    }

    @Override
    public KitLayout getLayout(Kit kit) {
        return kitLayouts.get(kit);
    }

    @Override
    public Map<Kit, KitLayout> getKitLayouts() {
        return kitLayouts;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Type extends ShopItem> Map<Type, Integer> getShopItems(Class<Type> shopItemClass) {
        if(!ownedItems.containsKey(shopItemClass)) {
            ownedItems.put(shopItemClass, new HashMap<>());
        }

        return (Map<Type, Integer>) Collections.unmodifiableMap(ownedItems.get(shopItemClass));
    }

    @Override
    public <Type extends ShopItem> void buyItemUses(Type shopItem, int amount) {
        if(getShopItems(shopItem.getClass()).containsKey(shopItem)) {
            if(!isPermanent(shopItem)) {
                if(amount == -1) {
                    ownedItems.get(shopItem.getClass()).put(shopItem, amount);
                    notifyObservers(ShopModifier.BOUGHT_ITEM_USES, shopItem, amount);
                } else {
                    int oldAmount = ownedItems.get(shopItem.getClass()).get(shopItem);
                    ownedItems.get(shopItem.getClass()).put(shopItem, oldAmount + amount);
                    notifyObservers(ShopModifier.BOUGHT_ITEM_USES, shopItem, oldAmount + amount);
                }
            }
        } else {
            ownedItems.get(shopItem.getClass()).put(shopItem, amount);
            notifyObservers(ShopModifier.BOUGHT_ITEM, shopItem, amount);
        }
    }

    @Override
    public <Type extends ShopItem> boolean useItem(Type shopItem) {
        if (!canUse(shopItem)) {
            return false;
        }

        if(isPermanent(shopItem)) {
            return true;
        }

        int amount = getRemainingUses(shopItem);

        if(amount <= 0) {
            return false;
        }

        return removeItem(shopItem, 1);
    }

    @Override
    public <Type extends ShopItem> boolean removeItem(Type shopItem) {
        if (!canUse(shopItem)) {
            return false;
        }

        ownedItems.get(shopItem.getClass()).remove(shopItem);
        notifyObservers(ShopModifier.DEPLETED_ITEM, shopItem);
        return true;
    }

    @Override
    public <Type extends ShopItem> boolean removeItem(Type shopItem, int amount) {
        if (!canUse(shopItem)) {
            return false;
        }

        if(isPermanent(shopItem)) {
            return false;
        }

        int currAmount = getRemainingUses(shopItem);

        if(currAmount <= 0) {
            return false;
        }

        currAmount-= amount;
        ownedItems.get(shopItem.getClass()).remove(shopItem);

        if(currAmount > 0) {
            ownedItems.get(shopItem.getClass()).put(shopItem, currAmount);
            notifyObservers(ShopModifier.USED_ITEM, shopItem, currAmount);
        } else {
            notifyObservers(ShopModifier.DEPLETED_ITEM, shopItem);
        }
        return false;
    }

    @Override
    public <Type extends ShopItem> boolean setItemAmount(Type shopItem, int amount) {
        if(getShopItems(shopItem.getClass()).containsKey(shopItem)) {
            if(!isPermanent(shopItem)) {
                ownedItems.get(shopItem.getClass()).put(shopItem, amount);
                notifyObservers(ShopModifier.BOUGHT_ITEM_USES, shopItem, amount);
            }
        } else {
            ownedItems.get(shopItem.getClass()).put(shopItem, amount);
            notifyObservers(ShopModifier.BOUGHT_ITEM, shopItem, amount);
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Type extends ShopItem> Type getSelectedItem(Class<Type> shopItemClass) {
        return (Type) selectedShopItems.get(shopItemClass);
    }

    @Override
    public <Type extends ShopItem> void selectItem(Type shopItem) {
        if(selectedShopItems.containsKey(shopItem.getClass())) {
            selectedShopItems.put(shopItem.getClass(), shopItem);
            notifyObservers(ShopModifier.UPDATE_SELECT_DEFAULT, shopItem);
        } else {
            selectedShopItems.put(shopItem.getClass(), shopItem);
            notifyObservers(ShopModifier.SELECT_DEFAULT, shopItem);
        }

        if(!fullyJoined) {
            return;
        }

        plugin.callEvent(new PlayerSelectItemEvent(this, shopItem));
    }

    @Override
    public <Type extends ShopItem> void deSelectItem(Type shopItem) {
        if(!selectedShopItems.containsKey(shopItem.getClass())) {
            return;
        }

        selectedShopItems.remove(shopItem.getClass());
        notifyObservers(ShopModifier.REMOVE_SELECTED_ITEM, shopItem);
        plugin.callEvent(new PlayerDeSelectItemEvent(this, shopItem));
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public Map<TreasureType, Integer> getTreasureKeyMap() {
        return treasureKeys;
    }

    @Override
    public int getKeys(TreasureType treasureType) {
        return treasureKeys.get(treasureType);
    }

    @Override
    public void setKeys(TreasureType treasureType, int amount) {
        treasureKeys.put(treasureType, amount);
        notifyObservers(TreasureModifier.MODIFY, treasureType);
    }

    public @Nullable ActiveQuest getQuest(String name) {
        return quests.get(name);
    }

    public void activateQuest(ActiveQuest quest) {
        this.quests.put(quest.getQuest().getName(), quest);

    }

    @Override
    public long getExperience() {
        return experience;
    }

    @Override
    public void setExperience(long experience) {
        this.experience = experience;
        notifyObservers(ExperienceModifier.SET_EXPERIENCE, experience);
    }

    @Override
    public void addExperience(long experience) {
        this.experience += experience;
        notifyObservers(ExperienceModifier.ADD_EXPERIENCE, experience);

        CoreLang.EXPERIENCE_EARNED.replaceAndSend(player, experience);
    }

    @Override
    public void removeExperience(long experience) {
        this.experience -= experience;
        notifyObservers(ExperienceModifier.REMOVE_EXPERIENCE, experience);
    }

    @Override
    public double getMoney() {
        return economyProvider.get() != null ? economyProvider.get().getBalance(player) : 0;
    }

    @Override
    public boolean giveMoney(double amount) {
        if (economyProvider.get() == null) {
            return false;
        }

        EconomyResponse economyResponse = economyProvider.get().depositPlayer(player, amount);
        if (economyResponse.type != EconomyResponse.ResponseType.SUCCESS) {
            return false;
        }

        CoreLang.MONEY_GIVEN.replaceAndSend(player, amount);
        return true;
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public void setPoints(int newPoints) {
        if(points == newPoints) {
            return;
        }

        this.points = newPoints;
        notifyObservers(PointsModifier.MODIFY);
    }

    @Override
    public Map<Class<? extends Statistic>, TrackedStatistic> getStatistics() {
        return statistics;
    }

    @Override
    public String getName() {
        return player == null ? playerIdentity.getName() : player.getName();
    }

    @Override
    public UUID getUniqueId() {
        return player == null ? playerIdentity.getUuid() : player.getUniqueId();
    }

    @Override
    public void setPlayerIdentity(PlayerIdentity playerIdentity) {
        this.playerIdentity = playerIdentity;
    }

    @Override
    public Location getJoinLocation() {
        return joinLocation;
    }

    @Override
    public void setJoinLocation(Location joinLocation) {
        this.joinLocation = joinLocation;
    }

    /**
     * Sets the player
     */
    public void setPlayer(Player player) {
        if(this.player != null) {
            throw new UnsupportedOperationException("Cannot re-define player object.");
        }

        this.player = player;
        this.playerInventory = new PlayerInventory(player);
    }

    @Override
    public void serialize(Map<String, Object> data) {
        data.put("locale", locale);
        data.put("keys", treasureKeys.entrySet().stream().collect(Collectors.toMap(data1 -> data1.getKey().name(), Map.Entry::getValue)));
    }

    @Override
    public void playSound(Sounds sound) {
        sound.play(player);
    }

    @Override
    public void playSound(Sounds sound, float volume, float pitch) {
        sound.play(player, volume, pitch);
    }

    @Override
    public World getWorld() {
        return player.getWorld();
    }

    @Override
    public int getEntityId() {
        return player.getEntityId();
    }

    @Deprecated // internal use
    public Map<Class<? extends ShopItem>, Map<ShopItem, Integer>> getOwnedItems() {
        return ownedItems;
    }

    @Deprecated // internal use
    public Map<Class<? extends ShopItem>, ShopItem> getSelectedShopItems() {
        return selectedShopItems;
    }
}