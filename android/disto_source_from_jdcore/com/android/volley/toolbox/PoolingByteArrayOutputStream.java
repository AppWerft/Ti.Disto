package com.android.volley.toolbox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PoolingByteArrayOutputStream
  extends ByteArrayOutputStream
{
  private final ByteArrayPool a;
  
  public PoolingByteArrayOutputStream(ByteArrayPool paramByteArrayPool)
  {
    this(paramByteArrayPool, 256);
  }
  
  public PoolingByteArrayOutputStream(ByteArrayPool paramByteArrayPool, int paramInt)
  {
    a = paramByteArrayPool;
    buf = a.getBuf(Math.max(paramInt, 256));
  }
  
  public void close()
    throws IOException
  {
    a.returnBuf(buf);
    buf = null;
    super.close();
  }
  
  public void finalize()
  {
    a.returnBuf(buf);
  }
  
  private void a(int paramInt)
  {
    if (count + paramInt <= buf.length) {
      return;
    }
    byte[] arrayOfByte = a.getBuf((count + paramInt) * 2);
    System.arraycopy(buf, 0, arrayOfByte, 0, count);
    a.returnBuf(buf);
    buf = arrayOfByte;
  }
  
  public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    a(paramInt2);
    super.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public synchronized void write(int paramInt)
  {
    a(1);
    super.write(paramInt);
  }
}
