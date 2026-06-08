package coyote.commons.i13n;

import java.util.Iterator;

/**
 * Utility class to format the contents of a StatBoard into a human-readable string.
 */
public class StatBoardFormatter {
  private StatBoardFormatter() {
    /* This utility class should not be instantiated */
  }


  private static final String LINE_SEP = System.getProperty("line.separator");

  /**
   * Formats the given StatBoard into a human-readable string.
   *
   * @param statBoard the StatBoard to format
   * @return a human-readable string representation of the StatBoard's state and contents.
   */
  public static String format(StatBoard statBoard) {
    if (statBoard == null) {
      return "StatBoard is null";
    }

    StringBuilder b = new StringBuilder();
    b.append("StatBoard: ").append(statBoard.getId()).append(LINE_SEP);
    b.append("Uptime: ").append(statBoard.getUptimeString()).append(LINE_SEP);
    b.append("Hostname: ").append(statBoard.getHostname()).append(LINE_SEP);

    // Counters
    if (statBoard.getCounterCount() > 0) {
      b.append(LINE_SEP).append("Counters:").append(LINE_SEP);
      Iterator<Counter> it = statBoard.getCounterIterator();
      while (it.hasNext()) {
        b.append("  ").append(it.next().toString()).append(LINE_SEP);
      }
    }

    // Gauges
    if (statBoard.getGaugeCount() > 0) {
      b.append(LINE_SEP).append("Gauges:").append(LINE_SEP);
      Iterator<Gauge> it = statBoard.getGaugeIterator();
      while (it.hasNext()) {
        Gauge g = it.next();
        b.append("  ").append(g.getName()).append(": ");
        b.append("Total=").append(g.getTotal());
        b.append(", Current=").append(g.getValuePerSecond()).append("/s");
        b.append(", Avg=").append(g.getAvgValuePerSecond()).append("/s");
        b.append(", Min=").append(g.getMinValuePerSecond()).append("/s");
        b.append(", Max=").append(g.getMaxValuePerSecond()).append("/s");
        b.append(LINE_SEP);
      }
    }

    // States
    if (statBoard.getStateCount() > 0) {
      b.append(LINE_SEP).append("States:").append(LINE_SEP);
      Iterator<State> it = statBoard.getStateIterator();
      while (it.hasNext()) {
        b.append("  ").append(it.next().toString()).append(LINE_SEP);
      }
    }

    // Timers
    Iterator<TimingMaster> itTimer = statBoard.getTimerIterator();
    if (itTimer.hasNext()) {
      b.append(LINE_SEP).append("Timers:").append(LINE_SEP);
      while (itTimer.hasNext()) {
        b.append("  ").append(itTimer.next().toString()).append(LINE_SEP);
      }
    }

    // ARM
    Iterator<ArmMaster> itArm = statBoard.getArmIterator();
    if (itArm.hasNext()) {
      b.append(LINE_SEP).append("ARM:").append(LINE_SEP);
      while (itArm.hasNext()) {
        b.append("  ").append(itArm.next().toString()).append(LINE_SEP);
      }
    }

    return b.toString();
  }

  /**
   * Formats the given StatBoard into an alternate human-readable string.
   *
   * @param statBoard the StatBoard to format
   * @return a human-readable string representation of the StatBoard's state and contents.
   */
  public static String format2(StatBoard statBoard) {
    if (statBoard == null) {
      return "NULL StatBoard";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("StatBoard: ").append(statBoard.getId()).append("\n");
    sb.append("Uptime: ").append(statBoard.getUptimeString()).append("\n");

    // Format Timers
    Iterator<TimingMaster> it = statBoard.getTimerIterator();
    if (it.hasNext()) {
      sb.append("\nTimers:\n");
      sb.append(String.format("%-40s %10s %15s %15s\n", "Name", "Hits", "Total Time", "Avg Time"));
      sb.append("-------------------------------------------------------------------------------------\n");
      while (it.hasNext()) {
        TimingMaster tm = it.next();
        // TimingMaster.toString() usually provides some info, but we can customize it
        sb.append(String.format("%-40s %10d %15d %15.2f\n",
                tm.getName(),
                tm.getGloballyActive(), // This might be the hit count depending on implementation
                tm.getAccrued(),
                (tm.getGloballyActive() > 0 ? (double)tm.getAccrued() / tm.getGloballyActive() : 0)));
      }
    }

    // Format Counters
    Iterator<Counter> cit = statBoard.getCounterIterator();
    if (cit.hasNext()) {
      sb.append("\nCounters:\n");
      sb.append(String.format("%-40s %15s\n", "Name", "Value"));
      sb.append("-------------------------------------------------------------\n");
      while (cit.hasNext()) {
        Counter c = cit.next();
        sb.append(String.format("%-40s %15d\n", c.getName(), c.getValue()));
      }
    }

    // Format Gauges
    Iterator<Gauge> git = statBoard.getGaugeIterator();
    if (git.hasNext()) {
      sb.append("\nGauges:\n");
      sb.append(String.format("%-40s %15s %15s\n", "Name", "Total", "Avg/sec"));
      sb.append("----------------------------------------------------------------------------\n");
      while (git.hasNext()) {
        Gauge g = git.next();
        sb.append(String.format("%-40s %15d %15.2f\n", g.getName(), g.getTotal(), g.getValuePerSecond()));
      }
    }

    return sb.toString();
  }

}
