package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import java.io.UnsupportedEncodingException;

public abstract class JsonRequest<T>
  extends Request<T>
{
  protected static final String PROTOCOL_CHARSET = "utf-8";
  private static final String a = String.format("application/json; charset=%s", new Object[] { "utf-8" });
  private final Object b = new Object();
  private Response.Listener<T> c;
  private final String d;
  
  @Deprecated
  public JsonRequest(String paramString1, String paramString2, Response.Listener<T> paramListener, Response.ErrorListener paramErrorListener)
  {
    this(-1, paramString1, paramString2, paramListener, paramErrorListener);
  }
  
  public JsonRequest(int paramInt, String paramString1, String paramString2, Response.Listener<T> paramListener, Response.ErrorListener paramErrorListener)
  {
    super(paramInt, paramString1, paramErrorListener);
    c = paramListener;
    d = paramString2;
  }
  
  public void cancel()
  {
    super.cancel();
    synchronized (b)
    {
      c = null;
    }
  }
  
  protected void deliverResponse(T paramT)
  {
    Response.Listener localListener;
    synchronized (b)
    {
      localListener = c;
    }
    if (localListener != null) {
      localListener.onResponse(paramT);
    }
  }
  
  protected abstract Response<T> parseNetworkResponse(NetworkResponse paramNetworkResponse);
  
  @Deprecated
  public String getPostBodyContentType()
  {
    return getBodyContentType();
  }
  
  @Deprecated
  public byte[] getPostBody()
  {
    return getBody();
  }
  
  public String getBodyContentType()
  {
    return a;
  }
  
  public byte[] getBody()
  {
    try
    {
      return d == null ? null : d.getBytes("utf-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", new Object[] { d, "utf-8" });
    }
    return null;
  }
}
