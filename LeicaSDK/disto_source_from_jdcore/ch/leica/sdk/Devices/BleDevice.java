package ch.leica.sdk.Devices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import ch.leica.sdk.commands.BLECommand;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.ReceivedDataPacket;
import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.connection.BaseConnectionManager;
import ch.leica.sdk.connection.ble.BleCharacteristic;
import ch.leica.sdk.connection.ble.BleConnectionManager;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.ArrayList;
import java.util.List;

public class BleDevice
  extends Device
{
  private String[] k;
  
  public BleDevice(Context paramContext, String paramString, BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
  {
    super(paramContext, Types.ConnectionType.ble);
    hasDistoServiceBeforeConnection = paramBoolean;
    deviceID = (paramString + "+++" + paramBluetoothDevice.getAddress());
    deviceName = paramString;
    bluetoothDevice = paramBluetoothDevice;
    Logs.log(Logs.LogTypes.debug, "hasDistoServiceBeforeConnection: " + hasDistoServiceBeforeConnection + " deviceID: " + deviceID);
    init();
  }
  
  protected void init()
  {
    deviceType = Types.DeviceType.Ble;
  }
  
  protected void assignConnectionManager()
  {
    connectionManager = new BleConnectionManager((BluetoothManager)context.getSystemService("bluetooth"), context);
    ((BleConnectionManager)connectionManager).setNoPairingNeeded(hasDistoServiceBeforeConnection);
  }
  
  public String[] getAvailableCommands()
  {
    return k;
  }
  
  public boolean getModelValue()
  {
    boolean bool = false;
    try
    {
      bool = connectionManager.readModelCharacteristic();
      Logs.log(Logs.LogTypes.debug, "HasModel: " + bool);
    }
    catch (DeviceException localDeviceException)
    {
      bool = false;
    }
    return bool;
  }
  
  public List<BleCharacteristic> getAllCharacteristics()
  {
    return ((BleConnectionManager)connectionManager).getAllCharacteristics();
  }
  
  public Response sendCommand(String paramString, long paramLong)
    throws DeviceException
  {
    if (paramString == null) {
      throw new DeviceException("command is null");
    }
    BLECommand localBLECommand = null;
    localBLECommand = new BLECommand(paramString);
    return processCommand(localBLECommand, paramLong);
  }
  
  public Response sendCommand(Types.Commands paramCommands, List<String> paramList)
    throws DeviceException
  {
    if (paramCommands == null) {
      throw new DeviceException("command is null");
    }
    if (paramList == null) {
      throw new DeviceException("parameters are null");
    }
    return processCommand(new BLECommand(paramCommands), 12000L);
  }
  
  public Response sendCommand(Types.Commands paramCommands)
    throws DeviceException
  {
    return sendCommand(paramCommands, new ArrayList());
  }
  
  protected void setConnectionParameters()
  {
    connectionManager.setConnectionParameters(new Object[] { bluetoothDevice });
  }
  
  public void saveResponseData(ReceivedData paramReceivedData, ErrorObject paramErrorObject)
  {
    super.saveResponseData(paramReceivedData, paramErrorObject);
    handleDataParsing(paramReceivedData, paramErrorObject);
  }
  
  protected void handleDataParsing(ReceivedData paramReceivedData, ErrorObject paramErrorObject)
  {
    synchronized (i)
    {
      i.setError(paramErrorObject);
      if (paramReceivedData == null) {
        Logs.log(Logs.LogTypes.codeerror, "receivedData is null");
      } else {
        responseHelper.b(paramReceivedData, i);
      }
    }
  }
  
  public void onConnected(BaseConnectionManager paramBaseConnectionManager)
  {
    g = false;
    connectionState = Device.ConnectionState.connected;
    Logs.log(Logs.LogTypes.debug, "state changed to connected");
    DeviceManager.getInstance(context).a(this);
    if (!getModelValue())
    {
      modelName = "D810";
      c();
    }
    if (connectionListener != null) {
      connectionListener.onConnectionStateChanged(this, Device.ConnectionState.connected);
    } else {
      Logs.log(Logs.LogTypes.codeerror, "listener is null");
    }
  }
  
  public void onDisconnected(BaseConnectionManager paramBaseConnectionManager)
  {
    super.onDisconnected(paramBaseConnectionManager);
  }
  
  public void startBTConnection(Device.BTConnectionCallback paramBTConnectionCallback)
  {
    connectionManager.startBTConnection(paramBTConnectionCallback);
  }
  
  public void pauseBTConnection(Device.BTConnectionCallback paramBTConnectionCallback)
  {
    connectionManager.pauseBTConnection(paramBTConnectionCallback);
  }
  
  public void registerReceivers()
  {
    if (e == null)
    {
      e = new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          String str = paramAnonymousIntent.getAction();
          Logs.log(Logs.LogTypes.debug, " action: " + str);
          if (str.equals("android.bluetooth.adapter.action.STATE_CHANGED"))
          {
            int i = paramAnonymousIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
            switch (i)
            {
            case 10: 
            case 13: 
              Logs.log(Logs.LogTypes.debug, "action STATE OFF: " + str);
              disconnect();
              ErrorObject.sendErrorBluetoothIsOff(c, BleDevice.this);
              break;
            case 12: 
              Logs.log(Logs.LogTypes.debug, "action STATE ON: " + str);
              break;
            case 11: 
              Logs.log(Logs.LogTypes.debug, "action STATE TURNING ON: " + str);
              break;
            default: 
              Logs.log(Logs.LogTypes.codeerror, "Unknown State");
            }
          }
        }
      };
      Logs.log(Logs.LogTypes.verbose, "bluetoothAdapterChangedReceiver start registering");
      IntentFilter localIntentFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
      context.registerReceiver(e, localIntentFilter);
      Logs.log(Logs.LogTypes.debug, "bluetoothAdapterChangedReceiver registered");
    }
    if ((Build.VERSION.SDK_INT < 21) && (f == null))
    {
      f = new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          String str1 = paramAnonymousIntent.getStringExtra("deviceName");
          String str2 = paramAnonymousIntent.getStringExtra("deviceAddress");
          Logs.log(Logs.LogTypes.debug, "deviceName: " + str1 + ", deviceAddress: " + str2);
        }
      };
      Logs.log(Logs.LogTypes.verbose, "deviceBondedReceiver start registering");
      LocalBroadcastManager.getInstance(context).registerReceiver(f, new IntentFilter("DEVICE_BONDED"));
      Logs.log(Logs.LogTypes.debug, "deviceBondedReceiver registered");
    }
  }
  
  public void connect()
  {
    int i = 1;
    if (context == null)
    {
      Logs.log(Logs.LogTypes.codeerror, "Context must not be null");
      i = 0;
    }
    if (getConnectionState() == Device.ConnectionState.connected)
    {
      Logs.log(Logs.LogTypes.debug, "Device is already connected");
      if (connectionListener != null) {
        connectionListener.onConnectionStateChanged(this, getConnectionState());
      }
      i = 0;
    }
    if (i == 1)
    {
      b();
      connectionHandler.post(new Runnable()
      {
        public void run()
        {
          setupConnectionManager();
          registerReceivers();
          ((BleConnectionManager)connectionManager).setDiscoverServicesDelay(getDiscoverServicesDelay());
          connectionManager.connect();
        }
      });
    }
  }
  
  public int getDiscoverServicesDelay()
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return 500;
    }
    if (!hasDistoServiceBeforeConnection) {
      return 600;
    }
    return 3000;
  }
  
  public void unpairDevice()
  {
    ((BleConnectionManager)connectionManager).unBond();
  }
  
  private String a(String paramString)
  {
    switch (paramString)
    {
    case "0": 
      return "D510";
    case "D210": 
      return "D2";
    }
    return paramString;
  }
  
  public void disconnect()
  {
    if ((getConnectionState() == Device.ConnectionState.connected) && (!((BleConnectionManager)connectionManager).isNoPairingNeeded()) && (!deviceType.equals(Types.DeviceType.Yeti)))
    {
      Logs.log(Logs.LogTypes.debug, "device needs pairing. so disconnect with unpair() for " + deviceID);
      ((BleConnectionManager)connectionManager).unBond();
    }
    if (((BleConnectionManager)connectionManager).getGattStatus() == 133)
    {
      if (c != null) {
        c.onError(new ErrorObject(2112, "Received a Gatt Error 133. Unfortunately this is unavoidable. The only solution is retrying to establish the connection."), this);
      }
    }
    else if ((((BleConnectionManager)connectionManager).getGattStatus() == 62) && (c != null)) {
      c.onError(new ErrorObject(2114, "Received a Gatt Error 62. This means establishing connection failed. In some cases, this is due to a bad signal strength received by the bluetooth antenna. This can be resolved by bringing the phone/tablet nearer to the device and just try again."), this);
    }
    super.disconnect();
  }
  
  public void onBleDataReceived(ReceivedData paramReceivedData, ErrorObject paramErrorObject)
  {
    if ((dataPacket != null) && ((dataPacket.dataId.equals("DS_MODEL_NAME")) || (dataPacket.dataId.equals("DI_MODEL_NUMBER"))) && (modelName.equals("") == true))
    {
      modelName = a(paramReceivedData);
      c();
    }
    if (i == null)
    {
      Logs.log(Logs.LogTypes.debug, "currentResponse is null, data is an event.");
      if (paramErrorObject != null)
      {
        if (c != null) {
          c.onError(paramErrorObject, this);
        }
      }
      else if (a != null)
      {
        Logs.log(Logs.LogTypes.verbose, "call this.receivedDataListener.onAsyncDataReceived()");
        a.onAsyncDataReceived(paramReceivedData);
      }
      return;
    }
    if (bleResponseHandler == null)
    {
      bleResponseThread = new HandlerThread("Device_BleResponseThread_" + System.currentTimeMillis());
      bleResponseThread.start();
      bleResponseHandler = new Handler(bleResponseThread.getLooper());
    }
    saveResponseData(paramReceivedData, paramErrorObject);
    synchronized (j)
    {
      if (!j.booleanValue())
      {
        j = Boolean.valueOf(true);
        Logs.log(Logs.LogTypes.debug, "waitingForBleResponsesTime: " + waitingForBleResponsesTime);
        bleResponseHandler.postDelayed(new Runnable()
        {
          public void run()
          {
            synchronized (j)
            {
              synchronized (h)
              {
                h = Boolean.valueOf(false);
              }
              if (i != null)
              {
                i.setWaitingForData(Boolean.valueOf(false));
                i = null;
              }
              responseHelper.setDistocomData("");
              j = Boolean.valueOf(false);
            }
          }
        }, waitingForBleResponsesTime);
      }
    }
  }
  
  private String a(ReceivedData paramReceivedData)
  {
    String str1 = "";
    if (dataPacket != null) {
      try
      {
        String str2 = dataPacket.getModelName();
        Logs.log(Logs.LogTypes.debug, "tempModelName:- " + str2);
        if (!str2.equals("")) {
          str1 = a(str2);
        }
        Logs.log(Logs.LogTypes.debug, "ModelName:- " + str1);
      }
      catch (Exception localException)
      {
        Logs.log(Logs.LogTypes.exception, "Error Getting the Model.", localException);
      }
    }
    return str1;
  }
  
  private void c()
  {
    Logs.log(Logs.LogTypes.debug, "ModelName = " + modelName);
    if (modelName != null)
    {
      if ("D510".equals(modelName) == true) {
        k = new String[0];
      } else if ("".equals(modelName) == true) {
        k = new String[0];
      } else {
        k = new String[] { Types.Commands.Custom.name(), Types.Commands.Distance.name(), Types.Commands.LaserOn.name(), Types.Commands.LaserOff.name(), Types.Commands.StartTracking.name(), Types.Commands.StopTracking.name() };
      }
    }
    else {
      k = new String[0];
    }
  }
  
  public boolean isBleOperationInProgress()
  {
    return ((BleConnectionManager)connectionManager).isBleOperationInProgress();
  }
  
  public void readAllBleCharacteristics(Device.BTConnectionCallback paramBTConnectionCallback)
  {
    ((BleConnectionManager)connectionManager).readAllCharacteristics(paramBTConnectionCallback);
  }
}
