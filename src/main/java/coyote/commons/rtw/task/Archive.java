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
 * This task generates a compressed archive of a file or directory.
 * 
 * <p>The common use case for this task is to archive all the artifacts in a 
 * job directory so it can be sent as a single file to some destination either 
 * by file transfer more messaging tools.
 * 
 * <pre>"PostProcess": {
 *   "Archive" : { "directory": "wrkdir", "target": "wrkdir.zip", "enabled": false  },
 * }</pre>
 * 
 * Goals:
 * Archive a directory to a zip file.
 * Archive a file to a zip file
 * Archive a pattern of files to a zip file (like Copy task)
 * Add a directory to an existing zip
 * Add a file to an existing zip
 * 
 */
public class Archive extends AbstractFileTask {

  private static final String SUFFIX = ".zip";




  /**
   *
   */
  @Override
  protected void performTask() throws TaskException {

    final String source = getString(ConfigTag.SOURCE);
    final String target = getString(ConfigTag.TARGET);

    //final String pattern = getString(ConfigTag.PATTERN);
    //boolean overwrite = getBoolean(ConfigTag.OVERWRITE);

    if (StringUtil.isNotBlank(source)) {
      File sourceFile = new File(source);
      if (sourceFile.exists()) {
        File targetFile;

        if (StringUtil.isNotBlank(target)) {
          targetFile = new File(target);
        } else {
          targetFile = new File(sourceFile.getAbsolutePath() + SUFFIX);
        }

        Log.debug("Archiving " + sourceFile.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
        try {
          ZipUtil.zip(sourceFile, targetFile);
        } catch (IOException e) {
          throw new TaskException("Could not archive file: " + e.getMessage(), e);
        }
      } else {
        throw new TaskException("Source does not exist: " + sourceFile.getAbsolutePath());
      }
    } else {
      throw new TaskException("No soure specified");
    }
  }
}
