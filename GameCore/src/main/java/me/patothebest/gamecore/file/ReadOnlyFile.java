package me.patothebest.gamecore.file;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ReadOnlyFile extends FlatFile {

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public ReadOnlyFile(String fileName) {
        super(fileName);
    }

    public ReadOnlyFile(File file) {
        super(file);
    }

    // -------------------------------------------- //
    // PUBLIC METHODS
    // -------------------------------------------- //

    @Override
    public void save() {
        throw new UnsupportedOperationException("File is read-only!");
    }

    @Override
    public void save(File file) throws IOException {
        throw new UnsupportedOperationException("File is read-only!");
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("File is read-only!");
    }

    // -------------------------------------------- //
    // CLASS METHODS
    // -------------------------------------------- //

    @Override
    protected String fileToYAML(InputStream stream) throws IOException {
        // create the InputStreamReader with Charset UTF-8,
        // this allows loading files with special characters
        // such as accents
        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        BufferedReader input = new BufferedReader(reader);

        String currentLine;

        StringBuilder whole = new StringBuilder();

        // read all the lines from the file
        while ((currentLine = input.readLine()) != null) {
            whole.append(currentLine).append("\n");
        }

        input.close();
        return whole.toString();
    }

    protected String getHeader(String title) {
        return "############################################################\n" +
                "# +------------------------------------------------------+ #\n" +
                "# |" + StringUtils.center(title, 54) + "| #\n" +
                "# +------------------------------------------------------+ #\n" +
                "############################################################\n";
    }
}
