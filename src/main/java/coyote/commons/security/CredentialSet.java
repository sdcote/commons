/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;


/**
 * Represents a set of data for authentication operations.
 * 
 * <p>Modern systems use more than just username and password for 
 * authentication. In order to support these system and the security operations 
 * of more sophisticated systems, this class supports a set of credential data 
 * to be used in various ways.</p>
 * 
 * <p>Data in this class is stored as bytes so future systems can store a 
 * variety of data (e.g. biometric) in this class. This class can even help 
 * with public-private key management.</p>
 * 
 * <p>Credentials should not be stored in memory in their raw state to help
 * prevent accidental exposure should a memory dump occurs or memory become
 * accessible as might be the case in an overflow attack. This class supports
 * the ability to only store the hashed or digested representation of the 
 * credential data by setting the number of rounds of digest calculations are 
 * performed on credentials before they are stored and checked.</p> 
 */
public class CredentialSet {

  private Hashtable<String, byte[]> _credentials = new Hashtable<String, byte[]>();

  private int _rounds = 0;

  private static final String MD5 = "MD5";
  private static final String UTF8 = "UTF8";

  // Identifier key
  public static final String IDENT = "identifier";

  // Account key
  public static final String ACCOUNT = "account";

  // Password key
  public static final String PASSWORD = "password";

  // Public key for encryption
  public static final String PUBLICKEY = "publickey";

  // Private key for encryption
  public static final String PRIVATEKEY = "privatekey";

  // An activation token
  public static final String ACTIVATION = "activation";

  static {
    try {
      MessageDigest md = MessageDigest.getInstance( MD5 );
    } catch ( NoSuchAlgorithmException e ) {
      e.printStackTrace();
    }
    try {
      UTF8.getBytes( UTF8 );
    } catch ( UnsupportedEncodingException e ) {
      e.printStackTrace();
    }
  }




  /**
   * Constructor CredentialSet
   */
  public CredentialSet() {}




  /**
   * Create a credential set using the given number of rounds.
   * 
   * <p>In order to prevent keeping a copy of the credential data in memory in 
   * an accessible format, the credential set can pass the credential data 
   * through a MD5 digest and store the digest value instead of the actual 
   * credential data. Setting the number of rounds to 1 (or more) will result 
   * in this class using the MD5 has of the credential.</p>
   * 
   * @param rounds Number of rounds of MD5 digests to perform.  0 equals using the raw (unsecure) credentials, 1=single round of digest (more secure).
   */
  public CredentialSet( int rounds ) {
    if ( rounds < 0 )
      throw new IllegalArgumentException( "Number of rounds cannot be negative" );

    _rounds = rounds;
  }




  /**
   * Retrieve the number of times credential values are to be passed through 
   * digest calculations. 
   *
   * @return the number of rounds of MD5 to perform on credentials
   */
  public int getRounds() {
    return _rounds;
  }




  /**
   * Add the named data to these credentials
   * 
   * <p>If rounds are set to 1 or more, the credential data will be stored as 
   * an MD5 digest.</p> 
   * 
   * @param name The name of the credential
   * @param bytes the bytes representing the credential
   */
  public void add( String name, byte[] bytes ) {
    byte[] val = bytes;

    if ( _rounds > 0 ) {
      MessageDigest md = null;
      try {
        md = MessageDigest.getInstance( MD5 );
      } catch ( NoSuchAlgorithmException e ) {}

      if ( md != null ) {
        // make sure all the credentials are hashed even if added after digest
        for ( int x = 0; x < _rounds; x++ ) {
          val = md.digest( val );
        }
      }
    }

    // add the data
    _credentials.put( name, val );
  }




  /**
   * Constructor which populates an account name and a password.
   * 
   * <p>This is a convenience method for one of the most common use case 
   * scenarios for this class; username / account and a password.</p>
   * 
   * <p>The account and password values are stored in UTF8 encoding, again 
   * addressing the most common character sets.</p>
   *
   * @param acct The name of the account
   * @param passwd The authenticating data
   */
  public CredentialSet( String acct, String passwd ) {
    try {
      add( ACCOUNT, acct.getBytes( UTF8 ) );
      add( PASSWORD, passwd.getBytes( UTF8 ) );
    } catch ( UnsupportedEncodingException e ) {}
  }




  public boolean contains( String name ) {
    return _credentials.containsKey( name );
  }

}