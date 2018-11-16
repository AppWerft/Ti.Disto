package ch.leica.sdk.connection.ble;

import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.UUID;

public class BleCharacteristic
{
  private UUID a;
  private UUID b;
  private String c;
  private String d;
  private Notification e;
  private boolean f;
  
  public boolean isEnable()
  {
    return f;
  }
  
  public void setEnable(boolean paramBoolean)
  {
    f = paramBoolean;
  }
  
  public BleCharacteristic(UUID paramUUID1, UUID paramUUID2, String paramString)
  {
    a = paramUUID1;
    b = paramUUID2;
    d = paramString;
    f = true;
    a();
    Logs.log(Logs.LogTypes.verbose, c);
  }
  
  public BleCharacteristic(UUID paramUUID1, UUID paramUUID2, boolean paramBoolean, int paramInt)
  {
    a = paramUUID1;
    b = paramUUID2;
    f = paramBoolean;
    setNotificationValue(paramInt);
    a();
    Logs.log(Logs.LogTypes.verbose, c);
  }
  
  public BleCharacteristic(UUID paramUUID1, UUID paramUUID2, String paramString, boolean paramBoolean)
  {
    a = paramUUID1;
    b = paramUUID2;
    d = paramString;
    f = paramBoolean;
    a();
    Logs.log(Logs.LogTypes.verbose, c);
  }
  
  private void a()
  {
    c = ("Service: " + a.toString() + " Characteristic: " + b.toString());
  }
  
  public UUID getServiceUUID()
  {
    return a;
  }
  
  public UUID getCharacteristicUUID()
  {
    return b;
  }
  
  public String getStrValue()
  {
    return c;
  }
  
  public String getId()
  {
    return d;
  }
  
  public void setId(String paramString)
  {
    d = paramString;
  }
  
  public Notification getNotificationValue()
  {
    return e;
  }
  
  public void setNotificationValue(int paramInt)
  {
    if ((paramInt & 0x10) != 0) {
      e = Notification.notify;
    } else if ((paramInt & 0x20) != 0) {
      e = Notification.indicate;
    } else {
      e = Notification.none;
    }
    Logs.log(Logs.LogTypes.debug, "Characteristic: " + b + " Notification Value: " + e);
  }
  
  public static enum Notification
  {
    private Notification() {}
  }
}
