package ch.leica.sdk.commands.response;

import ch.leica.sdk.Types.Commands;

public final class ResponsePlain
  extends Response
{
  String e;
  
  public ResponsePlain(Types.Commands paramCommands)
  {
    super(paramCommands);
  }
  
  public String getReceivedDataString()
  {
    return e;
  }
  
  public void setDataString(String paramString)
  {
    e = paramString;
    validateError(e);
  }
  
  public String[] getResponseSegments(String paramString)
  {
    String[] arrayOfString = null;
    if (e != null)
    {
      arrayOfString = e.split(paramString);
      for (int i = 0; i < arrayOfString.length; i++) {
        arrayOfString[i] = arrayOfString[i].trim();
      }
    }
    else
    {
      arrayOfString = new String[0];
    }
    return arrayOfString;
  }
}
