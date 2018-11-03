package ch.leica.sdk.commands.response;

import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.commands.MeasuredValue;
import ch.leica.sdk.commands.MeasurementConverter;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;

public abstract class ResponseMeasurement
  extends Response
{
  private MeasuredValue e;
  private short f = MeasurementConverter.getDefaultWifiDistanceUnit();
  
  public ResponseMeasurement(Types.Commands paramCommands)
  {
    super(paramCommands);
    try
    {
      e = new MeasuredValue();
    }
    catch (IllegalArgumentCheckedException localIllegalArgumentCheckedException)
    {
      Logs.log(Logs.LogTypes.exception, "Error caused by: ", localIllegalArgumentCheckedException);
    }
  }
  
  public MeasuredValue getDistanceValue()
  {
    return e;
  }
  
  public void setDistance(float paramFloat)
    throws IllegalArgumentCheckedException
  {
    e = new MeasuredValue(paramFloat, f);
    e.convertDistance();
  }
  
  public void setDistance(float paramFloat, short paramShort)
    throws IllegalArgumentCheckedException
  {
    e = new MeasuredValue(paramFloat, paramShort);
    e.convertDistance();
  }
}
