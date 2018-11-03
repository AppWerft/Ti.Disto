package ch.leica.sdk.commands;

import ch.leica.sdk.ErrorHandling.WrongDataException;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;

public class ReceivedDataPacket
{
  public String dataId;
  public String response = "";
  
  ReceivedDataPacket(String paramString)
  {
    Logs.log(Logs.LogTypes.verbose, "ID: " + paramString + " bytes");
    dataId = paramString;
  }
  
  public ReceivedDataPacket() {}
  
  public String getModelName()
  {
    return "";
  }
  
  public Image getImage()
    throws WrongDataException
  {
    return null;
  }
}
