package com.android.volley;

public class VolleyError
  extends Exception
{
  public final NetworkResponse networkResponse;
  private long a;
  
  public VolleyError()
  {
    networkResponse = null;
  }
  
  public VolleyError(NetworkResponse paramNetworkResponse)
  {
    networkResponse = paramNetworkResponse;
  }
  
  public VolleyError(String paramString)
  {
    super(paramString);
    networkResponse = null;
  }
  
  public VolleyError(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    networkResponse = null;
  }
  
  public VolleyError(Throwable paramThrowable)
  {
    super(paramThrowable);
    networkResponse = null;
  }
  
  void a(long paramLong)
  {
    a = paramLong;
  }
  
  public long getNetworkTimeMs()
  {
    return a;
  }
}
