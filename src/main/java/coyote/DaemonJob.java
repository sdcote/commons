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
 * DaemonJob class that extends AbstractSnapJob.
 * It operates by being called from the BootStrap based on a configuration file.
 * The configuration contains a list of jobs and logging settings.
 */
public class DaemonJob extends AbstractSnapJob {

    /**
     * Scheduler used for job scheduling and execution.
     */
    private final Scheduler scheduler = new Scheduler();
    /**
     * HTTP server used for remote monitoring and control.
     */
    private HTTPDRouter server = null;

    /**
     * Configure the job with the provided configuration.
     *
     * @param cfg The configuration for the job.
     * @throws ConfigurationException If there is a problem with the configuration.
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
     * Configure the jobs with the provided configuration.
     *
     * @param cfg The configuration for the jobs.
     * @throws ConfigurationException If there is a problem with the configuration.
     */
    private void configureJobs(Config cfg) throws ConfigurationException {
        try {
            for (Config jobSection : cfg.getSections(ConfigTag.JOB)) {
                if (jobSection.isArray()) {
                    // This is an array of job objects
                    for (DataField field : jobSection.getFields()) {
                        if (field.isFrame()) {
                            Config jobCfg = new Config((DataFrame) field.getObjectValue());
                            loadJob(jobCfg);
                        } else {
                            Log.debug("Skipping non-frame job configuration: " + field);
                            continue;
                        }
                    }
                } else {
                    // This looks like a single Job object
                    loadJob(jobSection);
                }
            }
        } catch (Exception e) {
            Log.error(String.format("Could not configure jobs: %s", e.getMessage()));
            throw new ConfigurationException("Job scheduling failed", e);
        }
    }

    CronEntry parseSchedule(Config scheduleCfg) throws ConfigurationException {
        CronEntry cronentry = new CronEntry();

        // go through each in order, this allows the user to determine how
        // attributes are applied by processing them in order they appear and
        // overwriting previous attributes.
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

    private ScheduledJob loadJob(Config config) throws ConfigurationException {
        Log.debug("Loading job configuration: " + config.toString());
        ScheduledJob retval = null;

        // use the first attribute of the configuration as the classname.
        DataField configField = config.getField(0);

        // Get the "Schedule" config section. THis describes how often the Job is to be run
        List<Config> cfgs = config.getSections(ConfigTag.SCHEDULE);
        if (cfgs.size() > 0) {
            Config scheduleCfg = cfgs.get(0);

            if(scheduleCfg.containsIgnoreCase(ConfigTag.MILLIS)){
                // This is a standard scheduled job with an interval in milliseconds
                long millis = scheduleCfg.getLong(ConfigTag.MILLIS, 1000);
                retval = new ScheduledJob();
                retval.setExecutionInterval(millis);
            } else {
                // assume this is a CronEntry pattern
                CronJob cronJob = new CronJob();
                CronEntry cronentry = parseSchedule(scheduleCfg);
                cronJob.setCronEntry(cronentry);
                retval = cronJob;
            }
        } else {
            // If there is no Schedule section, Just set this job to repeat with an intervale of 1 second
            retval = new CronJob();
        }

        // Try to determine the class name for the job
        if (configField != null && StringUtil.isNotEmpty(configField.getName())) {
            String className = configField.getName();
            Config cfgFrame = new Config();
            if (configField.isFrame()) {
                cfgFrame = new Config((DataFrame) configField.getObjectValue());
            }

            // if the class is not fully qualified, try to look it up
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
                Class<?> clazz = Class.forName(className);
                Constructor<?> ctor = clazz.getConstructor();
                Object object = ctor.newInstance();

                if (object instanceof SnapJob) {
                    SnapJob snapJob = (SnapJob) object;
                    try {
                        snapJob.configure(cfgFrame);

                        // retval.setWork(snapJob);
                        retval.setDoWorkOnce(true);




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

        // Return the scheduled job we created to run the job in this configuration
        return retval;
    }


    /**
     * Configure the HTTP server with the provided configuration.
     *
     * @param cfg The configuration for the HTTP server.
     */
    private void configureServer(Config cfg) throws ConfigurationException {
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
            server.addRoute("/api/command", CommandResponder.class, this);
            server.addRoute("/api/status", StatusResponder.class, this);

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
     * Start the daemon job.
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
     * Shut everything down when the job is requested to stop.
     *
     * <p>This is used by a shutdown hook to ensure the HTTP server and the
     * scheduler is shut down correctly before the JVM exits.</p>
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

}
