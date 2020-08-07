package me.patothebest.gamecore.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines a command that will be hidden from the help menu
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HiddenCommand {
}
