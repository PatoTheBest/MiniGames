package me.patothebest.gamecore.file;

public class ParserValidations {

    public static void isTrue(boolean expression) {
        isTrue(expression, "The validated expression is false");
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new ParserException(message);
        }
    }

    public static void notNull(Object object) {
        notNull(object, "The validated object is null");
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new ParserException(message);
        }
    }

}
