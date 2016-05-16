package coyote.commons;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TemperatureConversionTests {

  // FAHRENHEIT CONVERSION TESTS

  @Test
  public void testFarenheitToCelsius() {
    assertEquals( 300, TemperatureConversion.convert( TemperatureScale.FAHRENHEIT, TemperatureScale.CELSIUS, 572 ), 0 );
  }




  @Test
  public void testFarenheitToKelvin() {
    assertEquals( 573.15, TemperatureConversion.convert( TemperatureScale.FAHRENHEIT, TemperatureScale.KELVIN, 572 ), 0.001 );
  }




  @Test
  public void testFarenheitToRankine() {
    assertEquals( 1031.67, TemperatureConversion.convert( TemperatureScale.FAHRENHEIT, TemperatureScale.RANKINE, 572 ), 0 );
  }




  // CELSIUS CONVERSION TESTS

  @Test
  public void testCelsiusToFarenheit() {
    assertEquals( 572, TemperatureConversion.convert( TemperatureScale.CELSIUS, TemperatureScale.FAHRENHEIT, 300 ), 0 );
  }




  @Test
  public void testCelsiusToKelvin() {
    assertEquals( 573.15, TemperatureConversion.convert( TemperatureScale.CELSIUS, TemperatureScale.KELVIN, 300 ), 0.001 );
  }




  @Test
  public void testCelsiusToRankine() {
    assertEquals( 1031.67, TemperatureConversion.convert( TemperatureScale.CELSIUS, TemperatureScale.RANKINE, 300 ), 0.001 );
  }




  // KELVIN CONVERSION TESTS

  @Test
  public void testKelvinToFarenheit() {
    assertEquals( 338, TemperatureConversion.convert( TemperatureScale.KELVIN, TemperatureScale.FAHRENHEIT, 443.15 ), 0.001 );
  }




  @Test
  public void testKelvinToCelsius() {
    assertEquals( 170, TemperatureConversion.convert( TemperatureScale.KELVIN, TemperatureScale.CELSIUS, 443.15 ), 0.001 );
  }




  @Test
  public void testKelvinToRankine() {
    assertEquals( 797.67, TemperatureConversion.convert( TemperatureScale.KELVIN, TemperatureScale.RANKINE, 443.15 ), 0.001 );
  }




  // RANKINE CONVERSION TESTS

  @Test
  public void testRankineToFarenheit() {
    assertEquals( -459.67, TemperatureConversion.convert( TemperatureScale.RANKINE, TemperatureScale.FAHRENHEIT, 0 ), 0.001 );
  }




  @Test
  public void testRankineToCelsius() {
    assertEquals( -273.15, TemperatureConversion.convert( TemperatureScale.RANKINE, TemperatureScale.CELSIUS, 0 ), 0.001 );
  }




  @Test
  public void testRankineToKelvin() {
    assertEquals( 0, TemperatureConversion.convert( TemperatureScale.RANKINE, TemperatureScale.KELVIN, 0 ), 0.001 );
  }

}
