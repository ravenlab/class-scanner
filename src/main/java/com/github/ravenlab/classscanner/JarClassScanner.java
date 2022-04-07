package com.github.ravenlab.classscanner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarClassScanner implements ClassScanner<Class> {

    @Override
    public <T> Collection<Class<? extends T>> collect(String packagePattern,
                                                      Class source,
                                                      Class<? extends T> superClazz) {
        Collection<Class<? extends T>> list = new ArrayList<>();
        URL url = source.getProtectionDomain().getCodeSource().getLocation();
        try {
            File file = Paths.get(url.toURI()).toFile();
            try (JarFile jar = new JarFile(file)) {
                Iterator<JarEntry> it = jar.entries().asIterator();
                while (it.hasNext()) {
                    JarEntry next = it.next();
                    String className = next.getName().replace("/", ".");
                    if (className.startsWith(packagePattern) && className.endsWith(".class")) {
                        String loadedName = className.replace(".class", "");
                        Class<?> checkClass = Class.forName(loadedName, false, source.getClassLoader());
                        if (superClazz.isAssignableFrom(checkClass)
                                && !Modifier.isAbstract(checkClass.getModifiers())) {
                            list.add((Class<? extends T>) checkClass);
                        }
                    }
                }
            }
        } catch (URISyntaxException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }
}