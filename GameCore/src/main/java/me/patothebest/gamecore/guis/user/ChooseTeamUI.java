package me.patothebest.gamecore.guis.user;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.GUIUpdater;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.util.Callback;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChooseTeamUI extends GUIPage {

    private final AbstractArena arena;
    private final GUIUpdater guiUpdater;
    private final Callback<Player> callback;

    @Inject private ChooseTeamUI(CorePlugin plugin, @Assisted Player player, @Assisted AbstractArena arena, GUIUpdater guiUpdater, @Nullable @Assisted Callback<Player> callback) {
        super(plugin, player, CoreLang.GUI_USER_CHOOSE_TEAM_TITLE.getMessage(player), ((int) Math.ceil(arena.getTeams().size() / 9.0))*9);
        this.arena = arena;
        this.guiUpdater = guiUpdater;
        this.callback = callback;
        guiUpdater.register(this);
        build();
    }

    @Override
    public void buildPage() {
        final int[] slot = {0};
        int maxPerTeam = (int) Math.ceil(arena.getMaxPlayers()/arena.getTeams().size());
        arena.getTeams().values().forEach((AbstractGameTeam gameTeam) -> {
            int onTeam = (arena.getTeamPreferences().containsKey(gameTeam) ? arena.getTeamPreferences().get(gameTeam).size() : 0) + gameTeam.getPlayers().size();
            List<String> playersOnTeam = new ArrayList<>();
            if (arena.getTeamPreferences().containsKey(gameTeam)) {
                playersOnTeam.addAll(arena.getTeamPreferences().get(gameTeam).stream().map(Player::getName).collect(Collectors.toList()));
            }
            playersOnTeam.addAll(gameTeam.getPlayers().stream().map(Player::getName).collect(Collectors.toList()));
            addButton(new SimpleButton(new ItemStackBuilder()
                    .material(Material.WHITE_WOOL)
                    .name(Utils.getColorFromDye(gameTeam.getColor()) + gameTeam.getName() + " (" + onTeam + "/" + maxPerTeam + ")")
                    .color(gameTeam.getColor())
                    .amount(onTeam)
                    .lore(Utils.orderListForLore("Players", playersOnTeam))).action(() -> {
                AbstractGameTeam otherTeam = arena.getTeamPreference(player);

                if(otherTeam != null && otherTeam.equals(gameTeam)) {
                    player.sendMessage(CoreLang.GUI_USER_CHOOSE_TEAM_YOU_ARE_ALREADY_IN.replace(player, Utils.getColorFromDye(gameTeam.getColor()), gameTeam.getName()));
                    return;
                }

                if(onTeam >= maxPerTeam) {
                    player.sendMessage(CoreLang.GUI_USER_CHOOSE_TEAM_FULL.replace(player, Utils.getColorFromDye(gameTeam.getColor()), gameTeam.getName()));
                    return;
                }

                if(otherTeam != null) {
                    arena.getTeamPreferences().get(otherTeam).remove(player);
                }

                List<Player> players = arena.getTeamPreferences().computeIfAbsent(gameTeam, k -> new ArrayList<>());
                players.add(player);
                arena.getTeamPreferences().putIfAbsent(gameTeam, players);
                player.sendMessage(CoreLang.GUI_USER_CHOOSE_TEAM_YOU_JOINED.replace(player, Utils.getColorFromDye(gameTeam.getColor()), gameTeam.getName()));

                if (callback != null) {
                    callback.call(player);
                } else {
                    guiUpdater.refresh(this);
                }
            }), slot[0]);
            slot[0]++;
        });
    }

    @Override
    public void destroy() {
        guiUpdater.unregister(this);
    }
}