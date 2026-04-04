/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;

import java.io.File;
import java.io.IOException;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TaskException;

/**
 * Recursively copies the contents of the source directory into the target directory.
 *
 * <p>The structure of the source directory is completely represented in the target directory.</p>
 *
 * <p>If the configuration contains {@code move=true} then the source file is moved to the target
 * directory instead of copied. When moving the source directory to the target directory, the goal
 * is to remove the contents of the source directory. The default value is {@code move=false},
 * resulting in copying the file by default.</p>
 *
 * <p>If a file already exists in the target directory, the {@code overwrite} configuration setting
 * determines whether it is overwritten. If {@code overwrite=true} then the file is overwritten.
 * If {@code overwrite=false}, the file is not overwritten, and the file remains in the source
 * directory structure. The default mode of operation is not to overwrite the file ({@code overwrite=false}).</p>
 *
 * <p>If the file exists, and {@code overwrite} is {@code false}, the task checks the {@code rename}
 * configuration option. If {@code rename=true}, the task attempts to rename the target file by
 * adding "(1)" to the base name of the file. The base name is the portion of the file preceding
 * the file extension. The extension of the file must remain unchanged in all cases. The sequence
 * number must increase so that the target file is never overwritten. The task must increment the
 * sequence number until a unique target file is generated.</p>
 *
 * <p>Multiple runs of this task should never result in the loss of data in the target directory
 * unless overwrite is set to true.</p>
 */
public class MergeDirectory extends AbstractFileTask {

  /**
   * @return the source directory for the merge operation.
   */
  public String getSource() {
    if (configuration.containsIgnoreCase(ConfigTag.SOURCE)) {
      return configuration.getString(ConfigTag.SOURCE);
    } else if (configuration.containsIgnoreCase(ConfigTag.FROMDIR)) {
      return configuration.getString(ConfigTag.FROMDIR);
    }
    return null;
  }

  /**
   * @return the target directory for the merge operation.
   */
  public String getTarget() {
    if (configuration.containsIgnoreCase(ConfigTag.TARGET)) {
      return configuration.getString(ConfigTag.TARGET);
    } else if (configuration.containsIgnoreCase(ConfigTag.TODIR)) {
      return configuration.getString(ConfigTag.TODIR);
    }
    return null;
  }

  /**
   * @return true if the source file is to be moved to the target directory, false if it is to be copied.
   */
  public boolean isMove() {
    if (configuration.containsIgnoreCase(ConfigTag.MOVE)) {
      return getBoolean(ConfigTag.MOVE);
    }
    return false;
  }

  /**
   * @return true if the target file is to be overwritten if it exists, false otherwise.
   */
  public boolean isOverwrite() {
    if (configuration.containsIgnoreCase(ConfigTag.OVERWRITE)) {
      return getBoolean(ConfigTag.OVERWRITE);
    }
    return false;
  }

  /**
   * @return true if the target file is to be renamed if it exists and overwrite is false, false otherwise.
   */
  public boolean isRename() {
    if (configuration.containsIgnoreCase(ConfigTag.RENAME)) {
      return getBoolean(ConfigTag.RENAME);
    }
    return false;
  }

  /**
   * @see coyote.commons.rtw.task.AbstractTransformTask#performTask()
   */
  @Override
  protected void performTask() throws TaskException {
    String sourceDirName = getSource();
    String targetDirName = getTarget();

    if (StringUtil.isBlank(sourceDirName)) {
      throw new TaskException("Source directory must be specified");
    }
    if (StringUtil.isBlank(targetDirName)) {
      throw new TaskException("Target directory must be specified");
    }

    File sourceDir = getAbsoluteFile(sourceDirName);
    File targetDir = getAbsoluteFile(targetDirName);

    if (!sourceDir.exists()) {
      throw new TaskException("Source directory does not exist: " + sourceDir.getAbsolutePath());
    }
    if (!sourceDir.isDirectory()) {
      throw new TaskException("Source is not a directory: " + sourceDir.getAbsolutePath());
    }

    try {
      merge(sourceDir, targetDir);
    } catch (IOException e) {
      throw new TaskException("Error merging directories: " + e.getMessage(), e);
    }
  }

  /**
   * Recursively merge the source directory into the target directory.
   *
   * @param source the source directory to merge from
   * @param target the target directory to merge into
   * @throws IOException if there are problems with any of the file operations
   */
  private void merge(File source, File target) throws IOException {
    if (!target.exists()) {
      if (!target.mkdirs()) {
        throw new IOException("Could not create target directory: " + target.getAbsolutePath());
      }
    }

    File[] files = source.listFiles();
    if (files != null) {
      for (File file : files) {
        File targetFile = new File(target, file.getName());
        if (file.isDirectory()) {
          merge(file, targetFile);
          if (isMove()) {
            File[] remainingFiles = file.listFiles();
            if (remainingFiles == null || remainingFiles.length == 0) {
              FileUtil.deleteDirectory(file);
            }
          }
        } else {
          processFile(file, targetFile);
        }
      }
    }
  }

  /**
   * Process an individual file by copying or moving it to the target location.
   *
   * @param source the source file to process
   * @param target the target file to process
   * @throws IOException if there are problems with any of the file operations
   */
  private void processFile(File source, File target) throws IOException {
    boolean overwrite = isOverwrite();
    boolean rename = isRename();
    boolean move = isMove();

    if (target.exists()) {
      if (overwrite) {
        performOperation(source, target, move);
      } else if (rename) {
        File uniqueTarget = getUniqueFile(target);
        performOperation(source, uniqueTarget, move);
      } else {
        if (move) {
          Log.warn("Could not move file " + source.getAbsolutePath() + " to " + target.getAbsolutePath() + " because it would overwrite a target file and rename is false.");
        }
      }
    } else {
      performOperation(source, target, move);
    }
  }

  /**
   * Perform the actual copy or move operation.
   *
   * @param source the source file
   * @param target the target file
   * @param move   true to move the file, false to copy it
   * @throws IOException if there are problems with any of the file operations
   */
  private void performOperation(File source, File target, boolean move) throws IOException {
    if (move) {
      FileUtil.moveFile(source, target);
    } else {
      FileUtil.copyFile(source, target);
    }
  }

  /**
   * Generate a unique file name by incrementing a sequence number.
   *
   * @param file the target file that already exists
   * @return a unique file that does not exist in the target directory
   * @throws IOException if there are problems with any of the file operations
   */
  private File getUniqueFile(File file) throws IOException {
    String parent = file.getParent();
    String name = file.getName();
    String base = FileUtil.getBase(name);
    String ext = FileUtil.getExtension(name);

    // Remove any existing sequence token like (1) from the base name
    if (base.endsWith(")")) {
      int openParen = base.lastIndexOf('(');
      if (openParen != -1) {
        String sequence = base.substring(openParen + 1, base.length() - 1);
        try {
          Integer.parseInt(sequence);
          base = base.substring(0, openParen);
        } catch (NumberFormatException e) {
          // not a sequence token, ignore
        }
      }
    }

    int count = 1;
    File uniqueFile;
    do {
      String newName = base + "(" + count + ")" + (StringUtil.isBlank(ext) ? "" : "." + ext);
      uniqueFile = new File(parent, newName);
      count++;
    } while (uniqueFile.exists());

    return uniqueFile;
  }

}
