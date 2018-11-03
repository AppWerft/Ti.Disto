package com.android.volley.toolbox;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class ImageLoader
{
  private final RequestQueue a;
  private int b = 100;
  private final ImageCache c;
  private final HashMap<String, a> d = new HashMap();
  private final HashMap<String, a> e = new HashMap();
  private final Handler f = new Handler(Looper.getMainLooper());
  private Runnable g;
  
  public ImageLoader(RequestQueue paramRequestQueue, ImageCache paramImageCache)
  {
    a = paramRequestQueue;
    c = paramImageCache;
  }
  
  public static ImageListener getImageListener(final ImageView paramImageView, final int paramInt1, int paramInt2)
  {
    new ImageListener()
    {
      public void onErrorResponse(VolleyError paramAnonymousVolleyError)
      {
        if (a != 0) {
          paramImageView.setImageResource(a);
        }
      }
      
      public void onResponse(ImageLoader.ImageContainer paramAnonymousImageContainer, boolean paramAnonymousBoolean)
      {
        if (paramAnonymousImageContainer.getBitmap() != null) {
          paramImageView.setImageBitmap(paramAnonymousImageContainer.getBitmap());
        } else if (paramInt1 != 0) {
          paramImageView.setImageResource(paramInt1);
        }
      }
    };
  }
  
  public boolean isCached(String paramString, int paramInt1, int paramInt2)
  {
    return isCached(paramString, paramInt1, paramInt2, ImageView.ScaleType.CENTER_INSIDE);
  }
  
  public boolean isCached(String paramString, int paramInt1, int paramInt2, ImageView.ScaleType paramScaleType)
  {
    a();
    String str = a(paramString, paramInt1, paramInt2, paramScaleType);
    return c.getBitmap(str) != null;
  }
  
  public ImageContainer get(String paramString, ImageListener paramImageListener)
  {
    return get(paramString, paramImageListener, 0, 0);
  }
  
  public ImageContainer get(String paramString, ImageListener paramImageListener, int paramInt1, int paramInt2)
  {
    return get(paramString, paramImageListener, paramInt1, paramInt2, ImageView.ScaleType.CENTER_INSIDE);
  }
  
  public ImageContainer get(String paramString, ImageListener paramImageListener, int paramInt1, int paramInt2, ImageView.ScaleType paramScaleType)
  {
    a();
    String str = a(paramString, paramInt1, paramInt2, paramScaleType);
    Bitmap localBitmap = c.getBitmap(str);
    if (localBitmap != null)
    {
      localImageContainer = new ImageContainer(localBitmap, paramString, null, null);
      paramImageListener.onResponse(localImageContainer, true);
      return localImageContainer;
    }
    ImageContainer localImageContainer = new ImageContainer(null, paramString, str, paramImageListener);
    paramImageListener.onResponse(localImageContainer, true);
    a localA = (a)d.get(str);
    if (localA != null)
    {
      localA.a(localImageContainer);
      return localImageContainer;
    }
    Request localRequest = makeImageRequest(paramString, paramInt1, paramInt2, paramScaleType, str);
    a.add(localRequest);
    d.put(str, new a(localRequest, localImageContainer));
    return localImageContainer;
  }
  
  protected Request<Bitmap> makeImageRequest(String paramString1, int paramInt1, int paramInt2, ImageView.ScaleType paramScaleType, final String paramString2)
  {
    new ImageRequest(paramString1, new Response.Listener()
    {
      public void a(Bitmap paramAnonymousBitmap)
      {
        onGetImageSuccess(paramString2, paramAnonymousBitmap);
      }
    }, paramInt1, paramInt2, paramScaleType, Bitmap.Config.RGB_565, new Response.ErrorListener()
    {
      public void onErrorResponse(VolleyError paramAnonymousVolleyError)
      {
        onGetImageError(paramString2, paramAnonymousVolleyError);
      }
    });
  }
  
  public void setBatchedResponseDelay(int paramInt)
  {
    b = paramInt;
  }
  
  protected void onGetImageSuccess(String paramString, Bitmap paramBitmap)
  {
    c.putBitmap(paramString, paramBitmap);
    a localA = (a)d.remove(paramString);
    if (localA != null)
    {
      a.a(localA, paramBitmap);
      a(paramString, localA);
    }
  }
  
  protected void onGetImageError(String paramString, VolleyError paramVolleyError)
  {
    a localA = (a)d.remove(paramString);
    if (localA != null)
    {
      localA.a(paramVolleyError);
      a(paramString, localA);
    }
  }
  
  private void a(String paramString, a paramA)
  {
    e.put(paramString, paramA);
    if (g == null)
    {
      g = new Runnable()
      {
        public void run()
        {
          Iterator localIterator1 = ImageLoader.b(ImageLoader.this).values().iterator();
          while (localIterator1.hasNext())
          {
            ImageLoader.a localA = (ImageLoader.a)localIterator1.next();
            Iterator localIterator2 = ImageLoader.a.a(localA).iterator();
            while (localIterator2.hasNext())
            {
              ImageLoader.ImageContainer localImageContainer = (ImageLoader.ImageContainer)localIterator2.next();
              if (ImageLoader.ImageContainer.a(localImageContainer) != null) {
                if (localA.a() == null)
                {
                  ImageLoader.ImageContainer.a(localImageContainer, ImageLoader.a.b(localA));
                  ImageLoader.ImageContainer.a(localImageContainer).onResponse(localImageContainer, false);
                }
                else
                {
                  ImageLoader.ImageContainer.a(localImageContainer).onErrorResponse(localA.a());
                }
              }
            }
          }
          ImageLoader.b(ImageLoader.this).clear();
          ImageLoader.a(ImageLoader.this, null);
        }
      };
      f.postDelayed(g, b);
    }
  }
  
  private void a()
  {
    if (Looper.myLooper() != Looper.getMainLooper()) {
      throw new IllegalStateException("ImageLoader must be invoked from the main thread.");
    }
  }
  
  private static String a(String paramString, int paramInt1, int paramInt2, ImageView.ScaleType paramScaleType)
  {
    return paramString.length() + 12 + "#W" + paramInt1 + "#H" + paramInt2 + "#S" + paramScaleType.ordinal() + paramString;
  }
  
  private class a
  {
    private final Request<?> b;
    private Bitmap c;
    private VolleyError d;
    private final LinkedList<ImageLoader.ImageContainer> e = new LinkedList();
    
    public a(ImageLoader.ImageContainer paramImageContainer)
    {
      b = paramImageContainer;
      Object localObject;
      e.add(localObject);
    }
    
    public void a(VolleyError paramVolleyError)
    {
      d = paramVolleyError;
    }
    
    public VolleyError a()
    {
      return d;
    }
    
    public void a(ImageLoader.ImageContainer paramImageContainer)
    {
      e.add(paramImageContainer);
    }
    
    public boolean b(ImageLoader.ImageContainer paramImageContainer)
    {
      e.remove(paramImageContainer);
      if (e.size() == 0)
      {
        b.cancel();
        return true;
      }
      return false;
    }
  }
  
  public class ImageContainer
  {
    private Bitmap b;
    private final ImageLoader.ImageListener c;
    private final String d;
    private final String e;
    
    public ImageContainer(Bitmap paramBitmap, String paramString1, String paramString2, ImageLoader.ImageListener paramImageListener)
    {
      b = paramBitmap;
      e = paramString1;
      d = paramString2;
      c = paramImageListener;
    }
    
    public void cancelRequest()
    {
      if (c == null) {
        return;
      }
      ImageLoader.a localA = (ImageLoader.a)ImageLoader.a(ImageLoader.this).get(d);
      if (localA != null)
      {
        boolean bool = localA.b(this);
        if (bool) {
          ImageLoader.a(ImageLoader.this).remove(d);
        }
      }
      else
      {
        localA = (ImageLoader.a)ImageLoader.b(ImageLoader.this).get(d);
        if (localA != null)
        {
          localA.b(this);
          if (ImageLoader.a.a(localA).size() == 0) {
            ImageLoader.b(ImageLoader.this).remove(d);
          }
        }
      }
    }
    
    public Bitmap getBitmap()
    {
      return b;
    }
    
    public String getRequestUrl()
    {
      return e;
    }
  }
  
  public static abstract interface ImageListener
    extends Response.ErrorListener
  {
    public abstract void onResponse(ImageLoader.ImageContainer paramImageContainer, boolean paramBoolean);
  }
  
  public static abstract interface ImageCache
  {
    public abstract Bitmap getBitmap(String paramString);
    
    public abstract void putBitmap(String paramString, Bitmap paramBitmap);
  }
}
