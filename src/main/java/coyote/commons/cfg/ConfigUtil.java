/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.cfg;

import coyote.commons.Log;
import coyote.commons.StringUtil;

import java.io.File;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * The ConfigUtil class models a static fixture that holds a map of strings and
 * a map of capsules that can be shared in a runtime environment.
 *
 * <p>This class allows very complex configuration to be managed in a simple
 * fashion. Resource bundles can be merged with this fixture providing the
 * single point of reference for all resources.
 *
 * <p>The various static methods on this fixture provide generic configuration
 * utilities like validating a work directory and setting up a work directory
 * in the home of the account executing the runtime.
 */
public class ConfigUtil {

    /**
     * The mapping of names to objects
     */
    private static final HashMap resources = new HashMap();

    /**
     * DataCapsule map allowing all components to share configuration objects
     */
    private static final HashMap capsules = new HashMap();


    /**
     * Private constructor because everything is static
     */
    private ConfigUtil() {
    }


    /**
     * Method getString
     *
     * @param key
     * @return TODO finish documentation.
     */
    public static String getString(String key) {
        Object obj = resources.get(key);

        if (obj != null) {
            return obj.toString();
        }

        return key;
    }


    /**
     * Method getString
     *
     * @param key  name of the value to return
     * @param dflt the default value to return
     *
     */
    public static String getString(String key, String dflt) {
        Object obj = resources.get(key);

        if (obj != null) {
            return obj.toString();
        }

        return dflt;
    }


    /**
     * Take all the strings from the given resource bundle and merge it into our
     * resource map, overwriting existing keys.
     *
     * @param bundle The resource bundle to merge into our own.
     */
    public static void merge(ResourceBundle bundle) {
        if (bundle != null) {
            synchronized (resources) {
                for (Enumeration bundleKeys = bundle.getKeys(); bundleKeys.hasMoreElements(); ) {
                    String key = (String) bundleKeys.nextElement();
                    String value = bundle.getString(key);
                    resources.put(key, value);
                }
            }
        } else {
            System.err.println("ResourceBundle was null");
        }
    }


    /**
     * Returns the first character of the actual language text out of the
     * resource bundle.
     *
     * <p>The method can be useful for getting mnemonics. If the key is not
     * defined, it returns the first char of the key itself.
     *
     * @param key
     *
     */
    public static char getChar(String key) {
        String str = getString(key);
        return str.charAt(0);
    }


    /**
     * Returns the actual language text out of the resource bundle and formats it
     * accordingly with the given Object array.
     *
     * <p>If the key is not defined it returns the key itself.
     *
     * @param key
     * @param obj
     * @return TODO finish documentation.
     */
    public static String getFormatedString(String key, Object[] obj) {
        String value = null;

        try {
            value = MessageFormat.format(getString(key), obj);
        } catch (MissingResourceException exp) {
        }

        if (value == null) {
            value = key;
        }

        return value;
    }


    /**
     * Setup a working directory in the user's home directory with the given name.
     *
     * @param dirname name of the directory to create
     * @return A reference to a file allowing access to the working directory
     * @throws ConfigurationException If the working directory could not be created
     */
    public static File initHomeWorkDirectory(String dirname) throws ConfigurationException {
        // if the name was null, create a directory named "work"
        if (dirname == null) {
            dirname = "wrk";
        }

        // setup a reference to the user's home directory
        return validateWorkDirectory(System.getProperty("user.home") + System.getProperty("file.separator") + dirname);
    }


    /**
     * Validate and return a file reference suitable for using as a working
     * directory.
     *
     * @param dirname name of the directory to create
     * @return A reference to a file representing the directory.
     * @throws ConfigurationException if the directory could not be made.
     */
    public static File validateWorkDirectory(String dirname) throws ConfigurationException {
        // if the name was null, create a directory named "work"
        if (dirname == null) {
            return null;
        }

        // setup a reference to the new work directory
        File retval = new File(dirname);

        // If the given directory name is not absolute...
        if (!retval.isAbsolute()) {
            // ...prepend the current directory
            retval = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + dirname);
        }

        // If the directory does not exist, create it
        if (!retval.exists()) {
            if (!retval.mkdirs()) {
                retval = null;

                throw new ConfigurationException("Could not create\"" + retval + "\" as our working directory");
            }
        }

        // Make sure we can write to it
        if (retval.isDirectory() && retval.canWrite()) {
            return retval;
        } else {
            return null;
        }
    }


    /**
     * Reads a JSON configuration file and returns a Config object.
     *
     * @param filename the name of the file to read
     * @return A Config object representing the JSON data in the file, or an empty Config if the file does not exist or is corrupted.
     */
    public static Config readConfig(String filename) {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(new File(filename))) {
            return Config.read(fis);
        } catch (Exception e) {
            Log.error("Could not read configuration file: " + filename + " - " + e.getMessage());
            return new Config();
        }
    }


    /**
     * Writes the given Config object to a file as nicely formatted JSON.
     *
     * @param cfg      the Config object to write
     * @param filename the name of the file to which the configuration will be written
     * @throws java.io.IOException if there are problems writing the file
     */
    public static void writeConfig(Config cfg, String filename) throws java.io.IOException {
        if (cfg == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }
        if (StringUtil.isBlank(filename)) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        String json = coyote.commons.dataframe.marshal.JSONMarshaler.toFormattedString(cfg);
        if (!coyote.commons.FileUtil.stringToFile(json, filename)) {
            throw new java.io.IOException("Failed to write configuration to file: " + filename);
        }
    }

}