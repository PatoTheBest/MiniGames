package me.patothebest.gamecore.util;

import java.util.ArrayList;
import java.util.List;

public class ThrowOnce {

    private static final List<Throwable> throwables = new ArrayList<>();

    public static void printOnce(Throwable throwable) {
        if(throwables.contains(throwable)) {
            return;
        }

        throwables.add(throwable);
        throwable.printStackTrace();
    }

    public static void throwOnce(Throwable throwable) {
        if(throwables.contains(throwable)) {
            return;
        }

        throwables.add(throwable);
        throw sneakyThrow(throwable);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> RuntimeException sneakyThrow(Throwable t) throws T {
        throw (T)t;
    }
}
