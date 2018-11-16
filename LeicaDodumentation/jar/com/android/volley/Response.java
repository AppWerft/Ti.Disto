package com.android.volley;

public class Response<T>
{
  public final T result;
  public final Cache.Entry cacheEntry;
  public final VolleyError error;
  public boolean intermediate = false;
  
  public static <T> Response<T> success(T paramT, Cache.Entry paramEntry)
  {
    return new Response(paramT, paramEntry);
  }
  
  public static <T> Response<T> error(VolleyError paramVolleyError)
  {
    return new Response(paramVolleyError);
  }
  
  public boolean isSuccess()
  {
    return error == null;
  }
  
  private Response(T paramT, Cache.Entry paramEntry)
  {
    result = paramT;
    cacheEntry = paramEntry;
    error = null;
  }
  
  private Response(VolleyError paramVolleyError)
  {
    result = null;
    cacheEntry = null;
    error = paramVolleyError;
  }
  
  public static abstract interface ErrorListener
  {
    public abstract void onErrorResponse(VolleyError paramVolleyError);
  }
  
  public static abstract interface Listener<T>
  {
    public abstract void onResponse(T paramT);
  }
}
