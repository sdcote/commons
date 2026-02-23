/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;

import coyote.commons.StringUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.dataframe.DataFrameException;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.RTW;
import coyote.commons.rtw.TaskException;
import coyote.commons.rtw.TransformTask;
import coyote.commons.rtw.context.TransformContext;
import coyote.commons.rtw.db.Database;
import coyote.commons.rtw.db.DatabaseConnector;
import coyote.commons.template.Template;


/**
 * This runs a set of SQL commands from a file against a database connection.
 *
 * <p>With this task, it is possible to execute a SQL script against a JDBC
 * connection to perform complex database processing. The goal is to fully
 * customize a relational database for subsequent use by other components in
 * the job.
 */
public class RunSql extends AbstractTransformTask implements TransformTask {

  /** The thing we use to get connections to the database */
  private DatabaseConnector connector = null;

  /** The JDBC connection used by this writer to interact with the database */
  private Connection connection;

  /** The number of records we should batch before executing an UPDATE */
  private int batchsize = 0;

  /** Holds the current state of the command being built. */
  private final StringBuffer buffer = new StringBuffer();

  private BufferedReader bufferedReader = null;
  private int linePointer = 0;
  private boolean hasNext = true;
  private int lineToSkip = 0;
  private boolean linesSkipped = false;




  /**
   *
   */
  @Override
  public void close() throws IOException {
    closeQuietly(bufferedReader);
    super.close();
  }




  /**
   * Close the given statement and consume any thrown exceptions.
   *
   * @param reader the resource to close.
   */
  private void closeQuietly(final Reader reader) {
    if (reader != null) {
      try {
        reader.close();
      } catch (final Exception ignore) {
        // no exceptions
      }
    }
  }




  public int getBatchSize() {
    try {
      return configuration.getAsInt(ConfigTag.BATCH);
    } catch (final DataFrameException ignore) {}
    return 0;
  }




  /**
   * Return the connector we use for
   * @return the connector
   */
  public DatabaseConnector getConnector() {
    return connector;
  }




  /**
   * Reads the next line from the file.
   *
   * @return the next line from the file without trailing newline
   *
   * @throws IOException if the next line could not be read
   */
  private String getNextLine() throws IOException {
    if (!linesSkipped) {
      for (int i = 0; i < lineToSkip; i++) {
        bufferedReader.readLine();
        linePointer++;
      }
      linesSkipped = true;
    }
    final String nextLine = bufferedReader.readLine();
    linePointer++;
    if (nextLine == null) {
      hasNext = false;
    }

    return hasNext ? nextLine : null;
  }




  public String getSource() {
    if (configuration.containsIgnoreCase(ConfigTag.SOURCE)) {
      return configuration.getString(ConfigTag.SOURCE);
    }
    return null;
  }




  /**
   *
   */
  @Override
  public void open(final TransformContext context) {
    super.open(context);

    File sourceFile = new File(getSource());
    if (!sourceFile.isAbsolute()) {
      sourceFile = RTW.resolveFile(sourceFile, getContext());
    }
    Log.debug("Using an absolute source file of " + sourceFile.getAbsolutePath());

    if (sourceFile.exists() && sourceFile.canRead()) {
      try {
        bufferedReader = new BufferedReader(new FileReader(sourceFile));

      } catch (final Exception e) {
        Log.error("Could not create reader: " + e.getMessage());
        context.setError(e.getMessage());
      }
    } else {
      context.setError(String.format("Reader.could_not_read_from_source", getClass().getName(), sourceFile.getAbsolutePath()).toString());
    }

    // If we don't have a connection, prepare to create one
    if (connection == null) {

      // Look for a database connector in the context bound with the name specified in the TARGET attribute
      String target = getConfiguration().getString(ConfigTag.TARGET);
      target = Template.preProcess(target, context.getSymbols());
      final Object obj = getContext().get(target);
      if (obj != null && obj instanceof DatabaseConnector) {
        setConnector((DatabaseConnector)obj);
        Log.debug("Using database connector found in context bound to '" + target + "'");
      }

      if (getConnector() == null) {
        // we have to create a Database based on our configuration
        final Database database = new Database();
        final Config cfg = new Config();

        if (StringUtil.isNotBlank(getString(ConfigTag.TARGET))) {
          cfg.put(ConfigTag.TARGET, getString(ConfigTag.TARGET));
        }

        if (StringUtil.isNotBlank(getString(ConfigTag.DRIVER))) {
          cfg.put(ConfigTag.DRIVER, getString(ConfigTag.DRIVER));
        }

        if (StringUtil.isNotBlank(getString(ConfigTag.LIBRARY))) {
          cfg.put(ConfigTag.LIBRARY, getString(ConfigTag.LIBRARY));
        }

        if (StringUtil.isNotBlank(getString(ConfigTag.USERNAME))) {
          cfg.put(ConfigTag.USERNAME, getString(ConfigTag.USERNAME));
        }

        if (StringUtil.isNotBlank(getString(ConfigTag.PASSWORD))) {
          cfg.put(ConfigTag.PASSWORD, getString(ConfigTag.PASSWORD));
        }

        setConnector(database);

        try {
          database.setConfiguration(cfg);
          if (Log.isLogging(Log.DEBUG_EVENTS)) {
            Log.debug(String.format("Component.using_target", getClass().getSimpleName(), database.getTarget()));
            Log.debug(String.format("Component.using_driver", getClass().getSimpleName(), database.getDriver()));
            Log.debug(String.format("Component.using_library", getClass().getSimpleName(), database.getLibrary()));
            Log.debug(String.format("Component.using_user", getClass().getSimpleName(), database.getUserName()));
            Log.debug(String.format("Component.using_password", getClass().getSimpleName(), StringUtil.isBlank(database.getPassword()) ? 0 : database.getPassword().length()));
          }
        } catch (final ConfigurationException e) {
          context.setError("Could not configure database connector: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
      }
    } else {
      Log.debug(String.format("Component.using_existing_connection", getClass().getSimpleName()));
    }

    setBatchSize(getInteger(ConfigTag.BATCH));
    Log.debug(String.format("Component.using_batch_size", getClass().getSimpleName(), getBatchSize()));

    // validate and cache our batch size
    if (getBatchSize() < 1) {
      batchsize = 0;
    } else {
      batchsize = getBatchSize();
    }
  }




  /**
   *
   */
  @Override
  protected void performTask() throws TaskException {

    do {
      try {
        String nextLine = getNextLine();
        if (StringUtil.isNotBlank(nextLine)) {
          nextLine = nextLine.trim();

          // Handle Multi-line comments /* this is commented out */
          // Handle EOL comments  -- this is commented out
          // Use StringParser to parse comments out of the file
          // SqlReader extends StringParser
          // parser.getNextCommand()

          int delimiter = nextLine.indexOf(';');
          if (delimiter >= 0) {
            buffer.append(' ');
            buffer.append(nextLine.substring(0, delimiter + 1));
            processCommand(buffer.toString().trim(), linePointer);
            buffer.delete(0, buffer.length());
            buffer.append(nextLine.substring(delimiter + 1));
          } else {
            buffer.append(nextLine);
          }
        }
      } catch (final IOException e) {
        throw new TaskException("read error", e);
      }
    }
    while (hasNext);
  }




  private void processCommand(String command, int index) {
    System.out.println(index + ":" + command);
  }




  /**
   * @param value
   */
  public void setBatchSize(final int value) {
    batchsize = value;
    configuration.put(ConfigTag.BATCH, value);
  }




  /**
   *
   */
  @Override
  public void setConfiguration(final Config cfg) throws ConfigurationException {
    super.setConfiguration(cfg);

    if (StringUtil.isBlank(getSource())) {
      throw new ConfigurationException("Null, empty or blank argument for " + ConfigTag.SOURCE + " configuration parameter");
    }

  }




  /**
   * @param connector the connector to set
   */
  public void setConnector(final DatabaseConnector connector) {
    this.connector = connector;
  }




  /**
   * @param value
   */
  public void setSource(final String value) {
    configuration.put(ConfigTag.SOURCE, value);
  }

}
