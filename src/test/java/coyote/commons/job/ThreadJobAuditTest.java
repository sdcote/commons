package coyote.commons.job;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadJobAuditTest {

    @Test
    public void testDoubleDaemonize() throws InterruptedException {
        final AtomicInteger workCount = new AtomicInteger(0);
        ThreadJob job = new ThreadJob() {
            @Override
            public void doWork() {
                workCount.incrementAndGet();
                try { Thread.sleep(500); } catch (InterruptedException e) {}
            }
        };

        Thread t1 = job.daemonize();
        job.waitForActive(1000);
        assertTrue(job.isActive(), "Job should be active");
        
        Thread t2 = job.daemonize();
        assertTrue(t1 == t2, "Second daemonize should return the same thread if already active");
        
        job.shutdown();
        t1.join(1000);
        
        assertTrue(!job.isActive(), "Job should not be active after shutdown");
    }

    @Test
    public void testRevRace() throws InterruptedException {
        // Test if rev() works as expected and doesn't have visibility issues
        ThreadJob job = new ThreadJob() {
            @Override
            public void doWork() {
                // do nothing, just loop
            }
        };
        job.setIdleTimeout(100);
        job.daemonize();
        job.waitForActive(1000);
        
        Thread.sleep(200); // Wait for it to start idling
        assertTrue(job.isIdle(), "Job should be idling now");
        
        job.rev();
        assertTrue(!job.isIdle(), "Job should NOT be idling after rev()");
        
        job.shutdown();
        job.join(1000);
    }

    @Test
    public void testJoinTimeout() throws InterruptedException {
        ThreadJob job = new ThreadJob() {
            @Override
            public void doWork() {
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
            }
        };
        job.daemonize();
        job.waitForActive(1000);
        
        long start = System.currentTimeMillis();
        job.join(100);
        long duration = System.currentTimeMillis() - start;
        assertTrue(duration >= 100 && duration < 500, "Join(millis) should respect timeout, duration: " + duration);
        
        job.shutdown();
        job.join(2000);
    }
}
