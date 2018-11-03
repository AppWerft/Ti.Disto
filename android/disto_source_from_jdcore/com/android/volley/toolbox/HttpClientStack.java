package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

@Deprecated
public class HttpClientStack
  implements HttpStack
{
  protected final HttpClient mClient;
  
  public HttpClientStack(HttpClient paramHttpClient)
  {
    mClient = paramHttpClient;
  }
  
  private static void a(HttpUriRequest paramHttpUriRequest, Map<String, String> paramMap)
  {
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      paramHttpUriRequest.setHeader(str, (String)paramMap.get(str));
    }
  }
  
  public HttpResponse performRequest(Request<?> paramRequest, Map<String, String> paramMap)
    throws IOException, AuthFailureError
  {
    HttpUriRequest localHttpUriRequest = a(paramRequest, paramMap);
    a(localHttpUriRequest, paramMap);
    a(localHttpUriRequest, paramRequest.getHeaders());
    onPrepareRequest(localHttpUriRequest);
    HttpParams localHttpParams = localHttpUriRequest.getParams();
    int i = paramRequest.getTimeoutMs();
    HttpConnectionParams.setConnectionTimeout(localHttpParams, 5000);
    HttpConnectionParams.setSoTimeout(localHttpParams, i);
    return mClient.execute(localHttpUriRequest);
  }
  
  static HttpUriRequest a(Request<?> paramRequest, Map<String, String> paramMap)
    throws AuthFailureError
  {
    Object localObject;
    switch (paramRequest.getMethod())
    {
    case -1: 
      localObject = paramRequest.getPostBody();
      if (localObject != null)
      {
        HttpPost localHttpPost = new HttpPost(paramRequest.getUrl());
        localHttpPost.addHeader("Content-Type", paramRequest.getPostBodyContentType());
        ByteArrayEntity localByteArrayEntity = new ByteArrayEntity((byte[])localObject);
        localHttpPost.setEntity(localByteArrayEntity);
        return localHttpPost;
      }
      return new HttpGet(paramRequest.getUrl());
    case 0: 
      return new HttpGet(paramRequest.getUrl());
    case 3: 
      return new HttpDelete(paramRequest.getUrl());
    case 1: 
      localObject = new HttpPost(paramRequest.getUrl());
      ((HttpPost)localObject).addHeader("Content-Type", paramRequest.getBodyContentType());
      a((HttpEntityEnclosingRequestBase)localObject, paramRequest);
      return localObject;
    case 2: 
      localObject = new HttpPut(paramRequest.getUrl());
      ((HttpPut)localObject).addHeader("Content-Type", paramRequest.getBodyContentType());
      a((HttpEntityEnclosingRequestBase)localObject, paramRequest);
      return localObject;
    case 4: 
      return new HttpHead(paramRequest.getUrl());
    case 5: 
      return new HttpOptions(paramRequest.getUrl());
    case 6: 
      return new HttpTrace(paramRequest.getUrl());
    case 7: 
      localObject = new HttpPatch(paramRequest.getUrl());
      ((HttpPatch)localObject).addHeader("Content-Type", paramRequest.getBodyContentType());
      a((HttpEntityEnclosingRequestBase)localObject, paramRequest);
      return localObject;
    }
    throw new IllegalStateException("Unknown request method.");
  }
  
  private static void a(HttpEntityEnclosingRequestBase paramHttpEntityEnclosingRequestBase, Request<?> paramRequest)
    throws AuthFailureError
  {
    byte[] arrayOfByte = paramRequest.getBody();
    if (arrayOfByte != null)
    {
      ByteArrayEntity localByteArrayEntity = new ByteArrayEntity(arrayOfByte);
      paramHttpEntityEnclosingRequestBase.setEntity(localByteArrayEntity);
    }
  }
  
  protected void onPrepareRequest(HttpUriRequest paramHttpUriRequest)
    throws IOException
  {}
  
  public static final class HttpPatch
    extends HttpEntityEnclosingRequestBase
  {
    public static final String METHOD_NAME = "PATCH";
    
    public HttpPatch() {}
    
    public HttpPatch(URI paramURI)
    {
      setURI(paramURI);
    }
    
    public HttpPatch(String paramString)
    {
      setURI(URI.create(paramString));
    }
    
    public String getMethod()
    {
      return "PATCH";
    }
  }
}
