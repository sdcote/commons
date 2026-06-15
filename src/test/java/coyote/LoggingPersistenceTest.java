package coyote;

import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.log.Log;
import coyote.commons.log.NullLogger;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.snap.JobLoader;
import coyote.commons.snap.LoggingConfigurator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoggingPersistenceTest {

    @Test
    public void testLoggingPersistence() throws ConfigurationException {
        // 1. Setup global logging
        String json = "{" +
                "  \"logging\": {\"StandardOutput\": {\"name\": \"global\", \"categories\": \"info, debug\"}}," +
                "  \"job\": {\"class\": \"coyote.RtwJob\", \"name\": \"testjob\"}" +
                "}";
        Config cfg = new Config(json);

        Log.removeAllLoggers();
        LoggingConfigurator.configure(cfg, null, null);
        
        assertTrue(Log.getLoggerCount() > 0);
        assertNotNull(Log.getLogger("global"));
        assertTrue(Log.isLogging("debug"), "Debug should be enabled initially");

        // 2. Load jobs - this should NOT reset logging because AbstractSnapJob no longer calls LoggingConfigurator.configure
        JobLoader.loadJobs(cfg);

        // 3. Verify logging is still there
        assertNotNull(Log.getLogger("global"), "Global logger should still exist after loading jobs");
        assertFalse(Log.getLoggers().get(0) instanceof NullLogger, "Should not have been replaced by NullLogger");
        assertTrue(Log.isLogging("debug"), "Debug should still be enabled after loading jobs");
    }

    @Test
    public void testLoggingNoOverride() throws ConfigurationException {
        // Even if the job section DOES have a logging section, it SHOULD NOT override anymore
        String json = "{" +
                "  \"logging\": {\"StandardOutput\": {\"name\": \"global\", \"categories\": \"info, debug\"}}," +
                "  \"job\": {" +
                "    \"class\": \"coyote.RtwJob\"," +
                "    \"logging\": {\"StandardOutput\": {\"name\": \"overridden\", \"categories\": \"error\"}}" +
                "  }" +
                "}";
        Config cfg = new Config(json);

        Log.removeAllLoggers();
        LoggingConfigurator.configure(cfg, null, null);
        assertNotNull(Log.getLogger("global"));

        JobLoader.loadJobs(cfg);

        assertNotNull(Log.getLogger("global"), "Global logger should NOT have been removed by job-specific logging anymore");
        assertNull(Log.getLogger("overridden"), "Job-specific logger should NOT exist");
        assertTrue(Log.isLogging("debug"), "Debug should still be enabled as job-specific logging was ignored");
    }
}
