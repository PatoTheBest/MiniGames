package me.patothebest.gamecore.lang;

import me.patothebest.gamecore.lang.interfaces.IComment;
import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.chat.ChatColorEscaper;
import me.patothebest.gamecore.file.FlatFile;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class LocaleFile extends FlatFile {

    private final CorePlugin plugin;
    private final Locale locale;
    private final ILang[] lang;

    public LocaleFile(CorePlugin plugin, Locale locale, File file, ILang... iLangs) {
        super(file);
        this.plugin = plugin;

        this.lang = iLangs;
        this.header = "Locale File";
        this.locale = locale;

        load();
    }

    @Override
    public void load() {
        super.load();

        try (InputStream coreLocaleStream = plugin.getResource("corelocale/" + locale.getName() + ".yml");
             InputStream localeStream = plugin.getResource("locale/" + locale.getName() + ".yml")){

            if (coreLocaleStream == null || localeStream == null) {
                boolean regenerate = false;
                for(ILang message : lang) {
                    if(message.isComment()) {
                        continue;
                    }

                    if(getString(message.getPath()) == null) {
                        regenerate = true;
                        message.setMessage(locale, message.getDefaultMessage());
                        continue;
                    }

                    message.setMessage(locale, getString(message.getPath()));
                }

                if(regenerate) {
                    delete();
                    generateFile();
                }
                return;
            }

            YamlConfiguration resourceFile = new YamlConfiguration();
            resourceFile.loadFromString(fileToYAML(coreLocaleStream));
            resourceFile.loadFromString(fileToYAML(localeStream));

            boolean regenerate = false;
            for (ILang message : lang) {
                if(message.isComment()) {
                    continue;
                }

                String resourceMessage = resourceFile.getString(message.getPath());
                String fileMessage = getString(message.getPath());
                String defaultMessage = message.getDefaultMessage();

                if(resourceMessage == null) {
                    if (fileMessage == null) {
                        message.setMessage(locale, defaultMessage);
                        regenerate = true;
                    } else {
                        message.setMessage(locale, fileMessage);
                    }
                    continue;
                }

                if (fileMessage == null) {
                    message.setMessage(locale, ChatColorEscaper.toColorCodes(resourceMessage));
                    regenerate = true;
                } else {
                    if (fileMessage.equalsIgnoreCase(message.getDefaultMessage()) &&
                            !fileMessage.equalsIgnoreCase(ChatColorEscaper.toColorCodes(resourceMessage))) {
                        message.setMessage(locale, ChatColorEscaper.toColorCodes(resourceMessage));
                        regenerate = true;
                    } else {
                        message.setMessage(locale, fileMessage);
                    }
                }
            }

            if(regenerate) {
                System.out.println("Regenerating file!");
                delete();
                generateFile();
            }
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeFile(BufferedWriter writer) throws IOException {
        for (ILang lang : lang) {
            if(lang.isComment()) {
                IComment comment = lang.getComment();
                for (String s : comment.getLines()) {
                    if(s.isEmpty()) {
                        writer.write("\n\n");
                        continue;
                    }

                    if(lang.getComment().isHeader()) {
                        writer.write(getHeader(s));
                    } else {
                        writer.write("# " + s + "\n");
                    }
                }
            } else {
                writer.write(lang.getPath() + ": " + "\"" + (lang.getRawMessage(locale) == null ? lang.getDefaultMessage() : lang.getRawMessage(locale)) + "\"\n");
            }
        }
    }
}
