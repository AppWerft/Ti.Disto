package com.android.volley;

public class DefaultRetryPolicy
  implements RetryPolicy
{
  private int a;
  private int b;
  private final int c;
  private final float d;
  public static final int DEFAULT_TIMEOUT_MS = 2500;
  public static final int DEFAULT_MAX_RETRIES = 1;
  public static final float DEFAULT_BACKOFF_MULT = 1.0F;
  
  public DefaultRetryPolicy()
  {
    this(2500, 1, 1.0F);
  }
  
  public DefaultRetryPolicy(int paramInt1, int paramInt2, float paramFloat)
  {
    a = paramInt1;
    c = paramInt2;
    d = paramFloat;
  }
  
  public int getCurrentTimeout()
  {
    return a;
  }
  
  public int getCurrentRetryCount()
  {
    return b;
  }
  
  public float getBackoffMultiplier()
  {
    return d;
  }
  
  public void retry(VolleyError paramVolleyError)
    throws VolleyError
  {
    b += 1;
    a = ((int)(a + a * d));
    if (!hasAttemptRemaining()) {
      throw paramVolleyError;
    }
  }
  
  protected boolean hasAttemptRemaining()
  {
    return b <= c;
  }
}
