/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.validate;


import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.rtw.FrameValidator;
import coyote.commons.rtw.context.TransactionContext;

/**
 * There must be a value, even if it is an empty string or all whitespace.
 */
public class NotNull extends AbstractValidator implements FrameValidator {

  public NotNull() {
    description = "Field must contain a value";
  }




  /**
   *
   */
  @Override
  public boolean process(TransactionContext context) throws ValidationException {
    boolean retval = true;
    // get the field from the working frame of the given context
    DataFrame frame = context.getWorkingFrame();

    if (frame != null) {
      DataField field = frame.getField(fieldName);
      if (field != null) {
        if (field.isNull()) {
          retval = false;
          fail(context, fieldName);
        }
      } else {
        // if the field does not exist, it is effectively null - no value
        retval = false;
        fail(context, fieldName);
      }
    } else {
      // fail && error
      retval = false;
      fail(context, fieldName, "There is no working frame");
    }

    return retval;
  }

}
