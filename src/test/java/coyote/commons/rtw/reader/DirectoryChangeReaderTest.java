package coyote.commons.rtw.reader;

import coyote.commons.FileUtil;
import coyote.commons.cfg.Config;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.context.TransactionContext;
import coyote.commons.rtw.context.TransformContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DirectoryChangeReaderTest {
    private File testDir;

    @BeforeEach
    public void setUp() throws IOException {
        testDir = Files.createTempDirectory("dcrt").toFile();
    }

    @AfterEach
    public void tearDown() {
        FileUtil.deleteDirectory(testDir);
    }

    @Test
    public void testIntervalTag() throws Exception {
        DataFrame cfg = new DataFrame()
                .set(ConfigTag.DIRECTORY, testDir.getAbsolutePath())
                .set(ConfigTag.INTERVAL, 1);
        Config config = new Config(cfg);

        DirectoryChangeReader reader = new DirectoryChangeReader();
        reader.setConfiguration(config);
        TransformContext context = new TransformContext();
        reader.open(context);
        assertFalse(context.isInError());

        TransactionContext txnContext = new TransactionContext(context);

        File newFile = new File(testDir, "interval_test.txt");
        assertTrue(newFile.createNewFile());

        DataFrame frame = reader.read(txnContext);
        assertNotNull(frame);
        assertEquals(newFile.getAbsolutePath(), frame.getAsString(DirectoryChangeReader.FILENAME_FIELD));
    }

    @Test
    public void testDetection() throws Exception {
        DataFrame cfg = new DataFrame()
                .set(ConfigTag.DIRECTORY, testDir.getAbsolutePath())
                .set(ConfigTag.SECONDS, 1);
        Config config = new Config(cfg);

        DirectoryChangeReader reader = new DirectoryChangeReader();
        reader.setConfiguration(config);
        TransformContext context = new TransformContext();
        reader.open(context);
        assertFalse(context.isInError());

        TransactionContext txnContext = new TransactionContext(context);

        // Test Creation
        File newFile = new File(testDir, "test.txt");
        assertTrue(newFile.createNewFile());

        DataFrame frame = reader.read(txnContext);
        assertNotNull(frame);
        assertEquals(newFile.getAbsolutePath(), frame.getAsString(DirectoryChangeReader.FILENAME_FIELD));
        assertEquals(DirectoryChangeReader.CREATED, frame.getAsString(DirectoryChangeReader.CHANGE_FIELD));

        // Test Deletion
        assertTrue(newFile.delete());
        frame = reader.read(txnContext);
        assertNotNull(frame);
        assertEquals(newFile.getAbsolutePath(), frame.getAsString(DirectoryChangeReader.FILENAME_FIELD));
        assertEquals(DirectoryChangeReader.DELETED, frame.getAsString(DirectoryChangeReader.CHANGE_FIELD));

        // Test Modification (Size change)
        assertTrue(newFile.createNewFile());
        frame = reader.read(txnContext); // consume the creation
        
        FileUtil.stringToFile("some data", newFile.getAbsolutePath());
        frame = reader.read(txnContext);
        assertNotNull(frame);
        assertEquals(newFile.getAbsolutePath(), frame.getAsString(DirectoryChangeReader.FILENAME_FIELD));
        assertEquals(DirectoryChangeReader.MODIFIED, frame.getAsString(DirectoryChangeReader.CHANGE_FIELD));
        assertTrue(frame.contains(DirectoryChangeReader.PREVIOUS_SIZE));
        assertTrue(frame.contains(DirectoryChangeReader.CURRENT_SIZE));
        assertEquals(0L, frame.getAsLong(DirectoryChangeReader.PREVIOUS_SIZE));
        assertEquals(9L, frame.getAsLong(DirectoryChangeReader.CURRENT_SIZE));
    }

    @Test
    public void testFiltering() throws Exception {
        DataFrame cfg = new DataFrame()
                .set(ConfigTag.DIRECTORY, testDir.getAbsolutePath())
                .set(ConfigTag.SECONDS, 1);
        cfg.add(ConfigTag.INCLUDE, ".*\\.txt");
        Config config = new Config(cfg);

        DirectoryChangeReader reader = new DirectoryChangeReader();
        reader.setConfiguration(config);
        TransformContext context = new TransformContext();
        reader.open(context);

        // Create a matching file and a non-matching file
        File matchingFile = new File(testDir, "match.txt");
        File nonMatchingFile = new File(testDir, "ignore.log");
        assertTrue(matchingFile.createNewFile());
        assertTrue(nonMatchingFile.createNewFile());

        TransactionContext txnContext = new TransactionContext(context);
        DataFrame frame = reader.read(txnContext);
        assertNotNull(frame);
        assertEquals(matchingFile.getAbsolutePath(), frame.getAsString(DirectoryChangeReader.FILENAME_FIELD));
        
        // The log file should be ignored, so the next read should wait if we didn't have any more changes.
        // But since we want to test quickly, we can't easily test it didn't find it without waiting.
        // We'll create another matching file and see if it's the next one returned.
        File matchingFile2 = new File(testDir, "match2.txt");
        assertTrue(matchingFile2.createNewFile());
        
        frame = reader.read(txnContext);
        assertNotNull(frame);
        assertEquals(matchingFile2.getAbsolutePath(), frame.getAsString(DirectoryChangeReader.FILENAME_FIELD));
    }

    @Test
    public void testDirectoryDeletion() throws Exception {
        DataFrame cfg = new DataFrame()
                .set(ConfigTag.DIRECTORY, testDir.getAbsolutePath())
                .set(ConfigTag.SECONDS, 1);
        Config config = new Config(cfg);

        DirectoryChangeReader reader = new DirectoryChangeReader();
        reader.setConfiguration(config);
        TransformContext context = new TransformContext();
        reader.open(context);

        // Create sub directory with files
        File subDir = new File(testDir, "sub");
        assertTrue(subDir.mkdir());
        File file1 = new File(subDir, "file1.txt");
        File file2 = new File(subDir, "file2.txt");
        assertTrue(file1.createNewFile());
        assertTrue(file2.createNewFile());

        TransactionContext txnContext = new TransactionContext(context);
        
        // Drain creations
        reader.read(txnContext); // sub
        reader.read(txnContext); // file1
        reader.read(txnContext); // file2

        // Delete sub directory with files
        FileUtil.deleteDirectory(subDir);

        // Should detect 3 deletions (2 files and 1 directory)
        List<String> deletedPaths = new ArrayList<>();
        deletedPaths.add(reader.read(txnContext).getAsString(DirectoryChangeReader.FILENAME_FIELD));
        deletedPaths.add(reader.read(txnContext).getAsString(DirectoryChangeReader.FILENAME_FIELD));
        deletedPaths.add(reader.read(txnContext).getAsString(DirectoryChangeReader.FILENAME_FIELD));

        assertTrue(deletedPaths.contains(subDir.getAbsolutePath()));
        assertTrue(deletedPaths.contains(file1.getAbsolutePath()));
        assertTrue(deletedPaths.contains(file2.getAbsolutePath()));
    }
    @Test
    public void testExclusion() throws Exception {
        DataFrame cfg = new DataFrame()
                .set(ConfigTag.DIRECTORY, testDir.getAbsolutePath())
                .set(ConfigTag.SECONDS, 1);
        cfg.add(ConfigTag.EXCLUDE, ".*\\.tmp");
        Config config = new Config(cfg);

        DirectoryChangeReader reader = new DirectoryChangeReader();
        reader.setConfiguration(config);
        TransformContext context = new TransformContext();
        reader.open(context);

        // Create a matching file and an excluded file
        File matchingFile = new File(testDir, "normal.txt");
        File excludedFile = new File(testDir, "temp.tmp");
        assertTrue(matchingFile.createNewFile());
        assertTrue(excludedFile.createNewFile());

        TransactionContext txnContext = new TransactionContext(context);
        DataFrame frame = reader.read(txnContext);
        assertNotNull(frame);
        assertEquals(matchingFile.getAbsolutePath(), frame.getAsString(DirectoryChangeReader.FILENAME_FIELD));

        // Create another matching file to verify the excluded one was skipped
        File matchingFile2 = new File(testDir, "normal2.txt");
        assertTrue(matchingFile2.createNewFile());

        frame = reader.read(txnContext);
        assertNotNull(frame);
        assertEquals(matchingFile2.getAbsolutePath(), frame.getAsString(DirectoryChangeReader.FILENAME_FIELD));
    }

    @Test
    public void testNonRecursive() throws Exception {
        DataFrame cfg = new DataFrame()
                .set(ConfigTag.DIRECTORY, testDir.getAbsolutePath())
                .set(ConfigTag.SECONDS, 1)
                .set(ConfigTag.RECURSE, false);
        Config config = new Config(cfg);

        DirectoryChangeReader reader = new DirectoryChangeReader();
        reader.setConfiguration(config);
        TransformContext context = new TransformContext();
        reader.open(context);

        TransactionContext txnContext = new TransactionContext(context);

        // Create a file in the top level directory - should be detected
        File topLevelFile = new File(testDir, "top.txt");
        assertTrue(topLevelFile.createNewFile());
        DataFrame frame = reader.read(txnContext);
        assertNotNull(frame);
        assertEquals(topLevelFile.getAbsolutePath(), frame.getAsString(DirectoryChangeReader.FILENAME_FIELD));

        // Create a sub directory and a file inside it - neither should be detected
        // Note: According to requirements: "If a directory is added to the monitored directory, it is not included."
        // "If the directory itself is removed or subsequently added, it is included." 
        // Wait, the prompt said: "If a directory is added to the monitored directory, it is not included. If the directory itself is removed or subsequently added, it is included."
        // That seems slightly contradictory if "it is included" refers to the directory.
        // Let's re-read: "If a directory is added to the monitored directory, it is not included." (This means subdirectories are NOT reported as changes when recurse=false).
        // "If the directory itself is removed or subsequently added, it is included." (This refers to the monitored directory? No, "If the directory itself" probably means a directory being added/removed in the top level).
        // Actually, "limit scanning to the specific directory ... only files added or deleted from the directory will be considered."
        // "If a directory is added ... it is not included."
        // "If the directory itself is removed or subsequently added, it is included."
        
        // My implementation: `shouldInclude(entry)` is called for every entry.
        // If entry is a directory and `recurse` is false, it is NOT included because `shouldInclude` returns true but we don't recurse?
        // Wait, `doScan` iterates over entries. 
        // for (File entry : entries) {
        //   if (shouldInclude(entry)) results.add(path);
        //   if (entry.isDirectory() && recursive) doScan(entry, results, true);
        // }
        // If it's a directory and recursive=false, it WILL be added to results if `shouldInclude(entry)` is true.
        // But the requirement says: "If a directory is added to the monitored directory, it is not included."
        // So I need to modify `shouldInclude` or `doScan` to exclude directories if `recurse` is false.
        
        File subDir = new File(testDir, "sub");
        assertTrue(subDir.mkdir());
        File subFile = new File(subDir, "subFile.txt");
        assertTrue(subFile.createNewFile());
        
        // If we add another top level file, we should get that one next, skipping the subDir and subFile.
        File topLevelFile2 = new File(testDir, "top2.txt");
        assertTrue(topLevelFile2.createNewFile());
        
        frame = reader.read(txnContext);
        assertNotNull(frame);
        assertEquals(topLevelFile2.getAbsolutePath(), frame.getAsString(DirectoryChangeReader.FILENAME_FIELD));
    }
}
