/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote;

import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.Log;
import coyote.commons.network.http.auth.GenericAuthProvider;
import coyote.commons.network.http.responder.HTTPDRouter;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.daemonjob.CommandResponder;
import coyote.commons.rtw.daemonjob.StatusResponder;
import coyote.commons.snap.AbstractSnapJob;

import java.io.IOException;

/**
 * DaemonJob class that extends AbstractSnapJob.
 * It operates by being called from the BootStrap based on a configuration file.
 * The configuration contains a list of jobs and logging settings.
 */
public class DaemonJob extends AbstractSnapJob {

    private HTTPDRouter server = null;

    /**
     * Configure the job with the provided configuration.
     *
     * @param cfg The configuration for the job.
     * @throws ConfigurationException If there is a problem with the configuration.
     */
    @Override
    public void configure(Config cfg) throws ConfigurationException {
        super.configure(cfg);

        if (cfg.containsIgnoreCase(ConfigTag.SERVER)) {
            try {
                DataFrame serverFrame = cfg.getAsFrame(cfg.getFieldIgnoreCase(ConfigTag.SERVER).getName());
                Config serverConfig = new Config(serverFrame);
                int port = 80;
                if (serverConfig.containsIgnoreCase(ConfigTag.PORT)) {
                    try {
                        port = serverConfig.getInt(ConfigTag.PORT);
                    } catch (Exception e) {
                        Log.error("Invalid port in server configuration: " + e.getMessage());
                    }
                }

                server = new HTTPDRouter(port);

                if (serverConfig.containsIgnoreCase(GenericAuthProvider.AUTH_SECTION) || serverConfig.containsIgnoreCase(GenericAuthProvider.USER_SECTION)) {
                    DataFrame authFrame = null;
                    if (serverConfig.containsIgnoreCase(GenericAuthProvider.AUTH_SECTION)) {
                        authFrame = serverConfig.getAsFrame(serverConfig.getFieldIgnoreCase(GenericAuthProvider.AUTH_SECTION).getName());
                    } else {
                        authFrame = serverFrame;
                    }
                    server.setAuthProvider(new GenericAuthProvider(new Config(authFrame)));
                }

                server.addDefaultRoutes();
                server.addRoute("/api/command", CommandResponder.class, this);
                server.addRoute("/api/status", StatusResponder.class, this);

                if (serverConfig.containsIgnoreCase(ConfigTag.IPACL)) {
                    server.configIpACL(new Config(serverConfig.getAsFrame(serverConfig.getFieldIgnoreCase(ConfigTag.IPACL).getName())));
                }

                if (serverConfig.containsIgnoreCase(ConfigTag.FREQUENCY)) {
                    server.configDosTables(new Config(serverConfig.getAsFrame(serverConfig.getFieldIgnoreCase(ConfigTag.FREQUENCY).getName())));
                }
            } catch (Exception e) {
                Log.error("Could not configure HTTP Server: " + e.getMessage());
            }
        }
    }

    /**
     * Start the daemon job.
     * Initially, it only writes an "info" message of "Hello World."
     */
    @Override
    public void start() {
        Log.info("Hello World");
        if (server != null && !server.isAlive()) {
            try {
                server.start();
                Log.info("HTTP listener started on port " + server.getListeningPort());
            } catch (IOException e) {
                Log.error("Could not start HTTP listener: " + e.getMessage());
            }
        }
    }

    /**
     * Shut everything down when the job is requested to stop.
     */
    @Override
    public void stop() {
        if (server != null && server.isAlive()) {
            server.stop();
            Log.info("HTTP listener stopped");
        }
    }

}
