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

    ChainedException root = new ChainedException( ROOT );
    ChainedException nested = new ChainedException( NESTED, root );
    ChainedException ex = new ChainedException( TOP, nested );

    assertTrue( ROOT.equals( ex.getRootMessage() ) );
    assertTrue( ROOT.equals( nested.getRootMessage() ) );
    assertTrue( ROOT.equals( root.getRootMessage() ) );
  }




  /**
   * Method testGetCauseMessage
   */
  @Test
  public void testGetCauseMessage() {
    String ROOT = "Root";
    String NESTED = "Nested";
    String TOP = "Top";

    ChainedException root = new ChainedException( ROOT );
    ChainedException nested = new ChainedException( NESTED, root );
    ChainedException ex = new ChainedException( TOP, nested );

    System.out.println();
    System.out.println( "ex=" + ex.getCauseMessage() );
    System.out.println( "nested=" + nested.getCauseMessage() );
    System.out.println( "root=" + root.getCauseMessage() );

    assertTrue( NESTED.equals( ex.getCauseMessage() ) );
    assertTrue( ROOT.equals( nested.getCauseMessage() ) );
    assertTrue( ROOT.equals( root.getCauseMessage() ) );
  }
}