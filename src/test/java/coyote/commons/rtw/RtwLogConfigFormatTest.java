package coyote.commons.rtw;

import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.log.Log;
import coyote.commons.snap.AbstractSnapJob;
import coyote.commons.snap.LoggingConfigurator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the new RTW Log Configuration Format.
 *
 * <p>We moved from a simple JSON object format to a JSON array. This is
 * because we may define the same-named logger multiple times and that results
 * in invalid JSON. We need to support both until the old object style is
 * fully deprecated.</p>
 *
 * <p>While the initLogging() method can easily handle duplicate attribute
 * names, it is not valid JSON, and it has bugged some for a while now.</p>
 */
public class RtwLogConfigFormatTest {

    /**
     * Logging configured as an array of loggers.
     * @throws ConfigurationException
     */
    @Test
    public void testInitLoggingWithNewFormat() throws ConfigurationException {
        String json = "{\"Logging\": [" +
                "{\"StandardError\": { \"name\": \"error\", \"target\": \"STDERR\", \"categories\": \"error, fatal, warn\" }}," +
                "{\"StandardOutput\": { \"name\": \"default\", \"target\": \"STDOUT\", \"categories\": \"info, notice,debug,trace\" }}" +
                "]}";
        Config cfg = new Config(json);
        
        LoggingConfigurator.configure(cfg, null, null);
        
        TestJob job = new TestJob();
        job.configure(cfg);
        
        List<String> loggerNames = Log.getLoggerNames();
        
        boolean foundError = false;
        boolean foundDefault = false;
        for(String name : loggerNames) {
            if ("error".equals(name)) foundError = true;
            if ("default".equals(name)) foundDefault = true;
        }
        
        assertTrue(foundError, "Logger 'error' should be found");
        assertTrue(foundDefault, "Logger 'default' should be found");
    }


    /**
     * Logging configured as an object of loggers with each logger class (type) as an attribute.
     *
     * @throws ConfigurationException
     */
    @Test
    public void testInitLoggingWithOldFormat() throws ConfigurationException {
        String json = "{\"Logging\": {" +
                "\"StandardError\": { \"name\": \"old_error\", \"target\": \"STDERR\", \"categories\": \"error, fatal, warn\" }," +
                "\"StandardOutput\": { \"name\": \"old_default\", \"target\": \"STDOUT\", \"categories\": \"info, notice,debug,trace\" }" +
                "}}";
        Config cfg = new Config(json);

        LoggingConfigurator.configure(cfg, null, null);

        TestJob job = new TestJob();
        job.configure(cfg);

        List<String> loggerNames = Log.getLoggerNames();

        boolean foundError = false;
        boolean foundDefault = false;
        for(String name : loggerNames) {
            if ("old_error".equals(name)) foundError = true;
            if ("old_default".equals(name)) foundDefault = true;
        }

        assertTrue(foundError, "Logger 'old_error' should be found");
        assertTrue(foundDefault, "Logger 'old_default' should be found");
    }

    private static class TestJob extends AbstractSnapJob {
        @Override
        public void start() {}
        @Override
        public void stop() {}
    }
}
