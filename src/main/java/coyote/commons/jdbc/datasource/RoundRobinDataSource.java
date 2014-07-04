package coyote.commons.jdbc.datasource;

import java.util.Collection;

import javax.sql.DataSource;


public class RoundRobinDataSource extends LoadBalancingDataSource {

  public RoundRobinDataSource( final Collection<? extends DataSource> c ) {
    super( c, new RoundRobin( c.size() ) );
  }
}
