package coyote.commons.snap;

import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;

public interface SnapJob {
      static String APP_HOME = "app.home";


    void configure(Config cfg) throws ConfigurationException;

    void start();

    void stop();

    void setCommandLineArguments(String[] args);

}
