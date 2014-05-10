package coyote.commons;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.AccessControlException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import coyote.commons.network.Ip4Address;
import coyote.commons.network.Ip6Address;
import coyote.commons.network.dns.DNS;
import coyote.commons.network.dns.DNSQuery;


/**
 * This class models...QueryTest
 */

public class QueryTest {
  public static void main( String[] args ) {
    // http://cbl.abuseat.org/lookup.cgi?ip=68.91.120.252
    //String dnsServer = "cbl.abuseat.org";
    //String ipAddress = "68.91.120.252";

    String dnsServer = "24.95.80.41";
    String ipAddress = "68.91.120.252";

    String queryType = "ANY";
    String queryClass = "ANY";

    // / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
    // Valid types
    System.out.println( "Valid types:" );
    Set types = new TreeSet( DNS.validRRQueryTypes );
    for( Iterator i = types.iterator(); i.hasNext(); ) {
      System.out.println( i.next() );
    }
    int type = ( queryType.equals( "All" ) ) ? DNS.TYPE_ANY : DNS.typeFromCode( queryType );
    System.out.println();

    // / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
    // Valid Classes
    System.out.println( "Valid Classes:" );
    Set classes = new TreeSet( DNS.validRRAddressClasses );
    for( Iterator i = classes.iterator(); i.hasNext(); ) {
      System.out.println( i.next() );
    }
    int clas = ( queryClass.equals( "Any" ) ) ? DNS.CLASS_ANY : DNS.classFromCode( queryClass );
    System.out.println();

    // Determine whether it's a reverse query and modify accordingly.
    if( type == DNS.TYPE_PTR ) {
      if( Ip4Address.isIPv4DottedDecimal( ipAddress ) ) {
        ipAddress = Ip4Address.IPv4ptrQueryString( ipAddress );
      } else if( Ip6Address.isValidIPv6( ipAddress ) ) {
        ipAddress = Ip6Address.IPv6ptrQueryString( ipAddress );
      }
    }

    String target = ipAddress;
    boolean recurse = true;
    int udpTimeout = DNS.DEFAULT_UDP_INIT_TIMEOUT;
    int thisServerPort = DNS.DEFAULT_PORT;
    int udpRetry = 1;

    // Assume that the query will get a response
    // ... will be changed to false if problem encountered.
    boolean receivedOK = true;

    System.out.println( "Target:" + target );
    System.out.println( "Type:" + type );
    System.out.println( "Class:" + clas );
    System.out.println( "Recurse:" + recurse );

    DNSQuery query = new DNSQuery( target, type, clas, recurse );

    try {
      boolean received = false;
      int count = 0;
      int currentTimeout = udpTimeout * 1000;

      DatagramSocket socket = new DatagramSocket();
      try {
        while( !received ) {
          socket.setSoTimeout( currentTimeout );

          try {
            query.sendUDP( socket, InetAddress.getByName( dnsServer ), thisServerPort );
            query.getUDP( socket );
            received = true;
            System.out.println( "Success\n" );
          }
          catch( AccessControlException ex ) {
            System.err.println( "Error: " + ex.getMessage() );
          }
          catch( InterruptedIOException ex ) {
            if( count++ < udpRetry ) {
              currentTimeout *= 2;
              System.out.println( "resend...to=" + currentTimeout + "\n" );
            } else {
              receivedOK = false;

              throw new IOException( "No response received from nameserver" );
            }
          }
        }
      }
      finally {
        socket.close();
      }
    }
    catch( IOException ex ) {
      System.out.println( "Exception during coyote.commons.network.dns UDP query: '" + ex.getMessage() + "'\n" );
    }

    StringBuffer buffer = new StringBuffer();

    // Output the result, assuming that no problem was encountered.
    if( receivedOK ) {
      Vector queryResults = query.formatResponse();
      Enumeration querySet = queryResults.elements();
      while( querySet.hasMoreElements() ) {
        buffer.append( querySet.nextElement().toString() + "\n" );
      }
    }

    System.out.println( "Results:\n" + buffer.toString() );
  }

}