package ch.leica.sdk.connection.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.Build.VERSION;
import android.os.ParcelUuid;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Utilities.SerialNumberHelper;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class BleScanCallback
  extends ScanCallback
{
  private BleScanDevicesListener a;
  
  public BleScanCallback(BleScanDevicesListener paramBleScanDevicesListener)
  {
    a = paramBleScanDevicesListener;
  }
  
  public void onScanResult(int paramInt, ScanResult paramScanResult)
  {
    a.onScanCallbackSuccessful();
    if (paramScanResult != null)
    {
      boolean bool = false;
      Object localObject;
      String str1;
      if (Build.VERSION.SDK_INT >= 21)
      {
        if ((paramScanResult.getScanRecord() != null) && (paramScanResult.getScanRecord().getServiceUuids() != null))
        {
          localObject = paramScanResult.getScanRecord().getServiceUuids().iterator();
          while (((Iterator)localObject).hasNext())
          {
            ParcelUuid localParcelUuid = (ParcelUuid)((Iterator)localObject).next();
            Logs.log(Logs.LogTypes.verbose, "service uuid: " + localParcelUuid);
            String str2 = localParcelUuid.toString().toLowerCase();
            String str3 = BleConnectionManager.DISTO_SERVICE.toString().toLowerCase();
            if ((str2.equalsIgnoreCase(str3)) || (str2.contains(str3)) || (str3.contains(str2)))
            {
              Logs.log(Logs.LogTypes.verbose, "disto service found: " + localParcelUuid);
              bool = true;
            }
          }
        }
        str1 = paramScanResult.getScanRecord().getDeviceName();
        if ((str1 != null) && (str1.length() <= 10))
        {
          str1 = str1 + " " + SerialNumberHelper.getDeviceSerialNumber(paramScanResult.getScanRecord());
          Logs.log(Logs.LogTypes.verbose, "DeviceName: " + str1);
          if (str1.length() <= 10) {
            Logs.log(Logs.LogTypes.verbose, "Still no Serial");
          }
        }
      }
      else
      {
        return;
      }
      Logs.log(Logs.LogTypes.verbose, "deviceName: " + str1 + ", deviceAdress: " + paramScanResult.getDevice().getAddress() + ", bondState: " + paramScanResult.getDevice().getBondState());
      if (str1 != null)
      {
        if (Build.VERSION.SDK_INT >= 21) {
          localObject = paramScanResult.getDevice();
        } else {
          return;
        }
        if (LeicaSdk.validDeviceName(str1))
        {
          Logs.log(Logs.LogTypes.debug, " matches if inside called for: " + str1);
          a.onBluetoothDeviceFound(str1, (BluetoothDevice)localObject, bool, false);
        }
      }
    }
  }
  
  public void onBatchScanResults(List<ScanResult> paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      ScanResult localScanResult = (ScanResult)localIterator.next();
      if (Build.VERSION.SDK_INT >= 21) {
        Logs.log(Logs.LogTypes.verbose, "ScanResult - Results" + localScanResult.toString());
      }
    }
  }
  
  public void onScanFailed(int paramInt)
  {
    Logs.log(Logs.LogTypes.verbose, "Scan Failed, Error Code: " + paramInt);
  }
  
  public static abstract interface BleScanDevicesListener
  {
    public abstract void onBluetoothDeviceFound(String paramString, BluetoothDevice paramBluetoothDevice, boolean paramBoolean1, boolean paramBoolean2);
    
    public abstract void onScanCallbackSuccessful();
  }
}
