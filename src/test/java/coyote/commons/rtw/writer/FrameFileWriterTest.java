package coyote.commons.rtw.writer;

import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.context.TransformContext;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class FrameFileWriterTest {

    private final TransformContext transformContext = new TransformContext();

    @Test
    void openTest() {

        try (FrameFileWriter subject = new FrameFileWriter()) {
            subject.getConfiguration().set(ConfigTag.TARGET, "testfile.txt");
            subject.open(transformContext);
            assertNotNull(subject.getPrintwriter());
            File targetFile = subject.getTargetFile();
            assertNotNull(targetFile);
            subject.reset();

            subject.getConfiguration().set(ConfigTag.TARGET, "file:///anotherfile.txt");
            subject.open(transformContext);
            assertNotNull(subject.getPrintwriter());
            targetFile = subject.getTargetFile();
            assertNotNull(targetFile);
        } catch (Exception e) {
            fail(e);
        }

    }
}