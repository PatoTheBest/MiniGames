package me.patothebest.gamecore.treasure.reward.rewards;

import com.google.inject.Inject;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.KitManager;
import me.patothebest.gamecore.logger.InjectParentLogger;
import me.patothebest.gamecore.logger.Logger;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.treasure.TreasureManager;
import me.patothebest.gamecore.treasure.reward.Reward;
import me.patothebest.gamecore.treasure.reward.RewardData;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class KitReward extends Reward {

    private int minAmount = 1;
    private int maxAmount = 1;
    private final List<Kit> kits = new ArrayList<>();
    private final KitManager kitManager;
    private final PlayerManager playerManager;
    @InjectParentLogger(parent = TreasureManager.class) private Logger logger;

    @Inject private KitReward(KitManager kitManager, PlayerManager playerManager) {
        this.kitManager = kitManager;
        this.playerManager = playerManager;
        hologramMessage = "%amount%Kit %kit%";
        displayItem = new ItemStackBuilder(Material.BARRIER);
    }

    @Override
    public boolean parse(ConfigurationSection configurationSection) {
        boolean parsedSuper = super.parse(configurationSection);
        boolean parsed = false;

        if (configurationSection.isSet("kit")) {
            Kit kit = kitManager.getKits().get(configurationSection.getString("kit"));

            if(kit != null) {
                kits.add(kit);
                parsed = true;
            } else {
                logger.severe("Reward {0} tried to register kit {1} that doesn't exist.", configurationSection.getString("kit"), configurationSection.getCurrentPath());
            }
        }

        if (configurationSection.isSet("kits")) {
            for (String kitName : configurationSection.getStringList("kits")) {
                Kit kit = kitManager.getKits().get(kitName);

                if(kit != null) {
                    kits.add(kit);
                    parsed = true;
                } else {
                    logger.severe("Reward {0} tried to register kit {1} that doesn't exist.", kitName, configurationSection.getCurrentPath());
                }
            }
        }

        if (configurationSection.isSet("min-amount")) {
            minAmount = configurationSection.getInt("min-amount");
        } else {
            parsed = false;
            logger.severe("Reward {0} is missing min-amount!", configurationSection.getCurrentPath());
        }

        if (configurationSection.isSet("max-amount")) {
            maxAmount = configurationSection.getInt("max-amount");
        } else {
            parsed = false;
            logger.severe("Reward {0} is missing max-amount!", configurationSection.getCurrentPath());
        }

        return parsed && parsedSuper;
    }

    @Override
    public boolean canGiveReward(IPlayer player) {
        List<Kit> kitsForPlayer = new ArrayList<>(kits);
        kitsForPlayer.removeIf(kit -> !kit.isOneTimeKit() && player.canUseKit(kit));
        return !kitsForPlayer.isEmpty();
    }

    @Override
    public RewardData give(IPlayer player) {
        List<Kit> kitsForPlayer = new ArrayList<>(kits);
        kitsForPlayer.removeIf(kit -> !kit.isOneTimeKit() && player.canUseKit(kit));
        Kit kit = Utils.getRandomElementFromList(kitsForPlayer);

        if(kit == null) {
            System.err.println("Kit is null in reward KitReward for player " + player.getName());
            return new RewardData("Error", new ItemStackBuilder(Material.BARRIER));
        }

        int amount = -1;

        if(kit.isOneTimeKit()) {
            amount = Utils.randInt(minAmount, maxAmount);
            player.addKitUses(kit, amount);
        } else {
            player.buyPermanentKit(kit);
        }

        return new RewardData(hologramMessage.replace("%kit%", kit.getKitName()).replace("%amount%", amount == -1 ? "" : amount + " "), kit.getDisplayItem());
    }
}