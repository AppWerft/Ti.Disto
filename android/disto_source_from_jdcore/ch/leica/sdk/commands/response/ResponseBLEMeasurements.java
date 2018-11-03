package ch.leica.sdk.commands.response;

import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.commands.MeasuredValue;
import ch.leica.sdk.commands.MeasurementConverter;

public final class ResponseBLEMeasurements
  extends ResponseMeasurement
{
  private MeasuredValue e;
  private MeasuredValue f;
  
  public ResponseBLEMeasurements(Types.Commands paramCommands)
  {
    super(paramCommands);
  }
  
  public MeasuredValue getAngleInclination()
  {
    return e;
  }
  
  public void setAngleInclination(float paramFloat)
    throws IllegalArgumentCheckedException
  {
    try
    {
      e = new MeasuredValue(paramFloat, MeasurementConverter.getDefaultWifiAngleUnit());
      e.convertAngle();
    }
    catch (IllegalArgumentCheckedException localIllegalArgumentCheckedException)
    {
      throw new IllegalArgumentCheckedException("Error creating the value in " + localIllegalArgumentCheckedException);
    }
  }
  
  public void setAngleInclination(float paramFloat, short paramShort)
    throws IllegalArgumentCheckedException
  {
    try
    {
      e = new MeasuredValue(paramFloat, paramShort);
      e.convertAngle();
    }
    catch (IllegalArgumentCheckedException localIllegalArgumentCheckedException)
    {
      throw new IllegalArgumentCheckedException("Error creating the value in " + localIllegalArgumentCheckedException);
    }
  }
  
  public void setAngleInclination(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {
    if (paramMeasuredValue != null) {
      e = paramMeasuredValue;
    } else {
      throw new IllegalArgumentCheckedException("Error Assigning Measured value.");
    }
  }
  
  public MeasuredValue getAngleDirection()
  {
    return f;
  }
  
  public void setAngleDirection(float paramFloat)
    throws IllegalArgumentCheckedException
  {
    try
    {
      f = new MeasuredValue(paramFloat, MeasurementConverter.getDefaultDirectionAngleUnit());
      f.convertAngle();
    }
    catch (IllegalArgumentCheckedException localIllegalArgumentCheckedException)
    {
      throw new IllegalArgumentCheckedException("Error creating the value in " + localIllegalArgumentCheckedException);
    }
  }
  
  public void setAngleDirection(float paramFloat, short paramShort)
    throws IllegalArgumentCheckedException
  {
    try
    {
      f = new MeasuredValue(paramFloat, paramShort);
      f.convertAngle();
    }
    catch (IllegalArgumentCheckedException localIllegalArgumentCheckedException)
    {
      throw new IllegalArgumentCheckedException("Error creating the value in " + localIllegalArgumentCheckedException);
    }
  }
  
  public void setAngleDirection(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {
    if (paramMeasuredValue != null) {
      f = paramMeasuredValue;
    } else {
      throw new IllegalArgumentCheckedException("Error Assigning Measured value.");
    }
  }
}
