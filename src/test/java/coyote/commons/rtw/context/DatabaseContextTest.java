/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.context;


import coyote.commons.cfg.Config;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.ConsoleAppender;
import coyote.commons.log.Log;
import coyote.commons.rtw.DefaultTransformEngine;
import coyote.commons.rtw.Symbols;
import coyote.commons.rtw.TransformEngine;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 
 */
public class DatabaseContextTest extends AbstractContextTest {
  private static final String CATALOG = "context";
  private static final String JDBC_DRIVER = "org.h2.Driver";
  private static final String DB_URL = "jdbc:h2:./"+CATALOG;
  private static final String LIBRARY_LOC = "jar:file:.src/resources/demojars/h2-1.4.196.jar!/";
  private static final String USER = "username";
  private static final String PASS = "password";
  

  /**
   * @throws java.lang.Exception
   */
  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    Log.addLogger(Log.DEFAULT_LOGGER_NAME, new ConsoleAppender(Log.TRACE_EVENTS | Log.DEBUG_EVENTS | Log.INFO_EVENTS | Log.WARN_EVENTS | Log.ERROR_EVENTS | Log.FATAL_EVENTS));
  }




  /**
   * @throws Exception
   */
 @AfterAll
  public static void tearDownAfterClass() throws Exception {
    File dbfile = new File(CATALOG+".mv.db");
    Log.debug(dbfile.getAbsolutePath());
    dbfile.delete();
    dbfile = new File(CATALOG+".trace.db");
    if (dbfile.exists()) {
      Log.debug(dbfile.getAbsolutePath());
      dbfile.delete();
    }
  }




  public void contextWithLibraryAttribute() {
    String jobName = "ContextTest";

    DataFrame config = new DataFrame() //
        .set("class", "DatabaseContext") //
        .set("target", DB_URL) //
        .set("autocreate", true) //
        .set("library", LIBRARY_LOC) //
        .set("driver", JDBC_DRIVER) //
        .set("username", USER) //
        .set("password", PASS) //
        .set("fields",
            new DataFrame() //
                .set("SomeKey", "SomeValue") //
                .set("AnotherKey", "AnotherValue") //
    );

    TransformEngine engine = new DefaultTransformEngine();
    engine.setName(jobName);
    TransformContext context = new DatabaseContext();
    context.setConfiguration(new Config(config));
    engine.setContext(context);

    turnOver(engine);

    Object obj = context.get(Symbols.RUN_COUNT);
    assertTrue(obj instanceof Long);
    long runcount = (Long)obj;
    assertTrue(runcount > 0);

    turnOver(engine);

    obj = context.get(Symbols.RUN_COUNT);
    assertTrue(obj instanceof Long);
    long nextRunCount = (Long)obj;
    assertEquals( nextRunCount,runcount + 1);

    // Replace the context with a new one to test reading from database
    context = new DatabaseContext();
    context.setConfiguration(new Config(config));
    engine.setContext(context);

    turnOver(engine);

    obj = context.get(Symbols.RUN_COUNT);
    assertTrue(obj instanceof Long);
    long lastRunCount = (Long)obj;
    assertEquals(nextRunCount + 1, lastRunCount);
    
    context.close();
  }




  public void msqltests() {
    String jobName = "ContextTest";

    DataFrame config = new DataFrame() //
        .set("class", "DatabaseContext") //
        .set("target", "jdbc:sqlserver://coyote.database.windows.net:1433;database=coyotedx") //
        .set("autocreate", true) //
        .set("library", "jar:file:src/resources/demojars/sqljdbc42.jar!/") //
        .set("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver") //
        .set("identity", "818E9553-4525-582D-AAD1-3DCAABDA98F918E955") //
        .set("ENC:username", "12345v067dPZRCsUdG/B4FsyrmPUM1WsVrQY8szJIetIJE3TBbjmBQ==") //
        .set("ENC:password", "k004712345b0xaR3tlZcWkQKlyFNmIGISCRN0wW45gU=") //
        .set("fields",
            new DataFrame() //
                .set("SomeKey", "SomeValue") //
                .set("AnotherKey", "AnotherValue") //
    );

    TransformEngine engine = new DefaultTransformEngine();
    engine.setName(jobName);
    TransformContext context = new DatabaseContext();
    context.setConfiguration(new Config(config));
    engine.setContext(context);

    turnOver(engine);

    Object obj = context.get(Symbols.RUN_COUNT);
    assertTrue(obj instanceof Long);
    long runcount = (Long)obj;
    assertTrue(runcount > 0);

    turnOver(engine);

    obj = context.get(Symbols.RUN_COUNT);
    assertTrue(obj instanceof Long);
    long nextRunCount = (Long)obj;
    assertEquals(runcount + 1, nextRunCount);

    // Replace the context with a new one to test reading from database
    context = new DatabaseContext();
    context.setConfiguration(new Config(config));
    engine.setContext(context);

    turnOver(engine);

    obj = context.get(Symbols.RUN_COUNT);
    assertTrue(obj instanceof Long);
    long lastRunCount = (Long)obj;
    assertEquals(nextRunCount + 1, lastRunCount);
  }

}
