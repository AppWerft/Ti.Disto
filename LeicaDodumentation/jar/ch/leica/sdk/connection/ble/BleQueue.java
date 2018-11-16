package ch.leica.sdk.connection.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class BleQueue
{
  protected final Object lock = new Object();
  public boolean operationInProgress = true;
  public final Queue<BleRequest> taskQueue = new LinkedList();
  private BluetoothGatt a = null;
  private QueueListener b;
  
  public BleQueue(BluetoothGatt paramBluetoothGatt, QueueListener paramQueueListener)
  {
    b = paramQueueListener;
    a = paramBluetoothGatt;
  }
  
  private boolean a(BleRequest paramBleRequest)
  {
    Logs.log(Logs.LogTypes.debug, "Request: " + a.toString());
    synchronized (lock)
    {
      taskQueue.add(paramBleRequest);
    }
    nextRequest();
    return true;
  }
  
  protected boolean readCharacteristic(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
  {
    a = paramBluetoothGatt;
    return a(BleRequest.newReadRequest(paramBluetoothGattCharacteristic));
  }
  
  public final boolean setNotifications(BluetoothGatt paramBluetoothGatt, BleCharacteristic paramBleCharacteristic)
  {
    b.onQueueStateChanged(true);
    a = paramBluetoothGatt;
    return a(BleRequest.newEnableNotificationsRequest(paramBleCharacteristic));
  }
  
  protected synchronized boolean internalEnableNotifications(BleCharacteristic paramBleCharacteristic)
  {
    boolean bool1 = false;
    int i = 0;
    if ((a == null) || (paramBleCharacteristic == null))
    {
      Logs.log(Logs.LogTypes.codeerror, "Error Enabling Notifications. ");
    }
    else
    {
      BluetoothGattCharacteristic localBluetoothGattCharacteristic = a.getService(paramBleCharacteristic.getServiceUUID()).getCharacteristic(paramBleCharacteristic.getCharacteristicUUID());
      if (localBluetoothGattCharacteristic != null)
      {
        UUID localUUID = localBluetoothGattCharacteristic.getUuid();
        paramBleCharacteristic.setNotificationValue(localBluetoothGattCharacteristic.getProperties());
        Logs.log(Logs.LogTypes.debug, "call gatt.setCharacteristicNotification with: " + paramBleCharacteristic.isEnable());
        boolean bool2 = a.setCharacteristicNotification(localBluetoothGattCharacteristic, paramBleCharacteristic.isEnable());
        Logs.log(Logs.LogTypes.debug, "gatt.setCharacteristicNotification returned " + bool2 + ", UUID: " + localBluetoothGattCharacteristic.getUuid());
        if (!bool2)
        {
          i = 0;
        }
        else
        {
          if (paramBleCharacteristic.isEnable() == true)
          {
            BleCharacteristic localBleCharacteristic = new BleCharacteristic(paramBleCharacteristic.getServiceUUID(), paramBleCharacteristic.getCharacteristicUUID(), paramBleCharacteristic.getId());
            localBleCharacteristic.setEnable(false);
            b.onCharacteristicEnabled(localBleCharacteristic);
          }
          i = 1;
        }
        if (i == 1)
        {
          int j = localBluetoothGattCharacteristic.getDescriptors().size();
          if (j > 0)
          {
            BluetoothGattDescriptor localBluetoothGattDescriptor = (BluetoothGattDescriptor)localBluetoothGattCharacteristic.getDescriptors().get(0);
            Logs.log(Logs.LogTypes.debug, "characteristic: " + localUUID + " Number of Descriptors: " + j);
            if (paramBleCharacteristic.isEnable() == true)
            {
              if (paramBleCharacteristic.getNotificationValue().equals(BleCharacteristic.Notification.notify))
              {
                localBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                Logs.log(Logs.LogTypes.debug, "BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE: " + localUUID);
              }
              else if (paramBleCharacteristic.getNotificationValue().equals(BleCharacteristic.Notification.indicate))
              {
                localBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                Logs.log(Logs.LogTypes.debug, "BluetoothGattDescriptor.ENABLE_INDICATION_VALUE: " + localUUID);
              }
            }
            else
            {
              localBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
              Logs.log(Logs.LogTypes.debug, "BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE: " + localUUID);
            }
            Logs.log(Logs.LogTypes.debug, "call gatt.writeDescriptor() now for gattDescriptor: " + localBluetoothGattDescriptor.getUuid() + ", gattCharacteristics: " + localUUID);
            bool1 = a.writeDescriptor(localBluetoothGattDescriptor);
            b.onNotificationEnd(a, localBluetoothGattCharacteristic, bool1);
          }
          else if (taskQueue.size() > 0)
          {
            operationInProgress = false;
            nextRequest();
          }
        }
      }
    }
    return bool1;
  }
  
  protected boolean internalReadBatteryLevel()
  {
    return false;
  }
  
  protected boolean internalReadCharacteristic(BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
  {
    if ((a == null) || (paramBluetoothGattCharacteristic == null)) {
      return false;
    }
    int i = paramBluetoothGattCharacteristic.getProperties();
    if ((i & 0x2) == 0) {
      return false;
    }
    return a.readCharacteristic(paramBluetoothGattCharacteristic);
  }
  
  protected boolean internalReadDescriptor(BluetoothGattDescriptor paramBluetoothGattDescriptor)
  {
    if ((a == null) || (paramBluetoothGattDescriptor == null)) {
      return false;
    }
    return a.readDescriptor(paramBluetoothGattDescriptor);
  }
  
  protected boolean internalSetBatteryNotifications(boolean paramBoolean)
  {
    return false;
  }
  
  protected boolean internalWriteCharacteristic(BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
  {
    if ((a == null) || (paramBluetoothGattCharacteristic == null)) {
      return false;
    }
    int i = paramBluetoothGattCharacteristic.getProperties();
    if ((i & 0xC) == 0) {
      return false;
    }
    return a.writeCharacteristic(paramBluetoothGattCharacteristic);
  }
  
  protected boolean internalWriteDescriptor(BluetoothGattDescriptor paramBluetoothGattDescriptor)
  {
    if ((a == null) || (paramBluetoothGattDescriptor == null)) {
      return false;
    }
    BluetoothGattCharacteristic localBluetoothGattCharacteristic = paramBluetoothGattDescriptor.getCharacteristic();
    int i = localBluetoothGattCharacteristic.getWriteType();
    localBluetoothGattCharacteristic.setWriteType(2);
    boolean bool = a.writeDescriptor(paramBluetoothGattDescriptor);
    localBluetoothGattCharacteristic.setWriteType(i);
    return bool;
  }
  
  public final boolean writeDescriptor(BluetoothGatt paramBluetoothGatt, BluetoothGattDescriptor paramBluetoothGattDescriptor)
  {
    a = paramBluetoothGatt;
    return a(BleRequest.newWriteRequest(paramBluetoothGattDescriptor, paramBluetoothGattDescriptor.getValue()));
  }
  
  protected boolean writeCharacteristic(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
  {
    a = paramBluetoothGatt;
    return a(BleRequest.newWriteRequest(paramBluetoothGattCharacteristic, paramBluetoothGattCharacteristic.getValue()));
  }
  
  public final boolean readDescriptor(BluetoothGatt paramBluetoothGatt, BluetoothGattDescriptor paramBluetoothGattDescriptor)
  {
    a = paramBluetoothGatt;
    return a(BleRequest.newReadRequest(paramBluetoothGattDescriptor));
  }
  
  public void nextRequest()
  {
    if (operationInProgress) {
      return;
    }
    BleRequest localBleRequest = null;
    if (localBleRequest == null)
    {
      synchronized (lock)
      {
        localBleRequest = (BleRequest)taskQueue.poll();
      }
      if (localBleRequest == null)
      {
        b.onQueueStateChanged(false);
        return;
      }
    }
    Logs.log(Logs.LogTypes.debug, "TaskSize: " + taskQueue.size() + " Next Task: " + a.toString());
    operationInProgress = true;
    boolean bool = false;
    Object localObject2;
    switch (1.a[a.ordinal()])
    {
    case 1: 
      bool = internalReadCharacteristic(b);
      break;
    case 2: 
      localObject2 = b;
      ((BluetoothGattCharacteristic)localObject2).setValue(e);
      ((BluetoothGattCharacteristic)localObject2).setWriteType(f);
      bool = internalWriteCharacteristic((BluetoothGattCharacteristic)localObject2);
      break;
    case 3: 
      bool = internalReadDescriptor(d);
      break;
    case 4: 
      localObject2 = d;
      ((BluetoothGattDescriptor)localObject2).setValue(e);
      bool = internalWriteDescriptor((BluetoothGattDescriptor)localObject2);
      break;
    case 5: 
      bool = internalEnableNotifications(c);
      break;
    case 6: 
      bool = internalReadBatteryLevel();
      break;
    case 7: 
      bool = internalSetBatteryNotifications(true);
      break;
    case 8: 
      bool = internalSetBatteryNotifications(false);
      break;
    case 9: 
      bool = ensureServiceChangedEnabled();
    }
    if (!bool)
    {
      operationInProgress = false;
      nextRequest();
    }
  }
  
  protected boolean ensureServiceChangedEnabled()
  {
    return false;
  }
  
  public static abstract interface QueueListener
  {
    public abstract void onNotificationEnd(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, boolean paramBoolean);
    
    public abstract void onCharacteristicEnabled(BleCharacteristic paramBleCharacteristic);
    
    public abstract void onQueueStateChanged(boolean paramBoolean);
  }
}
