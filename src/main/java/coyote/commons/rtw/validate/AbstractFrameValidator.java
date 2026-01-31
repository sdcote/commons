/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.validate;

import coyote.commons.rtw.AbstractConfigurableComponent;
import coyote.commons.rtw.ConfigurableComponent;
import coyote.commons.rtw.FrameValidator;
import coyote.commons.rtw.context.TransformContext;

import java.io.IOException;




/**
 * 
 */
public abstract class AbstractFrameValidator extends AbstractConfigurableComponent implements FrameValidator, ConfigurableComponent {

  /**
   *
   */
  public void open(TransformContext context) {}




  /**
   * @see java.io.Closeable#close()
   */
  public void close() throws IOException {}

}
