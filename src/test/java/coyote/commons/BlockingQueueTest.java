package coyote.commons;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;


/**
 * Class URITest
 *
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision: 1.5 $
 */
public class BlockingQueueTest {

  /**
   * Method testGetScheme
   */
  @Test
  public void testLodeOne() {
    int capacity = 59872;
    BlockingQueue queue = new BlockingQueue( capacity );

    try {
      for( int i = 0; i < queue.capacity(); i++ ) {
        queue.put( new String( "string" ), 2 );

        int size = i + 1;
        float load = (float)size / (float)capacity;

        assertTrue( "Size should be " + size + " is " + queue.size(), queue.size() == size );
        assertTrue( "Load should be " + load + " is " + queue.load(), queue.load() == load );
      }
    }
    catch( Exception e ) {
      fail( "Exception" );
    }
    finally {
      queue.clear();
    }
  }




  /**
   * Method testBlockOne
   */
  @Test
  public void testBlockOne() {
    int capacity = 5;

    BlockingQueue queue = new BlockingQueue( capacity );

    try {
      for( int i = 0; i < queue.capacity(); i++ ) {
        queue.put( new String( "string" ), 2 );
      }

      try {
        // try to put one more object in the queue, but it should time out
        queue.put( new String( "string" ), 20 );
        fail( "Should have timed out" );
      }
      catch( Exception ignore ) {
        // Since there is nothing to remove anything from the queue, the put
        // should fail by timing out
      }
    }
    catch( Exception e ) {
      fail( "Exception: " + e.getMessage() );
    }
    finally {
      queue.clear();
    }
  }




  /**
   * Method testBlockTwo
   */
  @Test
  public void testBlockTwo() {
    int capacity = 5;

    BlockingQueue queue = new BlockingQueue( capacity );

    try {
      // try to get an object from an empty queue. The operation should block
      // here and when it times out, an null reference should be returned
      Object obj = queue.get( 20 );

      assertNull( "GET should have timed out", obj );
    }
    catch( Exception ignore ) {
      fail( "Should not have been interrupted" );
    }
    finally {
      queue.clear();
    }
  }
}