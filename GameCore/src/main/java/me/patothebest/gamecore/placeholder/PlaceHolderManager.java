package me.patothebest.gamecore.placeholder;

import com.google.inject.Singleton;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.pluginhooks.PluginHookManager;
import me.patothebest.gamecore.pluginhooks.hooks.MVdWPlaceholderAPIHook;
import me.patothebest.gamecore.pluginhooks.hooks.PlaceholderAPIHook;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.modules.Module;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class PlaceHolderManager implements Module {

    private static CorePlugin plugin;
    private static PluginHookManager pluginHookManager;
    private static Set<PlaceHolder> placeHolders;
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(?:" + PluginConfig.PLACEHOLDER_PREFIX + "_)?(.*?)(?::(.*?))?\\}");

    @Inject
    public PlaceHolderManager(CorePlugin plugin, Set<PlaceHolder> placeHolderSet, PluginHookManager pluginHookManager) {
        PlaceHolderManager.plugin = plugin;
        PlaceHolderManager.placeHolders = placeHolderSet;
        PlaceHolderManager.pluginHookManager = pluginHookManager;
    }

    public void registerMDWPlaceholder() {
        plugin.log(ChatColor.YELLOW + "Registering maximvdw placeholders...");

        placeHolders.forEach(placeHolder -> {
            pluginHookManager.getHook(MVdWPlaceholderAPIHook.class).registerPlaceholder(placeHolder);
            plugin.log("Registered placeholder " + "{" + PluginConfig.PLACEHOLDER_PREFIX + "_" + placeHolder.getPlaceholderName() + "}");
        });
    }

    public void registerClipsPlaceholders() {
        plugin.log(ChatColor.YELLOW + "Registering clip placeholders...");
        pluginHookManager.getHook(PlaceholderAPIHook.class).register(plugin, new EzPlaceholderWrapper(placeHolders));
        plugin.log("Registered placeholders");
    }

    public static String replace(Player player, String string) {
        if (pluginHookManager.isHookLoaded(MVdWPlaceholderAPIHook.class)) {
            string = pluginHookManager.getHook(MVdWPlaceholderAPIHook.class).replace(player, string);
        }

        if (pluginHookManager.isHookLoaded(PlaceholderAPIHook.class)) {
            string = pluginHookManager.getHook(PlaceholderAPIHook.class).replaceString(player, string);
        }

        Matcher m = PLACEHOLDER_PATTERN.matcher(string);

        while (m.find()) {
            String replacedPortion = m.group();
            String placeholder = m.group(1);
            String extraData = m.group(2);
            for (PlaceHolder placeHolder : placeHolders) {
                if(placeHolder.getPlaceholderName().equalsIgnoreCase(placeholder)) {
                    string = string.replace(replacedPortion, placeHolder.replace(player, extraData));
                    break;
                }
            }
        }

        return ChatColor.translateAlternateColorCodes('&', string);
    }


    public String replace(AbstractArena arena, String string) {
        final String[] replacedString = {string};
        placeHolders.forEach(placeHolder -> {
            if (!replacedString[0].contains("{" + placeHolder.getPlaceholderName() + "}")) {
                return;
            }

            replacedString[0] = replacedString[0].replace("{" + placeHolder.getPlaceholderName() + "}", placeHolder.replace(arena));
        });

        return ChatColor.translateAlternateColorCodes('&', replacedString[0]);
    }
}
