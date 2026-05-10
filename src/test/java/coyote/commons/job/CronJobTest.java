package coyote.commons.job;

import coyote.commons.CronEntry;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

public class CronJobTest {

    @Test
    public void testCronJobExecutionTime() throws ParseException {
        CronJob job = new CronJob("* * * * *"); // every minute
        long now = System.currentTimeMillis();
        long nextTime = job.getExecutionTime();
        assertTrue(nextTime > now, "Next execution time should be in the future");
        
        CronEntry entry = job.getCronEntry();
        assertNotNull(entry);
        assertEquals(entry.getNextTime(), job.getExecutionTime(), "Execution time should match CronEntry next time");
    }

    @Test
    public void testCronJobRepeatable() throws ParseException {
        CronJob job = new CronJob("* * * * *");
        assertTrue(job.isRepeatable(), "CronJob should be repeatable by default");
    }
}
