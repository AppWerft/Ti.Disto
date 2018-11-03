package com.android.volley;

import android.os.Handler;
import java.util.concurrent.Executor;

public class ExecutorDelivery
  implements ResponseDelivery
{
  private final Executor a;
  
  public ExecutorDelivery(final Handler paramHandler)
  {
    a = new Executor()
    {
      public void execute(Runnable paramAnonymousRunnable)
      {
        paramHandler.post(paramAnonymousRunnable);
      }
    };
  }
  
  public ExecutorDelivery(Executor paramExecutor)
  {
    a = paramExecutor;
  }
  
  public void postResponse(Request<?> paramRequest, Response<?> paramResponse)
  {
    postResponse(paramRequest, paramResponse, null);
  }
  
  public void postResponse(Request<?> paramRequest, Response<?> paramResponse, Runnable paramRunnable)
  {
    paramRequest.markDelivered();
    paramRequest.addMarker("post-response");
    a.execute(new a(paramRequest, paramResponse, paramRunnable));
  }
  
  public void postError(Request<?> paramRequest, VolleyError paramVolleyError)
  {
    paramRequest.addMarker("post-error");
    Response localResponse = Response.error(paramVolleyError);
    a.execute(new a(paramRequest, localResponse, null));
  }
  
  private class a
    implements Runnable
  {
    private final Request b;
    private final Response c;
    private final Runnable d;
    
    public a(Request paramRequest, Response paramResponse, Runnable paramRunnable)
    {
      b = paramRequest;
      c = paramResponse;
      d = paramRunnable;
    }
    
    public void run()
    {
      if (b.isCanceled())
      {
        b.a("canceled-at-delivery");
        return;
      }
      if (c.isSuccess()) {
        b.deliverResponse(c.result);
      } else {
        b.deliverError(c.error);
      }
      if (c.intermediate) {
        b.addMarker("intermediate-response");
      } else {
        b.a("done");
      }
      if (d != null) {
        d.run();
      }
    }
  }
}
