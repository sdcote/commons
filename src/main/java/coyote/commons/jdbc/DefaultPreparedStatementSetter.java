package coyote.commons.jdbc;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;


/**
 * A class which uses a list of objects to use as arguments in 
 * {@code PreparedStatements}
 */
public class DefaultPreparedStatementSetter implements PreparedStatementSetter {

  private final List<?> argList;




  /**
   * Construct the setter with the given list of objects.
   * 
   * @param argList The list of objects to use when setting prepared statements.
   */
  public DefaultPreparedStatementSetter( final List<?> argList ) {
    this.argList = argList;
  }




  /**
   * Set the given prepared statement with the the list of arguments in this instance.
   */
  @Override
  public void setPreparedStatement( final PreparedStatement ps ) throws SQLException {
    int argIndex = 0;
    for ( final Object arg : argList ) {
      ++argIndex;

      if ( arg == null ) {
        ps.setNull( argIndex, Types.NULL );
      } else if ( arg instanceof Boolean ) {
        ps.setBoolean( argIndex, (Boolean)arg );
      } else if ( arg instanceof Byte ) {
        ps.setByte( argIndex, (Byte)arg );
      } else if ( arg instanceof Short ) {
        ps.setShort( argIndex, (Short)arg );
      } else if ( arg instanceof Integer ) {
        ps.setInt( argIndex, (Integer)arg );
      } else if ( arg instanceof Long ) {
        ps.setLong( argIndex, (Long)arg );
      } else if ( arg instanceof Float ) {
        ps.setFloat( argIndex, (Float)arg );
      } else if ( arg instanceof Double ) {
        ps.setDouble( argIndex, (Double)arg );
      } else if ( arg instanceof BigDecimal ) {
        ps.setBigDecimal( argIndex, (BigDecimal)arg );
      } else if ( arg instanceof String ) {
        ps.setString( argIndex, (String)arg );
      } else if ( arg instanceof byte[] ) {
        ps.setBytes( argIndex, (byte[])arg );
      } else if ( arg instanceof Date ) {
        ps.setDate( argIndex, (Date)arg );
      } else if ( arg instanceof Time ) {
        ps.setTime( argIndex, (Time)arg );
      } else if ( arg instanceof Timestamp ) {
        ps.setTimestamp( argIndex, (Timestamp)arg );
      } else {
        ps.setObject( argIndex, arg );
      }
    }
  }
}
