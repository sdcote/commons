/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * 
 */
public class PPGTest {

  /**
   * Test method for {@link coyote.commons.security.PPG#generate(int, int, int)}.
   */
  @Test
  public void testGenerate() {
    String phrase = PPG.generate( 3, 3, 5 );
    String[] tokens = phrase.split( " " );
    assertTrue( tokens.length == 3 );
    phrase = PPG.generate( 4, 3, 5 );
    tokens = phrase.split( " " );
    assertTrue( tokens.length == 4 );
    phrase = PPG.generate( 5, 3, 5 );
    tokens = phrase.split( " " );
    assertTrue( tokens.length == 5 );
    phrase = PPG.generate( 6, 3, 5 );
    tokens = phrase.split( " " );
    assertTrue( tokens.length == 6 );
  }




  /**
   * Test method for {@link coyote.commons.security.PPG#generateToken(int)}.
   */
  @Test
  public void testGenerateToken() {
    String token = PPG.generateToken( 3 );
    assertTrue( token.length() == 3 );
    token = PPG.generateToken( 4 );
    assertTrue( token.length() == 4 );
    token = PPG.generateToken( 5 );
    assertTrue( token.length() == 5 );
    token = PPG.generateToken( 6 );
    assertTrue( token.length() == 6 );
  }

}
