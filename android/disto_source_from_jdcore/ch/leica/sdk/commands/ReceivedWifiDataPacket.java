package ch.leica.sdk.commands;

import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.StringTokenizer;

public class ReceivedWifiDataPacket
  extends ReceivedDataPacket
{
  private final String a = ":";
  private final String b = "Ev";
  private final String c = "@E";
  private String d = "";
  private String e = "";
  private String f = "";
  private String g = "";
  private String h = "";
  private String i = "";
  private int j = 55537;
  private String k = "";
  private float l = -9999.0F;
  private int m = 55537;
  private float n = -9999.0F;
  private float o = -9999.0F;
  private float p = -9999.0F;
  private float q = -9999.0F;
  private float r = -9999.0F;
  private float s = -9999.0F;
  private float t = -9999.0F;
  private float u = -9999.0F;
  private float v = -9999.0F;
  private float w = -9999.0F;
  private float x = -9999.0F;
  private float y = -9999.0F;
  private float z = -9999.0F;
  private float A = -9999.0F;
  private float B = -9999.0F;
  private float C = -9999.0F;
  private int D = 55537;
  private int E = 55537;
  private int F = 55537;
  private int G = 55537;
  private int H = 55537;
  private int I = 55537;
  private int J = 55537;
  private int K = 55537;
  private int L = 55537;
  private int M = 55537;
  private short N = 55537;
  private Image O = null;
  private h P;
  private b Q;
  private d R;
  private c S;
  private g T;
  private a U;
  private e V;
  private f W;
  private final String X = "EvLev";
  private final String Y = "EvPos";
  private final String Z = "EvKey";
  private final String aa = "EvBat";
  private final String ab = "EvLine";
  private final String ac = "EvMsg";
  private final String ad = "EvCal";
  private final String ae = "EvMp";
  private final String af = "EvMpi";
  
  ReceivedWifiDataPacket()
    throws IllegalArgumentCheckedException
  {}
  
  ReceivedWifiDataPacket(String paramString1, String paramString2)
    throws IllegalArgumentCheckedException, DeviceException
  {
    dataId = "ID_WIFIDATAPACKET";
    response = paramString2;
    if (!paramString1.startsWith("Ev")) {
      a();
    } else {
      a(paramString1, paramString2);
    }
  }
  
  private void a(String paramString1, String paramString2)
    throws DeviceException
  {
    paramString2 = paramString2.trim();
    String str2;
    switch (paramString1)
    {
    case "EvLev": 
      dataId = "EVLEV";
      short s1 = Short.valueOf(paramString2).shortValue();
      S = new c(Short.valueOf(s1));
      Logs.log(Logs.LogTypes.debug, paramString1 + ": value:" + S.a());
      break;
    case "EvPos": 
      dataId = "EVPOS";
      str2 = paramString2.substring(0, 1);
      String str3 = paramString2.substring(2, paramString2.length());
      getClass();
      if (str3.startsWith("@E")) {
        throw new DeviceException(paramString2.substring(1));
      }
      P = new h(str2, str3);
      Logs.log(Logs.LogTypes.debug, paramString1 + ": Motor: " + P.a() + " Result: " + P.b());
      break;
    case "EvKey": 
      dataId = "EVKEY";
      str2 = paramString2.substring(paramString2.length() - 1, paramString2.length()).trim();
      int i2 = Integer.valueOf(paramString2.substring(0, paramString2.length() - 1).trim()).intValue();
      Q = new b(i2, str2);
      Logs.log(Logs.LogTypes.debug, paramString1 + ": EvKey: Key:" + Q.a() + " event: " + Q.b());
      break;
    case "EvBat": 
      dataId = "EVBAT";
      if (R == null) {
        R = new d();
      }
      str2 = paramString2;
      R.a = str2;
      Logs.log(Logs.LogTypes.debug, paramString1 + ":  value: " + R.a);
    case "EvLine": 
      dataId = "EVLINE";
      if (R == null) {
        R = new d();
      }
      str2 = paramString2;
      R.b = str2;
      Logs.log(Logs.LogTypes.debug, paramString1 + ":  value: " + R.b);
      break;
    case "EvMsg": 
      dataId = "EVMSG";
      str2 = paramString2.substring(0, 1);
      paramString2 = paramString2.substring(1, paramString2.length()).trim();
      String str4 = paramString2.substring(0, 1);
      paramString2 = paramString2.substring(1, paramString2.length()).trim();
      int i3 = Integer.valueOf(paramString2).intValue();
      T = new g(str2, str4, i3);
      Logs.log(Logs.LogTypes.debug, paramString1 + "Action: " + T.a() + " Message: " + T.b() + " value:" + T.c());
      break;
    case "EvCal": 
      dataId = "EVCAL";
      str2 = paramString2;
      getClass();
      if (str2.startsWith("@E")) {
        throw new DeviceException(paramString2.substring(1));
      }
      U = new a(str2);
      Logs.log(Logs.LogTypes.debug, paramString1 + ": Result: " + U.a());
      break;
    case "EvMp": 
      dataId = "EVMP";
      a();
      V = new e(getDistance(), getHorizontalAnglewithTilt_hz(), getVerticalAngleWithTilt_v(), getHorizontalAngleWithoutTilt_ni_hz(), getVerticalAngleWithoutTilt_ni_v(), getLevel_iState());
      Logs.log(Logs.LogTypes.debug, paramString1 + ":   Dist:" + V.a() + "  Hz:" + V.b() + "  V:" + V.c() + "  ni_hz:" + V.d() + "  ni_V:" + V.e() + "  iState:" + V.f());
      break;
    case "EvMpi": 
      dataId = "EVMPI";
      a();
      W = new f(getDistance(), getHorizontalAnglewithTilt_hz(), getVerticalAngleWithTilt_v(), getHorizontalAngleWithoutTilt_ni_hz(), getVerticalAngleWithoutTilt_ni_v(), getLevel_iState(), getImage());
      Logs.log(Logs.LogTypes.debug, paramString1 + ":   Dist:" + W.a() + "  Hz:" + W.b() + "  V:" + W.c() + "  ni_hz:" + W.d() + "  ni_V:" + W.e() + "  iState:" + W.f());
    }
  }
  
  private void a()
    throws DeviceException
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(response);
    while (localStringTokenizer.hasMoreTokens())
    {
      String str1 = localStringTokenizer.nextToken();
      int i1 = str1.indexOf(":");
      if (i1 > 0)
      {
        int i2 = Integer.valueOf(str1.substring(0, i1)).intValue();
        String str2 = str1.substring(i1 + 1);
        try
        {
          if (str2.startsWith("#"))
          {
            str2 = str2.substring(1);
          }
          else
          {
            getClass();
            if (str2.startsWith("@E")) {
              throw new DeviceException(str2.substring(1));
            }
          }
          switch (i2)
          {
          case 1: 
            d = str2;
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 8: 
            l = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 12: 
            e = str2;
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 13: 
            D = Integer.valueOf(str2).intValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 16: 
            f = str2;
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 17: 
            g = str2;
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 19: 
            m = Integer.valueOf(str2).intValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 31: 
            r = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 21: 
            n = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 22: 
            o = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 23: 
            p = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 24: 
            q = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 40: 
            E = Integer.valueOf(str2).intValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 41: 
            F = Integer.valueOf(str2).intValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 51: 
            s = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 52: 
            t = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 53: 
            u = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 54: 
            v = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 55: 
            w = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 56: 
            G = Integer.valueOf(str2).intValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 60: 
            H = Integer.valueOf(str2).intValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 61: 
            I = Integer.valueOf(str2).intValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 70: 
            x = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 71: 
            y = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 72: 
            z = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 75: 
            N = Short.valueOf(str2).shortValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 99: 
            h = str2;
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 100: 
            i = str2;
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 102: 
            J = Integer.valueOf(str2).intValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 103: 
            j = Integer.valueOf(str2).intValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 105: 
            k = str2;
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 200: 
          case 201: 
          case 202: 
          case 203: 
          case 204: 
          case 205: 
            a(response);
            Logs.log(Logs.LogTypes.debug, "ID: " + i2);
            break;
          case 1033: 
            A = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 2100: 
            K = Integer.valueOf(str2).intValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 3012: 
            B = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 3013: 
            C = Float.valueOf(str2).floatValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 521: 
            L = Integer.valueOf(str2).intValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          case 522: 
            M = Integer.valueOf(str2).intValue();
            Logs.log(Logs.LogTypes.debug, "id: " + i2 + " data: " + str2);
            break;
          default: 
            Logs.log(Logs.LogTypes.debug, "ReceivedData not yet implemented, id: " + i2 + " value:" + str2);
          }
        }
        catch (DeviceException localDeviceException)
        {
          Logs.log(Logs.LogTypes.exception, "Error receiving data. ", localDeviceException);
          throw new DeviceException(str2.substring(1));
        }
        catch (Exception localException)
        {
          Logs.log(Logs.LogTypes.exception, "Error receiving data. ", localException);
        }
      }
    }
  }
  
  private void a(String paramString)
  {
    if (paramString != null)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
      String str1 = "";
      Image localImage = new Image();
      while (localStringTokenizer.hasMoreTokens())
      {
        String str2 = localStringTokenizer.nextToken();
        int i1 = str2.indexOf(":");
        if (i1 > 0) {
          try
          {
            int i2 = Integer.valueOf(str2.substring(0, i1)).intValue();
            String str3 = str2.substring(i1 + 1);
            switch (i2)
            {
            case 210: 
              Logs.log(Logs.LogTypes.debug, "x-coordinate: " + str3);
              localImage.setxCoordinateCrosshair(Short.valueOf(str3).shortValue());
              break;
            case 211: 
              Logs.log(Logs.LogTypes.debug, "y-coordinate: " + str3);
              localImage.setyCoordinateCrosshair(Short.valueOf(str3).shortValue());
              break;
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: 
            case 205: 
              Logs.log(Logs.LogTypes.debug, "image string");
              str1 = str2.substring(0, i1);
              localImage.setImageBytes(str3);
              break;
            case 206: 
            case 207: 
            case 208: 
            case 209: 
            default: 
              Logs.log(Logs.LogTypes.debug, "Data not regarding to image, id: " + i2 + " value:" + str3);
            }
          }
          catch (Exception localException)
          {
            Logs.log(Logs.LogTypes.exception, "Error receiving data. ", localException);
          }
        }
      }
      if (!str1.equals("")) {
        O = localImage;
      }
    }
    else
    {
      Logs.log(Logs.LogTypes.exception, "Error setting image data. parameter Response is null ");
    }
  }
  
  public void setLiveImagePacket(byte[] paramArrayOfByte)
  {
    dataId = "LIVEIMAGE";
    try
    {
      if (paramArrayOfByte != null)
      {
        LiveImage localLiveImage = new LiveImage();
        localLiveImage.setLiveImageData(paramArrayOfByte);
        O = localLiveImage;
      }
      else
      {
        Logs.log(Logs.LogTypes.codeerror, "Data Received NULL");
        O = new Image();
      }
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.exception, "Error retrieving information of the liveImagePacket", localException);
    }
  }
  
  public String getIpAddress()
  {
    return d;
  }
  
  public String getSerialNumber()
  {
    return e;
  }
  
  public String getSoftwareName()
  {
    return f;
  }
  
  public String getSoftwareVersion()
  {
    return g;
  }
  
  public String getMac()
  {
    return h;
  }
  
  public String getWlanVersions()
  {
    return i;
  }
  
  public int getWlanFreq()
  {
    return j;
  }
  
  public String getWlanESSID()
  {
    return k;
  }
  
  public float gethTime()
  {
    return l;
  }
  
  public int getEquipment()
  {
    return m;
  }
  
  public float getHorizontalAnglewithTilt_hz()
  {
    return n;
  }
  
  public float getVerticalAngleWithTilt_v()
  {
    return o;
  }
  
  public float getHorizontalAngleWithoutTilt_ni_hz()
  {
    return p;
  }
  
  public float getVerticalAngleWithoutTilt_ni_v()
  {
    return q;
  }
  
  public float getDistance()
  {
    return r;
  }
  
  public float getHz_temp()
  {
    return s;
  }
  
  public float getV_temp()
  {
    return t;
  }
  
  public float getEdm_temp()
  {
    return u;
  }
  
  public float getBle_temp()
  {
    return v;
  }
  
  public float getBat_v()
  {
    return w;
  }
  
  public float getiHz()
  {
    return x;
  }
  
  public float getiLen()
  {
    return y;
  }
  
  public float getiCross()
  {
    return z;
  }
  
  public float getUsr_vind()
  {
    return A;
  }
  
  public float getUser_camlasX()
  {
    return B;
  }
  
  public float getUser_camlasY()
  {
    return C;
  }
  
  public int getDeviceType()
  {
    return D;
  }
  
  public int getFace()
  {
    return E;
  }
  
  public int getMotWhile()
  {
    return F;
  }
  
  public int getBat_s()
  {
    return G;
  }
  
  public int getLedSE()
  {
    return H;
  }
  
  public int getLedW()
  {
    return I;
  }
  
  public int getWlanCH()
  {
    return J;
  }
  
  public int getiSensitiveMode()
  {
    return K;
  }
  
  public short getLevel_iState()
  {
    return N;
  }
  
  public short getEvLevel()
  {
    return S.a();
  }
  
  public String getEvPosMotor()
  {
    return P.a();
  }
  
  public String getEvPosMotorResult()
  {
    return P.b();
  }
  
  public int getMotorStatusH()
  {
    return L;
  }
  
  public int getMotorStatusV()
  {
    return M;
  }
  
  public int getEvKeyKey()
  {
    return Q.a();
  }
  
  public String getEvKeyEvent()
  {
    return Q.b();
  }
  
  public String getEvLineBattery()
  {
    return R.a();
  }
  
  public String getEvLineLine()
  {
    return R.b();
  }
  
  public String getEvMsgAction()
  {
    return T.a();
  }
  
  public String getEvMsgMessage()
  {
    return T.b();
  }
  
  public int getEvMsgBit()
  {
    return T.c();
  }
  
  public String getEvCalResult()
  {
    return U.a();
  }
  
  public Image getImage()
  {
    return O;
  }
  
  private class f
    extends ReceivedWifiDataPacket.e
  {
    private Image c;
    
    public f(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, short paramShort, Image paramImage)
    {
      super(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramShort);
      c = paramImage;
    }
  }
  
  private class e
  {
    private float b;
    private float c;
    private float d;
    private float e;
    private float f;
    private float g;
    
    public e(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, short paramShort)
    {
      b = paramFloat1;
      c = paramFloat2;
      d = paramFloat3;
      e = paramFloat4;
      f = paramFloat5;
      g = paramShort;
    }
    
    public float a()
    {
      return b;
    }
    
    public float b()
    {
      return c;
    }
    
    public float c()
    {
      return d;
    }
    
    public float d()
    {
      return e;
    }
    
    public float e()
    {
      return f;
    }
    
    public float f()
    {
      return g;
    }
  }
  
  private class a
  {
    private String b;
    
    public a(String paramString)
    {
      b = paramString;
    }
    
    public String a()
    {
      return b;
    }
  }
  
  private class g
  {
    private String b;
    private String c;
    private int d;
    
    public g(String paramString1, String paramString2, int paramInt)
    {
      b = paramString1;
      c = paramString2;
      d = paramInt;
    }
    
    public String a()
    {
      return b;
    }
    
    public String b()
    {
      return c;
    }
    
    public int c()
    {
      return d;
    }
  }
  
  private class c
  {
    private short b = 55537;
    
    public c(Short paramShort)
    {
      b = paramShort.shortValue();
    }
    
    public short a()
    {
      return b;
    }
  }
  
  private class d
  {
    public String a = "";
    public String b = "";
    
    public d() {}
    
    public String a()
    {
      return a;
    }
    
    public String b()
    {
      return b;
    }
  }
  
  private class b
  {
    private int b = 55537;
    private String c = "";
    
    public b(int paramInt, String paramString)
    {
      b = paramInt;
      c = paramString;
    }
    
    public int a()
    {
      return b;
    }
    
    public String b()
    {
      return c;
    }
  }
  
  private class h
  {
    private String b = "";
    private String c = "";
    
    public h(String paramString1, String paramString2)
    {
      b = paramString1;
      c = paramString2;
    }
    
    public String a()
    {
      return b;
    }
    
    public String b()
    {
      return c;
    }
  }
}
