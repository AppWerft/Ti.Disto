package ch.leica.sdk;

import android.content.Context;
import android.content.res.AssetManager;
import ch.leica.a.a.a;
import ch.leica.sdk.Devices.ScanConfig;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.commands.CommandsParser;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;

public class LeicaSdk
{
  private static final Pattern[] a = { Pattern.compile(".*DISTO.*") };
  private static final Pattern[] b = { Pattern.compile(".*3DD.*") };
  private static final Pattern[] c = { Pattern.compile(".*X3.*|.*X4.*") };
  public static int LOGLEVEL = 2;
  public static boolean VERBOSE = true;
  public static boolean DEBUG = true;
  public static boolean INFO = true;
  public static boolean WARN = true;
  public static boolean ERROR = true;
  public static boolean METHODCALLEDLOG = true;
  public static ScanConfig scanConfig = new ScanConfig();
  public static boolean isInit = false;
  
  private LeicaSdk() {}
  
  public static String getVersion()
  {
    return "1.0.0.2";
  }
  
  public static boolean init(Context paramContext, InitObject paramInitObject)
    throws JSONException, IllegalArgumentCheckedException, IOException
  {
    Logs.log(Logs.LogTypes.debug, "Leica Sdk Version: " + getVersion());
    if (paramInitObject == null)
    {
      Logs.log(Logs.LogTypes.codeerror, "initObject cannot be null");
      return false;
    }
    if (InitObject.a(paramInitObject) == null)
    {
      Logs.log(Logs.LogTypes.codeerror, "initObject.commandsFile cannot be null");
      return false;
    }
    InputStream localInputStream = paramContext.getAssets().open(InitObject.a(paramInitObject));
    new CommandsParser(localInputStream);
    isInit = true;
    return true;
  }
  
  public static void setLicenses(ArrayList<String> paramArrayList)
  {
    a.a(paramArrayList);
  }
  
  public static boolean validDeviceName(String paramString)
  {
    if (paramString != null) {
      return (isDisto3DName(paramString)) || (isDistoGenericName(paramString)) || (isYetiName(paramString));
    }
    return false;
  }
  
  public static boolean isDisto3DName(String paramString)
  {
    if (paramString != null) {
      try
      {
        for (Pattern localPattern : b) {
          if (localPattern.matcher(paramString).matches()) {
            return true;
          }
        }
      }
      catch (Exception localException)
      {
        Logs.log(Logs.LogTypes.exception, "Invalid Disto3DName: " + paramString, localException);
      }
    }
    return false;
  }
  
  public static boolean isDistoGenericName(String paramString)
  {
    if (paramString != null) {
      try
      {
        for (Pattern localPattern : a) {
          if (localPattern.matcher(paramString).matches()) {
            return true;
          }
        }
      }
      catch (Exception localException)
      {
        Logs.log(Logs.LogTypes.exception, "Invalid DistoGenericName: " + paramString, localException);
      }
    }
    return false;
  }
  
  public static boolean isYetiName(String paramString)
  {
    if (paramString != null) {
      try
      {
        for (Pattern localPattern : c) {
          if (localPattern.matcher(paramString).matches()) {
            return true;
          }
        }
      }
      catch (Exception localException)
      {
        Logs.log(Logs.LogTypes.exception, "Invalid YETIName: " + paramString, localException);
      }
    }
    return false;
  }
  
  public static void setLogLevel(int paramInt)
  {
    LOGLEVEL = paramInt;
    VERBOSE = LOGLEVEL <= 2;
    DEBUG = LOGLEVEL <= 3;
    INFO = LOGLEVEL <= 4;
    WARN = LOGLEVEL <= 5;
    ERROR = LOGLEVEL <= 6;
    Logs.log(Logs.LogTypes.debug, "LOGLEVEL: " + LOGLEVEL + " VERBOSE: " + VERBOSE + " DEBUG: " + DEBUG + " INFO: " + INFO + " WARN: " + WARN + "ERROR: " + ERROR);
  }
  
  public static void setMethodCalledLog(boolean paramBoolean)
  {
    METHODCALLEDLOG = paramBoolean;
  }
  
  public static void setScanConfig(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    scanConfig.setDevices(paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4);
  }
  
  public static class InitObject
  {
    private String a;
    
    public InitObject(String paramString)
    {
      a = paramString;
    }
  }
}
