package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.Header;
import com.android.volley.Request;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class HurlStack
  extends BaseHttpStack
{
  private final UrlRewriter a;
  private final SSLSocketFactory b;
  
  public HurlStack()
  {
    this(null);
  }
  
  public HurlStack(UrlRewriter paramUrlRewriter)
  {
    this(paramUrlRewriter, null);
  }
  
  public HurlStack(UrlRewriter paramUrlRewriter, SSLSocketFactory paramSSLSocketFactory)
  {
    a = paramUrlRewriter;
    b = paramSSLSocketFactory;
  }
  
  public HttpResponse executeRequest(Request<?> paramRequest, Map<String, String> paramMap)
    throws IOException, AuthFailureError
  {
    Object localObject1 = paramRequest.getUrl();
    HashMap localHashMap = new HashMap();
    localHashMap.putAll(paramRequest.getHeaders());
    localHashMap.putAll(paramMap);
    if (a != null)
    {
      localObject2 = a.rewriteUrl((String)localObject1);
      if (localObject2 == null) {
        throw new IOException("URL blocked by rewriter: " + (String)localObject1);
      }
      localObject1 = localObject2;
    }
    Object localObject2 = new URL((String)localObject1);
    HttpURLConnection localHttpURLConnection = a((URL)localObject2, paramRequest);
    Iterator localIterator = localHashMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localHttpURLConnection.addRequestProperty(str, (String)localHashMap.get(str));
    }
    a(localHttpURLConnection, paramRequest);
    int i = localHttpURLConnection.getResponseCode();
    if (i == -1) {
      throw new IOException("Could not retrieve response code from HttpUrlConnection.");
    }
    if (!a(paramRequest.getMethod(), i)) {
      return new HttpResponse(i, a(localHttpURLConnection.getHeaderFields()));
    }
    return new HttpResponse(i, a(localHttpURLConnection.getHeaderFields()), localHttpURLConnection.getContentLength(), a(localHttpURLConnection));
  }
  
  static List<Header> a(Map<String, List<String>> paramMap)
  {
    ArrayList localArrayList = new ArrayList(paramMap.size());
    Iterator localIterator1 = paramMap.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      if (localEntry.getKey() != null)
      {
        Iterator localIterator2 = ((List)localEntry.getValue()).iterator();
        while (localIterator2.hasNext())
        {
          String str = (String)localIterator2.next();
          localArrayList.add(new Header((String)localEntry.getKey(), str));
        }
      }
    }
    return localArrayList;
  }
  
  private static boolean a(int paramInt1, int paramInt2)
  {
    return (paramInt1 != 4) && ((100 > paramInt2) || (paramInt2 >= 200)) && (paramInt2 != 204) && (paramInt2 != 304);
  }
  
  private static InputStream a(HttpURLConnection paramHttpURLConnection)
  {
    InputStream localInputStream;
    try
    {
      localInputStream = paramHttpURLConnection.getInputStream();
    }
    catch (IOException localIOException)
    {
      localInputStream = paramHttpURLConnection.getErrorStream();
    }
    return localInputStream;
  }
  
  protected HttpURLConnection createConnection(URL paramURL)
    throws IOException
  {
    HttpURLConnection localHttpURLConnection = (HttpURLConnection)paramURL.openConnection();
    localHttpURLConnection.setInstanceFollowRedirects(HttpURLConnection.getFollowRedirects());
    return localHttpURLConnection;
  }
  
  private HttpURLConnection a(URL paramURL, Request<?> paramRequest)
    throws IOException
  {
    HttpURLConnection localHttpURLConnection = createConnection(paramURL);
    int i = paramRequest.getTimeoutMs();
    localHttpURLConnection.setConnectTimeout(i);
    localHttpURLConnection.setReadTimeout(i);
    localHttpURLConnection.setUseCaches(false);
    localHttpURLConnection.setDoInput(true);
    if (("https".equals(paramURL.getProtocol())) && (b != null)) {
      ((HttpsURLConnection)localHttpURLConnection).setSSLSocketFactory(b);
    }
    return localHttpURLConnection;
  }
  
  static void a(HttpURLConnection paramHttpURLConnection, Request<?> paramRequest)
    throws IOException, AuthFailureError
  {
    switch (paramRequest.getMethod())
    {
    case -1: 
      byte[] arrayOfByte = paramRequest.getPostBody();
      if (arrayOfByte != null)
      {
        paramHttpURLConnection.setRequestMethod("POST");
        a(paramHttpURLConnection, paramRequest, arrayOfByte);
      }
      break;
    case 0: 
      paramHttpURLConnection.setRequestMethod("GET");
      break;
    case 3: 
      paramHttpURLConnection.setRequestMethod("DELETE");
      break;
    case 1: 
      paramHttpURLConnection.setRequestMethod("POST");
      b(paramHttpURLConnection, paramRequest);
      break;
    case 2: 
      paramHttpURLConnection.setRequestMethod("PUT");
      b(paramHttpURLConnection, paramRequest);
      break;
    case 4: 
      paramHttpURLConnection.setRequestMethod("HEAD");
      break;
    case 5: 
      paramHttpURLConnection.setRequestMethod("OPTIONS");
      break;
    case 6: 
      paramHttpURLConnection.setRequestMethod("TRACE");
      break;
    case 7: 
      paramHttpURLConnection.setRequestMethod("PATCH");
      b(paramHttpURLConnection, paramRequest);
      break;
    default: 
      throw new IllegalStateException("Unknown method type.");
    }
  }
  
  private static void b(HttpURLConnection paramHttpURLConnection, Request<?> paramRequest)
    throws IOException, AuthFailureError
  {
    byte[] arrayOfByte = paramRequest.getBody();
    if (arrayOfByte != null) {
      a(paramHttpURLConnection, paramRequest, arrayOfByte);
    }
  }
  
  private static void a(HttpURLConnection paramHttpURLConnection, Request<?> paramRequest, byte[] paramArrayOfByte)
    throws IOException, AuthFailureError
  {
    paramHttpURLConnection.setDoOutput(true);
    paramHttpURLConnection.addRequestProperty("Content-Type", paramRequest.getBodyContentType());
    DataOutputStream localDataOutputStream = new DataOutputStream(paramHttpURLConnection.getOutputStream());
    localDataOutputStream.write(paramArrayOfByte);
    localDataOutputStream.close();
  }
  
  public static abstract interface UrlRewriter
  {
    public abstract String rewriteUrl(String paramString);
  }
}
