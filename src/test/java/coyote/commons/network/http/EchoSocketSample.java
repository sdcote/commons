package coyote.commons.network.http;

import java.io.IOException;


public class EchoSocketSample {

  public static void main( final String[] args ) throws IOException {
    final boolean debugMode = ( args.length >= 2 ) && "-d".equals( args[1].toLowerCase() );
    final WSD ws = new DebugWebSocketServer( args.length > 0 ? Integer.parseInt( args[0] ) : 9090, debugMode );
    ws.start();
    System.out.println( "Server started, hit Enter to stop.\n" );
    try {
      System.in.read();
    } catch ( final IOException ignored ) {}
    ws.stop();
    System.out.println( "Server stopped.\n" );
  }

}
