/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote;

import coyote.commons.ClasspathUtil;
import coyote.commons.CronEntry;
import coyote.commons.ExceptionUtil;
import coyote.commons.StringUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.job.CronJob;
import coyote.commons.job.ScheduledJob;
import coyote.commons.job.Scheduler;
import coyote.commons.log.Log;
import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.auth.GenericAuthProvider;
import coyote.commons.network.http.responder.HTTPDRouter;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.daemonjob.CommandResponder;
import coyote.commons.rtw.daemonjob.StatusResponder;
import coyote.commons.snap.AbstractSnapJob;
import coyote.commons.snap.SnapJob;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;


/**
 * DaemonJob is a specialized job that manages multiple sub-jobs using a scheduler.
 *
 * <p>This job acts as a container for other jobs, allowing them to be
 * scheduled according to specific intervals or cron-like patterns. These jobs
 * are assumed to be {@code SnapJob} configurations. Any {@code SnapJob}
 * configuration can be place in this {@code DaemonJob} and with the addition
 * of a "Schedule" configuration, that job will be run repeatedly at regular
 * intervals.</p>
 *
 * <p>This class also provides an optional HTTP server for remote monitoring
 * and control of the scheduled jobs. THis allows the DaemonJob to run as a
 * background process while still providing the ability to monitor and control
 * the jobs remotely.</p>
 *
 * <p>The configuration for this job typically includes a "Server" section for
 * HTTP settings and a "Job" section defining the tasks to be run. The "Job"
 * section is either a single JSON object representing a SnapJob or an array of
 * SnapJob objects.</p>
 */
public class DaemonJob extends AbstractSnapJob {

    /**
     * The scheduler used for managing the execution of sub-jobs.
     */
    private final Scheduler scheduler = new Scheduler();

    /**
     * The HTTP server used for remote monitoring and control.
     * This may be null if no server configuration is provided.
     */
    private HTTPDRouter server = null;

    /**
     * Configures this DaemonJob instance using the provided configuration.
     *
     * <p>This method initializes the HTTP server if a "Server" section is present
     * and schedules any jobs defined in the "Job" sections.</p>
     *
     * @param cfg The configuration containing server and job definitions.
     * @throws ConfigurationException If there is an error in the configuration format or content.
     */
    @Override
    public void configure(Config cfg) throws ConfigurationException {
        super.configure(cfg);

        if (cfg.containsIgnoreCase(ConfigTag.SERVER)) {
            configureServer(cfg);
        }

        if (cfg.containsIgnoreCase(ConfigTag.JOB)) {
            configureJobs(cfg);
        }

    }


    /**
     * Parses the "Job" sections of the configuration and schedules them.
     *
     * <p>Job sections can be individual objects or arrays of job configurations.</p>
     *
     * @param cfg The main configuration containing the job sections.
     * @throws ConfigurationException If there is an error during job configuration or scheduling.
     */
    private void configureJobs(Config cfg) throws ConfigurationException {
        try {
            for (Config jobSection : cfg.getSections(ConfigTag.JOB)) {
                if (jobSection.isArray()) {
                    // If the job section is an array, iterate through and schedule each job frame
                    for (DataField field : jobSection.getFields()) {
                        if (field.isFrame()) {
                            Config jobCfg = new Config((DataFrame) field.getObjectValue());
                            scheduler.schedule(loadJob(jobCfg));
                        } else {
                            Log.debug("Skipping non-frame job configuration: " + field);
                            continue;
                        }
                    }
                } else {
                    // Otherwise, treat the section as a single job configuration
                    scheduler.schedule(loadJob(jobSection));
                }
            }
        } catch (Exception e) {
            Log.error(String.format("Could not configure jobs: %s", e.getMessage()));
            throw new ConfigurationException("Job scheduling failed", e);
        }
    }

    /**
     * Parses a schedule configuration into a CronEntry.
     *
     * <p>This supports both full cron patterns and individual time components (minutes, hours, etc.).
     * Attributes are processed in the order they appear, allowing later ones to override earlier ones.</p>
     *
     * @param scheduleCfg The configuration frame containing schedule attributes.
     * @return A CronEntry representing the parsed schedule.
     * @throws ConfigurationException If there is a problem parsing the configuration.
     */
    CronEntry parseSchedule(Config scheduleCfg) throws ConfigurationException {
        CronEntry cronentry = new CronEntry();

        for (DataField field : scheduleCfg.getFields()) {
            if (ConfigTag.PATTERN.equalsIgnoreCase(field.getName())) {
                try {
                    cronentry = CronEntry.parse(field.getStringValue());
                } catch (ParseException e) {
                    Log.error(String.format("Problems parsing Schedule pattern: %s", e.getMessage()));
                }
            } else if (ConfigTag.MINUTES.equalsIgnoreCase(field.getName())) {
                cronentry.setMinutePattern(field.getStringValue());
            } else if (ConfigTag.HOURS.equalsIgnoreCase(field.getName())) {
                cronentry.setHourPattern(field.getStringValue());
            } else if (ConfigTag.MONTHS.equalsIgnoreCase(field.getName())) {
                cronentry.setMonthPattern(field.getStringValue());
            } else if (ConfigTag.DAYS.equalsIgnoreCase(field.getName())) {
                cronentry.setDayPattern(field.getStringValue());
            } else if (ConfigTag.DAYS_OF_WEEK.equalsIgnoreCase(field.getName())) {
                cronentry.setDayOfWeekPattern(field.getStringValue());
            }
        }
        return cronentry;
    }

    /**
     * Loads and initializes a job based on its configuration.
     *
     * <p>This method determines the execution schedule (interval or cron) and
     * instantiates the job class, resolving it if it is not fully qualified.</p>
     *
     * @param config The configuration frame for the job.
     * @return A ScheduledJob instance ready to be added to the scheduler.
     * @throws ConfigurationException If the job class cannot be resolved or instantiated.
     */
    private ScheduledJob loadJob(Config config) throws ConfigurationException {
        Log.debug("Loading job configuration: " + config.toString());
        ScheduledJob retval = null;

        // There should be only one element in this configuration, the name of the element is the name of the Job class
        if( config.getElementCount() != 1 ) {
            throw new ConfigurationException("Job configuration must have exactly one element, the name of the Job class");
        }

        List<Config> sections = config.getSections();
        if(sections.isEmpty()) {
            throw new ConfigurationException("Job configuration must have a frame as the first element");
        }

        // Get the configuration for the job
        Config jobConfig = sections.get(0);
        // Get the "Schedule" config section. This describes how often the Job is to be run.
        List<Config> scheduleConfig = jobConfig.getSections(ConfigTag.SCHEDULE);





        if (scheduleConfig.size() > 0) {
            Config scheduleCfg = scheduleConfig.get(0);

            if (scheduleCfg.containsIgnoreCase(ConfigTag.MILLIS)) {
                // This is a standard scheduled job with an interval in milliseconds
                long millis = scheduleCfg.getLong(ConfigTag.MILLIS, 1000);
                retval = new ScheduledJob();
                retval.setExecutionInterval(millis);
            } else {
                // Assume this is a CronEntry pattern
                CronJob cronJob = new CronJob();
                CronEntry cronentry = parseSchedule(scheduleCfg);
                cronJob.setCronEntry(cronentry);
                retval = cronJob;
            }
        } else {
            // If there is no Schedule section, default to repeating every 1 minute
            retval = new CronJob();
        }



        // Use the first attribute of the configuration as the classname of the job class.
        DataField configField = config.getField(0);

        // Try to determine and instantiate the class name for the job
        if (configField != null && StringUtil.isNotEmpty(configField.getName())) {
            String className = configField.getName();
            Config cfgFrame = new Config();
            if (configField.isFrame()) {
                cfgFrame = new Config((DataFrame) configField.getObjectValue());
            }

            // If the class name is not fully qualified, attempt to resolve it via ClasspathUtil
            if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
                try {
                    Log.info("Resolving Job class: " + className);
                    List<String> names = ClasspathUtil.resolve(className);
                    if (names.isEmpty()) {
                        Log.error("Failed to resolve Job class: " + className);
                        throw new ConfigurationException("Failed to resolve Job class: " + className);
                    } else if (names.size() > 1) {
                        Log.error("Ambiguous Job class resolution for: " + className + ", found multiple matches: " + names);
                        throw new ConfigurationException("Ambiguous Job class resolution for: " + className + ", found multiple matches: " + names);
                    } else {
                        Log.info("Resolved Job class: " + className + " to: " + names.get(0));
                        className = names.get(0);
                    }

                } catch (Exception e) {
                    Log.error("Failed to resolve class ", e);
                }
            }

            try {
                // Instantiate the job class and wrap it in a SnapJobRunner
                Class<?> clazz = Class.forName(className);
                Constructor<?> ctor = clazz.getConstructor();
                Object object = ctor.newInstance();

                if (object instanceof SnapJob) {
                    SnapJob snapJob = (SnapJob) object;
                    try {
                        snapJob.configure(cfgFrame);
                        retval.setWork(new SnapJobRunner(snapJob));
                    } catch (ConfigurationException e) {
                        System.err.printf("Could not configure snap job %s - %s: %s%n", object.getClass().getName(), e.getClass().getSimpleName(), e.getMessage());
                        System.exit(6);
                    }
                } else {
                    System.err.printf("Class is not a job: %s%n", className);
                    System.exit(5);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                     | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                System.err.println("Instantiation Error: " + className + " was not found - " + e.getClass().getName() + ": " + e.getMessage());
                System.exit(4);
            }

        } else {
            System.err.println("Empty configuration.");
        }

        return retval;
    }


    /**
     * Initializes the HTTP server using the provided configuration.
     *
     * <p>This sets up the port, authentication, default routes, IP ACLs,
     * DoS protection, and SSL if configured.</p>
     *
     * @param cfg The configuration frame containing the "Server" section.
     * @throws ConfigurationException If there is an error during server initialization.
     */
    private void configureServer(Config cfg) throws ConfigurationException {
        try {
            DataFrame serverFrame = cfg.getAsFrame(cfg.getFieldIgnoreCase(ConfigTag.SERVER).getName());
            Config serverConfig = new Config(serverFrame);
            int port = 80; // Default HTTP port

            // Handle port configuration
            if (serverConfig.containsIgnoreCase(ConfigTag.PORT)) {
                try {
                    port = serverConfig.getInt(ConfigTag.PORT);
                } catch (Exception e) {
                    Log.error(String.format("Invalid port in server configuration: %s", e.getMessage()));
                }
            }

            server = new HTTPDRouter(port);

            // Configure Authentication
            if (serverConfig.containsIgnoreCase(GenericAuthProvider.AUTH_SECTION) || serverConfig.containsIgnoreCase(GenericAuthProvider.USER_SECTION)) {
                DataFrame authFrame = null;
                if (serverConfig.containsIgnoreCase(GenericAuthProvider.AUTH_SECTION)) {
                    authFrame = serverConfig.getAsFrame(serverConfig.getFieldIgnoreCase(GenericAuthProvider.AUTH_SECTION).getName());
                } else {
                    authFrame = serverFrame;
                }
                server.setAuthProvider(new GenericAuthProvider(new Config(authFrame)));
            }

            // Set up routes
            server.addDefaultRoutes();
            server.addRoute("/api/command", CommandResponder.class, this);
            server.addRoute("/api/status", StatusResponder.class, this);

            // Configure IP Access Control List
            if (serverConfig.containsIgnoreCase(ConfigTag.IPACL)) {
                server.configIpACL(new Config(serverConfig.getAsFrame(serverConfig.getFieldIgnoreCase(ConfigTag.IPACL).getName())));
            }

            // Configure Denial of Service protection
            if (serverConfig.containsIgnoreCase(ConfigTag.FREQUENCY)) {
                server.configDosTables(new Config(serverConfig.getAsFrame(serverConfig.getFieldIgnoreCase(ConfigTag.FREQUENCY).getName())));
            }

            // Configure SSL/TLS
            if (serverConfig.containsIgnoreCase(ConfigTag.SECURE)) {
                try {
                    DataFrame secureFrame = serverConfig.getAsFrame(serverConfig.getFieldIgnoreCase(ConfigTag.SECURE).getName());
                    Config secureConfig = new Config(secureFrame);
                    String keystorePath = secureConfig.getString(ConfigTag.FILE);
                    String password = secureConfig.getString(ConfigTag.PASSWORD);
                    if (StringUtil.isNotBlank(keystorePath) && StringUtil.isNotBlank(password)) {
                        server.makeSecure(HTTPD.makeSSLSocketFactory(keystorePath, password.toCharArray()), null);
                    } else {
                        Log.error("Incomplete secure configuration: file and password are required.");
                    }
                } catch (Exception e) {
                    Log.error(String.format("Could not configure SSL: %s", e.getMessage()));
                }
            }
        } catch (Exception e) {
            Log.error(String.format("Could not configure HTTP Server: %s", e.getMessage()));
            throw new ConfigurationException("HTTP server configuration failed", e);
        }
    }


    /**
     * Starts the DaemonJob.
     *
     * <p>This method starts the HTTP server (if configured) and then runs the
     * scheduler. The scheduler will block the current thread until it is shut down.</p>
     */
    @Override
    public void start() {
        if (server != null && !server.isAlive()) {
            try {
                server.start(); // as a daemon thread
                Log.info(String.format("HTTP listener started on port %d", server.getListeningPort()));
            } catch (IOException e) {
                Log.error(String.format("Could not start HTTP listener: %s", e.getMessage()));
            }
        }

        // Run the scheduler in the current thread. This keeps the JVM open.
        scheduler.run();
        // If we are here, the scheduler has completed and is terminated. (i.e., it was shut down)
    }

    /**
     * Shuts down the DaemonJob and all its components.
     *
     * <p>This ensures that both the scheduler and the HTTP server are stopped
     * gracefully before the JVM exits.</p>
     */
    @Override
    public void stop() {
        scheduler.shutdown();
        scheduler.waitForInActive(3000);

        if (server != null && server.isAlive()) {
            server.stop();
            Log.info("HTTP listener stopped");
        }
    }

    /**
     * A wrapper for SnapJob instances to allow them to be executed by the scheduler.
     */
    private class SnapJobRunner implements Runnable {
        /**
         * The job to be executed.
         */
        SnapJob snapJob;

        /**
         * Creates a new SnapJobRunner for the specified job.
         *
         * @param snapJob The job to run.
         */
        public SnapJobRunner(SnapJob snapJob) { this.snapJob = snapJob; }

        /**
         * Executes the job.
         */
        @Override
        public void run() {
            try {
                snapJob.start();
            } catch (Exception e) {
                Log.error("The job threw an exception and terminated: " + e.getLocalizedMessage() + " - " + ExceptionUtil.stackTrace(e));
            }
        }
    }
}
