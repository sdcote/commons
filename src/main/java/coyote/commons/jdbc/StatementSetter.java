package coyote.commons.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * An interface to an object which sets the arguments in a prepared statement.
 */
public interface StatementSetter {

  void setPreparedStatement( PreparedStatement ps ) throws SQLException;
}
