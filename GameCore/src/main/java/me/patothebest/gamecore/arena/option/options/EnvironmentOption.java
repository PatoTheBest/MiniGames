package me.patothebest.gamecore.arena.option.options;

import me.patothebest.gamecore.arena.option.ArenaOption;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class EnvironmentOption extends ArenaOption {

    private World.Environment environment = World.Environment.NORMAL;

    @Override
    public void parse(Map<String, Object> data) {
        if (data.containsKey("environment")) {
            environment = Utils.getEnumValueFromString(World.Environment.class, (String) data.get("environment"), World.Environment.NORMAL);
        }
    }

    @Override
    public String getName() {
        return "environment";
    }

    @Override
    public ILang getDescription() {
        return CoreLang.OPTION_ENVIRONMENT_DESC;
    }

    @Override
    public void sendDescription(CommandSender sender) {
        CoreLang.OPTION_ENVIRONMENT.replaceAndSend(sender, Utils.capitalizeFirstLetter(environment.name()));
        CoreLang.OPTION_ENVIRONMENT_DESC.sendMessage(sender);
        CoreLang.OPTION_ENVIRONMENT_LIST.sendMessage(sender);
        CoreLang.OPTION_ENVIRONMENT_COMMAND.sendMessage(sender);
    }

    @Override
    public void serialize(Map<String, Object> data) {
        data.put("environment", environment.name());
    }

    public World.Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(World.Environment environment) {
        this.environment = environment;
    }

}
