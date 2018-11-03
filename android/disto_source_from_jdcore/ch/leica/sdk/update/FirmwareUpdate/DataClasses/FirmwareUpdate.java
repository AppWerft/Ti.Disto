package ch.leica.sdk.update.FirmwareUpdate.DataClasses;

import ch.leica.sdk.ErrorHandling.ErrorObject;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class FirmwareUpdate
{
  public String forCurrentVersion;
  public String version;
  public String brandIdentifier;
  public String productIdentifier;
  public String name;
  public String changelog;
  public List<FirmwareBinary> binaries = new ArrayList();
  public List<FirmwareComponent> components = new ArrayList();
  public List<ErrorObject> errors = new ArrayList();
  public JSONObject productInfoJSon;
  public boolean isValid = false;
  
  public FirmwareUpdate()
  {
    isValid = false;
  }
  
  public FirmwareUpdate(String paramString1, String paramString2, List<FirmwareBinary> paramList)
  {
    isValid = true;
    brandIdentifier = paramString1;
    productIdentifier = paramString2;
    if (paramList == null) {
      binaries = new ArrayList();
    } else {
      binaries = paramList;
    }
  }
  
  public FirmwareUpdate(String paramString1, String paramString2, String paramString3, String paramString4, List<FirmwareBinary> paramList)
  {
    isValid = true;
    version = paramString1;
    brandIdentifier = paramString2;
    name = paramString3;
    productIdentifier = paramString4;
    if (paramList == null) {
      binaries = new ArrayList();
    } else {
      binaries = paramList;
    }
  }
  
  public String getVersion()
  {
    return version;
  }
  
  public String getBrandIdentifier()
  {
    return brandIdentifier;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setChangelog(String paramString)
  {
    changelog = paramString;
  }
  
  public String getChangelog()
  {
    return changelog;
  }
  
  public List<FirmwareBinary> getBinaries()
  {
    return binaries;
  }
  
  public List<FirmwareComponent> getComponents()
  {
    return components;
  }
  
  public boolean isValid()
  {
    return isValid;
  }
}
