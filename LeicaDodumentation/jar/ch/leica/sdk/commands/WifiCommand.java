package ch.leica.sdk.commands;

import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WifiCommand
  extends Command
{
  static Map<Types.Commands, Command.a> a = new HashMap();
  
  public WifiCommand(String paramString)
  {
    a();
    preparePayload(paramString);
    commandValue = Types.Commands.Custom;
  }
  
  public WifiCommand(Types.Commands paramCommands)
  {
    a();
    preparePayload(paramCommands, new ArrayList());
    commandValue = paramCommands;
  }
  
  public WifiCommand(Types.Commands paramCommands, List<String> paramList)
  {
    a();
    preparePayload(paramCommands, paramList);
    commandValue = paramCommands;
  }
  
  private void a()
  {
    receivedData = new ReceivedData();
    hasTerminator = true;
  }
  
  public void preparePayload(Types.Commands paramCommands, List<String> paramList)
  {
    Object localObject = new ArrayList();
    String str1 = getCommand(paramCommands);
    if (str1 != null)
    {
      if (paramList.size() == 0)
      {
        String str2 = getCommandParameter(paramCommands);
        if ((str2 != null) && (!str2.isEmpty())) {
          ((List)localObject).add(str2);
        }
      }
      else
      {
        localObject = paramList;
      }
      setPayload(str1, hasTerminator, (List)localObject);
    }
    else
    {
      Logs.log(Logs.LogTypes.codeerror, "Caused by: Terminator not Existing or null command");
      payload = "";
    }
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
      Logs.log(Logs.LogTypes.exception, " Caused by Value entry:  Command not found. " + paramCommands.toString());
    }
    return null;
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
  
  public static String getCommandParameter(Types.Commands paramCommands)
  {
    if (paramCommands != null)
    {
      if (a.containsKey(paramCommands)) {
        return ((Command.a)a.get(paramCommands)).a();
      }
      return null;
    }
    Logs.log(Logs.LogTypes.codeerror, " Caused by Value entry:   Wrong parameters sent to the function:command is null");
    return null;
  }
}
