/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;

import java.util.Locale;

import coyote.commons.StringUtil;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TaskException;


/**
 * Log the current state of the symbol table.
 * 
 * <p>By default it logs it to the INFO category, but can be configured 
 * thusly:<pre>"LogSymbols":{"category":"debug"}</pre>
 */
public class LogSymbols extends AbstractFileTask {

  /**
   *
   */
  @Override
  protected void performTask() throws TaskException {
    String category = getString(ConfigTag.CATEGORY);
    long mask;
    if (StringUtil.isNotEmpty(category)) {
      mask = Log.getCode(category.trim().toUpperCase(Locale.getDefault()));
    } else {
      mask = Log.INFO_EVENTS;
    }
    Log.append(mask, getContext().getSymbols().dump());
  }

}
