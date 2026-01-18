package cookbook.uml;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.uml.UmlAssociation;
import coyote.commons.uml.UmlAssociationEnd;
import coyote.commons.uml.UmlAttribute;
import coyote.commons.uml.UmlClass;
import coyote.commons.uml.UmlDataType;
import coyote.commons.uml.UmlDiagram;
import coyote.commons.uml.UmlDiagramElement;
import coyote.commons.uml.UmlGeneralization;
import coyote.commons.uml.UmlModel;
import coyote.commons.uml.UmlPackage;
import coyote.commons.uml.UmlStereotype;
import coyote.commons.uml.UmlType;
import coyote.commons.uml.marshal.UmlMarshaler;
import coyote.commons.uml.marshal.Xmi11Marshaler;

/**
 * This is an example of how to create a UML model which Sparx Enterprise
 * Architect (EA) can read.
 * 
 * <p>
 * This models a few tables in a manner common to a ServiceNow instance.
 * </p>
 * 
 * http://www.sparxsystems.com/enterprise_architect_user_guide/12.0/database_engineering/dbmsdatatypes.html
 */
public class ModelMarshal {

    public static final UmlStereotype TABLE_STEREOTYPE = new UmlStereotype("table");
    public static final UmlStereotype COLUMN_STEREOTYPE = new UmlStereotype("column");

    /**
     * @param args
     */
    public static void main(String[] args) {
        UmlModel model = new UmlModel("SnowModel");

        // The root package
        UmlPackage rootPkg = new UmlPackage("Snow");
        model.addElement(rootPkg);

        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // Add some model-level data types
        UmlDataType stringDataType = new UmlDataType("STRING");
        model.addDataType(stringDataType);
        // create a UML Type which can be attached to a structural feature defining
        // its data type. Here we reference the DataType attached to the model.
        UmlType stringType = new UmlType();// reusable type object
        stringType.setReference(stringDataType.getId()); // set the ID reference

        UmlDataType referenceDataType = new UmlDataType("REFERENCE");
        model.addDataType(referenceDataType);
        UmlType referenceType = new UmlType();
        referenceType.setReference(referenceDataType.getId());
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

        // The package containing the table schema
        UmlPackage tablePkg = new UmlPackage("Tables");
        rootPkg.addElement(tablePkg);

        // / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
        // Read the ServiceNow tables into classes with the "table" stereotype
        // use the sys_id of the table in the dictionary as the ID
        UmlClass sysUser = new UmlClass("sys_user");
        tablePkg.addElement(sysUser);
        sysUser.addStereotype(TABLE_STEREOTYPE);

        // add properties to the table, created_by,updated_by modified_count, etc...
        sysUser.setTaggedValue("stereotype", TABLE_STEREOTYPE.getName()); // gets the icon
        sysUser.setTaggedValue("gentype", "ServiceNow");// enables database modeling
        sysUser.setTaggedValue("product_name", "ServiceNow");// add for each table
        sysUser.setTaggedValue("documentation",
                "This is a &lt;b&gt;system&lt;/b&gt; table which holds all the users of the system.");

        // Read the ServiceNow fields into the tables with the "column" stereotype
        // Use Attribute instead of Property to assist in generating 1.x XMI model
        // use the sys_id of the field in the dictionary as the ID
        UmlAttribute column = new UmlAttribute("sys_id");
        sysUser.addFeature(column);
        column.addStereotype(COLUMN_STEREOTYPE);
        column.addType(stringType); // may not be necessary
        // column.setTaggedValue( "type", stringDataType.getName() );
        column.setTaggedValue("length", "32");
        column.setTaggedValue("created", "2015-03-15 10:21:18");
        column.setTaggedValue("stereotype", COLUMN_STEREOTYPE.getName());
        column.setTaggedValue("documentation", "This is the primary key into this table.");

        column = new UmlAttribute("created_by");
        sysUser.addFeature(column);
        column.addStereotype(COLUMN_STEREOTYPE);
        column.addType(referenceType);// may not be necessary
        // column.setTaggedValue( "type", referenceDataType.getName() );
        column.setTaggedValue("length", "32");
        column.setTaggedValue("created", "2015-03-15 10:21:18");
        column.setTaggedValue("stereotype", COLUMN_STEREOTYPE.getName());

        // / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
        UmlClass myTable = new UmlClass("u_my_table");
        tablePkg.addElement(myTable);
        myTable.addStereotype(TABLE_STEREOTYPE);
        myTable.setTaggedValue("created", "2016-04-15 20:15:44");
        myTable.setTaggedValue("stereotype", TABLE_STEREOTYPE.getName());
        myTable.setTaggedValue("gentype", "ServiceNow");// enables database modeling
        myTable.setTaggedValue("product_name", "ServiceNow");// add for each table

        column = new UmlAttribute("user");
        myTable.addFeature(column);
        column.addStereotype(COLUMN_STEREOTYPE);
        column.addType(referenceType);// may not be necessary
        column.setTaggedValue("length", "32");
        column.setTaggedValue("stereotype", COLUMN_STEREOTYPE.getName());

        // / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
        UmlClass taskTable = new UmlClass("task");
        tablePkg.addElement(taskTable);
        taskTable.addStereotype(TABLE_STEREOTYPE);
        taskTable.setTaggedValue("stereotype", TABLE_STEREOTYPE.getName());
        taskTable.setTaggedValue("gentype", "ServiceNow");// enables database modeling
        taskTable.setTaggedValue("product_name", "ServiceNow");// add for each table

        column = new UmlAttribute("name");
        taskTable.addFeature(column);
        column.addStereotype(COLUMN_STEREOTYPE);
        column.addType(referenceType);// may not be necessary
        column.setTaggedValue("length", "32");

        // Many tables are a subclass of another
        UmlGeneralization generalization = new UmlGeneralization(myTable.getId(), taskTable.getId());
        tablePkg.addElement(generalization);

        // For each reference create an association to the sys_id of the referenced
        // table
        UmlAssociation association = new UmlAssociation();
        UmlAssociationEnd srcend = new UmlAssociationEnd(myTable.getId(), "user");
        UmlAssociationEnd tgtend = new UmlAssociationEnd(sysUser.getId(), "sys_id");
        tgtend.setNavigable(true);
        association.addEnd(srcend);
        association.addEnd(tgtend);
        tablePkg.addElement(association);

        // / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
        // Create a diagram
        UmlDiagram diagram = new UmlDiagram("Simple Diagram");
        model.addDiagram(diagram);
        diagram.setParent(tablePkg);// set the parent/owner of this diagram

        UmlDiagramElement element = new UmlDiagramElement(taskTable, 0); // create
        diagram.add(element); // add
        element.setGeometry("Left=10;Top=10;Right=288;Bottom=616;"); // configure where on the diagram this is to
                                                                     // display

        element = diagram.addSubject(myTable); // create & add
        element.setGeometry("Left=200;Top=200;"); // configure where on the diagram this is to display

        element = diagram.addSubject(sysUser);
        element.setGeometry("Left=300;Top=300;");

        // / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /

        // Must be specified for Enterprise Architect 12 to recognize column types!
        Xmi11Marshaler marshaler = new Xmi11Marshaler();
        marshaler.setName("Enterprise Architect");
        marshaler.setVersion("2.5");

        String xml = marshaler.marshal(model, true);

        if (save(xml, "ModelMarshal.xml", "UTF-8")) {
            System.out.println(xml);
        } else {
            System.out.println("Could not save to file");
        }

    }

    /**
     * @param xml     the XML to write
     * @param fname   the name of the file to write
     * @param charset the character set to use (e.g. UTF-8)
     * 
     * @return true if the file was written successfully, false otherwise
     */
    public static boolean save(String xml, String fname, String charset) {
        if (StringUtil.checkCharacterSetName(charset)) {
            StringBuilder b = new StringBuilder("<?xml version=\"1.0\" encoding=\"");
            b.append(charset);
            b.append("\"?>\r\n");
            b.append(xml);
            return FileUtil.stringToFile(b.toString(), fname, charset);
        } else {
            System.err.println("The character set of '" + charset + "' is not supported in this runtime");
            return false;
        }
    }

}
