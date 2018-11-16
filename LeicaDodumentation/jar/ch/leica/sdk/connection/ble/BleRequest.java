package ch.leica.sdk.connection.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

public class BleRequest
{
  final a a;
  final BluetoothGattCharacteristic b;
  final BleCharacteristic c;
  final BluetoothGattDescriptor d;
  final byte[] e;
  final int f;
  
  public BleRequest(a paramA)
  {
    a = paramA;
    b = null;
    d = null;
    e = null;
    f = 0;
    c = null;
  }
  
  public BleRequest(a paramA, BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
  {
    a = paramA;
    b = paramBluetoothGattCharacteristic;
    d = null;
    e = null;
    f = 0;
    c = null;
  }
  
  public BleRequest(a paramA, BleCharacteristic paramBleCharacteristic)
  {
    a = paramA;
    b = null;
    d = null;
    e = null;
    f = 0;
    c = paramBleCharacteristic;
  }
  
  public BleRequest(a paramA, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    a = paramA;
    b = paramBluetoothGattCharacteristic;
    d = null;
    e = a(paramArrayOfByte, paramInt2, paramInt3);
    f = paramInt1;
    c = null;
  }
  
  public BleRequest(a paramA, BluetoothGattDescriptor paramBluetoothGattDescriptor)
  {
    a = paramA;
    b = null;
    d = paramBluetoothGattDescriptor;
    e = null;
    f = 0;
    c = null;
  }
  
  public BleRequest(a paramA, BluetoothGattDescriptor paramBluetoothGattDescriptor, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    a = paramA;
    b = null;
    d = paramBluetoothGattDescriptor;
    e = a(paramArrayOfByte, paramInt1, paramInt2);
    f = 2;
    c = null;
  }
  
  private static byte[] a(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if ((paramArrayOfByte == null) || (paramInt1 > paramArrayOfByte.length)) {
      return null;
    }
    int i = Math.min(paramArrayOfByte.length - paramInt1, paramInt2);
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  public static BleRequest newReadRequest(BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
  {
    return new BleRequest(a.b, paramBluetoothGattCharacteristic);
  }
  
  public static BleRequest newWriteRequest(BluetoothGattCharacteristic paramBluetoothGattCharacteristic, byte[] paramArrayOfByte)
  {
    return new BleRequest(a.a, paramBluetoothGattCharacteristic, paramBluetoothGattCharacteristic.getWriteType(), paramArrayOfByte, 0, paramArrayOfByte != null ? paramArrayOfByte.length : 0);
  }
  
  public static BleRequest newWriteRequest(BluetoothGattCharacteristic paramBluetoothGattCharacteristic, byte[] paramArrayOfByte, int paramInt)
  {
    return new BleRequest(a.a, paramBluetoothGattCharacteristic, paramInt, paramArrayOfByte, 0, paramArrayOfByte != null ? paramArrayOfByte.length : 0);
  }
  
  public static BleRequest newWriteRequest(BluetoothGattCharacteristic paramBluetoothGattCharacteristic, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new BleRequest(a.a, paramBluetoothGattCharacteristic, paramBluetoothGattCharacteristic.getWriteType(), paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public static BleRequest newWriteRequest(BluetoothGattCharacteristic paramBluetoothGattCharacteristic, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    return new BleRequest(a.a, paramBluetoothGattCharacteristic, paramInt3, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public static BleRequest newReadRequest(BluetoothGattDescriptor paramBluetoothGattDescriptor)
  {
    return new BleRequest(a.d, paramBluetoothGattDescriptor);
  }
  
  public static BleRequest newWriteRequest(BluetoothGattDescriptor paramBluetoothGattDescriptor, byte[] paramArrayOfByte)
  {
    return new BleRequest(a.c, paramBluetoothGattDescriptor, paramArrayOfByte, 0, paramArrayOfByte != null ? paramArrayOfByte.length : 0);
  }
  
  public static BleRequest newWriteRequest(BluetoothGattDescriptor paramBluetoothGattDescriptor, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new BleRequest(a.c, paramBluetoothGattDescriptor, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public static BleRequest newEnableNotificationsRequest(BleCharacteristic paramBleCharacteristic)
  {
    return new BleRequest(a.e, paramBleCharacteristic);
  }
  
  public static BleRequest newEnableIndicationsRequest(BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
  {
    return new BleRequest(a.f, paramBluetoothGattCharacteristic);
  }
  
  public static BleRequest newReadBatteryLevelRequest()
  {
    return new BleRequest(a.g);
  }
  
  public static BleRequest newEnableBatteryLevelNotificationsRequest()
  {
    return new BleRequest(a.h);
  }
  
  public static BleRequest newDisableBatteryLevelNotificationsRequest()
  {
    return new BleRequest(a.i);
  }
  
  static enum a
  {
    private a() {}
  }
}
