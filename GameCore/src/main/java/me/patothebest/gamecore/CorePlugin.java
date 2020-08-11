package me.patothebest.gamecore;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandNumberFormatException;
import me.patothebest.gamecore.command.CommandPermissionsException;
import me.patothebest.gamecore.command.CommandUsageException;
import me.patothebest.gamecore.command.MissingNestedCommandException;
import me.patothebest.gamecore.command.WrappedCommandException;
import me.patothebest.gamecore.command.impl.CommandManager;
import me.patothebest.gamecore.injector.BukkitInjector;
import me.patothebest.gamecore.injector.GuiceInjectorAdapter;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.modules.ModulePriority;
import me.patothebest.gamecore.modules.ParentCommandModule;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.selection.DefaultSelectionManager;
import me.patothebest.gamecore.selection.SelectionManager;
import me.patothebest.gamecore.timings.TimingsData;
import me.patothebest.gamecore.timings.TimingsManager;
import me.patothebest.gamecore.util.Priority;
import me.patothebest.gamecore.util.StringUtil;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.world.DefaultWorldHandler;
import me.patothebest.gamecore.world.WorldHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CorePlugin extends JavaPlugin {

    private final CommandManager commandManager = new CommandManager(this);
    private final List<Class<? extends Module>> modules = new ArrayList<>();
    private final List<Map.Entry<Class<? extends Module>, Module>> moduleInstances = new ArrayList<>();
    private final Map<Class<? extends Module>, ModuleName> moduleNames = new HashMap<>();

    private Level loggingLevel = Level.INFO;
    @Inject private Injector injector;
    @InjectLogger(name = "Core") private Logger logger;

    // Not final in case WorldEdit is present
    private SelectionManager selectionManager = new DefaultSelectionManager();
    private WorldHandler worldHandler = new DefaultWorldHandler();

    public final void configureCore(Binder binder) {
        binder.install(new CoreModule<>(this));
        configure(binder);
    }

    public abstract void configure(Binder binder);

    @Override
    public void onLoad() {
        PluginConfig.parse(getClass().getAnnotation(PluginInfo.class));
    }

    @Override
    public final void onEnable() {
        final long currentTime = System.currentTimeMillis();

        for (String s : PluginConfig.HEADER.split("\n")) {
            Bukkit.getConsoleSender().sendMessage("§b" + s);
        }

        Bukkit.getConsoleSender().sendMessage("§3[§b" + this.getDescription().getName() + " " + this.getDescription().getVersion() + "§3] §e");
        Bukkit.getConsoleSender().sendMessage("§e=== ENABLE START ===");

        if(!getServer().getPluginManager().isPluginEnabled("Vault")) {
            Bukkit.getConsoleSender().sendMessage("§cVault not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        File configFile = new File(Utils.PLUGIN_DIR, "config.yml");
        if (configFile.exists()) {
            String configContents = Utils.readFileAsString(configFile);
            Matcher matcher = Pattern.compile("log-level: (\\w+)", Pattern.MULTILINE).matcher(configContents);
            if (matcher.find()) {
                String loggingLevelName = matcher.group(1);
                loggingLevel = Level.parse(loggingLevelName);
                getLogger().log(Level.INFO, "Logging level set to: " + loggingLevelName);
            }
        }

        try {
            new BukkitInjector<>(this);
        } catch (Throwable t) {
            getLogger().log(Level.SEVERE, "Unable to create injector!", t);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Utils.setLogger(logger);
        logger.info(ChatColor.GREEN + "Created injector!");

        commandManager.getCommandManager().setInjector(new GuiceInjectorAdapter(injector));

        modules.sort((module1, module2) -> {
            Priority module1Priority = Priority.NORMAL;
            Priority module2Priority = Priority.NORMAL;

            if(module1.isAnnotationPresent(ModulePriority.class)) {
                module1Priority = module1.getAnnotation(ModulePriority.class).priority();
            }

            if(module2.isAnnotationPresent(ModulePriority.class)) {
                module2Priority = module2.getAnnotation(ModulePriority.class).priority();
            }

            return module1Priority.compareTo(module2Priority);
        });

        modules.forEach(moduleClass -> {
            moduleInstances.add(new AbstractMap.SimpleEntry<>(moduleClass, injector.getInstance(moduleClass)));
            if (moduleClass.isAnnotationPresent(ModuleName.class)) {
                moduleNames.put(moduleClass, moduleClass.getAnnotation(ModuleName.class));
            }
        });

        moduleInstances.forEach((entry) -> {
            Class<? extends Module> key = entry.getKey();
            Module module = entry.getValue();
            if (module instanceof ListenerModule) {
                registerListener((Listener) module);
                logFine("Registered listener %s", module.getClass());
//            } else if(module instanceof Listener) {
//                Utils.printError("Not registering listener " + module.getClass().getSimpleName(), "Class is not an instance of ModuleListener");
            }

            if (module instanceof RegisteredCommandModule) {
                commandManager.register(key);
                logFine("Registered command %s", module.getClass());
            }

            if (module instanceof ActivableModule) {
                try {
                    logFinest("Pre enabling module %s", module.getClass());
                    ((ActivableModule) module).onPreEnable();
                    logFiner("Pre enabled %s", module.getClass());
                } catch (Throwable t) {
                    logError("Error while pre enabling module %s", module.getClass());
                    t.printStackTrace();
                }
            }
        });

        moduleInstances.forEach((entry) -> {
            Module module = entry.getValue();
            if (module instanceof ActivableModule) {
                try {
                    logFinest("Enabling module %s", module.getClass());
                    ((ActivableModule) module).onEnable();
                    logFiner("Enabled %s", module.getClass());
                } catch (Throwable t) {
                    logError("Error while enabling module %s", module.getClass());
                    t.printStackTrace();
                }
            }
        });

        moduleInstances.forEach((entry) -> {
            Class<? extends Module> key = entry.getKey();
            Module module = entry.getValue();
            if (module instanceof ParentCommandModule) {
                commandManager.register(key);
                logFine("Registered parent command %s", module.getClass());
            }
        });

        moduleInstances.forEach((entry) -> {
            Module module = entry.getValue();
            if (module instanceof ActivableModule) {
                try {
                    logFinest("Post enabling module %s", module.getClass());
                    ((ActivableModule) module).onPostEnable();
                    logFiner("Post enabled %s", module.getClass());
                } catch (Throwable t) {
                    logError("Error while post enabling module %s", module.getClass());
                    t.printStackTrace();
                }
            }
        });

        try {
            commandManager.getCommandManager().processChildCommands();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not process child commands!", e);
        }

        registerListener(selectionManager);
        onPluginEnable();

        Bukkit.getConsoleSender().sendMessage("§e=== ENABLE §aCOMPLETE §e(Took §d" + (System.currentTimeMillis() - currentTime) + "ms§e) ===");
    }

    public void onPluginEnable() { }

    @Override
    public final void onDisable() {
        final long currentTime = System.currentTimeMillis();
        Bukkit.getConsoleSender().sendMessage("§3[§b" + this.getDescription().getName() + " " + this.getDescription().getVersion() + "§3] §e");
        Bukkit.getConsoleSender().sendMessage("§e=== DISABLE START ===");

        moduleInstances.forEach((entry) -> {
            Class<? extends Module> key = entry.getKey();
            Module module = entry.getValue();

            if (module instanceof ActivableModule) {
                try {
                    logFiner("Disabling module %s", key);
                    ((ActivableModule) module).onDisable();
                    logFine("Disabled module %s", key);
                } catch (Throwable t) {
                    logError("Error while disabling module %s", key);
                    t.printStackTrace();
                }
            }
        });

        onPluginDisable();

        Bukkit.getConsoleSender().sendMessage("§e=== DISABLE §aCOMPLETE §e(Took §d" + (System.currentTimeMillis() - currentTime) + "ms§e) ===");
    }

    public void onPluginDisable() { }

    private void logInfo(String message, Class<? extends Module> moduleClass) {
        log(Level.INFO, message, moduleClass);
    }

    private void logFine(String message, Class<? extends Module> moduleClass) {
        log(Level.FINE, message, moduleClass);
    }

    private void logFiner(String message, Class<? extends Module> moduleClass) {
        log(Level.FINER, message, moduleClass);
    }

    private void logFinest(String message, Class<? extends Module> moduleClass) {
        log(Level.FINEST, message, moduleClass);
    }

    private void log(Level level, String message, Class<? extends Module> moduleClass) {
        if (!moduleNames.containsKey(moduleClass)) {
            return;
        }

        StringBuilder sbuf = new StringBuilder();
        Formatter fmt = new Formatter(sbuf);
        fmt.format(message, moduleNames.get(moduleClass).value());
        logger.log(level, sbuf.toString());
    }

    private void logError(String message, Class<? extends Module> moduleClass) {
        String moduleName = moduleClass.getName();
        if (moduleNames.containsKey(moduleClass)) {
            moduleName = moduleNames.get(moduleClass).value();
        }

        StringBuilder sbuf = new StringBuilder();
        Formatter fmt = new Formatter(sbuf);
        fmt.format(message, moduleName);
        logger.log(Level.SEVERE, sbuf.toString());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        TimingsData timing = TimingsManager.create("Command /" + cmd.getName() + " " + StringUtil.joinString(args, " "));
        try {
            this.commandManager.getCommandManager().execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(CoreLang.NO_PERMISSION.getMessage(sender));
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(CoreLang.USAGE.getMessage(sender).replace("%usage%", e.getUsage()));
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(CoreLang.USAGE.getMessage(sender).replace("%usage%", e.getUsage()));
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(CoreLang.NOT_A_NUMBER.getMessage(sender));
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred while attempting to execute this command. If you are the system administrator, check the console for errors. If not, please report this to an administrator.");
                e.printStackTrace();
            }
        } catch (CommandNumberFormatException e) {
            sender.sendMessage(CoreLang.NOT_A_NUMBER.getMessage(sender));
        } catch (CommandException e) {
            if (e.getLang() != null) {
                sender.sendMessage(e.getLang().getMessage(sender));
            } else {
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
        }
        timing.stop(50);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        try {
            return this.commandManager.getCommandManager().complete(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(CoreLang.NO_PERMISSION.getMessage(sender));
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(CoreLang.USAGE.replace(sender, e.getUsage()));
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(CoreLang.USAGE.replace(sender, e.getUsage()));
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(CoreLang.NOT_A_NUMBER.getMessage(sender));
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred while attempting to tab complete. If you are the system administrator, check the console for errors. If not, please report this to an administrator.");
                e.printStackTrace();
            }
        } catch (CommandNumberFormatException e) {
            sender.sendMessage(CoreLang.NOT_A_NUMBER.getMessage(sender));
        } catch (CommandException e) {
            if (e.getLang() != null) {
                sender.sendMessage(e.getLang().getMessage(sender));
            } else {
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
        }

        return Collections.emptyList();
    }

    public <T extends Event> T callEvent(T event) {
        // call an event
        getServer().getPluginManager().callEvent(event);
        return event;
    }

    public <T extends Listener> T registerListener(T listener) {
        // register a listener using this plugin
        getServer().getPluginManager().registerEvents(listener, this);
        return listener;
    }

    public final Class<? extends Module> registerModule(Class<? extends Module> module) {
        modules.add(module);
        return module;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + message);
    }

    public Level getLoggingLevel() {
        return loggingLevel;
    }

    public Injector getInjector() {
        return injector;
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public WorldHandler getWorldHandler() {
        return worldHandler;
    }

    public void setWorldHandler(WorldHandler worldHandler) {
        this.worldHandler = worldHandler;
    }

    public void setSelectionManager(SelectionManager selectionManager) {
        this.selectionManager = selectionManager;
    }

    public ClassLoader getPluginClassLoader() {
        return getClassLoader();
    }

    public List<Map.Entry<Class<? extends Module>, Module>> getModuleInstances() {
        return moduleInstances;
    }
}