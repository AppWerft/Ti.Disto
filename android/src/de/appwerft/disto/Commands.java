package de.appwerft.disto;

import java.util.concurrent.CountDownLatch;

import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

import ch.leica.sdk.Types;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.commands.response.ResponsePlain;

public class Commands {
	private static String LCAT = TidistoModule.LCAT;

	public Commands() {
	}

	public static void getDistance(final Device currentDevice,
			KrollProxy proxy, KrollFunction callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final ResponsePlain response = (ResponsePlain) currentDevice
							.sendCommand(Types.Commands.DistanceDC);
					Log.i(LCAT, "sendCommand");
					response.waitForData();
					Log.i(LCAT,response.getReceivedDataString());
					if (response.getError() != null) {
						Log.e(LCAT, response.getError().getErrorMessage());
					} else {
						Log.i(LCAT, response.getReceivedDataString());
					}
					Log.i(LCAT, "after proceeding");
				} catch (DeviceException e) {
					Log.e(LCAT, e.getMessage());
				}
			}
		}).start();
	}

	public static void getDeviceInfo(final Device currentDevice,
			KrollProxy proxy, KrollFunction callback) {
		final CountDownLatch deviceInfoLatch = new CountDownLatch(1);

		try {
			if (currentDevice != null
					&& currentDevice.isInUpdateMode() == false) {
				return;
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					ResponsePlain response = null;
					try {

						response = (ResponsePlain) currentDevice
								.sendCommand(Types.Commands.GetBrandDistocom);
						response.waitForData();
						String logCommandTag = "getBrand";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: "
									+ response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": "
									+ response.getReceivedDataString());
						}

						response = (ResponsePlain) currentDevice
								.sendCommand(Types.Commands.GetIDDistocom);
						response.waitForData();
						logCommandTag = "getId";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: "
									+ response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": "
									+ response.getReceivedDataString());
						}

						response = (ResponsePlain) currentDevice
								.sendCommand(Types.Commands.GetSoftwareVersionAPPDistocom);
						response.waitForData();
						logCommandTag = "GetSoftwareVersionAPPDistocom";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: "
									+ response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": "
									+ response.getReceivedDataString());
						}

						response = (ResponsePlain) currentDevice
								.sendCommand(Types.Commands.GetSoftwareVersionEDMDistocom);
						response.waitForData();
						logCommandTag = "GetSoftwareVersionEDMDistocom";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: "
									+ response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": "
									+ response.getReceivedDataString());
						}

						response = (ResponsePlain) currentDevice
								.sendCommand(Types.Commands.GetSoftwareVersionFTADistocom);
						response.waitForData();
						logCommandTag = "GetSoftwareVersionFTADistocom";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: "
									+ response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": "
									+ response.getReceivedDataString());
						}

						response = (ResponsePlain) currentDevice
								.sendCommand(Types.Commands.GetSerialAPPDistocom);
						response.waitForData();
						logCommandTag = "GetSerialAPPDistocom";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: "
									+ response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": "
									+ response.getReceivedDataString());
						}

						response = (ResponsePlain) currentDevice
								.sendCommand(Types.Commands.GetSerialEDMDistocom);
						response.waitForData();
						logCommandTag = "GetSerialEDMDistocom";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: "
									+ response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": "
									+ response.getReceivedDataString());
						}

						response = (ResponsePlain) currentDevice
								.sendCommand(Types.Commands.GetSerialFTADistocom);
						response.waitForData();
						logCommandTag = "GetSerialFTADistocom";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: "
									+ response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": "
									+ response.getReceivedDataString());
						}

						deviceInfoLatch.countDown();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

}
