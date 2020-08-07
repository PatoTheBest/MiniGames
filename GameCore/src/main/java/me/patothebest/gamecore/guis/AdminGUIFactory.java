package me.patothebest.gamecore.guis;

import me.patothebest.gamecore.guis.admin.AdminChooseTeamUI;
import me.patothebest.gamecore.guis.admin.AdminJoinArenaGUI;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.player.IPlayer;
import org.bukkit.entity.Player;

public interface AdminGUIFactory {

    AdminChooseTeamUI createMenu(IPlayer player, AbstractArena abstractArena);

    AdminJoinArenaGUI createMenu(Player player);

}
