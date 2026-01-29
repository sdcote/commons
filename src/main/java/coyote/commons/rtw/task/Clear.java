/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TaskException;


/**
 * Clear out the contents of a directory or delete it altogether
 * 
 * recurse = also clear out the subdirectories; the directory structure remains intact
 */
public class Clear extends AbstractFileTask {

  /**
   *
   */
  @Override
  protected void performTask() throws TaskException {
    final String directory = getString(ConfigTag.DIRECTORY);
    if (contains(ConfigTag.HALT_ON_ERROR)) {
      setHaltOnError(getBoolean(ConfigTag.HALT_ON_ERROR));
    }

    // get if we should recurse into sub directories when clearing directories
    boolean recurse = true;
    if (contains(ConfigTag.RECURSE)) {
      recurse = getBoolean(ConfigTag.RECURSE);
    }

    if (StringUtil.isNotBlank(directory)) {
      final String dir = resolveArgument(directory);
      Log.info(String.format( "Task.Clearing directory named {%s}", dir));

      try {
        FileUtil.clearDir(dir, true, recurse);
      } catch (final Exception e) {
        if (haltOnError) {
          getContext().setError(String.format("Task.Clearing directory operation '%s' failed: %s", dir, e.getMessage()));
          return;
        }
      }

    } else {
      Log.warn(String.format( "Task.Clear has no {%s} or {%s} argument - nothing to do.", ConfigTag.FILE, ConfigTag.DIRECTORY));
    }

  }

}
