package coyote.commons.jdbc.datasource;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;


public abstract class PreparedStatementProxy implements PreparedStatement {

  private final PreparedStatement _preparedStatement;




  public PreparedStatementProxy( final PreparedStatement preparedStatement ) {
    _preparedStatement = preparedStatement;
  }




  @Override
  public void addBatch() throws SQLException {
    _preparedStatement.addBatch();
  }




  @Override
  public void addBatch( final String sql ) throws SQLException {
    _preparedStatement.addBatch( sql );
  }




  @Override
  public void cancel() throws SQLException {
    _preparedStatement.cancel();
  }




  @Override
  public void clearBatch() throws SQLException {
    _preparedStatement.clearBatch();
  }




  @Override
  public void clearParameters() throws SQLException {
    _preparedStatement.clearParameters();
  }




  @Override
  public void clearWarnings() throws SQLException {
    _preparedStatement.clearWarnings();
  }




  @Override
  public void close() throws SQLException {
    _preparedStatement.close();
  }




  @Override
  public void closeOnCompletion() throws SQLException {
    _preparedStatement.closeOnCompletion();
  }




  @Override
  public boolean execute() throws SQLException {
    return _preparedStatement.execute();
  }




  @Override
  public boolean execute( final String sql ) throws SQLException {
    return _preparedStatement.execute( sql );
  }




  @Override
  public boolean execute( final String sql, final int autoGeneratedKeys ) throws SQLException {
    return _preparedStatement.execute( sql, autoGeneratedKeys );
  }




  @Override
  public boolean execute( final String sql, final int[] columnIndexes ) throws SQLException {
    return _preparedStatement.execute( sql, columnIndexes );
  }




  @Override
  public boolean execute( final String sql, final String[] columnNames ) throws SQLException {
    return _preparedStatement.execute( sql, columnNames );
  }




  @Override
  public int[] executeBatch() throws SQLException {
    return _preparedStatement.executeBatch();
  }




  @Override
  public ResultSet executeQuery() throws SQLException {
    return _preparedStatement.executeQuery();
  }




  @Override
  public ResultSet executeQuery( final String sql ) throws SQLException {
    return _preparedStatement.executeQuery( sql );
  }




  @Override
  public int executeUpdate() throws SQLException {
    return _preparedStatement.executeUpdate();
  }




  @Override
  public int executeUpdate( final String sql ) throws SQLException {
    return _preparedStatement.executeUpdate( sql );
  }




  @Override
  public int executeUpdate( final String sql, final int autoGeneratedKeys ) throws SQLException {
    return _preparedStatement.executeUpdate( sql, autoGeneratedKeys );
  }




  @Override
  public int executeUpdate( final String sql, final int[] columnIndexes ) throws SQLException {
    return _preparedStatement.executeUpdate( sql, columnIndexes );
  }




  @Override
  public int executeUpdate( final String sql, final String[] columnNames ) throws SQLException {
    return _preparedStatement.executeUpdate( sql, columnNames );
  }




  @Override
  public Connection getConnection() throws SQLException {
    return _preparedStatement.getConnection();
  }




  @Override
  public int getFetchDirection() throws SQLException {
    return _preparedStatement.getFetchDirection();
  }




  @Override
  public int getFetchSize() throws SQLException {
    return _preparedStatement.getFetchSize();
  }




  @Override
  public ResultSet getGeneratedKeys() throws SQLException {
    return _preparedStatement.getGeneratedKeys();
  }




  @Override
  public int getMaxFieldSize() throws SQLException {
    return _preparedStatement.getMaxFieldSize();
  }




  @Override
  public int getMaxRows() throws SQLException {
    return _preparedStatement.getMaxRows();
  }




  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return _preparedStatement.getMetaData();
  }




  @Override
  public boolean getMoreResults() throws SQLException {
    return _preparedStatement.getMoreResults();
  }




  @Override
  public boolean getMoreResults( final int current ) throws SQLException {
    return _preparedStatement.getMoreResults( current );
  }




  @Override
  public ParameterMetaData getParameterMetaData() throws SQLException {
    return _preparedStatement.getParameterMetaData();
  }




  @Override
  public int getQueryTimeout() throws SQLException {
    return _preparedStatement.getQueryTimeout();
  }




  @Override
  public ResultSet getResultSet() throws SQLException {
    return _preparedStatement.getResultSet();
  }




  @Override
  public int getResultSetConcurrency() throws SQLException {
    return _preparedStatement.getResultSetConcurrency();
  }




  @Override
  public int getResultSetHoldability() throws SQLException {
    return _preparedStatement.getResultSetHoldability();
  }




  @Override
  public int getResultSetType() throws SQLException {
    return _preparedStatement.getResultSetType();
  }




  @Override
  public int getUpdateCount() throws SQLException {
    return _preparedStatement.getUpdateCount();
  }




  @Override
  public SQLWarning getWarnings() throws SQLException {
    return _preparedStatement.getWarnings();
  }




  @Override
  public boolean isClosed() throws SQLException {
    return _preparedStatement.isClosed();
  }




  @Override
  public boolean isCloseOnCompletion() throws SQLException {
    return _preparedStatement.isCloseOnCompletion();
  }




  @Override
  public boolean isPoolable() throws SQLException {
    return _preparedStatement.isPoolable();
  }




  @Override
  public boolean isWrapperFor( final Class<?> iface ) throws SQLException {
    return _preparedStatement.isWrapperFor( iface );
  }




  @Override
  public void setArray( final int parameterIndex, final Array x ) throws SQLException {
    _preparedStatement.setArray( parameterIndex, x );
  }




  @Override
  public void setAsciiStream( final int parameterIndex, final InputStream x ) throws SQLException {
    _preparedStatement.setAsciiStream( parameterIndex, x );
  }




  @Override
  public void setAsciiStream( final int parameterIndex, final InputStream x, final int length ) throws SQLException {
    _preparedStatement.setAsciiStream( parameterIndex, x, length );
  }




  @Override
  public void setAsciiStream( final int parameterIndex, final InputStream x, final long length ) throws SQLException {
    _preparedStatement.setAsciiStream( parameterIndex, x, length );
  }




  @Override
  public void setBigDecimal( final int parameterIndex, final BigDecimal x ) throws SQLException {
    _preparedStatement.setBigDecimal( parameterIndex, x );
  }




  @Override
  public void setBinaryStream( final int parameterIndex, final InputStream x ) throws SQLException {
    _preparedStatement.setBinaryStream( parameterIndex, x );
  }




  @Override
  public void setBinaryStream( final int parameterIndex, final InputStream x, final int length ) throws SQLException {
    _preparedStatement.setBinaryStream( parameterIndex, x, length );
  }




  @Override
  public void setBinaryStream( final int parameterIndex, final InputStream x, final long length ) throws SQLException {
    _preparedStatement.setBinaryStream( parameterIndex, x, length );
  }




  @Override
  public void setBlob( final int parameterIndex, final Blob x ) throws SQLException {
    _preparedStatement.setBlob( parameterIndex, x );
  }




  @Override
  public void setBlob( final int parameterIndex, final InputStream inputStream ) throws SQLException {
    _preparedStatement.setBlob( parameterIndex, inputStream );
  }




  @Override
  public void setBlob( final int parameterIndex, final InputStream inputStream, final long length ) throws SQLException {
    _preparedStatement.setBlob( parameterIndex, inputStream, length );
  }




  @Override
  public void setBoolean( final int parameterIndex, final boolean x ) throws SQLException {
    _preparedStatement.setBoolean( parameterIndex, x );
  }




  @Override
  public void setByte( final int parameterIndex, final byte x ) throws SQLException {
    _preparedStatement.setByte( parameterIndex, x );
  }




  @Override
  public void setBytes( final int parameterIndex, final byte[] x ) throws SQLException {
    _preparedStatement.setBytes( parameterIndex, x );
  }




  @Override
  public void setCharacterStream( final int parameterIndex, final Reader reader ) throws SQLException {
    _preparedStatement.setCharacterStream( parameterIndex, reader );
  }




  @Override
  public void setCharacterStream( final int parameterIndex, final Reader reader, final int length ) throws SQLException {
    _preparedStatement.setCharacterStream( parameterIndex, reader, length );
  }




  @Override
  public void setCharacterStream( final int parameterIndex, final Reader reader, final long length ) throws SQLException {
    _preparedStatement.setCharacterStream( parameterIndex, reader, length );
  }




  @Override
  public void setClob( final int parameterIndex, final Clob x ) throws SQLException {
    _preparedStatement.setClob( parameterIndex, x );
  }




  @Override
  public void setClob( final int parameterIndex, final Reader reader ) throws SQLException {
    _preparedStatement.setClob( parameterIndex, reader );
  }




  @Override
  public void setClob( final int parameterIndex, final Reader reader, final long length ) throws SQLException {
    _preparedStatement.setClob( parameterIndex, reader, length );
  }




  @Override
  public void setCursorName( final String name ) throws SQLException {
    _preparedStatement.setCursorName( name );
  }




  @Override
  public void setDate( final int parameterIndex, final Date x ) throws SQLException {
    _preparedStatement.setDate( parameterIndex, x );
  }




  @Override
  public void setDate( final int parameterIndex, final Date x, final Calendar cal ) throws SQLException {
    _preparedStatement.setDate( parameterIndex, x, cal );
  }




  @Override
  public void setDouble( final int parameterIndex, final double x ) throws SQLException {
    _preparedStatement.setDouble( parameterIndex, x );
  }




  @Override
  public void setEscapeProcessing( final boolean enable ) throws SQLException {
    _preparedStatement.setEscapeProcessing( enable );
  }




  @Override
  public void setFetchDirection( final int direction ) throws SQLException {
    _preparedStatement.setFetchDirection( direction );
  }




  @Override
  public void setFetchSize( final int rows ) throws SQLException {
    _preparedStatement.setFetchSize( rows );
  }




  @Override
  public void setFloat( final int parameterIndex, final float x ) throws SQLException {
    _preparedStatement.setFloat( parameterIndex, x );
  }




  @Override
  public void setInt( final int parameterIndex, final int x ) throws SQLException {
    _preparedStatement.setInt( parameterIndex, x );
  }




  @Override
  public void setLong( final int parameterIndex, final long x ) throws SQLException {
    _preparedStatement.setLong( parameterIndex, x );
  }




  @Override
  public void setMaxFieldSize( final int max ) throws SQLException {
    _preparedStatement.setMaxFieldSize( max );
  }




  @Override
  public void setMaxRows( final int max ) throws SQLException {
    _preparedStatement.setMaxRows( max );
  }




  @Override
  public void setNCharacterStream( final int parameterIndex, final Reader value ) throws SQLException {
    _preparedStatement.setNCharacterStream( parameterIndex, value );
  }




  @Override
  public void setNCharacterStream( final int parameterIndex, final Reader value, final long length ) throws SQLException {
    _preparedStatement.setNCharacterStream( parameterIndex, value, length );
  }




  @Override
  public void setNClob( final int parameterIndex, final NClob value ) throws SQLException {
    _preparedStatement.setNClob( parameterIndex, value );
  }




  @Override
  public void setNClob( final int parameterIndex, final Reader reader ) throws SQLException {
    _preparedStatement.setNClob( parameterIndex, reader );
  }




  @Override
  public void setNClob( final int parameterIndex, final Reader reader, final long length ) throws SQLException {
    _preparedStatement.setNClob( parameterIndex, reader, length );
  }




  @Override
  public void setNString( final int parameterIndex, final String value ) throws SQLException {
    _preparedStatement.setNString( parameterIndex, value );
  }




  @Override
  public void setNull( final int parameterIndex, final int sqlType ) throws SQLException {
    _preparedStatement.setNull( parameterIndex, sqlType );
  }




  @Override
  public void setNull( final int parameterIndex, final int sqlType, final String typeName ) throws SQLException {
    _preparedStatement.setNull( parameterIndex, sqlType, typeName );
  }




  @Override
  public void setObject( final int parameterIndex, final Object x ) throws SQLException {
    _preparedStatement.setObject( parameterIndex, x );
  }




  @Override
  public void setObject( final int parameterIndex, final Object x, final int targetSqlType ) throws SQLException {
    _preparedStatement.setObject( parameterIndex, x, targetSqlType );
  }




  @Override
  public void setObject( final int parameterIndex, final Object x, final int targetSqlType, final int scaleOrLength ) throws SQLException {
    _preparedStatement.setObject( parameterIndex, x, targetSqlType, scaleOrLength );
  }




  @Override
  public void setPoolable( final boolean poolable ) throws SQLException {
    _preparedStatement.setPoolable( poolable );
  }




  @Override
  public void setQueryTimeout( final int seconds ) throws SQLException {
    _preparedStatement.setQueryTimeout( seconds );
  }




  @Override
  public void setRef( final int parameterIndex, final Ref x ) throws SQLException {
    _preparedStatement.setRef( parameterIndex, x );
  }




  @Override
  public void setRowId( final int parameterIndex, final RowId x ) throws SQLException {
    _preparedStatement.setRowId( parameterIndex, x );
  }




  @Override
  public void setShort( final int parameterIndex, final short x ) throws SQLException {
    _preparedStatement.setShort( parameterIndex, x );
  }




  @Override
  public void setSQLXML( final int parameterIndex, final SQLXML xmlObject ) throws SQLException {
    _preparedStatement.setSQLXML( parameterIndex, xmlObject );
  }




  @Override
  public void setString( final int parameterIndex, final String x ) throws SQLException {
    _preparedStatement.setString( parameterIndex, x );
  }




  @Override
  public void setTime( final int parameterIndex, final Time x ) throws SQLException {
    _preparedStatement.setTime( parameterIndex, x );
  }




  @Override
  public void setTime( final int parameterIndex, final Time x, final Calendar cal ) throws SQLException {
    _preparedStatement.setTime( parameterIndex, x, cal );
  }




  @Override
  public void setTimestamp( final int parameterIndex, final Timestamp x ) throws SQLException {
    _preparedStatement.setTimestamp( parameterIndex, x );
  }




  @Override
  public void setTimestamp( final int parameterIndex, final Timestamp x, final Calendar cal ) throws SQLException {
    _preparedStatement.setTimestamp( parameterIndex, x, cal );
  }




  @Override
  public void setUnicodeStream( final int parameterIndex, final InputStream x, final int length ) throws SQLException {
    _preparedStatement.setUnicodeStream( parameterIndex, x, length );
  }




  @Override
  public void setURL( final int parameterIndex, final URL x ) throws SQLException {
    _preparedStatement.setURL( parameterIndex, x );
  }




  @Override
  public <T> T unwrap( final Class<T> iface ) throws SQLException {
    return _preparedStatement.unwrap( iface );
  }
}