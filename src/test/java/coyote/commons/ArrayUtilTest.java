package coyote.commons;

//import static org.junit.Assert.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Class ArrayUtilTest
 * 
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision:$
 */
public class ArrayUtilTest {

  /**
   * Method testGetScheme
   */
  @Test
  public void testGetScheme() {
    long[] primary = { 1l, 3l, 5l, 7l, 9l, 11l };

    long[] secondary = { 13l, 3l, 9l, 22l };

    long[] intersect = ArrayUtil.intersect( primary, secondary );

    assertTrue( intersect.length == 2 );

    System.out.print( "Intersection = (" );

    for ( int x = 0; x < intersect.length; x++ ) {
      System.out.print( intersect[x] );

      if ( x + 1 < intersect.length ) {
        System.out.print( "," );
      } else {
        System.out.println( ")" );
      }
    }
  }
}