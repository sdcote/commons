package coyote.commons;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class FileMergeTest {
    private File tempDir;
    private File dir1;
    private File dir2;

    @BeforeEach
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("filemerge_test").toFile();
        dir1 = new File(tempDir, "dir1");
        dir2 = new File(tempDir, "dir2");
        dir1.mkdirs();
        dir2.mkdirs();
    }

    @AfterEach
    public void tearDown() {
        FileUtil.deleteDirectory(tempDir);
    }

    @Test
    public void testMergeDirectories() throws IOException {
        // Setup dir1
        File file1_1 = new File(dir1, "file1.txt");
        FileUtil.stringToFile("dir1 file1", file1_1.getAbsolutePath());
        File sub1 = new File(dir1, "sub");
        sub1.mkdirs();
        File fileSub1 = new File(sub1, "sub1.txt");
        FileUtil.stringToFile("dir1 sub1", fileSub1.getAbsolutePath());

        // Setup dir2
        File file2_1 = new File(dir2, "file2.txt");
        FileUtil.stringToFile("dir2 file2", file2_1.getAbsolutePath());
        File file1_overwrite = new File(dir2, "file1.txt");
        FileUtil.stringToFile("dir2 file1 overwrite", file1_overwrite.getAbsolutePath());
        File sub2 = new File(dir2, "sub");
        sub2.mkdirs();
        File fileSub2 = new File(sub2, "sub2.txt");
        FileUtil.stringToFile("dir2 sub2", fileSub2.getAbsolutePath());

        // Merge WITHOUT overwrite
        FileUtil.mergeDirectory(dir2, dir1, false);

        assertTrue(new File(dir1, "file1.txt").exists());
        assertEquals("dir1 file1", FileUtil.fileToString(new File(dir1, "file1.txt"))); // Should NOT be overwritten
        assertTrue(new File(dir1, "file2.txt").exists());
        assertTrue(new File(dir1, "sub/sub1.txt").exists());
        assertTrue(new File(dir1, "sub/sub2.txt").exists());

        // Merge WITH overwrite
        FileUtil.mergeDirectory(dir2, dir1, true);
        assertEquals("dir2 file1 overwrite", FileUtil.fileToString(new File(dir1, "file1.txt"))); // Should be overwritten
    }
}
