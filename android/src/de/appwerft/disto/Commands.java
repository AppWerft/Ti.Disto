package de.appwerft.disto;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

import ch.leica.sdk.Types;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.commands.response.ResponseMeasurement;
import ch.leica.sdk.commands.response.ResponsePlain;

public class Commands {
	private static String LCAT = TidistoModule.LCAT;

	public Commands() {
	}

	public static void getDistance(final Device currentDevice,final
			KrollProxy proxy,final KrollFunction callback) {
		final long delay = 20;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					long startTime = System.currentTimeMillis();
					final ResponsePlain firstresponse = (ResponsePlain) currentDevice
							.sendCommand(Types.Commands.DistanceDC);
					Log.i(LCAT, "start waiting for data");
					firstresponse.waitForData();
					Log.i(LCAT, "end waiting for data");
					if (readDataFromResponseObject(firstresponse) != null) {
						TimeUnit.MILLISECONDS.sleep(delay);
						final ResponsePlain secondresponse = (ResponsePlain) currentDevice
								.sendCommand(Types.Commands.DistanceDC);
						secondresponse.waitForData();
						TimeUnit.MILLISECONDS.sleep(delay);
						final ResponsePlain thirdresponse = (ResponsePlain) currentDevice
								.sendCommand(Types.Commands.DistanceDC);
						thirdresponse.waitForData();
						KrollDict event = new KrollDict();
						long stopTime = System.currentTimeMillis();
						event.put("time", stopTime - startTime);
						if (callback != null)
							callback.callAsync(proxy.getKrollObject(), event);
					} else
						Log.e(LCAT, "response was null");
				} catch (DeviceException | InterruptedException e) {
					Log.e(LCAT, e.getMessage());
				}
			}
		}).start();
	}

	public static String readDataFromResponseObject(final Response response) {
		if (response.getError() != null) {
			Log.e(LCAT, ": response error: "
					+ response.getError().getErrorMessage());
			return null;
		}
		if (response instanceof ResponsePlain) {
			return ((ResponsePlain) response).getReceivedDataString();

		}
		return null;
	}

	public void extractDataFromPlainResponse(ResponsePlain response) {

		final String METHODTAG = "extractDataFromPlainResponse";
		Log.v(LCAT, METHODTAG + " called");

	}

}
