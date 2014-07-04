package coyote.commons.jdbc.datasource;

import java.util.Map;

import javax.sql.DataSource;


public abstract class KeyDeterminedRoutingDataSource extends RoutingDataSource {

  public KeyDeterminedRoutingDataSource( final Map<?, ? extends DataSource> dataSourcesMap ) {
    super( dataSourcesMap );
  }




  public KeyDeterminedRoutingDataSource( final Map<?, ? extends DataSource> dataSourcesMap, final DataSource defaultDataSource ) {
    super( dataSourcesMap, defaultDataSource );
  }




  @Override
  protected final DataSource getTargetDataSource( final Map<Object, DataSource> availableDataSourcesMap ) throws Exception {
    return availableDataSourcesMap.get( getTargetKey() );
  }




  protected abstract Object getTargetKey() throws Exception;
}
