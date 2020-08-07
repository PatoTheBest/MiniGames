package me.patothebest.gamecore.scheduler;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.modules.Module;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.List;

public class PluginScheduler implements Module {

    private final CorePlugin plugin;

    @Inject public PluginScheduler(CorePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Schedules a once off task to occur after a delay.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param task Task to be executed
     * @param delay Delay in server ticks before executing task
     * @return Task id number (-1 if scheduling failed)
     */
    public int scheduleSyncDelayedTask(Runnable task, long delay) {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task, delay);
    }

    /**
     * Schedules a once off task to occur as soon as possible.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param task Task to be executed
     * @return Task id number (-1 if scheduling failed)
     */
    public int scheduleSyncDelayedTask(Runnable task) {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task);
    }

    /**
     * Schedules a repeating task.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param task Task to be executed
     * @param delay Delay in server ticks before executing first repeat
     * @param period Period in server ticks of the task
     * @return Task id number (-1 if scheduling failed)
     */
    public int scheduleSyncRepeatingTask(Runnable task, long delay, long period) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task, delay, period);
    }

    /**
     * Removes task from scheduler.
     *
     * @param taskId Id number of task to be removed
     */
    public void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    /**
     * Removes all tasks associated with a particular plugin from the
     * scheduler.
     *
     * @param plugin Owner of tasks to be removed
     */
    public void cancelTasks(Plugin plugin) {
        Bukkit.getScheduler().cancelTasks(plugin);
    }

    /**
     * Check if the task currently running.
     * <p>
     * A repeating task might not be running currently, but will be running in
     * the future. A task that has finished, and does not repeat, will not be
     * running ever again.
     * <p>
     * Explicitly, a task is running if there exists a thread for it, and that
     * thread is alive.
     *
     * @param taskId The task to check.
     * <p>
     * @return If the task is currently running.
     */
    public boolean isCurrentlyRunning(int taskId) {
        return Bukkit.getScheduler().isCurrentlyRunning(taskId);
    }

    /**
     * Check if the task queued to be run later.
     * <p>
     * If a repeating task is currently running, it might not be queued now
     * but could be in the future. A task that is not queued, and not running,
     * will not be queued again.
     *
     * @param taskId The task to check.
     * <p>
     * @return If the task is queued to be run.
     */
    public boolean isQueued(int taskId) {
        return Bukkit.getScheduler().isQueued(taskId);
    }

    /**
     * Returns a list of all active workers.
     * <p>
     * This list contains async tasks that are being executed by separate
     * threads.
     *
     * @return Active workers
     */
    public List<BukkitWorker> getActiveWorkers() {
        return Bukkit.getScheduler().getActiveWorkers();
    }

    /**
     * Returns a list of all pending tasks. The ordering of the tasks is not
     * related to their order of execution.
     *
     * @return Active workers
     */
    public List<BukkitTask> getPendingTasks() {
        return Bukkit.getScheduler().getPendingTasks();
    }

    /**
     * Returns a task that will run on the next server tick.
     *
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    public BukkitTask runTask(Runnable task) throws IllegalArgumentException {
        return Bukkit.getScheduler().runTask(plugin, task);
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Returns a task that will run asynchronously.
     *
     * @param task the task to be run
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    public BukkitTask runTaskAsynchronously(Runnable task) throws IllegalArgumentException {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    /**
     * Returns a task that will run after the specified number of server
     * ticks.
     *
     * @param task the task to be run
     * @param delay the ticks to wait before running the task
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    public BukkitTask runTaskLater(Runnable task, long delay) throws IllegalArgumentException {
        return Bukkit.getScheduler().runTaskLater(plugin, task, delay);
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Returns a task that will run asynchronously after the specified number
     * of server ticks.
     *
     * @param task the task to be run
     * @param delay the ticks to wait before running the task
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    public BukkitTask runTaskLaterAsynchronously(Runnable task, long delay) throws IllegalArgumentException {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
    }

    /**
     * Returns a task that will repeatedly run until cancelled, starting after
     * the specified number of server ticks.
     *
     * @param task the task to be run
     * @param delay the ticks to wait before running the task
     * @param period the ticks to wait between runs
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    public BukkitTask runTaskTimer(Runnable task, long delay, long period) throws IllegalArgumentException {
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Returns a task that will repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param task the task to be run
     * @param delay the ticks to wait before running the task for the first
     *     time
     * @param period the ticks to wait between runs
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalArgumentException if task is null
     */
    public BukkitTask runTaskTimerAsynchronously(Runnable task, long delay, long period) throws IllegalArgumentException {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
    }

    public void callEvenSync(Event event) {
        runTask(() -> Bukkit.getServer().getPluginManager().callEvent(event));
    }

    public void ensureSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            runTask(runnable);
        }
    }


    public void ensureAsync(Runnable runnable) {
        if (!Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            runTaskAsynchronously(runnable);
        }
    }
}
