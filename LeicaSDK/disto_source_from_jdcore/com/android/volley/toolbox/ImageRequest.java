package com.android.volley.toolbox;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.widget.ImageView.ScaleType;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;

public class ImageRequest
  extends Request<Bitmap>
{
  public static final int DEFAULT_IMAGE_TIMEOUT_MS = 1000;
  public static final int DEFAULT_IMAGE_MAX_RETRIES = 2;
  public static final float DEFAULT_IMAGE_BACKOFF_MULT = 2.0F;
  private final Object a = new Object();
  private Response.Listener<Bitmap> b;
  private final Bitmap.Config c;
  private final int d;
  private final int e;
  private final ImageView.ScaleType f;
  private static final Object g = new Object();
  
  public ImageRequest(String paramString, Response.Listener<Bitmap> paramListener, int paramInt1, int paramInt2, ImageView.ScaleType paramScaleType, Bitmap.Config paramConfig, Response.ErrorListener paramErrorListener)
  {
    super(0, paramString, paramErrorListener);
    setRetryPolicy(new DefaultRetryPolicy(1000, 2, 2.0F));
    b = paramListener;
    c = paramConfig;
    d = paramInt1;
    e = paramInt2;
    f = paramScaleType;
  }
  
  @Deprecated
  public ImageRequest(String paramString, Response.Listener<Bitmap> paramListener, int paramInt1, int paramInt2, Bitmap.Config paramConfig, Response.ErrorListener paramErrorListener)
  {
    this(paramString, paramListener, paramInt1, paramInt2, ImageView.ScaleType.CENTER_INSIDE, paramConfig, paramErrorListener);
  }
  
  public Request.Priority getPriority()
  {
    return Request.Priority.LOW;
  }
  
  private static int a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageView.ScaleType paramScaleType)
  {
    if ((paramInt1 == 0) && (paramInt2 == 0)) {
      return paramInt3;
    }
    if (paramScaleType == ImageView.ScaleType.FIT_XY)
    {
      if (paramInt1 == 0) {
        return paramInt3;
      }
      return paramInt1;
    }
    if (paramInt1 == 0)
    {
      d1 = paramInt2 / paramInt4;
      return (int)(paramInt3 * d1);
    }
    if (paramInt2 == 0) {
      return paramInt1;
    }
    double d1 = paramInt4 / paramInt3;
    int i = paramInt1;
    if (paramScaleType == ImageView.ScaleType.CENTER_CROP)
    {
      if (i * d1 < paramInt2) {
        i = (int)(paramInt2 / d1);
      }
      return i;
    }
    if (i * d1 > paramInt2) {
      i = (int)(paramInt2 / d1);
    }
    return i;
  }
  
  protected Response<Bitmap> parseNetworkResponse(NetworkResponse paramNetworkResponse)
  {
    synchronized (g)
    {
      try
      {
        return a(paramNetworkResponse);
      }
      catch (OutOfMemoryError localOutOfMemoryError)
      {
        VolleyLog.e("Caught OOM for %d byte image, url=%s", new Object[] { Integer.valueOf(data.length), getUrl() });
        return Response.error(new ParseError(localOutOfMemoryError));
      }
    }
  }
  
  private Response<Bitmap> a(NetworkResponse paramNetworkResponse)
  {
    byte[] arrayOfByte = data;
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    Object localObject = null;
    if ((d == 0) && (e == 0))
    {
      inPreferredConfig = c;
      localObject = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length, localOptions);
    }
    else
    {
      inJustDecodeBounds = true;
      BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length, localOptions);
      int i = outWidth;
      int j = outHeight;
      int k = a(d, e, i, j, f);
      int m = a(e, d, j, i, f);
      inJustDecodeBounds = false;
      inSampleSize = a(i, j, k, m);
      Bitmap localBitmap = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length, localOptions);
      if ((localBitmap != null) && ((localBitmap.getWidth() > k) || (localBitmap.getHeight() > m)))
      {
        localObject = Bitmap.createScaledBitmap(localBitmap, k, m, true);
        localBitmap.recycle();
      }
      else
      {
        localObject = localBitmap;
      }
    }
    if (localObject == null) {
      return Response.error(new ParseError(paramNetworkResponse));
    }
    return Response.success(localObject, HttpHeaderParser.parseCacheHeaders(paramNetworkResponse));
  }
  
  public void cancel()
  {
    super.cancel();
    synchronized (a)
    {
      b = null;
    }
  }
  
  protected void deliverResponse(Bitmap paramBitmap)
  {
    Response.Listener localListener;
    synchronized (a)
    {
      localListener = b;
    }
    if (localListener != null) {
      localListener.onResponse(paramBitmap);
    }
  }
  
  static int a(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    double d1 = paramInt1 / paramInt3;
    double d2 = paramInt2 / paramInt4;
    double d3 = Math.min(d1, d2);
    for (float f1 = 1.0F; f1 * 2.0F <= d3; f1 *= 2.0F) {}
    return (int)f1;
  }
}
