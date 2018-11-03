package com.android.volley;

public class ClientError
  extends ServerError
{
  public ClientError(NetworkResponse paramNetworkResponse)
  {
    super(paramNetworkResponse);
  }
  
  public ClientError() {}
}
