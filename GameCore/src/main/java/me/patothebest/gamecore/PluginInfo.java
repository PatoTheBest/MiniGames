package me.patothebest.gamecore;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PluginInfo {

    String pluginName();

    String pluginTitle();

    String worldPrefix();

    String langPrefix();

    String gameTitle();

    String placeholderPrefix();

    String permissionPrefix();

    String sqlPrefix();

    String baseCommand();

    String loggerPrefix();

    String resourceId();

    String header();

}
