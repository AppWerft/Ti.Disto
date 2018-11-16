package de.appwerft.disto;

import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.HandlerThread;
import ch.leica.sdk.Types;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.commands.response.ResponsePlain;

public class CommandsDialog {
	private KrollProxy proxy;
	private HandlerThread sendCustomCommandThread;
	private Handler sendCustomCommandHandler;
	private HandlerThread getDeviceStateThread;
	private Handler getDeviceStateHandler;
	private HandlerThread getDeviceInfoThread;
	private Handler getDeviceInfoHandler;
	private Device currentDevice;
	private AlertDialog customCommandDialog;
	private AlertDialog commandDialog;
	private static String LCAT = TidistoModule.LCAT;

	
	public CommandsDialog() {
	}
	
	public CommandsDialog(KrollProxy proxy, Device device) {
		this.proxy = proxy;
		currentDevice = device;

	}

	public void create() {
		AlertDialog.Builder comandDialogBuilder = new AlertDialog.Builder(
				TiApplication.getAppCurrentActivity());
		comandDialogBuilder.setTitle("Select Command");
		comandDialogBuilder.setItems(currentDevice.getAvailableCommands(),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String command = currentDevice
								.getAvailableCommands()[which];

						if (command.equals(Types.Commands.Custom.name())) {
							// showCustomCommandDialog();
						} else {
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										Response response = currentDevice
												.sendCommand(Types.Commands
														.valueOf(command));
										response.waitForData();
										readDataFromResponseObject(response);
									} catch (DeviceException e) {
										Log.e(LCAT, ": send command error: ", e);
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
		Log.v(LCAT, METHODTAG + " called");
		if (response.getError() != null) {
			Log.e(LCAT, METHODTAG + ": response error: "
					+ response.getError().getErrorMessage());
			return;
		}
		if (response instanceof ResponsePlain) {
			extractDataFromPlainResponse((ResponsePlain) response);
		}
	}
	public void extractDataFromPlainResponse(ResponsePlain response) {

		final String METHODTAG = "extractDataFromPlainResponse";
		Log.v(LCAT, METHODTAG + " called");

		
	}

}
