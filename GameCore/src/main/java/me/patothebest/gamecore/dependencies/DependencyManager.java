/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.patothebest.gamecore.dependencies;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DependencyManager {

    private static Method ADD_URL_METHOD;
    public static final List<Dependency> DEPENDENCIES_TO_LOAD = new ArrayList<>();
    public static final File dataDirectory = new File("lib");

    public static void loadDependencies(URLClassLoader classLoader) {
        System.out.println("Loading dependencies...");
        System.out.println("Identified the following dependencies: " + DEPENDENCIES_TO_LOAD.toString());

        dataDirectory.mkdirs();

        // Download files.
        List<Map.Entry<Dependency, File>> toLoad = new ArrayList<>();
        for (Dependency dependency : DEPENDENCIES_TO_LOAD) {
            try {
                toLoad.add(new AbstractMap.SimpleEntry<>(dependency, downloadDependency(dataDirectory, dependency)));
            } catch (Throwable e) {
                System.err.println("Exception whilst downloading dependency " + dependency.name());
                e.printStackTrace();
            }
        }

        // Load classes.
        for (Map.Entry<Dependency, File> e : toLoad) {
            if(e.getKey().getTestClass() != null) {
                try {
                    Class.forName(e.getKey().getTestClass());
                    System.out.println("Dependency " + e.getKey().name().toLowerCase() + " was loaded by another plugin already!");
                } catch (ClassNotFoundException ignored) { }
            }

            try {
                loadJar(classLoader, e.getValue());
            } catch (Throwable e1) {
                System.err.println("Failed to load jar for dependency " + e.getKey().name());
                e1.printStackTrace();
            }
        }
    }

    private static File downloadDependency(File libDir, Dependency dependency) throws Exception {
        String name = dependency.name().toLowerCase() + "-" + dependency.getVersion() + ".jar";

        File file = new File(libDir, name);
        if (file.exists()) {
            return file;
        }

        URL url = new URL(dependency.getUrl());

        System.out.println("Dependency '" + name + "' could not be found. Attempting to download.");
        try (InputStream in = url.openStream()) {
            Files.copy(in, file.toPath());
        }

        if (!file.exists()) {
            throw new IllegalStateException("File not present. - " + file.toString());
        } else {
            System.out.println("Dependency '" + name + "' successfully downloaded.");
            return file;
        }
    }

    private static void loadJar(URLClassLoader classLoader, File file) throws Exception {
        ADD_URL_METHOD.invoke(classLoader, file.toURI().toURL());
    }

    static {
        try {
            ADD_URL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            ADD_URL_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}