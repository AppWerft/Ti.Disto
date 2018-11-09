/**
w * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2017 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package de.appwerft.disto;

import java.util.List;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutArrangement;
import org.appcelerator.titanium.view.TiUIView;

import ch.leica.sdk.Types;
import ch.leica.sdk.Devices.BleDevice;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Listeners.ReceivedDataListener;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.ReceivedWifiDataPacket;
import ch.leica.sdk.commands.ReceivedYetiDataPacket;
import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.commands.response.ResponsePlain;
import ch.leica.sdk.connection.ble.BleCharacteristic;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

// This proxy can be created by calling Tidisto.createExample({message: "hello world"})
@Kroll.proxy(creatableInModule = TidistoModule.class)
public class DeviceProxy extends KrollProxy implements
		Device.ConnectionListener, ErrorListener, ReceivedDataListener {
	// Standard Debugging variables
	private static final String LCAT = TidistoModule.LCAT;
	private Device currentDevice;
	private KrollFunction connectCallback = null;
	private KrollFunction dataCallback = null;
	private AlertDialog commandDialog =null;

	// Constructor
	public DeviceProxy() {
		super();
	}

	public DeviceProxy(Device device) {
		super();
		currentDevice = device;
		currentDevice.setConnectionListener(this);
		currentDevice.setErrorListener(this);
		currentDevice.setReceiveDataListener(this);
		Log.i(LCAT, "YETI created");
	}

	@Kroll.method
	public void connect(KrollDict o) {
		if (o.containsKeyAndNotNull("onconnect")) {
			connectCallback = (KrollFunction) o.get("onconnect");
		}
		if (o.containsKeyAndNotNull("ondata")) {
			dataCallback = (KrollFunction) o.get("ondata");
		}
		currentDevice.connect();
	}

	@Kroll.getProperty
	@Kroll.method
	public String getName() {
		return currentDevice.getDeviceName();
	}

	@Kroll.getProperty
	@Kroll.method
	public String getId() {
		return currentDevice.getDeviceID();
	}

	@Kroll.method
	public String[] getAvailableCommands() {
		if (currentDevice != null)
			return currentDevice.getAvailableCommands();
		else
			return null;
	}

	@Kroll.method
	public void startBaseMode() {
	//	if (currentDevice != null)
		//	currentDevice.StartBaseMode();
	}
	
	@Kroll.method
	public void showCommandDialog() {

		
		

		AlertDialog.Builder comandDialogBuilder = new AlertDialog.Builder(TiApplication.getAppCurrentActivity());
		comandDialogBuilder.setTitle("Select Command");
		comandDialogBuilder.setItems(currentDevice.getAvailableCommands(), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String command = currentDevice.getAvailableCommands()[which];

				if (command.equals(Types.Commands.Custom.name())) {
					//showCustomCommandDialog();
				} else {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Response response = currentDevice.sendCommand(Types.Commands.valueOf(command));
								response.waitForData();

								readDataFromResponseObject(response);
							} catch (DeviceException e) {
							}
						}

					}).start();
				}
			}
		});
		commandDialog = comandDialogBuilder.create();
		commandDialog.show();
	}

	public void readDataFromResponseObject(final Response response) {

		final String METHODTAG = ".readDataFromResponseObject";
		

		runOnMainThread(new Runnable() {
			@Override
			public void run() {

				if (response.getError() != null) {

		
					return;
				}

				if (response instanceof ResponsePlain) {
					//extractDataFromPlainResponse((ResponsePlain) response);
				}
			}
		});
		


}
	
	@Kroll.method
	public void sendCommand(KrollDict o) {
		String cmd="";
		
		if (o.containsKeyAndNotNull("command")) {
			cmd = o.getString("command");
		}
		if (o.containsKeyAndNotNull("ondata")) {
			dataCallback = (KrollFunction)o.get("ondata");
		}
		final String command = cmd;
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Response response = currentDevice
							.sendCommand(Types.Commands.valueOf(command));
					response.waitForData();

					//readDataFromResponseObject(response);
				} catch (DeviceException e) {

				}
			}

		}).start();
	}

	@Kroll.getProperty
	@Kroll.method
	public KrollDict getAllCharacteristics() {
		KrollDict res = new KrollDict();
		try {
			List<BleCharacteristic> characteristics = currentDevice
					.getAllCharacteristics();
			for (BleCharacteristic characteristic : characteristics) {

			}

		} catch (DeviceException e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public void onConnectionStateChanged(final Device device,
			final Device.ConnectionState connectionState) {
		final String METHODTAG = ".onConnectionStateChanged";
		final KrollDict event = new KrollDict();
		event.put("device", new DeviceProxy(device));
		event.put("state", connectionState.ordinal());

		Log.i(LCAT, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		Log.i(LCAT, METHODTAG + ": " + device.getDeviceID() + ", state: "
				+ connectionState);
		try {
			if (connectionState == Device.ConnectionState.disconnected) {
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
											connectCallback.callAsync(
													getKrollObject(), event);

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
			Log.e(LCAT, METHODTAG, e);
		}
	}

	@Override
	public void onError(ErrorObject errorObject, Device device) {
		// uiHelper.setLog(this, log, "onError" + ": " +
		// errorObject.getErrorMessage() + ", errorCode: " +
		// errorObject.getErrorCode());
	}

	@Override
	public void onAsyncDataReceived(ReceivedData receivedData) {
		if (receivedData.dataPacket instanceof ReceivedYetiDataPacket) {
			getYetiInformation((ReceivedYetiDataPacket) receivedData.dataPacket);
		}

	}

	private void getYetiInformation(ReceivedYetiDataPacket dataPacket) {
		KrollDict data = new KrollDict();
		KrollDict event = new KrollDict();
		try {
			data.put("Distance: ", dataPacket.getBasicMeasurements()
					.getDistance());
			data.put("DistanceUnit: ", dataPacket.getBasicMeasurements()
					.getDistanceUnit());
			data.put("Inclination: ", dataPacket.getBasicMeasurements()
					.getInclination());
			data.put("InclinationUnit: ", dataPacket.getBasicMeasurements()
					.getInclinationUnit());
			data.put("Direction: ", dataPacket.getBasicMeasurements()
					.getDirection());
			data.put("DirectionUnit: ", dataPacket.getBasicMeasurements()
					.getDirectionUnit());
			data.put("V_temp: ", dataPacket.getBasicMeasurements()
					.getTimestampAndFlags());

			data.put("HzAngle: ", dataPacket.getP2P().getHzAngle());
			data.put("VeAngle: ", dataPacket.getP2P().getVeAngle());
			data.put("InclinationStatus: ", dataPacket.getP2P()
					.getInclinationStatus());
			data.put("TimestampAndFlags: ", dataPacket.getP2P()
					.getTimestampAndFlags());

			data.put("Quaternion_X: ", dataPacket.getQuaternion()
					.getQuaternion_X());
			data.put("Quaternion_Y: ", dataPacket.getQuaternion()
					.getQuaternion_Y());
			data.put("Quaternion_Z: ", dataPacket.getQuaternion()
					.getQuaternion_Z());
			data.put("TimestampAndFlags: ", dataPacket.getQuaternion()
					.getTimestampAndFlags());

			data.put("Magnetometer_X: ", dataPacket.getMagnetometer()
					.getMagnetometer_X());
			data.put("Magnetometer_Y: ", dataPacket.getMagnetometer()
					.getMagnetometer_Y());
			data.put("Magnetometer_Z: ", dataPacket.getMagnetometer()
					.getMagnetometer_Z());
			data.put("TimestampAndFlags: ", dataPacket.getMagnetometer()
					.getTimestampAndFlags());
			data.put("Acceleration_X: ", dataPacket
					.getAccelerationAndRotation().getAcceleration_X());
			data.put("Acceleration_Y: ", dataPacket
					.getAccelerationAndRotation().getAcceleration_Y());
			data.put("Acceleration_Z: ", dataPacket
					.getAccelerationAndRotation().getAcceleration_Z());
			data.put("AccSensitivity: ", dataPacket
					.getAccelerationAndRotation().getAccSensitivity());
			data.put("Rotation_X: ", dataPacket.getAccelerationAndRotation()
					.getRotation_X());
			data.put("Rotation_Y: ", dataPacket.getAccelerationAndRotation()
					.getRotation_Y());
			data.put("Rotation_Z: ", dataPacket.getAccelerationAndRotation()
					.getRotation_Z());
			data.put("RotationSensitivity: ", dataPacket
					.getAccelerationAndRotation().getRotationSensitivity());
			data.put("TimestampAndFlags: ", dataPacket
					.getAccelerationAndRotation().getTimestampAndFlags());
			data.put("DistocomReceivedMessage: ",
					dataPacket.getDistocomReceivedMessage());
			data.put("Distocom: ", dataPacket.getDistocom().getRawString());
			event.put("data", data);
			event.put("success", true);
		} catch (Exception e) {
			event.put("success", false);
			event.put("error", e.getMessage());
		}
		if (dataCallback != null)
			dataCallback.callAsync(getKrollObject(), event);
	}

}
