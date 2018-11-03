package ch.leica.sdk.update.FirmwareUpdate.DataClasses;

import android.webkit.URLUtil;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.UpdateException;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import ch.leica.sdk.update.FirmwareUpdate.DownloadBinaries;
import ch.leica.sdk.update.FirmwareUpdate.FileRequest;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import java.io.File;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class FirmwareComponentVersion
{
  private String a;
  private String b;
  private String c = "firmware";
  public String urlStr = "url";
  public String next;
  private final String d = "next";
  public String identifier;
  private final String e = "identifier";
  public String version;
  private final String f = "version";
  public String filesize;
  private final String g = "filesize";
  public JSONObject componentVersion;
  public ErrorObject error = null;
  
  public FirmwareComponentVersion(JSONObject paramJSONObject)
    throws UpdateException
  {
    componentVersion = paramJSONObject;
    boolean bool = initProductVersion();
    if (!bool) {
      throw new UpdateException("Unable to parse the FirmwareComponentVersion");
    }
  }
  
  public boolean validateJsonKey(String paramString)
  {
    return componentVersion.has(paramString);
  }
  
  public boolean initProductVersion()
  {
    boolean bool = true;
    try
    {
      if (validateJsonKey(urlStr) == true)
      {
        a = componentVersion.getString(urlStr);
        b = URLUtil.guessFileName(a, null, null);
        c = ("/" + b.split("\\.")[0]);
      }
      else
      {
        bool = false;
      }
      if (validateJsonKey("filesize") == true) {
        filesize = componentVersion.getString("filesize");
      } else {
        bool = false;
      }
      if (validateJsonKey("next") == true) {
        next = componentVersion.getString("next");
      } else {
        bool = false;
      }
      if (validateJsonKey("identifier") == true) {
        identifier = componentVersion.getString("identifier");
      } else {
        bool = false;
      }
      if (validateJsonKey("version") == true) {
        version = componentVersion.getString("version");
      } else {
        bool = false;
      }
    }
    catch (JSONException localJSONException)
    {
      error = new ErrorObject(7012, localJSONException.getMessage());
      Logs.log(Logs.LogTypes.exception, "Json Error", localJSONException);
      bool = false;
    }
    return bool;
  }
  
  public String getIdentifier()
  {
    Logs.log(Logs.LogTypes.debug, identifier);
    return identifier;
  }
  
  public String getVersion()
  {
    Logs.log(Logs.LogTypes.debug, version);
    return version;
  }
  
  public String getNext()
  {
    Logs.log(Logs.LogTypes.debug, next);
    return next;
  }
  
  public String getUrl()
  {
    Logs.log(Logs.LogTypes.debug, a);
    return a;
  }
  
  public FileRequest downloadBinaries(File paramFile, final DownloadBinariesCallback paramDownloadBinariesCallback)
  {
    final File localFile = new File(paramFile + "/" + c + "/");
    FileRequest localFileRequest = new FileRequest(0, a, new Response.Listener()new Response.ErrorListener
    {
      public void a(byte[] paramAnonymousArrayOfByte)
      {
        DownloadBinaries localDownloadBinaries = new DownloadBinaries(paramAnonymousArrayOfByte, localFile, FirmwareComponentVersion.a(FirmwareComponentVersion.this));
        if (localDownloadBinaries.getError() != null) {
          paramDownloadBinariesCallback.downloadBinariesResult(null, null, localDownloadBinaries.getError());
        } else {
          paramDownloadBinariesCallback.downloadBinariesResult(localDownloadBinaries.getBinaries(), localDownloadBinaries.getOtherFiles(), null);
        }
      }
    }, new Response.ErrorListener()
    {
      public void onErrorResponse(VolleyError paramAnonymousVolleyError)
      {
        Logs.log(Logs.LogTypes.exception, paramAnonymousVolleyError.getMessage());
        ErrorObject localErrorObject = null;
        if (paramAnonymousVolleyError.getLocalizedMessage().toLowerCase().contains("unable to resolve host")) {
          localErrorObject = new ErrorObject(1750, "Unable to resolve host, it seems that there is no internet connection.");
        } else {
          localErrorObject = new ErrorObject(17001, paramAnonymousVolleyError.getLocalizedMessage());
        }
        paramDownloadBinariesCallback.downloadBinariesResult(null, null, localErrorObject);
      }
    }, null);
    return localFileRequest;
  }
  
  public String getFilesize()
  {
    return filesize;
  }
  
  public static abstract interface DownloadBinariesCallback
  {
    public abstract void downloadBinariesResult(List<FirmwareBinary> paramList, List<String> paramList1, ErrorObject paramErrorObject);
  }
}
