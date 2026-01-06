package coyote.commons.dataframe.marshal.xml;

import java.io.IOException;
import java.io.Writer;

import coyote.commons.dataframe.DataField;


public class TypedXmlWriter extends XmlWriter {

  private static final String TYPE = "type";




  /**
   * @param writer
   */
  TypedXmlWriter( final Writer writer ) {
    super( writer );
  }




  /**
   * @see coyote.dataframe.marshal.xml.XmlWriter#writeFieldType(DataField)
   */
  @Override
  public void writeFieldType( final DataField field ) throws IOException {
    if ( !field.isFrame() ) {
      writeSpace();
      writeLiteral( TYPE );
      writeEquals();
      writeString( field.getTypeName() );
    }
  }

}
