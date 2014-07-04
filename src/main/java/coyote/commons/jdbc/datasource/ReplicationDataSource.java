package coyote.commons.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import coyote.commons.Assert;


public class ReplicationDataSource extends AbstractDataSource {

  private final DataSource _readWreiteDatasource;
  private final DataSource _readOnlyDatasource;




  public ReplicationDataSource( final DataSource readWriteDs, final DataSource readOnlyDs ) {
    Assert.argumentIsNotNull( readWriteDs, "ReadWrite datasource cannot be null" );
    Assert.argumentIsNotNull( readOnlyDs, "Read only datasource cannot be null" );
    _readWreiteDatasource = readWriteDs;
    _readOnlyDatasource = readOnlyDs;
  }




  @Override
  public Connection getConnection() throws SQLException {
    return new ReplicationConnection( _readWreiteDatasource.getConnection(), _readOnlyDatasource.getConnection() );
  }




  @Override
  public Connection getConnection( final String username, final String password ) throws SQLException {
    return new ReplicationConnection( _readWreiteDatasource.getConnection( username, password ), _readOnlyDatasource.getConnection( username, password ) );
  }
}
