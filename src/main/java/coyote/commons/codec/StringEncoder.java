package coyote.commons.codec;

/**
 * Defines common encoding methods for String encoders.
 */
public interface StringEncoder extends Encoder {

  /**
   * Encodes a String and returns a String.
   * 
   * @param source the String to encode
   * 
   * @return the encoded String
   * 
   * @throws EncoderException thrown if there is an error condition during the 
   * encoding process.
   */
  String encode( String source ) throws EncoderException;
}
