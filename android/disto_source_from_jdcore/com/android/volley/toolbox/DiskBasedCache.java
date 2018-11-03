package com.android.volley.toolbox;

import android.os.SystemClock;
import android.text.TextUtils;
import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Header;
import com.android.volley.VolleyLog;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DiskBasedCache
  implements Cache
{
  private final Map<String, a> a = new LinkedHashMap(16, 0.75F, true);
  private long b = 0L;
  private final File c;
  private final int d;
  
  public DiskBasedCache(File paramFile, int paramInt)
  {
    c = paramFile;
    d = paramInt;
  }
  
  public DiskBasedCache(File paramFile)
  {
    this(paramFile, 5242880);
  }
  
  public synchronized void clear()
  {
    File[] arrayOfFile1 = c.listFiles();
    if (arrayOfFile1 != null) {
      for (File localFile : arrayOfFile1) {
        localFile.delete();
      }
    }
    a.clear();
    b = 0L;
    VolleyLog.d("Cache cleared.", new Object[0]);
  }
  
  public synchronized Cache.Entry get(String paramString)
  {
    a localA1 = (a)a.get(paramString);
    if (localA1 == null) {
      return null;
    }
    File localFile = getFileForKey(paramString);
    try
    {
      b localB = new b(new BufferedInputStream(a(localFile)), localFile.length());
      try
      {
        a localA2 = a.a(localB);
        if (!TextUtils.equals(paramString, b))
        {
          VolleyLog.d("%s: key=%s, found=%s", new Object[] { localFile.getAbsolutePath(), paramString, b });
          b(paramString);
          localObject1 = null;
          return localObject1;
        }
        Object localObject1 = a(localB, localB.a());
        Cache.Entry localEntry = localA1.a((byte[])localObject1);
        return localEntry;
      }
      finally
      {
        localB.close();
      }
      return null;
    }
    catch (IOException localIOException)
    {
      VolleyLog.d("%s: %s", new Object[] { localFile.getAbsolutePath(), localIOException.toString() });
      remove(paramString);
    }
  }
  
  public synchronized void initialize()
  {
    if (!c.exists())
    {
      if (!c.mkdirs()) {
        VolleyLog.e("Unable to create cache dir %s", new Object[] { c.getAbsolutePath() });
      }
      return;
    }
    File[] arrayOfFile1 = c.listFiles();
    if (arrayOfFile1 == null) {
      return;
    }
    for (File localFile : arrayOfFile1) {
      try
      {
        long l = localFile.length();
        b localB = new b(new BufferedInputStream(a(localFile)), l);
        try
        {
          a localA = a.a(localB);
          a = l;
          a(b, localA);
        }
        finally
        {
          localB.close();
        }
      }
      catch (IOException localIOException)
      {
        localFile.delete();
      }
    }
  }
  
  public synchronized void invalidate(String paramString, boolean paramBoolean)
  {
    Cache.Entry localEntry = get(paramString);
    if (localEntry != null)
    {
      softTtl = 0L;
      if (paramBoolean) {
        ttl = 0L;
      }
      put(paramString, localEntry);
    }
  }
  
  public synchronized void put(String paramString, Cache.Entry paramEntry)
  {
    a(data.length);
    File localFile = getFileForKey(paramString);
    try
    {
      BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(b(localFile));
      a localA = new a(paramString, paramEntry);
      boolean bool2 = localA.a(localBufferedOutputStream);
      if (!bool2)
      {
        localBufferedOutputStream.close();
        VolleyLog.d("Failed to write header for %s", new Object[] { localFile.getAbsolutePath() });
        throw new IOException();
      }
      localBufferedOutputStream.write(data);
      localBufferedOutputStream.close();
      a(paramString, localA);
      return;
    }
    catch (IOException localIOException)
    {
      boolean bool1 = localFile.delete();
      if (!bool1) {
        VolleyLog.d("Could not clean up file %s", new Object[] { localFile.getAbsolutePath() });
      }
    }
  }
  
  public synchronized void remove(String paramString)
  {
    boolean bool = getFileForKey(paramString).delete();
    b(paramString);
    if (!bool) {
      VolleyLog.d("Could not delete cache entry for key=%s, filename=%s", new Object[] { paramString, a(paramString) });
    }
  }
  
  private String a(String paramString)
  {
    int i = paramString.length() / 2;
    String str = String.valueOf(paramString.substring(0, i).hashCode());
    str = str + String.valueOf(paramString.substring(i).hashCode());
    return str;
  }
  
  public File getFileForKey(String paramString)
  {
    return new File(c, a(paramString));
  }
  
  private void a(int paramInt)
  {
    if (b + paramInt < d) {
      return;
    }
    if (VolleyLog.DEBUG) {
      VolleyLog.v("Pruning old cache entries.", new Object[0]);
    }
    long l1 = b;
    int i = 0;
    long l2 = SystemClock.elapsedRealtime();
    Iterator localIterator = a.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      a localA = (a)localEntry.getValue();
      boolean bool = getFileForKey(b).delete();
      if (bool) {
        b -= a;
      } else {
        VolleyLog.d("Could not delete cache entry for key=%s, filename=%s", new Object[] { b, a(b) });
      }
      localIterator.remove();
      i++;
      if ((float)(b + paramInt) < d * 0.9F) {
        break;
      }
    }
    if (VolleyLog.DEBUG) {
      VolleyLog.v("pruned %d files, %d bytes, %d ms", new Object[] { Integer.valueOf(i), Long.valueOf(b - l1), Long.valueOf(SystemClock.elapsedRealtime() - l2) });
    }
  }
  
  private void a(String paramString, a paramA)
  {
    if (!a.containsKey(paramString))
    {
      b += a;
    }
    else
    {
      a localA = (a)a.get(paramString);
      b += a - a;
    }
    a.put(paramString, paramA);
  }
  
  private void b(String paramString)
  {
    a localA = (a)a.remove(paramString);
    if (localA != null) {
      b -= a;
    }
  }
  
  static byte[] a(b paramB, long paramLong)
    throws IOException
  {
    long l = paramB.a();
    if ((paramLong < 0L) || (paramLong > l) || ((int)paramLong != paramLong)) {
      throw new IOException("streamToBytes length=" + paramLong + ", maxLength=" + l);
    }
    byte[] arrayOfByte = new byte[(int)paramLong];
    new DataInputStream(paramB).readFully(arrayOfByte);
    return arrayOfByte;
  }
  
  InputStream a(File paramFile)
    throws FileNotFoundException
  {
    return new FileInputStream(paramFile);
  }
  
  OutputStream b(File paramFile)
    throws FileNotFoundException
  {
    return new FileOutputStream(paramFile);
  }
  
  private static int c(InputStream paramInputStream)
    throws IOException
  {
    int i = paramInputStream.read();
    if (i == -1) {
      throw new EOFException();
    }
    return i;
  }
  
  static void a(OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    paramOutputStream.write(paramInt >> 0 & 0xFF);
    paramOutputStream.write(paramInt >> 8 & 0xFF);
    paramOutputStream.write(paramInt >> 16 & 0xFF);
    paramOutputStream.write(paramInt >> 24 & 0xFF);
  }
  
  static int a(InputStream paramInputStream)
    throws IOException
  {
    int i = 0;
    i |= c(paramInputStream) << 0;
    i |= c(paramInputStream) << 8;
    i |= c(paramInputStream) << 16;
    i |= c(paramInputStream) << 24;
    return i;
  }
  
  static void a(OutputStream paramOutputStream, long paramLong)
    throws IOException
  {
    paramOutputStream.write((byte)(int)(paramLong >>> 0));
    paramOutputStream.write((byte)(int)(paramLong >>> 8));
    paramOutputStream.write((byte)(int)(paramLong >>> 16));
    paramOutputStream.write((byte)(int)(paramLong >>> 24));
    paramOutputStream.write((byte)(int)(paramLong >>> 32));
    paramOutputStream.write((byte)(int)(paramLong >>> 40));
    paramOutputStream.write((byte)(int)(paramLong >>> 48));
    paramOutputStream.write((byte)(int)(paramLong >>> 56));
  }
  
  static long b(InputStream paramInputStream)
    throws IOException
  {
    long l = 0L;
    l |= (c(paramInputStream) & 0xFF) << 0;
    l |= (c(paramInputStream) & 0xFF) << 8;
    l |= (c(paramInputStream) & 0xFF) << 16;
    l |= (c(paramInputStream) & 0xFF) << 24;
    l |= (c(paramInputStream) & 0xFF) << 32;
    l |= (c(paramInputStream) & 0xFF) << 40;
    l |= (c(paramInputStream) & 0xFF) << 48;
    l |= (c(paramInputStream) & 0xFF) << 56;
    return l;
  }
  
  static void a(OutputStream paramOutputStream, String paramString)
    throws IOException
  {
    byte[] arrayOfByte = paramString.getBytes("UTF-8");
    a(paramOutputStream, arrayOfByte.length);
    paramOutputStream.write(arrayOfByte, 0, arrayOfByte.length);
  }
  
  static String a(b paramB)
    throws IOException
  {
    long l = b(paramB);
    byte[] arrayOfByte = a(paramB, l);
    return new String(arrayOfByte, "UTF-8");
  }
  
  static void a(List<Header> paramList, OutputStream paramOutputStream)
    throws IOException
  {
    if (paramList != null)
    {
      a(paramOutputStream, paramList.size());
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        Header localHeader = (Header)localIterator.next();
        a(paramOutputStream, localHeader.getName());
        a(paramOutputStream, localHeader.getValue());
      }
    }
    else
    {
      a(paramOutputStream, 0);
    }
  }
  
  static List<Header> b(b paramB)
    throws IOException
  {
    int i = a(paramB);
    if (i < 0) {
      throw new IOException("readHeaderList size=" + i);
    }
    ArrayList localArrayList = i == 0 ? Collections.emptyList() : new ArrayList();
    for (int j = 0; j < i; j++)
    {
      String str1 = a(paramB).intern();
      String str2 = a(paramB).intern();
      localArrayList.add(new Header(str1, str2));
    }
    return localArrayList;
  }
  
  static class b
    extends FilterInputStream
  {
    private final long a;
    private long b;
    
    b(InputStream paramInputStream, long paramLong)
    {
      super();
      a = paramLong;
    }
    
    public int read()
      throws IOException
    {
      int i = super.read();
      if (i != -1) {
        b += 1L;
      }
      return i;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = super.read(paramArrayOfByte, paramInt1, paramInt2);
      if (i != -1) {
        b += i;
      }
      return i;
    }
    
    long a()
    {
      return a - b;
    }
  }
  
  static class a
  {
    long a;
    final String b;
    final String c;
    final long d;
    final long e;
    final long f;
    final long g;
    final List<Header> h;
    
    private a(String paramString1, String paramString2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, List<Header> paramList)
    {
      b = paramString1;
      c = ("".equals(paramString2) ? null : paramString2);
      d = paramLong1;
      e = paramLong2;
      f = paramLong3;
      g = paramLong4;
      h = paramList;
    }
    
    a(String paramString, Cache.Entry paramEntry)
    {
      this(paramString, etag, serverDate, lastModified, ttl, softTtl, a(paramEntry));
      a = data.length;
    }
    
    private static List<Header> a(Cache.Entry paramEntry)
    {
      if (allResponseHeaders != null) {
        return allResponseHeaders;
      }
      return HttpHeaderParser.a(responseHeaders);
    }
    
    static a a(DiskBasedCache.b paramB)
      throws IOException
    {
      int i = DiskBasedCache.a(paramB);
      if (i != 538247942) {
        throw new IOException();
      }
      String str1 = DiskBasedCache.a(paramB);
      String str2 = DiskBasedCache.a(paramB);
      long l1 = DiskBasedCache.b(paramB);
      long l2 = DiskBasedCache.b(paramB);
      long l3 = DiskBasedCache.b(paramB);
      long l4 = DiskBasedCache.b(paramB);
      List localList = DiskBasedCache.b(paramB);
      return new a(str1, str2, l1, l2, l3, l4, localList);
    }
    
    Cache.Entry a(byte[] paramArrayOfByte)
    {
      Cache.Entry localEntry = new Cache.Entry();
      data = paramArrayOfByte;
      etag = c;
      serverDate = d;
      lastModified = e;
      ttl = f;
      softTtl = g;
      responseHeaders = HttpHeaderParser.a(h);
      allResponseHeaders = Collections.unmodifiableList(h);
      return localEntry;
    }
    
    boolean a(OutputStream paramOutputStream)
    {
      try
      {
        DiskBasedCache.a(paramOutputStream, 538247942);
        DiskBasedCache.a(paramOutputStream, b);
        DiskBasedCache.a(paramOutputStream, c == null ? "" : c);
        DiskBasedCache.a(paramOutputStream, d);
        DiskBasedCache.a(paramOutputStream, e);
        DiskBasedCache.a(paramOutputStream, f);
        DiskBasedCache.a(paramOutputStream, g);
        DiskBasedCache.a(h, paramOutputStream);
        paramOutputStream.flush();
        return true;
      }
      catch (IOException localIOException)
      {
        VolleyLog.d("%s", new Object[] { localIOException.toString() });
      }
      return false;
    }
  }
}
