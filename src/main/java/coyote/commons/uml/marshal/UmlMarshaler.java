package coyote.commons.uml.marshal;

import coyote.commons.uml.UmlModel;

public interface UmlMarshaler {

    /**
     * @return the name
     */
    public String getName() ;


    /**
     * @param name the name to set
     */
    public void setName(String name) ;


    /**
     * @return the version
     */
    public String getVersion() ;

    
    /**
     * @param version the version to set
     */
    public void setVersion(String version) ;

    /**
     * @return the identifier for this exporter
     */
    public String getId() ;

    /**
     * @param version the identifier to set
     */
    public void setId(String id) ;


    /** 
     * Convert the model to a string
     */
    String marshal(UmlModel model, boolean indentFlag);

    
}
