package me.patothebest.gamecore.util;

@FunctionalInterface
public interface IndexedFunction<T, R> {
    R apply(T t, int index);
}