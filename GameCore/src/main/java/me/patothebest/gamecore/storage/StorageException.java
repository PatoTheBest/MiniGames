package me.patothebest.gamecore.storage;

/**
 * A class representing an exception that could happen
 * in the storage module.
 */
public class StorageException extends RuntimeException {

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
s     *                later retrieval by the {@link #getMessage()} method.
     */
    public StorageException(String message) {
        super(message);
    }
}
