/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.listener;

import java.io.IOException;

import coyote.commons.StringUtil;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.rtw.*;
import coyote.commons.rtw.context.ContextListener;
import coyote.commons.rtw.context.OperationalContext;
import coyote.commons.rtw.context.TransactionContext;
import coyote.commons.rtw.context.TransformContext;
import coyote.commons.rtw.eval.Evaluator;


/**
 * No-op implementation of a listener to assist in cleaner coding of listeners.
 */
public abstract class AbstractListener extends AbstractConfigurableComponent implements ContextListener, ConfigurableComponent {

  protected Evaluator evaluator = new Evaluator();




  /**
   * Return the conditional expression from the configuration.
   * 
   * @return the condition which must evaluate to true before the task is to 
   *         execute.
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
  public void open(TransformContext context) {
    setContext(context);
    evaluator.setContext(context);

    if (StringUtil.isNotBlank(getCondition())) {
      try {
        evaluator.evaluateBoolean(getCondition());
      } catch (final IllegalArgumentException e) {
        context.setError("Invalid boolean expression in listener: " + e.getMessage());
      }
    }
  }




  /**
   *
   */
  @Override
  public void onError(OperationalContext context) {
    // listeners should override this method to receive error notifications
  }




  /**
   *
   */
  @Override
  public void onEnd(OperationalContext context) {
    // listeners should override this method to perform processing when the transform or context ends
  }




  /**
   *
   */
  @Override
  public void onStart(OperationalContext context) {
    // listeners should override this method to perform processing before the transform or transaction starts
  }




  /**
   *
   */
  @Override
  public void onRead(TransactionContext context, FrameReader reader) {
    // listeners should override this method to perform processing related to reads
  }




  /**
   *
   */
  @Override
  public void onWrite(TransactionContext context, FrameWriter writer) {
    // listeners should override this method to perform processing related to write
  }




  /**
   *
   */
  @Override
  public void onValidationFailed(OperationalContext context, FrameValidator validator, String msg) {
    // listeners should override this method to perform processing when a field fails validations
  }




  /**
   * @see java.io.Closeable#close()
   */
  @Override
  public void close() throws IOException {
    // listeners should perform their clean-up processing here
  }




  /**
   *
   */
  @Override
  public void onFrameValidationFailed(TransactionContext context) {
    // override this method to perform processing when the entire frame fails validation
  }




  /**
   *
   */
  @Override
  public void onMap(TransactionContext txnContext) {
    // listeners should override this method to perform processing after mapping and before writing.
  }




  /**
   *
   */
  @Override
  public void preload(DataFrame frame) {
    // override to receive historic data, if the job is configured with a preloader    
  }

}
