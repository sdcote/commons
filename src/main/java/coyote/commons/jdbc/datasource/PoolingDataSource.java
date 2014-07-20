package coyote.commons.jdbc.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import coyote.commons.Assert;


public class PoolingDataSource implements DataSource {

  private final DataSource originalDataSource;
  private final ConnectionPool connectionPool;




  public PoolingDataSource( final DataSource originalDataSource, final ConnectionPool connectionPool ) {
    Assert.notNull( originalDataSource, "Datasource must not be null" );
    Assert.notNull( connectionPool, "ConnectionPool must not be null" );

    this.originalDataSource = originalDataSource;
    this.connectionPool = connectionPool;
  }




  public PoolingDataSource( final DataSource originalDataSource, final int initialSize, final int maxSize, final boolean defaultAutoCommit, final int defaultTransactionIsolation, final boolean autoReadOnly, final String defaultCategory ) throws SQLException {
    this( originalDataSource, new QueuedConnectionPool( originalDataSource, initialSize, maxSize, defaultAutoCommit, defaultTransactionIsolation, autoReadOnly, defaultCategory ) );
  }




  @Override
  public Connection getConnection() throws SQLException {
    try {
      return new PoolableConnection( connectionPool.borrowConnection(), connectionPool );
    } catch ( final Exception ex ) {
      throw new SQLException( ex );
    }
  }




  /**
   * Invoking this method will not retrieves the connection from the pool.
   * It will get the connection directly from the data source.
   * @param username the database user on whose behalf the connection is
   *                 being made
   * @param password the user's password
   * @return a connection from the data source
   * @exception SQLException if a database access error occurs
   */
  @Override
  public Connection getConnection( final String username, final String password ) throws SQLException {
    return originalDataSource.getConnection( username, password );
  }




  @Override
  public int getLoginTimeout() throws SQLException {
    return originalDataSource.getLoginTimeout();
  }




  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return originalDataSource.getLogWriter();
  }




  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return originalDataSource.getParentLogger();
  }




  @Override
  public boolean isWrapperFor( final Class<?> iface ) throws SQLException {
    return iface.isInstance( originalDataSource );
  }




  @Override
  public void setLoginTimeout( final int seconds ) throws SQLException {
    originalDataSource.setLoginTimeout( seconds );
  }




  @Override
  public void setLogWriter( final PrintWriter out ) throws SQLException {
    originalDataSource.setLogWriter( out );
  }




  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap( final Class<T> iface ) throws SQLException {
    if ( iface == null ) {
      throw new SQLException( "iface must not be null" );
    }
    if ( !iface.isInstance( originalDataSource ) ) {
      throw new SQLException( String.format( "no object found that implements the interface: %s", iface ) );
    }
    return (T)originalDataSource;
  }
}
