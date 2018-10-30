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
import android.content.Context;
import android.os.Bundle;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.Device.ConnectionState;
import ch.leica.sdk.Devices.DeviceManager;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.PermissionException;
import ch.leica.sdk.Listeners.ErrorListener;

@Kroll.proxy(creatableInModule = TidistoModule.class, propertyAccessors = { "onFound" })
public class DeviceManagerProxy extends KrollProxy implements
		DeviceManager.FoundAvailableDeviceListener, Device.ConnectionListener,
		ErrorListener {
	private Device currentDevice;
	private Context ctx;
	private DeviceManager deviceManager;
	// needed for connection timeout
	private Timer connectionTimeoutTimer;
	private TimerTask connectionTimeoutTask;
	// to do infinite rounds of finding devices
	private Timer findDevicesTimer;
	boolean findDevicesRunning = false;
	boolean activityStopped = true;
	// to handle user cancel connection attempt
	private Map<Device, Boolean> connectionAttempts = new HashMap<>();
	private Device currentConnectionAttemptToDevice = null;
	public static boolean DEBUG = false;
	private List<Device> availableDevices = new ArrayList<>();

	public static final String LCAT = TidistoModule.LCAT;

	/**
     * Defines the default behavior when an error is notified.
     * Presents alert to user showsing a error message
     *
     * @param errorObject error object comes from different sources SDK or APP.
     */
	@Override
	public void onError(ErrorObject err, Device device) {
		Log.e(LCAT, err.getErrorMessage());

	}

	/**
     * Called when the connection state of a device changed
     *
     * @param device currently connected device
     * @param state  current device connection state
     */
	@Override
	public void onConnectionStateChanged(final Device device,
			ConnectionState state) {
		Log.i(LCAT, device.getModel() + "  " + state);

	}

	/**
     * called when a valid Leica device is found
     *
     * @param device the device
     */
	@Override
	public void onAvailableDeviceFound(final Device device) {
		Log.i(LCAT, "Hurra! |||||||||||||||||||||||||||||||");
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
		ctx = TiApplication.getInstance().getApplicationContext();
		deviceManager = DeviceManager.getInstance(ctx);
	}

	@Override
	public void handleCreationDict(
			@Kroll.argument(optional = true) KrollDict opts) {
		Log.i(LCAT, "handleCreationDict() called");

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
		Log.i(LCAT, "findAvailableDevices() called ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		deviceManager.setFoundAvailableDeviceListener(this);
		deviceManager.setErrorListener(this);
		try {
			deviceManager.findAvailableDevices(ctx);
		} catch (PermissionException e) {
			Log.e(LCAT, "Missing permission: " + e.getMessage());
		}
		findDevicesRunning = true;
	}

	@Kroll.method
	public void stopFindingDevices() {

		Log.i(LCAT,
				" Stop find Devices Task and set BroadcastReceivers to Null");
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
