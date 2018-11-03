package ch.leica.sdk.logging;

import android.util.Log;
import ch.leica.sdk.LeicaSdk;

public final class Logs
{
  private Logs() {}
  
  public static synchronized void log(LogTypes paramLogTypes, String paramString, Object paramObject)
  {
    String str;
    switch (1.a[paramLogTypes.ordinal()])
    {
    case 1: 
      if (LeicaSdk.ERROR)
      {
        str = a();
        Log.e("SDK - CodeError : ", str + " - " + paramString);
      }
      break;
    case 2: 
      if (paramObject == null)
      {
        if (LeicaSdk.ERROR)
        {
          str = a();
          Log.e("SDK - Exception : ", str + " - Caused by: " + paramString);
        }
      }
      else if (LeicaSdk.ERROR)
      {
        str = a();
        Log.e("SDK - Exception : ", str + " - Caused by: " + paramString, (Exception)paramObject);
      }
      break;
    case 3: 
      if (LeicaSdk.ERROR)
      {
        str = a();
        Log.wtf("SDK - CriticalExc : ", str + " - Caused by: " + paramString, (Exception)paramObject);
      }
      break;
    default: 
      if (LeicaSdk.ERROR)
      {
        str = a();
        Log.e("SDK - Unknown : ", str + " - " + paramString);
      }
      break;
    }
  }
  
  public static synchronized void log(LogTypes paramLogTypes, String paramString)
  {
    String str;
    switch (1.a[paramLogTypes.ordinal()])
    {
    case 4: 
      if (LeicaSdk.VERBOSE)
      {
        str = a();
        Log.d("SDK - Verbose : ", str + " - " + paramString);
      }
      break;
    case 5: 
      if (LeicaSdk.DEBUG)
      {
        str = a();
        Log.d("SDK - Debug : ", str + " - " + paramString);
      }
      break;
    case 6: 
      if (paramString.length() > 100)
      {
        if (LeicaSdk.INFO)
        {
          str = a();
          Log.i("SDK - Info : ", "(truncated on 100 characters)" + str + " - " + paramString.substring(0, 100));
        }
      }
      else if (LeicaSdk.INFO)
      {
        str = a();
        Log.i("SDK - Info : ", str + " - " + paramString);
      }
      break;
    case 7: 
      if (LeicaSdk.INFO)
      {
        str = a();
        Log.i("SDK - Info : ", str + " - " + paramString);
      }
      break;
    case 8: 
      if (LeicaSdk.WARN)
      {
        str = a();
        Log.w("SDK - Warn : ", str + " - " + paramString);
      }
      break;
    case 1: 
      if (LeicaSdk.ERROR)
      {
        str = a();
        Log.e("SDK - CodeError : ", str + " - " + paramString);
      }
      break;
    case 2: 
    case 3: 
    default: 
      if (LeicaSdk.INFO)
      {
        str = a();
        Log.e("SDK - Unknown : ", str + " - " + paramString);
      }
      break;
    }
  }
  
  private static String a()
  {
    Exception localException = new Exception();
    String str = localException.getStackTrace()[2].getClassName() + "." + localException.getStackTrace()[2].getMethodName();
    return str;
  }
  
  public static enum LogTypes
  {
    private LogTypes() {}
  }
}
