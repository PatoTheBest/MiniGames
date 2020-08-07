package me.patothebest.gamecore.feature.features.other;

import me.patothebest.gamecore.feature.AbstractFeature;
import org.bukkit.WorldBorder;

public class WorldBorderFeature extends AbstractFeature {

    /** Percentage indicating the starting area of
     * the border in regards with the arena area */
    private int startSize = 100;

    /** Percentage indicating the ending area of
     * the border in regards with the arena area */
    private int endSize = 100;

    private boolean shrink = false;
    private int timeToShrink = 10;

    @Override
    public void initializeFeature() {
        WorldBorder worldBorder = arena.getWorld().getWorldBorder();
        double size = Math.max(arena.getArea().getSizeX(), arena.getArea().getSizeZ());
        int startSize = (int) (this.startSize * size / 100.0);
        worldBorder.setCenter(arena.getArea().getCenter());
        worldBorder.setSize(startSize);
        worldBorder.setWarningDistance(10);
        worldBorder.setWarningTime(15);
        worldBorder.setDamageAmount(2);

        if (shrink) {
            int endSize = Math.max((int) (this.endSize * size / 100.0), 2);
            worldBorder.setSize(endSize, timeToShrink);
        }
    }

    public int getStartSize() {
        return startSize;
    }

    public void setStartSize(int startSize) {
        this.startSize = startSize;
    }

    public int getEndSize() {
        return endSize;
    }

    public void setEndSize(int endSize) {
        this.endSize = endSize;
    }

    public boolean isShrink() {
        return shrink;
    }

    public void setShrink(boolean shrink) {
        this.shrink = shrink;
    }

    public int getTimeToShrink() {
        return timeToShrink;
    }

    public void setTimeToShrink(int timeToShrink) {
        this.timeToShrink = timeToShrink;
    }
}
