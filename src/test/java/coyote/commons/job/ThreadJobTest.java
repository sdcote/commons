package coyote.commons.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit tests for ThreadJob.
 */
public class ThreadJobTest {

  @Test
  public void testConstructor() {
    ThreadJob job = new ThreadJob();
    assertNotNull(job);
    assertFalse(job.isActive());
  }

  @Test
  public void testConstructorWithRunnable() {
    final AtomicBoolean runCalled = new AtomicBoolean(false);
    Runnable work = () -> runCalled.set(true);
    ThreadJob job = new ThreadJob(work);
    job.setDoWorkOnce(true);
    job.run();
    assertTrue(runCalled.get());
    assertTrue(job.isShutdown());
  }

  @Test
  public void testLifecycle() throws InterruptedException {
    final AtomicInteger initCount = new AtomicInteger(0);
    final AtomicInteger workCount = new AtomicInteger(0);
    final AtomicInteger termCount = new AtomicInteger(0);

    ThreadJob job = new ThreadJob() {
      @Override
      public void initialize() {
        initCount.incrementAndGet();
      }

      @Override
      public void doWork() {
        workCount.incrementAndGet();
        shutdown();
      }

      @Override
      public void terminate() {
        termCount.incrementAndGet();
      }
    };

    Thread t = job.daemonize();
    t.join(5000);

    assertEquals(1, initCount.get());
    assertEquals(1, workCount.get());
    assertEquals(1, termCount.get());
    assertFalse(job.isActive());
    assertTrue(job.isShutdown());
  }

  @Test
  public void testDoWorkOnce() throws InterruptedException {
    final AtomicInteger workCount = new AtomicInteger(0);
    ThreadJob job = new ThreadJob() {
      @Override
      public void doWork() {
        workCount.incrementAndGet();
      }
    };
    job.setDoWorkOnce(true);
    job.run();
    assertEquals(1, workCount.get());
    assertFalse(job.isActive());
  }

  @Test
  public void testRestart() throws InterruptedException {
    final AtomicInteger initCount = new AtomicInteger(0);
    final AtomicInteger workCount = new AtomicInteger(0);

    ThreadJob job = new ThreadJob() {
      @Override
      public void initialize() {
        initCount.incrementAndGet();
      }

      @Override
      public void doWork() {
        workCount.incrementAndGet();
        if (initCount.get() == 1) {
          restart();
        } else {
          shutdown();
        }
      }
    };

    job.run();
    assertEquals(2, initCount.get());
    assertEquals(2, workCount.get());
  }

  @Test
  public void testSuspendResume() throws InterruptedException {
    final AtomicInteger workCount = new AtomicInteger(0);
    ThreadJob job = new ThreadJob() {
      @Override
      public void doWork() {
        workCount.incrementAndGet();
        suspend();
      }
    };

    Thread t = job.daemonize();
    job.waitForActive(1000);

    // Wait for it to suspend after first work
    while (!job.isSuspended() && t.isAlive()) {
      Thread.sleep(10);
    }
    assertTrue(job.isSuspended());
    int countAfterSuspend = workCount.get();

    // Resume
    job.resume();
    
    // Give it time to run again and suspend again
    Thread.sleep(100);
    assertTrue(workCount.get() > countAfterSuspend);
    
    job.shutdown();
    t.join(1000);
  }

  @Test
  public void testIdle() throws InterruptedException {
    ThreadJob job = new ThreadJob();
    job.setIdleTimeout(100);
    job.setIdleWait(50);
    
    final AtomicBoolean wasIdle = new AtomicBoolean(false);
    ThreadJob spy = new ThreadJob() {
        @Override
        public void doWork() {
            if (isIdle()) {
                wasIdle.set(true);
                shutdown();
            }
        }
    };
    spy.setIdleTimeout(50);
    spy.setIdleWait(10);
    
    Thread t = spy.daemonize();
    t.join(2000);
    assertTrue(wasIdle.get());
  }

  @Test
  public void testHyper() throws InterruptedException {
    ThreadJob job = new ThreadJob();
    job.setHyper(true);
    assertTrue(job.isHyper());
    job.setHyper(false);
    assertFalse(job.isHyper());
  }

  @Test
  public void testParkSleep() {
    ThreadJob job = new ThreadJob();
    assertFalse(job.isParkSleep());
    job.setParkSleep(true);
    assertTrue(job.isParkSleep());
  }

  @Test
  public void testIdleSettings() {
    ThreadJob job = new ThreadJob();
    job.setIdleTimeout(5000);
    assertEquals(5000, job.getIdleTimeout());
    job.setIdleWait(200);
    assertEquals(200, job.getIdleWait());
  }

  @Test
  public void testRev() {
    ThreadJob job = new ThreadJob();
    job.idle();
    assertTrue(job.isIdle());
    job.rev();
    assertFalse(job.isIdle());
  }
  
  @Test
  public void testDaemonizeWithName() {
      ThreadJob job = new ThreadJob();
      Thread t = job.daemonize("TestThread");
      assertEquals("TestThread", t.getName());
      job.shutdown();
  }

  @Test
  public void testParkInterrupt() throws InterruptedException {
    final AtomicBoolean interrupted = new AtomicBoolean(false);
    ThreadJob job = new ThreadJob() {
      @Override
      public void doWork() {
        park(10000); // long park
        if (Thread.currentThread().isInterrupted()) {
            interrupted.set(true);
        }
        shutdown();
      }
    };
    Thread t = job.daemonize();
    job.waitForActive(1000);
    Thread.sleep(100);
    job.shutdown(); // should interrupt park
    t.join(1000);
    assertTrue(interrupted.get());
  }

  @Test
  public void testSleep() throws InterruptedException {
      ThreadJob job = new ThreadJob();
      long start = System.currentTimeMillis();
      job.sleep(100);
      long duration = System.currentTimeMillis() - start;
      assertTrue(duration >= 100);
  }

  @Test
  public void testHasStarted() throws InterruptedException {
      ThreadJob job = new ThreadJob() {
          @Override
          public void doWork() {
              shutdown();
          }
      };
      assertFalse(job.hasStarted());
      job.run();
      assertTrue(job.hasStarted());
  }

  @Test
  public void testGetThread() {
      ThreadJob job = new ThreadJob();
      assertNull(job.getThread());
      Thread t = job.daemonize();
      assertEquals(t, job.getThread());
      job.shutdown();
  }
  @Test
  public void testJoin() throws InterruptedException {
      ThreadJob job = new ThreadJob() {
          @Override
          public void doWork() {
              try { Thread.sleep(100); } catch (InterruptedException e) {}
              shutdown();
          }
      };
      job.daemonize();
      job.waitForActive(1000);
      long start = System.currentTimeMillis();
      job.join();
      long duration = System.currentTimeMillis() - start;
      assertTrue(duration >= 100);
      assertFalse(job.isActive());
  }
}
