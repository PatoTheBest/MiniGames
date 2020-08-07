package me.patothebest.gamecore.modules;

import me.patothebest.gamecore.util.Priority;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Defines the priority in which the module will be reloaded
 * This annotation is optional and must be used exclusively
 * on the class which implements {@link ReloadableModule}
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
public @interface ReloadPriority {

    /**
     * Gets the priority the module will be reloaded in
     *
     * @return the priority the module should be reloaded in
     */
    Priority priority();

}
