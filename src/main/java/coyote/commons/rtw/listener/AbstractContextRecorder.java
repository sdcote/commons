/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.listener;


import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.dataframe.DataFrameException;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.context.ContextListener;

/**
 * Base class for context listeners sending output to some target
 */
public abstract class AbstractContextRecorder extends AbstractListener implements ContextListener {

  protected boolean onRead = false;
  protected boolean onWrite = false;




  /**
   * @return the target URI to which the writer will write
   */
  public String getTarget() {
    return configuration.getAsString(ConfigTag.TARGET);
  }




  /**
   * Set the URI to where the writer will write its data.
   * 
   * @param value the URI to where the writer should write its data
   */
  public void setTarget(final String value) {
    configuration.put(ConfigTag.TARGET, value);
  }




  /**
   *
   */
  @Override
  public void setConfiguration(Config cfg) throws ConfigurationException {
    super.setConfiguration(cfg);

    if (cfg.contains(ConfigTag.READ)) {
      try {
        onRead = cfg.getAsBoolean(ConfigTag.READ);
        Log.debug(String.format( "ContextRecorder.read_flag_set_as %s", onRead));
      } catch (DataFrameException e) {
        Log.warn(String.format( "ContextRecorder.read_flag_not_valid %s", e.getMessage()));
        onRead = false;
      }
    } else {
      Log.debug( "ContextRecorder.no_read_flag");
    }

    if (cfg.contains(ConfigTag.WRITE)) {
      try {
        onWrite = cfg.getAsBoolean(ConfigTag.WRITE);
        Log.debug(String.format( "ContextRecorder.write_flag_set_as %s", onWrite));
      } catch (DataFrameException e) {
        Log.warn(String.format( "ContextRecorder.write_flag_not_valid %s", e.getMessage()));
        onWrite = false;
      }
    } else {
      Log.debug( "ContextRecorder.no_write_flag");
    }

  }
}
