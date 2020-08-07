package me.patothebest.arcade.lang;

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

    HEADER(CommentType.HEADER, "Arcade Messages"),
    GENERAL(CommentType.SUBHEADER_NOT_SPACED, "General Messages"),
    COMMAND_DESC("Command for general arcade commands"),

    // -------------------------------------------- //
    // Setup messages
    // -------------------------------------------- //
    SETUP(CommentType.HEADER, "Setup messages"),
    SETUP_COMMAND_DESCRIPTIONS(CommentType.SUBHEADER_NOT_SPACED, "Command descriptions"),
    ADD_SPAWN("Adds a spawn to a game"),
    REMOVE_SPAWN("Removes a spawn from a game"),
    LIST_SPAWN("List all the spawns from a game"),
    CLEAR_SPAWNS("Clears all the spawns from a game"),
    ENABLE_GAME("Enables a game for the arena"),
    DISABLE_GAME("Disables a game for the arena"),
    TELEPORT_TO_GAME("Teleports to the game's spawn"),
    SET_GAME_AREA("Sets the game area"),
    SET_FLOOR("Sets the game's floor"),

    SETUP_SUCCESSFUL_MESSAGES(CommentType.SUBHEADER_SPACED, "Successful messages"),
    CLEARED_SPAWNS(true, "&cCleared all spawns"),
    ADD_STOP_SPAWNS(true, "&aStopped adding spawns"),
    SPAWN_ADDED(true, "&aSpawn %id% added", "id"),
    SPAWN_REMOVED(true, "&7Spawn %id% removed", "id"),
    ENABLED_GAME(true, "&6%game% &aenabled for arena %6%arena%", "game", "arena"),
    DISABLED_GAME(true, "&6%game% &aenabled for arena %6%arena%", "game", "arena"),
    TELEPORTED_TO_GAME(true, "&aYou have been teleported to the game."),
    GAME_AREA_SET(true, "&aGame's area has been set!"),
    FLOOR_SET(true, "&aGame's floor has been set!"),

    SETUP_ERRORS(CommentType.SUBHEADER_SPACED, "Error messages"),
    ARENA_MUST_BE_SOLO(true, "&cArena must be solo arena"),
    ARENA_MUST_BE_TEAM(true, "&cArena must be team arena"),
    SETUP_ERROR_SET_AREA("&cMissing area! Use &e/%base_command% setup setarenaarea &cto set it"),
    SETUP_ERROR_LOBBY_LOCATION("&cMissing lobby location! Use &e/games setup %game% setlobby"),
    SETUP_ERROR_SPECTATOR_LOCATION("&cMissing spectator location! Use &e/games %game% setspec"),
    SETUP_ERROR_SPAWN("&cMissing at least 1 spawn! Use &e/games %game% addspawn"),
    SETUP_ERROR_FLOOR("&cMissing floor! Use &e/games %game% setfloor"),
    NO_SPAWNS("There are no spawns set for the game."),


    FIRST_PLACE("&e1st Place &7- &7%winner% &7- %stars% &e⭑", "winner", "stars"),
    SECOND_PLACE("&62nd Place &7- &7%winner% &7- %stars% &e⭑", "winner", "stars"),
    THIRD_PLACE("&c3rd Place &7- &7%winner% &7- %stars% &e⭑", "winner", "stars"),
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