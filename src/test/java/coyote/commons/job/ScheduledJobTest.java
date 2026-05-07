package coyote.commons.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Unit tests for ScheduledJob.
 */
public class ScheduledJobTest {

    @Test
    public void testDefaultConstructor() {
        ScheduledJob job = new ScheduledJob();
        assertNotNull(job);
        assertNull(job.getName());
        assertNull(job.getDescription());
        assertEquals(0, job.getExecutionInterval());
        assertEquals(0, job.getExecutionLimit());
        assertEquals(0, job.getExecutionTime());
        assertEquals(0, job.getExpirationTime());
        assertFalse(job.isCancelled());
        assertFalse(job.isRepeatable());
        assertTrue(job.isEnabled());
        assertEquals(0, job.getExecutionCount());
    }

    @Test
    public void testRunnableConstructor() {
        AtomicBoolean runCalled = new AtomicBoolean(false);
        Runnable task = () -> runCalled.set(true);
        ScheduledJob job = new ScheduledJob(task);
        
        job.setDoWorkOnce(true);
        job.run();
        assertTrue(runCalled.get());
    }

    @Test
    public void testNameAndDescription() {
        ScheduledJob job = new ScheduledJob();
        job.setName("TestJob").setDescription("TestDescription");
        assertEquals("TestJob", job.getName());
        assertEquals("TestDescription", job.getDescription());
        assertEquals("TestJob", job.toString());
    }

    @Test
    public void testExecutionIntervalAndLimit() {
        ScheduledJob job = new ScheduledJob();
        job.setExecutionInterval(1000).setExecutionLimit(5);
        assertEquals(1000, job.getExecutionInterval());
        assertEquals(5, job.getExecutionLimit());
    }

    @Test
    public void testExecutionTime() {
        ScheduledJob job = new ScheduledJob();
        long now = System.currentTimeMillis();
        
        job.setExecutionTime(now);
        assertEquals(now, job.getExecutionTime());

        Date date = new Date(now + 1000);
        job.setExecutionTime(date);
        assertEquals(now + 1000, job.getExecutionTime());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now + 2000);
        job.setExecutionTime(cal);
        assertEquals(now + 2000, job.getExecutionTime());
        
        job.setExecutionTime((Date)null);
        assertEquals(now + 2000, job.getExecutionTime());

        job.setExecutionTime((Calendar)null);
        assertEquals(now + 2000, job.getExecutionTime());
    }

    @Test
    public void testExpirationTime() {
        ScheduledJob job = new ScheduledJob();
        long now = System.currentTimeMillis();

        job.setExpirationTime(now + 5000);
        assertEquals(now + 5000, job.getExpirationTime());

        Date date = new Date(now + 6000);
        job.setExpirationTime(date);
        assertEquals(now + 6000, job.getExpirationTime());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now + 7000);
        job.setExpirationTime(cal);
        assertEquals(now + 7000, job.getExpirationTime());

        job.setExpirationTime((Date)null);
        assertEquals(now + 7000, job.getExpirationTime());

        job.setExpirationTime((Calendar)null);
        assertEquals(now + 7000, job.getExpirationTime());
    }

    @Test
    public void testIsExpired() throws InterruptedException {
        ScheduledJob job = new ScheduledJob();
        assertFalse(job.isExpired());

        // We can't easily set started_time because it's protected in ThreadJob.
        // But if started_time is 0 (uninitialized), it's treated as relative to epoch.
        // isExpired() logic: (expirationTime > 0) && (System.currentTimeMillis() - this.started_time > expirationTime)

        long now = System.currentTimeMillis();
        
        // expirationTime of 1ms, started_time is 0. 
        // System.currentTimeMillis() - 0 > 1 is definitely true.
        job.setExpirationTime(1); 
        assertTrue(job.isExpired());
        
        // Set expiration time to something in the far future relative to now (and definitely relative to started_time=0)
        job.setExpirationTime(now + 1000000);
        assertFalse(job.isExpired());

        job.setExpirationTime(0);
        assertFalse(job.isExpired());
        
        // Testing with started_time initialized
        ScheduledJob startedJob = new ScheduledJob() {
            @Override
            public void doWork() {
                // do nothing, just need it to run
            }
        };
        startedJob.setDoWorkOnce(true);
        startedJob.setExpirationTime(500); // Expires 500ms after start
        
        startedJob.run(); // sets started_time to now
        assertFalse(startedJob.isExpired());
        
        Thread.sleep(600);
        assertTrue(startedJob.isExpired());
    }

    @Test
    public void testCancelled() {
        ScheduledJob job = new ScheduledJob();
        assertFalse(job.isCancelled());
        job.setCancelled(true);
        assertTrue(job.isCancelled());
        job.setCancelled(false);
        assertFalse(job.isCancelled());
    }

    @Test
    public void testJobLinking() {
        ScheduledJob job1 = new ScheduledJob();
        ScheduledJob job2 = new ScheduledJob();
        ScheduledJob job3 = new ScheduledJob();

        job2.setPreviousJob(job1).setNextJob(job3);
        assertSame(job1, job2.getPreviousJob());
        assertSame(job3, job2.getNextJob());
    }

    @Test
    public void testChainedJob() {
        ScheduledJob job1 = new ScheduledJob();
        ScheduledJob job2 = new ScheduledJob();

        job1.setChainedJob(job2);
        assertSame(job2, job1.getChainedJob());
    }

    @Test
    public void testExecutionCountAndRepeatable() {
        ScheduledJob job = new ScheduledJob();
        assertEquals(0, job.getExecutionCount());
        job.incrementExecutionCount();
        assertEquals(1, job.getExecutionCount());
        job.incrementExecutionCount();
        assertEquals(2, job.getExecutionCount());

        assertFalse(job.isRepeatable());
        job.setRepeatable(true);
        assertTrue(job.isRepeatable());
    }

    @Test
    public void testEnabled() {
        ScheduledJob job = new ScheduledJob();
        assertTrue(job.isEnabled());
        job.setEnabled(false);
        assertFalse(job.isEnabled());
        job.setEnabled(true);
        assertTrue(job.isEnabled());
    }

    @Test
    public void testToString() {
        ScheduledJob job = new ScheduledJob();
        assertEquals("ScheduledJob", job.toString());
        job.setName("   ");
        assertEquals("ScheduledJob", job.toString());
        job.setName("MyJob");
        assertEquals("MyJob", job.toString());
    }
}
