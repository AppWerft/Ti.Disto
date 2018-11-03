package ch.leica.sdk.commands.response;

import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.Types.Commands;

public final class ResponseImage
  extends Response
{
  private byte[] e = null;
  private short f = 55537;
  private short g = 55537;
  
  public ResponseImage(Types.Commands paramCommands)
  {
    super(paramCommands);
  }
  
  public void setxCoordinateCrosshair(short paramShort)
  {
    f = paramShort;
  }
  
  public void setyCoordinateCrosshair(short paramShort)
  {
    g = paramShort;
  }
  
  public short getxCoordinateCrosshair()
  {
    return f;
  }
  
  public short getyCoordinateCrosshair()
  {
    return g;
  }
  
  public byte[] getImageBytes()
    throws IllegalArgumentCheckedException
  {
    if (e != null) {
      return e;
    }
    throw new IllegalArgumentCheckedException("Error: Imagebytes were never assigned. ImageBytes = NULL");
  }
  
  public void setImageBytes(byte[] paramArrayOfByte)
  {
    e = paramArrayOfByte;
  }
}
