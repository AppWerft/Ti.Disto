package ch.leica.sdk.ErrorHandling;

public class UpdateException
  extends ChainedException
{
  public UpdateException() {}
  
  public UpdateException(String paramString)
  {
    super(paramString);
  }
  
  public UpdateException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}
