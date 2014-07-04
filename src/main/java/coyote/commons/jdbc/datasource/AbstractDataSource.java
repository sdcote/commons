package coyote.commons.jdbc.datasource;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import coyote.commons.Assert;


public abstract class AbstractDataSource implements DataSource {

  @Override
  public int getLoginTimeout() throws SQLException {
    return 0;
  }




  @Override
  public PrintWriter getLogWriter() throws SQLException {
    throw new UnsupportedOperationException( "getLogWriter is not " + "supported" );
  }




  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );
  }




  @Override
  public boolean isWrapperFor( final Class<?> iface ) throws SQLException {
    return DataSource.class.equals( iface );
  }




  @Override
  public void setLoginTimeout( final int seconds ) throws SQLException {
    throw new UnsupportedOperationException( "setLoginTimeout is not " + "supported" );
  }




  @Override
  public void setLogWriter( final PrintWriter out ) throws SQLException {
    throw new UnsupportedOperationException( "setLogWriter is not " + "supported" );
  }




  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap( final Class<T> iface ) throws SQLException {
    Assert.argumentIsNotNull( iface, "Argument iface cannot be null." );
    if ( iface.isInstance( this ) ) {
      return (T)this;
    }
    throw new SQLException( String.format( "no object found that " + "implements the interface [%s]", iface ) );
  }
}
