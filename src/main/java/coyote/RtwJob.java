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
 * This is a Snap job that handles Read-Transform-Write tasks.
 *
 * <p>Using the coyote.commons.rtw package, this job uses a set of common classes to perform basic data processing.</p>
 */
public class RtwJob extends AbstractSnapJob {

    /**
     * If there is no specified directory in the APP_HOME system property, just use the current working directory
     */
    public static final String DEFAULT_HOME = System.getProperty("user.dir");
    private static final String OVERRIDE_WORK_DIR_ARG = "-owd";
    private static final String WORK_DIR_NAME = "wrk";
    private static final String LOG_DIR_NAME = "log";
    TransformEngine engine = null;
    boolean repeat = false;

    public RtwJob() {
        Log.setMask(Log.ALL_EVENTS);
    }

    /**
     *
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
            for (int x = 0; x < commandLineArguments.length; x++) {
                engine.getSymbolTable().put(Symbols.COMMAND_LINE_ARG_PREFIX + x, commandLineArguments[x]);
            }

            // store environment variables in the symbol table
            Map<String, String> env = System.getenv();
            for (String envName : env.keySet()) {
                engine.getSymbolTable().put(Symbols.ENVIRONMENT_VAR_PREFIX + envName, env.get(envName));
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
     * Ensure we have a name either inn the configuration or the name of the configuration file from the command line.
     */
    private void determineName() {
        if (StringUtil.isBlank(configuration.getName())) {
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
     *
     */
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
     * Shut everything down when the JRE terminates.
     *
     * <p>There is a shutdown hook registered with the JRE when this Job is
     * loaded. The shutdown hook will call this method when the JRE is
     * terminating so that the Job can terminate any long-running processes.</p>
     *
     * <p>Note: this is different from {@code close()} but {@code shutdown()}
     * will normally result in {@code close()} being invoked at some point.</p>
     */

    public void stop() {
        engine.shutdown();
    }


    /**
     * Determine the value of the "app.home" system property.
     *
     * <p>If the app home property is already set, it is preserved, if not
     * normalized. If there is no value, this attempts to determine the location
     * of the configuration file used to configure this job and if found, uses
     * that directory as the home directory of all transformation operations. The
     * reasoning is that all artifacts should be kept together. Also, it is
     * probable that the DX job will be called from a central location while
     * each DX job will live is its own project directory.</p>
     *
     * <p>The most common use case is for the DX job to be called from a
     * scheduler (e.g. cron) with an absolute path to a configuration file.
     * Another very probable use case is the DX job being called from a
     * project directory with one configuration file per directory.</p>
     *
     * <p>It is possible that multiple files with different configurations will
     * exist in one directory.</p>
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
        Log.debug(String.format("Job.home_dir_set %s", System.getProperty(BootStrap.APP_HOME)));
    }


    /**
     * Look for "-owd" and just use the current directory
     * Look in system properties for "app.work"
     * Look for app.home and user the 'wrk' directory under there
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
        Log.debug(String.format("Job.work_dir_set", System.getProperty(APP_WORK)));
    }


    /**
     * @return
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
