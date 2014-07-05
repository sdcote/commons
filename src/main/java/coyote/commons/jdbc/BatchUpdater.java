package coyote.commons.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import coyote.commons.Assert;


public class BatchUpdater {

  private final PreparedStatement ps;




  BatchUpdater( final PreparedStatement ps ) {
    Assert.argumentIsNotNull( ps, "PreparedStatement must not be null" );
    this.ps = ps;
  }




  public BatchUpdater addBatch( final List<?> parameters ) throws DataAccessException {
    Assert.argumentIsNotNull( parameters, "parameters cannot be null" );

    final PreparedStatementSetter setter = new SimplePreparedStatementSetter( parameters );
    return addBatch( setter );

  }




  public BatchUpdater addBatch( final PreparedStatementSetter setter ) throws DataAccessException {
    Assert.argumentIsNotNull( setter, "setter must not be null" );

    try {
      ps.clearParameters();
      setter.setPreparedStatement( ps );
      ps.addBatch();
      return this;
    } catch ( final SQLException ex ) {
      closePreparedStatement( ps );
      throw new DataAccessException( ex );
    }
  }




  private void closePreparedStatement( final PreparedStatement ps ) throws DataAccessException {
    try {
      ps.close();
    } catch ( final SQLException ex ) {
      throw new DataAccessException( ex );
    }
  }




  public int[] doBatch() throws DataAccessException {
    try {
      return ps.executeBatch();
    } catch ( final SQLException ex ) {
      throw new DataAccessException( ex );
    }
    finally {
      closePreparedStatement( ps );
    }
  }




  public <K> List<K> doBatch( final ResultMapper<K> keyMapper ) throws DataAccessException {
    Assert.argumentIsNotNull( keyMapper, "keyMapper must not be null" );

    try {
      int initArraySize = 0;
      final int[] rows = ps.executeBatch();
      for ( final int row : rows ) {
        if ( row > 0 ) {
          initArraySize += row;
        }
      }

      final ResultSet keyRs = ps.getGeneratedKeys();
      final List<K> keys = new ArrayList<K>( initArraySize );
      while ( keyRs.next() ) {
        keys.add( keyMapper.map( new ExtendedResultSetImpl( keyRs ) ) );
      }
      return keys;
    } catch ( final SQLException ex ) {
      throw new DataAccessException( ex );
    }
  }
}
