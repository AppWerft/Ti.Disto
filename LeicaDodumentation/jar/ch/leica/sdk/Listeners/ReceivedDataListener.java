package ch.leica.sdk.Listeners;

import ch.leica.sdk.commands.ReceivedData;

public abstract interface ReceivedDataListener
{
  public abstract void onAsyncDataReceived(ReceivedData paramReceivedData);
}
