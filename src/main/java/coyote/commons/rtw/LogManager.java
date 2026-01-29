/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.Log;
import coyote.commons.log.Logger;
import coyote.commons.rtw.context.TransformContext;
import coyote.commons.template.Template;


/**
 * This class sets up logging only after the engine is run and the context is
 * opened to enable use of the context values and templates in logging
 * configuration values.
 * <p>
 * TODO This code has been build incrementally and is in need of refactoring.
 */
public class LogManager extends AbstractConfigurableComponent implements ConfigurableComponent {

    /**
     * Constant to assist in determining the full class name of loggers
     */
    private static final String LOGGER_PKG = Log.class.getPackage().getName();

    /**
     * The list of logger names to add and remove loggers from the logging subsystem.
     */
    private final List<String> loggerNames = new ArrayList<>();

    private static Logger createLogger(Config cfg) {
        Logger retval = null;
        if (cfg != null) {
            if (cfg.contains(ConfigTag.CLASS)) {
                String className = cfg.getAsString(ConfigTag.CLASS);

                try {
                    Class<?> clazz = Class.forName(className);
                    Constructor<?> ctor = clazz.getConstructor();
                    Object object = ctor.newInstance();

                    if (object instanceof Logger) {
                        retval = (Logger) object;
                        try {
                            retval.setConfig(cfg);
                        } catch (Exception e) {
                            Log.error(String.format( "EngineFactory.could_not_configure_logger {%s} - {%s} : {%s}", object.getClass().getName(), e.getClass().getSimpleName(), e.getMessage()));
                        }
                    } else {
                        Log.warn(String.format( "EngineFactory.instance_is_not_a_logger of {%s} is not configurable", className));
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    Log.error(String.format( "EngineFactory.Could not instantiate {%s} reason: {%s} - {%s}", className, e.getClass().getName(), e.getMessage()));
                }
            } else {
                Log.error( "EngineFactory.Configuration frame did not contain a class name");
            }
        }

        return retval;
    }

    /**
     *
     */
    @Override
    public void setConfiguration(Config cfg) throws ConfigurationException {
        super.setConfiguration(cfg);

        // TODO we should probably perform some validation checks here to ensure logging operates as expected when the engine runs.
    }


    @Override
    public void open(TransformContext context) {
        setContext(context);

        if (getConfiguration() != null && getConfiguration().getFieldCount() > 0) {
            Log.debug(String.format( "LogManager.initializing"));

            // Find the loggers
            for (DataField field : getConfiguration().getFields()) {

                if (field.isFrame()) {
                    DataFrame cfgFrame = (DataFrame) field.getObjectValue();
                    if (StringUtil.isNotBlank(field.getName())) {
                        if (field.isFrame()) {
                            configLogger(field.getName(), cfgFrame);
                        } else {
                            System.err.println(String.format( "EngineFactory.invalid_logger_configuration_section"));
                        }
                    } else {
                        System.err.println(String.format( "EngineFactory.no_logger_classname", cfgFrame.toString()));
                    }
                } else {
                    System.err.println(String.format( "EngineFactory.invalid_logger_configuration_section"));
                }

            }

        } else {
            Log.debug(String.format( "LogManager.no_config"));
        }

    }


    /**
     * Go through our list of loggers and close each one. Then remove them from the logging subsystem.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        for (String name : loggerNames) {
            Logger logger = Log.getLogger(name);
            if (logger != null) {
                try {
                    logger.terminate();
                } catch (Exception t) {
                    System.err.println("Problems closing logger '" + name + "' - reason: " + t.getMessage());
                }
            }
            // finally, remove the logger
            Log.removeLogger(name);
        }
    }


    private void configLogger(String className, DataFrame frame) {
        // Make sure the class is fully qualified
        if (className != null && StringUtil.countOccurrencesOf(className, ".") < 1) {
            className = LOGGER_PKG + "." + className;
            frame.put(ConfigTag.CLASS, className);
        } else {
            System.err.println("No logger Class: " + frame.toString());
        }

        String loggerName = frame.getAsString(ConfigTag.NAME);

        // It is necessary to name loggers, but if not configured, generate a name
        if (StringUtil.isBlank(loggerName)) {
            loggerName = UUID.randomUUID().toString();
        }

        // All loggers must have a name
        if (StringUtil.isNotBlank(loggerName)) {

            Config cfg = new Config();

            // populate the logger config, replacing strings with template values
            for (DataField field : frame.getFields()) {
                if (field.getType() == DataField.STRING) {
                    String cval = getString(field.getStringValue());
                    if (StringUtil.isBlank(cval)) {
                        cval = Template.resolve(field.getStringValue(), getContext().getSymbols());
                    }

                    // treat targets a little differently
                    if (StringUtil.equalsIgnoreCase(ConfigTag.TARGET, field.getName())) {

                        // the targets for loggers MUST be a URI
                        if (!("stdout".equalsIgnoreCase(cval) || "stderr".equalsIgnoreCase(cval))) {
                            URI testTarget = UriUtil.parse(cval);

                            if (testTarget != null) {
                                if (testTarget.getScheme() == null) {
                                    cval = "file://" + cval;
                                }
                            } else {
                                File file = new File(cval);
                                URI fileUri = FileUtil.getFileURI(file);
                                if (fileUri != null) {
                                    cval = fileUri.toString();
                                }
                            }

                            URI testUri = UriUtil.parse(cval);
                            if (testUri != null) {
                                if (UriUtil.isFile(testUri)) {
                                    File logfile = UriUtil.getFile(testUri);

                                    // make it absolute to our job directory
                                    if (!logfile.isAbsolute()) {
                                        String path = getJobDirectory().getPath();
                                        File logdir = new File(path);
                                        logfile = new File(logdir, logfile.getPath());
                                        testUri = FileUtil.getFileURI(logfile);
                                        cval = testUri.toString();
                                    }
                                }

                            } else {
                                System.out.println("Bad target URI '" + cval + "'");
                                System.exit(11);
                            }
                        }

                        // make sure the URI is valid
                        URI testUri = UriUtil.parse(cval);
                        if (testUri != null) {
                            if (UriUtil.isFile(testUri)) {
                                File logfile = UriUtil.getFile(testUri);

                                // make it absolute to our job directory
                                if (!logfile.isAbsolute()) {
                                    if (getContext() != null && getContext().getSymbols() != null) {
                                        logfile = new File(getJobDirectory(), logfile.getPath());
                                        testUri = FileUtil.getFileURI(logfile);
                                        cval = testUri.toString();
                                    }
                                }
                            }

                        } else {
                            System.out.println("Bad target URI '" + cval + "'");
                        }

                    } else if (StringUtil.equalsIgnoreCase(ConfigTag.CATEGORIES, field.getName())) {
                        // Categories should be normalized to upper case
                        cval = cval.toUpperCase();
                    }

                    cfg.add(field.getName(), cval);
                } else {
                    cfg.add(field);
                }

            }

            Logger logger = createLogger(cfg);

            if (logger != null) {
                try {
                    Log.addLogger(loggerName, logger);
                    loggerNames.add(loggerName);
                } catch (Exception e) {
                    System.out.println(String.format( "LogManager.Could not add configured logger", loggerName, logger.getClass(), e.getMessage()));
                }
            } else {
                System.err.println(String.format( "LogManager.Could not create an instance of the specified logger"));
            }
        }
    }

}
