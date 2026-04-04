package cookbook;

import coyote.commons.FileUtil;

import java.io.File;
import java.io.IOException;

public class DirectoryMerge {
    public static void main(String[] args) throws IOException {
        File source = new File("/Volumes/PRO-G40/Media/Pictures2");
        File target = new File("/Volumes/PRO-G40/Media/Pictures1");

        FileUtil.mergeDirectory(source,target,false);
    }
}
