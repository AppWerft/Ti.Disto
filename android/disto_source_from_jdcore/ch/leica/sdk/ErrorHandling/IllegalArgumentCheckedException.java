package ch.leica.sdk.ErrorHandling;

public final class IllegalArgumentCheckedException
  extends ChainedException
{
  public IllegalArgumentCheckedException() {}
  
  public IllegalArgumentCheckedException(String paramString)
  {
    super(paramString);
  }
  
  public IllegalArgumentCheckedException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}
