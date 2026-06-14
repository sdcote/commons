package coyote.commons.snap;

import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;

/**
 * The SnapJob interface defines the contract for lightweight, configurable components that can be
 * executed as a job.
 *
 * <p>A SnapJob follows a simple lifecycle: configuration, initialization (implied), and execution
 * (start/stop).</p>
 */
public interface SnapJob extends Runnable {

    /**
     * The name of the property used to define the application's working directory.
     *
     * <p>This is often used to resolve relative paths for logging, data files, and other job-specific
     * resources.</p>
     */
    static String APP_WORK = "app.work";


    /**
     * Configure the job with the provided configuration.
     *
     * @param cfg The configuration for the job.
     * @throws ConfigurationException If there is a problem with the configuration.
     */
    void configure(Config cfg) throws ConfigurationException;

    /**
     * Start the job execution.
     *
     * <p>This method should contain the primary logic for the job. It may be synchronous or
     * asynchronous depending on the implementation.</p>
     */
    void start();

    /**
     * Signal the job to stop its execution.
     *
     * <p>This method should provide a way to gracefully shut down the job, releasing any resources
     * and stopping any background threads.</p>
     */
    void stop();

    /**
     * Set the command line arguments for the job.
     *
     * @param args The command line arguments passed to the application.
     */
    void setCommandLineArguments(String[] args);

    /**
     * @return the name of the job
     */
    String getName();

    /**
     * @param name the name of the job
     */
    void setName(String name);

}
