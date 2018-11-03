package ch.leica.sdk.connection;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import ch.leica.sdk.Devices.Device.LiveImageSpeed;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.Utilities.LiveImageStream;
import ch.leica.sdk.Utilities.LiveImageStream.FrameListener;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class RndisConnectionManager
  extends BaseConnectionManager
{
  protected HandlerThread liveImageNotifyThread = new HandlerThread("RndisConnectionManager_LiveImageNotifyThread_" + System.currentTimeMillis(), 10);
  protected Handler liveImageNotifyHandler;
  private LiveImageStream c;
  private a d;
  
  public RndisConnectionManager(Context paramContext)
  {
    super(paramContext);
    liveImageNotifyThread.start();
    liveImageNotifyHandler = new Handler(liveImageNotifyThread.getLooper());
  }
  
  protected void connectToDevice()
  {
    setState(BaseConnectionManager.ConnectionState.connecting, true);
    InetSocketAddress localInetSocketAddress = new InetSocketAddress("192.168.86.81", 22222);
    socket = new Socket();
    try
    {
      socket.connect(localInetSocketAddress, 1000);
      setState(BaseConnectionManager.ConnectionState.connected, true);
    }
    catch (IOException localIOException)
    {
      Logs.log(Logs.LogTypes.exception, "Could not connect to device");
      setState(BaseConnectionManager.ConnectionState.disconnected, true);
    }
  }
  
  public synchronized void findAvailableDevices()
  {
    if (shouldScan == true)
    {
      Logs.log(Logs.LogTypes.verbose, "shouldScan already true");
      stopScan();
    }
    try
    {
      InetAddress localInetAddress = InetAddress.getByName("192.168.86.81");
      if (localInetAddress.isReachable(200))
      {
        Logs.log(Logs.LogTypes.debug, "Possible device is reachable");
        foundAvailableRndisDevice("RNDIS_3DD", "192.168.86.81");
        stopDiscovery = false;
        shouldScan = true;
      }
    }
    catch (UnknownHostException localUnknownHostException)
    {
      localUnknownHostException.printStackTrace();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
    if (!shouldScan)
    {
      Logs.log(Logs.LogTypes.verbose, "shouldScan is false, return");
    }
    else
    {
      if (findTimer == null)
      {
        Logs.log(Logs.LogTypes.debug, "created timer");
        findTimer = new Timer();
      }
      Logs.log(Logs.LogTypes.verbose, "schedule timer");
      findTimer.schedule(new TimerTask()
      {
        public void run()
        {
          Logs.log(Logs.LogTypes.verbose, "in timer: shouldScan: " + shouldScan);
          if (shouldScan == true) {
            findAvailableDevices();
          }
        }
      }, 1000L);
    }
  }
  
  public synchronized void stopScan()
  {
    if (d == null)
    {
      Logs.log(Logs.LogTypes.warn, "Find device task was not active");
    }
    else
    {
      d.a();
      d = null;
    }
  }
  
  public void stopDiscovery()
  {
    int i = 42;
  }
  
  public void connect()
  {
    connectToDevice();
  }
  
  public boolean verifyConnection(String paramString)
  {
    return false;
  }
  
  public boolean checkConnectionMethodsAvailable()
  {
    return true;
  }
  
  public void setConnectionParameters(Object... paramVarArgs)
  {
    int i = 42;
  }
  
  public void enableFunctionality()
  {
    int i = 42;
  }
  
  public void connectLiveChannel(Device.LiveImageSpeed paramLiveImageSpeed)
  {
    try
    {
      InetSocketAddress localInetSocketAddress = new InetSocketAddress("192.168.86.81", 22222);
      c = new LiveImageStream(localInetSocketAddress, new b(null));
    }
    catch (SocketException localSocketException)
    {
      Logs.log(Logs.LogTypes.exception, "Failed to initialize stream");
      return;
    }
    c.startStream();
  }
  
  public void disconnectLiveChannel()
  {
    if (c != null) {
      c.stopStream();
    }
  }
  
  private class b
    implements LiveImageStream.FrameListener
  {
    private b() {}
    
    public void onNextFrame(ReceivedData paramReceivedData)
    {
      if (wifiReceivedDataListener != null) {
        try
        {
          wifiReceivedDataListener.onLiveImageDataReceived(paramReceivedData);
        }
        catch (DeviceException localDeviceException)
        {
          Logs.log(Logs.LogTypes.exception, "WifiReceiver can't handle the current frame");
        }
      }
    }
  }
  
  private class a
    extends Thread
  {
    private boolean b;
    
    public void run()
    {
      synchronized (this)
      {
        for (;;)
        {
          if (b)
          {
            try
            {
              InetAddress localInetAddress = InetAddress.getByName("192.168.86.81");
              if (localInetAddress.isReachable(200))
              {
                Logs.log(Logs.LogTypes.debug, "Possible device is reachable");
                a.foundAvailableRndisDevice("RNDIS_3DD", "192.168.86.81");
              }
            }
            catch (UnknownHostException localUnknownHostException)
            {
              localUnknownHostException.printStackTrace();
            }
            catch (IOException localIOException)
            {
              localIOException.printStackTrace();
            }
            try
            {
              wait(800L);
            }
            catch (InterruptedException localInterruptedException)
            {
              Logs.log(Logs.LogTypes.exception, "Forcefully killed");
            }
          }
        }
      }
    }
    
    public void a()
    {
      synchronized (this)
      {
        b = false;
        notifyAll();
      }
    }
  }
}
