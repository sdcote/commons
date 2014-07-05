package coyote.commons.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import coyote.commons.Assert;
import coyote.commons.jdbc.datasource.CloseOnCompletionConnection;


/**
 * The JdbcTemplate simplifies the use of the native JDBC (retrieving
 * an available connection from a data source, preparing a statement with
 * placeholders, filling in the missing arguments, executing a query or an
 * update task, and finally transforming the result set returned from the
 * execution into a entity object or a list of entity objects) into just one
 * single method invocation including <code>query(...)</code>,
 * <code>queryOne(...)</code>, <code>update(...)</code> and
 * <code>updateOne(...)</code>.
 */
public class JdbcTemplate {

  private final DataSource dataSource;




  /**
   * Construct a JdbcTemplate instance from an existing dataSource object
   * 
   * @param dataSource a data source object, not null
   * 
   * @throws java.lang.IllegalArgumentException dataSource is null
   */
  public JdbcTemplate( final DataSource dataSource ) {
    Assert.argumentIsNotNull( dataSource, "dataSource must not be null" );
    this.dataSource = dataSource;
  }




  /**
   * Returns a BatchUpdater object with the supplied SQL. 
   * 
   * <p>The placeholder is set in the BatchUpdater instance returned.</p>
   * 
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * 
   * @return a BatchUpdater object that defines the batch update to be executed
   * 
   * @see coyote.commons.jdbc.BatchUpdater
   */
  public BatchUpdater batchUpdater( final String sql ) {
    final Connection connection = getConnection();
    try {
      connection.setReadOnly( false );

      final PreparedStatement ps = connection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
      return new BatchUpdater( ps );
    } catch ( final SQLException ex ) {
      rollbackConnection( connection );
      throw new DataAccessException( ex );
    }
    finally {
      closeConnection( connection );
    }
  }




  /**
   * This method is used to close a connection as soon as the task has either 
   * been successfully executed or failed with an exception.
   * 
   * @param connection the connection to be closed
   * 
   * @throws DataAccessException if SQLException is thrown during the close phase.
   */
  protected void closeConnection( final Connection connection ) throws DataAccessException {
    try {
      connection.close();
    } catch ( final SQLException ex ) {
      throw new DataAccessException( "failed to close the connection", ex );
    }
  }




  /**
   * A factory method of the connection from the data source.
   * 
   * <p>This returns a wrapper around a java SQL connection which will handle 
   * the closing of resources when it is closed.</p>
   *  
   * @return a connection from the data source.
   */
  protected Connection getConnection() {
    try {
      final Connection connection = dataSource.getConnection();
      return new CloseOnCompletionConnection( connection );
    } catch ( final SQLException ex ) {
      throw new DataAccessException( "failed to get a connection from the data source", ex );
    }
  }




  public <E> List<E> query( final String sql, final ResultMapper<E> dataMapper ) throws DataAccessException {
    return query( sql, dataMapper, new LinkedList<E>() );
  }




  public <E> List<E> query( final String sql, final ResultMapper<E> dataMapper, final List<E> resultList ) throws DataAccessException {
    Assert.argumentIsNotNull( dataMapper, "dataMapper must not be null" );
    Assert.argumentIsNotNull( resultList, "resultList must not be null" );

    final Connection connection = getConnection();
    try {
      connection.setReadOnly( true );
      final Statement stmt = connection.createStatement();
      try {
        final ResultSet rs = stmt.executeQuery( sql );
        try {
          while ( rs.next() ) {
            final E entity = dataMapper.map( new ExtendedResultSetImpl( rs ) );
            resultList.add( entity );
          }
        }
        finally {
          rs.close();
        }
      }
      finally {
        stmt.close();
      }

      return resultList;
    } catch ( final SQLException ex ) {
      rollbackConnection( connection );
      throw new DataAccessException( ex );
    }
    finally {
      closeConnection( connection );
    }
  }




  /**
   * Query for a list of entity of type E
   * 
   * <p>By default, the type of the returned list will be
   * {@link java.util.LinkedList}. To specify the desired instance to be 
   * returned, use 
   * {@link #query(String, java.util.List, ResultMapper, java.util.List)} 
   * instead.
   * 
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param parameters the parameter list to the SQL statement. the parameter will be filled into the sql sequentially
   * @param dataMapper a dataMapper instance which acts as a mapper between the result set and the entity object
   * @param <E> the type of the entity class
   * 
   * @return a list of entity objects of type E produced by the query.
   * 
   * @throws DataAccessException if any exception occurs during the data access of the database
   */
  public <E> List<E> query( final String sql, final List<?> parameters, final ResultMapper<E> dataMapper ) throws DataAccessException {
    return query( sql, parameters, dataMapper, new LinkedList<E>() );
  }




  /**
   * Query for a list of entity of type E
   * 
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param parameters the parameter list to the SQL statement. the parameter will be filled into the sql sequentially
   * @param dataMapper a dataMapper instance which acts as a mapper between the result set and the entity object
   * @param resultList the result list to be returned by this method. This is list must not be unmodifiable. Otherwise, an UnsupportedOperationException is likely to be thrown.
   * @param <E> the type of the entity class
   * 
   * @return the resultList which is passed in the parameter list. The resultList will contain the entity objects produced by the query.
   * 
   * @throws DataAccessException if any exception occurs during the data access of the database
   */
  public <E> List<E> query( final String sql, final List<?> parameters, final ResultMapper<E> dataMapper, final List<E> resultList ) throws DataAccessException {
    Assert.argumentIsNotNull( parameters, "parameters cannot be null" );

    final PreparedStatementSetter setter = new SimplePreparedStatementSetter( parameters );
    return query( sql, setter, dataMapper, resultList );
  }




  /**
   * Query for a list of entity of type E
   * @param sql an SQL statement that may contain one or more '?' IN
   *            parameter placeholders
   * @param setter a PreparedStatementSetter instance to fill the
   *               placeholders of the sql
   * @param dataMapper a dataMapper instance which acts as a mapper between
   *                   the result set and the entity object
   * @param <E> the type of the entity class
   * @return a list of entity objects of type E produced by the query.
   *         By default, the type of the returned list will be
   *         {@link java.util.LinkedList}. To specify the desired instance
   *         to be returned, use {@link #query(String, java.util.List,
   *         ResultMapper, java.util.List)} instead.
   * @throws DataAccessException if any exception occurs during the data
   *                             access of the database
   */
  public <E> List<E> query( final String sql, final PreparedStatementSetter setter, final ResultMapper<E> dataMapper ) throws DataAccessException {
    return query( sql, setter, dataMapper, new LinkedList<E>() );
  }




  /**
   * query for a list of entity of type E
   * @param sql an SQL statement that may contain one or more '?' IN
   *            parameter placeholders
   * @param setter a PreparedStatementSetter instance to fill the
   *               placeholders of the sql
   * @param dataMapper a dataMapper instance which acts as a mapper between
   *                   the result set and the entity object
   * @param resultList the result list to be returned by this method. This is
   *                   list must not be unmodifiable. Otherwise, an
   *                   UnsupportedOperationException is likely to be thrown.
   * @param <E> the type of the entity class
   * @return the resultList which is passed in the parameter list. The
   *         resultList will contain the entity objects produced by the
   *         query.
   * @throws DataAccessException if any exception occurs during the data
   *                             access of the database
   */
  public <E> List<E> query( final String sql, final PreparedStatementSetter setter, final ResultMapper<E> dataMapper, final List<E> resultList ) throws DataAccessException {
    Assert.argumentIsNotNull( setter, "setter cannot be null" );
    Assert.argumentIsNotNull( dataMapper, "dataMapper cannot be null" );

    final Connection connection = getConnection();
    try {
      connection.setReadOnly( true );
      final PreparedStatement ps = connection.prepareStatement( sql );
      try {
        setter.setPreparedStatement( ps );

        final ResultSet rs = ps.executeQuery();
        try {
          while ( rs.next() ) {
            final E entity = dataMapper.map( new ExtendedResultSetImpl( rs ) );
            resultList.add( entity );
          }
        }
        finally {
          rs.close();
        }
      }
      finally {
        ps.close();
      }

      return resultList;
    } catch ( final SQLException ex ) {
      rollbackConnection( connection );
      throw new DataAccessException( ex );
    }
    finally {
      closeConnection( connection );
    }
  }




  public <E> E queryOne( final String sql, final ResultMapper<E> dataMapper ) throws DataAccessException {
    Assert.argumentIsNotNull( dataMapper, "dataMapper must not be null" );

    final Connection connection = getConnection();
    try {
      connection.setReadOnly( true );
      final Statement stmt = connection.createStatement();
      try {
        final ResultSet rs = stmt.executeQuery( sql );

        try {
          return ( rs.next() ) ? dataMapper.map( new ExtendedResultSetImpl( rs ) ) : null;
        }
        finally {
          rs.close();
        }
      }
      finally {
        stmt.close();
      }
    } catch ( final SQLException ex ) {
      rollbackConnection( connection );
      throw new DataAccessException( ex );
    }
    finally {
      closeConnection( connection );
    }
  }




  /**
   * query only a single entity of type E. If there is multiple results
   * returned from the query, the first one will be returned. If there is no
   * result, <code>NULL</code> will be returned.
   * @param sql an SQL statement that may contain one or more '?' IN
   *            parameter placeholders
   * @param dataMapper a dataMapper instance which acts as a mapper between
   *                   the result set and the entity object
   * @param <E> the type of the entity class
   * @return the single entity produced by the query. If no result return
   *         from the query, <code>NULL</code> will be returned.
   * @throws DataAccessException  if any exception occurs during the data
   *                             access of the database
   */
  public <E> E queryOne( final String sql, final List<?> parameters, final ResultMapper<E> dataMapper ) throws DataAccessException {
    Assert.argumentIsNotNull( parameters, "parameters cannot be null" );

    final PreparedStatementSetter setter = new SimplePreparedStatementSetter( parameters );
    return queryOne( sql, setter, dataMapper );
  }




  /**
   * query only a single entity of type E. If there is multiple results
   * returned from the query, the first one will be returned. If there is no
   * result, <code>NULL</code> will be returned.
   * @param sql an SQL statement that may contain one or more '?' IN
   *            parameter placeholders
   * @param setter a PreparedStatementSetter instance to fill the
   *               placeholders of the sql
   * @param dataMapper a dataMapper instance which acts as a mapper between
   *                   the result set and the entity object
   * @param <E> the type of the entity class
   * @return the single entity produced by the query. If no result return
   *         from the query, <code>NULL</code> will be returned.
   * @throws DataAccessException  if any exception occurs during the data
   *                             access of the database
   */
  public <E> E queryOne( final String sql, final PreparedStatementSetter setter, final ResultMapper<E> dataMapper ) throws DataAccessException {
    Assert.argumentIsNotNull( setter, "setter cannot be null" );
    Assert.argumentIsNotNull( dataMapper, "dataMapper cannot be null" );

    final Connection connection = getConnection();
    try {
      connection.setReadOnly( true );

      final PreparedStatement ps = connection.prepareStatement( sql );
      try {
        setter.setPreparedStatement( ps );

        final ResultSet rs = ps.executeQuery();
        try {
          return ( rs.next() ) ? dataMapper.map( new ExtendedResultSetImpl( rs ) ) : null;
        }
        finally {
          rs.close();
        }
      }
      finally {
        ps.close();
      }
    } catch ( final SQLException ex ) {
      rollbackConnection( connection );
      throw new DataAccessException( ex );
    }
    finally {
      closeConnection( connection );
    }
  }




  /**
   * this method is used to rollback a connection if SQLException is thrown
   * during the execution. But when the auto-commit is not disabled
   * (by default), the <code>connection.rollback()</code> will not be
   * executed.
   * @param connection the connection to rollback
   * @throws DataAccessException if SQLException is thrown during the
   * rollback
   */
  protected void rollbackConnection( final Connection connection ) throws DataAccessException {
    try {
      if ( !connection.getAutoCommit() ) {
        connection.rollback();
      }
    } catch ( final SQLException ex ) {
      throw new DataAccessException( "failed to rollback the connection", ex );
    }
  }




  /**
   * execute the update with supplied parameters.
   * @param sql an SQL statement that may contain one or more '?' IN
   *            parameter placeholders
   * @param parameters the parameter list to the SQL statement.
   *                   the parameter will be filled into the sql sequentially
   * @return either (1) the row count for SQL Data Manipulation Language
   *         (DML) statements or (2) 0 for SQL statements that return nothing
   * @throws DataAccessException  if any exception occurs during the data
   *                             access of the database
   */
  public int update( final String sql, final List<?> parameters ) throws DataAccessException {
    Assert.argumentIsNotNull( parameters, "parameters cannot be null" );

    final PreparedStatementSetter setter = new SimplePreparedStatementSetter( parameters );
    return update( sql, setter );
  }




  /**
   * execute the update with supplied parameters and return a list of
   * generated keys to this update.
   * @param sql an SQL statement that may contain one or more '?' IN
   *            parameter placeholders
   * @param parameters the parameter list to the SQL statement.
   *                   the parameter will be filled into the sql sequentially
   * @param keyMapper the dataMapper instance which acts as a mapper between
   *                  the result set and the generated key
   * @param <K> the type of the key
   * @return a list of generated keys to this update.
   * @throws DataAccessException if any exception occurs during the data
   *                             access of the database
   */
  public <K> List<K> update( final String sql, final List<?> parameters, final ResultMapper<K> keyMapper ) throws DataAccessException {
    Assert.argumentIsNotNull( parameters, "parameters cannot be null" );
    Assert.argumentIsNotNull( keyMapper, "keyMapper cannot be null" );

    final PreparedStatementSetter setter = new SimplePreparedStatementSetter( parameters );
    return update( sql, setter, keyMapper );
  }




  /**
   * execute the update with supplied parameters.
   * @param sql an SQL statement that may contain one or more '?' IN
   *            parameter placeholders
   * @param setter a PreparedStatementSetter instance to fill the
   *               placeholders of the sql
   * @return either (1) the row count for SQL Data Manipulation Language
   *         (DML) statements or (2) 0 for SQL statements that return nothing
   * @throws DataAccessException if any exception occurs during the data
   *                             access of the database
   */
  public int update( final String sql, final PreparedStatementSetter setter ) throws DataAccessException {
    Assert.argumentIsNotNull( setter, "setter cannot be null" );

    final Connection connection = getConnection();
    try {
      connection.setReadOnly( false );

      final PreparedStatement ps = connection.prepareStatement( sql );
      try {
        setter.setPreparedStatement( ps );

        return ps.executeUpdate();
      }
      finally {
        ps.close();
      }
    } catch ( final SQLException ex ) {
      rollbackConnection( connection );
      throw new DataAccessException( ex );
    }
    finally {
      closeConnection( connection );
    }
  }




  /**
   * execute the update with supplied parameters and return a list of
   * generated keys to this update.
   * @param sql an SQL statement that may contain one or more '?' IN
   *            parameter placeholders
   * @param setter a PreparedStatementSetter instance to fill the
   *               placeholders of the sql
   * @param keyMapper the dataMapper instance which acts as a mapper between
   *                  the result set and the generated key
   * @param <K> the type of the key
   * @return a list of generated keys to this update.
   * @throws DataAccessException if any exception occurs during the data
   *                             access of the database
   */
  public <K> List<K> update( final String sql, final PreparedStatementSetter setter, final ResultMapper<K> keyMapper ) throws DataAccessException {
    Assert.argumentIsNotNull( setter, "setter cannot be null" );
    Assert.argumentIsNotNull( keyMapper, "keyMapper cannot be null" );

    final Connection connection = getConnection();
    try {
      connection.setReadOnly( false );

      final PreparedStatement ps = connection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
      try {
        setter.setPreparedStatement( ps );

        final int row = ps.executeUpdate();
        final List<K> keyList = new ArrayList<K>( row );

        final ResultSet rs = ps.getGeneratedKeys();
        while ( rs.next() ) {
          final K key = keyMapper.map( new ExtendedResultSetImpl( rs ) );
          keyList.add( key );
        }
        rs.close();
        return keyList;
      }
      finally {
        ps.close();
      }
    } catch ( final SQLException ex ) {
      rollbackConnection( connection );
      throw new DataAccessException( ex );
    }
    finally {
      closeConnection( connection );
    }
  }




  /**
   * execute the update with supplied parameters and return only a single key
   * object to this update. If there are multiple keys returned after the
   * update, the first key will be returned. And if there is no key returned,
   * <code>NULL</code> will be returned.
   * @param sql an SQL statement that may contain one or more '?' IN
   *            parameter placeholders
   * @param parameters the parameter list to the SQL statement.
   *                   the parameter will be filled into the sql sequentially
   * @param keyMapper the dataMapper instance which acts as a mapper between
   *                  the result set and the generated key
   * @param <K> the type of the key
   * @return only a single key object to this update. If there are multiple
   * keys returned after the update, the first key will be returned. And if
   * there is no key returned, <code>NULL</code> will be returned.
   * @throws DataAccessException if any exception occurs during the data
   *                             access of the database
   */
  public <K> K updateOne( final String sql, final List<?> parameters, final ResultMapper<K> keyMapper ) throws DataAccessException {
    Assert.argumentIsNotNull( parameters, "parameters cannot be null" );
    Assert.argumentIsNotNull( keyMapper, "keyMapper cannot be null" );

    final PreparedStatementSetter setter = new SimplePreparedStatementSetter( parameters );
    return updateOne( sql, setter, keyMapper );
  }




  /**
   * execute the update with supplied parameters and return only a single key
   * object to this update. If there are multiple keys returned after the
   * update, the first key will be returned. And if there is no key returned,
   * <code>NULL</code> will be returned.
   * @param sql an SQL statement that may contain one or more '?' IN
   *            parameter placeholders
   * @param setter a PreparedStatementSetter instance to fill the
   *               placeholders of the sql
   * @param keyMapper the dataMapper instance which acts as a mapper between
   *                  the result set and the generated key
   * @param <K> the type of the key
   * @return only a single key object to this update. If there are multiple
   * keys returned after the update, the first key will be returned. And if
   * there is no key returned, <code>NULL</code> will be returned.
   * @throws DataAccessException if any exception occurs during the data
   *                             access of the database
   */
  public <K> K updateOne( final String sql, final PreparedStatementSetter setter, final ResultMapper<K> keyMapper ) throws DataAccessException {
    Assert.argumentIsNotNull( setter, "setter cannot be null" );
    Assert.argumentIsNotNull( keyMapper, "keyMapper cannot be null" );

    final Connection connection = getConnection();
    try {
      connection.setReadOnly( false );

      final PreparedStatement ps = connection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
      try {
        setter.setPreparedStatement( ps );

        ps.executeUpdate();

        final ResultSet rs = ps.getGeneratedKeys();
        final K result = ( rs.next() ) ? keyMapper.map( new ExtendedResultSetImpl( rs ) ) : null;
        rs.close();
        return result;
      }
      finally {
        ps.close();
      }
    } catch ( final SQLException ex ) {
      rollbackConnection( connection );
      throw new DataAccessException( ex );
    }
    finally {
      closeConnection( connection );
    }
  }
}
