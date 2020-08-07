package me.patothebest.gamecore.treasure;

import me.patothebest.gamecore.treasure.chest.TreasureChest;
import me.patothebest.gamecore.treasure.chest.TreasureChestLocation;
import me.patothebest.gamecore.treasure.reward.RewardRandomizer;
import me.patothebest.gamecore.treasure.type.TreasureType;
import me.patothebest.gamecore.treasure.ui.BuyTreasureChestsUI;
import me.patothebest.gamecore.treasure.ui.OpenChestButton;
import me.patothebest.gamecore.treasure.ui.OpenChestNoVoteUI;
import me.patothebest.gamecore.treasure.ui.OpenChestVoteUI;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.util.Callback;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public interface TreasureFactory {

    TreasureChest createChest(IPlayer player, TreasureType treasureType, TreasureChestLocation treasureLocation, Callback<TreasureChestLocation> callback);

    TreasureChestLocation createLocation(Location location);

    TreasureChestLocation createLocation(Map<String, Object> location);

    RewardRandomizer createRewardRandomizer(IPlayer iPlayer, Player player, TreasureType treasureType);

    OpenChestVoteUI createMenuWithVote(IPlayer player, TreasureChestLocation treasureLocation);

    OpenChestNoVoteUI createMenuWithoutVote(IPlayer player, TreasureChestLocation treasureLocation);

    BuyTreasureChestsUI createBuyMenu(IPlayer player, TreasureType treasureType);

    OpenChestButton createMenuButton(TreasureType chestType, IPlayer player, TreasureChestLocation location);
}
