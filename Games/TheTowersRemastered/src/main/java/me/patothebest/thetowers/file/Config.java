package me.patothebest.thetowers.file;

import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.thetowers.TheTowersRemastered;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class Config extends CoreConfig {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final ArrayList<PotionEffect> potionEffects;
    private boolean itemEnabled;
    private double moneyToScore;
    private double moneyToKill;
    private double moneyTowin;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject private Config(TheTowersRemastered plugin) {
        super(plugin, "config");
        this.header = "The Towers Configuration";
        this.potionEffects = new ArrayList<>();

        // load the config into memory
        load();
    }

    // -------------------------------------------- //
    // CACHE
    // -------------------------------------------- //

    @Override
    public void readConfig() {
        // read core values
        super.readConfig();

        // Potion Effects
        // Iterate over each potion effect
        for (final String string : getConfigurationSection("potion-effects").getKeys(false)) {
            try {
                // get the potion effect type
                PotionEffectType effectType = PotionEffectType.getByName(string);

                // get the amplifier and duration
                int modifier = Integer.parseInt(getString("potion-effects." + string + ".modifier"));
                int time = Integer.parseInt(getString("potion-effects." + string + ".time"));

                // cache the effect
                this.potionEffects.add(new PotionEffect(effectType, time * 20, modifier));
            } catch (Exception e) {
                System.out.println("Could not load potion effect " + string);
                e.printStackTrace();
            }
        }

        // Vault integrations
        // these will not be used if there is no vault
        // present, or the hook is disabled
        moneyToScore = getDouble("money.score");
    }

    // -------------------------------------------- //
    // GETTERS AND SETTERS
    // -------------------------------------------- //

    public ArrayList<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public double getMoneyToScore() {
        return moneyToScore;
    }
}
