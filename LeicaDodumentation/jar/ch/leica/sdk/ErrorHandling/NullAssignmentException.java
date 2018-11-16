package ch.leica.sdk.ErrorHandling;

public final class NullAssignmentException
  extends ChainedException
{
  public NullAssignmentException() {}
  
  public NullAssignmentException(String paramString)
  {
    super(paramString);
  }
  
  public NullAssignmentException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}
