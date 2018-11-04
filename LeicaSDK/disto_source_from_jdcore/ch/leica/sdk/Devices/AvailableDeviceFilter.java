package ch.leica.sdk.Devices;

import ch.leica.a.a.a;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;

public class AvailableDeviceFilter {
	public AvailableDeviceFilter() {
	}

	public boolean isDeviceAllowed(Device paramDevice) {
		Logs.log(Logs.LogTypes.debug, "check: " + deviceID);
		String str = "none";
		if ((LeicaSdk.isDistoGenericName(deviceName) == true)
				|| (LeicaSdk.isYetiName(deviceName) == true)) {
			str = "leica";
		} else if (LeicaSdk.isDisto3DName(deviceName) == true) {
			str = "leica";
			if (a(paramDevice)) {
				str = "geomax";
			}
		}
		Logs.log(Logs.LogTypes.debug, "filtertype: " + str);
		boolean bool = false;
		if (str.equalsIgnoreCase("leica")) {
			bool = a.a();
		} else if (str.equalsIgnoreCase("geomax")) {
			bool = a.b();
		}
		Logs.log(Logs.LogTypes.debug, "result: " + bool);
		return bool;
	}

	boolean a(Device paramDevice) {
		if (!deviceName.startsWith("3DD_0")) {
			return false;
		}
		String str = deviceName.substring(5);
		try {
			Integer localInteger = new Integer(str);
			if (localInteger == null) {
				return false;
			}
			if ((localInteger.intValue() < 1781000)
					|| (localInteger.intValue() > 1782999)) {
				return false;
			}
		} catch (Exception localException) {
			return false;
		}
		return true;
	}
}
