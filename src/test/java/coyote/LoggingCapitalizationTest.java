package coyote;

import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.log.Log;
import coyote.commons.log.Logger;
import coyote.commons.rtw.ConfigTag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoggingCapitalizationTest {

    @Test
    public void testLoggingRecognition() throws ConfigurationException {
        // Clear all loggers first
        Log.removeAllLoggers();
        
        // We expect that BootStrap (or whatever handles the config) should recognize "Logging"
        // This is a bit tricky to test without running BootStrap.main, but we can test LoggingConfigurator.
        
        String json = "{\n" +
                "  \"Logging\": [\n" +
                "    { \"ConsoleAppender\": { \"name\": \"test\", \"categories\": \"info\" } }\n" +
                "  ]\n" +
                "}";
        
        Config cfg = new Config(json);
        
        // Mocking what AbstractSnapJob does:
        coyote.commons.snap.LoggingConfigurator.configure(cfg, null, null);
        
        boolean found = false;
        List<String> names = Log.getLoggerNames();
        for (String name : names) {
            if ("test".equals(name)) {
                found = true;
                break;
            }
        }
        
        assertTrue(found, "Logger with name 'test' should have been configured from 'Logging' section");
    }

    @Test
    public void testLowercaseLoggingRecognition() throws ConfigurationException {
        Log.removeAllLoggers();
        String json = "{\n" +
                "  \"logging\": [\n" +
                "    { \"ConsoleAppender\": { \"name\": \"test-lower\", \"categories\": \"info\" } }\n" +
                "  ]\n" +
                "}";
        Config cfg = new Config(json);
        coyote.commons.snap.LoggingConfigurator.configure(cfg, null, null);
        assertTrue(Log.getLoggerNames().contains("test-lower"));
    }

    @Test
    public void testLoggingAsObject() throws ConfigurationException {
        Log.removeAllLoggers();
        String json = "{\n" +
                "  \"Logging\": {\n" +
                "    \"ConsoleAppender\": { \"name\": \"test-object\", \"categories\": \"info\" }\n" +
                "  }\n" +
                "}";
        Config cfg = new Config(json);
        coyote.commons.snap.LoggingConfigurator.configure(cfg, null, null);
        assertTrue(Log.getLoggerNames().contains("test-object"));
    }

    @Test
    public void testConfigurationCapitalization() throws ConfigurationException {
        // Just verify the tag constant change didn't break something fundamentally 
        // as many things use ConfigTag.CONFIGURATION.
        // Since Config.getSections is case-insensitive, it should be fine.
        String json = "{\n" +
                "  \"configuration\": {\n" +
                "    \"Reader\": { \"class\": \"JsonReader\" }\n" +
                "  }\n" +
                "}";
        Config cfg = new Config(json);
        assertTrue(cfg.containsIgnoreCase(ConfigTag.CONFIGURATION));
        assertEquals(1, cfg.getSections(ConfigTag.CONFIGURATION).size());
    }
}
