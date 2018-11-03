package com.android.volley;

public class ParseError
  extends VolleyError
{
  public ParseError() {}
  
  public ParseError(NetworkResponse paramNetworkResponse)
  {
    super(paramNetworkResponse);
  }
  
  public ParseError(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}
