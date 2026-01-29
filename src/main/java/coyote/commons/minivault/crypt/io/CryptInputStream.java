package coyote.commons.minivault.crypt.io;

import coyote.commons.minivault.crypt.Cbc;
import coyote.commons.minivault.crypt.DecryptException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads from an encrypted {@link InputStream} and provides the decrypted data.
 *
 * <p>The encryption key is provided with the constructor. The initialization vector can also be provided. Otherwise
 * this vector is read from the stream.</p>
 */
public class CryptInputStream extends InputStream {

  /**
   * Maximum size of data that will be read from the underlying stream.
   */
  private static final int FETCH_BUFFER_SIZE = 32;

  /**
   * Underlying stream that provides the encrypted data.
   */
  private final InputStream _parent;

  /**
   * Cipher.
   */
  private final Cbc _cipher;

  private final ByteArrayOutputStream _decrypted;
  /**
   * Buffer for storing the encrypted data.
   */
  private final byte[] _fetchBuffer = new byte[FETCH_BUFFER_SIZE];
  /**
   * Buffer of unencrypted data. If the buffer is completely returned, another chunk of data will be decrypted.
   */
  private byte[] _buffer = null;
  /**
   * Number of {@code byte}s that are already returned from {@link CryptInputStream#_buffer}.
   */
  private int _bufferUsed = 0;
  /**
   * Signals, if the last encrypted data was read. If we run out of buffers, the stream is at its end.
   */
  private boolean _lastBufferRead = false;

  /**
   * Creates a cipher with the key and iv provided.
   *
   * @param parent Stream that provides the encrypted data
   * @param key    key for the cipher algorithm
   * @param iv     initial values for the CBC scheme
   */
  public CryptInputStream(InputStream parent, byte[] key, byte[] iv) {
    this._parent = parent;
    this._decrypted = new ByteArrayOutputStream();
    this._cipher = new Cbc(iv, key, this._decrypted);
  }

  /**
   * Creates a cipher with the key.
   *
   * <p>The iv will be read from the {@code parent} stream. If there are not enough {@code byte}s in the stream, an
   * {@link IOException} will be raised.</p>
   *
   * @param parent Stream that provides the encrypted data
   * @param key    key for the cipher algorithm
   * @throws IOException if the iv can't be read
   */
  public CryptInputStream(InputStream parent, byte[] key) throws IOException {
    this._parent = parent;
    byte[] iv = new byte[16];
    int readed = 0;
    while (readed < 16) {
      int cur = parent.read(iv, readed, 16 - readed);
      if (cur < 0) {
        throw new IOException("No initial values in stream.");
      }
      readed += cur;
    }
    this._decrypted = new ByteArrayOutputStream();
    this._cipher = new Cbc(iv, key, this._decrypted);
  }

  /**
   * Tries to read the next decrypted data from the output stream
   */
  private void readFromStream() {
    if (this._decrypted.size() > 0) {
      this._buffer = this._decrypted.toByteArray();
      this._decrypted.reset();
    }
  }

  /**
   * Returns the next decrypted {@code byte}.
   *
   * <p>If there is no more data, {@code -1} will be returned. If the decryption fails or the underlying stream
   * throws an {@link IOException}, an {@link IOException} will be thrown.</p>
   *
   * @return next decrypted {@code byte} or {@code -1}
   * @throws IOException if the decryption fails or the underlying stream throws an exception
   */
  @Override
  public int read() throws IOException {
    while (this._buffer == null || this._bufferUsed >= this._buffer.length) {
      if (this._lastBufferRead) {
        return -1;
      }

      this._bufferUsed = 0;
      this._buffer = null;

      int readed = this._parent.read(this._fetchBuffer, 0, FETCH_BUFFER_SIZE);
      if (readed < 0) {
        this._lastBufferRead = true;
        try {
          this._cipher.finishDecryption();
          readFromStream();
        } catch (DecryptException ex) {
          throw new IOException("can't decrypt");
        }
      } else {
        this._cipher.decrypt(this._fetchBuffer, readed);
        readFromStream();
      }
    }

    return this._buffer[this._bufferUsed++] & 0xff;
  }

  /**
   * Closes the parent stream.
   *
   * @throws IOException if the parent stream throws an exception
   */
  @Override
  public void close() throws IOException {
    this._parent.close();
  }

}
