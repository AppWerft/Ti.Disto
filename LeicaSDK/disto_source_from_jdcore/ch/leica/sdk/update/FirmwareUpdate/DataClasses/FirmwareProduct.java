package ch.leica.sdk.update.FirmwareUpdate.DataClasses;

import android.support.annotation.VisibleForTesting;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.UpdateException;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import ch.leica.sdk.update.FirmwareUpdate.SerialRange;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FirmwareProduct
{
  private String a;
  private String b = "name";
  private List<FirmwareProductVersion> c = new ArrayList();
  private String d = "versions";
  private List<FirmwareComponent> e = new ArrayList();
  private String f = "components";
  private String g;
  private String h = "version_command";
  private String i;
  private String j = "serial_command";
  private JSONObject k;
  private String l = "serial";
  private String m;
  private String n = "from";
  private String o;
  private String p = "to";
  private String q;
  private String r = "identifier";
  private JSONObject s;
  private String t = "brand";
  private String u;
  private String v = "name";
  private String w;
  private String x = "identifier";
  private JSONObject y;
  private ErrorObject z;
  private SerialRange A;
  
  public FirmwareProduct(JSONObject paramJSONObject)
    throws UpdateException
  {
    y = paramJSONObject;
    boolean bool = a();
    if (!bool) {
      throw new UpdateException(z.getErrorMessage());
    }
  }
  
  private boolean a(String paramString)
  {
    return y.has(paramString);
  }
  
  private boolean a()
  {
    boolean bool = true;
    try
    {
      if (a(b) == true) {
        a = y.getString(b);
      } else {
        bool = false;
      }
      int i1;
      if (a(d) == true)
      {
        JSONArray localJSONArray1 = y.getJSONArray(d);
        for (i1 = 0; i1 < localJSONArray1.length(); i1++) {
          c.add(new FirmwareProductVersion(localJSONArray1.getJSONObject(i1)));
        }
      }
      else
      {
        bool = false;
      }
      if (a(f) == true)
      {
        JSONArray localJSONArray2 = y.getJSONArray(f);
        for (i1 = 0; i1 < localJSONArray2.length(); i1++) {
          e.add(new FirmwareComponent(localJSONArray2.getJSONObject(i1)));
        }
      }
      else
      {
        bool = false;
      }
      if (a(h) == true) {
        g = y.getString(h);
      } else {
        bool = false;
      }
      if (a(j) == true) {
        i = y.getString(j);
      } else {
        bool = false;
      }
      if (a(l) == true)
      {
        k = y.getJSONObject(l);
        if (k.has(n))
        {
          m = k.getString(n);
          if (!b(m))
          {
            m = "";
            bool = false;
          }
        }
        else
        {
          bool = false;
        }
        if (k.has(p))
        {
          o = k.getString(p);
          if (!b(o))
          {
            o = "";
            bool = false;
          }
        }
        else
        {
          bool = false;
        }
        if (bool == true) {
          A = new SerialRange(m, o);
        }
      }
      else
      {
        bool = false;
      }
      if (a(r) == true) {
        q = y.getString(r);
      } else {
        bool = false;
      }
      if (a(t) == true)
      {
        s = y.getJSONObject(t);
        u = s.getString(v);
        w = s.getString(x);
      }
      else
      {
        bool = false;
      }
    }
    catch (JSONException|UpdateException localJSONException)
    {
      Logs.log(Logs.LogTypes.exception, "JSON Error", localJSONException);
      bool = false;
      z = new ErrorObject(7012, localJSONException.getMessage());
    }
    return bool;
  }
  
  public FirmwareProductVersion getNextProductVersion(String paramString)
  {
    FirmwareProductVersion localFirmwareProductVersion1 = null;
    FirmwareProductVersion localFirmwareProductVersion2 = getCurrentProductVersion(paramString);
    if (localFirmwareProductVersion2 != null)
    {
      String str = localFirmwareProductVersion2.getNext();
      if (!str.equals("null")) {
        localFirmwareProductVersion1 = getCurrentProductVersion(str);
      } else {
        z = new ErrorObject(7500, "The device already has the latest version - no need to update.");
      }
    }
    return localFirmwareProductVersion1;
  }
  
  public FirmwareProductVersion getCurrentProductVersion(String paramString)
  {
    if (paramString == null)
    {
      z = new ErrorObject(7628, "The received appCurrentVersion is null");
      return null;
    }
    Object localObject = null;
    if (c == null) {
      return null;
    }
    Iterator localIterator = c.iterator();
    while (localIterator.hasNext())
    {
      FirmwareProductVersion localFirmwareProductVersion = (FirmwareProductVersion)localIterator.next();
      if (localFirmwareProductVersion.getIdentifier().equals(paramString) == true)
      {
        localObject = localFirmwareProductVersion;
        break;
      }
    }
    if (localObject == null) {
      z = new ErrorObject(7510, paramString + " : " + "Version has not been found in JSON.");
    }
    return localObject;
  }
  
  public FirmwareComponent getFittingComponentNoSerial(FirmwareComponent paramFirmwareComponent)
  {
    Object localObject = null;
    Iterator localIterator = getComponents().iterator();
    while (localIterator.hasNext())
    {
      FirmwareComponent localFirmwareComponent = (FirmwareComponent)localIterator.next();
      if (localFirmwareComponent.getIdentifier().equals(paramFirmwareComponent.getIdentifier())) {
        localObject = localFirmwareComponent;
      }
    }
    return localObject;
  }
  
  public FirmwareComponent getFittingComponentWithSerial(FirmwareComponent paramFirmwareComponent, String paramString)
  {
    Object localObject = null;
    Iterator localIterator = getComponents().iterator();
    while (localIterator.hasNext())
    {
      FirmwareComponent localFirmwareComponent = (FirmwareComponent)localIterator.next();
      if (localFirmwareComponent.getIdentifier().equals(paramFirmwareComponent.getIdentifier()))
      {
        SerialRange localSerialRange = localFirmwareComponent.getSerialRange();
        if (localSerialRange != null) {
          if (localSerialRange.isValid(paramString) == true)
          {
            localObject = localFirmwareComponent;
            Logs.log(Logs.LogTypes.debug, " --- component found");
            break;
          }
        }
      }
    }
    return localObject;
  }
  
  private boolean b(String paramString)
  {
    return paramString.matches("-?\\d+(\\.\\d+)?");
  }
  
  public String getIdentifier()
  {
    return q;
  }
  
  public String getVersionCommand()
  {
    return g;
  }
  
  public String getSerialCommand()
  {
    return i;
  }
  
  @VisibleForTesting
  public String getSerialFrom()
  {
    return m;
  }
  
  @VisibleForTesting
  public String getSerialTo()
  {
    return o;
  }
  
  public String getBrandName()
  {
    return u;
  }
  
  public String getBrandIdentifier()
  {
    return w;
  }
  
  public SerialRange getSerialRange()
  {
    return A;
  }
  
  @VisibleForTesting
  public List<FirmwareProductVersion> getProductVersions()
  {
    return c;
  }
  
  public List<FirmwareComponent> getComponents()
  {
    return e;
  }
  
  public String getName()
  {
    return a;
  }
  
  public ErrorObject getErrorObject()
  {
    return z;
  }
  
  public JSONObject getProductJSON()
  {
    return y;
  }
}
