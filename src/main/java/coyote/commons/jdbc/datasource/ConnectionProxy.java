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


public abstract class ConnectionProxy implements Connection {

  private final Connection _originalConnection;




  public ConnectionProxy( final Connection original ) {
    this._originalConnection = original;
  }




  @Override
  public void abort( final Executor executor ) throws SQLException {
    _originalConnection.abort( executor );
  }




  @Override
  public void clearWarnings() throws SQLException {
    _originalConnection.clearWarnings();
  }




  @Override
  public void close() throws SQLException {
    _originalConnection.close();
  }




  @Override
  public void commit() throws SQLException {
    _originalConnection.commit();
  }




  @Override
  public Array createArrayOf( final String typeName, final Object[] elements ) throws SQLException {
    return _originalConnection.createArrayOf( typeName, elements );
  }




  @Override
  public Blob createBlob() throws SQLException {
    return _originalConnection.createBlob();
  }




  @Override
  public Clob createClob() throws SQLException {
    return _originalConnection.createClob();
  }




  @Override
  public NClob createNClob() throws SQLException {
    return _originalConnection.createNClob();
  }




  @Override
  public SQLXML createSQLXML() throws SQLException {
    return _originalConnection.createSQLXML();
  }




  @Override
  public Statement createStatement() throws SQLException {
    return _originalConnection.createStatement();
  }




  @Override
  public Statement createStatement( final int resultSetType, final int resultSetConcurrency ) throws SQLException {
    return _originalConnection.createStatement( resultSetType, resultSetConcurrency );
  }




  @Override
  public Statement createStatement( final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability ) throws SQLException {
    return _originalConnection.createStatement( resultSetType, resultSetConcurrency, resultSetHoldability );
  }




  @Override
  public Struct createStruct( final String typeName, final Object[] attributes ) throws SQLException {
    return _originalConnection.createStruct( typeName, attributes );
  }




  @Override
  public boolean getAutoCommit() throws SQLException {
    return _originalConnection.getAutoCommit();
  }




  @Override
  public String getCatalog() throws SQLException {
    return _originalConnection.getCatalog();
  }




  @Override
  public Properties getClientInfo() throws SQLException {
    return _originalConnection.getClientInfo();
  }




  @Override
  public String getClientInfo( final String name ) throws SQLException {
    return _originalConnection.getClientInfo( name );
  }




  @Override
  public int getHoldability() throws SQLException {
    return _originalConnection.getHoldability();
  }




  @Override
  public DatabaseMetaData getMetaData() throws SQLException {
    return _originalConnection.getMetaData();
  }




  @Override
  public int getNetworkTimeout() throws SQLException {
    return _originalConnection.getNetworkTimeout();
  }




  protected Connection getOriginal() {
    return _originalConnection;
  }




  @Override
  public String getSchema() throws SQLException {
    return _originalConnection.getSchema();
  }




  @Override
  public int getTransactionIsolation() throws SQLException {
    return _originalConnection.getTransactionIsolation();
  }




  @Override
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    return _originalConnection.getTypeMap();
  }




  @Override
  public SQLWarning getWarnings() throws SQLException {
    return _originalConnection.getWarnings();
  }




  @Override
  public boolean isClosed() throws SQLException {
    return _originalConnection.isClosed();
  }




  @Override
  public boolean isReadOnly() throws SQLException {
    return _originalConnection.isReadOnly();
  }




  @Override
  public boolean isValid( final int timeout ) throws SQLException {
    return _originalConnection.isValid( timeout );
  }




  @Override
  public boolean isWrapperFor( final Class<?> intrfce ) throws SQLException {
    if ( intrfce == null ) {
      throw new SQLException( "interface must not be null" );
    }
    return intrfce.isInstance( _originalConnection );
  }




  @Override
  public String nativeSQL( final String sql ) throws SQLException {
    return _originalConnection.nativeSQL( sql );
  }




  @Override
  public CallableStatement prepareCall( final String sql ) throws SQLException {
    return _originalConnection.prepareCall( sql );
  }




  @Override
  public CallableStatement prepareCall( final String sql, final int resultSetType, final int resultSetConcurrency ) throws SQLException {
    return _originalConnection.prepareCall( sql, resultSetType, resultSetConcurrency );
  }




  @Override
  public CallableStatement prepareCall( final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability ) throws SQLException {
    return _originalConnection.prepareCall( sql, resultSetType, resultSetConcurrency, resultSetHoldability );
  }




  @Override
  public PreparedStatement prepareStatement( final String sql ) throws SQLException {
    return _originalConnection.prepareStatement( sql );
  }




  @Override
  public PreparedStatement prepareStatement( final String sql, final int autoGeneratedKeys ) throws SQLException {
    return _originalConnection.prepareStatement( sql, autoGeneratedKeys );
  }




  @Override
  public PreparedStatement prepareStatement( final String sql, final int resultSetType, final int resultSetConcurrency ) throws SQLException {
    return _originalConnection.prepareStatement( sql, resultSetType, resultSetConcurrency );
  }




  @Override
  public PreparedStatement prepareStatement( final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability ) throws SQLException {
    return _originalConnection.prepareStatement( sql, resultSetType, resultSetConcurrency, resultSetHoldability );
  }




  @Override
  public PreparedStatement prepareStatement( final String sql, final int[] columnIndexes ) throws SQLException {
    return _originalConnection.prepareStatement( sql, columnIndexes );
  }




  @Override
  public PreparedStatement prepareStatement( final String sql, final String[] columnNames ) throws SQLException {
    return _originalConnection.prepareStatement( sql, columnNames );
  }




  @Override
  public void releaseSavepoint( final Savepoint savepoint ) throws SQLException {
    _originalConnection.releaseSavepoint( savepoint );
  }




  @Override
  public void rollback() throws SQLException {
    _originalConnection.rollback();
  }




  @Override
  public void rollback( final Savepoint savepoint ) throws SQLException {
    _originalConnection.rollback();
  }




  @Override
  public void setAutoCommit( final boolean autoCommit ) throws SQLException {
    _originalConnection.setAutoCommit( autoCommit );
  }




  @Override
  public void setCatalog( final String catalog ) throws SQLException {
    _originalConnection.setCatalog( catalog );
  }




  @Override
  public void setClientInfo( final Properties properties ) throws SQLClientInfoException {
    _originalConnection.setClientInfo( properties );
  }




  @Override
  public void setClientInfo( final String name, final String value ) throws SQLClientInfoException {
    _originalConnection.setClientInfo( name, value );
  }




  @Override
  public void setHoldability( final int holdability ) throws SQLException {
    _originalConnection.setHoldability( holdability );
  }




  @Override
  public void setNetworkTimeout( final Executor executor, final int milliseconds ) throws SQLException {
    _originalConnection.setNetworkTimeout( executor, milliseconds );
  }




  @Override
  public void setReadOnly( final boolean readOnly ) throws SQLException {
    _originalConnection.setReadOnly( readOnly );
  }




  @Override
  public Savepoint setSavepoint() throws SQLException {
    return _originalConnection.setSavepoint();
  }




  @Override
  public Savepoint setSavepoint( final String name ) throws SQLException {
    return _originalConnection.setSavepoint( name );
  }




  @Override
  public void setSchema( final String schema ) throws SQLException {
    _originalConnection.setSchema( schema );
  }




  @Override
  public void setTransactionIsolation( final int level ) throws SQLException {
    _originalConnection.setTransactionIsolation( level );
  }




  @Override
  public void setTypeMap( final Map<String, Class<?>> map ) throws SQLException {
    _originalConnection.setTypeMap( map );
  }




  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap( final Class<T> iface ) throws SQLException {
    if ( iface == null ) {
      throw new SQLException( "iface must not be null" );
    }
    if ( !iface.isInstance( _originalConnection ) ) {
      throw new SQLException( String.format( "no object found that implements the interface: %s", iface ) );
    }
    return (T)_originalConnection;
  }
}
