/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.daemonjob;

import coyote.DaemonJob;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.Status;
import coyote.commons.network.http.responder.AbstractJsonResponder;
import coyote.commons.network.http.responder.Resource;

import java.util.Map;

/**
 * StatusResponder returns the current status of the DaemonJob.
 */
public class StatusResponder extends AbstractJsonResponder {

    /**
     * Handle the GET request to retrieve job status.
     *
     * @param resource  The resource containing initialization parameters.
     * @param urlParams The parameters parsed from the URL.
     * @param session   The HTTP session.
     * @return The response to the request.
     */
    @Override
    public Response get(Resource resource, Map<String, String> urlParams, HTTPSession session) {
        DaemonJob job = resource.initParameter(DaemonJob.class);
        if (job == null) {
            results.set("error", "DaemonJob not initialized in responder");
            setStatus(Status.INTERNAL_ERROR);
        } else {
            if (job.getContext() != null) {
                results.set("startTime", job.getContext().getStartTime());
                results.set("elapsed", job.getContext().getElapsed());
                results.set("state", job.getContext().getState());
            }
            setStatus(Status.OK);
        }
        return Response.createFixedLengthResponse(getStatus(), getMimeType(), getText());
    }
}
