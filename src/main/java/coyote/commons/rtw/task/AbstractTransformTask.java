/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;

import java.io.IOException;

import coyote.commons.StringUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.log.Log;
import coyote.commons.rtw.*;
import coyote.commons.rtw.context.TransformContext;
import coyote.commons.rtw.eval.Evaluator;


/**
 * This is the base class for pre- and post-processing tasks.
 * 
 * <p>Subclasses should override  {@link #performTask()} and let this class 
 * handle determining if the task should run based on the conditional 
 * statements (if one is set) and the enabled flag. This way, all conditional 
 * checks are handled uniformly across all tasks.
 * 
 * <p>Values in the configuration are first used as keys to the context. If 
 * there is no value with that name in the context, the task uses the value as 
 * a literal argument. The primary use case is to just use the literal value in 
 * the configuration, but this context look-up gives the tasks the ability to 
 * get dynamic values from the context which were placed there by other 
 * components during runtime operation.
 */
public abstract class AbstractTransformTask extends AbstractConfigurableComponent implements TransformTask {
  protected boolean haltOnError = true;
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
   * Determine if errors should cause the task processing to terminate.
   * 
   * @return true if the task is to generate an error (throw TaskException) 
   *         and exit when an error occurs, false the task will just exit 
   *         without setting the context to an error state and aborting the 
   *         transform process.
   */
  public boolean haltOnError() {
    return haltOnError;
  }




  /**
   * Control if the task should set the context to an error state, aborting the 
   * transformation run or simply exit the task and let the rest of the 
   * components run.
   * 
   * @param flag true to abort the transform on error, false to just exit the task
   */
  public void setHaltOnError(boolean flag) {
    this.haltOnError = flag;
  }




  /**
   *
   */
  @Override
  public void setConfiguration(Config cfg) throws ConfigurationException {
    super.setConfiguration(cfg);

    // If there is a halt on error flag, then set it, otherwise keep the 
    // default value of true    
    if (contains(ConfigTag.HALT_ON_ERROR)) {
      setHaltOnError(getBoolean(getConfiguration().getFieldIgnoreCase(ConfigTag.HALT_ON_ERROR).getName()));
    }

  }




  /**
   * All components are initialized through the {@code open(TransformContext)} method.
   *
   * <p>A last chack of the configuration parameters should be performed here
   * to make sure there are no issues or conflicts with other settings. For
   * example, setting that were present during configuration may have changed
   * at the time of initialization. If there are any issues during
   * initialization, simply place an error in the Transform context:
   * {@code context.setError("Initialization error");}</p>
   *
   * <p>Once all components have been initialized, the engine will start
   * running, unless there is an error in the Transform context.</p>
   *
   * @param context The transform context all components share.
   */
  public void open(TransformContext context) {
    setContext(context);
    evaluator.setContext(context);

    if (StringUtil.isNotBlank(getCondition())) {
      try {
        evaluator.evaluateBoolean(getCondition());
      } catch (final IllegalArgumentException e) {
        context.setError("Invalid boolean expression in task: " + e.getMessage());
      }
    }
  }




  /**
   * Subclasses should probably override {@link #performTask()} instead of 
   * this method enabling this class to handle conditional checks.
   */
  @Override
  public void execute() throws TaskException {

    if (isEnabled()) {
      if (getCondition() != null) {
        try {
          if (evaluator.evaluateBoolean(getCondition())) {
            performTask();
          } else {
            if (Log.isLogging(Log.DEBUG_EVENTS)) {
              Log.debug(String.format( "Task.boolean_evaluation_false %s", getCondition()));
            }
          }
        } catch (final IllegalArgumentException e) {
          Log.error(String.format( "Task.boolean_evaluation_error %s %s", getCondition(), e.getMessage()));
        }
      } else {
        performTask();
      }
    }
  }




  /**
   * This method is called by the AbstractTransformTask when the 
   * {@link #execute()} method is called but only if the enabled flag is set 
   * and any set conditional expression is matched or there is no conditional 
   * expression.
   * 
   * <p>Overriding this task instead of {@link #execute()} allows the 
   * AbstractTransfromTask to handle all checks in a uniform manner for all 
   * subclasses.
   * 
   * @throws TaskException if there were problems performing the task
   */
  protected void performTask() throws TaskException {
    // do nothing method
  }




  /**
   * Close this task.
   * 
   * @see java.io.Closeable#close()
   */
  public void close() throws IOException {
    // subclass should override this to perform clean-up
  }

}
