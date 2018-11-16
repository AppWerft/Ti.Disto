package com.android.volley;

import android.text.TextUtils;

public final class Header
{
  private final String a;
  private final String b;
  
  public Header(String paramString1, String paramString2)
  {
    a = paramString1;
    b = paramString2;
  }
  
  public final String getName()
  {
    return a;
  }
  
  public final String getValue()
  {
    return b;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    Header localHeader = (Header)paramObject;
    return (TextUtils.equals(a, a)) && (TextUtils.equals(b, b));
  }
  
  public int hashCode()
  {
    int i = a.hashCode();
    i = 31 * i + b.hashCode();
    return i;
  }
  
  public String toString()
  {
    return "Header[name=" + a + ",value=" + b + "]";
  }
}
