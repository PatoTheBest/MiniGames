package me.patothebest.gamecore.storage.converter;

import com.google.inject.Inject;
import me.patothebest.gamecore.commands.admin.AdminCommand;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.storage.StorageType;
import me.patothebest.gamecore.storage.split.SplitType;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

@ChildOf(AdminCommand.class)
public class ConverterCommand implements RegisteredCommandModule {

    private final StorageConverter storageConverter;
    private final PluginScheduler pluginScheduler;

    @Inject private ConverterCommand(StorageConverter storageConverter, PluginScheduler pluginScheduler) {
        this.storageConverter = storageConverter;
        this.pluginScheduler = pluginScheduler;
    }

    @Command(
            aliases = {"convert"},
            usage = "<soure(mysql|flatfile)> <destination(mysql|flatfile)> <players|kits>",
            min = 3,
            max = 3,
            langDescription = @LangDescription(
                    element = "CONVERT_COMMAND",
                    langClass = CoreLang.class
            )
    )
    public List<String> convert(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            switch (args.getSuggestionContext().getIndex()) {
                case 0:
                case 1:
                    CommandUtils.complete(args.getString(args.getSuggestionContext().getIndex()), new StorageType[]{StorageType.FLATFILE, StorageType.MYSQL});
                    break;
                case 2:
                    CommandUtils.complete(args.getString(2), SplitType.values());
                    break;
            }

            return null;
        }

        StorageType source = Utils.getEnumValueFromString(StorageType.class, args.getString(0));
        StorageType dest = Utils.getEnumValueFromString(StorageType.class, args.getString(1));
        SplitType splitType = Utils.getEnumValueFromString(SplitType.class, args.getString(2));

        CommandUtils.validateNotNull(source, CoreLang.STORAGE_CAN_ONLY_BE);
        CommandUtils.validateNotNull(dest, CoreLang.STORAGE_CAN_ONLY_BE);
        CommandUtils.validateNotNull(splitType, CoreLang.STORAGE_TYPE);
        CommandUtils.validateTrue(source != dest, CoreLang.STORAGE_CANNOT_BE_SAME);

        pluginScheduler.runTaskAsynchronously(() -> storageConverter.convert(splitType, source, dest));
        return null;
    }
}
