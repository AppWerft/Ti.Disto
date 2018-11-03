package ch.leica.sdk.Devices;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.content.LocalBroadcastManager;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Listeners.ReceivedDataListener;
import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.Types.ConnectionType;
import ch.leica.sdk.Types.DeviceType;
import ch.leica.sdk.commands.Command;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.commands.response.ResponseUpdate;
import ch.leica.sdk.connection.BaseConnectionManager;
import ch.leica.sdk.connection.BaseConnectionManager.BleReceivedDataListener;
import ch.leica.sdk.connection.BaseConnectionManager.ConnectionListener;
import ch.leica.sdk.connection.BaseConnectionManager.WifiReceivedDataListener;
import ch.leica.sdk.connection.ble.BleCharacteristic;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareUpdate;
import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Device
  implements ErrorListener, BaseConnectionManager.BleReceivedDataListener, BaseConnectionManager.ConnectionListener, BaseConnectionManager.WifiReceivedDataListener, Serializable
{
  public static final String ID_SEPERATOR = "+++";
  private final long k = 12000L;
  private final long l = 60000L;
  public String modelName = "";
  public ConnectionListener connectionListener;
  protected HandlerThread commandThread;
  protected Handler commandHandler;
  protected HandlerThread responseThread;
  protected Handler responseHandler;
  protected HandlerThread eventThread;
  protected Handler eventHandler;
  protected HandlerThread connectionThread;
  protected Handler connectionHandler;
  protected HandlerThread bleResponseThread;
  protected Handler bleResponseHandler;
  protected Context context;
  protected String deviceIP;
  protected String deviceID;
  protected String deviceName;
  protected Types.DeviceType deviceType;
  protected BluetoothDevice bluetoothDevice;
  protected ConnectionState connectionState;
  protected BaseConnectionManager connectionManager;
  protected boolean hasDistoServiceBeforeConnection = false;
  protected int waitingForBleResponsesTime = 750;
  ReceivedDataListener a;
  DeviceState b = DeviceState.normal;
  ErrorListener c;
  BroadcastReceiver d;
  BroadcastReceiver e;
  BroadcastReceiver f;
  boolean g = false;
  Boolean h = Boolean.valueOf(false);
  Response i = null;
  Boolean j = Boolean.valueOf(false);
  private Timer m;
  private a n;
  private Types.ConnectionType o;
  protected ResponseHelper responseHelper = new ResponseHelper();
  
  public Device(Context paramContext, Types.ConnectionType paramConnectionType)
  {
    o = paramConnectionType;
    if (paramContext == null) {
      throw new IllegalArgumentException("Context must not be null");
    }
    context = paramContext;
    connectionState = ConnectionState.disconnected;
    Logs.log(Logs.LogTypes.debug, "Created DEVICE with deviceID: " + deviceID);
  }
  
  private void c()
  {
    synchronized (h)
    {
      h = Boolean.valueOf(false);
      if (i == null)
      {
        Logs.log(Logs.LogTypes.codeerror, "currentResponse is null");
        return;
      }
      synchronized (i)
      {
        if (n != null) {
          n.cancel();
        }
        if (m != null) {
          m.purge();
        }
        i.setDataString("No response for this command.");
        i.setWaitingForData(Boolean.valueOf(false));
        i = null;
      }
    }
  }
  
  protected void setupConnectionManager()
  {
    if (context == null)
    {
      Logs.log(Logs.LogTypes.codeerror, "Context must not be null");
      return;
    }
    assignConnectionManager();
    connectionManager.setContext(context);
    connectionManager.wifiReceivedDataListener = this;
    connectionManager.bleReceivedDataListener = this;
    connectionManager.connectionListener = this;
    connectionManager.setErrorListener(this);
    setConnectionParameters();
    Logs.log(Logs.LogTypes.debug, "Connection Manager Setup");
  }
  
  protected abstract void assignConnectionManager();
  
  protected abstract void setConnectionParameters();
  
  public abstract void onConnected(BaseConnectionManager paramBaseConnectionManager);
  
  public abstract void connect();
  
  public abstract String[] getAvailableCommands();
  
  public abstract boolean getModelValue();
  
  public void onBleDataReceived(ReceivedData paramReceivedData, ErrorObject paramErrorObject)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public void onDisconnected(BaseConnectionManager paramBaseConnectionManager)
  {
    if (g == true)
    {
      Logs.log(Logs.LogTypes.debug, "disconnectCalled is true");
      return;
    }
    disconnect();
  }
  
  public void onError(ErrorObject paramErrorObject, Device paramDevice)
  {
    if (c != null)
    {
      if (paramErrorObject.getErrorCode() == 2802) {
        paramErrorObject.setErrorMessage(paramErrorObject.getErrorMessage() + "The SSID should be: " + getDeviceName());
      }
      c.onError(paramErrorObject, this);
    }
    else
    {
      Logs.log(Logs.LogTypes.codeerror, "listener is null");
    }
  }
  
  public void onResponseReceived(ReceivedData paramReceivedData, ErrorObject paramErrorObject)
  {
    if (i == null)
    {
      if (paramErrorObject != null)
      {
        if (c != null) {
          c.onError(paramErrorObject, this);
        }
      }
      else if (a != null) {
        a.onAsyncDataReceived(paramReceivedData);
      }
      return;
    }
    saveResponseData(paramReceivedData, paramErrorObject);
    synchronized (h)
    {
      h = Boolean.valueOf(false);
    }
    if (i != null)
    {
      i.setWaitingForData(Boolean.valueOf(false));
      i = null;
    }
  }
  
  public void onEventDataReceived(ReceivedData paramReceivedData)
  {
    if (paramReceivedData == null)
    {
      Logs.log(Logs.LogTypes.codeerror, "receivedData is null");
      return;
    }
    if (a != null)
    {
      if (dataPacket != null) {
        a.onAsyncDataReceived(paramReceivedData);
      } else {
        Logs.log(Logs.LogTypes.debug, "Data Packet size is 0");
      }
    }
    else {
      Logs.log(Logs.LogTypes.codeerror, "listener is null");
    }
  }
  
  public void onLiveImageDataReceived(ReceivedData paramReceivedData)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public void connectLiveChannel(LiveImageSpeed paramLiveImageSpeed)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public void disconnect()
  {
    g = true;
    connectionManager.killConnection();
    DeviceManager.getInstance(context).b(this);
    unregisterReceivers();
    connectionState = ConnectionState.disconnected;
    Logs.log(Logs.LogTypes.debug, "state changed to disconnected");
    if (connectionListener != null) {
      connectionListener.onConnectionStateChanged(this, connectionState);
    } else {
      Logs.log(Logs.LogTypes.debug, "connection listener is null");
    }
    if (commandThread != null)
    {
      commandThread.interrupt();
      commandThread = null;
      commandHandler = null;
    }
    if (responseThread != null)
    {
      responseThread.interrupt();
      responseThread = null;
      responseHandler = null;
    }
    if (eventThread != null)
    {
      eventThread.interrupt();
      eventThread = null;
      eventHandler = null;
    }
    if (connectionThread != null)
    {
      connectionThread.interrupt();
      connectionThread = null;
      connectionHandler = null;
    }
    Logs.log(Logs.LogTypes.debug, "Disconnected successfully from the device. ");
  }
  
  public void disconnectLiveChannel()
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public List<BleCharacteristic> getAllCharacteristics()
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public FirmwareUpdate getAvailableFirmwareUpdate()
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public ConnectionState getConnectionState()
  {
    return connectionState;
  }
  
  public Types.ConnectionType getConnectionType()
  {
    return o;
  }
  
  public String getDeviceID()
  {
    return deviceID;
  }
  
  public String getDeviceName()
  {
    return deviceName;
  }
  
  public Types.DeviceType getDeviceType()
  {
    if (deviceType == null) {
      Logs.log(Logs.LogTypes.codeerror, "Not device Type was set");
    }
    return deviceType;
  }
  
  public ErrorListener getErrorListener()
  {
    return c;
  }
  
  public String getIP()
  {
    return deviceIP;
  }
  
  public String getModel()
  {
    Logs.log(Logs.LogTypes.debug, "Model: " + modelName);
    if (modelName.equals(""))
    {
      try
      {
        Thread.sleep(500L);
      }
      catch (InterruptedException localInterruptedException)
      {
        Logs.log(Logs.LogTypes.debug, "Sleep Thread Interrupted. ");
      }
      getModelValue();
    }
    return modelName;
  }
  
  public FirmwareUpdate getReinstallFirmware()
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public long getTIMEOUT_LONG()
  {
    return 60000L;
  }
  
  public long getTIMEOUT_NORMAL()
  {
    return 12000L;
  }
  
  public boolean isInUpdateMode()
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public void pauseBTConnection(BTConnectionCallback paramBTConnectionCallback)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public synchronized Response processCommand(final Command paramCommand, long paramLong)
    throws DeviceException
  {
    Response localResponse;
    try
    {
      Logs.log(Logs.LogTypes.debug, "Called with type: " + paramCommand.getCommandValue() + "Value: " + paramCommand.getPayload());
      if (connectionState != ConnectionState.connected)
      {
        Logs.log(Logs.LogTypes.debug, "State is NOT CONNECTED");
        throw new DeviceException("Device is not connected");
      }
      synchronized (b)
      {
        if ((b == DeviceState.update) && (!paramCommand.isUpdateCommand()))
        {
          Logs.log(Logs.LogTypes.debug, "device is in update process.");
          throw new DeviceException("Device is in update process");
        }
      }
      synchronized (h)
      {
        if (h.booleanValue() == true) {
          throw new DeviceException("A command is already in progress");
        }
        h = Boolean.valueOf(true);
      }
      i = paramCommand.getResponseForCommand();
      if (i == null) {
        throw new DeviceException("Internal error. Could not send command (null command).");
      }
      boolean bool = true;
      if (!connectionManager.hasNewBleService())
      {
        if (!paramCommand.hasCommandResponse().booleanValue()) {
          bool = false;
        }
      }
      else {
        Logs.log(Logs.LogTypes.debug, "Has new ble services");
      }
      Logs.log(Logs.LogTypes.debug, "waitForData: " + bool);
      i.setWaitingForData(Boolean.valueOf(bool));
      if (commandHandler == null)
      {
        commandThread.start();
        commandHandler = new Handler(commandThread.getLooper());
      }
      commandHandler.post(new Runnable()
      {
        public void run()
        {
          try
          {
            Logs.log(Logs.LogTypes.verbose, "Start of handler thread. Now send command with connection manager");
            connectionManager.sendCommand(paramCommand);
          }
          catch (Exception localException)
          {
            Logs.log(Logs.LogTypes.exception, "send command failed. e.message: " + localException.getMessage(), localException);
            disconnect();
          }
        }
      });
      if (bool == true)
      {
        if (m == null) {
          m = new Timer();
        }
        n = new a(null);
        m.schedule(n, paramLong);
      }
      localResponse = i;
      if (!bool) {
        c();
      }
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.exception, "Error processing the message. " + localException.getMessage(), localException);
      ErrorObject localErrorObject = new ErrorObject(3508, localException.getMessage());
      throw new DeviceException(localErrorObject);
    }
    return localResponse;
  }
  
  public void readAllBleCharacteristics(BTConnectionCallback paramBTConnectionCallback)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public abstract void registerReceivers();
  
  public void saveResponseData(ReceivedData paramReceivedData, ErrorObject paramErrorObject)
  {
    if (i == null)
    {
      Logs.log(Logs.LogTypes.codeerror, "current response is null.");
      return;
    }
    synchronized (i)
    {
      if (n != null) {
        n.cancel();
      }
      if (m != null) {
        m.purge();
      }
      i.setError(paramErrorObject);
    }
  }
  
  public abstract Response sendCommand(String paramString, long paramLong)
    throws DeviceException;
  
  public abstract Response sendCommand(Types.Commands paramCommands)
    throws DeviceException;
  
  public abstract Response sendCommand(Types.Commands paramCommands, List<String> paramList)
    throws DeviceException;
  
  public Response sendCommand(Types.Commands paramCommands, long paramLong)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public Response sendCommand(Types.Commands paramCommands, long paramLong, List<String> paramList)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public void sendCommandMotorPositionAbsolute(double paramDouble1, double paramDouble2, boolean paramBoolean)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public void sendCommandMotorPositionRelative(double paramDouble1, double paramDouble2, boolean paramBoolean)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public void sendCommandMoveMotorDown(int paramInt)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public void sendCommandMoveMotorLeft(int paramInt)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public void sendCommandMoveMotorRight(int paramInt)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public void sendCommandMoveMotorUp(int paramInt)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public void sendCommandPositionStopHorizontal()
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public void sendCommandPositionStopVertical()
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public Response sendCustomCommand(String paramString)
    throws DeviceException
  {
    return sendCommand(paramString, 12000L);
  }
  
  public Response sendCustomCommand(String paramString, long paramLong)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public void setConnectionListener(ConnectionListener paramConnectionListener)
  {
    connectionListener = paramConnectionListener;
  }
  
  public synchronized void setCurrentState(DeviceState paramDeviceState)
  {
    b = paramDeviceState;
  }
  
  public void setErrorListener(ErrorListener paramErrorListener)
  {
    c = paramErrorListener;
  }
  
  public void setReceiveDataListener(ReceivedDataListener paramReceivedDataListener)
  {
    a = paramReceivedDataListener;
  }
  
  public void startBTConnection(BTConnectionCallback paramBTConnectionCallback)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  public void unpairDevice()
  {
    Logs.log(Logs.LogTypes.codeerror, "Method not implemented for this device");
  }
  
  public void unregisterReceivers()
  {
    try
    {
      if (e != null)
      {
        context.unregisterReceiver(e);
        e = null;
      }
      Logs.log(Logs.LogTypes.debug, "bluetoothAdapterChangedReceiver unregistered");
      if (Build.VERSION.SDK_INT < 21)
      {
        if (f != null)
        {
          LocalBroadcastManager.getInstance(context).unregisterReceiver(f);
          f = null;
        }
        Logs.log(Logs.LogTypes.debug, "deviceBondedReceiver unregistered");
      }
      if (d != null)
      {
        context.unregisterReceiver(d);
        d = null;
      }
      Logs.log(Logs.LogTypes.debug, "wifiChangeReceiver unregistered");
      connectionManager.unregisterReceivers();
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.exception, "Error Unregistering the Receiver", localException);
    }
  }
  
  public ResponseUpdate updateDeviceFirmwareWithFirmwareUpdate(FirmwareUpdate paramFirmwareUpdate, UpdateDeviceListener paramUpdateDeviceListener)
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  BaseConnectionManager a()
  {
    return connectionManager;
  }
  
  void b()
  {
    if (commandThread != null) {
      commandThread.interrupt();
    }
    commandThread = new HandlerThread("Device_CommandThread_" + System.currentTimeMillis(), 10);
    commandThread.start();
    commandHandler = new Handler(commandThread.getLooper());
    if (responseThread != null) {
      responseThread.interrupt();
    }
    responseThread = new HandlerThread("Device__ResponseThread_" + System.currentTimeMillis());
    responseThread.start();
    responseHandler = new Handler(responseThread.getLooper());
    if (eventThread != null) {
      eventThread.interrupt();
    }
    eventThread = new HandlerThread("Device__EventThread_" + System.currentTimeMillis());
    eventThread.start();
    eventHandler = new Handler(eventThread.getLooper());
    if (connectionThread != null) {
      connectionThread.interrupt();
    }
    connectionThread = new HandlerThread("Device__ConnectionThread_" + System.currentTimeMillis(), 10);
    connectionThread.start();
    connectionHandler = new Handler(connectionThread.getLooper());
  }
  
  public boolean isBleOperationInProgress()
    throws DeviceException
  {
    throw new DeviceException("Method not implemented for this device");
  }
  
  private class a
    extends TimerTask
  {
    private a() {}
    
    public void run()
    {
      if (i == null)
      {
        Logs.log(Logs.LogTypes.codeerror, "current response is null. this should never happen.");
        return;
      }
      synchronized (i)
      {
        i.setError(new ErrorObject(3502, "No response received within given timeframe."));
        synchronized (h)
        {
          h = Boolean.valueOf(false);
        }
        i.setWaitingForData(Boolean.valueOf(false));
        i = null;
      }
    }
  }
  
  public static abstract interface UpdateDeviceListener
  {
    public abstract void onFirmwareUpdateStarted(String paramString1, String paramString2);
    
    public abstract void onProgress(long paramLong1, long paramLong2);
  }
  
  public static abstract interface ConnectionListener
  {
    public abstract void onConnectionStateChanged(Device paramDevice, Device.ConnectionState paramConnectionState);
  }
  
  public static enum LiveImageSpeed
  {
    private LiveImageSpeed() {}
  }
  
  public static enum DeviceState
  {
    private DeviceState() {}
  }
  
  public static enum ConnectionState
  {
    private ConnectionState() {}
  }
  
  public static abstract interface BTConnectionCallback
  {
    public abstract void onFinished();
  }
}
