package ch.leica.sdk.update.FirmwareUpdate.DataClasses;

public class FirmwareBinary
{
  public int orderNumber;
  public String command;
  public int offset;
  public byte[] data;
  
  public FirmwareBinary(int paramInt1, String paramString, int paramInt2, byte[] paramArrayOfByte)
  {
    orderNumber = paramInt1;
    command = paramString;
    offset = paramInt2;
    data = paramArrayOfByte;
  }
  
  public int getOrderNumber()
  {
    return orderNumber;
  }
  
  public int getOffset()
  {
    return offset;
  }
  
  public byte[] getData()
  {
    return data;
  }
  
  public String getCommand()
  {
    return command;
  }
}
