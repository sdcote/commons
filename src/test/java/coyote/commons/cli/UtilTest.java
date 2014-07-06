package coyote.commons.cli;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;


/**
 *
 */
public class UtilTest {
  @Test
  public void testStripLeadingHyphens() {
    assertEquals( "f", Util.stripLeadingHyphens( "-f" ) );
    assertEquals( "foo", Util.stripLeadingHyphens( "--foo" ) );
    assertEquals( "-foo", Util.stripLeadingHyphens( "---foo" ) );
    assertNull( Util.stripLeadingHyphens( null ) );
  }




  @Test
  public void testStripLeadingAndTrailingQuotes() {
    assertEquals( "foo", Util.stripLeadingAndTrailingQuotes( "\"foo\"" ) );
  }
}
