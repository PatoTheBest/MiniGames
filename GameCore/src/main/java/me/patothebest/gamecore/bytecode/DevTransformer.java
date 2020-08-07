package me.patothebest.gamecore.bytecode;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import me.patothebest.gamecore.util.Utils;

import java.io.File;
import java.io.IOException;

public class DevTransformer {

    private final ClassPool pool = ClassPool.getDefault();

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected 1 argument: directory with classes to transform");
        }
        File classesDir = new File(args[0]);

        new DevTransformer().instrumentClassesIn(classesDir);
    }

    private void instrumentClassesIn(File classesDir) throws Exception {
        pool.appendClassPath(classesDir.getPath());

        CtClass ctClass = modifyConstant(Utils.class.getName(), "USER_ID", "PATO_DEV");
        ctClass.writeFile(classesDir.getPath());
        System.out.println(">>>" + DevTransformer.class.getSimpleName() + ": Transformation done for "
                + Utils.class.getName());
    }

    private CtClass modifyConstant(String clazz, String constantName, String value) {
        try {
            CtClass pt = pool.get(clazz);
            CtField field = pt.getField(constantName);
            pt.removeField(field);
            CtField newField = CtField.make("private static final String " + constantName + "=\"" + value + "\";", pt);
            pt.addField(newField);
            pt.writeFile();
            return pt;
        } catch (NotFoundException | CannotCompileException | IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return null;
    }
}