package ch.leica.sdk.Listeners;

import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.ErrorHandling.ErrorObject;

public abstract interface ErrorListener
{
  public abstract void onError(ErrorObject paramErrorObject, Device paramDevice);
}
