package ch.leica.sdk.commands;

import android.support.annotation.VisibleForTesting;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;

public final class ReceivedData
{
  private String a = "";
  public ReceivedDataPacket dataPacket;
  private final String b = ": ";
  private final String c = "!";
  private final String d = "@E";
  private final String e = "empty";
  private String f;
  
  public ReceivedData() {}
  
  private ErrorObject a(String paramString1, String paramString2)
  {
    Logs.log(Logs.LogTypes.verbose, "ID: " + paramString1 + " receivedDataText: " + paramString2);
    if (paramString2 == null)
    {
      Logs.log(Logs.LogTypes.codeerror, "Null Response Text");
      return new ErrorObject(2704, "Unknown response received.");
    }
    f = "";
    if (paramString2.isEmpty())
    {
      Logs.log(Logs.LogTypes.codeerror, "Empty Response Text");
      return new ErrorObject(2704, "Unknown response received.");
    }
    return null;
  }
  
  private void a(ReceivedDataPacket paramReceivedDataPacket)
  {
    dataPacket = paramReceivedDataPacket;
  }
  
  private String a(String paramString)
  {
    Logs.log(Logs.LogTypes.verbose, "receivedDataText: " + paramString);
    if (paramString == null)
    {
      Logs.log(Logs.LogTypes.codeerror, "Null Response Text");
      return null;
    }
    if (paramString.isEmpty())
    {
      Logs.log(Logs.LogTypes.codeerror, "Empty Response Text");
      return null;
    }
    if (paramString.contains("\r")) {
      paramString = paramString.split("\r")[0];
    }
    f = "";
    int i = paramString.indexOf(": ");
    if (i <= 0)
    {
      Logs.log(Logs.LogTypes.debug, "Wrong Response Text");
      return null;
    }
    f = paramString.substring(0, i);
    String str = paramString.substring(i + ": ".length()).trim();
    Logs.log(Logs.LogTypes.verbose, "commandStr: " + f + ", receivedData: " + str);
    return str;
  }
  
  public void parseReceivedWifiData(String paramString)
    throws DeviceException
  {
    if (paramString != null)
    {
      String str = a(paramString);
      if (str != null)
      {
        if ("!".equals(str))
        {
          Logs.log(Logs.LogTypes.debug, "Successfully executed command");
        }
        else if (paramString.startsWith("empty"))
        {
          Logs.log(Logs.LogTypes.debug, "Empty GetBattery: id: EVBAT value:1");
          try
          {
            ReceivedWifiDataPacket localReceivedWifiDataPacket1 = new ReceivedWifiDataPacket(f, "empty");
            a(localReceivedWifiDataPacket1);
          }
          catch (IllegalArgumentCheckedException localIllegalArgumentCheckedException1)
          {
            Logs.log(Logs.LogTypes.exception, "Error Creating the Wifi Packet", localIllegalArgumentCheckedException1);
          }
        }
        else
        {
          getClass();
          if (str.startsWith("@E")) {
            throw new DeviceException(str.substring(1));
          }
          try
          {
            ReceivedWifiDataPacket localReceivedWifiDataPacket2 = new ReceivedWifiDataPacket(f, str);
            a(localReceivedWifiDataPacket2);
          }
          catch (IllegalArgumentCheckedException localIllegalArgumentCheckedException2)
          {
            Logs.log(Logs.LogTypes.exception, "Error Caused by: ", localIllegalArgumentCheckedException2);
          }
          catch (DeviceException localDeviceException)
          {
            Logs.log(Logs.LogTypes.exception, "Error Caused by: ", localDeviceException);
            throw new DeviceException(str.substring(1));
          }
        }
      }
      else {
        throw new DeviceException("Null Response Text");
      }
    }
    else
    {
      throw new DeviceException("Null Received Text");
    }
  }
  
  public void setLiveImagePacket(byte[] paramArrayOfByte)
  {
    try
    {
      ReceivedWifiDataPacket localReceivedWifiDataPacket = new ReceivedWifiDataPacket();
      localReceivedWifiDataPacket.setLiveImagePacket(paramArrayOfByte);
      a(localReceivedWifiDataPacket);
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.exception, "Error Caused by: ", localException);
    }
  }
  
  public ErrorObject parseYetiReceivedData(String paramString, byte[] paramArrayOfByte)
  {
    try
    {
      ErrorObject localErrorObject = null;
      Object localObject;
      if ((paramString.equals("IMU_DISTOCOM_TRANSMIT")) || (paramString.equals("IMU_DISTOCOM_EVENT")))
      {
        localObject = new String(paramArrayOfByte);
        b((String)localObject);
        if ((((String)localObject).endsWith("\r\n")) || (((String)localObject).endsWith("\n")) || (((String)localObject).endsWith("\r")))
        {
          Logs.log(Logs.LogTypes.debug, "YetiDataPacket saved.");
          localErrorObject = a(paramString, a);
          if (localErrorObject == null)
          {
            a(new ReceivedYetiDataPacket(paramString, a));
            Logs.log(Logs.LogTypes.debug, "Complete distocomReceivedMessage = " + a);
          }
        }
      }
      else
      {
        localObject = new ReceivedYetiDataPacket(paramString, paramArrayOfByte);
        a((ReceivedDataPacket)localObject);
      }
      return localErrorObject;
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.exception, "Error receiving data. ", localException);
    }
    return new ErrorObject(3504, "Unknown response received.");
  }
  
  private void b(String paramString)
  {
    Logs.log(Logs.LogTypes.verbose, "DistocomPartialMessage: " + paramString);
    a += paramString;
    Logs.log(Logs.LogTypes.verbose, "DistocomString: " + a);
  }
  
  public ErrorObject parseBleReceivedData(String paramString, byte[] paramArrayOfByte)
  {
    try
    {
      a(new ReceivedBleDataPacket(paramString, paramArrayOfByte));
      return null;
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.exception, "Error receiving data. ", localException);
    }
    return new ErrorObject(3504, "Unknown response received.");
  }
  
  @VisibleForTesting
  public String getCommandStr()
  {
    return f;
  }
}
