package com.github.ravenlab.classscanner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.jar.JarFile;

public class JarClassScanner implements ClassScanner<Class> {

    @Override
    public <T> Collection<Class<? extends T>> collect(Class loadFrom, Class<? extends T> superClazz) {
        Collection<Class<? extends T>> list = new ArrayList<>();
        URL url = loadFrom.getProtectionDomain().getCodeSource().getLocation();
        try {
            File file = Paths.get(url.toURI()).toFile();
            try (JarFile jar = new JarFile(file)) {
                
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}
