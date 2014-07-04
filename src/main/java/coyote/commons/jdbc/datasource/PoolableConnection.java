package coyote.commons.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;


public class PoolableConnection extends ConnectionProxy {

  private final ConnectionPool _connectionPool;




  public PoolableConnection( final Connection connectionToBePooled, final ConnectionPool connectionPool ) {
    super( connectionToBePooled );
    _connectionPool = connectionPool;
  }




  @Override
  public void close() throws SQLException {
    try {
      _connectionPool.returnConnection( getOriginal() );
    } catch ( final SQLException ex ) {
      throw ex;
    } catch ( final Exception ex ) {
      throw new SQLException( ex );
    }
  }
}
