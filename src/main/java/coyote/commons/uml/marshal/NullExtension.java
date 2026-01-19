package coyote.commons.uml.marshal;

import coyote.commons.uml.UmlModel;

/**
 * This is a null or No-Op extension that is used to generate pure XMI models.
 */
public class NullExtension implements MarshalerExtension {
    /**
     * This generates and extension block immediately following the
     * {@code uml:Model} block.
     *
     * <p>The extension has the ability to query the model and perform any
     * processing necessary to help the target system properly import and
     * render the standard XMI model.</p>
     *
     * @param b     the string builder to which all text should be appended.
     * @param model the UML model
     * @param level the current level of indentation. A value of -1 indicate no
     *              indentation or line feeds are to be used.
     */
    @Override
    public void generateExtensionBlock(StringBuilder b, UmlModel model, int level) {
        // no-op implementation
    }
}
