package ch.leica.sdk.commands.response;

import ch.leica.sdk.Types.Commands;

public final class ResponseDeviceInfo
  extends Response
{
  private String e = "";
  private String f = "";
  private int g = 55537;
  private String h = "";
  private String i = "";
  private String j = "";
  private String k = "";
  private int l = 55537;
  private int m = 55537;
  private float n = -9999.0F;
  private float o = -9999.0F;
  private float p = -9999.0F;
  private String q = "";
  private String r = "";
  private float s = -9999.0F;
  
  public ResponseDeviceInfo(Types.Commands paramCommands)
  {
    super(paramCommands);
  }
  
  public String getIP()
  {
    return e;
  }
  
  public void setIP(String paramString)
  {
    e = paramString;
  }
  
  public String getSerialNumber()
  {
    return f;
  }
  
  public void setSerialNumber(String paramString)
  {
    f = paramString;
  }
  
  public int getDeviceType()
  {
    return g;
  }
  
  public void setDeviceType(int paramInt)
  {
    g = paramInt;
  }
  
  public String getSoftwareName()
  {
    return h;
  }
  
  public void setSoftwareName(String paramString)
  {
    h = paramString;
  }
  
  public String getSoftwareVersion()
  {
    return i;
  }
  
  public void setSoftwareVersion(String paramString)
  {
    i = paramString;
  }
  
  public String getMacAddress()
  {
    return j;
  }
  
  public void setMacAddress(String paramString)
  {
    j = paramString;
  }
  
  public String getWifiModuleVersion()
  {
    return k;
  }
  
  public void setWifiModuleVersion(String paramString)
  {
    k = paramString;
  }
  
  public int getWifiChannelNumber()
  {
    return l;
  }
  
  public void setWifiChannelNumber(int paramInt)
  {
    l = paramInt;
  }
  
  public float getUserVind()
  {
    return n;
  }
  
  public void setUserVind(float paramFloat)
  {
    n = paramFloat;
  }
  
  public float getUserCamLasX()
  {
    return o;
  }
  
  public void setUserCamLasX(float paramFloat)
  {
    o = paramFloat;
  }
  
  public float getUserCamLasY()
  {
    return p;
  }
  
  public void setUserCamLasY(float paramFloat)
  {
    p = paramFloat;
  }
  
  public String getEquipmentNumber()
  {
    return r;
  }
  
  public void setEquipmentNumber(String paramString)
  {
    r = paramString;
  }
  
  public int getWifiFrequency()
  {
    return m;
  }
  
  public void setWifiFrequency(int paramInt)
  {
    m = paramInt;
  }
  
  public String getWifiESSID()
  {
    return q;
  }
  
  public void setWifiESSID(String paramString)
  {
    q = paramString;
  }
  
  public float getSensitiveMode()
  {
    return s;
  }
  
  public void setSensitiveMode(float paramFloat)
  {
    s = paramFloat;
  }
}
