package me.patothebest.gamecore.title;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractTitle implements Title {

    /* Title packet */ Class<?> packetTitle;
    /* Title packet actions ENUM */ Class<?> packetActions;
    /* Chat serializer */ Class<?> nmsChatSerializer;
    Class<?> chatBaseComponent;
    /* Title text and color */ String title = "";
    ChatColor titleColor = ChatColor.WHITE;
    /* Subtitle text and color */ String subtitle = "";
    ChatColor subtitleColor = ChatColor.WHITE;
    /* Title timings */ int fadeInTime = -1;
    int stayTime = -1;
    int fadeOutTime = -1;
    boolean ticks = false;

    private static final Map<Class<?>, Class<?>> CORRESPONDING_TYPES = new HashMap<>();

    /**
     * Create a new 1.8 title
     *
     * @param title Title
     */
    AbstractTitle(String title) {
        this.title = title;
        loadClasses();
    }

    /**
     * Create a new 1.8 title
     *
     * @param title Title text
     * @param subtitle Subtitle text
     */
    public AbstractTitle(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
        loadClasses();
    }

    /**
     * Copy 1.8 title
     *
     * @param title Title
     */
    public AbstractTitle(AbstractTitle title) {
        // Copy title
        this.title = title.title;
        this.subtitle = title.subtitle;
        this.titleColor = title.titleColor;
        this.subtitleColor = title.subtitleColor;
        this.fadeInTime = title.fadeInTime;
        this.fadeOutTime = title.fadeOutTime;
        this.stayTime = title.stayTime;
        this.ticks = title.ticks;
        loadClasses();
    }

    /**
     * Create a new 1.8 title
     *
     * @param title Title text
     * @param subtitle Subtitle text
     * @param fadeInTime Fade in time
     * @param stayTime Stay on screen time
     * @param fadeOutTime Fade out time
     */
    public AbstractTitle(String title, String subtitle, int fadeInTime, int stayTime, int fadeOutTime) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeInTime = fadeInTime;
        this.stayTime = stayTime;
        this.fadeOutTime = fadeOutTime;
        loadClasses();
    }

    /**
     * Load spigot and NMS classes
     */

    protected abstract void loadClasses();

    /**
     * Set title text
     *
     * @param title Title
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get title text
     *
     * @return Title text
     */
    @Override
    public String getTitle() {
        return this.title;
    }

    /**
     * Set subtitle text
     *
     * @param subtitle Subtitle text
     */
    @Override
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * Get subtitle text
     *
     * @return Subtitle text
     */
    @Override
    public String getSubtitle() {
        return this.subtitle;
    }

    /**
     * Set the title color
     *
     * @param color Chat color
     */
    @Override
    public void setTitleColor(ChatColor color) {
        this.titleColor = color;
    }

    /**
     * Set the subtitle color
     *
     * @param color Chat color
     */
    @Override
    public void setSubtitleColor(ChatColor color) {
        this.subtitleColor = color;
    }

    /**
     * Set title fade in time
     *
     * @param time Time
     */
    @Override
    public void setFadeInTime(int time) {
        this.fadeInTime = time;
    }

    /**
     * Set title fade out time
     *
     * @param time Time
     */
    @Override
    public void setFadeOutTime(int time) {
        this.fadeOutTime = time;
    }

    /**
     * Set title stay time
     *
     * @param time Time
     */
    @Override
    public void setStayTime(int time) {
        this.stayTime = time;
    }

    /**
     * Set timings to ticks
     */
    @Override
    public void setTimingsToTicks() {
        ticks = true;
    }

    /**
     * Set timings to seconds
     */
    @Override
    public void setTimingsToSeconds() {
        ticks = false;
    }

    /**
     * Broadcast the title to all players
     */
    @Override
    public void broadcast() {
        Bukkit.getOnlinePlayers().forEach(this::send);
    }

    /**
     * Check if running spigot
     *
     * @return Spigot
     */
    boolean isSpigot() {
        return Bukkit.getVersion().contains("Spigot");
    }

    /**
     * Get class by url
     *
     * @param namespace Namespace url
     * @return Class
     */
    Class<?> getClass(String namespace) {
        try {
            return Class.forName(namespace);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Field getField(String name, Class<?> clazz) throws Exception {
        return clazz.getDeclaredField(name);
    }

    Object getValue(String name, Object obj) throws Exception {
        Field f = getField(name, obj.getClass());
        f.setAccessible(true);
        return f.get(obj);
    }

    private Class<?> getPrimitiveType(Class<?> clazz) {
        return CORRESPONDING_TYPES
                .getOrDefault(clazz, clazz);
    }

    private Class<?>[] toPrimitiveTypeArray(Class<?>[] classes) {
        int a = classes != null ? classes.length : 0;
        Class<?>[] types = new Class<?>[a];
        for (int i = 0; i < a; i++)
            types[i] = getPrimitiveType(classes[i]);
        return types;
    }

    private static boolean equalsTypeArray(Class<?>[] a, Class<?>[] o) {
        if (a.length != o.length)
            return false;
        for (int i = 0; i < a.length; i++)
            if (!a[i].equals(o[i]) && !a[i].isAssignableFrom(o[i]))
                return false;
        return true;
    }

    Object getHandle(Object obj) {
        try {
            return getMethod("getHandle", obj.getClass()).invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    Method getMethod(String name, Class<?> clazz, Class<?>... paramTypes) {
        Class<?>[] t = toPrimitiveTypeArray(paramTypes);
        for (Method m : clazz.getMethods()) {
            Class<?>[] types = toPrimitiveTypeArray(m.getParameterTypes());
            if (m.getName().equals(name) && equalsTypeArray(types, t))
                return m;
        }
        return null;
    }

    private String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1) + ".";
    }

    Class<?> getNMSClass(String className) {
        String fullName = "net.minecraft.server." + getVersion() + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    Method getMethod(Class<?> clazz, String name, Class<?>... args) {
        for (Method m : clazz.getMethods())
            if (m.getName().equals(name) && (args.length == 0 || ClassListEqual(args, m.getParameterTypes()))) {
                m.setAccessible(true);
                return m;
            }
        return null;
    }

    private boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
        boolean equal = true;
        if (l1.length != l2.length)
            return false;
        for (int i = 0; i < l1.length; i++)
            if (l1[i] != l2[i]) {
                equal = false;
                break;
            }
        return equal;
    }
}
