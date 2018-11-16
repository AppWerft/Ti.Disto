package ch.leica.sdk.commands.response;

import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.commands.MeasuredValue;
import ch.leica.sdk.commands.MeasurementConverter;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;

public final class ResponseWifiMeasurements
  extends ResponseMeasurement
{
  private MeasuredValue e = null;
  private MeasuredValue f = null;
  private MeasuredValue g = null;
  private MeasuredValue h = null;
  private int i = 55537;
  private float j = -9999.0F;
  private float k = -9999.0F;
  private float l = -9999.0F;
  private short m = MeasurementConverter.getDefaultWifiAngleUnit();
  
  public ResponseWifiMeasurements(Types.Commands paramCommands)
  {
    super(paramCommands);
    try
    {
      e = new MeasuredValue();
      f = new MeasuredValue();
      g = new MeasuredValue();
      h = new MeasuredValue();
    }
    catch (IllegalArgumentCheckedException localIllegalArgumentCheckedException)
    {
      Logs.log(Logs.LogTypes.exception, "Error caused by: ", localIllegalArgumentCheckedException);
    }
  }
  
  public MeasuredValue getHorizontalAngleWithTilt_HZ()
  {
    return e;
  }
  
  public void setHorizontalAngleWithTilt_HZ(float paramFloat)
    throws IllegalArgumentCheckedException
  {
    e = MeasurementConverter.convertAngle(new MeasuredValue(paramFloat, m));
  }
  
  public MeasuredValue getVerticalAngleWithTilt_V()
  {
    return f;
  }
  
  public void setVerticalAngleWithTilt_V(float paramFloat)
    throws IllegalArgumentCheckedException
  {
    f = MeasurementConverter.convertAngle(new MeasuredValue(paramFloat, m));
  }
  
  public MeasuredValue getHorizontalAngleWithouthTilt_NI_HZ()
  {
    return g;
  }
  
  public void setHorizontalAngleWithouthTilt_NI_HZ(float paramFloat)
    throws IllegalArgumentCheckedException
  {
    g = MeasurementConverter.convertAngle(new MeasuredValue(paramFloat, m));
  }
  
  public MeasuredValue getVerticalAngleWithoutTilt_NI_V()
  {
    return h;
  }
  
  public void setVerticalAngleWithouthTilt_NI_V(float paramFloat)
    throws IllegalArgumentCheckedException
  {
    h = MeasurementConverter.convertAngle(new MeasuredValue(paramFloat, m));
  }
  
  public int getIState()
  {
    return i;
  }
  
  public void setiState(short paramShort)
  {
    i = paramShort;
    Logs.log(Logs.LogTypes.debug, "setiState: " + paramShort);
  }
  
  public float getIhz()
  {
    return j;
  }
  
  public void setIhz(float paramFloat)
  {
    j = paramFloat;
  }
  
  public float getILen()
  {
    return k;
  }
  
  public void setILen(float paramFloat)
  {
    k = paramFloat;
  }
  
  public float getICross()
  {
    return l;
  }
  
  public void setICross(float paramFloat)
  {
    l = paramFloat;
  }
}
