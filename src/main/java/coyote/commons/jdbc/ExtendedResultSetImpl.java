package coyote.commons.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;


class ExtendedResultSetImpl extends ResultSetWrapper implements ExtendedResultSet {

  ExtendedResultSetImpl( final ResultSet resultSet ) {
    super( resultSet );
  }




  @Override
  public <E extends Enum<E>> E getEnum( final int columnIndex, final Class<E> enumClz ) throws SQLException {
    final String enumValue = getString( columnIndex );
    return ( enumValue == null ) ? null : Enum.valueOf( enumClz, enumValue );
  }




  @Override
  public <E extends Enum<E>> E getEnum( final String columnLabel, final Class<E> enumClz ) throws SQLException {
    final String enumValue = getString( columnLabel );
    return ( enumValue == null ) ? null : Enum.valueOf( enumClz, enumValue );
  }




  @Override
  public Boolean getNullableBoolean( final int columnIndex ) throws SQLException {
    final boolean value = getBoolean( columnIndex );
    return wasNull() ? null : value;
  }




  @Override
  public Boolean getNullableBoolean( final String columnLabel ) throws SQLException {
    final boolean value = getBoolean( columnLabel );
    return wasNull() ? null : value;
  }




  @Override
  public Byte getNullableByte( final int columnIndex ) throws SQLException {
    final byte value = getByte( columnIndex );
    return wasNull() ? null : value;
  }




  @Override
  public Byte getNullableByte( final String columnLabel ) throws SQLException {
    final byte value = getByte( columnLabel );
    return wasNull() ? null : value;
  }




  @Override
  public Double getNullableDouble( final int columnIndex ) throws SQLException {
    final double value = getDouble( columnIndex );
    return wasNull() ? null : value;
  }




  @Override
  public Double getNullableDouble( final String columnLabel ) throws SQLException {
    final double value = getDouble( columnLabel );
    return wasNull() ? null : value;
  }




  @Override
  public Float getNullableFloat( final int columnIndex ) throws SQLException {
    final float value = getFloat( columnIndex );
    return wasNull() ? null : value;
  }




  @Override
  public Float getNullableFloat( final String columnLabel ) throws SQLException {
    final float value = getFloat( columnLabel );
    return wasNull() ? null : value;
  }




  @Override
  public Integer getNullableInt( final int columnIndex ) throws SQLException {
    final int value = getInt( columnIndex );
    return wasNull() ? null : value;
  }




  @Override
  public Integer getNullableInt( final String columnLabel ) throws SQLException {
    final int value = getInt( columnLabel );
    return wasNull() ? null : value;
  }




  @Override
  public Long getNullableLong( final int columnIndex ) throws SQLException {
    final long value = getLong( columnIndex );
    return wasNull() ? null : value;
  }




  @Override
  public Long getNullableLong( final String columnLabel ) throws SQLException {
    final long value = getLong( columnLabel );
    return wasNull() ? null : value;
  }




  @Override
  public Short getNullableShort( final int columnIndex ) throws SQLException {
    final short value = getShort( columnIndex );
    return wasNull() ? null : value;
  }




  @Override
  public Short getNullableShort( final String columnLabel ) throws SQLException {
    final short value = getShort( columnLabel );
    return wasNull() ? null : value;
  }

}
