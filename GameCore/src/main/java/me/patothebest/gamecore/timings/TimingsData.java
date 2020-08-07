package me.patothebest.gamecore.timings;

import java.util.LinkedHashMap;
import java.util.Map;

public class TimingsData {

    private final Map<String, Long> actionsMap = new LinkedHashMap<>();
    private final long startTime;
    private String timing;
    private long stopTime;

    public TimingsData(String timing) {
        this.timing = timing;
        this.startTime = System.nanoTime();
    }

    public void trackAction(String action) {
        actionsMap.put(action, System.nanoTime());
    }

    public Map<String, Long> getActionsMap() {
        return actionsMap;
    }

    TimingsData end() {
        stopTime = System.nanoTime();
        return this;
    }

    public void stop() {
        stop(-1);
    }

    public boolean end(long threshhold) {
        stopTime = System.nanoTime();
        return (stopTime - startTime) / 1000000L > threshhold;
    }

    public void print() {
        TimingsManager.stop(this, -1);
    }

    public void stop(long threshold) {
        stopTime = System.nanoTime();
        TimingsManager.stop(this, threshold);
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getTimingName() {
        return timing;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getStopTime() {
        return stopTime;
    }
}
