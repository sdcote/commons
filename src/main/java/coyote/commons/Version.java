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

/**
 * Simple version reporting mechanism.
 * 
 * @see http://semver.org/
 */
public class Version {

  private int major;
  private int minor;
  private int patch;
  private short release;

  public static final short GENERAL = 4;
  public static final short BETA = 3;
  public static final short ALPHA = 2;
  public static final short DEVELOPMENT = 1;
  public static final short EXPERIMENTAL = 0;

  private static final String[] releaseNames = { "exp", "dev", "alpha", "beta", "ga" };




  /**
   * Constructor Version
   */
  public Version() {
    this.major = 0;
    this.minor = 0;
    this.patch = 0;
    this.release = GENERAL;
  }




  /**
   * Constructor Version
   *
   * @param maj
   * @param min
   * @param pch
   */
  public Version( int maj, int min, int pch ) {
    major = maj;
    minor = min;
    patch = pch;
    release = GENERAL;
  }




  public Version( int maj, int min, int pch, short rls ) {
    major = maj;
    minor = min;
    patch = pch;
    if ( rls > -1 && rls <= GENERAL ) {
      release = rls;
    }
  }




  /**
   * Method createVersion
   *
   * @param text
   *
   * @return TODO finish documentation.
   */
  public static Version createVersion( String text ) {
    Version retval = new Version();

    if ( text != null ) {
      int mark = 0;
      int mode = 0;

      for ( int i = 0; i < text.length(); i++ ) {
        if ( ( text.charAt( i ) == '.' ) || ( text.charAt( i ) == ' ' ) || ( text.charAt( i ) == '-' ) ) {
          try {
            switch ( mode ) {

              case 0:
                retval.setMajor( Integer.parseInt( text.substring( mark, i ) ) );
                break;

              case 1:
                retval.setMinor( Integer.parseInt( text.substring( mark, i ) ) );
                break;

              case 2:
                retval.setPatch( Integer.parseInt( text.substring( mark, i ) ) );
                break;
            }
          } catch ( NumberFormatException nfe ) {
            return retval;
          }

          mode++;

          mark = i + 1;
        }
      }

      try {
        // Now finishup
        switch ( mode ) {

          case 0:
            retval.setMajor( Integer.parseInt( text.substring( mark ) ) );
            break;

          case 1:
            retval.setMinor( Integer.parseInt( text.substring( mark ) ) );
            break;

          case 2:
            retval.setPatch( Integer.parseInt( text.substring( mark ) ) );
            break;
        }

      } catch ( Exception ex ) {

      }

    }

    return retval;
  }




  /**
   * Method getMajor
   *
   * @return TODO finish documentation.
   */
  public int getMajor() {
    return major;
  }




  /**
   * Method getMinor
   *
   * @return TODO finish documentation.
   */
  public int getMinor() {
    return minor;
  }




  /**
   * Method getPatch
   *
   * @return TODO finish documentation.
   */
  public int getPatch() {
    return patch;
  }




  /**
   * Method setMajor
   *
   * @param i
   */
  public void setMajor( int i ) {
    major = i;
  }




  /**
   * Method setMinor
   *
   * @param i
   */
  public void setMinor( int i ) {
    minor = i;
  }




  /**
   * Method setPatch
   *
   * @param i
   */
  public void setPatch( int i ) {
    patch = i;
  }




  /**
   * Method toString
   *
   * @return TODO finish documentation.
   */
  public String toString() {
    StringBuffer retval = new StringBuffer( major + "." + minor );

    if ( patch > 0 ) {
      retval.append( "." + patch );
    }

    if ( release < GENERAL ) {
      retval.append( "-" + getReleaseString( release ) );
    }

    return retval.toString();
  }




  public static String getReleaseString( short code ) {
    if ( code < GENERAL ) {
      return releaseNames[code];
    }
    return releaseNames[GENERAL];
  }




  /**
   * @return Returns the release.
   */
  public short getRelease() {
    return release;
  }




  /**
   * @param release The release to set.
   */
  public void setRelease( short release ) {
    if ( release > -1 && release >= GENERAL ) {
      this.release = release;
    }
  }




  /**
   * Tests to see if this version is logically greater than or equal to the
   * given version.
   *
   * @param std The version against which we test this object.
   * @return True if this version is logically greater than or equal to the
   *         given version, false if this version is less than the argument.
   */
  public boolean isAtLeast( Version std ) {
    if ( major > std.major ) {
      return true;
    } else {
      if ( major == std.major ) {
        if ( minor > std.minor ) {
          return true;
        } else {
          if ( minor == std.minor ) {
            if ( patch > std.patch ) {
              return true;
            }
          }
        }
      }
    }

    return false;
  }




  /**
   * Tests to see if this version is logically less than or equal to the given
   * version.
   *
   * @param std The version against which we test this object.
   * 
   * @return True if this version is logically less than or equal to the given
   *         version, false if this version is greater than the argument.
   */
  public boolean isAtMost( Version std ) {
    if ( major < std.major ) {
      return true;
    } else {
      if ( major == std.major ) {
        if ( minor < std.minor ) {
          return true;
        } else {
          if ( minor == std.minor ) {
            if ( patch < std.patch ) {
              return true;
            }
          }
        }
      }
    }

    return false;
  }




  /**
   * Tests to see if this version is logically equal to the given version.
   *
   * @param std The version against which we test this object.
   * 
   * @return True if this version is logically equal to the given version, false
   *         if this version is greater or less than the argument.
   */
  public boolean equals( Version std ) {
    return ( ( major == std.major ) && ( minor == std.minor ) && ( patch == std.patch ) && ( release == std.release ) );
  }

}