package me.patothebest.gamecore.arena.option;

import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.util.NameableObject;
import me.patothebest.gamecore.util.SerializableObject;
import org.bukkit.command.CommandSender;

import java.util.Map;

public abstract class ArenaOption extends AbstractFeature implements SerializableObject, NameableObject {

    public abstract void parse(Map<String, Object> data);

    public abstract String getName();

    public abstract ILang getDescription();

    public abstract void sendDescription(CommandSender sender);
}
