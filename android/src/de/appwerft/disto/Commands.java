package de.appwerft.disto;

import java.util.concurrent.CountDownLatch;

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

	public static void getDistance(final Device currentDevice,
			KrollProxy proxy, KrollFunction callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final ResponsePlain firstresponse = (ResponsePlain) currentDevice
							.sendCommand(Types.Commands.DistanceDC);
					firstresponse.waitForData();
					if (readDataFromResponseObject(firstresponse)!=null) {
						final ResponsePlain secondresponse = (ResponsePlain) currentDevice
								.sendCommand(Types.Commands.DistanceDC);
						secondresponse.waitForData();
					};
				} catch (DeviceException e) {
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
