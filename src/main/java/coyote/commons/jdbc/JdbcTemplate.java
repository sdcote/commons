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
 * The JdbcTemplate simplifies the use of JDBC.
 * 
 * <p>All the complexities of retrieving an available connection from a data 
 * source, preparing a statement with placeholders, filling in the arguments, 
 * executing a query or an update task, and finally transforming the result set 
 * returned from the execution into an entity object or a list of entity objects
 * can be condensed down into just one method call.
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
    Assert.notNull( dataSource, "dataSource must not be null" );
    this.dataSource = dataSource;
  }




  /**
   * Returns a BatchUpdater object with the supplied SQL. 
   * 
   * <p>The placeholder is set in the BatchUpdater instance returned.
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
      throw new DataException( ex );
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
   * @throws DataException if SQLException is thrown during the close phase.
   */
  protected void closeConnection( final Connection connection ) throws DataException {
    try {
      connection.close();
    } catch ( final SQLException ex ) {
      throw new DataException( "failed to close the connection", ex );
    }
  }




  /**
   * A factory method of the connection from the data source.
   * 
   * <p>This returns a wrapper around a java SQL connection which will handle 
   * the closing of resources when it is closed.
   *  
   * @return a connection from the data source.
   */
  protected Connection getConnection() {
    try {
      final Connection connection = dataSource.getConnection();
      return new CloseOnCompletionConnection( connection );
    } catch ( final SQLException ex ) {
      throw new DataException( "failed to get a connection from the data source", ex );
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
   * @param parameters the parameter list to the SQL statement. the parameter will be filled into the SQL sequentially
   * @param dataMapper a dataMapper instance which acts as a mapper between the result set and the entity object
   * @param <E> the type of the entity class
   * 
   * @return a list of entity objects of type E produced by the query.
   * 
   * @throws DataException if any exception occurs during the data access of the database
   */
  public <E> List<E> query( final String sql, final List<?> parameters, final ResultMapper<E> dataMapper ) throws DataException {
    return query( sql, parameters, dataMapper, new LinkedList<E>() );
  }




  /**
   * Query for a list of entity of type E
   * 
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param parameters the parameter list to the SQL statement. the parameter will be filled into the SQL sequentially
   * @param dataMapper a dataMapper instance which acts as a mapper between the result set and the entity object
   * @param resultList the result list to be returned by this method. This is list must not be unmodifiable. Otherwise, an UnsupportedOperationException is likely to be thrown.
   * @param <E> the type of the entity class
   * 
   * @return the resultList which is passed in the parameter list. The resultList will contain the entity objects produced by the query.
   * 
   * @throws DataException if any exception occurs during the data access of the database
   */
  public <E> List<E> query( final String sql, final List<?> parameters, final ResultMapper<E> dataMapper, final List<E> resultList ) throws DataException {
    Assert.notNull( parameters, "parameters cannot be null" );

    final StatementSetter setter = new DefaultStatementSetter( parameters );
    return query( sql, setter, dataMapper, resultList );
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
   * @param setter a StatementSetter instance to fill the placeholders of the SQL
   * @param dataMapper a dataMapper instance which acts as a mapper between the result set and the entity object
   * @param <E> the type of the entity class
   * 
   * @return a list of entity objects of type E produced by the query.
   * @throws DataException if any exception occurs during the data access of the database
   */
  public <E> List<E> query( final String sql, final StatementSetter setter, final ResultMapper<E> dataMapper ) throws DataException {
    return query( sql, setter, dataMapper, new LinkedList<E>() );
  }




  /**
   * Query for a list of entity of type E.
   * 
   * <p>TheresultList will contain the entity objects produced by the query.
   * 
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param setter a StatementSetter instance to fill the placeholders of the SQL
   * @param dataMapper a dataMapper instance which acts as a mapper between the result set and the entity object
   * @param resultList the result list to be returned by this method. This is list must not be unmodifiable. Otherwise, an UnsupportedOperationException is likely to be thrown.
   * @param <E> the type of the entity class
   * 
   * @return the resultList which is passed in the parameter list. 
   * 
   * @throws DataException if any exception occurs during the data access of the database
   */
  public <E> List<E> query( final String sql, final StatementSetter setter, final ResultMapper<E> dataMapper, final List<E> resultList ) throws DataException {
    Assert.notNull( setter, "setter cannot be null" );
    Assert.notNull( dataMapper, "dataMapper cannot be null" );

    final Connection connection = getConnection();
    try {
      connection.setReadOnly( true );
      final PreparedStatement ps = connection.prepareStatement( sql );
      try {
        setter.setPreparedStatement( ps );

        final ResultSet rs = ps.executeQuery();
        try {
          while ( rs.next() ) {
            final E entity = dataMapper.map( new DefaultTypedResultSet( rs ) );
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
      throw new DataException( ex );
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
   * @param dataMapper a dataMapper instance which acts as a mapper between the result set and the entity object
   * @param <E> the type of the entity class
   * 
   * @return a list of entity objects of type E produced by the query.
   * 
   * @throws DataException if any exception occurs during the data access of the database
   */
  public <E> List<E> query( final String sql, final ResultMapper<E> dataMapper ) throws DataException {
    return query( sql, dataMapper, new LinkedList<E>() );
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
   * @param dataMapper a dataMapper instance which acts as a mapper between the result set and the entity object
   * @param <E> the type of the entity class
   * 
   * @return a list of entity objects of type E produced by the query.
   * 
   * @throws DataException if any exception occurs during the data access of the database
   */
  public <E> List<E> query( final String sql, final ResultMapper<E> dataMapper, final List<E> resultList ) throws DataException {
    Assert.notNull( dataMapper, "ResultMapper must not be null" );
    Assert.notNull( resultList, "result list must not be null" );

    final Connection connection = getConnection();
    try {
      connection.setReadOnly( true );
      final Statement stmt = connection.createStatement();
      try {
        final ResultSet rs = stmt.executeQuery( sql );
        try {
          while ( rs.next() ) {
            final E entity = dataMapper.map( new DefaultTypedResultSet( rs ) );
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
      throw new DataException( ex );
    }
    finally {
      closeConnection( connection );
    }
  }




  /**
   * Query only a single entity of type E. 
   * 
   * <p>If there is multiple results returned from the query, the first one 
   * will be returned. If there are no results, {@code NULL} will be 
   * returned.
   * 
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param parameters a list of parameters to fill the placeholders of the SQL
   * @param dataMapper a dataMapper instance which acts as a mapper between the result set and the entity object
   * @param <E> the type of the entity class
   * 
   * @return the single entity produced by the query. If no result return from the query, {@code NULL} will be returned.
   * 
   * @throws DataException  if any exception occurs during the data access of the database
   */
  public <E> E queryOne( final String sql, final List<?> parameters, final ResultMapper<E> dataMapper ) throws DataException {
    Assert.notNull( parameters, "parameters cannot be null" );

    final StatementSetter setter = new DefaultStatementSetter( parameters );
    return queryOne( sql, setter, dataMapper );
  }




  /**
   * Query only a single entity of type E. 
   * 
   * <p>If there is multiple results returned from the query, the first one 
   * will be returned. If there are no results, {@code NULL} will be 
   * returned.
   * 
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param setter a StatementSetter instance to fill the placeholders of the SQL
   * @param dataMapper a dataMapper instance which acts as a mapper between the result set and the entity object
   * @param <E> the type of the entity class
   * 
   * @return the single entity produced by the query. If no result return from the query, {@code NULL} will be returned.
   * 
   * @throws DataException  if any exception occurs during the data access of the database
   */
  public <E> E queryOne( final String sql, final StatementSetter setter, final ResultMapper<E> dataMapper ) throws DataException {
    Assert.notNull( setter, "setter cannot be null" );
    Assert.notNull( dataMapper, "dataMapper cannot be null" );

    final Connection connection = getConnection();
    try {
      connection.setReadOnly( true );

      final PreparedStatement ps = connection.prepareStatement( sql );
      try {
        setter.setPreparedStatement( ps );

        final ResultSet rs = ps.executeQuery();
        try {
          return ( rs.next() ) ? dataMapper.map( new DefaultTypedResultSet( rs ) ) : null;
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
      throw new DataException( ex );
    }
    finally {
      closeConnection( connection );
    }
  }




  /**
   * Query only a single entity of type E. 
   * 
   * <p>If there is multiple results returned from the query, the first one 
   * will be returned. If there are no results, {@code NULL} will be 
   * returned.
   * 
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param dataMapper a dataMapper instance which acts as a mapper between the result set and the entity object
   * @param <E> the type of the entity class
   * 
   * @return the single entity produced by the query. If no result return from the query, {@code NULL} will be returned.
   * 
   * @throws DataException  if any exception occurs during the data access of the database
   */
  public <E> E queryOne( final String sql, final ResultMapper<E> dataMapper ) throws DataException {
    Assert.notNull( dataMapper, "dataMapper must not be null" );

    final Connection connection = getConnection();
    try {
      connection.setReadOnly( true );
      final Statement stmt = connection.createStatement();
      try {
        final ResultSet rs = stmt.executeQuery( sql );

        try {
          return ( rs.next() ) ? dataMapper.map( new DefaultTypedResultSet( rs ) ) : null;
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
      throw new DataException( ex );
    }
    finally {
      closeConnection( connection );
    }
  }




  /**
   * This method is used to rollback a connection if SQLException is thrown 
   * during the execution.
   * 
   * <p>When the auto-commit is not disabled (by default), the {@code 
   * connection.rollback()} will not be executed.
   * 
   * @param connection the connection to rollback
   * 
   * @throws DataException if SQLException is thrown during the rollback
   */
  protected void rollbackConnection( final Connection connection ) throws DataException {
    try {
      if ( !connection.getAutoCommit() ) {
        connection.rollback();
      }
    } catch ( final SQLException ex ) {
      throw new DataException( "failed to rollback the connection", ex );
    }
  }




  /**
   * Execute the update with supplied parameters.
   * 
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param parameters the parameter list to the SQL statement. the parameter will be filled into the SQL sequentially
   * 
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing
   * 
   * @throws DataException  if any exception occurs during the data access of the database
   */
  public int update( final String sql, final List<?> parameters ) throws DataException {
    Assert.notNull( parameters, "parameters cannot be null" );

    final StatementSetter setter = new DefaultStatementSetter( parameters );
    return update( sql, setter );
  }




  /**
   * Execute the update with supplied parameters and return a list of generated 
   * keys to this update.
   * 
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param parameters the parameter list to the SQL statement. the parameter will be filled into the SQL sequentially
   * @param resultMapper the dataMapper instance which acts as a mapper between the result set and the generated key
   * @param <K> the type of the key
   * 
   * @return a list of generated keys to this update.
   * 
   * @throws DataException if any exception occurs during the data access of the database
   */
  public <K> List<K> update( final String sql, final List<?> parameters, final ResultMapper<K> resultMapper ) throws DataException {
    Assert.notNull( parameters, "parameters cannot be null" );
    Assert.notNull( resultMapper, "resultMapper cannot be null" );

    final StatementSetter setter = new DefaultStatementSetter( parameters );
    return update( sql, setter, resultMapper );
  }




  /**
   * Execute the update with supplied parameters.
   * 
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param setter a StatementSetter instance to fill the placeholders of the SQL
   * 
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing
   * 
   * @throws DataException if any exception occurs during the data access of the database
   */
  public int update( final String sql, final StatementSetter setter ) throws DataException {
    Assert.notNull( setter, "setter cannot be null" );

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
      throw new DataException( ex );
    }
    finally {
      closeConnection( connection );
    }
  }




  /**
   * Execute the update with supplied parameters and return only a single key
   * object to this update.
   * 
   * <p>Not all drivers return keys after an update. Successful updates may 
   * still return null depending on the driver being used.
   *  
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param setter a StatementSetter instance to fill the placeholders of the SQL
   * @param resultMapper the dataMapper instance which acts as a mapper between the result set and the generated key
   * @param <K> the type of the key
   * 
   * @return a list of generated keys to this update.
   * 
   * @throws DataException if any exception occurs during the data access of the database
   */
  public <K> List<K> update( final String sql, final StatementSetter setter, final ResultMapper<K> resultMapper ) throws DataException {
    Assert.notNull( setter, "setter cannot be null" );
    Assert.notNull( resultMapper, "resultMapper cannot be null" );

    final Connection connection = getConnection();
    try {
      connection.setReadOnly( false );

      final PreparedStatement ps = connection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
      try {
        setter.setPreparedStatement( ps );

        final int count = ps.executeUpdate();
        final List<K> keyList = new ArrayList<K>( count );

        final ResultSet rs = ps.getGeneratedKeys();
        while ( rs.next() ) {
          final K key = resultMapper.map( new DefaultTypedResultSet( rs ) );
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
      throw new DataException( ex );
    }
    finally {
      closeConnection( connection );
    }
  }




  /**
   * Execute the update with supplied parameters and return only a single key
   * object to this update.
   * 
   * <p>Not all drivers return keys after an update. Successful updates may 
   * still return null depending on the driver being used.
   *  
   * <p>If there are multiple keys returned after the update, the first key 
   * will be returned. And if there is no key returned, {@code NULL} will be 
   * returned.
   * 
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param parameters the parameter list to the SQL statement. the parameter will be filled into the SQL sequentially
   * @param resultMapper the dataMapper instance which acts as a mapper between the result set and the generated key
   * @param <K> the type of the key
   * 
   * @return only a single key object to this update.
   * 
   * @throws DataException if any exception occurs during the data access of the database
   */
  public <K> K updateOne( final String sql, final List<?> parameters, final ResultMapper<K> resultMapper ) throws DataException {
    Assert.notNull( parameters, "parameters cannot be null" );
    Assert.notNull( resultMapper, "ResultMapper cannot be null" );

    final StatementSetter setter = new DefaultStatementSetter( parameters );
    return updateOne( sql, setter, resultMapper );
  }




  /**
   * Execute the update with supplied parameters and return only a single key
   * object to this update.
   * 
   * <p>Not all drivers return keys after an update. Successful updates may 
   * still return null depending on the driver being used.
   *  
   * <p>If there are multiple keys returned after the update, the first key 
   * will be returned. And if there is no key returned, {@code NULL} will be 
   * returned.
   * 
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param setter a StatementSetter instance to fill the placeholders of the SQL
   * @param resultMapper the dataMapper instance which acts as a mapper between the result set and the generated key
   * @param <K> the type of the key
   * 
   * @return only a single key object to this update.
   * 
   * @throws DataException if any exception occurs during the data access of the database
   */
  public <K> K updateOne( final String sql, final StatementSetter setter, final ResultMapper<K> resultMapper ) throws DataException {
    Assert.notNull( setter, "setter cannot be null" );
    Assert.notNull( resultMapper, "resultMapper cannot be null" );

    final Connection connection = getConnection();
    try {
      connection.setReadOnly( false );

      final PreparedStatement ps = connection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
      try {
        setter.setPreparedStatement( ps );

        ps.executeUpdate();

        final ResultSet rs = ps.getGeneratedKeys();
        final K result = ( rs.next() ) ? resultMapper.map( new DefaultTypedResultSet( rs ) ) : null;
        rs.close();
        return result;
      }
      finally {
        ps.close();
      }
    } catch ( final SQLException ex ) {
      rollbackConnection( connection );
      throw new DataException( ex );
    }
    finally {
      closeConnection( connection );
    }
  }
}
