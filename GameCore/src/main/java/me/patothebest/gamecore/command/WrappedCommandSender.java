package me.patothebest.gamecore.command;

public interface WrappedCommandSender {
    String getName();

    void sendMessage(String message);

    void sendMessage(String[] messages);

    boolean hasPermission(String permission);

    Type getType();

    Object getCommandSender();

    enum Type {
        CONSOLE,
        PLAYER,
        BLOCK,
        UNKNOWN
    }
}
