package coyote.commons.snap;

import coyote.BootStrap;
import coyote.commons.FileUtil;
import coyote.commons.Log;
import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.i13n.StatBoard;
import coyote.commons.i13n.StatBoardImpl;
import coyote.commons.log.Logger;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.context.OperationalContext;
import coyote.commons.template.SymbolTable;
import coyote.commons.template.Template;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class AbstractSnapJob implements SnapJob {

    /** The component responsible for tracking operational statistics for all the components in this runtime */
    protected final StatBoard stats = new StatBoardImpl();
    /** A symbol table to support basic template functions */
    protected final SymbolTable symbols = new SymbolTable();
    /** Our configuration */
    protected Config configuration = new Config();
    /** The command line arguments used to invoke the loader */
    protected String[] commandLineArguments = null;
    /** Logical identifier for this instance. May not be unique across the system. */
    protected String instanceName = null;
    /** */
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
     * This returns the value of app.home from either the environment variables
     * or the system properties, with the system properties overriding the
     * environment variables.
     *
     * @return The value of app.home from either the environment variables or
     * system properties or null if neither are defined.
     */
    protected static String getAppHome() {
        return getVariable(BootStrap.APP_HOME);
    }


    /**
     * Returns the value from either the environment variables or the system
     * properties with the system properties taking precedence over environment
     * variables.
     *
     * @param variable the name of the variable to lookup
     * @return The value from either the environment variables or system
     * properties or null if neither are defined.
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
     *
     * @param cfg
     * @throws ConfigurationException
     */
    public void configure(Config cfg) throws ConfigurationException {
        if (cfg != null) configuration = cfg;
        else configuration = new Config(); // prevent null configurations

        // Fill the symbol table with runtime values
        symbols.readEnvironmentVariables();

        // System properties override environment variables
        symbols.readSystemProperties();

        // Replace all values in the configuration with symbols - runtime variables
        configuration = new Config(Template.preProcess(configuration.toString(), symbols));

        // setup logging as soon as we can
        initLogging();
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
     * Load loggers for the entire runtime.
     *
     * <p>This looks for a section named logging in the main configuration and loads the
     * loggers from there.</p>
     */
    private void initLogging() {
        List<Config> loggers = configuration.getSections(ConfigTag.LOGGING);

        // There is a logger section, remove all the existing loggers and start
        // from scratch so we don't wind up with duplicate messages. Even if it is
        // empty, assume the configuration contains the exact state of logging
        // desired.
        if (loggers.size() > 0) {
            coyote.commons.log.Log.removeAllLoggers();
        }

        // for each of the logger sections
        for (Config cfg : loggers) {

            // Find the individual loggers
            for (DataField field : cfg.getFields()) {

                // each logger is a frame
                if (field.isFrame()) {

                    DataFrame cfgFrame = (DataFrame) field.getObjectValue();
                    // we need named sections, not arrays
                    if (StringUtil.isNotBlank(field.getName())) {

                        // start building the configuration for logger
                        Config loggerConfiguration = new Config();

                        // use the name of the section as the class name
                        String className = field.getName();

                        // Make sure the class is fully qualified
                        if (StringUtil.countOccurrencesOf(className, ".") < 1) {
                            className = LOGGER_PKG + "." + className;
                        }

                        // put the name of the class in the logger configuration
                        loggerConfiguration.put(ConfigTag.CLASS, className);

                        // add each of the fields in the config frame to the logger config
                        for (DataField lfield : cfgFrame.getFields()) {

                            // handle the target...make sure it is relative to ????
                            if (ConfigTag.TARGET.equalsIgnoreCase(lfield.getName())) {
                                String cval = lfield.getStringValue();
                                if (StringUtil.isNotEmpty(cval)) {
                                    cval = Template.preProcess(cval, symbols);
                                }

                                // the targets for loggers MUST be a URI
                                if (!("stdout".equalsIgnoreCase(cval) || "stderr".equalsIgnoreCase(cval))) {
                                    URI testTarget = UriUtil.parse(cval);

                                    if (testTarget != null) {
                                        if (testTarget.getScheme() == null) {
                                            cval = "file://" + cval;
                                        }
                                    } else {
                                        File file = new File(cval);
                                        URI fileUri = FileUtil.getFileURI(file);
                                        if (fileUri != null) {
                                            cval = fileUri.toString();
                                        }
                                    }

                                    URI testUri = UriUtil.parse(cval);
                                    if (testUri != null) {
                                        if (UriUtil.isFile(testUri)) {
                                            File logfile = UriUtil.getFile(testUri);

                                            // make it absolute to our job directory
                                            if (!logfile.isAbsolute()) {

                                                // as a loader, we use app.home as our home directory
                                                // and therefore logging should be in a "log" directory
                                                // off of app.home
                                                String path = getAppHome();

                                                // If no app.home, try the directory the configuration file is in
                                                if (StringUtil.isBlank(path)) {

                                                    String cfgUri = System.getProperty(ConfigTag.CONFIG_URI);
                                                    if (StringUtil.isNotBlank(cfgUri) && cfgUri.startsWith("file:")) {
                                                        try {
                                                            File cfgFile = UriUtil.getFile(new URI(cfgUri));
                                                            if (cfgFile != null && cfgFile.exists()) {
                                                                path = cfgFile.getParent();
                                                            } else {
                                                                // should not happen
                                                                path = System.getProperties().getProperty("user.dir").concat("/snap/log");
                                                            }
                                                        } catch (URISyntaxException e) {
                                                            // should not happen
                                                            path = System.getProperties().getProperty("user.dir").concat("/snap/log");
                                                        }
                                                    } else {
                                                        // probably a configuration file read across the network
                                                        path = System.getProperties().getProperty("user.dir").concat("/snap/log");
                                                    }
                                                } else {
                                                    path = path.concat("/log");
                                                }

                                                File logdir = new File(path);
                                                logfile = new File(logdir, logfile.getPath());
                                                testUri = FileUtil.getFileURI(logfile);
                                                cval = testUri.toString();
                                            }
                                        }

                                    } else {
                                        System.out.println("Bad target URI '" + cval + "'");
                                        System.exit(11);
                                    }
                                }

                                // set the validated URI in the target field
                                loggerConfiguration.put(ConfigTag.TARGET, cval);
                            } else if (ConfigTag.CATEGORIES.equalsIgnoreCase(lfield.getName())) {
                                // Categories should be normalized to upper case
                                String cval = lfield.getStringValue();
                                if (StringUtil.isNotEmpty(cval)) {
                                    cval = cval.toUpperCase();
                                    loggerConfiguration.put(ConfigTag.CATEGORIES, cval);
                                }
                            } else {
                                // pass the rest of the attributes unmolested
                                loggerConfiguration.add(lfield);
                            }
                        }

                        // create the logger
                        Logger logger = createLogger(loggerConfiguration);
                        String name = null;
                        if (logger != null) {
                            // Get the name of the logger
                            name = loggerConfiguration.getString(ConfigTag.NAME);

                            // If there is no name, try looking for an ID
                            if (StringUtil.isBlank(name)) {
                                name = loggerConfiguration.getString(ConfigTag.ID);
                            }

                            //If no name or ID, assign it a name
                            if (StringUtil.isBlank(name)) {
                                name = UUID.randomUUID().toString();
                            }

                            try {
                                coyote.commons.log.Log.addLogger(name, logger);
                            } catch (Exception e) {
                                System.out.println(String.format( "Could not add configured logger", name, logger.getClass(), e.getMessage()));
                                System.exit(11);
                            }
                        } else {
                            System.err.println(String.format( "could_not_create_an_instance_of_the_specified_logger",name));
                            System.exit(11);
                        }

                    } else {
                        System.err.println(String.format( "no_logger_classname", cfgFrame.toString()));
                        System.exit(11);
                    }
                } else {
                    System.err.println(String.format( "invalid_logger_configuration_section"));
                    System.exit(11);
                } // must be a frame/section

            } // for each logger

        } // for each logger section

        coyote.commons.log.Log.debug(String.format( "Logging initiated %tF %<tT.%<tL", new Date()));
    }



    private static Logger createLogger(Config cfg) {
        Logger retval = null;
        if (cfg != null) {
            if (cfg.contains(ConfigTag.CLASS)) {
                String className = cfg.getAsString(ConfigTag.CLASS);

                try {
                    Class<?> clazz = Class.forName(className);
                    Constructor<?> ctor = clazz.getConstructor();
                    Object object = ctor.newInstance();

                    if (object instanceof Logger) {
                        retval = (Logger) object;
                        try {
                            retval.setConfig(cfg);
                        } catch (Exception e) {
                            coyote.commons.log.Log.error(String.format( "could_not_configure_logger", object.getClass().getName(), e.getClass().getSimpleName(), e.getMessage()));
                        }
                    } else {
                        coyote.commons.log.Log.warn(String.format( "instance_is_not_a_logger", className));
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException |
                         InvocationTargetException e) {
                    coyote.commons.log.Log.error(String.format( "logger_instantiation_error", className, e.getClass().getName(), e.getMessage()));
                }
            } else {
                coyote.commons.log.Log.error(String.format( "logger_configuration_did_not_contain_a_classname"));
            }
        }

        return retval;
    }

}
