/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.db;

import java.sql.Connection;

import coyote.commons.StringUtil;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.context.TransactionContext;
import coyote.commons.rtw.context.TransformContext;
import coyote.commons.rtw.db.DatabaseFixture;
import coyote.commons.rtw.reader.AbstractFrameReader;


/**
 *  
 */
public class DatabaseFixtureCheck extends AbstractFrameReader {
  private int counter = 0;
  private int limit = 1;




  /**
   * 
   */
  @Override
  public DataFrame read(TransactionContext context) {
    counter++;
    if (counter >= limit) {
      context.setLastFrame(true);
    }
    return runCheck();
  }




  private DataFrame runCheck() {
    DataFrame retval = new DataFrame();
    boolean tryConnection = false;
    try {
      tryConnection = getConfiguration().getBoolean("Connect");
    } catch (Exception ignore) {
      // no worries
    }

    String name = getConfiguration().getString(ConfigTag.SOURCE);
    if (StringUtil.isNotBlank(name)) {
      Object obj = getContext().get(name);
      if (obj != null) {
        if (obj instanceof DatabaseFixture) {
          DatabaseFixture fixture = (DatabaseFixture)obj;
          fixture.isPooled();
          if (tryConnection) {
            Connection conn = fixture.getConnection();
            if (conn == null) {
              getContext().setError("Connection failed");
            }
          }
        } else {
          getContext().setError("Found different object bound to the context as '" + name + "' - " + obj.getClass().getName());
        }
      } else {
        getContext().setError("Could not find the fixture bound to the context as '" + name + "'");
      }

    } else {
      Log.error("Was not configured with a name to search");
      getContext().setError("The unit test was not configured properly");
    }
    // do our checks here
    return retval;
  }




  /**
   *
   */
  @Override
  public boolean eof() {
    return counter >= limit;
  }




  /**
   *
   */
  @Override
  public void open(TransformContext context) {
    super.open(context);
    counter = 0;
    try {
      limit = configuration.getInt(ConfigTag.LIMIT);
    } catch (NumberFormatException e) {
      limit = 1;
    }

  }

}
