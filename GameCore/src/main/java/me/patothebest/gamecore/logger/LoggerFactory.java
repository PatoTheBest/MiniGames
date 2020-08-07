package me.patothebest.gamecore.logger;

import me.patothebest.gamecore.CorePlugin;

import java.util.HashMap;
import java.util.Map;

class LoggerFactory {

    private final CorePlugin plugin;
    private final Map<String, Logger> loggerMap = new HashMap<>();

    LoggerFactory(CorePlugin plugin) {
        this.plugin = plugin;
    }

    public Logger getLogger(String name) {
        if(loggerMap.containsKey(name)) {
            return loggerMap.get(name);
        }

        Logger coreLogger = new Logger(plugin.getLogger(), name, plugin.getLoggingLevel());
        loggerMap.put(name, coreLogger);
        return coreLogger;
    }
}