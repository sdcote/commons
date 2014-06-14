package coyote.commons;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * Class ArrayUtilTest
 * 
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision:$
 */
public class ChainedExceptionTest {

  /**
   * Method testGetRootMessage
   */
  @Test
  public void testGetRootMessage() {
    String ROOT = "Root";
    String NESTED = "Nested";
    String TOP = "Top";

    Exception root = new Exception( ROOT );
    Exception nested = new Exception( NESTED, root );
    Exception ex = new Exception( TOP, nested );

    assertTrue( ROOT.equals( ExceptionUtil.getRootMessage( ex ) ) );
    assertTrue( ROOT.equals( ExceptionUtil.getRootMessage( nested ) ) );
    assertTrue( ROOT.equals( ExceptionUtil.getRootMessage( root ) ) );
  }

}