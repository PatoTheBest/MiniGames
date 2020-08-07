package me.patothebest.gamecore.feature.features.other;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.command.ChatColor;
import me.patothebest.gamecore.event.arena.CountdownStartEvent;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.feature.AbstractRunnableFeature;
import me.patothebest.gamecore.phase.phases.CagePhase;
import me.patothebest.gamecore.title.Title;
import me.patothebest.gamecore.title.TitleManager;

import javax.inject.Inject;

public class CountdownFeature extends AbstractRunnableFeature {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final CorePlugin plugin;
    private final CoreConfig coreConfig;
    private final EventRegistry eventRegistry;

    private int time;
    private boolean running = false;
    private boolean overrideRunning = false;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject
    public CountdownFeature(CorePlugin plugin, CoreConfig coreConfig, EventRegistry eventRegistry) {
        this.plugin = plugin;
        this.coreConfig = coreConfig;
        this.eventRegistry = eventRegistry;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void run() {
        if (!coreConfig.isSet("min-players") || coreConfig.getString("min-players").contains("-1")) {
            // If a player has left the arena and the player count is lower
            // then the required amount to start...
            if (arena.getPlayers().size() < arena.getMinPlayers() && !overrideRunning) {
                // ...return
                running = false;
                return;
            }
        } else {
            int minSize;
            int percentage = Integer.parseInt(coreConfig.getString("min-players").replace("%", ""));
            minSize = (int) Math.ceil(percentage * arena.getMaxPlayers() / 100.0);

            // If a player has left the arena and the player count is lower
            // then the required amount to start...
            if (arena.getPlayers().size() < minSize && !overrideRunning) {
                // ...return
                running = false;
                return;
            }
        }

        if (!running && overrideRunning) {
            return;
        }

        if (!running) {
            startCountdown();
        }

        // decrement the time by one
        time--;

        if (arena.getPlayers().size() == arena.getMaxPlayers() && time > 10) {
            time = 10;
        }

        // If the time has hit 0...
        if (time <= 0) {
            // ...start the game and cancel the task
            arena.startGame();
            cancel();
            return;
        }

        // iterate over each player
        arena.getPlayers().forEach(player -> {
            // set their xp level
            player.setLevel(time);

            if (time <= 10 || time % 10 == 0) {
                // create and send the countdown message
                CoreLang titleLang = CoreLang.GAME_STARTING_TITLE;
                CoreLang subtitleLang = CoreLang.GAME_STARTING_SUBTITLE;
                CoreLang chatMessage = CoreLang.GAME_STARTING_CHAT;

                if (arena.getPhase() instanceof CagePhase) {
                    if (arena.getTeams().size() > 1) {
                        titleLang = CoreLang.TEAM_CAGE_RELEASING_TITLE;
                        subtitleLang = CoreLang.TEAM_CAGE_RELEASING_SUBTITLE;
                        chatMessage = CoreLang.TEAM_CAGE_RELEASING_CHAT;
                    } else {
                        titleLang = CoreLang.SOLO_CAGE_RELEASING_TITLE;
                        subtitleLang = CoreLang.SOLO_CAGE_RELEASING_SUBTITLE;
                        chatMessage = CoreLang.SOLO_CAGE_RELEASING_CHAT;
                    }
                }

                String secondLang = (time == 1 ? CoreLang.SECOND.getMessage(player) : CoreLang.SECONDS.getMessage(player));
                String seconds = String.valueOf(time);
                if (time == 3) {
                    seconds = ChatColor.RED + seconds;
                } else if (time == 2) {
                    seconds = ChatColor.YELLOW + seconds;
                } else if (time == 1) {
                    seconds = ChatColor.GREEN + seconds;
                } else {
                    seconds = ChatColor.WHITE + seconds;
                }


                Title title = TitleManager.newInstance(titleLang.replace(player, seconds, secondLang));
                title.setSubtitle(subtitleLang.replace(player, seconds, secondLang));
                title.setFadeInTime(0);
                title.setFadeOutTime(1);
                title.setStayTime(2);
                title.send(player);

                // send the players the countdown message
                player.sendMessage(chatMessage.replace(player, seconds, secondLang));
            }
        });
    }

    public void startCountdown(int time) {
        // If is already running...
        if (running) {
            // ...return
            return;
        }

        // set the time to the config seconds + 1 initial second since
        // we decrement a second from the time at the beginning of the
        // task, so we don't have any inconsistencies with the scoreboard
        //time = plugin.getConfig().getCountdownTime()+1;

        this.time = time + 1;

        // run the task
        running = true;
    }

    public void startCountdown() {
        if (running) {
            time--;
            return;
        }

        // set the time to the config seconds + 1 initial second since
        // we decrement a second from the time at the beginning of the
        // task, so we don't have any inconsistencies with the scoreboard
        //time = plugin.getConfig().getCountdownTime()+1;

        time = coreConfig.getCountdownTime() + 1;

        // run the task
        running = true;

        eventRegistry.callEvent(new CountdownStartEvent(arena));
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        // If the task has not been scheduled yet...
        if (!hasBeenScheduled()) {
            // ...return
            return;
        }

        // set running to false
        running = false;
        overrideRunning = false;

        // cancel the actual task
        super.cancel();
    }

    // -------------------------------------------- //
    // GETTERS AND SETTERS
    // -------------------------------------------- //

    public boolean isOverrideRunning() {
        return overrideRunning;
    }

    public void setOverrideRunning(boolean overrideRunning) {
        this.overrideRunning = overrideRunning;
    }

    public boolean isRunning() {
        return running;
    }

    public int getTime() {
        return time;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public String toString() {
        return "Countdown{" +
                "arena=" + arena.getName() +
                ", time=" + time +
                ", running=" + running +
                '}';
    }

    @Override
    public void initializeFeature() {
        runTaskTimer(plugin, 0L, 20L);
    }
}
