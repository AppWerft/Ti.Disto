package ch.leica.sdk.update.FirmwareUpdate;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import java.util.HashMap;
import java.util.Map;

public class FileRequest
  extends Request<byte[]>
{
  private final Response.Listener<byte[]> a;
  private Map<String, String> b;
  public Map<String, String> responseHeaders;
  
  public FileRequest(int paramInt, String paramString, Response.Listener<byte[]> paramListener, Response.ErrorListener paramErrorListener, HashMap<String, String> paramHashMap)
  {
    super(paramInt, paramString, paramErrorListener);
    setShouldCache(false);
    a = paramListener;
    b = paramHashMap;
  }
  
  protected Map<String, String> getParams()
    throws AuthFailureError
  {
    return b;
  }
  
  protected void deliverResponse(byte[] paramArrayOfByte)
  {
    a.onResponse(paramArrayOfByte);
  }
  
  protected Response<byte[]> parseNetworkResponse(NetworkResponse paramNetworkResponse)
  {
    responseHeaders = headers;
    return Response.success(data, HttpHeaderParser.parseCacheHeaders(paramNetworkResponse));
  }
}
