package coyote.commons.minivault.crypt.io;

import coyote.commons.minivault.crypt.Cbc;
import coyote.commons.minivault.util.CryptUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * Encrypts the passed data and stores it into the underlying {@link OutputStream}.
 *
 * <p>If no initial vector is provided in the constructor, the cipher will be initialized with random data and this
 * data will be sent directly to the underlying stream.</p>
 */
public class CryptOutputStream extends OutputStream {

  /**
   * Cipher.
   */
  private final Cbc _cipher;

  /**
   * Buffer for sending single {@code byte}s.
   */
  private final byte[] _buffer = new byte[1];

  /**
   * Initializes the cipher with the given key and initial values.
   *
   * @param parent underlying {@link OutputStream}
   * @param key    key for the cipher algorithm
   * @param iv     initial values for the CBC scheme
   */
  public CryptOutputStream(OutputStream parent, byte[] key, byte[] iv) {
    this._cipher = new Cbc(iv, key, parent);
  }

  /**
   * Initializes the cipher with the given key. The initial values for the CBC scheme will be
   * random and sent to the underlying stream.
   *
   * @param parent underlying {@link OutputStream}
   * @param key    key for the cipher algorithm
   * @throws IOException if the initial values can't be written to the underlying stream
   */
  public CryptOutputStream(OutputStream parent, byte[] key)
          throws IOException {
    byte[] iv = new byte[16];
    Random rnd = CryptUtils.newRandomNumberGenerator();
    rnd.nextBytes(iv);
    parent.write(iv);

    this._cipher = new Cbc(iv, key, parent);
  }

  /**
   * Encrypts a single {@code byte}.
   *
   * @param b {@code byte} to be encrypted
   * @throws IOException if encrypted data can't be written to the underlying stream
   */
  @Override
  public void write(int b) throws IOException {
    this._buffer[0] = (byte) b;
    this._cipher.encrypt(this._buffer);
  }

  /**
   * Encrypts a {@code byte} array.
   *
   * @param b {@code byte} array to be encrypted
   * @throws IOException if encrypted data can't be written to the underlying stream
   */
  @Override
  public void write(byte[] b) throws IOException {
    this._cipher.encrypt(b);
  }

  /**
   * Finalizes the encryption and closes the underlying stream.
   *
   * @throws IOException if the encryption fails or the encrypted data can't be written to the
   *                     underlying stream
   */
  @Override
  public void close() throws IOException {
    this._cipher.finishEncryption();
  }

}
