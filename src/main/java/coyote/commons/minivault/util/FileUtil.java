package coyote.commons.minivault.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtil {


  /**
   * Opens a file, reads it and returns the data as a string and closes the file.
   *
   * @param file - file to open
   *
   * @return String representing the file data
   */
  public static String fileToString(final File file) {
    try {
      final byte[] data = FileUtil.read(file);
      if (data != null) {
        try {
          return new String(data, StandardCharsets.UTF_8);
        } catch (final Throwable t) {
          return new String(data);
        }
      }
    } catch (final Exception ex) {}

    return null;
  }


  /**
   * Read the entire file into memory as an array of bytes.
   *
   * @param file The file to read
   *
   * @return A byte array that contains the contents of the file.
   *
   * @throws IOException If problems occur.
   */
  public static byte[] read(final File file) throws IOException {
    if (file == null) {
      throw new IOException("File reference was null");
    }

    if (file.exists() && file.canRead()) {
      DataInputStream dis = null;
      final byte[] bytes = new byte[new Long(file.length()).intValue()];

      try {
        dis = new DataInputStream(new FileInputStream(file));

        dis.readFully(bytes);

        return bytes;
      } catch (final Exception ignore) {}
      finally {
        // Attempt to close the data input stream
        try {
          if (dis != null) {
            dis.close();
          }
        } catch (final Exception ignore) {}
      }
    }
    return new byte[0];
  }

}
