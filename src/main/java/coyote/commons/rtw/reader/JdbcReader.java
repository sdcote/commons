/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.reader;

import coyote.commons.StringUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.jdbc.DatabaseDialect;
import coyote.commons.jdbc.DatabaseUtil;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.context.TransactionContext;
import coyote.commons.rtw.context.TransformContext;
import coyote.commons.rtw.db.Database;
import coyote.commons.rtw.db.DatabaseConnector;
import coyote.commons.template.Template;

import java.io.IOException;
import java.sql.*;

/**
 * This is a frame reader which uses a JDBC result set to create frames.
 */
public class JdbcReader extends AbstractFrameReader {

    /**
     * The JDBC connection used by this reader to interact with the database
     */
    protected Connection connection;

    /**
     * The thing we use to get connections to the database
     */
    private DatabaseConnector connector = null;

    private ResultSet result = null;
    private Statement statement = null;
    private volatile boolean EOF = true;
    private ResultSetMetaData rsmd = null;
    private int columnCount = 0;


    /**
     * 
     */
    @Override
    public void open(TransformContext context) {
        super.setContext(context);

        if (getConfiguration().containsIgnoreCase(ConfigTag.SOURCE)) {
            String source = getString(ConfigTag.SOURCE);
            Log.debug(String.format("%s configured source is %s", this.getClass().getSimpleName(), source));

            // If we don't have a connection, prepare to create one
            if (connection == null) {

                String target = getConfiguration().getString(ConfigTag.SOURCE);
                target = Template.preProcess(target, context.getSymbols());
                Object obj = getContext().get(target);
                if (obj != null && obj instanceof DatabaseConnector) {
                    setConnector((DatabaseConnector) obj);
                    Log.debug(String.format("%s found connector in context %s", this.getClass().getSimpleName(), target));
                }

                if (getConnector() == null) {
                    // we have to create a Database based on our configuration
                    Database database = new Database();
                    Config cfg = new Config();

                    if (StringUtil.isNotBlank(getString(ConfigTag.SOURCE)))
                        cfg.put(ConfigTag.TARGET, getString(ConfigTag.SOURCE)); // connection target

                    if (StringUtil.isNotBlank(getString(ConfigTag.DRIVER)))
                        cfg.put(ConfigTag.DRIVER, getString(ConfigTag.DRIVER));

                    if (StringUtil.isNotBlank(getString(ConfigTag.LIBRARY)))
                        cfg.put(ConfigTag.LIBRARY, getString(ConfigTag.LIBRARY));

                    if (StringUtil.isNotBlank(getString(ConfigTag.USERNAME)))
                        cfg.put(ConfigTag.USERNAME, getString(ConfigTag.USERNAME));

                    if (StringUtil.isNotBlank(getString(ConfigTag.PASSWORD)))
                        cfg.put(ConfigTag.PASSWORD, getString(ConfigTag.PASSWORD));

                    setConnector(database);

                    try {
                        database.setConfiguration(cfg);
                        if (Log.isLogging(Log.DEBUG_EVENTS)) {
                            Log.debug(String.format("%s using target %s", getClass().getSimpleName(), database.getTarget()));
                            Log.debug(String.format("%s using driver %s", getClass().getSimpleName(), database.getDriver()));
                            Log.debug(String.format("%s using library %s", getClass().getSimpleName(), database.getLibrary()));
                            Log.debug(String.format("%s using user %s", getClass().getSimpleName(), database.getUserName()));
                            Log.debug(String.format("%s using password %s", getClass().getSimpleName(), StringUtil.isBlank(database.getPassword()) ? 0 : database.getPassword().length()));
                        }
                    } catch (ConfigurationException e) {
                        String msg = String.format("%s could not configure database - %s", getClass().getSimpleName(), e.getMessage()).toString();
                        Log.error(msg, e);
                        context.setError(msg);
                    }

                    // if there is no schema in the configuration, set it to the same as the username
                    if (StringUtil.isBlank(getString(ConfigTag.SCHEMA))) {
                        getConfiguration().set(ConfigTag.SCHEMA, database.getUserName());
                    }
                }
            } else {
                Log.debug(String.format("%s using existing connection", getClass().getSimpleName()));
                // What if that connection is closed or otherwise broken?
                // Disconnect and allow getConnection to reconnect.
                try {
                    if (!connection.isValid(15)) {
                        disconnect();
                    }
                } catch (SQLException e) {
                    disconnect();
                }

            }

            getConnection();

            if (connection != null) {
                String query = getString(ConfigTag.QUERY);
                Log.debug(String.format("%s using query %s", this.getClass().getSimpleName(), query));

                try {
                    statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    result = statement.executeQuery(query);
                    rsmd = result.getMetaData();
                    columnCount = rsmd.getColumnCount();

                    if (result.isBeforeFirst()) {
                        EOF = false;
                    }
                } catch (SQLException e) {
                    String msg = String.format("%s error quering database: %s%n%s", getClass().getSimpleName(), e.getMessage().trim(), query);
                    context.setError(msg);
                }
            } else {
                String msg = String.format("%s could not connect to source %s", getClass().getSimpleName(), getSource());
                Log.error(msg);
                context.setError(msg);
            }
        } else {
            String msg = String.format("%s no source specified", getClass().getSimpleName());
            Log.error(msg);
            context.setError(msg);
        }
    }


    /**
     *
     */
    @Override
    public DataFrame read(TransactionContext context) {
        DataFrame retval = null;

        if (result != null) {
            try {
                if (result.next()) {
                    retval = new DataFrame();

                    if (result.isLast()) {
                        EOF = true;
                        context.setLastFrame(true);
                    }

                    for (int i = 1; i <= columnCount; i++) {
                        retval.add(rsmd.getColumnName(i), DatabaseDialect.resolveValue(result.getObject(i), rsmd.getColumnType(i)));
                    }

                    context.setLastFrame(result.isLast());
                } else {
                    Log.error("Read past EOF");
                    EOF = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                EOF = true;
            }
        } else {
            EOF = true;
        }

        return retval;
    }


    /**
     *
     */
    @Override
    public boolean eof() {
        return EOF;
    }


    /**
     *
     */
    @Override
    public void close() throws IOException {
        DatabaseUtil.closeQuietly(result);
        DatabaseUtil.closeQuietly(connection);
        super.close();
    }


    /**
     * @return the connector we use for creating connections.
     */
    public DatabaseConnector getConnector() {
        return connector;
    }


    /**
     * @param connector the connector to set
     */
    public void setConnector(DatabaseConnector connector) {
        this.connector = connector;
    }


    /**
     * Use the currently set connector to create a connection.
     */
    private void getConnection() {
        if (connection == null) {
            if (getConnector() == null) {
                Log.fatal(String.format("%s No connector", getClass().getSimpleName()));
            }
            connection = getConnector().getConnection();
            if (Log.isLogging(Log.DEBUG_EVENTS) && connection != null) {
                Log.debug(String.format("%s connected to %s", getClass().getSimpleName(), getSource()));
            }
        }
    }


    /**
     * Quietly close the connection and set the connection reference to null.
     */
    private void disconnect() {
        if (connection != null) {
            DatabaseUtil.closeQuietly(connection);
            connection = null;
        }
    }

}