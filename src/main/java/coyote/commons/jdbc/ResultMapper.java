package coyote.commons.jdbc;

import java.sql.SQLException;


/**
 * This interface defines a contract for an object which will map a 
 * {@code ResultSet} to an instance of <E>.
 * 
 * @param <E> the type of the entity object
 */
public interface ResultMapper<E> {

  /**
   *
   * @param rs
   * 
   * @return
   * 
   * @throws SQLException
   * 
   * @see coyote.commons.jdbc.ExtendedResultSet
   */
  public E map( ExtendedResultSet rs ) throws SQLException;
}
