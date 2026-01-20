package coyote.commons.uml.marshal;

import coyote.commons.StringUtil;
import coyote.commons.uml.DiagramBounds;
import coyote.commons.uml.UmlDiagram;
import coyote.commons.uml.UmlDiagramElement;
import coyote.commons.uml.UmlModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * This is an XLI marshaler extension leverages Sparx Enterprise Architect extensions.
 */
public class SparxExtension implements MarshalerExtension {

    private static final String CRLF = "\r\n";
    private static final String EMPTY_STRING = "";
    private static final String DEFAULT_NAME = "Enterprise Architect";
    private static final String DEFAULT_ID = "6.5";
    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    private String name = DEFAULT_NAME;
    private String identifier = DEFAULT_ID;

    /**
     * The padding for the given level.
     *
     * <p>
     * There are two spaces per level of padding. Zero or less levels result
     * in an empty string being returned. This will never return null.
     * </p>
     *
     * @param level the level of padding required
     * @return a string to indent to the given level
     */
    protected static String getPadding(int level) {
        if (level < 1) {
            return EMPTY_STRING;
        } else {
            int length = level * 2;
            StringBuilder outputBuffer = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                outputBuffer.append(" ");
            }
            return outputBuffer.toString();
        }
    }

    /**
     * Returns either CRFL or EMPTY_STRING depending on the level
     *
     * @param level the level of indenting
     * @return CRFL if level is zero or grater, an empty string "" otherwise.
     */
    protected static String lineEnd(int level) {
        if (level > -1) {
            return CRLF;
        } else {
            return EMPTY_STRING;
        }
    }

    /**
     * Translates standard XMI bounds into the Sparx EA geometry string format.
     *
     * @param bounds the DiagramBounds of a DiagramElement
     * @return A formatted string: Left=x;Top=y;Right=r;Bottom=b;
     */
    public static String translateToGeometry(DiagramBounds bounds) {
        return translateToGeometry(bounds.getXPosition(), bounds.getYPosition(), bounds.getWidth(), bounds.getHeight());
    }

    /**
     * Translates standard XMI bounds into the Sparx EA geometry string format.
     *
     * @param x      The horizontal starting position
     * @param y      The vertical starting position
     * @param width  The width of the element
     * @param height The height of the element
     * @return A formatted string: Left=x;Top=y;Right=r;Bottom=b;
     */
    public static String translateToGeometry(int x, int y, int width, int height) {
        int left = x;
        int top = y;
        int right = x + width;
        int bottom = y + height;
        return String.format("Left=%d;Top=%d;Right=%d;Bottom=%d;", left, top, right, bottom);
    }

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
        extensionBlockStart(b, level);
        extendDiagrams(b, model, (level > -1) ? level + 1 : level);
        extensionBlockEnd(b, level);
    }

    /**
     * Called just after the {@code uml:model} opening block to give some tools
     * a hint that certain extensions are to follow the model.
     *
     * @param b     the string builder to which all text should be appended.
     * @param model the UML model
     * @param level the current level of indentation. A value of -1 indicate no
     *              indentation or line feeds are to be used.
     */
    @Override
    public void generateModelMetaData(StringBuilder b, UmlModel model, int level) {
//        extensionBlockStart(b, level);
//        extendProperties(b, (level > -1) ? level + 1 : level);
//        extensionBlockEnd(b, level);
    }

    private void extendProperties(StringBuilder b, int level) {
        b.append(getPadding(level));
        b.append("<properties sType=\"Model\" nType=\"0\" scope=\"public\"/>");
        b.append(lineEnd(level));
    }

    private void extensionBlockEnd(StringBuilder b, int level) {
        b.append(getPadding(level));
        b.append("</xmi:Extension>");
        b.append(lineEnd(level));
    }

    private void extensionBlockStart(StringBuilder b, int level) {
        b.append(getPadding(level));
        b.append("<xmi:Extension extender=\"");
        b.append(name);
        b.append("\" extenderID=\"");
        b.append(identifier);
        b.append("\">");
        b.append(lineEnd(level));
    }

    private void extendDiagrams(StringBuilder b, UmlModel model, int level) {
        List<UmlDiagram> diagrams = model.getDiagrams();
        if (!diagrams.isEmpty()) {
            b.append(getPadding(level));
            b.append("<diagrams>");
            b.append(lineEnd(level));

            for (UmlDiagram diagram : diagrams) {
                extendDiagram(b, diagram, (level > -1) ? level + 1 : level);
            }

            b.append(getPadding(level));
            b.append("</diagrams>");
            b.append(lineEnd(level));
        }
    }

    private void extendDiagram(StringBuilder b, UmlDiagram diagram, int level) {
        b.append(getPadding(level));
        b.append("<diagram xmi:id=\"");
        b.append(diagram.getId());
        b.append("\">");
        b.append(lineEnd(level));
        extendDiagramDetails(b, diagram, (level > -1) ? level + 1 : level);
        b.append(getPadding(level));
        b.append("</diagram>");
        b.append(lineEnd(level));
    }

    private void extendDiagramDetails(StringBuilder b, UmlDiagram diagram, int level) {
        // model
        b.append(getPadding(level));
        b.append("<model package=\"");
        b.append(diagram.getParent().getId());
        b.append("\"");
        b.append(" localID=\"-1\" owner=\"");
        b.append(diagram.getParent().getId());
        b.append("\"/>");
        b.append(lineEnd(level));

        // properties
        b.append(getPadding(level));
        b.append("<properties name=\"");
        b.append(diagram.getName());
        b.append("\" type=\"");
        b.append(diagram.getDiagramType().getName());
        b.append("\"/>");
        b.append(lineEnd(level));

        // Project
        b.append(getPadding(level));
        b.append("<project author=\"CoyoteUML API\" version=\"1.0\" created=\"");
        b.append(DATEFORMAT.format(Calendar.getInstance().getTime()));
        b.append("\" modified=\"");
        b.append(DATEFORMAT.format(Calendar.getInstance().getTime()));
        b.append("\"/>");
        b.append(lineEnd(level));

        // Style
        b.append(getPadding(level));
        b.append("<style appearance=\"0\" rectangle=\"0\" orientation=\"L\"/>");
        b.append(lineEnd(level));

        b.append(getPadding(level));
        b.append("<elements>");
        b.append(lineEnd(level));
        int seq = 1;
        for (UmlDiagramElement element : diagram.getDiagramElements()) {
            genDiagramElement(b, element, seq++, (level > -1) ? level + 1 : level);
        }
        b.append(getPadding(level));
        b.append("</elements>");
        b.append(lineEnd(level));


    }

    private void genDiagramElement(StringBuilder b, UmlDiagramElement element, int seqNumber, int level) {
        b.append(getPadding(level));
        b.append("<element geometry=\"");
        b.append(translateToGeometry(element.getBounds()));
        b.append("\" subject=\"");
        b.append(element.getSubject().getId());
        b.append("\" seqno=\"");
        b.append(seqNumber);
        b.append("\" styleex=\"VDI=1;\"/>");
        b.append(lineEnd(level));
    }

    public String getName() {
        return name;
    }

    public SparxExtension setName(String name) {
        this.name = name;
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public SparxExtension setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

}
