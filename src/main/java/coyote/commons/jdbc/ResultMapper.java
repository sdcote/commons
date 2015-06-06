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
   * The JdbcTemplate will pass the results of its processing to this interface 
   * for parsing of rows of returned data into domain objects.
   * 
   * <p>Some update and insert functions will use this method to create objects 
   * representing generated keys.  In these cases, domain objects are not 
   * technically created, but object encapsulating the keys.</p>
   *   
   * @param rs The result set proxy which contains extended methods to make working with the Java SQL {@code ResutSet} easier.
   * 
   * @return a domain object
   * 
   * @throws SQLException
   * 
   * @see coyote.commons.jdbc.TypedResultSet
   */
  public E map( TypedResultSet rs ) throws SQLException;
}
