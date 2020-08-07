package me.patothebest.gamecore.commands;

import com.google.inject.Inject;
import com.google.inject.Provider;
import me.patothebest.gamecore.combat.CombatEntry;
import me.patothebest.gamecore.command.ChatColor;
import me.patothebest.gamecore.command.HiddenCommand;
import me.patothebest.gamecore.cosmetics.cage.CageStructure;
import me.patothebest.gamecore.cosmetics.cage.model.CageModel;
import me.patothebest.gamecore.cosmetics.projectiletrails.ProjectileManager;
import me.patothebest.gamecore.cosmetics.shop.ShopFactory;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.selection.Selection;
import me.patothebest.gamecore.selection.SelectionManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandPermissions;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.event.arena.ArenaPhaseChangeEvent;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.timings.TimingsManager;
import me.patothebest.gamecore.util.CancellationDetector;
import me.patothebest.gamecore.util.CommandUtils;
import me.patothebest.gamecore.vector.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleName("Debug Command")
public class DebugCommand implements ActivableModule, ListenerModule {

    private final CancellationDetector<BlockBreakEvent> blockBreak = new CancellationDetector<>(BlockBreakEvent.class);
    private final CancellationDetector<BlockPlaceEvent> blockPlace = new CancellationDetector<>(BlockPlaceEvent.class);
    private final CancellationDetector<PlayerCommandPreprocessEvent> command = new CancellationDetector<>(PlayerCommandPreprocessEvent.class);
    private final CancellationDetector<PlayerInteractEvent> interactEvent = new CancellationDetector<>(PlayerInteractEvent.class);
    private final List<Player> debugPlayers = new ArrayList<>();

    private final PluginScheduler pluginScheduler;
    private final Provider<SelectionManager> selectionManagerProvider;
    private final Provider<NMS> nms;
    private final ShopFactory shopFactory;
    private final ProjectileManager arrowManager;
    private final PlayerManager playerManager;
    private final ArenaManager arenaManager;
    private final CommandsManager<CommandSender> commandsManager;

    @Inject
    private DebugCommand(PluginScheduler pluginScheduler, Provider<SelectionManager> selectionManagerProvider, Provider<NMS> nms, ShopFactory shopFactory, ProjectileManager arrowManager, PlayerManager playerManager, ArenaManager arenaManager, CommandsManager<CommandSender> commandsManager) {
        this.pluginScheduler = pluginScheduler;
        this.selectionManagerProvider = selectionManagerProvider;
        this.nms = nms;
        this.shopFactory = shopFactory;
        this.arrowManager = arrowManager;
        this.playerManager = playerManager;
        this.arenaManager = arenaManager;
        this.commandsManager = commandsManager;
    }

    @Override
    public void onEnable() {
        blockBreak.addListener((plugin, event, listener) -> {
            if (debugPlayers.contains(event.getPlayer())) {
                CoreLang.EVENT_STATE_CHANGED.replaceAndSend(event.getPlayer(),
                        event.getClass().getSimpleName(),
                        (event.isCancelled() ? ChatColor.RED + "cancelled" : ChatColor.GREEN + "not cancelled"),
                        listener.getClass().getName() + "(" + plugin.getDescription().getFullName() + ")");
            }
        });
        blockPlace.addListener((plugin, event, listener) -> {
            if (debugPlayers.contains(event.getPlayer())) {
                CoreLang.EVENT_STATE_CHANGED.replaceAndSend(event.getPlayer(),
                        event.getClass().getSimpleName(),
                        (event.isCancelled() ? ChatColor.RED + "cancelled" : ChatColor.GREEN + "not cancelled"),
                        listener.getClass().getName() + "(" + plugin.getDescription().getFullName() + ")");
            }
        });
        command.addListener((plugin, event, listener) -> {
            if (debugPlayers.contains(event.getPlayer())) {
                CoreLang.EVENT_STATE_CHANGED.replaceAndSend(event.getPlayer(),
                        event.getClass().getSimpleName(),
                        (event.isCancelled() ? ChatColor.RED + "cancelled" : ChatColor.GREEN + "not cancelled"),
                        listener.getClass().getName() + "(" + plugin.getDescription().getFullName() + ")");
            }
        });
        interactEvent.addListener((plugin, event, listener) -> {
            if (debugPlayers.contains(event.getPlayer())) {
                CoreLang.EVENT_STATE_CHANGED.replaceAndSend(event.getPlayer(),
                        event.getClass().getSimpleName(),
                        (event.isCancelled() ? ChatColor.RED + "cancelled" : ChatColor.GREEN + "not cancelled"),
                        listener.getClass().getName() + "(" + plugin.getDescription().getFullName() + ")");
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        debugPlayers.remove(event.getPlayer());
    }

    @EventHandler
    public void onPhaseChange(ArenaPhaseChangeEvent event) {
        for (Player player : debugPlayers) {
            player.sendMessage(CoreLang.PREFIX.getMessage(player) + "PhaseChange from: " + event.getOldPhase().getClass() + " to " + event.getNewPhase().getClass());
        }
    }

    @EventHandler
    public void onDeath(CombatDeathEvent event) {
        if (!debugPlayers.contains(event.getPlayer())) {
            return;
        }

        event.getPlayer().sendMessage("Death cause: " + event.getDeathCause().name());
        event.getPlayer().sendMessage("Killer: " + event.getKiller());
        event.getPlayer().sendMessage("Item: " + event.getItemKilledWith());
        event.getPlayer().sendMessage("Entries: ");
        for (CombatEntry combatEvent : event.getCombatEvents()) {
            event.getPlayer().sendMessage(combatEvent.getTick() + " - " +
                    combatEvent.getDamageCause().name() + " => " +
                    combatEvent.getDeathCause().name() + ": " +
                    combatEvent.getDamage() + " (" +
                    (Arrays.toString(combatEvent.getDamageOptions())) +
                    getName(combatEvent.getKiller()) +
                    getPlayerName(combatEvent.getPlayerKiller()) + ")");
        }
    }

    private String getPlayerName(WeakReference<Player> entityWeakReference) {
        return entityWeakReference == null ? null : entityWeakReference.get() == null ? null : entityWeakReference.get().getName();
    }

    private String getName(WeakReference<Entity> entityWeakReference) {
        return entityWeakReference == null ? null : entityWeakReference.get() == null ? null : entityWeakReference.get().getName();
    }

    @Command(
            aliases = {"debug"},
            max = 0,
            langDescription = @LangDescription(
                    element = "DEBUG_MODE",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.ADMIN)
    public void debugBlock(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if (debugPlayers.contains(player)) {
            debugPlayers.remove(player);
            player.sendMessage(CoreLang.DEBUG_MODE_TOGGLE.replace(player, CoreLang.GUI_DISABLED.getMessage(player)));
        } else {
            debugPlayers.add(player);
            player.sendMessage(CoreLang.DEBUG_MODE_TOGGLE.replace(player, CoreLang.GUI_ENABLED.getMessage(player)));
        }
    }

    private BukkitTask bukkitTask;

    @Command(
            aliases = {"testcage"},
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = ""
            )
    )
    @HiddenCommand
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> test(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        CageModel cageModel = new CageModel(nms.get(), CageStructure.INDIVIDUAL, player.getLocation());
        cageModel.spawn();

        bukkitTask = pluginScheduler.runTaskTimer(() -> {
            if (!player.isOnline()) {
                cageModel.destroy();
                bukkitTask.cancel();
                return;
            }

            cageModel.updateRotation(player.getLocation());
        }, 10L, 1L);
        return null;
    }

    @Command(
            aliases = {"testshop"},
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = ""
            )
    )
    @HiddenCommand
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> test2(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        shopFactory.createShopMenu(player, arrowManager);
        return null;
    }

    @Command(
            aliases = {"timings"},
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = ""
            )
    )
    @HiddenCommand
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> timings(CommandContext args, CommandSender sender) throws CommandException {
        TimingsManager.debug = !TimingsManager.debug;
        return null;
    }

    @Command(
            aliases = {"nextphase"},
            usage = "",
            langDescription = @LangDescription(
                    element = "",
                    langClass = CoreLang.class
            )
    )
    @HiddenCommand
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> nextPhase(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        AbstractArena currentArena = playerManager.getPlayer(player).getCurrentArena();
        if (currentArena == null) {
            return null;
        }

        currentArena.nextPhase();
        return null;
    }


    @Command(
            aliases = {"checkblocks"},
            usage = "[-b(roadcast)]",
            flags = "b",
            langDescription = @LangDescription(
                    element = "",
                    langClass = CoreLang.class
            )
    )
    @HiddenCommand
    @CommandPermissions(permission = Permission.ADMIN)
    public void checkblocks(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        Selection selection = selectionManagerProvider.get().getSelection(player);
        CommandUtils.validateTrue(selection != null && selection.arePointsSet(), CoreLang.SELECT_AN_AREA);

        Cuboid cuboid = selection.toCubiod("temp", null);

        List<ChunkSnapshot> chunkSnapshots = new ArrayList<>();
        for (Chunk chunk : cuboid.getChunks()) {
            chunkSnapshots.add(chunk.getChunkSnapshot());
        }

        pluginScheduler.runTaskAsynchronously(() -> {
            int blocksToScan = chunkSnapshots.size() * 16 * 16 * 256;
            int blocksScanned = 0;
            double interval = blocksToScan / 100.0;
            long lastSent = 0;

            player.sendMessage("Scanning area...");
            Map<Material, Integer> materialIntegerMap = new HashMap<>();

            for (ChunkSnapshot chunkSnapshot : chunkSnapshots) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 256; y++) {
                            org.bukkit.Material blockType = chunkSnapshot.getBlockType(x, y, z);
                            Material material = Material.matchMaterial(blockType);
                            materialIntegerMap.put(material, materialIntegerMap.getOrDefault(material, 0) + 1);

                            blocksScanned++;
                            if (blocksScanned % interval < 1) {
                                double progress = (double) blocksScanned / blocksToScan;

                                if(System.currentTimeMillis() - 50 >= lastSent) {
                                    Utils.displayProgress("Scanning area...", progress, Math.ceil(progress * 100.0) + "%", player);
                                    lastSent = System.currentTimeMillis();
                                }
                            }
                        }
                    }
                }
            }

            Map<Integer, List<Map.Entry<Material, Integer>>> mats = new HashMap<>();
            for (Map.Entry<Material, Integer> materialIntegerEntry : materialIntegerMap.entrySet()) {
                int version = materialIntegerEntry.getKey().getMaterialVersion();
                if (!mats.containsKey(version)) {
                    mats.put(version, new ArrayList<>());
                }

                mats.get(version).add(materialIntegerEntry);
            }

            CommandSender commandSender = args.hasFlag('b') ? Bukkit.getConsoleSender() : player;
            mats.forEach((integer, entries) -> {
                StringBuilder materials = new StringBuilder();
                int version = integer == 0 ? 8 : integer;
                materials.append(ChatColor.GREEN);
                materials.append("1.").append(version).append(" ")
                        .append(ChatColor.YELLOW).append("(").append(entries.size()).append(") :")
                        .append(ChatColor.WHITE);

                for (int i = 0; i < entries.size(); i++) {
                    if (i != 0) {
                        materials.append(", ");
                    }

                    materials.append(Utils.capitalizeFirstLetter(entries.get(i).getKey().name()));
                }

                commandSender.sendMessage(materials.toString());
            });

            Utils.displayProgress("Scanning area...", 1, 100.0 + "%", player);
        });
    }

    @Command(
            aliases = {"printcommands"},
            flags = "b",
            langDescription = @LangDescription(
                    element = "",
                    langClass = CoreLang.class
            )
    )
    @HiddenCommand
    @CommandPermissions(permission = Permission.ADMIN)
    public void printCommands(CommandContext args, CommandSender sender) throws CommandException {
        printCommands(commandsManager.getMethods().get(null), "/", new CopyOnWriteArrayList<>());
    }

    private void printCommands(Map<String, Method> methods, String path, List<Method> commands) {
        methods.forEach((commandName, executor) -> {
            if (commands.contains(executor)) {
                return;
            }

            commands.add(executor);
            System.out.println(path + " " + commandName);
            if (executor != null && commandsManager.getMethods().containsKey(executor)) {
                printCommands(commandsManager.getMethods().get(executor), path + " " + commandName, commands);
            }
        });
    }
}