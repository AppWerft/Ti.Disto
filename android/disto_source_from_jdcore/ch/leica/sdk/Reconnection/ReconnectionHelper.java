package ch.leica.sdk.Reconnection;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.Device.ConnectionListener;
import ch.leica.sdk.Devices.Device.ConnectionState;
import ch.leica.sdk.Devices.DeviceManager;
import ch.leica.sdk.Devices.DeviceManager.FoundAvailableDeviceListener;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.PermissionException;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Types.ConnectionType;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;

public class ReconnectionHelper
  implements Device.ConnectionListener, DeviceManager.FoundAvailableDeviceListener, ErrorListener
{
  private ReconnectListener f;
  Device a;
  Context b;
  DeviceManager c;
  ErrorListener d;
  private HandlerThread g;
  private Handler h;
  boolean e = false;
  public int bleInitialDelay = 5000;
  public int bleConnectDelay = 3000;
  
  public boolean isReconnectingActive()
  {
    return e;
  }
  
  public ReconnectionHelper(Device paramDevice, Context paramContext)
  {
    a = paramDevice;
    b = paramContext;
    c = DeviceManager.getInstance(paramContext);
    g = new HandlerThread("ReconnectionHelper_Thread_" + System.currentTimeMillis());
    g.start();
    h = new Handler(g.getLooper());
  }
  
  public ReconnectListener getReconnectListener()
  {
    return f;
  }
  
  public void setReconnectListener(ReconnectListener paramReconnectListener)
  {
    f = paramReconnectListener;
  }
  
  public ErrorListener getErrorListener()
  {
    return d;
  }
  
  public void setErrorListener(ErrorListener paramErrorListener)
  {
    d = paramErrorListener;
  }
  
  public void startReconnecting()
  {
    Logs.log(Logs.LogTypes.debug, "START Reconnecting.");
    e = true;
    a.setReceiveDataListener(null);
    a.setErrorListener(null);
    a.setConnectionListener(null);
    a.disconnect();
    int i = 0;
    if (a.getConnectionType() == Types.ConnectionType.wifiHotspot) {
      i = 3000;
    }
    if (a.getConnectionType() == Types.ConnectionType.ble) {
      i = bleInitialDelay;
    } else if (a.getConnectionType() == Types.ConnectionType.wifiAP) {
      i = 10000;
    } else if (a.getConnectionType() == Types.ConnectionType.rndis) {
      i = 3000;
    }
    c.stopFindingDevices();
    h.postDelayed(new Runnable()
    {
      public void run()
      {
        if (b == null)
        {
          Logs.log(Logs.LogTypes.codeerror, "context is null");
          e = false;
          return;
        }
        boolean bool;
        if (a.getConnectionType() == Types.ConnectionType.ble)
        {
          Logs.log(Logs.LogTypes.debug, "deviceToReconnectTo is bluetooth");
          bool = c.checkBluetoothAvailibilty();
          if (!bool)
          {
            Logs.log(Logs.LogTypes.debug, "bluetooth is not available");
            ErrorObject.sendErrorBluetoothIsOff(d, ReconnectionHelper.this);
            e = false;
            return;
          }
        }
        else if (a.getConnectionType() == Types.ConnectionType.wifiAP)
        {
          Logs.log(Logs.LogTypes.debug, "deviceToReconnectTo is wifiHotspot");
          bool = c.checkWifiAvailibilty();
          if (!bool)
          {
            Logs.log(Logs.LogTypes.debug, "wifi is not available");
            ErrorObject.sendErrorWifiIsOff(d, ReconnectionHelper.this);
            e = false;
            return;
          }
        }
        c.setFoundAvailableDeviceListener(ReconnectionHelper.this);
        c.setErrorListener(ReconnectionHelper.this);
        c.registerReceivers(b);
        try
        {
          c.findAvailableDevices(b);
        }
        catch (PermissionException localPermissionException)
        {
          Logs.log(Logs.LogTypes.debug, "Permission exception while reconnecting. NF");
        }
        e = true;
      }
    }, i);
  }
  
  public void setDeviceManager()
  {
    c.setFoundAvailableDeviceListener(this);
    c.setErrorListener(this);
    c.registerReceivers(b);
  }
  
  public void stopReconnecting()
  {
    if ((c == null) || (!e)) {
      return;
    }
    c.setFoundAvailableDeviceListener(null);
    c.setErrorListener(null);
    c.stopFindingDevices();
  }
  
  public void onAvailableDeviceFound(final Device paramDevice)
  {
    if (!paramDevice.getDeviceID().equalsIgnoreCase(a.getDeviceID()))
    {
      Logs.log(Logs.LogTypes.debug, "available device has not the same deviceId: " + paramDevice.getDeviceID());
      Boolean localBoolean = Boolean.valueOf(false);
      String str1 = paramDevice.getDeviceID().substring(paramDevice.getDeviceID().indexOf("+++"));
      String str2 = a.getDeviceID().substring(paramDevice.getDeviceID().indexOf("+++"));
      if (str1.equalsIgnoreCase(str2) == true) {
        localBoolean = Boolean.valueOf(true);
      }
      if (!localBoolean.booleanValue())
      {
        Logs.log(Logs.LogTypes.debug, "device post fix are NOT equal - shouldReconnect: " + localBoolean);
        return;
      }
      Logs.log(Logs.LogTypes.debug, "device post fix are equal - shouldReconnect: " + localBoolean);
    }
    else
    {
      Logs.log(Logs.LogTypes.debug, "deviceIds are equal: " + paramDevice.getDeviceID());
    }
    if (paramDevice.getConnectionType() != a.getConnectionType())
    {
      Logs.log(Logs.LogTypes.debug, "available device has not the same connection type: " + paramDevice.getConnectionType());
      return;
    }
    Logs.log(Logs.LogTypes.debug, "available device has the same connection type: " + paramDevice.getConnectionType());
    c.stopFindingDevices();
    stopReconnecting();
    paramDevice.setConnectionListener(this);
    paramDevice.setErrorListener(this);
    Logs.log(Logs.LogTypes.debug, "connect! ");
    if (a.getConnectionType() == Types.ConnectionType.wifiHotspot)
    {
      h.postDelayed(new Runnable()
      {
        public void run()
        {
          paramDevice.connect();
        }
      }, 7500L);
      return;
    }
    if (a.getConnectionType() == Types.ConnectionType.wifiAP)
    {
      h.postDelayed(new Runnable()
      {
        public void run()
        {
          paramDevice.connect();
        }
      }, 2000L);
      return;
    }
    h.postDelayed(new Runnable()
    {
      public void run()
      {
        paramDevice.connect();
      }
    }, bleConnectDelay);
  }
  
  public void onConnectionStateChanged(Device paramDevice, Device.ConnectionState paramConnectionState)
  {
    Logs.log(Logs.LogTypes.debug, paramDevice.getDeviceID() + ", state: " + paramConnectionState);
    switch (5.a[paramConnectionState.ordinal()])
    {
    case 1: 
      if (!a.getDeviceID().equalsIgnoreCase(paramDevice.getDeviceID()))
      {
        Logs.log(Logs.LogTypes.codeerror, "deviceId " + paramDevice.getDeviceID() + " just connected to does not equal the deviceId that should be: " + a.getDeviceID());
        ErrorObject.sendErrorReconnectionOnConnectedDeviceIdNotEqual(d, this);
        return;
      }
      if (f != null) {
        f.onReconnect(paramDevice);
      } else {
        Logs.log(Logs.LogTypes.debug, "no listener is set");
      }
      e = false;
      break;
    case 2: 
      a = paramDevice;
      startReconnecting();
      break;
    default: 
      Logs.log(Logs.LogTypes.codeerror, "connection State not being handled");
    }
  }
  
  public void onError(ErrorObject paramErrorObject, Device paramDevice)
  {
    Logs.log(Logs.LogTypes.debug, "errorCode: " + paramErrorObject.getErrorCode() + ", message: " + paramErrorObject.getErrorMessage());
    if ((paramErrorObject.getErrorCode() != 2112) && (d != null)) {
      d.onError(paramErrorObject, paramDevice);
    }
  }
  
  public static abstract interface ReconnectListener
  {
    public abstract void onReconnect(Device paramDevice);
  }
}
