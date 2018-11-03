package ch.leica.sdk.ErrorHandling;

import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.HashMap;
import java.util.Map;

public final class ErrorObject
{
  private static final Map<Integer, String> a = new HashMap();
  private int b;
  private String c;
  
  public ErrorObject(int paramInt, String paramString)
  {
    b = paramInt;
    c = (paramString + " " + a(paramString));
    Logs.log(Logs.LogTypes.debug, "Error Description Number: " + paramInt + " Error description String: " + c);
  }
  
  public int getErrorCode()
  {
    return b;
  }
  
  public void setErrorCode(int paramInt)
  {
    b = paramInt;
  }
  
  public String getErrorMessage()
  {
    return c;
  }
  
  public void setErrorMessage(String paramString)
  {
    c = (paramString + " " + a(paramString));
  }
  
  public static void sendErrorBluetoothIsOff(ErrorListener paramErrorListener, Object paramObject)
  {
    if (paramErrorListener != null) {
      paramErrorListener.onError(new ErrorObject(2102, "BluetoothAdapter is not enabled. "), null);
    }
  }
  
  public static void sendErrorWifiIsOff(ErrorListener paramErrorListener, Object paramObject)
  {
    if (paramErrorListener == null) {
      return;
    }
    paramErrorListener.onError(new ErrorObject(2804, "Wifi adapter is not enabled. "), null);
  }
  
  public static void sendErrorIncorrectSSID(ErrorListener paramErrorListener, Object paramObject)
  {
    if (paramErrorListener == null) {
      return;
    }
    paramErrorListener.onError(new ErrorObject(2802, "The SSID of the WIFI does not match the SSID connected before. WIFI connection lost or changed? "), null);
  }
  
  public static void sendErrorDeviceNotConnected(String paramString, ErrorListener paramErrorListener, Object paramObject)
  {
    if (paramErrorListener == null) {
      return;
    }
    paramErrorListener.onError(new ErrorObject(3002, "Device is not connected. deviceId: " + paramString), null);
  }
  
  public static void sendErrorEventChannelSocketNotConnecting(String paramString, ErrorListener paramErrorListener, Object paramObject)
  {
    if (paramErrorListener == null) {
      return;
    }
    paramErrorListener.onError(new ErrorObject(3102, "Socket for event channel could not be established. error: " + paramString), null);
  }
  
  public static void sendErrorResponseChannelSocketNotConnecting(String paramString, ErrorListener paramErrorListener, Object paramObject)
  {
    if (paramErrorListener == null) {
      return;
    }
    paramErrorListener.onError(new ErrorObject(3302, "Socket for response channel could not be established. error: " + paramString), null);
  }
  
  public static void sendErrorReconnectionOnConnectedDeviceIdNotEqual(ErrorListener paramErrorListener, Object paramObject)
  {
    if (paramErrorListener == null) {
      return;
    }
    paramErrorListener.onError(new ErrorObject(3202, "The deviceId which was reconnected to does not equal the deviceId that should have been connected to. "), null);
  }
  
  public static void sendErrorResponseTimeout(ErrorListener paramErrorListener, Object paramObject)
  {
    if (paramErrorListener != null) {
      paramErrorListener.onError(new ErrorObject(2104, "Did not receive a response after 12000 milliseconds"), null);
    }
  }
  
  public static void sendErrorReceivedErrorFromDevice(ErrorListener paramErrorListener, Object paramObject, String paramString)
  {
    if (paramErrorListener != null) {
      paramErrorListener.onError(new ErrorObject(2702, "Error received from device. " + paramString), null);
    }
  }
  
  public static void sendErrorBluetoothDeviceNotPaired(ErrorListener paramErrorListener, Object paramObject)
  {
    if (paramErrorListener != null) {
      paramErrorListener.onError(new ErrorObject(2106, "Device is not paired. Please go to the settings and pair with the device first."), null);
    }
  }
  
  public static void sendErrorBluetoothDeviceUnableToPair(ErrorListener paramErrorListener, Object paramObject)
  {
    if (paramErrorListener != null) {
      paramErrorListener.onError(new ErrorObject(2108, "Unable to pair with the device. Please go to settings and try pairing again."), null);
    }
  }
  
  public static void sendErrorHotspotDeviceIpNotReachable(ErrorListener paramErrorListener, Object paramObject)
  {
    if (paramErrorListener != null) {
      paramErrorListener.onError(new ErrorObject(2504, "Device's IP is not reachable. "), null);
    }
  }
  
  public static void sendErrorAPDeviceIpNotReachable(ErrorListener paramErrorListener, Object paramObject)
  {
    if (paramErrorListener != null) {
      paramErrorListener.onError(new ErrorObject(2504, "Device's IP is not reachable. "), null);
    }
  }
  
  public static void sendErrorResponseNotParseable(ErrorListener paramErrorListener, Object paramObject)
  {
    if (paramErrorListener != null) {
      paramErrorListener.onError(new ErrorObject(2704, "Error received from device. "), null);
    }
  }
  
  private static String a(String paramString)
  {
    String str1 = "";
    try
    {
      String str2 = paramString.replaceAll("[^0-9]", "");
      int i = Integer.valueOf(str2).intValue();
      str1 = getErrorDescription(Integer.valueOf(i));
    }
    catch (Exception localException)
    {
      str1 = "";
    }
    if (str1 == null) {
      str1 = "";
    }
    return str1;
  }
  
  public static Map<Integer, String> getErrors()
  {
    return a;
  }
  
  public static void setErrors(String paramString1, String paramString2)
    throws IllegalArgumentCheckedException
  {
    if ((paramString1 != null) && (paramString2 != null)) {
      try
      {
        int i = 0;
        Integer localInteger = Integer.valueOf(-1);
        try
        {
          localInteger = Integer.valueOf(paramString1);
          if (!paramString2.isEmpty()) {
            i = 1;
          }
        }
        catch (NumberFormatException localNumberFormatException)
        {
          i = 0;
        }
        if ((i != 0) && (localInteger.intValue() != -1))
        {
          a.put(localInteger, paramString2);
        }
        else
        {
          Logs.log(Logs.LogTypes.codeerror, " Caused by Value entry:  wrong format.  " + paramString1 + " - " + "Please validate the corresponding JSON file.");
          throw new IllegalArgumentCheckedException(" Caused by Value entry:  wrong format.  " + paramString1);
        }
      }
      catch (Exception localException)
      {
        Logs.log(Logs.LogTypes.exception, " Caused by Value entry:  " + paramString1 + " - " + "Please validate the corresponding JSON file.");
        throw new IllegalArgumentCheckedException(" Caused by Value entry:  " + paramString1, localException);
      }
    } else {
      throw new IllegalArgumentCheckedException(" Caused by Value entry:   Wrong parameters sent to the function: Key and/or Value are null ");
    }
  }
  
  public static String getErrorDescription(Integer paramInteger)
  {
    if (paramInteger != null) {
      return (String)a.get(paramInteger);
    }
    Logs.log(Logs.LogTypes.codeerror, " Caused by Value entry:   Wrong parameters sent to the function:errorCode is null");
    return null;
  }
}
