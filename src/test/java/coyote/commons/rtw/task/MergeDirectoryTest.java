package coyote.commons.rtw.task;

import coyote.commons.FileUtil;
import coyote.commons.cfg.Config;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TaskException;
import coyote.commons.rtw.context.TransformContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the MergeDirectory task.
 */
public class MergeDirectoryTest {
  private File testDir;
  private File sourceDir;
  private File targetDir;

  @BeforeEach
  public void setUp() throws IOException {
    testDir = new File("merge_test_dir");
    FileUtil.makeDirectory(testDir);
    sourceDir = new File(testDir, "source");
    targetDir = new File(testDir, "target");
    FileUtil.makeDirectory(sourceDir);
    FileUtil.makeDirectory(targetDir);
  }

  @AfterEach
  public void tearDown() {
    FileUtil.deleteDirectory(testDir);
  }

  @Test
  public void testBasicCopy() throws Exception {
    File file1 = new File(sourceDir, "file1.txt");
    FileUtil.stringToFile("content1", file1.getAbsolutePath());
    File subDir = new File(sourceDir, "sub");
    FileUtil.makeDirectory(subDir);
    File file2 = new File(subDir, "file2.txt");
    FileUtil.stringToFile("content2", file2.getAbsolutePath());

    MergeDirectory task = new MergeDirectory();
    Config cfg = new Config();
    cfg.set(ConfigTag.SOURCE, sourceDir.getAbsolutePath());
    cfg.set(ConfigTag.TARGET, targetDir.getAbsolutePath());
    task.setConfiguration(cfg);
    task.open(new TransformContext());
    task.execute();

    assertTrue(new File(targetDir, "file1.txt").exists());
    assertTrue(new File(targetDir, "sub/file2.txt").exists());
    assertEquals("content1", FileUtil.fileToString(new File(targetDir, "file1.txt")));
    assertTrue(file1.exists()); // should still exist (copy)
  }

  @Test
  public void testMove() throws Exception {
    File file1 = new File(sourceDir, "file1.txt");
    FileUtil.stringToFile("content1", file1.getAbsolutePath());

    MergeDirectory task = new MergeDirectory();
    Config cfg = new Config();
    cfg.set(ConfigTag.SOURCE, sourceDir.getAbsolutePath());
    cfg.set(ConfigTag.TARGET, targetDir.getAbsolutePath());
    cfg.set(ConfigTag.MOVE, true);
    task.setConfiguration(cfg);
    task.open(new TransformContext());
    task.execute();

    assertTrue(new File(targetDir, "file1.txt").exists());
    assertFalse(file1.exists());
    // sourceDir might still exist but should be empty if all files were moved
    File[] files = sourceDir.listFiles();
    assertTrue(files == null || files.length == 0);
  }

  @Test
  public void testOverwriteTrue() throws Exception {
    File file1 = new File(sourceDir, "file1.txt");
    FileUtil.stringToFile("new content", file1.getAbsolutePath());
    File targetFile1 = new File(targetDir, "file1.txt");
    FileUtil.stringToFile("old content", targetFile1.getAbsolutePath());

    MergeDirectory task = new MergeDirectory();
    Config cfg = new Config();
    cfg.set(ConfigTag.SOURCE, sourceDir.getAbsolutePath());
    cfg.set(ConfigTag.TARGET, targetDir.getAbsolutePath());
    cfg.set(ConfigTag.OVERWRITE, true);
    task.setConfiguration(cfg);
    task.open(new TransformContext());
    task.execute();

    assertEquals("new content", FileUtil.fileToString(targetFile1));
  }

  @Test
  public void testOverwriteFalse() throws Exception {
    File file1 = new File(sourceDir, "file1.txt");
    FileUtil.stringToFile("new content", file1.getAbsolutePath());
    File targetFile1 = new File(targetDir, "file1.txt");
    FileUtil.stringToFile("old content", targetFile1.getAbsolutePath());

    MergeDirectory task = new MergeDirectory();
    Config cfg = new Config();
    cfg.set(ConfigTag.SOURCE, sourceDir.getAbsolutePath());
    cfg.set(ConfigTag.TARGET, targetDir.getAbsolutePath());
    cfg.set(ConfigTag.OVERWRITE, false);
    task.setConfiguration(cfg);
    task.open(new TransformContext());
    task.execute();

    assertEquals("old content", FileUtil.fileToString(targetFile1));
  }

  @Test
  public void testRename() throws Exception {
    File file1 = new File(sourceDir, "file1.txt");
    FileUtil.stringToFile("new content", file1.getAbsolutePath());
    File targetFile1 = new File(targetDir, "file1.txt");
    FileUtil.stringToFile("old content", targetFile1.getAbsolutePath());

    MergeDirectory task = new MergeDirectory();
    Config cfg = new Config();
    cfg.set(ConfigTag.SOURCE, sourceDir.getAbsolutePath());
    cfg.set(ConfigTag.TARGET, targetDir.getAbsolutePath());
    cfg.set(ConfigTag.OVERWRITE, false);
    cfg.set(ConfigTag.RENAME, true);
    task.setConfiguration(cfg);
    task.open(new TransformContext());
    task.execute();

    assertTrue(targetFile1.exists());
    assertEquals("old content", FileUtil.fileToString(targetFile1));
    File renamedFile = new File(targetDir, "file1(1).txt");
    assertTrue(renamedFile.exists());
    assertEquals("new content", FileUtil.fileToString(renamedFile));
  }

  @Test
  public void testRenameNestedToken() throws Exception {
    File file1 = new File(sourceDir, "file1.txt");
    FileUtil.stringToFile("new content", file1.getAbsolutePath());
    FileUtil.stringToFile("content 0", new File(targetDir, "file1.txt").getAbsolutePath());
    FileUtil.stringToFile("content 1", new File(targetDir, "file1(1).txt").getAbsolutePath());

    MergeDirectory task = new MergeDirectory();
    Config cfg = new Config();
    cfg.set(ConfigTag.SOURCE, sourceDir.getAbsolutePath());
    cfg.set(ConfigTag.TARGET, targetDir.getAbsolutePath());
    cfg.set(ConfigTag.OVERWRITE, false);
    cfg.set(ConfigTag.RENAME, true);
    task.setConfiguration(cfg);
    task.open(new TransformContext());
    task.execute();

    assertTrue(new File(targetDir, "file1(2).txt").exists());
    assertFalse(new File(targetDir, "file1(1)(1).txt").exists());
  }

  @Test
  public void testMoveNoOverwriteNoRename() throws Exception {
      File file1 = new File(sourceDir, "file1.txt");
      FileUtil.stringToFile("new content", file1.getAbsolutePath());
      File targetFile1 = new File(targetDir, "file1.txt");
      FileUtil.stringToFile("old content", targetFile1.getAbsolutePath());

      MergeDirectory task = new MergeDirectory();
      Config cfg = new Config();
      cfg.set(ConfigTag.SOURCE, sourceDir.getAbsolutePath());
      cfg.set(ConfigTag.TARGET, targetDir.getAbsolutePath());
      cfg.set(ConfigTag.MOVE, true);
      cfg.set(ConfigTag.OVERWRITE, false);
      cfg.set(ConfigTag.RENAME, false);
      task.setConfiguration(cfg);
      task.open(new TransformContext());
      task.execute();

      assertTrue(file1.exists()); // Should NOT be moved because it would overwrite
      assertEquals("old content", FileUtil.fileToString(targetFile1));
  }
}
