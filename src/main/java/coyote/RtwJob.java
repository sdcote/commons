/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote;

import coyote.commons.ExceptionUtil;
import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.Symbols;
import coyote.commons.rtw.TransformEngine;
import coyote.commons.rtw.TransformEngineFactory;
import coyote.commons.snap.AbstractSnapJob;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * This is a Snap job that handles Read-Transform-Write (RTW) tasks.
 *
 * <p>Using the {@code coyote.commons.rtw} package, this job uses a set of common classes to perform basic data
 * processing. It initializes a {@link TransformEngine} based on the provided configuration and runs it.</p>
 *
 * <p>The job supports features like an ever-repeating execution, command-line argument passing to the engine's
 * symbol table, and automatic determination of home and work directories based on environment or configuration.</p>
 */
public class RtwJob extends AbstractSnapJob {

    /**
     * If there is no specified directory in the {@code APP_HOME} system property, just use the current working directory.
     */
    public static final String DEFAULT_HOME = System.getProperty("user.dir");

    /**
     * The command line argument used to override the work directory.
     */
    private static final String OVERRIDE_WORK_DIR_ARG = "-owd";

    /**
     * The name of the default work directory under the application home.
     */
    private static final String WORK_DIR_NAME = "wrk";

    /**
     * The name of the default log directory (reserved for future use or descriptive purposes).
     */
    private static final String LOG_DIR_NAME = "log";

    /**
     * The transformation engine that performs the actual data processing.
     */
    TransformEngine engine = null;

    /**
     * Flag indicating whether the job should repeat its execution indefinitely.
     */
    boolean repeat = false;


    /**
     * Configure the job and run any initialization prior to being started.
     *
     * <p>This method initializes the job name, home directory, work directory, and the transformation engine.
     * It also populates the engine's symbol table with command-line arguments, environment variables, and
     * system properties.</p>
     *
     * @param cfg The configuration for the job.
     * @throws ConfigurationException If there is a problem with the configuration or engine initialization.
     */
    @Override
    public void configure(Config cfg) throws ConfigurationException {
        try {
            super.configure(cfg);

            // Support the concept of an ever-repeating job
            try {
                repeat = configuration.getBoolean(ConfigTag.REPEAT);
            } catch (NumberFormatException ignore) {
                // probably does not exist
            }

            // Ensure we have a name in our configuration
            determineName();

            // calculate and normalize the appropriate value for "app.home"
            determineHomeDirectory();

            // Ensure we have a place for our work files if necessary
            determineWorkDirectory();

            // have the Engine Factory create a transformation engine based on our configuration
            engine = TransformEngineFactory.getInstance(getConfig());

            // store the command line arguments in the symbol table of the engine
            if (commandLineArguments != null) {
                for (int x = 0; x < commandLineArguments.length; x++) {
                    engine.getSymbolTable().put(Symbols.COMMAND_LINE_ARG_PREFIX + x, commandLineArguments[x]);
                }
            }

            // store environment variables in the symbol table
            for (String envName : System.getenv().keySet()) {
                engine.getSymbolTable().put(Symbols.ENVIRONMENT_VAR_PREFIX + envName, System.getenv().get(envName));
            }

            for (String propName : System.getProperties().stringPropertyNames()) {
                engine.getSymbolTable().put(Symbols.SYSTEM_PROPERTY_PREFIX + propName, System.getProperty(propName));
            }

            if (StringUtil.isBlank(engine.getName())) {
                Log.trace("Job.unnamed_engine_configured");
            } else {
                Log.trace("Job.engine_configured: " + engine.getName());
            }
        } catch (Throwable e) {
            System.err.println(ExceptionUtil.stackTrace(e));
            throw e;
        }
    }


    /**
     * Start the transformation engine.
     *
     * <p>The engine will run until completion. If the {@code repeat} flag is set to {@code true},
     * the engine will be restarted immediately after each run. Exceptions during execution are logged,
     * and the engine is closed after each run (or each iteration if repeating).</p>
     */
    @Override
    public void start() {
        Log.info("Starting");

        if (engine != null) {
            Log.trace("Job.running: " + engine.getName());

            do {
                try {
                    engine.run();
                } catch (final Exception e) {
                    Log.fatal("Job.exception_running_engine " + e.getClass().getSimpleName() + e.getMessage() + engine.getName() + engine.getClass().getSimpleName());
                    Log.fatal(ExceptionUtil.toString(e));
                    if (Log.isLogging(Log.DEBUG_EVENTS)) {
                        Log.debug(ExceptionUtil.stackTrace(e));
                    }
                } finally {
                    try {
                        engine.close();
                    } catch (final IOException ignore) {
                    }
                    Log.trace("Job completed: " + engine.getName());
                } // try-catch-finally
            }
            while (repeat);

        } else {
            Log.fatal("RtwJob.no_engine");
        }

    }


    /**
     * Shut everything down when the job is requested to stop or the JRE terminates.
     *
     * <p>This calls {@link TransformEngine#shutdown()} to ensure that any long-running processes
     * or resources held by the engine are properly handled.</p>
     *
     * <p>There is a shutdown hook registered with the JRE when this Job is
     * loaded. The shutdown hook will call this method when the JRE is
     * terminating so that the Job can terminate any long-running processes.</p>
     *
     * <p>Note: this is different from {@code close()} but {@code shutdown()}
     * will normally result in {@code close()} being invoked at some point.</p>
     */
    @Override
    public void stop() {
        engine.shutdown();
    }


    /**
     * Determine the value of the "app.home" system property.
     *
     * <p>If the app home property is already set, it is preserved and normalized.
     * If there is no value, this attempts to determine the location
     * of the configuration file used to configure this job (via command-line arguments)
     * and if found, uses that directory as the home directory of all transformation operations.
     * The reasoning is that all artifacts should be kept together.</p>
     *
     * <p>If it cannot be determined from arguments, it defaults to the current working directory.</p>
     */
    protected void determineHomeDirectory() {
        // If our home directory is not specified as a system property...
        if (System.getProperty(BootStrap.APP_HOME) == null) {

            // see of there are command line arguments to use
            if (getCommandLineArguments() != null) {
                // use the first argument to the bootstrap loader to determine the
                // location of our configuration file
                File cfgFile = new File(getCommandLineArguments()[0]);

                // If that file exists, then use that files parent directory as our work
                // directory
                if (cfgFile.exists()) {
                    if (cfgFile.getParentFile() != null) {
                        System.setProperty(BootStrap.APP_HOME, cfgFile.getParentFile().getAbsolutePath());
                    } else {
                        // This is an odd case, but it is a possibility we need to accommodate
                        System.setProperty(BootStrap.APP_HOME, DEFAULT_HOME);
                    }
                } else {
                    // we could not determine the path to the configuration file, use the
                    // current working directory
                    System.setProperty(BootStrap.APP_HOME, DEFAULT_HOME);
                }
            } else {
                System.setProperty(BootStrap.APP_HOME, DEFAULT_HOME);
            }
        } else {

            // Normalize the "." that sometimes is set in the app.home property
            if (System.getProperty(BootStrap.APP_HOME).trim().equals(".")) {
                System.setProperty(BootStrap.APP_HOME, DEFAULT_HOME);
            } else if (StringUtil.isBlank(System.getProperty(BootStrap.APP_HOME))) {
                // catch empty home property and just use the home directory
                System.setProperty(BootStrap.APP_HOME, DEFAULT_HOME);
            }
        }

        // Remove all the relations and extra slashes from the home path
        System.setProperty(BootStrap.APP_HOME, FileUtil.normalizePath(System.getProperty(BootStrap.APP_HOME)));
        Log.debug(String.format("Job home directory set to %s", System.getProperty(BootStrap.APP_HOME)));
    }


    /**
     * Determine and initialize the work directory.
     *
     * <p>The logic follows these steps:
     * <ol>
     *     <li>Check for the {@code -owd} command line argument to override the work directory with the configuration directory or current directory.</li>
     *     <li>Check the {@code app.work} system property.</li>
     *     <li>Check for a {@code wrk} directory under {@code app.home}.</li>
     *     <li>Default to the current working directory.</li>
     * </ol>
     * The resulting path is normalized and set in the {@code app.work} system property.</p>
     */
    protected void determineWorkDirectory() {
        File result = null;

        // if the override work directory command line argument is present, set
        // the app.work system property to the same directory of the configuration
        // file or the current working directory if it does not exist as a file.
        if (commandLineArguments != null) {
            for (int x = 0; x < commandLineArguments.length; x++) {
                if (OVERRIDE_WORK_DIR_ARG.equalsIgnoreCase(commandLineArguments[x])) {
                    String path = getConfigDir();
                    if (path == null) {
                        path = System.getProperty("user.dir");
                    }
                    Log.debug("Overriding APP.WORK of '" + System.getProperties().getProperty(APP_WORK) + "' with '" + path + "'");
                    System.setProperty(APP_WORK, path);
                    break;
                }
            }
        }

        // First check for the app.work system property
        String path = System.getProperties().getProperty(APP_WORK);

        if (StringUtil.isNotBlank(path)) {
            Log.debug("Initializing APP.WORK directory '" + path + "'");
            String workDir = FileUtil.normalizePath(path);
            File workingDir = new File(workDir);
            if (workingDir.exists()) {
                Log.debug("APP.WORK directory '" + path + "' already exists");
                if (workingDir.isDirectory()) {
                    if (workingDir.canWrite()) {
                        result = workingDir;
                    } else {
                        Log.warn("The app.work property specified an un-writable (permissions) directory: " + workDir);
                    }
                } else {
                    Log.warn("The app.work property does not specify a directory: " + workDir);
                }
            } else {
                Log.debug("Creating APP.WORK directory '" + path + "'");
                try {
                    FileUtil.makeDirectory(workingDir);
                    result = workingDir;
                } catch (IOException e) {
                    Log.error("Could not create working directory specified in app.work property: " + workDir + " - " + e.getMessage());
                }
            }
        } else if (StringUtil.isNotBlank(getAppHome())) {
            // No app.work defined, so try to locate "app.home" and use the 'wrk' directory under there
            File appHome = new File(getAppHome());
            if (appHome.exists()) {
                File workingDir = new File(appHome, WORK_DIR_NAME);
                if (!workingDir.exists()) {
                    try {
                        FileUtil.makeDirectory(workingDir);
                    } catch (IOException e) {
                        Log.warn("Problems creating the work directory: " + workingDir + " - " + e.getMessage());
                    }
                }
                result = workingDir;
            }

        } else {
            result = new File(System.getProperty("user.dir"));
        }

        // If we have a result,
        if (result != null) {
            // set it as our working directory
            System.setProperty(APP_WORK, result.getAbsolutePath());
        } else {
            // else just use the current working directory
            System.setProperty(APP_WORK, DEFAULT_HOME);
            Log.debug("No usable configuration directory found, using current working directory");
        }

        // Remove all the relations and extra slashes from the home path
        System.setProperty(APP_WORK, FileUtil.normalizePath(System.getProperty(APP_WORK)));
        Log.debug(String.format("Job work directory set to %s", System.getProperty(APP_WORK)));
    }


    /**
     * Ensure the job has a name.
     *
     * <p>If no name is set in the configuration, it attempts to use the name of the configuration
     * file from the command line arguments as the job name.</p>
     */
    private void determineName() {
        if (StringUtil.isBlank(configuration.getName()) && commandLineArguments != null) {
            String cfgName = null;
            // use the first non-delimited argument as the config location, others are considered arguments to the BootStrap loader
            for (int x = 0; x < commandLineArguments.length; x++) {
                if (!commandLineArguments[x].startsWith("-") && cfgName == null) {
                    cfgName = commandLineArguments[x];
                    break;
                }
            }
            configuration.setName(cfgName);
        }
    }


    /**
     * Retrieve the directory containing the configuration file.
     *
     * <p>This is determined by parsing the {@code CONFIG_URI} system property.
     * If it is a file URI, the parent directory is returned if it exists and is writable.</p>
     *
     * @return The absolute path to the configuration directory, or {@code null} if it cannot be determined.
     */
    private String getConfigDir() {
        String retval = null;
        URI cfgUri = UriUtil.parse(System.getProperty(ConfigTag.CONFIG_URI));
        if (UriUtil.isFile(cfgUri)) {
            File cfgFile = UriUtil.getFile(cfgUri);
            if (cfgFile != null) {
                File workingDir = new File(cfgFile.getParent());
                if (workingDir.exists() && workingDir.isDirectory() && workingDir.canWrite()) {
                    retval = workingDir.getAbsolutePath();
                }
            }
        }
        return retval;
    }


}
