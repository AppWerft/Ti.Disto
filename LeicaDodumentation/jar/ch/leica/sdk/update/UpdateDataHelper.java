package ch.leica.sdk.update;

import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.Arrays;

public final class UpdateDataHelper
{
  private int a;
  private byte[] b = null;
  private int c = -1;
  private int d;
  private int e;
  private int f;
  
  public UpdateDataHelper(byte[] paramArrayOfByte)
  {
    b = paramArrayOfByte;
    a = 0;
    d = paramArrayOfByte.length;
    e = d;
  }
  
  public int getRealFilesize()
  {
    return e;
  }
  
  public void setBlockSize(int paramInt)
  {
    c = paramInt;
  }
  
  public UpdateData getNextData()
  {
    if ((b == null) || (b.length < 1) || (c < 0) || (a > d))
    {
      a = -1;
      return null;
    }
    UpdateData localUpdateData = new UpdateData();
    if (a + c > b.length) {
      localUpdateData.setDatalength(b.length - a);
    } else {
      localUpdateData.setDatalength(c);
    }
    byte[] arrayOfByte = Arrays.copyOfRange(b, a, a + localUpdateData.getDataLength());
    localUpdateData.setBlock(arrayOfByte);
    UpdateData.a(localUpdateData, a.a(arrayOfByte, arrayOfByte.length, -1));
    Logs.log(Logs.LogTypes.debug, " Block(offset): " + a + " CRC: " + localUpdateData);
    localUpdateData.setoffset(a);
    localUpdateData.setoffset(a);
    a += c;
    return localUpdateData;
  }
  
  public String getCrcWholeFileData()
  {
    byte[] arrayOfByte = Arrays.copyOfRange(b, f, b.length);
    return Integer.toHexString(a.a(arrayOfByte, arrayOfByte.length, -1)).toUpperCase();
  }
  
  public void setOffset(int paramInt)
  {
    f = paramInt;
    a = paramInt;
    e -= a;
  }
  
  public class UpdateData
  {
    private byte[] b = null;
    private int c = -1;
    private int d;
    private int e;
    
    public UpdateData() {}
    
    public byte[] getBlock()
    {
      return b;
    }
    
    public int getCrc()
    {
      return c;
    }
    
    public String getCrcStr()
    {
      return Integer.toHexString(getCrc()).toUpperCase();
    }
    
    public int getDataLength()
    {
      return e;
    }
    
    public String getDataLengthStr()
    {
      return Integer.toHexString(getDataLength()).toUpperCase();
    }
    
    public int getOffset()
    {
      return d;
    }
    
    public String getOffsetStr()
    {
      return Integer.toHexString(getOffset()).toUpperCase();
    }
    
    public void setBlock(byte[] paramArrayOfByte)
    {
      b = paramArrayOfByte;
    }
    
    public void setCrc(int paramInt)
    {
      c = paramInt;
    }
    
    public void setoffset(int paramInt)
    {
      d = paramInt;
    }
    
    public void setDatalength(int paramInt)
    {
      e = paramInt;
    }
  }
}
