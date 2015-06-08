/*
 * Copyright (c) 2006 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial API and implementation
 */
package coyote.commons;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;


/**
 * General string utilities.
 */
public final class StringUtil {

  /** CarriageReturn and LineFeed character sequence */
  public static final String CRLF = "\r\n";

  /** CarriageReturn string */
  public static final String CR = "\r";

  /** The space string */
  public static final String SP = " ";

  /** Hard tab string */
  public static final String HT = "\t";

  /** NewLine string */
  public static final String NL = "\n";

  /** Platform specific line separator (default = CRLF) */
  public static final String LINE_FEED = System.getProperty( "line.separator", "\r\n" );

  /** Length of the platform specific LineFeed sequence */
  public static final int LINE_FEED_LEN = StringUtil.LINE_FEED.length();

  /** Platform specific path separator (default = "/") */
  public static final String PATH_SEPARATOR = System.getProperty( "path.separator", "/" );

  /** Platform specific path separator (default = ":") */
  public static final String FILE_SEPARATOR = System.getProperty( "file.separator", ":" );

  /**
   * An "XML Safe" string require thats certain strings (or more correctly,
   * characters) be substituted for others. See page 257 of "XML by Example".
   * <ul> <li>&amp; - &amp;amp; <li>&lt; - &amp;lt; <li>&gt; - &amp;gt;
   * <li>&quote; - &amp;quote; <li>&apos; - &amp;apos; </ul>
   */
  public static final String XML_ENTITYREFS[] = { "&", "&amp;", "<", "&lt;", ">", "&gt;", "\"", "&quot;", "'", "&apos;" };

  /** Same as XML but there is no entity reference for an apostrophe. */
  public static final String HTML_ENTITYREFS[] = { "&", "&amp;", "<", "&lt;", ">", "&gt;", "\"", "&quot;" };

  /** Soundex Character Mapping */
  private static final char soundex_map[] = "01230120032455012623010202".toCharArray();

  /**
   * CharEncodingISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1. </p>
   */
  public static final String ISO_8859_1 = "ISO-8859-1";

  /** Seven-bit ASCII, also known as ISO646-US, also known as the Basic Latin block of the Unicode character set. */
  public static final String US_ASCII = "US-ASCII";

  /** Sixteen-bit Unicode Transformation Format, The byte order specified by a mandatory initial byte-order mark (either order accepted on input, big-endian used on output) */
  public static final String UTF_16 = "UTF-16";

  /** Sixteen-bit Unicode Transformation Format, big-endian byte order. */
  public static final String UTF_16BE = "UTF-16BE";

  /** Sixteen-bit Unicode Transformation Format, little-endian byte order. */
  public static final String UTF_16LE = "UTF-16LE";

  /** Eight-bit Unicode Transformation Format. */
  public static final String UTF_8 = "UTF-8";

  /** Field ISO8859_1 */
  public static String ISO8859_1;
  static {
    final String iso = System.getProperty( "ISO_8859_1" );
    if ( iso != null ) {
      StringUtil.ISO8859_1 = iso;
    } else {
      try {
        new String( new byte[] { (byte)20 }, "ISO-8859-1" );

        StringUtil.ISO8859_1 = "ISO-8859-1";
      } catch ( final java.io.UnsupportedEncodingException e ) {
        StringUtil.ISO8859_1 = "ISO8859_1";
      }
    }
  }




  /**
   * Private constructor because everything is static
   */
  private StringUtil() {}




  /**
   * Replace a list of tokens in a string.
   * 
   * <p> String <code>2i</code> is replaced with String <code>2i+1</code>.
   * Order is very important. If you want to convert &lt; to &amp;lt; and you
   * also want to convert &amp to &amp;amp; then it is important that you
   * first convert &amp; to &amp;amp; before converting &lt; to &amp;lt;. If
   * you do not, then the &amp in &amp;lt; will be converted to &amp;amp;lt;.
   * </p>
   * 
   * @param tokens is an array of strings such that string <code>2i</code> is
   *            replaced with string <code>2i+1</code>.
   * @param string is the string to be searched.
   * @param fromStart If true, the substitution will be performed from the
   *            begining , otherwise the replacement will begin from the end
   *            of the array resulting in a reverse substitution
   * 
   * @return string with tokens replaced.
   */
  public static final String tokenSubst( final String[] tokens, final String string, final boolean fromStart ) {
    String temps = ( string == null ) ? "" : string;

    if ( temps.length() > 0 ) {
      final int delta = ( fromStart ) ? 2 : -2;
      int i_old = ( fromStart ) ? 0 : tokens.length - 1;
      int i_new = i_old + delta / 2;
      final int num_to_do = tokens.length / 2;
      int cnt;

      for ( cnt = 0; cnt < num_to_do; ++cnt ) {
        StringBuffer buf = null;
        int last_pos = 0;
        final String tok_string = tokens[i_old];
        final int tok_len = tok_string.length();
        int tok_pos = temps.indexOf( tok_string, last_pos );

        while ( tok_pos >= 0 ) {
          if ( buf == null ) {
            buf = new StringBuffer();
          }

          if ( last_pos != tok_pos ) {
            buf.append( temps.substring( last_pos, tok_pos ) );
          }

          buf.append( tokens[i_new] );

          last_pos = tok_pos + tok_len;
          tok_pos = temps.indexOf( tok_string, last_pos );
        }

        if ( ( last_pos < temps.length() ) && ( buf != null ) ) {
          buf.append( temps.substring( last_pos ) );
        }

        if ( buf != null ) {
          temps = buf.toString();
        }

        i_old += delta;
        i_new += delta;
      }
    }

    return temps;
  }




  /**
   * Replace one character with another in a string.
   * 
   * @param target the target character to replace
   * @param desired the desired character
   * @param string is the string to be searched.
   * 
   * @return string with tokens replaced.
   */
  public static final String charSubst( final char target, final char desired, final String string ) {
    final StringBuffer buf = new StringBuffer( ( string == null ) ? "" : string );

    for ( int indx = buf.length() - 1; indx >= 0; --indx ) {
      if ( buf.charAt( indx ) == target ) {
        buf.setCharAt( indx, desired );
      }
    }

    return buf.toString();
  }




  /**
   * Return a string made XML safe back to its original condition.
   * 
   * @param string the string to convert
   * 
   * @return XML String converted to an XML safe string.
   */
  public static final String XMLToString( final String string ) {
    return StringUtil.tokenSubst( StringUtil.XML_ENTITYREFS, string, false );
  }




  /**
   * Makes a string safe to place in HTML
   * 
   * @param string the string to convert
   * 
   * @return a new string with HTML characters replaces with HTML entities.
   */
  public static final String HTMLToString( final String string ) {
    return StringUtil.replace( StringUtil.tokenSubst( StringUtil.XML_ENTITYREFS, string, false ), "&nbsp;", "" );
  }




  /**
   * Check that the given CharSequence is neither {@code null} nor of length 0.
   * Note: Will return {@code true} for a CharSequence that purely consists of whitespace.
   * <p><pre class="code">
   * StringUtil.hasLength(null) = false
   * StringUtil.hasLength("") = false
   * StringUtil.hasLength(" ") = true
   * StringUtil.hasLength("Hello") = true
   * </pre>
   * @param str the CharSequence to check (may be {@code null})
   * 
   * @return {@code true} if the CharSequence is not null and has length
   */
  public static boolean hasLength( CharSequence str ) {
    return ( str != null && str.length() > 0 );
  }




  /**
   * Check that the given String is neither {@code null} nor of length 0.
   * 
   * <p><strong>Note:</strong> This will return {@code true} for a String that 
   * consists entirely of whitespace.</p>
   * 
   * @param str the String to check (may be {@code null})
   * 
   * @return {@code true} if the String is not null and has length
   */
  public static boolean hasLength( String str ) {
    return hasLength( (CharSequence)str );
  }




  /**
   * Check whether the given CharSequence has actual text.
   * 
   * <p>Returns {@code true} if the string not {@code null}, its length is 
   * greater than 0, and it contains at least one non-whitespace character.</p>
   * 
   * <pre class="code">
   * StringUtils.hasText(null) = false
   * StringUtils.hasText("") = false
   * StringUtils.hasText(" ") = false
   * StringUtils.hasText("12345") = true
   * StringUtils.hasText(" 12345 ") = true
   * </pre>
   * 
   * @param str the CharSequence to check (may be {@code null})
   * 
   * @return {@code true} if the CharSequence is not {@code null}, its length is greater than 0, and it does not contain whitespace only
   */
  public static boolean hasText( CharSequence str ) {
    if ( !hasLength( str ) ) {
      return false;
    }
    int strLen = str.length();
    for ( int i = 0; i < strLen; i++ ) {
      if ( !Character.isWhitespace( str.charAt( i ) ) ) {
        return true;
      }
    }
    return false;
  }




  /**
   * Check whether the given String has actual text.
   * 
   * <p>Returns {@code true} if the string not {@code null}, its length is 
   * greater than 0, and it contains at least one non-whitespace character.</p>
   * 
   * @param str the String to check (may be {@code null})
   * 
   * @return {@code true} if the String is not {@code null}, its length is greater than 0, and it does not contain whitespace only
   */
  public static boolean hasText( String str ) {
    return hasText( (CharSequence)str );
  }




  /**
   * Make a string safe to send as part of an XML message.
   * 
   * @param string the string to convert
   * 
   * @return Restored string.
   */
  public static final String StringToXML( final String string ) {
    return StringUtil.tokenSubst( StringUtil.XML_ENTITYREFS, StringUtil.notNull( string ), true );
  }




  /**
   * Make a string safe to send as part of an HTML message.
   * 
   * @param string
   * @return Restored string.
   */
  public static final String StringToHTML( final String string ) {
    return StringUtil.tokenSubst( StringUtil.HTML_ENTITYREFS, StringUtil.notNull( string ), true );
  }




  /**
   * Throws an IllegalArgumentException with the given message if null or
   * blank.
   * 
   * @param arg the string to test
   * @param message the message to send back if the string is null or empty
   */
  public static final void assertNotBlank( final String arg, final String message ) {
    if ( arg == null ) {
      throw new IllegalArgumentException( "Null argument not allowed: " + message );
    }

    if ( arg.trim().equals( "" ) ) {
      throw new IllegalArgumentException( "Blank argument not allowed: " + message );
    }
  }




  /**
   * Make sure a string is not null.
   * 
   * @param arg Any string, possibly null
   * 
   * @return An empty string if the original was null, else the original
   */
  public static final String notNull( final String arg ) {
    if ( arg == null ) {
      return new String( "" );
    }

    return arg;
  }




  /**
   * Checks if a string is not null, empty ("") and not only whitespace.
   * 
   * <p>This is a convenience wrapper around isBlank(String) to make code 
   * slightly more readable.</p>
   * 
   * @param str the String to check, may be null
   * 
   * @return <code>true</code> if the String is not empty and not null and not
   *         whitespace
   * 
   * @see #isBlank(String)
   */
  public static boolean isNotBlank( String str ) {
    return !StringUtil.isBlank( str );
  }




  /**
   * Checks if a string is not null, empty ("") and not only whitespace.
   * 
   * @param str the String to check, may be null
   * 
   * @return <code>true</code> if the String is not empty and not null and not
   *         whitespace
   */
  public static boolean isBlank( String str ) {
    int strLen;
    if ( str == null || ( strLen = str.length() ) == 0 ) {
      return true;
    }
    for ( int i = 0; i < strLen; i++ ) {
      if ( ( Character.isWhitespace( str.charAt( i ) ) == false ) ) {
        return false;
      }
    }
    return true;
  }




  /**
   * Convert a number to a letter (1..26) to (a..z).
   * 
   * <p> This method is useful for creating lists that use letters instead of
   * numbers, such as a, b, c, d...instead of 1, 2, 3, 4. Valid numbers are
   * from 1 to 26, corresponding to the 26 letters of the alphabet. </p>
   * 
   * <p> By default, the letter is returned as a lowercase, but if the boolean
   * upperCaseFlag is true, the letter will be returned as an uppercase. </p>
   * 
   * @param number int representing the number to transform
   * @param upperCaseFlag
   * @return java.lang.String representing the character
   * 
   * @throws IllegalArgumentException if the number is out of range (1-26)
   */
  public static final String numberToLetter( final int number, final boolean upperCaseFlag ) throws Exception {
    // add nine to bring the numbers into the right range (in java, a= 10, z
    // = 35)
    if ( ( number < 1 ) || ( number > 26 ) ) {
      throw new IllegalArgumentException( "StringUtil.numberToLetter:The number is out of the proper range (1 to 26) to be converted to a letter." );
    }

    final int modnumber = number + 9;
    char thechar = Character.forDigit( modnumber, 36 );
    if ( upperCaseFlag ) {
      thechar = Character.toUpperCase( thechar );
    }

    return "" + thechar;
  }




  /**
   * replace substrings within string.
   * 
   * @param text the text to scan
   * @param target the string to replace in the text
   * @param desired the string to put in place of the text
   * 
   * @return a string with all the occurrences of the target strings replaced
   *         with the desired strings
   */
  public static final String replace( final String text, final String target, final String desired ) {
    int ch = 0;
    int indx = text.indexOf( target, ch );
    if ( indx == -1 ) {
      return text;
    }

    final StringBuffer buf = new StringBuffer( text.length() + desired.length() );
    do {
      buf.append( text.substring( ch, indx ) );
      buf.append( desired );

      ch = indx + target.length();
    }
    while ( ( indx = text.indexOf( target, ch ) ) != -1 );

    if ( ch < text.length() ) {
      buf.append( text.substring( ch, text.length() ) );
    }

    return buf.toString();
  }




  /**
   * Return if a string is numeric by trying to parse it.
   * 
   * <p> The goal of this method is to give a simple test for whole numbers
   * that are used as identifiers. </p>
   * 
   * @param string the string to check
   * 
   * @return True if the string is a parsable numeric (number) False
   *         otherwise.
   */
  public static final boolean isNumeric( final String string ) {
    if ( string == null ) {
      return false;
    }
    int len = string.length();
    for ( int i = 0; i < len; i++ ) {
      if ( Character.isDigit( string.charAt( i ) ) == false ) {
        return false;
      }
    }
    return true;
  }




  /**
   * Cut or pad the string to the given size
   * 
   * @param str
   * @param size the wanted length
   * @param padByte char to use for padding
   * 
   * @return the string with correct length, padded with pad if necessary
   * 
   * @throws java.io.UnsupportedEncodingException
   */
  public static final byte[] forceToSize( final String str, final int size, final byte padByte ) throws java.io.UnsupportedEncodingException {
    // If the sizes match, return the string unchanged
    if ( ( str != null ) && ( str.length() == size ) ) {
      return str.getBytes();
    }

    final byte[] result = new byte[size];
    // It null, pad the whole thing
    if ( str == null ) {
      for ( int ii = 0; ii < size; ii++ ) {
        result[ii] = padByte;
      }

      return result;
    }

    // Do some cutting
    if ( str.length() > size ) {
      return str.substring( 0, size ).getBytes();
    }

    // Do some padding
    final byte[] tmp = str.getBytes();

    for ( int jj = 0; jj < tmp.length; jj++ ) {
      result[jj] = tmp[jj];
    }

    for ( int kk = tmp.length; kk < size; kk++ ) {
      result[kk] = padByte;
    }

    return result;
  }




  /**
   * This maps a character to a soundex code.
   * 
   * @param ch the character to lookup
   * 
   * @return the soundex character for the given character.
   */
  private static char getSoundexMappingCode( final char ch ) {
    if ( Character.isLetter( ch ) ) {
      final int idx = Character.toUpperCase( ch ) - 'A';

      if ( ( 0 <= idx ) && ( idx < StringUtil.soundex_map.length ) ) {
        return StringUtil.soundex_map[idx];
      }
    }

    return 0;
  }




  /**
   * This will return a 6-character soundex code for the given string.
   * 
   * <p> This is useful in detecting alternate spellings of the same name,
   * helping to detect duplicates. </p>
   * 
   * <p>
   * 
   * <pre> String string[] = { &quot;Stephan&quot;, &quot;Steven&quot;,
   * &quot;Stevens&quot;, &quot;getSoundexMappingCode&quot; }; for (int indx =
   * 0; indx &lt; string.length; indx++) System.out.println(string[indx] +
   * &quot; = &quot; + StringUtil.soundex(string[indx])); </pre>
   * 
   * </p>
   * 
   * @param in The string from which the code is to be generated.
   * 
   * @return the soundex string
   */
  public static String soundex( final String in ) {
    final char out[] = { '0', '0', '0', '0', '0', '0' };

    char last, mapped;
    int incount = 1, count = 1;
    out[0] = Character.toUpperCase( in.charAt( 0 ) );
    last = StringUtil.getSoundexMappingCode( in.charAt( 0 ) );

    while ( ( incount < in.length() ) && ( ( mapped = StringUtil.getSoundexMappingCode( in.charAt( incount++ ) ) ) != 0 ) && ( count < out.length ) ) {
      if ( ( mapped != '0' ) && ( mapped != last ) ) {
        out[count++] = mapped;
      }

      last = mapped;
    }

    return ( new String( out ) );
  }




  /**
   * Build a string with the specified number of spaces.
   * 
   * @param num the number of spaces to return
   * 
   * @return A string with the specified number of spaces.
   */
  public static final String getSpaces( int num ) {
    if ( num < 1 )
      return "";

    final StringBuffer sb = new StringBuffer( num );

    while ( num-- > 0 ) {
      sb.append( " " );
    }

    return sb.toString();
  }




  /**
   * Justify a string to a certain width.
   * 
   * <p> The string is never truncated. </p>
   * 
   * @param width
   * @param string
   * @param method 0 = left justified, 1 = center justified, 2 = right
   *            justified
   * 
   * @return String justified as specified in a particular width field.
   */
  static public final String justifyString( final int width, String string, final int method ) {
    if ( string == null ) {
      string = "";
    }

    int left = 0;
    int right = 0;
    final int extra = width - string.length();

    if ( extra > 0 ) {
      if ( method == 0 ) {
        left = extra;
      } else {
        if ( method == 1 ) {
          left = extra / 2;
          right = extra - left;
        } else {
          right = extra;
        }
      }
    }

    return StringUtil.getSpaces( left ) + string + StringUtil.getSpaces( right );
  }




  /**
   * Center-justify a string to a certain width.
   * 
   * <p> The string is never truncated. </p>
   * 
   * @param width the number of characters wide the string should be
   * @param text the original text
   * 
   * @return String justified as specified in a particular width field.
   */
  static public final String justifyCenter( final int width, final String text ) {
    return StringUtil.justifyString( width, text, 1 );
  }




  /**
   * Left-justify a string to a certain width.
   * 
   * <p> The string is never truncated. </p>
   * 
   * @param width the number of characters wide the string should be
   * @param text the original text
   * 
   * @return String justified as specified in a particular width field.
   */
  static public final String justifyLeft( final int width, final String text ) {
    return StringUtil.justifyString( width, text, 2 );
  }




  /**
   * Right-justify a string to a certain width. Useful in generating column
   * labels.
   * 
   * <p> The string is never truncated. </p>
   * 
   * @param width the number of characters wide the string should be
   * @param text the original text
   * 
   * @return String justified as specified in a particular width field.
   */
  static public final String justifyRight( final int width, final String text ) {
    return StringUtil.justifyString( width, text, 0 );
  }




  /**
   * Safely get an indexed value from a string array returning the default
   * indexed value if the original index is out of bounds.
   * 
   * <p> It is safe insofar as it will not throu an index out of bounds
   * exception. </p>
   * 
   * @param index the index of the string to return
   * @param array the array of strings serving as the source of strings
   * @param defaultIndex the default index of the string to return if the
   *            index is out of bounds
   * 
   * @return The string from the array at the given index or the default index
   *         or null if both indexes were out of bounds.
   */
  public static final String safeGetStringFromArray( final int index, final String[] array, final int defaultIndex ) {
    if ( ( 0 <= index ) && ( index < array.length ) ) {
      return array[index];
    }

    if ( ( 0 <= defaultIndex ) && ( defaultIndex < array.length ) ) {
      return array[defaultIndex];
    }

    return ( array.length != 0 ) ? array[0] : null;
  }




  /**
   * Find the position of a string in an array of strings.
   * 
   * @param search_str
   * @param array
   * @param default_loc
   * 
   * @return the location of search string in the array or default_loc if not
   *          found.
   */
  public static final int findStringInArray( final String search_str, final String[] array, final int default_loc ) {
    int x = default_loc;

    if ( search_str != null ) {
      for ( int indx = 0; indx < array.length; ++indx ) {
        if ( search_str.equalsIgnoreCase( array[indx] ) ) {
          x = indx;

          break;
        }
      }
    }

    if ( ( x < 0 ) || ( array.length <= x ) ) {
      x = default_loc;
    }

    return x;
  }




  /**
   * Performs a null safe check of string equality, useful when comparing two
   * user controlled string variables, either of which might be null.
   * 
   * @param s1 one of the strings to check
   * @param s2 the other string to check
   * 
   * @return true if both strings are null, empty or equivaliant.
   */
  public static final boolean stringsEqual( final String s1, final String s2 ) {
    if ( ( s1 == null ) || ( s1.length() == 0 ) ) {
      return ( s2 == null ) || ( s2.length() == 0 );
    }

    if ( s2 == null ) {
      return false;
    }

    return s1.equals( s2 );
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
  /* * * * * * * * * These methods are still being tested. * * * * * * * * * * */
  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private static final char[] LOWERCASES = { '\000', '\001', '\002', '\003', '\004', '\005', '\006', '\007', '\010', '\011', '\012', '\013', '\014', '\015', '\016', '\017', '\020', '\021', '\022', '\023', '\024', '\025', '\026', '\027', '\030', '\031', '\032', '\033', '\034', '\035', '\036', '\037', '\040', '\041', '\042', '\043', '\044', '\045', '\046', '\047', '\050', '\051', '\052', '\053', '\054', '\055', '\056', '\057', '\060', '\061', '\062', '\063', '\064', '\065', '\066', '\067', '\070', '\071', '\072', '\073', '\074', '\075', '\076', '\077', '\100', '\141', '\142', '\143', '\144', '\145', '\146', '\147', '\150', '\151', '\152', '\153', '\154', '\155', '\156', '\157', '\160', '\161', '\162', '\163', '\164', '\165', '\166', '\167', '\170', '\171', '\172', '\133', '\134', '\135', '\136', '\137', '\140', '\141', '\142', '\143', '\144', '\145', '\146', '\147', '\150', '\151', '\152', '\153', '\154', '\155', '\156', '\157', '\160', '\161', '\162', '\163', '\164', '\165', '\166', '\167', '\170', '\171',
      '\172', '\173', '\174', '\175', '\176', '\177' };




  /**
   * Fast lower case conversion. Only works on ascii (not unicode)
   * 
   * @param string the string to convert
   * 
   * @return a lower case version of string
   */
  public static final String asciiToLowerCase( final String string ) {
    final char[] ch = string.toCharArray();

    for ( int indx = ch.length; indx-- > 0; ) {
      if ( ch[indx] <= 127 ) {
        ch[indx] = StringUtil.LOWERCASES[ch[indx]];
      }
    }

    return ( new String( ch ) );
  }




  /**
   * Returns the next index of a character from the chars string
   * 
   * @param string The string to search
   * @param chars The characters for which to look
   * 
   * @return the position of a the given characters in the string
   */
  public static final int indexFrom( final String string, final String chars ) {
    for ( int indx = 0; indx < string.length(); indx++ ) {
      if ( chars.indexOf( string.charAt( indx ) ) >= 0 ) {
        return indx;
      }
    }

    return -1;
  }




  /**
   * Remove single or double quotes.
   * 
   * @param string
   * @return string
   */
  public static final String unquote( final String string ) {
    if ( ( string.startsWith( "\"" ) && string.endsWith( "\"" ) ) || ( string.startsWith( "'" ) && string.endsWith( "'" ) ) ) {
      return string.substring( 1, string.length() - 1 );
    }

    return string;
  }




  /**
   * Back up in a string to the first carriage return BEFORE the startloc
   * location in the string.
   * 
   * <p>Useful when the indata string contains a whole files worth of data,
   * including the returns.</p>
   * 
   * @param indata data to search.
   * @param startloc starting location within the search string.
   * 
   * @return The string/line just before the starting location.
   */
  public static String BackupToCR( final String indata, final int startloc ) {
    int indx;
    String outstr;
    char cr;

    for ( indx = startloc; indx >= 0; indx-- ) {
      cr = indata.charAt( indx );
      if ( cr == '\n' )
        break;
    }

    if ( indx < 0 )
      indx = 0;

    outstr = new String( indata.substring( indx ) );

    return ( outstr );
  }




  /**
   * Backup to an occurrence of a string return all the data after that
   * occurrence.
   * 
   * @param indata look for the lookfor string in here
   * @param startloc location to start in the indata string
   * @param lookfor string to search for in the indata string
   * 
   * @return String at start of lookfor string, including all data after
   *         lookfor string
   */
  public static String BackupToStr( final String indata, final int startloc, final String lookfor ) {
    int indx;
    final int len = lookfor.length();
    String outstr;

    for ( indx = startloc; indx >= 0; indx-- ) {
      if ( indata.regionMatches( true, indx, lookfor, 0, len ) ) {
        break;
      }
    }

    if ( indx < 0 ) {
      outstr = null;
    } else {
      outstr = new String( indata.substring( indx ) );
    }

    return ( outstr );
  }




  /**
   * Returns the position of the first non-space character in a char array
   * starting a position position.
   * 
   * @param str the char array
   * @param pos the position to start looking
   * 
   * @return the position of the first non-space character
   */
  final static int skipSpace( final char[] str, int pos ) {
    final int len = str.length;

    while ( ( pos < len ) && Character.isWhitespace( str[pos] ) ) {
      pos++;
    }

    return pos;
  }




  /**
   * Returns the position of the first space character in a char array
   * starting at position pos.
   * 
   * @param str the char array
   * @param pos the position to start looking
   * 
   * @return the position of the first space character, or the length of the
   *         string if not found
   */
  final static int findSpace( final char[] str, int pos ) {
    final int len = str.length;

    while ( ( pos < len ) && !Character.isWhitespace( str[pos] ) ) {
      pos++;
    }

    return pos;
  }




  /**
   * Strip characters from the {@code input} string which are contained 
   * in the {@code strip} string.
   * 
   * @param input The string to scan and transform.
   * @param strip The string of characters to remove from the input string.
   * 
   * @return a string representing all the characters from the {@code strip} string removed from the {@code input} string 
   */
  public static String stripChars( String input, String strip ) {
    StringBuilder result = new StringBuilder();
    for ( char c : input.toCharArray() ) {
      if ( strip.indexOf( c ) == -1 )
        result.append( c );
    }
    return result.toString();
  }




  /**
   * Method findInteger
   * 
   * @param string
   * @param startTag
   * @param endTag
   * 
   * @return TODO Complete Documentation
   */
  public static int findInteger( final String string, final String startTag, final String endTag ) {
    final long l = StringUtil.findLong( string, startTag, endTag );

    if ( l <= Integer.MAX_VALUE ) {
      return (int)l;
    } else {
      return -1;
    }
  }




  /**
   * Method findLong
   * 
   * @param string
   * @param startTag
   * @param endTag
   * 
   * @return TODO Complete Documentation
   */
  public static long findLong( final String string, final String startTag, final String endTag ) {
    int indx = string.indexOf( startTag );

    if ( indx < 0 ) {
      return -1L;
    }

    for ( indx += startTag.length(); indx < string.length(); indx++ ) {
      if ( Character.isDigit( string.charAt( indx ) ) ) {
        break;
      }
    }

    if ( indx == string.length() ) {
      return -1L;
    }

    long retval = -1L;

    for ( ; indx < string.length(); indx++ ) {
      final char ch = string.charAt( indx );

      if ( !Character.isDigit( ch ) ) {
        break;
      }

      if ( retval > 0L ) {
        retval *= 10L;
      } else {
        retval = 0L;
      }

      retval += Character.digit( ch, 10 );
    }

    if ( ( endTag != null ) && ( indx < string.length() ) ) {
      final int j = string.indexOf( endTag, indx );

      if ( j < 0 ) {
        retval = -1L;
      }
    }

    return retval;
  }




  /**
   * Format an array of Object as a list with commas.
   * 
   * <p> Example:<br> <code>String[] list = { "apples", "oranges", "pumpkins",
   * "bananas" };
   * System.out.println(StringUtil.arrayToCommaList(list));</code>
   * 
   * @param array
   * 
   * @return TODO Complete Documentation
   */
  public static String arrayToCommaList( final Object[] array ) {
    final StringBuffer sb = new StringBuffer();

    for ( int indx = 0; indx < array.length; indx++ ) {
      if ( ( indx > 0 ) && ( indx < array.length - 1 ) ) {
        sb.append( ',' );
      }

      if ( indx > 0 ) {
        sb.append( ' ' );
      }

      if ( indx == ( array.length - 1 ) ) {
        sb.append( "and " );
      }

      sb.append( array[indx] );
    }

    return sb.toString();
  }




  /**
   * Method getString
   * 
   * @param string
   * @param as
   * @param indx
   * 
   * @return TODO Complete Documentation
   */
  public static String getString( final String string, final String as[], final int indx ) {
    if ( indx >= as.length ) {
      throw new IllegalArgumentException( "missing argument to -".concat( String.valueOf( string ) ) );
    } else {
      return as[indx];
    }
  }




  /**
   * Method asJavaName
   * 
   * @param text
   * 
   * @return TODO Complete Documentation
   */
  public static String asJavaName( final String text ) {
    final StringBuffer retval = new StringBuffer();

    for ( int indx = 0; indx < text.length(); indx++ ) {
      final char ch = text.charAt( indx );

      if ( ( ( indx == 0 ) && Character.isJavaIdentifierStart( ch ) ) || Character.isJavaIdentifierPart( ch ) ) {
        retval.append( ch );
      }
    }

    return retval.toString();
  }




  /**
   * Convert the string to Camel Notation
   * 
   * @param string
   * @param flag
   * 
   * @return TODO Complete Documentation
   */
  private static String asCamelNotation( final String string, final boolean flag ) {
    final StringBuffer retval = new StringBuffer();
    final String text = string.trim();
    boolean toUpper = flag;

    for ( int indx = 0; indx < text.length(); indx++ ) {
      final char ch = text.charAt( indx );

      if ( ch == ' ' ) {
        toUpper = true;
      } else {
        if ( ( ( indx == 0 ) && Character.isJavaIdentifierStart( ch ) ) || Character.isJavaIdentifierPart( ch ) ) {
          if ( toUpper ) {
            retval.append( Character.toUpperCase( ch ) );
            toUpper = false;
          } else {
            retval.append( Character.toLowerCase( ch ) );
          }
        }
      }
    }

    return retval.toString();
  }




  /**
   * Returns the given sentence as a valid Java identifier, using camel
   * notation.
   * 
   * <p> All characters of the name are lowercase unless they are preceded by
   * a space, in which case the space will be removed and the next character
   * will be converted to uppercase. This should make the sentence into a
   * standard Java identifier suitable for JavaBean naming. </p>
   * 
   * @param string the sentence to convert
   * 
   * @return the sentence with all non-java identifier characters removed and
   *         Capitalized as required.
   */
  public static String asCamelNotation( final String string ) {
    return StringUtil.asCamelNotation( string, false );
  }




  /**
   * Returns the given sentence as a valid Java identifier, using camel
   * notation.
   * 
   * <p>All characters of the name are lowercase unless the character is the
   * first character in the sequence or is preceded by a space, in which case
   * the space will be removed and the next character will be converted to
   * uppercase.</p>
   * 
   * @param string the sentence to convert
   * 
   * @return the sentence with all non-java identifier characters removed and
   *         capitalized as required.
   */
  public static String asInitNotation( final String string ) {
    return StringUtil.asCamelNotation( string, true );
  }




  /**
   * When given a fully qualified class name (without the .class extension)
   * this will return the package portion of the class name.
   * 
   * @param classname
   * 
   * @return TODO Complete Documentation
   */
  public static String getJavaPackage( final String classname ) {
    final int indx = classname.lastIndexOf( '.' );
    return ( indx == -1 ) ? null : classname.substring( 0, indx );
  }




  /**
   * Method getLocalJavaName
   * 
   * @param classname
   * 
   * @return TODO Complete Documentation
   */
  public static String getLocalJavaName( final String classname ) {
    return StringUtil.tail( classname, '.' );
  }




  /**
   * Method getLocalFileName
   * 
   * @param file
   * 
   * @return TODO Complete Documentation
   */
  public static String getLocalFileName( final String file ) {
    return StringUtil.tail( file, '/' );
  }




  /**
   * Method isUTF16
   * 
   * @param abyte0
   * 
   * @return TODO Complete Documentation
   */
  public static boolean isUTF16( final byte abyte0[] ) {
    if ( abyte0.length < 2 ) {
      return false;
    } else {
      return ( ( abyte0[0] == -1 ) && ( abyte0[1] == -2 ) ) || ( ( abyte0[0] == -2 ) && ( abyte0[1] == -1 ) );
    }
  }




  /**
   * Method isUTF8
   * 
   * @param string
   * 
   * @return TODO Complete Documentation
   */
  public static boolean isUTF8( final String string ) {
    return ( string == null ) || string.equalsIgnoreCase( "UTF-8" ) || string.equalsIgnoreCase( "UTF8" );
  }




  /**
   * Method normalizeEncoding
   * 
   * @param encoding
   * 
   * @return TODO Complete Documentation
   */
  public static String normalizeEncoding( final String encoding ) {
    return ( ( encoding != null ) && !encoding.equalsIgnoreCase( "UTF-8" ) ) ? encoding : "UTF8";
  }




  /**
   * Method toString
   * 
   * @param abyte0
   * 
   * @return TODO Complete Documentation
   * 
   * @throws UnsupportedEncodingException
   */
  public static String toString( final byte abyte0[] ) throws UnsupportedEncodingException {
    return StringUtil.isUTF16( abyte0 ) ? new String( abyte0, "UTF-16" ) : new String( abyte0, "UTF8" );
  }




  /**
   * Method extension
   * 
   * @param string
   * 
   * @return TODO Complete Documentation
   */
  public static String extension( final String string ) {
    final int indx = string.lastIndexOf( '/' );
    final int j = string.lastIndexOf( '.' );
    return ( j <= indx ) ? null : string.substring( j + 1 );
  }




  /**
   * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
   * 
   * @param s The string to process
   * 
   * @return a whole new string with the appropriate characters escaped
   */
  public static String escape( String s ) {
    if ( s == null )
      return null;
    StringBuffer sb = new StringBuffer();
    escape( s, sb );
    return sb.toString();
  }




  /**
   * Escape the characters in the given string and place the results at the end 
   * of the given string buffer.
   * 
   * @param s the string to process - Must not be null.
   * @param sb the buffer to fill
   */
  static void escape( String s, StringBuffer sb ) {
    final int len = s.length();
    for ( int i = 0; i < len; i++ ) {
      char ch = s.charAt( i );
      switch ( ch ) {
        case '"':
          sb.append( "\\\"" );
          break;
        case '\\':
          sb.append( "\\\\" );
          break;
        case '\b':
          sb.append( "\\b" );
          break;
        case '\f':
          sb.append( "\\f" );
          break;
        case '\n':
          sb.append( "\\n" );
          break;
        case '\r':
          sb.append( "\\r" );
          break;
        case '\t':
          sb.append( "\\t" );
          break;
        case '/':
          sb.append( "\\/" );
          break;
        default:
          //Reference: http://www.unicode.org/versions/Unicode5.1.0/
          if ( ( ch >= '\u0000' && ch <= '\u001F' ) || ( ch >= '\u007F' && ch <= '\u009F' ) || ( ch >= '\u2000' && ch <= '\u20FF' ) ) {
            String ss = Integer.toHexString( ch );
            sb.append( "\\u" );
            for ( int k = 0; k < 4 - ss.length(); k++ ) {
              sb.append( '0' );
            }
            sb.append( ss.toUpperCase() );
          } else {
            sb.append( ch );
          }
      }
    }//for
  }




  /**
   * Return the string after the last occurrence of the given character in the
   * given string.
   * 
   * <p> Useful for getting extensions from filenames. Also used to retrieve
   * the last segment of an IP address. </p>
   * 
   * @param text
   * @param ch
   * 
   * @return TODO Complete Documentation
   */
  public static String tail( final String text, final char ch ) {
    final int indx = text.lastIndexOf( ch );
    return ( indx != -1 ) ? text.substring( indx + 1 ) : text;
  }




  /**
   * Return the string before the last occurrence of the given character in
   * the given string.
   * 
   * <p> Useful for getting the body of a filename. </p>
   * 
   * @param string
   * @param ch
   * 
   * @return TODO Complete Documentation
   */
  public static String head( final String string, final char ch ) {
    final int indx = string.lastIndexOf( ch );
    return ( indx != -1 ) ? string.substring( 0, indx ) : string;
  }




  /**
   * Appends the node to the path with the &quot;/&quot; delimiter
   * 
   * <p> Useful for adding a directory or filename to the end of a directory
   * path. </p>
   * 
   * @param path
   * @param node
   * 
   * @return the path with the node appended delimited with &quot;/&quot;
   */
  public static String splice( final String path, final String node ) {
    if ( node.length() == 0 ) {
      return path;
    }

    if ( path.endsWith( "/" ) && node.startsWith( "/" ) ) {
      return path + node.substring( 1 );
    }

    if ( path.endsWith( "/" ) || node.startsWith( "/" ) ) {
      return path + node;
    }

    if ( path.length() > 0 ) {
      return String.valueOf( ( new StringBuffer( String.valueOf( path ) ) ).append( "/" ).append( node ) );
    } else {
      return node;
    }
  }




  /**
   * Method arrayToString
   * 
   * @param array
   * 
   * @return TODO Complete Documentation
   */
  public static String arrayToString( final Object array ) {
    final int indx = Array.getLength( array );

    if ( indx == 0 ) {
      return "()";
    }

    final StringBuffer stringbuffer = new StringBuffer();
    stringbuffer.append( "( " );

    for ( int j = 0; j < indx; j++ ) {
      if ( j > 0 ) {
        stringbuffer.append( ", " );
      }

      stringbuffer.append( Array.get( array, j ) );
    }

    return stringbuffer.append( " )" ).toString();
  }




  /**
   * Get the string after the last occurrence of &quot;/&quot;
   * 
   * @param uri
   * 
   * @return TODO Complete Documentation
   */
  public static String getURN( final String uri ) {
    final int indx = uri.lastIndexOf( "/" );
    return ( indx != -1 ) ? uri.substring( indx + 1 ) : uri;
  }




  /**
   * Get everything up to the last occurrence of &quot;/&quot; but if there is
   * no &quot;/&quot; in the string, retun an empty string NOT a null.
   * 
   * <p> This is useful in determining the endpoints or servlet contexts in
   * URI or URI strings. </p>
   * 
   * @param uri
   * 
   * @return TODO Complete Documentation
   */
  public static String getEndpoint( final String uri ) {
    final int indx = uri.lastIndexOf( "/" );
    return ( indx != -1 ) ? uri.substring( 0, indx ) : "";
  }




  /**
   * Method readLong
   * 
   * @param string
   * @param indx
   * 
   * @return TODO Complete Documentation
   */
  public static long readLong( final String string, int indx ) {
    long l = -1L;
    final StringBuffer stringbuffer = new StringBuffer();

    for ( final int j = string.length(); indx < j; ) {
      final char ch = string.charAt( indx++ );

      if ( !Character.isDigit( ch ) ) {
        break;
      }

      stringbuffer.append( ch );
    }

    if ( stringbuffer.length() > 0 ) {
      l = Long.valueOf( stringbuffer.toString() ).longValue();
    }

    return l;
  }




  /**
   * Method readColumn
   * 
   * @param text
   * @param col
   * 
   * @return TODO Complete Documentation
   */
  public static String readColumn( final String text, final int col ) {
    final StringTokenizer stringtokenizer = new StringTokenizer( text );

    if ( stringtokenizer.countTokens() >= col ) {
      for ( int indx = 1; indx <= col; indx++ ) {
        final String retval = stringtokenizer.nextToken();

        if ( indx == col ) {
          return retval;
        }
      }
    }

    return "";
  }




  /**
   * Method toFilename
   * 
   * @param string
   * 
   * @return TODO Complete Documentation
   */
  public static String toFilename( final String string ) {
    final StringBuffer stringbuffer = new StringBuffer();

    for ( int indx = 0; indx < string.length(); indx++ ) {
      final char ch = string.charAt( indx );

      if ( Character.isLetterOrDigit( ch ) || ( ch == '.' ) ) {
        stringbuffer.append( ch );
      } else {
        stringbuffer.append( '_' );
        stringbuffer.append( Integer.toString( ch ) );
        stringbuffer.append( '_' );
      }
    }

    return stringbuffer.toString();
  }




  /**
   * Method fromFilename
   * 
   * @param string
   * 
   * @return TODO Complete Documentation
   */
  public static String fromFilename( final String string ) {
    final int indx = string.indexOf( '_' );

    if ( indx == -1 ) {
      return string;
    } else {
      final int j = string.indexOf( '_', indx + 1 );
      final char ch = (char)Integer.parseInt( string.substring( indx + 1, j ) );
      return String.valueOf( ( new StringBuffer( String.valueOf( string.substring( 0, indx ) ) ) ).append( ch ).append( StringUtil.fromFilename( string.substring( j + 1 ) ) ) );
    }
  }




  /**
   * Method substitute
   * 
   * @param as
   * @param as1
   * 
   * @throws IOException
   */
  public static void substitute( final String as[][], final String as1[][] ) throws IOException {
    top:

    for ( int indx = 0; indx < as.length; indx++ ) {
      final String string = as[indx][1];

      if ( ( string.length() <= 0 ) || ( string.charAt( 0 ) != '$' ) ) {
        continue;
      }

      final String tmp = string.substring( 1, string.length() );
      int j = 0;

      do {
        if ( j >= as1.length ) {
          continue top;
        }

        if ( as1[j][0].equals( tmp ) ) {
          as[indx][1] = as1[j][1];

          continue top;
        }

        j++;
      }
      while ( true );
    }
  }




  /**
   * Method getCapitalized
   * 
   * @param string
   * 
   * @return TODO Complete Documentation
   */
  public static String getCapitalized( final String string ) {
    return Character.toUpperCase( string.charAt( 0 ) ) + string.substring( 1 );
  }




  /**
   * Add zeros to the beginning of the given number to make it the requested 
   * length.
   * 
   * <p>The requested string may be longer than requested if the string 
   * representation of the number is longer than the requested length. In 
   * effect, the size argument is therefore a requested minimum size and no 
   * truncation of the given number will occur.</p>
   * 
   * @param num the number to represent
   * @param size the requested length of the string to be returned
   * 
   * @return a string representing the given number padded with zeros to the 
   *         requested length.
   */
  public static String zeropad( final short num, final int size ) {
    return StringUtil.zeropad( (long)num, size );
  }




  /**
   * Add zeros to the beginning of the given number to make it the requested 
   * length.
   * 
   * <p>The requested string may be longer than requested if the string 
   * representation of the number is longer than the requested length. In 
   * effect, the size argument is therefore a requested minimum size and no 
   * truncation of the given number will occur.</p>
   * 
   * @param num the number to represent
   * @param size the requested length of the string to be returned
   * 
   * @return a string representing the given number padded with zeros to the 
   *         requested length.
   */
  public static String zeropad( final int num, final int size ) {
    return StringUtil.zeropad( (long)num, size );
  }




  /**
   * Add zeros to the beginning of the given number to make it the requested 
   * length.
   * 
   * <p>The requested string may be longer than requested if the string 
   * representation of the number is longer than the requested length. In 
   * effect, the size argument is therefore a requested minimum size and no 
   * truncation of the given number will occur.</p>
   * 
   * @param num the number to represent
   * @param size the requested length of the string to be returned
   * 
   * @return a string representing the given number padded with zeros to the 
   *         requested length.
   */
  public static String zeropad( final long num, final int size ) {
    final String value = Long.toString( num );

    if ( value.length() >= size ) {
      return value;
    }

    final StringBuffer buf = new StringBuffer( size );
    for ( int indx = 0; indx++ < ( size - value.length() ); buf.append( '0' ) ) {
      ;
    }

    buf.append( value );

    return buf.toString();
  }




  /**
   * Splits a String on a delimiter into a List of Strings.
   * 
   * @param str the String to split
   * @param delim the delimiter character(s) to join on (null will split on
   *            whitespace)
   * 
   * @return a list of Strings
   */
  public static List<String> split( final String str, final String delim ) {
    List<String> splitList = null;
    StringTokenizer st = null;

    if ( str == null ) {
      return splitList;
    }

    if ( delim != null ) {
      st = new StringTokenizer( str, delim );
    } else {
      st = new StringTokenizer( str );
    }

    if ( ( st != null ) && st.hasMoreTokens() ) {
      splitList = new ArrayList<String>();

      while ( st.hasMoreTokens() ) {
        splitList.add( st.nextToken() );
      }
    }

    return splitList;
  }




  /**
   * Remove all whitespace from the given string.
   * 
   * @param text The text from which the whitespace is to be removed.
   * 
   * @return a copy of the given text string with no whitespace or null of the
   *         passed text was null.
   */
  public static final String removeWhitespace( final String text ) {
    String retval = null;
    if ( text != null ) {
      final char[] chars = new char[text.length()];
      int mrk = 0;

      for ( int i = 0; i < text.length(); i++ ) {
        final char c = text.charAt( i );
        if ( !Character.isWhitespace( c ) ) {
          chars[mrk++] = c;
        }
      }

      if ( mrk > 0 ) {
        final char[] data = new char[mrk];
        for ( int i = 0; i < mrk; data[i] = chars[i++] ) {
          ;
        }

        retval = new String( data );
      } else {
        retval = new String();
      }
    }

    return retval;
  }




  /**
   * Method hexByte.
   * 
   * @param b
   * 
   * @return TODO Complete Documentation
   */
  public static String hexByte( final byte b ) {
    int pos = b;
    if ( pos < 0 ) {
      pos += 256;
    }

    String returnString = new String();
    returnString += Integer.toHexString( pos / 16 );
    returnString += Integer.toHexString( pos % 16 );

    return returnString;
  }




  /**
   * Method getHex.
   * 
   * @param theByte
   * 
   * @return TODO Complete Documentation
   */
  public static String getHex( final byte theByte ) {
    int b = theByte;

    if ( b < 0 ) {
      b += 256;
    }

    String returnString = new String( Integer.toHexString( b ) );

    // add leading 0 if needed
    if ( returnString.length() % 2 == 1 ) {
      returnString = "0" + returnString;
    }

    return returnString;
  }




  /**
   * Remove the leading and trailing quotes from {@code str}. E.g. if str is 
   * '"one two"', then 'one two' is returned.
   *
   * @param str The string from which the leading and trailing quotes
   * should be removed.
   *
   * @return The string without the leading and trailing quotes.
   */
  public static String stripLeadingAndTrailingQuotes( String str ) {
    if ( str.startsWith( "\"" ) ) {
      str = str.substring( 1, str.length() );
    }
    if ( str.endsWith( "\"" ) ) {
      str = str.substring( 0, str.length() - 1 );
    }
    return str;
  }




  /**
   * Remove the hyphens from the beginning of {@code str} and return the new 
   * String.
   *
   * @param str The string from which the hyphens should be removed.
   *
   * @return the new String.
   */
  public static String stripLeadingHyphens( final String str ) {
    if ( str == null ) {
      return null;
    }
    if ( str.startsWith( "--" ) ) {
      return str.substring( 2, str.length() );
    } else if ( str.startsWith( "-" ) ) {
      return str.substring( 1, str.length() );
    }

    return str;
  }




  private static IllegalStateException newIllegalStateException( String charsetName, UnsupportedEncodingException e ) {
    return new IllegalStateException( charsetName + ": " + e );
  }




  /**
   * Constructs a new <code>String</code> by decoding the specified array of bytes using the given charset.
   * 
   * <p>This method catches {@link UnsupportedEncodingException} and re-throws it as {@link IllegalStateException}, which should never happen for a required charset name. Use this method when the encoding is required to be in the JRE.</p>
   * 
   * @param bytes The bytes to be decoded into characters, may be <code>null</code>
   * @param charsetName The name of a required {@link java.nio.charset.Charset}
   * 
   * @return A new <code>String</code> decoded from the specified array of bytes using the given charset, or <code>null</code> if the input byte array was <code>null</code>.
   * 
   * @throws IllegalStateException Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen for a required charset name.
   * 
   * @see String#String(byte[], String)
   */
  public static String newString( byte[] bytes, String charsetName ) {
    if ( bytes == null ) {
      return null;
    }
    try {
      return new String( bytes, charsetName );
    } catch ( UnsupportedEncodingException e ) {
      throw StringUtil.newIllegalStateException( charsetName, e );
    }
  }




  /**
   * Constructs a new <code>String</code> by decoding the specified array of bytes using the UTF-8 charset.
   * 
   * @param bytes The bytes to be decoded into characters
   * 
   * @return A new <code>String</code> decoded from the specified array of bytes using the UTF-8 charset, or <code>null</code> if the input byte array was <code>null</code>.
   * 
   * @throws IllegalStateException Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen since the charset is required.
   */
  public static String newStringUtf8( byte[] bytes ) {
    return StringUtil.newString( bytes, StringUtil.UTF_8 );
  }




  /**
   * Constructs a new <code>String</code> by decoding the specified array of bytes using the ISO-8859-1 charset.
   * 
   * @param bytes The bytes to be decoded into characters, may be <code>null</code>
   * 
   * @return A new <code>String</code> decoded from the specified array of bytes using the ISO-8859-1 charset, or <code>null</code> if the input byte array was <code>null</code>.
   * 
   * @throws IllegalStateException Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen since the charset is required.
   */
  public static String newStringIso8859_1( byte[] bytes ) {
    return StringUtil.newString( bytes, StringUtil.ISO_8859_1 );
  }




  /**
   * Constructs a new <code>String</code> by decoding the specified array of bytes using the US-ASCII charset.
   * 
   * @param bytes The bytes to be decoded into characters
   * 
   * @return A new <code>String</code> decoded from the specified array of bytes using the US-ASCII charset, or <code>null</code> if the input byte array was <code>null</code>.
   * 
   * @throws IllegalStateException Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen since the charset is required.
   */
  public static String newStringUsAscii( byte[] bytes ) {
    return StringUtil.newString( bytes, StringUtil.US_ASCII );
  }




  /**
   * Constructs a new <code>String</code> by decoding the specified array of bytes using the UTF-16 charset.
   * 
   * @param bytes The bytes to be decoded into characters
   * 
   * @return A new <code>String</code> decoded from the specified array of bytes using the UTF-16 charset or <code>null</code> if the input byte array was <code>null</code>.
   * 
   * @throws IllegalStateException Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen since the charset is required.
   */
  public static String newStringUtf16( byte[] bytes ) {
    return StringUtil.newString( bytes, StringUtil.UTF_16 );
  }




  /**
   * Constructs a new <code>String</code> by decoding the specified array of bytes using the UTF-16BE charset.
   * 
   * @param bytes The bytes to be decoded into characters
   * 
   * @return A new <code>String</code> decoded from the specified array of bytes using the UTF-16BE charset, or <code>null</code> if the input byte array was <code>null</code>.
   * 
   * @throws IllegalStateException Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen since the charset is required.
   */
  public static String newStringUtf16Be( byte[] bytes ) {
    return StringUtil.newString( bytes, StringUtil.UTF_16BE );
  }




  /**
   * Constructs a new <code>String</code> by decoding the specified array of bytes using the UTF-16LE charset.
   * 
   * @param bytes The bytes to be decoded into characters
   * 
   * @return A new <code>String</code> decoded from the specified array of bytes using the UTF-16LE charset, or <code>null</code> if the input byte array was <code>null</code>.
   * 
   * @throws IllegalStateException Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen since the charset is required.
   */
  public static String newStringUtf16Le( byte[] bytes ) {
    return StringUtil.newString( bytes, StringUtil.UTF_16LE );
  }




  /**
   * Encodes the given string into a sequence of bytes using the ISO-8859-1 charset, storing the result into a new byte array.
   * 
   * @param string the String to encode, may be <code>null</code>
   * 
   * @return encoded bytes, or <code>null</code> if the input string was <code>null</code>
   * 
   * @throws IllegalStateException Thrown when the charset is missing, which should be never according the the Java specification.
   */
  public static byte[] getBytesIso8859_1( String string ) {
    return StringUtil.getBytesUnchecked( string, StringUtil.ISO_8859_1 );
  }




  /**
   * Encodes the given string into a sequence of bytes using the US-ASCII charset, storing the result into a new byte array.
   * 
   * @param string the String to encode, may be <code>null</code>
   * 
   * @return encoded bytes, or <code>null</code> if the input string was <code>null</code>
   * 
   * @throws IllegalStateException Thrown when the charset is missing, which should be never according the the Java specification.
   */
  public static byte[] getBytesUsAscii( String string ) {
    return StringUtil.getBytesUnchecked( string, StringUtil.US_ASCII );
  }




  /**
   * Encodes the given string into a sequence of bytes using the UTF-16 charset, storing the result into a new byte array.
   * 
   * @param string the String to encode, may be <code>null</code>
   * 
   * @return encoded bytes, or <code>null</code> if the input string was <code>null</code>
   * 
   * @throws IllegalStateException Thrown when the charset is missing, which should be never according the the Java specification.
   */
  public static byte[] getBytesUtf16( String string ) {
    return StringUtil.getBytesUnchecked( string, StringUtil.UTF_16 );
  }




  /**
   * Encodes the given string into a sequence of bytes using the UTF-16BE charset, storing the result into a new byte array.
   * 
   * @param string the String to encode, may be <code>null</code>
   * 
   * @return encoded bytes, or <code>null</code> if the input string was <code>null</code>
   * 
   * @throws IllegalStateException Thrown when the charset is missing, which should be never according the the Java specification.
   */
  public static byte[] getBytesUtf16Be( String string ) {
    return StringUtil.getBytesUnchecked( string, StringUtil.UTF_16BE );
  }




  /**
   * Encodes the given string into a sequence of bytes using the UTF-16LE charset, storing the result into a new byte array.
   * 
   * @param string the String to encode, may be <code>null</code>
   * 
   * @return encoded bytes, or <code>null</code> if the input string was <code>null</code>
   * 
   * @throws IllegalStateException Thrown when the charset is missing, which should be never according the the Java specification.
   */
  public static byte[] getBytesUtf16Le( String string ) {
    return StringUtil.getBytesUnchecked( string, StringUtil.UTF_16LE );
  }




  /**
   * Encodes the given string into a sequence of bytes using the UTF-8 charset, storing the result into a new byte array.
   * 
   * @param string the String to encode, may be <code>null</code>
   * 
   * @return encoded bytes, or <code>null</code> if the input string was <code>null</code>
   * 
   * @throws IllegalStateException Thrown when the charset is missing, which should be never according the the Java specification.
   */
  public static byte[] getBytesUtf8( String string ) {
    return StringUtil.getBytesUnchecked( string, StringUtil.UTF_8 );
  }




  /**
   * Encodes the given string into a sequence of bytes using the named charset, storing the result into a new byte array.
   * <p> This method catches {@link UnsupportedEncodingException} and rethrows it as {@link IllegalStateException}, which should never happen for a required charset name. Use this method when the encoding is required to be in the JRE.</p>
   * 
   * @param string the String to encode, may be <code>null</code>
   * @param charsetName The name of a required {@link java.nio.charset.Charset}
   * 
   * @return encoded bytes, or <code>null</code> if the input string was <code>null</code>
   * 
   * @throws IllegalStateException Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen for a required charset name.
   */
  public static byte[] getBytesUnchecked( String string, String charsetName ) {
    if ( string == null ) {
      return null;
    }
    try {
      return string.getBytes( charsetName );
    } catch ( UnsupportedEncodingException e ) {
      throw StringUtil.newIllegalStateException( charsetName, e );
    }
  }




  /**
   * Tokenize the given String into a String array via a StringTokenizer.
   * 
   * <p>The given delimiters string is supposed to consist of any number of
   * delimiter characters. Each of those characters can be used to separate
   * tokens. A delimiter is always a single character; for multi-character
   * delimiters, consider using {@code delimitedListToStringArray}</p>
   * 
   * @param str the String to tokenize
   * @param delimiters the delimiter characters, assembled as String (each of those characters is individually considered as delimiter)
   * @param trimTokens trim the tokens via String's {@code trim}
   * @param ignoreEmptyTokens omit empty tokens from the result array (only applies to tokens that are empty after trimming; StringTokenizer will not consider subsequent delimiters as token in the first place).
   * 
   * @return an array of the tokens ({@code null} if the input String was {@code null})
   */
  public static String[] tokenizeToStringArray( String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens ) {

    if ( str == null ) {
      return null;
    }
    StringTokenizer st = new StringTokenizer( str, delimiters );
    List<String> tokens = new ArrayList<String>();
    while ( st.hasMoreTokens() ) {
      String token = st.nextToken();
      if ( trimTokens ) {
        token = token.trim();
      }
      if ( !ignoreEmptyTokens || token.length() > 0 ) {
        tokens.add( token );
      }
    }
    return toStringArray( tokens );
  }




  /**
   * Copy the given Collection into a String array.
   * 
   * <p>The Collection must contain String elements only.</p>
   * 
   * @param collection the Collection to copy
   * 
   * @return the String array ({@code null} if the passed-in Collection was 
   * {@code null})
   */
  public static String[] toStringArray( Collection<String> collection ) {
    if ( collection == null ) {
      return null;
    }
    return collection.toArray( new String[collection.size()] );
  }




  /**
   * Count the occurrences of the substring in string s.
   * 
   * @param str string to search in. Return 0 if this is null.
   * @param sub string to search for. Return 0 if this is null.
   */
  public static int countOccurrencesOf( String str, String sub ) {
    if ( str == null || sub == null || str.length() == 0 || sub.length() == 0 ) {
      return 0;
    }
    int count = 0;
    int pos = 0;
    int idx;
    while ( ( idx = str.indexOf( sub, pos ) ) != -1 ) {
      ++count;
      pos = idx + sub.length();
    }
    return count;
  }
}
