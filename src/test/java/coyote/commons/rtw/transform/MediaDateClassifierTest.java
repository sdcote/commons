package coyote.commons.rtw.transform;

import coyote.commons.dataframe.DataFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class MediaDateClassifierTest {

    @TempDir
    Path tempDir;

    private File testFile;
    private String expectedDateDir;

    @BeforeEach
    public void setUp() throws IOException {
        testFile = new File(tempDir.toFile(), "test.jpg");
        assertTrue(testFile.createNewFile());
        
        long lastModified = testFile.lastModified();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        expectedDateDir = sdf.format(new Date(lastModified));
    }

    @Test
    public void testFallbackToFileDate() throws Exception {
        MediaDateClassifier classifier = new MediaDateClassifier();
        // Since it's an empty file, metadata extraction will fail and it should fallback to file date
        DataFrame frame = new DataFrame().set("filename", testFile.getAbsolutePath());

        DataFrame result = classifier.process(frame);

        assertNotNull(result);
        String newPath = result.getAsString("filename");
        File movedFile = new File(newPath);

        assertEquals(expectedDateDir, movedFile.getParentFile().getName());
        assertTrue(movedFile.exists(), "Moved file should exist");
        assertFalse(testFile.exists(), "Original file should be gone");
    }

    @Test
    public void testNonExistentFile() throws Exception {
        MediaDateClassifier classifier = new MediaDateClassifier();
        String missingPath = new File(tempDir.toFile(), "missing.jpg").getAbsolutePath();
        DataFrame frame = new DataFrame().set("filename", missingPath);

        DataFrame result = classifier.process(frame);

        assertNotNull(result);
        assertEquals(missingPath, result.getAsString("filename"));
    }
}
