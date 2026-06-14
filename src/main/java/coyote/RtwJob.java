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

    private static final String WORK_DIR_NAME = "wrk";
    public static final String LOG_DIR_NAME = "log";

    /**
     * The transformation engine that performs the actual data processing.
     */
    TransformEngine engine = null;

    /**
     * Flag indicating whether the job should repeat its execution indefinitely.
     */
    boolean repeat = false;


    @Override
    protected void doConfigure() throws ConfigurationException {
        super.doConfigure();
        try {
            repeat = configuration.getBoolean(ConfigTag.REPEAT);
        } catch (NumberFormatException ignore) {
        }
        determineName();
        TransformEngineFactory.setSymbols(symbols);
        engine = TransformEngineFactory.getInstance(getConfig());
        if (StringUtil.isBlank(engine.getName())) {
            Log.trace("Job.unnamed_engine_configured");
        } else {
            Log.trace("Job.engine_configured: " + engine.getName());
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
        Log.trace(String.format("Starting %s",this.getClass().getSimpleName()));

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
            Log.fatal("No engine configured.");
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
}
