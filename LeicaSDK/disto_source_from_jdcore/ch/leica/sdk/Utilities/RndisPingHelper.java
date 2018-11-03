package ch.leica.sdk.Utilities;

import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Types.ConnectionType;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class RndisPingHelper
{
  private static Timer a = new Timer();
  private static TimerTask b;
  private static int c = 5000;
  private static int d = 4000;
  
  public RndisPingHelper() {}
  
  public static void startTask(Device paramDevice)
  {
    b = new TimerTask()
    {
      public void run()
      {
        try
        {
          boolean bool = Inet4Address.getByName(a.getIP()).isReachable(RndisPingHelper.a());
          Logs.log(Logs.LogTypes.debug, "RNDIS Device isReachable: " + bool);
          if (!bool)
          {
            a.disconnect();
            if (a.getConnectionType() == Types.ConnectionType.rndis)
            {
              Logs.log(Logs.LogTypes.debug, "RNDIS Device stopped");
              RndisPingHelper.stopTask();
            }
          }
        }
        catch (IOException localIOException)
        {
          Logs.log(Logs.LogTypes.debug, localIOException.getMessage(), localIOException);
        }
        Logs.log(Logs.LogTypes.debug, "called");
      }
    };
    a.scheduleAtFixedRate(b, c, c);
  }
  
  public static void stopTask()
  {
    if (b != null) {
      b.cancel();
    }
    if (a != null) {
      a.purge();
    }
    Logs.log(Logs.LogTypes.debug, "called");
  }
}
