package coyote;

import java.util.List;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.log.Log;
import coyote.commons.log.NullLogger;
import coyote.commons.snap.LoggingConfigurator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoggingConfigTest {

    @Test
    public void testCaseInsensitiveLoggingTag() throws ConfigurationException {
        // Case-insensitive check for "Logging" - single object
        String json = "{\"lOgGiNg\":{\"StandardOutput\":{\"name\":\"test\"}}}";
        Config cfg = new Config(json);
        
        Log.removeAllLoggers();
        assertEquals(0, Log.getLoggerCount());
        
        LoggingConfigurator.configure(cfg, null, null);
        
        assertTrue(Log.getLoggerCount() > 0, "Should have loaded the logger from lOgGiNg section");
        assertNotNull(Log.getLogger("test"), "Logger 'test' should exist");

        // Case-insensitive check for "Logging" - array
        json = "{\"LOGGING\":[{\"StandardOutput\":{\"name\":\"test2\"}}]}";
        cfg = new Config(json);
        Log.removeAllLoggers();
        LoggingConfigurator.configure(cfg, null, null);
        assertTrue(Log.getLoggerCount() > 0, "Should have loaded the logger from LOGGING array section");
        assertNotNull(Log.getLogger("test2"), "Logger 'test2' should exist");
    }

    @Test
    public void testMissingLoggingSection() throws ConfigurationException {
        // When no logging section is present, it should use NullLogger
        String json = "{\"job\":{\"class\":\"coyote.BootStrapResilienceTest$SuccessJob\"}}";
        Config cfg = new Config(json);
        
        Log.addLogger("temporary", new NullLogger());
        assertTrue(Log.getLoggerCount() > 0);
        
        LoggingConfigurator.configure(cfg, null, null);
        
        assertEquals(1, Log.getLoggerCount(), "Should have exactly one logger");
        assertTrue(Log.getLoggerNames().contains("NullLogger") || Log.getLogger("NullLogger") != null || Log.getLoggers().get(0) instanceof NullLogger, "Should be NullLogger");
    }
}
