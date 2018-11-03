package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;

class a
  extends BaseHttpStack
{
  private final HttpStack a;
  
  a(HttpStack paramHttpStack)
  {
    a = paramHttpStack;
  }
  
  public HttpResponse executeRequest(Request<?> paramRequest, Map<String, String> paramMap)
    throws IOException, AuthFailureError
  {
    org.apache.http.HttpResponse localHttpResponse;
    try
    {
      localHttpResponse = a.performRequest(paramRequest, paramMap);
    }
    catch (ConnectTimeoutException localConnectTimeoutException)
    {
      throw new SocketTimeoutException(localConnectTimeoutException.getMessage());
    }
    int i = localHttpResponse.getStatusLine().getStatusCode();
    org.apache.http.Header[] arrayOfHeader1 = localHttpResponse.getAllHeaders();
    ArrayList localArrayList = new ArrayList(arrayOfHeader1.length);
    for (org.apache.http.Header localHeader : arrayOfHeader1) {
      localArrayList.add(new com.android.volley.Header(localHeader.getName(), localHeader.getValue()));
    }
    if (localHttpResponse.getEntity() == null) {
      return new HttpResponse(i, localArrayList);
    }
    long l = localHttpResponse.getEntity().getContentLength();
    if ((int)l != l) {
      throw new IOException("Response too large: " + l);
    }
    return new HttpResponse(i, localArrayList, (int)localHttpResponse.getEntity().getContentLength(), localHttpResponse.getEntity().getContent());
  }
}
