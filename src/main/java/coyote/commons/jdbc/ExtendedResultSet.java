package coyote.commons.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * A result set interface which extends the native JDBC {@code ResultSet},
 * adding the boxed type (including Boolean, Byte, Short, Integer, Long, Float
 * and Double) retrieve methods, from which the result will be {@code null} if 
 * the value from the database is SQL NULL.
 */
public interface ExtendedResultSet extends ResultSet {

  /**
   * Retrieves the value of the designated column in the current row of this 
   * {@code ResultSet} object as a {@code Enum}.
   * 
   * @param columnIndex Number of the column to retrieve; the first is 1
   * @param enumClass the class of the enumeration
   * @param <E> the type of the enumeration
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  <E extends Enum<E>> E getEnum( int columnIndex, Class<E> enumClass ) throws SQLException;




  /**
   * Retrieves the value of the designated column in the current row of this 
   * {@code ResultSet} object as a {@code Enum}.
   * 
   * @param columnName the name of the column specified with the SQL AS clause. If not specified, then the label is the name of the column
   * @param enumClass the class of the enumeration
   * @param <E> the type of the enumeration
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  <E extends Enum<E>> E getEnum( String columnName, Class<E> enumClass ) throws SQLException;




  /**
   * Retrieves the value of the designated column in the current row of this 
   * {@code ResultSet} object as a {@code Boolean}
   * .
   * @param columnIndex Number of the column to retrieve; the first is 1
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  Boolean getNullableBoolean( int columnIndex ) throws SQLException;




  /**
   * Retrieves the value of the designated column in the current row of this 
   * {@code ResultSet} as a {@code Boolean}
   * 
   * @param columnName the name of the column specified with the SQL AS clause. If not specified, then the label is the name of the column
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  Boolean getNullableBoolean( String columnName ) throws SQLException;




  /**
   * Retrieves the value of the designated column in the current row of this 
   * {@code ResultSet} as a {@code Byte}.
   * 
   * @param columnIndex Number of the column to retrieve; the first is 1
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  Byte getNullableByte( int columnIndex ) throws SQLException;




  /**
   * Retrieves the value of the designated column in the current row of this 
   * {@code ResultSet} as a {@code Byte}.
   * 
   * @param columnName the name of the column specified with the SQL AS clause. If not specified, then the label is the name of the column
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  Byte getNullableByte( String columnName ) throws SQLException;




  /**
   * Retrieves the value of the designated column in the current row of this 
   * {@code ResultSet} as a {@code Double}.
   * 
   * @param columnIndex Number of the column to retrieve; the first is 1
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  Double getNullableDouble( int columnIndex ) throws SQLException;




  /**
   * Retrieves the value of the designated column in the current row of this 
   * {@code ResultSet} as a {@code Double}.
   * 
   * @param columnName the name of the column specified with the SQL AS clause. If not specified, then the label is the name of the column
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  Double getNullableDouble( String columnName ) throws SQLException;




  /**
   * Retrieves the value of the designated column in the current row of this 
   * {@code ResultSet} as a {@code Float}.
   * 
   * @param columnIndex Number of the column to retrieve; the first is 1
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  Float getNullableFloat( int columnIndex ) throws SQLException;




  /**
   * Retrieves the value of the designated column in the current row of this 
   * {@code Float} as a {@code Long}.
   * 
   * @param columnName the name of the column specified with the SQL AS clause. If not specified, then the label is the name of the column
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  Float getNullableFloat( String columnName ) throws SQLException;




  /**
   * Retrieves the value of the designated column in the current row of this 
   * {@code ResultSet} as a {@code Integer}.
   * 
   * @param columnIndex Number of the column to retrieve; the first is 1
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  Integer getNullableInt( int columnIndex ) throws SQLException;




  /**
   * Retrieves the value of the designated column in the current row of this 
   * {@code ResultSet} as a {@code Integer}.
   * 
   * @param columnName the name of the column specified with the SQL AS clause. If not specified, then the label is the name of the column
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  Integer getNullableInt( String columnName ) throws SQLException;




  /**
   * Retrieves the value of the designated column in the current row of this 
   * {@code ResultSet} as a {@code Long}.
   * 
   * @param columnIndex Number of the column to retrieve; the first is 1
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  Long getNullableLong( int columnIndex ) throws SQLException;




  /**
   * Retrieves the value of the designated column in the current row of this 
   * {@code ResultSet} as a {@code Long}.
   * 
   * @param columnName the name of the column specified with the SQL AS clause. If not specified, then the label is the name of the column
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  Long getNullableLong( String columnName ) throws SQLException;




  /**
   * Retrieves the value of the designated column in the current row of this 
   * {@code ResultSet} as a {@code Short}.
   * 
   * @param columnIndex Number of the column to retrieve; the first is 1
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  Short getNullableShort( int columnIndex ) throws SQLException;




  /**
   * Retrieves the value of the designated column in the current row* of this 
   * {@code ResultSet} object as a {@code Short}.
   * 
   * @param columnName the name of the column specified with the SQL AS clause. If not specified, then the label is the name of the column
   * 
   * @return the column value; if the value is SQL {@code NULL}, the value returned is {@code null}.
   * 
   * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set.
   */
  Short getNullableShort( String columnName ) throws SQLException;
}
