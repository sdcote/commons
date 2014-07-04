package coyote.commons.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sql.DataSource;

import coyote.commons.Assert;


/**
 * A blocking queued connection pool implementation of connection pool.
 * All the connection in the pool will be served FIFO.
 * The connection is always retrieved from the pool. If there is no
 * connection available, it will be blocked until a connection is returned to
 * the pool instead of retrieving a new one from the data source.
 */
public class BlockingQueuedConnectionPool extends AbstractConnectionPool {

  private final BlockingQueue<Connection> idleConnections;




  /**
   * Construct a blocking queued connection pool according the parameters,
   * including the data source where the connection is retrieved,
   * the initial and max size of the pool and the default connection state
   * after a connection is returned to the pool
   * @param dataSource all the connection in the pool will be retrieved from
   *                   this data source
   * @param size the initial and max of the pool
   * @param defaultAutoCommit the default autoCommit of a connection from
   *                          the pool
   * @param defaultTransactionIsolation the default transaction isolation
   *                                    level of a connection from the pool
   * @param defaultReadOnly the default read only of a connection from the
   *                        pool
   * @param defaultCategory the default category of a connection from the
   *                        pool
   * @throws SQLException when connections cannot be retrieved from the
   *                      data source
   */
  public BlockingQueuedConnectionPool( final DataSource dataSource, final int size, final boolean defaultAutoCommit, final int defaultTransactionIsolation, final boolean defaultReadOnly, final String defaultCategory ) throws SQLException {
    super( defaultAutoCommit, defaultTransactionIsolation, defaultReadOnly, defaultCategory );
    Assert.argumentIsNotNull( dataSource, "dataSource must not be null" );
    Assert.argumentIsValid( size >= 0, "size must not be negative" );

    idleConnections = new LinkedBlockingQueue<Connection>( size );

    for ( int i = 0; i < size; ++i ) {
      idleConnections.add( dataSource.getConnection() );
    }
  }




  /**
   * Always returns a connection from the connection pool.
   * 
   * <p>This does and does not retrieve a new one from the data source when the 
   * pool is empty. This method will be blocked if there is no available 
   * connection in the pool.
   * 
   * @return an available connection from the connection pool
   * 
   * @throws InterruptedException when this method is interrupted
   * @throws Exception unexpected exceptions
   */
  @Override
  public Connection borrowConnection() throws Exception {
    return idleConnections.take();
  }




  /**
   * Return a connection to the connection pool. It is recommended to only
   * return the connection borrowed from this pool. All connection returned
   * will be reinitialized to its default state (including auto commit,
   * transaction isolation level, read only and category) which is
   * configured in the constructor of this class.
   * @param connection the connection to be returned to the pool
   * @throws IllegalStateException when the connection is already closed.
   * @throws Exception unexpected exceptions
   */
  @Override
  public void returnConnection( final Connection connection ) throws Exception {
    Assert.argumentIsNotNull( connection, "connection must not be null" );

    if ( connection.isClosed() ) {
      throw new IllegalStateException( "the connection is already " + "closed" );
    }
    reinitialize( connection );

    if ( !idleConnections.offer( connection ) ) {
      drop( connection );
    }
  }
}
