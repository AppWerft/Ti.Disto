package ch.leica.sdk.connection.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.os.Looper;
import ch.leica.sdk.connection.BaseConnectionManager.ConnectionState;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class BleManagerGattCallback
  extends BluetoothGattCallback
  implements BleQueue.QueueListener
{
  private BleProcessListener d;
  private BleQueue e;
  private int f = 3;
  private Stack<BleCharacteristic> g = new Stack();
  boolean a = false;
  private int h;
  private int i = 3;
  private final Object j = new Object();
  final Lock b = new ReentrantLock();
  final Condition c = b.newCondition();
  public boolean isInitSequence = true;
  private boolean k = false;
  
  public BleManagerGattCallback(BleProcessListener paramBleProcessListener, BluetoothGatt paramBluetoothGatt, int paramInt)
  {
    d = paramBleProcessListener;
    h = paramInt;
    e = new BleQueue(paramBluetoothGatt, this);
  }
  
  public boolean readCharacteristic(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
  {
    return e.readCharacteristic(paramBluetoothGatt, paramBluetoothGattCharacteristic);
  }
  
  private void a(BluetoothGatt paramBluetoothGatt, boolean paramBoolean)
  {
    e.operationInProgress = true;
    try
    {
      List localList = paramBluetoothGatt.getServices();
      Iterator localIterator1 = localList.iterator();
      while (localIterator1.hasNext())
      {
        BluetoothGattService localBluetoothGattService = (BluetoothGattService)localIterator1.next();
        Logs.log(Logs.LogTypes.debug, "Service: " + localBluetoothGattService.getUuid());
        Iterator localIterator2 = localBluetoothGattService.getCharacteristics().iterator();
        Object localObject;
        while (localIterator2.hasNext())
        {
          localObject = (BluetoothGattCharacteristic)localIterator2.next();
          d.onCharacteristicFound(new BleCharacteristic(localBluetoothGattService.getUuid(), ((BluetoothGattCharacteristic)localObject).getUuid(), "id"));
          Logs.log(Logs.LogTypes.debug, " Characteristic: " + ((BluetoothGattCharacteristic)localObject).getUuid() + " Enable: " + paramBoolean);
        }
        if (localBluetoothGattService.getUuid() == null)
        {
          Logs.log(Logs.LogTypes.debug, "found service UUID is null");
        }
        else if (localBluetoothGattService.getUuid().equals(BleConnectionManager.DISTO_SERVICE))
        {
          localIterator2 = BleConnectionManager.BLEUIIDMap.entrySet().iterator();
          BluetoothGattCharacteristic localBluetoothGattCharacteristic;
          while (localIterator2.hasNext())
          {
            localObject = (Map.Entry)localIterator2.next();
            if (((BleCharacteristic)((Map.Entry)localObject).getValue()).isEnable() == true)
            {
              localBluetoothGattCharacteristic = localBluetoothGattService.getCharacteristic((UUID)((Map.Entry)localObject).getKey());
              if (localBluetoothGattCharacteristic != null) {
                a(paramBluetoothGatt, (BleCharacteristic)((Map.Entry)localObject).getValue(), paramBoolean);
              }
            }
          }
          localIterator2 = BleConnectionManager.YetiUIIDMap.entrySet().iterator();
          while (localIterator2.hasNext())
          {
            localObject = (Map.Entry)localIterator2.next();
            if (((BleCharacteristic)((Map.Entry)localObject).getValue()).isEnable() == true)
            {
              localBluetoothGattCharacteristic = localBluetoothGattService.getCharacteristic((UUID)((Map.Entry)localObject).getKey());
              if (localBluetoothGattCharacteristic != null)
              {
                k = true;
                d.onNewBleServiceDetected();
                a(paramBluetoothGatt, (BleCharacteristic)((Map.Entry)localObject).getValue(), paramBoolean);
              }
            }
          }
        }
      }
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.exception, " Error discovering services: " + localException.getMessage());
    }
    e.operationInProgress = false;
  }
  
  private void a(BluetoothGatt paramBluetoothGatt, BleCharacteristic paramBleCharacteristic, boolean paramBoolean)
  {
    paramBleCharacteristic.setEnable(paramBoolean);
    e.setNotifications(paramBluetoothGatt, paramBleCharacteristic);
    Logs.log(Logs.LogTypes.debug, " Characteristic UUID: " + paramBleCharacteristic.getCharacteristicUUID() + " Enable:  " + paramBoolean);
  }
  
  public void onConnectionStateChange(BluetoothGatt paramBluetoothGatt, int paramInt1, int paramInt2)
  {
    Logs.log(Logs.LogTypes.debug, "status: " + paramInt1 + ", state:  " + paramInt2);
    d.onGattStatusChanged(paramInt1);
    if ((paramInt1 == 0) && (paramInt2 == 2))
    {
      Logs.log(Logs.LogTypes.debug, "CONNECTED");
      d.onConnectionStateChange(BaseConnectionManager.ConnectionState.connected, false);
      discoverServices(paramBluetoothGatt);
    }
    else if (paramInt2 == 0)
    {
      e.operationInProgress = true;
      synchronized (j)
      {
        e.taskQueue.clear();
      }
      Logs.log(Logs.LogTypes.debug, "DISCONNECTED");
      d.onConnectionStateChange(BaseConnectionManager.ConnectionState.disconnected, true);
    }
    else if (paramInt2 == 1)
    {
      Logs.log(Logs.LogTypes.debug, "CONNECTING");
    }
    else
    {
      Logs.log(Logs.LogTypes.debug, "UNKNOWN_STATE");
    }
  }
  
  public void onServicesDiscovered(BluetoothGatt paramBluetoothGatt, int paramInt)
  {
    Logs.log(Logs.LogTypes.debug, "On Services Discovered Status: " + paramInt);
    toggleNotifications(paramBluetoothGatt, true);
    a = false;
  }
  
  public void onCharacteristicRead(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, int paramInt)
  {
    if (paramInt != 0)
    {
      Logs.log(Logs.LogTypes.debug, "GATT NOT SUCCESS");
      return;
    }
    Logs.log(Logs.LogTypes.debug, "uuid: " + paramBluetoothGattCharacteristic.getUuid() + ", value: " + new String(paramBluetoothGattCharacteristic.getValue()));
    d.onReceivedCommandResponse(paramBluetoothGattCharacteristic);
    e.operationInProgress = false;
    e.nextRequest();
  }
  
  public void onCharacteristicChanged(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
  {
    Logs.log(Logs.LogTypes.debug, "Characteristic changed: " + paramBluetoothGattCharacteristic.getUuid().toString());
    onCharacteristicRead(paramBluetoothGatt, paramBluetoothGattCharacteristic, 0);
  }
  
  public void onDescriptorRead(BluetoothGatt paramBluetoothGatt, BluetoothGattDescriptor paramBluetoothGattDescriptor, int paramInt) {}
  
  public void onDescriptorWrite(BluetoothGatt paramBluetoothGatt, BluetoothGattDescriptor paramBluetoothGattDescriptor, int paramInt)
  {
    if (paramInt == 0)
    {
      if ((e.taskQueue.size() == 0) && (isInitSequence == true))
      {
        d.onSetupisFinished();
        isInitSequence = false;
      }
    }
    else if (paramInt == 5)
    {
      if (paramBluetoothGatt.getDevice().getBondState() != 10) {
        Logs.log(Logs.LogTypes.codeerror, "Phone has lost bonding information");
      }
    }
    else {
      Logs.log(Logs.LogTypes.codeerror, "Error on writing descriptor " + paramInt);
    }
    e.operationInProgress = false;
    e.nextRequest();
  }
  
  public final void onCharacteristicWrite(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, int paramInt)
  {
    if (k)
    {
      Logs.log(Logs.LogTypes.debug, "ack");
      b.lock();
      c.signal();
      b.unlock();
    }
  }
  
  public void discoverServices(final BluetoothGatt paramBluetoothGatt)
  {
    Logs.log(Logs.LogTypes.debug, "DiscoverServicesDelay: " + h);
    new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
    {
      public void run()
      {
        if (!a)
        {
          if (paramBluetoothGatt != null) {
            try
            {
              a = true;
              if (paramBluetoothGatt.getDevice().getBondState() != 11)
              {
                Logs.log(Logs.LogTypes.debug, "Discover Services is called");
                paramBluetoothGatt.discoverServices();
              }
              else
              {
                Logs.log(Logs.LogTypes.debug, "BondState: " + paramBluetoothGatt.getDevice().getBondState());
              }
            }
            catch (Exception localException)
            {
              Logs.log(Logs.LogTypes.exception, "Error on connection state change", localException);
            }
          } else {
            Logs.log(Logs.LogTypes.codeerror, "Error on connection state changeDiscoverServices has not been called, bluetoothGatt is null.");
          }
        }
        else {
          Logs.log(Logs.LogTypes.debug, "Error on connection state changeDiscoverServices is already running it has not been called again");
        }
      }
    }, h);
  }
  
  public void toggleNotifications(BluetoothGatt paramBluetoothGatt, boolean paramBoolean)
  {
    Logs.log(Logs.LogTypes.debug, "Notifications are set to: " + paramBoolean);
    ReentrantLock localReentrantLock = new ReentrantLock();
    localReentrantLock.lock();
    try
    {
      if (paramBoolean == true)
      {
        try
        {
          a(paramBluetoothGatt, paramBoolean);
          if (e.taskQueue.size() > 0)
          {
            e.operationInProgress = false;
            e.nextRequest();
          }
          else
          {
            Logs.log(Logs.LogTypes.debug, "No characteristics were found.");
            if (i > 0)
            {
              i -= 1;
              h += 500;
              a = false;
              discoverServices(paramBluetoothGatt);
            }
            else
            {
              d.onConnectionStateChange(BaseConnectionManager.ConnectionState.disconnected, true);
            }
          }
        }
        catch (Exception localException1)
        {
          Logs.log(Logs.LogTypes.exception, "exception: " + localException1.getMessage(), localException1);
        }
      }
      else
      {
        e.operationInProgress = false;
        Logs.log(Logs.LogTypes.debug, "disableNotificationsCharacteristics Task Size: " + g.size());
        Iterator localIterator = g.iterator();
        while (localIterator.hasNext())
        {
          BleCharacteristic localBleCharacteristic = (BleCharacteristic)localIterator.next();
          e.setNotifications(paramBluetoothGatt, localBleCharacteristic);
        }
        g.clear();
        e.nextRequest();
      }
    }
    catch (Exception localException2)
    {
      Logs.log(Logs.LogTypes.exception, "Error, Toogle Notifications ENABLE: " + paramBoolean, localException2);
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public void writeCharacteristic(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, byte[] paramArrayOfByte)
  {
    Logs.log(Logs.LogTypes.debug, "write - begin");
    b.lock();
    if (k) {
      paramBluetoothGattCharacteristic.setWriteType(2);
    } else {
      paramBluetoothGattCharacteristic.setWriteType(1);
    }
    try
    {
      int m = (int)Math.ceil(paramArrayOfByte.length / 200.0D);
      Logs.log(Logs.LogTypes.debug, "package amount: " + m);
      for (int n = 0; n < m; n++)
      {
        int i1 = n * 200;
        int i2 = Math.min(i1 + 200, paramArrayOfByte.length);
        Logs.log(Logs.LogTypes.debug, "write package (" + (n + 1) + "/" + m + ")");
        paramBluetoothGattCharacteristic.setValue(Arrays.copyOfRange(paramArrayOfByte, i1, i2));
        paramBluetoothGatt.writeCharacteristic(paramBluetoothGattCharacteristic);
        if (k) {
          c.await();
        }
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      Logs.log(Logs.LogTypes.exception, "Threading exception. caused by: ", localInterruptedException);
    }
    finally
    {
      b.unlock();
    }
    Logs.log(Logs.LogTypes.debug, "write - end");
  }
  
  public void onNotificationEnd(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      Logs.log(Logs.LogTypes.codeerror, "UUID: " + paramBluetoothGattCharacteristic.getUuid() + " gatt.writeDescriptor returned false mRetrys: " + h);
      try
      {
        Thread.sleep(1000L);
      }
      catch (InterruptedException localInterruptedException)
      {
        Logs.log(Logs.LogTypes.exception, "Threading exception. caused by: ", localInterruptedException);
      }
      if (f > 0)
      {
        f -= 1;
        synchronized (j)
        {
          e.taskQueue.clear();
        }
        h += 500;
        a = false;
        discoverServices(paramBluetoothGatt);
      }
      else
      {
        synchronized (j)
        {
          e.taskQueue.clear();
        }
        d.onConnectionStateChange(BaseConnectionManager.ConnectionState.disconnected, true);
        d.onConnectionStateChange(BaseConnectionManager.ConnectionState.disconnected, true);
      }
    }
    else
    {
      Logs.log(Logs.LogTypes.debug, "gatt.writeDescriptor returned true");
    }
  }
  
  public void onCharacteristicEnabled(BleCharacteristic paramBleCharacteristic)
  {
    g.push(paramBleCharacteristic);
  }
  
  public void onQueueStateChanged(boolean paramBoolean)
  {
    d.onQueueStateChanged(paramBoolean);
  }
  
  public static abstract interface BleProcessListener
  {
    public abstract void onConnectionStateChange(BaseConnectionManager.ConnectionState paramConnectionState, boolean paramBoolean);
    
    public abstract void onGattStatusChanged(int paramInt);
    
    public abstract void onNewBleServiceDetected();
    
    public abstract void onCharacteristicFound(BleCharacteristic paramBleCharacteristic);
    
    public abstract void onSetupisFinished();
    
    public abstract void onReceivedCommandResponse(BluetoothGattCharacteristic paramBluetoothGattCharacteristic);
    
    public abstract void onQueueStateChanged(boolean paramBoolean);
  }
}
