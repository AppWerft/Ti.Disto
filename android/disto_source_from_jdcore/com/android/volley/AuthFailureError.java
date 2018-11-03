package com.android.volley;

import android.content.Intent;

public class AuthFailureError
  extends VolleyError
{
  private Intent a;
  
  public AuthFailureError() {}
  
  public AuthFailureError(Intent paramIntent)
  {
    a = paramIntent;
  }
  
  public AuthFailureError(NetworkResponse paramNetworkResponse)
  {
    super(paramNetworkResponse);
  }
  
  public AuthFailureError(String paramString)
  {
    super(paramString);
  }
  
  public AuthFailureError(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
  
  public Intent getResolutionIntent()
  {
    return a;
  }
  
  public String getMessage()
  {
    if (a != null) {
      return "User needs to (re)enter credentials.";
    }
    return super.getMessage();
  }
}
