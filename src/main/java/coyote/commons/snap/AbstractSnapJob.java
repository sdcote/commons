package coyote.commons.snap;

import coyote.commons.CoyoteEnvironment;
import coyote.commons.FileUtil;
import coyote.commons.Log;
import coyote.commons.StringUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.i13n.StatBoard;
import coyote.commons.i13n.StatBoardImpl;
import coyote.commons.rtw.Symbols;
import coyote.commons.rtw.context.OperationalContext;
import coyote.commons.template.SymbolTable;

import java.io.File;
import java.io.IOException;

/**
 * Base implementation of a SnapJob providing common functionality.
 *
 * <p>This class handles:
 * <ul>
 *   <li>Configuration management and preprocessing with symbols</li>
 *   <li>Logging initialization and configuration</li>
 *   <li>Symbol table management for template-based configurations</li>
 *   <li>Operational statistics tracking</li>
 *   <li>Command line argument storage</li>
 *   <li>Operational context management</li>
 * </ul>
 */
public abstract class AbstractSnapJob implements SnapJob {

    /** The component responsible for tracking operational statistics for all the components in this runtime */
    protected final StatBoard stats = new StatBoardImpl();
    /** A symbol table to support basic template functions */
    protected final SymbolTable symbols = new SymbolTable();
    /** Our configuration */
    public static final String OVERRIDE_WORK_DIR_ARG = "-owd";
    protected static final String DEFAULT_HOME = System.getProperty("user.dir");

    protected Config configuration = new Config();
    /** The command line arguments used to invoke the loader */
    protected String[] commandLineArguments = null;
    /** Logical identifier for this instance. May not be unique across the system. */
    protected String instanceName = null;
    /** The operational context for this job, providing a shared space for job components to communicate. */
    private final OperationalContext context = new OperationalContext();
    /** Constant to assist in determining the full class name of loggers */
    private static final String LOGGER_PKG = coyote.commons.log.Log.class.getPackage().getName();


    /**
     * Add a shutdown hook into the JVM to help us shut everything down nicely.
     *
     * @param job The job to terminate
     */
    protected static void registerShutdownHook(final SnapJob job) {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread("JobHook") {
                public void run() {
                    Log.debug("Runtime terminating");
                    if (job != null) {
                        job.stop();
                    }
                    Log.debug("Runtime terminated");
                }
            });
        } catch (Exception e) {
            // Ignore - should not happen
        }
    }


    /**
     * This returns the value of app.home from either the environment variables
     * or the system properties, with the system properties overriding the
     * environment variables.
     *
     * @return The value of app.home from either the environment variables or
     * system properties or null if neither are defined.
     */
    protected static String getAppHome() {
        return CoyoteEnvironment.getHomeDirectory();
    }




    /**
     * Return the value in either the named environment variable or system
     * property, returning the given default value if neither are found.
     *
     * <p>The system property takes precedence over the environment variable.</p>
     *
     * @param tag          the name of the environment variable or system property
     *                     to locate.
     * @param defaultValue The default value to return if neither ar found.
     * @return The value found, or the default value.
     */
    public static String getEnvironmentOrProperty(String tag, String defaultValue) {
        String retval = defaultValue;
        String envVar = System.getenv(tag);
        String sysProp = System.getProperty(tag);
        if (StringUtil.isNotBlank(sysProp)) {
            retval = sysProp;
        } else if (StringUtil.isNotBlank(envVar)) {
            retval = envVar;
        }
        return retval;
    }



    /**
     * Determine the value of the "app.home" system property.
     *
     * <p>If the app home property is already set, it is preserved and normalized.
     * If there is no value, this attempts to determine the location
     * of the configuration file used to configure this job (via command-line arguments)
     * and if found, uses that directory as the home directory of all operations.
     * The reasoning is that all artifacts should be kept together.</p>
     *
     * <p>If it cannot be determined from arguments, it defaults to the current working directory.</p>
     */
    protected void determineHomeDirectory() {
        if (System.getProperty(CoyoteEnvironment.APP_HOME) == null) {
            if (getCommandLineArguments() != null && getCommandLineArguments().length > 0) {
                File cfgFile = new File(getCommandLineArguments()[0]);
                if (cfgFile.exists()) {
                    if (cfgFile.getParentFile() != null) {
                        System.setProperty(CoyoteEnvironment.APP_HOME, cfgFile.getParentFile().getAbsolutePath());
                    } else {
                        System.setProperty(CoyoteEnvironment.APP_HOME, DEFAULT_HOME);
                    }
                } else {
                    System.setProperty(CoyoteEnvironment.APP_HOME, DEFAULT_HOME);
                }
            } else {
                System.setProperty(CoyoteEnvironment.APP_HOME, DEFAULT_HOME);
            }
        } else {
            if (System.getProperty(CoyoteEnvironment.APP_HOME).trim().equals(".")) {
                System.setProperty(CoyoteEnvironment.APP_HOME, DEFAULT_HOME);
            } else if (StringUtil.isBlank(System.getProperty(CoyoteEnvironment.APP_HOME))) {
                System.setProperty(CoyoteEnvironment.APP_HOME, DEFAULT_HOME);
            }
        }
        System.setProperty(CoyoteEnvironment.APP_HOME, FileUtil.normalizePath(System.getProperty(CoyoteEnvironment.APP_HOME)));
        Log.debug(String.format("Job home directory set to %s", System.getProperty(CoyoteEnvironment.APP_HOME)));
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
        if (commandLineArguments != null) {
            for (int x = 0; x < commandLineArguments.length; x++) {
                if (OVERRIDE_WORK_DIR_ARG.equalsIgnoreCase(commandLineArguments[x])) {
                    String path = getConfigDir();
                    if (path == null) {
                        path = System.getProperty("user.dir");
                    }
                    Log.debug("Overriding APP.WORK of '" + System.getProperties().getProperty(CoyoteEnvironment.APP_WORK) + "' with '" + path + "'");
                    System.setProperty(CoyoteEnvironment.APP_WORK, path);
                    break;
                }
            }
        }
        String path = System.getProperties().getProperty(CoyoteEnvironment.APP_WORK);
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
                    Log.error("Could not create APP.WORK directory '" + path + "' - " + e.getMessage());
                }
            }
        }
        if (result == null) {
            String home = System.getProperty(CoyoteEnvironment.APP_HOME);
            if (StringUtil.isNotBlank(home)) {
                File workingDir = new File(home, "wrk");
                if (!workingDir.exists()) {
                    try {
                        FileUtil.makeDirectory(workingDir);
                        result = workingDir;
                    } catch (IOException e) {
                        Log.error("Could not create APP.WORK directory in APP.HOME '" + path + "' - " + e.getMessage());
                    }
                } else {
                    result = workingDir;
                }
            }
        }
        if (result == null) {
            result = new File(System.getProperty("user.dir"));
        }
        System.setProperty(CoyoteEnvironment.APP_WORK, result.getAbsolutePath());
        Log.debug("APP.WORK set to " + System.getProperty(CoyoteEnvironment.APP_WORK));
    }

    protected String getConfigDir() {
        String retval = null;
        if (commandLineArguments != null && commandLineArguments.length > 0) {
            File cfgFile = new File(commandLineArguments[0]);
            if (cfgFile.exists()) {
                retval = cfgFile.getParent();
            }
        }
        return retval;
    }

    /**
     * Configure the job with the provided configuration.
     *
     * <p>This method initializes the symbol table, pre-processes the configuration
     * using the symbol table (resolving variables), and then initializes logging
     * based on the processed configuration.</p>
     *
     * @param cfg The configuration for the job.
     * @throws ConfigurationException If there is a problem with the configuration or initialization.
     */
    public final void configure(Config cfg) throws ConfigurationException {
        if (cfg != null) configuration = cfg;
        else configuration = new Config();

        determineHomeDirectory();
        determineWorkDirectory();

        preConfigure();
        doConfigure();
        postConfigure();
    }

    protected void preConfigure() {
        symbols.readEnvironmentVariables();
        symbols.readSystemProperties();
        if (commandLineArguments != null) {
            for (int x = 0; x < commandLineArguments.length; x++) {
                symbols.put(Symbols.COMMAND_LINE_ARG_PREFIX + x, commandLineArguments[x]);
            }
        }
        symbols.put(Symbols.JOB_DIRECTORY, System.getProperty(CoyoteEnvironment.APP_HOME));
        symbols.put(Symbols.WORK_DIRECTORY, System.getProperty(CoyoteEnvironment.APP_WORK));
    }

    protected void doConfigure() throws ConfigurationException {
    }

    protected void postConfigure() {
    }


    /**
     * @return the command line arguments used to invoke this loader
     */
    public String[] getCommandLineArguments() {
        return commandLineArguments;
    }


    /**
     * @param args the command line arguments to set
     */
    public void setCommandLineArguments(String[] args) {
        commandLineArguments = args;
    }


    @Override
    public void run() {
        start();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    /**
     * Initialize the symbol table in the context with system properties and
     * other useful data.
     */
    public void initSymbolTable() {
        if (context != null) {
            if (context.getSymbols() != null) {
                // Fill the symbol table with system properties
                context.getSymbols().readSystemProperties();
            } else {
                Log.warn("no symbols in context to initialize");
            }
        } else {
            Log.warn("no context defined to init symbol table");
        }
    }



    @Override
    public String getName() {
        return instanceName;
    }


    @Override
    public void setName(String name) {
        instanceName = name;
    }


    /**
     * @return the configuration for this job.
     */
    public Config getConfig() {
        if (configuration == null) {
            configuration = new Config();
        }
        return configuration;
    }



    /**
     * @return the operational context for this job.
     */
    public OperationalContext getContext() {
        return context;
    }


    /**
     * Access instrumentation services for this loader.
     *
     * <p>This enables tracking operational statistics for all components in
     * the runtime.
     *
     * <p>Statistics tracking is disabled by default but can be toggled anytime.
     *
     * @return the StatBoard for this server.
     */
    public StatBoard getStats() {
        return stats;
    }






}
