package coyote;

import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.snap.JobLoader;
import coyote.commons.snap.SnapJob;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JobLoaderTest {

    @Test
    public void testJobLoading() throws ConfigurationException {
        Config cfg = new Config();
        cfg.put(ConfigTag.CLASS, "coyote.RtwJob");
        Config subCfg = new Config();
        subCfg.put("TestKey", "TestValue");
        cfg.put(ConfigTag.CONFIGURATION, subCfg);

        SnapJob job = JobLoader.loadJob(cfg);
        assertNotNull(job);
        assertTrue(job instanceof RtwJob);
        assertEquals("TestValue", ((RtwJob)job).getConfig().getString("TestKey"));
    }

    @Test
    public void testLegacyJobLoading() throws ConfigurationException {
        Config cfg = new Config();
        Config subCfg = new Config();
        subCfg.put("TestKey", "TestValue");
        cfg.put("RtwJob", subCfg);

        assertThrows(ConfigurationException.class, () -> {
            JobLoader.loadJob(cfg);
        });
    }
}
