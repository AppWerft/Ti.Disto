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

public class FirmwareProductVersion
{
  private String a;
  private String b;
  private String c = "firmware";
  private String d = "url";
  private String e;
  private String f = "next";
  private String g;
  private String h = "identifier";
  private String i;
  private String j = "filesize";
  private String k;
  private String l = "version";
  private JSONObject m;
  private String n = "changelog";
  public ErrorObject error = null;
  
  public FirmwareProductVersion(JSONObject paramJSONObject)
    throws UpdateException
  {
    m = paramJSONObject;
    boolean bool = a();
    if (!bool) {
      throw new UpdateException("Unable to create productVersion");
    }
  }
  
  private boolean a(String paramString)
  {
    return m.has(paramString);
  }
  
  private boolean a()
  {
    boolean bool = true;
    try
    {
      if (a(j) == true) {
        i = m.getString(j);
      } else {
        bool = false;
      }
      if (a(d) == true)
      {
        a = m.getString(d);
        b = URLUtil.guessFileName(a, null, null);
        c = ("/" + b.split("\\.")[0]);
      }
      else
      {
        bool = false;
      }
      if (a(f) == true) {
        e = m.getString(f);
      } else {
        bool = false;
      }
      if (a(h) == true) {
        g = m.getString(h);
      } else {
        bool = false;
      }
      if (a(l) == true) {
        k = m.getString(l);
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
    Logs.log(Logs.LogTypes.debug, g);
    return g;
  }
  
  public String getVersion()
  {
    Logs.log(Logs.LogTypes.debug, k);
    return k;
  }
  
  public String getNext()
  {
    Logs.log(Logs.LogTypes.debug, e);
    return e;
  }
  
  public String getUrl()
  {
    Logs.log(Logs.LogTypes.debug, a);
    return a;
  }
  
  public FileRequest downloadBinaries(File paramFile, final DownloadBinariesCallback paramDownloadBinariesCallback)
  {
    final File localFile = new File(paramFile + "/" + c + "/");
    Logs.log(Logs.LogTypes.debug, "URL: " + a + "\n File: " + localFile + "/" + b);
    FileRequest localFileRequest = new FileRequest(0, a, new Response.Listener()new Response.ErrorListener
    {
      public void a(byte[] paramAnonymousArrayOfByte)
      {
        DownloadBinaries localDownloadBinaries = new DownloadBinaries(paramAnonymousArrayOfByte, localFile, FirmwareProductVersion.a(FirmwareProductVersion.this));
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
        ErrorObject localErrorObject = new ErrorObject(17001, paramAnonymousVolleyError.getLocalizedMessage());
        paramDownloadBinariesCallback.downloadBinariesResult(null, null, localErrorObject);
      }
    }, null);
    return localFileRequest;
  }
  
  public String getFilesize()
  {
    return i;
  }
  
  public static abstract interface DownloadBinariesCallback
  {
    public abstract void downloadBinariesResult(List<FirmwareBinary> paramList, List<String> paramList1, ErrorObject paramErrorObject);
  }
}
