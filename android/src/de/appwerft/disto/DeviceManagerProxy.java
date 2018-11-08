package de.appwerft.disto;

import java.util.ArrayList;
import java.util.List;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;
import ch.leica.sdk.Devices.YetiDevice;
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
	KrollFunction onFoundCallback ;
	public static final String PROPERTY_ONFOUND = TidistoModule.PROPERTY_ONFOUND;
	public static final String LCAT = TidistoModule.LCAT;
	private static final int MSG_START = 500;
	private static final int MSG_STOP = 501;

	public DeviceManagerProxy() {
		super();
		if (hasProperty(PROPERTY_ONFOUND)) {
			onFoundCallback=(KrollFunction)getProperty(PROPERTY_ONFOUND);
		}
	}

	@Kroll.method
	public void findAvailableDevices() {
		if (TiApplication.isUIThread())
			handleFindAvailableDevices();
		else
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(
					MSG_START));
	}

	@Override
	public boolean handleMessage(Message msg) {
		Log.d(LCAT, "handleMessage " + msg.what);
		AsyncResult result = null;
		switch (msg.what) {
		case MSG_START: {
			result = (AsyncResult) msg.obj;
			handleFindAvailableDevices();
			result.setResult(null);
			return true;
		}
		case MSG_STOP: {
			result = (AsyncResult) msg.obj;
			handleStopFindingDevices();
			result.setResult(null);
			return true;
		}
		default: {
			return super.handleMessage(msg);
		}
		}
	}

	private void handleFindAvailableDevices() {
		TiApplication app = TiApplication.getInstance();
		if (app != null) {
			ctx = app.getApplicationContext();
			deviceManager = DeviceManager.getInstance(ctx);
		} else
			Log.e(LCAT, "app == null");
		// only YETI (X3*)
		
		LeicaSdk.setScanConfig(false, false, true, false);
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

	@Override
	public void onError(ErrorObject err, Device device) {
		Log.e(LCAT, err.getErrorMessage());
	}

	@Override
	public void onAvailableDeviceFound(final Device device) {
		Log.i(LCAT, "FOUND: "+ device.getDeviceName());
		KrollDict event = new KrollDict();
		boolean isYeti = LeicaSdk.isYetiName(device.getDeviceName());
		YetiDeviceProxy x3 = new YetiDeviceProxy(device);
	 	event.put("device", x3);
		event.put("isYeti", isYeti);
		event.put("type", device.getClass().getName());
		
		event.put("success", true);
		if (onFoundCallback!=null) {
			onFoundCallback.callAsync(getKrollObject(), event);
		}
		deviceManager.stopFindingDevices();
	}

	@Kroll.method
	public void stopFindingDevices() {
		if (TiApplication.isUIThread())
			handleStopFindingDevices();
		else
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(
					MSG_STOP));
	}

	private void handleStopFindingDevices() {
		findDevicesRunning = false;
		if (deviceManager != null)
			deviceManager.stopFindingDevices();
	}

	@Kroll.method
	public KrollDict getConnectedDevices() {
		KrollDict res = new KrollDict();
		List<YetiDeviceProxy> deviceArray = new ArrayList<YetiDeviceProxy>();
		List<Device> devices = deviceManager.getConnectedDevices();
		for (Device device : devices) {
			deviceArray.add(new YetiDeviceProxy(device));
		}
		res.put("devices", deviceArray.toArray(new YetiDeviceProxy[devices.size()]));
		return res;
	}

}
