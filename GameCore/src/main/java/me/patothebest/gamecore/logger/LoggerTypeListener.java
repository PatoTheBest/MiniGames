package me.patothebest.gamecore.logger;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.modules.ModuleName;

import java.lang.reflect.Field;

class LoggerTypeListener implements TypeListener {

    private final LoggerFactory loggerFactory;

    LoggerTypeListener(CorePlugin plugin) {
        this.loggerFactory = new LoggerFactory(plugin);
    }

    public <T> void hear(TypeLiteral<T> typeLiteral, TypeEncounter<T> typeEncounter) {
        Class<?> clazz = typeLiteral.getRawType();
        Class<?> implementationClass = clazz;
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType() == Logger.class  || field.getType() == java.util.logging.Logger.class) {
                    if(field.isAnnotationPresent(InjectLogger.class)) {
                        String name = field.getAnnotation(InjectLogger.class).name();
                        if (name.isEmpty()) {
                            if (clazz.isAnnotationPresent(ModuleName.class)) {
                                name = field.getDeclaringClass().getAnnotation(ModuleName.class).value();
                            } else {
                                name = field.getDeclaringClass().getSimpleName();
                            }
                        }
                        typeEncounter.register(new LoggerMembersInjector<>(field, loggerFactory.getLogger(name)));
                    } else if(field.isAnnotationPresent(InjectImplementationLogger.class)) {
                        String name = implementationClass.getSimpleName();
                        if (implementationClass.isAnnotationPresent(ModuleName.class)) {
                            name = implementationClass.getAnnotation(ModuleName.class).value();
                        }
                        typeEncounter.register(new LoggerMembersInjector<>(field, loggerFactory.getLogger(name)));
                    } else if(field.isAnnotationPresent(InjectParentLogger.class)) {
                        Class<?> parentClass = field.getAnnotation(InjectParentLogger.class).parent();
                        boolean injected = false;
                        for (Field parentField : parentClass.getDeclaredFields()) {
                            if ((parentField.getType() == Logger.class || parentField.getType() == java.util.logging.Logger.class) && parentField.isAnnotationPresent(InjectLogger.class)) {
                                String name = parentField.getAnnotation(InjectLogger.class).name();
                                if (name.isEmpty()) {
                                    if (parentClass.isAnnotationPresent(ModuleName.class)) {
                                        name = parentClass.getAnnotation(ModuleName.class).value();
                                    } else {
                                        name = parentClass.getSimpleName();
                                    }
                                }
                                typeEncounter.register(new LoggerMembersInjector<>(field, loggerFactory.getLogger(name)));
                                injected = true;
                            }
                        }

                        if(!injected) {
                            throw new RuntimeException("Could not inject logger for class " + clazz.getSimpleName());
                        }
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }
    }
}
