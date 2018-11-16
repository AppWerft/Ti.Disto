package ch.leica.sdk.Devices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build.VERSION;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.Types.DeviceType;
import ch.leica.sdk.commands.DistocomCommand;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.commands.response.ResponsePlain;
import ch.leica.sdk.commands.response.ResponseUpdate;
import ch.leica.sdk.connection.ble.BleConnectionManager;
import ch.leica.sdk.connection.ble.YetiBleConnectionManager;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareUpdate;
import ch.leica.sdk.update.Update;
import java.util.List;

public class YetiDevice
  extends BleDevice
{
  private Update k;
  
  public YetiDevice(Context paramContext, String paramString, BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
  {
    super(paramContext, paramString, paramBluetoothDevice, paramBoolean);
    Logs.log(Logs.LogTypes.debug, "YetiDevice Created");
    init();
  }
  
  protected void init()
  {
    deviceType = Types.DeviceType.Yeti;
    k = new Update(context);
    waitingForBleResponsesTime = 1;
  }
  
  protected void assignConnectionManager()
  {
    connectionManager = new YetiBleConnectionManager((BluetoothManager)context.getSystemService("bluetooth"), context);
    ((BleConnectionManager)connectionManager).setNoPairingNeeded(false);
  }
  
  public String[] getAvailableCommands()
  {
    String[] arrayOfString = { Types.Commands.Custom.name(), Types.Commands.DistanceDC.name(), Types.Commands.StartMeasurePlan.name(), Types.Commands.StartSmartRoom.name(), Types.Commands.StartBaseMode.name() };
    return arrayOfString;
  }
  
  public FirmwareUpdate getAvailableFirmwareUpdate()
    throws DeviceException
  {
    return k.getAvailableFirmwareUpdateForDevice(this);
  }
  
  public FirmwareUpdate getReinstallFirmware()
    throws DeviceException
  {
    return k.getCurrentFirmwareUpdateForDevice(this);
  }
  
  public ResponseUpdate updateDeviceFirmwareWithFirmwareUpdate(FirmwareUpdate paramFirmwareUpdate, Device.UpdateDeviceListener paramUpdateDeviceListener)
    throws DeviceException
  {
    Logs.log(Logs.LogTypes.debug, "Brand: " + paramFirmwareUpdate.getBrandIdentifier() + " Product: " + paramFirmwareUpdate.getName() + " Version: " + paramFirmwareUpdate.getVersion());
    return k.updateDeviceFirmwares(paramFirmwareUpdate, this, paramUpdateDeviceListener);
  }
  
  public boolean isInUpdateMode()
    throws DeviceException
  {
    ResponseUpdate localResponseUpdate = null;
    boolean bool = false;
    try
    {
      localResponseUpdate = (ResponseUpdate)sendCommand(Types.Commands.InUpdateMode, getTIMEOUT_NORMAL());
      localResponseUpdate.waitForData();
      if ((localResponseUpdate.getError() != null) || (localResponseUpdate.getIsUpdateMode() == -1))
      {
        synchronized (b)
        {
          b = Device.DeviceState.normal;
        }
        if (localResponseUpdate.getError() != null) {
          Logs.log(Logs.LogTypes.debug, localResponseUpdate.getError().getErrorCode() + " " + localResponseUpdate.getError().getErrorMessage());
        }
        throw new DeviceException("Unable to get inSWDMode ERROR: is in update mode: " + localResponseUpdate.getIsUpdateMode() + "ERROR: " + localResponseUpdate.getError().getErrorCode() + " " + localResponseUpdate.getError().getErrorMessage());
      }
      if (localResponseUpdate.getIsUpdateMode() == 0)
      {
        synchronized (b)
        {
          b = Device.DeviceState.normal;
        }
        bool = false;
      }
      else if (localResponseUpdate.getIsUpdateMode() == 1)
      {
        synchronized (b)
        {
          b = Device.DeviceState.update;
        }
        bool = true;
      }
    }
    catch (Exception localException)
    {
      synchronized (b)
      {
        b = Device.DeviceState.normal;
      }
      Logs.log(Logs.LogTypes.exception, "Exception caused by: ", localException);
      throw new DeviceException("Exception Caused by: ", localException);
    }
    return bool;
  }
  
  public Response sendCommand(Types.Commands paramCommands)
    throws DeviceException
  {
    return sendCommand(paramCommands, getTIMEOUT_NORMAL());
  }
  
  public Response sendCommand(String paramString, long paramLong)
    throws DeviceException
  {
    return a(paramString, paramLong);
  }
  
  public Response sendCommand(Types.Commands paramCommands, long paramLong, byte[] paramArrayOfByte)
    throws DeviceException
  {
    if (paramCommands == null) {
      throw new DeviceException("command is null");
    }
    return processCommand(new DistocomCommand(paramCommands, paramArrayOfByte), paramLong);
  }
  
  public Response sendCommand(Types.Commands paramCommands, long paramLong, List<String> paramList)
    throws DeviceException
  {
    if (paramCommands == null) {
      throw new DeviceException("command is null");
    }
    if (paramList == null) {
      throw new DeviceException("parameters are null");
    }
    if (paramLong <= 0L) {
      throw new DeviceException("wrong timeOutTime");
    }
    return processCommand(new DistocomCommand(paramCommands, paramList), paramLong);
  }
  
  public Response sendCommand(Types.Commands paramCommands, long paramLong)
    throws DeviceException
  {
    if (paramCommands == null) {
      throw new DeviceException("command is null");
    }
    if (paramLong <= 0L) {
      throw new DeviceException("wrong timeOutTime");
    }
    return processCommand(new DistocomCommand(paramCommands), paramLong);
  }
  
  public Response sendCustomCommand(String paramString, long paramLong)
    throws DeviceException
  {
    return a(paramString, paramLong);
  }
  
  private Response a(String paramString, long paramLong)
    throws DeviceException
  {
    if (paramString == null) {
      throw new DeviceException("command is null");
    }
    a(paramString);
    return processCommand(new DistocomCommand(paramString, true), paramLong);
  }
  
  private void a(String paramString)
  {
    if ("getDiscoStatus".contains(paramString) == true) {
      waitingForBleResponsesTime = 150;
    } else {
      waitingForBleResponsesTime = 1;
    }
  }
  
  public int getDiscoverServicesDelay()
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return 400;
    }
    return 1000;
  }
  
  public String getCurrentSoftwareVersion()
  {
    try
    {
      ResponsePlain localResponsePlain = (ResponsePlain)sendCommand(Types.Commands.GetSoftwareVersionAPPDistocom, getTIMEOUT_NORMAL());
      if (localResponsePlain.getError() == null) {
        return localResponsePlain.getReceivedDataString();
      }
    }
    catch (DeviceException localDeviceException)
    {
      Logs.log(Logs.LogTypes.exception, "Caused by: " + localDeviceException.getMessage());
    }
    return null;
  }
  
  protected void handleDataParsing(ReceivedData paramReceivedData, ErrorObject paramErrorObject)
  {
    synchronized (i)
    {
      i.setError(paramErrorObject);
      if (paramReceivedData == null) {
        Logs.log(Logs.LogTypes.codeerror, "receivedData is null");
      } else {
        responseHelper.c(paramReceivedData, i);
      }
    }
  }
  
  public Update getUpdate()
  {
    return k;
  }
}
