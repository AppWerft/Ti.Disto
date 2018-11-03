package ch.leica.sdk.commands.response;

import ch.leica.sdk.Types.Commands;

public final class ResponseFace
  extends Response
{
  public int face;
  
  public ResponseFace(Types.Commands paramCommands)
  {
    super(paramCommands);
  }
  
  public void setFace(int paramInt)
  {
    face = paramInt;
  }
  
  public int getFace()
  {
    return face;
  }
}
