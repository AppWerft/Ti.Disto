package de.appwerft.disto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.Device.ConnectionState;
import ch.leica.sdk.Devices.DeviceManager;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.ErrorHandling.PermissionException;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Listeners.ErrorListener;



// https://github.com/AppWerft/Ti.Disto/blob/master/DISTO%20SDK%20united%20for%20Android%20v1.0.0_EN/ImplementationGuide/LeicaSdkQuickStartSampleApp/app/src/main/java/leica/ch/quickstartapp/MainActivity.java#L39
@Kroll.proxy(creatableInModule = TidistoModule.class, propertyAccessors = { "onFound" })
public class DeviceManagerProxy extends KrollProxy implements
		DeviceManager.FoundAvailableDeviceListener, Device.ConnectionListener, 
		ErrorListener {
	private Device currentDevice;
	private Context ctx;
 	private DeviceManager deviceManager;
	private Timer connectionTimeoutTimer;
	private TimerTask connectionTimeoutTask;
	private Timer findDevicesTimer;
	boolean findDevicesRunning = false;
	boolean activityStopped = true;
	private Map<Device, Boolean> connectionAttempts = new HashMap<>();
	private Device currentConnectionAttemptToDevice = null;
	public static boolean DEBUG = false;
	private List<Device> availableDevices = new ArrayList<>();
	private Activity activity;
	public static final String LCAT = TidistoModule.LCAT;

	@Override
	public void onError(ErrorObject err, Device device) {
		Log.e(LCAT, err.getErrorMessage());
	}

	@Override
	public void onConnectionStateChanged(final Device device,
			ConnectionState state) {
		Log.i(LCAT, device.getModel() + "  " + state);
	}

	@Override
	public void onAvailableDeviceFound(final Device device) {
		Log.i(LCAT,
				"Model: " + device.getModel() + " Name: "
						+ device.getDeviceName());
		synchronized (availableDevices) {
			for (Device availableDevice : availableDevices) {
				if (availableDevice.getDeviceID().equalsIgnoreCase(
						device.getDeviceID())) {
					return;
				}
			}
			KrollDict res = new KrollDict();
			res.put("device", new DeviceProxy(device));
			if (device != null)
				availableDevices.add(device);
			if (hasProperty("onFound")) {
				KrollFunction onFound = (KrollFunction) getProperty("onFound");
				onFound.call(getKrollObject(), res);
			}
		}
		currentDevice = device;
	}

	public DeviceManagerProxy() {
		super();
	}
	
	@Override
	public void handleCreationDict(
			@Kroll.argument(optional = true) KrollDict opts) {
		super.handleCreationDict(opts);
		ctx = TiApplication.getInstance().getApplicationContext();
		deviceManager = DeviceManager.getInstance(ctx);
	}

	@Kroll.method
	public DeviceManagerProxy enableBLE() {
		if (deviceManager != null
				&& deviceManager.checkBluetoothAvailibilty() == false)
			deviceManager.enableBLE();
		return this;
	}

	@Kroll.method
	public void findAvailableDevices() {
		// https://github.com/AppWerft/Ti.Disto/blob/master/DISTO%20SDK%20united%20for%20Android%20v1.0.0_EN/ImplementationGuide/LeicaSdkQuickStartSampleApp/app/src/main/java/leica/ch/quickstartapp/MainActivity.java#L374
		deviceManager.setFoundAvailableDeviceListener(this);
		deviceManager.setErrorListener(this);
		try {
			// https://github.com/AppWerft/Ti.Disto/blob/master/DISTO%20SDK%20united%20for%20Android%20v1.0.0_EN/ImplementationGuide/LeicaSdkQuickStartSampleApp/app/src/main/java/leica/ch/quickstartapp/MainActivity.java#L378
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
	public void onStart(Activity activity) {
		Log.i(LCAT, ">>>>>>>>>>>>>>>>>>>>>>>>>  onStart");
		super.onStart(activity);
	}

	@Override
	public void onResume(Activity activity) {
		Log.i(LCAT, ">>>>>>>>>>>>>>>>>>>>>>>>>  onResume");
		super.onResume(activity);
	}

	@Override
	public void onCreate(Activity activity, Bundle savedInstanceState) {
		Log.i(LCAT, ">>>>>>>>>>>>>>>>>>>>>>>>>  onCreate");
		super.onCreate(activity, savedInstanceState);
	}

	
}
