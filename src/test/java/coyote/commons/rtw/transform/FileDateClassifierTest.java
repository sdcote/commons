package coyote.commons.rtw.transform;

import coyote.commons.FileUtil;
import coyote.commons.dataframe.DataFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class FileDateClassifierTest {

    @TempDir
    Path tempDir;

    private File testFile;
    private File sidecarFile;
    private String expectedDateDir;

    @BeforeEach
    public void setUp() throws IOException {
        testFile = new File(tempDir.toFile(), "test.txt");
        assertTrue(testFile.createNewFile());

        sidecarFile = new File(tempDir.toFile(), "test.log");
        assertTrue(sidecarFile.createNewFile());

        long lastModified = testFile.lastModified();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        expectedDateDir = sdf.format(new Date(lastModified));
    }

    @Test
    public void testDateClassification() throws Exception {
        FileDateClassifier classifier = new FileDateClassifier();
        DataFrame frame = new DataFrame().set("filename", testFile.getAbsolutePath());

        DataFrame result = classifier.process(frame);

        assertNotNull(result);
        String newPath = result.getAsString("filename");
        File movedFile = new File(newPath);

        assertEquals(expectedDateDir, movedFile.getParentFile().getName());
        assertTrue(movedFile.exists(), "Moved file should exist");

        File movedSidecar = new File(movedFile.getParentFile(), "test.log");
        assertTrue(movedSidecar.exists(), "Sidecar file should also be moved");

        assertFalse(testFile.exists(), "Original file should be gone");
        assertFalse(sidecarFile.exists(), "Original sidecar should be gone");

        // Manually move files back to original location so @TempDir can delete them if it wants
        // but the error was that they couldn't be deleted from the SUBDIRECTORY
        // Actually, the error was: C:\Users\scote\AppData\Local\Temp\junit-...\2026-03-30\test.log: The process cannot access the file
        // This usually means a stream was left open.
    }

    @Test
    public void testFileNotFound() throws Exception {
        FileDateClassifier classifier = new FileDateClassifier();
        String missingPath = new File(tempDir.toFile(), "missing.txt").getAbsolutePath();
        DataFrame frame = new DataFrame().set("filename", missingPath);

        DataFrame result = classifier.process(frame);

        assertNotNull(result);
        assertEquals(missingPath, result.getAsString("filename"));
    }

    @Test
    public void testNoFilenameField() throws Exception {
        FileDateClassifier classifier = new FileDateClassifier();
        DataFrame frame = new DataFrame().set("something", "else");

        DataFrame result = classifier.process(frame);

        assertNotNull(result);
        assertNull(result.getAsString("filename"));
    }
}
