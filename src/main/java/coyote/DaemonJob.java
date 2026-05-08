/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote;

import coyote.commons.CronEntry;
import coyote.commons.StringUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
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

/**
 * DaemonJob class that extends AbstractSnapJob.
 * It operates by being called from the BootStrap based on a configuration file.
 * The configuration contains a list of jobs and logging settings.
 */
public class DaemonJob extends AbstractSnapJob {

    /**
     * HTTP server used for remote monitoring and control.
     */
    private HTTPDRouter server = null;

    /**
     * Scheduler used for job scheduling and execution.
     */
    private final Scheduler scheduler = new Scheduler();


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
            for (Config jobCfg : cfg.getSections(ConfigTag.JOB)) {
                for (DataField field : jobCfg.getFields()) {
                    if (field.isFrame() && StringUtil.isNotEmpty(field.getName())
                            && !field.getName().equalsIgnoreCase(ConfigTag.NAME)
                            && !field.getName().equalsIgnoreCase(ConfigTag.DESCRIPTION)
                            && !field.getName().equalsIgnoreCase(ConfigTag.INTERVAL)
                            && !field.getName().equalsIgnoreCase(ConfigTag.SCHEDULE)
                            && !field.getName().equalsIgnoreCase(ConfigTag.REPEAT)) {

                        String className = field.getName();
                        Config taskCfg = new Config((DataFrame) field.getObjectValue());

                        // if the class is not fully qualified, assume the same namespace as the bootstrap loader.
                        if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
                            className = BootStrap.class.getPackage().getName() + "." + className;
                        }

                        try {
                            Class<?> clazz = Class.forName(className);
                            Constructor<?> ctor = clazz.getConstructor();
                            Object object = ctor.newInstance();

                            if (object instanceof SnapJob) {
                                final SnapJob job = (SnapJob) object;
                                job.setCommandLineArguments(getCommandLineArguments());
                                job.configure(taskCfg);

                                ScheduledJob scheduledJob = new ScheduledJob(new Runnable() {
                                    @Override
                                    public void run() {
                                        job.start();
                                    }
                                });

                                if (jobCfg.containsIgnoreCase(ConfigTag.NAME)) {
                                    scheduledJob.setName(jobCfg.getString(ConfigTag.NAME));
                                } else {
                                    scheduledJob.setName(className);
                                }

                                if (jobCfg.containsIgnoreCase(ConfigTag.DESCRIPTION)) {
                                    scheduledJob.setDescription(jobCfg.getString(ConfigTag.DESCRIPTION));
                                }

                                // If the SnapJob does not define a repeat pattern in its configuration,
                                // use the default pattern of "* * * * *"
                                String schedule = "* * * * *";
                                boolean hasSchedule = false;
                                if (jobCfg.containsIgnoreCase(ConfigTag.SCHEDULE)) {
                                    schedule = jobCfg.getString(ConfigTag.SCHEDULE);
                                    hasSchedule = true;
                                }

                                try {
                                    CronEntry cron = CronEntry.parse(schedule);
                                    scheduledJob.setExecutionTime(cron.getNextTime());
                                } catch (Exception e) {
                                    Log.error("Invalid cron schedule: " + e.getMessage());
                                }

                                if (jobCfg.containsIgnoreCase(ConfigTag.INTERVAL)) {
                                    scheduledJob.setExecutionInterval(jobCfg.getLong(ConfigTag.INTERVAL));
                                    scheduledJob.setRepeatable(true);
                                } else if (hasSchedule) {
                                    scheduledJob.setRepeatable(true);
                                }

                                if (jobCfg.containsIgnoreCase(ConfigTag.REPEAT)) {
                                    scheduledJob.setRepeatable(jobCfg.getAsBoolean(ConfigTag.REPEAT));
                                }

                                scheduler.schedule(scheduledJob);
                            } else {
                                Log.error(String.format("Class is not a job: %s", className));
                            }
                        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                                 | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            Log.error("Instantiation Error: " + className + " was not found - " + e.getClass().getName() + ": " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.error(String.format("Could not configure jobs: %s", e.getMessage()));
            throw new ConfigurationException("Job scheduling failed", e);
        }
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
     */
    @Override
    public void stop() {
        if (server != null && server.isAlive()) {
            server.stop();
            Log.info("HTTP listener stopped");
        }
    }

}
