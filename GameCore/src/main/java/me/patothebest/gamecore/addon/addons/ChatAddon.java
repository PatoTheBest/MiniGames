package me.patothebest.gamecore.addon.addons;

import com.google.inject.Provider;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.arena.ArenaState;
import me.patothebest.gamecore.placeholder.PlaceHolderManager;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.addon.Addon;
import me.patothebest.gamecore.arena.ArenaManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import javax.inject.Inject;

public class ChatAddon extends Addon {

    private final ArenaManager arenaManager;
    private final PlayerManager playerManager;

    // Vault objects
    private final Provider<Permission> permissions;
    private final Provider<Chat> chat;

    // Cache objects
    private String globalChat;
    private String globalChatTeam;
    private String teamChat;
    private String soloChat;
    private String lobbyChat;
    private String preGame;
    private String spectator;
    private boolean removeWhitespaces;

    @Inject private ChatAddon(ArenaManager arenaManager, PlayerManager playerManager, Provider<net.milkbowl.vault.permission.Permission> permissions, Provider<Chat> chat) {
        this.arenaManager = arenaManager;
        this.playerManager = playerManager;
        this.permissions = permissions;
        this.chat = chat;
    }

    @Override
    public void configure(ConfigurationSection addonConfigSection) {
        globalChat = addonConfigSection.getString("global", "");
        globalChatTeam = addonConfigSection.getString("global-team", "");
        teamChat = addonConfigSection.getString("team", "");
        soloChat = addonConfigSection.getString("solo", "");
        preGame = addonConfigSection.getString("pre-game", "");
        lobbyChat = addonConfigSection.getString("lobby", "");
        spectator = addonConfigSection.getString("spectator", "");
        removeWhitespaces = addonConfigSection.getBoolean("remove-double-whitespaces");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(getPlayer(player) == null) {
            return;
        }

        if(getPlayer(player).isInArena()) {
            AbstractArena arena = playerManager.getPlayer(player).getCurrentArena();
            if(arena.getArenaState() == ArenaState.WAITING || playerManager.getPlayer(player).getGameTeam() == null) {
                event.getRecipients().clear();

                if(!arena.getSpectators().contains(player)) {
                    event.getRecipients().addAll(arena.getPlayers());
                    event.getRecipients().addAll(arena.getSpectators());
                    setMessage(event, preGame);
                } else {
                    event.getRecipients().addAll(arena.getSpectators());
                    setMessage(event, spectator);
                }
            } else {
                event.getRecipients().clear();
                AbstractGameTeam gameTeam = playerManager.getPlayer(player).getGameTeam();
                if(event.getMessage().startsWith("!") && arena.getTeams().size() > 1) {
                    event.getRecipients().addAll(arena.getPlayers());
                    String format;

                    if(gameTeam.hasTeamChatPrefix()) {
                        format = globalChatTeam.replace("%teamcolor%", Utils.getColorFromDye(gameTeam.getColor()).toString()).replace("%teamname%", gameTeam.getName());
                    } else {
                        format = globalChat;
                    }

                    setMessage(event, format);
                    event.getRecipients().addAll(arena.getSpectators());
                } else {
                    event.getRecipients().addAll(gameTeam.getPlayers());
                    String format;

                    if(gameTeam.hasTeamChatPrefix()) {
                        format = teamChat.replace("%teamcolor%", Utils.getColorFromDye(gameTeam.getColor()).toString()).replace("%teamname%", gameTeam.getName());
                    } else {
                        format = soloChat;
                        event.getRecipients().addAll(arena.getSpectators());
                    }

                    setMessage(event, format);
                }
            }
        } else {
            for (AbstractArena arena : arenaManager.getArenas().values()) {
                event.getRecipients().removeAll(arena.getPlayers());
            }

            setMessage(event, lobbyChat);
        }
    }

    private void setMessage(AsyncPlayerChatEvent event, String format) {
        format = parseMessage(event.getPlayer(), format);

        /*
        * ^\\s+  replaces all whitespaces from the beginning
        *    ^   asserts position at start of the string
        *    \s  matches any whitespace character
        *    +   Quantifier — Matches between one and unlimited times, as many times as possible, giving back as needed
        */
        format = ChatColor.translateAlternateColorCodes('&', format).replaceAll("^\\s+", "");

        if(removeWhitespaces) {
            /*
            *  [ ]{2,}  Matches two or more whitespaces
            *    [ ]    matches the character ' ' literally (the space)
            *    {2,}   Quantifier — Matches between 2 and unlimited times, as many times as possible, giving back as needed (greedy)
            */
            format = format.replaceAll("[ ]{2,}", " ");
        }

        if(!format.isEmpty()) {
            event.setFormat(format);
        }

        if(event.getPlayer().hasPermission(me.patothebest.gamecore.permission.Permission.CHAT_COLORS.getBukkitPermission())) {
            event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()).replaceFirst("!", ""));
        }
    }

    private IPlayer getPlayer(Player player) {
        return playerManager.getPlayer(player);
    }

    private String parseMessage(final Player player, String format) {
        String group = (permissions.get() == null) ? "" : permissions.get().hasGroupSupport() ? permissions.get().getPrimaryGroup(player) : "";
        String groupPrefix = (chat.get() == null) ? "" : chat.get().getGroupPrefix(player.getWorld(), group);
        String groupSuffix = (chat.get() == null) ? "" : chat.get().getGroupSuffix(player.getWorld(), group);
        String playerName = player.getName();
        String modPlayername = player.getDisplayName();
        format = format.replace("{group}", group).replace("{permprefix}", groupPrefix).replace("{permsuffix}", groupSuffix).replace("{playername}", playerName).replace("{modplayername}", modPlayername).replace("{msg}", "%2$s").replace("&", "§");
        format = PlaceHolderManager.replace(player, format);
        return format;
    }

    @Override
    public String getConfigPath() {
        return "chat";
    }
}
