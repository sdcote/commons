package coyote.commons.jdbc.datasource;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import coyote.commons.Assert;


class ReplicationConnection implements Connection {

  private Connection _currentConn;
  private final Connection _readWriteConn;
  private final Connection _readOnlyConn;




  public ReplicationConnection( final Connection readWriteConn, final Connection readOnlyConn ) {
    Assert.argumentIsNotNull( readWriteConn, "Connection cannot be null" );
    Assert.argumentIsNotNull( readOnlyConn, "Connection cannot be null" );

    _readWriteConn = readWriteConn;
    _readOnlyConn = readOnlyConn;
    _currentConn = _readWriteConn;
  }




  @Override
  public void abort( final Executor executor ) throws SQLException {
    _currentConn.abort( executor );
  }




  @Override
  public void clearWarnings() throws SQLException {
    _currentConn.clearWarnings();
  }




  @Override
  public void close() throws SQLException {
    _readWriteConn.close();
    _readOnlyConn.close();
  }




  @Override
  public void commit() throws SQLException {
    _currentConn.commit();
  }




  @Override
  public Array createArrayOf( final String typeName, final Object[] elements ) throws SQLException {
    return _currentConn.createArrayOf( typeName, elements );
  }




  @Override
  public Blob createBlob() throws SQLException {
    return _currentConn.createBlob();
  }




  @Override
  public Clob createClob() throws SQLException {
    return _currentConn.createClob();
  }




  @Override
  public NClob createNClob() throws SQLException {
    return _currentConn.createNClob();
  }




  @Override
  public SQLXML createSQLXML() throws SQLException {
    return _currentConn.createSQLXML();
  }




  @Override
  public Statement createStatement() throws SQLException {
    return _currentConn.createStatement();
  }




  @Override
  public Statement createStatement( final int resultSetType, final int resultSetConcurrency ) throws SQLException {
    return _currentConn.createStatement( resultSetType, resultSetConcurrency );
  }




  @Override
  public Statement createStatement( final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability ) throws SQLException {
    return _currentConn.createStatement( resultSetType, resultSetConcurrency, resultSetHoldability );
  }




  @Override
  public Struct createStruct( final String typeName, final Object[] attributes ) throws SQLException {
    return _currentConn.createStruct( typeName, attributes );
  }




  @Override
  public boolean getAutoCommit() throws SQLException {
    return _currentConn.getAutoCommit();
  }




  @Override
  public String getCatalog() throws SQLException {
    return _currentConn.getCatalog();
  }




  @Override
  public Properties getClientInfo() throws SQLException {
    return _currentConn.getClientInfo();
  }




  @Override
  public String getClientInfo( final String name ) throws SQLException {
    return _currentConn.getClientInfo( name );
  }




  @Override
  public int getHoldability() throws SQLException {
    return _currentConn.getHoldability();
  }




  @Override
  public DatabaseMetaData getMetaData() throws SQLException {
    return _currentConn.getMetaData();
  }




  @Override
  public int getNetworkTimeout() throws SQLException {
    return _currentConn.getNetworkTimeout();
  }




  @Override
  public String getSchema() throws SQLException {
    return _currentConn.getSchema();
  }




  @Override
  public int getTransactionIsolation() throws SQLException {
    return _currentConn.getTransactionIsolation();
  }




  @Override
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    return _currentConn.getTypeMap();
  }




  @Override
  public SQLWarning getWarnings() throws SQLException {
    return _currentConn.getWarnings();
  }




  @Override
  public boolean isClosed() throws SQLException {
    return _currentConn.isClosed();
  }




  @Override
  public boolean isReadOnly() throws SQLException {
    return _currentConn.isReadOnly();
  }




  @Override
  public boolean isValid( final int timeout ) throws SQLException {
    return _currentConn.isValid( timeout );
  }




  @Override
  public boolean isWrapperFor( final Class<?> iface ) throws SQLException {
    return _currentConn.isWrapperFor( iface );
  }




  @Override
  public String nativeSQL( final String sql ) throws SQLException {
    return _currentConn.nativeSQL( sql );
  }




  @Override
  public CallableStatement prepareCall( final String sql ) throws SQLException {
    return _currentConn.prepareCall( sql );
  }




  @Override
  public CallableStatement prepareCall( final String sql, final int resultSetType, final int resultSetConcurrency ) throws SQLException {
    return _currentConn.prepareCall( sql, resultSetType, resultSetConcurrency );
  }




  @Override
  public CallableStatement prepareCall( final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability ) throws SQLException {
    return _currentConn.prepareCall( sql, resultSetType, resultSetConcurrency, resultSetHoldability );
  }




  @Override
  public PreparedStatement prepareStatement( final String sql ) throws SQLException {
    return _currentConn.prepareStatement( sql );
  }




  @Override
  public PreparedStatement prepareStatement( final String sql, final int autoGeneratedKeys ) throws SQLException {
    return _currentConn.prepareStatement( sql, autoGeneratedKeys );
  }




  @Override
  public PreparedStatement prepareStatement( final String sql, final int resultSetType, final int resultSetConcurrency ) throws SQLException {
    return _currentConn.prepareStatement( sql, resultSetType, resultSetConcurrency );
  }




  @Override
  public PreparedStatement prepareStatement( final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability ) throws SQLException {
    return _currentConn.prepareStatement( sql, resultSetType, resultSetConcurrency, resultSetHoldability );
  }




  @Override
  public PreparedStatement prepareStatement( final String sql, final int[] columnIndexes ) throws SQLException {
    return _currentConn.prepareStatement( sql, columnIndexes );
  }




  @Override
  public PreparedStatement prepareStatement( final String sql, final String[] columnNames ) throws SQLException {
    return _currentConn.prepareStatement( sql, columnNames );
  }




  @Override
  public void releaseSavepoint( final Savepoint savepoint ) throws SQLException {
    _currentConn.releaseSavepoint( savepoint );
  }




  @Override
  public void rollback() throws SQLException {
    _currentConn.rollback();
  }




  @Override
  public void rollback( final Savepoint savepoint ) throws SQLException {
    _currentConn.rollback();
  }




  @Override
  public void setAutoCommit( final boolean autoCommit ) throws SQLException {
    _currentConn.setAutoCommit( autoCommit );
  }




  @Override
  public void setCatalog( final String catalog ) throws SQLException {
    _currentConn.setCatalog( catalog );
  }




  @Override
  public void setClientInfo( final Properties properties ) throws SQLClientInfoException {
    _currentConn.setClientInfo( properties );
  }




  @Override
  public void setClientInfo( final String name, final String value ) throws SQLClientInfoException {
    _currentConn.setClientInfo( name, value );
  }




  @Override
  public void setHoldability( final int holdability ) throws SQLException {
    _currentConn.setHoldability( holdability );
  }




  @Override
  public void setNetworkTimeout( final Executor executor, final int milliseconds ) throws SQLException {
    _currentConn.setNetworkTimeout( executor, milliseconds );
  }




  @Override
  public void setReadOnly( final boolean readOnly ) throws SQLException {
    if ( readOnly ) {
      if ( _currentConn != _readOnlyConn ) {
        switchToConnection( _readOnlyConn );
      }
    } else {
      if ( _currentConn != _readWriteConn ) {
        switchToConnection( _readWriteConn );
      }
    }
    _currentConn.setReadOnly( readOnly );
  }




  @Override
  public Savepoint setSavepoint() throws SQLException {
    return _currentConn.setSavepoint();
  }




  @Override
  public Savepoint setSavepoint( final String name ) throws SQLException {
    return _currentConn.setSavepoint( name );
  }




  @Override
  public void setSchema( final String schema ) throws SQLException {
    _currentConn.setSchema( schema );
  }




  @Override
  public void setTransactionIsolation( final int level ) throws SQLException {
    _currentConn.setTransactionIsolation( level );
  }




  @Override
  public void setTypeMap( final Map<String, Class<?>> map ) throws SQLException {
    _currentConn.setTypeMap( map );
  }




  private void switchToConnection( final Connection connection ) throws SQLException {
    connection.setAutoCommit( _currentConn.getAutoCommit() );
    connection.setCatalog( _currentConn.getCatalog() );
    connection.setTransactionIsolation( _currentConn.getTransactionIsolation() );
    _currentConn = connection;
  }




  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap( final Class<T> iface ) throws SQLException {
    return (T)_currentConn;
  }
}
