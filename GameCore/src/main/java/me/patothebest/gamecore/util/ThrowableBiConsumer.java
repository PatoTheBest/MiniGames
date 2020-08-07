package me.patothebest.gamecore.util;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ThrowableBiConsumer<K, V> extends BiConsumer<K, V> {

    default void accept(K k, V v) {
        try {
            acceptThrows(k, v);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    void acceptThrows(K k, V v) throws Exception;

}
