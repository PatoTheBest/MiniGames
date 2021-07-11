package me.patothebest.skywars.lang;

import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.lang.CommentType;
import me.patothebest.gamecore.lang.CoreComments;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.CoreLocaleManager;
import me.patothebest.gamecore.lang.LangComment;
import me.patothebest.gamecore.lang.Locale;
import me.patothebest.gamecore.lang.interfaces.IComment;
import me.patothebest.gamecore.lang.interfaces.ILang;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public enum Lang implements ILang {

    // -----------------------------------
    // General Messages
    // -----------------------------------

    HEADER(CommentType.HEADER, "SkyWars Core Messages"),
    GENERAL(CommentType.SUBHEADER_NOT_SPACED, "General Messages"),
    COMMAND_DESC("Command for general skywars commands"),

    // -------------------------------------------- //
    // Setup messages
    // -------------------------------------------- //
    SETUP(CommentType.HEADER, "Setup messages"),
    SETUP_COMMAND_DESCRIPTIONS(CommentType.SUBHEADER_NOT_SPACED, "Command descriptions"),
    ADD_SPAWN("Adds a spawn to a solo arena"),
    REMOVE_SPAWN("Removes a spawn from a solo arena"),
    LIST_SPAWN("List all the spawns from a solo arena"),
    CLEAR_SPAWNS("Clears all the spawns from a solo arena"),
    ADD_SPAWNS_DESC("Adds multiple to a solo arena by right clicking"),
    LIST_MID_CHEST("List all the mid chests from an arena"),
    CLEAR_MID_CHESTS("Clears all the mid chests from an arena"),
    MID_CHEST("Adds mid chests to the arena"),
    SCAN_CHESTS("Scans the selected area for mid chests"),
    SPAWN_HEIGHT("Sets the spawn height for the arena"),

    SETUP_SUCCESSFUL_MESSAGES(CommentType.SUBHEADER_SPACED, "Successful messages"),
    SPAWNS_DESC("Command for spawns"),
    MIDCHEST_DESC("Command for mid chests"),
    ADD_MID_CHEST(true, "&aClick all the chests in mid to add them to the list. Type this command again to stop adding chests."),
    ADD_STOP_MID_CHEST(true, "&aStopped adding mid chests."),
    MID_CHEST_ADDED(true, "&aMid-chest added"),
    MID_CHEST_REMOVED(true, "&aMid-chest removed"),
    SCANNING_AREA("Scanning area..."),
    SCANNED_AREA(true, "Scan complete! Found %chests% chests.", "chests"),
    ADD_SPAWNS(true, "&aClick all the blocks where you want a player to spawn. Type this command again to stop adding spawns."),
    CLEARED_SPAWNS(true, "&cCleared all spawns"),
    CLEARED_MID_CHEST(true, "&cCleared all mid chests"),
    ADD_STOP_SPAWNS(true, "&aStopped adding spawns"),
    SPAWN_ADDED(true, "&aSpawn %id% added", "id"),
    SPAWN_REMOVED(true, "&7Spawn %id% removed", "id"),
    SET_SPAWN_HEIGHT(true, "&aSpawn height has been set!"),

    SETUP_ERRORS(CommentType.SUBHEADER_SPACED, "Error messages"),
    ARENA_MUST_BE_SOLO(true, "&cArena must be solo arena"),
    ARENA_MUST_BE_TEAM(true, "&cArena must be team arena"),

    PHASE_NONE("None"),
    PHASE_REFILL("Refill"),
    PHASE_DOOM("Doom"),
    PHASE_BORDER_SHRINK("Border Shrink"),
    PHASE_END("End"),

;

    private String defaultMessage;
    private Map<Locale, String> message;
    private Map<Locale, String> rawMessage;
    private boolean prefix;
    private IComment comment;
    private String[] arguments;

    Lang(CoreComments CoreComments) {
        this.comment = CoreComments;
    }

    Lang(CommentType commentType, String title) {
        this.comment = new LangComment(commentType, title);
    }

    Lang(String defaultMessage, String... arguments) {
        this(false, defaultMessage, arguments);
    }

    Lang(boolean prefix, String defaultMessage, String... arguments) {
        this.defaultMessage = defaultMessage;
        this.prefix = prefix;
        this.message = new HashMap<>();
        this.rawMessage = new HashMap<>();
        this.arguments = arguments;
        setMessage(CoreLocaleManager.getLocale("en"), defaultMessage);
    }

    @Override
    public String getMessage(Locale locale) {
        return (prefix ? CoreLang.PREFIX.getMessage(locale) : "") + message.get(locale);
    }

    @Override
    public String getRawMessage(Locale locale) {
        return rawMessage.get(locale);
    }

    @Override
    public void setMessage(Locale locale, String message) {
        this.message.put(locale, ChatColor.translateAlternateColorCodes('&', message).replace("%newline%", "\n").replace("%plugin_prefix%", PluginConfig.LANG_PREFIX).replace("%game_title%", PluginConfig.GAME_TITLE));
        this.rawMessage.put(locale, message);
    }

    public void transferMessage(Lang lang) {
        message = lang.message;
        rawMessage = lang.rawMessage;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public boolean isComment() {
        return comment != null;
    }

    @Override
    public IComment getComment() {
        return comment;
    }

    @Override
    public String[] getArguments() {
        return arguments;
    }
}