package com.android.volley;

public abstract interface RetryPolicy
{
  public abstract int getCurrentTimeout();
  
  public abstract int getCurrentRetryCount();
  
  public abstract void retry(VolleyError paramVolleyError)
    throws VolleyError;
}
