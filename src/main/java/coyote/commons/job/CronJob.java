package coyote.commons.job;

import coyote.commons.CronEntry;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This is a job that uses a CronEntry to determine its next execution time.
 */
public class CronJob extends ScheduledJob {

  private CronEntry cronEntry = null;


  /**
   * Default constructor.
   */
  public CronJob() {
    super();
    setRepeatable(true);
  }


  /**
   * Constructor setting a runnable task.
   *
   * @param task The task to run when the time comes.
   */
  public CronJob(Runnable task) {
    super(task);
    setRepeatable(true);
  }


  /**
   * Constructor setting a cron pattern.
   *
   * @param pattern The cron pattern to use.
   * @throws ParseException if the pattern is invalid
   */
  public CronJob(String pattern) throws ParseException {
    this();
    setCronEntry(CronEntry.parse(pattern));
  }


  /**
   * Constructor setting a runnable task and a cron pattern.
   *
   * @param task    The task to run when the time comes.
   * @param pattern The cron pattern to use.
   * @throws ParseException if the pattern is invalid
   */
  public CronJob(Runnable task, String pattern) throws ParseException {
    this(task);
    setCronEntry(CronEntry.parse(pattern));
  }


  /**
   * @return the cron entry used to calculate the next execution time.
   */
  public CronEntry getCronEntry() {
    return cronEntry;
  }


  /**
   * Set the cron entry used to calculate the next execution time.
   *
   * @param entry the cron entry to use.
   * @return a reference to this CronJob for method chaining.
   */
  public CronJob setCronEntry(CronEntry entry) {
    this.cronEntry = entry;
    if (this.cronEntry != null) {
      setExecutionTime(cronEntry.getNextTime());
    }
    return this;
  }


  /**
   * Overridden to calculate the interval based on the next cron time.
   *
   * <p>The Scheduler uses this to determine when to next run the job if it is
   * repeatable. Since CronJobs are repeatable by nature, we calculate the
   * number of milliseconds from now until the next time the cron entry
   * specifies.</p>
   *
   * @return the number of milliseconds until the next execution time.
   */
  @Override
  public long getExecutionInterval() {
    if (cronEntry != null) {
      return cronEntry.getNextTime() - System.currentTimeMillis();
    }
    return super.getExecutionInterval();
  }

}
