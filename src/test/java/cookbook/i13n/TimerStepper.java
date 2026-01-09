package cookbook.i13n;

import coyote.commons.i13n.Tabs;
import coyote.commons.i13n.Timer;

/**
 *
 */
public class TimerStepper {

  /**
   * @param args
   */
  public static void main(String[] args) {
    Tabs.getId();

    Timer timer = Tabs.startTimer("Mytimer");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    timer.stop();

    System.out.println(timer.toString());
  }

}
