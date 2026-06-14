package coyote.commons.snap;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.commons.cfg.Config;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.Log;
import coyote.commons.log.Logger;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.template.SymbolTable;
import coyote.commons.template.Template;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Utility class to configure logging for a job.
 */
public class LoggingConfigurator {

    private static final String LOGGER_PKG = "coyote.commons.log";

    public static void configure(Config configuration, SymbolTable symbols, String appHome) {
        List<Config> loggers = configuration.getSections(ConfigTag.LOGGING);

        if (loggers.size() > 0) {
            Log.removeAllLoggers();
        }

        for (Config cfg : loggers) {
            for (DataField field : cfg.getFields()) {
                if (field.isFrame()) {
                    DataFrame cfgFrame = (DataFrame) field.getObjectValue();
                    String className = field.getName();

                    if (StringUtil.isBlank(className) && cfgFrame.getFieldCount() > 0) {
                        DataField firstField = cfgFrame.getField(0);
                        if (firstField.isFrame()) {
                            className = firstField.getName();
                            cfgFrame = (DataFrame) firstField.getObjectValue();
                        }
                    }

                    if (StringUtil.isNotBlank(className)) {
                        Config loggerConfiguration = new Config();
                        if (className.indexOf('.') < 1) {
                            className = LOGGER_PKG + "." + className;
                        }
                        loggerConfiguration.put(ConfigTag.CLASS, className);

                        for (DataField lfield : cfgFrame.getFields()) {
                            if (ConfigTag.TARGET.equalsIgnoreCase(lfield.getName())) {
                                String cval = lfield.getStringValue();
                                if (StringUtil.isNotEmpty(cval)) {
                                    cval = Template.preProcess(cval, symbols);
                                }

                                if (!("stdout".equalsIgnoreCase(cval) || "stderr".equalsIgnoreCase(cval))) {
                                    URI testTarget = UriUtil.parse(cval);
                                    if (testTarget == null || testTarget.getScheme() == null) {
                                        if (testTarget == null) {
                                            File file = new File(cval);
                                            URI fileUri = FileUtil.getFileURI(file);
                                            if (fileUri != null) {
                                                cval = fileUri.toString();
                                            }
                                        } else {
                                            cval = "file://" + cval;
                                        }
                                    }

                                    URI testUri = UriUtil.parse(cval);
                                    if (testUri != null && UriUtil.isFile(testUri)) {
                                        File logfile = UriUtil.getFile(testUri);
                                        if (!logfile.isAbsolute()) {
                                            String path = appHome;
                                            if (StringUtil.isBlank(path)) {
                                                String cfgUri = System.getProperty(ConfigTag.CONFIG_URI);
                                                if (StringUtil.isNotBlank(cfgUri) && cfgUri.startsWith("file:")) {
                                                    try {
                                                        File cfgFile = UriUtil.getFile(new URI(cfgUri));
                                                        path = (cfgFile != null && cfgFile.exists()) ? cfgFile.getParent() : System.getProperty("user.dir") + "/snap/log";
                                                    } catch (URISyntaxException e) {
                                                        path = System.getProperty("user.dir") + "/snap/log";
                                                    }
                                                } else {
                                                    path = System.getProperty("user.dir") + "/snap/log";
                                                }
                                            } else {
                                                path = path + "/log";
                                            }
                                            logfile = new File(path, logfile.getPath());
                                            cval = FileUtil.getFileURI(logfile).toString();
                                        }
                                    }
                                }
                                loggerConfiguration.put(ConfigTag.TARGET, cval);
                            } else {
                                loggerConfiguration.put(lfield.getName(), lfield.getObjectValue());
                            }
                        }

                        Logger logger = createLogger(loggerConfiguration);
                        if (logger != null) {
                            String name = loggerConfiguration.getString(ConfigTag.NAME);
                            if (StringUtil.isBlank(name)) {
                                name = className;
                            }
                            Log.addLogger(name, logger);
                        }
                    }
                }
            }
        }
    }

    private static Logger createLogger(Config cfg) {
        Logger retval = null;
        if (cfg != null) {
            String className = cfg.getString(ConfigTag.CLASS);
            if (StringUtil.isNotBlank(className)) {
                try {
                    Class<?> clazz = Class.forName(className);
                    Constructor<?> ctor = clazz.getConstructor();
                    Object object = ctor.newInstance();
                    if (object instanceof Logger) {
                        retval = (Logger) object;
                        retval.setConfig(cfg);
                    } else {
                        Log.error("Class is not a Logger: " + className);
                    }
                } catch (Exception e) {
                    Log.error("Could not instantiate logger: " + className + " - " + e.getMessage());
                }
            }
        }
        return retval;
    }
}
