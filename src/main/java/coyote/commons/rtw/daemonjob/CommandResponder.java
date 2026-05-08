/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.daemonjob;

import coyote.DaemonJob;
import coyote.commons.StringUtil;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.Status;
import coyote.commons.network.http.auth.Auth;
import coyote.commons.network.http.responder.Resource;
import coyote.commons.network.http.responder.ServiceResponder;

import java.util.Map;

/**
 * CommandResponder handles control commands for the DaemonJob.
 *
 * <p>It expects a POST request with a JSON body containing a "command" field.
 * Valid commands are "start", "stop", and "shutdown".</p>
 */
@Auth
public class CommandResponder extends ServiceResponder {

    /**
     * Handle the POST request to execute a command.
     *
     * @param resource  The resource containing initialization parameters.
     * @param urlParams The parameters parsed from the URL.
     * @param session   The HTTP session.
     * @return The response to the request.
     */
    @Override
    public Response post(Resource resource, Map<String, String> urlParams, HTTPSession session) {
        DaemonJob job = resource.initParameter(DaemonJob.class);
        if (job == null) {
            return Response.createFixedLengthResponse(Status.INTERNAL_ERROR, getMimeType(),
                    new DataFrame().set("error", "DaemonJob not initialized in responder").toString());
        }

        DataFrame body = marshalBody(session);
        if (body == null) {
            return Response.createFixedLengthResponse(Status.BAD_REQUEST, getMimeType(),
                    new DataFrame().set("error", "Invalid or missing JSON body").toString());
        }

        String command = body.getAsString("command");
        if (StringUtil.isBlank(command)) {
            return Response.createFixedLengthResponse(Status.BAD_REQUEST, getMimeType(),
                    new DataFrame().set("error", "Missing 'command' field").toString());
        }

        DataFrame results = new DataFrame();
        results.set("command", command);

        switch (command.toLowerCase()) {
            case "start":
                job.start();
                results.set("status", "started");
                break;
            case "stop":
                job.stop();
                results.set("status", "stopped");
                break;
            case "shutdown":
                job.stop();
                results.set("status", "shutting down");
                // We might want to trigger a system exit or similar, but for now just stop the job
                break;
            default:
                return Response.createFixedLengthResponse(Status.BAD_REQUEST, getMimeType(),
                        new DataFrame().set("error", "Unknown command: " + command).toString());
        }

        return Response.createFixedLengthResponse(Status.OK, getMimeType(), results.toString());
    }
}
