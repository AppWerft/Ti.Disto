package com.android.volley;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class Request<T>
  implements Comparable<Request<T>>
{
  private final VolleyLog.a a = VolleyLog.a.a ? new VolleyLog.a() : null;
  private final int b;
  private final String c;
  private final int d;
  private final Object e = new Object();
  private Response.ErrorListener f;
  private Integer g;
  private RequestQueue h;
  private boolean i = true;
  private boolean j = false;
  private boolean k = false;
  private boolean l = false;
  private RetryPolicy m;
  private Cache.Entry n = null;
  private Object o;
  private a p;
  
  @Deprecated
  public Request(String paramString, Response.ErrorListener paramErrorListener)
  {
    this(-1, paramString, paramErrorListener);
  }
  
  public Request(int paramInt, String paramString, Response.ErrorListener paramErrorListener)
  {
    b = paramInt;
    c = paramString;
    f = paramErrorListener;
    setRetryPolicy(new DefaultRetryPolicy());
    d = b(paramString);
  }
  
  public int getMethod()
  {
    return b;
  }
  
  public Request<?> setTag(Object paramObject)
  {
    o = paramObject;
    return this;
  }
  
  public Object getTag()
  {
    return o;
  }
  
  public Response.ErrorListener getErrorListener()
  {
    return f;
  }
  
  public int getTrafficStatsTag()
  {
    return d;
  }
  
  private static int b(String paramString)
  {
    if (!TextUtils.isEmpty(paramString))
    {
      Uri localUri = Uri.parse(paramString);
      if (localUri != null)
      {
        String str = localUri.getHost();
        if (str != null) {
          return str.hashCode();
        }
      }
    }
    return 0;
  }
  
  public Request<?> setRetryPolicy(RetryPolicy paramRetryPolicy)
  {
    m = paramRetryPolicy;
    return this;
  }
  
  public void addMarker(String paramString)
  {
    if (VolleyLog.a.a) {
      a.a(paramString, Thread.currentThread().getId());
    }
  }
  
  void a(final String paramString)
  {
    if (h != null) {
      h.a(this);
    }
    if (VolleyLog.a.a)
    {
      final long l1 = Thread.currentThread().getId();
      if (Looper.myLooper() != Looper.getMainLooper())
      {
        Handler localHandler = new Handler(Looper.getMainLooper());
        localHandler.post(new Runnable()
        {
          public void run()
          {
            Request.a(Request.this).a(paramString, l1);
            Request.a(Request.this).a(toString());
          }
        });
        return;
      }
      a.a(paramString, l1);
      a.a(toString());
    }
  }
  
  public Request<?> setRequestQueue(RequestQueue paramRequestQueue)
  {
    h = paramRequestQueue;
    return this;
  }
  
  public final Request<?> setSequence(int paramInt)
  {
    g = Integer.valueOf(paramInt);
    return this;
  }
  
  public final int getSequence()
  {
    if (g == null) {
      throw new IllegalStateException("getSequence called before setSequence");
    }
    return g.intValue();
  }
  
  public String getUrl()
  {
    return c;
  }
  
  public String getCacheKey()
  {
    return getUrl();
  }
  
  public Request<?> setCacheEntry(Cache.Entry paramEntry)
  {
    n = paramEntry;
    return this;
  }
  
  public Cache.Entry getCacheEntry()
  {
    return n;
  }
  
  public void cancel()
  {
    synchronized (e)
    {
      j = true;
      f = null;
    }
  }
  
  public boolean isCanceled()
  {
    synchronized (e)
    {
      return j;
    }
  }
  
  public Map<String, String> getHeaders()
    throws AuthFailureError
  {
    return Collections.emptyMap();
  }
  
  @Deprecated
  protected Map<String, String> getPostParams()
    throws AuthFailureError
  {
    return getParams();
  }
  
  @Deprecated
  protected String getPostParamsEncoding()
  {
    return getParamsEncoding();
  }
  
  @Deprecated
  public String getPostBodyContentType()
  {
    return getBodyContentType();
  }
  
  @Deprecated
  public byte[] getPostBody()
    throws AuthFailureError
  {
    Map localMap = getPostParams();
    if ((localMap != null) && (localMap.size() > 0)) {
      return a(localMap, getPostParamsEncoding());
    }
    return null;
  }
  
  protected Map<String, String> getParams()
    throws AuthFailureError
  {
    return null;
  }
  
  protected String getParamsEncoding()
  {
    return "UTF-8";
  }
  
  public String getBodyContentType()
  {
    return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
  }
  
  public byte[] getBody()
    throws AuthFailureError
  {
    Map localMap = getParams();
    if ((localMap != null) && (localMap.size() > 0)) {
      return a(localMap, getParamsEncoding());
    }
    return null;
  }
  
  private byte[] a(Map<String, String> paramMap, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        localStringBuilder.append(URLEncoder.encode((String)localEntry.getKey(), paramString));
        localStringBuilder.append('=');
        localStringBuilder.append(URLEncoder.encode((String)localEntry.getValue(), paramString));
        localStringBuilder.append('&');
      }
      return localStringBuilder.toString().getBytes(paramString);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new RuntimeException("Encoding not supported: " + paramString, localUnsupportedEncodingException);
    }
  }
  
  public final Request<?> setShouldCache(boolean paramBoolean)
  {
    i = paramBoolean;
    return this;
  }
  
  public final boolean shouldCache()
  {
    return i;
  }
  
  public final Request<?> setShouldRetryServerErrors(boolean paramBoolean)
  {
    l = paramBoolean;
    return this;
  }
  
  public final boolean shouldRetryServerErrors()
  {
    return l;
  }
  
  public Priority getPriority()
  {
    return Priority.NORMAL;
  }
  
  public final int getTimeoutMs()
  {
    return m.getCurrentTimeout();
  }
  
  public RetryPolicy getRetryPolicy()
  {
    return m;
  }
  
  public void markDelivered()
  {
    synchronized (e)
    {
      k = true;
    }
  }
  
  public boolean hasHadResponseDelivered()
  {
    synchronized (e)
    {
      return k;
    }
  }
  
  protected abstract Response<T> parseNetworkResponse(NetworkResponse paramNetworkResponse);
  
  protected VolleyError parseNetworkError(VolleyError paramVolleyError)
  {
    return paramVolleyError;
  }
  
  protected abstract void deliverResponse(T paramT);
  
  public void deliverError(VolleyError paramVolleyError)
  {
    Response.ErrorListener localErrorListener;
    synchronized (e)
    {
      localErrorListener = f;
    }
    if (localErrorListener != null) {
      localErrorListener.onErrorResponse(paramVolleyError);
    }
  }
  
  void a(a paramA)
  {
    synchronized (e)
    {
      p = paramA;
    }
  }
  
  void a(Response<?> paramResponse)
  {
    a localA;
    synchronized (e)
    {
      localA = p;
    }
    if (localA != null) {
      localA.a(this, paramResponse);
    }
  }
  
  void a()
  {
    a localA;
    synchronized (e)
    {
      localA = p;
    }
    if (localA != null) {
      localA.a(this);
    }
  }
  
  public int compareTo(Request<T> paramRequest)
  {
    Priority localPriority1 = getPriority();
    Priority localPriority2 = paramRequest.getPriority();
    return localPriority1 == localPriority2 ? g.intValue() - g.intValue() : localPriority2.ordinal() - localPriority1.ordinal();
  }
  
  public String toString()
  {
    String str = "0x" + Integer.toHexString(getTrafficStatsTag());
    return (j ? "[X] " : "[ ] ") + getUrl() + " " + str + " " + getPriority() + " " + g;
  }
  
  public static enum Priority
  {
    private Priority() {}
  }
  
  static abstract interface a
  {
    public abstract void a(Request<?> paramRequest, Response<?> paramResponse);
    
    public abstract void a(Request<?> paramRequest);
  }
  
  public static abstract interface Method
  {
    public static final int DEPRECATED_GET_OR_POST = -1;
    public static final int GET = 0;
    public static final int POST = 1;
    public static final int PUT = 2;
    public static final int DELETE = 3;
    public static final int HEAD = 4;
    public static final int OPTIONS = 5;
    public static final int TRACE = 6;
    public static final int PATCH = 7;
  }
}
