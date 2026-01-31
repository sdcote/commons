/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.transform;


import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.FrameTransform;
import coyote.commons.rtw.TransformException;
import coyote.commons.rtw.transform.AbstractFieldTransform;

import java.util.UUID;

/**
 * Place a random globally unique identifier (GUID) in the named field.
 * 
 * <p>This can be configured thusly:<pre>
 * "Guid": { "field": "RecordId"}
 * "Guid": { "field": "RecordId", "secure": true }</pre>
 */
public class Guid extends AbstractFieldTransform implements FrameTransform {





  /**
   *
   */
  @Override
  public void setConfiguration(Config cfg) throws ConfigurationException {
    super.setConfiguration(cfg);
  }




  /**
   *
   */
  @Override
  protected DataFrame performTransform(DataFrame frame) throws TransformException {
    DataFrame retval = frame;
    String guid = UUID.randomUUID().toString();
    retval.put(getFieldName(), guid);
    return retval;
  }

}
