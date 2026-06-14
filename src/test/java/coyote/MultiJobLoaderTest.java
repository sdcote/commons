package coyote;

import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.job.ScheduledJob;
import coyote.commons.snap.JobLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MultiJobLoaderTest {

    @Test
    public void testMultiJobLoading() throws ConfigurationException {
        String json = "{\n" +
                "  \"Job\": [\n" +
                "    {\n" +
                "      \"RtwJob\": {\n" +
                "        \"name\": \"Job1\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"RtwJob\": {\n" +
                "        \"name\": \"Job2\",\n" +
                "        \"schedule\": {\n" +
                "          \"millis\": 1000\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        Config cfg = new Config(json);
        List<ScheduledJob> jobs = JobLoader.loadJobs(cfg);
        
        assertEquals(2, jobs.size());
        assertFalse(jobs.get(0).isRepeatable());
        assertTrue(jobs.get(1).isRepeatable());
        assertEquals(1000, jobs.get(1).getExecutionInterval());
    }

    @Test
    public void testRepeatTrue() throws ConfigurationException {
        String json = "{\n" +
                "  \"RtwJob\": {\n" +
                "    \"name\": \"RepeatJob\"\n" +
                "  },\n" +
                "  \"repeat\": true\n" +
                "}";
        Config cfg = new Config(json);
        List<ScheduledJob> jobs = JobLoader.loadJobs(cfg);

        assertEquals(1, jobs.size());
        assertTrue(jobs.get(0).isRepeatable());
    }
}
