package me.patothebest.gamecore.logger;

import me.patothebest.gamecore.PluginConfig;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class Logger extends java.util.logging.Logger {

    private final String prefix;

    Logger(java.util.logging.Logger parent, String name, Level loggingLevel) {
        super(PluginConfig.LOGGER_PREFIX, null);
        prefix = "[" + name + "] ";
        setParent(parent);
        setLevel(loggingLevel);
    }

    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(prefix + logRecord.getMessage());
        if (logRecord.getLevel().intValue() < Level.INFO.intValue()) {
            logRecord.setLevel(Level.INFO);
        }
        super.log(logRecord);
    }


    public void severe(String msg, Object... objects) {
        log(Level.SEVERE, msg, objects);
    }

    public void severe(String msg, Throwable thrown) {
        log(Level.SEVERE, msg, thrown);
    }

    public void severe(String msg, Throwable thrown, Object... objects) {
        log(Level.SEVERE, msg, thrown, objects);
    }

    public void warning(String msg, Object... objects) {
        log(Level.WARNING, msg, objects);
    }

    public void warning(String msg, Throwable thrown) {
        log(Level.WARNING, msg, thrown);
    }

    public void warning(String msg, Throwable thrown, Object... objects) {
        log(Level.WARNING, msg, thrown, objects);
    }

    public void info(String msg, Object... objects) {
        log(Level.INFO, msg, objects);
    }

    public void info(String msg, Throwable thrown) {
        log(Level.INFO, msg, thrown);
    }

    public void info(String msg, Throwable thrown, Object... objects) {
        log(Level.INFO, msg, thrown, objects);
    }

    public void config(String msg, Object... objects) {
        log(Level.CONFIG, msg, objects);
    }

    public void config(String msg, Throwable thrown) {
        log(Level.CONFIG, msg, thrown);
    }

    public void config(String msg, Throwable thrown, Object... objects) {
        log(Level.CONFIG, msg, thrown, objects);
    }

    public void fine(String msg, Object... objects) {
        log(Level.FINE, msg, objects);
    }

    public void fine(String msg, Throwable thrown) {
        log(Level.FINE, msg, thrown);
    }

    public void fine(String msg, Throwable thrown, Object... objects) {
        log(Level.FINE, msg, thrown, objects);
    }

    public void finer(String msg, Object... objects) {
        log(Level.FINER, msg, objects);
    }

    public void finer(String msg, Throwable thrown) {
        log(Level.FINER, msg, thrown);
    }

    public void finer(String msg, Throwable thrown, Object... objects) {
        log(Level.FINER, msg, thrown, objects);
    }

    public void finest(String msg, Object... objects) {
        log(Level.FINEST, msg, objects);
    }

    public void finest(String msg, Throwable thrown) {
        log(Level.FINEST, msg, thrown);
    }

    public void finest(String msg, Throwable thrown, Object... objects) {
        log(Level.FINEST, msg, thrown, objects);
    }

    private void log(Level level, String msg, Throwable thrown, Object[] objects) {
        if (!isLoggable(level)) {
            return;
        }
        
        LogRecord lr = new LogRecord(level, msg);
        lr.setThrown(thrown);
        lr.setParameters(objects);
        log(lr);   
    }
}
