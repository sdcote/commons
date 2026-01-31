/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.reader;

import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.XMLMarshaler;
import coyote.commons.rtw.ConfigurableComponent;
import coyote.commons.rtw.FrameReader;

import java.util.List;




/**
 * This reader reads in XML records and makes them available to the transform engine.
 *
 * Ths reader support the ability to flatten hierarchical records (complex objects) into a single flat (single level)
 * record format. This is turned on by default. This capability is provided by the MarshalingFrameReader super class.
 */

public class XmlReader extends MarshalingFrameReader implements FrameReader, ConfigurableComponent {

  /**
   *
   */
  @Override
  protected List<DataFrame> getFrames(String data) {
    return XMLMarshaler.marshal(data);
  }

}
