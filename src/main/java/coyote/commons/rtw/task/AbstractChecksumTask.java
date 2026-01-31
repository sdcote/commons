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
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TaskException;


/**
 * Methods common to multiple checksum tasks
 */
public abstract class AbstractChecksumTask extends AbstractFileTask {

  protected String CHECKSUM_EXTENSION;
  protected String ALGORITHM;




  /**
   * @return a new instance of a message checksum to use;
   */
  public abstract Checksum getChecksum();




  protected static String getCRC32Checksum(final File file) {
    try {
      return getChecksum(file, new CRC32());
    } catch (final IOException e) {
      return null;
    }
  }




  protected static String getAdler32Checksum(final File file) {
    try {
      return getChecksum(file, new Adler32());
    } catch (final IOException e) {
      return null;
    }
  }




  private static String getChecksum(final File file, final Checksum algorithm) throws IOException {
    long checksum = 0;
    try (InputStream fis = new FileInputStream(file); CheckedInputStream cis = new CheckedInputStream(fis, algorithm)) {
      final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
      while (cis.read(buffer) >= 0) {}
      checksum = cis.getChecksum().getValue();
    }
    return Long.toHexString(checksum);
  }




  /**
   * @return the name of the algorithm used
   */
  public String getAlgorithm() {
    return ALGORITHM;
  }




  /**
   * @return the file extension used for the checksum file
   */
  public String getFileExtension() {
    return CHECKSUM_EXTENSION;
  }




  /**
   * Handle all checksums the same way.
   */
  @Override
  protected void performTask() throws TaskException {
    final String source = getSourceOrFile();
    String expectedChecksum = null;

    // Retrieve the checksum from a context variable
    final String contextKey = getConfiguration().getString(ConfigTag.CONTEXT);
    if (StringUtil.isNotBlank(contextKey)) {
      expectedChecksum = getString(contextKey);
    }

    if (StringUtil.isNotBlank(source)) {
      final File file = getAbsoluteFile(source);
      if (file.exists()) {
        if (file.canRead()) {
          if (file.length() > 0) {
            String checksum = null;
            try {
              checksum = getChecksum(file, getChecksum());
              Log.debug(String.format( "Checksum.results", file.getAbsolutePath(), ALGORITHM, checksum));

              final String checksumFilename = file.getAbsolutePath() + CHECKSUM_EXTENSION;
              final File checksumFile = new File(checksumFilename);

              if (StringUtil.isNotBlank(expectedChecksum)) {
                if (StringUtil.equalsIgnoreCase(checksum, expectedChecksum.trim())) {
                  Log.info(String.format( "Checksum.verified", ALGORITHM, file.getAbsolutePath()));
                  getContext().set(checksumFilename, checksum);
                } else {
                  final String msg = String.format( "Checksum.verification_failed", ALGORITHM, source, file.getAbsolutePath()).toString();
                  if (haltOnError) {
                    throw new TaskException(msg);
                  } else {
                    Log.error(msg);
                    return;
                  }
                }
              } else {
                if (checksumFile.exists()) {
                  if (checksumFile.canRead()) {
                    final String expected = FileUtil.fileToString(checksumFile);
                    if (StringUtil.isNotBlank(expected)) {
                      if (StringUtil.equalsIgnoreCase(checksum, expected.trim())) {
                        Log.info(String.format( "Checksum.verified", ALGORITHM, file.getAbsolutePath()));
                        getContext().set(checksumFilename, checksum);
                      } else {
                        final String msg = String.format( "Checksum.verification_failed", ALGORITHM, source, file.getAbsolutePath()).toString();
                        if (haltOnError) {
                          throw new TaskException(msg);
                        } else {
                          Log.error(msg);
                          return;
                        }
                      }
                    } else {
                      final String msg = String.format( "Checksum.blank_digest_data", ALGORITHM, source, file.getAbsolutePath()).toString();
                      if (haltOnError) {
                        throw new TaskException(msg);
                      } else {
                        Log.error(msg);
                        return;
                      }
                    }
                  } else {
                    final String msg = String.format( "Checksum.could_not_read_digest_file", ALGORITHM, source, file.getAbsolutePath()).toString();
                    if (haltOnError) {
                      throw new TaskException(msg);
                    } else {
                      Log.error(msg);
                      return;
                    }
                  }
                } else {
                  Log.warn(String.format( "Checksum.no_digest_data", ALGORITHM, file.getAbsolutePath()));
                }
              }
            } catch (final IOException e) {
              final String msg = String.format( "Checksum.calculation_error", ALGORITHM, e.getMessage(), source, file.getAbsolutePath()).toString();
              if (haltOnError) {
                throw new TaskException(msg);
              } else {
                Log.error(msg);
                return;
              }
            }
          } else {
            final String msg = String.format( "Checksum.empty_source_file", ALGORITHM, source, file.getAbsolutePath()).toString();
            if (haltOnError) {
              throw new TaskException(msg);
            } else {
              Log.error(msg);
              return;
            }
          }
        } else {
          final String msg = String.format( "Checksum.source_could_not_be_read", ALGORITHM, source, file.getAbsolutePath()).toString();
          if (haltOnError) {
            throw new TaskException(msg);
          } else {
            Log.error(msg);
            return;
          }
        }
      } else {
        final String msg = String.format( "Checksum.source_does_not_exist", ALGORITHM, source, file.getAbsolutePath()).toString();
        if (haltOnError) {
          throw new TaskException(msg);
        } else {
          Log.error(msg);
          return;
        }
      }
    } else {
      final String msg = String.format( "Checksum.configuration_error", getClass().getSimpleName(), ConfigTag.SOURCE).toString();
      if (haltOnError) {
        throw new TaskException(msg);
      } else {
        Log.error(msg);
        return;
      }
    }
  }

}
