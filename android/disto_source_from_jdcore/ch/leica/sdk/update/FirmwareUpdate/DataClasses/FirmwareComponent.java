package ch.leica.sdk.update.FirmwareUpdate.DataClasses;

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

public class FirmwareComponent
{
  public String name;
  private final String b = "name";
  public List<FirmwareComponentVersion> componentVersions = new ArrayList();
  private final String c = "versions";
  public String versionCommand;
  private final String d = "version_command";
  public String serialCommand;
  private final String e = "serial_command";
  JSONObject a;
  private final String f = "serial";
  public String serialFrom;
  private final String g = "from";
  public String serialTo;
  private final String h = "to";
  public String identifier;
  private final String i = "identifier";
  public List<FirmwareBinary> binaries = new ArrayList();
  public JSONObject component;
  public ErrorObject errorObject;
  public SerialRange serialRange;
  public String currentVersion;
  public FirmwareComponentVersion lastAvailableComponentVersion = null;
  
  public FirmwareComponent() {}
  
  public FirmwareComponent(JSONObject paramJSONObject)
    throws UpdateException
  {
    component = paramJSONObject;
    boolean bool = initComponent();
    if (!bool) {
      throw new UpdateException(errorObject.getErrorMessage());
    }
  }
  
  public boolean validateJsonKey(String paramString)
  {
    return component.has(paramString);
  }
  
  public boolean initComponent()
  {
    boolean bool = true;
    try
    {
      if (validateJsonKey("name") == true) {
        name = component.getString("name");
      } else {
        bool = false;
      }
      if (validateJsonKey("versions") == true)
      {
        JSONArray localJSONArray = component.getJSONArray("versions");
        if (localJSONArray.length() > 0) {
          lastAvailableComponentVersion = new FirmwareComponentVersion(localJSONArray.getJSONObject(0));
        }
        for (int j = 0; j < localJSONArray.length(); j++)
        {
          FirmwareComponentVersion localFirmwareComponentVersion = new FirmwareComponentVersion(localJSONArray.getJSONObject(j));
          if ("null".equals(localFirmwareComponentVersion.getNext())) {
            lastAvailableComponentVersion = localFirmwareComponentVersion;
          }
          componentVersions.add(localFirmwareComponentVersion);
        }
      }
      else
      {
        bool = false;
      }
      if (validateJsonKey("version_command") == true) {
        versionCommand = component.getString("version_command");
      } else {
        bool = false;
      }
      if (validateJsonKey("serial_command") == true) {
        serialCommand = component.getString("serial_command");
      } else {
        bool = false;
      }
      if (validateJsonKey("serial") == true)
      {
        a = component.getJSONObject("serial");
        if (a.has("from"))
        {
          serialFrom = a.getString("from");
          if (!isNumeric(serialFrom))
          {
            serialFrom = "";
            bool = false;
          }
        }
        else
        {
          bool = false;
        }
        if (a.has("to"))
        {
          serialTo = a.getString("to");
          if (!isNumeric(serialTo))
          {
            serialTo = "";
            bool = false;
          }
        }
        else
        {
          bool = false;
        }
        if (bool == true) {
          serialRange = new SerialRange(serialFrom, serialTo);
        }
      }
      else
      {
        bool = false;
      }
      if (validateJsonKey("identifier") == true) {
        identifier = component.getString("identifier");
      } else {
        bool = false;
      }
    }
    catch (JSONException|UpdateException localJSONException)
    {
      Logs.log(Logs.LogTypes.exception, "JSON Error", localJSONException);
      bool = false;
      errorObject = new ErrorObject(7012, localJSONException.getMessage());
    }
    return bool;
  }
  
  public boolean isNumeric(String paramString)
  {
    return paramString.matches("-?\\d+(\\.\\d+)?");
  }
  
  public String getIdentifier()
  {
    return identifier;
  }
  
  public String getVersionCommand()
  {
    return versionCommand;
  }
  
  public String getSerialCommand()
  {
    return serialCommand;
  }
  
  public String getSerialFrom()
  {
    return serialFrom;
  }
  
  public String getSerialTo()
  {
    return serialTo;
  }
  
  public SerialRange getSerialRange()
  {
    return serialRange;
  }
  
  public String getName()
  {
    return name;
  }
  
  public List<FirmwareBinary> getBinaries()
  {
    return binaries;
  }
  
  public void setBinaries(List<FirmwareBinary> paramList)
  {
    binaries = paramList;
  }
  
  public List<FirmwareComponentVersion> getComponentVersions()
  {
    return componentVersions;
  }
  
  public String getCurrentVersion()
  {
    return currentVersion;
  }
  
  public void setCurrentVersion(String paramString)
  {
    currentVersion = paramString;
  }
  
  public FirmwareComponentVersion getLastAvailableComponentVersion()
  {
    return lastAvailableComponentVersion;
  }
  
  public FirmwareComponentVersion getCurrentComponentVersion(String paramString)
  {
    Object localObject = null;
    if (componentVersions == null) {
      return null;
    }
    Iterator localIterator = componentVersions.iterator();
    while (localIterator.hasNext())
    {
      FirmwareComponentVersion localFirmwareComponentVersion = (FirmwareComponentVersion)localIterator.next();
      if (localFirmwareComponentVersion.getIdentifier().equals(paramString) == true)
      {
        localObject = localFirmwareComponentVersion;
        Logs.log(Logs.LogTypes.debug, " --- version found for current version");
        break;
      }
    }
    return localObject;
  }
  
  public FirmwareComponentVersion getNextComponentVersion(String paramString)
  {
    FirmwareComponentVersion localFirmwareComponentVersion1 = null;
    FirmwareComponentVersion localFirmwareComponentVersion2 = null;
    localFirmwareComponentVersion1 = getCurrentComponentVersion(paramString);
    if (localFirmwareComponentVersion1 != null)
    {
      String str = localFirmwareComponentVersion1.getNext();
      if (!str.equals("null")) {
        localFirmwareComponentVersion2 = getCurrentComponentVersion(str);
      }
    }
    return localFirmwareComponentVersion2;
  }
}
