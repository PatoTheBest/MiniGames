package me.patothebest.gamecore.title;

import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Minecraft 1.8 (1.9 and 1.10) Title
 *
 * @version 1.0.4
 * @author Maxim Van de Wynckel modified by PatoTheBest for GameCore
 */
class Title1_8_1_10 extends AbstractTitle {

    Title1_8_1_10(String title) {
        super(title);
    }

    @Override
    protected void loadClasses() {
        if (packetTitle == null) {
            packetTitle = getNMSClass("PacketPlayOutTitle");
            packetActions = getNMSClass("PacketPlayOutTitle$EnumTitleAction");
            chatBaseComponent = getNMSClass("IChatBaseComponent");
            nmsChatSerializer = getNMSClass("ChatComponentText");
        }
    }

    @Override
    public void send(Player player) {
        if (packetTitle != null) {
            // First reset previous settings
            resetTitle(player);
            try {
                // Send timings first
                Object[] actions = packetActions.getEnumConstants();
                Object packet = packetTitle.getConstructor(packetActions, chatBaseComponent, Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(actions[2], null, fadeInTime * (ticks ? 1 : 20), stayTime * (ticks ? 1 : 20), fadeOutTime * (ticks ? 1 : 20));
                // Send if set
                if (fadeInTime != -1 && fadeOutTime != -1 && stayTime != -1) {
                    Utils.sendPacket(player, packet);
                }

                // Send title
                Object serialized = nmsChatSerializer.getConstructor(String.class).newInstance(ChatColor.translateAlternateColorCodes('&', title));
                packet = packetTitle.getConstructor(packetActions, chatBaseComponent).newInstance(actions[0], serialized);
                Utils.sendPacket(player, packet);

                if (subtitle != null && !subtitle.isEmpty()) {
                    // Send subtitle if present
                    serialized = nmsChatSerializer.getConstructor(String.class).newInstance(ChatColor.translateAlternateColorCodes('&', subtitle));
                    packet = packetTitle.getConstructor(packetActions, chatBaseComponent).newInstance(actions[1], serialized);
                    Utils.sendPacket(player, packet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void clearTitle(Player player) {
        try {
            // Send timings first
            Object[] actions = packetActions.getEnumConstants();
            Object packet = packetTitle.getConstructor(packetActions, chatBaseComponent).newInstance(actions[3], null);
            Utils.sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resetTitle(Player player) {
        try {
            // Send timings first
            Object[] actions = packetActions.getEnumConstants();
            Object packet = packetTitle.getConstructor(packetActions, chatBaseComponent).newInstance(actions[4], null);
            Utils.sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
