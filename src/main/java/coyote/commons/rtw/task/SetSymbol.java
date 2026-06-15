/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;

import coyote.commons.StringUtil;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TaskException;
import coyote.commons.rtw.TransformTask;
import coyote.commons.template.Template;



/**
 * This sets a value in the symbol table with either a static value or the 
 * result of an evaluation.
 * 
 * <p>This task can be configured thusly:<pre>
 * "SetSymbol":{"symbol":"lucky","value":7}
 * "SetSymbol":{"context":"offset","evaluate":"300+[#$counter#]"}
 * "SetSymbol":{"symbol":"flag","value":true}
 * </pre>
 */
public class SetSymbol extends AbstractTransformTask implements TransformTask {

  /**
   *
   */
  @SuppressWarnings("unchecked")
  @Override
  protected void performTask() throws TaskException {
    if (StringUtil.isBlank(getSymbolValue())) {
      if (StringUtil.isNotBlank(getEvaluationExpression())) {
        String expression = getEvaluationExpression();
        try {
          Double result = evaluator.evaluateNumeric(expression);

          // if it is a whole number (no fractional part) cast it into a long
          if (result % 1 == 0) {
            long longValue = new Double(result).longValue();
            getContext().getSymbols().put(getSymbolName(), longValue);
            Log.debug(String.format("%s set '%s' to numeric value of '%d' with expression: '%s'", getClass().getSimpleName(), getSymbolName(), longValue, expression));
          } else {
            getContext().getSymbols().put(getSymbolName(), result);
            Log.debug(String.format("%s set '%s' to numeric value of '%f' with expression: '%s'", getClass().getSimpleName(), getSymbolName(), result, expression));
          }
        } catch (Exception e) {
          // maybe it is a boolean expression
          try {
            boolean result = evaluator.evaluateBoolean(expression);
            getContext().getSymbols().put(getSymbolName(), result);
            Log.debug(String.format("%s set '%s' to boolean value of '%b' with expression: '%s'", getClass().getSimpleName(), getSymbolName(), result, expression));
          } catch (IllegalArgumentException e1) {
            Log.error(String.format("%s could not evaluate expression '%s' - Numeric error: %s - Boolean error: %s", getClass().getSimpleName(), expression, e.getMessage(), e1.getMessage()));
          }
        }
      } else {
        Log.error(String.format("%s did not have a value nor expression configured, value not set", getClass().getSimpleName()));
      }
    } else {
      getContext().getSymbols().put(getSymbolName(), resolveArgument(getSymbolValue()));
    }
  }




  /**
   * @return
   */
  private String getEvaluationExpression() {
    String retval = null;
    if (getConfiguration().containsIgnoreCase(ConfigTag.EVALUATE)) {
      String value = getConfiguration().getString(ConfigTag.EVALUATE);
      if (StringUtil.isNotBlank(value)) {
        String cval = getContext().getAsString(retval, true);
        if (cval != null) {
          value = cval;
        }
        retval = Template.preProcess(value, getContext().getSymbols());
      }
    }
    return retval;
  }




  /**
   * @return the name of the symbol to set.
   */
  private Object getSymbolName() {
    String retval = null;
    if (getConfiguration().containsIgnoreCase(ConfigTag.SYMBOL)) {
      retval = getConfiguration().getString(ConfigTag.SYMBOL);
    }
    return retval;
  }




  /**
   * @return
   */
  private String getSymbolValue() {
    String retval = null;
    if (getConfiguration().containsIgnoreCase(ConfigTag.VALUE)) {
      retval = getConfiguration().getString(ConfigTag.VALUE);
    }
    return retval;
  }

}
