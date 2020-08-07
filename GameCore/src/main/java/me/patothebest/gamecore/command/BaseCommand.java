package me.patothebest.gamecore.command;

import me.patothebest.gamecore.treasure.TreasureCommand;
import me.patothebest.gamecore.sign.SignCommand;

/**
 * An interface declaring the base command of the plugin
 * <p>
 * This class can be used on the {@link ChildOf} @interface
 * when you want a command to be a subcommand of the base command.
 * <p>
 * Some examples of {@link ChildOf} using this class are
 * <ul>
 *     <li>{@link SignCommand.Parent}</li>
 *     <li>{@link TreasureCommand.Parent}</li>
 * </ul>
 */
public interface BaseCommand {
}
