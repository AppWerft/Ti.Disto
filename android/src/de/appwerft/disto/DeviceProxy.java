package de.appwerft.disto;

import java.util.ArrayList;
import java.util.List;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollObject;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

import ch.leica.sdk.Devices.BleDevice;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Listeners.ReceivedDataListener;
import ch.leica.sdk.commands.ReceivedBleDataPacket;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.ReceivedWifiDataPacket;
import ch.leica.sdk.commands.ReceivedYetiDataPacket;
import ch.leica.sdk.connection.ble.BleCharacteristic;

@Kroll.proxy(creatableInModule = TidistoModule.class)
public class DeviceProxy extends KrollProxy implements
		Device.ConnectionListener, ErrorListener, ReceivedDataListener {

	private static final String LCAT = TidistoModule.LCAT;
	private Device currentDevice;
	// Callbacks for back communication to JS layer
	private KrollFunction connectCallback = null;
	private KrollFunction dataCallback = null;
	private KrollFunction errorCallback = null;

	public DeviceProxy() {
		super();
	}

	public DeviceProxy(Device device) {
		super();
		currentDevice = device;
		// setting all listeners
		currentDevice.setConnectionListener(this);
		currentDevice.setErrorListener(this);
		currentDevice.setReceiveDataListener(this);
	}

	@Kroll.method
	public void connect(KrollDict o) {
		// importing callbacks from JS
		if (o.containsKeyAndNotNull("onconnect")) {
			connectCallback = (KrollFunction) o.get("onconnect");
		}
		if (o.containsKeyAndNotNull("ondata")) {
			dataCallback = (KrollFunction) o.get("ondata");
		}
		if (o.containsKeyAndNotNull("onerror")) {
			errorCallback = (KrollFunction) o.get("onerror");
		}
		currentDevice.connect();
	}

	@Override
	public void onConnectionStateChanged(final Device device,
			final Device.ConnectionState connectionState) {
		final KrollDict event = new KrollDict();
		event.put("state", connectionState.ordinal());
		try {
			if (connectionState == Device.ConnectionState.disconnected) {
				if (connectCallback != null) {
					connectCallback.callAsync(getKrollObject(), event);
				}
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
										if (connectCallback != null) {
											connectCallback
													.callAsync(
															getKrollObject(),
															KrollDictExporter.DeviceToKrollDict(currentDevice));
										}

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
		KrollDict event = new KrollDict();
		event.put("device", this);
		event.put("message", errorObject.getErrorMessage());
		event.put("code", errorObject.getErrorCode());
		if (errorCallback != null) {
			errorCallback.call(getKrollObject(),event);
		}
	}

	@Override
	public void onAsyncDataReceived(ReceivedData receivedData) {
		KrollObject ko = getKrollObject();
		if (receivedData.dataPacket instanceof ReceivedYetiDataPacket) {
			if (dataCallback != null)
				dataCallback
						.callAsync(
								ko,
								KrollDictExporter
										.getYetiInformation((ReceivedYetiDataPacket) receivedData.dataPacket));
		} else if (receivedData.dataPacket instanceof ReceivedBleDataPacket) {
			if (dataCallback != null)
				dataCallback
						.callAsync(
								ko,
								KrollDictExporter
										.getBleInformation((ReceivedBleDataPacket) receivedData.dataPacket));
		} else if (receivedData.dataPacket instanceof ReceivedWifiDataPacket) {
			if (dataCallback != null)
				dataCallback
						.callAsync(
								ko,
								KrollDictExporter
										.getWifiInformation((ReceivedWifiDataPacket) receivedData.dataPacket));
		} else
			Log.w(LCAT, "receivedData type="
					+ receivedData.dataPacket.getClass().getSimpleName());

	}

	

}
