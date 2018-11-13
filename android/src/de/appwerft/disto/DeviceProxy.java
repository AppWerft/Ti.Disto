package de.appwerft.disto;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

import android.os.Handler;
import android.os.HandlerThread;
import ch.leica.sdk.Types;
import ch.leica.sdk.Devices.BleDevice;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Listeners.ReceivedDataListener;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.connection.BaseConnectionManager.BleReceivedDataListener;

@Kroll.proxy(creatableInModule = TidistoModule.class)
public class DeviceProxy extends KrollProxy implements
		Device.ConnectionListener, ErrorListener, ReceivedDataListener,
		BleReceivedDataListener {
	private Device currentDevice;
	private MessageDispatcher messageDispatcher;
	private static String LCAT = TidistoModule.LCAT;
	Handler sendCustomCommandHandler;
	HandlerThread sendCustomCommandThread;

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
	}

	@Kroll.method
	public void connect(KrollDict opts) {
		messageDispatcher.registerCallbacks(opts);
		currentDevice.connect();
	}

	@Kroll.method
	public String getId() {
		return currentDevice.getDeviceID();
	}

	@Kroll.method
	public String[] getAvailableCommands() {
		return currentDevice.getAvailableCommands();
	}

	@Kroll.method
	public void sendCommand(final String cmd, final boolean custom,
			@Kroll.argument(optional = true) KrollFunction callback) {
		if (sendCustomCommandThread == null) {
			sendCustomCommandThread = new HandlerThread("getDeviceStateThread"
					+ System.currentTimeMillis(), HandlerThread.MAX_PRIORITY);
			sendCustomCommandThread.start();
			sendCustomCommandHandler = new Handler(
					sendCustomCommandThread.getLooper());
		} else Log.d(LCAT,"sendCustomCommandThread != null");
		Log.d(LCAT, "send any string to device: " + cmd);
		try {
			sendCustomCommandHandler.post(new Runnable() {
				@Override
				public void run() {
					try {
						Log.d(LCAT,"inside new thread, preparing command. ");
						Response response;
							response = currentDevice.sendCommand(Types.Commands.DistanceDC,
									currentDevice.getTIMEOUT_NORMAL());
							response.waitForData();
						if (response.getError() != null) {
							Log.e(LCAT, ": error: "
									+ response.getError().getErrorMessage());
						}
						Log.d(LCAT, "DistoComResponse set with ResponsePlain");
					} catch (DeviceException e) {
						Log.e(LCAT, "Error sending the command.", e);
					}
				}
			});
		} catch (Exception e) {
			Log.e(LCAT, e.getMessage());
		}
	}

	@Override
	public void onConnectionStateChanged(final Device device,
			final Device.ConnectionState connectionState) {
		currentDevice = device;
		final KrollDict event = new KrollDict();
		event.put("device", this);
		try {
			if (connectionState == Device.ConnectionState.disconnected) {
				event.put("connected", false);
				messageDispatcher.dispatchDevice(event);
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
										event.put("connected", true);
										event.put("model",
												currentDevice.getModel());
										event.put("device", currentDevice
												.getDeviceType().name());
										event.put("commands", currentDevice
												.getAvailableCommands());
										event.put("type", currentDevice
												.getConnectionType().name());
										Log.i(LCAT, event.toString());
										messageDispatcher.dispatchDevice(event);
									}
								});
					} else
						Log.d(LCAT, "device is no Bluetooth device.");
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

	@Override
	public void onBleDataReceived(ReceivedData receivedData,
			ErrorObject errorObject) throws DeviceException {
		Log.d(LCAT,
				"//////////////////////////////" + receivedData.getCommandStr());

	}
}