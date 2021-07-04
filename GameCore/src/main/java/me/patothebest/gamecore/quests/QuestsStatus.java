package me.patothebest.gamecore.quests;

public enum QuestsStatus {

    IN_PROGRESS(0),
    COMPLETED(1),
    FAILED(2);

    private final int statusCode;

    QuestsStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public static QuestsStatus fromCode(int code) {
        return values()[code];
    }

    public int getStatusCode() {
        return statusCode;
    }
}
