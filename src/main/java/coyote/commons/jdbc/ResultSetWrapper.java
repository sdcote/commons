package coyote.commons.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;


/**
 * This is an abstract wrapper class for the standard result set which allows
 * other classes to implement extended functionality into the result set.
 * 
 * @see @code ExtendedResultSet} and {@code ExtendedResultSetImpl)
 */
public abstract class ResultSetWrapper implements ResultSet {

  private final ResultSet resultSet;




  public ResultSetWrapper( final ResultSet resultSet ) {
    this.resultSet = resultSet;
  }




  @Override
  public boolean absolute( final int row ) throws SQLException {
    return resultSet.absolute( row );
  }




  @Override
  public void afterLast() throws SQLException {
    resultSet.afterLast();
  }




  @Override
  public void beforeFirst() throws SQLException {
    resultSet.beforeFirst();
  }




  @Override
  public void cancelRowUpdates() throws SQLException {
    resultSet.cancelRowUpdates();
  }




  @Override
  public void clearWarnings() throws SQLException {
    resultSet.clearWarnings();
  }




  @Override
  public void close() throws SQLException {
    resultSet.close();
  }




  @Override
  public void deleteRow() throws SQLException {
    resultSet.deleteRow();
  }




  @Override
  public int findColumn( final String columnLabel ) throws SQLException {
    return resultSet.findColumn( columnLabel );
  }




  @Override
  public boolean first() throws SQLException {
    return resultSet.first();
  }




  @Override
  public Array getArray( final int columnIndex ) throws SQLException {
    return resultSet.getArray( columnIndex );
  }




  @Override
  public Array getArray( final String columnLabel ) throws SQLException {
    return resultSet.getArray( columnLabel );
  }




  @Override
  public InputStream getAsciiStream( final int columnIndex ) throws SQLException {
    return resultSet.getAsciiStream( columnIndex );
  }




  @Override
  public InputStream getAsciiStream( final String columnLabel ) throws SQLException {
    return resultSet.getAsciiStream( columnLabel );
  }




  @Override
  public BigDecimal getBigDecimal( final int columnIndex ) throws SQLException {
    return resultSet.getBigDecimal( columnIndex );
  }




  @Override
  public BigDecimal getBigDecimal( final int columnIndex, final int scale ) throws SQLException {
    return resultSet.getBigDecimal( columnIndex, scale );
  }




  @Override
  public BigDecimal getBigDecimal( final String columnLabel ) throws SQLException {
    return resultSet.getBigDecimal( columnLabel );
  }




  @Override
  public BigDecimal getBigDecimal( final String columnLabel, final int scale ) throws SQLException {
    return resultSet.getBigDecimal( columnLabel, scale );
  }




  @Override
  public InputStream getBinaryStream( final int columnIndex ) throws SQLException {
    return resultSet.getBinaryStream( columnIndex );
  }




  @Override
  public InputStream getBinaryStream( final String columnLabel ) throws SQLException {
    return resultSet.getBinaryStream( columnLabel );
  }




  @Override
  public Blob getBlob( final int columnIndex ) throws SQLException {
    return resultSet.getBlob( columnIndex );
  }




  @Override
  public Blob getBlob( final String columnLabel ) throws SQLException {
    return resultSet.getBlob( columnLabel );
  }




  @Override
  public boolean getBoolean( final int columnIndex ) throws SQLException {
    return resultSet.getBoolean( columnIndex );
  }




  @Override
  public boolean getBoolean( final String columnLabel ) throws SQLException {
    return resultSet.getBoolean( columnLabel );
  }




  @Override
  public byte getByte( final int columnIndex ) throws SQLException {
    return resultSet.getByte( columnIndex );
  }




  @Override
  public byte getByte( final String columnLabel ) throws SQLException {
    return resultSet.getByte( columnLabel );
  }




  @Override
  public byte[] getBytes( final int columnIndex ) throws SQLException {
    return resultSet.getBytes( columnIndex );
  }




  @Override
  public byte[] getBytes( final String columnLabel ) throws SQLException {
    return resultSet.getBytes( columnLabel );
  }




  @Override
  public Reader getCharacterStream( final int columnIndex ) throws SQLException {
    return resultSet.getCharacterStream( columnIndex );
  }




  @Override
  public Reader getCharacterStream( final String columnLabel ) throws SQLException {
    return resultSet.getCharacterStream( columnLabel );
  }




  @Override
  public Clob getClob( final int columnIndex ) throws SQLException {
    return resultSet.getClob( columnIndex );
  }




  @Override
  public Clob getClob( final String columnLabel ) throws SQLException {
    return resultSet.getClob( columnLabel );
  }




  @Override
  public int getConcurrency() throws SQLException {
    return resultSet.getConcurrency();
  }




  @Override
  public String getCursorName() throws SQLException {
    return resultSet.getCursorName();
  }




  @Override
  public Date getDate( final int columnIndex ) throws SQLException {
    return resultSet.getDate( columnIndex );
  }




  @Override
  public Date getDate( final int columnIndex, final Calendar cal ) throws SQLException {
    return resultSet.getDate( columnIndex, cal );
  }




  @Override
  public Date getDate( final String columnLabel ) throws SQLException {
    return resultSet.getDate( columnLabel );
  }




  @Override
  public Date getDate( final String columnLabel, final Calendar cal ) throws SQLException {
    return resultSet.getDate( columnLabel, cal );
  }




  @Override
  public double getDouble( final int columnIndex ) throws SQLException {
    return resultSet.getDouble( columnIndex );
  }




  @Override
  public double getDouble( final String columnLabel ) throws SQLException {
    return resultSet.getDouble( columnLabel );
  }




  @Override
  public int getFetchDirection() throws SQLException {
    return resultSet.getFetchDirection();
  }




  @Override
  public int getFetchSize() throws SQLException {
    return resultSet.getFetchSize();
  }




  @Override
  public float getFloat( final int columnIndex ) throws SQLException {
    return resultSet.getFloat( columnIndex );
  }




  @Override
  public float getFloat( final String columnLabel ) throws SQLException {
    return resultSet.getFloat( columnLabel );
  }




  @Override
  public int getHoldability() throws SQLException {
    return resultSet.getHoldability();
  }




  @Override
  public int getInt( final int columnIndex ) throws SQLException {
    return resultSet.getInt( columnIndex );
  }




  @Override
  public int getInt( final String columnLabel ) throws SQLException {
    return resultSet.getInt( columnLabel );
  }




  @Override
  public long getLong( final int columnIndex ) throws SQLException {
    return resultSet.getLong( columnIndex );
  }




  @Override
  public long getLong( final String columnLabel ) throws SQLException {
    return resultSet.getLong( columnLabel );
  }




  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return resultSet.getMetaData();
  }




  @Override
  public Reader getNCharacterStream( final int columnIndex ) throws SQLException {
    return resultSet.getNCharacterStream( columnIndex );
  }




  @Override
  public Reader getNCharacterStream( final String columnLabel ) throws SQLException {
    return resultSet.getNCharacterStream( columnLabel );
  }




  @Override
  public NClob getNClob( final int columnIndex ) throws SQLException {
    return resultSet.getNClob( columnIndex );
  }




  @Override
  public NClob getNClob( final String columnLabel ) throws SQLException {
    return resultSet.getNClob( columnLabel );
  }




  @Override
  public String getNString( final int columnIndex ) throws SQLException {
    return resultSet.getNString( columnIndex );
  }




  @Override
  public String getNString( final String columnLabel ) throws SQLException {
    return resultSet.getNString( columnLabel );
  }




  @Override
  public Object getObject( final int columnIndex ) throws SQLException {
    return resultSet.getObject( columnIndex );
  }




  @Override
  public <T> T getObject( final int columnIndex, final Class<T> type ) throws SQLException {
    return resultSet.getObject( columnIndex, type );
  }




  @Override
  public Object getObject( final int columnIndex, final Map<String, Class<?>> map ) throws SQLException {
    return resultSet.getObject( columnIndex, map );
  }




  @Override
  public Object getObject( final String columnLabel ) throws SQLException {
    return resultSet.getObject( columnLabel );
  }




  @Override
  public <T> T getObject( final String columnLabel, final Class<T> type ) throws SQLException {
    return resultSet.getObject( columnLabel, type );
  }




  @Override
  public Object getObject( final String columnLabel, final Map<String, Class<?>> map ) throws SQLException {
    return resultSet.getObject( columnLabel, map );
  }




  @Override
  public Ref getRef( final int columnIndex ) throws SQLException {
    return resultSet.getRef( columnIndex );
  }




  @Override
  public Ref getRef( final String columnLabel ) throws SQLException {
    return resultSet.getRef( columnLabel );
  }




  @Override
  public int getRow() throws SQLException {
    return resultSet.getRow();
  }




  @Override
  public RowId getRowId( final int columnIndex ) throws SQLException {
    return resultSet.getRowId( columnIndex );
  }




  @Override
  public RowId getRowId( final String columnLabel ) throws SQLException {
    return resultSet.getRowId( columnLabel );
  }




  @Override
  public short getShort( final int columnIndex ) throws SQLException {
    return resultSet.getShort( columnIndex );
  }




  @Override
  public short getShort( final String columnLabel ) throws SQLException {
    return resultSet.getShort( columnLabel );
  }




  @Override
  public SQLXML getSQLXML( final int columnIndex ) throws SQLException {
    return resultSet.getSQLXML( columnIndex );
  }




  @Override
  public SQLXML getSQLXML( final String columnLabel ) throws SQLException {
    return resultSet.getSQLXML( columnLabel );
  }




  @Override
  public Statement getStatement() throws SQLException {
    return resultSet.getStatement();
  }




  @Override
  public String getString( final int columnIndex ) throws SQLException {
    return resultSet.getString( columnIndex );
  }




  @Override
  public String getString( final String columnLabel ) throws SQLException {
    return resultSet.getString( columnLabel );
  }




  @Override
  public Time getTime( final int columnIndex ) throws SQLException {
    return resultSet.getTime( columnIndex );
  }




  @Override
  public Time getTime( final int columnIndex, final Calendar cal ) throws SQLException {
    return resultSet.getTime( columnIndex, cal );
  }




  @Override
  public Time getTime( final String columnLabel ) throws SQLException {
    return resultSet.getTime( columnLabel );
  }




  @Override
  public Time getTime( final String columnLabel, final Calendar cal ) throws SQLException {
    return resultSet.getTime( columnLabel, cal );
  }




  @Override
  public Timestamp getTimestamp( final int columnIndex ) throws SQLException {
    return resultSet.getTimestamp( columnIndex );
  }




  @Override
  public Timestamp getTimestamp( final int columnIndex, final Calendar cal ) throws SQLException {
    return resultSet.getTimestamp( columnIndex, cal );
  }




  @Override
  public Timestamp getTimestamp( final String columnLabel ) throws SQLException {
    return resultSet.getTimestamp( columnLabel );
  }




  @Override
  public Timestamp getTimestamp( final String columnLabel, final Calendar cal ) throws SQLException {
    return resultSet.getTimestamp( columnLabel, cal );
  }




  @Override
  public int getType() throws SQLException {
    return resultSet.getType();
  }




  @Override
  public InputStream getUnicodeStream( final int columnIndex ) throws SQLException {
    return resultSet.getUnicodeStream( columnIndex );
  }




  @Override
  public InputStream getUnicodeStream( final String columnLabel ) throws SQLException {
    return resultSet.getUnicodeStream( columnLabel );
  }




  @Override
  public URL getURL( final int columnIndex ) throws SQLException {
    return resultSet.getURL( columnIndex );
  }




  @Override
  public URL getURL( final String columnLabel ) throws SQLException {
    return resultSet.getURL( columnLabel );
  }




  @Override
  public SQLWarning getWarnings() throws SQLException {
    return resultSet.getWarnings();
  }




  @Override
  public void insertRow() throws SQLException {
    resultSet.insertRow();
  }




  @Override
  public boolean isAfterLast() throws SQLException {
    return resultSet.isAfterLast();
  }




  @Override
  public boolean isBeforeFirst() throws SQLException {
    return resultSet.isBeforeFirst();
  }




  @Override
  public boolean isClosed() throws SQLException {
    return resultSet.isClosed();
  }




  @Override
  public boolean isFirst() throws SQLException {
    return resultSet.isFirst();
  }




  @Override
  public boolean isLast() throws SQLException {
    return resultSet.isLast();
  }




  @Override
  public boolean isWrapperFor( final Class<?> iface ) throws SQLException {
    return resultSet.isWrapperFor( iface );
  }




  @Override
  public boolean last() throws SQLException {
    return resultSet.last();
  }




  @Override
  public void moveToCurrentRow() throws SQLException {
    resultSet.moveToCurrentRow();
  }




  @Override
  public void moveToInsertRow() throws SQLException {
    resultSet.moveToInsertRow();
  }




  @Override
  public boolean next() throws SQLException {
    return resultSet.next();
  }




  @Override
  public boolean previous() throws SQLException {
    return resultSet.previous();
  }




  @Override
  public void refreshRow() throws SQLException {
    resultSet.refreshRow();
  }




  @Override
  public boolean relative( final int rows ) throws SQLException {
    return resultSet.relative( rows );
  }




  @Override
  public boolean rowDeleted() throws SQLException {
    return resultSet.rowDeleted();
  }




  @Override
  public boolean rowInserted() throws SQLException {
    return resultSet.rowInserted();
  }




  @Override
  public boolean rowUpdated() throws SQLException {
    return resultSet.rowUpdated();
  }




  @Override
  public void setFetchDirection( final int direction ) throws SQLException {
    resultSet.setFetchDirection( direction );
  }




  @Override
  public void setFetchSize( final int rows ) throws SQLException {
    resultSet.setFetchSize( rows );
  }




  @Override
  public <T> T unwrap( final Class<T> iface ) throws SQLException {
    return resultSet.unwrap( iface );
  }




  @Override
  public void updateArray( final int columnIndex, final Array x ) throws SQLException {
    resultSet.updateArray( columnIndex, x );
  }




  @Override
  public void updateArray( final String columnLabel, final Array x ) throws SQLException {
    resultSet.updateArray( columnLabel, x );
  }




  @Override
  public void updateAsciiStream( final int columnIndex, final InputStream x ) throws SQLException {
    resultSet.updateAsciiStream( columnIndex, x );
  }




  @Override
  public void updateAsciiStream( final int columnIndex, final InputStream x, final int length ) throws SQLException {
    resultSet.updateAsciiStream( columnIndex, x, length );
  }




  @Override
  public void updateAsciiStream( final int columnIndex, final InputStream x, final long length ) throws SQLException {
    resultSet.updateAsciiStream( columnIndex, x, length );
  }




  @Override
  public void updateAsciiStream( final String columnLabel, final InputStream x ) throws SQLException {
    resultSet.updateAsciiStream( columnLabel, x );
  }




  @Override
  public void updateAsciiStream( final String columnLabel, final InputStream x, final int length ) throws SQLException {
    resultSet.updateAsciiStream( columnLabel, x, length );
  }




  @Override
  public void updateAsciiStream( final String columnLabel, final InputStream x, final long length ) throws SQLException {
    resultSet.updateAsciiStream( columnLabel, x, length );
  }




  @Override
  public void updateBigDecimal( final int columnIndex, final BigDecimal x ) throws SQLException {
    resultSet.updateBigDecimal( columnIndex, x );
  }




  @Override
  public void updateBigDecimal( final String columnLabel, final BigDecimal x ) throws SQLException {
    resultSet.updateBigDecimal( columnLabel, x );
  }




  @Override
  public void updateBinaryStream( final int columnIndex, final InputStream x ) throws SQLException {
    resultSet.updateBinaryStream( columnIndex, x );
  }




  @Override
  public void updateBinaryStream( final int columnIndex, final InputStream x, final int length ) throws SQLException {
    resultSet.updateBinaryStream( columnIndex, x, length );
  }




  @Override
  public void updateBinaryStream( final int columnIndex, final InputStream x, final long length ) throws SQLException {
    resultSet.updateBinaryStream( columnIndex, x, length );
  }




  @Override
  public void updateBinaryStream( final String columnLabel, final InputStream x ) throws SQLException {
    resultSet.updateBinaryStream( columnLabel, x );
  }




  @Override
  public void updateBinaryStream( final String columnLabel, final InputStream x, final int length ) throws SQLException {
    resultSet.updateBinaryStream( columnLabel, x, length );
  }




  @Override
  public void updateBinaryStream( final String columnLabel, final InputStream x, final long length ) throws SQLException {
    resultSet.updateBinaryStream( columnLabel, x, length );
  }




  @Override
  public void updateBlob( final int columnIndex, final Blob x ) throws SQLException {
    resultSet.updateBlob( columnIndex, x );
  }




  @Override
  public void updateBlob( final int columnIndex, final InputStream inputStream ) throws SQLException {
    resultSet.updateBlob( columnIndex, inputStream );
  }




  @Override
  public void updateBlob( final int columnIndex, final InputStream inputStream, final long length ) throws SQLException {
    resultSet.updateBlob( columnIndex, inputStream, length );
  }




  @Override
  public void updateBlob( final String columnLabel, final Blob x ) throws SQLException {
    resultSet.updateBlob( columnLabel, x );
  }




  @Override
  public void updateBlob( final String columnLabel, final InputStream inputStream ) throws SQLException {
    resultSet.updateBlob( columnLabel, inputStream );
  }




  @Override
  public void updateBlob( final String columnLabel, final InputStream inputStream, final long length ) throws SQLException {
    resultSet.updateBlob( columnLabel, inputStream, length );
  }




  @Override
  public void updateBoolean( final int columnIndex, final boolean x ) throws SQLException {
    resultSet.updateBoolean( columnIndex, x );
  }




  @Override
  public void updateBoolean( final String columnLabel, final boolean x ) throws SQLException {
    resultSet.updateBoolean( columnLabel, x );
  }




  @Override
  public void updateByte( final int columnIndex, final byte x ) throws SQLException {
    resultSet.updateByte( columnIndex, x );
  }




  @Override
  public void updateByte( final String columnLabel, final byte x ) throws SQLException {
    resultSet.updateByte( columnLabel, x );
  }




  @Override
  public void updateBytes( final int columnIndex, final byte[] x ) throws SQLException {
    resultSet.updateBytes( columnIndex, x );
  }




  @Override
  public void updateBytes( final String columnLabel, final byte[] x ) throws SQLException {
    resultSet.updateBytes( columnLabel, x );
  }




  @Override
  public void updateCharacterStream( final int columnIndex, final Reader x ) throws SQLException {
    resultSet.updateCharacterStream( columnIndex, x );
  }




  @Override
  public void updateCharacterStream( final int columnIndex, final Reader x, final int length ) throws SQLException {
    resultSet.updateCharacterStream( columnIndex, x, length );
  }




  @Override
  public void updateCharacterStream( final int columnIndex, final Reader x, final long length ) throws SQLException {
    resultSet.updateCharacterStream( columnIndex, x, length );
  }




  @Override
  public void updateCharacterStream( final String columnLabel, final Reader reader ) throws SQLException {
    resultSet.updateCharacterStream( columnLabel, reader );
  }




  @Override
  public void updateCharacterStream( final String columnLabel, final Reader reader, final int length ) throws SQLException {
    resultSet.updateCharacterStream( columnLabel, reader, length );
  }




  @Override
  public void updateCharacterStream( final String columnLabel, final Reader reader, final long length ) throws SQLException {
    resultSet.updateCharacterStream( columnLabel, reader, length );
  }




  @Override
  public void updateClob( final int columnIndex, final Clob x ) throws SQLException {
    resultSet.updateClob( columnIndex, x );
  }




  @Override
  public void updateClob( final int columnIndex, final Reader reader ) throws SQLException {
    resultSet.updateClob( columnIndex, reader );
  }




  @Override
  public void updateClob( final int columnIndex, final Reader reader, final long length ) throws SQLException {
    resultSet.updateClob( columnIndex, reader, length );
  }




  @Override
  public void updateClob( final String columnLabel, final Clob x ) throws SQLException {
    resultSet.updateClob( columnLabel, x );
  }




  @Override
  public void updateClob( final String columnLabel, final Reader reader ) throws SQLException {
    resultSet.updateClob( columnLabel, reader );
  }




  @Override
  public void updateClob( final String columnLabel, final Reader reader, final long length ) throws SQLException {
    resultSet.updateClob( columnLabel, reader, length );
  }




  @Override
  public void updateDate( final int columnIndex, final Date x ) throws SQLException {
    resultSet.updateDate( columnIndex, x );
  }




  @Override
  public void updateDate( final String columnLabel, final Date x ) throws SQLException {
    resultSet.updateDate( columnLabel, x );
  }




  @Override
  public void updateDouble( final int columnIndex, final double x ) throws SQLException {
    resultSet.updateDouble( columnIndex, x );
  }




  @Override
  public void updateDouble( final String columnLabel, final double x ) throws SQLException {
    resultSet.updateDouble( columnLabel, x );
  }




  @Override
  public void updateFloat( final int columnIndex, final float x ) throws SQLException {
    resultSet.updateFloat( columnIndex, x );
  }




  @Override
  public void updateFloat( final String columnLabel, final float x ) throws SQLException {
    resultSet.updateFloat( columnLabel, x );
  }




  @Override
  public void updateInt( final int columnIndex, final int x ) throws SQLException {
    resultSet.updateInt( columnIndex, x );
  }




  @Override
  public void updateInt( final String columnLabel, final int x ) throws SQLException {
    resultSet.updateInt( columnLabel, x );
  }




  @Override
  public void updateLong( final int columnIndex, final long x ) throws SQLException {
    resultSet.updateLong( columnIndex, x );
  }




  @Override
  public void updateLong( final String columnLabel, final long x ) throws SQLException {
    resultSet.updateLong( columnLabel, x );
  }




  @Override
  public void updateNCharacterStream( final int columnIndex, final Reader x ) throws SQLException {
    resultSet.updateNCharacterStream( columnIndex, x );
  }




  @Override
  public void updateNCharacterStream( final int columnIndex, final Reader x, final long length ) throws SQLException {
    resultSet.updateNCharacterStream( columnIndex, x, length );
  }




  @Override
  public void updateNCharacterStream( final String columnLabel, final Reader reader ) throws SQLException {
    resultSet.updateNCharacterStream( columnLabel, reader );
  }




  @Override
  public void updateNCharacterStream( final String columnLabel, final Reader reader, final long length ) throws SQLException {
    resultSet.updateNCharacterStream( columnLabel, reader, length );
  }




  @Override
  public void updateNClob( final int columnIndex, final NClob nClob ) throws SQLException {
    resultSet.updateNClob( columnIndex, nClob );
  }




  @Override
  public void updateNClob( final int columnIndex, final Reader reader ) throws SQLException {
    resultSet.updateNClob( columnIndex, reader );
  }




  @Override
  public void updateNClob( final int columnIndex, final Reader reader, final long length ) throws SQLException {
    resultSet.updateNClob( columnIndex, reader, length );
  }




  @Override
  public void updateNClob( final String columnLabel, final NClob nClob ) throws SQLException {
    resultSet.updateNClob( columnLabel, nClob );
  }




  @Override
  public void updateNClob( final String columnLabel, final Reader reader ) throws SQLException {
    resultSet.updateNClob( columnLabel, reader );
  }




  @Override
  public void updateNClob( final String columnLabel, final Reader reader, final long length ) throws SQLException {
    resultSet.updateNClob( columnLabel, reader, length );
  }




  @Override
  public void updateNString( final int columnIndex, final String nString ) throws SQLException {
    resultSet.updateNString( columnIndex, nString );
  }




  @Override
  public void updateNString( final String columnLabel, final String nString ) throws SQLException {
    resultSet.updateNString( columnLabel, nString );
  }




  @Override
  public void updateNull( final int columnIndex ) throws SQLException {
    resultSet.updateNull( columnIndex );
  }




  @Override
  public void updateNull( final String columnLabel ) throws SQLException {
    resultSet.updateNull( columnLabel );
  }




  @Override
  public void updateObject( final int columnIndex, final Object x ) throws SQLException {
    resultSet.updateObject( columnIndex, x );
  }




  @Override
  public void updateObject( final int columnIndex, final Object x, final int scaleOrLength ) throws SQLException {
    resultSet.updateObject( columnIndex, x, scaleOrLength );
  }




  @Override
  public void updateObject( final String columnLabel, final Object x ) throws SQLException {
    resultSet.updateObject( columnLabel, x );
  }




  @Override
  public void updateObject( final String columnLabel, final Object x, final int scaleOrLength ) throws SQLException {
    resultSet.updateObject( columnLabel, x, scaleOrLength );
  }




  @Override
  public void updateRef( final int columnIndex, final Ref x ) throws SQLException {
    resultSet.updateRef( columnIndex, x );
  }




  @Override
  public void updateRef( final String columnLabel, final Ref x ) throws SQLException {
    resultSet.updateRef( columnLabel, x );
  }




  @Override
  public void updateRow() throws SQLException {
    resultSet.updateRow();
  }




  @Override
  public void updateRowId( final int columnIndex, final RowId x ) throws SQLException {
    resultSet.updateRowId( columnIndex, x );
  }




  @Override
  public void updateRowId( final String columnLabel, final RowId x ) throws SQLException {
    resultSet.updateRowId( columnLabel, x );
  }




  @Override
  public void updateShort( final int columnIndex, final short x ) throws SQLException {
    resultSet.updateShort( columnIndex, x );
  }




  @Override
  public void updateShort( final String columnLabel, final short x ) throws SQLException {
    resultSet.updateShort( columnLabel, x );
  }




  @Override
  public void updateSQLXML( final int columnIndex, final SQLXML xmlObject ) throws SQLException {
    resultSet.updateSQLXML( columnIndex, xmlObject );
  }




  @Override
  public void updateSQLXML( final String columnLabel, final SQLXML xmlObject ) throws SQLException {
    resultSet.updateSQLXML( columnLabel, xmlObject );
  }




  @Override
  public void updateString( final int columnIndex, final String x ) throws SQLException {
    resultSet.updateString( columnIndex, x );
  }




  @Override
  public void updateString( final String columnLabel, final String x ) throws SQLException {
    resultSet.updateString( columnLabel, x );
  }




  @Override
  public void updateTime( final int columnIndex, final Time x ) throws SQLException {
    resultSet.updateTime( columnIndex, x );
  }




  @Override
  public void updateTime( final String columnLabel, final Time x ) throws SQLException {
    resultSet.updateTime( columnLabel, x );
  }




  @Override
  public void updateTimestamp( final int columnIndex, final Timestamp x ) throws SQLException {
    resultSet.updateTimestamp( columnIndex, x );
  }




  @Override
  public void updateTimestamp( final String columnLabel, final Timestamp x ) throws SQLException {
    resultSet.updateTimestamp( columnLabel, x );
  }




  @Override
  public boolean wasNull() throws SQLException {
    return resultSet.wasNull();
  }
}
