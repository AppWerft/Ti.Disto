package ch.leica.distosdkapp;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import ch.leica.sdk.Defines;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;
import ch.leica.sdk.Devices.YetiDevice;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorObject;

import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.ErrorHandling.PermissionException;
import ch.leica.sdk.ErrorHandling.WrongDataException;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Listeners.ReceivedDataListener;
import ch.leica.sdk.Reconnection.ReconnectionHelper;
import ch.leica.sdk.Types;

import ch.leica.sdk.Utilities.WaitAmoment;

import ch.leica.sdk.commands.MeasuredValue;
import ch.leica.sdk.commands.MeasurementConverter;
import ch.leica.sdk.commands.ReceivedBleDataPacket;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.ReceivedYetiDataPacket;
import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.commands.response.ResponsePlain;

import ch.leica.sdk.connection.ble.BleCharacteristic;



public class YetiInformationActivity extends AppCompatActivity implements ReceivedDataListener, Device.ConnectionListener, ErrorListener, ReconnectionHelper.ReconnectListener, Device.UpdateDeviceListener {

	private final static int APP_PERMISSIONS_REQUEST_READWRITE_EXTERNAL_STORAGE = 9001;
	static Device currentDevice;
	private ReconnectionHelper reconnectionHelper;
	static int defaultDirectionAngleUnit = MeasurementConverter.getDefaultDirectionAngleUnit();
	final CountDownLatch deviceInfoLatch = new CountDownLatch(1);
	/**
	 * Classname
	 */
	private final String CLASSTAG = YetiInformationActivity.class.getSimpleName();
	AlertDialog updateProgressDialog;
	boolean turnOnBluetoothDialogIsShown = false;
	boolean reconnectionIsRunning = false;
	boolean isDestroyed = false;
	HandlerThread sendCustomCommandThread;
	Handler sendCustomCommandHandler;
	HandlerThread getDeviceStateThread;
	Handler getDeviceStateHandler;
	HandlerThread getDeviceInfoThread;
	Handler getDeviceInfoHandler;
	TextView deviceName;
	AlertDialog customCommandDialog;
	private AlertDialog commandDialog;
	private boolean storagePermission = false;
	private TextView status;
	BroadcastReceiver bluetoothAdapterReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String METHODTAG = "bluetoothAdapterReceiver.receive";
			Log.d(CLASSTAG, METHODTAG);
			checkForReconnection();

		}
	};
	private TextView distance;
	private TextView distanceUnit;
	private TextView inclination;
	private TextView inclinationUnit;
	private TextView direction;
	private TextView directionUnit;
	private TextView timestamp_Basic_Measurements;
	//Start New Measurements for Yeti
	private TextView HZAngle;
	private TextView VeAngle;
	private TextView InclinationStatus;
	private TextView timestamp_P2P_Measurements;
	private TextView Quaternion_X;
	private TextView Quaternion_Y;
	private TextView Quaternion_Z;
	private TextView Quaternion_W;
	//End - New Measurements for Yeti
	private TextView timestamp_Quaternion_Measurements;
	private TextView Acceleration_X;
	private TextView Acceleration_Y;
	private TextView Acceleration_Z;
	private TextView AccSensitivity;
	private TextView Rotation_X;
	private TextView Rotation_Y;
	private TextView Rotation_Z;
	private TextView timestamp_ACCRotation_Measurements;
	private TextView RotationSensitivity;
	private TextView Magnetometer_X;
	private TextView Magnetometer_Y;
	private TextView Magnetometer_Z;
	private TextView timestamp_Magnetometer_Measurements;
	private TextView brandDistocom;
	private TextView IDDistocom;
	private TextView swVersionAPPDistocom;
	private TextView swVersionEDMDistocom;
	private TextView swVersionFTADistocom;
	private TextView serialAPPDistocom;
	private TextView serialEDMDistocom;
	private TextView serialFTADistocom;
	Runnable checkDeviceInfo = new Runnable() {
		@Override
		public void run() {
			getDeviceInfo();
		}
	};
	private TextView distoCOMResponse;
	private TextView distoCOMEvent;
	private TextView modelName;
	private Button deviceInfoBtn;
	private Button clear;
	private ButtonListener bl = new ButtonListener();
	private Button sendCommand;
	private Button updateButton;
	private Button reinstallButton;
	private Button distanceButton;
	private ScrollView measurementsScrollView;

	private Boolean receiverRegistered = false;
	private AlertDialog alertDialogConnect;
	private AlertDialog alertDialogDisconnect;
	private String version;

	/**
	 * Button Listener
	 */
	private class ButtonListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {

			final String METHODTAG = ".ButtonListener.onClick";

			switch (v.getId()) {
				case R.id.clear: {
					clear();
				}
				break;
				case R.id.sendCommand: {
					showCommandDialog();

				}
				break;
				case R.id.update: {
					launchUpdateProcess();
				}
				break;
				case R.id.deviceInfoButton: {
					if (getDeviceInfoThread == null) {
						getDeviceInfoThread = new HandlerThread("getDeviceStateThread_" + System.currentTimeMillis(), HandlerThread.MAX_PRIORITY);
						getDeviceInfoThread.start();
						getDeviceInfoHandler = new Handler(getDeviceInfoThread.getLooper());
					}
					getDeviceInfoHandler.post(checkDeviceInfo);
				}
				break;
				case R.id.reinstall: {
					launchReinstallProcess();
				}
				break;
				case R.id.distanceCommand: {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								//Measure polar
								final ResponsePlain response = (ResponsePlain) currentDevice.sendCommand(Types.Commands.DistanceDC);
								response.waitForData();

								if (response.getError() != null) {
									showAlert(response.getError().getErrorMessage());
								} else {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											distoCOMResponse.setText(response.getReceivedDataString());
										}
									});
								}

							} catch (DeviceException e) {
								Log.e(CLASSTAG, METHODTAG, e);
								showAlert("Error Sending Command. " + e.getMessage());
							}
						}
					}).start();
				}
				break;


				default: {

					Log.d(CLASSTAG, METHODTAG + ": Error setting data in the UI");

				}
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onAsyncDataReceived(final ReceivedData receivedData) {

		final String METHODTAG = ".onAsyncDataReceived";

		if (receivedData != null) {

			//Set data in the corresponding UI Element
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {

					//Default units = 0, for Basic Measurements, Distance, Orientation, Inclination
					try {
						ReceivedYetiDataPacket receivedYetiDataPacket = null;
						ReceivedBleDataPacket receivedBleDataPacket = null;

						if(receivedData.dataPacket instanceof ReceivedYetiDataPacket ) {
							receivedYetiDataPacket = (ReceivedYetiDataPacket) receivedData.dataPacket;
						}else{ //Model Information can come in a BleDataPacket
							receivedBleDataPacket = (ReceivedBleDataPacket) receivedData.dataPacket;
							Log.d(METHODTAG, "Async Data modelName" + receivedBleDataPacket.getModelName());
						}

						if (receivedYetiDataPacket != null) {
							String id = receivedYetiDataPacket.dataId;


							switch (id) {

								//Distance Measurement
								case Defines.ID_IMU_BASIC_MEASUREMENTS: {

									MeasuredValue distanceValue;
									MeasuredValue inclinationValue;
									MeasuredValue directionValue;

									ReceivedYetiDataPacket.YetiBasicMeasurements data = receivedYetiDataPacket.getBasicMeasurements();

									distanceValue = new MeasuredValue(data.getDistance());
									distanceValue.setUnit(data.getDistanceUnit());
									distanceValue = MeasurementConverter.convertDistance(distanceValue);
									distance.setText(distanceValue.getConvertedValueStrNoUnit());
									distanceUnit.setText(distanceValue.getUnitStr());
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getDistance());

									//Creates a measured value for the inclination angle

									inclinationValue = new MeasuredValue(data.getInclination());
									inclinationValue.setUnit(data.getInclinationUnit());
									inclinationValue = MeasurementConverter.convertAngle(inclinationValue);
									inclination.setText(inclinationValue.getConvertedValueStrNoUnit());
									inclinationUnit.setText(inclinationValue.getUnitStr());
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getInclination());

									//Creates a measured value for the direction angle
									directionValue = new MeasuredValue(data.getDirection());
									directionValue.setUnit(data.getDirectionUnit());
									directionValue = MeasurementConverter.convertAngle(directionValue);
									direction.setText(directionValue.getConvertedValueStrNoUnit());
									directionUnit.setText(directionValue.getUnitStr());
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getDirection());


									timestamp_Basic_Measurements.setText(String.valueOf(data.getTimestampAndFlags()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getTimestampAndFlags());


								}
								break;

								case Defines.ID_IMU_P2P: {


									MeasuredValue VeP2PValue;
									MeasuredValue HzP2PValue;
									ReceivedYetiDataPacket.YetiP2P data = receivedYetiDataPacket.getP2P();

									HzP2PValue = new MeasuredValue(data.getHzAngle());
									HzP2PValue.setUnit(defaultDirectionAngleUnit);
									HzP2PValue = MeasurementConverter.convertAngle(HzP2PValue);
									HZAngle.setText(HzP2PValue.getConvertedValueStrNoUnit());
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getHzAngle());

									VeP2PValue = new MeasuredValue(data.getVeAngle());
									VeP2PValue.setUnit(defaultDirectionAngleUnit);
									VeP2PValue = MeasurementConverter.convertAngle(VeP2PValue);
									VeAngle.setText(VeP2PValue.getConvertedValueStrNoUnit());
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getVeAngle());

									InclinationStatus.setText(String.valueOf(data.getInclinationStatus()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getInclinationStatus());

									timestamp_P2P_Measurements.setText(String.valueOf(data.getTimestampAndFlags()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getTimestampAndFlags());

								}
								break;

								case Defines.ID_IMU_QUATERNION: {
									ReceivedYetiDataPacket.YetiQuaternion data = receivedYetiDataPacket.getQuaternion();

									Quaternion_X.setText(String.valueOf(data.getQuaternion_X()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getQuaternion_X());

									Quaternion_Y.setText(String.valueOf(data.getQuaternion_Y()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getQuaternion_Y());

									Quaternion_Z.setText(String.valueOf(data.getQuaternion_Z()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getQuaternion_Z());

									Quaternion_W.setText(String.valueOf(data.getQuaternion_W()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getQuaternion_W());

									timestamp_Quaternion_Measurements.setText(String.valueOf(data.getTimestampAndFlags()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getTimestampAndFlags());

								}
								break;
								case Defines.ID_IMU_ACELERATION_AND_ROTATION: {
									ReceivedYetiDataPacket.YetiAccelerationAndRotation data = receivedYetiDataPacket.getAccelerationAndRotation();
									Acceleration_X.setText(String.valueOf(data.getAcceleration_X()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getAcceleration_X());

									Acceleration_Y.setText(String.valueOf(data.getAcceleration_Y()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getAcceleration_Y());

									Acceleration_Z.setText(String.valueOf(data.getAcceleration_Z()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getAcceleration_Z());

									AccSensitivity.setText(String.valueOf(data.getAccSensitivity()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getAccSensitivity());

									Rotation_X.setText(String.valueOf(data.getRotation_X()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getRotation_X());

									Rotation_Y.setText(String.valueOf(data.getRotation_Y()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getRotation_Y());

									Rotation_Z.setText(String.valueOf(data.getRotation_Z()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getRotation_Z());

									RotationSensitivity.setText(String.valueOf(data.getRotationSensitivity()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getRotationSensitivity());

									timestamp_ACCRotation_Measurements.setText(String.valueOf(data.getTimestampAndFlags()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getTimestampAndFlags());
								}
								break;


								case Defines.ID_IMU_MAGNETOMETER: {
									ReceivedYetiDataPacket.YetiMagnetometer data = receivedYetiDataPacket.getMagnetometer();
									Magnetometer_X.setText(String.valueOf(data.getMagnetometer_X()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getMagnetometer_X());

									Magnetometer_Y.setText(String.valueOf(data.getMagnetometer_Y()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getMagnetometer_Y());

									Magnetometer_Z.setText(String.valueOf(data.getMagnetometer_Z()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getMagnetometer_Z());

									timestamp_Magnetometer_Measurements.setText(String.valueOf(data.getTimestampAndFlags()));
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data.getTimestampAndFlags());

								}
								break;
								case Defines.ID_IMU_DISTOCOM_TRANSMIT: {
									String data = receivedYetiDataPacket.getDistocomReceivedMessage();
									data = data.trim();
									if (data != null && data.isEmpty() == false) {
										distoCOMResponse.setText(data);
									}
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);
								}
								break;
								case Defines.ID_IMU_DISTOCOM_EVENT: {
									String data = receivedYetiDataPacket.getDistocomReceivedMessage();
									distoCOMEvent.setText(data);
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);
								}
								break;
								default: {
									Log.d(CLASSTAG, METHODTAG + ":  Error setting data in the UI");
								}
								break;
							}
						}
					} catch (IllegalArgumentCheckedException e) {
						Log.e(CLASSTAG, METHODTAG, e);
					} catch (WrongDataException e) {
						Log.d(CLASSTAG, METHODTAG + " A wrong value has been set into the UI");
						showAlert("Wrong Value Received.");
					} catch (Exception e) {
						Log.e(CLASSTAG, METHODTAG + " Wrong data was received");
					}

				}
			});
		} else {
			Log.d(CLASSTAG, METHODTAG + ": Error onAsyncDataReceived: receivedData object is null  ");
		}
	}

	/**
	 * Check if the device is disconnected, if it is disconnected launch the reconnection function
	 *
	 * @param device the device on which the connection state changed
	 * @param state  the current connection state. If state is disconnected, the device object is not valid anymore. No connection can be established with this object any more.
	 */
	@Override
	public void onConnectionStateChanged(Device device, final Device.ConnectionState state) {
		final String METHODTAG = ".onConnectionStateChanged";
		Log.i(CLASSTAG, METHODTAG + ": " + device.getDeviceID() + ", state: " + state);

		try {
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {

					status.setText(state.toString());
					Log.i("Activity","checkForReconnection: Device is "+state.toString()+" - label need to change");
					if (reconnectionHelper != null && reconnectionHelper.isReconnectingActive() == true){
						status.setText("Reconnecting");
						Log.i("Activity","checkForReconnection: Device is reconnecting - label need to change");
					}

				}
			});

			if (state == Device.ConnectionState.disconnected) {

				this.runOnUiThread(new Runnable() {
					@Override
					public void run() {

						if (updateProgressDialog != null && updateProgressDialog.isShowing()) {
							updateProgressDialog.dismiss();
						}

						status.setText("Disconnected");
						Log.i("Activity","onConnectionStateChanged_Device is disconnected - label need to change");

					}
				});



				showConnectedDisconnectedDialog(false);
				checkForReconnection();


				return;
			}

			if(state == Device.ConnectionState.connected){
				this.runOnUiThread(new Runnable() {
					@Override
					public void run() {

						status.setText("connected");
						Log.i("Activity","onConnectionStateChanged_Device is connected - label need to change");
						stopFindingDevices();

					}
				});
			}

			showConnectedDisconnectedDialog(true);

		} catch (Exception e) {
			Log.e(CLASSTAG, METHODTAG, e);
		}

	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		InformationActivityData informationActivityData = Clipboard.INSTANCE.getInformationActivityData();
		if (informationActivityData != null){
			Device device = informationActivityData.device;
			if (device != null){
				currentDevice = device;
				currentDevice.setConnectionListener(this);
				currentDevice.setErrorListener(this);
				currentDevice.setReceiveDataListener(this);
			}

			ReconnectionHelper reconnectionHelper = informationActivityData.reconnectionHelper;
			if (reconnectionHelper != null){
				this.reconnectionHelper = reconnectionHelper;
				this.reconnectionHelper.setReconnectListener(this);
				this.reconnectionHelper.setErrorListener(this);
			}
		}

		//Initialize all UI Fields
		setContentView(R.layout.activity_yeti_information);


		commandDialog = null;
		status = (TextView) findViewById(R.id.status);
		distance = (TextView) findViewById(R.id.distance);
		distanceUnit = (TextView) findViewById(R.id.distanceUnit);
		inclination = (TextView) findViewById(R.id.inclination);
		inclinationUnit = (TextView) findViewById(R.id.inclinationUnit);
		direction = (TextView) findViewById(R.id.direction);
		directionUnit = (TextView) findViewById(R.id.directionUnit);
		timestamp_Basic_Measurements = (TextView) findViewById(R.id.timestamp_Basic_Measurements);

		HZAngle = (TextView) findViewById(R.id.fHZAngle);
		VeAngle = (TextView) findViewById(R.id.fVeAngle);
		InclinationStatus = (TextView) findViewById(R.id.uInclinationStatus);
		timestamp_P2P_Measurements = (TextView) findViewById(R.id.timestamp_P2P_Measurements);

		Quaternion_X = (TextView) findViewById(R.id.fQuaternion_X);
		Quaternion_Y = (TextView) findViewById(R.id.fQuaternion_Y);
		Quaternion_Z = (TextView) findViewById(R.id.fQuaternion_Z);
		Quaternion_W = (TextView) findViewById(R.id.fQuaternion_W);
		timestamp_Quaternion_Measurements = (TextView) findViewById(R.id.timestamp_Quaternion_Measurements);

		Acceleration_X = (TextView) findViewById(R.id.sAcceleration_X);
		Acceleration_Y = (TextView) findViewById(R.id.sAcceleration_Y);
		Acceleration_Z = (TextView) findViewById(R.id.sAcceleration_Z);
		AccSensitivity = (TextView) findViewById(R.id.sAccSensitivity);
		Rotation_X = (TextView) findViewById(R.id.sRotation_X);
		Rotation_Y = (TextView) findViewById(R.id.sRotation_Y);
		Rotation_Z = (TextView) findViewById(R.id.sRotation_Z);
		timestamp_ACCRotation_Measurements = (TextView) findViewById(R.id.timestamp_ACCRotation_Measurements);
		RotationSensitivity = (TextView) findViewById(R.id.fRotationSensitivity);


		Magnetometer_X = (TextView) findViewById(R.id.fMagnetometer_X);
		Magnetometer_Y = (TextView) findViewById(R.id.fMagnetometer_Y);
		Magnetometer_Z = (TextView) findViewById(R.id.fMagnetometer_Z);
		timestamp_Magnetometer_Measurements = (TextView) findViewById(R.id.timestamp_Magnetometer_Measurements);

		distoCOMResponse = (TextView) findViewById(R.id.distoCOMResponse);
		distoCOMEvent = (TextView) findViewById(R.id.distoCOMEvent);


		brandDistocom = (TextView) findViewById(R.id.brandDistocom);
		IDDistocom = (TextView) findViewById(R.id.IDDistocom);
		swVersionAPPDistocom = (TextView) findViewById(R.id.swVersionAPPDistocom);
		swVersionEDMDistocom = (TextView) findViewById(R.id.swVersionEDMDistocom);
		swVersionFTADistocom = (TextView) findViewById(R.id.swVersionFTADistocom);
		serialAPPDistocom = (TextView) findViewById(R.id.serialAPPDistocom);
		serialEDMDistocom = (TextView) findViewById(R.id.serialEDMDistocom);
		serialFTADistocom = (TextView) findViewById(R.id.serialFTADistocom);


		clear = (Button) findViewById(R.id.clear);
		clear.setOnClickListener(bl);
		deviceInfoBtn = (Button) findViewById(R.id.deviceInfoButton);
		deviceInfoBtn.setVisibility(View.INVISIBLE);
		deviceInfoBtn.setOnClickListener(bl);

		sendCommand = (Button) findViewById(R.id.sendCommand);
		sendCommand.setOnClickListener(bl);

		measurementsScrollView = (ScrollView) findViewById(R.id.commands);

		updateButton = (Button) findViewById(R.id.update);
		updateButton.setOnClickListener(bl);

		reinstallButton = (Button) findViewById(R.id.reinstall);
		reinstallButton.setOnClickListener(bl);
		distanceButton = (Button) findViewById(R.id.distanceCommand);
		distanceButton.setOnClickListener(bl);


		modelName = (TextView) findViewById(R.id.modelName);
		deviceName = (TextView) findViewById(R.id.deviceName);

		// set values when activity got recreated
		if (currentDevice != null) {
			deviceName.setText(currentDevice.getDeviceName());
			modelName.setText(currentDevice.getModel());
		}

		//Connect /Disconnect dialog
		AlertDialog.Builder alertConnectedBuilder = new AlertDialog.Builder(YetiInformationActivity.this);
		alertConnectedBuilder.setMessage("connection established");
		alertConnectedBuilder.setPositiveButton("Ok", null);

		AlertDialog.Builder alertDisconnectedBuilder = new AlertDialog.Builder(YetiInformationActivity.this);
		alertDisconnectedBuilder.setMessage("lost connection to device");
		alertDisconnectedBuilder.setPositiveButton("Ok", null);

		alertDialogConnect = alertConnectedBuilder.create();
		alertDialogDisconnect = alertDisconnectedBuilder.create();

		measurementsScrollView.setEnabled(true);
		measurementsScrollView.setVisibility(View.VISIBLE);
		deviceInfoBtn.setEnabled(true);
		deviceInfoBtn.setVisibility(View.VISIBLE);
		sendCommand.setEnabled(true);
		sendCommand.setVisibility(View.VISIBLE);
		updateButton.setEnabled(true);
		updateButton.setVisibility(View.VISIBLE);
		reinstallButton.setEnabled(true);
		reinstallButton.setVisibility(View.VISIBLE);
		distanceButton.setEnabled(true);
		distanceButton.setVisibility(View.VISIBLE);
		clear.setEnabled(true);
		clear.setVisibility(View.VISIBLE);

	}

	@Override
	protected void onStop() {
		super.onStop();

		//Unregister activity for bluetooth adapter changes

		if (receiverRegistered) {
			unregisterReceiver(bluetoothAdapterReceiver);
			receiverRegistered = false;
		}

		if (commandDialog != null) {
			commandDialog.dismiss();
		}

		if (customCommandDialog != null) {
			customCommandDialog.dismiss();
		}


		// Pause the bluetooth connection
		if (currentDevice != null) {
			try {
				currentDevice.pauseBTConnection(new Device.BTConnectionCallback() {
					@Override
					public void onFinished() {
						Log.d("onStop", "NOW Notifications are deactivated in the device");
						distanceButton.setEnabled(false);
						distanceButton.setVisibility(View.INVISIBLE);
						sendCommand.setEnabled(false);
						sendCommand.setVisibility(View.INVISIBLE);
						clear.setEnabled(false);
						clear.setVisibility(View.INVISIBLE);
						updateButton.setEnabled(false);
						updateButton.setVisibility(View.INVISIBLE);
						reinstallButton.setEnabled(false);
						reinstallButton.setVisibility(View.INVISIBLE);
						deviceInfoBtn.setEnabled(false);
						deviceInfoBtn.setVisibility(View.INVISIBLE);

					}
				});
			} catch (DeviceException e) {
				e.printStackTrace();
			}
		}

		InformationActivityData data = Clipboard.INSTANCE.getInformationActivityData();
		data.isSearchingEnabled = true;
		try {
			data.deviceManager.findAvailableDevices(this.getApplicationContext());
		} catch (PermissionException e) {
				e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		InformationActivityData data = Clipboard.INSTANCE.getInformationActivityData();
		data.device = currentDevice;
		data.reconnectionHelper = reconnectionHelper;

		final String METHODTAG = ".onDestroy";

		isDestroyed = true;

		//Unregister the activity for changes in the connection
		if (currentDevice != null) {
			currentDevice.setReceiveDataListener(null);
			currentDevice.setConnectionListener(null);
			currentDevice.setErrorListener(null);
			currentDevice = null;
		}
		//Unregister the activity for reconnection events
		if (reconnectionHelper != null) {
			reconnectionHelper.setErrorListener(null);
			reconnectionHelper.setReconnectListener(null);
			reconnectionHelper.stopReconnecting();
			reconnectionHelper = null;
			reconnectionIsRunning = false;
		}

		if (sendCustomCommandThread != null) {
			sendCustomCommandThread.interrupt();
			sendCustomCommandThread = null;
			sendCustomCommandHandler = null;
		}
		if (getDeviceStateThread != null) {
			getDeviceStateThread.interrupt();
			getDeviceStateThread = null;
			getDeviceStateHandler = null;
		}

		if (getDeviceInfoThread != null) {
			getDeviceInfoThread.interrupt();
			getDeviceInfoThread = null;
			getDeviceInfoHandler = null;
		}

//		if (currentDevice != null) {
//			//Disconnect the device
//			currentDevice.disconnect();
//			Log.d(CLASSTAG, METHODTAG + "Disconnected Device: " + currentDevice.modelName);
//
//		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onError(ErrorObject errorObject, Device device) {
		final String METHODTAG = ".onError";

		Log.e(CLASSTAG, METHODTAG + ": " + errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());
		if(currentDevice.getConnectionState().equals(Device.ConnectionState.disconnected) == false) {
			showAlert(errorObject.getErrorMessage() + " \n ErrorCode: " + errorObject.getErrorCode());
		}
	}

	/**
	 * Implemented method from Device.UpdateDeviceListener
	 *
	 * @param bytesSent        the number of bytes that are already sent
	 * @param bytesTotalNumber the total number of bytes that should be sent
	 */
	@Override
	public void onProgress(long bytesSent, long bytesTotalNumber) {

		final String METHODTAG = ".onProgress";

		Log.d(CLASSTAG, METHODTAG + ": number of bytes sent: " + bytesSent + " of total bytes: " + bytesTotalNumber);


		setUpdateProgressDialog("Updating device: "+dialogTitle, version +"\n"+
				" Progress: " + bytesSent/1024 + " kb/" + bytesTotalNumber/1024+" kb");

	}

	@Override
	public void onFirmwareUpdateStarted(String filename, String version) {
		final String METHODTAG = ".onFirmwareUpdateStarted";
		if(filename != null && version!=null)
		{
			Log.d(CLASSTAG, METHODTAG + ": Update Information: " + filename + " Version: " + version);
			this.dialogTitle = filename;
			this.version =  " Version: " + version;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onReconnect(Device device) {
		final String METHODTAG = ".onReconnect";
		Log.d(CLASSTAG, METHODTAG);

		reconnectionIsRunning = false;

		currentDevice = device;
		currentDevice.setErrorListener(this);
		currentDevice.setConnectionListener(this);
		currentDevice.setReceiveDataListener(this);

		reconnectionHelper = new ReconnectionHelper(currentDevice, getApplicationContext());
		reconnectionHelper.setReconnectListener(this);
		reconnectionHelper.setErrorListener(this);

		onConnectionStateChanged(currentDevice, currentDevice.getConnectionState());

		if (getDeviceStateThread == null) {
			getDeviceStateThread = new HandlerThread("getDeviceStateThread_" + System.currentTimeMillis(), HandlerThread.MAX_PRIORITY);
			getDeviceStateThread.start();
			getDeviceStateHandler = new Handler(getDeviceStateThread.getLooper());
		}


	}

	@Override
	protected void onResume() {
		super.onResume();

		final String METHODTAG = "onResume";
		//Put allUI disabled until getting the deviceState

		updateButton.setEnabled(true);
		updateButton.setVisibility(View.VISIBLE);

		reinstallButton.setEnabled(true);
		reinstallButton.setVisibility(View.VISIBLE);
		distanceButton.setEnabled(true);
		distanceButton.setVisibility(View.VISIBLE);

		sendCommand.setEnabled(true);
		sendCommand.setVisibility(View.VISIBLE);

		clear.setEnabled(true);
		clear.setVisibility(View.VISIBLE);

		measurementsScrollView.setVisibility(View.VISIBLE);

		// Register activity for bluetooth adapter changes
		if (!receiverRegistered) {
			receiverRegistered = true;
			IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
			registerReceiver(bluetoothAdapterReceiver, filter);
		}

		// setup according to device
		if (currentDevice != null) {
			currentDevice.setConnectionListener(this);
			currentDevice.setReceiveDataListener(this);
			currentDevice.setErrorListener(this);

			status.setText(currentDevice.getConnectionState().toString());
			Log.i("Activity","onConnectionStateChanged_Device is "+currentDevice.getConnectionState().toString()+" - label need to change");



			if (currentDevice.getConnectionState().equals(Device.ConnectionState.connected)) {

				// Ask for model number and serial number, this will result async
				if (currentDevice.getConnectionState().equals(Device.ConnectionState.connected)) {
					deviceName.setText(currentDevice.getDeviceName());

					// Ask for model number and serial number, this will result async
					String model = currentDevice.getModel();
					if (model.isEmpty() == false) {
						setUI(model);
					}
				}

				if (getDeviceStateThread == null) {
					getDeviceStateThread = new HandlerThread("getDeviceStateThread" +
							System.currentTimeMillis(), HandlerThread.MAX_PRIORITY);

					getDeviceStateThread.start();
					getDeviceStateHandler = new Handler(getDeviceStateThread.getLooper());
				}

				// start bt connection
				try {
					currentDevice.startBTConnection(new Device.BTConnectionCallback() {
						@Override
						public void onFinished() {

							distanceButton.setEnabled(true);
							distanceButton.setVisibility(View.VISIBLE);
							sendCommand.setEnabled(true);
							sendCommand.setVisibility(View.VISIBLE);
							clear.setEnabled(true);
							clear.setVisibility(View.VISIBLE);
							updateButton.setEnabled(true);
							updateButton.setVisibility(View.VISIBLE);
							reinstallButton.setEnabled(true);
							reinstallButton.setVisibility(View.VISIBLE);
							deviceInfoBtn.setEnabled(true);
							deviceInfoBtn.setVisibility(View.VISIBLE);
							Log.d(METHODTAG, "NOW YOU CAN SEND COMMANDS TO THE DEVICE");


						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

				/*this.runOnUiThread(new Runnable() {
					@Override
					public void run() {

						status.setText("Connected");

					}
				});*/

			}
			if (currentDevice.getConnectionState().equals(Device.ConnectionState.disconnected)) {
				this.runOnUiThread(new Runnable() {
					@Override
					public void run() {

						status.setText("disconnected");
						Log.i("Activity","onConnectionStateChanged_Device is disconnected - label need to change");

					}
				});
			}

			if (reconnectionHelper != null && reconnectionHelper.isReconnectingActive() == true){
				status.setText("reconnecting");
				Log.i("Activity","onConnectionStateChanged_Device is reconnecting - label need to change");
			}


		}
		if (reconnectionHelper != null) {
			Log.i(METHODTAG,"reconnectionHelper Set");
			reconnectionHelper.setErrorListener(this);
			reconnectionHelper.setReconnectListener(this);
			reconnectionHelper.setDeviceManager();
		}
		else{
			Log.i(METHODTAG,"reconnectionHelper is not set");

			InformationActivityData informationActivityData = Clipboard.INSTANCE.getInformationActivityData();
			ReconnectionHelper reconnectionHelper = informationActivityData.reconnectionHelper;
			if (reconnectionHelper != null){
				this.reconnectionHelper = reconnectionHelper;
				this.reconnectionHelper.setReconnectListener(this);
				this.reconnectionHelper.setErrorListener(this);
			}
		}

		if (reconnectionIsRunning) {
			status.setText(R.string.reconnecting);
			Log.i("Activity","onConnectionStateChanged_Device is "+R.string.reconnecting+" - label need to change");
		}

		try {
			if (currentDevice != null) {
				for (BleCharacteristic bGC : currentDevice.getAllCharacteristics()) {
					Log.d(CLASSTAG, METHODTAG + " ALL Characteristics UI:strValue:" + bGC.getStrValue());
				}
			}
		} catch (DeviceException e) {
			Log.d(CLASSTAG, METHODTAG + " Error getting the list of characteristics");
		}

		this.stopFindingDevices();

		SetStatusTask statusTask = new SetStatusTask();
		Timer timer = new Timer();
		timer.schedule(statusTask, 3000, 1500);


	}

	class SetStatusTask extends TimerTask {
		public void run() {
			// ERROR
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(currentDevice!=null && currentDevice.getConnectionState() != null) {
						status.setText(currentDevice.getConnectionState().toString());
					}
				}
			});

		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case APP_PERMISSIONS_REQUEST_READWRITE_EXTERNAL_STORAGE: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					//showUpdateTypeDialog();

				} else {
					this.updateButton.setEnabled(false);
					this.reinstallButton.setEnabled(false);
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	public void extractDataFromPlainResponse(ResponsePlain response) {

		final String METHODTAG = "extractDataFromPlainResponse";
		Log.v(CLASSTAG, METHODTAG + " called");

		distoCOMResponse.setText(response.getReceivedDataString());
	}

	/**
	 * Sending a command will return a Response object.
	 * Which type of response object is dependend of the sended command.
	 *
	 * @param response response object holding the data sent by the device
	 */
	public void readDataFromResponseObject(final Response response) {

		final String METHODTAG = ".readDataFromResponseObject";
		Log.v(CLASSTAG, METHODTAG + " called");

		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (response.getError() != null) {

					Log.e(CLASSTAG, METHODTAG + ": response error: " + response.getError().getErrorMessage());

					return;
				}

				if (response instanceof ResponsePlain) {
					extractDataFromPlainResponse((ResponsePlain) response);
				}
			}
		});

	}

	AlertDialog alertDialog;
	public void showAlert(final String message) {

		if (isDestroyed) {
			return;
		}

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(YetiInformationActivity.this);
				alertBuilder.setMessage(message);
				alertBuilder.setPositiveButton("Ok", null);
				alertDialog = alertBuilder.create();
				alertDialog.show();

			}
		});

	}

	/**
	 * show a list of available commands in a dialog
	 */
	public void showCommandDialog() {

		final String METHODTAG = ".showCommandDialog";
		Log.v(CLASSTAG, METHODTAG + " called");

		AlertDialog.Builder comandDialogBuilder = new AlertDialog.Builder(this);
		comandDialogBuilder.setTitle("Select Command");
		comandDialogBuilder.setItems(currentDevice.getAvailableCommands(), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String command = currentDevice.getAvailableCommands()[which];

				if (command.equals(Types.Commands.Custom.name())) {
					showCustomCommandDialog();
				} else {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Response response = currentDevice.sendCommand(Types.Commands.valueOf(command));
								response.waitForData();

								readDataFromResponseObject(response);
							} catch (DeviceException e) {
								Log.e(CLASSTAG, METHODTAG + ": send command error: ", e);
								showAlert("Error Sending Command. " + e.getMessage());
							}
						}

					}).start();
				}
			}
		});
		commandDialog = comandDialogBuilder.create();
		commandDialog.show();
	}

	void dismissUpdateProgressDialog() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if (updateProgressDialog == null) {
						return;
					}
					updateProgressDialog.dismiss();
				}catch(Exception e){
					e.printStackTrace();
				}


			}
		});
	}

	private String dialogTitle = "Updating Device: ";

	void setUpdateProgressDialog(final String title, final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (updateProgressDialog == null) {
					showUpdateProgressDialog(title);
				}
				updateProgressDialog.setTitle(title);
				updateProgressDialog.setMessage(message);
			}
		});
	}

	/**
	 * show a dialog in which a user can type in a text and send it as a command
	 */
	void showCustomCommandDialog() {


		final String METHODTAG = ".showCustomCommandDialog";
		final EditText input = new EditText(this);
		AlertDialog.Builder customCommandDialogBuilder = new AlertDialog.Builder(this);
		customCommandDialogBuilder.setTitle(R.string.custom_command);
		customCommandDialogBuilder.setView(input);
		customCommandDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {


				if (sendCustomCommandThread == null) {
					sendCustomCommandThread =
							new HandlerThread(
									"getDeviceStateThread" + System.currentTimeMillis(),
									HandlerThread.MAX_PRIORITY
							);

					sendCustomCommandThread.start();
					sendCustomCommandHandler = new Handler(sendCustomCommandThread.getLooper());
				}

				// send any string to device
				try {
					sendCustomCommandHandler.post(new Runnable() {
						@Override
						public void run() {
							try {
								Response response =
										currentDevice.sendCustomCommand(
												input.getText().toString()/*,
												currentDevice.getTIMEOUT_NORMAL()*/
										);

								response.waitForData();

								if (response.getError() != null) {
									Log.e(
											CLASSTAG,
											METHODTAG + ": error: " + response.getError().getErrorMessage()
									);
								}
								setDistoComResponse(((ResponsePlain) response).getReceivedDataString());
								Log.d(CLASSTAG, METHODTAG + "DistoComResponse set with ResponsePlain");

							} catch (DeviceException e) {

								Log.e(CLASSTAG, METHODTAG + ": Error sending the command.", e);
							}
						}
					});

				} catch (Exception e) {
					Log.e(CLASSTAG, METHODTAG + ": Error showCustomCommandDialog ", e);
				}
			}
		});
		customCommandDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		customCommandDialog = customCommandDialogBuilder.create();
		customCommandDialog.show();
	}

	void showUpdateProgressDialog(final String title) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (updateProgressDialog != null && updateProgressDialog.isShowing()) {
					updateProgressDialog.dismiss();
				}
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(YetiInformationActivity.this);
				dialogBuilder.setTitle(title);
				dialogBuilder.setMessage("Please wait...");
				dialogBuilder.setCancelable(false);
				updateProgressDialog = dialogBuilder.create();
				updateProgressDialog.show();
			}
		});
	}

	void startReinstallProcess() {
		YetiActivityUpdateProcessHelper helper = new YetiActivityUpdateProcessHelper();
		helper.startReinstallProcess((YetiDevice)currentDevice, this, storagePermission);
	}

	void startUpdateProcess() {
		YetiActivityUpdateProcessHelper helper = new YetiActivityUpdateProcessHelper();
		helper.startUpdateProcess((YetiDevice)currentDevice, storagePermission, this);
	}

	public static void setCurrentDevice(Device currentDevice, Context context) {
		YetiInformationActivity.currentDevice = currentDevice;
	}

	/**
	 * Verify if the current device need to be reconnected
	 * If the device disconnected and the reconnection function has not been called, then it will start
	 */
	synchronized void checkForReconnection() {

		final String METHODTAG = ".checkForReconnection";

		reconnectionHelper = new ReconnectionHelper(currentDevice, getApplicationContext());
		reconnectionHelper.setErrorListener(this);
		reconnectionHelper.setReconnectListener(this);

		if (currentDevice == null) {
			return;
		}
		if (currentDevice.getConnectionState() == Device.ConnectionState.connected) {
			return;
		}
		if (currentDevice.getConnectionType().equals(Types.ConnectionType.ble) == false){
			return;
		}
		if (turnOnBluetoothDialogIsShown == true) {
			Log.d(CLASSTAG, METHODTAG + ":  turnOnBluetoothDialogIsShown is true");
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				// wait a moment for bluetooth adapter to be setup
				WaitAmoment wait = new WaitAmoment();
				wait.waitAmoment(2000);

				// check if bluetooth is available
				boolean bluetoothIsAvailable = DeviceManager.getInstance(getApplicationContext()).checkBluetoothAvailibilty();
				if (!bluetoothIsAvailable) {
					Log.d(CLASSTAG, METHODTAG + ": bluetooth is not available");

					// show alert to turn on bluetooth
					showBluetoothTurnOn();

					return;
				}
				if(reconnectionHelper != null){
					reconnectionHelper = new ReconnectionHelper(currentDevice, getApplicationContext());
					reconnectionHelper.setErrorListener(YetiInformationActivity.this);
					reconnectionHelper.setReconnectListener(YetiInformationActivity.this);
				}

				if (!reconnectionIsRunning && reconnectionHelper != null) {
					reconnectionIsRunning = true;
					reconnectionHelper.startReconnecting();

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							status.setText(R.string.reconnecting);
							Log.i("Activity","checkForReconnection: Device is reconnecting - label need to change");
						}
					});
				}

			}
		}).start();
	}

	synchronized void showBluetoothTurnOn() {

		final String METHODTAG = ".showBluetoothTurnOn";
		if (turnOnBluetoothDialogIsShown) {
			Log.d(CLASSTAG, METHODTAG + ":  dialog is already shown");
			return;
		}

		turnOnBluetoothDialogIsShown = true;
		Log.d(CLASSTAG, METHODTAG + ": turnOnBluetoothDialogIsShown is true");

		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if(alertDialog != null) {
					alertDialog.dismiss();
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(YetiInformationActivity.this);
				builder.setMessage("Bluetooth has to be turned on.");
				builder.setPositiveButton("Turn it on", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							turnOnBluetoothDialogIsShown = false;
							DeviceManager.getInstance(getApplicationContext()).enableBLE();
						}
					});
				builder.create().show();
				Log.d(CLASSTAG, METHODTAG + ": SHOW");
			}
		});
	}

	/**
	 * Shows dialog after connection status changed.
	 *
	 * @param connected true -> Connected, false -> Disconnected
	 */
	synchronized void showConnectedDisconnectedDialog(final boolean connected) {
		try {

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						if (connected) {
							alertDialogConnect.show();
							if (alertDialogDisconnect.isShowing()) {
								alertDialogDisconnect.dismiss();
							}
						} else {
							alertDialogDisconnect.show();
							if (alertDialogDisconnect.isShowing()) {
								alertDialogConnect.dismiss();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show bluetooth turnOn dialog
	 */
	synchronized void showUpdateMessages(final String message) {

		final String METHODTAG = ".showUpdateMessages";
		if (turnOnBluetoothDialogIsShown) {
			Log.d(CLASSTAG, METHODTAG + ": dialog is already shown");
			return;
		}

		turnOnBluetoothDialogIsShown = true;
		Log.d(CLASSTAG, METHODTAG + ":  turnOnBluetoothDialogIsShown is true");

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					AlertDialog.Builder builder = new android.app.AlertDialog.Builder(YetiInformationActivity.this);
					builder.setMessage(message);
					builder.setCancelable(false);
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							turnOnBluetoothDialogIsShown = false;
							DeviceManager.getInstance(getApplicationContext()).enableBLE();
						}
					});
					builder.create().show();
					Log.d(CLASSTAG, METHODTAG + ": (String message)");
				} catch (Exception e) {
					Log.e(CLASSTAG, "Error showing update Message, UI Error", e);
				}
			}
		});

	}

	/**
	 * Clear all textfields
	 */
	private void clear() {
		distance.setText(R.string.default_value);
		distanceUnit.setText(R.string.default_value);
		inclination.setText(R.string.default_value);
		inclinationUnit.setText(R.string.default_value);
		direction.setText(R.string.default_value);
		directionUnit.setText(R.string.default_value);
		timestamp_Basic_Measurements.setText(R.string.default_value);

		HZAngle.setText(R.string.default_value);
		VeAngle.setText(R.string.default_value);
		InclinationStatus.setText(R.string.default_value);
		timestamp_P2P_Measurements.setText(R.string.default_value);

		Quaternion_X.setText(R.string.default_value);
		Quaternion_Y.setText(R.string.default_value);
		Quaternion_Z.setText(R.string.default_value);
		Quaternion_W.setText(R.string.default_value);
		timestamp_Quaternion_Measurements.setText(R.string.default_value);

		Acceleration_X.setText(R.string.default_value);
		Acceleration_Y.setText(R.string.default_value);
		Acceleration_Z.setText(R.string.default_value);
		AccSensitivity.setText(R.string.default_value);
		Rotation_X.setText(R.string.default_value);
		Rotation_Y.setText(R.string.default_value);
		Rotation_Z.setText(R.string.default_value);
		RotationSensitivity.setText(R.string.default_value);
		timestamp_ACCRotation_Measurements.setText(R.string.default_value);

		Magnetometer_X.setText(R.string.default_value);
		Magnetometer_Y.setText(R.string.default_value);
		Magnetometer_Z.setText(R.string.default_value);
		timestamp_Magnetometer_Measurements.setText(R.string.default_value);

		distoCOMEvent.setText(R.string.default_value);
		distoCOMResponse.setText(R.string.default_value);

		brandDistocom.setText(R.string.default_value);
		IDDistocom.setText(R.string.default_value);
		swVersionAPPDistocom.setText(R.string.default_value);
		swVersionEDMDistocom.setText(R.string.default_value);
		swVersionFTADistocom.setText(R.string.default_value);
		serialAPPDistocom.setText(R.string.default_value);
		serialEDMDistocom.setText(R.string.default_value);
		serialFTADistocom.setText(R.string.default_value);

	}

	private void getDeviceInfo() {

		try {
			/*if (currentDevice != null && currentDevice.isInUpdateMode() == false) {
				return;
			}*/

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ResponsePlain response = null;
					try {

						response = (ResponsePlain) currentDevice.sendCommand(Types.Commands.GetBrandDistocom);
						response.waitForData();
						String logCommandTag = "getBrand";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: " + response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": " + response.getReceivedDataString());
						}
						brandDistocom.setText(response.getReceivedDataString());

						response = (ResponsePlain) currentDevice.sendCommand(Types.Commands.GetIDDistocom);
						response.waitForData();
						logCommandTag = "getId";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: " + response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": " + response.getReceivedDataString());
						}
						IDDistocom.setText(response.getReceivedDataString());

						response = (ResponsePlain) currentDevice.sendCommand(Types.Commands.GetSoftwareVersionAPPDistocom);
						response.waitForData();
						logCommandTag = "GetSoftwareVersionAPPDistocom";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: " + response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": " + response.getReceivedDataString());
						}
						swVersionAPPDistocom.setText(response.getReceivedDataString());

						response = (ResponsePlain) currentDevice.sendCommand(Types.Commands.GetSoftwareVersionEDMDistocom);
						response.waitForData();
						logCommandTag = "GetSoftwareVersionEDMDistocom";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: " + response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": " + response.getReceivedDataString());
						}
						swVersionEDMDistocom.setText(response.getReceivedDataString());

						response = (ResponsePlain) currentDevice.sendCommand(Types.Commands.GetSoftwareVersionFTADistocom);
						response.waitForData();
						logCommandTag = "GetSoftwareVersionFTADistocom";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: " + response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": " + response.getReceivedDataString());
						}
						swVersionFTADistocom.setText(response.getReceivedDataString());

						response = (ResponsePlain) currentDevice.sendCommand(Types.Commands.GetSerialAPPDistocom);
						response.waitForData();
						logCommandTag = "GetSerialAPPDistocom";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: " + response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": " + response.getReceivedDataString());
						}
						serialAPPDistocom.setText(response.getReceivedDataString());

						response = (ResponsePlain) currentDevice.sendCommand(Types.Commands.GetSerialEDMDistocom);
						response.waitForData();
						logCommandTag = "GetSerialEDMDistocom";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: " + response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": " + response.getReceivedDataString());
						}
						serialEDMDistocom.setText(response.getReceivedDataString());

						response = (ResponsePlain) currentDevice.sendCommand(Types.Commands.GetSerialFTADistocom);
						response.waitForData();
						logCommandTag = "GetSerialFTADistocom";
						if (response.getError() != null) {
							Log.d("getDeviceInfo", logCommandTag + " error: " + response.getError().getErrorMessage());
						} else {
							Log.d("getDeviceInfo", logCommandTag + ": " + response.getReceivedDataString());
						}
						serialFTADistocom.setText(response.getReceivedDataString());

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

	private void launchReinstallProcess() {

		final String METHODTAG = ".launchReinstallProcess";

		try {
			// Here, this Activity is the current activity
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				/*if (ContextCompat.checkSelfPermission(YetiInformationActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(YetiInformationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {*/

				// Should we show an explanation?
					/*if (ActivityCompat.shouldShowRequestPermissionRationale(YetiInformationActivity.this,
							Manifest.permission.READ_EXTERNAL_STORAGE)) {

						showAlert("Read and write permissions are needed to update the device");

						// Show an explanation to the user *asynchronously* -- don't block
						// this thread waiting for the user's response! After the user
						// sees the explanation, try again to request the permission.

					}*/

				// No explanation needed, we can request the permission.
							/*ActivityCompat.requestPermissions(YetiInformationActivity.this,
							new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
							APP_PERMISSIONS_REQUEST_READWRITE_EXTERNAL_STORAGE);*/


				/*}else{*/
				storagePermission = true;
				Log.d(CLASSTAG, "StoragePermission, Storage permission granted");
				startReinstallProcess();
				/*}*/
			} else {
				storagePermission = true;
				Log.d(CLASSTAG, "StoragePermission, Storage permission granted");
				startReinstallProcess();
			}


		} catch (Exception e) {
			Log.e(CLASSTAG, METHODTAG, e);
		}

	}

	/**
	 *
	 */
	private void launchUpdateProcess() {

		final String METHODTAG = ".launchUpdateProcess";



		try {
			// Here, this Activity is the current activity
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				/*if (ContextCompat.checkSelfPermission(YetiInformationActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(YetiInformationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {*/

				// Should we show an explanation?
					/*if (ActivityCompat.shouldShowRequestPermissionRationale(YetiInformationActivity.this,
							Manifest.permission.READ_EXTERNAL_STORAGE)) {

						showAlert("Read and write permissions are needed to update the device");

						// Show an explanation to the user *asynchronously* -- don't block
						// this thread waiting for the user's response! After the user
						// sees the explanation, try again to request the permission.

					}*/

				// No explanation needed, we can request the permission.
							/*ActivityCompat.requestPermissions(YetiInformationActivity.this,
							new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
							APP_PERMISSIONS_REQUEST_READWRITE_EXTERNAL_STORAGE);*/


				/*}else{*/
				storagePermission = true;
				Log.d(CLASSTAG, "StoragePermission, Storage permission granted");
				startUpdateProcess();
				/*}*/
			} else {
				storagePermission = true;
				Log.d(CLASSTAG, "StoragePermission, Storage permission granted");
				startUpdateProcess();
			}


		} catch (Exception e) {
			Log.e(CLASSTAG, METHODTAG, e);
		}

	}


	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//Data Extraction
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	private void setDistoComResponse(final String receivedDataString) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				distoCOMResponse.setText(receivedDataString);
			}
		});
	}

	/**
	 * Show the corresponding UI elements for each of the models
	 * Different Leica models support different BTLE functionality.
	 *
	 * @param deviceModel Device Model
	 */
	private void setUI(String deviceModel) {

		modelName.setText(deviceModel);


	}
	/**
	 * stop finding devices
	 */
	void stopFindingDevices() {
		final String METHODTAG = ".stopFindingAvailableDevices";

		Log.i(CLASSTAG, METHODTAG + ": Stop find Devices Task and set BroadcastReceivers to Null");
		InformationActivityData informationActivityData = Clipboard.INSTANCE.getInformationActivityData();
		informationActivityData.isSearchingEnabled = false;
		informationActivityData.deviceManager.stopFindingDevices();
	}


}