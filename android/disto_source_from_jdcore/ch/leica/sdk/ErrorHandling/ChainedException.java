package ch.leica.sdk.ErrorHandling;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ChainedException
  extends Exception
{
  ErrorObject a;
  private Throwable b = null;
  
  public ChainedException() {}
  
  public ChainedException(String paramString)
  {
    super(paramString);
  }
  
  public ChainedException(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    b = paramThrowable;
  }
  
  public ChainedException(ErrorObject paramErrorObject)
  {
    super(paramErrorObject.getErrorMessage());
    a = paramErrorObject;
  }
  
  public ChainedException(ErrorObject paramErrorObject, Throwable paramThrowable)
  {
    super(paramErrorObject.getErrorMessage());
    a = paramErrorObject;
    b = paramThrowable;
  }
  
  public Throwable getCause()
  {
    return b;
  }
  
  public void printStackTrace()
  {
    super.printStackTrace();
    if (b != null)
    {
      System.err.println("Caused by:");
      b.printStackTrace();
    }
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    super.printStackTrace(paramPrintStream);
    if (b != null)
    {
      paramPrintStream.println("Caused by:");
      b.printStackTrace(paramPrintStream);
    }
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    super.printStackTrace(paramPrintWriter);
    if (b != null)
    {
      paramPrintWriter.println("Caused by:");
      b.printStackTrace(paramPrintWriter);
    }
  }
}
