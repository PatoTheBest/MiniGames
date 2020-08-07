package me.patothebest.gamecore.actionbar;

import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.util.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class ActionBar {

    // -------------------------------------------- //
    // CONSTANTS
    // -------------------------------------------- //

    private static final boolean useChatSerializer = Utils.SERVER_VERSION.contains("v1_8_R1") || Utils.SERVER_VERSION.contains("v1_7");
    private static final boolean useReflection = useChatSerializer || Utils.SERVER_VERSION.contains("v1_8_R2") || Utils.SERVER_VERSION.contains("v1_8_R3") || Utils.SERVER_VERSION.contains("v1_9_R1");
    private static final Class<?> packetPlayOutChatClass = Utils.getNMSClass("PacketPlayOutChat");
    private static final Class<?> chatSerializerClass = Utils.getNMSClassOrNull("ChatSerializer");
    private static final Class<?> chatComponentTextClass = Utils.getNMSClass("ChatComponentText");
    private static final Class<?> iChatBaseComponentClass = Utils.getNMSClass("IChatBaseComponent");
    private static final Method serializeMethod = Utils.getMethodOrNull(chatSerializerClass, "a", String.class);

    // -------------------------------------------- //
    // PUBLIC METHOD
    // -------------------------------------------- //

    public static void sendActionBar(Player player, ILang message) {
        sendActionBar(player, message.getMessage(player));
    }

    public static void sendActionBar(Player player, String message) {
        if (!useReflection) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            return;
        }

        try {
            Object packetPlayOutChat;

            // this will define if we use the 1.8 protocol version or the 1.8+ version
            if (useChatSerializer) {
                Object serializedText = iChatBaseComponentClass.cast(serializeMethod.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}"));
                packetPlayOutChat = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, Byte.TYPE).newInstance(serializedText, (byte)2);
            } else {
                Object chatBaseComponent = chatComponentTextClass.getConstructor(String.class).newInstance(message);
                packetPlayOutChat = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, Byte.TYPE).newInstance(chatBaseComponent, (byte)2);
            }

            // send the packet
            Utils.sendPacket(player, packetPlayOutChat);
        } catch (Exception ex) {
            // in case any errors, print them
            ex.printStackTrace();
        }
    }
}