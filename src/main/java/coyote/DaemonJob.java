package coyote;

import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.log.Log;
import coyote.commons.snap.AbstractSnapJob;

/**
 * DaemonJob class that extends AbstractSnapJob.
 * It operates by being called from the BootStrap based on a configuration file.
 * The configuration contains a list of jobs and logging settings.
 */
public class DaemonJob extends AbstractSnapJob {

    /**
     * Configure the job with the provided configuration.
     *
     * @param cfg The configuration for the job.
     * @throws ConfigurationException If there is a problem with the configuration.
     */
    @Override
    public void configure(Config cfg) throws ConfigurationException {
        super.configure(cfg);
        // The Logging section is handled by AbstractSnapJob.configure -> initLogging
    }

    /**
     * Start the daemon job.
     * Initially, it only writes an "info" message of "Hello World."
     */
    @Override
    public void start() {
        Log.info("Hello World");
    }

    /**
     * Shut everything down when the job is requested to stop.
     */
    @Override
    public void stop() {
        // currently no resources to stop
    }

}
