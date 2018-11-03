package ch.leica.sdk.Utilities;

import android.content.Context;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public final class WifiHelper
{
  public WifiHelper() {}
  
  public static synchronized String getWifiName(Context paramContext)
  {
    WifiManager localWifiManager = (WifiManager)paramContext.getSystemService("wifi");
    if (localWifiManager.isWifiEnabled())
    {
      WifiInfo localWifiInfo = localWifiManager.getConnectionInfo();
      if (localWifiInfo != null)
      {
        NetworkInfo.DetailedState localDetailedState = WifiInfo.getDetailedStateOf(localWifiInfo.getSupplicantState());
        if ((localDetailedState == NetworkInfo.DetailedState.CONNECTED) || (localDetailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR)) {
          return localWifiInfo.getSSID().replaceAll("\"", "");
        }
      }
    }
    return null;
  }
}
