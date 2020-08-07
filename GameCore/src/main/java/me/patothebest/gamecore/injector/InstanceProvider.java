package me.patothebest.gamecore.injector;

public interface InstanceProvider<T> {

    /**
     * Provides an instance of {@code T}. Must never return {@code null}.
     */
    T newInstance();

}
