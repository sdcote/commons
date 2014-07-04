package coyote.commons.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import coyote.commons.Assert;


/**
 * 
 */
public class SubstitutableDataSource extends AbstractDataSource {

  private final DataSource _dataSource;
  private DataSource _substitution;
  private boolean _infiniteLoopDetectionEnabled = true;
  private boolean _exceptionOmitted = true;
  private final ThreadLocal<Boolean> _errorOccurred = new ThreadLocal<Boolean>() {
    @Override
    protected Boolean initialValue() {
      return false;
    }
  };




  public SubstitutableDataSource( final DataSource dataSource ) {
    Assert.argumentIsNotNull( dataSource, "DataSource cannot be null" );
    _dataSource = dataSource;
  }




  private void afterConnectionReturned() {
    if ( _infiniteLoopDetectionEnabled ) {
      _errorOccurred.set( false );
    }
  }




  private void afterExceptionOccurred( final SQLException ex ) throws SQLException {
    if ( ( _substitution == null ) || _errorOccurred.get() ) {
      throw ex;
    }
    if ( _infiniteLoopDetectionEnabled ) {
      _errorOccurred.set( true );
    }
    if ( !_exceptionOmitted ) {
      ex.printStackTrace();
    }
  }




  @Override
  public Connection getConnection() throws SQLException {
    try {
      final Connection conn = _dataSource.getConnection();
      afterConnectionReturned();
      return conn;
    } catch ( final SQLException ex ) {
      afterExceptionOccurred( ex );
      return _substitution.getConnection();
    }
  }




  @Override
  public Connection getConnection( final String username, final String password ) throws SQLException {
    try {
      final Connection conn = _dataSource.getConnection( username, password );
      afterConnectionReturned();
      return conn;
    } catch ( final SQLException ex ) {
      afterExceptionOccurred( ex );
      return _substitution.getConnection( username, password );
    }
  }




  public boolean isExceptionOmitted() {
    return _exceptionOmitted;
  }




  public boolean isInfiniteLoopDetectionEnabled() {
    return _infiniteLoopDetectionEnabled;
  }




  public void setExceptionOmitted( final boolean exceptionOmitted ) {
    _exceptionOmitted = exceptionOmitted;
  }




  public void setInfiniteLoopDetectionEnabled( final boolean infiniteLoopDetectionEnabled ) {
    _infiniteLoopDetectionEnabled = infiniteLoopDetectionEnabled;
  }




  public void setSubstitution( final DataSource substitution ) {
    _substitution = substitution;
  }
}
