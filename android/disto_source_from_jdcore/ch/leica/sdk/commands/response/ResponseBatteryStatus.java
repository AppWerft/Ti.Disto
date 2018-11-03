package ch.leica.sdk.commands.response;

import ch.leica.sdk.Types.Commands;

public final class ResponseBatteryStatus
  extends Response
{
  private float e = -9999.0F;
  private int f = 55537;
  
  public ResponseBatteryStatus(Types.Commands paramCommands)
  {
    super(paramCommands);
  }
  
  public float getBatteryVoltage()
  {
    return e;
  }
  
  public void setBatteryVoltage(float paramFloat)
  {
    e = paramFloat;
  }
  
  public int getBatteryStatus()
  {
    return f;
  }
  
  public void setBatteryStatus(int paramInt)
  {
    f = paramInt;
  }
}
