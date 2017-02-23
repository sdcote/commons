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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.ServerSocket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;

import coyote.commons.network.IpAddress;
import coyote.commons.network.IpNetwork;


/**
 * Network utility functions
 */
public class NetUtil {
  private static String[] dnsServerList = null;
  private static boolean dnsProbedFlag;

  private static InetAddress localAddress = null;
  private static String cachedLocalHostName = null;

  final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

  /** System property which specifies the user name for the proxy server */
  public static final String PROXY_USER = "http.proxyUser";

  /** System property which specifies the user password for the proxy server */
  public static final String PROXY_PASSWORD = "http.proxyPassword";

  /** System property which specifies the proxy server host name */
  public static final String PROXY_HOST = "http.proxyHost";




  /**
   * Private constructor because everything is static
   */
  private NetUtil() {}




  /**
   * Check if system properties have data needed for loading an Authenticator
   * in the JVM.
   * 
   * <p>A network authenticator will handle proxy authentications for all 
   * connections in the JVM. This method looks for the following properties:
   * <ul>
   * <li><strong>http.proxyUser</strong> - the user name credential</li>
   * <li><strong>http.proxyPassword</strong> - the password credential</li>
   * <li><strong>http.proxyHost</strong> - the host name of the network proxy</li>
   * </ul>
   * 
   * <p>The optional <strong>http.proxyPort</strong> property is only needed 
   * by the JVM if the port is not the default, but it is a good practice to 
   * set it.
   * 
   * @return true of an authenticator was installed, false otherwise.
   */
  public static boolean installProxyAuthenticatorIfNeeded() {
    final String user = System.getProperty( PROXY_USER );
    final String password = System.getProperty( PROXY_PASSWORD );
    final String host = System.getProperty( PROXY_HOST );
    if ( StringUtil.isNotBlank( user ) && StringUtil.isNotBlank( password ) && StringUtil.isNotBlank( host ) ) {
      Authenticator.setDefault( new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication( user, password.toCharArray() );
        }
      } );
      return true;
    }
    return false;
  }




  /**
   * Just test to see if the given string can be parse into a URI.
   *
   * @param uri The string to parse
   *
   * @return the URI or null if the string could not be parsed.
   */
  public static URI validateURI( String uri ) {
    try {
      // We could try to open it, but even if it succeeds now, it may fail
      // later or if it fails right now, it may succeede later. So what is the
      // use?
      return new URI( uri );
    } catch ( Exception mfue ) {
      // System.err.println( "NetUtil.validateURI(String) The URI \"" +
      // uri + "\" is not valid:\n" );
    }

    return null;
  }




  /**
   * Return a port number that can be used to create a socket on the given
   * address starting with port 1.
   *
   * @param address The address on which to find the next available port 
   *
   * @return the next available port on the given address
   */
  public static int getNextAvailablePort( InetAddress address ) {
    return getNextAvailablePort( address, 1 );
  }




  /**
   * Return a port number that can be used to create a socket with the given
   * port on the local address.
   * 
   * <p>If you are looking for port 80, but it is taken, this method will 
   * return the next higher port starting with that port.  This will keep your 
   * ports grouped together.
   *
   * @param port The number of the port on which to start looking
   *
   * @return the next available port on the local address.
   */
  public static int getNextAvailablePort( int port ) {
    return getNextAvailablePort( null, port );
  }




  /**
   * Return a port number that can be used to create a socket on the given
   * address starting with the given port.
   *
   * <p>If the given port can be used to create a server socket (TCP) then that
   * port number will be used, otherwise, the port number will be incremented
   * and tested until a free port is found.
   *
   * <p>This is not thread-safe nor fool-proof. A valid value can be returned,
   * yet when a call is made to open a socket at that port, another thread may
   * have already opened a socket on that port. A better way would be to use
   * the <code>getNextServerSocket(address,port)</code> method if it desired to
   * obtain the next available server.
   *
   * @param address The address on which to find the next available port 
   * @param port The number of the port on which to start looking
   *
   * @return the next available port on the given address.
   */
  public static int getNextAvailablePort( InetAddress address, int port ) {
    ServerSocket socket = getNextServerSocket( address, port, 0 );
    int retval = -1;

    if ( socket != null ) {
      // Get the port as a return value
      retval = socket.getLocalPort();

      // Close the un-needed socket
      try {
        socket.close();
      } catch ( IOException e ) {
        // Ignore it
      }
    }

    return retval;
  }




  /**
   * Return a TCP server socket on the given address and port, incrementing the
   * port until a server socket can be opened.
   *
   * @param address The address on which to find the next available port 
   * @param port The number of the port on which to start looking
   * @param backlog requested maximum length of the queue of incoming connections.
   *
   * @return the next available port on the local address.
   */
  public static ServerSocket getNextServerSocket( InetAddress address, int port, int backlog ) {
    int i = port;
    ServerSocket socket = null;

    // If no address was given, then try to determine our local address so
    // we
    // can use our main address instead of 127.0.0.1 which may be chosen by
    // the
    // VM if it is not specified in the ServerSocket constructor
    if ( address == null ) {
      address = getLocalAddress();
    }

    while ( validatePort( i ) != 0 ) {
      try {
        if ( address == null ) {
          socket = new ServerSocket( i, backlog );
        } else {
          socket = new ServerSocket( i, backlog, address );
        }

        if ( socket != null ) {
          return socket;
        }

      } catch ( IOException e ) {
        i++;
      }
    }

    return null;
  }




  /**
   * Get the IP Address by which the rest of the world knows us.
   *
   * <p>This is useful in helping insure that we don't accidently start binding
   * to or otherwise using the local loopback address.
   *
   * <p>This requires some type of IP address resolver to be installed, like
   * DNS, NIS or at least hostname lookup.
   *
   * @return The InetAddress representing the host on the network and NOT the
   *         loopback address.
   */
  public static InetAddress getLocalAddress() {
    // If we already looked this up, use the cached result to save time
    if ( localAddress != null ) {
      return localAddress;
    }

    // No cached result, figure it out and cache it for later
    InetAddress addr = null;

    // Make sure we get the IP Address by which the rest of the world knows
    // us
    // or at least, our host's default network interface
    try {
      // This helps insure that we do not get localhost (127.0.0.1)
      addr = InetAddress.getByName( InetAddress.getLocalHost().getHostName() );
    } catch ( UnknownHostException e ) {
      // Aaaaww Phooey! DNS is not working or we are not in it.
      addr = null;
    }

    // If it looks like a unique address, return it, otherwise try again
    if ( ( addr != null ) && !addr.getHostAddress().equals( "127.0.0.1" ) && !addr.getHostAddress().equals( "0.0.0.0" ) ) {
      localAddress = addr;

      return addr;
    }

    // Try it the way it's supposed to work
    try {
      addr = InetAddress.getLocalHost();
    } catch ( Exception ex ) {
      addr = null;
    }

    localAddress = addr;

    return addr;
  }




  /**
   * Get the IP Address by which the rest of the world knows us as a string.
   *
   * <p>This is useful in helping insure that we don't accidently start binding
   * to or otherwise using the local loopback address.
   *
   * <p>This requires some type of IP address resolver to be installed, like
   * DNS, NIS or at least hostname lookup.
   *
   * @return The InetAddress representing the host on the network and NOT the
   *         loopback address.
   */
  public static String getLocalAddressString() {
    try {
      return getLocalAddress().getHostAddress();
    } catch ( RuntimeException e ) {}

    return null;
  }




  /**
   * Checks the string address as representing an InetAddress capable of being
   * used as a ServerSocket by binding a socket and returning the InetAddress it
   * represents.
   *
   * @param address String representation of an IP address.
   *
   * @return The InetAddress of the string if it can be used for a ServerSocket,
   *         null otherwise.
   */
  public static InetAddress validateBindAddress( String address ) {
    InetAddress temp = resolveAddress( address );
    return validateBindAddress( temp );
  }




  /**
   * Checks the address as being capable of being used as a ServerSocket by
   * binding a socket to that port.
   *
   * @param address String representation of an IP address.
   *
   * @return The InetAddress if it can be used for a ServerSocket, null
   *         otherwise.
   */
  public static InetAddress validateBindAddress( InetAddress address ) {
    try {
      ServerSocket socket = new ServerSocket( 0, 0, address );
      socket.close();

      return address;
    } catch ( IOException e ) {}

    return null;
  }




  /**
   * Resolve the given string into an actual IP address.
   * 
   * @param address the dotted quad address to resolve
   *
   * @return the actual IP address or null if the string could not be resolved
   */
  public static InetAddress resolveAddress( String address ) {
    try {
      return InetAddress.getByName( address );
    } catch ( UnknownHostException e ) {
      // System.err.println(
      // "NetUtil.resolveAddress(String) Could not resolve \"" + address +
      // "\":\n" );
    }

    return null;
  }




  /**
   * Determine if the given string represents a valid port.
   * 
   * This essentially parses the string into an integer and then checks its range.
   * 
   * @param port the string to validate
   *
   * @return the integer value of the parsed string, or 0 if the string could not be parsed or is outside of the valid range for a port.
   */
  public static int validatePort( String port ) {
    try {
      int temp = Integer.parseInt( port );
      return validatePort( temp );
    } catch ( NumberFormatException nfe ) {
      // System.err.println(
      // "NetUtil.validatePort(String) Could not convert \"" + port +
      // "\" into an integer:\n" );
    }

    return 0;
  }




  /**
   * Determines if the given string represents an integer between 0 and 65535
   * inclusive.
   *
   * @param port The string representing a number
   *
   * @return True if the string represents a valid port number, false otherwise
   */
  public static boolean isValidPort( String port ) {
    return ( validatePort( port ) != 0 );
  }




  /**
   * Validate if the port is within the valid range for an IP port.
   * 
   * @param port the port to check
   *
   * @return the value of the port if it is within range, 0 otherwise.
   */
  public static int validatePort( int port ) {
    if ( ( port < 0 ) || ( port > 0xFFFF ) ) {
      return 0;
    } else {
      return port;
    }
  }




  /**
   * Decode an array of bytes into an IP address
   *
   * @param data the bytes to decode
   *
   * @return the decoded IP Address
   * 
   * @throws IllegalArgumentException of the bytes do not represent a valid IP address
   */
  public static InetAddress decodeAddress( byte[] data ) {
    if ( data.length < 4 ) {
      throw new IllegalArgumentException( "data is too short" );
    }

    String address = ( data[0] & 0xFF ) + "." + ( data[1] & 0xFF ) + "." + ( data[2] & 0xFF ) + "." + ( data[3] & 0xFF );

    try {
      return InetAddress.getByName( address );
    } catch ( UnknownHostException uhe ) {
      throw new IllegalArgumentException( "data '" + address + "' is not a valid address" );
    }
  }




  /**
   * Represent the given IP address and a hex string
   *
   * @param address the address to convert
   *
   * @return the hex representation of the address or null if the address could not be represented in hex
   */
  public static String addressToHex( InetAddress address ) {
    if ( address != null ) {
      return bytesToHex( address.getAddress() );
    }

    return null;
  }




  /**
   * Convert the given hostname to its IP address in hex
   * 
   * @param host The hostname to resolve and represent in hex
   *
   * @return the hex representation of the address or null if the address could not be represented in hex
   */
  public static String hostToHex( InetAddress host ) {
    if ( host != null ) {
      return new String( bytesToHex( host.getAddress() ) );
    }

    return null;
  }




  /**
   * Represent the given host and port as a hexadecimal string.
   *
   * @param host ip address to convert
   * @param port ip port to convert
   *
   * @return the hex representation of the host and port or null if the pair could not be represented.
   */
  public static String hostPortToHex( InetAddress host, int port ) {
    if ( host != null ) {
      if ( ( port > -1 ) && ( port < 65536 ) ) {
        return new String( bytesToHex( host.getAddress() ) + bytesToHex( renderUnsignedShort( port ) ) );
      } else {
        return new String( bytesToHex( host.getAddress() ) + bytesToHex( renderUnsignedShort( 0 ) ) );
      }
    }

    return null;
  }




  /**
   * Represent the local host and given port as a hexadecimal string.
   *
   * @param port ip port to convert
   *
   * @return the hex representation of the local host and given port or null if the pair could not be represented.
   */
  public static String hostPortToHex( int port ) {
    return hostPortToHex( getLocalAddress(), port );
  }




  /**
   * Represent the host and port of the given URI as a hexadecimal string.
   *
   * @param uri the URI from which to extrach the host and port to convert
   *
   * @return the hex representation of the host and port in the given URI or null if the pair could not be represented.
   */
  public static String hostPortToHex( URI uri ) {
    if ( uri != null ) {
      return hostPortToHex( getHostAddress( uri ), uri.getPort() );
    }

    return null;
  }




  /**
   * Get the address of the host.
   *
   * <p><b>NOTE</b>: This may take a long time as it will perform a DNS lookup
   * which can take several seconds!
   *
   *
   * @param uri the URI to parse for host information
   *
   * @return The InetAddress of the host specified in the URI. Will return null
   *         if DNS lookup fails, if the URI reference is null or if no host is
   *         specified in the URI.
   */
  public static InetAddress getHostAddress( URI uri ) {
    if ( uri != null ) {
      String host = uri.getHost();

      if ( host != null ) {
        try {
          return InetAddress.getByName( host );
        } catch ( Exception exception ) {}
      }
    }

    return null;
  }




  /**
   * Convert the given hex string into a IP address
   *
   * @param hex the hex string to convert
   *
   * @return a valid IP address or null if the string could not be parsed into an IP address
   */
  public static InetAddress hexToAddress( String hex ) {
    if ( ( hex != null ) && ( hex.length() > 7 ) ) {
      try {
        // This will take longer as a DNS lookup will be performed as
        // part of
        // the InetAddress.getByName method.
        return InetAddress.getByName( decodeAddress( hexToBytes( hex.substring( 0, 8 ) ) ).getHostName() );
      } catch ( Exception ex ) {}
    }

    return null;
  }




  public static String bytesToHex( byte[] bytes ) {
    char[] hexChars = new char[bytes.length * 2];
    for ( int j = 0; j < bytes.length; j++ ) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String( hexChars );
  }




  /**
   * Convert hex representation of bytes to an array bytes
   * 
   * @param s The string to parse
   * 
   * @return an array of bytes represented by the string
   */
  // Quick hack, need to clean up and test
  public static byte[] hexToBytes( String s ) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for ( int i = 0; i < len; i += 2 ) {
      data[i / 2] = (byte)( ( Character.digit( s.charAt( i ), 16 ) << 4 ) + Character.digit( s.charAt( i + 1 ), 16 ) );
    }
    return data;
  }




  /**
   * Return a byte[] (2 byte value) as an unsigned short
   *
   * @param value value to convert
   *
   * @return 3 bytes representing and unsigned short
   */
  static byte[] renderUnsignedShort( final int value ) {
    final byte[] retval = new byte[2];
    retval[0] = (byte)( ( value >>> 8 ) & 0xFF );
    retval[1] = (byte)( ( value >>> 0 ) & 0xFF );

    return retval;
  }




  /**
   * Convert the given hexadecimal string into a dotted-quad address IP address 
   * string.
   *
   * @param hex the hexadecimal string to parse
   *
   * @return a dotted-quad IP address
   */
  public static String hexToAddressString( String hex ) {
    if ( ( hex != null ) && ( hex.length() > 7 ) ) {
      return new String( Integer.parseInt( hex.substring( 0, 2 ), 16 ) + "." + Integer.parseInt( hex.substring( 2, 4 ), 16 ) + "." + Integer.parseInt( hex.substring( 4, 6 ), 16 ) + "." + Integer.parseInt( hex.substring( 6, 8 ), 16 ) );
    }

    return null;
  }




  /**
   * Use the underlying getCanonicalHostName as used in Java 1.4, but return
   * null if the value is the numerical address (dotted-quad) representation of
   * the address.
   *
   * @param addr The IP address to lookup.
   *
   * @return The Canonical Host Name; null if the FQDN could not be determined
   *         or if the return value was the dotted-quad representation of the
   *         host address.
   */
  public static String getQualifiedHostName( InetAddress addr ) {
    String name = null;

    try {
      name = addr.getCanonicalHostName();

      if ( name != null ) {
        // Check for a return value of and address instead of a name
        if ( Character.isDigit( name.charAt( 0 ) ) ) {
          // Looks like an address, return null;
          return null;
        }

        // normalize the case
        name = name.toLowerCase();
      }
    } catch ( Exception ex ) {}

    return name;
  }




  /**
   * Attempted to return the FQDN of the given string representation of the IP
   * address.
   *
   * @param addr String representing a valid IP address.
   *
   * @return the FQDN of the addresses represented by the given string, or null
   *         if the FQDN could not be determined.
   */
  public static String getQualifiedHostName( String addr ) {
    try {
      InetAddress address = InetAddress.getByName( addr );

      if ( address != null ) {
        return getQualifiedHostName( address );
      }
    } catch ( Exception ex ) {}

    return null;
  }




  /**
   * Return the hostname of the given IP address in lowercase.
   *
   * @param addr the address to resolve
   *
   * @return lowercase representation of the hostname for the given address or null if it does not resolve
   */
  public static String getRelativeHostName( InetAddress addr ) {
    return addr.getHostName().toLowerCase();
  }




  /**
   * Return the hostname of the given IP address in lowercase.
   *
   * @param addr the address to resolve
   *
   * @return lowercase representation of the hostname for the given address or null if it does not resolve
   */
  public static String getRelativeHostName( String addr ) {
    try {
      InetAddress address = InetAddress.getByName( addr );

      if ( address != null ) {
        return getRelativeHostName( address );
      }
    } catch ( Exception ex ) {}

    return null;
  }




  /**
   * Return the hostname of the localhost in lowercase.
   *
   * @return lowercase representation of the local host or null if it does not resolve
   */
  public static String getLocalRelativeHostName() {
    return getRelativeHostName( getLocalAddress() );
  }




  /**
   * @return the FQDN of the local host or null if the lookup failed for any
   *         reason.
   */
  public static String getLocalQualifiedHostName() {
    // Use the cached version of the hostname to save DNS lookups
    if ( NetUtil.cachedLocalHostName != null ) {
      return NetUtil.cachedLocalHostName;
    }

    cachedLocalHostName = getQualifiedHostName( getLocalAddress() );

    return cachedLocalHostName;
  }




  /**
   * Method getLocalDomain
   *
   * @return the local domain
   */
  public static String getLocalDomain() {
    return getDomain( getLocalAddress() );
  }




  /**
   * Get the domain name of the given IP address
   *
   * @param addr the address to resolve
   *
   * @return the domain name of the given address or null if the given address does not resolve
   */
  public static String getDomain( InetAddress addr ) {
    if ( addr != null ) {
      String hostname = getQualifiedHostName( addr );

      if ( hostname != null ) {
        int indx = hostname.indexOf( '.' );

        if ( indx > 0 ) {
          String retval = hostname.substring( indx + 1 );

          if ( retval.indexOf( '.' ) > 0 ) {
            return retval;
          } else {
            return hostname;
          }
        }
      }
    }

    return null;
  }




  /**
   * Return a InetAddress that is suitable for use as a broadcast address.
   *
   * <p>Take a mask in the form of "255.255.111.0" and apply it to the local
   * address to calculate the broadcast address for the given subnet mask.
   *
   * @param mask Valid dotted-quad netmask.
   *
   * @return an InetAddress capable of being used as a broadcast address
   */
  public static InetAddress getLocalBroadcast( String mask ) {
    InetAddress retval = getLocalAddress();

    if ( retval != null ) {
      return getBroadcastAddress( retval, mask );
    }

    return retval;
  }




  /**
   * Return a InetAddress that is suitable for use as a broadcast address.
   *
   * <p>Take a mask in the form of "255.255.111.0" and apply it to the given
   * address to calculate the broadcast address for the given subnet mask.
   *
   * @param addr InetAddress representing a node in a subnet.
   * @param mask Valid dotted-quad netmask.
   *
   * @return an InetAddress capable of being used as a broadcast address in the
   *         given nodes subnet.
   */
  public static InetAddress getBroadcastAddress( InetAddress addr, String mask ) {
    if ( mask != null ) {
      try {
        IpNetwork network = new IpNetwork( addr.getHostAddress(), mask );
        IpAddress adr = network.getBroadcastAddress();
        return InetAddress.getByName( adr.toString() );
      } catch ( Exception e ) {
        // just return the address
      }
    }

    return addr;
  }




  /**
   * Return a InetAddress that is suitable for use as a broadcast address.
   *
   * <p>Take a mask in the form of "255.255.111.0" and apply it to the given
   * address to calculate the broadcast address for the given subnet mask.
   *
   * @param addr InetAddress representing a node in a subnet.
   * @param mask Valid dotted-quad netmask.
   *
   * @return an InetAddress capable of being used as a broadcast address in the
   *         given nodes subnet.
   */
  public static InetAddress getBroadcastAddress( String addr, String mask ) {
    InetAddress node = null;

    if ( mask != null ) {
      try {
        node = InetAddress.getByName( addr );

        IpNetwork network = new IpNetwork( addr, mask );
        IpAddress adr = network.getBroadcastAddress();
        return InetAddress.getByName( adr.toString() );
      } catch ( Exception ignore ) {
        // just return the node address
        try {
          node = InetAddress.getByName( "255.255.255.255" );
        } catch ( Exception e ) {
          // should always work
        }
      }
    }

    return node;
  }




  /**
   * Return a InetAddress that is suitable for use as a broadcast address.
   *
   * <p>Take a mask in the form of "255.255.111.0" and apply it to the given
   * address to calculate the broadcast address for the given subnet mask.
   *
   * @param addr InetAddress representing a node in a subnet.
   * @param mask Valid dotted-quad netmask.
   *
   * @return an InetAddress capable of being used as a broadcast address in the
   *         given nodes subnet.
   */
  public static String getBroadcastAddressString( String addr, String mask ) {
    if ( mask != null ) {
      try {
        InetAddress node = InetAddress.getByName( addr );
        IpNetwork network = new IpNetwork( addr, mask );
        return network.getBroadcastAddress().toString();
      } catch ( Exception ignore ) {
        // just return the node address
      }
    }

    return "255.255.255.255";
  }




  /**
   * Return a InetAddress that is suitable for use as a broadcast address.
   *
   * <p>Take a mask in the form of "255.255.111.0" and apply it to the local
   * address to calculate the broadcast address for the given subnet mask.
   *
   * @param mask Valid dotted-quad netmask.
   *
   * @return the broadcast address string or the global broadcast (255.255.255.255) if the mask could produce an address
   */
  public static String getLocalBroadcastString( String mask ) {
    String retval = "255.255.255.255";

    if ( mask != null ) {
      try {
        return getLocalBroadcast( mask ).getHostAddress();
      } catch ( Exception ignore ) {
        // just return the address
      }
    }

    return retval;
  }




  /**
   * Return a UDP server socket on the given address and port, incrementing the
   * port until a server socket can be opened.
   *
   * @param address The address on which the socket should be bound
   * @param port the port on which datagrams should be received
   * @param reuse true to allow the address/port to be used for other datagram sockets
   *
   * @return the datagram socket from which datagrams can be read, null if the socket could not be bound
   */
  public static DatagramSocket getNextDatagramSocket( InetAddress address, int port, boolean reuse ) {
    int i = port;
    DatagramSocket dgramsocket = null;

    // If no address was given, then try to determine our local address so
    // we
    // can use our main address instead of 127.0.0.1 which may be chosen by
    // the
    // VM if it is not specified in the DatagramSocket constructor
    if ( address == null ) {
      address = getLocalAddress();
    }

    while ( validatePort( i ) != 0 ) {
      try {
        if ( address == null ) {
          dgramsocket = new DatagramSocket( i );

          dgramsocket.setReuseAddress( reuse );
        } else {
          dgramsocket = new DatagramSocket( i, address );

          dgramsocket.setReuseAddress( reuse );
        }

        if ( dgramsocket != null ) {
          return dgramsocket;
        }

      } catch ( IOException e ) {
        i++;
      }
    }

    return null;
  }




  /**
   * Return a port number that can be used to create a datagram socket on the
   * given address starting with the given port.
   *
   * <p>If the given port can be used to create a datagram socket (UDP) then
   * that port number will be used, otherwise, the port number will be
   * incremented and tested until a free port is found.
   *
   * <p>This is not thread-safe nor fool-proof. A valid value can be returned,
   * yet when a call is made to open a socket at that port, another thread may
   * have already opened a socket on that port. A better way would be to use
   * the <code>getNextDatagramSocket(address,port)</code> method if it desired
   * to obtain the next available datagram server.
   *
   * @param address the address to which the datagram socket should be bound
   * @param port the port to use as the starting point for binding
   * @param reuse allow reuse of a socket via the SO_REUSEADDR socket option
   *
   * @return the next port to which a datagram socket can be bound.
   */
  public static int getNextAvailableUdpPort( InetAddress address, int port, boolean reuse ) {
    DatagramSocket dgramsocket = getNextDatagramSocket( address, port, reuse );

    int retval = -1;

    if ( dgramsocket != null ) {
      // Get the port as a return value
      retval = dgramsocket.getLocalPort();

      // Close the un-needed socket
      dgramsocket.close();
    }

    return retval;
  }




  /**
   * Return a port number that can be used to create a datagram socket on the
   * given address starting with the given port.
   *
   * <p>If the given port can be used to create a datagram socket (UDP) then
   * that port number will be used, otherwise, the port number will be
   * incremented and tested until a free port is found.
   *
   * <p>This is not thread-safe nor fool-proof. A valid value can be returned,
   * yet when a call is made to open a socket at that port, another thread may
   * have already opened a socket on that port. A better way would be to use
   * the <code>getNextDatagramSocket(address,port)</code> method if it desired
   * to obtain the next available datagram server.
   *
   * @param address the address to which the datagram socket should be bound
   * @param port the port to use as the starting point for binding
   *
   * @return the next port to which a datagram socket can be bound.
   */
  public static int getNextAvailableUdpPort( InetAddress address, int port ) {
    DatagramSocket dgramsocket = getNextDatagramSocket( address, port, false );

    int retval = -1;

    if ( dgramsocket != null ) {
      // Get the port as a return value
      retval = dgramsocket.getLocalPort();

      // Close the un-needed socket
      dgramsocket.close();
    }

    return retval;
  }




  /**
   * Return a port number that can be used to create a datagram socket on the
   * local address starting with the given port.
   *
   * <p>If the given port can be used to create a datagram socket (UDP) then
   * that port number will be used, otherwise, the port number will be
   * incremented and tested until a free port is found.
   *
   * <p>This is not thread-safe nor fool-proof. A valid value can be returned,
   * yet when a call is made to open a socket at that port, another thread may
   * have already opened a socket on that port. A better way would be to use
   * the <code>getNextDatagramSocket(address,port)</code> method if it desired
   * to obtain the next available datagram server.
   *
   * @param port the port to use as the starting point for binding
   *
   * @return the next port to which a datagram socket can be bound.
   */
  public static int getNextAvailableUdpPort( int port ) {
    return getNextAvailableUdpPort( null, port );
  }




  /**
   * Return a port number that can be used to create a datagram socket on the
   * given address starting with port 1.
   *
   * <p>If the given port can be used to create a datagram socket (UDP) then
   * that port number will be used, otherwise, the port number will be
   * incremented and tested until a free port is found.
   *
   * <p>This is not thread-safe nor fool-proof. A valid value can be returned,
   * yet when a call is made to open a socket at that port, another thread may
   * have already opened a socket on that port. A better way would be to use
   * the <code>getNextDatagramSocket(address,port)</code> method if it desired
   * to obtain the next available datagram server.
   *
   * @param address the address to which the datagram socket should be bound
   *
   * @return the next port to which a datagram socket can be bound.
   */
  public static int getNextAvailableUdpPort( InetAddress address ) {
    return getNextAvailableUdpPort( address, 1 );
  }




  /**
   * Set the cached Fully-qualified hostname to avoid additional DNS lookups.
   *
   * @param name the name to use as the local host name.
   */
  public static void setCachedLocalHostName( String name ) {
    cachedLocalHostName = name;
  }




  /**
   * Check to see if the system properties have a comma-separated list of DNS
   * server names for us to use.
   */
  private static void findDnsProperty() {
    Vector v = null;
    String prop = System.getProperty( "dns.server" );

    if ( prop != null ) {
      String s;

      for ( StringTokenizer st = new StringTokenizer( prop, "," ); st.hasMoreTokens(); v.addElement( s ) ) {
        s = st.nextToken();

        if ( v == null ) {
          v = new Vector();
        }
      }

      if ( v != null ) {
        dnsServerList = new String[v.size()];

        for ( int i = 0; i < v.size(); i++ ) {
          dnsServerList[i] = (String)v.elementAt( i );
        }
      }
    }
  }




  /**
   * Parse through the resolver configuration file on a Unix platform and locate
   * the name server entries.
   */
  private static void findUnix() {
    InputStream in = null;

    try {
      in = new FileInputStream( "/etc/resolv.conf" );
    } catch ( FileNotFoundException _ex ) {
      return;
    }

    InputStreamReader isr = new InputStreamReader( in );
    BufferedReader br = new BufferedReader( isr );
    Vector vserver = null;

    try {
      String line;

      while ( ( line = br.readLine() ) != null ) {
        if ( line.startsWith( "nameserver" ) ) {
          if ( vserver == null ) {
            vserver = new Vector();
          }

          StringTokenizer st = new StringTokenizer( line );

          st.nextToken();
          vserver.addElement( st.nextToken() );
        }
      }

      br.close();
    } catch ( IOException _ex ) {}

    if ( ( dnsServerList == null ) && ( vserver != null ) ) {
      dnsServerList = new String[vserver.size()];

      for ( int i = 0; i < vserver.size(); i++ ) {
        dnsServerList[i] = (String)vserver.elementAt( i );
      }
    }
  }




  /**
   * Parse through the output of Windows IP configuration stats and locate the
   * DNS Server entries
   *
   * @param in the input stream to parse
   */
  private static void findWin( InputStream in ) {
    BufferedReader br = new BufferedReader( new InputStreamReader( in ) );

    try {
      Vector vserver = null;
      String line = null;
      boolean readingServers = false;

      while ( ( line = br.readLine() ) != null ) {
        // ignore empty lines
        if ( line.length() == 0 ) {
          continue;
        }

        StringTokenizer st = new StringTokenizer( line );

        if ( !st.hasMoreTokens() ) {
          readingServers = false;
        } else {
          String s = st.nextToken();

          if ( line.indexOf( ":" ) != -1 ) {
            readingServers = false;
          }

          if ( readingServers || ( line.indexOf( "DNS Servers" ) != -1 ) ) {
            while ( st.hasMoreTokens() ) {
              s = st.nextToken();
            }

            if ( !s.equals( ":" ) ) {
              if ( vserver == null ) {
                vserver = new Vector();
              }

              vserver.addElement( s );

              readingServers = true;
            }
          }
        }
      }

      if ( ( dnsServerList == null ) && ( vserver != null ) ) {
        dnsServerList = new String[vserver.size()];

        for ( int i = 0; i < vserver.size(); i++ ) {
          dnsServerList[i] = (String)vserver.elementAt( i );
        }
      }
    } catch ( IOException _ex ) {}
    finally {
      try {
        br.close();
      } catch ( IOException _ex ) {}
    }
  }




  /**
   * Determine the name servers on a Windows 95 box by calling 'winipcfg'
   */
  private static void find95() {
    String s = "winipcfg.out";

    try {
      Process p = Runtime.getRuntime().exec( "winipcfg /all /batch winipcfg.out" );

      p.waitFor();

      File f = new File( "winipcfg.out" );

      findWin( new FileInputStream( f ) );
      f.delete();
    } catch ( Exception _ex ) {
      return;
    }
  }




  /**
   * Call the 'ipconfig" utility on Windows NT, XP and 200X operating systems.
   */
  private static void findNT() {
    try {
      Process p = Runtime.getRuntime().exec( "ipconfig /all" );

      findWin( p.getInputStream() );
      p.destroy();
    } catch ( Exception _ex ) {
      return;
    }
  }




  /**
   * Probe the platform configuration for DNS server data.
   */
  private static synchronized void probeDnsConfig() {
    if ( dnsProbedFlag ) {
      return;
    }

    dnsProbedFlag = true;

    findDnsProperty();

    if ( dnsServerList == null ) {
      String OS = System.getProperty( "os.name" );

      if ( OS.indexOf( "Windows" ) != -1 ) {
        if ( ( OS.indexOf( "NT" ) != -1 ) || ( OS.indexOf( "200" ) != -1 ) || ( OS.indexOf( "XP" ) != -1 ) ) {
          findNT();
        } else {
          find95();
        }
      } else {
        findUnix();
      }
    }
  }




  /**
   * @return an array of strings containing the names of DNS servers discovered
   *         for this platform
   */
  public static String[] getDnsServers() {
    probeDnsConfig();

    return dnsServerList;
  }




  /**
   * @return the primary DNS server name for this platform, or null if no server
   *         could be found.
   */
  public static String getDnsServer() {
    probeDnsConfig();

    String[] array = dnsServerList;

    if ( array == null ) {
      return null;
    } else {
      return array[0];
    }
  }

}