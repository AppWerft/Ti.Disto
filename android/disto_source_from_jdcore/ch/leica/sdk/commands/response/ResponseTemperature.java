package ch.leica.sdk.commands.response;

import ch.leica.sdk.Types.Commands;

public final class ResponseTemperature
  extends Response
{
  private float e;
  private float f;
  private float g;
  private float h;
  
  public ResponseTemperature(Types.Commands paramCommands)
  {
    super(paramCommands);
  }
  
  public float getTemperatureHorizontalAngleSensor_Hz()
  {
    return e;
  }
  
  public void setTemperatureHorizontalAngleSensor_Hz(float paramFloat)
  {
    e = paramFloat;
  }
  
  public float getTemperatureVerticalAngleSensor_V()
  {
    return f;
  }
  
  public void setTemperatureVerticalAngleSensor_V(float paramFloat)
  {
    f = paramFloat;
  }
  
  public float getTemperatureDistanceMeasurementSensor_Edm()
  {
    return g;
  }
  
  public void setTemperatureDistanceMeasurementSensor_Edm(float paramFloat)
  {
    g = paramFloat;
  }
  
  public float getTemperatureBLESensor()
  {
    return h;
  }
  
  public void setTemperatureBLESensor(float paramFloat)
  {
    h = paramFloat;
  }
}
