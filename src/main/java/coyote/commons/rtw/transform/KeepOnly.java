package coyote.commons.rtw.transform;


import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.Log;
import coyote.commons.rtw.FrameTransform;
import coyote.commons.rtw.TransformException;

/**
 * This transformation removes all fields from the frame except for the named field.
 *
 * <p>This is useful when only one field in the record is desired. Often, additional transforms will build out the rest
 * of the record.</p>
 *
 * <p>The following only keeps the field named {@code requestCount}:
 * <pre> "KeepOnly" : { "Field" : "requestCount" }</pre>
 *
 * <p>If there is no field by that name, the result will be an empty frame.</p>
 */
public class KeepOnly extends AbstractFieldTransform implements FrameTransform {
    /**
     *
     */
    @Override
    public DataFrame process(final DataFrame frame) throws TransformException {
        DataFrame retval;
        if (getExpression() != null) {
            try {
                if (evaluator.evaluateBoolean(getExpression())) {
                    retval = keepOnly(frame);
                } else {
                    retval = (DataFrame) frame.clone();
                }
            } catch (final IllegalArgumentException e) {
                Log.warn(String.format( "Transform.KeepOnly_boolean_evaluation_error", e.getMessage()));
                retval = (DataFrame) frame.clone();
            }
        } else {
            retval = keepOnly(frame);
        }
        return retval;
    }


    private DataFrame keepOnly(DataFrame frame) {
        DataFrame retval = new DataFrame();
        DataField field = frame.getField(getFieldName());
        if (field != null) {
            retval.add(field);
        }
        return retval;
    }
}
