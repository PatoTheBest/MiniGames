package me.patothebest.gamecore.injector;

import com.google.inject.Injector;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * An adpater for the injector from the command framework.
 */
public class GuiceInjectorAdapter implements me.patothebest.gamecore.command.Injector {

    /** The injector. */
    private final Injector injector;

    /**
     * Instantiates a new guice injector adapter.
     *
     * @param injector the injector
     */
    @Inject public GuiceInjectorAdapter(Injector injector) {
        this.injector = injector;
    }

    /* (non-Javadoc)
     * @see me.patothebest.core.command.Injector#getProviderOrNull(java.lang.Class)
     */
    @Override
    public <T> Provider<? extends T> getProviderOrNull(Class<T> cls) {
        return injector.getProvider(cls);
    }

    /* (non-Javadoc)
     * @see me.patothebest.core.command.Injector#getInstance(java.lang.Class)
     */
    @Override
    public Object getInstance(Class<?> cls) {
        return injector.getInstance(cls);
    }
}
