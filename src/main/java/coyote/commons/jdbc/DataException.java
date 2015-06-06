package coyote.commons.jdbc;

public class DataException extends RuntimeException {

  private static final long serialVersionUID = -5339592051913495626L;




  public DataException() {
    super( "Data Access Exception" );
  }




  public DataException( final String message ) {
    super( message );
  }




  public DataException( final String message, final Throwable cause ) {
    super( message, cause );
  }




  public DataException( final Throwable cause ) {
    super( cause );
  }
}
