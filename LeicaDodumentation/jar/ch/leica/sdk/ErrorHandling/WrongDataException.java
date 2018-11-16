package ch.leica.sdk.ErrorHandling;

public class WrongDataException
  extends ChainedException
{
  public WrongDataException() {}
  
  public WrongDataException(String paramString)
  {
    super(paramString);
  }
  
  public WrongDataException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}
