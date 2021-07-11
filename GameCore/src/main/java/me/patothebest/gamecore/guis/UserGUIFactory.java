package me.patothebest.gamecore.guis;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.feature.features.gameoptions.GameOptionsFeature;
import me.patothebest.gamecore.guis.user.ChooseTeamUI;
import me.patothebest.gamecore.guis.user.GameOptionsUI;
import me.patothebest.gamecore.guis.user.JoinArenaGUI;
import me.patothebest.gamecore.guis.user.kit.BuyKitUsesUI;
import me.patothebest.gamecore.guis.user.kit.KitLayoutUI;
import me.patothebest.gamecore.guis.user.kit.KitOptionsUI;
import me.patothebest.gamecore.guis.user.kit.KitShopUI;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.util.Callback;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public interface UserGUIFactory {

    ChooseTeamUI createTeamUI(Player player, AbstractArena arena, @Nullable Callback<Player> callback);

    BuyKitUsesUI createBuyKitUsesUI(IPlayer player, Kit kit, Runnable onBack);

    KitOptionsUI openKitOptions(IPlayer player, Kit kit, Runnable onBack);

    KitShopUI openKitShop(Player player);

    KitLayoutUI openKitLayoutEditor(IPlayer player, Kit kit, Runnable onBack);

    GameOptionsUI openGameOptions(Player player, GameOptionsFeature gameOptionsFeature);

    JoinArenaGUI createJoinArenaUI(Player player);

    JoinArenaGUI createJoinArenaUI(Player player, Predicate<AbstractArena> filter);

}
