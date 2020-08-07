package me.patothebest.gamecore.lang;

import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

public abstract class CoreLocaleManager {

    private static final Map<String, Locale> LOCALES = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    public static Locale DEFAULT_LOCALE;

    protected final CorePlugin plugin;
    private final ILang[] pluginLangValues;

    public CoreLocaleManager(CorePlugin plugin, ILang[] pluginLangValues) {
        this.plugin = plugin;
        this.pluginLangValues = pluginLangValues;
        DEFAULT_LOCALE = getOrCreateLocale("en");

        loadLocales(new File(Utils.PLUGIN_DIR, "l10n"));
    }

    public void loadLocales(File localeDirectory) {
        try {
            String[] locales = Utils.getResourceListing(plugin.getClass(), "locale/");
            for (String locale : locales) {
                if (locale.isEmpty()) {
                    continue;
                }

                getOrCreateLocale(locale.replace(".yml", ""));
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        if (localeDirectory.listFiles() != null) {
            for (File file : localeDirectory.listFiles()) {
                try {
                    getOrCreateLocale(file.getName().replace(".yml", ""));
                } catch (Throwable t) {
                    plugin.log(ChatColor.RED + "Could not load locale " + file.getName() + "!");
                    t.printStackTrace();
                }
            }
        }
    }

    public Locale getOrCreateLocale(String localeName) {
        if (LOCALES.containsKey(localeName)) {
            return LOCALES.get(localeName);
        }

        Locale locale = new Locale(localeName);
        LOCALES.put(localeName, locale);

        plugin.log(ChatColor.YELLOW + "loading locale " + locale.getName());
        initializeLangFile(locale);
        plugin.log("Loaded locale " + locale.getName());

        return locale;
    }

    public static Locale getLocale(String locale) {
        return LOCALES.get(locale);
    }

    public static Locale getOrDefault(String locale, Locale defaultLocale) {
        return LOCALES.getOrDefault(locale, defaultLocale);
    }

    protected void initializeLangFile(Locale locale) {
        File file = new File(Utils.PLUGIN_DIR, "l10n/" + locale.getName() + ".yml");
        new LocaleFile(plugin, locale, file, Utils.concatenateArray(CoreLang.values(), pluginLangValues, ILang.class));
    }

    public static Map<String, Locale> getLocales() {
        return LOCALES;
    }
}
