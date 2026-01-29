/*
 * Copyright (c) 2018 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.aggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import coyote.commons.StringUtil;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.Log;
import coyote.commons.rtw.*;
import coyote.commons.rtw.context.TransactionContext;
import coyote.commons.rtw.context.TransformContext;


/**
 * Simple aggregator to emit frames in a sorted order.
 * 
 * <p>This is a naive approach and results in consuming twice the about of 
 * memory it should. The original list of frames are buffered and upon 
 * completion, will create a duplicate list in sorted order. This is not 
 * optimal and a more memory efficient approach should be developed. 
 * 
 * <p>A sample configuration is as follows:<pre>
 * "Aggregator": { "class": "Sort", "field":"Price", "sort":"decend" }</pre>
 * The above configuration sort all the frames in descending order by price.
 */
public class Sort extends AbstractFrameAggregator implements FrameAggregator {

  private RTW.Sort sort = RTW.Sort.NONE;
  private List<DataFrame> frameList = new ArrayList<>();




  /**
   *
   */
  @Override
  public void open(TransformContext context) {
    super.open(context);

    String sortMode = getString(ConfigTag.MODE);
    if (StringUtil.isNotBlank(sortMode)) {
      if (ConfigTag.ASCEND.equalsIgnoreCase(sortMode)) {
        sort = RTW.Sort.ASCEND;
      } else if (ConfigTag.DESCEND.equalsIgnoreCase(sortMode)) {
        sort = RTW.Sort.DESCEND;
      } else if (ConfigTag.ASCEND_CI.equalsIgnoreCase(sortMode)) {
        sort = RTW.Sort.ASCEND_CI;
      } else if (ConfigTag.DESCEND_CI.equalsIgnoreCase(sortMode)) {
        sort = RTW.Sort.DESCEND_CI;
      } else if (ConfigTag.NONE.equalsIgnoreCase(sortMode)) {
        sort = RTW.Sort.NONE;
      } else {
        Log.warn("Unrecognized sourt parameter '" + sortMode + "' - no sorting will occur");
        sort = RTW.Sort.NONE;
      }
    } else {
      sort = RTW.Sort.NONE;
    }

  }




  /**
   *
   */
  @Override
  protected List<DataFrame> aggregate(List<DataFrame> frames, TransactionContext txnContext) {
    List<DataFrame> retval = new ArrayList<>();

    for (int x = 0; x < frames.size(); x++) {
      frameList.add(frames.get(x));
    }

    if (txnContext.isLastFrame()) {
      retval = compileFrames();
    }

    return retval;
  }




  /**
   * @return
   */
  private List<DataFrame> compileFrames() {
    List<DataFrame> retval = frameList;

    String fieldName = getString(ConfigTag.FIELD);
    if (StringUtil.isNotBlank(fieldName) && sort != RTW.Sort.NONE) {
      retval = new ArrayList<>();
      // iterate through all the frames in the list and place their keys in a hash set
      Set<String> keys = new HashSet<>();
      for (int x = 0; x < frameList.size(); x++) {
        keys.add(frameList.get(x).getAsString(fieldName));
      }

      // Extract all the keys from the set into a list
      List<String> list = new ArrayList<String>(keys);

      // sort the list
      if (sort != RTW.Sort.NONE) {
        if (sort == RTW.Sort.ASCEND || sort == RTW.Sort.DESCEND) {
          Collections.sort(list);
        } else {
          Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        }
        if (sort == RTW.Sort.DESCEND || sort == RTW.Sort.DESCEND_CI) {
          Collections.reverse(list);
        }
      }

      for (int x = 0; x < list.size(); x++) {
        String value = list.get(x);
        for (int y = 0; y < frameList.size(); y++) {
          // if the frame has a field value which matches the current key, remove it from the list and add it to the return value
          if (value.equals(frameList.get(y).getAsString(fieldName))) {
            retval.add(frameList.get(y));
          }
        } // for each frame in the list
      } // for each key
    }
    return retval;
  }

}
