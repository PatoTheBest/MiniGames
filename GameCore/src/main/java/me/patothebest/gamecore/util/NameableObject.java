package me.patothebest.gamecore.util;

/**
 * An interface for all objects that should implement the
 * getName() method. This is useful especially for abstract
 * classes and utility methods that their function is, for
 * example, transform a collection of objects to a collection
 * of strings that are the names of the objects.
 */
public interface NameableObject {

    String getName();

}
