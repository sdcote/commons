/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.daemonjob;

import coyote.BootStrap;
import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.Status;
import coyote.commons.network.http.responder.AbstractJsonResponder;
import coyote.commons.network.http.responder.Resource;

import java.util.Map;

/**
 * StatusResponder returns the current status of the BootStrap loader.
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
        BootStrap job = resource.initParameter(BootStrap.class);
        if (job == null) {
            results.set("error", "BootStrap not initialized in responder");
            setStatus(Status.INTERNAL_ERROR);
        } else {
            // results.set("startTime", ...); // BootStrap doesn't have context yet
            setStatus(Status.OK);
        }
        return Response.createFixedLengthResponse(getStatus(), getMimeType(), getText());
    }
}
