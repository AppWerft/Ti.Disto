package ch.leica.distosdkapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;


public class CleanupService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_NOT_STICKY;
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onTaskRemoved(Intent rootIntent){
		Log.i("CleanupService","onTaskRemoved: APP TERMINATED");

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
