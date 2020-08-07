package me.patothebest.gamecore.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowableConsumer<T> extends Consumer<T> {

    default void accept(T t) {
        try {
            acceptThrows(t);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    void acceptThrows(T t) throws Exception;

}
