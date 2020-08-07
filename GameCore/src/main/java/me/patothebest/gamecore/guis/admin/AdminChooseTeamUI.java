package me.patothebest.gamecore.guis.admin;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminChooseTeamUI extends GUIPage {

    private final AbstractArena arena;
    private final IPlayer player;

    @Inject private AdminChooseTeamUI(CorePlugin plugin, @Assisted IPlayer player, @Assisted AbstractArena arena) {
        super(plugin, player.getPlayer(), CoreLang.GUI_USER_CHOOSE_TEAM_TITLE, ((int) Math.ceil(arena.getTeams().size() / 9.0))*9);
        this.player = player;
        this.arena = arena;
        build();
    }

    @Override
    public void buildPage() {
        final int[] slot = {0};
        for (AbstractGameTeam gameTeam : arena.getTeams().values()) {
            addButton(new SimpleButton(new ItemStackBuilder()
                    .material(Material.WHITE_WOOL)
                    .name(Utils.getColorFromDye(gameTeam.getColor()) + gameTeam.getName()).color(gameTeam.getColor())
                    .amount(arena.getTeamPreferences().containsKey(gameTeam) ? arena.getTeamPreferences().get(gameTeam).size() : 0)
                    .lore(Utils.orderListForLore("Players", gameTeam.getPlayers().stream().map(Player::getName).collect(Collectors.toList())))).action(() -> {
                Location oldLocation = null;
                if(player.isInArena()) {
                    if(player.getGameTeam() != null) {
                        if(player.getGameTeam() == gameTeam) {
                            CoreLang.GUI_USER_CHOOSE_TEAM_YOU_ARE_ALREADY_IN.sendMessage(player);
                            return;
                        }
                    }

                    oldLocation = player.getLocation();
                    arena.removePlayer(super.player, true);
                }

                List<Player> players = arena.getTeamPreferences().getOrDefault(gameTeam, new ArrayList<>());
                players.add(player.getPlayer());
                arena.getTeamPreferences().putIfAbsent(gameTeam, players);
                player.sendMessage(CoreLang.GUI_USER_CHOOSE_TEAM_YOU_JOINED.replace(player, Utils.getColorFromDye(gameTeam.getColor()), gameTeam.getName()));
                arena.addPlayer(super.player);

                if(oldLocation != null) {
                    super.player.teleport(oldLocation);
                }

                super.player.closeInventory();
            }), slot[0]);
            slot[0]++;
        }
    }

}