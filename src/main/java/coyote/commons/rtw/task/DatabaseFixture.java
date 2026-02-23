/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;

import coyote.commons.StringUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TaskException;
import coyote.commons.rtw.context.OperationalContext;
import coyote.commons.rtw.context.TransformContext;
import coyote.commons.rtw.db.DefaultDatabaseFixture;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * Create a database fixture in the Transform context.
 *
 * <p>This fixture is accessible by all components in the transform context so
 * it is possible for all components to share a datasource which makes it
 * simpler to specify a database in one location in contrast with copying a
 * configuration across multiple sections.
 *
 * <p>Additionally, this task enables the pooling of connections in a fixture
 * by allowing different fixture classes to be created and placed in the
 * transform context.
 */
public class DatabaseFixture extends AbstractDatabaseFixtureTask {
    private static final String DEFAULT_NAME = "Default";
    private DatabaseFixture fixture = null;
    private String fixtureName = null;


    /**
     *
     */
    @Override
    public void setConfiguration(Config cfg) throws ConfigurationException {
        super.setConfiguration(cfg);

        Log.info("Fixture configuration is valid");
    }


    /**
     *
     */
    @Override
    public void open(TransformContext context) {
        super.open(context);
        Log.info("Opening database fixture");

        Config cfg = getConfiguration();

        // Determine which fixture to load
        String className = cfg.getString(ConfigTag.CLASS);
        if (StringUtil.isBlank(className)) {
            className = DefaultDatabaseFixture.class.getName();
        } else if (StringUtil.countOccurrencesOf(className, ".") < 1) {
            className = DefaultDatabaseFixture.class.getPackage().getName() + "." + className;
        }
        cfg.put(ConfigTag.CLASS, className);

        // Make sure there is a name
        fixtureName = cfg.getString(ConfigTag.NAME);
        if (StringUtil.isBlank(fixtureName)) {
            fixtureName = DEFAULT_NAME;
            Log.notice("Unnamed database fixture will be bound to the default name of '" + fixtureName + "'");
        }

        // Now create an instance of the fixture and configure it
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getConstructor();
            Object object = ctor.newInstance();

            if (object instanceof DatabaseFixture) {
                try {
                    fixture = (DatabaseFixture) object;
                    fixture.setConfiguration(cfg);
                    Log.debug("Created database fixture");
                } catch (Exception e) {
                    Log.error("Could not configure database fixture");
                }
            } else {
                Log.warn("Class did not specify a database fixture");
            }
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException |
                 IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Log.error("Could not create an instance of database fixture: " + e.getClass().getName() + " - " + e.getMessage());
        }

    }


    /**
     *
     */
    @Override
    protected void performTask() throws TaskException {
        // create an instance of the fixture and place it in the context
        Log.info("Setting fixture in context");
        if (fixture != null) {
            getContext().set(fixtureName, fixture);
        } else {
            throw new TaskException("Could not create fixture");
        }
    }


    /**
     *
     */
    @Override
    public void onEnd(OperationalContext context) {
        if (context instanceof TransformContext) {
            Log.info("Closing database fixture");
        }
    }

}
