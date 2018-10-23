package ch.leica.distosdkapp;

import android.Manifest;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.LocationManager;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;

import ch.leica.sdk.ErrorHandling.ErrorDefinitions;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.ErrorHandling.PermissionException;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Listeners.ErrorListener;

import ch.leica.sdk.Types;
import ch.leica.sdk.Utilities.WifiHelper;

// just to check if volley is resolved appside
import com.android.volley.VolleyError;


/**
 * This is the main activity which handles finding available devices and holds the list of available devices.
 */
public class SearchDevicesActivity extends AppCompatActivity implements DeviceManager.FoundAvailableDeviceListener, Device.ConnectionListener, ErrorListener {

	// just to check if volley is resolved appside
	VolleyError error = new VolleyError();

	// to handle info dialog at app start and only at app start
	// has to be static, otherwise the alert will be displayed more than one time
	static boolean searchInfoShown = false;
	/**
	 * ClassName
	 */
	private final String CLASSTAG = SearchDevicesActivity.class.getSimpleName();
	/**
	 * UI holding the available devices
	 */
	ListView deviceList;
	ConnectionTypeAdapter connectionTypeAdapter;
	/**
	 * List with all the devices available in BLE and wifi mode
	 */
	List<Device> availableDevices = new ArrayList<>();

	//ui-alterts to present errors to users
	AlertDialog activateWifiDialog = null;
	AlertDialog connectingDialog = null;
	AlertDialog activateBluetoothDialog = null;
	// for finding and connecting to a device
	DeviceManager deviceManager;
	boolean findDevicesRunning = false;
	/**
	 * Current selected device
	 */
	Device currentDevice;
	// needed for connection timeout
	Timer connectionTimeoutTimer;
	TimerTask connectionTimeoutTask;
	// to do infinite rounds of finding devices
	Timer findDevicesTimer;
	boolean activityStopped = true;
	// to handle user cancel connection attempt
	Map<Device, Boolean> connectionAttempts = new HashMap<>();
	Device currentConnectionAttemptToDevice = null;

	private TextView version;

	//Gatt133ErrorHandler gatt133ErrorHandler;

	/**
	 * Defines what happen on a click on an item in the list
	 * If device is already connected directly jump to the coresponding activity
	 * - BLE devices - BLEInformationActivity
	 * - BLE YETI devices - YetiInformationActivity
	 * - Wifi devices - WifiInformationActivity
	 * <p>
	 * For Hotspot devices check if smartphone is connected to the correct hotspot
	 * Otherwise connect to the device
	 */
	private class OnItemClickListener implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final String METHODTAG = ".deviceList OnItemClick";

			stopFindAvailableDevicesTimer();


			//Call this to avoid interference in Bluetooth operations
			stopFindingDevices();

			Log.d(METHODTAG, "OnItemClickListener");

			// get device
			Device device = availableDevices.get(position);

			if (device == null) {
				Log.i(METHODTAG, "device not found");
				return;
			}

			currentDevice = device;

			// already connected
			if (device.getConnectionState() == Device.ConnectionState.connected) {
				goToInfoScreen(device);
				return;
			}

			// show connecting dialog
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AlertDialog.Builder builder = new AlertDialog.Builder(SearchDevicesActivity.this);
					builder.setMessage("Connecting... This may take up to 30 seconds... ").setTitle("Connecting");
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							new Thread(new Runnable() {
								@Override
								public void run() {

									// cancel connection attempt
									stopConnectionAttempt();
									findAvailableDevices();

									// if gatt133 error handling is in process
									/*if (gatt133ErrorHandler != null){
										gatt133ErrorHandler.stop();
									}*/

								}
							}).start();

							// cancel connection attempt
							//stopConnectionAttempt();
							//findAvailableDevices();
						}
					});

					connectingDialog = builder.create();
					connectingDialog.setCancelable(false);
					connectingDialog.show();
				}
			});

			// if hotspot go to wifi settings first if the wifi is incorrect
			if (currentDevice.getConnectionType().equals(Types.ConnectionType.wifiHotspot)) {

				String wifiName = WifiHelper.getWifiName(getApplicationContext());

				if (wifiName == null) {
					launchWifiPanel();
					return;
				} else if (wifiName.equalsIgnoreCase(currentDevice.getDeviceName()) == false) {
					launchWifiPanel();
					return;
				} else {
					// wifi is correct, connect!
					connectToDevice(currentDevice);
					return;
				}
			}

			//Start Timer here, if bt device. for wifi we do not need a timer, there we have a socket timeout
			if (currentDevice.getConnectionType() == Types.ConnectionType.ble) {
				startConnectionTimeOutTimer();
			}

			// connect the device
			connectToDevice(currentDevice);
		}
	}

	/**
	 * called when a valid Leica device is found
	 *
	 * @param device the device
	 */
	@Override
	public void onAvailableDeviceFound(final Device device) {

		final String METHODTAG = ".onAvailableDeviceFound";


		//new Thread
		Log.i(CLASSTAG, METHODTAG + ": deviceId: " + device.getDeviceID() + ", deviceName: " + device.getDeviceName());




		// synchronized, because internally onAvailableDeviceFound() can be called from different threads
		synchronized (availableDevices) {

			// in rare cases it can happen, that a device is found twice. so here is a double check.
			for (Device availableDevice : availableDevices) {
				if (availableDevice.getDeviceID().equalsIgnoreCase(device.getDeviceID())) {
					return;
				}
			}
			availableDevices.add(device);
		}

		updateList();
	}

	/**
	 * when closing main activity, disconnect from all devices
	 */
	@Override
	public void onBackPressed() {

		final String METHODTAG = ".onBackPressed";
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("You are about to close the app. That results in disconnecting from all devices. Do you want to close the app?");

		final Thread disconnectThread;

		disconnectThread = new Thread(new Runnable() {
			@Override
			public void run() {
				for (final Device connectedDevice : deviceManager.getConnectedDevices()) {
					connectedDevice.disconnect();
					Log.i(CLASSTAG, METHODTAG + "Disconnected Device model: " + connectedDevice.modelName +
							" deviceId: " + connectedDevice.getDeviceID());
				}

				finish();
			}
		});

		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {

				final String message = "Disconnecting devices ";  // + connectedDevice.getDeviceID();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SearchDevicesActivity.this);
						builder.setMessage(message);
						builder.setCancelable(false);
						builder.create().show();
					}
				});

				if (deviceManager != null) {
					// Disconnect from all the connected devices
					Log.i(CLASSTAG, METHODTAG + "To disconnect Devices count: " + deviceManager.getConnectedDevices().size());

					disconnectThread.start();
				}
				
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				// do nothing
			}
		});

		builder.create().show();
	}

	/**
	 * Called when the connection state of a device changed
	 *
	 * @param device currently connected device
	 * @param state  current device connection state
	 */
	@Override
	public void onConnectionStateChanged(Device device, Device.ConnectionState state) {
		final String METHODTAG = ".onConnectionStateChanged";
		Log.i(CLASSTAG, METHODTAG + ": onConnectionStateChanged: " + device.getDeviceID() + ", state: " + state);

		switch (state) {
			case connected:

				// update UI
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(connectingDialog != null) {
							connectingDialog.dismiss();
						}
					}
				});

				// if connection attempt was canceled
				Boolean canceled = connectionAttempts.get(device);
				if (canceled != null) {

					// connection attempt was canceled
					if (canceled == Boolean.TRUE) {
						// disconnect device
						device.disconnect();
						// clean map
						connectionAttempts.remove(device);
						// update UI
						updateList();
						return;
					}
				}

				goToInfoScreen(device);

				break;
			case disconnected:

				if(currentDevice != null) {
					if (Types.ConnectionType.ble.equals(currentDevice.getConnectionType())) {

						stopConnectionTimeOutTimer();

					}
				}else{
					stopConnectionTimeOutTimer();
				}

				break;
		}
	}

	/**
	 * Defines the default behavior when an error is notified.
	 * Presents alert to user showing a error message
	 *
	 * @param errorObject error object comes from different sources SDK or APP.
	 */
	@Override
	public void onError(final ErrorObject errorObject, final Device device) {
		final String METHODTAG = ".onError";
		if (LeicaSdk.INFO) {
			Log.i(CLASSTAG, METHODTAG + ": " + errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());
		}

		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				/*
				* first check for gatt 133
				* gatt error 133 handling
				* */
				if (errorObject.getErrorCode() == ErrorDefinitions.BLUETOOTH_DEVICE_133_ERROR_CODE
						|| errorObject.getErrorCode() == ErrorDefinitions.BLUETOOTH_DEVICE_62_ERROR_CODE) {

					if (device.getDeviceID().equalsIgnoreCase(currentConnectionAttemptToDevice.getDeviceID())){

						if (connectingDialog != null){
							connectingDialog.setTitle("Device not found");
							connectingDialog.setMessage(
									"The Device can not be found, please verify the device is turned ON and in range"
							);
						}



						/*if (gatt133ErrorHandler == null){
							gatt133ErrorHandler = new Gatt133ErrorHandler();
						}*/

						// show some different text on connection dialog to let user know that this time it can take longer
						/*if (connectingDialog != null){
							connectingDialog.setTitle("Exceptional Connection Attempt");
							connectingDialog.setMessage("Because of unavoidable gatt 133 / gatt 62 errors," +
									" multiple connection attempts needs to be done. " +
									"This will happen now and will take around 30s. To improve the " +
									"chance of connection, please move the phone/tablet and the device nearer together. Please wait...");
						}*/





						/*gatt133ErrorHandler.handleGatt133Error(
								getApplicationContext(),
								device,
								new Gatt133ErrorHandler.Gatt133ErrorHandlerListener() {
									@Override
									public void onSuccess(Device device) {

										Log.d("Gatt133", "onSuccess() - " + device.getDeviceID() +
												", connectionState: " + device.getConnectionState());

										onConnectionStateChanged(device, device.getConnectionState());

									}

									@Override
									public void onError(Device device, ErrorObject error) {
										Log.d("onError", "gatt 133 error handler returned with error");

										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												if (connectingDialog != null) {
													connectingDialog.dismiss();
												}

												showAlert("Could not connect to device. " +
														"Please turn off/on the device and the bluetooth adapter on your phone and try again.");
											}
										});
									}
								});*/

						return;
					}

					if (connectingDialog != null) {
						connectingDialog.dismiss();
					}

					stopConnectionAttempt();
					showAlert(errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode() );

					return;
				}


				if (connectingDialog != null) {
					connectingDialog.dismiss();
				}

				if (errorObject.getErrorCode() == ErrorDefinitions.HOTSPOT_DEVICE_IP_NOT_REACHABLE_CODE) {
					showAlert(errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode() + "\nPlease try again.");
					return;
				}
				if (errorObject.getErrorCode() == ErrorDefinitions.AP_DEVICE_IP_NOT_REACHABLE_CODE) {
					showAlert(errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode() + "\nPlease try again.");
					return;
				}
				if (errorObject.getErrorCode() == ErrorDefinitions.BLUETOOTH_DEVICE_UNABLE_TO_PAIR_CODE) {

					// + "\n\nPlease Reset Device and remove pairing Settings manually in Android settings.\nand try again.");
					showAlert(errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode() );

					stopConnectionTimeOutTimer();
					return;
				}

				// show the error
				showAlert(errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());

			}
		});
	}

	/**
	 * Show alert message
	 */
	public void showAlert(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(SearchDevicesActivity.this);
					alertBuilder.setMessage(message);
					alertBuilder.setPositiveButton("Ok", null);
					alertBuilder.create().show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Inits device list
	 */
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		final String METHODTAG = ".onCreate";
		super.onCreate(savedInstanceState);

		InformationActivityData informationActivityData = Clipboard.INSTANCE.getInformationActivityData();
		if (informationActivityData != null){
			Device device = informationActivityData.device;
			if (device != null){
				currentDevice = device;
				currentDevice.setConnectionListener(this);
				currentDevice.setErrorListener(this);
			}
		}

		setContentView(R.layout.activity_search_devices);

		deviceList = (ListView) findViewById(R.id.devices);

		//Shows the icon (connection Type) next to each of the available devices
		this.connectionTypeAdapter = new ConnectionTypeAdapter(getApplicationContext(), new ArrayList<Device>());
		this.deviceList.setAdapter(connectionTypeAdapter);

		updateList();

		availableDevices = new ArrayList<>();
		deviceList.setOnItemClickListener(new OnItemClickListener());

		connectionTimeoutTimer = new Timer();

		//Get the SDK version
		version = (TextView) findViewById(R.id.sdkVersion);

		this.init();

		Log.d(CLASSTAG, METHODTAG + ": onCreate Finished.");

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		InformationActivityData data = Clipboard.INSTANCE.getInformationActivityData();
		data.device = currentDevice;

		final String METHODTAG = ".onDestroy";
		Log.d(METHODTAG,"called");
		// stop finding devices when activity finishes
		// stop the timer for finding devices
		if (findDevicesTimer != null) {
			findDevicesTimer.cancel();
			findDevicesTimer.purge();
			findDevicesTimer = null;
		}
		activityStopped = true;
		stopFindingDevices();

		//dismiss all the dialogs that may be activated
		if (activateWifiDialog != null) {
			activateWifiDialog.dismiss();
		}
		if (activateBluetoothDialog != null) {
			activateBluetoothDialog.dismiss();
		}
		if (connectingDialog != null) {
			connectingDialog.dismiss();
		}

/*
		if (deviceManager != null) {
			//Disconnect from all the connected devices
			for (Device connectedDevice : deviceManager.getConnectedDevices()) {
				connectedDevice.disconnect();
				Log.i(CLASSTAG, METHODTAG + "Disconnected Device: " + connectedDevice.modelName);

			}

		}
*/

		Log.i(CLASSTAG, METHODTAG + ": Activity destroyed");


	}

	@Override
	protected void onResume() {
		super.onResume();

		final String METHODTAG = ".onResume";

		activityStopped = false;

		// show only connected devices
		this.availableDevices = deviceManager.getConnectedDevices();

		//Update the Devices List UI
		updateList();

		// only show info dialog once
		if (searchInfoShown == false) {
			searchInfoShown = true;
			showAlert("Finding Devices started. Please wait. Please turn on the bluetooth, wifi and gps adapter to find all possible devices in reach.");
		}

		// immediately start finding devices when activity resumes
		findAvailableDevices();
		if (LeicaSdk.INFO) {
			Log.i(CLASSTAG, METHODTAG + ": Activity onResume Completed");
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		/*final String METHODTAG = ".onStop";
		stopFindAvailableDevicesTimer();
		activityStopped = true;
		stopFindingDevices();

		Log.d(CLASSTAG, METHODTAG + ": Activity initialization was finished completely");*/


	}

	/**
	 * Connects to device
	 *
	 * @param device device to connect to
	 */
	void connectToDevice(final Device device) {
		final String METHODTAG = ".connectToDevice";

		Log.d(CLASSTAG, METHODTAG + ": Called");


		// remember to which device connection is attempting, for eventually cancelling the connection attempt
		currentConnectionAttemptToDevice = device;
		connectionAttempts.put(device, Boolean.FALSE);

		device.setConnectionListener(this);
		device.setErrorListener(this);
		device.connect();

	}

	/**
	 * start looking for available devices
	 * - first clears the UI and show only connected devices
	 * - setups deviceManager and start finding devices process
	 * - setups timer for restarting finding process (after 25 seconds start a new search iteration)
	 */
	void findAvailableDevices() {
		InformationActivityData data = Clipboard.INSTANCE.getInformationActivityData();
		if(data.isSearchingEnabled == false){
			Log.i("findAvailableDevices", "not available for searching");
			return;
		}
		final String METHODTAG = ".findAvailableDevices";

		Log.d(CLASSTAG, METHODTAG + ": Called");


		findDevicesRunning = true;

		long findAvailableDevicesDelay = 10000;
		long findAvailableDevicesPeriod = 10000;

		// show only connected devices
		availableDevices = deviceManager.getConnectedDevices();
		updateList();

		// Verify and enable Wifi and Bluetooth, according to what the user allowed
		verifyPermissions();

		deviceManager.setErrorListener(this);
		deviceManager.setFoundAvailableDeviceListener(this);

		try {
			deviceManager.findAvailableDevices(getApplicationContext());
		} catch (PermissionException e) {
			if (LeicaSdk.ERROR) {
				Log.e(CLASSTAG, METHODTAG + ": missing permission: " + e.getMessage());
			}
		}

		//
		// restart for finding devices:
		// a) because already found devices may be out of reach by now,
		// b) the user may changed adapter settings meanwhile
		if (findDevicesTimer == null) {
			findDevicesTimer = new Timer();
			findDevicesTimer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					findAvailableDevices();
				}
			}, findAvailableDevicesDelay, findAvailableDevicesPeriod);
		}
	}

	/**
	 * Switch activity
	 * BTLE devices go to BLEInformationActivty
	 * BTLE Yeti go to YetiInformationActivty
	 * Wifi devices go to WifiInformationActivity
	 */
	void goToInfoScreen(Device device) {

		final String METHODTAG = ".goToInfoScreen";

		Log.d(CLASSTAG, METHODTAG + ": Called");


		// if bluetooth
		if (device.getConnectionType() == Types.ConnectionType.ble) {
			// Stop Timer here
			stopConnectionTimeOutTimer();

			// if device is Yeti
			if (device.getDeviceType().equals(Types.DeviceType.Yeti)) {

				// set the current device object for the next activity
				//Clipboard singleton = Clipboard.INSTANCE;
				//singleton.setDevice(device);
				//Launch the YetiInformationActivity

				// need this because ble and yeti info activty uses the clipboard.
				Clipboard singleton = Clipboard.INSTANCE;
				singleton.setInformationActivityData(new InformationActivityData(device, null, deviceManager));

				YetiInformationActivity.setCurrentDevice(device, getApplicationContext());

				Intent informationIntent = new Intent(SearchDevicesActivity.this, YetiInformationActivity.class);
				startActivity(informationIntent);
			} else {
				// set the current device object for the next acitivity
				//BLEInformationActivity.setCurrentDevice(device, getApplicationContext());

				Clipboard singleton = Clipboard.INSTANCE;
				singleton.setInformationActivityData(new InformationActivityData(device, null,deviceManager));
				//////////////////////////////////////

				//Launch the BLEInformationActivity
				Intent informationIntent = new Intent(SearchDevicesActivity.this, BLEInformationActivity.class);
				startActivity(informationIntent);
			}
		} else if (device.getConnectionType() == Types.ConnectionType.wifiHotspot || device.getConnectionType() == Types.ConnectionType.wifiAP ) {

			// set the current device object for the next acitivity
			WifiInformationActivity.setCurrentDevice(device, getApplicationContext());

			//Launch the WifiInformationActivity
			Intent informationIntent = new Intent(SearchDevicesActivity.this, WifiInformationActivity.class);
			startActivity(informationIntent);

		} else if(device.getConnectionType() == Types.ConnectionType.rndis){

			// set the current device object for the next acitivity
			RndisInformationActivity.setCurrentDevice(device, getApplicationContext());

			//Launch the WifiInformationActivity
			Intent informationIntent = new Intent(SearchDevicesActivity.this, RndisInformationActivity.class);
			startActivity(informationIntent);

		}
		else {

			Log.e(CLASSTAG, METHODTAG + ": unknown connection type. this should never happen.");


		}

		// forget current device
		this.currentDevice = null;
	}

	/**
	 * read data from the Commands file and load it in the commands class
	 * <p>
	 * sets the name pattern to filter the scan results.
	 * <p>
	 * sets up DeviceManager
	 */
	void init() {

		final String METHODTAG = ".init";

		if (LeicaSdk.isInit == false){

			if (LeicaSdk.DEBUG) {
				Log.d(CLASSTAG, METHODTAG + ": Called");
			}

			// this "commands.json" file can be named differently. it only has to exist in the assets folder
			LeicaSdk.InitObject initObject = new LeicaSdk.InitObject("commands.json");

			try {
				LeicaSdk.init(getApplicationContext(), initObject);
				LeicaSdk.setLogLevel(Log.VERBOSE);
				LeicaSdk.setMethodCalledLog(false);

				//boolean distoWifi, boolean distoBle, boolean yeti, boolean disto3DD
				LeicaSdk.setScanConfig(true, true, true, true);

				// set licenses
				AppLicenses appLicenses = new AppLicenses();

				LeicaSdk.setLicenses(appLicenses.keys);

			} catch (JSONException e) {
				Toast.makeText(
						this,
						"Error in the structure of the JSON File, closing the application",
						Toast.LENGTH_LONG
				).show();

				Log.e(
						CLASSTAG,
						METHODTAG + ": Error in the structure of the JSON File, closing the application",
						e
				);

				finish();

			} catch (IllegalArgumentCheckedException e) {
				Toast.makeText(
						this,
						"Error in the data of the JSON File, closing the application",
						Toast.LENGTH_LONG
				).show();

				Log.e(CLASSTAG, METHODTAG + ": Error in the data of the JSON File, closing the application", e);

				finish();

			} catch (IOException e) {
				Toast.makeText(
						this,
						"Error reading JSON File, closing the application",
						Toast.LENGTH_LONG
				).show();

				if (LeicaSdk.ERROR) {
					Log.e(CLASSTAG, METHODTAG + ": Error reading JSON File, closing the application", e);
				}
				finish();
			}
		}

		String versionStr = LeicaSdk.getVersion();
		version.setText(String.format("Version: %s", versionStr.substring(0,versionStr.lastIndexOf("."))));

		deviceManager = DeviceManager.getInstance(this);
		deviceManager.setFoundAvailableDeviceListener(this);
		deviceManager.setErrorListener(this);

		Log.d(CLASSTAG, METHODTAG + ": Activity initialization was finished completely");

	}

	/**
	 * show wifi system settings
	 * - connection to hotspot devices needs to be done manually by the user since
	 * programmatically connection does not work properly for most android devices
	 * and android os versions
	 */
	void launchWifiPanel() {

		final String METHODTAG = ".launchWifiPanel";

		Log.d(CLASSTAG, METHODTAG + ": Called");

		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				AlertDialog.Builder builder = new AlertDialog.Builder(SearchDevicesActivity.this);
				builder.setMessage("Please connect to the WIFI HOTSPOT from the device.");
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

						connectingDialog.dismiss();

						startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

						Log.i(CLASSTAG, METHODTAG + ": Wifi Panel launched");

					}
				});
				builder.setCancelable(false);
				builder.create().show();
			}
		});
	}

	/**
	 * Displays the timeout dialog, when the app is not able to connect to the DISTO device after 30 seconds.
	 *
	 * @see #startConnectionTimeOutTimer
	 */
	void showConnectionTimedOutDialog() {
		try {
			final String METHODTAG = ".showConnectionTimedOutDialog";

			Log.d(CLASSTAG, METHODTAG + ": Called");


			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (currentDevice != null) {
						String message = "Could not connect to \n" + currentDevice.getDeviceID() +
								"\nPlease check your device and adapters and try again.";

						AlertDialog.Builder builder = new AlertDialog.Builder(SearchDevicesActivity.this);
						builder.setMessage(message).setTitle("Connection Timeout");
						builder.setNegativeButton("Ok", null);
						connectingDialog = builder.create();
						connectingDialog.setCancelable(true);
						connectingDialog.show();

						// show only connected devices
						availableDevices = deviceManager.getConnectedDevices();
						updateList();
					} else {
						if (LeicaSdk.INFO) {
							Log.i(CLASSTAG, METHODTAG + " Current Device is null.");
						}
					}
				}
			});
		}catch(Exception e){
			Log.e("SearchDevicesActivity","showConnectionTimedOutDialog"+e.getMessage(),e );
		}
	}

	/**
	 * Start a timer to stop connecting attempt and show a timeout dialog (only for BTLE devices)
	 */
	void startConnectionTimeOutTimer() {
		final String METHODTAG = ".startConnectionTimeOutTimer";

		Log.i(
				CLASSTAG,
				METHODTAG + ": Connection timeout timer started at: " + System.currentTimeMillis()
		);

		final long connectionTimeout = 90*1000; // 90s - multiple connection attempts

		//Start Timer
		this.connectionTimeoutTask = new TimerTask() {
			@Override
			public void run() {
				// stop connecting attempt
				stopConnectionAttempt();

				// show timeout dialog
				showConnectionTimedOutDialog();
				if (currentDevice != null) {
					currentDevice.disconnect();
				}

				findAvailableDevices();

			}
		};
		this.connectionTimeoutTimer.schedule(connectionTimeoutTask, connectionTimeout);
	}

	/**
	 * stop connection timeout timer
	 */
	void stopConnectionTimeOutTimer() {

		final String METHODTAG = ".stopConnectionTimeOutTimer";

		Log.i(
				CLASSTAG,
				METHODTAG + ": Connection timeoutstopped at: " + System.currentTimeMillis()
		);



		if (connectionTimeoutTask == null) {
			return;
		}
		this.connectionTimeoutTask.cancel();
		this.connectionTimeoutTimer.purge();
	}

	/**
	 * stop finding devices
	 */
	void stopFindingDevices() {
		final String METHODTAG = ".stopFindingAvailableDevices";

		Log.i(
				CLASSTAG,
				METHODTAG + ": Stop find Devices Task and set BroadcastReceivers to Null"
		);


		findDevicesRunning = false;
		deviceManager.stopFindingDevices();
	}

	/**
	 * responsible for updating the device list UI
	 */
	void updateList() {

		final String METHODTAG = ".updateList";


		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				ArrayList<Device> adapterList = new ArrayList<>(availableDevices);
				connectionTypeAdapter.setNewDeviceList(adapterList);
				connectionTypeAdapter.notifyDataSetChanged();
				deviceList.setAdapter(connectionTypeAdapter);

				Log.i(CLASSTAG, METHODTAG + ": List Updated");

			}
		});
	}

	/**
	 * To check which kind of devices/connection modes should be scanned for
	 * E.g. if wifi permission is not given the sdk only scans for bluetooth devices, vice versa
	 * The location permission is needed to scan for bluetooth devices
	 *
	 * @return an array of booleans which represents what can/should be scanned for
	 */
	boolean[] verifyPermissions() {
		final String METHODTAG = ".verifyPermissions";

		Log.d(CLASSTAG, METHODTAG + ": Called");


		ArrayList<String> manifestPermission = new ArrayList<>();

		boolean[] permissions = {false, false};
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


			if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
				manifestPermission.add(Manifest.permission.INTERNET);

			}

			if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED) {
				manifestPermission.add(Manifest.permission.ACCESS_NETWORK_STATE);
			}

			if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
				manifestPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
			}

			if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_DENIED) {
				manifestPermission.add(Manifest.permission.ACCESS_WIFI_STATE);
			}

			if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_DENIED) {
				manifestPermission.add(Manifest.permission.CHANGE_WIFI_STATE);
			}

			if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED) {
				manifestPermission.add(Manifest.permission.BLUETOOTH);
			}

			if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_DENIED) {
				manifestPermission.add(Manifest.permission.BLUETOOTH_ADMIN);
			}

			String[] manifestPermissionStrArray = new String[manifestPermission.size()];
			int i = 0;
			for (String permission : manifestPermission) {
				manifestPermissionStrArray[i] = permission;
			}
			try {
				if (manifestPermissionStrArray.length > 0) {
					ActivityCompat.requestPermissions(this, manifestPermissionStrArray, 1);
				}
			} catch (Exception e) {
				if (LeicaSdk.ERROR){
					Log.e(CLASSTAG, METHODTAG + "Permissions error: ", e);
				}
			}

			if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
				//permissions[0] = true;
				LeicaSdk.scanConfig.setWifiAdapterOn(true);
			}

			if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED && getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
				//permissions[1] = true;
				LeicaSdk.scanConfig.setBleAdapterOn(true);
			}

			LocationManager lm = (LocationManager) getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);
			boolean gps_enabled = false;
			boolean network_enabled = false;


			try {
				network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			} catch (Exception e) {

				Log.e(CLASSTAG, METHODTAG + "NETWORK PROVIDER, network not enabled", e);

			}

			if (!network_enabled) {
				// notify user

				this.runOnUiThread(new Runnable() {
					@Override
					public void run() {

						AlertDialog.Builder locationServicesDialog = new AlertDialog.Builder(SearchDevicesActivity.this);
						locationServicesDialog.setMessage("Please activate Location Services.");
						locationServicesDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {

								Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								SearchDevicesActivity.this.startActivity(myIntent);

							}
						});
						locationServicesDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface paramDialogInterface, int paramInt) {

							}
						});
						locationServicesDialog.create().show();
					}
				});
			}
		} else {
			LeicaSdk.scanConfig.setWifiAdapterOn(true);
			LeicaSdk.scanConfig.setBleAdapterOn(getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE));

		}

		Log.i(CLASSTAG, METHODTAG + ": Permissions: WIFI: " + LeicaSdk.scanConfig.isWifiAdapterOn() + ", BLE: " + LeicaSdk.scanConfig.isBleAdapterOn());

		return permissions;
	}

	/**
	 * stop connection attempt
	 */
	synchronized void stopConnectionAttempt() {

		final String METHODTAG = ".stopConnectionAttempt";
		Log.d(CLASSTAG, METHODTAG + ": Called");


		// remember for which device connection attempt is canceled
		if (currentConnectionAttemptToDevice != null) {
			connectionAttempts.put(currentConnectionAttemptToDevice, Boolean.TRUE);
		}

		stopConnectionTimeOutTimer();

		if (connectingDialog != null) {
			connectingDialog.dismiss();
		}

		if (currentDevice != null) {
			currentDevice.disconnect();
		}

	}

	private void stopFindAvailableDevicesTimer() {

		final String METHODTAG = ".stopFindAvailableDevicesTimer";
		// stop finding devices when activity stops
		// stop the timer for finding devices
		if (findDevicesTimer != null) {
			findDevicesTimer.cancel();
			findDevicesTimer.purge();
			findDevicesTimer = null;
		}
		Log.i(CLASSTAG, METHODTAG + ": FindAvailableDevices timer stopped");

	}
}