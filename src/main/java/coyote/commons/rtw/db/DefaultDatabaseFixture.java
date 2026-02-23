/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.db;

import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;

import java.sql.Connection;




/**
 * 
 */
public class DefaultDatabaseFixture implements DatabaseFixture {

  private Database database = new Database();




  /**
   *
   */
  @Override
  public Connection getConnection() {
    return database.getConnection();
  }




  /**
   *
   */
  @Override
  public boolean isPooled() {
    return false;
  }




  /**
   * @throws ConfigurationException 
   */
  @Override
  public void setConfiguration(Config cfg) throws ConfigurationException {
    database = new Database();
    database.setConfiguration(cfg);
  }




  /**
   *
   */
  @Override
  public String getUserName() {
    return database.getUserName();
  }

}
