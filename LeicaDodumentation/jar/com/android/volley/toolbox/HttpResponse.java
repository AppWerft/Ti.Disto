package com.android.volley.toolbox;

import com.android.volley.Header;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public final class HttpResponse
{
  private final int a;
  private final List<Header> b;
  private final int c;
  private final InputStream d;
  
  public HttpResponse(int paramInt, List<Header> paramList)
  {
    this(paramInt, paramList, -1, null);
  }
  
  public HttpResponse(int paramInt1, List<Header> paramList, int paramInt2, InputStream paramInputStream)
  {
    a = paramInt1;
    b = paramList;
    c = paramInt2;
    d = paramInputStream;
  }
  
  public final int getStatusCode()
  {
    return a;
  }
  
  public final List<Header> getHeaders()
  {
    return Collections.unmodifiableList(b);
  }
  
  public final int getContentLength()
  {
    return c;
  }
  
  public final InputStream getContent()
  {
    return d;
  }
}
