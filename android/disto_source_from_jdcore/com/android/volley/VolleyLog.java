package com.android.volley;

import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class VolleyLog
{
  public static String TAG = "Volley";
  public static boolean DEBUG = Log.isLoggable(TAG, 2);
  private static final String a = VolleyLog.class.getName();
  
  public VolleyLog() {}
  
  public static void setTag(String paramString)
  {
    d("Changing log tag to %s", new Object[] { paramString });
    TAG = paramString;
    DEBUG = Log.isLoggable(TAG, 2);
  }
  
  public static void v(String paramString, Object... paramVarArgs)
  {
    if (DEBUG) {
      Log.v(TAG, a(paramString, paramVarArgs));
    }
  }
  
  public static void d(String paramString, Object... paramVarArgs)
  {
    Log.d(TAG, a(paramString, paramVarArgs));
  }
  
  public static void e(String paramString, Object... paramVarArgs)
  {
    Log.e(TAG, a(paramString, paramVarArgs));
  }
  
  public static void e(Throwable paramThrowable, String paramString, Object... paramVarArgs)
  {
    Log.e(TAG, a(paramString, paramVarArgs), paramThrowable);
  }
  
  public static void wtf(String paramString, Object... paramVarArgs)
  {
    Log.wtf(TAG, a(paramString, paramVarArgs));
  }
  
  public static void wtf(Throwable paramThrowable, String paramString, Object... paramVarArgs)
  {
    Log.wtf(TAG, a(paramString, paramVarArgs), paramThrowable);
  }
  
  private static String a(String paramString, Object... paramVarArgs)
  {
    String str1 = paramVarArgs == null ? paramString : String.format(Locale.US, paramString, paramVarArgs);
    StackTraceElement[] arrayOfStackTraceElement = new Throwable().fillInStackTrace().getStackTrace();
    String str2 = "<unknown>";
    for (int i = 2; i < arrayOfStackTraceElement.length; i++)
    {
      String str3 = arrayOfStackTraceElement[i].getClassName();
      if (!str3.equals(a))
      {
        String str4 = arrayOfStackTraceElement[i].getClassName();
        str4 = str4.substring(str4.lastIndexOf('.') + 1);
        str4 = str4.substring(str4.lastIndexOf('$') + 1);
        str2 = str4 + "." + arrayOfStackTraceElement[i].getMethodName();
        break;
      }
    }
    return String.format(Locale.US, "[%d] %s: %s", new Object[] { Long.valueOf(Thread.currentThread().getId()), str2, str1 });
  }
  
  static class a
  {
    public static final boolean a = VolleyLog.DEBUG;
    private final List<a> b = new ArrayList();
    private boolean c = false;
    
    a() {}
    
    public synchronized void a(String paramString, long paramLong)
    {
      if (c) {
        throw new IllegalStateException("Marker added to finished log");
      }
      b.add(new a(paramString, paramLong, SystemClock.elapsedRealtime()));
    }
    
    public synchronized void a(String paramString)
    {
      c = true;
      long l1 = a();
      if (l1 <= 0L) {
        return;
      }
      long l2 = b.get(0)).c;
      VolleyLog.d("(%-4d ms) %s", new Object[] { Long.valueOf(l1), paramString });
      Iterator localIterator = b.iterator();
      while (localIterator.hasNext())
      {
        a localA = (a)localIterator.next();
        long l3 = c;
        VolleyLog.d("(+%-4d) [%2d] %s", new Object[] { Long.valueOf(l3 - l2), Long.valueOf(b), a });
        l2 = l3;
      }
    }
    
    protected void finalize()
      throws Throwable
    {
      if (!c)
      {
        a("Request on the loose");
        VolleyLog.e("Marker log finalized without finish() - uncaught exit point for request", new Object[0]);
      }
    }
    
    private long a()
    {
      if (b.size() == 0) {
        return 0L;
      }
      long l1 = b.get(0)).c;
      long l2 = b.get(b.size() - 1)).c;
      return l2 - l1;
    }
    
    private static class a
    {
      public final String a;
      public final long b;
      public final long c;
      
      public a(String paramString, long paramLong1, long paramLong2)
      {
        a = paramString;
        b = paramLong1;
        c = paramLong2;
      }
    }
  }
}
