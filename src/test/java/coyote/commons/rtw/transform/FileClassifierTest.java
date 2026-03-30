package coyote.commons.rtw.transform;

import coyote.commons.FileUtil;
import coyote.commons.cfg.Config;
import coyote.commons.dataframe.DataFrame;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileClassifierTest {

    private static File testDir;

    @BeforeAll
    public static void setUp() throws IOException {
        testDir = new File("test_classifier_dir");
        if (!testDir.exists()) {
            testDir.mkdirs();
        }
    }

    @AfterAll
    public static void tearDown() {
        if (testDir != null && testDir.exists()) {
            FileUtil.deleteDirectory(testDir);
        }
    }

    @Test
    public void testFileClassification() throws Exception {
        // Create a test file
        File jpgFile = new File(testDir, "test1.jpg");
        jpgFile.createNewFile();
        File jpgSidecar = new File(testDir, "test1.txt");
        jpgSidecar.createNewFile();
        
        File mp4File = new File(testDir, "video1.mp4");
        mp4File.createNewFile();
        File mp4Sidecar = new File(testDir, "video1.log");
        mp4Sidecar.createNewFile();

        // Configure the transform
        Config cfg = new Config();
        Config map = new Config();
        map.add("jpg", "image");
        map.add("mp4", "video");
        cfg.add("map", map);

        FileClassifier classifier = new FileClassifier();
        classifier.setConfiguration(cfg);

        // Test JPG move
        DataFrame frame1 = new DataFrame().set("filename", jpgFile.getAbsolutePath());
        DataFrame result1 = classifier.process(frame1);

        assertNotNull(result1);
        String newJpgPath = result1.getAsString("filename");
        File newJpgFile = new File(newJpgPath);
        
        assertTrue(newJpgFile.exists());
        assertEquals("image", newJpgFile.getParentFile().getName());
        assertTrue(new File(newJpgFile.getParentFile(), "test1.txt").exists());
        assertFalse(jpgFile.exists());
        assertFalse(jpgSidecar.exists());

        // Test MP4 move
        DataFrame frame2 = new DataFrame().set("filename", mp4File.getAbsolutePath());
        DataFrame result2 = classifier.process(frame2);

        assertNotNull(result2);
        String newMp4Path = result2.getAsString("filename");
        File newMp4File = new File(newMp4Path);

        assertTrue(newMp4File.exists());
        assertEquals("video", newMp4File.getParentFile().getName());
        assertTrue(new File(newMp4File.getParentFile(), "video1.log").exists());
        assertFalse(mp4File.exists());
        assertFalse(mp4Sidecar.exists());
    }
    
    @Test
    public void testNoExtension() throws Exception {
        File noExtFile = new File(testDir, "noextension");
        noExtFile.createNewFile();
        
        Config cfg = new Config();
        Config map = new Config();
        map.add("jpg", "image");
        cfg.add("map", map);
        
        FileClassifier classifier = new FileClassifier();
        classifier.setConfiguration(cfg);
        
        DataFrame frame = new DataFrame().set("filename", noExtFile.getAbsolutePath());
        DataFrame result = classifier.process(frame);
        
        assertNotNull(result);
        assertEquals(noExtFile.getAbsolutePath(), result.getAsString("filename"));
        assertTrue(noExtFile.exists());
    }

    @Test
    public void testExtensionNotFound() throws Exception {
        File unknownFile = new File(testDir, "unknown.pdf");
        unknownFile.createNewFile();
        
        Config cfg = new Config();
        Config map = new Config();
        map.add("jpg", "image");
        cfg.add("map", map);
        
        FileClassifier classifier = new FileClassifier();
        classifier.setConfiguration(cfg);
        
        DataFrame frame = new DataFrame().set("filename", unknownFile.getAbsolutePath());
        DataFrame result = classifier.process(frame);
        
        assertNotNull(result);
        assertEquals(unknownFile.getAbsolutePath(), result.getAsString("filename"));
        assertTrue(unknownFile.exists());
    }
}
