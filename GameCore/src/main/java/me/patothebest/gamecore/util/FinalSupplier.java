package me.patothebest.gamecore.util;

import java.util.function.Supplier;

public class FinalSupplier<T> implements Supplier<T> {

    private final T object;

    public FinalSupplier(T object) {
        this.object = object;
    }

    @Override
    public T get() {
        return object;
    }
}
