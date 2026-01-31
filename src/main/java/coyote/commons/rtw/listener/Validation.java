/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.listener;

import java.util.ArrayList;
import java.util.List;

import coyote.commons.StringUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.rtw.FrameValidator;
import coyote.commons.rtw.context.OperationalContext;
import coyote.commons.rtw.context.TransactionContext;


/**
 * This writes the record with all the errors for later processing.
 */
public class Validation extends AbstractFileRecorder {

  List<String> validationErrors = new ArrayList<String>();




  /**
   *
   */
  @Override
  public void setConfiguration(Config cfg) throws ConfigurationException {
    super.setConfiguration(cfg);

    // check for any other options to set here...like format, whether to include the error message...

  }




  /**
   *
   */
  @Override
  public void onValidationFailed(OperationalContext context, FrameValidator validator, String errorMessage) {
    StringBuffer b = new StringBuffer();
    b.append("Field '");
    b.append(validator.getFieldName());
    b.append("' did not pass '");
    b.append(validator.getClass().getSimpleName());
    b.append("' check: ");
    b.append(validator.getDescription());
    validationErrors.add(b.toString());
  }




  /**
   * Write the errors out with the record.
   * 
   *
   */
  public void onFrameValidationFailed(TransactionContext context) {
    StringBuffer b = new StringBuffer();
    b.append(context.getRow());
    b.append(": ");

    // Show the validation errors
    for (int x = 0; x < validationErrors.size(); x++) {
      b.append(validationErrors.get(x));
      if (x + 1 < validationErrors.size()) {
        b.append(", ");
      }
    }

    // show the frame which failed validation
    b.append(": ");
    b.append(context.getWorkingFrame().toString());
    b.append(StringUtil.LINE_FEED);

    // clear out the collected errors
    validationErrors.clear();

    // write out the validation failure
    write(b.toString());

  }

}
