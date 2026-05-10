package coyote.commons;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClasspathUtilTest {

    @TempDir
    Path tempDir;

    @Test
    public void testResolveFromDirectory() throws IOException {
        // Setup a directory structure
        Path classesDir = tempDir.resolve("classes");
        Files.createDirectories(classesDir);

        Path pkgDir = classesDir.resolve("com/example");
        Files.createDirectories(pkgDir);

        Files.createFile(pkgDir.resolve("TestClass.class"));

        // We need to manipulate the classpath to include our temp directory
        String originalClasspath = System.getProperty("java.class.path");
        try {
            System.setProperty("java.class.path", originalClasspath + File.pathSeparator + classesDir.toAbsolutePath());

            List<String> results = ClasspathUtil.resolve("TestClass");
            assertTrue(results.contains("com.example.TestClass"), "Should find com.example.TestClass in " + results);
        } finally {
            System.setProperty("java.class.path", originalClasspath);
        }
    }

    @Test
    public void testResolveFromJar() throws IOException {
        Path jarFile = tempDir.resolve("test.jar");
        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(jarFile.toFile().toPath()))) {
            JarEntry entry = new JarEntry("org/apache/MyJarClass.class");
            jos.putNextEntry(entry);
            jos.closeEntry();
        }

        String originalClasspath = System.getProperty("java.class.path");
        try {
            System.setProperty("java.class.path", originalClasspath + File.pathSeparator + jarFile.toAbsolutePath());

            List<String> results = ClasspathUtil.resolve("MyJarClass");
            assertTrue(results.contains("org.apache.MyJarClass"), "Should find org.apache.MyJarClass in " + results);
        } finally {
            System.setProperty("java.class.path", originalClasspath);
        }
    }

    @Test
    public void testResolveStandardLibrary() {
        // String.class should be in java.base module in Java 9+
        List<String> results = ClasspathUtil.resolve("String");
        assertTrue(results.contains("java.lang.String"), "Should find java.lang.String");
    }

    @Test
    public void testResolveDefaultPackage() throws IOException {
        Path classesDir = tempDir.resolve("defaultpkg");
        Files.createDirectories(classesDir);
        Files.createFile(classesDir.resolve("DefaultClass.class"));

        String originalClasspath = System.getProperty("java.class.path");
        try {
            // 1. Test WITHOUT trailing slash
            String pathNoSlash = classesDir.toAbsolutePath().toString();
            if (pathNoSlash.endsWith(File.separator)) {
                pathNoSlash = pathNoSlash.substring(0, pathNoSlash.length() - 1);
            }
            System.setProperty("java.class.path", originalClasspath + File.pathSeparator + pathNoSlash);
            List<String> results = ClasspathUtil.resolve("DefaultClass");
            assertTrue(results.contains("DefaultClass"), "Should find DefaultClass without slash, found: " + results);

            // 2. Test WITH trailing slash
            String pathWithSlash = pathNoSlash + File.separator;
            System.setProperty("java.class.path", originalClasspath + File.pathSeparator + pathWithSlash);
            results = ClasspathUtil.resolve("DefaultClass");
            assertTrue(results.contains("DefaultClass"), "Should find DefaultClass with slash, found: " + results);
        } finally {
            System.setProperty("java.class.path", originalClasspath);
        }
    }

    @Test
    public void testSimpleResolve() throws Exception {
        List<String> names = ClasspathUtil.resolve("List");
        assertNotNull(names);
        // There should be at least 3
        assertTrue(names.size() > 0);
    }
}
