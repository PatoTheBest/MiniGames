package me.patothebest.gamecore.commands;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.command.BaseCommand;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.modules.ParentCommandModule;
import me.patothebest.gamecore.util.Callback;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

@ChildOf(BaseCommand.class)
@Singleton
public class ConfirmCommand implements ParentCommandModule {

    private final Cache<CommandSender, Callback<CommandSender>> actionCache = CacheBuilder
            .newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build();

    @Command(
            aliases = {"confirm"},
            min = 0,
            max = 1,
            langDescription = @LangDescription(
                    element = "CONFIRM_DESC",
                    langClass = CoreLang.class
            )
    )
    public void confirm(CommandContext args, CommandSender sender) throws CommandException {
        Callback<CommandSender> callback = actionCache.getIfPresent(sender);
        CommandUtils.validateNotNull(callback, CoreLang.NO_CONFIRMATION);

        callback.call(sender);
    }

    public void addConfiration(CommandSender sender, Callback<CommandSender> callback) {
        actionCache.put(sender, callback);
        CoreLang.CONFIRM.sendMessage(sender);
    }
}
