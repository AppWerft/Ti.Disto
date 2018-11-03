package ch.leica.sdk.Utilities;

import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.concurrent.CountDownLatch;

public final class WaitAmoment
{
  Thread a;
  
  public WaitAmoment() {}
  
  public void waitAmoment(final long paramLong)
  {
    CountDownLatch localCountDownLatch = new CountDownLatch(1);
    a = new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          Thread.sleep(paramLong);
          b.countDown();
        }
        catch (InterruptedException localInterruptedException)
        {
          Logs.log(Logs.LogTypes.debug, " - waiting was interrupted");
          b.countDown();
        }
      }
    });
    a.start();
    try
    {
      localCountDownLatch.await();
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  public void stopWaiting()
  {
    if (a == null) {
      return;
    }
    a.interrupt();
  }
}
