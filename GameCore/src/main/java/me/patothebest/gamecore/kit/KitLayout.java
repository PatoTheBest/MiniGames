package me.patothebest.gamecore.kit;

import me.patothebest.gamecore.file.ParserException;

public class KitLayout {

    private int[] remap = new int[36];

    public KitLayout() {}

    public KitLayout(int[] remap) {
        this.remap = remap;
    }

    public KitLayout(String layout) {
        parseLayout(layout);
    }

    public void parseLayout(String input) throws ParserException {
        String[] split = input.split(",");
        for (int i = 0; i < split.length && i < remap.length; i++) {
            try {
                remap[i] = Integer.parseInt(split[i]);
            } catch (NumberFormatException e) {
                throw new ParserException(split[i] + " is not a number!");
            }
        }
    }

    public String toString() {
        StringBuilder stb = new StringBuilder();
        stb.append(remap[0]);
        for (int i = 1; i < remap.length; i++) {
            stb.append(",").append(remap[i]);
        }
        return stb.toString();
    }

    public int[] getRemap() {
        return remap;
    }
}
