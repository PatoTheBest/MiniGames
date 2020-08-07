package me.patothebest.gamecore.arena.option.options;

import me.patothebest.gamecore.arena.option.ArenaOption;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class TimeOfDayOption extends ArenaOption {

    private Time time = Time.NOON;

    @Override
    public void parse(Map<String, Object> data) {
        if (data.containsKey("time")) {
            time = Utils.getEnumValueFromString(Time.class, (String) data.get("time"), Time.NOON);
        }
    }

    @Override
    public String getName() {
        return "time";
    }

    @Override
    public ILang getDescription() {
        return CoreLang.OPTION_TIME_DESC;
    }

    @Override
    public void sendDescription(CommandSender sender) {
        CoreLang.OPTION_TIME.replaceAndSend(sender, Utils.capitalizeFirstLetter(time.name()));
        CoreLang.OPTION_TIME_DESC.sendMessage(sender);
        CoreLang.OPTION_TIME_LIST.sendMessage(sender);
        CoreLang.OPTION_TIME_COMMAND.sendMessage(sender);
    }

    @Override
    public void serialize(Map<String, Object> data) {
        data.put("time", time.name());
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public enum Time {

        SUNRISE(23000),
        DAY(1000),
        NOON(6000),
        SUNSET(12000),
        NIGHT(13000),
        MIDNIGHT(18000);

        private final int tick;

        Time(int ticks) {
            this.tick = ticks;
        }

        public int getTick() {
            return tick;
        }
    }
}
