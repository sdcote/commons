package coyote.commons.cli;

/**
 * Contains useful helper methods for classes within this package.
 */
class Util {
  /**
   * Remove the leading and trailing quotes from <code>str</code>.
   * E.g. if str is '"one two"', then 'one two' is returned.
   *
   * @param str The string from which the leading and trailing quotes
   * should be removed.
   *
   * @return The string without the leading and trailing quotes.
   */
  static String stripLeadingAndTrailingQuotes( String str ) {
    if ( str.startsWith( "\"" ) ) {
      str = str.substring( 1, str.length() );
    }
    if ( str.endsWith( "\"" ) ) {
      str = str.substring( 0, str.length() - 1 );
    }
    return str;
  }




  /**
   * Remove the hyphens from the beginning of <code>str</code> and
   * return the new String.
   *
   * @param str The string from which the hyphens should be removed.
   *
   * @return the new String.
   */
  static String stripLeadingHyphens( final String str ) {
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
}
