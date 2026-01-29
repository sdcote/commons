/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.reader;

import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.JSONMarshaler;
import coyote.commons.rtw.ConfigurableComponent;
import coyote.commons.rtw.FrameReader;

import java.util.List;




/**
 * This reader reads in JSON records and makes them available to the transform engine.
 *
 * <p> The reader can be configured thusly:
 * <pre>"Reader": { "class": "JsonReader", "source": "requestmetric.json" },</pre>
 * <p>This reader support the ability to flatten hierarchical records (complex objects) into a single flat (single level)
 * record format. This is turned on by default. This capability is provided by the MarshalingFrameReader super class.
 */
public class JsonReader extends MarshalingFrameReader implements FrameReader, ConfigurableComponent {

  /**
   *
   */
  @Override
  protected List<DataFrame> getFrames(String data) {
    return JSONMarshaler.marshal(data);
  }

}
