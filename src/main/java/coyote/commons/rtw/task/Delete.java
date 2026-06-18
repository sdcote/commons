/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;

import java.io.File;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TaskException;


/**
 * Delete a file or a directory from the file system.
 */
public class Delete extends AbstractFileTask {

  /**
   *
   */
  @Override
  protected void performTask() throws TaskException {
    final String filename = getString(ConfigTag.FILE);
    final String directory = getString(ConfigTag.DIRECTORY);
    if (contains(ConfigTag.HALT_ON_ERROR)) {
      setHaltOnError(getBoolean(ConfigTag.HALT_ON_ERROR));
    }

    if (StringUtil.isNotBlank(filename)) {
      final String file = resolveArgument(filename);

      File sourceFile = resolveFile(file);
      Log.debug(String.format("Deleting file %s (%s)", file, sourceFile.getAbsolutePath()));

      if (!FileUtil.deleteFile(sourceFile)) {
        String msg = String.format("File deletion error: %s (%s)", file, sourceFile.getAbsolutePath());
        if (haltOnError) {
          throw new TaskException(msg);
        } else {
          Log.error(msg);
          return;
        }
      }

    } else if (StringUtil.isNotBlank(directory)) {
      final String dir = resolveArgument(directory);
      File dirFile = resolveFile(dir);
      Log.debug(String.format("Deleting directory %s (%s)", dir, dirFile.getAbsolutePath()));

      if (dirFile.exists()) {
        if (dirFile.isDirectory()) {
          try {
            FileUtil.deleteDirectory(dirFile);
          } catch (final Exception e) {
            String msg = String.format("Directory deletion error: %s (%s)", dir, dirFile.getAbsolutePath());
            if (haltOnError) {
              throw new TaskException(msg);
            } else {
              Log.error(msg);
              return;
            }
          }
        } else {
          String msg = String.format("Directory specified but file found: %s (%s)", dir, dirFile.getAbsolutePath());
          if (haltOnError) {
            throw new TaskException(msg);
          } else {
            Log.error(msg);
            return;
          }
        }
      }

    } else {
      Log.warn(String.format("Delete task configuration error: missing %s or %s", ConfigTag.FILE, ConfigTag.DIRECTORY));
    }

  }

}
