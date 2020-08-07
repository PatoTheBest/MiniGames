package me.patothebest.gamecore.cosmetics.victoryeffects.types;

import fr.mrmicky.fastparticle.FastParticle;
import fr.mrmicky.fastparticle.ParticleType;
import me.patothebest.gamecore.cosmetics.victoryeffects.RepeatingVictoryEffect;
import me.patothebest.gamecore.player.IPlayer;

public class LavaPopVictoryEffect extends RepeatingVictoryEffect {

    @Override
    public void displayEffect(IPlayer player) {
        for (int i = 0; i < 30; ++i) {
            FastParticle.spawnParticle(player.getPlayer().getWorld(), ParticleType.LAVA, player.getLocation(), 1);
        }
    }

    @Override
    public long getPeriod() {
        return 20;
    }
}
