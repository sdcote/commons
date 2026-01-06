package coyote.commons.dataframe;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NullTypeTest
{
  /** The data type under test. */
  static UndefinedType datatype = null;




  /**
   * @throws Exception
   */
  @BeforeAll
  public static void setUpBeforeClass() throws Exception
  {
    datatype = new UndefinedType();
  }




  /**
   * @throws Exception
   */
  @AfterAll
  public static void tearDownAfterClass() throws Exception
  {
    datatype = null;
  }




  @Test
  public void testGetSize()
  {
    assertTrue( datatype.getSize() == 0 );
  }




  @Test
  public void testCheckType()
  {
    assertTrue( datatype.checkType( null ) );
    assertFalse( datatype.checkType( "" ) );
  }




  @Test
  public void testEncode()
  {
    byte[] result = datatype.encode( null );
    assertNotNull( result );
    assertTrue( result.length == 0 );
  }




  @Test
  public void testDecode()
  {
    byte[] data = new byte[0];
    Object obj = datatype.decode( data );
    assertNull( obj );
  }




  @Test
  public void testGetTypeName()
  {
    assertTrue( datatype.getTypeName().equals( "UDEF" ) );
  }




  @Test
  public void testIsNumeric()
  {
    assertFalse( datatype.isNumeric() );
  }

}
