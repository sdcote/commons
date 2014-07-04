package coyote.commons.jdbc.datasource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class CloseOnCompletionStatement extends StatementProxy {

  private final Queue<ResultSet> _resultSetsToBeClosed;




  public CloseOnCompletionStatement( final Statement statement ) {
    super( statement );
    _resultSetsToBeClosed = new ConcurrentLinkedQueue<ResultSet>();
  }




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
  public ResultSet executeQuery( final String sql ) throws SQLException {
    final ResultSet resultSetToBeClosed = super.executeQuery( sql );
    _resultSetsToBeClosed.add( resultSetToBeClosed );
    return resultSetToBeClosed;
  }




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




  @Override
  public boolean isCloseOnCompletion() {
    return true;
  }
}
