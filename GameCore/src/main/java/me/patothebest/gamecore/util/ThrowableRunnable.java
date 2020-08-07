package me.patothebest.gamecore.util;

@FunctionalInterface
public interface ThrowableRunnable<T extends Throwable> {

    void run() throws T;
}