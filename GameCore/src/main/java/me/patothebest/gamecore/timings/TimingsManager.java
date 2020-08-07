package me.patothebest.gamecore.timings;

import me.patothebest.gamecore.PluginConfig;

import java.util.Map;

public class TimingsManager {

    public static boolean debug = false;

    private TimingsManager() {
    }

    public static TimingsData create(String timing) {
        return new TimingsData(timing);
    }


    public static void stop(TimingsData timings) {
        stop(timings, -1L);
    }

    public static void stop(TimingsData timings, long threshold) {
        long totalTime = calculateTime(timings.getStartTime(), timings.getStopTime());
        if (totalTime >= threshold && threshold != -1) {
            System.out.println("[" + PluginConfig.LOGGER_PREFIX + "] WARNING: " + timings.getTimingName() + " took " + totalTime + "ms to process!");
        }

        if (debug) {
            System.out.println("=== TIMINGS (" + timings.getTimingName() + ") ===");
            long time = timings.getStartTime();

            for (Map.Entry<String, Long> stringLongEntry : timings.getActionsMap().entrySet()) {
                System.out.println(stringLongEntry.getKey() + " - " + calculateTime(time, stringLongEntry.getValue()) + "ms");
                time = stringLongEntry.getValue();
            }

            System.out.println("End - " + calculateTime(time, timings.getStopTime()) + "ms");
            System.out.println("Total time - " + totalTime + "ms");
            System.out.println("=================================");
        }


    }

    private static long calculateTime(long start, long end) {
        return (end - start) / 1000000L;
    }
}