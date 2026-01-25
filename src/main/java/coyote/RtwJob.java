package coyote;

import coyote.commons.log.Log;
import coyote.commons.snap.AbstractSnapJob;
import coyote.commons.snap.SnapJob;

/**
 * This is a Snap job that handles Read-Transform-Write tasks.
 * 
 * <p>
 * Using the coyote.commons.rtw package, this job uses a set of common classes to perform basic data processing.
 * </p>
 */
public class RtwJob extends AbstractSnapJob {


    @Override
    public void start() {
        Log.info("Starting");
    }

    @Override
    public void stop() {
        Log.info("Stopping");

    }

}
