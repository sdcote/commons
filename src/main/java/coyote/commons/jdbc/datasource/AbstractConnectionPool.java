package coyote.commons.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;


public abstract class AbstractConnectionPool implements ConnectionPool {

  private final boolean _defaultAutoCommit;
  private final int _defaultTransactionIsolation;
  private final boolean _defaultReadOnly;
  private final String _defaultCategory;




  public AbstractConnectionPool( final boolean defaultAutoCommit, final int defaultTransactionIsolation, final boolean defaultReadOnly, final String defaultCategory ) {
    _defaultAutoCommit = defaultAutoCommit;
    _defaultTransactionIsolation = defaultTransactionIsolation;
    _defaultReadOnly = defaultReadOnly;
    _defaultCategory = defaultCategory;
  }




  protected void drop( final Connection connection ) throws SQLException {
    if ( ( connection != null ) && !connection.isClosed() ) {
      connection.close();
    }
  }




  protected void reinitialize( final Connection connection ) throws SQLException {
    connection.rollback();
    connection.setAutoCommit( _defaultAutoCommit );
    connection.setTransactionIsolation( _defaultTransactionIsolation );
    connection.setReadOnly( _defaultReadOnly );
    connection.setCatalog( _defaultCategory );
  }
}
