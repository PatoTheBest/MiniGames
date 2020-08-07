package me.patothebest.gamecore.title;

import org.bukkit.entity.Player;

class UnsupportedTitle extends AbstractTitle {

    UnsupportedTitle(String title) {
        super(title);
    }

    @Override
    protected void loadClasses() {

    }

    @Override
    public void send(Player player) {

    }

    @Override
    public void clearTitle(Player player) {

    }

    @Override
    public void resetTitle(Player player) {

    }
}
