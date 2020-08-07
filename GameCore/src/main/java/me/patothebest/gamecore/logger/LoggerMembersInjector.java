package me.patothebest.gamecore.logger;

import com.google.inject.MembersInjector;

import java.lang.reflect.Field;

class LoggerMembersInjector<T> implements MembersInjector<T> {

    private final Field field;
    private final Logger logger;

    LoggerMembersInjector(Field field, Logger logger) {
        this.field = field;
        this.logger = logger;
        field.setAccessible(true);
    }

    public void injectMembers(T t) {
        try {
            field.set(t, logger);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}