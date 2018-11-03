package com.android.volley.toolbox;

import android.os.Handler;
import android.os.Looper;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Request.Priority;
import com.android.volley.Response;

public class ClearCacheRequest
  extends Request<Object>
{
  private final Cache a;
  private final Runnable b;
  
  public ClearCacheRequest(Cache paramCache, Runnable paramRunnable)
  {
    super(0, null, null);
    a = paramCache;
    b = paramRunnable;
  }
  
  public boolean isCanceled()
  {
    a.clear();
    if (b != null)
    {
      Handler localHandler = new Handler(Looper.getMainLooper());
      localHandler.postAtFrontOfQueue(b);
    }
    return true;
  }
  
  public Request.Priority getPriority()
  {
    return Request.Priority.IMMEDIATE;
  }
  
  protected Response<Object> parseNetworkResponse(NetworkResponse paramNetworkResponse)
  {
    return null;
  }
  
  protected void deliverResponse(Object paramObject) {}
}
