package me.patothebest.gamecore.commands.admin;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.command.ChatColor;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ParentCommandModule;
import me.patothebest.gamecore.modules.ReloadPriority;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.storage.StorageManager;
import me.patothebest.gamecore.util.CommandUtils;
import me.patothebest.gamecore.util.Priority;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

@Singleton
@ChildOf(AdminCommand.class)
@ModuleName("General Admin Command")
public class GeneralAdminCommand implements ParentCommandModule, ActivableModule {

    private final List<ReloadableModule> reloadableModules = new ArrayList<>();
    private final List<String> reloadableModuleNames = new ArrayList<>();
    private final CorePlugin corePlugin;
    private final StorageManager storageManager;

    @Inject private GeneralAdminCommand(CorePlugin corePlugin, StorageManager storageManager) {
        this.corePlugin = corePlugin;
        this.storageManager = storageManager;
    }

    @Override
    public void onPostEnable() {
        corePlugin.getModuleInstances().forEach(classModuleEntry -> {
            if(classModuleEntry.getValue() instanceof ReloadableModule) {
                reloadableModules.add((ReloadableModule) classModuleEntry.getValue());
                reloadableModuleNames.add(((ReloadableModule) classModuleEntry.getValue()).getReloadName());
            }
        });

        reloadableModules.sort((module1, module2) -> {
            Priority module1Priority = Priority.NORMAL;
            Priority module2Priority = Priority.NORMAL;

            if(module1.getClass().isAnnotationPresent(ReloadPriority.class)) {
                module1Priority = module1.getClass().getAnnotation(ReloadPriority.class).priority();
            }

            if(module2.getClass().isAnnotationPresent(ReloadPriority.class)) {
                module2Priority = module2.getClass().getAnnotation(ReloadPriority.class).priority();
            }

            return module1Priority.compareTo(module2Priority);
        });

        reloadableModuleNames.add("all");
    }

    @Command(
            aliases = {"reload", "rl"},
            usage = "<module|all>",
            max = 1,
            langDescription = @LangDescription(
                    element = "RELOAD_COMMAND_DESC",
                    langClass = CoreLang.class
            )
    )
    public List<String> onReload(CommandContext args, CommandSender sender) throws CommandException {
        if(args.getSuggestionContext() != null) {
            return CommandUtils.complete(args.getString(0), reloadableModuleNames);
        }

        if(args.argsLength() == 0) {
            CoreLang.RELOADABLE_MODULES.replaceAndSend(sender);
            for (String reloadableModuleName : reloadableModuleNames) {
                sender.sendMessage(ChatColor.BLUE + "- " + ChatColor.YELLOW + reloadableModuleName);
            }
            return null;
        }

        CommandUtils.validateTrue(reloadableModuleNames.contains(args.getString(0)), CoreLang.RELOAD_UNKNOWN);

        if(args.getString(0).equalsIgnoreCase("all")) {
            long total = 0;
            for (ReloadableModule reloadableModule : reloadableModules) {
                long reloadTime = reloadModule(reloadableModule);

                if(reloadTime != -1) {
                    total += reloadTime;
                    CoreLang.RELOADED_MODULE.replaceAndSend(sender, Utils.capitalizeFirstLetter(reloadableModule.getReloadName()), reloadTime);
                } else {
                    CoreLang.RELOADED_MODULE_FAIL.replaceAndSend(sender, Utils.capitalizeFirstLetter(reloadableModule.getReloadName()));
                }
            }

            CoreLang.RELOADED_MODULE.replaceAndSend(sender, "All", total);
        } else {
            for (ReloadableModule reloadableModule : reloadableModules) {
                if(reloadableModule.getReloadName().equalsIgnoreCase(args.getString(0))) {
                    long reloadTime = reloadModule(reloadableModule);

//                    if(reloadableModule instanceof ShopManager) {
//                        reloadTime += reloadModule(storageManager); // Fix for shop object mapping
//                    }

                    if(reloadTime != -1) {
                        CoreLang.RELOADED_MODULE.replaceAndSend(sender, Utils.capitalizeFirstLetter(reloadableModule.getReloadName()), reloadTime);
                    } else {
                        CoreLang.RELOADED_MODULE_FAIL.replaceAndSend(sender, Utils.capitalizeFirstLetter(reloadableModule.getReloadName()));
                    }
                }
            }
        }
        return null;
    }

    @Command(
            aliases = {"version", "ver"},
            max = 0,
            langDescription = @LangDescription(
                    element = "VERSION_COMMAND_DESC",
                    langClass = CoreLang.class
            )
    )
    public void verion(CommandContext args, CommandSender sender) throws CommandException {
        URLClassLoader cl = (URLClassLoader) getClass().getClassLoader();
        try {
            // get the MANIFEST.MF
            URL url = cl.findResource("META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(url.openStream());

            // send the details of the build
            sender.sendMessage(format("Version: " + org.bukkit.ChatColor.GOLD + manifest.getMainAttributes().getValue("Implementation-Version")));
            sender.sendMessage(format("Built By: " + org.bukkit.ChatColor.GOLD + manifest.getMainAttributes().getValue("Built-By")));
            sender.sendMessage(format("Built On: " + org.bukkit.ChatColor.GOLD + manifest.getMainAttributes().getValue("Build-Time")));
            sender.sendMessage(format("Built With: " + org.bukkit.ChatColor.GOLD + "Java " + manifest.getMainAttributes().getValue("Build-Jdk")));
        } catch (IOException ignored) {
        }
    }

    private String format(String s) {
        return org.bukkit.ChatColor.DARK_GRAY + "[" + org.bukkit.ChatColor.RED + PluginConfig.LANG_PREFIX + org.bukkit.ChatColor.DARK_GRAY + "] » " + org.bukkit.ChatColor.GRAY + s;
    }

    private long reloadModule(ReloadableModule reloadableModule) {
        long start = System.currentTimeMillis();
        try {
            reloadableModule.onReload();
            return System.currentTimeMillis() - start;
        } catch (Throwable t) {
            return -1;
        }
    }
}
