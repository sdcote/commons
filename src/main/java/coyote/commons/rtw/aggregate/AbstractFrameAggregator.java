/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.aggregate;

import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.Log;
import coyote.commons.rtw.*;
import coyote.commons.rtw.context.TransactionContext;
import coyote.commons.rtw.context.TransformContext;
import coyote.commons.rtw.eval.Evaluator;

import java.io.IOException;
import java.util.List;



/**
 * 
 */
public abstract class AbstractFrameAggregator extends AbstractConfigurableComponent implements FrameAggregator {
  protected Evaluator evaluator = new Evaluator();




  /**
   *
   */
  public void open(final TransformContext context) {
    super.context = context;

    // set the transform context in the evaluator so it can resolve variables
    evaluator.setContext(context);
  }




  /**
   * @see java.io.Closeable#close()
   */
  public void close() throws IOException {
    // no-op implementation
  }




  /**
   * Return the conditional expression from the configuration.
   * 
   * @return the condition which must evaluate to true before the aggregator 
   *         is to execute.
   */
  public String getCondition() {
    if (configuration.containsIgnoreCase(ConfigTag.CONDITION)) {
      return configuration.getString(ConfigTag.CONDITION);
    }
    return null;
  }




  /**
   *
   */
  @Override
  public List<DataFrame> process(List<DataFrame> frames, TransactionContext txnContext) {
    List<DataFrame> retval = null;
    if (isEnabled()) {
      if (getCondition() != null) {
        try {
          if (evaluator.evaluateBoolean(getCondition())) {
            retval = aggregate(frames, txnContext);
          } else {
            if (Log.isLogging(Log.DEBUG_EVENTS)) {
              Log.debug(String.format( "Aggregator.boolean_evaluation_false", getCondition()));
            }
          }
        } catch (final IllegalArgumentException e) {
          Log.error(String.format( "Aggregator.boolean_evaluation_error", getCondition(), e.getMessage()));
        }
      } else {
        retval = aggregate(frames, txnContext);
      }
    }
    return retval;
  }




  /**
   * Perform the actual aggregation.
   * 
   * @param frames frames to aggregate
   * @param txnContext the transaction context
   * 
   * @return a list of dataframes representing the aggregation. This may be empty but never null.
   */
  protected abstract List<DataFrame> aggregate(List<DataFrame> frames, TransactionContext txnContext);

}
