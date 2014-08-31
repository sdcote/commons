package coyote.commons;

import java.io.PrintStream;


/**
 * Internal logger.
 * 
 * Logging is controlled by system properties:
 * <pre>System.getProperties().setProperty( "coyote.commons.Log.trace", "true" );
 * <pre>System.getProperties().setProperty( "coyote.commons.Log.debug", "true" );</pre>
 */
public final class Log {

  public static enum Level {
    TRACE, DEBUG, INFO, WARN, ERROR
  }

  public static final boolean TRACE = Boolean.getBoolean( Log.class.getName() + ".trace" );

  public static final boolean DEBUG = TRACE || Boolean.getBoolean( Log.class.getName() + ".debug" );

  private static PrintStream output = System.out;




  public static void debug( final Object... messages ) {
    if ( TRACE || DEBUG ) {
      log( Level.DEBUG, messages );
    }
  }




  public static void trace( final Object... messages ) {
    if ( TRACE ) {
      log( Level.TRACE, messages );
    }
  }




  public static void warn( final Object... messages ) {
    log( Level.WARN, messages );
  }




  public static void error( final Object... messages ) {
    log( Level.ERROR, messages );
  }




  public static PrintStream getOutput() {
    return output;
  }




  /**
   */
  public static void info( final Object... messages ) {
    log( Level.INFO, messages );
  }




  static void log( final Level level, final Object... messages ) {
    synchronized( output ) {
      output.format( "[%s] ", level );

      for ( int i = 0; i < messages.length; i++ ) {
        if ( ( ( i + 1 ) == messages.length ) && ( messages[i] instanceof Throwable ) ) {
          output.println();
          ( (Throwable)messages[i] ).printStackTrace( output );
        } else {
          render( output, messages[i] );
        }
      }

      output.println();
      output.flush();
    }
  }




  /**
   * Helper to support rendering messages.
   */
  static void render( final PrintStream out, final Object message ) {
    if ( message.getClass().isArray() ) {
      final Object[] array = (Object[])message;

      out.print( "[" );
      for ( int i = 0; i < array.length; i++ ) {
        out.print( array[i] );
        if ( ( i + 1 ) < array.length ) {
          out.print( "," );
        }
      }
      out.print( "]" );
    } else {
      out.print( message );
    }
  }




  public static void setOutput( final PrintStream out ) {
    Assert.notNull( out );
    output = out;
  }

}