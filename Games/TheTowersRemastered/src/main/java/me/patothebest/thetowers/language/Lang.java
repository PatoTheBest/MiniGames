package me.patothebest.thetowers.language;

import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.CoreLocaleManager;
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

    HEADER(Comments.HEADER),
    GENERAL(Comments.GENERAL_MESSAGES),
    COMMAND_DESCRIPTION("Command for TheTowers commands"),
    PAGE(false, "(Page %page% of %pages%)"),

    // -----------------------------------
    // Setup Commands
    // -----------------------------------

    // Command descriptions
    SETUP_HEADER(Comments.SETUP_COMMANDS),
    SETUP_COMMAND_DESCRIPTIONS(Comments.COMMAND_DESCRIPTIONS),
    PROTECTED_AREAS(false, "Subcommand for protected areas"),
    NEW_DROPPER(false, "Creates item spawner on your location"),
    ADD_POINT_AREA(false, "Adds a point area for the specified game team."),
    PROTECTED_AREAS_LIST(false, "Lists all the protected areas"),
    PROTECT_AREA(false, "Protects an area on the arena"),
    REMOVE_PROTECTED_AREA(false, "Remove a protected area from the arena"),
    OPEN_GUI(false, "Opens the graphical user interface"),
    SHOW_PROTECTED_AREA(false, "Shoes a protected area"),
    HIDE_PROTECTED_AREA(false, "Hides a protected area"),

    // Errors in command arguments
    SETUP_COMMAND_ERRORS(Comments.ERRORS_IN_CMD_ARGS),
    TEAM_COLOR_ALREADY_EXISTS("&cTeam color already exists!"),
    TEAM_COLOR_DOES_NOT_EXIST("&cTeam color doesn't exists!"),
    DISABLE_ARENA_FIRST("&cTo modify an arena you must disable it first using /tt setup disablearena <arena>"),
    ENABLE_ARENA_FIRST("&cTu use this command you need to enable the arena first"),
    WRONG_ITEMSTACK_FORMAT("&cThe item could not be parsed. \nPlease use the format material:data,amount (wool:14,32 = 32 red wool"),

    // Successful messages
    SETUP_SUCCESSFUL_EXECUTION(Comments.SUCCESSFUL_EXECUTION),
    PROTECTED_AREA_SHOW("&aProtected area has been shown!"),
    PROTECTED_AREA_HIDE("&aProtected area has been hidden!"),
    PROTECTED_AREA_LIST_HEADER(false, "&9Protected Areas of %arena%"),


    // -----------------------------------
    // Admin Commands
    // -----------------------------------

    // Command descriptions
    ADMIN_HEADER(Comments.ADMIN_COMMANDS),
    ADMIN_COMMAND_DESCRIPTIONS(Comments.COMMAND_DESCRIPTIONS),
    VERSION(false, "Displays jar information"),
    DEBUG(false, "Debug command"),
    RELOAD(false, "Reloads the whole plugin"),
    SCORE(false, "Scores for the team you are on"),
    REFILL(false, "Refill the chests on the arena you are in"),


    // Errors in command arguments
    ADMIN_COMMAND_ERRORS(Comments.ERRORS_IN_CMD_ARGS),
    CHESTS_REFILLED("Chests refilled."),

    // Successful messages
    ADMIN_SUCCESSFUL_EXECUTION(Comments.SUCCESSFUL_EXECUTION),


    // -----------------------------------
    // Errors
    // -----------------------------------

    // Setup errors
    ERROR_HEADER(Comments.ERRORS),
    SETUP_ERRORS(Comments.SETUP_ERRORS),
    SETUP_ERROR_SET_AREA("&cMissing area! Use &e/tt setup setarenaarea &cto set it"),
    SETUP_ERROR_LOBBY_LOCATION("&cMissing lobby location! Use &e/tt setup setlobby"),

    // -----------------------------------
    // Game messages
    // -----------------------------------

    // General game messages
    GAME_HEADER(Comments.GAME_MESSAGES),
    GAME_GENERAL_HEADER(Comments.GENERAL_GAME_MESSAGES),
    YOU_ARE_IN_TEAM(false, "&6You are in team %teamcolor%%teamname%"),
    YOU_ARE_IN_TEAM_TITLE("&7You are in team %teamcolor%%teamname%"),
    PLAYER_SCORED("%teamcolor%%player% &ehas scored a point!"),
    TEAM_WON(false, "%teamcolor%%teamname% has won the game!"),

    SPECTATOR_TITLE(false, "&cGame Over"),
    SPECTATOR_SUBTITLE(false, "%teamcolor%%teamname% has won the game!", "teamcolor", "teamname"),

    SUMMARY_TIME(false, "&fThe game lasted &e&l%minutes% minutes", "minutes"),
    SUMMARY_KILLS(false, "&fYou killed &e&l%kills% opponent", "kills"),
    SUMMARY_POINTS(false, "&fYou scored &e&l%points% points &ffor your team", "points"),
    SUMMARY_TEAMS_HEADER(false, "&f&lEach team scored:"),
    SUMMARY_TEAMS(false, "&f%place%. %teamcolor%%teamname% &f%teampoints%", "place", "teamcolor", "teamname", "teampoints"),
    SUMMARY_MOST_POINTS_HEADER(false, "&f&lTop 5 scoring players:"),
    SUMMARY_MOST_POINTS(false, "&f%place%. %teamcolor%%player% &f%points%", "place", "teamcolor", "player", "points"),
    SUMMARY_MOST_KILLS_HEADER(false, "&f&lTop 5 killers:"),
    SUMMARY_MOST_KILLS(false, "&f%place%. %teamcolor%%player% &f%kills%", "place", "teamcolor", "player", "kills"),

    // -----------------------------------
    // GUI messages
    // -----------------------------------

    // General Messages
    GUI_HEADER(Comments.GUI_MESSAGES),

    // Main menu messages
    GUI_MAIN(Comments.GUI_MAIN),
    GUI_MAIN_TITLE(false, "Main Menu"),
    GUI_MAIN_CREATE_ARENA(false, "&aCreate an arena"),
    GUI_MAIN_EDIT_ARENA(false, "&aEdit an arena"),

    // Choose arena menu
    GUI_CHOOSE_ARENA(Comments.GUI_CHOOSE_ARENA),
    GUI_CHOOSE_ARENA_TITLE(false, "Choose an Arena to Edit"),
    GUI_CHOOSE_ARENA_EDIT_ITEM(false, "&aEdit arena %arena%"),

    // Edit arena menu
    GUI_EDIT_ARENA(Comments.GUI_EDIT_ARENA),
    GUI_EDIT_ARENA_TITLE(false, "Edit Arena %arena%"),
    GUI_EDIT_ARENA_GAME_TEAMS(false, "&eGame Teams"),
    GUI_EDIT_ARENA_PLAYERS(false, "&ePlayers"),
    GUI_EDIT_ARENA_TELEPORT(false, "&bTeleport"),
    GUI_EDIT_ARENA_DROPPERS(false, "&7Droppers (Item Spawners)"),
    GUI_EDIT_ARENA_SET_LOBBY(false, "&eSet Lobby"),
    GUI_EDIT_ARENA_SET_PERMISSION(false, "&eSet Permission"),
    GUI_EDIT_ARENA_DELETE(false, "&cDelete Arena"),
    GUI_EDIT_ARENA_DISABLE_TO_ACCESS(false, "&cDisable arena to access edit menu"),

    // Choose team menu
    GUI_CHOOSE_TEAM(Comments.GUI_CHOOSE_TEAM),
    GUI_CHOOSE_TEAM_TITLE(false, "Choose Team"),
    GUI_CHOOSE_TEAM_CREATE_TEAM(false, "%teamcolor%Create team"),
    GUI_CHOOSE_TEAM_EDIT_TEAM(false, "%teamcolor%Edit %teamname%"),

    // Edit team menu
    GUI_EDIT_TEAM(Comments.GUI_CHOOSE_TEAM),
    GUI_EDIT_TEAM_TITLE(false, "Edit team"),
    GUI_EDIT_TEAM_SET_SPAWN(false, "&eSet spawn"),
    GUI_EDIT_TEAM_POINT_AREAAS(false, "&6Point areas"),
    GUI_EDIT_TEAM_DELETE(false, "&cDelete team"),
    GUI_EDIT_TEAM_DELETED(false, "&cTeam deleted"),

    // Choose point area
    GUI_CHOOSE_POINT_AREA(Comments.GUI_CHOOSE_POINT_AREA),
    GUI_CHOOSE_POINT_AREA_TITLE(false, "Choose Point Area"),
    GUI_CHOOSE_POINT_AREA_CREATE(false, "&aAdd point area"),

    // Edit point area
    GUI_EDIT_POINT_AREA(Comments.GUI_EDIT_POINT_AREA),
    GUI_EDIT_POINT_AREA_TITLE(false, "Edit Point Area"),
    GUI_EDIT_POINT_AREA_DELETE(false, "&cDelete point area"),

    // Choose dropper
    GUI_CHOOSE_DROPPER(Comments.GUI_CHOOSE_DROPPER),
    GUI_CHOOSE_DROPPER_TITLE(false, "Choose Dropper"),
    GUI_CHOOSE_DROPPER_CREATE(false, "Create a dropper"),

    // Edit dropper
    GUI_EDIT_DROPPER(Comments.GUI_EDIT_DROPPER),
    GUI_EDIT_DROPPER_TITLE(false, "Edit Dropper"),
    GUI_EDIT_DROPPER_CHANGE_INTERVAL(false, "&aChange the item spawn interval"),
    GUI_EDIT_DROPPER_CHANGE_ITEM_NAME(false, "&a%name% item"),
    GUI_EDIT_DROPPER_CHANGE_ITEM_LORE(false, "&7Interval: &f%interval%%newline%&7Click to change item"),
    GUI_EDIT_DROPPER_DELETE(false, "&cDelete"),

    // Edit interval
    GUI_EDIT_INTERVAL(Comments.GUI_EDIT_INTERVAL),
    GUI_EDIT_INTERVAL_TITLE(false, "Edit interval"),
    GUI_EDIT_INTERVAL_ITEM_NAME(false, "&a%name%"),
    GUI_EDIT_INTERVAL_ITEM_LORE(false, "&7Interval: &f%interval%"),
    GUI_EDIT_INTERVAL_RESET(false, "&cReset amount to 1"),

    // Edit players
    GUI_EDIT_PLAYERS(Comments.GUI_EDIT_PLAYERS),
    GUI_EDIT_PLAYERS_TITLE(false, "Edit Players"),
    GUI_EDIT_PLAYERS_MIN_PLAYERS_NAME(false, "&eMin players"),
    GUI_EDIT_PLAYERS_MAX_PLAYERS_NAME(false, "&eMax players"),
    GUI_EDIT_PLAYERS_LORE(false, "&7Amount: &f%amount%"),
    ;

    private String defaultMessage;
    private Map<Locale, String> message;
    private Map<Locale, String> rawMessage;
    private boolean prefix;
    private Comments comment;
    private String[] arguments;

    Lang(Comments comments) {
        this.comment = comments;
    }

    Lang(String defaultMessage) {
        this(true, defaultMessage);
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
