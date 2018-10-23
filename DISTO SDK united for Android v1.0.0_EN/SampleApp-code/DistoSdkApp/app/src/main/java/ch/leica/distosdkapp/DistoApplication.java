package ch.leica.distosdkapp;

import android.app.Application;

import android.content.Intent;
import android.util.Log;

import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;

public class DistoApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("DistoApplication", "onCreate");
		DeviceManager deviceManager = DeviceManager.getInstance(this);
		// register receivers for internally receiving wifi and bluetooth adapter changes
		deviceManager.registerReceivers(this);
		Intent intent = new Intent(this, CleanupService.class);
		startService(intent);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();

		DeviceManager deviceManager = DeviceManager.getInstance(this);
		// unregister internal broadcast receivers
		deviceManager.unregisterReceivers();

		if (deviceManager != null) {
			//Disconnect from all the connected devices
			for (Device connectedDevice : deviceManager.getConnectedDevices()) {
				connectedDevice.disconnect();
				Log.i("DistoApplication", "onTerminate, Disconnected Device: " + connectedDevice.modelName);
			}
		}
	}
}
