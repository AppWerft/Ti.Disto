package ch.leica.sdk.commands;

import java.nio.ByteBuffer;
import java.util.Arrays;

public final class LiveImage
  extends Image
{
  private double a;
  private double b;
  private double c;
  private double d;
  private short e;
  private int f;
  private int g;
  private int h;
  
  public LiveImage() {}
  
  private void a(double paramDouble)
  {
    a = paramDouble;
  }
  
  private void b(double paramDouble)
  {
    b = paramDouble;
  }
  
  private void c(double paramDouble)
  {
    c = paramDouble;
  }
  
  private void d(double paramDouble)
  {
    d = paramDouble;
  }
  
  private void a(short paramShort)
  {
    e = paramShort;
  }
  
  private void a(int paramInt)
  {
    f = paramInt;
  }
  
  private void b(int paramInt)
  {
    g = paramInt;
  }
  
  private void c(int paramInt)
  {
    h = paramInt;
  }
  
  public double getHorizontalAngleCorrected()
  {
    return a;
  }
  
  public double getVerticalAngleCorrected()
  {
    return b;
  }
  
  public double getHorizontalAngleNotCorrected()
  {
    return c;
  }
  
  public double getVerticalAngleNotCorrected()
  {
    return d;
  }
  
  public short getOrientation()
  {
    return e;
  }
  
  public int getCounter()
  {
    return f;
  }
  
  public int getTimeSeconds()
  {
    return g;
  }
  
  public int getTimeNanoseconds()
  {
    return h;
  }
  
  public void setLiveImageData(byte[] paramArrayOfByte)
  {
    ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
    int i = 0;
    a(localByteBuffer.getDouble(i));
    int j = 8;
    b(localByteBuffer.getDouble(j));
    int k = 16;
    c(localByteBuffer.getDouble(k));
    int m = 24;
    d(localByteBuffer.getDouble(m));
    int n = 32;
    setxCoordinateCrosshair(localByteBuffer.getShort(n));
    int i1 = 34;
    setyCoordinateCrosshair(localByteBuffer.getShort(i1));
    int i2 = 36;
    a(localByteBuffer.getShort(i2));
    int i3 = 40;
    a(localByteBuffer.getInt(i3));
    int i4 = 44;
    b(localByteBuffer.getInt(i4));
    int i5 = 48;
    c(localByteBuffer.getInt(i5));
    int i6 = 64;
    setImageBytes(Arrays.copyOfRange(paramArrayOfByte, i6, paramArrayOfByte.length));
  }
}
