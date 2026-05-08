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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DuplicateFileReaderTest {
    private File testDir;

    @BeforeEach
    public void setUp() throws IOException {
        testDir = Files.createTempDirectory("dfrt").toFile();
    }

    @AfterEach
    public void tearDown() {
        FileUtil.deleteDirectory(testDir);
    }

    @Test
    public void testDuplicateDetection() throws Exception {
        // Create duplicate files
        File file1 = new File(testDir, "file1.txt");
        File file2 = new File(testDir, "file2.txt");
        File file3 = new File(testDir, "file3.txt");
        
        String content = "duplicate content";
        Files.write(file1.toPath(), content.getBytes());
        Files.write(file2.toPath(), content.getBytes());
        Files.write(file3.toPath(), "unique content".getBytes());

        DataFrame cfg = new DataFrame()
                .set(ConfigTag.DIRECTORY, testDir.getAbsolutePath());
        Config config = new Config(cfg);

        DuplicateFileReader reader = new DuplicateFileReader();
        reader.setConfiguration(config);
        TransformContext context = new TransformContext();
        reader.open(context);
        assertFalse(context.isInError());

        TransactionContext txnContext = new TransactionContext(context);
        
        // Should return one frame with the duplicate group
        assertFalse(reader.eof());
        DataFrame frame = reader.read(txnContext);
        assertNotNull(frame);
        
        assertEquals(2, frame.getAsInt(DuplicateFileReader.COUNT_FIELD));
        DataFrame dupsFrame = frame.getAsFrame(DuplicateFileReader.DUPLICATES_FIELD);
        assertNotNull(dupsFrame);
        assertEquals(2, dupsFrame.getFieldCount());
        
        boolean found1 = false;
        boolean found2 = false;
        for (int i = 0; i < dupsFrame.getFieldCount(); i++) {
            String path = dupsFrame.getField(i).getStringValue();
            if (path.equals(file1.getAbsolutePath())) found1 = true;
            if (path.equals(file2.getAbsolutePath())) found2 = true;
        }
        assertTrue(found1);
        assertTrue(found2);
        
        assertTrue(reader.eof());
        assertNull(reader.read(txnContext));
    }

    @Test
    public void testGlobPattern() throws Exception {
        File subDir = new File(testDir, "sub");
        subDir.mkdirs();
        
        File file1 = new File(testDir, "match.txt");
        File file2 = new File(subDir, "match.txt");
        File file3 = new File(testDir, "ignore.log");
        
        String content = "matching content";
        Files.write(file1.toPath(), content.getBytes());
        Files.write(file2.toPath(), content.getBytes());
        Files.write(file3.toPath(), content.getBytes());

        // Use glob to only find .txt files
        DataFrame cfg = new DataFrame()
                .set(ConfigTag.DIRECTORY, testDir.getAbsolutePath() + File.separator + "*.txt");
        Config config = new Config(cfg);

        DuplicateFileReader reader = new DuplicateFileReader();
        reader.setConfiguration(config);
        TransformContext context = new TransformContext();
        reader.open(context);
        
        DataFrame frame = reader.read(new TransactionContext(context));
        assertNotNull(frame);
        assertEquals(2, frame.getAsInt(DuplicateFileReader.COUNT_FIELD));
        DataFrame dupsFrame = frame.getAsFrame(DuplicateFileReader.DUPLICATES_FIELD);
        assertEquals(2, dupsFrame.getFieldCount());
        
        boolean found1 = false;
        boolean found2 = false;
        for (int i = 0; i < dupsFrame.getFieldCount(); i++) {
            String path = dupsFrame.getField(i).getStringValue();
            if (path.equals(file1.getAbsolutePath())) found1 = true;
            if (path.equals(file2.getAbsolutePath())) found2 = true;
        }
        assertTrue(found1);
        assertTrue(found2);
    }

    @Test
    public void testRegexPattern() throws Exception {
        File file1 = new File(testDir, "data01.dat");
        File file2 = new File(testDir, "data02.dat");
        
        String content = "regex content";
        Files.write(file1.toPath(), content.getBytes());
        Files.write(file2.toPath(), content.getBytes());

        // Use regex to match data[0-9]+.dat
        DataFrame cfg = new DataFrame()
                .set(ConfigTag.DIRECTORY, testDir.getAbsolutePath() + File.separator + "data[0-9]+\\.dat");
        Config config = new Config(cfg);

        DuplicateFileReader reader = new DuplicateFileReader();
        reader.setConfiguration(config);
        TransformContext context = new TransformContext();
        reader.open(context);
        
        DataFrame frame = reader.read(new TransactionContext(context));
        assertNotNull(frame);
        assertEquals(2, frame.getAsInt(DuplicateFileReader.COUNT_FIELD));
    }
}
