/*
 * Copyright (c) 2004 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.job;


import java.util.Calendar;
import java.util.Date;


public class ScheduledJob extends ThreadJob {
    private final Object mutex = new Object();

    /**
     * The name of this job for easy reporting
     */
    protected String name = null;

    /**
     * The description of this Scheduled Job
     */
    protected String description = null;

    /**
     * When we are supposed to start running
     */
    protected volatile long executionTime = 0;

    /**
     * When we are supposed to stop running
     */
    protected volatile long expirationTime = 0;

    /**
     * How long between executions we should wait
     */
    protected volatile long executionInterval = 0;

    /**
     * How many times we are allowed to execute
     */
    protected volatile long executionLimit = 0;

    /**
     * How many times we have executed
     */
    protected volatile long executionCount = 0;

    /**
     * The next job that we should run when we finish
     */
    protected volatile ScheduledJob chainedJob = null;

    /**
     * Used to implement a doubly-linked list in the Scheduler
     */
    protected volatile ScheduledJob nextJob = null;

    /**
     * Used to implement a doubly-linked list in the Scheduler
     */
    protected volatile ScheduledJob previousJob = null;

    /**
     * Indicates this job has been canceled
     */
    protected volatile boolean cancelled = false;

    /**
     * Indicates this job is to be repeated
     */
    protected volatile boolean repeatable = false;

    /**
     * Indicates this job is enabled to be run
     */
    protected volatile boolean enabled = true;


    /**
     * Default Constructor
     */
    public ScheduledJob() {
    }


    /**
     * Constructor setting a runnable task
     *
     * @param task The task to run when the time comes
     */
    public ScheduledJob(Runnable task) {
        super.work = task;
    }


    /**
     * @return the description of this job.
     */
    public String getDescription() {
        return description;
    }


    /**
     * Set the description of this job.
     *
     * @param description the description of this job.
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setDescription(String description) {
        this.description = description;
        return this;
    }


    /**
     * @return the name of this job
     */
    public String getName() {
        return name;
    }


    /**
     * Set the name of this job
     *
     * @param name the name of this job
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setName(String name) {
        this.name = name;
        return this;
    }


    /**
     * @return the number of milliseconds between executions of this job
     */
    public long getExecutionInterval() {
        return executionInterval;
    }


    /**
     * Set the number of milliseconds between executions of this job
     *
     * @param executionInterval the number of milliseconds between executions of this job
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setExecutionInterval(long executionInterval) {
        this.executionInterval = executionInterval;
        return this;
    }


    /**
     * @return the maximum number of times this job is to run
     */
    public long getExecutionLimit() {
        return executionLimit;
    }


    /**
     * Set the maximum number of times this job is to run
     *
     * @param executionLimit the maximum number of times this job is to run
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setExecutionLimit(long executionLimit) {
        this.executionLimit = executionLimit;
        return this;
    }


    /**
     * @return the epoch time (in milliseconds) when this job is to run
     */
    public long getExecutionTime() {
        synchronized (mutex) {
            return executionTime;
        }
    }


    /**
     * Set the time when this job is to run.
     *
     * @param date when this job is to run
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setExecutionTime(Date date) {
        if (date != null) {
            synchronized (mutex) {
                executionTime = date.getTime();
            }
        }
        return this;
    }


    /**
     * Set the time when this job is to run.
     *
     * @param cal when this job is supposed to run
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setExecutionTime(Calendar cal) {
        if (cal != null) {
            synchronized (mutex) {
                executionTime = cal.getTime().getTime();
            }
        }
        return this;
    }


    /**
     * Set the epoch time (in milliseconds) when this job is to run
     *
     * @param millis the epoch time (in milliseconds) when this job is to run
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setExecutionTime(long millis) {
        synchronized (mutex) {
            executionTime = millis;
        }
        return this;
    }


    /**
     * @return the epoch time (in milliseconds) when this job is no longer needed
     */
    public long getExpirationTime() {
        return expirationTime;
    }


    /**
     * Set the date/time which this job is considered too old to run.
     *
     * @param date the date/time which this job is considered too old to run.
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setExpirationTime(Date date) {
        if (date != null) {
            expirationTime = date.getTime();
        }
        return this;
    }


    /**
     * Set the date/time which this job is considered too old to run.
     *
     * @param cal the date/time which this job is considered too old to run.
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setExpirationTime(Calendar cal) {
        if (cal != null) {
            expirationTime = cal.getTime().getTime();
        }
        return this;
    }


    /**
     * Set the epoch time (in milliseconds) which this job is considered too old
     * to run
     *
     * @param millis the epoch time (in milliseconds) which this job is
     *               considered too old to run
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setExpirationTime(long millis) {
        expirationTime = millis;
        return this;
    }


    /**
     * Returns whether this job has been running longer than it is
     * supposed.
     *
     * @return True if the expiration time has elapsed, false if the expiration
     * time has not passed or if no expiration time has been set.
     */
    public boolean isExpired() {
        return (expirationTime > 0) && (System.currentTimeMillis() - this.started_time > expirationTime);
    }


    /**
     * @return true if this job has been canceled (do not run) false if this job
     * is otherwise approved to run.
     */
    public boolean isCancelled() {
        return cancelled;
    }


    /**
     * Set this job's canceled state
     *
     * @param cancelled true if this job has been canceled (do not run) false if
     *                  this job is otherwise approved to run.
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
        return this;
    }


    /**
     * @return the next job to be run after this one
     */
    public ScheduledJob getNextJob() {
        return nextJob;
    }


    /**
     * Set  the next job to be run after this one
     *
     * @param nextJob the next job to be run after this one
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setNextJob(ScheduledJob nextJob) {
        this.nextJob = nextJob;
        return this;
    }


    /**
     * @return the next job to be run before this job
     */
    public ScheduledJob getPreviousJob() {
        return previousJob;
    }


    /**
     * Get the next job to be run before this job
     *
     * @param previousJob the next job to be run before this job
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setPreviousJob(ScheduledJob previousJob) {
        this.previousJob = previousJob;
        return this;
    }


    /**
     * @return the job linked to this job
     */
    public ScheduledJob getChainedJob() {
        return chainedJob;
    }


    /**
     * It is possible to set a change of jobs together. This method allows the
     * setting of a sub job.
     *
     * <p>This is different from {@link #getNextJob()} in that this has nothing
     * to do with linking all the job together. This is only allowing the
     * attachment of another job to run immediately after this job, without
     * respect to a time.</p>
     *
     * @param chainedJob the job to execute immediately after this job completes
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setChainedJob(ScheduledJob chainedJob) {
        this.chainedJob = chainedJob;
        return this;
    }


    /**
     * @return the number of times this job has been executed
     */
    public long getExecutionCount() {
        synchronized (mutex) {
            return executionCount;
        }
    }


    /**
     * Increment the job execution counter by 1 (one).
     *
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob incrementExecutionCount() {
        synchronized (mutex) {
            this.executionCount++;
        }
        return this;
    }


    /**
     * @return true if this job is a recurring job, false if it is to be run only
     * once.
     */
    public boolean isRepeatable() {
        synchronized (mutex) {
            return repeatable;
        }
    }


    /**
     * Set this job to be a recurring job.
     *
     * @param repeatable true if this job is a recurring job, false if it is to
     *                   be run only once.
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setRepeatable(boolean repeatable) {
        synchronized (mutex) {
            this.repeatable = repeatable;
        }
        return this;
    }


    /**
     * @return true if this job is eligible for scheduling, false if it is not to
     * be (re) added to the scheduler.
     */
    public boolean isEnabled() {
        synchronized (mutex) {
            return enabled;
        }
    }


    /**
     * Set if this job is to be (re)scheduled in the scheduler
     *
     * @param enabled true if this job is eligible for scheduling, false if it is
     *                not to be (re) added to the scheduler.
     * @return a reference to this ScheduledJob for method chaining.
     */
    public ScheduledJob setEnabled(boolean enabled) {
        synchronized (mutex) {
            this.enabled = enabled;
        }
        return this;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        if (name != null && name.trim().length() > 0) {
            b.append(name);
        } else {
            b.append(this.getClass().getSimpleName());
        }
        return b.toString();
    }

}