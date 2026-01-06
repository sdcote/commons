package coyote.commons.network.http.nugget;

import java.io.File;
import java.io.IOException;

import coyote.commons.network.http.ServerRunner;
import coyote.commons.network.http.nugget.GeneralHandler;
import coyote.commons.network.http.nugget.HTTPDRouter;
import coyote.commons.network.http.nugget.UriResponder;


/**
 * Show how to write an application server with responders.
 */
public class MiniApp extends HTTPDRouter {

  private static final int PORT = 9090;

  /**
   * Create the server instance
   */
  public MiniApp() throws IOException {
    super( PORT );
    addMappings();
    System.out.println( "\nRunning! Point your browers to http://localhost:" + PORT + "/ \n" );
  }




  /**
   * Add the routes Every route is an absolute path Parameters starts with ":"
   * Handler class should implement {@link UriResponder} interface. If the 
   * handler does not implement {@link UriResponder} interface - toString() is used
   */
  @Override
  public void addMappings() {
    super.addMappings();
    addRoute( "/user", DebugHandler.class );
    addRoute( "/user/:id", DebugHandler.class );
    addRoute( "/user/help", GeneralHandler.class );
    addRoute( "/general/:param1/:param2", GeneralHandler.class );
    addRoute( "/photos/:customer_id/:photo_id", null );
    addRoute( "/test", String.class );
    addRoute( "/interface", UriResponder.class ); // this will cause an error
                                                  // when called
    addRoute( "/toBeDeleted", String.class );
    removeRoute( "/toBeDeleted" );
    addRoute( "/stream", StreamUrl.class );
    addRoute( "/browse/(.)+", StaticPageTestHandler.class, new File( "src/test/resources" ).getAbsoluteFile() );
  }




  /**
   * Main entry point
   * 
   * @param args
   */
  public static void main( String[] args ) {
    ServerRunner.run( MiniApp.class );
  }
}
