package me.patothebest.gamecore;

public class PluginConfig {

    public static String PLUGIN_NAME = "Core";
    public static String PLUGIN_TITLE = "CORE";
    public static String WORLD_PREFIX = "Core_";
    public static String LANG_PREFIX = "Core";
    public static String GAME_TITLE = "Core";
    public static String PLACEHOLDER_PREFIX = "core";
    public static String PERMISSION_PREFIX = "core";
    public static String SQL_PREFIX = "core";
    public static String BASE_COMMAND = "";
    public static String LOGGER_PREFIX = "CORE";
    public static String RESOURCE_ID = "";
    public static String HEADER = "";

    static void parse(PluginInfo pluginInfo) {
        PLUGIN_NAME = pluginInfo.pluginName();
        PLUGIN_TITLE = pluginInfo.pluginTitle();
        WORLD_PREFIX = pluginInfo.worldPrefix();
        LANG_PREFIX = pluginInfo.langPrefix();
        GAME_TITLE = pluginInfo.gameTitle();
        PLACEHOLDER_PREFIX = pluginInfo.placeholderPrefix();
        PERMISSION_PREFIX = pluginInfo.permissionPrefix();
        SQL_PREFIX = pluginInfo.sqlPrefix();
        BASE_COMMAND = pluginInfo.baseCommand();
        LOGGER_PREFIX = pluginInfo.loggerPrefix();
        RESOURCE_ID = pluginInfo.resourceId();
        HEADER = pluginInfo.header();
    }

}
