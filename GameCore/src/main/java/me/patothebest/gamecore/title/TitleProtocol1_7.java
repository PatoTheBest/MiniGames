package me.patothebest.gamecore.title;

import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Minecraft 1.7 Protocol Title
 *
 * @version 1.0.4
 * @author Maxim Van de Wynckel modified by PatoTheBest for GameCore
 */
class TitleProtocol1_7 extends AbstractTitle {

    TitleProtocol1_7(String title) {
        super(title);
    }

    @Override
    protected void loadClasses() {
        packetTitle = getClass("org.spigotmc.ProtocolInjector$PacketTitle");
        packetActions = getClass("org.spigotmc.ProtocolInjector$PacketTitle$Action");
        nmsChatSerializer = getNMSClass("ChatSerializer");
    }

    /**
     * Send the title to a player
     *
     * @param player Player
     */
    public void send(Player player) {
        if (getProtocolVersion(player) >= 47 && isSpigot() && packetTitle != null) {
            // First reset previous settings
            resetTitle(player);
            try {
                // Send timings first
                Object[] actions = packetActions.getEnumConstants();
                Object packet = packetTitle.getConstructor(packetActions, Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance( actions[2], fadeInTime * (ticks ? 1 : 20), stayTime * (ticks ? 1 : 20), fadeOutTime * (ticks ? 1 : 20));
                // Send if set
                if (fadeInTime != -1 && fadeOutTime != -1 && stayTime != -1)
                    Utils.sendPacket(player, packet);

                // Send title
                Object serialized = getMethod(nmsChatSerializer, "a", String.class).invoke(null, "{text:\""  + ChatColor.translateAlternateColorCodes('&', title) + "\",color:" + titleColor.name().toLowerCase() + "}");
                packet = packetTitle.getConstructor(packetActions, getNMSClass("IChatBaseComponent")) .newInstance(actions[0], serialized);
                Utils.sendPacket(player, packet);
                if (subtitle != null && !subtitle.isEmpty()) {
                    // Send subtitle if present
                    serialized = getMethod(nmsChatSerializer, "a", String.class).invoke(null, "{text:\"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\",color:" + subtitleColor.name().toLowerCase() + "}");
                    packet = packetTitle.getConstructor(packetActions, getNMSClass("IChatBaseComponent")).newInstance(actions[1], serialized);
                    Utils.sendPacket(player, packet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Clear the title
     *
     * @param player Player
     */
    public void clearTitle(Player player) {
        if (getProtocolVersion(player) >= 47 && isSpigot()) {
            try {
                // Send timings first
                Object[] actions = packetActions.getEnumConstants();
                Object packet = packetTitle.getConstructor(packetActions).newInstance(actions[3]);
                Utils.sendPacket(player, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reset the title settings
     *
     * @param player Player
     */
    public void resetTitle(Player player) {
        if (getProtocolVersion(player) >= 47 && isSpigot()) {
            try {
                // Send timings first
                Object[] actions = packetActions.getEnumConstants();
                Object packet = packetTitle.getConstructor(packetActions).newInstance(actions[4]);
                Utils.sendPacket(player, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the protocol version of the player
     *
     * @param player Player
     * @return Protocol version
     */
    private int getProtocolVersion(Player player) {
        int version = 0;
        try {
            Object handle = getHandle(player);
            Object connection = getField(handle.getClass(), "playerConnection").get(handle);
            Object networkManager = getValue("networkManager", connection);
            version = (Integer) getMethod("getVersion", networkManager.getClass()).invoke(networkManager);

            return version;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return version;
    }
}