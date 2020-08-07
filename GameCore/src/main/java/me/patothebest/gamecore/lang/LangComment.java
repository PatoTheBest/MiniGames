package me.patothebest.gamecore.lang;

import me.patothebest.gamecore.lang.interfaces.IComment;

public class LangComment implements IComment {

    private final String[] lines;
    private final boolean isHeader;

    public LangComment(CommentType commentType, String line) {
        this.isHeader = commentType == CommentType.HEADER;
        this.lines = commentType != CommentType.SUBHEADER_SPACED ? new String[]{line} : new String[]{"", line};
    }

    @Override
    public String[] getLines() {
        return lines;
    }

    @Override
    public boolean isHeader() {
        return isHeader;
    }
}
