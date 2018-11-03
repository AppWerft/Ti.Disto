package com.android.volley.toolbox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build.VERSION;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import java.io.File;

public class Volley
{
  public Volley() {}
  
  public static RequestQueue newRequestQueue(Context paramContext, BaseHttpStack paramBaseHttpStack)
  {
    BasicNetwork localBasicNetwork;
    if (paramBaseHttpStack == null)
    {
      if (Build.VERSION.SDK_INT >= 9)
      {
        localBasicNetwork = new BasicNetwork(new HurlStack());
      }
      else
      {
        String str1 = "volley/0";
        try
        {
          String str2 = paramContext.getPackageName();
          PackageInfo localPackageInfo = paramContext.getPackageManager().getPackageInfo(str2, 0);
          str1 = str2 + "/" + versionCode;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
        localBasicNetwork = new BasicNetwork(new HttpClientStack(AndroidHttpClient.newInstance(str1)));
      }
    }
    else {
      localBasicNetwork = new BasicNetwork(paramBaseHttpStack);
    }
    return a(paramContext, localBasicNetwork);
  }
  
  @Deprecated
  public static RequestQueue newRequestQueue(Context paramContext, HttpStack paramHttpStack)
  {
    if (paramHttpStack == null) {
      return newRequestQueue(paramContext, (BaseHttpStack)null);
    }
    return a(paramContext, new BasicNetwork(paramHttpStack));
  }
  
  private static RequestQueue a(Context paramContext, Network paramNetwork)
  {
    File localFile = new File(paramContext.getCacheDir(), "volley");
    RequestQueue localRequestQueue = new RequestQueue(new DiskBasedCache(localFile), paramNetwork);
    localRequestQueue.start();
    return localRequestQueue;
  }
  
  public static RequestQueue newRequestQueue(Context paramContext)
  {
    return newRequestQueue(paramContext, (BaseHttpStack)null);
  }
}
