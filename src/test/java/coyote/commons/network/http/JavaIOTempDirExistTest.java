package coyote.commons.network.http;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class JavaIOTempDirExistTest {

    //@Ignore
    public void testJavaIoTempDefault() throws Exception {
        final String tmpdir = System.getProperty("java.io.tmpdir");
        final DefaultCacheManager manager = new DefaultCacheManager();
        final DefaultCacheFile tempFile = (DefaultCacheFile) manager.createCacheFile("xx");
        final File tempFileBackRef = new File(tempFile.getName());
        assertEquals(tempFileBackRef.getParentFile(), new File(tmpdir));

        // force an exception
        tempFileBackRef.delete();
        Exception e = null;
        try {
            tempFile.delete();
        } catch (final Exception ex) {
            e = ex;
        }
        assertNotNull(e);
        manager.clear();
    }


    @Test
    public void testJavaIoTempSpecific() throws IOException {
        final String tmpdir = System.getProperty("java.io.tmpdir");
        try {
            final String tempFileName = UUID.randomUUID().toString();
            final File newDir = new File("target", tempFileName);
            System.setProperty("java.io.tmpdir", newDir.getAbsolutePath());
            assertFalse(newDir.exists());
            new DefaultCacheManager();
            assertTrue(newDir.exists());
            newDir.delete();
        } finally {
            System.setProperty("java.io.tmpdir", tmpdir);
        }

    }

}
