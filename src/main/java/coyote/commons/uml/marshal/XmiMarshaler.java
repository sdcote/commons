/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml.marshal;

import java.text.SimpleDateFormat;


/**
 * Useful methods for generating XMI strings.
 */
public abstract class XmiMarshaler {

  protected static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
  protected static final String CRLF = "\r\n";
  protected static final String EMPTY_STRING = "";




  /**
   * The padding for the given level.
   * 
   * <p>There are two spaces per level of padding. Zero or less levels result 
   * in an empty string being returned. This will never return null.</p>
   * 
   * @param level the level of padding required
   * 
   * @return a string to indent to the given level
   */
  protected static String getPadding(int level) {
    if (level < 1) {
      return EMPTY_STRING;
    } else {
      int length = level * 2;
      StringBuffer outputBuffer = new StringBuffer(length);
      for (int i = 0; i < length; i++) {
        outputBuffer.append(" ");
      }
      return outputBuffer.toString();
    }
  }




  /**
   * Returns either CRFL or EMPTY_STRING depending on the level
   * @param level the level of indenting
   * @return CRFL if level is zero or grater, an empty string "" otherwise.
   */
  protected static String lineEnd(int level) {
    if (level > -1) {
      return CRLF;
    } else {
      return EMPTY_STRING;
    }
  }

}
