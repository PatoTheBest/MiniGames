package me.patothebest.gamecore.kit;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.NoSuchElementException;

@SerializableAs("WrappedPotionEffect")
public class WrappedPotionEffect extends PotionEffect {

    private static final String AMPLIFIER = "amplifier";
    private static final String DURATION = "duration";
    private static final String TYPE = "effect";
    private static final String AMBIENT = "ambient";
    private static final String PARTICLES = "has-particles";

    private int amplifier;
    private int duration;
    private PotionEffectType type;
    private boolean ambient;
    private boolean particles;
    private Color color;

    public WrappedPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles, Color color) {
        super(type, duration, amplifier, ambient, particles);
        Validate.notNull(type, "effect type cannot be null");
        this.type = type;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.particles = particles;
        this.color = color;
    }

    public WrappedPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles) {
        this(type, duration, amplifier, ambient, particles, null);
    }

    public WrappedPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient) {
        this(type, duration, amplifier, ambient, true);
    }

    public WrappedPotionEffect(PotionEffectType type, int duration, int amplifier) {
        this(type, duration, amplifier, true);
    }

    public WrappedPotionEffect(Map<String, Object> map) {
        this(getEffectType(map), getInt(map, "duration"), getInt(map, "amplifier"), getBool(map, "ambient", false), getBool(map, "has-particles", true));
    }

    private static PotionEffectType getEffectType(Map<?, ?> map) {
        int type = getInt(map, "effect");
        @SuppressWarnings("deprecation") PotionEffectType effect = PotionEffectType.getById(type);
        if(effect != null) {
            return effect;
        } else {
            throw new NoSuchElementException(map + " does not contain " + "effect");
        }
    }

    private static int getInt(Map<?, ?> map, Object key) {
        Object num = map.get(key);
        if(num instanceof Integer) {
            return (Integer) num;
        } else {
            throw new NoSuchElementException(map + " does not contain " + key);
        }
    }

    private static boolean getBool(Map<?, ?> map, Object key, boolean def) {
        Object bool = map.get(key);
        return bool instanceof Boolean? (Boolean) bool :def;
    }

    @Override
    public Map<String, Object> serialize() {
        //noinspection deprecation
        return ImmutableMap.of("effect", this.type.getId(), "duration", this.duration, "amplifier", this.amplifier, "ambient", this.ambient, "has-particles", this.particles);
    }

    @Override
    public boolean apply(LivingEntity entity) {
        return entity.addPotionEffect(this);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(!(obj instanceof org.bukkit.potion.PotionEffect)) {
            return false;
        } else {
            org.bukkit.potion.PotionEffect that = (org.bukkit.potion.PotionEffect)obj;
            return this.type.equals(that.getType()) && this.ambient == that.isAmbient() && this.amplifier == that.getAmplifier() && this.duration == that.getDuration() && this.particles == that.hasParticles();
        }
    }

    @Override
    public int getAmplifier() {
        return this.amplifier;
    }

    @Override
    public int getDuration() {
        return this.duration;
    }

    @Override
    public PotionEffectType getType() {
        return this.type;
    }

    @Override
    public boolean isAmbient() {
        return this.ambient;
    }

    @Override
    public boolean hasParticles() {
        return this.particles;
    }

    // Do not @Override
    // 1.8 doesn't have this method!
    public Color getColor() {
        return this.color;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setType(PotionEffectType type) {
        this.type = type;
    }

    public void setAmbient(boolean ambient) {
        this.ambient = ambient;
    }

    public void setParticles(boolean particles) {
        this.particles = particles;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int hashCode() {
        byte hash = 1;
        int hash1 = hash * 31 + this.type.hashCode();
        hash1 = hash1 * 31 + this.amplifier;
        hash1 = hash1 * 31 + this.duration;
        hash1 ^= 572662306 >> (this.ambient?1:-1);
        hash1 ^= 572662306 >> (this.particles?1:-1);
        return hash1;
    }

    @Override
    public String toString() {
        return this.type.getName() + (this.ambient?":(":":") + this.duration + "t-x" + this.amplifier + (this.ambient?")":"");
    }
}