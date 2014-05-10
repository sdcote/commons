/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial API and implementation
 */
package coyote.commons.network;

import java.net.InetAddress;


/**
 * Models an IP network with a netmask.
 *
 * <p>The 3 blocks of private addresses, as specified in IANA (Internet
 * Assigned Numbers Authority) are:<ul>
 * <li>10.0.0.0     - 10.255.255.255  (10/8 prefix)</li>
 * <li>172.16.0.0   - 172.31.255.255  (172.16/12 prefix)</li>
 * <li>192.168.0.0  - 192.168.255.255 (192.168/16 prefix)</li>
 * </ul>
 * </p>
 *
 * TODO Validate the mask to disallow masks like 255.22.255.0 - the segment of 22 is invalid or that last 255 should be 0
 */
public class IpNetwork extends IpAddress {
  private static short[] BITS = { 0, 128, 192, 224, 240, 248, 252, 254, 255 };

  private IpAddress netmask = null;
  private static short[] HOSTMASK = { 255, 255, 255, 255 };




  /**
   * Creates a new instance of IpNetwork
   *
   * @param net the network address like: 192.168.0.0
   * @param mask the mask address like: 255.255.0.0
   */
  public IpNetwork( IpAddress net, IpAddress mask ) {
    super( net );

    netmask = (IpAddress)mask.clone();
  }




  /**
   * Creates a new instance of IpNetwork
   *
   * @param net String representing a network address like: 192.168.0.0
   * @param mask String representing a mask address like: 255.255.0.0
   *
   * @throws IpAddressException
   */
  public IpNetwork( String net, String mask ) throws IpAddressException {
    super( net );

    netmask = new IpAddress( mask );
  }




  /**
   * Creates a new instance of IpNetwork using Class-less Inter-Domain Routing
   * (CIDR) notation.
   *
   * <p>A CIDR address includes the standard 32-bit IP address and also
   * information on how many bits are used for the network prefix. For example,
   * in the CIDR address 206.13.01.48/25, the "/25" indicates the first 25 bits
   * are used to identify the unique network leaving the remaining bits to
   * identify the specific host.</p>
   *
   * @param prefix CIDR notation of the network block
   *
   * @throws IpAddressException
   */
  public IpNetwork( String prefix ) throws IpAddressException {
    // Look for the '/' delimiting the bitmask count
    int mark = prefix.indexOf( '/' );

    if( mark > 0 ) {
      try {
        netmask = new IpAddress( getOctets( Integer.parseInt( prefix.substring( mark + 1 ) ) ) );
        octets = getOctets( prefix.substring( 0, mark ) );
      }
      catch( Exception ex ) {
        throw new IpAddressException( "Invalid network block" );
      }
    } else {
      throw new IpAddressException( "Could not find bitmask count" );
    }
  }




  public boolean equals( Object obj ) {
    if( obj != null && obj instanceof IpNetwork ) {
      return super.equals( (IpAddress)obj );
    }
    return false;
  }




  /**
   * @return an IpNetwork which represents only the local host.
   */
  public static IpNetwork getLocalHost() {
    return new IpNetwork( IpInterface.getPrimary().getAddress(), new IpAddress( HOSTMASK ) );
  }




  /**
   * Checks to see of the given IpAddress is within this network.
   *
   * @param addr the address to check
   *
   * @return true if the address is in this subnet, false otherwise
   */
  public boolean contains( IpAddress addr ) {
    boolean retval = false;

    if( ( addr != null ) || ( netmask != null ) ) {
      IpAddress ip = new IpAddress( addr );
      ip.applyNetMask( netmask );

      retval = ip.equals( this );
    }

    return retval;

  }




  /**
   * Checks to see of the given IpAddress is within this network.
   *
   * @param addr the address to check
   *
   * @return true if the address is in this subnet, false otherwise
   */
  public boolean contains( String addr ) {
    try {
      return contains( new IpAddress( addr ) );
    }
    catch( Exception e ) {
      return false;
    }
  }




  /**
   * Checks to see of the given IpAddress is within this network.
   *
   * @param addr the address to check
   *
   * @return true if the address is in this subnet, false otherwise
   */
  public boolean contains( InetAddress addr ) {
    try {
      return contains( new IpAddress( addr ) );
    }
    catch( Exception e ) {
      return false;
    }
  }




  /**
   * Given the current address, get the broadcast address for the specified
   * netmask.
   *
   * @return an IpAddress representing the broadcast address for this network
   *
   */
  public IpAddress getBroadcastAddress() {
    short[] mask = netmask.getOctets();
    short[] result = new short[octets.length];

    for( int i = 0; i < mask.length; i++ ) {
      result[i] = octets[i];
      result[i] &= mask[i];

      mask[i] = (byte)( ~mask[i] & 0xffff );
      result[i] |= mask[i];

      if( result[i] < 0 ) {
        result[i] = (short)( 256 - ( (byte)( result[i] ) * -1 ) );
      }
    }

    IpAddress retval = null;

    try {
      retval = new IpAddress( result );
    }
    catch( Exception e ) {
      // should always work
    }

    return retval;
  }




  /**
   * This is a convenience method to check if a specified address resides in a
   * specified network.
   *
   * @param address
   * @param network
   * @param netmask
   *
   * @return
   */
  public static boolean checkAddressInNetwork( String address, String network, String netmask ) {
    boolean retval = false;

    if( ( address == null ) || ( network == null ) || ( netmask == null ) ) {
      return false;
    }

    try {
      IpAddress ip = new IpAddress( address );
      ip.applyNetMask( netmask );

      retval = ip.equals( network );
    }
    catch( IpAddressException ipe ) {}

    return retval;
  }




  /**
   * Method getOctets
   *
   * @param num_bits_desired
   *
   * @return
   */
  public static short[] getOctets( int num_bits_desired ) {
    short[] retval = new short[IP4_OCTETS];

    if( num_bits_desired > 32 ) {
      retval = new short[IP6_OCTETS];
    }

    if( num_bits_desired > 0 ) {
      int extra_bits = num_bits_desired % 8;
      int fullBytes = num_bits_desired / 8;

      // For each full octet, create a short value of 255
      for( int i = 0; i < fullBytes; ++i ) {
        retval[i] = 255;
      }

      // If we have extra bits to place
      if( extra_bits != 0 ) {
        retval[fullBytes] = BITS[extra_bits];
      }

      // populate the rest of the short elements
      for( int i = fullBytes + 1; i < retval.length; ++i ) {
        retval[i] = 0;
      }

    } else {
      // populate the short elements with all zeros
      for( int i = 0; i < retval.length; ++i ) {
        retval[i] = 0;
      }
    }

    return retval;
  }




  /**
   * Return the string representation of the network in CIDR format.
   *
   * @return
   */
  public String toString() {
    StringBuffer buf = new StringBuffer();
    short[] addr = getOctets();

    int last = addr.length - 1;

    // Find the last occurance of a non-zero segment
    for( ; addr[last] == 0; last-- );

    // Concatenate the segments up to and including the last non-zero value
    for( int i = 0; i <= last; i++ ) {
      buf.append( addr[i] );

      if( i != last ) {
        buf.append( '.' );
      }
    }

    // Delimit the bitmask size
    buf.append( '/' );

    // Figure out the number of bits set in the mask
    short[] mask = netmask.getOctets();
    int bitcount = 0;

    for( int i = 0; i < mask.length; i++ ) {
      for( int x = 0; x < BITS.length; x++ ) {
        if( BITS[x] == mask[i] ) {
          bitcount += x;

          break;
        }
      }
    }

    // append the number of bits in the mask
    buf.append( bitcount );

    // Return the CIDR block format
    return buf.toString();
  }

}
