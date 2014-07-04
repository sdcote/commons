package coyote.commons.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import javax.sql.DataSource;

import coyote.commons.Assert;


public class LoadBalancingDataSource extends AbstractDataSource {

  private final DataSource[] _dataSources;
  private final LoadBalancingStrategy _strategy;




  public LoadBalancingDataSource( final Collection<? extends DataSource> c, final LoadBalancingStrategy strategy ) {
    Assert.argumentIsNotNull( c, "DataSource collection must not be null" );
    Assert.argumentIsValid( c.size() > 0, "DataSource collection must contain at least one item" );
    Assert.argumentIsNotNull( strategy, "Strategy must not be null" );
    _dataSources = c.toArray( new DataSource[c.size()] );
    _strategy = strategy;
  }




  @Override
  public Connection getConnection() throws SQLException {
    return getNextDataSource().getConnection();
  }




  @Override
  public Connection getConnection( final String username, final String password ) throws SQLException {
    return getNextDataSource().getConnection( username, password );
  }




  private DataSource getNextDataSource() {
    return _dataSources[_strategy.next()];
  }
}
