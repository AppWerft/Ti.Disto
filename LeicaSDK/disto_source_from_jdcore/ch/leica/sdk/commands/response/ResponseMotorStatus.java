package ch.leica.sdk.commands.response;

import ch.leica.sdk.Types.Commands;

public final class ResponseMotorStatus
  extends Response
{
  private int e;
  private int f;
  
  public ResponseMotorStatus(Types.Commands paramCommands)
  {
    super(paramCommands);
  }
  
  public int getMotorStatusVerticalAxis()
  {
    return e;
  }
  
  public void setMotorStatusVerticalAxis(int paramInt)
  {
    e = paramInt;
  }
  
  public int getMotorStatusHorizontalAxis()
  {
    return f;
  }
  
  public void setMotorStatusHorizontalAxis(int paramInt)
  {
    f = paramInt;
  }
}
