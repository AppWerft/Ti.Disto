package com.android.volley;

import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestQueue
{
  private final AtomicInteger a = new AtomicInteger();
  private final Set<Request<?>> b = new HashSet();
  private final PriorityBlockingQueue<Request<?>> c = new PriorityBlockingQueue();
  private final PriorityBlockingQueue<Request<?>> d = new PriorityBlockingQueue();
  private final Cache e;
  private final Network f;
  private final ResponseDelivery g;
  private final NetworkDispatcher[] h;
  private CacheDispatcher i;
  private final List<RequestFinishedListener> j = new ArrayList();
  
  public RequestQueue(Cache paramCache, Network paramNetwork, int paramInt, ResponseDelivery paramResponseDelivery)
  {
    e = paramCache;
    f = paramNetwork;
    h = new NetworkDispatcher[paramInt];
    g = paramResponseDelivery;
  }
  
  public RequestQueue(Cache paramCache, Network paramNetwork, int paramInt)
  {
    this(paramCache, paramNetwork, paramInt, new ExecutorDelivery(new Handler(Looper.getMainLooper())));
  }
  
  public RequestQueue(Cache paramCache, Network paramNetwork)
  {
    this(paramCache, paramNetwork, 4);
  }
  
  public void start()
  {
    stop();
    i = new CacheDispatcher(c, d, e, g);
    i.start();
    for (int k = 0; k < h.length; k++)
    {
      NetworkDispatcher localNetworkDispatcher = new NetworkDispatcher(d, f, e, g);
      h[k] = localNetworkDispatcher;
      localNetworkDispatcher.start();
    }
  }
  
  public void stop()
  {
    if (i != null) {
      i.quit();
    }
    for (NetworkDispatcher localNetworkDispatcher : h) {
      if (localNetworkDispatcher != null) {
        localNetworkDispatcher.quit();
      }
    }
  }
  
  public int getSequenceNumber()
  {
    return a.incrementAndGet();
  }
  
  public Cache getCache()
  {
    return e;
  }
  
  public void cancelAll(RequestFilter paramRequestFilter)
  {
    synchronized (b)
    {
      Iterator localIterator = b.iterator();
      while (localIterator.hasNext())
      {
        Request localRequest = (Request)localIterator.next();
        if (paramRequestFilter.apply(localRequest)) {
          localRequest.cancel();
        }
      }
    }
  }
  
  public void cancelAll(final Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("Cannot cancelAll with a null tag");
    }
    cancelAll(new RequestFilter()
    {
      public boolean apply(Request<?> paramAnonymousRequest)
      {
        return paramAnonymousRequest.getTag() == paramObject;
      }
    });
  }
  
  public <T> Request<T> add(Request<T> paramRequest)
  {
    paramRequest.setRequestQueue(this);
    synchronized (b)
    {
      b.add(paramRequest);
    }
    paramRequest.setSequence(getSequenceNumber());
    paramRequest.addMarker("add-to-queue");
    if (!paramRequest.shouldCache())
    {
      d.add(paramRequest);
      return paramRequest;
    }
    c.add(paramRequest);
    return paramRequest;
  }
  
  <T> void a(Request<T> paramRequest)
  {
    synchronized (b)
    {
      b.remove(paramRequest);
    }
    synchronized (j)
    {
      Iterator localIterator = j.iterator();
      while (localIterator.hasNext())
      {
        RequestFinishedListener localRequestFinishedListener = (RequestFinishedListener)localIterator.next();
        localRequestFinishedListener.onRequestFinished(paramRequest);
      }
    }
  }
  
  public <T> void addRequestFinishedListener(RequestFinishedListener<T> paramRequestFinishedListener)
  {
    synchronized (j)
    {
      j.add(paramRequestFinishedListener);
    }
  }
  
  public <T> void removeRequestFinishedListener(RequestFinishedListener<T> paramRequestFinishedListener)
  {
    synchronized (j)
    {
      j.remove(paramRequestFinishedListener);
    }
  }
  
  public static abstract interface RequestFilter
  {
    public abstract boolean apply(Request<?> paramRequest);
  }
  
  public static abstract interface RequestFinishedListener<T>
  {
    public abstract void onRequestFinished(Request<T> paramRequest);
  }
}
