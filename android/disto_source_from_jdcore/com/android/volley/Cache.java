package com.android.volley;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract interface Cache
{
  public abstract Entry get(String paramString);
  
  public abstract void put(String paramString, Entry paramEntry);
  
  public abstract void initialize();
  
  public abstract void invalidate(String paramString, boolean paramBoolean);
  
  public abstract void remove(String paramString);
  
  public abstract void clear();
  
  public static class Entry
  {
    public byte[] data;
    public String etag;
    public long serverDate;
    public long lastModified;
    public long ttl;
    public long softTtl;
    public Map<String, String> responseHeaders = Collections.emptyMap();
    public List<Header> allResponseHeaders;
    
    public Entry() {}
    
    public boolean isExpired()
    {
      return ttl < System.currentTimeMillis();
    }
    
    public boolean refreshNeeded()
    {
      return softTtl < System.currentTimeMillis();
    }
  }
}
