package coyote.commons.network.http;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;


/**
 * This is the core of the HTTP Server.
 * 
 * <p>This class should be subclassed and the {@link #serve(IHTTPSession)} 
 * method overridden to serve the request.
 */
public abstract class HTTPD {

  private static final String CONTENT_DISPOSITION_REGEX = "([ |\t]*Content-Disposition[ |\t]*:)(.*)";

  static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile( CONTENT_DISPOSITION_REGEX, Pattern.CASE_INSENSITIVE );

  private static final String CONTENT_TYPE_REGEX = "([ |\t]*content-type[ |\t]*:)(.*)";

  static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile( CONTENT_TYPE_REGEX, Pattern.CASE_INSENSITIVE );

  private static final String CONTENT_DISPOSITION_ATTRIBUTE_REGEX = "[ |\t]*([a-zA-Z]*)[ |\t]*=[ |\t]*['|\"]([^\"^']*)['|\"]";

  static final Pattern CONTENT_DISPOSITION_ATTRIBUTE_PATTERN = Pattern.compile( CONTENT_DISPOSITION_ATTRIBUTE_REGEX );

  /**
   * Maximum time to wait on Socket.getInputStream().read() (in milliseconds)
   * This is required as the Keep-Alive HTTP connections would otherwise block
   * the socket reading thread forever (or as long the browser is open).
   */
  public static final int SOCKET_READ_TIMEOUT = 5000;

  /**
   * Common MIME type for dynamic content: plain text
   */
  public static final String MIME_PLAINTEXT = "text/plain";

  /**
   * Common MIME type for dynamic content: html
   */
  public static final String MIME_HTML = "text/html";

  /**
   * Pseudo-Parameter to use to store the actual query string in the
   * parameters map for later re-processing.
   */
  private static final String QUERY_STRING_PARAMETER = "Httpd.QUERY_STRING";

  /**
   * logger to log to.
   */
  static final Logger LOG = Logger.getLogger( HTTPD.class.getName() );

  /**
   * Hashtable mapping (String)FILENAME_EXTENSION -&gt; (String)MIME_TYPE
   */
  protected static Map<String, String> MIME_TYPES;


  final String hostname;
  final int myPort;

  volatile ServerSocket myServerSocket;

  private ServerSocketFactory serverSocketFactory = new DefaultServerSocketFactory();

  private Thread myThread;

  /**
   * Pluggable strategy for asynchronously executing requests.
   */
  protected AsyncRunner asyncRunner;

  /**
   * Pluggable strategy for creating and cleaning up temporary files.
   */
  TempFileManagerFactory tempFileManagerFactory;



  /**
   * Decode parameters from a URL, handing the case where a single parameter
   * name might have been supplied several times, by return lists of values.
   * 
   * <p>In general these lists will contain a single element.
   * 
   * @param parms original HTTPD parameters values, as passed to the 
   *        {@code serve()} method.
   * @return a map of {@code String} (parameter name) to
   *         {@code List<String>} - a list of the values supplied.
   */
  protected static Map<String, List<String>> decodeParameters( final Map<String, String> parms ) {
    return decodeParameters( parms.get( HTTPD.QUERY_STRING_PARAMETER ) );
  }




  /**
   * Decode parameters from a URL, handing the case where a single parameter
   * name might have been supplied several times, by return lists of values.
   * 
   * <p>In general these lists will contain a single element.
   * 
   * @param queryString a query string pulled from the URL.
   * @return a map of {@code String} (parameter name) to 
   *         {@code List<String>} (a list of the values supplied).
   */
  protected static Map<String, List<String>> decodeParameters( final String queryString ) {
    final Map<String, List<String>> parms = new HashMap<String, List<String>>();
    if ( queryString != null ) {
      final StringTokenizer st = new StringTokenizer( queryString, "&" );
      while ( st.hasMoreTokens() ) {
        final String e = st.nextToken();
        final int sep = e.indexOf( '=' );
        final String propertyName = sep >= 0 ? decodePercent( e.substring( 0, sep ) ).trim() : decodePercent( e ).trim();
        if ( !parms.containsKey( propertyName ) ) {
          parms.put( propertyName, new ArrayList<String>() );
        }
        final String propertyValue = sep >= 0 ? decodePercent( e.substring( sep + 1 ) ) : null;
        if ( propertyValue != null ) {
          parms.get( propertyName ).add( propertyValue );
        }
      }
    }
    return parms;
  };




  /**
   * Decode percent encoded {@code String} values.
   * 
   * @param str the percent encoded {@code String}
   * 
   * @return expanded form of the input, for example "foo%20bar" becomes
   *         "foo bar"
   */
  protected static String decodePercent( final String str ) {
    String decoded = null;
    try {
      decoded = URLDecoder.decode( str, "UTF8" );
    } catch ( final UnsupportedEncodingException ignored ) {
      HTTPD.LOG.log( Level.WARNING, "Encoding not supported, ignored", ignored );
    }
    return decoded;
  }




  /**
   * Get MIME type from file name extension, if possible
   * 
   * @param uri the string representing a file
   * 
   * @return the connected mime/type
   */
  public static String getMimeTypeForFile( final String uri ) {
    final int dot = uri.lastIndexOf( '.' );
    String mime = null;
    if ( dot >= 0 ) {
      mime = mimeTypes().get( uri.substring( dot + 1 ).toLowerCase() );
    }
    return mime == null ? "application/octet-stream" : mime;
  }




  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static void loadMimeTypes( final Map<String, String> result, final String resourceName ) {
    try {
      final Enumeration<URL> resources = HTTPD.class.getClassLoader().getResources( resourceName );
      while ( resources.hasMoreElements() ) {
        final URL url = resources.nextElement();
        final Properties properties = new Properties();
        InputStream stream = null;
        try {
          stream = url.openStream();
          properties.load( url.openStream() );
        } catch ( final IOException e ) {
          LOG.log( Level.SEVERE, "could not load mimetypes from " + url, e );
        }
        finally {
          safeClose( stream );
        }
        result.putAll( (Map)properties );
      }
    } catch ( final IOException e ) {
      LOG.log( Level.INFO, "no mime types available at " + resourceName );
    }
  }




  /**
   * Creates an SSLSocketFactory for HTTPS. Pass a loaded KeyStore and an
   * array of loaded KeyManagers. These objects must properly
   * loaded/initialized by the caller.
   */
  public static SSLServerSocketFactory makeSSLSocketFactory( final KeyStore loadedKeyStore, final KeyManager[] keyManagers ) throws IOException {
    SSLServerSocketFactory res = null;
    try {
      final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm() );
      trustManagerFactory.init( loadedKeyStore );
      final SSLContext ctx = SSLContext.getInstance( "TLS" );
      ctx.init( keyManagers, trustManagerFactory.getTrustManagers(), null );
      res = ctx.getServerSocketFactory();
    } catch ( final Exception e ) {
      throw new IOException( e.getMessage() );
    }
    return res;
  }




  /**
   * Creates an SSLSocketFactory for HTTPS. Pass a loaded KeyStore and a
   * loaded KeyManagerFactory. These objects must properly loaded/initialized
   * by the caller.
   */
  public static SSLServerSocketFactory makeSSLSocketFactory( final KeyStore loadedKeyStore, final KeyManagerFactory loadedKeyFactory ) throws IOException {
    try {
      return makeSSLSocketFactory( loadedKeyStore, loadedKeyFactory.getKeyManagers() );
    } catch ( final Exception e ) {
      throw new IOException( e.getMessage() );
    }
  }




  /**
   * Creates an SSLSocketFactory for HTTPS. Pass a KeyStore resource with your
   * certificate and passphrase
   */
  public static SSLServerSocketFactory makeSSLSocketFactory( final String keyAndTrustStoreClasspathPath, final char[] passphrase ) throws IOException {
    try {
      final KeyStore keystore = KeyStore.getInstance( KeyStore.getDefaultType() );
      final InputStream keystoreStream = HTTPD.class.getResourceAsStream( keyAndTrustStoreClasspathPath );

      if ( keystoreStream == null ) {
        throw new IOException( "Unable to load keystore from classpath: " + keyAndTrustStoreClasspathPath );
      }

      keystore.load( keystoreStream, passphrase );
      final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
      keyManagerFactory.init( keystore, passphrase );
      return makeSSLSocketFactory( keystore, keyManagerFactory );
    } catch ( final Exception e ) {
      throw new IOException( e.getMessage() );
    }
  }




  public static Map<String, String> mimeTypes() {
    if ( MIME_TYPES == null ) {
      MIME_TYPES = new HashMap<String, String>();
      loadMimeTypes( MIME_TYPES, "httpd/default-mimetypes.properties" );
      loadMimeTypes( MIME_TYPES, "httpd/mimetypes.properties" );
      if ( MIME_TYPES.isEmpty() ) {
        LOG.log( Level.WARNING, "no mime types found in the classpath! please provide mimetypes.properties" );
      }
    }
    return MIME_TYPES;
  }




  /**
   * Create a response with unknown length (using HTTP 1.1 chunking).
   */
  public static Response newChunkedResponse( final IStatus status, final String mimeType, final InputStream data ) {
    return new Response( status, mimeType, data, -1 );
  }




  /**
   * Create a response with known length.
   */
  public static Response newFixedLengthResponse( final IStatus status, final String mimeType, final InputStream data, final long totalBytes ) {
    return new Response( status, mimeType, data, totalBytes );
  }




  /**
   * Create a text response with known length.
   */
  public static Response newFixedLengthResponse( final IStatus status, final String mimeType, final String txt ) {
    ContentType contentType = new ContentType( mimeType );
    if ( txt == null ) {
      return newFixedLengthResponse( status, mimeType, new ByteArrayInputStream( new byte[0] ), 0 );
    } else {
      byte[] bytes;
      try {
        final CharsetEncoder newEncoder = Charset.forName( contentType.getEncoding() ).newEncoder();
        if ( !newEncoder.canEncode( txt ) ) {
          contentType = contentType.tryUTF8();
        }
        bytes = txt.getBytes( contentType.getEncoding() );
      } catch ( final UnsupportedEncodingException e ) {
        HTTPD.LOG.log( Level.SEVERE, "encoding problem", e );
        bytes = new byte[0];
      }
      return newFixedLengthResponse( status, contentType.getContentTypeHeader(), new ByteArrayInputStream( bytes ), bytes.length );
    }
  }




  /**
   * Create a text response with known length.
   */
  public static Response newFixedLengthResponse( final String msg ) {
    return newFixedLengthResponse( Status.OK, HTTPD.MIME_HTML, msg );
  }




  static final void safeClose( final Object closeable ) {
    try {
      if ( closeable != null ) {
        if ( closeable instanceof Closeable ) {
          ( (Closeable)closeable ).close();
        } else if ( closeable instanceof Socket ) {
          ( (Socket)closeable ).close();
        } else if ( closeable instanceof ServerSocket ) {
          ( (ServerSocket)closeable ).close();
        } else {
          throw new IllegalArgumentException( "Unknown object to close" );
        }
      }
    } catch ( final IOException e ) {
      HTTPD.LOG.log( Level.SEVERE, "Could not close", e );
    }
  }




  /**
   * Constructs an HTTP server on given port.
   */
  public HTTPD( final int port ) {
    this( null, port );
  }




  /**
   * Constructs an HTTP server on given hostname and port.
   */
  public HTTPD( final String hostname, final int port ) {
    this.hostname = hostname;
    myPort = port;
    setTempFileManagerFactory( new DefaultTempFileManagerFactory() );
    setAsyncRunner( new DefaultAsyncRunner() );
  }




  /**
   * Forcibly closes all connections that are open.
   */
  public synchronized void closeAllConnections() {
    stop();
  }




  /**
   * Create a instance of the client handler, subclasses can return a subclass
   * of the ClientHandler.
   * 
   * @param finalAccept the socket the cleint is connected to
   * @param inputStream the input stream
   * 
   * @return the client handler
   */
  protected ClientHandler createClientHandler( final Socket finalAccept, final InputStream inputStream ) {
    return new ClientHandler( this, inputStream, finalAccept );
  }




  /**
   * Instantiate the server runnable, can be overwritten by subclasses to
   * provide a subclass of the ServerRunnable.
   * 
   * @param timeout the socet timeout to use.
   * 
   * @return the server runnable.
   */
  protected ServerRunnable createServerRunnable( final int timeout ) {
    return new ServerRunnable( this, timeout );
  }




  public String getHostname() {
    return hostname;
  }




  public final int getListeningPort() {
    return myServerSocket == null ? -1 : myServerSocket.getLocalPort();
  }




  public ServerSocketFactory getServerSocketFactory() {
    return serverSocketFactory;
  }




  public TempFileManagerFactory getTempFileManagerFactory() {
    return tempFileManagerFactory;
  }




  public final boolean isAlive() {
    return wasStarted() && !myServerSocket.isClosed() && myThread.isAlive();
  }




  /**
   * Call before {@code start()} to serve over HTTPS instead of HTTP
   */
  public void makeSecure( final SSLServerSocketFactory sslServerSocketFactory, final String[] sslProtocols ) {
    serverSocketFactory = new SecureServerSocketFactory( sslServerSocketFactory, sslProtocols );
  }




  /**
   * Override this to customize the server.
   * 
   * <p>This returns a 404 "Not Found" plain text error response.
   * 
   * @param session The HTTP session
   * 
   * @return HTTP response, see class Response for details
   */
  public Response serve( final IHTTPSession session ) {
    final Map<String, String> files = new HashMap<String, String>();
    final Method method = session.getMethod();
    if ( Method.PUT.equals( method ) || Method.POST.equals( method ) ) {
      try {
        session.parseBody( files );
      } catch ( final IOException ioe ) {
        return newFixedLengthResponse( Status.INTERNAL_ERROR, HTTPD.MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage() );
      } catch ( final ResponseException re ) {
        return newFixedLengthResponse( re.getStatus(), HTTPD.MIME_PLAINTEXT, re.getMessage() );
      }
    }

    final Map<String, String> parms = session.getParms();
    parms.put( HTTPD.QUERY_STRING_PARAMETER, session.getQueryParameterString() );

    return newFixedLengthResponse( Status.NOT_FOUND, HTTPD.MIME_PLAINTEXT, "Not Found" );
  }




  /**
   * Pluggable strategy for asynchronously executing requests.
   * 
   * @param asyncRunner strategy for handling threads.
   */
  public void setAsyncRunner( final AsyncRunner asyncRunner ) {
    this.asyncRunner = asyncRunner;
  }




  public void setServerSocketFactory( final ServerSocketFactory serverSocketFactory ) {
    this.serverSocketFactory = serverSocketFactory;
  }




  /**
   * Pluggable strategy for creating and cleaning up temporary files.
   * 
   * @param factory new strategy for handling temp files.
   */
  public void setTempFileManagerFactory( final TempFileManagerFactory factory ) {
    tempFileManagerFactory = factory;
  }




  /**
   * Start the server.
   * 
   * @throws IOException if the socket is in use.
   */
  public void start() throws IOException {
    start( HTTPD.SOCKET_READ_TIMEOUT );
  }




  /**
   * Starts the server (in setDaemon(true) mode).
   */
  public void start( final int timeout ) throws IOException {
    start( timeout, true );
  }




  /**
   * Start the server.
   * 
   * @param timeout timeout to use for socket connections.
   * @param daemon start the thread daemon or not.
   * 
   * @throws IOException if the socket is in use.
   */
  public void start( final int timeout, final boolean daemon ) throws IOException {
    myServerSocket = getServerSocketFactory().create();
    myServerSocket.setReuseAddress( true );

    final ServerRunnable serverRunnable = createServerRunnable( timeout );
    myThread = new Thread( serverRunnable );
    myThread.setDaemon( daemon );
    myThread.setName( "HTTPD Main Listener" );
    myThread.start();
    while ( !serverRunnable.hasBinded && ( serverRunnable.bindException == null ) ) {
      try {
        Thread.sleep( 10L );
      } catch ( final Throwable e ) {
        // on some platforms (e.g. mobile devices) this may not be allowed, 
        // that is why we catch throwable.  THis should happen right away 
        // because we are just waiting for the socket to bind.
      }
    }
    if ( serverRunnable.bindException != null ) {
      throw serverRunnable.bindException;
    }
  }




  /**
   * Stop the server.
   */
  public void stop() {
    try {
      safeClose( myServerSocket );
      asyncRunner.closeAll();
      if ( myThread != null ) {
        myThread.join();
      }
    } catch ( final Exception e ) {
      HTTPD.LOG.log( Level.SEVERE, "Could not stop all connections", e );
    }
  }




  /**
   * @return true if the gzip compression should be used if the client accespts 
   *         it. Default this option is on for text content and off for 
   *         everything. Override this for custom semantics.
   */
  @SuppressWarnings("static-method")
  protected boolean useGzipWhenAccepted( final Response r ) {
    return ( r.getMimeType() != null ) && r.getMimeType().toLowerCase().contains( "text/" );
  }




  public final boolean wasStarted() {
    return ( myServerSocket != null ) && ( myThread != null );
  }

}
