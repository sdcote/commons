package coyote.commons.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.sql.DataSource;


public class MasterSlaveDataSource extends AbstractDataSource {

  private final DataSource _masterSlaveDataSource;




  public MasterSlaveDataSource( final DataSource master, final Collection<? extends DataSource> slaves ) {
    // join all data sources as read replicas
    final Collection<DataSource> masterAndSlaves = new ArrayList<DataSource>( slaves.size() + 1 );
    masterAndSlaves.add( master );
    masterAndSlaves.addAll( slaves );

    // each node is the substitution for another according to the list
    // order
    final Collection<SubstitutableDataSource> substitutableDataSources = new SubstitutableDataSourceCycle( masterAndSlaves );

    // round-robin replicas
    final RoundRobinDataSource roundRobinReads = new RoundRobinDataSource( substitutableDataSources );
    // split read (master) and write (replicas)
    _masterSlaveDataSource = new ReplicationDataSource( master, roundRobinReads );
  }




  @Override
  public Connection getConnection() throws SQLException {
    return _masterSlaveDataSource.getConnection();
  }




  @Override
  public Connection getConnection( final String username, final String password ) throws SQLException {
    return _masterSlaveDataSource.getConnection( username, password );
  }
}
