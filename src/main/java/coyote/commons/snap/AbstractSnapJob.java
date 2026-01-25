package coyote.commons.snap;

import coyote.commons.Log;
import coyote.commons.StringUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.i13n.StatBoard;
import coyote.commons.i13n.StatBoardImpl;
import coyote.commons.rtw.OperationalContext;
import coyote.commons.template.SymbolTable;
import coyote.commons.template.Template;

public abstract class AbstractSnapJob implements SnapJob {

    /**
     * Our configuration
     */
    protected Config configuration = new Config();

    /**
     * The command line arguments used to invoke the loader
     */
    protected String[] commandLineArguments = null;

    /**
     * The component responsible for tracking operational statistics for all the
     * components in this runtime
     */
    protected final StatBoard stats = new StatBoardImpl();

    /**
     * Logical identifier for this instance. May not be unique across the system.
     */
    protected String instanceName = null;

    /**
     * A symbol table to support basic template functions
     */
    protected final SymbolTable symbols = new SymbolTable();

    /**
     * 
     */
    private OperationalContext context = new OperationalContext();

    
    /**
     * Add a shutdown hook into the JVM to help us shut everything down nicely.
     *
     * @param loader The loader to terminate
     */
    protected static void registerShutdownHook(final SnapJob job) {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread("LoaderHook") {
                public void run() {
                    Log.debug("Runtime_terminating");
                    if (job != null) {
                        job.stop();
                    }
                    Log.debug("Runtime_terminated");
                }
            });
        } catch (Exception e) {
            // Ignore - should not happen
        }
    }


    /**
     * 
     * @param cfg
     * @throws ConfigurationException
     */
    public void configure(Config cfg) throws ConfigurationException {
        configuration = cfg;

        // Fill the symbol table with runtime values
        symbols.readEnvironmentVariables();

        // System properties override environment variables
        symbols.readSystemProperties();

        // Replace all values in the configuration with symbols - runtime variables
        configuration = new Config(Template.preProcess(configuration.toString(), symbols));

    }


    /**
     * This returns the value of app.home from either the environment variables
     * or the system properties, with the system properties overriding the
     * environment variables.
     *
     * @return The value of app.home from either the environment variables or
     *         system properties or null if neither are defined.
     */
    protected static String getAppHome() {
        return getVariable(APP_HOME);
    }


    /**
     * @return the command line arguments used to invoke this loader
     */
    public String[] getCommandLineArguments() {
        return commandLineArguments;
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


    /**
     * @param args the command line arguments to set
     */
    public void setCommandLineArguments(String[] args) {
        commandLineArguments = args;
    }


    /**
     * 
     */
    public Config getConfig() {
        if (configuration == null) {
            configuration = new Config();
        }
        return configuration;
    }


    /**
     * 
     */
    public OperationalContext getContext() {
        return context;
    }


    /**
     * Access instrumentation services for this loader.
     *
     * <p>
     * This enables tracking operational statistics for all components in the
     * runtime.
     *
     * <p>
     * Statistics tracking is disabled by default but can be toggled antime.
     *
     * @return the StatBoard for this server.
     */
    public StatBoard getStats() {
        return stats;
    }



    /**
     * Returns the value from either the environment variables or the system
     * properties with the system properties taking precedence over environment
     * variables.
     *
     * @param variable the name of the variable to lookup
     * @return The value from either the environment variables or system
     *         properties or null if neither are defined.
     */
    private static String getVariable(String variable) {
        String retval = System.getenv().get(variable);
        if (StringUtil.isNotBlank(System.getProperties().getProperty(variable))) {
            retval = System.getProperties().getProperty(variable);
        }
        return retval;
    }


    /**
     * Return the value in either the named environment variable or system
     * property, returning the given default value if neither are found.
     *
     * <p>
     * The system property takes precedence over the environment variable.
     * </p>
     *
     * @param tag          the name of the environment variable or system property
     *                     to locate.
     * @param defaultValue The default value to return if neither ar found.
     *
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

}
