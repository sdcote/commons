/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    String token = PPG.generateToken(3);
    assertEquals(3, token.length());
    token = PPG.generateToken(4);
    assertEquals(4, token.length());
    token = PPG.generateToken(5);
    assertEquals(5, token.length());
    token = PPG.generateToken(6);
    assertEquals(6, token.length());
  }

}
