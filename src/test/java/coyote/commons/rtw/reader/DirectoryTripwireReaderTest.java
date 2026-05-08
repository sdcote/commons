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

import static org.junit.jupiter.api.Assertions.*;

public class DirectoryTripwireReaderTest {
    private File testDir;

    @BeforeEach
    public void setUp() throws IOException {
        testDir = Files.createTempDirectory("dtrt").toFile();
    }

    @AfterEach
    public void tearDown() {
        FileUtil.deleteDirectory(testDir);
    }

    @Test
    public void testModificationDetection() throws Exception {
        DataFrame cfg = new DataFrame()
                .set(ConfigTag.DIRECTORY, testDir.getAbsolutePath())
                .set(ConfigTag.SECONDS, 1);
        Config config = new Config(cfg);

        DirectoryTripwireReader reader = new DirectoryTripwireReader();
        reader.setConfiguration(config);
        TransformContext context = new TransformContext();
        reader.open(context);
        assertFalse(context.isInError());

        TransactionContext txnContext = new TransactionContext(context);

        // 1. Test Creation
        File file = new File(testDir, "trip.txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("initial content".getBytes());
        }

        DataFrame frame = reader.read(txnContext);
        assertNotNull(frame);
        assertEquals(file.getAbsolutePath(), frame.getAsString(DirectoryTripwireReader.FILENAME_FIELD));
        assertEquals(DirectoryTripwireReader.CREATED, frame.getAsString(DirectoryTripwireReader.CHANGE_FIELD));
        assertNotNull(frame.get(DirectoryTripwireReader.CURRENT_CHECKSUM));
        assertEquals(file.length(), frame.getAsLong(DirectoryTripwireReader.CURRENT_SIZE));

        String initialChecksum = frame.getAsString(DirectoryTripwireReader.CURRENT_CHECKSUM);

        // 2. Test Modification (size change)
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.write(" appended".getBytes());
        }
        
        frame = reader.read(txnContext);
        assertNotNull(frame);
        assertEquals(file.getAbsolutePath(), frame.getAsString(DirectoryTripwireReader.FILENAME_FIELD));
        assertEquals(DirectoryTripwireReader.MODIFIED, frame.getAsString(DirectoryTripwireReader.CHANGE_FIELD));
        assertEquals(initialChecksum, frame.getAsString(DirectoryTripwireReader.PREVIOUS_CHECKSUM));
        assertNotEquals(initialChecksum, frame.getAsString(DirectoryTripwireReader.CURRENT_CHECKSUM));
        
        String secondChecksum = frame.getAsString(DirectoryTripwireReader.CURRENT_CHECKSUM);

        // 3. Test Modification (same size, different content - rare but possible if we overwrite)
        // For simplicity, let's just change content and keep size the same
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("changed content!!".getBytes()); // "initial content appended" is 24 bytes. "changed content!!" is 17. 
            // Let's make it exactly 17 bytes to match "initial content". 
        }
        // "initial content" is 15 bytes.
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("123456789012345".getBytes());
        }
        
        frame = reader.read(txnContext);
        assertNotNull(frame);
        assertEquals(DirectoryTripwireReader.MODIFIED, frame.getAsString(DirectoryTripwireReader.CHANGE_FIELD));
        assertNotEquals(secondChecksum, frame.getAsString(DirectoryTripwireReader.CURRENT_CHECKSUM));

        // 4. Test Deletion
        assertTrue(file.delete());
        frame = reader.read(txnContext);
        assertNotNull(frame);
        assertEquals(DirectoryTripwireReader.DELETED, frame.getAsString(DirectoryTripwireReader.CHANGE_FIELD));
    }
}
