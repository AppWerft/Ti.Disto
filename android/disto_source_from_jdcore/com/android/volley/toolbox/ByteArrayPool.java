package com.android.volley.toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ByteArrayPool
{
  private final List<byte[]> a = new LinkedList();
  private final List<byte[]> b = new ArrayList(64);
  private int c = 0;
  private final int d;
  protected static final Comparator<byte[]> BUF_COMPARATOR = new Comparator()
  {
    public int a(byte[] paramAnonymousArrayOfByte1, byte[] paramAnonymousArrayOfByte2)
    {
      return paramAnonymousArrayOfByte1.length - paramAnonymousArrayOfByte2.length;
    }
  };
  
  public ByteArrayPool(int paramInt)
  {
    d = paramInt;
  }
  
  public synchronized byte[] getBuf(int paramInt)
  {
    for (int i = 0; i < b.size(); i++)
    {
      byte[] arrayOfByte = (byte[])b.get(i);
      if (arrayOfByte.length >= paramInt)
      {
        c -= arrayOfByte.length;
        b.remove(i);
        a.remove(arrayOfByte);
        return arrayOfByte;
      }
    }
    return new byte[paramInt];
  }
  
  public synchronized void returnBuf(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length > d)) {
      return;
    }
    a.add(paramArrayOfByte);
    int i = Collections.binarySearch(b, paramArrayOfByte, BUF_COMPARATOR);
    if (i < 0) {
      i = -i - 1;
    }
    b.add(i, paramArrayOfByte);
    c += paramArrayOfByte.length;
    a();
  }
  
  private synchronized void a()
  {
    while (c > d)
    {
      byte[] arrayOfByte = (byte[])a.remove(0);
      b.remove(arrayOfByte);
      c -= arrayOfByte.length;
    }
  }
}
