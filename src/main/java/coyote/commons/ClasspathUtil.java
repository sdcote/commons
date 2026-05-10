/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Simple utility to work with class paths.
 *
 * <p>This utility can perform a validation of your context's class path.
 */
public class ClasspathUtil {

    private static volatile boolean checkedClasspathAlready = false;

    /** Map of shadowed class names and the details of where they were found. */
    private static HashMap<String, String> shadowClasses = new HashMap<String, String>();

    /** List of libraries missing from the classpath. */
    private static ArrayList<String> missingLibraries = new ArrayList<String>();

    /**
     * Map of classes (key) to a JAR (value)
     */
    private static HashMap<String, String> jarMap = new HashMap<String, String>();


    /**
     * Simple class path checker.
     *
     * <p>Go through each entry in the class path and verify that it is actually
     * on the file system, can be read, and if it is a JAR or ZIP, verify that
     * the entries can be read.
     *
     * <p>This method will also generate a hashmap of classes that appear in more
     * than one path entry. These shadow classes should not be the cause of
     * errors, but some class loaders can get confused when shadow classes exist.
     * Also, is the wrong version of the class is loaded because it appears first
     * on the class path, the system many not operate as expected. So it is a
     * good idea to eliminate them if possible.<p>
     *
     * <p>This class will also generate a list of missing path entries. These are
     * class path entries which do not exist on the file system. An example of
     * this if if a JAR was removed from the disk and now the system is throwing
     * "class not found" exceptions. Sometimes class paths get too long for the
     * OS environment variables (Windows) and older systems have empty class path
     * entries which are no longer needed. This feature can be used to identify
     * empty. useless entries so they can be removed providing more room for new
     * entries.
     *
     * @return True if the class path is clean, false if there are invalid entries.
     * @see #getShadowedClasses()
     * @see #getShadowClassDetails()
     * @see #getMissingClasspathEntries()
     */
    public static boolean verifyClasspath() {
        checkedClasspathAlready = true;

        HashMap<String, String> classMap = new HashMap<String, String>();
        HashMap<String, String> jarMap = new HashMap<String, String>();
        HashMap<String, String> shadows = new HashMap<String, String>();
        ArrayList<String> missing = new ArrayList<String>();

        @SuppressWarnings("unused")
        int bytesRead;

        boolean retval = true;

        StringTokenizer st = new StringTokenizer(System.getProperty("java.class.path"), System.getProperty("path.separator"));
        while (st.hasMoreTokens()) {
            String entry = st.nextToken();
            File file = new File(entry);
            if (file.exists()) {
                if (file.canRead()) {
                    if (entry.endsWith("jar")) {
                        JarFile jarfile = null;
                        try {
                            jarfile = new JarFile(file);
                            Log.trace("checking '" + entry + "'");
                            for (Enumeration<JarEntry> en = jarfile.entries(); en.hasMoreElements(); ) {
                                JarEntry jentry = en.nextElement();
                                Log.trace("    '" + jentry.getName() + "' " + jentry.getCrc());
                                jarMap.put(jentry.getName(), entry);
                                if (jentry.getName().endsWith(".class") && classMap.containsKey(jentry.getName())) {
                                    shadows.put(jentry.getName() + " found in '" + classMap.get(jentry.getName()) + "'; shadowed in '" + entry + "'", jentry.getName());
                                } else {
                                    classMap.put(jentry.getName(), entry);
                                }

                                try {
                                    InputStream entryStream = jarfile.getInputStream(jentry);
                                    byte[] buffer = new byte[1024];
                                    while ((bytesRead = entryStream.read(buffer)) != -1) ;
                                } catch (Exception ex) {
                                    Log.warn("Class path entry '" + entry + "' is not a valid archive, problems accessing '" + jentry.getName() + "' - " + ex.getMessage());
                                    retval = false;
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            Log.warn("Class path entry '" + entry + "' is not a valid java archive: " + e.getMessage());
                            retval = false;
                        }
                    } else if (entry.endsWith("zip")) {
                        ZipFile zipfile = null;
                        try {
                            zipfile = new ZipFile(file);
                            for (Enumeration<? extends ZipEntry> en = zipfile.entries(); en.hasMoreElements(); ) {
                                ZipEntry zentry = en.nextElement();
                                Log.trace("    '" + zentry.getName() + "' " + zentry.getCrc());
                                if (zentry.getName().endsWith(".class") && classMap.containsKey(zentry.getName())) {
                                    shadows.put(zentry.getName() + " found in '" + classMap.get(zentry.getName()) + "'; shadowed in '" + entry + "'", zentry.getName());
                                } else {
                                    classMap.put(zentry.getName(), entry);
                                }

                                try {
                                    InputStream entryStream = zipfile.getInputStream(zentry);
                                    byte[] buffer = new byte[1024];
                                    while ((bytesRead = entryStream.read(buffer)) != -1) ;
                                } catch (Exception ex) {
                                    Log.warn("Class path entry '" + entry + "' is not a valid archive, problems accessing '" + zentry.getName() + "' - " + ex.getMessage());
                                    retval = false;
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            Log.warn("Class path entry '" + entry + "' is not a valid zip archive: " + e.getMessage());
                            retval = false;
                        }
                    }
                } else {
                    Log.warn("Class path entry '" + entry + "' is not readable");
                    retval = false;
                }
            } else {
                Log.warn("Class path entry '" + entry + "' does not appear to exist on file system");
                missing.add(entry);
                retval = false;
            }

        } // while more path entries

        ClasspathUtil.shadowClasses = shadows;
        ClasspathUtil.missingLibraries = missing;
        ClasspathUtil.jarMap = jarMap;

        return retval;
    }


    /**
     * Get a listing of libraries missing from the class path.
     *
     * <p>This is a listing of all the files or directories specified on the
     * class path but could not be found or read from the file system.
     *
     * @return An array of class path entries that do not exist on the file
     * system.
     */
    public static String[] getMissingClasspathEntries() {
        if (!checkedClasspathAlready)
            ClasspathUtil.verifyClasspath();

        String[] retval = new String[missingLibraries.size()];
        for (int x = 0; x < retval.length; x++) {
            retval[x] = missingLibraries.get(x);
        }
        return retval;
    }


    /**
     * Get a listing of class names that appear in more than one entry in the
     * class path.
     *
     * <p>This not necessarily an error, but it can allow an application to
     * determine why a version of a class is not loading as expected. This may be
     * the case when an older version of the class appears first on the class
     * path.
     *
     * @return an array of fully qualified class names that appear more than one
     * in the class path.
     */
    public static String[] getShadowedClasses() {
        if (!checkedClasspathAlready)
            ClasspathUtil.verifyClasspath();

        HashSet<String> set = new HashSet<String>();
        for (Iterator<String> it = shadowClasses.values().iterator(); it.hasNext(); ) {
            set.add(it.next());
        }

        String[] retval = new String[set.size()];
        int x = 0;
        for (Iterator<String> it = set.iterator(); it.hasNext(); retval[x++] = it.next()) ;

        return retval;
    }


    /**
     * Access a detailed line of text describing what classes were found shadowed
     * (i.e. duplicated) in which class path entries.
     *
     * <p>This not necessarily an error, but it can allow an application to
     * determine why a version of a class is not loading as expected. This may be
     * the case when an older version of the class appears first on the class
     * path.
     * <p>
     * The format is: <pre>
     * <tt>&lt;class&gt;</tt> found in <tt>&lsquo;&lt;initial&gt;</tt>&rsquo;;shadowed in &lsquo;<tt>&lt;secondary&gt;</tt>&rsquo;<br></pre>
     *
     * <p>Where: <tt>&lt;class&gt;</tt> is the fully-qualified class name that
     * appears more than once in the class path, <tt>&lt;initial&gt;</tt> is the
     * class path entry where the class was first located and from where it will
     * probably be found by the class loader, and <tt>&lt;secondary&gt;</tt> is
     * the class path entry when a copy of the class was found.
     *
     * <p>This will show each class that occurs more than once in the class path
     * listing; both the first class path entry from which the class will
     * probably be loaded and the class path entry contains a shadow copy of the
     * entry.
     *
     * @return an array of shadowed class entries.
     */
    public static String[] getShadowClassDetails() {
        if (!checkedClasspathAlready)
            ClasspathUtil.verifyClasspath();

        ArrayList<String> list = new ArrayList<String>();
        for (Iterator<String> it = shadowClasses.keySet().iterator(); it.hasNext(); list.add(it.next())) ;

        String[] retval = new String[list.size()];
        for (int x = 0; x < retval.length; retval[x] = list.get(x++)) ;

        return retval;
    }


    /**
     * Get a list of JAR files in which the given fully qualified class name
     * resides.
     *
     * @param fqrn the fully qualified name of the resource for which to search.
     * @return an array of fully qualified jar file names in which the class was found
     */
    public static String[] findJarForResource(String fqrn) {
        if (!checkedClasspathAlready)
            ClasspathUtil.verifyClasspath();

        ArrayList<String> list = new ArrayList<String>();

        for (Map.Entry<String, String> entry : jarMap.entrySet()) {
            if (entry.getKey().equals(fqrn)) {
                list.add(entry.getValue());
            }
        }

        String[] retval = new String[list.size()];
        for (int x = 0; x < retval.length; retval[x] = list.get(x++)) ;

        return retval;
    }


    /**
     * Get a list of JAR files in which the given type resides.
     *
     * <p>This is a broader search than {@link #findJarForResource(String)} as
     * it will match the end of the entry name.</p>
     *
     * @param string the name of the type for which to search.
     * @return an array of fully qualified jar file names in which the type was found.
     */
    public static String[] findJarForType(String string) {
        String type;
        if (string != null && string.trim().length() > 0) {
            type = string.trim();
            ArrayList<String> list = new ArrayList<String>();

            if (type.indexOf('/') > 0) {
                type = type.substring(type.indexOf('/'));
            }

            for (Map.Entry<String, String> entry : jarMap.entrySet()) {
                if (entry.getKey().endsWith(type)) {
                    list.add(entry.getValue());
                }
            }
            String[] retval = new String[list.size()];
            for (int x = 0; x < retval.length; retval[x] = list.get(x++)) ;
            return retval;
        }
        return new String[0];
    }


    /**
     * Perform basic class path verification, logging any irregularities.
     *
     * @param args - fully qualified names of resources for which to search.
     */
    public static void main(String[] args) {
        //System.getProperties().setProperty( "coyote.commons.Log.trace", "true" );

        if (args.length > 0) {
            for (int x = 0; x < args.length; x++) {
                String[] results = ClasspathUtil.findJarForResource(args[x]);
                if (results.length == 0) {
                    results = ClasspathUtil.findJarForType(args[x]);
                    if (results.length > 0) {
                        StringBuilder buffer = new StringBuilder("The following jars contain the type of ");
                        buffer.append(args[x]);
                        buffer.append("\n");
                        for (int i = 0; i < results.length; buffer.append(results[i++] + "\n")) ;
                        Log.info(buffer);
                    } else {
                        Log.info("Could not find '" + args[x] + "' in any JAR in the currently set classpath");
                    }
                } else {
                    StringBuilder buffer = new StringBuilder("The following jars contain the fully qualified class of ");
                    buffer.append(args[x]);
                    buffer.append("\n");
                    for (int i = 0; i < results.length; buffer.append(results[i++] + "\n")) ;
                    Log.info(buffer);
                }
            }

        } else {

            if (ClasspathUtil.verifyClasspath()) {
                Log.info("Class path checks out O.K.");
            } else {
                Log.warn("Class path has some problems. Check the logs for details. Summary follows:");

                // Get a listing of classes that appear more than once in the class path
                String[] shadowedClasses = ClasspathUtil.getShadowedClasses();

                // print them out
                if (shadowedClasses.length > 0) {
                    StringBuilder buffer = new StringBuilder("The following classes appear more than once in the class path:\n");
                    for (int x = 0; x < shadowedClasses.length; buffer.append(shadowedClasses[x++]).append("\n")) ;
                    Log.warn(buffer);
                } else {
                    Log.info("No shadowed classes were found");
                }

                // Get a detailed listing of classes that appear more than once in the class path
                String[] shadowedDetails = ClasspathUtil.getShadowClassDetails();

                // print them out
                if (shadowedDetails.length > 0) {
                    StringBuilder buffer = new StringBuilder("Details of shadowed classes:\n");
                    for (int x = 0; x < shadowedClasses.length; buffer.append(shadowedDetails[x++]).append("\n")) ;
                    Log.warn(buffer);
                } else {
                    Log.info("No shadowed classes were found");
                }

                String[] missing = ClasspathUtil.getMissingClasspathEntries();
                // print them out
                if (missing.length > 0) {
                    StringBuilder buffer = new StringBuilder("Missing class path entries:\n");
                    for (int x = 0; x < missing.length; buffer.append(missing[x++]).append("\n")) ;
                    Log.warn(buffer);
                } else {
                    Log.info("No missing entries were found");
                }
            }
        }
    }

    /**
     * Resolves a simple class name by searching the classpath for matching fully
     * qualified class names.
     *
     * <p>This is a resource-intensive process and should not be performed as
     * part of a normal application operation. It is intended for diagnostic or
     * debugging purposes only.</p>
     *
     * @param simpleName the simple name of the class to resolve
     * @return a list of fully qualified class names that match the simple name
     */
    public static List<String> resolve(String simpleName) {
        Set<String> results = new LinkedHashSet<>();
        String targetFileName = simpleName + ".class";

        // 1. Scan the standard Java libraries (Java 9+ Modules)
        scanJrtFileSystem(targetFileName, results);

        // 2. Scan the application classpath (Directories and JARs)
        scanClasspath(targetFileName, results);

        return new ArrayList<>(results);
    }

    /**
     * Scan the application classpath (Directories and JARs) for the target file.
     *
     * @param targetFileName the name of the file to search for.
     * @param results        the set to which results will be added.
     */
    private static void scanClasspath(String targetFileName, Set<String> results) {
        String classpath = System.getProperty("java.class.path");
        String[] paths = classpath.split(File.pathSeparator);

        for (String path : paths) {
            File file = new File(path);
            if (!file.exists()) continue;

            if (file.isDirectory()) {
                scanDirectory(file, file.getAbsolutePath(), targetFileName, results);
            } else if (file.getName().toLowerCase().endsWith(".jar")) {
                scanJar(file, targetFileName, results);
            }
        }
    }

    /**
     * Scan a directory recursively for the target file.
     *
     * @param dir            the directory to scan.
     * @param rootPath       the root path of the directory scan.
     * @param targetFileName the name of the file to search for.
     * @param results        the set to which results will be added.
     */
    private static void scanDirectory(File dir, String rootPath, String targetFileName, Set<String> results) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) {
                scanDirectory(f, rootPath, targetFileName, results);
            } else if (f.getName().equals(targetFileName)) {
                // Convert file path to FQCN format
                String absPath = f.getAbsolutePath();
                String classNamePath = absPath.substring(rootPath.length(), absPath.length() - 6);
                if (classNamePath.startsWith(File.separator)) {
                    classNamePath = classNamePath.substring(1);
                }
                results.add(classNamePath.replace(File.separatorChar, '.'));
            }
        }
    }

    /**
     * Scan a JAR file for the target file.
     *
     * @param jarFile        the JAR file to scan.
     * @param targetFileName the name of the file to search for.
     * @param results        the set to which results will be added.
     */
    private static void scanJar(File jarFile, String targetFileName, Set<String> results) {
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            String targetSuffix = "/" + targetFileName;

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                // JAR entries always use '/' as the separator
                if (!entry.isDirectory() && (name.equals(targetFileName) || name.endsWith(targetSuffix))) {
                    String className = name.substring(0, name.length() - 6).replace('/', '.');
                    results.add(className);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read JAR: " + jarFile.getAbsolutePath());
        }
    }

    /**
     * Scan the JRT file system (Java 9+ Modules) for the target file.
     *
     * @param targetFileName the name of the file to search for.
     * @param results        the set to which results will be added.
     */
    private static void scanJrtFileSystem(String targetFileName, Set<String> results) {
        try {
            FileSystem jrt = FileSystems.getFileSystem(URI.create("jrt:/"));
            Path modulesPath = jrt.getPath("/modules");

            try (Stream<Path> paths = Files.walk(modulesPath)) {
                paths.filter(path -> path.getFileName() != null && path.getFileName().toString().equals(targetFileName))
                        .forEach(path -> {
                            // JRT paths look like: /modules/java.base/java/util/List.class
                            // We must drop the first two elements ("/modules" and "java.base")
                            int nameCount = path.getNameCount();
                            if (nameCount > 2) {
                                StringBuilder fqcn = new StringBuilder();
                                for (int i = 2; i < nameCount; i++) {
                                    fqcn.append(path.getName(i));
                                    if (i < nameCount - 1) fqcn.append(".");
                                }
                                // Remove the ".class" extension
                                results.add(fqcn.substring(0, fqcn.length() - 6).toString());
                            }
                        });
            }
        } catch (Exception e) {
            // Ignore if running on Java 8 or if the JRT filesystem is inaccessible
        }
    }

    /**
     * When the context is loaded, perform a scan of the class path to see if
     * there are any irregularities.
     *
     * <p>This class supports {@code ServletContextListener} so it can be easily
     * included in a web.xml or other servlet context file:<pre>&lt;listener&gt;
     *   &lt;listener-class&gt;coyote.commons.ClasspathUtil$ClasspathListener&lt;/listener-class&gt;
     * &lt;/listener&gt;
     * </pre>to perform a validation of your context's class path.
     */
    public static class ClasspathListener implements ServletContextListener {
        @Override
        public void contextInitialized(ServletContextEvent sce) {
            // just call the main method to perform the check
            ClasspathUtil.main(null);
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
        }
    }

}
