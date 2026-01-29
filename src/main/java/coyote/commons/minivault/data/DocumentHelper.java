package coyote.commons.minivault.data;

import coyote.commons.minivault.Entries;
import coyote.commons.minivault.crypt.io.CryptInputStream;
import coyote.commons.minivault.crypt.io.CryptOutputStream;
import coyote.commons.minivault.util.JsonMarshaller;
import coyote.commons.minivault.util.StringUtil;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Helper class for reading and writing (encrypted) XML documents.
 */
public final class DocumentHelper {

  /**
   * Converter between document objects and streams representing JSON
   */
  private static final JsonMarshaller MARSHALLER = new JsonMarshaller();


  /**
   * File name to read/write.
   */
  private final String fileName;

  /**
   * Key for encryption.
   */
  private final byte[] key;

  /**
   * Creates a DocumentHelper instance.
   *
   * @param fileName file name
   * @param key      key for encryption
   */
  private DocumentHelper(final String fileName, final byte[] key) {
    this.fileName = fileName;
    this.key = key;
  }

  /**
   * Creates a document helper with no encryption.
   *
   * @param fileName file name
   * @return a new DocumentHelper object
   */
  public static DocumentHelper newInstance(final String fileName) {
    return new DocumentHelper(fileName, null);
  }

  /**
   * Creates a document helper with encryption.
   *
   * @param fileName file name
   * @param key      key for encryption
   * @return a new DocumentHelper object
   */
  public static DocumentHelper newInstance(final String fileName, final byte[] key) {
    return new DocumentHelper(fileName, key);
  }


  /**
   * Writes a document into a JSON file piped to a crypto stream piped to a Gzip stream.
   *
   * @param entries  the secrets entries to write
   * @param textSafe flag indicating
   * @throws DocumentProcessException when the document format is incorrect
   * @throws IOException              if there were problems writing out the entries
   */
  public void writeJsonDocument(final Entries entries, boolean textSafe) throws DocumentProcessException, IOException {
    OutputStream outputStream = null;
    try {
      if (this.key == null) {
        outputStream = new FileOutputStream(this.fileName);
      } else {
        outputStream = new GZIPOutputStream(new CryptOutputStream(new BufferedOutputStream(new FileOutputStream(this.fileName)), this.key));
      }
      MARSHALLER.write(entries, outputStream);
    } catch (Exception e) {
      throw new DocumentProcessException(StringUtil.stripString(e.getMessage()));
    } finally {
      if (outputStream != null) {
        outputStream.close();
      }
    }
  }


  /**
   * Reads a JSON file into an {@link Entries} object.
   *
   * @return Entries read in from the file.
   * @throws IOException
   * @throws DocumentProcessException when the document format is incorrect
   */
  public Entries readJsonDocument() throws IOException, DocumentProcessException {
    InputStream inputStream = null;
    Entries entries;
    try {
      if (this.key == null) {
        inputStream = new FileInputStream(this.fileName);
      } else {
        inputStream = new GZIPInputStream(new CryptInputStream(new BufferedInputStream(new FileInputStream(this.fileName)), this.key));
      }
      entries = MARSHALLER.read(inputStream);
    } catch (Exception e) {
      throw new DocumentProcessException(StringUtil.stripString(e.getMessage()));
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
    }
    return entries;
  }

}
