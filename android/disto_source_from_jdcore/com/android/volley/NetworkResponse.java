package com.android.volley;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class NetworkResponse
{
  public final int statusCode;
  public final byte[] data;
  public final Map<String, String> headers;
  public final List<Header> allHeaders;
  public final boolean notModified;
  public final long networkTimeMs;
  
  @Deprecated
  public NetworkResponse(int paramInt, byte[] paramArrayOfByte, Map<String, String> paramMap, boolean paramBoolean, long paramLong)
  {
    this(paramInt, paramArrayOfByte, paramMap, a(paramMap), paramBoolean, paramLong);
  }
  
  public NetworkResponse(int paramInt, byte[] paramArrayOfByte, boolean paramBoolean, long paramLong, List<Header> paramList)
  {
    this(paramInt, paramArrayOfByte, a(paramList), paramList, paramBoolean, paramLong);
  }
  
  @Deprecated
  public NetworkResponse(int paramInt, byte[] paramArrayOfByte, Map<String, String> paramMap, boolean paramBoolean)
  {
    this(paramInt, paramArrayOfByte, paramMap, paramBoolean, 0L);
  }
  
  public NetworkResponse(byte[] paramArrayOfByte)
  {
    this(200, paramArrayOfByte, false, 0L, Collections.emptyList());
  }
  
  @Deprecated
  public NetworkResponse(byte[] paramArrayOfByte, Map<String, String> paramMap)
  {
    this(200, paramArrayOfByte, paramMap, false, 0L);
  }
  
  private NetworkResponse(int paramInt, byte[] paramArrayOfByte, Map<String, String> paramMap, List<Header> paramList, boolean paramBoolean, long paramLong)
  {
    statusCode = paramInt;
    data = paramArrayOfByte;
    headers = paramMap;
    if (paramList == null) {
      allHeaders = null;
    } else {
      allHeaders = Collections.unmodifiableList(paramList);
    }
    notModified = paramBoolean;
    networkTimeMs = paramLong;
  }
  
  private static Map<String, String> a(List<Header> paramList)
  {
    if (paramList == null) {
      return null;
    }
    if (paramList.isEmpty()) {
      return Collections.emptyMap();
    }
    TreeMap localTreeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Header localHeader = (Header)localIterator.next();
      localTreeMap.put(localHeader.getName(), localHeader.getValue());
    }
    return localTreeMap;
  }
  
  private static List<Header> a(Map<String, String> paramMap)
  {
    if (paramMap == null) {
      return null;
    }
    if (paramMap.isEmpty()) {
      return Collections.emptyList();
    }
    ArrayList localArrayList = new ArrayList(paramMap.size());
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localArrayList.add(new Header((String)localEntry.getKey(), (String)localEntry.getValue()));
    }
    return localArrayList;
  }
}
