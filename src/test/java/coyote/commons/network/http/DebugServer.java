package coyote.commons.network.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This is an example of a simple HTTP server.
 * 
 * <p>It allows you to run basic tests and see the data involved.
 */
public class DebugServer extends HTTPD {

  /**
   * THe main entry point
   * @param args
   */
  public static void main( final String[] args ) {
    ServerRunner.run( DebugServer.class );
  }




  public DebugServer() {
    super( 8080 );
  }




  private void listItem( final StringBuilder sb, final Map.Entry<String, ? extends Object> entry ) {
    sb.append( "<li><code><b>" ).append( entry.getKey() ).append( "</b> = " ).append( entry.getValue() ).append( "</code></li>" );
  }




  @Override
  public Response serve( final IHTTPSession session ) {
    final Map<String, List<String>> decodedQueryParameters = decodeParameters( session.getQueryParameterString() );

    final StringBuilder sb = new StringBuilder();
    sb.append( "<html>" );
    sb.append( "<head><title>Debug Server</title></head>" );
    sb.append( "<body>" );
    sb.append( "<h1>Debug Server</h1>" );

    sb.append( "<p><blockquote><b>URI</b> = " ).append( String.valueOf( session.getUri() ) ).append( "<br />" );

    sb.append( "<b>Method</b> = " ).append( String.valueOf( session.getMethod() ) ).append( "</blockquote>" );

    sb.append( "<h3>Headers</h3><p><blockquote>" ).append( toString( session.getHeaders() ) ).append( "</blockquote>" );

    sb.append( "<h3>Parms</h3><p><blockquote>" ).append( toString( session.getParms() ) ).append( "</blockquote>" );

    sb.append( "<h3>Parms (multi values?)</h3><p><blockquote>" ).append( toString( decodedQueryParameters ) ).append( "</blockquote>" );

    try {
      final Map<String, String> files = new HashMap<String, String>();
      session.parseBody( files );
      sb.append( "<h3>Files</h3><p><blockquote>" ).append( toString( files ) ).append( "</blockquote>" );
    } catch ( final Exception e ) {
      e.printStackTrace();
    }

    sb.append( "</body>" );
    sb.append( "</html>" );
    return newFixedLengthResponse( sb.toString() );
  }




  private String toString( final Map<String, ? extends Object> map ) {
    if ( map.size() == 0 ) {
      return "";
    }
    return unsortedList( map );
  }




  private String unsortedList( final Map<String, ? extends Object> map ) {
    final StringBuilder sb = new StringBuilder();
    sb.append( "<ul>" );
    for ( final Map.Entry<String, ? extends Object> entry : map.entrySet() ) {
      listItem( sb, entry );
    }
    sb.append( "</ul>" );
    return sb.toString();
  }
}
