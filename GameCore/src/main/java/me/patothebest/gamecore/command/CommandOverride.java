package me.patothebest.gamecore.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines a command as one that'll override any axisting command
 * with the aliases
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandOverride {
}
