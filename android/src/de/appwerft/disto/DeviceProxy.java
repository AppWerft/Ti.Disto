package de.appwerft.disto;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;

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
	static Device currentDevice;
	private MessageDispatcher messageDispatcher;
	private static String LCAT = TidistoModule.LCAT;
	Handler sendCustomCommandHandler;
	private boolean deviceIsInTrackingMode = false;
	HandlerThread sendCustomCommandThread;
	Timer tracker;
	public DeviceProxy() {
		super();
	}

	public DeviceProxy(Device device) {
		super();
		currentDevice = device;

		messageDispatcher = new MessageDispatcher(this);
	}

	@Kroll.method
	public void connect(KrollDict opts) {
		currentDevice.setConnectionListener(this);
		currentDevice.setErrorListener(this);
		currentDevice.setReceiveDataListener(this);
		messageDispatcher.registerCallbacks(opts);
		currentDevice.connect();
	}

	@Kroll.method
	public void disconnect() {
		if (currentDevice != null && currentDevice instanceof BleDevice) {
			try {
				currentDevice
						.pauseBTConnection(new Device.BTConnectionCallback() {
							@Override
							public void onFinished() {
								Log.d("onStop",
										"NOW Notifications are deactivated in the device");
								currentDevice.disconnect();
							}
						});
			} catch (DeviceException e) {
				e.printStackTrace();
			}
		} else if (currentDevice != null) {
			currentDevice.disconnect();
		}
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
	public void unpair() {
		if (currentDevice != null)
			currentDevice.unpairDevice();
	}

	@Kroll.method
	public KrollDict getConnectionState() {
		if (currentDevice == null)
			return null;
		KrollDict event = new KrollDict();
		event.put("name", currentDevice.getConnectionState().name());
		event.put("code", currentDevice.getConnectionState().ordinal());
		return event;
	}

	@Kroll.method
	public void createCommandsDialog() {
		CommandsDialog dialog = new CommandsDialog(this, currentDevice);
		dialog.create();
	}

	@Kroll.method
	public boolean isInUpdateMode() {
		if (currentDevice == null)
			return false;
		try {
			return currentDevice.isInUpdateMode();
		} catch (DeviceException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Kroll.method
	public void startBaseMode() {
		try {
			if (currentDevice == null)
				return;
			currentDevice.sendCommand(Types.Commands.StartBaseMode);
		} catch (DeviceException e) {
			e.printStackTrace();
		}
	}

	@Kroll.method
	public void startMeasurePlan() {
		try {
			if (currentDevice == null)
				return;
			currentDevice.sendCommand(Types.Commands.StartMeasurePlan);
		} catch (DeviceException e) {
			e.printStackTrace();
		}
	}

	@Kroll.method
	public void startSmartRoom() {
		try {
			if (currentDevice == null)
				return;
			currentDevice.sendCommand(Types.Commands.StartSmartRoom);
		} catch (DeviceException e) {
			e.printStackTrace();
		}
	}

	
	@Kroll.method
	public void toggleTracking(@Kroll.argument(optional = true) KrollFunction callback) {
		if (deviceIsInTrackingMode==true) {
			stopTracking() ;
		}
		else startTracking( callback);
	}
	
	@Kroll.method
	public void startTracking(
			@Kroll.argument(optional = true) final KrollFunction callback) {
		tracker = new Timer();
		tracker.scheduleAtFixedRate(new TimerTask(){
		    @Override
		    public void run(){
		    	Commands.getDistance(currentDevice, DeviceProxy.this, callback);
		    }
		},0,800);
		deviceIsInTrackingMode = true;
	}

	@Kroll.method
	public void stopTracking() {
		tracker.cancel();
		deviceIsInTrackingMode = false;
	}

	@Kroll.method
	public void getDistance(@Kroll.argument(optional = true) KrollFunction callback) {
		Commands.getDistance(currentDevice, this, callback);
	}

	@Kroll.method
	public void sendCustomeCommand(final String cmd,
			@Kroll.argument(optional = true) KrollFunction callback) {
		if (sendCustomCommandThread == null) {
			sendCustomCommandThread = new HandlerThread("getDeviceStateThread"
					+ System.currentTimeMillis(), HandlerThread.MAX_PRIORITY);
			sendCustomCommandThread.start();
			sendCustomCommandHandler = new Handler(
					sendCustomCommandThread.getLooper());
		} else
			Log.d(LCAT, "sendCustomCommandThread != null");
		Log.d(LCAT, "send any string to device: " + cmd);
		try {
			sendCustomCommandHandler.post(new Runnable() {
				@Override
				public void run() {
					try {
						Response response;
						response = currentDevice.sendCustomCommand(cmd,
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
										event.put("started", true);
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