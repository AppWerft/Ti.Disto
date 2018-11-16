package ch.leica.sdk.Reconnection;

import android.content.Context;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;

public class Gatt133ErrorHandler
  implements ErrorListener, ReconnectionHelper.ReconnectListener
{
  Device a;
  Gatt133ErrorHandlerListener b;
  ReconnectionHelper c;
  int d = 0;
  final int e = 6;
  
  public Gatt133ErrorHandler() {}
  
  public void handleGatt133Error(Context paramContext, Device paramDevice, Gatt133ErrorHandlerListener paramGatt133ErrorHandlerListener)
  {
    Logs.log(Logs.LogTypes.debug, "called");
    if ((paramContext == null) || (paramDevice == null))
    {
      if (paramGatt133ErrorHandlerListener != null) {
        paramGatt133ErrorHandlerListener.onError(paramDevice, new ErrorObject(3602, "null objects as args"));
      }
      return;
    }
    a = paramDevice;
    a.setConnectionListener(null);
    a.setErrorListener(null);
    b = paramGatt133ErrorHandlerListener;
    c = new ReconnectionHelper(a, paramContext);
    c.setErrorListener(this);
    c.setReconnectListener(this);
    c.bleInitialDelay = 2000;
    c.bleConnectDelay = 3000;
    Logs.log(Logs.LogTypes.debug, "start reconnecting now");
    d = 0;
    c.startReconnecting();
  }
  
  public void onError(ErrorObject paramErrorObject, Device paramDevice)
  {
    Logs.log(Logs.LogTypes.debug, "error received");
    if ((paramErrorObject != null) && (paramErrorObject.getErrorMessage() != null)) {
      Logs.log(Logs.LogTypes.debug, "error message: " + paramErrorObject.getErrorMessage());
    }
    if (paramErrorObject.getErrorCode() == 2112) {
      Logs.log(Logs.LogTypes.debug, "error is gatt 133 error");
    }
    if (paramErrorObject.getErrorCode() == 2114) {
      Logs.log(Logs.LogTypes.debug, "error is gatt 62 error");
    }
    d += 1;
    Logs.log(Logs.LogTypes.debug, "unsuccessfulCounter: " + d);
    if (d <= 6) {
      return;
    }
    stop();
    if (b != null) {
      b.onError(paramDevice, paramErrorObject);
    }
  }
  
  public void onReconnect(Device paramDevice)
  {
    if (b != null)
    {
      if (a.getDeviceID().equalsIgnoreCase(paramDevice.getDeviceID()) == true)
      {
        stop();
        b.onSuccess(paramDevice);
      }
      else
      {
        Logs.log(Logs.LogTypes.codeerror, "device id not equal. this should never happen.");
      }
    }
    else {
      Logs.log(Logs.LogTypes.debug, "no listener is set");
    }
  }
  
  public void stop()
  {
    Logs.log(Logs.LogTypes.debug, "stop");
    if (c != null) {
      c.stopReconnecting();
    }
  }
  
  public static abstract interface Gatt133ErrorHandlerListener
  {
    public abstract void onSuccess(Device paramDevice);
    
    public abstract void onError(Device paramDevice, ErrorObject paramErrorObject);
  }
}
