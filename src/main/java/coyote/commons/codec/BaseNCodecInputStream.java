package coyote.commons.codec;

import static coyote.commons.codec.BaseNCodec.EOF;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import coyote.commons.codec.BaseNCodec.Context;


/**
 * Abstract superclass for Base-N input streams.
 */
public class BaseNCodecInputStream extends FilterInputStream {

  private final BaseNCodec baseNCodec;

  private final boolean doEncode;

  private final byte[] singleByte = new byte[1];

  private final Context context = new Context();




  protected BaseNCodecInputStream( final InputStream in, final BaseNCodec baseNCodec, final boolean doEncode ) {
    super( in );
    this.doEncode = doEncode;
    this.baseNCodec = baseNCodec;
  }




  /**
   * {@inheritDoc}
   *
   * @return {@code 0} if the {@link InputStream} has reached {@code EOF}, 
   * {@code 1} otherwise
   */
  @Override
  public int available() throws IOException {
    // Note: the logic is similar to the InflaterInputStream: as long as we 
    // have not reached EOF, indicate that there is more data available. As we 
    // do not know for sure how much data is left, just return 1 as a safe guess.
    return context.eof ? 0 : 1;
  }




  /**
   * Marks the current position in this input stream.
   * 
   * <p>The {@link #mark} method of {@link BaseNCodecInputStream} does nothing.</p>
   *
   * @param readLimit the maximum limit of bytes that can be read before the mark position becomes invalid.
   */
  @Override
  public synchronized void mark( final int readLimit ) {}




  /**
   * {@inheritDoc}
   *
   * @return always returns <code>false</code>
   */
  @Override
  public boolean markSupported() {
    return false; // too difficult to support marks right now
  }




  /**
   * Reads one <code>byte</code> from this input stream.
   *
   * @return the byte as an integer in the range 0 to 255. Returns -1 if EOF 
   * has been reached.
   * 
   * @throws IOException if an I/O error occurs.
   */
  @Override
  public int read() throws IOException {
    int r = read( singleByte, 0, 1 );
    while ( r == 0 ) {
      r = read( singleByte, 0, 1 );
    }
    if ( r > 0 ) {
      final byte b = singleByte[0];
      return b < 0 ? 256 + b : b;
    }
    return EOF;
  }




  /**
   * Attempts to read <code>len</code> bytes into the specified {@code b} array 
   * starting at {@code offset} from this InputStream.
   *
   * @param b destination byte array
   * @param offset where to start writing the bytes
   * @param len maximum number of bytes to read
   *
   * @return number of bytes read
   * 
   * @throws IOException if an I/O error occurs.
   * @throws NullPointerException if the byte array parameter is null
   * @throws IndexOutOfBoundsException if offset, len or buffer size are invalid
   */
  @Override
  public int read( final byte b[], final int offset, final int len ) throws IOException {
    if ( b == null ) {
      throw new NullPointerException();
    } else if ( ( offset < 0 ) || ( len < 0 ) ) {
      throw new IndexOutOfBoundsException();
    } else if ( ( offset > b.length ) || ( ( offset + len ) > b.length ) ) {
      throw new IndexOutOfBoundsException();
    } else if ( len == 0 ) {
      return 0;
    } else {
      int readLen = 0;
      while ( readLen == 0 ) {
        if ( !baseNCodec.hasData( context ) ) {
          final byte[] buf = new byte[doEncode ? 4096 : 8192];
          final int c = in.read( buf );
          if ( doEncode ) {
            baseNCodec.encode( buf, 0, c, context );
          } else {
            baseNCodec.decode( buf, 0, c, context );
          }
        }
        readLen = baseNCodec.readResults( b, offset, len, context );
      }
      return readLen;
    }
  }




  /**
   * Repositions this stream to the position at the time the mark method was 
   * last called on this input stream.
   * 
   * <p> The {@link #reset} method of {@link BaseNCodecInputStream} does 
   * nothing except throw an {@link IOException}.
   *
   * @throws IOException if this method is invoked
   */
  @Override
  public synchronized void reset() throws IOException {
    throw new IOException( "mark/reset not supported" );
  }




  /**
   * {@inheritDoc}
   *
   * @throws IllegalArgumentException if the provided skip length is negative
   */
  @Override
  public long skip( final long n ) throws IOException {
    if ( n < 0 ) {
      throw new IllegalArgumentException( "Negative skip length: " + n );
    }

    final byte[] b = new byte[512];
    long todo = n;

    while ( todo > 0 ) {
      int len = (int)Math.min( b.length, todo );
      len = this.read( b, 0, len );
      if ( len == EOF ) {
        break;
      }
      todo -= len;
    }

    return n - todo;
  }
}