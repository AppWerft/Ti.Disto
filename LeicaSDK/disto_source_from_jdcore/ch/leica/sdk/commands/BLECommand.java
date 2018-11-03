package ch.leica.sdk.commands;

import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BLECommand
  extends Command
{
  static final Map<Types.Commands, Command.a> a = new HashMap();
  
  public BLECommand() {}
  
  public BLECommand(String paramString)
  {
    a();
    preparePayload(paramString);
    commandValue = Types.Commands.Custom;
  }
  
  public BLECommand(Types.Commands paramCommands)
  {
    a();
    commandValue = paramCommands;
    preparePayload(commandValue, new ArrayList());
  }
  
  public BLECommand(byte[] paramArrayOfByte)
  {
    a();
    preparePayload(paramArrayOfByte);
  }
  
  private void a()
  {
    receivedData = new ReceivedData();
    hasTerminator = false;
  }
  
  public void preparePayload(String paramString)
  {
    if (paramString != null)
    {
      setPayload(paramString, hasTerminator, null);
    }
    else
    {
      Logs.log(Logs.LogTypes.codeerror, "Caused by: null command");
      payload = "";
    }
  }
  
  public void preparePayload(Types.Commands paramCommands, List<String> paramList)
  {
    Logs.log(Logs.LogTypes.verbose, " Called.");
    ArrayList localArrayList = new ArrayList();
    String str = getCommand(paramCommands);
    if (str != null)
    {
      if (paramList.size() > 0)
      {
        Logs.log(Logs.LogTypes.codeerror, "Caused by: Wrong use of the preparePayload function. BLE Commands do not have parameters");
        payload = "";
        return;
      }
      setPayload(str, hasTerminator, localArrayList);
    }
    else
    {
      Logs.log(Logs.LogTypes.codeerror, "Caused by: Terminator not Existing or null command");
      payload = "";
    }
  }
  
  public static void setCommand(Types.Commands paramCommands, String paramString1, String paramString2, String paramString3)
    throws IllegalArgumentCheckedException
  {
    if ((paramCommands != null) && (paramString1 != null)) {
      try
      {
        int i = 0;
        if (isCommandCorrect(paramString1, paramString2, paramString3)) {
          i = 1;
        }
        if (i != 0)
        {
          a.put(paramCommands, new Command.a(paramString1, paramString2, paramString3));
        }
        else
        {
          Logs.log(Logs.LogTypes.codeerror, " Caused by Value entry:  wrong format.  " + paramCommands + " - " + "Please validate the corresponding JSON file.");
          throw new IllegalArgumentCheckedException(" Caused by Value entry:  wrong format.  " + paramCommands);
        }
      }
      catch (Exception localException)
      {
        Logs.log(Logs.LogTypes.codeerror, " Verify if the corresponding enum entry in Types exists.  Caused by Value entry: " + paramCommands + " - " + "Please validate the corresponding JSON file.");
        throw new IllegalArgumentCheckedException(" Caused by Value entry:  Verify if the corresponding enum entry in Types exists. " + paramString1 + paramCommands, localException);
      }
    } else {
      throw new IllegalArgumentCheckedException(" Caused by Value entry:   Wrong parameters sent to the function: Key and/or Value are null ");
    }
  }
  
  public static String getCommand(Types.Commands paramCommands)
  {
    try
    {
      if (paramCommands != null) {
        return ((Command.a)a.get(paramCommands)).b();
      }
      Logs.log(Logs.LogTypes.codeerror, " Caused by Value entry:   Wrong parameters sent to the function:Command or CommandType are null");
      return null;
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.codeerror, " Caused by Value entry:  Command not found. " + paramCommands.toString());
    }
    return null;
  }
  
  public Boolean hasCommandResponse()
  {
    if (getCommandValue() == null) {
      return Boolean.valueOf(false);
    }
    switch (1.a[getCommandValue().ordinal()])
    {
    case 1: 
      return Boolean.valueOf(true);
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
      return Boolean.valueOf(false);
    }
    Logs.log(Logs.LogTypes.codeerror, "hasCommand Response called with wrong Value. ");
    return Boolean.valueOf(false);
  }
}
