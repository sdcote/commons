/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.reader;


import coyote.commons.dataframe.DataFrame;
import coyote.commons.rtw.context.TransactionContext;
import coyote.commons.rtw.context.TransformContext;

/**
 * A do-nothing implementation of a reader, useful for testing.
 */
public class NullReader extends AbstractFrameReader {

  /**
   *
   */
  @Override
  public DataFrame read(TransactionContext context) {
    return null;
  }




  /**
   *
   */
  @Override
  public boolean eof() {
    return true;
  }




  /**
   *
   */
  @Override
  public void open(TransformContext context) {}

}
