package coyote.commons.codec;

import java.io.OutputStream;


/**
 * Provides Base32 encoding and decoding in a streaming fashion (unlimited 
 * size). 
 * 
 * <p>When encoding the default lineLength is 76 characters and the default 
 * lineEnding is CRLF, but these can be overridden by using the appropriate
 * constructor.
 * 
 * <p> The default behaviour of the Base32OutputStream is to ENCODE, whereas 
 * the default behaviour of the Base32InputStream is to DECODE. But this 
 * behavior can be overridden by using a different constructor.
 * 
 * <p> Since this class operates directly on byte streams, and not character 
 * streams, it is hard-coded to only encode/decode character encodings which 
 * are compatible with the lower 127 ASCII chart (ISO-8859-1, Windows-1252, 
 * UTF-8, etc).
 * 
 * <p><strong>Note:</strong> It is mandatory to close the stream after the last 
 * byte has been written to it, otherwise the final padding will be omitted and 
 * the resulting data will be incomplete/inconsistent.
 * @see <a href="http://www.ietf.org/rfc/rfc4648.txt">RFC 4648</a>
 */
public class Base32OutputStream extends BaseNCodecOutputStream {

  /**
   * Creates a Base32OutputStream such that all data written is Base32-encoded 
   * to the original provided OutputStream.
   *
   * @param out OutputStream to wrap.
   */
  public Base32OutputStream( final OutputStream out ) {
    this( out, true );
  }




  /**
   * Creates a Base32OutputStream such that all data written is either 
   * Base32-encoded or Base32-decoded to the original provided OutputStream.
   *
   * @param out OutputStream to wrap.
   * @param doEncode true if we should encode all data written to us, false if we should decode.
   */
  public Base32OutputStream( final OutputStream out, final boolean doEncode ) {
    super( out, new Base32( false ), doEncode );
  }




  /**
   * Creates a Base32OutputStream such that all data written is either 
   * Base32-encoded or Base32-decoded to the original provided OutputStream.
   *
   * @param out OutputStream to wrap.
   * @param doEncode true if we should encode all data written to us, false if 
   * we should decode.
   * @param lineLength If doEncode is true, each line of encoded data will 
   * contain lineLength characters (rounded down to nearest multiple of 4). If 
   * lineLength &lt;= 0, the encoded data is not divided into lines. If 
   * doEncode is false, lineLength is ignored.
   * @param lineSeparator If doEncode is true, each line of encoded data will 
   * be terminated with this byte sequence (e.g. \r\n). If lineLength &lt;= 0, 
   * the lineSeparator is not used. If doEncode is false lineSeparator is ignored.
   */
  public Base32OutputStream( final OutputStream out, final boolean doEncode, final int lineLength, final byte[] lineSeparator ) {
    super( out, new Base32( lineLength, lineSeparator ), doEncode );
  }

}