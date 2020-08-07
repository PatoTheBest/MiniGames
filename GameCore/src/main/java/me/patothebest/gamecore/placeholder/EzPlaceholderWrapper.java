package me.patothebest.gamecore.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.patothebest.gamecore.PluginConfig;
import org.bukkit.entity.Player;

import java.util.Set;

public class EzPlaceholderWrapper extends PlaceholderExpansion {

    private final Set<PlaceHolder> placeHolders;

    public EzPlaceholderWrapper(Set<PlaceHolder> placeHolders) {
        this.placeHolders = placeHolders;
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        String placeholderName = s;
        String extradata = null;

        if(s.contains(":")) {
            String[] split = s.split(":");
            placeholderName = split[0];
            extradata = split[1];
        }

        String finalPlaceholderName = placeholderName;
        PlaceHolder placeHolder = placeHolders.stream().filter(placeHolder1 -> placeHolder1.getPlaceholderName().equalsIgnoreCase(finalPlaceholderName)).findAny().orElse(null);

        if(placeHolder == null) {
            return "unknown";
        }

        return placeHolder.replace(player, extradata);
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return PluginConfig.PLACEHOLDER_PREFIX;
    }

    @Override
    public String getAuthor() {
        return "PatoTheBest";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
