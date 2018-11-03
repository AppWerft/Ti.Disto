package com.android.volley.toolbox;

import android.os.SystemClock;
import com.android.volley.AuthFailureError;
import com.android.volley.Cache.Entry;
import com.android.volley.ClientError;
import com.android.volley.Header;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class BasicNetwork
  implements Network
{
  protected static final boolean DEBUG = VolleyLog.DEBUG;
  @Deprecated
  protected final HttpStack mHttpStack;
  private final BaseHttpStack a;
  protected final ByteArrayPool mPool;
  
  @Deprecated
  public BasicNetwork(HttpStack paramHttpStack)
  {
    this(paramHttpStack, new ByteArrayPool(4096));
  }
  
  @Deprecated
  public BasicNetwork(HttpStack paramHttpStack, ByteArrayPool paramByteArrayPool)
  {
    mHttpStack = paramHttpStack;
    a = new a(paramHttpStack);
    mPool = paramByteArrayPool;
  }
  
  public BasicNetwork(BaseHttpStack paramBaseHttpStack)
  {
    this(paramBaseHttpStack, new ByteArrayPool(4096));
  }
  
  public BasicNetwork(BaseHttpStack paramBaseHttpStack, ByteArrayPool paramByteArrayPool)
  {
    a = paramBaseHttpStack;
    mHttpStack = paramBaseHttpStack;
    mPool = paramByteArrayPool;
  }
  
  public NetworkResponse performRequest(Request<?> paramRequest)
    throws VolleyError
  {
    long l1 = SystemClock.elapsedRealtime();
    for (;;)
    {
      HttpResponse localHttpResponse = null;
      byte[] arrayOfByte = null;
      List localList1 = Collections.emptyList();
      int i;
      Object localObject;
      try
      {
        Map localMap = a(paramRequest.getCacheEntry());
        localHttpResponse = a.executeRequest(paramRequest, localMap);
        i = localHttpResponse.getStatusCode();
        localList1 = localHttpResponse.getHeaders();
        if (i == 304)
        {
          localObject = paramRequest.getCacheEntry();
          if (localObject == null) {
            return new NetworkResponse(304, null, true, SystemClock.elapsedRealtime() - l1, localList1);
          }
          List localList2 = a(localList1, (Cache.Entry)localObject);
          return new NetworkResponse(304, data, true, SystemClock.elapsedRealtime() - l1, localList2);
        }
        localObject = localHttpResponse.getContent();
        if (localObject != null) {
          arrayOfByte = a((InputStream)localObject, localHttpResponse.getContentLength());
        } else {
          arrayOfByte = new byte[0];
        }
        long l2 = SystemClock.elapsedRealtime() - l1;
        a(l2, paramRequest, arrayOfByte, i);
        if ((i < 200) || (i > 299)) {
          throw new IOException();
        }
        return new NetworkResponse(i, arrayOfByte, false, SystemClock.elapsedRealtime() - l1, localList1);
      }
      catch (SocketTimeoutException localSocketTimeoutException)
      {
        a("socket", paramRequest, new TimeoutError());
      }
      catch (MalformedURLException localMalformedURLException)
      {
        throw new RuntimeException("Bad URL " + paramRequest.getUrl(), localMalformedURLException);
      }
      catch (IOException localIOException)
      {
        if (localHttpResponse != null) {
          i = localHttpResponse.getStatusCode();
        } else {
          throw new NoConnectionError(localIOException);
        }
      }
      VolleyLog.e("Unexpected response code %d for %s", new Object[] { Integer.valueOf(i), paramRequest.getUrl() });
      if (arrayOfByte != null)
      {
        localObject = new NetworkResponse(i, arrayOfByte, false, SystemClock.elapsedRealtime() - l1, localList1);
        if ((i == 401) || (i == 403))
        {
          a("auth", paramRequest, new AuthFailureError((NetworkResponse)localObject));
        }
        else
        {
          if ((i >= 400) && (i <= 499)) {
            throw new ClientError((NetworkResponse)localObject);
          }
          if ((i >= 500) && (i <= 599))
          {
            if (paramRequest.shouldRetryServerErrors()) {
              a("server", paramRequest, new ServerError((NetworkResponse)localObject));
            } else {
              throw new ServerError((NetworkResponse)localObject);
            }
          }
          else {
            throw new ServerError((NetworkResponse)localObject);
          }
        }
      }
      else
      {
        a("network", paramRequest, new NetworkError());
      }
    }
  }
  
  private void a(long paramLong, Request<?> paramRequest, byte[] paramArrayOfByte, int paramInt)
  {
    if ((DEBUG) || (paramLong > 3000L)) {
      VolleyLog.d("HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]", new Object[] { paramRequest, Long.valueOf(paramLong), paramArrayOfByte != null ? Integer.valueOf(paramArrayOfByte.length) : "null", Integer.valueOf(paramInt), Integer.valueOf(paramRequest.getRetryPolicy().getCurrentRetryCount()) });
    }
  }
  
  private static void a(String paramString, Request<?> paramRequest, VolleyError paramVolleyError)
    throws VolleyError
  {
    RetryPolicy localRetryPolicy = paramRequest.getRetryPolicy();
    int i = paramRequest.getTimeoutMs();
    try
    {
      localRetryPolicy.retry(paramVolleyError);
    }
    catch (VolleyError localVolleyError)
    {
      paramRequest.addMarker(String.format("%s-timeout-giveup [timeout=%s]", new Object[] { paramString, Integer.valueOf(i) }));
      throw localVolleyError;
    }
    paramRequest.addMarker(String.format("%s-retry [timeout=%s]", new Object[] { paramString, Integer.valueOf(i) }));
  }
  
  private Map<String, String> a(Cache.Entry paramEntry)
  {
    if (paramEntry == null) {
      return Collections.emptyMap();
    }
    HashMap localHashMap = new HashMap();
    if (etag != null) {
      localHashMap.put("If-None-Match", etag);
    }
    if (lastModified > 0L) {
      localHashMap.put("If-Modified-Since", HttpHeaderParser.a(lastModified));
    }
    return localHashMap;
  }
  
  protected void logError(String paramString1, String paramString2, long paramLong)
  {
    long l = SystemClock.elapsedRealtime();
    VolleyLog.v("HTTP ERROR(%s) %d ms to fetch %s", new Object[] { paramString1, Long.valueOf(l - paramLong), paramString2 });
  }
  
  private byte[] a(InputStream paramInputStream, int paramInt)
    throws IOException, ServerError
  {
    PoolingByteArrayOutputStream localPoolingByteArrayOutputStream = new PoolingByteArrayOutputStream(mPool, paramInt);
    byte[] arrayOfByte1 = null;
    try
    {
      if (paramInputStream == null) {
        throw new ServerError();
      }
      arrayOfByte1 = mPool.getBuf(1024);
      int i;
      while ((i = paramInputStream.read(arrayOfByte1)) != -1) {
        localPoolingByteArrayOutputStream.write(arrayOfByte1, 0, i);
      }
      byte[] arrayOfByte2 = localPoolingByteArrayOutputStream.toByteArray();
      return arrayOfByte2;
    }
    finally
    {
      try
      {
        if (paramInputStream != null) {
          paramInputStream.close();
        }
      }
      catch (IOException localIOException2)
      {
        VolleyLog.v("Error occurred when closing InputStream", new Object[0]);
      }
      mPool.returnBuf(arrayOfByte1);
      localPoolingByteArrayOutputStream.close();
    }
  }
  
  @Deprecated
  protected static Map<String, String> convertHeaders(Header[] paramArrayOfHeader)
  {
    TreeMap localTreeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    for (int i = 0; i < paramArrayOfHeader.length; i++) {
      localTreeMap.put(paramArrayOfHeader[i].getName(), paramArrayOfHeader[i].getValue());
    }
    return localTreeMap;
  }
  
  private static List<Header> a(List<Header> paramList, Cache.Entry paramEntry)
  {
    TreeSet localTreeSet = new TreeSet(String.CASE_INSENSITIVE_ORDER);
    Object localObject2;
    if (!paramList.isEmpty())
    {
      localObject1 = paramList.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Header)((Iterator)localObject1).next();
        localTreeSet.add(((Header)localObject2).getName());
      }
    }
    Object localObject1 = new ArrayList(paramList);
    Object localObject3;
    if (allResponseHeaders != null)
    {
      if (!allResponseHeaders.isEmpty())
      {
        localObject2 = allResponseHeaders.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (Header)((Iterator)localObject2).next();
          if (!localTreeSet.contains(((Header)localObject3).getName())) {
            ((List)localObject1).add(localObject3);
          }
        }
      }
    }
    else if (!responseHeaders.isEmpty())
    {
      localObject2 = responseHeaders.entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Map.Entry)((Iterator)localObject2).next();
        if (!localTreeSet.contains(((Map.Entry)localObject3).getKey())) {
          ((List)localObject1).add(new Header((String)((Map.Entry)localObject3).getKey(), (String)((Map.Entry)localObject3).getValue()));
        }
      }
    }
    return localObject1;
  }
}
