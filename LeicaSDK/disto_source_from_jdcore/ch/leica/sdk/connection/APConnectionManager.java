package ch.leica.sdk.connection;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.Utilities.WaitAmoment;
import ch.leica.sdk.Utilities.WifiHelper;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.ReceivedWifiDataPacket;
import ch.leica.sdk.commands.WifiCommand;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public final class APConnectionManager
  extends BaseConnectionManager
{
  private WifiManager c;
  private DatagramSocket d;
  private Thread e;
  private boolean f = true;
  private int g = 0;
  private boolean h = false;
  
  public APConnectionManager(WifiManager paramWifiManager, Context paramContext)
  {
    super(paramContext);
    context = paramContext;
    c = paramWifiManager;
  }
  
  public void setConnectionParameters(Object... paramVarArgs)
  {
    setSSID((String)paramVarArgs[0]);
    setIP((String)paramVarArgs[1]);
  }
  
  public synchronized void findAvailableDevices()
  {
    if (shouldScan == true)
    {
      Logs.log(Logs.LogTypes.verbose, "shouldScan already true");
      stopScan();
    }
    String str = WifiHelper.getWifiName(context);
    if ((str != null) && (!str.isEmpty())) {
      if (LeicaSdk.validDeviceName(str))
      {
        Logs.log(Logs.LogTypes.verbose, "Already connected to AP device");
      }
      else
      {
        try
        {
          InetSocketAddress localInetSocketAddress = new InetSocketAddress(b(), 22222);
          if ((d == null) || (d.isClosed()))
          {
            d = new DatagramSocket(localInetSocketAddress.getPort());
            Logs.log(Logs.LogTypes.verbose, "created new discover socket");
          }
          d.setSoTimeout(2000);
          d.setBroadcast(true);
          d.setReuseAddress(true);
          byte[] arrayOfByte = (WifiCommand.getCommand(Types.Commands.Discover) + WifiCommand.getCommand(Types.Commands.Terminator)).getBytes();
          DatagramPacket localDatagramPacket = new DatagramPacket(arrayOfByte, arrayOfByte.length, localInetSocketAddress);
          Logs.log(Logs.LogTypes.verbose, "sending " + Arrays.toString(localDatagramPacket.getData()) + " in discover packet");
          stopDiscovery = false;
          shouldScan = true;
          d.send(localDatagramPacket);
          Logs.log(Logs.LogTypes.verbose, "sent discover packet");
          long l = System.currentTimeMillis();
          while ((System.currentTimeMillis() <= l + 2000L - 5L) && (!stopDiscovery)) {
            a();
          }
          d.close();
        }
        catch (IOException localIOException)
        {
          Logs.log(Logs.LogTypes.exception, "Not able to find devices in this try", localIOException);
        }
        if (!shouldScan)
        {
          Logs.log(Logs.LogTypes.verbose, "shouldScan is false, return");
        }
        else
        {
          if (findTimer == null)
          {
            Logs.log(Logs.LogTypes.verbose, "created timer");
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
          }, 1500L);
        }
      }
    }
  }
  
  private void a()
  {
    int i = 0;
    ReceivedData localReceivedData = new ReceivedData();
    DatagramPacket localDatagramPacket = new DatagramPacket(new byte['က'], 4096);
    try
    {
      if (!d.isClosed())
      {
        d.setReceiveBufferSize(4096);
        d.receive(localDatagramPacket);
      }
    }
    catch (SocketTimeoutException localSocketTimeoutException)
    {
      i = 1;
    }
    catch (SocketException localSocketException)
    {
      Logs.log(Logs.LogTypes.verbose, "Socket error");
    }
    catch (IOException localIOException)
    {
      Logs.log(Logs.LogTypes.verbose, "IO error");
    }
    if (i == 0)
    {
      byte[] arrayOfByte = localDatagramPacket.getData();
      for (int j = 0; (j < arrayOfByte.length) && (arrayOfByte[j] != 13); j++) {}
      String str1 = new String(Arrays.copyOfRange(arrayOfByte, 0, j + 1));
      try
      {
        localReceivedData.parseReceivedWifiData(str1);
        String str2 = null;
        if (dataPacket != null) {
          str2 = ((ReceivedWifiDataPacket)dataPacket).getSerialNumber();
        }
        if (str2.equals(""))
        {
          Logs.log(Logs.LogTypes.verbose, "Failed to get serialnumber from: " + str1);
          i = 1;
        }
        if (i == 0)
        {
          String str3 = localDatagramPacket.getAddress().toString().substring(1);
          if (str3.equals("192.168.87.81")) {
            Logs.log(Logs.LogTypes.codeerror, "DISTO \"" + str3 + "\" is in Hotspot mode.");
          } else {
            foundAvailableApDevice("DISTO " + str2, str3);
          }
        }
      }
      catch (Exception localException)
      {
        Logs.log(Logs.LogTypes.verbose, "Exception: " + str1 + "Exception Message: " + localException.getMessage());
      }
    }
  }
  
  public void stopScan()
  {
    shouldScan = false;
  }
  
  public void stopDiscovery()
  {
    stopDiscovery = true;
    if ((d != null) && (!d.isClosed())) {
      d.close();
    }
  }
  
  public boolean checkConnectionMethodsAvailable()
  {
    if (c.isWifiEnabled())
    {
      setState(BaseConnectionManager.ConnectionState.disconnected, true);
      Logs.log(Logs.LogTypes.debug, "Connection State changed: " + getState().toString());
      return true;
    }
    setState(BaseConnectionManager.ConnectionState.disconnected, true);
    Logs.log(Logs.LogTypes.debug, "Connection State changed: " + getState().toString());
    return false;
  }
  
  public boolean verifyConnection(String paramString)
  {
    Logs.log(Logs.LogTypes.verbose, "parameter: " + paramString);
    if (Build.VERSION.SDK_INT < 21) {
      return true;
    }
    try
    {
      InetAddress localInetAddress = InetAddress.getByName(paramString);
      return localInetAddress.isReachable(12000);
    }
    catch (UnknownHostException localUnknownHostException)
    {
      Logs.log(Logs.LogTypes.exception, "UnknownHostException ", localUnknownHostException);
      return false;
    }
    catch (IOException localIOException)
    {
      Logs.log(Logs.LogTypes.exception, "IOException ", localIOException);
    }
    return false;
  }
  
  public void connect()
  {
    if (verifyConnection(getIP()))
    {
      setState(BaseConnectionManager.ConnectionState.connecting, true);
      Logs.log(Logs.LogTypes.debug, "Connecting with the device - Connection State changed: " + getState().toString());
      connectToDevice();
    }
    else
    {
      Logs.log(Logs.LogTypes.debug, "IP not reachable");
      ErrorObject.sendErrorAPDeviceIpNotReachable(errorListener, this);
    }
  }
  
  protected void connectToDevice()
  {
    if (WifiHelper.getWifiName(context) == null)
    {
      setState(BaseConnectionManager.ConnectionState.disconnected, true);
      Logs.log(Logs.LogTypes.debug, "Connection State changed: " + getState().toString());
      return;
    }
    try
    {
      InetSocketAddress localInetSocketAddress = new InetSocketAddress(getIP(), 22222);
      socket = new Socket();
      socket.connect(localInetSocketAddress, 3000);
      Logs.log(Logs.LogTypes.debug, "connect socket successful");
      setState(BaseConnectionManager.ConnectionState.connected, true);
      d();
    }
    catch (IOException localIOException)
    {
      Logs.log(Logs.LogTypes.exception, "IOException", localIOException);
      setState(BaseConnectionManager.ConnectionState.disconnected, true);
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.exception, "Exception", localException);
      setState(BaseConnectionManager.ConnectionState.disconnected, true);
    }
  }
  
  private InetAddress b()
    throws IOException
  {
    DhcpInfo localDhcpInfo = c.getDhcpInfo();
    if (localDhcpInfo == null) {
      return InetAddress.getByName("255.255.255.255");
    }
    int i = ipAddress & netmask | netmask ^ 0xFFFFFFFF;
    byte[] arrayOfByte = new byte[4];
    for (int j = 0; j < 4; j++) {
      arrayOfByte[j] = ((byte)(i >> j * 8 & 0xFF));
    }
    InetAddress localInetAddress = InetAddress.getByAddress(arrayOfByte);
    Logs.log(Logs.LogTypes.verbose, "BroadcastAddress: " + localInetAddress);
    return localInetAddress;
  }
  
  public void enableFunctionality() {}
  
  public void killConnection()
  {
    super.killConnection();
    c();
  }
  
  private void c()
  {
    if (e == null) {
      return;
    }
    e.interrupt();
    e = null;
    h = false;
  }
  
  private void d()
  {
    if (e != null)
    {
      Logs.log(Logs.LogTypes.verbose, "pokeConnectionThread already running");
    }
    else
    {
      h = true;
      WaitAmoment localWaitAmoment = new WaitAmoment();
      localWaitAmoment.waitAmoment(5000L);
      e = new Thread(new Runnable()
      {
        public void run()
        {
          for (;;)
          {
            if (!APConnectionManager.a(APConnectionManager.this))
            {
              Logs.log(Logs.LogTypes.debug, "should not check connection");
              break;
            }
            try
            {
              Thread.sleep(10000L);
            }
            catch (InterruptedException localInterruptedException)
            {
              Logs.log(Logs.LogTypes.verbose, "interrupted, return! stop poking");
              break;
            }
            synchronized (socket)
            {
              if (!socket.isClosed())
              {
                APConnectionManager.a(APConnectionManager.this, false);
                if (Build.VERSION.SDK_INT < 21)
                {
                  try
                  {
                    boolean bool = Inet4Address.getByName(getIP()).isReachable(3000);
                    if (bool == true)
                    {
                      Logs.log(Logs.LogTypes.verbose, "poke was successful (isReachable())");
                      APConnectionManager.a(APConnectionManager.this, true);
                      APConnectionManager.a(APConnectionManager.this, 0);
                    }
                    else
                    {
                      Logs.log(Logs.LogTypes.verbose, "poke was not successful (isReachable)");
                    }
                  }
                  catch (IOException localIOException)
                  {
                    Logs.log(Logs.LogTypes.exception, "Error caused by: ", localIOException);
                  }
                }
                else if (verifyConnection(getIP()) == true)
                {
                  Logs.log(Logs.LogTypes.verbose, "poke was successful (verify connection)");
                  APConnectionManager.a(APConnectionManager.this, true);
                  APConnectionManager.a(APConnectionManager.this, 0);
                }
                else
                {
                  Logs.log(Logs.LogTypes.verbose, "poke was not successful (verify connection)");
                }
              }
            }
          }
        }
      });
      e.start();
      Thread localThread = new Thread(new Runnable()
      {
        public void run()
        {
          do
          {
            for (;;)
            {
              if (!APConnectionManager.a(APConnectionManager.this))
              {
                Logs.log(Logs.LogTypes.verbose, "should not check connection");
                return;
              }
              try
              {
                Thread.sleep(8000L);
              }
              catch (InterruptedException localInterruptedException)
              {
                Logs.log(Logs.LogTypes.verbose, "interrupted, return! stop checking poke");
                return;
              }
              if (!APConnectionManager.b(APConnectionManager.this))
              {
                APConnectionManager.c(APConnectionManager.this);
                Logs.log(Logs.LogTypes.verbose, "poke was not successful. pokeNotSuccessfulCounter: " + APConnectionManager.d(APConnectionManager.this));
                break;
              }
              Logs.log(Logs.LogTypes.verbose, "poke was successful");
              APConnectionManager.a(APConnectionManager.this, 0);
            }
          } while (APConnectionManager.d(APConnectionManager.this) < 2);
          Logs.log(Logs.LogTypes.debug, "poke was not successful too many times, seems like device is not connected. kill connection!");
          killConnection();
        }
      });
      localThread.start();
    }
  }
}
