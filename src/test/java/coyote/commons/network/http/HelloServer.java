package coyote.commons.network.http;

import java.util.Map;
import java.util.logging.Logger;


/**
 * An example of subclassing HTTPD to make a custom HTTP server.
 */
public class HelloServer extends HTTPD {

  /**
   * logger to log to.
   */
  private static final Logger LOG = Logger.getLogger( HelloServer.class.getName() );




  public static void main( final String[] args ) {
    ServerRunner.run( HelloServer.class );
  }




  public HelloServer() {
    super( 8080 );
  }




  @Override
  public Response serve( final IHTTPSession session ) {
    final Method method = session.getMethod();
    final String uri = session.getUri();
    HelloServer.LOG.info( method + " '" + uri + "' " );

    String msg = "<html><body><h1>Hello server</h1>\n";
    final Map<String, String> parms = session.getParms();
    if ( parms.get( "username" ) == null ) {
      msg += "<form action='?' method='get'>\n" + "  <p>Your name: <input type='text' name='username'>\n" + "</form>\n";
    } else {
      msg += "<p>Hello, " + parms.get( "username" ) + "!";
    }

    msg += "</body></html>\n";

    return newFixedLengthResponse( msg );
  }
}
