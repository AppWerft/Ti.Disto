package ch.leica.sdk.ErrorHandling;

public final class DeviceException
  extends ChainedException
{
  public DeviceException() {}
  
  public DeviceException(String paramString)
  {
    super(paramString);
  }
  
  public DeviceException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public DeviceException(ErrorObject paramErrorObject)
  {
    super(paramErrorObject);
  }
  
  public DeviceException(ErrorObject paramErrorObject, Throwable paramThrowable)
  {
    super(paramErrorObject, paramThrowable);
  }
}
