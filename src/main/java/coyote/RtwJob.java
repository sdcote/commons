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
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TransformEngine;
import coyote.commons.rtw.TransformEngineFactory;
import coyote.commons.snap.AbstractSnapJob;

import java.io.IOException;

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

        Config rtwConfig = getConfig();
        if (configuration.containsIgnoreCase(ConfigTag.CONFIGURATION)) {
            Config section = configuration.getSection(ConfigTag.CONFIGURATION);
            if (section != null) {
                rtwConfig = section;
            }
        }

        TransformEngineFactory.setSymbols(symbols);
        engine = TransformEngineFactory.getInstance(rtwConfig);

        // Propagate name if not set in engine config but present in job
        if (StringUtil.isNotBlank(rtwConfig.getName())) {
            engine.setName(rtwConfig.getName());
        } else if (StringUtil.isNotBlank(getName())) {
            engine.setName(getName());
        }

        engine.contextInit();

        if (StringUtil.isNotBlank(engine.getName())) {
            Log.trace("Job.engine_configured: " + engine.getName());
        } else {
            Log.trace("Job.unnamed_engine_configured");
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
            Log.info("Job.running: " + engine.getName());

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
    public void setName(String name) {
        super.setName(name);
        if (engine != null && StringUtil.isNotBlank(name)) {
            engine.setName(name);
            // Re-initialize context if name was used to determine directory
            engine.contextInit();
        }
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
