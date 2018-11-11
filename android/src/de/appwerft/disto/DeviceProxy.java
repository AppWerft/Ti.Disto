package de.appwerft.disto;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

import ch.leica.sdk.Devices.BleDevice;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Listeners.ReceivedDataListener;
import ch.leica.sdk.commands.ReceivedData;


@Kroll.proxy(creatableInModule = TidistoModule.class)
public class DeviceProxy extends KrollProxy implements
		Device.ConnectionListener, ErrorListener, ReceivedDataListener {
	private Device currentDevice;
	private MessageDispatcher messageDispatcher;
	private static String LCAT = TidistoModule.LCAT;

	public DeviceProxy() {
		super();
	}

	public DeviceProxy(Device device) {
		super();
		currentDevice = device;
		currentDevice.setConnectionListener(this);
		currentDevice.setErrorListener(this);
		currentDevice.setReceiveDataListener(this);
		messageDispatcher = new MessageDispatcher(this);
		Log.d(LCAT,"DeviceProxy created");
	}

	@Kroll.method
	public void connect(KrollDict opts) {
		messageDispatcher.registerCallbacks(opts);
		currentDevice.connect();
		Log.d(LCAT,"Device try to connect");
	}
	
	@Kroll.method
	public String getId() {
		return currentDevice.getDeviceID();
	}

	@Override
	public void onConnectionStateChanged(final Device device,
			final Device.ConnectionState connectionState) {
		try {
			if (connectionState == Device.ConnectionState.connected) {
				try {
					if (currentDevice != null
							&& currentDevice instanceof BleDevice) {
						currentDevice
								.startBTConnection(new Device.BTConnectionCallback() {
									@Override
									public void onFinished() {
										messageDispatcher
												.dispatchDevice(currentDevice);
									}
								});
					} else Log.d(LCAT,"device is no Bluetooth device.");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		} catch (Exception e) {
			Log.e(LCAT, e.getMessage());
		}
	}

	@Override
	public void onError(ErrorObject errorObject, Device device) {
		messageDispatcher.dispatchError(errorObject);
	}

	@Override
	public void onAsyncDataReceived(ReceivedData receivedData) {
		messageDispatcher.dispatchData(receivedData);
	}
}