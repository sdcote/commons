package coyote.commons.jdbc;

import java.sql.SQLException;


/**
 * @param <E> the type of the entity object
 */
public interface DataMapper<E> {

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
  E map( ExtendedResultSet rs ) throws SQLException;
}
