package coyote.commons.job;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit tests for Scheduler.
 */
public class SchedulerTest {

    @Test
    public void testSchedulerExecution() throws InterruptedException {
        Scheduler scheduler = new Scheduler();
        scheduler.initialize(); // Ensure executor is initialized
        
        final AtomicInteger executionCount = new AtomicInteger(0);
        ScheduledJob job = new ScheduledJob(() -> {
            executionCount.incrementAndGet();
        });
        job.setExecutionTime(System.currentTimeMillis());
        
        scheduler.schedule(job);
        
        // Start scheduler in a separate thread
        Thread schedulerThread = scheduler.daemonize();
        
        // Wait for the job to be executed.
        // The scheduler checks every 50ms (WAIT_TIME).
        long start = System.currentTimeMillis();
        while (executionCount.get() == 0 && (System.currentTimeMillis() - start < 2000)) {
            Thread.sleep(100);
        }
        
        scheduler.shutdown();
        schedulerThread.join(1000);
        
        assertEquals(1, executionCount.get(), "Job should have been executed once");
    }

    @Test
    public void testSchedulerRepeatableJob() throws InterruptedException {
        Scheduler scheduler = new Scheduler();
        scheduler.initialize();
        
        final AtomicInteger executionCount = new AtomicInteger(0);
        ScheduledJob job = new ScheduledJob(() -> {
            executionCount.incrementAndGet();
        });
        job.setExecutionTime(System.currentTimeMillis());
        job.setRepeatable(true);
        job.setExecutionInterval(100);
        job.setExecutionLimit(3);
        
        scheduler.schedule(job);
        Thread schedulerThread = scheduler.daemonize();
        
        long start = System.currentTimeMillis();
        while (executionCount.get() < 3 && (System.currentTimeMillis() - start < 3000)) {
            Thread.sleep(100);
        }
        
        scheduler.shutdown();
        schedulerThread.join(1000);
        
        assertEquals(3, executionCount.get(), "Job should have been executed three times");
    }
}
