package de.appwerft.disto;

import java.util.ArrayList;
import java.util.List;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.PermissionException;
import ch.leica.sdk.Listeners.ErrorListener;

@Kroll.proxy(creatableInModule = TidistoModule.class, propertyAccessors = { TidistoModule.PROPERTY_ONFOUND })
public class DeviceManagerProxy extends KrollProxy implements
		DeviceManager.FoundAvailableDeviceListener, ErrorListener {
	private Context ctx;
	private DeviceManager deviceManager;
	boolean findDevicesRunning = false;
	boolean activityStopped = true;
	public static final String PROPERTY_ONFOUND = TidistoModule.PROPERTY_ONFOUND;
	public static final String LCAT = TidistoModule.LCAT;

	@Kroll.method
	public void findAvailableDevices() {
		// opened for all device types
		LeicaSdk.setScanConfig(true, true, true, true);
		deviceManager.setFoundAvailableDeviceListener(this);
		deviceManager.setErrorListener(this);
		try {
			// method below crashes the app (lost reference?)
			deviceManager.findAvailableDevices(ctx);
		} catch (PermissionException e) {
			Log.e(LCAT, "Missing permission: " + e.getMessage());
		}
		findDevicesRunning = true;
	}

	@Kroll.method
	public void stopFindingDevices() {
		findDevicesRunning = false;
		if (deviceManager != null)
			deviceManager.stopFindingDevices();
	}
	@Override
	public void onError(ErrorObject err, Device device) {
		Log.e(LCAT, err.getErrorMessage());
	}

	@Override
	public void onAvailableDeviceFound(final Device device) {
		Log.i(LCAT,
				"Model: " + device.getModel() + " Name: "
						+ device.getDeviceName());
	}

	public DeviceManagerProxy() {
		super();
		TiApplication app = TiApplication.getInstance();
		if (app != null) {
			ctx = app.getApplicationContext();
			deviceManager = DeviceManager.getInstance(ctx);
		} else
			Log.e(LCAT, "app == null");	
	}

	@Kroll.method
	public KrollDict getConnectedDevices() {
		KrollDict res = new KrollDict();
		List<DeviceProxy> deviceArray = new ArrayList<DeviceProxy>();
		List<Device> devices = deviceManager.getConnectedDevices();
		for (Device device : devices) {
			deviceArray.add(new DeviceProxy(device));
		}
		res.put("devices", deviceArray.toArray(new DeviceProxy[devices.size()]));
		return res;
	}
}
