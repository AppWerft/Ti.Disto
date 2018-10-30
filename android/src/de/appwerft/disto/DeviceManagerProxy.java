package de.appwerft.disto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;

import android.content.Context;
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
		Log.i(LCAT, "Hurra! |||||||||||||||||||||||||||||||");
		Log.i(LCAT, device.getDeviceName());
		Log.i(LCAT, device.getModel());
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
		}
		currentDevice = device;
	}

	public DeviceManagerProxy() {
		super();
	}

	@Override
	public void handleCreationDict(
			@Kroll.argument(optional = true) KrollDict opts) {
		Log.i(LCAT,"handleCreationDict() called");
		
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
		Log.i(LCAT,"findAvailableDevices() called");
		ctx = TiApplication.getInstance().getApplicationContext();
		deviceManager = DeviceManager.getInstance(ctx);
		Log.i(LCAT,"deviceManager created: " + deviceManager.toString());
		deviceManager.setErrorListener(this);
		deviceManager.setFoundAvailableDeviceListener(this);
		Log.i(LCAT, "listener set deviceManager.checkBluetoothAvailibilty "
				+ deviceManager.checkBluetoothAvailibilty());
		try {
			deviceManager.findAvailableDevices(ctx);
			Log.i(LCAT, "search started!");
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
		deviceManager.stopFindingDevices();
	}

	

}
