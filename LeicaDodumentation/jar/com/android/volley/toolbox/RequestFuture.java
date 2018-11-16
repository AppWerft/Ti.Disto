package com.android.volley.toolbox;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestFuture<T>
  implements Response.ErrorListener, Response.Listener<T>, Future<T>
{
  private Request<?> a;
  private boolean b = false;
  private T c;
  private VolleyError d;
  
  public static <E> RequestFuture<E> newFuture()
  {
    return new RequestFuture();
  }
  
  private RequestFuture() {}
  
  public void setRequest(Request<?> paramRequest)
  {
    a = paramRequest;
  }
  
  public synchronized boolean cancel(boolean paramBoolean)
  {
    if (a == null) {
      return false;
    }
    if (!isDone())
    {
      a.cancel();
      return true;
    }
    return false;
  }
  
  public T get()
    throws InterruptedException, ExecutionException
  {
    try
    {
      return a(null);
    }
    catch (TimeoutException localTimeoutException)
    {
      throw new AssertionError(localTimeoutException);
    }
  }
  
  public T get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    return a(Long.valueOf(TimeUnit.MILLISECONDS.convert(paramLong, paramTimeUnit)));
  }
  
  private synchronized T a(Long paramLong)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    if (d != null) {
      throw new ExecutionException(d);
    }
    if (b) {
      return c;
    }
    if (paramLong == null) {
      wait(0L);
    } else if (paramLong.longValue() > 0L) {
      wait(paramLong.longValue());
    }
    if (d != null) {
      throw new ExecutionException(d);
    }
    if (!b) {
      throw new TimeoutException();
    }
    return c;
  }
  
  public boolean isCancelled()
  {
    if (a == null) {
      return false;
    }
    return a.isCanceled();
  }
  
  public synchronized boolean isDone()
  {
    return (b) || (d != null) || (isCancelled());
  }
  
  public synchronized void onResponse(T paramT)
  {
    b = true;
    c = paramT;
    notifyAll();
  }
  
  public synchronized void onErrorResponse(VolleyError paramVolleyError)
  {
    d = paramVolleyError;
    notifyAll();
  }
}
