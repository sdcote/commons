package coyote.commons.cli;

/**
 * Validates an Option string.
 */
class OptionValidator {
  /**
   * Returns whether the specified character is a valid character.
   *
   * @param c the character to validate
   * @return true if <code>c</code> is a letter.
   */
  private static boolean isValidChar( final char c ) {
    return Character.isJavaIdentifierPart( c );
  }




  /**
   * Returns whether the specified character is a valid Option.
   *
   * @param c the option to validate
   * @return true if <code>c</code> is a letter, ' ', '?' or '@',
   *         otherwise false.
   */
  private static boolean isValidOpt( final char c ) {
    return isValidChar( c ) || ( c == ' ' ) || ( c == '?' ) || ( c == '@' );
  }




  /**
   * Validates whether <code>opt</code> is a permissible Option
   * shortOpt.  The rules that specify if the <code>opt</code>
   * is valid are:
   *
   * <ul>
   *  <li><code>opt</code> is not NULL</li>
   *  <li>a single character <code>opt</code> that is either
   *  ' '(special case), '?', '@' or a letter</li>
   *  <li>a multi character <code>opt</code> that only contains
   *  letters.</li>
   * </ul>
   *
   * @param opt The option string to validate
   * @throws IllegalArgumentException if the Option is not valid.
   */
  static void validateOption( final String opt ) throws IllegalArgumentException {
    // check that opt is not NULL
    if ( opt == null ) {
      return;
    }

    // handle the single character opt
    else if ( opt.length() == 1 ) {
      final char ch = opt.charAt( 0 );

      if ( !isValidOpt( ch ) ) {
        throw new IllegalArgumentException( "illegal option value '" + ch + "'" );
      }
    }

    // handle the multi character opt
    else {
      final char[] chars = opt.toCharArray();

      for ( int i = 0; i < chars.length; i++ ) {
        if ( !isValidChar( chars[i] ) ) {
          throw new IllegalArgumentException( "opt contains illegal character value '" + chars[i] + "'" );
        }
      }
    }
  }
}
