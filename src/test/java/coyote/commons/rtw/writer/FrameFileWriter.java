package coyote.commons.rtw.writer;

import coyote.commons.rtw.ConfigurableComponent;
import coyote.commons.rtw.FrameWriter;

import java.io.File;

/**
 * This is a test implementation of a AbstractFrameFileWriter that tests the base methods in the abstract class
 */
public class FrameFileWriter  extends AbstractFrameFileWriter implements FrameWriter, ConfigurableComponent {


    public File getTargetFile() {
        return targetFile;
    }

    public void reset() {
        targetFile = null;
        printwriter = null;
    }
}
