package ch.leica.sdk.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;
import ch.leica.sdk.Devices.Device.LiveImageSpeed;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.WrongDataException;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.Utilities.WaitAmoment;
import ch.leica.sdk.Utilities.WifiHelper;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.ReceivedDataPacket;
import ch.leica.sdk.commands.WifiCommand;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public final class HSConnectionManager
  extends BaseConnectionManager
{
  private WifiManager c;
  private String d;
  private List<ScanResult> e;
  private IntentFilter f;
  private BroadcastReceiver g;
  private boolean h;
  private DatagramSocket i = null;
  private boolean j = false;
  private boolean k = false;
  private HandlerThread l;
  private Handler m;
  
  public HSConnectionManager(WifiManager paramWifiManager, Context paramContext)
  {
    super(paramContext);
    c = paramWifiManager;
    setContext(paramContext);
    setIP("192.168.87.81");
    h = false;
    f = new IntentFilter("android.net.wifi.SCAN_RESULTS");
    g = new a(null);
    if (l != null) {
      l.interrupt();
    }
    l = new HandlerThread("HSConnectionManager_LiveImageNotifyThread_" + System.currentTimeMillis(), 10);
    l.start();
    m = new Handler(l.getLooper());
  }
  
  public void setConnectionParameters(Object... paramVarArgs)
  {
    setSSID((String)paramVarArgs[0]);
    setIP("192.168.87.81");
  }
  
  public void registerReceivers(Context paramContext)
  {
    if (k == true) {
      return;
    }
    k = true;
    try
    {
      super.registerReceivers(paramContext);
      paramContext.registerReceiver(g, f);
      Logs.log(Logs.LogTypes.debug, "scanFinishedReceiver registered");
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.debug, "Error Registering Receivers. NF");
    }
  }
  
  public void unregisterReceivers()
  {
    if (!k) {
      return;
    }
    k = false;
    try
    {
      context.unregisterReceiver(g);
      Logs.log(Logs.LogTypes.debug, "scanFinishedReceiver registered");
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.debug, "Error UnRegistering Receivers. NF");
    }
  }
  
  public synchronized void findAvailableDevices()
  {
    try
    {
      if (shouldScan == true)
      {
        Logs.log(Logs.LogTypes.debug, ". is already shouldScan ");
        stopScan();
      }
      registerReceivers(context);
      c.startScan();
      if (e != null) {
        e.clear();
      }
      shouldScan = true;
      if (findTimer == null) {
        findTimer = new Timer();
      }
      findTimer.schedule(new TimerTask()
      {
        public void run()
        {
          Logs.log(Logs.LogTypes.debug, "shouldScan: " + shouldScan);
          if (shouldScan == true) {
            findAvailableDevices();
          }
        }
      }, 5000L);
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.debug, "Error Finding Available Devices");
    }
  }
  
  public synchronized void stopScan()
  {
    unregisterReceivers();
    stopDiscovery();
    shouldScan = false;
  }
  
  private synchronized void a()
  {
    e = c.getScanResults();
    if (e != null) {
      for (int n = 0; n < e.size(); n++)
      {
        String str = e.get(n)).SSID;
        Logs.log(Logs.LogTypes.verbose, "WLAN: " + str);
        if (LeicaSdk.validDeviceName(str)) {
          foundAvailableHotspotDevice(str);
        }
      }
    }
    setState(BaseConnectionManager.ConnectionState.disconnected, true);
  }
  
  public void stopDiscovery() {}
  
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
    Logs.log(Logs.LogTypes.debug, "parameter: " + paramString);
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
    String str = WifiHelper.getWifiName(context);
    if (str == null)
    {
      ErrorObject.sendErrorIncorrectSSID(errorListener, this);
      setState(BaseConnectionManager.ConnectionState.disconnected, true);
      return;
    }
    d = str;
    Logs.log(Logs.LogTypes.debug, "this.wifiName: " + d);
    if (!LeicaSdk.validDeviceName(d))
    {
      Logs.log(Logs.LogTypes.debug, "wifiName does not match");
      setState(BaseConnectionManager.ConnectionState.disconnected, true);
      return;
    }
    if (!verifyConnection(getIP()))
    {
      Logs.log(Logs.LogTypes.debug, "IP not reachable");
      ErrorObject.sendErrorHotspotDeviceIpNotReachable(errorListener, this);
      return;
    }
    setState(BaseConnectionManager.ConnectionState.connecting, true);
    connectToDevice();
  }
  
  protected void connectToDevice()
  {
    setState(BaseConnectionManager.ConnectionState.connecting, true);
    try
    {
      Logs.log(Logs.LogTypes.verbose, "try create socket");
      InetSocketAddress localInetSocketAddress = new InetSocketAddress(getIP(), 22222);
      socket = new Socket();
      socket.connect(localInetSocketAddress, 10000);
      Logs.log(Logs.LogTypes.debug, "create socket OK");
      setState(BaseConnectionManager.ConnectionState.connected, true);
      Logs.log(Logs.LogTypes.debug, "is connected.");
    }
    catch (IOException localIOException)
    {
      Logs.log(Logs.LogTypes.exception, "IOException: e.message: " + localIOException.getMessage(), localIOException);
      if (errorListener != null) {
        errorListener.onError(new ErrorObject(2502, "Could not connect to device. "), null);
      }
      setState(BaseConnectionManager.ConnectionState.disconnected, true);
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.exception, "Exception", localException);
      setState(BaseConnectionManager.ConnectionState.disconnected, true);
    }
  }
  
  public void killConnection()
  {
    super.killConnection();
    stopScan();
    j = true;
    disconnectLiveChannel();
    setState(BaseConnectionManager.ConnectionState.disconnected, true);
    Logs.log(Logs.LogTypes.debug, "Connection State changed: " + getState().toString());
  }
  
  public void enableFunctionality()
  {
    c.setWifiEnabled(true);
  }
  
  public void connectLiveChannel(Device.LiveImageSpeed paramLiveImageSpeed)
  {
    if (h == true)
    {
      Logs.log(Logs.LogTypes.verbose, "live channel already connected");
      return;
    }
    h = true;
    while ((h) && (j != true))
    {
      InetSocketAddress localInetSocketAddress = new InetSocketAddress("192.168.87.81", 22222);
      try
      {
        if ((i == null) || (i.isClosed())) {
          i = new DatagramSocket(22222);
        }
        i.setSoTimeout(60);
        i.setReuseAddress(true);
      }
      catch (SocketException localSocketException1)
      {
        Logs.log(Logs.LogTypes.exception, "Failed opening live channel", localSocketException1);
        break;
      }
      try
      {
        byte[] arrayOfByte = (WifiCommand.getCommand(Types.Commands.LiveImage) + WifiCommand.getCommand(Types.Commands.Terminator)).getBytes();
        DatagramPacket localDatagramPacket = new DatagramPacket(arrayOfByte, arrayOfByte.length, localInetSocketAddress);
        i.send(localDatagramPacket);
      }
      catch (SocketException localSocketException2)
      {
        Logs.log(Logs.LogTypes.exception, "Failed constructing a datagram packet", localSocketException2);
        break;
      }
      catch (IOException localIOException)
      {
        Logs.log(Logs.LogTypes.exception, "Failed writing to live channel socket", localIOException);
        break;
      }
      a(i);
      WaitAmoment localWaitAmoment = new WaitAmoment();
      int n = 4;
      if (paramLiveImageSpeed != null) {
        switch (3.a[paramLiveImageSpeed.ordinal()])
        {
        case 1: 
          n = 1;
          break;
        case 2: 
          n = 2;
          break;
        case 3: 
          n = 4;
          break;
        case 4: 
          n = 8;
          break;
        case 5: 
          n = 16;
        }
      }
      localWaitAmoment.waitAmoment(975 / n);
    }
  }
  
  public void disconnectLiveChannel()
  {
    h = false;
    if ((i != null) && (!i.isClosed())) {
      i.close();
    }
  }
  
  private void a(DatagramSocket paramDatagramSocket)
  {
    DatagramPacket localDatagramPacket = new DatagramPacket(new byte[65507], 65507);
    try
    {
      paramDatagramSocket.setReceiveBufferSize(65507);
      paramDatagramSocket.receive(localDatagramPacket);
    }
    catch (SocketTimeoutException localSocketTimeoutException)
    {
      return;
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.exception, "Caused by: ", localException);
      return;
    }
    byte[] arrayOfByte = Arrays.copyOfRange(localDatagramPacket.getData(), localDatagramPacket.getOffset(), localDatagramPacket.getLength());
    if (arrayOfByte.length <= 64) {
      return;
    }
    final ReceivedData localReceivedData = new ReceivedData();
    localReceivedData.setLiveImagePacket(arrayOfByte);
    if (dataPacket == null)
    {
      Logs.log(Logs.LogTypes.verbose, "data packets null or empty");
      return;
    }
    try
    {
      if (dataPacket.getImage() == null)
      {
        Logs.log(Logs.LogTypes.verbose, "live image object is null");
        return;
      }
    }
    catch (WrongDataException localWrongDataException)
    {
      Logs.log(Logs.LogTypes.exception, "live image object is null", localWrongDataException);
      return;
    }
    if (wifiReceivedDataListener != null) {
      m.post(new Runnable()
      {
        public void run()
        {
          try
          {
            wifiReceivedDataListener.onLiveImageDataReceived(localReceivedData);
          }
          catch (DeviceException localDeviceException)
          {
            Logs.log(Logs.LogTypes.exception, "Caused by:", localDeviceException);
          }
        }
      });
    } else {
      Logs.log(Logs.LogTypes.debug, "listener is null");
    }
  }
  
  private class a
    extends BroadcastReceiver
  {
    private a() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if ((!shouldScan) && (HSConnectionManager.a(HSConnectionManager.this) == true))
      {
        unregisterReceivers();
        return;
      }
      if (paramIntent.getAction().equals("android.net.wifi.SCAN_RESULTS"))
      {
        Logs.log(Logs.LogTypes.verbose, "Called listDistoSSIDS");
        HSConnectionManager.b(HSConnectionManager.this);
      }
    }
  }
}
