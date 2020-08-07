package me.patothebest.gamecore.command;

import me.patothebest.gamecore.lang.interfaces.ILang;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation indicates that a command has a description
 * that can be translatable per player, meaning that the description
 * is an enum element which implements {@link ILang}.
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface LangDescription {

    /**
     * The enum class that extends ILang
     *
     * @return the class which we will use to get the enum element
     */
    Class<? extends Enum<? extends ILang>> langClass();

    /**
     * The element name inside the enum
     *
     * @return the name of the element we will be using
     */
    String element();

}
