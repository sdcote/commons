package coyote.commons.jdbc.datasource;

import java.util.concurrent.atomic.AtomicInteger;

import coyote.commons.Assert;


public class RoundRobin implements LoadBalancingStrategy {

  private final int _maxIndex;
  private final AtomicInteger _counter = new AtomicInteger( 0 );




  public RoundRobin( final int maxIndex ) {
    Assert.argumentIsPositive( maxIndex, "_maxIndex must be a positive number" );
    _maxIndex = maxIndex;
  }




  @Override
  public int next() {
    while ( true ) {
      final int current = _counter.get();
      final int next = ( current + 1 ) % _maxIndex;
      if ( _counter.compareAndSet( current, next ) ) {
        return current;
      }
    }
  }
}
