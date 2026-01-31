package coyote.commons.rtw.context;


import coyote.commons.dataframe.DataFrame;

/**
 * This is a component responsible for holding data involved with the
 * extraction, transformation and loading of single row of data.
 *
 * <p>Transform context references the current source, working and target data
 * frames on which all component in the transform pipeline operate.
 *
 * <p>The Source frame is the frame as it was read in. It should never be
 * changed. This provides a backup and current state reference for
 * transformation operations.</p>
 *
 * <p>The working frame in the record all the components work on. When the
 * process first starts out, the source and working frames are identical. Then
 * mappers, and transformers begin to modify the working frame into its final
 * state.</p>
 *
 * <p>The target frame is what is used by the writers to write the final state
 * of the record to the respective destinations. At the end of the process,
 * the working frame is copied to the target frame and the two match.</p>
 */
public class TransactionContext extends OperationalContext {
    private DataFrame sourceFrame = null;
    private DataFrame targetFrame = null;
    private DataFrame workingFrame = null;

    private boolean lastFrame = false;




    public TransactionContext(TransformContext context) {
        this.parent = context;
    }




    /**
     * @return the sourceFrame
     */
    public DataFrame getSourceFrame() {
        return sourceFrame;
    }




    /**
     * Set the source frame.
     *
     * <p>This also makes a copy (clone) of the source frame and sets it as the
     * working frame. Any time the source frame is set, a new working frame
     * should be created as well since it represents a new starting point.</p>
     *
     * @param sourceFrame the sourceFrame to set
     */
    public void setSourceFrame(DataFrame sourceFrame) {
        this.sourceFrame = sourceFrame;
        this.workingFrame = (DataFrame)sourceFrame.clone();
    }




    /**
     * @return the targetFrame
     */
    public DataFrame getTargetFrame() {
        return targetFrame;
    }




    /**
     * @param targetFrame the targetFrame to set
     */
    public void setTargetFrame(DataFrame targetFrame) {
        this.targetFrame = targetFrame;
    }




    /**
     * @return the workingFrame
     */
    public DataFrame getWorkingFrame() {
        return workingFrame;
    }




    /**
     * @param workingFrame the workingFrame to set
     */
    public void setWorkingFrame(DataFrame workingFrame) {
        this.workingFrame = workingFrame;
    }




    /**
     * @return true if this is the last frame in the stream, false if more frames are coming.
     */
    public boolean isLastFrame() {
        return lastFrame;
    }




    /**
     * @param isLast true if this is the last frame in the stream
     */
    public void setLastFrame(boolean isLast) {
        lastFrame = isLast;
    }

}