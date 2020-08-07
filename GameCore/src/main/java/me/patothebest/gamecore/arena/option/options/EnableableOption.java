package me.patothebest.gamecore.arena.option.options;

import me.patothebest.gamecore.arena.option.ArenaOption;
import me.patothebest.gamecore.lang.CoreLang;
import org.bukkit.command.CommandSender;

import java.util.Map;

public abstract class EnableableOption extends ArenaOption {

    private boolean enabled;

    public void parse(Map<String, Object> data) {
        if (data.containsKey(getName())) {
            enabled = (boolean) data.get(getName());
        }
    }

    @Override
    public void serialize(Map<String, Object> data) {
        data.put(getName(), enabled);
    }

    @Override
    public void sendDescription(CommandSender sender) {
        CoreLang.OPTION_ENABLEABLE.replaceAndSend(sender, getName(), (enabled) ? CoreLang.GUI_ENABLED : CoreLang.GUI_DISABLED);
        getDescription().sendMessage(sender);
        CoreLang.OPTION_ENABLEABLE_COMMAND.replaceAndSend(sender, getName());
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
