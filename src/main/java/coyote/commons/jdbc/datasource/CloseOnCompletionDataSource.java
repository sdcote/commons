package coyote.commons.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import coyote.commons.Assert;

/**
 * All connections constructed by a {@code CloseOnCompletionDataSource} 
 * instance will be closed sequentially as soon as the datasource has been 
 * closed. This removes the need to close statements manually.
 */
public class CloseOnCompletionDataSource extends AbstractDataSource {

  private final DataSource _dataSource;




  public CloseOnCompletionDataSource( final DataSource dataSource ) {
    Assert.notNull( dataSource, "_dataSource cannot be null" );
    _dataSource = dataSource;
  }




  @Override
  public Connection getConnection() throws SQLException {
    return new CloseOnCompletionConnection( _dataSource.getConnection() );
  }




  @Override
  public Connection getConnection( final String username, final String password ) throws SQLException {
    return new CloseOnCompletionConnection( _dataSource.getConnection( username, password ) );
  }




  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap( final Class<T> iface ) throws SQLException {
    if ( iface.isInstance( this ) ) {
      return (T)_dataSource;
    }
    return super.unwrap( iface );
  }
}
