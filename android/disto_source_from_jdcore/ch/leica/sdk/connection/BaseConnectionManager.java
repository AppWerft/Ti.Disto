package ch.leica.sdk.connection;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.Build.VERSION;
import ch.leica.sdk.Devices.Device.BTConnectionCallback;
import ch.leica.sdk.Devices.Device.LiveImageSpeed;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Utilities.WaitAmoment;
import ch.leica.sdk.commands.Command;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;

public abstract class BaseConnectionManager
{
  public ConnectionListener connectionListener;
  public WifiReceivedDataListener wifiReceivedDataListener;
  public BleReceivedDataListener bleReceivedDataListener;
  protected boolean shouldScan = false;
  protected Timer findTimer;
  private ConnectionState c;
  private String d;
  private BluetoothDevice e;
  private String f;
  protected boolean stopDiscovery;
  public ScanDevicesListener scanDevicesListener;
  protected Socket socket;
  protected BluetoothGatt currentBluetoothGatt;
  protected Socket eventSocket;
  public ErrorListener errorListener;
  public Context context;
  boolean a = false;
  int b = 3;
  
  public boolean readModelCharacteristic()
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public BaseConnectionManager(Context paramContext)
  {
    setState(ConnectionState.disconnected, true);
    stopDiscovery = false;
    context = paramContext;
  }
  
  protected abstract void connectToDevice();
  
  public abstract void findAvailableDevices();
  
  public abstract void stopScan();
  
  public abstract void stopDiscovery();
  
  public abstract void connect();
  
  public abstract boolean verifyConnection(String paramString);
  
  public abstract boolean checkConnectionMethodsAvailable();
  
  public abstract void setConnectionParameters(Object... paramVarArgs);
  
  public void setBluetoothDevice(BluetoothDevice paramBluetoothDevice)
  {
    e = paramBluetoothDevice;
  }
  
  public BluetoothDevice getBluetoothDevice()
  {
    if (e != null) {
      return e;
    }
    Logs.log(Logs.LogTypes.codeerror, "NULL Value - Connection Parameter BlueToothDevice was not set");
    return null;
  }
  
  public String getSSID()
  {
    if (d != null) {
      return d;
    }
    Logs.log(Logs.LogTypes.codeerror, "NULL Value - SSID was not set", null);
    return null;
  }
  
  public void setSSID(String paramString)
  {
    d = paramString;
  }
  
  public String getIP()
  {
    if (f != null) {
      return f;
    }
    Logs.log(Logs.LogTypes.codeerror, "NULL Value - ip was not set");
    return null;
  }
  
  public void setIP(String paramString)
  {
    f = paramString;
  }
  
  public synchronized ConnectionState getState()
  {
    return c;
  }
  
  public void pauseBTConnection(Device.BTConnectionCallback paramBTConnectionCallback) {}
  
  public void startBTConnection(Device.BTConnectionCallback paramBTConnectionCallback) {}
  
  public abstract void enableFunctionality();
  
  public void connectToResponseChannel(String paramString)
  {
    Logs.log(Logs.LogTypes.verbose, "called with ip: " + paramString);
    InputStream localInputStream = null;
    try
    {
      localInputStream = socket.getInputStream();
    }
    catch (IOException localIOException1)
    {
      Logs.log(Logs.LogTypes.debug, "sleep thread interrupted. NF");
    }
    InputStreamReader localInputStreamReader = null;
    BufferedReader localBufferedReader = null;
    for (;;)
    {
      if (a == true)
      {
        Logs.log(Logs.LogTypes.debug, "connection already killed. return.");
      }
      else
      {
        try
        {
          Thread.sleep(100L);
        }
        catch (InterruptedException localInterruptedException)
        {
          Logs.log(Logs.LogTypes.verbose, "sleep thread interrupted. NF");
        }
        try
        {
          ReceivedData localReceivedData = new ReceivedData();
          Object localObject1;
          if ((socket == null) || (!socket.isConnected()))
          {
            localObject1 = new InetSocketAddress(getIP(), 22222);
            socket = new Socket();
            socket.connect((SocketAddress)localObject1, 3000);
            localInputStream = socket.getInputStream();
            Logs.log(Logs.LogTypes.debug, "connect socket successful");
          }
          try
          {
            if (localInputStream != null)
            {
              localObject1 = "";
              if (localInputStreamReader == null) {
                localInputStreamReader = new InputStreamReader(localInputStream);
              }
              Object localObject2;
              if (Build.VERSION.SDK_INT < 21)
              {
                if (!socket.isClosed())
                {
                  localObject2 = new WaitAmoment();
                  while (!localInputStreamReader.ready()) {
                    ((WaitAmoment)localObject2).waitAmoment(500L);
                  }
                  Logs.log(Logs.LogTypes.verbose, "inputStreamReader is ready!");
                  int i = localInputStream.available();
                  Logs.log(Logs.LogTypes.verbose, "inputStream.available: " + i);
                  char[] arrayOfChar = new char[i];
                  int j = localInputStreamReader.read(arrayOfChar);
                  Logs.log(Logs.LogTypes.verbose, "readCount: " + j);
                  String str = new String(arrayOfChar);
                  Logs.log(Logs.LogTypes.verbose, "buffer string : " + str);
                  ErrorObject localErrorObject = null;
                  try
                  {
                    localReceivedData.parseReceivedWifiData(str);
                  }
                  catch (DeviceException localDeviceException2)
                  {
                    localErrorObject = new ErrorObject(2702, localDeviceException2.getMessage());
                  }
                  if (wifiReceivedDataListener != null) {
                    wifiReceivedDataListener.onResponseReceived(localReceivedData, localErrorObject);
                  } else {
                    Logs.log(Logs.LogTypes.debug, "no receivedData listener is set");
                  }
                }
              }
              else if (!socket.isClosed())
              {
                if (localBufferedReader == null) {
                  localBufferedReader = new BufferedReader(localInputStreamReader, 128000);
                }
                localObject1 = localBufferedReader.readLine();
                if (localObject1 == null)
                {
                  Logs.log(Logs.LogTypes.codeerror, "receivedData is null. sth is wrong. continue the loop.");
                  if (b > 0)
                  {
                    connectToResponseChannel(paramString);
                    b -= 1;
                    continue;
                  }
                  return;
                }
                localObject2 = null;
                try
                {
                  localReceivedData.parseReceivedWifiData((String)localObject1);
                }
                catch (DeviceException localDeviceException1)
                {
                  localObject2 = new ErrorObject(2702, localDeviceException1.getMessage());
                }
                if (wifiReceivedDataListener != null) {
                  wifiReceivedDataListener.onResponseReceived(localReceivedData, (ErrorObject)localObject2);
                } else {
                  Logs.log(Logs.LogTypes.debug, "no receivedData listener is set");
                }
              }
            }
            else
            {
              Logs.log(Logs.LogTypes.codeerror, "inputStream  = NULL, Validate the response channel code");
            }
          }
          catch (Exception localException)
          {
            Logs.log(Logs.LogTypes.exception, "Error Caused by: ", localException);
            if (wifiReceivedDataListener != null) {
              wifiReceivedDataListener.onResponseReceived(null, new ErrorObject(3304, localException.getMessage()));
            } else {
              Logs.log(Logs.LogTypes.debug, "no receivedData listener is set");
            }
          }
        }
        catch (IOException localIOException2)
        {
          Logs.log(Logs.LogTypes.exception, "IOException" + localIOException2.getMessage());
          if (wifiReceivedDataListener != null) {
            wifiReceivedDataListener.onResponseReceived(null, new ErrorObject(3302, localIOException2.getMessage()));
          } else {
            Logs.log(Logs.LogTypes.debug, "no receivedData listener is set");
          }
          setState(ConnectionState.disconnected, true);
        }
      }
    }
  }
  
  public void connectEventChannel(String paramString)
  {
    Logs.log(Logs.LogTypes.debug, "called with ip: " + paramString);
    InputStream localInputStream = null;
    InputStreamReader localInputStreamReader = null;
    BufferedReader localBufferedReader = null;
    for (;;)
    {
      Logs.log(Logs.LogTypes.verbose, "begin of loop");
      if (a == true)
      {
        Logs.log(Logs.LogTypes.debug, "connection already killed. return.");
      }
      else
      {
        try
        {
          Thread.sleep(100L);
        }
        catch (InterruptedException localInterruptedException)
        {
          Logs.log(Logs.LogTypes.debug, "Thread interrupted.");
        }
        try
        {
          ReceivedData localReceivedData = new ReceivedData();
          Object localObject;
          if ((eventSocket == null) || (!eventSocket.isConnected()))
          {
            localObject = new InetSocketAddress(paramString, 22223);
            eventSocket = new Socket();
            eventSocket.connect((SocketAddress)localObject, 3000);
            localInputStream = eventSocket.getInputStream();
            Logs.log(Logs.LogTypes.debug, "connect event socket successful");
          }
          try
          {
            if (localInputStream != null)
            {
              localObject = "";
              if (localInputStreamReader == null) {
                localInputStreamReader = new InputStreamReader(localInputStream);
              }
              if (Build.VERSION.SDK_INT < 21)
              {
                if (!eventSocket.isClosed())
                {
                  WaitAmoment localWaitAmoment = new WaitAmoment();
                  while (!localInputStreamReader.ready()) {
                    localWaitAmoment.waitAmoment(500L);
                  }
                  Logs.log(Logs.LogTypes.verbose, "inputStreamReader is ready!");
                  localWaitAmoment.waitAmoment(300L);
                  int i = localInputStream.available();
                  Logs.log(Logs.LogTypes.verbose, "inputStream.available: " + i);
                  char[] arrayOfChar = new char[i];
                  int j = localInputStreamReader.read(arrayOfChar);
                  Logs.log(Logs.LogTypes.verbose, "readCount: " + j);
                  String str = new String(arrayOfChar);
                  Logs.log(Logs.LogTypes.verbose, "buffer string : " + str);
                  localReceivedData.parseReceivedWifiData(str);
                  if (wifiReceivedDataListener != null) {
                    wifiReceivedDataListener.onEventDataReceived(localReceivedData);
                  } else {
                    Logs.log(Logs.LogTypes.debug, "no receivedData listener is set");
                  }
                }
              }
              else
              {
                localBufferedReader = new BufferedReader(new InputStreamReader(eventSocket.getInputStream()));
                Logs.log(Logs.LogTypes.verbose, "now try to receive data");
                if (!eventSocket.isClosed())
                {
                  Logs.log(Logs.LogTypes.verbose, "event socket is not closed");
                  Logs.log(Logs.LogTypes.verbose, "now waiting for receiving data");
                  localObject = localBufferedReader.readLine();
                  if (localObject == null)
                  {
                    Logs.log(Logs.LogTypes.codeerror, "receivedData is null. sth is wrong. continue the loop.");
                    break;
                  }
                  localReceivedData.parseReceivedWifiData((String)localObject);
                  if (wifiReceivedDataListener != null) {
                    wifiReceivedDataListener.onEventDataReceived(localReceivedData);
                  } else {
                    Logs.log(Logs.LogTypes.debug, "no receivedData listener is set");
                  }
                }
              }
            }
            else
            {
              Logs.log(Logs.LogTypes.codeerror, "inputStream  = NULL, Validate the response channel code");
            }
          }
          catch (DeviceException localDeviceException)
          {
            Logs.log(Logs.LogTypes.exception, "device exception: " + localDeviceException.getMessage());
          }
        }
        catch (IOException localIOException)
        {
          Logs.log(Logs.LogTypes.exception, "IOException" + localIOException.getMessage(), localIOException);
          ErrorObject.sendErrorEventChannelSocketNotConnecting("", errorListener, this);
          setState(ConnectionState.disconnected, true);
          break;
        }
      }
    }
  }
  
  public void connectLiveChannel(Device.LiveImageSpeed paramLiveImageSpeed) {}
  
  public void disconnectLiveChannel() {}
  
  public synchronized void killConnection()
  {
    a = true;
    if ((socket != null) && (!socket.isClosed()))
    {
      try
      {
        Logs.log(Logs.LogTypes.debug, "try close socket");
        socket.shutdownInput();
        socket.shutdownOutput();
        socket.close();
      }
      catch (IOException localIOException1)
      {
        Logs.log(Logs.LogTypes.debug, "socket.close() IOEXCEPTION");
      }
      Logs.log(Logs.LogTypes.debug, "close socket finished");
    }
    if ((eventSocket != null) && (eventSocket.isConnected()))
    {
      try
      {
        Logs.log(Logs.LogTypes.debug, "try close event socket");
        eventSocket.close();
      }
      catch (IOException localIOException2)
      {
        Logs.log(Logs.LogTypes.debug, "eventSocket.close() IOEXCEPTION");
      }
      Logs.log(Logs.LogTypes.debug, "close event socket finished");
    }
    setState(ConnectionState.disconnected, true);
    Logs.log(Logs.LogTypes.debug, "end of kill connection");
  }
  
  public void setContext(Context paramContext)
  {
    context = paramContext;
  }
  
  public synchronized void setState(ConnectionState paramConnectionState, boolean paramBoolean)
  {
    Logs.log(Logs.LogTypes.debug, "connection state: " + paramConnectionState);
    c = paramConnectionState;
    if (!paramBoolean) {
      return;
    }
    if (connectionListener == null)
    {
      Logs.log(Logs.LogTypes.debug, "listener is null");
      return;
    }
    switch (1.a[paramConnectionState.ordinal()])
    {
    case 1: 
      connectionListener.onDisconnected(this);
      break;
    case 2: 
      break;
    case 3: 
      connectionListener.onConnected(this);
    }
  }
  
  public void foundAvailableBluetoothDevice(String paramString, BluetoothDevice paramBluetoothDevice, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (scanDevicesListener != null)
    {
      Logs.log(Logs.LogTypes.debug, "available BLE device found, tell listener now");
      scanDevicesListener.onBluetoothDeviceFound(paramString, paramBluetoothDevice, paramBoolean1, paramBoolean2);
    }
    else
    {
      Logs.log(Logs.LogTypes.debug, "available BLE device found, but no listener is set");
    }
  }
  
  public void disconnectedACLBluetoothDevice(String paramString)
  {
    if (scanDevicesListener != null)
    {
      Logs.log(Logs.LogTypes.debug, "BLE device disconnected in settings, tell listener now");
      scanDevicesListener.onBluetoothDeviceACLDisconnected(paramString);
    }
    else
    {
      Logs.log(Logs.LogTypes.debug, "available BLE device found, but no listener is set");
    }
  }
  
  public void foundAvailableHotspotDevice(String paramString)
  {
    if (scanDevicesListener != null)
    {
      Logs.log(Logs.LogTypes.debug, "available HOTSPOT device found, tell listener now");
      scanDevicesListener.onHotspotDeviceFound(paramString);
    }
    else
    {
      Logs.log(Logs.LogTypes.debug, "available HOTSPOT device found, but no listener is set");
    }
  }
  
  public void foundAvailableApDevice(String paramString1, String paramString2)
  {
    if (scanDevicesListener != null)
    {
      Logs.log(Logs.LogTypes.debug, "available AP device found, tell listener now");
      scanDevicesListener.onApDeviceFound(paramString1, paramString2);
    }
    else
    {
      Logs.log(Logs.LogTypes.debug, "available AP device found, but no listener is set");
    }
  }
  
  public void foundAvailableRndisDevice(String paramString1, String paramString2)
  {
    if (scanDevicesListener != null)
    {
      Logs.log(Logs.LogTypes.debug, "available RNDIS device found, tell listener now");
      scanDevicesListener.onRndisDeviceFound(paramString1, paramString2);
    }
    else
    {
      Logs.log(Logs.LogTypes.debug, "available RNDIS device found, but no listener is set");
    }
  }
  
  public void sendCommand(Command paramCommand)
    throws Exception
  {
    String str = paramCommand.getPayload();
    synchronized (socket)
    {
      if (!socket.isClosed())
      {
        OutputStream localOutputStream = socket.getOutputStream();
        DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);
        localDataOutputStream.writeBytes(str);
        localDataOutputStream.flush();
        Logs.log(Logs.LogTypes.debug, " - Sending - Command send successfully. type: " + paramCommand.getPayload());
      }
      else
      {
        Logs.log(Logs.LogTypes.exception, "Socket was closed, retrying the connection. Status - " + getState().toString());
      }
    }
  }
  
  public void registerReceivers(Context paramContext)
  {
    context = paramContext;
  }
  
  public void unregisterReceivers() {}
  
  public ErrorListener getErrorListener()
  {
    return errorListener;
  }
  
  public void setErrorListener(ErrorListener paramErrorListener)
  {
    errorListener = paramErrorListener;
  }
  
  public boolean hasNewBleService()
    throws DeviceException
  {
    return false;
  }
  
  public boolean isDiscoverServicesIsRunning()
  {
    return false;
  }
  
  public boolean getisSocketConnected()
  {
    return (socket.isClosed()) && (socket.isConnected());
  }
  
  public static abstract interface ScanDevicesListener
  {
    public abstract void onBluetoothDeviceFound(String paramString, BluetoothDevice paramBluetoothDevice, boolean paramBoolean1, boolean paramBoolean2);
    
    public abstract void onBluetoothDeviceACLDisconnected(String paramString);
    
    public abstract void onHotspotDeviceFound(String paramString);
    
    public abstract void onApDeviceFound(String paramString1, String paramString2);
    
    public abstract void onRndisDeviceFound(String paramString1, String paramString2);
  }
  
  public static abstract interface BleReceivedDataListener
  {
    public abstract void onBleDataReceived(ReceivedData paramReceivedData, ErrorObject paramErrorObject)
      throws DeviceException;
  }
  
  public static abstract interface WifiReceivedDataListener
  {
    public abstract void onResponseReceived(ReceivedData paramReceivedData, ErrorObject paramErrorObject);
    
    public abstract void onEventDataReceived(ReceivedData paramReceivedData);
    
    public abstract void onLiveImageDataReceived(ReceivedData paramReceivedData)
      throws DeviceException;
  }
  
  public static abstract interface ConnectionListener
  {
    public abstract void onConnected(BaseConnectionManager paramBaseConnectionManager);
    
    public abstract void onDisconnected(BaseConnectionManager paramBaseConnectionManager);
  }
  
  public static enum ConnectionState
  {
    private ConnectionState() {}
  }
}
