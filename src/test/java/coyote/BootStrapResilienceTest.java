package coyote;

import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.job.ScheduledJob;
import coyote.commons.snap.AbstractSnapJob;
import coyote.commons.snap.JobLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BootStrapResilienceTest {

    @Test
    public void testFailingJobDoesNotKillLoader() throws ConfigurationException {
        String json = "{\n" +
                "  \"Job\": [\n" +
                "    {\n" +
                "      \"class\": \"coyote.BootStrapResilienceTest$FailingJob\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"class\": \"coyote.BootStrapResilienceTest$SuccessJob\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        Config cfg = new Config(json);
        List<ScheduledJob> jobs = JobLoader.loadJobs(cfg);
        assertEquals(2, jobs.size());

        // We want to simulate what BootStrap does.
        // If we run them sequentially in the main thread (like BootStrap does for non-repeatable single jobs, 
        // but here we have two so it would use a scheduler)
        
        for (ScheduledJob job : jobs) {
            // We NO LONGER need manual try-catch here because JobLoader.SnapJobRunner handles it
            job.run();
        }
        
        assertTrue(SuccessJob.executed, "Success job should have run");
    }

    public static class FailingJob extends AbstractSnapJob {
        @Override
        public void start() {
            System.out.println("FailingJob running and throwing exception");
            throw new RuntimeException("Job Failed!");
        }
    }

    public static class SuccessJob extends AbstractSnapJob {
        public static boolean executed = false;
        @Override
        public void start() {
            System.out.println("SuccessJob running");
            executed = true;
        }
    }
}
