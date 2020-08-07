package me.patothebest.gamecore.file;

import com.google.common.io.Files;
import me.patothebest.gamecore.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlatFile extends YamlConfiguration {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    protected final File file;
    protected final String fileName;
    protected String header;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public FlatFile(String fileName) {
        this.fileName = fileName + ".yml";
        this.file = new File(Utils.PLUGIN_DIR, this.fileName);
        this.header = fileName;

        DumperOptions dumperOptions = Utils.getFieldValue(YamlConfiguration.class, "yamlOptions", this);
        assert dumperOptions != null;
        dumperOptions.setWidth(Integer.MAX_VALUE);
    }

    public FlatFile(File file) {
        this.file = file;
        this.fileName = file.getName();
        this.header = file.getName();
    }

    // -------------------------------------------- //
    // PUBLIC METHODS
    // -------------------------------------------- //

    public void save() {
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(File file) throws IOException {
        try {
            // create parent directory
            Files.createParentDirs(file);

            // We convert the default YAML file comments
            // into YAML paths so that they aren't deleted by
            // the poorly coded loading and saving YAML methods
            String data = escapeYamlCharacters(saveToString());

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                // write the data into the file
                writer.write(data);
            }
        } catch (IOException ex) {
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save " + file.getName() + " to " + file.getPath(), ex);
        }
    }

    public void delete() {
        file.delete();
    }

    public File getFile() {
        return file;
    }

    // -------------------------------------------- //
    // CLASS METHODS
    // -------------------------------------------- //

    protected void generateFile() {
        try {
            // create parent directory and the file
            Files.createParentDirs(file);
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create a bufferedwriter to write into the file
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));){
            // write and close the writer
            writeFile(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void writeFile(BufferedWriter writer) throws IOException {
        // each file contains a header only
        writer.write(getHeader(header));
    }

    public void load() {
        // first we clear to avoid any issues
        map.clear();

        // if the file doesn't exist...
        if(!file.exists()) {
            // ...generate the file
            generateFile();
        }

        try {
            // load the file
            loadFromString(fileToYAML(file));
        } catch (InvalidConfigurationException e1) {
            System.out.println("Error while loading file " + file.getName());
            e1.printStackTrace();

            // rename the file to broken
            file.renameTo(new File(Utils.PLUGIN_DIR, file.getName().substring(0, file.getName().length()-4) + ".broken-" + System.currentTimeMillis() + ".yml"));

            // generate a new file
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String fileToYAML(File fileToRead) throws IOException {
        // create a FileInputStream
        FileInputStream stream = new FileInputStream(fileToRead);

        // load the file
        return fileToYAML(stream);
    }

    protected String fileToYAML(InputStream stream) throws IOException {
        // create the InputStreamReader with Charset UTF-8,
        // this allows loading files with special characters
        // such as accents
        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        BufferedReader input = new BufferedReader(reader);

        int commentNum = 0;
        int spaces = 0;
        boolean stringList = false;
        String addLine;
        String currentLine;

        StringBuilder whole = new StringBuilder();

        // read all the lines from the file
        while ((currentLine = input.readLine()) != null) {
            if (currentLine.equalsIgnoreCase("---")) { // crowdin line ignore
                continue;
            }

            // if the line starts with the #...
            if (currentLine.replace(" ", "").startsWith("#")) {
                // ...it is a comment so we need to convert it
                // into a YAML path
                addLine = currentLine.replace(":", "REPLACED_COLON").replace("'", "SINGLE_QUOTE").replace("{", "RIGHT_BRACKET").replace("}", "LEFT_BRACKET").replace("(", "RIGHT_PARENTHESIS").replace(")", "LEFT_PARENTHESIS").replaceFirst("#", "GAMECORE_COMMENT_" + commentNum + ":").replace(": ", ": '").replace(":#", ": '#");
                whole.append(addLine).append("'\n");
                commentNum++;
                spaces = countSpaces(addLine);
            } else if(currentLine.isEmpty()) {
                // the line is blank so we just use GAMECORE_BLANKLINE
                // as an identifier for the blank line
                whole.append(StringUtils.repeat(" ", (stringList ? spaces-2 : spaces))).append("GAMECORE_BLANKLINE").append(commentNum).append(": ''\n");
                commentNum++;
            } else {
                // the line is a normal line so just append
                stringList = currentLine.trim().startsWith("-");
                whole.append(currentLine).append("\n");
                spaces = countSpaces(currentLine);
            }
        }

        input.close();
        return whole.toString();
    }

    protected int countSpaces(String str) {
        int count = 0;
        for(int i = 0; i < str.length(); i++) {
            if(Character.isWhitespace(str.charAt(i))) {
                count++;
            } else {
                return count;
            }
        }

        return count;
    }

    private String escapeYamlCharacters(String configString) {
        String[] lines = configString.split("\n");
        StringBuilder config = new StringBuilder();
        for (String line : lines) {
            if (line.contains("GAMECORE_COMMENT")) {
                String comment = line.replace(line.substring(line.indexOf("GAMECORE_COMMENT"), line.indexOf(":") + 2), "# ");

                if (comment.startsWith("# '")) {
                    comment = comment.substring(0, comment.length() - 1).replaceFirst("# '#", "##").replaceFirst("# '", "# ").replace("REPLACED_COLON", ":");
                } else {
                    comment = comment.replace("REPLACED_COLON", ":").replace("SINGLE_QUOTE", "'").replace("RIGHT_BRACKET", "{").replace("LEFT_BRACKET", "}").replace("RIGHT_PARENTHESIS", "(").replace("LEFT_PARENTHESIS", ")");
                }

                config.append(comment).append("\n");
            } else if(line.contains("GAMECORE_BLANKLINE")) {
                config.append("\n");
            } else {
                config.append(line).append("\n");
            }
        }

        return config.toString();
    }

    protected String getHeader(String title) {
        return "############################################################\n" +
               "# +------------------------------------------------------+ #\n" +
               "# |" + StringUtils.center(title, 54) + "| #\n" +
               "# +------------------------------------------------------+ #\n" +
               "############################################################\n";
    }
}
