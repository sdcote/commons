/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.writer;


import coyote.commons.dataframe.DataFrame;
import coyote.commons.rtw.context.TransformContext;

/**
 * A do-nothing implementation of a writer useful for testing
 */
public class NullWriter extends AbstractFrameWriter {

  /**
   *
   */
  @Override
  public void write(DataFrame frame) {}




  /**
   *
   */
  @Override
  public void open(TransformContext context) {}

}
