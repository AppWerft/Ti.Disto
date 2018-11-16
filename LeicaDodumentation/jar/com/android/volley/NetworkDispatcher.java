package com.android.volley;

import android.annotation.TargetApi;
import android.net.TrafficStats;
import android.os.Build.VERSION;
import android.os.Process;
import android.os.SystemClock;
import java.util.concurrent.BlockingQueue;

public class NetworkDispatcher
  extends Thread
{
  private final BlockingQueue<Request<?>> a;
  private final Network b;
  private final Cache c;
  private final ResponseDelivery d;
  private volatile boolean e = false;
  
  public NetworkDispatcher(BlockingQueue<Request<?>> paramBlockingQueue, Network paramNetwork, Cache paramCache, ResponseDelivery paramResponseDelivery)
  {
    a = paramBlockingQueue;
    b = paramNetwork;
    c = paramCache;
    d = paramResponseDelivery;
  }
  
  public void quit()
  {
    e = true;
    interrupt();
  }
  
  @TargetApi(14)
  private void a(Request<?> paramRequest)
  {
    if (Build.VERSION.SDK_INT >= 14) {
      TrafficStats.setThreadStatsTag(paramRequest.getTrafficStatsTag());
    }
  }
  
  public void run()
  {
    Process.setThreadPriority(10);
    try
    {
      for (;;)
      {
        a();
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      if (e) {
        return;
      }
    }
  }
  
  private void a()
    throws InterruptedException
  {
    Request localRequest = (Request)a.take();
    long l = SystemClock.elapsedRealtime();
    try
    {
      localRequest.addMarker("network-queue-take");
      if (localRequest.isCanceled())
      {
        localRequest.a("network-discard-cancelled");
        localRequest.a();
        return;
      }
      a(localRequest);
      NetworkResponse localNetworkResponse = b.performRequest(localRequest);
      localRequest.addMarker("network-http-complete");
      if ((notModified) && (localRequest.hasHadResponseDelivered()))
      {
        localRequest.a("not-modified");
        localRequest.a();
        return;
      }
      localObject = localRequest.parseNetworkResponse(localNetworkResponse);
      localRequest.addMarker("network-parse-complete");
      if ((localRequest.shouldCache()) && (cacheEntry != null))
      {
        c.put(localRequest.getCacheKey(), cacheEntry);
        localRequest.addMarker("network-cache-written");
      }
      localRequest.markDelivered();
      d.postResponse(localRequest, (Response)localObject);
      localRequest.a((Response)localObject);
    }
    catch (VolleyError localVolleyError)
    {
      localVolleyError.a(SystemClock.elapsedRealtime() - l);
      a(localRequest, localVolleyError);
      localRequest.a();
    }
    catch (Exception localException)
    {
      VolleyLog.e(localException, "Unhandled exception %s", new Object[] { localException.toString() });
      Object localObject = new VolleyError(localException);
      ((VolleyError)localObject).a(SystemClock.elapsedRealtime() - l);
      d.postError(localRequest, (VolleyError)localObject);
      localRequest.a();
    }
  }
  
  private void a(Request<?> paramRequest, VolleyError paramVolleyError)
  {
    paramVolleyError = paramRequest.parseNetworkError(paramVolleyError);
    d.postError(paramRequest, paramVolleyError);
  }
}
