/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;


import coyote.commons.dataframe.DataFrame;
import coyote.commons.rtw.FrameReader;
import coyote.commons.rtw.FrameValidator;
import coyote.commons.rtw.FrameWriter;
import coyote.commons.rtw.context.ContextListener;
import coyote.commons.rtw.context.OperationalContext;
import coyote.commons.rtw.context.TransactionContext;
import coyote.commons.rtw.context.TransformContext;

/**
 * 
 */
public abstract class AbstractDatabaseFixtureTask extends AbstractTransformTask implements ContextListener {

  /**
   *
   */
  @Override
  public void open(TransformContext context) {
    super.open(context);
    context.addListener(this);
  }




  /**
   *
   */
  @Override
  public void onEnd(OperationalContext context) {
    // no-op
  }




  /**
   *
   */
  @Override
  public void onStart(OperationalContext context) {
    // no-op
  }




  /**
   *
   */
  @Override
  public void onWrite(TransactionContext context, FrameWriter writer) {
    // no-op
  }




  /**
   *
   */
  @Override
  public void onRead(TransactionContext context, FrameReader reader) {
    // no-op
  }




  /**
   *
   */
  @Override
  public void onError(OperationalContext context) {
    // no-op
  }




  /**
   *
   */
  @Override
  public void onValidationFailed(OperationalContext context, FrameValidator validator, String msg) {
    // no-op
  }




  /**
   *
   */
  @Override
  public void onFrameValidationFailed(TransactionContext context) {
    // no-op
  }




  /**
   *
   */
  @Override
  public void onMap(TransactionContext txnContext) {
    // no-op
  }




  /**
   *
   */
  @Override
  public void preload(DataFrame frame) {
    //  not needed
  }

}
