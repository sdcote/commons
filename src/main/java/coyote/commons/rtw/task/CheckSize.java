/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;

import java.io.File;

import coyote.commons.StringUtil;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TaskException;


/**
 * Check the size of the given file.
 */
public class CheckSize extends AbstractFileTask {

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
        String attrName = getConfiguration().getString(ConfigTag.CONTEXT);

        String value = getContext().getAsString(attrName);
        Log.info(String.format("Size should be %s", value));
        long size;

        try {
          size = Long.parseLong(value);
        } catch (NumberFormatException e) {
          final String msg = String.format("%s failed: Context attribute %s does not contain a valid numeric (%s)", getClass().getSimpleName(), attrName, value);
          if (haltOnError) {
            throw new TaskException(msg);
          } else {
            Log.error(msg);
            return;
          }
        }

        if (file.length() > 0) {
          if (file.length() == size) {
            Log.info(String.format("File size verified for %s", file.getAbsolutePath()));
          } else {
            final String msg = String.format("File size verification failed for '%s' expecting %d was actually %d", file.getAbsolutePath(), size, file.length());
            if (haltOnError) {
              throw new TaskException(msg);
            } else {
              Log.error(msg);
              return;
            }
          }
        } else {
          Log.warn(String.format("%s did not read any data from %s - empty file (%s)", getClass().getSimpleName(), source, file.getAbsolutePath()));
        }
      } else {
        final String msg = String.format("%s failed: File does not exist: %s (%s)", getClass().getSimpleName(), source, file.getAbsolutePath());
        if (haltOnError) {
          throw new TaskException(msg);
        } else {
          Log.error(msg);
          return;
        }
      }
    } else {
      final String msg = String.format("%s failed: No data in %s configuration attribute", getClass().getSimpleName(), ConfigTag.SOURCE);
      if (haltOnError) {
        throw new TaskException(msg);
      } else {
        Log.error(msg);
        return;
      }
    }
  }

}
