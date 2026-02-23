/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;


import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.JSONMarshaler;
import coyote.commons.log.ConsoleAppender;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * This tests the task which places a database fixture in the transform 
 * context for other components to share.
 */
public class DatabaseFixtureTaskTest {

  /**
   * @throws java.lang.Exception
   */
  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    Log.addLogger(Log.DEFAULT_LOGGER_NAME, new ConsoleAppender(Log.TRACE_EVENTS | Log.DEBUG_EVENTS | Log.INFO_EVENTS | Log.WARN_EVENTS | Log.ERROR_EVENTS | Log.FATAL_EVENTS));
  }




  /**
   * @throws java.lang.Exception
   */
 @AfterAll
  public static void tearDownAfterClass() throws Exception {
    File dbfile = new File("demodb.mv.db");
    System.out.println(dbfile.getAbsolutePath());
    dbfile.delete();
  }




  @Test
  public void basic() throws ConfigurationException {
    String fixtureName = "Default";
    // Create a Job
    DataFrame jobFrame = new DataFrame().set(ConfigTag.NAME, "test") //
        .set(ConfigTag.READER, // 
            new DataFrame().set(ConfigTag.CLASS, "DatabaseFixtureCheck") // the thing which will check our fixture
                .set(ConfigTag.SOURCE, fixtureName) // name of the fixture to check
        ).set(ConfigTag.TASK, // create a task section
            new DataFrame().set("DatabaseFixture", // add a task, DatabaseFixture
                new DataFrame().set(ConfigTag.CLASS, "coyote.commons.db.DefaultDatabaseFixture") // static fixture
                    .set(ConfigTag.NAME, fixtureName) // name of the fixture for look-up
                    .set(ConfigTag.TARGET, "jdbc:h2:./demodb") //
                    .set(ConfigTag.DRIVER, "org.h2.jdbcx.JdbcDataSource") //
                    .set(ConfigTag.AUTO_CREATE, true) //
                    .set(ConfigTag.USERNAME, "sa") //
                    .set(ConfigTag.PASSWORD, "")) //
    );

    Config configuration = new Config();
    configuration.add(ConfigTag.JOB, jobFrame);

    Log.info(JSONMarshaler.toFormattedString(configuration));


  }




  @Test
  public void partialClass() throws ConfigurationException {
    String fixtureName = "Default";
    // Create a Job
    DataFrame jobFrame = new DataFrame().set(ConfigTag.NAME, "test") //
        .set(ConfigTag.READER, // 
            new DataFrame().set(ConfigTag.CLASS, "DatabaseFixtureCheck") //
                .set(ConfigTag.SOURCE, fixtureName) // name of the fixture to check
        ).set(ConfigTag.TASK, // create a task section
            new DataFrame().set("DatabaseFixture", // add a task
                new DataFrame().set(ConfigTag.CLASS, "DefaultDatabaseFixture") // static fixture
                    .set(ConfigTag.NAME, fixtureName) // name of the fixture for look-up
                    .set(ConfigTag.TARGET, "jdbc:h2:./demodb;MODE=Oracle") //
                    .set(ConfigTag.TABLE, "user") //
                    .set(ConfigTag.AUTO_CREATE, true) //
                    .set(ConfigTag.DRIVER, "org.h2.jdbcx.JdbcDataSource") //
                    .set(ConfigTag.USERNAME, "sa") //
                    .set(ConfigTag.PASSWORD, "")) //
    );

    Config configuration = new Config();
    configuration.add(ConfigTag.JOB, jobFrame);

    Log.info(JSONMarshaler.toFormattedString(configuration));


  }




  @Test
  public void noName() throws ConfigurationException {
    String fixtureName = "Default";
    // Create a Job
    DataFrame jobFrame = new DataFrame().set(ConfigTag.NAME, "test") //
        .set(ConfigTag.READER, // 
            new DataFrame().set(ConfigTag.CLASS, "DatabaseFixtureCheck") //
                .set(ConfigTag.SOURCE, fixtureName) // name of the fixture to check
        ).set(ConfigTag.TASK, // create a task section
            new DataFrame().set("DatabaseFixture", // add a task
                new DataFrame().set(ConfigTag.CLASS, "coyote.commons.db.DefaultDatabaseFixture") // static fixture
                    .set(ConfigTag.TARGET, "jdbc:h2:./demodb;MODE=Oracle") //
                    .set(ConfigTag.TABLE, "user") //
                    .set(ConfigTag.AUTO_CREATE, true) //
                    .set(ConfigTag.DRIVER, "org.h2.jdbcx.JdbcDataSource") //
                    .set(ConfigTag.USERNAME, "sa") //
                    .set(ConfigTag.PASSWORD, "")) //
    );

    Config configuration = new Config();
    configuration.add(ConfigTag.JOB, jobFrame);

    Log.info(JSONMarshaler.toFormattedString(configuration));


  }




  @Test
  public void noClass() throws ConfigurationException {
    String fixtureName = "Default";

    // Create a Job
    DataFrame jobFrame = new DataFrame().set(ConfigTag.NAME, "test") //
        .set(ConfigTag.READER, // 
            new DataFrame().set(ConfigTag.CLASS, "DatabaseFixtureCheck") //
                .set(ConfigTag.SOURCE, fixtureName) // name of the fixture to check
        ).set(ConfigTag.TASK, // create a task section
            new DataFrame().set("DatabaseFixture", // add a task
                new DataFrame().set(ConfigTag.TARGET, "jdbc:h2:./demodb;MODE=Oracle") //
                    .set(ConfigTag.TABLE, "user") //
                    .set(ConfigTag.AUTO_CREATE, true) //
                    .set(ConfigTag.DRIVER, "org.h2.jdbcx.JdbcDataSource") //
                    .set(ConfigTag.USERNAME, "sa") //
                    .set(ConfigTag.PASSWORD, "")) //
    );

    Config configuration = new Config();
    configuration.add(ConfigTag.JOB, jobFrame);

    Log.info(JSONMarshaler.toFormattedString(configuration));


  }




  public void h2Connection() throws ConfigurationException {
    String fixtureName = "Default";

    // Create a Job
    DataFrame jobFrame = new DataFrame().set(ConfigTag.NAME, "test") //
        .set(ConfigTag.READER, // 
            new DataFrame().set(ConfigTag.CLASS, "DatabaseFixtureCheck") //
                .set(ConfigTag.SOURCE, fixtureName) // name of the fixture to check
                .set("Connect", true) // name of the fixture to check
        ).set(ConfigTag.TASK, // create a task section
            new DataFrame().set("DatabaseFixture", // add a task
                new DataFrame().set(ConfigTag.TARGET, "jdbc:h2:./demodb") //
                    .set(ConfigTag.TABLE, "user") //
                    .set(ConfigTag.AUTO_CREATE, true) //
                    .set(ConfigTag.DRIVER, "org.h2.Driver") //
                    .set(ConfigTag.LIBRARY, "jar:file:src/resources/demojars/h2-1.4.196.jar!/") //
                    .set(ConfigTag.USERNAME, "sa") //
                    .set(ConfigTag.PASSWORD, "")) //
    );

    Config configuration = new Config();
    configuration.add(ConfigTag.JOB, jobFrame);

    Log.info(JSONMarshaler.toFormattedString(configuration));


  }

}
