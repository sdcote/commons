package coyote.commons.codec;

import java.io.UnsupportedEncodingException;

import coyote.commons.StringUtil;


/**
* Converts hexadecimal Strings. 
* 
* <p>The charset used for certain operation can be set, the default is set in 
* {@link #DEFAULT_CHARSET_NAME}
*/
public class Hex implements BinaryEncoder, BinaryDecoder {

  /** Default charset name is {@link StringUtil#UTF_8} */
  public static final String DEFAULT_CHARSET_NAME = StringUtil.UTF_8;

  /** Used to build output as Hex */
  private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

  /** Used to build output as Hex */
  private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };




  /**
   * Converts an array of characters representing hexadecimal values into an 
   * array of bytes of those same values. 
   * 
   * <p>The returned array will be half the length of the passed array, as it 
   * takes two characters to represent any given byte. An exception is thrown 
   * if the passed char array has an odd number of elements.
   * 
   * @param data An array of characters containing hexadecimal digits
   * 
   * @return A byte array containing binary data decoded from the supplied char 
   * array.
   * 
   * @throws DecoderException Thrown if an odd number or illegal of characters 
   * is supplied
   */
  public static byte[] decodeHex( final char[] data ) throws DecoderException {

    final int len = data.length;

    if ( ( len & 0x01 ) != 0 ) {
      throw new DecoderException( "Odd number of characters." );
    }

    final byte[] out = new byte[len >> 1];

    // two characters form the hex value.
    for ( int i = 0, j = 0; j < len; i++ ) {
      int f = toDigit( data[j], j ) << 4;
      j++;
      f = f | toDigit( data[j], j );
      j++;
      out[i] = (byte)( f & 0xFF );
    }

    return out;
  }




  /**
   * Converts an array of bytes into an array of characters representing the 
   * hexadecimal values of each byte in order.
   * 
   * <p>The returned array will be double the length of the passed array, as it 
   * takes two characters to represent any given byte.
   * 
   * @param data a byte[] to convert to Hex characters
   * 
   * @return A char[] containing hexadecimal characters
   */
  public static char[] encodeHex( final byte[] data ) {
    return encodeHex( data, true );
  }




  /**
   * Converts an array of bytes into an array of characters representing the 
   * hexadecimal values of each byte in order.
   * 
   * <p>The returned array will be double the length of the passed array, as it 
   * takes two characters to represent any given byte.
   * 
   * @param data a byte[] to convert to Hex characters
   * @param toLowerCase <code>true</code> converts to lowercase, {@code false} 
   * to uppercase
   * 
   * @return A char[] containing hexadecimal characters
   */
  public static char[] encodeHex( final byte[] data, final boolean toLowerCase ) {
    return encodeHex( data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER );
  }




  /**
   * Converts an array of bytes into an array of characters representing the 
   * hexadecimal values of each byte in order.
   * 
   * <p>The returned array will be double the length of the passed array, as it 
   * takes two characters to represent any given byte.
   * 
   * @param data a byte[] to convert to Hex characters
   * @param toDigits the output alphabet
   * 
   * @return A char[] containing hexadecimal characters
   */
  protected static char[] encodeHex( final byte[] data, final char[] toDigits ) {
    final int l = data.length;
    final char[] out = new char[l << 1];
    // two characters form the hex value.
    for ( int i = 0, j = 0; i < l; i++ ) {
      out[j++] = toDigits[( 0xF0 & data[i] ) >>> 4];
      out[j++] = toDigits[0x0F & data[i]];
    }
    return out;
  }




  /**
   * Converts an array of bytes into a String representing the hexadecimal 
   * values of each byte in order. 
   * 
   * <p>The returned String will be double the length of the passed array, as 
   * it takes two characters to represent any given byte.
   * 
   * @param data a byte[] to convert to Hex characters
   * 
   * @return A String containing hexadecimal characters
   */
  public static String encodeHexString( final byte[] data ) {
    return new String( encodeHex( data ) );
  }




  /**
   * Converts a hexadecimal character to an integer.
   * 
   * @param ch A character to convert to an integer digit
   * @param index The index of the character in the source
   * 
   * @return An integer
   * 
   * @throws DecoderException Thrown if ch is an illegal hex character
   */
  protected static int toDigit( final char ch, final int index ) throws DecoderException {
    final int digit = Character.digit( ch, 16 );
    if ( digit == -1 ) {
      throw new DecoderException( "Illegal hexadecimal character " + ch + " at index " + index );
    }
    return digit;
  }

  private final String charsetName;




  /**
   * Creates a new codec with the default charset name 
   * {@link #DEFAULT_CHARSET_NAME}
   */
  public Hex() {
    // use default encoding
    charsetName = DEFAULT_CHARSET_NAME;
  }




  /**
   * Creates a new codec with the given charset name.
   * 
   * @param csName the charset name.
   */
  public Hex( final String csName ) {
    charsetName = csName;
  }




  /**
   * Converts an array of character bytes representing hexadecimal values into 
   * an array of bytes of those same values.
   * 
   * <p>The returned array will be half the length of the passed array, as it 
   * takes two characters to represent any given byte. An exception is thrown 
   * if the passed char array has an odd number of elements.
   * 
   * @param array An array of character bytes containing hexadecimal digits
   * 
   * @return A byte array containing binary data decoded from the supplied byte 
   * array (representing characters).
   * 
   * @throws DecoderException Thrown if an odd number of characters is supplied 
   * to this function
   */
  @Override
  public byte[] decode( final byte[] array ) throws DecoderException {
    try {
      return decodeHex( new String( array, getCharsetName() ).toCharArray() );
    } catch ( final UnsupportedEncodingException e ) {
      throw new DecoderException( e.getMessage(), e );
    }
  }




  /**
   * Converts a String or an array of character bytes representing hexadecimal 
   * values into an array of bytes of those same values. 
   * 
   * <p>The returned array will be half the length of the passed String or 
   * array, as it takes two characters to represent any given byte. An 
   * exception is thrown if the passed char array has an odd number of 
   * elements.
   * 
   * @param object A String or, an array of character bytes containing 
   * hexadecimal digits
   * 
   * @return A byte array containing binary data decoded from the supplied byte 
   * array (representing characters).
   * 
   * @throws DecoderException Thrown if an odd number of characters is supplied 
   * to this function or the object is not a String or char[]
   */
  @Override
  public Object decode( final Object object ) throws DecoderException {
    try {
      final char[] charArray = object instanceof String ? ( (String)object ).toCharArray() : (char[])object;
      return decodeHex( charArray );
    } catch ( final ClassCastException e ) {
      throw new DecoderException( e.getMessage(), e );
    }
  }




  /**
   * Converts an array of bytes into an array of bytes for the characters 
   * representing the hexadecimal values of each byte in order. 
   * 
   * <p>The returned array will be double the length of the passed array, as it 
   * takes two characters to represent any given byte.
   * 
   * <p>The conversion from hexadecimal characters to the returned bytes is 
   * performed with the charset named by {@link #getCharsetName()}. 
   * 
   * @param array a byte[] to convert to Hex characters
   * 
   * @return A byte[] containing the bytes of the hexadecimal characters
   * 
   * @throws IllegalStateException if the charsetName is invalid. This API 
   * throws {@link IllegalStateException} instead of 
   * {@link UnsupportedEncodingException} for backward compatibility.
   */
  @Override
  public byte[] encode( final byte[] array ) {
    return StringUtil.getBytesUnchecked( encodeHexString( array ), getCharsetName() );
  }




  /**
   * Converts a String or an array of bytes into an array of characters 
   * representing the hexadecimal values of each byte in order. 
   * 
   * <p>The returned array will be double the length of the passed String or 
   * array, as it takes two characters to represent any given byte.
   * 
   * <p>The conversion from hexadecimal characters to bytes to be encoded to 
   * performed with the charset named by {@link #getCharsetName()}. 
   * 
   * @param object a String, or byte[] to convert to Hex characters
   * 
   * @return A char[] containing hexadecimal characters
   * 
   * @throws EncoderException Thrown if the given object is not a String or 
   * byte[]
   */
  @Override
  public Object encode( final Object object ) throws EncoderException {
    try {
      final byte[] byteArray = object instanceof String ? ( (String)object ).getBytes( getCharsetName() ) : (byte[])object;
      return encodeHex( byteArray );
    } catch ( final ClassCastException e ) {
      throw new EncoderException( e.getMessage(), e );
    } catch ( final UnsupportedEncodingException e ) {
      throw new EncoderException( e.getMessage(), e );
    }
  }




  /**
   * Gets the charset name.
   * 
   * @return the charset name.
   */
  public String getCharsetName() {
    return charsetName;
  }




  /**
   * Returns a string representation of the object, which includes the charset 
   * name.
   * 
   * @return a string representation of the object.
   */
  @Override
  public String toString() {
    return super.toString() + "[charsetName=" + charsetName + "]";
  }
}
