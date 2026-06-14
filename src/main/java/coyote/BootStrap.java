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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import coyote.commons.CoyoteEnvironment;
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
import coyote.commons.dataframe.DataFrame;
import coyote.commons.job.ScheduledJob;
import coyote.commons.job.Scheduler;
import coyote.commons.log.Log;
import coyote.commons.network.MimeType;
import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.Status;
import coyote.commons.network.http.auth.GenericAuthProvider;
import coyote.commons.network.http.responder.ClassloadingResponder;
import coyote.commons.network.http.responder.HTTPDRouter;
import coyote.commons.network.http.responder.Resource;
import coyote.commons.network.http.responder.Responder;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.daemonjob.CommandResponder;
import coyote.commons.rtw.daemonjob.StatusResponder;
import coyote.commons.snap.AbstractSnapJob;
import coyote.commons.snap.JobLoader;
import coyote.commons.snap.SnapJob;

/**
 * This acts as a very generic loader of classes based on configuration files.
 * 
 * <p>The bootstrap loader takes an opinionated approach to running generic jobs, allowing the developer to focus on business logic
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
 * 
  * APP_HOME - specific directory specified as a system property or environment variable
 *  CFG_HOME - usually a subdirectory named "cfg" under the APP_HOME directory.
 */
public class BootStrap {

    static{
        Log.initConsoleLogging();
    }

    private static Config configuration = null;
    private static String cfgLoc = null;
    private static URI cfgUri = null;
    private static final Scheduler scheduler = new Scheduler();
    private static HTTPDRouter server = null;
    public static String APP_HOME = CoyoteEnvironment.APP_HOME;

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

        // Parse the command line arguments
        parseArgs(args);

        Log.info("Info logging is enabled");
        Log.debug("Debug logging is enabled");
        Log.trace("Trace logging is enabled");

        // confirm the configuration location is a valid URI
        confirmConfigurationLocation();

        // Read in the configuration
        readConfig();

        List<ScheduledJob> jobs = null;
        try {
            jobs = JobLoader.loadJobs(configuration);
        } catch (ConfigurationException e) {
            Log.fatal(String.format("Could not load/configure jobs: %s: %s", e.getClass().getSimpleName(), e.getMessage()));
            System.exit(6);
        }

        if (jobs == null || jobs.isEmpty()) {
            Log.fatal("No jobs were created.");
            System.exit(2);
        }

        // Configure server if needed
        if (configuration.containsIgnoreCase(ConfigTag.SERVER)) {
            try {
                configureServer(configuration);
            } catch (ConfigurationException e) {
                Log.error("Could not configure HTTP Server: " + e.getMessage());
            }
        }

        // Determine if we should use the scheduler or just run a single job
        boolean useScheduler = false;
        if (jobs.size() > 1 || server != null) {
            useScheduler = true;
        } else {
            for (ScheduledJob job : jobs) {
                if (job.isRepeatable()) {
                    useScheduler = true;
                    break;
                }
            }
        }

        if (useScheduler) {
            for (ScheduledJob job : jobs) {
                scheduler.schedule(job);
            }

            if (server != null) {
                try {
                    server.start();
                    Log.info(String.format("HTTP listener started on port %d", server.getListeningPort()));
                } catch (IOException e) {
                    Log.error(String.format("Could not start HTTP listener: %s", e.getMessage()));
                }
            }

            // Register a shutdown method to terminate cleanly when the JVM exit
            registerShutdownHook(null);

            scheduler.run();
        } else {
            // Run the single job in the current thread
            ScheduledJob scheduledJob = jobs.get(0);
            registerShutdownHook(scheduledJob);
            try {
                scheduledJob.run();
            } catch (Throwable e) {
                Log.fatal("The single job threw an exception and the loader is terminating: " + e.getMessage());
            }
        }

        System.exit(0);
    }

    /**
     * Start the bootstrap loader.
     */
    public void start() {
        if (server != null && !server.isAlive()) {
            try {
                server.start();
            } catch (IOException e) {
                Log.error("Could not start server: " + e.getMessage());
            }
        }
        if (!scheduler.isActive()) {
            scheduler.run();
        }
    }

    /**
     * Stop the bootstrap loader.
     */
    public void stop() {
        scheduler.shutdown();
        if (server != null) {
            server.stop();
        }
    }

    private static void configureServer(Config cfg) throws ConfigurationException {
        try {
            DataFrame serverFrame = cfg.getAsFrame(cfg.getFieldIgnoreCase(ConfigTag.SERVER).getName());
            Config serverConfig = new Config(serverFrame);
            int port = 80;

            if (serverConfig.containsIgnoreCase(ConfigTag.PORT)) {
                try {
                    port = serverConfig.getInt(ConfigTag.PORT);
                } catch (Exception e) {
                    Log.error(String.format("Invalid port in server configuration: %s", e.getMessage()));
                }
            }

            server = new HTTPDRouter(port);

            if (serverConfig.containsIgnoreCase(GenericAuthProvider.AUTH_SECTION) || serverConfig.containsIgnoreCase(GenericAuthProvider.USER_SECTION)) {
                DataFrame authFrame = null;
                if (serverConfig.containsIgnoreCase(GenericAuthProvider.AUTH_SECTION)) {
                    authFrame = serverConfig.getAsFrame(serverConfig.getFieldIgnoreCase(GenericAuthProvider.AUTH_SECTION).getName());
                } else {
                    authFrame = serverFrame;
                }
                server.setAuthProvider(new GenericAuthProvider(new Config(authFrame)));
            }

            server.addDefaultRoutes();
            server.addRoute("/", RedirectResponder.class, "daemonjob/index.html");
            server.addRoute("/api/command", CommandResponder.class, new BootStrap());
            server.addRoute("/api/status", StatusResponder.class, new BootStrap());
            server.addRoute("daemonjob/(.)+", ClassloadingResponder.class, "daemonjob");

            if (serverConfig.containsIgnoreCase(ConfigTag.IPACL)) {
                server.configIpACL(new Config(serverConfig.getAsFrame(serverConfig.getFieldIgnoreCase(ConfigTag.IPACL).getName())));
            }

            if (serverConfig.containsIgnoreCase(ConfigTag.FREQUENCY)) {
                server.configDosTables(new Config(serverConfig.getAsFrame(serverConfig.getFieldIgnoreCase(ConfigTag.FREQUENCY).getName())));
            }

            if (serverConfig.containsIgnoreCase(ConfigTag.SECURE)) {
                try {
                    DataFrame secureFrame = serverConfig.getAsFrame(serverConfig.getFieldIgnoreCase(ConfigTag.SECURE).getName());
                    Config secureConfig = new Config(secureFrame);
                    String keystorePath = secureConfig.getString(ConfigTag.FILE);
                    String password = secureConfig.getString(ConfigTag.PASSWORD);
                    if (StringUtil.isNotBlank(keystorePath) && StringUtil.isNotBlank(password)) {
                        server.makeSecure(HTTPD.makeSSLSocketFactory(keystorePath, password.toCharArray()), null);
                    }
                } catch (Exception e) {
                    Log.error(String.format("Could not configure SSL: %s", e.getMessage()));
                }
            }
        } catch (Exception e) {
            throw new ConfigurationException("HTTP server configuration failed", e);
        }
    }

    /**
     * A simple responder that redirects to another URI.
     */
    public static class RedirectResponder implements Responder {
        @Override
        public Response get(Resource resource, Map<String, String> urlParams, HTTPSession session) {
            String target = resource.initParameter(String.class);
            Response response = Response.createFixedLengthResponse(Status.REDIRECT, MimeType.HTML.getType(), "Redirecting to " + target);
            response.addHeader("Location", target);
            return response;
        }

        @Override
        public Response post(Resource resource, Map<String, String> urlParams, HTTPSession session) {
            return get(resource, urlParams, session);
        }

        @Override
        public Response put(Resource resource, Map<String, String> urlParams, HTTPSession session) {
            return get(resource, urlParams, session);
        }

        @Override
        public Response delete(Resource resource, Map<String, String> urlParams, HTTPSession session) {
            return get(resource, urlParams, session);
        }

        @Override
        public Response other(String method, Resource resource, Map<String, String> urlParams, HTTPSession session) {
            return get(resource, urlParams, session);
        }
    }


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
            Log.fatal("No configuration location specified.");
            System.exit(8);
        }

    }

    private static void registerShutdownHook(final ScheduledJob job) {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread("BootstrapHook") {
                public void run() {
                    Log.debug("Runtime terminating, stopping job.");
                    if (job != null) {
                        job.shutdown();
                    }
                    scheduler.shutdown();
                    if (server != null) {
                        server.stop();
                    }
                    Log.debug("Job stop completed.");
                }
            });
        } catch (Exception e) {
            // Ignore - should not happen
        }
    }

    protected static void confirmAppHome() {
        // see if there is an environment variable or a system property with a shared
        // configuration directory
        String path = CoyoteEnvironment.getHomeDirectory();

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
                        Log.warn("The app.home property specified an un-readable (permissions) directory: " + appDir);
                    }
                } else {
                    Log.warn("The app.home property does not specify a directory: " + appDir);
                }
            } else {
                Log.notice("The app.home property does not exist: " + appDir);
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
            String configUri = cfgUri.toString();
            System.setProperty(ConfigTag.CONFIG_URI, configUri);
            if (StringUtil.isNotBlank(configUri) && configUri.startsWith("file:")) {
                File cfgFile = UriUtil.getFile(cfgUri);
                if (cfgFile != null && cfgFile.exists()) {
                    System.setProperty(CONFIG_DIR, cfgFile.getParent());
                }
            }
        } catch (IOException | ConfigurationException e) {
            System.err.println(String.format("Error reading configuration %s - %s %s", cfgUri, e.getLocalizedMessage(), ExceptionUtil.stackTrace(e)));
            System.exit(7);
        }

    }

    /**
     * Confirm the configuration location
     */
    private static void confirmConfigurationLocation() {
        // start building error messages for user feedback
        StringBuffer errMsg = new StringBuffer("Confirming cfg location " + cfgLoc + StringUtil.CRLF);

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
                        errMsg.append(String.format("No local cfg file %s%n", localfile.getAbsolutePath()));

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
                                            // if an alternate was found in the local directory, use it
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
                                                    errMsg.append(String.format("No common configuration file: %s%s", cfgFile.getAbsolutePath(), StringUtil.CRLF));
                                                    errMsg.append(String.format("Configuration file not found: %s%s", cfgLoc, StringUtil.CRLF));
                                                    System.out.println(errMsg.toString());
                                                    System.exit(9);
                                                }
                                            }
                                        }
                                    } else {
                                        // the specified config directory was not a directory
                                        errMsg.append(String.format("Configuration directory is not a directory: %s%s", appDir, StringUtil.CRLF));
                                        System.out.println(errMsg.toString());
                                        System.exit(10);
                                    }
                                } else {
                                    // the specified config directory does not exist
                                    errMsg.append(String.format("Cfg dir '%s' does not exist %n", appDir));
                                    System.out.println(errMsg.toString());
                                    System.exit(11);
                                }
                            } else {
                                if (alternativeFile != null) {
                                    cfgUri = FileUtil.getFileURI(alternativeFile);
                                } else {
                                    // no shared config directory provided in system properties
                                    errMsg.append(String.format("Configuration directory not provided: %s%s", APP_HOME, StringUtil.CRLF));
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
                        errMsg.append(String.format("Configuration file not readable: %s%s", test.getAbsolutePath(), StringUtil.CRLF));
                        System.out.println(errMsg.toString());
                        System.exit(13);
                    }
                    Log.info(String.format("Reading configuration from file: %s", test.getAbsolutePath()));
                } else {
                    Log.info("Reading configuration from network");
                }
            } else {
                errMsg.append(String.format("Configuration file not found: %s%s", cfgLoc, StringUtil.CRLF));
                System.out.println(errMsg.toString());
                System.exit(9);

            }

        } else {
            System.err.println("No configuration URI defined");
            System.exit(1);
        }

    }

    /**
     * @return The value of app.home from either the environment variables or system properties or null if neither are defined.
     */
    protected static String getAppHome() {
        return CoyoteEnvironment.getHomeDirectory();
    }


}
