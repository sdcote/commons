/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote;

import coyote.commons.ExceptionUtil;
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * This is a Snap job that handles Read-Transform-Write tasks.
 *
 * <p>Using the coyote.commons.rtw package, this job uses a set of common classes to perform basic data processing.</p>
 */
public class RtwJob extends AbstractSnapJob {

    TransformEngine engine = null;
    boolean repeat = false;

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

            // calculate and normalize the appropriate value for "app.home"
            //determineHomeDirectory();

            //determineWorkDirectory();

            List<Config> jobs = cfg.getSections(ConfigTag.JOB);

            if (jobs.size() > 0) {

                Config job = jobs.get(0);

                // If the job has no name...
                if (StringUtil.isBlank(job.getName())) {
                    if (StringUtil.isNotBlank(cfg.getName())) {
                        //...set it to the name of the parent...
                        job.setName(cfg.getName());
                    } else {
                        //...or the base of the configuration URI
                        String cfguri = System.getProperty(ConfigTag.CONFIG_URI);
                        if (StringUtil.isNotBlank(cfguri)) {
                            try {
                                job.setName(UriUtil.getBase(new URI(cfguri)));
                            } catch (URISyntaxException ignore) {
                                // well, we tried, it will probably get assigned a UUID later
                            }
                        }
                    }
                }

                // have the Engine Factory create a transformation engine based on the
                // configuration
                engine = TransformEngineFactory.getInstance(job);


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
            } else {
                Log.fatal("Job.no_job_section");
            }
        } catch (Throwable e) {
            System.err.println(ExceptionUtil.stackTrace(e));
            throw e;
        }
    }


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

}
