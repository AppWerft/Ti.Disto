/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2017 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package de.appwerft.disto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.json.JSONException;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.Device.ConnectionState;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.ErrorHandling.PermissionException;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Types;
import ch.leica.sdk.Listeners.ErrorListener;

@Kroll.module(name = "Tidisto", id = "de.appwerft.disto", propertyAccessors = { "onScanResult" })
public class TidistoModule extends KrollModule  {

	/* Constants */
	@Kroll.constant
	public static final int DEVICE_TYPE_BLE = Types.DeviceType.Ble.ordinal();
	@Kroll.constant
	public static final int DEVICE_TYPE_DISTO = Types.DeviceType.Disto
			.ordinal();
	@Kroll.constant
	public static final int DEVICE_TYPE_YETI = Types.DeviceType.Yeti.ordinal();

	@Kroll.constant
	public static final int DEVICE_CONNECTION_STATE_CONNECTED = Device.ConnectionState.connected
			.ordinal();
	@Kroll.constant
	public static final int DEVICE_CONNECTION_STATE_DISCONNECTED = Device.ConnectionState.disconnected
			.ordinal();
	@Kroll.constant
	public static final int DEVICE_STATE_NORMAL = Device.DeviceState.normal
			.ordinal();
	@Kroll.constant
	public static final int DEVICE_STATE_UPDATE = Device.DeviceState.update
			.ordinal();
	@Kroll.constant
	public static final int CONNECTION_TYPE_WIFI_AP = Types.ConnectionType.wifiAP
			.ordinal();
	@Kroll.constant
	public static final int CONNECTION_TYPE_WIFI_HOTSPOT = Types.ConnectionType.wifiHotspot
			.ordinal();

	@Kroll.constant
	public static final int WIFI = 1;
	@Kroll.constant
	public static final int BLE = 2;
	@Kroll.constant
	public static final int BLUETOOTH = 2;
	
	List<Device> availableDevices = new ArrayList<>();
	// Standard Debugging variables
	public static final String LCAT = "TiDisto";
	public static final String PROPERTY_ONFOUND = "onFound";
	private KrollFunction Callback;
	
	/**
	 * Current selected device
	 */
	Device currentDevice;
	Context ctx;
	DeviceManager deviceManager;
	// needed for connection timeout
	Timer connectionTimeoutTimer;
	TimerTask connectionTimeoutTask;
	// to do infinite rounds of finding devices
	Timer findDevicesTimer;
	boolean activityStopped = true;
	// to handle user cancel connection attempt
	Map<Device, Boolean> connectionAttempts = new HashMap<>();
	Device currentConnectionAttemptToDevice = null;
	public static boolean DEBUG = false;

	public TidistoModule() {
		super();
		ctx = TiApplication.getInstance().getApplicationContext();
		deviceManager = DeviceManager.getInstance(ctx);
		
		
	}

	private void addLicenceKey() {
		ArrayList<String> keys = new ArrayList<>();
		String key = TiApplication.getInstance().getAppProperties().getString("DISTO_KEY", "");
		keys.add(key);
	}
	
	@Kroll.method
	public String getVersion() {
		return LeicaSdk.getVersion();
	}


	@Kroll.method
	public TidistoModule setTimeout(int timeout) {
		return this;
	}

	@Kroll.method
	public TidistoModule enableDebugging() {
		DEBUG = true;
		return this;
	}

	
	@Kroll.method
	public void init() {
		if (DEBUG)
			Log.i(LCAT, "====== START leica ========");
		verifyPermissions();
		if (LeicaSdk.isInit == false) {
			LeicaSdk.InitObject initObject = new LeicaSdk.InitObject(
					"commands.json");
			try {
				LeicaSdk.init(ctx, initObject);
				LeicaSdk.setMethodCalledLog(false);
				LeicaSdk.setScanConfig(true, true, true, true);
				addLicenceKey();

			} catch (JSONException e) {
				Log.e(LCAT,
						"Error in the structure of the JSON File, closing the application");
				Log.d(LCAT, e.getMessage());

			} catch (IllegalArgumentCheckedException e) {
				Log.e(LCAT,
						"Error in the data of the JSON File, closing the application");
				Log.d(LCAT, e.getMessage());

			} catch (IOException e) {
				Log.d(LCAT, e.getMessage());
			}

		} else if (DEBUG)
			Log.d(LCAT, "was always initalized.");
	}

	private boolean hasPermission(String permission) {
		if (Build.VERSION.SDK_INT >= 23) {
			Activity currentActivity = TiApplication.getInstance()
					.getCurrentActivity();
			if (currentActivity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	public boolean verifyPermissions() {
		Log.d(LCAT, "Starting verifyPermissions()");
		boolean granted = false;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (!hasPermission("android.permission.ACCESS_FINE_LOCATION")
					&& !hasPermission("android.permission.ACCESS_COARSE_LOCATION"))
				granted = false;
			Log.i(LCAT, "ACCESS_FINE_LOCATION="
					+ hasPermission("android.permission.ACCESS_FINE_LOCATION"));
			Log.i(LCAT,
					"ACCESS_COARSE_LOCATION="
							+ hasPermission("android.permission.ACCESS_COARSE_LOCATION"));

			LocationManager locationManager = (LocationManager) ctx
					.getSystemService(Context.LOCATION_SERVICE);
			boolean network_enabled = false;
			try {
				network_enabled = locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			} catch (Exception e) {
				granted = false;
				Log.e(LCAT + "NETWORK PROVIDER, network not enabled",
						e.getMessage());
			}
			if (network_enabled) {
				// LeicaSdk.scanConfig.setWifiAdapterOn(true);
				LeicaSdk.scanConfig.setBleAdapterOn(ctx.getPackageManager()
						.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE));
				Log.d(LCAT, "setBleAdapterOn");
			}
			Log.i(LCAT,
					"Permissions: WIFI: "
							+ LeicaSdk.scanConfig.isWifiAdapterOn() + ", BLE: "
							+ LeicaSdk.scanConfig.isBleAdapterOn());
			if (!hasPermission("android.permission.ACCESS_FINE_LOCATION"))
				granted = false;
		}
		return granted;
	}

	private void dispatchMessage(KrollDict dict) {
		if (Callback != null) {
			Callback.call(getKrollObject(), dict);
		}
		KrollFunction onScanResult = (KrollFunction) getProperty("onScanResult");
		if (onScanResult != null) {
			onScanResult.call(getKrollObject(), new Object[] { dict });
		}
		if (hasListeners("availableDeviceFound"))
			fireEvent("availableDeviceFound", dict);
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
