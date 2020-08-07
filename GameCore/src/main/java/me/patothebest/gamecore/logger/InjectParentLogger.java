package me.patothebest.gamecore.logger;

import me.patothebest.gamecore.modules.Module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface InjectParentLogger {

    Class<? extends Module> parent();

}
