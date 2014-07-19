package coyote.commons.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import coyote.commons.Assert;


public abstract class RoutingDataSource extends AbstractDataSource {

  private final Map<Object, DataSource> _availableDataSourcesMap;
  private final DataSource _defaultDataSource;




  public RoutingDataSource( final Map<?, ? extends DataSource> dataSourcesMap ) {
    this( dataSourcesMap, null );
  }




  public RoutingDataSource( final Map<?, ? extends DataSource> dataSourcesMap, final DataSource defaultDataSource ) {
    Assert.notEmpty( dataSourcesMap, "dataSourcesMap must not be null or empty" );
    _availableDataSourcesMap = Collections.unmodifiableMap( new HashMap<Object, DataSource>( dataSourcesMap ) );
    _defaultDataSource = defaultDataSource;
  }




  @Override
  public Connection getConnection() throws SQLException {
    return getTargetDataSourceOrThrowException().getConnection();
  }




  @Override
  public Connection getConnection( final String username, final String password ) throws SQLException {
    return getTargetDataSourceOrThrowException().getConnection( username, password );
  }




  protected abstract DataSource getTargetDataSource( Map<Object, DataSource> availableDataSourcesMap ) throws Exception;




  private DataSource getTargetDataSourceOrThrowException() throws SQLException {
    try {
      return getTargetDataSource( _availableDataSourcesMap );
    } catch ( final Exception ex ) {
      if ( _defaultDataSource == null ) {
        throw new SQLException( ex );
      }
      return _defaultDataSource;
    }
  }
}
