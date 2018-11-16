package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

public abstract class BaseHttpStack
  implements HttpStack
{
  public BaseHttpStack() {}
  
  public abstract HttpResponse executeRequest(Request<?> paramRequest, Map<String, String> paramMap)
    throws IOException, AuthFailureError;
  
  @Deprecated
  public final org.apache.http.HttpResponse performRequest(Request<?> paramRequest, Map<String, String> paramMap)
    throws IOException, AuthFailureError
  {
    HttpResponse localHttpResponse = executeRequest(paramRequest, paramMap);
    ProtocolVersion localProtocolVersion = new ProtocolVersion("HTTP", 1, 1);
    BasicStatusLine localBasicStatusLine = new BasicStatusLine(localProtocolVersion, localHttpResponse.getStatusCode(), "");
    BasicHttpResponse localBasicHttpResponse = new BasicHttpResponse(localBasicStatusLine);
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = localHttpResponse.getHeaders().iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (com.android.volley.Header)((Iterator)localObject1).next();
      localArrayList.add(new BasicHeader(((com.android.volley.Header)localObject2).getName(), ((com.android.volley.Header)localObject2).getValue()));
    }
    localBasicHttpResponse.setHeaders((org.apache.http.Header[])localArrayList.toArray(new org.apache.http.Header[localArrayList.size()]));
    localObject1 = localHttpResponse.getContent();
    if (localObject1 != null)
    {
      localObject2 = new BasicHttpEntity();
      ((BasicHttpEntity)localObject2).setContent((InputStream)localObject1);
      ((BasicHttpEntity)localObject2).setContentLength(localHttpResponse.getContentLength());
      localBasicHttpResponse.setEntity((HttpEntity)localObject2);
    }
    return localBasicHttpResponse;
  }
}
