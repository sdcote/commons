package coyote.commons.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import coyote.commons.Assert;


public class QueuedConnectionPool extends AbstractConnectionPool {

  private final DataSource _dataSource;
  private final int _maxSize;
  private final Queue<Connection> _idleConnections;

  private final Lock _idleConnsLock = new ReentrantLock();




  public QueuedConnectionPool( final DataSource dataSource, final int initialSize, final int maxSize, final boolean defaultAutoCommit, final int defaultTransactionIsolation, final boolean defaultReadOnly, final String defaultCategory ) throws SQLException {
    super( defaultAutoCommit, defaultTransactionIsolation, defaultReadOnly, defaultCategory );
    Assert.argumentIsNotNull( dataSource, "_dataSource must not be null" );
    Assert.argumentIsValid( initialSize >= 0, "initialSize must not be negative" );
    Assert.argumentIsValid( maxSize >= 0, "_maxSize must not be negative" );
    Assert.argumentIsValid( maxSize >= initialSize, "_maxSize must be equal or greater than initialSize" );

    _dataSource = dataSource;
    _maxSize = maxSize;
    _idleConnections = new ConcurrentLinkedQueue<Connection>();

    for ( int i = 0; i < initialSize; ++i ) {
      _idleConnections.add( dataSource.getConnection() );
    }
  }




  @Override
  public Connection borrowConnection() throws Exception {
    _idleConnsLock.lock();
    try {
      return ( _idleConnections.isEmpty() ) ? _dataSource.getConnection() : _idleConnections.poll();
    }
    finally {
      _idleConnsLock.unlock();
    }
  }




  @Override
  public void returnConnection( final Connection connection ) throws Exception {
    Assert.argumentIsNotNull( connection, "connection must not be null" );

    if ( connection.isClosed() ) {
      return;
    }
    reinitialize( connection );

    boolean drop = false;

    _idleConnsLock.lock();
    try {
      if ( _idleConnections.size() >= _maxSize ) {
        drop = true;
      } else {
        _idleConnections.add( connection );
      }
    }
    finally {
      _idleConnsLock.unlock();
    }

    if ( drop ) {
      drop( connection );
    }
  }
}
