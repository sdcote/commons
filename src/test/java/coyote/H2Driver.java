/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote;


import coyote.commons.log.ConsoleAppender;
import coyote.commons.log.Log;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * 
 */
public class H2Driver {
  private static final String CATALOG = "h2test";




  /**
   *
   */
  @BeforeAll
  static void setUpBeforeClass() {
    Log.addLogger(Log.DEFAULT_LOGGER_NAME, new ConsoleAppender(Log.INFO_EVENTS | Log.WARN_EVENTS | Log.ERROR_EVENTS | Log.FATAL_EVENTS));
  }




  /**
   *
   */
  @AfterAll
  static void tearDownAfterClass() {
    File dbfile = new File(CATALOG + ".mv.db");
    dbfile.delete();
    dbfile = new File(CATALOG + ".trace.db");
    if (dbfile.exists()) {
      dbfile.delete();
    }
  }




  @Test
  public void h2OnClassPath() {
    try {
      Class.forName("org.h2.jdbcx.JdbcDataSource");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      fail("Where is your H2 JDBC Driver? It should be on the class path.");
    }
    Connection connection = null;
    try {
      connection = DriverManager.getConnection("jdbc:h2:./" + CATALOG + ";MODE=Oracle", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
      fail("Connection Failed! " + e.getClass().getName() + " - " + e.getMessage());
    }

    assertNotNull(connection, "Failed to make connection!");
  }

}
