package me.patothebest.gamecore.util;

public interface ThrowableCallback<T, E extends Throwable> {

    void call(T t) throws E;

}