package ch.leica.sdk.commands;

import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DistocomCommand
  extends Command
{
  public DistocomCommand(String paramString, boolean paramBoolean)
  {
    commandValue = Types.Commands.Custom;
    a();
    hasTerminator = paramBoolean;
    preparePayload(paramString);
  }
  
  public DistocomCommand(Types.Commands paramCommands)
  {
    commandValue = paramCommands;
    a();
    preparePayload(commandValue, new ArrayList());
  }
  
  public DistocomCommand(Types.Commands paramCommands, List<String> paramList)
  {
    commandValue = paramCommands;
    a();
    preparePayload(commandValue, paramList);
  }
  
  public DistocomCommand(Types.Commands paramCommands, byte[] paramArrayOfByte)
  {
    commandValue = paramCommands;
    a();
    preparePayload(paramArrayOfByte);
  }
  
  private void a()
  {
    receivedData = new ReceivedData();
    hasTerminator = commandHasTerminator(commandValue);
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
    ArrayList localArrayList = new ArrayList();
    String str1 = BLECommand.getCommand(paramCommands);
    Logs.log(Logs.LogTypes.debug, "commandString: " + str1);
    if (str1 != null)
    {
      String str2 = getCommandParameter(paramCommands);
      Logs.log(Logs.LogTypes.debug, "commandParams: " + str2);
      if ((str2 != null) && (!str2.isEmpty())) {
        localArrayList.add(str2);
      }
      if (!paramList.isEmpty()) {
        localArrayList.addAll(paramList);
      }
      setPayload(str1, hasTerminator, localArrayList);
    }
    else
    {
      Logs.log(Logs.LogTypes.codeerror, "Caused by: Terminator not Existing or null command");
      payload = "";
    }
  }
  
  public static String getCommandParameter(Types.Commands paramCommands)
  {
    if (paramCommands != null)
    {
      if (BLECommand.a.containsKey(paramCommands)) {
        return ((Command.a)BLECommand.a.get(paramCommands)).a();
      }
      return "";
    }
    Logs.log(Logs.LogTypes.codeerror, " Caused by Value entry:   Wrong parameters sent to the function:command is null");
    return null;
  }
  
  public boolean commandHasTerminator(Types.Commands paramCommands)
  {
    if (paramCommands == null) {
      return false;
    }
    switch (1.a[paramCommands.ordinal()])
    {
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    case 16: 
    case 17: 
    case 18: 
    case 19: 
    case 20: 
      return true;
    case 21: 
      return false;
    }
    return false;
  }
}
