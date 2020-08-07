package me.patothebest.gamecore.lang;

import me.patothebest.gamecore.chat.ChatColorEscaper;
import me.patothebest.gamecore.lang.interfaces.ILang;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LocaleGenerator {

    public static void main(String[] args) {
        if(args[1].contains("/Games.yml")) {
            return;
        }

        try {
            Class<? extends ILang> aClass = (Class<? extends ILang>) Class.forName(args[0]);
            ILang[] enumConstants = aClass.getEnumConstants();
            String filePath = args[1];
            File file = new File(filePath);

            if (!file.exists()) {
                file.createNewFile();
            }

            try (FileWriter writer = new FileWriter(file, false)){
                for (ILang enumConstant : enumConstants) {
                    if (enumConstant.isComment()) {
                        continue;
                    }

                    writer.append(enumConstant.name().toLowerCase().replace("_", "-"))
                            .append(": \"")
                            .append(ChatColorEscaper.escapeColors(enumConstant.getDefaultMessage()))
                            .append("\"\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}