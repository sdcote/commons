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

import coyote.commons.cfg.Config;
import coyote.commons.rtw.ConfigTag;

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
    public void testPatternMatching() throws Exception {
        FileDateClassifier classifier = new FileDateClassifier();
        Config config = new Config();
        config.put(ConfigTag.PATTERN, ".*\\.txt");
        classifier.setConfiguration(config);
        classifier.open(null);

        // test.txt should match
        DataFrame frame1 = new DataFrame().set("filename", testFile.getAbsolutePath());
        DataFrame result1 = classifier.process(frame1);
        String newPath1 = result1.getAsString("filename");
        File movedFile1 = new File(newPath1);
        assertTrue(movedFile1.exists(), "Matching file should be moved");
        assertEquals(expectedDateDir, movedFile1.getParentFile().getName());

        // Create another file that doesn't match
        File otherFile = new File(tempDir.toFile(), "other.log");
        assertTrue(otherFile.createNewFile());
        DataFrame frame2 = new DataFrame().set("filename", otherFile.getAbsolutePath());
        DataFrame result2 = classifier.process(frame2);
        assertEquals(otherFile.getAbsolutePath(), result2.getAsString("filename"), "Non-matching file should NOT be moved");
        assertTrue(otherFile.exists(), "Non-matching file should still exist at original location");
    }

    @Test
    public void testMultiplePatternsMatching() throws Exception {
        FileDateClassifier classifier = new FileDateClassifier();
        Config config = new Config();
        DataFrame patterns = new DataFrame();
        patterns.add(ConfigTag.PATTERN, ".*\\.txt");
        patterns.add(ConfigTag.PATTERN, ".*\\.log");
        config.put(ConfigTag.PATTERN, patterns);
        classifier.setConfiguration(config);
        classifier.open(null);

        // test.txt should match
        DataFrame frame1 = new DataFrame().set("filename", testFile.getAbsolutePath());
        DataFrame result1 = classifier.process(frame1);
        String newPath1 = result1.getAsString("filename");
        assertNotEquals(testFile.getAbsolutePath(), newPath1, "File should have been moved");
        assertTrue(new File(newPath1).exists());

        // We MUST re-create sidecarFile because test.txt move might have moved it too!
        // Actually, FileDateClassifier moves ALL files with the same base name.
        // test.txt and test.log have the same base name "test"
        // When test.txt was processed, test.log was ALSO moved.
        File movedSidecar = new File(new File(testFile.getParentFile(), expectedDateDir), "test.log");
        assertTrue(movedSidecar.exists(), "Sidecar should have been moved when test.txt was moved");

        // other.raw should NOT match
        File otherFile = new File(tempDir.toFile(), "other.raw");
        assertTrue(otherFile.createNewFile(), "Failed to create other.raw");
        DataFrame frame3 = new DataFrame().set("filename", otherFile.getAbsolutePath());
        DataFrame result3 = classifier.process(frame3);
        String finalPath = result3.getAsString("filename");
        assertEquals(otherFile.getAbsolutePath(), finalPath, "File should NOT have been moved. Patterns are: " + patterns.toString());
        assertTrue(new File(otherFile.getAbsolutePath()).exists(), "Original file should still exist");
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
