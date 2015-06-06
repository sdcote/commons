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
    Assert.notNull( ps, "PreparedStatement must not be null" );
    this.ps = ps;
  }




  public BatchUpdater addBatch( final List<?> parameters ) throws DataException {
    Assert.notNull( parameters, "parameters cannot be null" );

    final StatementSetter setter = new DefaultStatementSetter( parameters );
    return addBatch( setter );

  }




  public BatchUpdater addBatch( final StatementSetter setter ) throws DataException {
    Assert.notNull( setter, "setter must not be null" );

    try {
      ps.clearParameters();
      setter.setPreparedStatement( ps );
      ps.addBatch();
      return this;
    } catch ( final SQLException ex ) {
      closePreparedStatement( ps );
      throw new DataException( ex );
    }
  }




  private void closePreparedStatement( final PreparedStatement ps ) throws DataException {
    try {
      ps.close();
    } catch ( final SQLException ex ) {
      throw new DataException( ex );
    }
  }




  public int[] doBatch() throws DataException {
    try {
      return ps.executeBatch();
    } catch ( final SQLException ex ) {
      throw new DataException( ex );
    }
    finally {
      closePreparedStatement( ps );
    }
  }




  public <K> List<K> doBatch( final ResultMapper<K> resultMapper ) throws DataException {
    Assert.notNull( resultMapper, "Result Mapper must not be null" );

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
        keys.add( resultMapper.map( new DefaultTypedResultSet( keyRs ) ) );
      }
      return keys;
    } catch ( final SQLException ex ) {
      throw new DataException( ex );
    }
  }
}
