package com.github.ravenlab.classscanner.test;

import com.github.ravenlab.classscanner.ClassScanner;
import com.github.ravenlab.classscanner.JarClassScanner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JarClassScannerTest {

    private static final File TEST_FOLDER = new File("test_data");
    private static final File JAR_FILE = new File(TEST_FOLDER, "test.jar");
    private static final String JAR_URL = "https://repo1.maven.org/maven2/commons-io/commons-io/2.11.0/commons-io-2.11.0.jar";

    @BeforeAll
    public static void setup() throws IOException, URISyntaxException, InterruptedException {
        if (!TEST_FOLDER.exists()) {
            TEST_FOLDER.mkdir();
        }
        if (!JAR_FILE.exists()) {
            download(JAR_URL, JAR_FILE);
        }
    }

    @Test
    public void testScanner() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        URLClassLoader loader = new URLClassLoader("test_loader",
                new URL[] { JAR_FILE.toURI().toURL() },
                this.getClass().getClassLoader());
        Class<?> fileFilter = loader.loadClass("org.apache.commons.io.filefilter.AbstractFileFilter");
        String packageName = "org.apache.commons.io.filefilter";
        ClassScanner<Class> scanner = new JarClassScanner();
        Collection<Class<?>> collected = scanner.collect(packageName, fileFilter, fileFilter);
        for (Class<?> clazz : collected) {
            System.out.println(clazz.getName());
            for (Constructor con : clazz.getDeclaredConstructors()) { //Check that we can construct 0 arg constructors
                con.setAccessible(true);
                if (con.getParameterCount() == 0) {
                    con.newInstance();
                    break;
                }
            }
        }
        assertTrue(collected.size() == 24);
    }

    private static void download(String url, File download) throws IOException,
            URISyntaxException,
            InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        client.send(HttpRequest.newBuilder()
                .GET()
                .uri(new URL(url).toURI())
                .build(), HttpResponse.BodyHandlers.ofFile(download.toPath()));
    }
}
