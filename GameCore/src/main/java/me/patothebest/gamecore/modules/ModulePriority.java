package me.patothebest.gamecore.modules;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.util.Priority;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines the priority in which the module will be loaded
 * in on the {@link CorePlugin#onEnable()}. This annotation
 * is optional and must be used exclusively on the class
 * which implements {@link Module}
 * <p>
 * First priority to the last priority executed:
 * <ol>
 *  <li>LOWEST
 *  <li>LOW
 *  <li>NORMAL
 *  <li>HIGH
 *  <li>HIGHEST
 * </ol>
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ModulePriority {

    /**
     * Gets the priority the module will be loaded in
     *
     * @return the priority the module should be loaded in
     */
    Priority priority();

}