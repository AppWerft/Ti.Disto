package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import java.io.UnsupportedEncodingException;

public class StringRequest
  extends Request<String>
{
  private final Object a = new Object();
  private Response.Listener<String> b;
  
  public StringRequest(int paramInt, String paramString, Response.Listener<String> paramListener, Response.ErrorListener paramErrorListener)
  {
    super(paramInt, paramString, paramErrorListener);
    b = paramListener;
  }
  
  public StringRequest(String paramString, Response.Listener<String> paramListener, Response.ErrorListener paramErrorListener)
  {
    this(0, paramString, paramListener, paramErrorListener);
  }
  
  public void cancel()
  {
    super.cancel();
    synchronized (a)
    {
      b = null;
    }
  }
  
  protected void deliverResponse(String paramString)
  {
    Response.Listener localListener;
    synchronized (a)
    {
      localListener = b;
    }
    if (localListener != null) {
      localListener.onResponse(paramString);
    }
  }
  
  protected Response<String> parseNetworkResponse(NetworkResponse paramNetworkResponse)
  {
    String str;
    try
    {
      str = new String(data, HttpHeaderParser.parseCharset(headers));
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      str = new String(data);
    }
    return Response.success(str, HttpHeaderParser.parseCacheHeaders(paramNetworkResponse));
  }
}
