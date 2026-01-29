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
import coyote.commons.rtw.FrameAggregator;
import coyote.commons.rtw.context.TransactionContext;

import java.util.List;



/**
 * 
 */
public class DebugAggregator extends AbstractFrameAggregator implements FrameAggregator {

  /**
   * @param frames the frames to aggregate
   * @param txnContext the transaction context
   * @return aggregated frames
   */
  @Override
  protected List<DataFrame> aggregate(List<DataFrame> frames, TransactionContext txnContext) {
    Log.debug("Aggregating " + frames.size() + " frames. LastFrame=" + txnContext.isLastFrame());
    return frames;
  }

}
