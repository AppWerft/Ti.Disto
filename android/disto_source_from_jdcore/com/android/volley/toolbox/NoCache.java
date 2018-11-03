package com.android.volley.toolbox;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;

public class NoCache
  implements Cache
{
  public NoCache() {}
  
  public void clear() {}
  
  public Cache.Entry get(String paramString)
  {
    return null;
  }
  
  public void put(String paramString, Cache.Entry paramEntry) {}
  
  public void invalidate(String paramString, boolean paramBoolean) {}
  
  public void remove(String paramString) {}
  
  public void initialize() {}
}
