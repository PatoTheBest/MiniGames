package me.patothebest.gamecore.chat;

import me.patothebest.gamecore.command.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatColorEscaper {

    private static final char COLOR_CHAR = '\u00A7';
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)[" + COLOR_CHAR + "&]([0-9A-FK-OR])");
    private static final Pattern COLOR_PATTERN = Pattern.compile("%%([a-z_]+?)%%");

    private ChatColorEscaper() {}

    public static String escapeColors(String originalString) {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = STRIP_COLOR_PATTERN.matcher(originalString);
        while (matcher.find()) {
            String colorChar = matcher.group(1);
            ChatColor byChar = ChatColor.getByChar(colorChar);
            matcher.appendReplacement(sb, "%%" + byChar.name().toLowerCase() + "%%");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String toColor(String originalString) {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = COLOR_PATTERN.matcher(originalString);
        while (matcher.find()) {
            String colorName = matcher.group(1);
            ChatColor byName = ChatColor.getByName(colorName.toUpperCase());
            if (byName != null) {
                matcher.appendReplacement(sb, COLOR_CHAR + String.valueOf(byName.getChar()));
            }
        }
        matcher.appendTail(sb);
        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }

    public static String toColorCodes(String originalString) {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = COLOR_PATTERN.matcher(originalString);
        while (matcher.find()) {
            String colorName = matcher.group(1);
            ChatColor byName = ChatColor.getByName(colorName.toUpperCase());
            if (byName != null) {
                matcher.appendReplacement(sb, '&' + String.valueOf(byName.getChar()));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
