package me.patothebest.gamecore.lang;

import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.chat.ChatColorEscaper;
import me.patothebest.gamecore.lang.interfaces.IComment;
import me.patothebest.gamecore.lang.interfaces.ILang;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum CoreLang implements ILang {

    // -----------------------------------
    // General Messages
    // -----------------------------------

    HEADER(CommentType.HEADER, "Game Core Messages"),
    GENERAL(CommentType.SUBHEADER_NOT_SPACED, "General Messages"),
    NAME("English"),
    SKIN("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhYzk3NzRkYTEyMTcyNDg1MzJjZTE0N2Y3ODMxZjY3YTEyZmRjY2ExY2YwY2I0YjM4NDhkZTZiYzk0YjQifX19"),
    PREFIX("&8[&c%plugin_prefix%&8]: &7", "plugin_prefix"),
    PAGE("(Page %page% of %pages%)", "page", "pages"),
    INVALID_PAGRE("&cInvalid Page! (Page %page% of %pages%)", "page", "pages"),
    NO_PERMISSION("&cYou do not have permission to perform this command!"),
    COMMAND_CORRECTION("&7Did you mean &6%correction%&7?", "correction"),
    TOO_FEW_ARGUMENTS("&cToo few arguments"),
    TOO_MANY_ARGUMENTS("&cToo many arguments"),
    USAGE("&cUsage: %usage%", "usage"),
    UNKNOWN_FLAG("&cUnknown flag %flag%", "flag"),
    NOT_A_NUMBER("&c'%string%' is not a number!", "string"),
    OUT_OF_BOUNDS("Out of bounds!"),
    NO_RESULTS("&cThere are no results."),
    CONFIRM(true, "Type &6/%base_command% confirm &7to confirm."),
    CONFIRM_DESC("Confirms an action"),
    NO_CONFIRMATION("No action to be confirmed."),
    MONEY_GIVEN("&6+%amount% coins", "amount"),
    SPECTATOR("Spectator"),
    LOADING("Loading..."),
    DATABASE_REQUIREMENT("This requires a running database!"),
    LINE_SEPARATOR("&a&m" + StringUtils.repeat(" ", 80)),
    TIME_MONTHS("months"),
    TIME_MONTH("month"),
    TIME_WEEKS("weeks"),
    TIME_WEEK("week"),
    TIME_DAYS("days"),
    TIME_DAY("day"),
    TIME_HOURS("hours"),
    TIME_HOUR("hour"),
    TIME_MINUTES("minutes"),
    TIME_MINUTE("minute"),
    TIME_SECONDS("seconds"),
    TIME_SECOND("second"),


    // -----------------------------------
    // User Commands
    // -----------------------------------

    // Command descriptions
    USER_HEADER(CoreComments.USER_COMMANDS),
    USER_COMMAND_DESCRIPTIONS(CoreComments.COMMAND_DESCRIPTIONS),
    JOIN_ARENA("Join an arena"),
    SPEC_ARENA("Spectate an arena"),
    LEAVE_ARENA("Leave an arena"),
    LIST_ARENAS("List all the arenas"),
    KIT_COMMAND("Opens the menu to choose a kit"),
    KIT_COMMAND_DESCRIPTION("Command for kits"),
    LOCALE_COMMAND("Opens the menu to change your language"),

    // Errors in command arguments
    USER_COMMAND_ERRORS(CoreComments.ERRORS_IN_CMD_ARGS),
    ARENA_IS_NOT_PLAYABLE(true, "&cThe arena you are trying to play is not available."),
    ARENA_IS_RESTARTING(true, "&cThe arena is restarting. Please try again later."),
    ARENA_HAS_STARTED(true, "&cThe arena has already started."),
    ALREADY_IN_ARENA(true, "&cYou are already in an arena!"),
    NOT_IN_AN_ARENA(true, "&cYou are not in an arena!"),
    ARENA_IS_FULL(true, "&cThe arena you are trying to join is currently full"),
    NO_ARENAS(true, "&cThere are no arenas to play."),

    // Successful messages
    USER_SUCCESSFUL_EXECUTION(CoreComments.SUCCESSFUL_EXECUTION),
    ARENA_ENABLED_SHORT("enabled"),
    ARENA_DISABLED_SHORT("disabled"),
    ARENAS(true, "Arenas: "),

    // -----------------------------------
    // Arena Setup Commands
    // -----------------------------------

    // Command descriptions
    SETUP_HEADER(CommentType.HEADER, "Arena Setup Commands"),
    SETUP_COMMAND_DESCRIPTIONS(CoreComments.COMMAND_DESCRIPTIONS),
    SETUP_COMMAND_DESCRIPTION("Command for setup"),
    TEAMS_COMMAND_DESCRIPTION("Command for teams"),
    NEW_ARENA("Creates a new arena"),
    IMPORT_ARENA("Imports an arena world"),
    NEW_TEAM("Creates a new team for the arena"),
    NEW_DROPPER("Creates item spawner on your location"),
    NEW_TEAM_SPAWN("Sets the team spawn for the specified team"),
    PROTECTED_AREAS_LIST("Protects an area on the arena"),
    PROTECT_AREA("Lists all the protected areas"),
    SET_LOBBY_LOCATION("Sets the arena lobby location"),
    SET_SPECTATOR_LOCATION("Sets the arena spectator location"),
    ENABLE_ARENA("Enables an arena to be playable"),
    DISABLE_ARENA("Disables an arena to allow it to be modified"),
    TELEPORT_TO_ARENA("Teleports to the arena"),
    CHECK_ARENA("Checks to see whether or not an arena can be enabled"),
    SET_ARENA_AREA("Sets the arena's playable area"),
    SET_LOBBY_AREA("Sets the lobby's area"),
    SHOW_ARENA_AREA("Shows the arena's area"),
    HIDE_ARENA_AREA("Hides the arena's area"),
    SET_MAIN_LOBBY("Sets the main lobby"),
    TP_MAIN_LOBBY("Teleports you to the main lobby"),
    SET_MIN_PLAYERS("Sets the minimum amount of players for the arena"),
    SET_MAX_PLAYERS("Sets the maximum amount of players for the arena"),
    OPEN_GUI("Opens the graphical user interface"),
    DELETE_ARENA("Deletes an arena"),
    CLEAR_ARENA("Clears an arena's configuration"),

    // Errors in command arguments
    SETUP_COMMAND_ERRORS(CoreComments.ERRORS_IN_CMD_ARGS),
    TEAM_COLOR_NOT_FOUND(true, "&cTeam color not found."),
    NO_PERMISSION_ARENA(true, "&cYou do not have permission to join this arena!"),
    TEAM_COLOR_ALREADY_EXIST(true, "&cTeam color already exist!"),
    TEAM_COLOR_DOES_NOT_EXIST(true, "&cTeam color doesn't exist!"),
    TEAM_DOES_NOT_EXIST(true, "&cTeam doesn't exist"),
    ARENA_ALREADY_DISABLED(true, "&cThe arena is already disabled!"),
    ARENA_ALREADY_ENABLED(true, "&cThe arena is already enabled!"),
    DISABLE_ARENA_FIRST(true, "&cTo modify an arena you must disable it first using /%base_command% setup disablearena <arena>"),
    ENABLE_ARENA_FIRST(true, "&cTo use this command you need to enable the arena first"),
    WRONG_ITEMSTACK_FORMAT(true, "&cThe item could not be parsed. %newline%Please use the format material:data,amount (wool:14,32 = 32 red wool"),
    ARENA_ALREADY_EXIST(true, "&cArena already exist!"),
    ARENA_DOES_NOT_EXIST(true, "&cArena doesn't exist!"),
    FOLDER_DOESNT_EXIST(true, "&cA world with that name doesn't exist! Please verify the world name."),
    ARENA_IS_FILE(true, "&cThere's no world folder with that arena name"),
    SOMETHING_WENT_WRONG_IMPORTING(true, "&cSomething went wrong while importing world. Couldn't rename directory."),
    AREA_DOES_NOT_EXIST(true, "&cArea doesn't exist!"),

    // Successful messages
    SETUP_SUCCESSFUL_EXECUTION(CoreComments.SUCCESSFUL_EXECUTION),
    TEAM_CREATED(true, "&aTeam successfully created!"),
    TEAM_SPAWN_SET(true, "&aTeam spawn has been set!"),
    TEAM_POINT_AREA_SET(true, "&aTeam point area has been added!"),
    AREA_PROTECTED(true, "&aArea protected!"),
    AREA_REMOVED(true, "&aArea removed!"),
    MAIN_LOBBY_TP(true, "&aYou have been teleported to the main lobby!"),
    SPAWN_TP(true, "&eSince the main lobby isn't set you have been teleported to the main world's spawn. Consider setting the main lobby using &6/%base_command% setup setmainlobby"),
    LOBBY_LOCATION_SET(true, "&aLobby location has been set!"),
    SPECTATOR_LOCATION_SET(true, "&aSpectator location has been set!"),
    DROPPER_ADDED(true, "&aDropper has been added!"),
    MIN_PLAYERS_SET(true, "&aMinimum amount of players have been set!"),
    MAX_PLAYERS_SET(true, "&aMaximum amount of players have been set!"),
    ARENA_ENABLED(true, "&aArena has been enabled!"),
    ARENA_DISABLED(true, "&aArena has been disabled!"),
    ARENA_AREA_SET(true, "&aArena's area has been set!"),
    ARENA_LOBBY_AREA_SET(true, "&aArena's lobby area has been set!"),
    TELEPORTED_TO_ARENA(true, "&aYou have been teleported to the arena."),
    READY_ARENA(true, "&aThe arena is ready to be enabled."),
    ARENA_CREATED(true, "&aArena successfully created!"),
    ARENA_IMPORTED(true, "&aArena successfully imported!"),
    NO_AREA_SET(true, "&cNo area has been established!"),
    AREA_SHOWN(true, "&aArena's area has been shown!"),
    AREA_HIDE(true, "&aArena's area has been hidden!"),
    POINT1_SELECTED(true, "&bFirst position set to (%location%).", "location"),
    POINT2_SELECTED(true, "&bSecond position set to (%location%).", "location"),
    MAIN_LOBBY_SET(true, "&aMain Lobby has been set!"),
    LIST_HEADER("&9Protected Areas of %arena%", "arena"),
    ARENA_DELETED("&cArena has been deleted!"),
    ARENA_CLEARED("&cArena has been cleared!"),


    // -------------------------------------------- //
    // Setup Commands
    // -------------------------------------------- //

    SETUP_COMMANDS(CommentType.HEADER, "Setup Commands"),
    TREASURE_COMMADNS(CommentType.SUBHEADER_NOT_SPACED, "Treasure commands"),
    TREASURE_ADD_DESC("Adds a treasure chest by right clicking a chest"),
    YOU_MUST_RIGHT_CLICK_CHEST(true, "&cYou must right click a chest!"),
    TREASURE_ADD(true, "Right click a chest to make it a treasure chest"),
    TREASURE_ADDED(true, "&aA Treasure Chest has been created!"),
    ARENA_GROUP_NOT_FOUND("&cThe arena group doesn't exist!"),

    SIGN_COMMADNS(CommentType.SUBHEADER_SPACED, "Sign commands"),
    SIGN_COMMAND_DESC("Command for signs"),
    CREATE_SIGN("Creates a sign"),
    LIST_SIGNS("List all the signs"),
    SIGN_ADD_COMMAND_OVERRIDE("&cTo force add a sign for an arena that does not exist type &6/%base_command% signs add %arena% -c", "arena"),
    SELECT_SIGN(true, "&aSelect a sign"),
    SIGN_CREATED(true, "&aSign created!"),
    SIGN_REMOVED(true, "&cSign removed!"),

    KIT_COMMAND_HEADER(CommentType.SUBHEADER_SPACED, "Kit commands"),
    KIT_CREATE("Create a kit"),
    KIT_GUI("Opens the menu to edit kits"),
    KIT_ALREADY_EXISTS("&cKit already exists!"),
    NO_PERMISSION_KIT("&cYou do not have permission to choose this kit!"),
    KIT_CREATED("&aKit successfully created!"),
    KIT_DELETE("&cDelete"),

    OPTION_COMMAND(CommentType.SUBHEADER_SPACED, "Option commands"),
    OPTION_COMMAND_DESC("Modify all the additional arena options"),
    OPTION_ENABLE_COMMAND("Enable an option for the specified arena"),
    OPTION_DISABLE_COMMAND("Disable an option for the specified arena"),
    OPTION_LIST_COMMAND("List all the available options for the arena"),
    OPTION_SETTIME_COMMAND("Set the arena time of the day"),
    OPTION_SETENVIRONMENT_COMMAND("Set the arena environment"),
    OPTION_CANT_BE_ENABLED("This option cannot be enabled or disabled."),
    OPTION_NOT_FOUND("&cThis option is not found! &7Use &6/%base_command% setup options list"),
    OPTION_ENABLED("&6%option% &7has been &aenabled &7for arena &6%arena%", "option", "arena"),
    OPTION_DISABLED("&6%option% &7has been &cdisabled &7for arena &6%arena%", "option", "arena"),
    OPTION_TIME_CHANGED("&aTime has been set to: &6%time%", "time"),
    OPTION_TIME("&b&lTime Option &6| &aCurrently: &e%time%","time"),
    OPTION_TIME_DESC("&eChanges the time of the day the arena will be in"),
    OPTION_TIME_CURRENT_TIME("&7The current time of the arena is: &6%time%", "time"),
    OPTION_TIME_COMMAND("&7To change the time use &6/%base_command% setup options settime <arena> <time>"),
    OPTION_TIME_LIST("&eAvailable time of day: &6Sunrise, Day, Noon, Sunset, Night, Midnight"),
    OPTION_ENVIRONMENT_CHANGING("&6Changing environment, please wait..."),
    OPTION_ENVIRONMENT_CHANGED("&aEnvironment has been set to: &6%environment%", "environment"),
    OPTION_ENVIRONMENT("&b&lEnvironment Option &6| &aCurrently: &e%environment%","environment"),
    OPTION_ENVIRONMENT_DESC("&eChanges the arena environment"),
    OPTION_ENVIRONMENT_COMMAND("&7To change the environment use &6/%base_command% setup options setenvironment <arena> <environment>"),
    OPTION_ENVIRONMENT_LIST("&eAvailable environments: &6Normal, Nether, End"),
    OPTION_ENABLEABLE("&b&l%option% Option &6| &aCurrently: &e%status%", "option", "status"),
    OPTION_ENABLEABLE_COMMAND("&7To enable or disable use &6/%base_command% setup options <enable|disable> <arena> %option%", "option"),
    OPTION_TNT_EXPLOSIONS("Enable or disable tnt explosion block damage in the arena"),
    OPTION_TIME_INVALID("The specified time is invalid!"),


    // -------------------------------------------- //
    // Admin commands
    // -------------------------------------------- //

    ADMIN_COMMANDS(CommentType.HEADER, "Admin Commands"),
    ADMIN_COMMAND_DESC("Command for administrative commands"),
    RELOAD_COMMANDS(CommentType.SUBHEADER_NOT_SPACED, "Reload commands"),
    RELOAD_COMMAND_DESC("Reloads a module or various modules of the plugin"),
    VERSION_COMMAND_DESC("Prints out the version of the plugin"),
    FORCE_START("Force start an arena"),
    FORCE_END("Force end an arena"),
    RELOADABLE_MODULES("&aReloadable modules are:"),
    RELOADED_MODULE("&a%module% reloaded in %time%ms", "module", "time"),
    RELOADED_MODULE_FAIL("&cFailed to reload %module%! Please check console.", "module"),
    RELOAD_UNKNOWN("&cUnknown module", "module"),
    ARENA_ALREADY_STARTED("&cThe arena has already been started!"),
    ARENA_NOT_STARTED("&cThe arena is not in-game!"),
    ARENA_FORCE_STARTED("&aThe arena has been forced to start!"),
    ARENA_FORCE_ENDED("&aThe arena has been forced to end!"),

    CAGE_COMMANDS(CommentType.SUBHEADER_SPACED, "Cage commands"),
    GIVE_CAGE_COMMAND("Gives a cage to a player"),
    GIVE_ALL_CAGES_COMMAND("Gives all cage sto a player"),
    REMOVE_CAGE_COMMAND("Removes a cage from a player"),
    REMOVE_ALL_CAGES_COMMAND("Removes all cages from a player"),
    INVALID_CAGE("&cCage is invalid!"),
    PLAYER_OWNS_CAGE("&cPlayer owns cage!"),
    CAGE_GIVEN("&aCage successfully given"),
    CAGES_GIVEN("&aAll cages have been given"),
    PLAYER_DOESNT_OWNS_CAGE("&cPlayer doesn't own cage!"),
    CAGE_REMOVE("&aCage successfully removed"),
    CAGES_REMOVED("&aAll cages have been removed"),

    TREASURE_CHESTS_COMMANDS(CommentType.SUBHEADER_SPACED, "Treasure Chest commands"),
    TREASURE_CHESTS_COMMAND_DESCRIPTION("Command for treasure chests"),
    GIVE_TREASURE_CHESTS_COMMAND("Gives a treasure chest to a player (value can be negative to take)"),
    GIVE_ALL_TREASURE_CHESTS_COMMAND("Gives all treasure chest types to a player"),
    SET_TREASURE_CHEST_COMMAND("Sets the treasure chests amount of a type of a player"),
    SET_ALL_TREASURE_CHEST_COMMAND("Sets the treasure chests amount of a player for all types"),
    INVALID_TREASURE_CHEST("&Treasure chest is invalid!"),
    TREASURE_CHESTS_GIVEN(true, "&6%player% &7now has &6%amount% &7chests for type &6%type%", "player", "amount", "type"),

    STORAGE_COMMANDS(CommentType.SUBHEADER_SPACED, "Storage commands"),
    CONVERT_COMMAND("Converts the storage"),
    STORAGE_CANNOT_BE_SAME("Storage types must not be the same"),
    STORAGE_CAN_ONLY_BE("Storage can only be either MySQL or FlatFile"),
    STORAGE_TYPE("Type can only be Player or Kits"),

    BUNGEE_COMMANDS(CommentType.SUBHEADER_SPACED, "Bungee commands"),
    BUNGEE_COMMANDS_DESC("Bungee mode commands"),
    CHANGE_ARENA("Change arena map"),
    RESTART_SERVER("Stops the server when there are no players left"),
    INVALID_ARENA("&cInvalid Arena!"),
    NOT_IN_BUNGEE_MODE("&cPlugin is not in bungee mode!"),
    ADD_PLAYER("Adds player to which arena he'll join when he joins the server"),
    ARENA_CHANGED("&aArena changed!"),


    // -----------------------------------
    // Errors
    // -----------------------------------

    // Setup errors
    ERROR_HEADER(CoreComments.ERRORS),
    SETUP_ERRORS(CoreComments.SETUP_ERRORS),
    SETUP_ERROR_SET_AREA("&cMissing area! Use &e/%base_command% setup setarenaarea &cto set it"),
    SETUP_ERROR_LOBBY_LOCATION("&cMissing lobby location! Use &e/%base_command% setup setlobby"),
    SETUP_ERROR_SPECTATOR_LOCATION("&cMissing spectator location! Use &e/%base_command% setup setspec"),
    SETUP_ERROR_TEAM_MIN("&cMinimum amount of teams for a game is 2"),
    SETUP_ERROR_TEAM_SPAWN_NULL("%teamcolor%%teamname% &cspawn is missing!"),
    SETUP_ERROR_TEAM_POINT_AREA_NULL("%teamcolor%%teamname% &cmust have at least 1 point area!"),
    SETUP_ERROR_MIN_PLAYERS("&cMinimum amount of players must be set to 2 or higher!"),
    SETUP_ERROR_MIN_MAX("&cMinimum amount of players is set higher than the maximum amount of players!"),


    // -------------------------------------------- //
    // OTHER MESSAGES
    // -------------------------------------------- //

    CUBOID_TO_STRING("&7* &6%index%. &e%name% &6Cords: %cords%", "index", "name", "cords"),
    SELECT_AN_AREA(true, "&cPlease select a cuboid area using a bone"),
    SELECT_AN_AREA_WORLDEDIT("&cPlease select a cuboid area using worldedit selection"),
    PLAYER_JOINED(true, "%player% &ejoined the game (&d%players%&e/&d%max_players%&e)", "player", "players", "max_players"),
    CANNOT_PLACE_CHEST_ADJACENT(true, "&cYou cannot place a chest directly besides a map chest."),
    RETURN_PLAYABLE_AREA("&4&lWARNING: &cReturn to playable area"),
    YOU_HAVE_REACHED_PET_LIMIT("&cYou have reached the maximum amount of allowed pets."),
    LOCATION_TO_INFO("&6World: %world% &eCoords: %coords%", "world", "coords"),
    LOCATION_TO_COORDS("&eCoords: %coords%", "coords"),

    // -------------------------------------------- //
    // DEBUG
    // -------------------------------------------- //

    DEBUG_MODE("Toggle debug mode"),
    DEBUG_MODE_TOGGLE(true, "&7Debug mode %status%", "status"),
    EVENT_STATE_CHANGED(true, "&6%event% &7state changed to &6%state% &7by &6%plugin%", "event", "state", "plugin"),

    // -------------------------------------------- //
    // GAME MESSAGES
    // -------------------------------------------- //

    GAME(CommentType.HEADER, "Game messages"),
    WINNERS("&eWinners &7- &7%winners%", "winners"),
    FIRST_KILLER("&e1st Killer &7- &7%killer% &7- %kills%", "killer", "kills"),
    SECOND_KILLER("&62nd Killer &7- &7%killer% &7- %kills%", "killer", "kills"),
    THIRD_KILLER("&c3rd Killer &7- &7%killer% &7- %kills%", "killer", "kills"),
    YOU_KILLED_ACTION_BAR("&e⚔ You killed %player% ⚔", "player"),
    GAME_STARTING_TITLE("&c%game_title%", "seconds", "secondlang"),
    GAME_STARTING_SUBTITLE("&7Starting in &6%seconds% &7%secondlang%.", "seconds", "secondlang"),
    GAME_STARTING_CHAT(true, "&eThe game will start in &c%seconds% &e%secondlang%.", "seconds", "secondlang"),
    TEAM_CAGE_RELEASING_TITLE("&cPrepare", "seconds", "secondlang"),
    TEAM_CAGE_RELEASING_SUBTITLE("&7You will be released in &6%seconds% &7%secondlang%.", "seconds", "secondlang"),
    TEAM_CAGE_RELEASING_CHAT(true, "&eYou will be released in &c%seconds% &e%secondlang%.", "seconds", "secondlang"),
    SOLO_CAGE_RELEASING_TITLE("&f▶ %seconds% &f◀", "seconds", "secondlang"),
    SOLO_CAGE_RELEASING_SUBTITLE("&e⚠ &cTeaming in SOLO is bannable &e⚠", "seconds", "secondlang"),
    SOLO_CAGE_RELEASING_CHAT(true, "&eYou will be released in &c%seconds% &e%secondlang%.", "seconds", "secondlang"),
    SECONDS("seconds"),
    SECOND("second"),
    YOU_CANNOT_EXECUTE_COMMANDS("&cYou cannot this execute command in-game!"),
    COMPASS_TRACKER("&f&lNearest Player: &e%player%      &f&lDistance: &e%distance%", "player", "distance"),
    STARTING_MESSAGE("&a%arena% is starting! &eWant to join?", "arena"),
    STARTING_MESSAGE_CLICK("&b&lClick Here!"),
    STARTING_MESSAGE_HOVER("&aClick to join %arena%", "arena"),
    BORDER_SHRINK_SOON_CHAT("&4&lWARNING: &c&lBorder will start shrinking in 30 seconds!"),
    BORDER_SHRINK_SOON_SUBTITLE("&6Border will shrink soon!"),
    BORDER_SHRINKING("&4&lWARNING: &c&lBorder is shrinking!"),
    BORDER_SHRINKING_SUBTITLE("&6Border is shrinking!"),
    CHESTS_REFILLED("&eChests refilled!"),
    GRACE_PERIOD_ENDING("&eGrace period ending in &c%time% &e%secondlang%!", "time", "secondlang"),
    GRACE_PERIOD_ENDED("&cGrace period ended!"),
    DEATHMATCH_STARTING("&eDeathmatch starting in &c%time% &e%secondlang%", "time", "secondlang"),
    DEATHMATCH_STARTED("&cDeathmatch started!"),
    DEATHMATCH_YOU_CANNOT_THROW_TNT("&cYou cannot throw tnt while deathmatch is starting"),

    WINNER_TITLE("&6Victory!"),
    WINNER_SUBTITLE("&7You were the last man standing"),
    DEATH_TITLE("&cYou died!"),
    DEATH_SUBTITLE("&7You are now a spectator"),
    DEATH_MESSAGE("&c&lYou died! &aWant to play again?"),
    DEATH_MESSAGE_HOVER("&aClick to join a random arena"),
    DEATH_MESSAGE_CLICK("&b&lClick Here!"),
    LOSER_TITLE("&cGame Over"),
    LOSER_SUBTITLE("&7You weren't victorious"),

    LOBBY_CHOOSE_KIT("&6Choose kit"),
    LOBBY_CHOOSE_TEAM("&6Choose team"),
    LOBBY_CAGE_MENU("&fChange cage"),
    LOBBY_PROJECTILE_TRAIL_MENU("&fChange projectile trail"),
    LOBBY_WALK_TRAIL_MENU("&fChange walk trail"),
    LOBBY_ADMIN_MENU("&cAdmin menu"),
    LOBBY_LEAVE("&cLeave"),
    LOBBY_GAME_OPTIONS("&7Game Options"),

    // Admin main page
    GUI_ADMIN(CoreComments.GUI_ADMIN),
    GUI_ADMIN_TITLE("Arena admin menu"),
    GUI_ADMIN_START_COUNTDOWN("&aStart countdown"),
    GUI_ADMIN_STOP_COUNTDOWN("&cStop countdown"),

    // -------------------------------------------- //
    // SHOP messages
    // -------------------------------------------- //

    // General shop messages
    SHOP_MESSAGES(CommentType.HEADER, "Shop messages") ,
    GUI_SHOP_MESSAGES(CommentType.SUBHEADER_NOT_SPACED, "General shop messages"),
    GUI_SHOP_PRICE("&7Price: &a%price%", "price"),
    GUI_SHOP_FREE("free"),
    GUI_SHOP_CURRENCY("Dollars"),
    GUI_SHOP_WILL_BE_DEDUCTED("%amount%%currency% will be deducted from your account.", "amount", "currency"),
    GUI_SHOP_NOT_ENOUGH_MONEY("&cYou do not have enough money to buy this item."),
    GUI_SHOP_SELECTED("&aSelected"),
    GUI_SHOP_CLICK_TO_SELECT("&aClick to select"),
    GUI_SHOP_CLICK_TO_BUY("&aClick to buy"),
    GUI_SHOP_LEFT_CLICK_SELECT("&aLeft-click to select"),
    GUI_SHOP_RIGHT_CLICK("&aRight-click to buy uses"),
    GUI_SHOP_USES_LORE("&aUses: &a%uses%", "uses"),
    GUI_SHOP_YOU_BOUGHT("&aYou purchased %item%", "item"),
    GUI_SHOP_YOU_SELECTED("&aYou selected %item%", "item"),
    GUI_SHOP_NO_PERMISSION("&cYou need group %group%!", "group"),
    GUI_SHOP_NO_PERMISSION_STRING("&cYou don't have permission!"),
    GUI_SHOP_PURCHASED_USES("&aYou purchased %uses% use(s) for %item%", "uses", "item"),

    // Kit Shop
    GUI_USER_KIT_SHOP(CommentType.SUBHEADER_SPACED, "Kit Shop"),
    GUI_KIT_SHOP_TITLE("Kit shop"),
    GUI_KIT_SHOP_ALREADY_SELECTED("&cYou have already selected this kit as your default kit!"),
    GUI_KIT_SHOP_ALREADY_SELECTED_KIT("&cYou have already selected this kit!"),
    GUI_KIT_SHOP_YOU_PURCHASED_PERMANENT_KIT("&aYou purchased kit %kit%", "kit"),
    GUI_KIT_SHOP_PURCHASED_KIT_USES("&aYou purchased %uses% use(s) for kit %kit%", "uses", "kit"),
    GUI_KIT_SHOP_YOU_CHOSE_KIT_DEFAULT("&aYou selected kit %kit% as your default kit", "kit"),
    GUI_KIT_SHOP_YOU_CHOSE_KIT("&aYou selected kit %kit%", "kit"),
    GUI_KIT_SHOP_ECONOMY_PLUGIN_ERROR("&cThe kit is not free but there is nothing you can use to pay for it. Please contact the server administrator."),
    GUI_KIT_SHOP_NOT_ENOUGH_MONEY("&cYou do not have enough money to buy this kit."),
    GUI_KIT_SHOP_ERROR_OCCURRED("&cAn error has occurred while purchasing this kit."),
    GUI_KIT_SHOP_LEFT_CLICK("&aLeft-click to select as your default kit"),
    GUI_KIT_SHOP_LEFT_CLICK_SELECT("&aLeft-click to select"),
    GUI_KIT_SHOP_RIGHT_CLICK_OPTIONS("&aRight-click to show more options"),
    GUI_KIT_SHOP_CLICK_DEFAULT("&aClick to select as your default kit"),
    GUI_KIT_SHOP_CLICK_SELECT("&aClick to select"),
    GUI_KIT_SHOP_CLICK_BUY("&aClick to buy kit uses"),
    GUI_KIT_SHOP_CLICK_BUY_PERMANENT("&aClick to buy"),
    GUI_KIT_SHOP_KIT_USES("&7Kit uses: &a%uses%", "uses"),
    GUI_KIT_SHOP_OPTIONS_TITLE("&aOptions for %kit%", "kit"),
    GUI_KIT_OPTIONS_LAYOUT("&6Click to change the kit layout"),
    GUI_KIT_OPTIONS_PREVIEW("&6Click to preview kit"),

    // Shop commands
    SHOP_COMMANDS(CommentType.SUBHEADER_SPACED, "Shop commands"),
    SHOP_COMMANDS_DESC("Command for shop commands"),
    SHOP_NOT_FOUND("Shop does not exist"),
    SHOP_ITEM_NOT_FOUND("Shop item does not exist"),
    SHOP_ITEM_IS_PERMANENT("&cShop item is permanent"),
    SHOP_ITEMS_GIVE_COMMAND("Gives a specific shop item to player"),
    SHOP_ITEMS_GIVE_ALL_COMMAND("Gives all shop items to player (can be uses, permanents or both)"),
    SHOP_ITEMS_REMOVE_COMMAND("Removes a specific shop item to player"),
    SHOP_ITEMS_REMOVE_ALL_COMMAND("Removes all shop items from player"),
    SHOP_ITEMS_SET_COMMAND("Sets a specific shop item amount a player has"),
    SHOP_ITEMS_GIVE_MUST_BE_POSITIVE("&cAmount must be a positive number! use &6/%base_command% shop take &cif you want to remove items."),
    SHOP_ITEMS_TAKE_MUST_BE_POSITIVE("&cAmount must be a positive number!"),
    SHOP_ITEMS_GIVEN("&7You gave %player% %amount% %item% %shop%", "player", "amount", "item", "shop"),
    SHOP_ITEMS_GIVEN_PERMANENT("&7You gave %player% %item% %shop%", "player", "item", "shop"),
    SHOP_ITEMS_REMOVED("&7You removed %player% %amount% %item% %shop%", "player", "amount", "item", "shop"),
    SHOP_ITEMS_REMOVED_PERMANENT("&7You removed %player% %item% %shop%", "player", "item", "shop"),


    // Other shop messages
    OTHER_SHOP_MESSAGES(CommentType.SUBHEADER_SPACED, "Other shop messages"),
    SHOP_CAGE_TITLE("Choose a cage"),
    SHOP_CAGE_NAME("Cage"),
    SHOP_CAGE_COMMAND("Command for cages"),
    SHOP_PROJECTILE_TRAIL_TITLE("Choose a projectile trail"),
    SHOP_PROJECTILE_TRAIL_NAME("Projectile Trail"),
    SHOP_PROJECTILE_TRAIL_COMMAND("Command to choose projectile trails"),
    SHOP_WALK_TRAILS_TITLE("Choose a walk trail"),
    SHOP_WALK_TRAILS_NAME("Walk Trail"),
    SHOP_WALK_TRAILS_COMMAND("Command to choose walk trails"),
    SHOP_WIN_EFFECTS_TITLE("Choose a projectile trail"),
    SHOP_WIN_EFFECTS_TRAIL_NAME("Projectile Trail"),
    SHOP_WIN_EFFECTS_TRAIL_COMMAND("Command to choose projectile trails"),


    MENU_COMMANDS(CommentType.SUBHEADER_SPACED, "Menu commands"),
    MENU_COMMAND_DESC("Command for menus"),
    MENU_COMMAND_OPEN("Open a menu"),
    MENU_NOT_FOUND("That menu does not exist!"),
    STATS_COMMAND("Shows the stats for the player"),
    STATS_WEEK("&6Stats for this week:"),
    STATS_WEEK_PLAYER("&e%player% &6stats for this week:", "player"),
    STATS_WEEK_DISPLAY("&6%stat_name% &e%stat%", "stat_name", "stat"),
    STATS_ALL("&6Stats for all time:"),
    STATS_ALL_PLAYER("&e%player% &6stats for all time:", "player"),
    STATS_ALL_DISPLAY("&6%stat_name% &e%stat%", "stat_name", "stat"),

    // -----------------------------------
    // Leaderboard Messages
    // -----------------------------------

    LEADERBOARD_COMMANDS(CommentType.SUBHEADER_SPACED, "Leaderboard commands"),
    LEADERBOARD_COMMAND_DESC("Command for leaderboard related stuff"),
    LEADERBOARD_LIST("List all tracked statistics"),
    LEADERBOARD_MAX_PLACE("&cMax place is 10"),
    LEADERBOARD_STATISTIC_NOT_FOUND("&cStatistic does not exist!"),
    LEADERBOARD_PERIOD_NOT_FOUND("&cPeriod not found! &7Available periods: week, month, all"),
    LEADERBOARD_HOLOGRAM_DESC("Command for hologram leaderboards"),
    LEADERBOARD_HOLOGRAM_CREATE("Creates a new hologram"),
    LEADERBOARD_HOLOGRAM_NOT_FOUND("&cThe hologram &4%hologram% &cdoes not exist.", "hologram"),
    LEADERBOARD_HOLOGRAM_ALREADY_EXISTS("&cThe hologram already exists!"),
    LEADERBOARD_HOLOGRAM_CREATED("&aHologram Created!"),
    LEADERBOARD_HOLOGRAM_MOVE("Moves the hologram to your location"),
    LEADERBOARD_HOLOGRAM_MOVED("The hologram has been moved to your location"),
    LEADERBOARD_HOLOGRAM_LIST("List all the holograms"),
    LEADERBOARD_HOLOGRAM_LIST_TITLE("Holograms"),
    LEADERBOARD_HOLOGRAM_SETSIZE("Sets the amount of entries to display (max 10)"),
    LEADERBOARD_HOLOGRAM_SETSIZE_SUCCESS("Hologram will not display %size% entries", "size"),
    LEADERBOARD_HOLOGRAM_ADD("Add a stat to the hologram"),
    LEADERBOARD_HOLOGRAM_ADDED("&7Stat &6%stat_name% &7has been added to &7%hologram%", "stat_name", "hologram"),
    LEADERBOARD_HOLOGRAM_REMOVE("Removes a stat from the hologram"),
    LEADERBOARD_HOLOGRAM_REMOVE_INDEX("&cIndex out of bounds! &7Use &6%base_command% holo list <hologram> &7to display the indices."),
    LEADERBOARD_HOLOGRAM_REMOVED("&7Stat &6%stat_name% %period% &7has been removed from &7%hologram%", "stat_name", "hologram", "period"),
    LEADERBOARD_HOLOGRAM_DELETE("Deletes a hologram"),
    LEADERBOARD_HOLOGRAM_DELETE_CONFIRM("&7Type &6/%base_command% holo delete %hologram% -c &7to confirm the permanent deletion.", "hologram"),
    LEADERBOARD_HOLOGRAM_DELETED("&7The hologram has been deleted"),
    LEADERBOARD_SIGNS_DESC("Command for leaderboard signs"),
    LEADERBOARD_SIGNS_ADD("Adds a sign with the stat (need to right click sign)"),
    LEADERBOARD_SIGNS_CLICK_TO_ADD("&aRight-click a sign to add."),
    LEADERBOARD_SIGNS_ADDED("&aLeader sign has been created!"),
    LEADERBOARD_SIGNS_LIST("List all the signs"),
    LEADERBOARD_ATTACHMENTS_DESC("Command for sign attachments"),
    LEADERBOARD_ATTACHMENTS_ADD("Creates a new attachment (need to right click sign)"),
    LEADERBOARD_ATTACHMENTS_NOT_FOUND("&cAttachment not found!"),
    LEADERBOARD_ATTACHMENTS_NOT_USABLE("&cThis attachment is missing the following dependency: %dependency%", "dependency"),
    LEADERBOARD_ATTACHMENTS_RIGHT_CLICK("&aRight-click a leaderboard sign"),
    LEADERBOARD_ATTACHMENTS_ALREADY_ATTACHED("&cThe sign already had this attachment!"),
    LEADERBOARD_ATTACHMENTS_ADDED("&aAttachment added!"),
    LEADERBOARD_ATTACHMENTS_COULD_NOT_ADD("&cCould not find a suitable place for this attachment."),


    // -------------------------------------------- //
    // EXPERIENCE AND LEVELS
    // -------------------------------------------- //


    EXPERIENCE_SYSTEM(CommentType.SUBHEADER_SPACED, "Experience/Levels"),
    EXPERIENCE_COMMAND_DESC("Command for experience"),
    EXPERIENCE_POPULATE_DESC("Create and populate experience table from stats."),
    EXPERIENCE_POPULATE_TABLE_EXISTS("Table already exists! Please drop the table before continuing"),
    EXPERIENCE_POPULATE_DONE("&aDone populating!"),
    EXPERIENCE_POPULATE_CONFIRM("&cYou are about to populate the database with experience. Please confirm the following settings from the config are correct then run &6/%base_command% experience populate -c"),
    EXPERIENCE_POPULATE_CONFIG("&e&l%element%: &b%value%", "element", "value"),
    EXPERIENCE_EARNED("&b+%amount% experience", "amount"),

    // -----------------------------------
    // Quests Messages
    // -----------------------------------

    QUESTS_HEADER(CommentType.SUBHEADER_SPACED, "Quests"),
    QUEST_COMPLETED("&aQuest %quest% completed!", "quest"),
    QUESTS_COMMAND("Opens the quest menu"),
    GUI_QUEST_TITLE("&7Choose a quest"),
    GUI_QUEST_ITEM_NAME("&aQuest %quest%", "quest"),
    GUI_QUEST_STARTED("&aStarted Quest: &6%quest%", "quest"),
    GUI_QUEST_STATUS_START("&aClick to start quest!"),
    GUI_QUEST_STATUS_STARTED("&aStarted"),
    GUI_QUEST_STATUS_PROGRESS("&bProgress &7(&6%amount%&7/&6%goal%&7)", "amount", "goal"),
    GUI_QUEST_STATUS_DEADLINE("&7Deadline: &6%time%", "time"),
    GUI_QUEST_STATUS_COMPLETED("&aYou have completed this quest!"),
    GUI_QUEST_STATUS_COOLDOWN("&cThis quest is on cooldown!"),
    GUI_QUEST_STATUS_COOLDOWN_2("&cYou can start it again in %time%", "time"),
    GUI_QUEST_STATUS_EXP("&b+%amount% experience", "amount"),
    GUI_QUEST_STATUS_COINS("&6+%amount% coins", "amount"),

    // -----------------------------------
    // GUI messages
    // -----------------------------------

    // General Messages
    GUI_HEADER(CoreComments.GUI_MESSAGES),
    GENERAL_GUI(CoreComments.GUI_GENERAL_MESSAGES),
    GUI_ERROR_INIT("&cError initializing page"),
    GUI_NEXT_PAGE("&eClick to go to the next page (page %pagenumber%)", "pagenumber"),
    GUI_PREVIOUS_PAGE("&eClick to go to the previous page (page %pagenumber%)", "pagenumber"),
    GUI_YOU_ARE_ON("&eYou are on page %pagenumber%", "pagenumber"),
    GUI_ENABLED("&aenabled"),
    GUI_DISABLED("&cdisabled"),
    GUI_BACK("&cBack"),

    // Choose kit menu
    GUI_USER_CHOOSE_KIT(CoreComments.GUI_CHOOSE_KIT),
    GUI_USER_CHOOSE_TITLE("Choose kit"),
    GUI_USER_CHOOSE_ALREADY_BOUGHT("&cYou have already selected this kit!"),
    GUI_USER_CHOOSE_YOU_PURCHASED_PERMANENT_KIT("&aYou purchased kit %kit%", "kit"),
    GUI_USER_CHOOSE_YOU_PURCHASED_KIT_USES("&aYou purchased %uses% use(s) for kit %kit%", "uses", "kit"),
    GUI_USER_CHOOSE_YOU_CHOSE_KIT("&aYou chose kit %kit%", "kit"),
    GUI_USER_CHOOSE_ECONOMY_PLUGIN_ERROR("&cThe kit is not free but there is nothing you can use to pay for it. Please contact the server administrator."),
    GUI_USER_CHOOSE_NOT_ENOUGH_MONEY("&cYou do not have enough money to buy this kit."),
    GUI_USER_CHOOSE_ERROR_OCCURRED("&cAn error has occurred while purchasing this kit."),

    // Edit Layout menu
    GUI_USER_EDIT_KIT_LAYOUT(CoreComments.GUI_EDIT_KIT_LAYOUT),
    GUI_USER_EDIT_KIT_LAYOUT_TITLE("Edit layout for kit %kit%", "kit"),
    GUI_USER_EDIT_KIT_LAYOUT_SAVED("&aLayout saved for kit %kit%", "kit"),
    GUI_USER_EDIT_KIT_LAYOUT_SAVE("&aSave Kit"),

    // Choose arena menu
    GUI_CHOOSE_LOCALE(CoreComments.GUI_CHOOSE_LOCALE),
    GUI_CHOOSE_LOCALE_TITLE("Choose a language"),
    GUI_CHOOSE_LOCALE_SELECTED("&aSelected"),
    GUI_CHOOSE_LOCALE_CHANGED(true, "&aLanguage changed!"),
    GUI_CHOOSE_LOCALE_NOT_CHANGED(true, "&cYou already selected this language!"),

    GUI_USER_JOIN_ARENA(CoreComments.GUI_USER_CHOOSE_TEAM),
    GUI_USER_JOIN_ARENA_TITLE("Arenas"),
    GUI_USER_JOIN_ARENA_CLICK_TO_SPECTATE("&aClick to spectate!"),
    GUI_USER_JOIN_ARENA_CLICK_TO_JOIN("&aClick to join!"),
    GUI_USER_JOIN_ARENA_ARENA_FULL("&cThe arena is full!"),

    // Choose team menu
    GUI_USER_CHOOSE_TEAM(CoreComments.GUI_USER_CHOOSE_TEAM),
    GUI_USER_CHOOSE_TEAM_TITLE("Choose Team"),
    GUI_USER_CHOOSE_TEAM_YOU_ARE_ALREADY_IN("&cYou are already in %teamcolor%%teamname%", "teamcolor", "teamname"),
    GUI_USER_CHOOSE_TEAM_YOU_JOINED("&aYou joined %teamcolor%%teamname%", "teamcolor", "teamname"),
    GUI_USER_CHOOSE_TEAM_FULL("%teamcolor%%teamname% &cis full!", "teamcolor", "teamname"),
    GUI_USER_CHOOSE_TEAM_NO_PERMISSION("You must purchase a rank to open the team selector"),
    
    // Item main page
    GUI_EDIT_ITEM(CoreComments.GUI_EDIT_ITEM),
    GUI_EDIT_ITEM_TITLE("Edit item"),
    GUI_EDIT_ITEM_CHANGE_ITEM("&aChange item"),
    GUI_EDIT_ITEM_CHANGE_DATA("&aChange data"),
    GUI_EDIT_ITEM_CHANGE_AMOUNT("&aChange amount"),
    GUI_EDIT_ITEM_FILTER("&6Filter"),

    // Change amount
    GUI_CHANGE_AMOUNT(CoreComments.GUI_CHANGE_AMOUNT),
    GUI_CHANGE_AMOUNT_TITLE("Change item amount"),
    GUI_CHANGE_AMOUNT_RESET("&cReset data"),

    // Choose kit to edit
    GUI_CHOOSE_KIT(CoreComments.GUI_CHOOSE_KIT_TO_EDIT),
    GUI_CHOOSE_KIT_TITLE("Choose kit to edit"),
    GUI_CHOOSE_KIT_EDIT_ITEM("&aEdit kit %kit%", "kit"),
    GUI_CHOOSE_KIT_CREATE_ITEM("&aCreate kit"),

    // Choose Potion Effect
    GUI_CHOOSE_POTION_EFFECT(CoreComments.GUI_CHOOSE_POTION_EFFECT),
    GUI_CHOOSE_POTION_EFFECT_TITLE("Choose a potion effect"),
    GUI_CHOOSE_POTION_EFFECT_ADD("&aClick to add"),
    GUI_CHOOSE_POTION_EFFECT_LORE("&7Duration: &f%duration%%newline%&7Amplifier: &f%amplifier%", "duration", "amplifier"),

    // Edit Kit UI
    GUI_EDIT_KIT(CoreComments.GUI_EDIT_KIT),
    GUI_EDIT_KIT_TITLE("Edit kit %kit%", "kit"),
    GUI_EDIT_KIT_RENAME("&6Rename"),
    GUI_EDIT_KIT_PRICE("&aPrice"),
    GUI_EDIT_KIT_PREVIEW("&6Preview"),
    GUI_EDIT_KIT_CHANGE_ITEMS("&bChange items"),
    GUI_EDIT_KIT_POTION_EFFECTS("&dPotion effects"),
    GUI_EDIT_KIT_DESCRIPTION("&7Description"),
    GUI_EDIT_KIT_DISPLAY_ITEM("&6Change display item"),
    GUI_EDIT_KIT_DELETE("&cDelete kit"),
    GUI_EDIT_KIT_SET_PERMISSION("&eSet Permission"),
    GUI_EDIT_KIT_ONE_TIME("&fOne-time kit"),
    GUI_EDIT_KIT_ITEMS_UPDATED("&aKit items updated!"),
    GUI_EDIT_KIT_KIT_DELETED("&cKit deleted!"),
    GUI_EDIT_KIT_KIT_RENAMED("&aKit renamed!"),

    // Edit Price UI
    GUI_EDIT_PRICE(CoreComments.GUI_EDIT_PRICE),
    GUI_EDIT_PRICE_TITLE("Edit kit price"),
    GUI_EDIT_PRICE_KIT_ITEM("Kit %kit%", "kit"),
    GUI_EDIT_PRICE_LORE("&7Price: &f%price%", "price"),
    GUI_EDIT_PRICE_RESET("&cReset price to 0 (free)"),

    // Kit Preview
    GUI_PREVIEW_KIT(CoreComments.GUI_PREVIEW_KIT),
    GUI_PREVIEW_KIT_TITLE("%kit% preview", "kit"),
    GUI_PREVIEW_KIT_COPY("&aReceive kit"),

    // Potion Effect UI
    GUI_EDIT_POTION_EFFECT(CoreComments.GUI_KIT_EDIT_POTION_EFFECT),
    GUI_EDIT_POTION_EFFECT_TITLE("Edit potion effect"),
    GUI_EDIT_POTION_EFFECT_AMPLIFIER_ITEM("&aChange amplifier"),
    GUI_EDIT_POTION_EFFECT_AMPLIFIER_LORE("&7Amplifier: &f%amplifier%", "amplifier"),
    GUI_EDIT_POTION_EFFECT_DURATION_ITEM("&aChange duration"),
    GUI_EDIT_POTION_EFFECT_DURATION_LORE("&7Duration: &f%duration%", "duration"),

    // Description editor line
    GUI_DESCRIPTION_EDITOR(CoreComments.GUI_DESCRIPTION_EDITOR),
    GUI_DESCRIPTION_EDITOR_TITLE("Edit line"),
    GUI_DESCRIPTION_EDITOR_RENAME("&aRename"),
    GUI_DESCRIPTION_EDITOR_DELETE("&cDelete line"),

    // Description editor main page
    GUI_DESCRIPTION_EDITOR_MAIN(CoreComments.GUI_DESCRIPTION_EDITOR_MAIN),
    GUI_DESCRIPTION_EDITOR_MAIN_TITLE("Edit description"),
    GUI_DESCRIPTION_EDITOR_MAIN_NEW("&aAdd new line"),

    // Treasure Chest UI
    GUI_TREASURE_CHEST(CommentType.SUBHEADER_SPACED, "Treasure chests"),
    GUI_TREASURE_CHEST_TITLE("Open a chest"),
    GUI_TREASURE_CHEST_ITEM_NAME("&a%currency%", "currency"),
    GUI_TREASURE_CHEST_ITEM_LORE("&aYou have %amount% %currency%%newline%Purchase", "amount", "currency"),

    // Buy Treasure Chest UI
    GUI_BUY_TREASURE_CHEST(CommentType.SUBHEADER_SPACED, "Treasure chests"),
    GUI_BUY_TREASURE_CHEST_TITLE("Buy Treasure Chests"),
    GUI_BUY_TREASURE_CHEST_YOU_PURCHASED("&aYou purchased %amount% chest(s) for chest %chest%", "amount", "chest"),
    GUI_BUY_TREASURE_CHEST_ECONOMY_PLUGIN_ERROR("&cThe item is not free but there is nothing you can use to pay for it. Please contact the server administrator."),
    GUI_BUY_TREASURE_CHEST_NOT_ENOUGH_MONEY("&cYou do not have enough money to buy this treasure chest."),
    GUI_BUY_TREASURE_CHEST_ERROR_OCCURRED("&cAn error has occurred while purchasing this treasure chest."),

    // Weather Vote UI
    GUI_WEATHER_VOTE(CommentType.SUBHEADER_SPACED, "Weather Vote UI"),
    GUI_WEATHER_VOTE_TITLE("Vote for the weather"),
    GUI_WEATHER_VOTE_SUN("&eSun"),
    GUI_WEATHER_VOTE_RAIN("&bRain"),
    GUI_WEATHER_VOTE_VOTED("You voted for &6%weather%", "weather"),
    GUI_WEATHER_ITEM("&6Weather"),
    GUI_WEATHER_NO_PERMISSION("&cYou do not have permission to open the weather menu"),

    // Time Vote UI
    GUI_TIME_VOTE(CommentType.SUBHEADER_SPACED, "Time Vote UI"),
    GUI_TIME_VOTE_TITLE("Vote for the time"),
    GUI_TIME_VOTE_MORNING("&eMorning"),
    GUI_TIME_VOTE_NOON("&6Noon"),
    GUI_TIME_VOTE_SUNSET("&7Sunset"),
    GUI_TIME_VOTE_MIDNIGHT("&8Midnight"),
    GUI_TIME_VOTE_VOTED("You voted for &6%time%", "time"),
    GUI_TIME_ITEM("&6Time"),
    GUI_TIME_NO_PERMISSION("&cYou do not have permission to open the time menu"),

    // Weather Vote UI
    GUI_GAME_OPTIONS(CommentType.SUBHEADER_SPACED, "Game Options UI"),
    GUI_GAME_OPTIONS_TITLE("Game Options"),

    // Random arena Mode
    CHOOSE_MAP_UI(CommentType.SUBHEADER_SPACED, "Choose Map UI"),
    CHOOSE_MAP_UI_TITLE("Choose a map"),
    CHOOSE_GROUP("Choose a group"),
    CHOOSE_MAP_BUTTON_NAME("&6%map%", "map"),
    CHOOSE_GROUP_BUTTON_NAME("&6%group%", "group"),
    CHOOSE_MAP_BUTTON_JOIN_DESC("&aClick to join"),
    CHOOSE_MAP_BUTTON_SELECT_DESC("&aClick to create an arena for this map"),
    CHOOSE_MAP_ARENA_CREATED("&aAn arena has been created for you with your map!"),
    CHOOSE_MAP_ARENA_QUEUED("&aPlease be patient while we create an arena for you.%newline% This may take a few minutes while we wait for a game to end."),
    PLAYER_QUEUED("&cYou cannot join an arena while you're in queue. To leave the queue, try to join again."),
    PLAYER_QUEUE_LEAVE("&cYou have left the queue for the map %map%", "map"),
    RANDOM_ARENA_MODE_COMMAND("Command for random arena mode"),
    RANDOM_ARENA_MODE_CHOOSE_GROUP_COMMAND("Open the menu to choose an arena"),
    RANDOM_ARENA_MODE_NOT_FOUND("&cGroup not found"),

    // Private arenas
    GUI_PRIVATE_ARENA(CommentType.SUBHEADER_SPACED, "Private arena"),
    GUI_PRIVATE_ARENA_TITLE("Private arena options"),
    GUI_PRIVATE_ARENA_HEAD("&a%player%'s private arena", "player"),
    GUI_PRIVATE_ARENA_RENAME("&6Rename arena"),
    GUI_PRIVATE_ARENA_MANAGE_TEAMS("&bManage Teams"),
    GUI_PRIVATE_ARENA_START_COUNTDOWN("&aStart countdown"),
    GUI_PRIVATE_ARENA_STOP_COUNTDOWN("&cStop countdown"),
    GUI_PRIVATE_ARENA_ADD_CO_HOST("&aAdd a co-host"),
    GUI_PRIVATE_ARENA_REMOVE_CO_HOST("&cRemove a Co-Host"),
    GUI_PRIVATE_ARENA_KICK_PLAYERS("&cKick players"),
    GUI_PRIVATE_ARENA_ADD_PLAYERS_TO_WHITELIST("&aAdds players to whitelist"),
    GUI_PRIVATE_ARENA_REMOVE_PLAYERS_FROM_WHITELIST("&cRemove players from whitelist"),
    GUI_PRIVATE_ARENA_PLAYERS("&cRemove a co-host from the private arena"),
    GUI_PRIVATE_ARENA_CHANGE_MAP("&6Change map"),
    GUI_PRIVATE_ARENA_OPTIONS("&eArena Options"),
    GUI_PRIVATE_ARENA_CLOSE("&6Close the private arena"),
    GUI_PRIVATE_ARENA_KICK("&cKick %player%", "player"),
    GUI_PRIVATE_ARENA_KICKED("&c%player% has been kicked", "player"),
    GUI_PRIVATE_ARENA_CANT_KICK("&cYou cannot kick this player because they're co-host or host."),
    GUI_PRIVATE_ARENA_GIVE_CO_HOST("&aMake %player% Co-Host", "player"),
    GUI_PRIVATE_ARENA_CO_HOST_MADE("&c%player% has been made Co-Host", "player"),
    GUI_PRIVATE_ARENA_DEMOTE_CO_HOST("&cDemote %player% from Co-Host", "player"),
    GUI_PRIVATE_ARENA_CO_HOST_REMOVED("&c%player% has been removed from Co-Host", "player"),
    GUI_PRIVATE_ARENA_ADD_PLAYER_TO_WHITELIST("&aAdd %player% to whitelist", "player"),
    GUI_PRIVATE_ARENA_PLAYER_ADDED_TO_WHITELIST("&a%player% has been added to the whitelist", "player"),
    GUI_PRIVATE_ARENA_REMOVE_PLAYER_FROM_WHITELIST("&cRemove %player% from whitelist", "player"),
    GUI_PRIVATE_ARENA_PLAYER_REMOVED_ROM_WHITELIST("&c%player% has been removed from the whitelist", "player"),
    GUI_PRIVATE_ARENA_ADD_PLAYER_TO_WHITELIST_BY_NAME("&aAdd a player to the whitelist by name"),
    GUI_PRIVATE_ARENA_COMMAND("Creates a private arena"),
    GUI_PRIVATE_ARENA_LOBBY_MENU("&cHost menu"),
    GUI_PRIVATE_ARENA_WHITELIST("&fWhitelist"),
    GUI_PRIVATE_ARENA_PUBLIC_JOIN("&6Public Game"),
    GUI_PRIVATE_ARENA_PUBLIC_SPECTATE("&6Anyone Can Spectate"),
    GUI_PRIVATE_ARENA_TEAM_SELECTOR("&6Players can use team select"),
    GUI_PRIVATE_ARENA_STATS("&aGame stats"),
    GUI_PRIVATE_ARENA_NO_PERMISSION("&cYou do not have permission to change this option"),
    GUI_PRIVATE_ARENA_TEAM_SELECTION_DISABLED("&cThe host has disabled team selection for this arena."),
    GUI_PRIVATE_ARENA_SELECT_TEAM("Select a team to move players"),
    GUI_PRIVATE_ARENA_CURRENT_TEAM("&7Currently in: %team%", "team"),
    GUI_PRIVATE_ARENA_CURRENT_SPEC("&7Currently in: &7Spectators"),
    GUI_PRIVATE_ARENA_NO_CURRENT_TEAM("&7Currently in: &fnone"),
    GUI_PRIVATE_ARENA_CLICK_TO_REMOVE_TEAM("&eClick to remove from %team%", "team"),
    GUI_PRIVATE_ARENA_CLICK_TO_CHANGE_TEAM("&eClick to change to %team%", "team"),
    GUI_PRIVATE_ARENA_CLICK_TO_CHANGE_SPEC("&eClick to change to spectators"),
    GUI_PRIVATE_ARENA_CLICK_TO_CHANGE_PLAYER("&eClick to change to player")
    ;

    private String defaultMessage;
    private Map<Locale, String> message;
    private Map<Locale, String> rawMessage;
    private boolean prefix;
    private IComment comment;
    private String[] arguments;

    CoreLang(CoreComments CoreComments) {
        this.comment = CoreComments;
    }

    CoreLang(CommentType commentType, String title) {
        this.comment = new LangComment(commentType, title);
    }

    CoreLang(String defaultMessage, String... arguments) {
        this(false, defaultMessage, arguments);
    }

    CoreLang(boolean prefix, String defaultMessage, String... arguments) {
        this.defaultMessage = defaultMessage;
        this.prefix = prefix;
        this.message = new HashMap<>();
        this.rawMessage = new HashMap<>();
        this.arguments = arguments;
        setMessage(CoreLocaleManager.getLocale("en"), defaultMessage);
    }

    public String getMessage(Locale locale) {
        return (prefix ? CoreLang.PREFIX.getMessage(locale) : "") + message.get(locale);
    }

    @Override
    public String getRawMessage(Locale locale) {
        return rawMessage.get(locale);
    }

    @Override
    public void setMessage(Locale locale, String message) {
        this.message.put(locale, ChatColorEscaper.toColor(message)
                .replace("%newline%", "\n")
                .replace("%plugin_prefix%", PluginConfig.LANG_PREFIX)
                .replace("%game_title%", PluginConfig.GAME_TITLE)
                .replace("%base_command%", PluginConfig.BASE_COMMAND));
        this.rawMessage.put(locale, message);
    }

    public void transferMessage(CoreLang lang) {
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
