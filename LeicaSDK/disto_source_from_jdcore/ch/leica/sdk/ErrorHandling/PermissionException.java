package ch.leica.sdk.ErrorHandling;

public final class PermissionException
  extends ChainedException
{
  public PermissionException() {}
  
  public PermissionException(String paramString)
  {
    super(paramString);
  }
  
  public PermissionException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}
