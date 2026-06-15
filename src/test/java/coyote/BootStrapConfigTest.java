package coyote;

import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.snap.JobLoader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BootStrapConfigTest {

    @Test
    public void testMissingJobTag() throws ConfigurationException {
        String json = "{\"class\":\"coyote.commons.snap.AbstractSnapJob\"}";
        Config cfg = new Config(json);
        // In the new implementation, loadJobs returns an empty list if no Job tag is found
        assertTrue(JobLoader.loadJobs(cfg).isEmpty());
    }

    @Test
    public void testJobMissingClass() throws ConfigurationException {
        String json = "{\"Job\":{\"name\":\"NoClassJob\"}}";
        Config cfg = new Config(json);
        assertThrows(ConfigurationException.class, () -> {
            JobLoader.loadJobs(cfg);
        });
    }

    @Test
    public void testCaseInsensitiveJobTag() throws ConfigurationException {
        String json = "{\"job\":{\"class\":\"coyote.BootStrapResilienceTest$SuccessJob\"}}";
        Config cfg = new Config(json);
        assertEquals(1, JobLoader.loadJobs(cfg).size());
        
        json = "{\"JOB\":{\"class\":\"coyote.BootStrapResilienceTest$SuccessJob\"}}";
        cfg = new Config(json);
        assertEquals(1, JobLoader.loadJobs(cfg).size());

        // Array of jobs
        json = "{\"jOb\":[{\"class\":\"coyote.BootStrapResilienceTest$SuccessJob\"}, {\"class\":\"coyote.BootStrapResilienceTest$SuccessJob\"}]}";
        cfg = new Config(json);
        assertEquals(2, JobLoader.loadJobs(cfg).size());
    }
}
