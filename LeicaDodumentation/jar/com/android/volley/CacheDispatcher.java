package com.android.volley;

import android.os.Process;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class CacheDispatcher
  extends Thread
{
  private static final boolean a = VolleyLog.DEBUG;
  private final BlockingQueue<Request<?>> b;
  private final BlockingQueue<Request<?>> c;
  private final Cache d;
  private final ResponseDelivery e;
  private volatile boolean f = false;
  private final a g;
  
  public CacheDispatcher(BlockingQueue<Request<?>> paramBlockingQueue1, BlockingQueue<Request<?>> paramBlockingQueue2, Cache paramCache, ResponseDelivery paramResponseDelivery)
  {
    b = paramBlockingQueue1;
    c = paramBlockingQueue2;
    d = paramCache;
    e = paramResponseDelivery;
    g = new a(this);
  }
  
  public void quit()
  {
    f = true;
    interrupt();
  }
  
  public void run()
  {
    if (a) {
      VolleyLog.v("start new dispatcher", new Object[0]);
    }
    Process.setThreadPriority(10);
    d.initialize();
    try
    {
      for (;;)
      {
        a();
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      if (f) {
        return;
      }
    }
  }
  
  private void a()
    throws InterruptedException
  {
    final Request localRequest = (Request)b.take();
    localRequest.addMarker("cache-queue-take");
    if (localRequest.isCanceled())
    {
      localRequest.a("cache-discard-canceled");
      return;
    }
    Cache.Entry localEntry = d.get(localRequest.getCacheKey());
    if (localEntry == null)
    {
      localRequest.addMarker("cache-miss");
      if (!a.a(g, localRequest)) {
        c.put(localRequest);
      }
      return;
    }
    if (localEntry.isExpired())
    {
      localRequest.addMarker("cache-hit-expired");
      localRequest.setCacheEntry(localEntry);
      if (!a.a(g, localRequest)) {
        c.put(localRequest);
      }
      return;
    }
    localRequest.addMarker("cache-hit");
    Response localResponse = localRequest.parseNetworkResponse(new NetworkResponse(data, responseHeaders));
    localRequest.addMarker("cache-hit-parsed");
    if (!localEntry.refreshNeeded())
    {
      e.postResponse(localRequest, localResponse);
    }
    else
    {
      localRequest.addMarker("cache-hit-refresh-needed");
      localRequest.setCacheEntry(localEntry);
      intermediate = true;
      if (!a.a(g, localRequest)) {
        e.postResponse(localRequest, localResponse, new Runnable()
        {
          public void run()
          {
            try
            {
              CacheDispatcher.a(CacheDispatcher.this).put(localRequest);
            }
            catch (InterruptedException localInterruptedException)
            {
              Thread.currentThread().interrupt();
            }
          }
        });
      } else {
        e.postResponse(localRequest, localResponse);
      }
    }
  }
  
  private static class a
    implements Request.a
  {
    private final Map<String, List<Request<?>>> a = new HashMap();
    private final CacheDispatcher b;
    
    a(CacheDispatcher paramCacheDispatcher)
    {
      b = paramCacheDispatcher;
    }
    
    public void a(Request<?> paramRequest, Response<?> paramResponse)
    {
      if ((cacheEntry == null) || (cacheEntry.isExpired()))
      {
        a(paramRequest);
        return;
      }
      String str = paramRequest.getCacheKey();
      List localList;
      synchronized (this)
      {
        localList = (List)a.remove(str);
      }
      if (localList != null)
      {
        if (VolleyLog.DEBUG) {
          VolleyLog.v("Releasing %d waiting requests for cacheKey=%s.", new Object[] { Integer.valueOf(localList.size()), str });
        }
        ??? = localList.iterator();
        while (((Iterator)???).hasNext())
        {
          Request localRequest = (Request)((Iterator)???).next();
          CacheDispatcher.b(b).postResponse(localRequest, paramResponse);
        }
      }
    }
    
    public synchronized void a(Request<?> paramRequest)
    {
      String str = paramRequest.getCacheKey();
      List localList = (List)a.remove(str);
      if ((localList != null) && (!localList.isEmpty()))
      {
        if (VolleyLog.DEBUG) {
          VolleyLog.v("%d waiting requests for cacheKey=%s; resend to network", new Object[] { Integer.valueOf(localList.size()), str });
        }
        Request localRequest = (Request)localList.remove(0);
        a.put(str, localList);
        localRequest.a(this);
        try
        {
          CacheDispatcher.a(b).put(localRequest);
        }
        catch (InterruptedException localInterruptedException)
        {
          VolleyLog.e("Couldn't add request to queue. %s", new Object[] { localInterruptedException.toString() });
          Thread.currentThread().interrupt();
          b.quit();
        }
      }
    }
    
    private synchronized boolean b(Request<?> paramRequest)
    {
      String str = paramRequest.getCacheKey();
      if (a.containsKey(str))
      {
        Object localObject = (List)a.get(str);
        if (localObject == null) {
          localObject = new ArrayList();
        }
        paramRequest.addMarker("waiting-for-response");
        ((List)localObject).add(paramRequest);
        a.put(str, localObject);
        if (VolleyLog.DEBUG) {
          VolleyLog.d("Request for cacheKey=%s is in flight, putting on hold.", new Object[] { str });
        }
        return true;
      }
      a.put(str, null);
      paramRequest.a(this);
      if (VolleyLog.DEBUG) {
        VolleyLog.d("new request, sending to network %s", new Object[] { str });
      }
      return false;
    }
  }
}
