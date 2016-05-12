package coyote.commons;

public enum TemperatureScale {

  CELSIUS( "Celsius", "°C", TemperatureConversion.ABSOLUTE_ZERO_CELSIUS), FAHRENHEIT( "Farenheit", "°F", TemperatureConversion.ABSOLUTE_ZERO_FARENHEIT), KELVIN( "Kelvin", "K", TemperatureConversion.ABSOLUTE_ZERO_KELVIN), RANKINE( "Rankine", "°R", TemperatureConversion.ABSOLUTE_ZERO_RANKINE);

  private String name;
  private String units;
  private double absoluteZero = 0;




  TemperatureScale( String name, String units, double absoluteZero ) {
    this.name = name;
    this.units = units;
    this.absoluteZero = absoluteZero;
  }




  public String getName() {
    return name;
  }




  public String getUnits() {
    return units;
  }




  public String getValueString( double temperature ) {
    return temperature + " " + units;
  }




  public double getAbsoluteZero() {
    return absoluteZero;
  }




  @Override
  public String toString() {
    return name;
  }

}
