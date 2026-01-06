package coyote.commons;

//import static org.junit.Assert.*;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class ArrayUtilTest
 */
public class ExceptionUtilTest {

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

      assertEquals(ROOT, ExceptionUtil.getRootMessage(ex));
      assertEquals(ROOT, ExceptionUtil.getRootMessage(nested));
      assertEquals(ROOT, ExceptionUtil.getRootMessage(root));
  }
  
  /**
   * Test method for {@link coyote.commons.ExceptionUtil#getLocalJavaName(java.lang.String)}.
   */
  @Test
  public void testGetLocalJavaName() {
    assertNotNull( ExceptionUtil.getLocalJavaName( this.getClass().getName() ) );
      assertEquals("ExceptionUtilTest", ExceptionUtil.getLocalJavaName(this.getClass().getName()));
  }

  @Test
  public void testGetAbbreviatedClassname() {
      assertEquals("c.c.ExceptionUtilTest", ExceptionUtil.getAbbreviatedClassname(this.getClass().getName()));
      assertEquals("ThisClass", ExceptionUtil.getAbbreviatedClassname("ThisClass"));
    assertNotNull( ExceptionUtil.getAbbreviatedClassname( "" ) );
      assertEquals("", ExceptionUtil.getAbbreviatedClassname(""));
    assertNotNull( ExceptionUtil.getAbbreviatedClassname( null ) );
      assertEquals("", ExceptionUtil.getAbbreviatedClassname(null));
  }

}