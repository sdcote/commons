package coyote.commons.i13n;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StatBoardFormatterTest {

    @Test
    public void testFormat() {
        StatBoard scorecard = new StatBoardImpl();
        scorecard.setId("TestBoard");

        // Gauges are disabled by default in StatBoardImpl, let's enable them
        scorecard.enableGauges(true);

        // Add some metrics
        scorecard.increment("Counter1");
        scorecard.increase("Counter2", 10);

        scorecard.updateGauge("Gauge1", 50);
        scorecard.updateGauge("Gauge1", 60);

        scorecard.setState("State1", "Active");
        scorecard.setState("State2", 123L);

        scorecard.enableTiming(true);
        Timer timer = scorecard.startTimer("Timer1");
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {}
        timer.stop();

        scorecard.enableArm(true);
        ArmTransaction arm = scorecard.startArm("Arm1");
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {}
        arm.stop();

        String output = StatBoardFormatter.format(scorecard);

        assertNotNull(output);
        assertTrue(output.contains("StatBoard: TestBoard"), "Output should contain StatBoard ID");
        assertTrue(output.contains("Uptime:"), "Output should contain Uptime");
        assertTrue(output.contains("Counters:"), "Output should contain Counters section");
        assertTrue(output.contains("Counter1"), "Output should contain Counter1");
        assertTrue(output.contains("Counter2"), "Output should contain Counter2");
        assertTrue(output.contains("Gauges:"), "Output should contain Gauges section");
        assertTrue(output.contains("Gauge1"), "Output should contain Gauge1");
        assertTrue(output.contains("States:"), "Output should contain States section");
        assertTrue(output.contains("State1=Active"), "Output should contain State1=Active");
        assertTrue(output.contains("State2=123"), "Output should contain State2=123");
        assertTrue(output.contains("Timers:"), "Output should contain Timers section");
        assertTrue(output.contains("Timer1"), "Output should contain Timer1");
        assertTrue(output.contains("ARM:"), "Output should contain ARM section");
        assertTrue(output.contains("Arm1"), "Output should contain Arm1");
    }
}
