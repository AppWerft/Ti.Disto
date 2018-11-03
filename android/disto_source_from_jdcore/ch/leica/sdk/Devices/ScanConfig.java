package ch.leica.sdk.Devices;

public class ScanConfig
{
  private boolean a = false;
  private boolean b = false;
  private boolean c = false;
  private boolean d = false;
  private boolean e = false;
  private boolean f = false;
  
  public ScanConfig() {}
  
  public void setConnection(boolean paramBoolean1, boolean paramBoolean2)
  {
    setWifi(paramBoolean1);
    setBle(paramBoolean2);
  }
  
  public void setDevices(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    c = paramBoolean1;
    d = paramBoolean2;
    e = paramBoolean3;
    f = paramBoolean4;
  }
  
  public void setWifi(boolean paramBoolean)
  {
    isWifiAdapterOn();
  }
  
  public void setBle(boolean paramBoolean)
  {
    isBleAdapterOn();
  }
  
  public boolean isDistoWifi()
  {
    boolean bool = false;
    if ((c == true) && (a == true)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isDistoBle()
  {
    boolean bool = false;
    if ((d == true) && (b == true)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isYeti()
  {
    return e;
  }
  
  public boolean isDisto3DD()
  {
    return f;
  }
  
  public boolean isWifiAdapterOn()
  {
    return a;
  }
  
  public void setWifiAdapterOn(boolean paramBoolean)
  {
    a = paramBoolean;
  }
  
  public boolean isBleAdapterOn()
  {
    return b;
  }
  
  public void setBleAdapterOn(boolean paramBoolean)
  {
    b = paramBoolean;
  }
}
