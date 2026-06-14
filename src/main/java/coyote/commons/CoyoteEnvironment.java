package coyote.commons;

import coyote.commons.StringUtil;

/**
 * Centralized environment management for Coyote applications.
 */
public class CoyoteEnvironment {

    /**
     * The name of the property used to define the application's home directory.
     */
    public static final String APP_HOME = "app.home";

    /**
     * The name of the property used to define the application's working directory.
     */
    public static final String APP_WORK = "app.work";

    /**
     * Get the application home directory.
     *
     * @return the application home directory, or null if not set.
     */
    public static String getHomeDirectory() {
        return getVariable(APP_HOME);
    }

    /**
     * Get the application work directory.
     *
     * @return the application work directory, or null if not set.
     */
    public static String getWorkDirectory() {
        return getVariable(APP_WORK);
    }

    /**
     * Returns the value from either the environment variables or the system properties with the system properties taking precedence
     * over environment variables.
     *
     * @param variable the name of the variable to lookup
     * @return The value from either the environment variables or system properties or null if neither are defined.
     */
    public static String getVariable(String variable) {
        String retval = System.getenv().get(variable);
        if (StringUtil.isNotBlank(System.getProperty(variable))) {
            retval = System.getProperty(variable);
        }
        return retval;
    }
}
