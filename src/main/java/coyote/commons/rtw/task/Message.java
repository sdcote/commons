/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;

import coyote.commons.StringUtil;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TaskException;
import coyote.commons.template.Template;

/**
 * A task which generates a console message.
 */
public class Message extends AbstractTransformTask {

  public String getMessage() {
    if (configuration.containsIgnoreCase(ConfigTag.MESSAGE)) {
      return configuration.getString(ConfigTag.MESSAGE);
    }
    return null;
  }




  /**
   *
   */
  @Override
  protected void performTask() throws TaskException {

    String message = Template.resolve(getMessage(), getContext().getSymbols());

    if (StringUtil.isNotBlank(message)) {
      System.out.println(message);
    }

  }

}
