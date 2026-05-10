package coyote;

import coyote.commons.CronEntry;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.rtw.ConfigTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DaemonJobTest {

    @Test
    public void testParseSchedulePattern() throws ConfigurationException {
        DaemonJob job = new DaemonJob();
        Config cfg = new Config();
        cfg.add(ConfigTag.PATTERN, "1 2 3 4 5");
        CronEntry entry = job.parseSchedule(cfg);
        assertEquals("1", entry.getMinutePattern());
        assertEquals("2", entry.getHourPattern());
        assertEquals("3", entry.getDayPattern());
        assertEquals("4", entry.getMonthPattern());
        assertEquals("5", entry.getDayOfWeekPattern());
    }

    @Test
    public void testParseScheduleIndividualFields() throws ConfigurationException {
        DaemonJob job = new DaemonJob();
        Config cfg = new Config();
        cfg.add(ConfigTag.MINUTES, "10");
        cfg.add(ConfigTag.HOURS, "11");
        cfg.add(ConfigTag.DAYS, "12");
        cfg.add(ConfigTag.MONTHS, "11");
        cfg.add(ConfigTag.DAYS_OF_WEEK, "1");
        CronEntry entry = job.parseSchedule(cfg);
        assertEquals("10", entry.getMinutePattern());
        assertEquals("11", entry.getHourPattern());
        assertEquals("12", entry.getDayPattern());
        assertEquals("11", entry.getMonthPattern());
        assertEquals("1", entry.getDayOfWeekPattern());
    }

    @Test
    public void testParseScheduleOverwrite() throws ConfigurationException {
        DaemonJob job = new DaemonJob();
        Config cfg = new Config();
        cfg.add(ConfigTag.PATTERN, "* * * * *");
        cfg.add(ConfigTag.MINUTES, "30");
        CronEntry entry = job.parseSchedule(cfg);
        // The last one wins because it iterates through fields
        assertEquals("30", entry.getMinutePattern());
        
        cfg = new Config();
        cfg.add(ConfigTag.MINUTES, "30");
        cfg.add(ConfigTag.PATTERN, "1 1 1 1 1");
        entry = job.parseSchedule(cfg);
        assertEquals("1", entry.getMinutePattern());
    }

    @Test
    public void testParseScheduleComplexPatterns() throws ConfigurationException {
        DaemonJob job = new DaemonJob();
        Config cfg = new Config();
        
        // Test ranges
        cfg.add(ConfigTag.MINUTES, "0-15");
        // Test divisors
        cfg.add(ConfigTag.HOURS, "*/2");
        // Test wildcards
        cfg.add(ConfigTag.DAYS, "*");
        // Test lists (though not explicitly asked, it's common in cron)
        cfg.add(ConfigTag.MONTHS, "1,6,12");
        // Test range with divisor
        cfg.add(ConfigTag.DAYS_OF_WEEK, "1-5/2");
        
        CronEntry entry = job.parseSchedule(cfg);
        assertEquals("0-15", entry.getMinutePattern());
        assertEquals("*/2", entry.getHourPattern());
        assertEquals("*", entry.getDayPattern());
        assertEquals("1,6,12", entry.getMonthPattern());
        assertEquals("1-5/2", entry.getDayOfWeekPattern());
        
        // Verify they actually parse correctly in CronEntry (basic check)
        // These are private in CronEntry so we can't test them directly here easily 
        // without reflection or changing visibility, but we can check if they are set correctly.
        assertEquals("0-15", entry.getMinutePattern());
    }
}
