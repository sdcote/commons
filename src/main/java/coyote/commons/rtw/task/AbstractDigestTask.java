/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import coyote.commons.ByteUtil;
import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TaskException;

/**
 * Methods common to multiple checksum tasks
 */
public abstract class AbstractDigestTask extends AbstractFileTask {
  protected String CHECKSUM_EXTENSION;
  protected String ALGORITHM;




  /**
   * @return the name of the algorithm used
   */
  public String getAlgorithm() {
    return ALGORITHM;
  }




  /**
   * @return a new instance of a message digest to use;
   */
  abstract MessageDigest getDigest();




  /**
   * @return the file extension used for the checksum file
   */
  public String getFileExtension() {
    return CHECKSUM_EXTENSION;
  }




  /**
   * Handle all digest checks the same way.
   */
  @Override
  protected void performTask() throws TaskException {
    final String source = getSourceOrFile();
    String expectedDigest = null;

    // Retrieve the digest from a context variable
    final String contextKey = getConfiguration().getString(ConfigTag.CONTEXT);
    if (StringUtil.isNotBlank(contextKey)) {
      expectedDigest = getString(contextKey);
    }

    if (StringUtil.isNotBlank(source)) {
      final File file = getAbsoluteFile(source);
      if (file.exists()) {
        if (file.canRead()) {
          if (file.length() > 0) {
            String digest = null;
            try {
              digest = digest(file, getDigest());
              Log.debug(String.format("%s results for '%s': %s", ALGORITHM, file.getAbsolutePath(), digest));

              final String digestFilename = file.getAbsolutePath() + CHECKSUM_EXTENSION;
              final File digestFile = new File(digestFilename);

              if (StringUtil.isNotBlank(expectedDigest)) {
                if (StringUtil.equalsIgnoreCase(digest, expectedDigest.trim())) {
                  Log.info(String.format("%s verified for %s", ALGORITHM, file.getAbsolutePath()));
                  getContext().set(digestFilename, digest);
                } else {
                  final String msg = String.format("%s verification failed for %s (%s)", ALGORITHM, file.getAbsolutePath(), source);
                  if (haltOnError) {
                    throw new TaskException(msg);
                  } else {
                    Log.error(msg);
                    return;
                  }
                }
              } else {
                if (digestFile.exists()) {
                  if (digestFile.canRead()) {
                    final String expected = FileUtil.fileToString(digestFile);
                    if (StringUtil.isNotBlank(expected)) {
                      if (StringUtil.equalsIgnoreCase(digest, expected.trim())) {
                        Log.info(String.format("%s verified for %s", ALGORITHM, file.getAbsolutePath()));
                        getContext().set(digestFilename, digest);
                      } else {
                        final String msg = String.format("%s verification failed for %s (%s)", ALGORITHM, file.getAbsolutePath(), source);
                        if (haltOnError) {
                          throw new TaskException(msg);
                        } else {
                          Log.error(msg);
                          return;
                        }
                      }
                    } else {
                      final String msg = String.format("%s digest file is blank for %s (%s)", ALGORITHM, file.getAbsolutePath(), source);
                      if (haltOnError) {
                        throw new TaskException(msg);
                      } else {
                        Log.error(msg);
                        return;
                      }
                    }
                  } else {
                    final String msg = String.format("Could not read %s digest file for %s (%s)", ALGORITHM, file.getAbsolutePath(), source);
                    if (haltOnError) {
                      throw new TaskException(msg);
                    } else {
                      Log.error(msg);
                      return;
                    }
                  }
                } else {
                  Log.warn(String.format("No %s digest data found for %s", ALGORITHM, file.getAbsolutePath()));
                }
              }
            } catch (final IOException e) {
              final String msg = String.format("%s calculation error for %s (%s): %s", ALGORITHM, file.getAbsolutePath(), source, e.getMessage());
              if (haltOnError) {
                throw new TaskException(msg);
              } else {
                Log.error(msg);
                return;
              }
            }
          } else {
            final String msg = String.format("%s source file is empty: %s (%s)", ALGORITHM, file.getAbsolutePath(), source);
            if (haltOnError) {
              throw new TaskException(msg);
            } else {
              Log.error(msg);
              return;
            }
          }
        } else {
          final String msg = String.format("%s source file could not be read: %s (%s)", ALGORITHM, file.getAbsolutePath(), source);
          if (haltOnError) {
            throw new TaskException(msg);
          } else {
            Log.error(msg);
            return;
          }
        }
      } else {
        final String msg = String.format("%s source file does not exist: %s (%s)", ALGORITHM, file.getAbsolutePath(), source);
        if (haltOnError) {
          throw new TaskException(msg);
        } else {
          Log.error(msg);
          return;
        }
      }
    } else {
      final String msg = String.format("%s configuration error: missing %s", getClass().getSimpleName(), ConfigTag.SOURCE);
      if (haltOnError) {
        throw new TaskException(msg);
      } else {
        Log.error(msg);
        return;
      }
    }
  }




  /**
   * Generate the digest for the file.
   *
   * @param file The file to process
   * @param md the message digest to use
   *
   * @return a string representing the digest of the file
   *
   * @throws IOException if there were problems reading the given file
   */
  protected static String digest(final File file, final MessageDigest md) throws IOException {
    try (InputStream fis = new FileInputStream(file)) {
      final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
      int read = fis.read(buffer, 0, STREAM_BUFFER_LENGTH);
      while (read > -1) {
        md.update(buffer, 0, read);
        read = fis.read(buffer, 0, STREAM_BUFFER_LENGTH);
      }
      return ByteUtil.bytesToHex(md.digest(), "").toLowerCase();
    }
  }

}
