/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import coyote.commons.ExceptionUtil;
import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.cli.ArgumentException;
import coyote.commons.cli.ArgumentList;
import coyote.commons.cli.ArgumentParser;
import coyote.commons.cli.Options;
import coyote.commons.cli.PosixParser;
import coyote.commons.log.ConsoleAppender;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.snap.SnapJob;

/**
 * This acts as a very generic loader of classes based on configuration files.
 * 
 * <p>The bootStrap loader takes an opinionated approach to running generic jobs allowing the developer to focus on business logic 
 * instead of everything involved with creating CLI tools.</p>
 * 
 * <p>Snap Jobs are single-threaded components that perform some task, then exits. While the Job may be multithreaded, the
 * BootStrap loader simply runs the job in the main thread.</p>
 * 
 * <p>The lifecycle is simple:<ol>
 * <li>Get called from the command line</li>
 * <li>Determine and load a configuration file</li>
 * <li>Determine the class to load</li>
 * <li>Load the class dynamically</li>
 * <li>Pass it a configuration object</li>
 * <li>Start it running.</li>
 * <li>Exit when the main thread returns.</li>
 * </ol></p>
 * 
 * <p>The bulk of the responsibilities of this class is in the area of configuration. It tries to determine a configuration file 
 * based on the first argument on the command line. It is assumed to be a file name or a URI indicating a resource to be read. Once 
 * a file or resource has been received, it is parsed and used to dynamically load the class specified.</p>
 */
public class BootStrap {
    private static Config configuration = null;
    private static String cfgLoc = null;
    private static URI cfgUri = null;
    public static String APP_HOME = "app.home";

    private static final String JSON_EXT = ".json";

    /** Name ({@value}) of the system property containing the URI used to load the configuration. */
    public static final String CONFIG_DIR = "cfg.dir";



    static {
        // Set up a default uncaught exception handler to assist in diagnosing
        // silent thread death in any components.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                e.printStackTrace(new java.io.PrintWriter(out, true));
                String message = "UNCAUGHT THREAD EXCEPTION: " + t.getName() + ": " + e.getMessage() + "\n" + out.toString();
                Log.fatal(message);
                System.err.println(message);
            }
        });
    }

    /**
     * Main entry point of the bootstrap loader.
     * 
     * @param args command line arguments
     * @throws ArgumentException
     */
    public static void main(String[] args) throws ArgumentException {
        confirmAppHome();

        // set the default logger
        Log.addLogger(Log.DEFAULT_LOGGER_NAME, new ConsoleAppender(Log.INFO_EVENTS | Log.NOTICE_EVENTS | Log.ERROR_EVENTS | Log.FATAL_EVENTS));

        // Parse the command line arguments
        parseArgs(args);

        Log.info("Info logging is enabled");
        Log.debug("Debug logging is enabled");
        Log.trace("Trace logging is enabled");

        // confirm the configuration location is a valid URI
        confirmConfigurationLocation();

        // Read in the configuration
        readConfig();

        SnapJob job = loadJob(args);

        // If we have a loader
        if (job != null) {

            // Register a shutdown method to terminate cleanly when the JVM exit
            registerShutdownHook(job);

            // Start the job running in the current thread
            try {
                job.start();
            } catch (Exception e) {
                System.err.println("The job threw an exception and terminated: " + e.getLocalizedMessage() + " - " + ExceptionUtil.stackTrace(e));
                System.exit(3);
            }
        } else {
            System.err.println("No job was created.");
            System.exit(2);
        }

        // We don't have to stop the job because the shutdown hook will do it for us when the runtime terminates.

        // Normal termination
        System.exit(0);

    }

    /**
     * Determine the job class to use from the given configuration and create an instance of it.
     * 
     * <p>Once created, the job will be passed the configuration resulting in a configured job.</p>
     * 
     * @param args the command line arguments passed to this bootstrap loader
     * 
     * @return a configured job or null if there was no "CLASS" attribute in the root of the configuration indicating was not found.
     */
    private static SnapJob loadJob(String[] args) {
        SnapJob retval = null;

        // use the first attribute of the configuration as the classname.
        String className = configuration.getField(0).getName();

        // if the class is not fully qualified, assume the same namespace as the bootstrap loader.
        if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
            className = BootStrap.class.getPackage().getName() + "." + className;
        }

        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getConstructor();
            Object object = ctor.newInstance();

            if (object instanceof SnapJob) {
                retval = (SnapJob) object;
                try {
                    retval.setCommandLineArguments(args);
                    retval.configure(configuration);
                } catch (ConfigurationException e) {
                    System.err.println("could_not_config_job" + object.getClass().getName() + " " + e.getClass().getSimpleName() + " " + e.getMessage());
                    System.exit(6);
                }
            } else {
                System.err.println("class_is_not_job" + className);
                System.exit(5);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            System.err.println("Instantiation Error: " + className + " was not found - " + e.getClass().getName() + ": " + e.getMessage());
            System.exit(4);
        }

        return retval;
    }

    /**
     * 
     * @param args
     * @throws ArgumentException
     */
    private static void parseArgs(String[] args) throws ArgumentException {
        final Options options = new Options();
        options.addOption("h", "help", false, "show help");
        options.addOption("d", "debug", false, "debug logging");
        options.addOption("q", "quiet", false, "No logging");

        ArgumentParser parser = new PosixParser();

        ArgumentList argList = parser.parse(options, args);
        if (argList.hasOption("v")) {
            System.out.println("v1.23");
            System.exit(0);
        }
        if (argList.hasOption('h')) {
            System.out.println("Help!");
            System.exit(0);
        }

        if (argList.hasOption('q')) {
            Log.stopLoggingAllCategories();
        } else if (argList.hasOption('d')) {
            Log.startLoggingAllCategories();
        }

        // use the first non-delimited argument as the config location,
        // others are considered arguments to the loader
        for (int x = 0; x < args.length; x++) {
            if (!args[x].startsWith("-") && cfgLoc == null) {
                cfgLoc = args[x];
                break;
            }
        }

        // Make sure we have a configuration
        if (StringUtil.isBlank(cfgLoc)) {
            System.err.println("error_no_config");
            System.exit(8);
        }

    }

    /**
     * Add a shutdown hook into the JVM to help the job shut everything down nicely.
     *
     * @param job The job to terminate
     */
    protected static void registerShutdownHook(final SnapJob job) {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread("LoaderHook") {
                public void run() {
                    Log.debug("runtime_terminating");
                    if (job != null) { job.stop(); }
                    Log.debug("runtime_terminated");
                }
            });
        } catch (Exception e) {
            // Ignore - should not happen
        }
    }

    protected static void confirmAppHome() {
        // see if there is an environment variable or a system property with a shared
        // configuration directory
        String path = getAppHome();

        // if there is a application home directory specified
        if (StringUtil.isNotBlank(path)) {

            // remove all the relations and duplicate slashes
            String appDir = FileUtil.normalizePath(path);

            // create a file reference to that shared directory
            File homeDir = new File(appDir);

            if (homeDir.exists()) {
                // make sure it is a directory
                if (homeDir.isDirectory()) {
                    if (!homeDir.canRead()) {
                        System.out.println("The app.home property specified an un-readable (permissions) directory: " + appDir);
                    }
                } else {
                    System.out.println("The app.home property does not specify a directory: " + appDir);
                }
            } else {
                System.out.println("The app.home property does not exist: " + appDir);
            }

        }

    }


    /**
     * Generate a configuration from the file URI specified on the command line or the {@code cfg.uri} system property.
     * 
     * <p>
     * If the URI has no scheme, then it is assumed to be a file name. If the file name is relative, the current directory will be
     * checked for its existence. if it does not exist there, the {@code cfg.dir} system property is used to determine a common
     * configuration directory and the existence of the file will be checked in that location. If the file does not exist there, a
     * simple error message is displayed and the bootstrap loader terminates.
     * </p>
     */
    private static void readConfig() {
        try {
            configuration = Config.read(cfgUri);
            if (StringUtil.isBlank(configuration.getName())) {
                configuration.setName(UriUtil.getBase(cfgUri));
            }
            String configUri = cfgUri.toString();
            System.setProperty(ConfigTag.CONFIG_URI, configUri);
            if (StringUtil.isNotBlank(configUri) && configUri.startsWith("file:")) {
                File cfgFile = UriUtil.getFile(cfgUri);
                if (cfgFile != null && cfgFile.exists()) {
                    System.setProperty(CONFIG_DIR, cfgFile.getParent());
                }
            }
        } catch (IOException | ConfigurationException e) {
            System.err.println("error_reading_configuration "+ cfgUri+ " - " + e.getLocalizedMessage()+""+ ExceptionUtil.stackTrace(e));
            System.exit(7);
        }

    }

    /**
     * Confirm the configuration location
     */
    private static void confirmConfigurationLocation() {
        // start building error messages for user feedback
        StringBuffer errMsg = new StringBuffer("confirming_cfg_location " + cfgLoc + StringUtil.CRLF);

        // process the configuration location if it exists
        if (StringUtil.isNotBlank(cfgLoc)) {

            // all configurations locations should be a URI
            try {
                cfgUri = new URI(cfgLoc);
            } catch (URISyntaxException e) {
                // This can happen when the location is a filename
            }

            // if we could not create a URI from the location or its scheme is empty
            if (cfgUri == null || StringUtil.isBlank(cfgUri.getScheme())) {

                // try the location as a file
                File localfile = new File(cfgLoc);
                // keep track of an alternative version of specifying the file
                File alternativeFile = new File(cfgLoc + JSON_EXT);

                // make sure a file object has been created
                if (localfile != null) {

                    // if the file is an absolute path or is relative and exists in the
                    // current working directory...
                    if (localfile.exists()) {
                        // we are done, get the file location as a URI
                        cfgUri = FileUtil.getFileURI(localfile);
                    } else {
                        if (!alternativeFile.exists()) {
                            alternativeFile = null;
                        }

                        // add the filename we checked unsuccessfully to the error message
                        errMsg.append("Loader.no_local_cfg_file" +""+ localfile.getAbsolutePath() + StringUtil.CRLF);

                        // the file does not exist, so if it is a relative filename...
                        if (!localfile.isAbsolute()) {

                            // see if there is a system property with a shared configuration directory
                            String path = getAppHome();

                            // if there is a application home directory specified
                            if (StringUtil.isNotBlank(path)) {

                                // remove all the relations and duplicate slashes
                                String appDir = FileUtil.normalizePath(path);

                                // create a file reference to that shared directory
                                File homeDir = new File(appDir);

                                // create a reference to the configuration directory
                                File configDir = new File(homeDir, "cfg");

                                // make sure it exists
                                if (configDir.exists()) {
                                    // make sure it is a directory
                                    if (configDir.isDirectory()) {
                                        // make a file reference to expected file
                                        File cfgFile = new File(configDir, cfgLoc);
                                        // see if it exists
                                        if (cfgFile.exists()) {
                                            // Success - cfg was found in the shared config directory
                                            cfgUri = FileUtil.getFileURI(cfgFile);
                                        } else {
                                            // if an laternat was found in the local directory, use it
                                            if (alternativeFile != null) {
                                                cfgUri = FileUtil.getFileURI(alternativeFile);

                                            } else {
                                                // try adding an extension to the file in the common cfg directory
                                                alternativeFile = new File(configDir, cfgLoc + JSON_EXT);
                                                if (alternativeFile.exists()) {
                                                    // Success - cfg was found in the shared config directory with an added
                                                    // extension
                                                    cfgUri = FileUtil.getFileURI(alternativeFile);
                                                } else {
                                                    // we tried the local and shared locations, report error
                                                    errMsg.append( "Loader.no_common_cfg_file"+ cfgFile.getAbsolutePath() + StringUtil.CRLF);
                                                    errMsg.append("Loader.cfg_file_not_found"+ cfgLoc  + StringUtil.CRLF);
                                                    System.out.println(errMsg.toString());
                                                    System.exit(9);
                                                }
                                            }
                                        }
                                    } else {
                                        // the specified config directory was not a directory
                                        errMsg.append("Loader.cfg_dir_is_not_directory"+ appDir + StringUtil.CRLF);
                                        System.out.println(errMsg.toString());
                                        System.exit(10);
                                    }
                                } else {
                                    // the specified config directory does not exist
                                    errMsg.append("Loader.cfg_dir_does_not_exist"+ appDir + StringUtil.CRLF);
                                    System.out.println(errMsg.toString());
                                    System.exit(11);
                                }
                            } else {
                                if (alternativeFile != null) {
                                    cfgUri = FileUtil.getFileURI(alternativeFile);
                                } else {
                                    // no shared config directory provided in system properties
                                    errMsg.append( "Loader.cfg_dir_not_provided"+ APP_HOME + StringUtil.CRLF);
                                    System.out.println(errMsg.toString());
                                    System.exit(12);
                                }
                            }

                        } // localfile is absolute

                    } // localfile does not exist

                } // localfile != null

            } // cfguri is not valid

            if (cfgUri != null) {
                // Now check to see if the CFG is readable (if it is a file)
                if (UriUtil.isFile(cfgUri)) {
                    File test = UriUtil.getFile(cfgUri);
                    if (!test.exists() || !test.canRead()) {
                        errMsg.append("Loader.cfg_file_not_readable"+ test.getAbsolutePath() + StringUtil.CRLF);
                        System.out.println(errMsg.toString());
                        System.exit(13);
                    }
                    Log.info( "Loader.cfg_reading_from_file"+ test.getAbsolutePath());
                } else {
                    Log.info("Loader.cfg_reading_from_network");
                }
            } else {
                errMsg.append( "Loader.cfg_file_not_found"+ cfgLoc + StringUtil.CRLF);
                System.out.println(errMsg.toString());
                System.exit(9);

            }

        } else {
            System.err.println("Loader.no_config_uri_defined");
            System.exit(1);
        }

    }

    /**
     * This returns the value of app.home from either the environment variables or the system properties, with the system properties
     * overriding the environment variables.
     *
     * @return The value of app.home from either the environment variables or system properties or null if neither are defined.
     */
    protected static String getAppHome() {
        return getVariable(APP_HOME);
    }

    /**
     * Returns the value from either the environment variables or the system properties with the system properties taking precedence
     * over environment variables.
     *
     * @param variable the name of the variable to lookup
     * @return The value from either the environment variables or system properties or null if neither are defined.
     */
    private static String getVariable(String variable) {
        String retval = System.getenv().get(variable);
        if (StringUtil.isNotBlank(System.getProperties().getProperty(variable))) {
            retval = System.getProperties().getProperty(variable);
        }
        return retval;
    }

}
