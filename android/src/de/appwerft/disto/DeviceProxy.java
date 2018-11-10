package de.appwerft.disto;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

import ch.leica.sdk.Devices.BleDevice;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Listeners.ReceivedDataListener;
import ch.leica.sdk.commands.ReceivedData;

@Kroll.proxy(creatableInModule = TidistoModule.class)
public class DeviceProxy extends KrollProxy implements
		Device.ConnectionListener, ErrorListener, ReceivedDataListener {
	private static final String LCAT = TidistoModule.LCAT;
	private Device currentDevice;
	private MessageDispatcher messageDispatcher;

	public DeviceProxy() {
		super();
	}

	public DeviceProxy(Device device) {
		super();
		currentDevice = device;
		currentDevice.setConnectionListener(this);
		currentDevice.setErrorListener(this);
		currentDevice.setReceiveDataListener(this);
		messageDispatcher = new MessageDispatcher(getKrollObject());
	}

	@Kroll.method
	public void connect(KrollDict o) {
		// importing callbacks from JS
		messageDispatcher.registerCallbacks(o);
		currentDevice.connect();
	}

	@Override
	public void onConnectionStateChanged(final Device device,
			final Device.ConnectionState connectionState) {
		try {
			if (connectionState == Device.ConnectionState.disconnected) {
				KrollDict event = new KrollDict();
				event.put("connected", false);

				return;
			}
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
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		} catch (Exception e) {
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