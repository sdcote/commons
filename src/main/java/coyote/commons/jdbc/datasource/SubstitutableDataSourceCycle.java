package coyote.commons.jdbc.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import coyote.commons.Assert;


public class SubstitutableDataSourceCycle implements Collection<SubstitutableDataSource> {

  private final List<SubstitutableDataSource> _substitutableDataSources;




  public SubstitutableDataSourceCycle( final Collection<? extends DataSource> dataSources ) {
    Assert.notNull( dataSources, "DataSources cannot be null" );
    Assert.isTrue( dataSources.size() > 1, "There should be at " + "least one element in dataSources" );
    _substitutableDataSources = new ArrayList<SubstitutableDataSource>( dataSources.size() );
    for ( final DataSource ds : dataSources ) {
      _substitutableDataSources.add( new SubstitutableDataSource( ds ) );
    }
    final int length = _substitutableDataSources.size();
    for ( int i = 0; i < length; ++i ) {
      final int subsIndex = ( ( i + 1 ) >= length ) ? 0 : ( i + 1 );
      _substitutableDataSources.get( i ).setSubstitution( _substitutableDataSources.get( subsIndex ) );
    }
  }




  @Override
  public boolean add( final SubstitutableDataSource substitutableDataSource ) {
    throw new UnsupportedOperationException();
  }




  @Override
  public boolean addAll( final Collection<? extends SubstitutableDataSource> c ) {
    throw new UnsupportedOperationException();
  }




  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }




  @Override
  public boolean contains( final Object o ) {
    return _substitutableDataSources.contains( o );
  }




  @Override
  public boolean containsAll( final Collection<?> c ) {
    return _substitutableDataSources.containsAll( c );
  }




  @Override
  public boolean isEmpty() {
    return _substitutableDataSources.isEmpty();
  }




  @Override
  public Iterator<SubstitutableDataSource> iterator() {
    return _substitutableDataSources.iterator();
  }




  @Override
  public boolean remove( final Object o ) {
    throw new UnsupportedOperationException();

  }




  @Override
  public boolean removeAll( final Collection<?> c ) {
    throw new UnsupportedOperationException();
  }




  @Override
  public boolean retainAll( final Collection<?> c ) {
    throw new UnsupportedOperationException();

  }




  @Override
  public int size() {
    return _substitutableDataSources.size();
  }




  @Override
  public Object[] toArray() {
    return _substitutableDataSources.toArray();
  }




  @Override
  public <T> T[] toArray( final T[] a ) {
    return _substitutableDataSources.toArray( a );
  }
}
