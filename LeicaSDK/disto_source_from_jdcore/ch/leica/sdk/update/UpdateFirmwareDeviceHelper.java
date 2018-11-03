package ch.leica.sdk.update;

import android.util.Log;
import ch.leica.sdk.Devices.YetiDevice;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.commands.response.ResponsePlain;

public class UpdateFirmwareDeviceHelper
{
  public UpdateFirmwareDeviceHelper() {}
  
  public boolean isComponentConnected(YetiDevice paramYetiDevice, String paramString1, String paramString2)
  {
    ResponsePlain localResponsePlain = paramYetiDevice.getUpdate().getValue(paramString1, paramYetiDevice);
    if ((localResponsePlain.getError() != null) && (localResponsePlain.getError().getErrorCode() != 5110))
    {
      Log.i("isComponentConnected", "Component is not connected");
      return false;
    }
    return true;
  }
  
  public boolean isNumeric(String paramString)
  {
    return paramString.matches("^[0-9]*$");
  }
}
