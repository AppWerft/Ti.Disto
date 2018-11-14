package de.appwerft.disto;

import java.util.ArrayList;
import java.util.List;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;

import android.content.Context;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;
import ch.leica.sdk.Devices.YetiDevice;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.PermissionException;
import ch.leica.sdk.Listeners.ErrorListener;

@Kroll.module(parentModule = TidistoModule.class)
public class DeviceManagerModule extends TidistoModule implements
		DeviceManager.FoundAvailableDeviceListener, ErrorListener {
	private Context ctx;
	private DeviceManager deviceManager;
	boolean findDevicesRunning = false;
	boolean activityStopped = true;
	private KrollFunction onFoundCallback = null;
	private KrollFunction onErrorCallback = null;

	public static final String LCAT = TidistoModule.LCAT;

	public DeviceManagerModule() {
		super();

	}
	@Override
	public void handleCreationDict(KrollDict options) {
		
		super.handleCreationDict(options);
	}

	@Kroll.method
	public void findAvailableDevices(KrollDict options) {
		if (options.containsKeyAndNotNull("onfound"))
			onFoundCallback = (KrollFunction) options.get("onfound");
		if (options.containsKeyAndNotNull("onerror"))
			onErrorCallback = (KrollFunction) options.get("onerror");
		Log.d(LCAT, "handleCreationDict finished");
		TiApplication app = TiApplication.getInstance();
		if (app != null) {
			ctx = app.getApplicationContext();
			deviceManager = DeviceManager.getInstance(ctx);
		} else
			Log.e(LCAT, "app == null");
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
		Log.d(LCAT, "findAvailableDevices started");
	}

	@Override
	public void onError(ErrorObject err, Device device) {
		KrollDict event = new KrollDict();
		event.put("error", err.getErrorMessage());
		if (onErrorCallback != null)
			onErrorCallback.callAsync(getKrollObject(), event);
	}

	@Override
	public void onAvailableDeviceFound(final Device device) {
		if (device instanceof YetiDevice) {
			String name = device.getDeviceName();
			KrollDict event = new KrollDict();
			event.put("device", new DeviceProxy(device));
			event.put("type", device.getClass().getSimpleName());
			event.put("id", device.getDeviceID());
			event.put("name", name);
			event.put("model", device.modelName);
			event.put("success", true);
			Log.d(LCAT, event.toString());
			if (onFoundCallback != null) {
				onFoundCallback.callAsync(getKrollObject(), event);
			} else
				Log.e(LCAT, "onFound not defined!");
		}
	}

	/*
	 * if (TiApplication.isUIThread()) handleStopFindingDevices(); else
	 * TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(
	 * MSG_STOP)); }
	 */

	@Kroll.method
	public void stopFindingDevices() {
		findDevicesRunning = false;
		if (deviceManager != null)
			deviceManager.stopFindingDevices();
		else
			Log.w(LCAT, "try to stop findingDevices, but deviceManager is null");
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
