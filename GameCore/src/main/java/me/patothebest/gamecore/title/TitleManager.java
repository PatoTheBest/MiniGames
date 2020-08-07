package me.patothebest.gamecore.title;

import me.patothebest.gamecore.util.Utils;

public class TitleManager {

    /**
     * Creates a new title
     *
     * @param title the title string
     * @return the title
     */
    public static Title newInstance(String title) {
        if(Utils.SERVER_VERSION.contains("1_7")) {
            return new TitleProtocol1_7(title);
        } else if(Utils.SERVER_VERSION.contains("1_8") || Utils.SERVER_VERSION.contains("1_9") || Utils.SERVER_VERSION.contains("1_10")) {
            return new Title1_8_1_10(title);
        } else if(Utils.SERVER_VERSION.contains("1_11") || Utils.SERVER_VERSION.contains("1_12") || Utils.SERVER_VERSION.contains("1_13") || Utils.SERVER_VERSION.contains("1_14") || Utils.SERVER_VERSION.contains("1_15") || Utils.SERVER_VERSION.contains("1_16")) {
            return new Title1_11_1_16(title);
        } else {
            return new UnsupportedTitle(title);
        }
    }
}
