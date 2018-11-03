package ch.leica.sdk.commands;

import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ReceivedBleDataPacket
  extends ReceivedDataPacket
{
  private float b = -9999.0F;
  private short c = 55537;
  private float d = -9999.0F;
  private short e = 55537;
  private float f = -9999.0F;
  private short g = 55537;
  private float h = -9999.0F;
  private float i = -9999.0F;
  private short j = 55537;
  private String k = "";
  private String l = "";
  private String m = "";
  private String n = "";
  private String o = "";
  private String p = "";
  private String q = "";
  private String r = "";
  byte[] a;
  
  ReceivedBleDataPacket(String paramString, byte[] paramArrayOfByte)
    throws IllegalArgumentCheckedException
  {
    super(paramString);
    a = paramArrayOfByte;
    a();
  }
  
  public float getDistance()
  {
    return b;
  }
  
  public float getInclination()
  {
    return d;
  }
  
  public float getDirection()
  {
    return f;
  }
  
  public float getHorizontalIncline()
  {
    return h;
  }
  
  public float getVerticalIncline()
  {
    return i;
  }
  
  public short getDistanceUnit()
  {
    return c;
  }
  
  public short getInclinationUnit()
  {
    return e;
  }
  
  public short getDirectionUnit()
  {
    return g;
  }
  
  public short getResponse()
  {
    return j;
  }
  
  public String getModelName()
  {
    return k;
  }
  
  public String getFirmwareRevision()
  {
    return l;
  }
  
  public String getHardwareRevision()
  {
    return m;
  }
  
  public String getSerialNumber()
  {
    return n;
  }
  
  public String getManufacturerNameString()
  {
    return o;
  }
  
  public String getBatteryLevel()
  {
    return p;
  }
  
  public String getBatteryPowerState()
  {
    return q;
  }
  
  public String getTemperatureMeasurement()
  {
    return r;
  }
  
  private void a()
    throws IllegalArgumentCheckedException
  {
    if (a != null) {
      switch (dataId)
      {
      case "DS_DISTANCE": 
        b = b();
        break;
      case "DS_INCLINATION": 
        d = b();
        break;
      case "DS_DIRECTION": 
        f = b();
        break;
      case "DS_HORIZONTAL_INCLINE": 
        h = b();
        break;
      case "DS_VERTICAL_INCLINE": 
        i = b();
        break;
      case "DS_DISTANCE_UNIT": 
        c = c();
        break;
      case "DS_INCLINATION_UNIT": 
        e = c();
        break;
      case "DS_DIRECTION_UNIT": 
        g = c();
        break;
      case "DS_RESPONSE": 
        j = c();
        break;
      case "DS_MODEL_NAME": 
        k = d();
        break;
      case "DI_MODEL_NUMBER": 
        k = d();
        break;
      case "DI_FIRMWARE_REVISION": 
        l = d();
        break;
      case "DI_HARDWARE_REVISION": 
        m = d();
        break;
      case "DI_SERIAL_NUMBER": 
        n = d();
        break;
      case "DI_MANUFACTURER_NAME_STRING": 
        o = d();
        break;
      case "BT_BATTERY_LEVEL": 
        p = d();
        break;
      case "BT_BATTERY_POWER_STATE": 
        q = d();
        break;
      case "TH_TEMPERATURE_MEASUREMENT": 
        r = d();
        break;
      case "DI_PNP_ID": 
        Logs.log(Logs.LogTypes.debug, a.toString());
        break;
      default: 
        Logs.log(Logs.LogTypes.codeerror, "An error occurred parsing BLE Data");
      }
    } else {
      throw new IllegalArgumentCheckedException(" Error while receiving BLE Packet. Null Array of received Values. ");
    }
  }
  
  private float b()
  {
    float f1 = ByteBuffer.wrap(a).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    Logs.log(Logs.LogTypes.debug, "Float: " + f1);
    return f1;
  }
  
  private short c()
  {
    short s = ByteBuffer.wrap(a).order(ByteOrder.LITTLE_ENDIAN).getShort();
    Logs.log(Logs.LogTypes.debug, "Short: " + s);
    return s;
  }
  
  private String d()
  {
    String str = new String(a).replace("\000", "").trim();
    Logs.log(Logs.LogTypes.debug, "String: " + str);
    return str;
  }
}
