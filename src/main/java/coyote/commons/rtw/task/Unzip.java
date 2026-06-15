/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;

import java.io.File;
import java.io.IOException;

import coyote.commons.StringUtil;
import coyote.commons.ZipUtil;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TaskException;


/**
 * Unzip the given file.
 */
public class Unzip extends AbstractFileTask {

  /**
   *
   */
  @Override
  protected void performTask() throws TaskException {
    final String source = getSourceOrFile();
    if (StringUtil.isNotBlank(source)) {
      Log.debug(String.format("%s using a filename of '%s'", getClass().getSimpleName(), source));
      final File file = getExistingFile(source);
      Log.debug(String.format("%s using absolute filename of '%s'", getClass().getSimpleName(), file.getAbsolutePath()));

      if (file.exists()) {
        if (file.canRead()) {
          if (file.length() > 0) {
            try {
              ZipUtil.unzip(file, getDirectory());
            } catch (IOException e) {
              throw new TaskException("Could not unzip file: " + e.getMessage(), e);
            }
          } else {
            Log.warn(String.format("%s did not read any data from %s - empty file (%s)", getClass().getSimpleName(), source, file.getAbsolutePath()));
          }
        } else {
          final String msg = String.format("%s failed: File cannot be read %s (%s)", getClass().getSimpleName(), source, file.getAbsolutePath());
          if (haltOnError) {
            throw new TaskException(msg);
          } else {
            Log.error(msg);
          }
        }
      } else {
        final String msg = String.format("%s failed: File does not exist: %s (%s)", getClass().getSimpleName(), source, file.getAbsolutePath());
        if (haltOnError) {
          throw new TaskException(msg);
        } else {
          Log.error(msg);
        }
      }
    } else {
      final String msg = String.format( "%s failed: No data in %s configuration attribute", getClass().getSimpleName(), ConfigTag.SOURCE);
      if (haltOnError) {
        throw new TaskException(msg);
      } else {
        Log.error(msg);
      }
    }
  }




  /**
   * @return the target directory for the unzipped files
   */
  private File getDirectory() {
    String directory = getString(ConfigTag.DIRECTORY);
    if (StringUtil.isNotBlank(directory)) {
      File retval = new File(directory);
      if (!retval.isAbsolute()) {
        retval = new File(getJobDir(), directory);
      }
      return retval;
    } else {
      return new File(getJobDir());
    }
  }

}
