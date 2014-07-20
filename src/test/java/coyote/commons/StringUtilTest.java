/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class StringUtilTest {
  @Test
  public void testStripLeadingHyphens() {
    assertEquals( "f", StringUtil.stripLeadingHyphens( "-f" ) );
    assertEquals( "foo", StringUtil.stripLeadingHyphens( "--foo" ) );
    assertEquals( "-foo", StringUtil.stripLeadingHyphens( "---foo" ) );
    assertNull( StringUtil.stripLeadingHyphens( null ) );
  }




  @Test
  public void testStripLeadingAndTrailingQuotes() {
    assertEquals( "bar", StringUtil.stripLeadingAndTrailingQuotes( "\"bar\"" ) );
  }
}
