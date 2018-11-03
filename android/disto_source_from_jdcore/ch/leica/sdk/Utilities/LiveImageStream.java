package ch.leica.sdk.Utilities;

import android.os.Handler;
import android.os.HandlerThread;
import ch.leica.sdk.ErrorHandling.WrongDataException;
import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.ReceivedDataPacket;
import ch.leica.sdk.commands.WifiCommand;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

public class LiveImageStream
  extends Thread
{
  private Handler a;
  private FrameListener b;
  private volatile boolean c = false;
  private DatagramSocket d;
  private DatagramPacket e;
  private byte[] f;
  
  public LiveImageStream(InetSocketAddress paramInetSocketAddress, FrameListener paramFrameListener)
    throws SocketException
  {
    b = paramFrameListener;
    HandlerThread localHandlerThread = new HandlerThread("FrameNotifier");
    localHandlerThread.start();
    a = new Handler(localHandlerThread.getLooper());
    d = new DatagramSocket();
    byte[] arrayOfByte = (WifiCommand.getCommand(Types.Commands.LiveImage) + WifiCommand.getCommand(Types.Commands.Terminator)).getBytes();
    e = new DatagramPacket(arrayOfByte, arrayOfByte.length, paramInetSocketAddress);
    f = new byte[65507];
    d.setSoTimeout(1000);
    d.setReceiveBufferSize(arrayOfByte.length);
  }
  
  public void run()
  {
    int i = 0;
    int j = 150;
    while (c) {
      try
      {
        long l1 = System.currentTimeMillis();
        d.send(e);
        DatagramPacket localDatagramPacket = new DatagramPacket(f, f.length);
        d.receive(localDatagramPacket);
        Logs.log(Logs.LogTypes.debug, "...Received");
        if (localDatagramPacket.getLength() == i) {
          Logs.log(Logs.LogTypes.warn, "Frames have the same length");
        }
        i = localDatagramPacket.getLength();
        a.post(new a(Arrays.copyOfRange(localDatagramPacket.getData(), localDatagramPacket.getOffset(), localDatagramPacket.getLength())));
        long l2 = j - (System.currentTimeMillis() - l1);
        if (l2 > 0L) {
          sleep(l2);
        }
      }
      catch (IOException localIOException)
      {
        Logs.log(Logs.LogTypes.warn, "Current frame was not processed");
      }
      catch (InterruptedException localInterruptedException)
      {
        Logs.log(Logs.LogTypes.warn, "Wait for next frame failed failed");
      }
    }
  }
  
  public void startStream()
  {
    c = true;
    start();
  }
  
  public void stopStream()
  {
    c = false;
    try
    {
      join();
    }
    catch (InterruptedException localInterruptedException)
    {
      Logs.log(Logs.LogTypes.exception, "Join stopped live stream failed");
    }
  }
  
  private class a
    implements Runnable
  {
    byte[] a;
    
    public a(byte[] paramArrayOfByte)
    {
      a = paramArrayOfByte;
    }
    
    public void run()
    {
      if (a.length >= 64)
      {
        ReceivedData localReceivedData = new ReceivedData();
        localReceivedData.setLiveImagePacket(a);
        try
        {
          if ((dataPacket != null) && (dataPacket.getImage() != null) && (LiveImageStream.a(LiveImageStream.this) != null)) {
            LiveImageStream.a(LiveImageStream.this).onNextFrame(localReceivedData);
          }
        }
        catch (WrongDataException localWrongDataException)
        {
          Logs.log(Logs.LogTypes.warn, "Image parsing failed");
        }
      }
    }
  }
  
  public static abstract interface FrameListener
  {
    public abstract void onNextFrame(ReceivedData paramReceivedData);
  }
}
