package coyote.commons.jdbc;

public class DataAccessException extends RuntimeException {





  /**
   * 
   */
  private static final long serialVersionUID = -5339592051913495626L;




  public DataAccessException() {
    super( "data access exception occurred" );
  }




  public DataAccessException( final String message ) {
    super( message );
  }




  public DataAccessException( final String message, final Throwable cause ) {
    super( message, cause );
  }




  public DataAccessException( final Throwable cause ) {
    super( cause );
  }
}
