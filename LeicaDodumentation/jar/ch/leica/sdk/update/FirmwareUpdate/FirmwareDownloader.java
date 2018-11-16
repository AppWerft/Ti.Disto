package ch.leica.sdk.update.FirmwareUpdate;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import ch.leica.a.a.a;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.UpdateException;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareBinary;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareComponent;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareComponentVersion;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareComponentVersion.DownloadBinariesCallback;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareProduct;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareProductVersion;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareProductVersion.DownloadBinariesCallback;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareUpdate;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FirmwareDownloader
{
  private Context a;
  private final String b = "https://446973746f.s3.amazonaws.com/b262f5f2/";
  private final String c = "https://firmware-leica.cm3p.de/b262f5f2/";
  private String d;
  private RequestQueue e;
  private JSONObject f = null;
  @VisibleForTesting
  private String g;
  
  public FirmwareDownloader(Context paramContext)
  {
    a = paramContext;
    e = Volley.newRequestQueue(a);
  }
  
  private String a()
  {
    if (a.d()) {
      return "https://firmware-leica.cm3p.de/b262f5f2/";
    }
    if (a.c()) {
      return "https://446973746f.s3.amazonaws.com/b262f5f2/";
    }
    Logs.log(Logs.LogTypes.informative, "NO UPDATE LICENSE FOUND. UNABLE TO UPDATE THE DEVICE.");
    return null;
  }
  
  private String a(String paramString)
  {
    String str = a();
    if (str != null) {
      return str + paramString + "/update.json";
    }
    return null;
  }
  
  public void getFirmwareInformation(String paramString1, String paramString2, final String paramString3, final FirmwareProductCallback paramFirmwareProductCallback)
  {
    String str = paramString1 + paramString2;
    try
    {
      g = b(str);
      d = a(g);
      if (d == null)
      {
        localObject1 = new ErrorObject(8001, "No valid Update License Found. ");
        paramFirmwareProductCallback.firmwareProductResult(null, (ErrorObject)localObject1);
        return;
      }
      Logs.log(Logs.LogTypes.debug, "requestUrl: " + d);
      Object localObject1 = new JSONObject();
      localObject2 = new JsonObjectRequest(0, d, (JSONObject)localObject1, new Response.Listener()new Response.ErrorListener
      {
        public void a(JSONObject paramAnonymousJSONObject)
        {
          Logs.log(Logs.LogTypes.debug, "requestFirmwareServer: " + paramAnonymousJSONObject.toString());
          FirmwareProduct localFirmwareProduct = null;
          try
          {
            localFirmwareProduct = extractFirmwareProductFromJsonResponse(paramAnonymousJSONObject, paramString3);
          }
          catch (JSONException|UpdateException localJSONException)
          {
            ErrorObject localErrorObject2 = new ErrorObject(7020, "Unable to parse FirmwareProduct from retrieved JSON." + localJSONException.getMessage());
            Logs.log(Logs.LogTypes.debug, "firmwareProductResult ErrorObject: (code): " + localErrorObject2.getErrorCode() + "(Message): " + localErrorObject2.getErrorMessage());
            paramFirmwareProductCallback.firmwareProductResult(null, localErrorObject2);
          }
          if (localFirmwareProduct != null)
          {
            Logs.log(Logs.LogTypes.debug, "firmwareProductResult returned.");
            paramFirmwareProductCallback.firmwareProductResult(localFirmwareProduct, null);
          }
          else
          {
            ErrorObject localErrorObject1 = new ErrorObject(7020, "Unable to parse FirmwareProduct from retrieved JSON.");
            Logs.log(Logs.LogTypes.debug, "firmwareProductResult ErrorObject: (code): " + localErrorObject1.getErrorCode() + "(Message): " + localErrorObject1.getErrorMessage());
            paramFirmwareProductCallback.firmwareProductResult(null, localErrorObject1);
          }
        }
      }, new Response.ErrorListener()
      {
        public void onErrorResponse(VolleyError paramAnonymousVolleyError)
        {
          Logs.log(Logs.LogTypes.exception, paramAnonymousVolleyError.getMessage());
          ErrorObject localErrorObject = null;
          if ((paramAnonymousVolleyError != null) && (paramAnonymousVolleyError.getLocalizedMessage() != null))
          {
            if (paramAnonymousVolleyError.getLocalizedMessage().toLowerCase().contains("unable to resolve host")) {
              localErrorObject = new ErrorObject(1750, "Unable to resolve host, it seems that there is no internet connection.");
            } else {
              localErrorObject = new ErrorObject(17001, paramAnonymousVolleyError.getLocalizedMessage());
            }
          }
          else {
            localErrorObject = new ErrorObject(1750, "Unable to resolve host, it seems that there is no internet connection.");
          }
          paramFirmwareProductCallback.firmwareProductResult(null, localErrorObject);
        }
      });
      e.add((Request)localObject2);
    }
    catch (NoSuchAlgorithmException|UnsupportedEncodingException localNoSuchAlgorithmException)
    {
      Object localObject2 = new ErrorObject(7010, localNoSuchAlgorithmException.getMessage());
      paramFirmwareProductCallback.firmwareProductResult(null, (ErrorObject)localObject2);
    }
  }
  
  public void getFirmwareUpdateWithBinaries(final FirmwareProduct paramFirmwareProduct, final FirmwareProductVersion paramFirmwareProductVersion, final FirmwareUpdateCallback paramFirmwareUpdateCallback)
  {
    FileRequest localFileRequest = paramFirmwareProductVersion.downloadBinaries(a.getExternalCacheDir(), new FirmwareProductVersion.DownloadBinariesCallback()
    {
      public void downloadBinariesResult(List<FirmwareBinary> paramAnonymousList, List<String> paramAnonymousList1, ErrorObject paramAnonymousErrorObject)
      {
        if (paramAnonymousErrorObject == null)
        {
          FirmwareUpdate localFirmwareUpdate = new FirmwareUpdate(paramFirmwareProductVersion.getVersion(), paramFirmwareProduct.getBrandIdentifier(), paramFirmwareProduct.getName(), paramFirmwareProduct.getIdentifier(), paramAnonymousList);
          if ((paramAnonymousList1 != null) && (paramAnonymousList1.size() > 0)) {
            localFirmwareUpdate.setChangelog((String)paramAnonymousList1.get(0));
          }
          paramFirmwareUpdateCallback.firmwareUpdateResult(localFirmwareUpdate, null);
        }
        else
        {
          paramFirmwareUpdateCallback.firmwareUpdateResult(null, paramAnonymousErrorObject);
        }
      }
    });
    e.add(localFileRequest);
  }
  
  public void getAvailableFirmwareUpdatesForComponents(final FirmwareComponent paramFirmwareComponent, final FirmwareComponentVersion paramFirmwareComponentVersion, final FirmwareUpdate paramFirmwareUpdate, final FirmwareUpdateCallback paramFirmwareUpdateCallback)
  {
    FileRequest localFileRequest = paramFirmwareComponentVersion.downloadBinaries(a.getFilesDir(), new FirmwareComponentVersion.DownloadBinariesCallback()
    {
      public void downloadBinariesResult(List<FirmwareBinary> paramAnonymousList, List<String> paramAnonymousList1, ErrorObject paramAnonymousErrorObject)
      {
        if ((paramAnonymousErrorObject == null) && (paramAnonymousList != null) && (paramAnonymousList.size() > 0))
        {
          paramFirmwareComponent.setCurrentVersion(paramFirmwareComponentVersion.getVersion());
          paramFirmwareComponent.setBinaries(paramAnonymousList);
          paramFirmwareUpdate.getComponents().add(paramFirmwareComponent);
          if ((paramAnonymousList1 != null) && (paramAnonymousList1.size() > 0)) {
            paramFirmwareUpdate.setChangelog((String)paramAnonymousList1.get(0));
          }
          Logs.log(Logs.LogTypes.debug, "binaries size: " + paramAnonymousList.size() + " otherFiles size: " + paramAnonymousList1.size());
          paramFirmwareUpdateCallback.firmwareUpdateResult(paramFirmwareUpdate, null);
        }
        else
        {
          paramFirmwareUpdateCallback.firmwareUpdateResult(paramFirmwareUpdate, paramAnonymousErrorObject);
        }
      }
    });
    e.add(localFileRequest);
  }
  
  @VisibleForTesting
  public FirmwareProduct extractFirmwareProductFromJsonResponse(JSONObject paramJSONObject, String paramString)
    throws JSONException, UpdateException
  {
    String str = "products";
    f = paramJSONObject;
    Object localObject = null;
    if (f != null)
    {
      JSONArray localJSONArray = paramJSONObject.getJSONArray(str);
      for (int i = 0; i < localJSONArray.length(); i++)
      {
        JSONObject localJSONObject = localJSONArray.getJSONObject(i);
        FirmwareProduct localFirmwareProduct = new FirmwareProduct(localJSONObject);
        SerialRange localSerialRange = localFirmwareProduct.getSerialRange();
        if ((localSerialRange != null) && (localSerialRange.isValid(paramString)))
        {
          localObject = localFirmwareProduct;
          break;
        }
      }
    }
    else
    {
      Logs.log(Logs.LogTypes.debug, "No JSON Found for parsing");
    }
    return localObject;
  }
  
  private String b(String paramString)
    throws NoSuchAlgorithmException, UnsupportedEncodingException
  {
    MessageDigest localMessageDigest = MessageDigest.getInstance("SHA-256");
    byte[] arrayOfByte = localMessageDigest.digest(paramString.getBytes("UTF-8"));
    return a(arrayOfByte);
  }
  
  private String a(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int k : paramArrayOfByte)
    {
      String str = Integer.toHexString(0xFF & k);
      if (str.length() == 1) {
        localStringBuilder.append('0');
      }
      localStringBuilder.append(str);
    }
    return localStringBuilder.toString();
  }
  
  @VisibleForTesting
  public String getSha256HashTest()
  {
    return g;
  }
  
  @VisibleForTesting
  public JSONObject getServerResponseObjectTest()
  {
    return f;
  }
  
  @VisibleForTesting
  public FirmwareProduct getFirmwareProductTest()
  {
    return null;
  }
  
  @VisibleForTesting
  public String getRequestUrlTest()
  {
    return d;
  }
  
  @VisibleForTesting
  public String parseServerResponseTest()
  {
    return d;
  }
  
  public static abstract interface FirmwareUpdateCallback
  {
    public abstract void firmwareUpdateResult(FirmwareUpdate paramFirmwareUpdate, ErrorObject paramErrorObject);
  }
  
  public static abstract interface FirmwareProductCallback
  {
    public abstract void firmwareProductResult(FirmwareProduct paramFirmwareProduct, ErrorObject paramErrorObject);
  }
}
