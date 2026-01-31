package coyote.commons.snap;

import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;

public interface SnapJob {
      static String APP_WORK = "app.work";


    void configure(Config cfg) throws ConfigurationException;

    void start();

    void stop();

    void setCommandLineArguments(String[] args);

}
