package ch.leica.sdk.connection.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.LocalBroadcastManager;
import ch.leica.sdk.Devices.Device.BTConnectionCallback;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Utilities.WaitAmoment;
import ch.leica.sdk.commands.Command;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.connection.BaseConnectionManager;
import ch.leica.sdk.connection.BaseConnectionManager.BleReceivedDataListener;
import ch.leica.sdk.connection.BaseConnectionManager.ConnectionListener;
import ch.leica.sdk.connection.BaseConnectionManager.ConnectionState;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BleConnectionManager
  extends BaseConnectionManager
  implements BleManagerGattCallback.BleProcessListener, BleScanCallback.BleScanDevicesListener
{
  List<BleCharacteristic> c = new ArrayList();
  public static UUID DISTO_SERVICE = UUID.fromString("3ab10100-f831-4395-b29d-570977d5bf94");
  @VisibleForTesting
  public static UUID DEVICE_INFORMATION_SERVICE = UUID.fromString("0000180A-0000-1000-8000-00805F9B34FB");
  @VisibleForTesting
  public static UUID BATTERY_SERVICE = UUID.fromString("0000180F-0000-1000-8000-00805F9B34FB");
  @VisibleForTesting
  public static UUID THERMOMETER_SERVICE = UUID.fromString("00001809-0000-1000-8000-00805F9B34FB");
  @VisibleForTesting
  public UUID DS_DISTANCE = UUID.fromString("3ab10101-f831-4395-b29d-570977d5bf94");
  @VisibleForTesting
  public UUID DS_DISTANCE_UNIT = UUID.fromString("3ab10102-f831-4395-b29d-570977d5bf94");
  @VisibleForTesting
  public UUID DS_INCLINATION = UUID.fromString("3ab10103-f831-4395-b29d-570977d5bf94");
  @VisibleForTesting
  public UUID DS_INCLINATION_UNIT = UUID.fromString("3ab10104-f831-4395-b29d-570977d5bf94");
  @VisibleForTesting
  public UUID DS_DIRECTION = UUID.fromString("3ab10105-f831-4395-b29d-570977d5bf94");
  @VisibleForTesting
  public UUID DS_DIRECTION_UNIT = UUID.fromString("3ab10106-f831-4395-b29d-570977d5bf94");
  @VisibleForTesting
  public UUID DS_HORIZONTAL_INCLINE = UUID.fromString("3ab10107-f831-4395-b29d-570977d5bf94");
  @VisibleForTesting
  public UUID DS_VERTICAL_INCLINE = UUID.fromString("3ab10108-f831-4395-b29d-570977d5bf94");
  @VisibleForTesting
  public UUID DS_RESPONSE = UUID.fromString("3ab1010A-f831-4395-b29d-570977d5bf94");
  @VisibleForTesting
  public UUID DS_MODEL_NAME = UUID.fromString("3ab1010c-f831-4395-b29d-570977d5bf94");
  @VisibleForTesting
  public UUID DS_COMMAND = UUID.fromString("3ab10109-f831-4395-b29d-570977d5bf94");
  @VisibleForTesting
  public UUID BT_BATTERY_LEVEL = UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB");
  @VisibleForTesting
  public UUID BT_BATTERY_POWER_STATE = UUID.fromString("00002A1A-0000-1000-8000-00805F9B34FB");
  @VisibleForTesting
  public UUID DI_MODEL_NUMBER = UUID.fromString("00002A24-0000-1000-8000-00805F9B34FB");
  @VisibleForTesting
  public UUID DI_SERIAL_NUMBER = UUID.fromString("00002A25-0000-1000-8000-00805F9B34FB");
  @VisibleForTesting
  public UUID DI_FIRMWARE_REVISION = UUID.fromString("00002A26-0000-1000-8000-00805F9B34FB");
  @VisibleForTesting
  public UUID DI_HARDWARE_REVISION = UUID.fromString("00002A27-0000-1000-8000-00805F9B34FB");
  @VisibleForTesting
  public UUID DI_MANUFACTURER_NAME_STRING = UUID.fromString("00002A29-0000-1000-8000-00805F9B34FB");
  @VisibleForTesting
  public UUID DI_PNP_ID = UUID.fromString("00002A50-0000-1000-8000-00805F9B34FB");
  @VisibleForTesting
  public UUID IMU_MODEL_NAME = UUID.fromString("3AB1010C-F831-4395-B29D-570977D5BF94");
  @VisibleForTesting
  public UUID IMU_BASIC_MEASUREMENTS = UUID.fromString("3AB1010D-F831-4395-B29D-570977D5BF94");
  @VisibleForTesting
  public UUID IMU_P2P = UUID.fromString("3AB1010F-F831-4395-B29D-570977D5BF94");
  @VisibleForTesting
  public UUID IMU_QUATERNION = UUID.fromString("3AB10110-F831-4395-B29D-570977D5BF94");
  @VisibleForTesting
  public UUID IMU_ACELERATION_AND_ROTATION = UUID.fromString("3AB10111-F831-4395-B29D-570977D5BF94");
  @VisibleForTesting
  public UUID IMU_MAGNETOMETER = UUID.fromString("3AB10112-F831-4395-B29D-570977D5BF94");
  @VisibleForTesting
  public UUID IMU_DISTOCOM_RECEIVE = UUID.fromString("3AB10120-F831-4395-B29D-570977D5BF94");
  @VisibleForTesting
  public UUID IMU_DISTOCOM_TRANSMIT = UUID.fromString("3AB10121-F831-4395-B29D-570977D5BF94");
  @VisibleForTesting
  public UUID IMU_DISTOCOM_EVENT = UUID.fromString("3AB10122-F831-4395-B29D-570977D5BF94");
  @VisibleForTesting
  public UUID TH_TEMPERATURE_MEASUREMENT = UUID.fromString("00002A1C-0000-1000-8000-00805F9B34FB");
  public static final Map<UUID, BleCharacteristic> YetiUIIDMap = new HashMap();
  public static final Map<UUID, BleCharacteristic> BLEUIIDMap = new HashMap();
  public BluetoothManager bluetoothManager;
  protected BluetoothAdapter bluetoothAdapter;
  protected int discoverServicesDelay;
  protected int gattStatus = 0;
  protected boolean isBTStarted;
  protected BluetoothLeScanner bluetoothLEScanner;
  protected BleScanCallback CALLBACK_scan;
  protected boolean scanning = false;
  protected long checkScanCallbackSuccessfulStartTime = 0L;
  protected BleManagerGattCallback CALLBACK_gatt;
  boolean d = false;
  BroadcastReceiver e;
  BroadcastReceiver f;
  BroadcastReceiver g;
  BroadcastReceiver h;
  BroadcastReceiver i;
  public int RETRYPAIRINGLIMIT = 3;
  public int retryPairing = 0;
  protected static CountDownLatch pairingLatch;
  protected static CountDownLatch unBondingLatch;
  protected final Object lock = new Object();
  protected int pairingLatchAwaitTime = 0;
  protected int unBondingLatchAwaitTime = 0;
  protected boolean noPairingNeeded = true;
  protected boolean skipCommand;
  protected Timer commandStaller;
  boolean j = false;
  private boolean k;
  private Device.BTConnectionCallback l;
  public int pairingTimeDelayDelta = 2000;
  public int pairingTimeDelay = 0;
  
  public BleConnectionManager(BluetoothManager paramBluetoothManager, Context paramContext)
  {
    super(paramContext);
    setYetiUUIDMap();
    setBLEUUIDMap();
    setContext(paramContext);
    if ((paramBluetoothManager != null) && (paramContext != null))
    {
      setState(BaseConnectionManager.ConnectionState.disconnected, true);
      if (Build.VERSION.SDK_INT >= 21) {
        CALLBACK_scan = new BleScanCallback(this);
      } else {
        CALLBACK_scan = null;
      }
      bluetoothManager = paramBluetoothManager;
      bluetoothAdapter = bluetoothManager.getAdapter();
      skipCommand = false;
      commandStaller = new Timer();
      setLatchesTimeAccordingToVersion();
    }
    else
    {
      Logs.log(Logs.LogTypes.codeerror, "Unable to create the BleConnectionManager");
    }
  }
  
  public void setConnectionParameters(Object... paramVarArgs)
  {
    setBluetoothDevice((BluetoothDevice)paramVarArgs[0]);
  }
  
  public void setContext(Context paramContext)
  {
    super.setContext(paramContext);
    setBroadcastReceivers();
  }
  
  public void findAvailableDevices()
  {
    if (scanning == true)
    {
      Logs.log(Logs.LogTypes.verbose, "is already shouldScan ");
      stopScan();
    }
    Set localSet = bluetoothAdapter.getBondedDevices();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      BluetoothDevice localBluetoothDevice = (BluetoothDevice)localIterator.next();
      String str = localBluetoothDevice.getName();
      if ((str != null) && (str.length() >= 1)) {
        if (LeicaSdk.isYetiName(str)) {
          foundAvailableBluetoothDevice(str, localBluetoothDevice, false, false);
        }
      }
    }
    if (Build.VERSION.SDK_INT >= 21) {
      bluetoothLEScanner = bluetoothAdapter.getBluetoothLeScanner();
    }
    scanLeDevice(true);
  }
  
  protected void scanLeDevice(boolean paramBoolean)
  {
    try
    {
      if (paramBoolean)
      {
        Logs.log(Logs.LogTypes.debug, "Bluetooth, Scanning Started");
        scanning = true;
        if ((bluetoothLEScanner != null) && (Build.VERSION.SDK_INT >= 21))
        {
          bluetoothLEScanner.startScan(CALLBACK_scan);
          checkScanCallbackSuccessfulStartTime = System.currentTimeMillis();
        }
        if ((bluetoothAdapter != null) && (Build.VERSION.SDK_INT < 21)) {
          bluetoothAdapter.startDiscovery();
        }
      }
      else
      {
        Logs.log(Logs.LogTypes.debug, "Bluetooth, Scanning Stopped");
        if (scanning) {
          stopScan();
        }
      }
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.exception, "probably bluetooth adapter turned off by user", localException);
    }
  }
  
  public synchronized void stopScan()
  {
    Logs.log(Logs.LogTypes.debug, "called, scanning is: " + scanning);
    setState(BaseConnectionManager.ConnectionState.disconnected, true);
    scanning = false;
    try
    {
      if (!checkConnectionMethodsAvailable())
      {
        Logs.log(Logs.LogTypes.debug, "bluetooth adapter is OFF ?!" + scanning);
        return;
      }
      if ((bluetoothLEScanner != null) && (Build.VERSION.SDK_INT >= 21))
      {
        bluetoothLEScanner.stopScan(CALLBACK_scan);
        Logs.log(Logs.LogTypes.debug, "Bluetooth, Scanning Stopped");
      }
      if ((bluetoothAdapter != null) && (Build.VERSION.SDK_INT < 21))
      {
        bluetoothAdapter.cancelDiscovery();
        Logs.log(Logs.LogTypes.debug, "Bluetooth Adapter cancel discovery, Scanning Stopped");
      }
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.exception, "Exception stopping BLE Scanner", localException);
    }
  }
  
  public void stopDiscovery()
  {
    stopScan();
  }
  
  public void registerReceivers(Context paramContext)
  {
    try
    {
      super.registerReceivers(paramContext);
      IntentFilter localIntentFilter = new IntentFilter("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
      paramContext.registerReceiver(g, localIntentFilter);
      localIntentFilter = new IntentFilter("android.bluetooth.device.action.FOUND");
      context.registerReceiver(f, localIntentFilter);
      localIntentFilter = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
      context.registerReceiver(e, localIntentFilter);
      localIntentFilter = new IntentFilter("android.bluetooth.device.action.ACL_CONNECTED");
      context.registerReceiver(h, localIntentFilter);
      localIntentFilter = new IntentFilter("android.bluetooth.device.action.ACL_DISCONNECTED");
      context.registerReceiver(i, localIntentFilter);
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.debug, "Error registering receivers. NF");
    }
  }
  
  public void unregisterReceivers()
  {
    try
    {
      context.unregisterReceiver(g);
      context.unregisterReceiver(f);
      context.unregisterReceiver(e);
      context.unregisterReceiver(h);
      context.unregisterReceiver(i);
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.debug, "Error UnRegistering Receivers. NF");
    }
  }
  
  public void enableFunctionality()
  {
    BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (!localBluetoothAdapter.isEnabled()) {
      localBluetoothAdapter.enable();
    }
  }
  
  protected void setBroadcastReceivers()
  {
    if (e != null)
    {
      Logs.log(Logs.LogTypes.verbose, "bondStatChangedReceivers already existing");
      return;
    }
    if (f != null)
    {
      Logs.log(Logs.LogTypes.verbose, "actionFoundReceiver already existing");
      return;
    }
    if (g != null)
    {
      Logs.log(Logs.LogTypes.verbose, "discoveryChangedReceiver already existing");
      return;
    }
    if (h != null)
    {
      Logs.log(Logs.LogTypes.verbose, "connectActionReceiver already existing");
      return;
    }
    if (i != null)
    {
      Logs.log(Logs.LogTypes.verbose, "disconnectActionReceiver already existing");
      return;
    }
    if (context == null)
    {
      Logs.log(Logs.LogTypes.verbose, "context is null");
      return;
    }
    e = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramAnonymousIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        int i = paramAnonymousIntent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", -1);
        int j = paramAnonymousIntent.getIntExtra("android.bluetooth.device.extra.PREVIOUS_BOND_STATE", -1);
        Logs.log(Logs.LogTypes.debug, e + "deviceName: " + localBluetoothDevice.getName() + ", bondState: " + i + ", previous Bond State: " + j);
        switch (i)
        {
        case 11: 
          break;
        case 12: 
          if (BleConnectionManager.pairingLatch != null)
          {
            Logs.log(Logs.LogTypes.debug, "pairingLatch Countdown.");
            BleConnectionManager.pairingLatch.countDown();
          }
          break;
        case 10: 
          if (BleConnectionManager.unBondingLatch != null)
          {
            Logs.log(Logs.LogTypes.debug, "unBondingLatch Countdown.");
            BleConnectionManager.unBondingLatch.countDown();
          }
          break;
        }
        if (Build.VERSION.SDK_INT < 21) {
          if (i == 12)
          {
            Intent localIntent = new Intent("DEVICE_BONDED");
            localIntent.putExtra("deviceName", localBluetoothDevice.getName());
            localIntent.putExtra("deviceAddress", localBluetoothDevice.getAddress());
            LocalBroadcastManager.getInstance(paramAnonymousContext).sendBroadcast(localIntent);
            Logs.log(Logs.LogTypes.verbose, "broadcast intent sent");
          }
          else
          {
            Logs.log(Logs.LogTypes.verbose, "broadcast intent NOT sent");
          }
        }
      }
    };
    f = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (Build.VERSION.SDK_INT < 21)
        {
          String str1 = paramAnonymousIntent.getAction();
          Logs.log(Logs.LogTypes.debug, "action is: " + str1);
          if ("android.bluetooth.device.action.FOUND".equals(str1))
          {
            BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramAnonymousIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            String str2 = localBluetoothDevice.getName();
            String str3 = localBluetoothDevice.getAddress();
            Logs.log(Logs.LogTypes.verbose, "deviceName: " + str2 + ", deviceAdress: " + str3 + ", bondState: " + localBluetoothDevice.getBondState());
            if (str2 == null)
            {
              Logs.log(Logs.LogTypes.verbose, "deviceName is null");
              return;
            }
            if (str2.length() <= 10)
            {
              Logs.log(Logs.LogTypes.verbose, "deviceName is too short");
              if (Build.VERSION.SDK_INT >= 21)
              {
                Logs.log(Logs.LogTypes.debug, "sdk version: " + Build.VERSION.SDK_INT);
                long l = System.currentTimeMillis();
                if (l - checkScanCallbackSuccessfulStartTime < 4000L)
                {
                  Logs.log(Logs.LogTypes.verbose, "too early");
                  return;
                }
                Logs.log(Logs.LogTypes.verbose, "scan callback successful? - " + d);
                if (d == true) {
                  return;
                }
              }
            }
            if (LeicaSdk.validDeviceName(str2))
            {
              Logs.log(Logs.LogTypes.verbose, "matches if inside called for device: " + str2);
              foundAvailableBluetoothDevice(str2, localBluetoothDevice, false, false);
            }
            else
            {
              Logs.log(Logs.LogTypes.debug, "found NO-DISTO-DEVICE: " + str2);
            }
          }
        }
      }
    };
    g = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        String str = paramAnonymousIntent.getAction();
        if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(str))
        {
          if (!scanning) {
            return;
          }
          scanLeDevice(true);
        }
      }
    };
    h = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        Logs.log(Logs.LogTypes.debug, " Detected Device. ");
        String str = paramAnonymousIntent.getAction();
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramAnonymousIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        if (localBluetoothDevice != null)
        {
          Logs.log(Logs.LogTypes.verbose, "Device: " + localBluetoothDevice.getName() + " Action: " + str);
          if (("android.bluetooth.device.action.ACL_CONNECTED".equals(str)) && (LeicaSdk.isYetiName(localBluetoothDevice.getName()))) {
            foundAvailableBluetoothDevice(localBluetoothDevice.getName(), localBluetoothDevice, false, true);
          }
        }
        else
        {
          Logs.log(Logs.LogTypes.debug, " Detected Device is null.");
        }
      }
    };
    i = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        Logs.log(Logs.LogTypes.debug, " Device Disconnected. ");
        String str1 = paramAnonymousIntent.getAction();
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramAnonymousIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        if (localBluetoothDevice != null)
        {
          Logs.log(Logs.LogTypes.verbose, "Device: " + localBluetoothDevice.getName() + " Action: " + str1);
          if (("android.bluetooth.device.action.ACL_DISCONNECTED".equals(str1)) && (LeicaSdk.isYetiName(localBluetoothDevice.getName())))
          {
            String str2 = localBluetoothDevice.getName() + "+++" + localBluetoothDevice.getAddress();
            Logs.log(Logs.LogTypes.verbose, "DeviceId: " + str2);
            disconnectedACLBluetoothDevice(str2);
          }
        }
        else
        {
          Logs.log(Logs.LogTypes.debug, " Detected Device is null.");
        }
      }
    };
    Logs.log(Logs.LogTypes.debug, "receivers set");
  }
  
  protected void setLatchesTimeAccordingToVersion()
  {
    Logs.log(Logs.LogTypes.debug, "DISTO Version: 21Phone Version: " + Build.VERSION.SDK_INT + "M Version: " + 23);
    if (Build.VERSION.SDK_INT >= 23) {
      pairingLatchAwaitTime = 6000;
    } else {
      pairingLatchAwaitTime = 8000;
    }
    if (Build.VERSION.SDK_INT >= 23) {
      unBondingLatchAwaitTime = 3000;
    } else {
      unBondingLatchAwaitTime = 6000;
    }
  }
  
  public boolean pairAndBond()
  {
    BluetoothDevice localBluetoothDevice = getBluetoothDevice();
    int m = 2000;
    boolean bool1 = false;
    boolean bool2 = false;
    if (Build.VERSION.SDK_INT >= 21)
    {
      Logs.log(Logs.LogTypes.debug, "createBond called.");
      bool1 = localBluetoothDevice.createBond();
    }
    else
    {
      return false;
    }
    try
    {
      pairingLatch = new CountDownLatch(1);
      boolean bool3 = pairingLatch.await(pairingLatchAwaitTime, TimeUnit.MILLISECONDS);
      Logs.log(Logs.LogTypes.verbose, "awaiting: " + pairingLatchAwaitTime);
      if (!bool3)
      {
        pairingLatchAwaitTime += m;
        Logs.log(Logs.LogTypes.debug, "Unsuccessfull Bonding: Allotted time for pairAndBond has been reached. Waiting Time Increased to: " + pairingLatchAwaitTime + " miliseconds");
        bool2 = false;
      }
      else
      {
        Logs.log(Logs.LogTypes.debug, "Device is correctly Bonded and Paired, connection can begin.");
        bool2 = true;
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      Logs.log(Logs.LogTypes.exception, "Pairing Latch interrupted.", localInterruptedException);
      bool2 = false;
    }
    Logs.log(Logs.LogTypes.debug, "Pairing: create bond result: " + bool1);
    return bool2;
  }
  
  public boolean unBond()
  {
    return unBond(unBondingLatchAwaitTime);
  }
  
  public boolean unBond(int paramInt)
  {
    BluetoothDevice localBluetoothDevice = getBluetoothDevice();
    Logs.log(Logs.LogTypes.verbose, "remove bond for: " + localBluetoothDevice.getName());
    int m = 2000;
    boolean bool2 = false;
    try
    {
      unBondingLatch = new CountDownLatch(1);
      Method localMethod = localBluetoothDevice.getClass().getMethod("removeBond", (Class[])null);
      Object localObject = localMethod.invoke(localBluetoothDevice, (Object[])null);
      Logs.log(Logs.LogTypes.debug, "remove bond for: " + localBluetoothDevice.getName() + "UnBonding: remove bond result: " + localObject);
      boolean bool1 = unBondingLatch.await(paramInt, TimeUnit.MILLISECONDS);
      Logs.log(Logs.LogTypes.verbose, "awaiting: " + unBondingLatchAwaitTime);
      if (!bool1)
      {
        unBondingLatchAwaitTime += m;
        Logs.log(Logs.LogTypes.debug, "Unsuccessfull unBonding: Allotted time for unBonding has been reached, Waiting Time Increased to: " + unBondingLatchAwaitTime + " miliseconds.");
        bool2 = false;
      }
      else
      {
        Logs.log(Logs.LogTypes.debug, "Device UnBonded Successfully. Pair and bond process will begin.");
        bool2 = true;
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      Logs.log(Logs.LogTypes.exception, "unbond Latch interrupted.", localInterruptedException);
      bool2 = false;
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.exception, "fail", localException);
      bool2 = false;
    }
    return bool2;
  }
  
  public boolean checkConnectionMethodsAvailable()
  {
    if ((bluetoothManager.getAdapter() != null) && (bluetoothManager.getAdapter().isEnabled()) && (bluetoothManager.getAdapter().getState() != 10))
    {
      setState(BaseConnectionManager.ConnectionState.disconnected, true);
      Logs.log(Logs.LogTypes.debug, "Connection State changed: " + getState().toString());
      return true;
    }
    setState(BaseConnectionManager.ConnectionState.disconnected, true);
    Logs.log(Logs.LogTypes.debug, "Connection State changed: " + getState().toString());
    return false;
  }
  
  public boolean verifyConnection(String paramString)
  {
    return true;
  }
  
  protected void connectToDevice()
  {
    if (retryPairing < RETRYPAIRINGLIMIT)
    {
      Logs.log(Logs.LogTypes.debug, "connecting to device");
      setState(BaseConnectionManager.ConnectionState.connecting, true);
      BluetoothDevice localBluetoothDevice = getBluetoothDevice();
      boolean bool = false;
      if (localBluetoothDevice != null)
      {
        if (!noPairingNeeded)
        {
          Logs.log(Logs.LogTypes.debug, "pairing needed!");
          if (Build.VERSION.SDK_INT >= 21)
          {
            if (localBluetoothDevice.getBondState() != 12)
            {
              unBond();
              bool = pairAndBond();
            }
            else
            {
              bool = pairAndBond();
            }
          }
          else
          {
            unBond();
            bool = true;
          }
        }
        else
        {
          Logs.log(Logs.LogTypes.debug, "no pairing needed!");
          bool = true;
        }
        if ((currentBluetoothGatt == null) && (bool == true))
        {
          Logs.log(Logs.LogTypes.debug, "connect gatt");
          CALLBACK_gatt = new BleManagerGattCallback(this, currentBluetoothGatt, discoverServicesDelay);
          CALLBACK_gatt.isInitSequence = true;
          synchronized (lock)
          {
            currentBluetoothGatt = localBluetoothDevice.connectGatt(context, false, CALLBACK_gatt);
          }
        }
        else
        {
          Logs.log(Logs.LogTypes.debug, "connection not successfully prepared. ");
          retryPairing += 1;
          if (localBluetoothDevice.getBondState() == 12)
          {
            Logs.log(Logs.LogTypes.debug, "Device still bonded after failing to connect");
            unBond();
          }
          pairingTimeDelay += pairingTimeDelayDelta;
          try
          {
            Thread.sleep(pairingTimeDelay);
          }
          catch (InterruptedException localInterruptedException)
          {
            Logs.log(Logs.LogTypes.debug, "InterruptedException: ", localInterruptedException);
          }
          connectToDevice();
        }
      }
      else
      {
        Logs.log(Logs.LogTypes.exception, "Bluetooth Device is null. Connection can not be established.");
      }
    }
    else
    {
      setState(BaseConnectionManager.ConnectionState.disconnected, true);
      Logs.log(Logs.LogTypes.codeerror, "Connection Killed unable to bond the device. ");
      if (errorListener != null) {
        ErrorObject.sendErrorBluetoothDeviceUnableToPair(errorListener, this);
      }
    }
  }
  
  public void connect()
  {
    connectToDevice();
  }
  
  public synchronized void killConnection()
  {
    try
    {
      if ((currentBluetoothGatt != null) && (CALLBACK_gatt != null))
      {
        Logs.log(Logs.LogTypes.debug, "disconnect and close currentBluetoothGatt");
        currentBluetoothGatt.disconnect();
        Thread.sleep(400L);
        currentBluetoothGatt.close();
        currentBluetoothGatt = null;
        CALLBACK_gatt = null;
      }
    }
    catch (Exception localException1)
    {
      Logs.log(Logs.LogTypes.exception, "Error killing the connection. NF", localException1);
    }
    try
    {
      if (e != null)
      {
        context.unregisterReceiver(e);
        e = null;
      }
      if (f != null)
      {
        context.unregisterReceiver(f);
        f = null;
      }
    }
    catch (Exception localException2)
    {
      Logs.log(Logs.LogTypes.debug, "Receivers not registered. NF");
    }
    Logs.log(Logs.LogTypes.debug, "unregister receivers");
  }
  
  void a()
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        if (connectionListener == null)
        {
          Logs.log(Logs.LogTypes.debug, "connectionListener is null");
          return;
        }
        Logs.log(Logs.LogTypes.debug, "seems like we are connected!");
        if (connectionListener != null) {
          connectionListener.onConnected(BleConnectionManager.this);
        }
      }
    }).start();
  }
  
  public void pauseBTConnection(Device.BTConnectionCallback paramBTConnectionCallback)
  {
    l = paramBTConnectionCallback;
    if (!getIsBTStarted()) {
      return;
    }
    if (CALLBACK_gatt != null) {
      CALLBACK_gatt.toggleNotifications(currentBluetoothGatt, false);
    } else {
      setState(BaseConnectionManager.ConnectionState.disconnected, true);
    }
    setIsBTStarted(false);
  }
  
  public void startBTConnection(Device.BTConnectionCallback paramBTConnectionCallback)
  {
    l = paramBTConnectionCallback;
    if (getIsBTStarted() == true) {
      return;
    }
    if (CALLBACK_gatt != null)
    {
      k = true;
      CALLBACK_gatt.discoverServices(currentBluetoothGatt);
    }
    else
    {
      setState(BaseConnectionManager.ConnectionState.disconnected, true);
    }
    setIsBTStarted(true);
  }
  
  public boolean readModelCharacteristic()
  {
    boolean bool = readCharacteristic(DS_MODEL_NAME, DISTO_SERVICE);
    Logs.log(Logs.LogTypes.debug, "Model read from DISTO Service: " + bool);
    if (!bool)
    {
      bool = readCharacteristic(DI_MODEL_NUMBER, DEVICE_INFORMATION_SERVICE);
      Logs.log(Logs.LogTypes.debug, "model Read from DEVICE_INFORMATION_SERVICE: " + bool);
    }
    return bool;
  }
  
  protected boolean readCharacteristic(UUID paramUUID1, UUID paramUUID2)
  {
    BluetoothGattCharacteristic localBluetoothGattCharacteristic = getCharacteristic(paramUUID1, paramUUID2);
    if ((CALLBACK_gatt != null) && (localBluetoothGattCharacteristic != null)) {
      return CALLBACK_gatt.readCharacteristic(currentBluetoothGatt, localBluetoothGattCharacteristic);
    }
    return false;
  }
  
  public void readAllCharacteristics(Device.BTConnectionCallback paramBTConnectionCallback)
  {
    l = paramBTConnectionCallback;
    k = true;
    readCharacteristic(DS_HORIZONTAL_INCLINE, DISTO_SERVICE);
    readCharacteristic(DS_VERTICAL_INCLINE, DISTO_SERVICE);
    readCharacteristic(DI_PNP_ID, DEVICE_INFORMATION_SERVICE);
    readCharacteristic(DI_HARDWARE_REVISION, DEVICE_INFORMATION_SERVICE);
    readCharacteristic(BT_BATTERY_POWER_STATE, BATTERY_SERVICE);
    readCharacteristic(BT_BATTERY_LEVEL, BATTERY_SERVICE);
    readCharacteristic(DI_SERIAL_NUMBER, DEVICE_INFORMATION_SERVICE);
    readCharacteristic(DI_MODEL_NUMBER, DEVICE_INFORMATION_SERVICE);
    readCharacteristic(DS_MODEL_NAME, DISTO_SERVICE);
    readCharacteristic(DS_RESPONSE, DISTO_SERVICE);
    readCharacteristic(DS_DISTANCE, DISTO_SERVICE);
    readCharacteristic(DS_DISTANCE_UNIT, DISTO_SERVICE);
    readCharacteristic(DS_INCLINATION, DISTO_SERVICE);
    readCharacteristic(DS_INCLINATION_UNIT, DISTO_SERVICE);
    readCharacteristic(DS_DIRECTION, DISTO_SERVICE);
    readCharacteristic(DS_DIRECTION_UNIT, DISTO_SERVICE);
  }
  
  protected BluetoothGattCharacteristic getCharacteristic(UUID paramUUID1, UUID paramUUID2)
  {
    BluetoothGattCharacteristic localBluetoothGattCharacteristic = null;
    if (currentBluetoothGatt != null)
    {
      Logs.log(Logs.LogTypes.debug, "get characteristics - gatt is not null");
      BluetoothGatt localBluetoothGatt = currentBluetoothGatt;
      BluetoothGattService localBluetoothGattService = localBluetoothGatt.getService(paramUUID2);
      if (localBluetoothGattService != null)
      {
        Logs.log(Logs.LogTypes.debug, "get characteristics - bgs is not null");
        localBluetoothGattCharacteristic = localBluetoothGattService.getCharacteristic(paramUUID1);
        if (localBluetoothGattCharacteristic == null) {
          Logs.log(Logs.LogTypes.debug, "get characteristics - requestedCharacteristic is null");
        }
      }
    }
    return localBluetoothGattCharacteristic;
  }
  
  public void sendCommand(Command paramCommand)
  {
    if (!k)
    {
      if (paramCommand != null)
      {
        Logs.log(Logs.LogTypes.verbose, "-sending- called with type: " + paramCommand.getCommandValue());
        Object localObject;
        if ((skipCommand) && (!hasNewBleService()))
        {
          Logs.log(Logs.LogTypes.verbose, "due to command delay of 200 milliseconds, wait a bit");
          localObject = new WaitAmoment();
          ((WaitAmoment)localObject).waitAmoment(100L);
          return;
        }
        try
        {
          localObject = paramCommand.getBytePayload();
          Logs.log(Logs.LogTypes.debug, "-sending-getPayload is: " + paramCommand.getPayload());
          if (currentBluetoothGatt != null)
          {
            BluetoothGattService localBluetoothGattService = currentBluetoothGatt.getService(DISTO_SERVICE);
            BluetoothGattCharacteristic localBluetoothGattCharacteristic;
            if (hasNewBleService() == true) {
              localBluetoothGattCharacteristic = localBluetoothGattService.getCharacteristic(IMU_DISTOCOM_RECEIVE);
            } else {
              localBluetoothGattCharacteristic = localBluetoothGattService.getCharacteristic(DS_COMMAND);
            }
            if (localBluetoothGattCharacteristic != null)
            {
              if (!hasNewBleService())
              {
                skipCommand = true;
                commandStaller.schedule(new TimerTask()
                {
                  public void run()
                  {
                    skipCommand = false;
                  }
                }, 200L);
              }
              CALLBACK_gatt.writeCharacteristic(currentBluetoothGatt, localBluetoothGattCharacteristic, (byte[])localObject);
            }
            else
            {
              Logs.log(Logs.LogTypes.codeerror, "-sending- No corresponding Characteristic found.");
              if (errorListener != null) {
                errorListener.onError(new ErrorObject(2101, "Send Command. No corresponding Characteristic found. . command: " + paramCommand.getCommandValue()), null);
              }
            }
          }
        }
        catch (Exception localException)
        {
          Logs.log(Logs.LogTypes.codeerror, "-sending- Error in GATT characteristics. exceptionmessage: " + localException.getMessage());
        }
      }
    }
    else {
      errorListener.onError(new ErrorObject(8000, "The BleQueue is busy performing other tasks, command could not be sent"), null);
    }
  }
  
  void a(BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
  {
    ReceivedData localReceivedData = new ReceivedData();
    String str = "";
    ErrorObject localErrorObject = null;
    if (YetiUIIDMap.containsKey(paramBluetoothGattCharacteristic.getUuid()) == true)
    {
      str = ((BleCharacteristic)YetiUIIDMap.get(paramBluetoothGattCharacteristic.getUuid())).getId();
      Logs.log(Logs.LogTypes.verbose, "YetiUUID: " + str);
      localErrorObject = localReceivedData.parseYetiReceivedData(str, paramBluetoothGattCharacteristic.getValue());
    }
    else if (BLEUIIDMap.containsKey(paramBluetoothGattCharacteristic.getUuid()) == true)
    {
      str = ((BleCharacteristic)BLEUIIDMap.get(paramBluetoothGattCharacteristic.getUuid())).getId();
      Logs.log(Logs.LogTypes.verbose, "BLEUIID: " + str);
      localErrorObject = localReceivedData.parseBleReceivedData(str, paramBluetoothGattCharacteristic.getValue());
    }
    if (!str.equals(""))
    {
      if (bleReceivedDataListener != null) {
        try
        {
          bleReceivedDataListener.onBleDataReceived(localReceivedData, localErrorObject);
        }
        catch (DeviceException localDeviceException)
        {
          Logs.log(Logs.LogTypes.codeerror, "Wrong method called. Method not available for this device type. ");
        }
      } else {
        Logs.log(Logs.LogTypes.codeerror, "no receivedDataListener is set.");
      }
    }
    else {
      Logs.log(Logs.LogTypes.codeerror, "Information received from an un-mapped characteristic. - NOT Processed ");
    }
  }
  
  protected void setBLEUUIDMap()
  {
    BLEUIIDMap.put(DS_MODEL_NAME, new BleCharacteristic(DISTO_SERVICE, DS_MODEL_NAME, "DS_MODEL_NAME", false));
    BLEUIIDMap.put(DS_DISTANCE, new BleCharacteristic(DISTO_SERVICE, DS_DISTANCE, "DS_DISTANCE", true));
    BLEUIIDMap.put(DS_DISTANCE_UNIT, new BleCharacteristic(DISTO_SERVICE, DS_DISTANCE_UNIT, "DS_DISTANCE_UNIT", true));
    BLEUIIDMap.put(DS_INCLINATION, new BleCharacteristic(DISTO_SERVICE, DS_INCLINATION, "DS_INCLINATION", true));
    BLEUIIDMap.put(DS_INCLINATION_UNIT, new BleCharacteristic(DISTO_SERVICE, DS_INCLINATION_UNIT, "DS_INCLINATION_UNIT", true));
    BLEUIIDMap.put(DS_DIRECTION, new BleCharacteristic(DISTO_SERVICE, DS_DIRECTION, "DS_DIRECTION", true));
    BLEUIIDMap.put(DS_DIRECTION_UNIT, new BleCharacteristic(DISTO_SERVICE, DS_DIRECTION_UNIT, "DS_DIRECTION_UNIT", true));
    BLEUIIDMap.put(DS_HORIZONTAL_INCLINE, new BleCharacteristic(DISTO_SERVICE, DS_HORIZONTAL_INCLINE, "DS_HORIZONTAL_INCLINE", true));
    BLEUIIDMap.put(DS_VERTICAL_INCLINE, new BleCharacteristic(DISTO_SERVICE, DS_VERTICAL_INCLINE, "DS_VERTICAL_INCLINE", true));
    BLEUIIDMap.put(DS_RESPONSE, new BleCharacteristic(DISTO_SERVICE, DS_RESPONSE, "DS_RESPONSE", true));
    BLEUIIDMap.put(DI_MODEL_NUMBER, new BleCharacteristic(DISTO_SERVICE, DI_MODEL_NUMBER, "DI_MODEL_NUMBER", false));
    BLEUIIDMap.put(DI_FIRMWARE_REVISION, new BleCharacteristic(DISTO_SERVICE, DI_FIRMWARE_REVISION, "DI_FIRMWARE_REVISION", false));
    BLEUIIDMap.put(DI_HARDWARE_REVISION, new BleCharacteristic(DISTO_SERVICE, DI_HARDWARE_REVISION, "DI_HARDWARE_REVISION", false));
    BLEUIIDMap.put(DI_PNP_ID, new BleCharacteristic(DISTO_SERVICE, DI_PNP_ID, "DI_PNP_ID", false));
    BLEUIIDMap.put(DI_SERIAL_NUMBER, new BleCharacteristic(DISTO_SERVICE, DI_SERIAL_NUMBER, "DI_SERIAL_NUMBER", false));
    BLEUIIDMap.put(DI_MANUFACTURER_NAME_STRING, new BleCharacteristic(DISTO_SERVICE, DI_MANUFACTURER_NAME_STRING, "DI_MANUFACTURER_NAME_STRING", false));
    BLEUIIDMap.put(BT_BATTERY_LEVEL, new BleCharacteristic(DISTO_SERVICE, BT_BATTERY_LEVEL, "BT_BATTERY_LEVEL", false));
    BLEUIIDMap.put(BT_BATTERY_POWER_STATE, new BleCharacteristic(DISTO_SERVICE, BT_BATTERY_POWER_STATE, "BT_BATTERY_POWER_STATE", false));
    BLEUIIDMap.put(TH_TEMPERATURE_MEASUREMENT, new BleCharacteristic(DISTO_SERVICE, TH_TEMPERATURE_MEASUREMENT, "TH_TEMPERATURE_MEASUREMENT", false));
  }
  
  protected void setYetiUUIDMap()
  {
    YetiUIIDMap.put(IMU_BASIC_MEASUREMENTS, new BleCharacteristic(DISTO_SERVICE, IMU_BASIC_MEASUREMENTS, "IMU_BASIC_MEASUREMENTS", true));
    YetiUIIDMap.put(IMU_P2P, new BleCharacteristic(DISTO_SERVICE, IMU_P2P, "IMU_P2P", true));
    YetiUIIDMap.put(IMU_QUATERNION, new BleCharacteristic(DISTO_SERVICE, IMU_QUATERNION, "IMU_QUATERNION", true));
    YetiUIIDMap.put(IMU_ACELERATION_AND_ROTATION, new BleCharacteristic(DISTO_SERVICE, IMU_ACELERATION_AND_ROTATION, "IMU_ACELERATION_AND_ROTATION", true));
    YetiUIIDMap.put(IMU_MAGNETOMETER, new BleCharacteristic(DISTO_SERVICE, IMU_MAGNETOMETER, "IMU_MAGNETOMETER", true));
    YetiUIIDMap.put(IMU_DISTOCOM_TRANSMIT, new BleCharacteristic(DISTO_SERVICE, IMU_DISTOCOM_TRANSMIT, "IMU_DISTOCOM_TRANSMIT", true));
    YetiUIIDMap.put(IMU_DISTOCOM_EVENT, new BleCharacteristic(DISTO_SERVICE, IMU_DISTOCOM_EVENT, "IMU_DISTOCOM_EVENT", true));
    YetiUIIDMap.put(IMU_DISTOCOM_RECEIVE, new BleCharacteristic(DISTO_SERVICE, IMU_DISTOCOM_RECEIVE, "IMU_DISTOCOM_RECEIVE", false));
  }
  
  public boolean hasNewBleService()
  {
    return j;
  }
  
  public List<BleCharacteristic> getAllCharacteristics()
  {
    return c;
  }
  
  public int getGattStatus()
  {
    return gattStatus;
  }
  
  public boolean getIsBTStarted()
  {
    Logs.log(Logs.LogTypes.debug, " getIsBTStarted: " + isBTStarted);
    return isBTStarted;
  }
  
  public boolean isNoPairingNeeded()
  {
    return noPairingNeeded;
  }
  
  public void setDiscoverServicesDelay(int paramInt)
  {
    discoverServicesDelay = paramInt;
  }
  
  public void setGattStatus(int paramInt)
  {
    gattStatus = paramInt;
  }
  
  public void setIsBTStarted(boolean paramBoolean)
  {
    isBTStarted = paramBoolean;
    Logs.log(Logs.LogTypes.debug, " getIsBTStarted: " + paramBoolean);
  }
  
  public void setNoPairingNeeded(boolean paramBoolean)
  {
    noPairingNeeded = paramBoolean;
  }
  
  public void onBluetoothDeviceFound(String paramString, BluetoothDevice paramBluetoothDevice, boolean paramBoolean1, boolean paramBoolean2)
  {
    foundAvailableBluetoothDevice(paramString, paramBluetoothDevice, paramBoolean1, paramBoolean2);
  }
  
  public void onScanCallbackSuccessful()
  {
    d = true;
  }
  
  public void onConnectionStateChange(BaseConnectionManager.ConnectionState paramConnectionState, boolean paramBoolean)
  {
    setState(paramConnectionState, paramBoolean);
  }
  
  public void onGattStatusChanged(int paramInt)
  {
    setGattStatus(paramInt);
  }
  
  public void onNewBleServiceDetected()
  {
    j = true;
  }
  
  public void onCharacteristicFound(BleCharacteristic paramBleCharacteristic)
  {
    c.add(paramBleCharacteristic);
  }
  
  public void onReceivedCommandResponse(BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
  {
    a(paramBluetoothGattCharacteristic);
  }
  
  public void onQueueStateChanged(boolean paramBoolean)
  {
    k = paramBoolean;
    if ((l != null) && (!paramBoolean))
    {
      l.onFinished();
      l = null;
    }
  }
  
  public void onSetupisFinished()
  {
    a();
  }
  
  public boolean isBleOperationInProgress()
  {
    return k;
  }
}
