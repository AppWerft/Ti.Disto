package ch.leica.sdk.commands.response;

import ch.leica.sdk.Types.Commands;

public final class ResponseUpdate
  extends Response
{
  private String e;
  private String f = "!";
  private int g = -1;
  
  public ResponseUpdate(Types.Commands paramCommands)
  {
    super(paramCommands);
  }
  
  public int getIsUpdateMode()
  {
    return g;
  }
  
  public void setIsUpdateMode(int paramInt)
  {
    g = paramInt;
  }
  
  public void setDataString(String paramString)
  {
    e = paramString.trim();
  }
  
  public boolean isCallSuccessful()
  {
    validateError(e);
    return (e != null) && (e.contains(f));
  }
  
  public String getDataString()
  {
    return e;
  }
}
