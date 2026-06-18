# Scheduler Documentation

The `coyote.commons.job.Scheduler` is a background service designed to execute tasks (`Runnable` or `ScheduledJob`) at specific times or intervals. It leverages a thread pool for parallel execution and manages a priority-ordered queue of jobs. `BootStrap` uses an internal `Scheduler` instance to manage multi-job and repeating job execution.

## User Documentation

### Overview
The Scheduler runs as a background thread. You add jobs to it, and it ensures they run at the scheduled time. Jobs can be simple one-off tasks or repeating tasks with complex schedules.

### Basic Usage
To use the scheduler, you typically create an instance, daemonize it (to run it in the background), and then schedule your tasks.

```java
Scheduler scheduler = new Scheduler();
scheduler.daemonize(); // Starts the scheduler in its own background thread

// Schedule a simple task to run in 5 seconds
scheduler.schedule(() -> System.out.println("Hello World!"), System.currentTimeMillis() + 5000);
```

### Scheduling Options
The `schedule` method provides several ways to control execution:
- **One-off tasks**: Run once at a specific time.
- **Repeating tasks**: Run at regular intervals.
- **Limited repetitions**: Run a specific number of times.
- **Expiration**: Stop running after a certain date/time.

Example of a repeating task:
```java
// Run every minute, up to 10 times
scheduler.schedule(myTask, System.currentTimeMillis(), 60000, 0, 10);
```

### Managing Jobs
The `schedule` methods return a `ScheduledJob` object. You can use this object to:
- **Cancel** the job: `job.setCancelled(true);`
- **Disable/Enable** the job: `job.setEnabled(false);`
- **Check status**: `job.getExecutionCount()`, `job.isExpired()`.

---

## Developer Documentation

### Architecture
`Scheduler` extends `ThreadJob`, which provides a standardized lifecycle for background threads. It uses an `ExecutorService` (specifically a Cached Thread Pool) to execute jobs, so that the scheduler thread itself is never blocked by a long-running job.

### The `doWork()` Lifecycle
The core logic of the scheduler resides in the `doWork()` method, which is called repeatedly by the `ThreadJob.run()` loop.

#### 1. Initialization
When the scheduler starts, `initialize()` is called once. It sets up the `ExecutorService`.

#### 2. The Main Loop (`doWork`)
Each invocation of `doWork()` performs the following steps:

1.  **Check Next Job**: It looks at the head of the priority queue (`nextJob`).
2.  **Timing Analysis**:
    -   It calculates the time remaining until the job's scheduled execution time.
    -   If the job is more than `WAIT_TIME` (default 50ms) in the future, `doWork()` returns. This allows the parent `ThreadJob` to handle idling or yielding.
    -   If the job's time is within the `WAIT_TIME` threshold, the scheduler enters a focused wait state.
3.  **Precise Waiting**:
    -   The scheduler calls `mutex.wait(millis)` to wait for the exact moment of execution.
    -   This wait can be interrupted if a new job is added to the scheduler (via `schedule()`), causing a re-evaluation of the `nextJob`.
4.  **Job Execution**:
    -   Once the time arrives, the job is removed from the queue.
    -   If the job is enabled and not cancelled, it is submitted to the `ExecutorService`.
    -   The `executionCount` of the job is incremented.
5.  **Rescheduling**:
    -   If the job is marked as repeatable and has not reached its execution limit, it is rescheduled.
    -   The next execution time is calculated as `System.currentTimeMillis() + interval`.
    -   The job is re-inserted into the priority queue in its new sorted position.

### Thread Safety
The `Scheduler` uses a `mutex` object to synchronize access to the job queue.
-   Adding a job (`schedule`) synchronizes on `mutex`, inserts the job in sorted order, and calls `notifyAll()` to wake up `doWork()` if it was waiting for a later job.
-   Removing a job (`remove`) also synchronizes on `mutex`.
-   The `doWork()` method holds the `mutex` during its timing checks and job removal.

### Job Priority
Jobs are maintained in a linked list sorted by `executionTime`. The scheduler always processes the job with the earliest execution time first. If multiple jobs have the same execution time, they are processed in the order they were added.
