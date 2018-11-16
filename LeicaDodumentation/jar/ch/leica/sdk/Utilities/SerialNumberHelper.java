package ch.leica.sdk.Utilities;

import android.bluetooth.le.ScanRecord;
import android.os.Build.VERSION;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class SerialNumberHelper
{
  public SerialNumberHelper() {}
  
  public static String getDeviceSerialNumber(ScanRecord paramScanRecord)
  {
    String str = null;
    if (paramScanRecord != null) {
      if (Build.VERSION.SDK_INT >= 21) {
        try
        {
          Map localMap = paramScanRecord.getServiceData();
          Iterator localIterator = localMap.entrySet().iterator();
          while (localIterator.hasNext())
          {
            Map.Entry localEntry = (Map.Entry)localIterator.next();
            byte[] arrayOfByte = (byte[])localEntry.getValue();
            str = new String(arrayOfByte).trim();
            Logs.log(Logs.LogTypes.verbose, " - Key:" + localEntry.getKey() + " - DeviceName: " + paramScanRecord.getDeviceName() + " " + str);
          }
        }
        catch (Exception localException)
        {
          str = null;
          Logs.log(Logs.LogTypes.exception, "Error obtaining the Serial Number: " + paramScanRecord.getDeviceName());
        }
      } else {
        str = null;
      }
    }
    return str;
  }
}
