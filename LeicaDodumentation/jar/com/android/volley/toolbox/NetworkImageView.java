package com.android.volley.toolbox;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.android.volley.VolleyError;

public class NetworkImageView
  extends ImageView
{
  private String a;
  private int b;
  private int c;
  private ImageLoader d;
  private ImageLoader.ImageContainer e;
  
  public NetworkImageView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public NetworkImageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public NetworkImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public void setImageUrl(String paramString, ImageLoader paramImageLoader)
  {
    a = paramString;
    d = paramImageLoader;
    a(false);
  }
  
  public void setDefaultImageResId(int paramInt)
  {
    b = paramInt;
  }
  
  public void setErrorImageResId(int paramInt)
  {
    c = paramInt;
  }
  
  void a(final boolean paramBoolean)
  {
    int i = getWidth();
    int j = getHeight();
    ImageView.ScaleType localScaleType = getScaleType();
    int k = 0;
    int m = 0;
    if (getLayoutParams() != null)
    {
      k = getLayoutParamswidth == -2 ? 1 : 0;
      m = getLayoutParamsheight == -2 ? 1 : 0;
    }
    int n = (k != 0) && (m != 0) ? 1 : 0;
    if ((i == 0) && (j == 0) && (n == 0)) {
      return;
    }
    if (TextUtils.isEmpty(a))
    {
      if (e != null)
      {
        e.cancelRequest();
        e = null;
      }
      a();
      return;
    }
    if ((e != null) && (e.getRequestUrl() != null))
    {
      if (e.getRequestUrl().equals(a)) {
        return;
      }
      e.cancelRequest();
      a();
    }
    int i1 = k != 0 ? 0 : i;
    int i2 = m != 0 ? 0 : j;
    e = d.get(a, new ImageLoader.ImageListener()
    {
      public void onErrorResponse(VolleyError paramAnonymousVolleyError)
      {
        if (NetworkImageView.a(NetworkImageView.this) != 0) {
          setImageResource(NetworkImageView.a(NetworkImageView.this));
        }
      }
      
      public void onResponse(final ImageLoader.ImageContainer paramAnonymousImageContainer, boolean paramAnonymousBoolean)
      {
        if ((paramAnonymousBoolean) && (paramBoolean))
        {
          post(new Runnable()
          {
            public void run()
            {
              onResponse(paramAnonymousImageContainer, false);
            }
          });
          return;
        }
        if (paramAnonymousImageContainer.getBitmap() != null) {
          setImageBitmap(paramAnonymousImageContainer.getBitmap());
        } else if (NetworkImageView.b(NetworkImageView.this) != 0) {
          setImageResource(NetworkImageView.b(NetworkImageView.this));
        }
      }
    }, i1, i2, localScaleType);
  }
  
  private void a()
  {
    if (b != 0) {
      setImageResource(b);
    } else {
      setImageBitmap(null);
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    a(true);
  }
  
  protected void onDetachedFromWindow()
  {
    if (e != null)
    {
      e.cancelRequest();
      setImageBitmap(null);
      e = null;
    }
    super.onDetachedFromWindow();
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    invalidate();
  }
}
