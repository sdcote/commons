/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.filter;

import coyote.commons.log.Log;
import coyote.commons.rtw.FrameFilter;
import coyote.commons.rtw.context.TransactionContext;
import coyote.commons.template.Template;


/**
 * If the condition is met, then this frame is accepted as it is, no other rule 
 * processing is performed.
 */
public class Accept extends AbstractFrameFilter implements FrameFilter {

  public Accept() {}




  public Accept(String condition) {
    super(condition);
  }




  /**
   *
   */
  @Override
  public boolean process(TransactionContext context) {

    // If there is a conditional expression
    if (expression != null) {

      // Treat expressions as templates
      String resolvedExpression = Template.preProcess(expression, getContext().getSymbols());

      try {
        // if the condition evaluates to true
        if (evaluator.evaluateBoolean(resolvedExpression)) {
          if (Log.isLogging(Log.DEBUG_EVENTS)) {
            Log.debug("Accepted frame " + context.getRow());
          }

          // signal that other filters should not run
          return false;
        } else {
          if (Log.isLogging(Log.DEBUG_EVENTS)) {
            Log.debug("Did not pass accept expression of '" + resolvedExpression + "'");
          }
        }
      } catch (IllegalArgumentException e) {
        Log.warn(String.format( "Filter.accept_boolean_evaluation_error %s", e.getMessage()));
      }
    }

    return true;
  }

}
