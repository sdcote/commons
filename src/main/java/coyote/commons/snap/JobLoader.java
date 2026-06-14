package coyote.commons.snap;

import coyote.commons.ClasspathUtil;
import coyote.commons.StringUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Utility class to load and configure jobs.
 */
public class JobLoader {

    /**
     * Load a job from the given configuration.
     *
     * @param config the configuration to use
     * @return a configured job
     * @throws ConfigurationException if there are configuration errors
     */
    public static SnapJob loadJob(Config config) throws ConfigurationException {
        SnapJob retval = null;
        String className = null;
        Config jobConfig = null;

        if (config.containsIgnoreCase(ConfigTag.CLASS)) {
            className = config.getString(ConfigTag.CLASS);
            jobConfig = config.getSection(ConfigTag.CONFIGURATION);
            if (jobConfig == null) {
                jobConfig = config;
            }
        } else {
            // Fallback to legacy behavior: first attribute name is the class name
            DataField configField = config.getField(0);
            if (configField != null && StringUtil.isNotEmpty(configField.getName())) {
                className = configField.getName();
                if (configField.isFrame()) {
                    jobConfig = new Config((DataFrame) configField.getObjectValue());
                }
            }
        }

        if (StringUtil.isEmpty(className)) {
            throw new ConfigurationException("Job configuration must specify a class");
        }

        // If the class name is not fully qualified, attempt to resolve it
        if (className.indexOf('.') < 1) {
            List<String> names = ClasspathUtil.resolve(className);
            if (!names.isEmpty()) {
                className = names.get(0);
            } else {
                // Last resort: assume it's in the coyote package (legacy)
                className = "coyote." + className;
            }
        }

        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getConstructor();
            Object object = ctor.newInstance();

            if (object instanceof SnapJob) {
                retval = (SnapJob) object;
                if (jobConfig != null) {
                    retval.configure(jobConfig);
                }
            } else {
                throw new ConfigurationException("Class is not a SnapJob: " + className);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                 | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ConfigurationException("Could not instantiate job class: " + className + " - " + e.getMessage(), e);
        }

        return retval;
    }
}
