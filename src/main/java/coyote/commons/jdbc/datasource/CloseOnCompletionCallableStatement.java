package coyote.commons.jdbc.datasource;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 *
 */
public class CloseOnCompletionCallableStatement extends CallableStatementProxy {

  private final Queue<ResultSet> _resultSetsToBeClosed;




  /**
   * Construct a CloseOnCompletionCallableStatement with a existing 
   * callableStatement instance which will be delegated by this object.
   * 
   * @param callableStatement the delegated callableStatement instance
   */
  public CloseOnCompletionCallableStatement( final CallableStatement callableStatement ) {
    super( callableStatement );
    _resultSetsToBeClosed = new ConcurrentLinkedQueue<ResultSet>();
  }




  /**
   * Release this CallableStatement's database, and at the same time close all 
   * the ResultSet instances which was generated by this object.
   * 
   * @throws SQLException
   */
  @Override
  public void close() throws SQLException {
    try {
      for ( final ResultSet resultSetToBeClosed : _resultSetsToBeClosed ) {
        if ( !resultSetToBeClosed.isClosed() ) {
          resultSetToBeClosed.close();
        }
      }
    }
    finally {
      super.close();
    }
  }




  @Override
  public ResultSet executeQuery() throws SQLException {
    final ResultSet resultSetToBeClosed = super.executeQuery();
    _resultSetsToBeClosed.add( resultSetToBeClosed );
    return resultSetToBeClosed;
  }




  @Override
  public ResultSet executeQuery( final String sql ) throws SQLException {
    final ResultSet resultSetToBeClosed = super.executeQuery( sql );
    _resultSetsToBeClosed.add( resultSetToBeClosed );
    return resultSetToBeClosed;
  }




  /**
   * When this CallableStatement instance is released by GC, {@link #close()}
   * will be called automatically to ensure that all connections and resources 
   * to the database have been safely closed.
   * 
   * @throws Throwable
   */
  @Override
  protected void finalize() throws Throwable {
    try {
      if ( !isClosed() ) {
        close();
      }
    }
    finally {
      super.finalize();
    }
  }




  @Override
  public ResultSet getGeneratedKeys() throws SQLException {
    final ResultSet resultSetToBeClosed = super.getGeneratedKeys();
    _resultSetsToBeClosed.add( resultSetToBeClosed );
    return resultSetToBeClosed;
  }




  @Override
  public ResultSet getResultSet() throws SQLException {
    final ResultSet resultSetToBeClosed = super.getResultSet();
    _resultSetsToBeClosed.add( resultSetToBeClosed );
    return resultSetToBeClosed;
  }




  /**
   * Since this CallableStatement is will be closed when all its dependent 
   * result sets are closed, it will always return true.
   * 
   * @return true (always)
   */
  @Override
  public boolean isCloseOnCompletion() {
    return true;
  }
}
