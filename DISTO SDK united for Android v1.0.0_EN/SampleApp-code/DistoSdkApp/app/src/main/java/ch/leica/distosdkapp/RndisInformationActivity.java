package ch.leica.distosdkapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import ch.leica.sdk.Defines;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.Disto3DDevice;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorDefinitions;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.ErrorHandling.WrongDataException;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Listeners.ReceivedDataListener;
import ch.leica.sdk.Reconnection.ReconnectionHelper;
import ch.leica.sdk.Types;
import ch.leica.sdk.Utilities.LiveImagePixelConverter;
import ch.leica.sdk.Utilities.WaitAmoment;
import ch.leica.sdk.Utilities.WifiHelper;
import ch.leica.sdk.commands.Image;
import ch.leica.sdk.commands.LiveImage;
import ch.leica.sdk.commands.MeasuredValue;
import ch.leica.sdk.commands.MeasurementConverter;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.ReceivedWifiDataPacket;
import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.commands.response.ResponseBatteryStatus;
import ch.leica.sdk.commands.response.ResponseDeviceInfo;
import ch.leica.sdk.commands.response.ResponseFace;
import ch.leica.sdk.commands.response.ResponseImage;
import ch.leica.sdk.commands.response.ResponseMotorStatus;
import ch.leica.sdk.commands.response.ResponsePlain;
import ch.leica.sdk.commands.response.ResponseTemperature;
import ch.leica.sdk.commands.response.ResponseWifiMeasurements;

import static android.R.attr.id;


public class RndisInformationActivity extends AppCompatActivity implements Device.ConnectionListener, ReceivedDataListener, ErrorListener, ReconnectionHelper.ReconnectListener {

	/**
	 * ClassName
	 */
	private static final String CLASSTAG = RndisInformationActivity.class.getSimpleName();

	static Device currentDevice;
	static ReconnectionHelper reconnectionHelper;

	//Variables hold information transferred from the Leica devices
	private ImageView previewImage;
	private ImageView levelImage; //Indicates level status

	/**
	 * Distance
	 */
	private TextView dist;

	/**
	 * Distance Unit
	 */
	private TextView distUnit;

	/**
	 * Horizontal angle with tilt
	 */
	private TextView horizontalAngle;

	/**
	 * Vertical angle with tilt
	 */
	private TextView verticalAngle;

	/**
	 * Horizontal angle without tilt
	 */
	private TextView horizontalAngleNotCorrected;

	/**
	 * Vertical angle without tilt
	 */
	private TextView verticalAngleNotCorrected;

	/**
	 * Angle unit
	 */
	private TextView angleUnit;

	/**
	 * Current device connection Status
	 */
	private TextView status;

	/**
	 * Serial Number
	 */
	private TextView serialNumber;

	/**
	 * Software Version
	 */
	private TextView softwareVersion;

	/**
	 * Device ID
	 */
	private TextView deviceName;


	/**
	 * IP-Address
	 */
	private TextView ipAddress; //IP-Address

	/**
	 * Device operating hours
	 */
	private TextView htime;

	/**
	 * Software Name
	 */
	private TextView softwareName; //Software Name

	/**
	 * Device ID
	 */
	private TextView device;

	/**
	 * Equipment number
	 */
	private TextView equipment;

	/**
	 * Telescope position
	 */
	private TextView face;

	/**
	 * Motor polling
	 */
	private TextView mot_while;

	/**
	 * Temperature of HZ sensor
	 */
	private TextView hz_temp;

	/**
	 * Temperature of V sensor
	 */
	private TextView v_temp;

	/**
	 * Temperature of Bluetooth Chip
	 */
	private TextView ble_temp;

	/**
	 * Temperature of EDM sensor
	 */
	private TextView edm_temp;

	/**
	 * Battery voltage
	 */
	private TextView bat_v;

	/**
	 * Battery status
	 */
	private TextView bat_s;

	private TextView event;

	/**
	 * LED System Error
	 */
	private TextView led_se;

	/**
	 * LED Warning
	 */
	private TextView led_w;

	/**
	 * Horizontal angle with all corrections for which the tilt applies
	 */
	private TextView ihz;

	/**
	 * Longitudinal tilt to horizontal angle
	 */
	private TextView ilen;

	/**
	 * Transverse tilt to horizontal angle
	 */
	private TextView iCross;

	/**
	 * Mac Address
	 */
	private TextView mac_address;

	/**
	 * Firmwareversion of Wifi module
	 */
	private TextView wlan_module_version;

	/**
	 * WLan channel
	 */
	private TextView wlan_ch;

	/**
	 * Wlan Frequency
	 */
	private TextView wlan_freq;

	/**
	 * Current network name
	 */
	private TextView wlan_essid;

	/**
	 * Height index
	 */
	private TextView usr_vind;

	/**
	 * X offset
	 */
	private TextView user_camlasX;

	/**
	 * Y offset
	 */
	private TextView user_camlasY;

	/**
	 * Raw Device Response
	 */
	private TextView deviceResponse;

	/**
	 * Sensivity Mode (0 = normal mode, 1 = rugged mode)
	 */
	private TextView isensitive_mode;

	/**
	 * Defines image zoom
	 */
	private TextView zoom_one;
	private TextView zoom_two;
	private TextView zoom_four;
	private TextView zoom_eight;

	/**
	 * ModelName
	 */
	private TextView modelName;

	private AlertDialog commandDialog, customCommandDialog;
	private Boolean receiverRegistered;


	//Units used for the wifi measurement conversion as the wifi protocol only receives SI Units
	private static short defaultWifiDistanceUnit = MeasurementConverter.getDefaultWifiDistanceUnit();
	private static short defaultWifiAngleUnit = MeasurementConverter.getDefaultWifiAngleUnit();

	//private Button backButton;
	private SeekBar zoomBar;

	private Button sendCommandButton; //Open the available commands panel
	private Button distCommandButton; //Measure distance
	private Button startLiveImageButton; //Starts live image transfer
	private Button stopLiveImageButton; //Stops live image transfer
	private Button wifiUpButton; //Moves the 3D Disto device in the up direction
	private Button wifiDownButton; //Moves the 3D Disto device in the down direction
	private Button wifiRightButton; //Moves the 3D Disto device in the right direction
	private Button wifiLeftButton; //Moves the 3D Disto device in the left direction

	private RelativeLayout wifiUpButtonContainer; //Moves the 3D Disto device in the up direction
	private RelativeLayout wifiDownButtonContainer; //Moves the 3D Disto device in the down direction
	private RelativeLayout wifiRightButtonContainer; //Moves the 3D Disto device in the right direction
	private RelativeLayout wifiLeftButtonContainer; //Moves the 3D Disto device in the left direction


	// used to save the current SSID in which the device in Wifi Connection Mode: AP is connected to. this is important for later reconnection process to only do reconnection if the phone returns in the same SSID
	String currentSSIDforAPmode;

	boolean isDestroyed = false;


	boolean reconnectionIsRunning = false;

	private LiveImage currentLiveImage;

	private AlertDialog alertDialogConnect;
	private AlertDialog alertDialogDisconnect;

	// needed for handling onTouch events when in liveimage mode
	long onTouchTime = System.currentTimeMillis();

	boolean isConnectedDialog = false;

	//Extra thread to handle the Device Info
	HandlerThread setDeviceInfoThread;
	Handler setDeviceInfoHandler;


	Boolean setImageInProcess = Boolean.FALSE;

	private LiveImagePixelConverter liveImagePixelConverter;

	// listen to changes to the wifi adapter
	BroadcastReceiver RECEIVER_wifiChange = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					checkForReconnection();
				}
			});
		}
	};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final String METHODTAG ="onCreate";
		Log.v(CLASSTAG,METHODTAG+" called.");
		commandDialog = null;
		receiverRegistered = false;

		setContentView(R.layout.activity_rndis_information);
		previewImage = (ImageView) findViewById(R.id.picture_preview);
		levelImage = (ImageView) findViewById(R.id.level_view);

		//Init textfields
		dist = (TextView) findViewById(R.id.dist);
		distUnit = (TextView) findViewById(R.id.distunit);
		verticalAngle = (TextView) findViewById(R.id.v);
		horizontalAngle = (TextView) findViewById(R.id.hz);
		horizontalAngleNotCorrected = (TextView) findViewById(R.id.ni_hz);
		verticalAngleNotCorrected = (TextView) findViewById(R.id.ni_v);
		angleUnit = (TextView) findViewById(R.id.angleunit);

		ipAddress = (TextView) findViewById(R.id.ip);


		htime = (TextView) findViewById(R.id.htime);
		softwareName = (TextView) findViewById(R.id.swname);
		device = (TextView) findViewById(R.id.device);
		equipment = (TextView) findViewById(R.id.equipment);
		face = (TextView) findViewById(R.id.face);
		mot_while = (TextView) findViewById(R.id.motwhile);
		hz_temp = (TextView) findViewById(R.id.hztemp);
		v_temp = (TextView) findViewById(R.id.vtemp);
		ble_temp = (TextView) findViewById(R.id.bletemp);
		edm_temp = (TextView) findViewById(R.id.edmtemp);
		bat_v = (TextView) findViewById(R.id.batv);
		bat_s = (TextView) findViewById(R.id.bats);
		led_se = (TextView) findViewById(R.id.ledse);
		led_w = (TextView) findViewById(R.id.ledw);
		ihz = (TextView) findViewById(R.id.ihz);
		ilen = (TextView) findViewById(R.id.ilen);
		iCross = (TextView) findViewById(R.id.icross);
		mac_address = (TextView) findViewById(R.id.macaddress);
		wlan_module_version = (TextView) findViewById(R.id.wlanmoduleversion);
		wlan_ch = (TextView) findViewById(R.id.wlanchannel);
		wlan_freq = (TextView) findViewById(R.id.wlanfreq);
		wlan_essid = (TextView) findViewById(R.id.wlanessid);
		usr_vind = (TextView) findViewById(R.id.usrvind);
		user_camlasX = (TextView) findViewById(R.id.usercamlasx);
		user_camlasY = (TextView) findViewById(R.id.usercamlasy);
		deviceResponse = (TextView) findViewById(R.id.deviceResponse);
		isensitive_mode = (TextView) findViewById(R.id.isensitivemode);

		event =  (TextView) findViewById(R.id.event);

		status = (TextView) findViewById(R.id.status);

		serialNumber = (TextView) findViewById(R.id.serialnumber);
		softwareVersion = (TextView) findViewById(R.id.sw_vers);

		deviceName = (TextView) findViewById(R.id.deviceName);

		zoomBar = (SeekBar) findViewById(R.id.zoomBar);
		zoom_one = (TextView) findViewById(R.id.zoom_one);
		zoom_two = (TextView) findViewById(R.id.zoom_two);
		zoom_four = (TextView) findViewById(R.id.zoom_four);
		zoom_eight = (TextView) findViewById(R.id.zoom_eight);
		sendCommandButton = (Button) findViewById(R.id.send_command_button);
		distCommandButton = (Button) findViewById(R.id.dist_command_button);
		startLiveImageButton = (Button) findViewById(R.id.start_live_image_button);
		stopLiveImageButton = (Button) findViewById(R.id.stop_live_image_button);
		wifiUpButton = (Button) findViewById(R.id.up_button);
		wifiRightButton = (Button) findViewById(R.id.right_button);
		wifiDownButton = (Button) findViewById(R.id.down_button);
		wifiLeftButton = (Button) findViewById(R.id.left_button);
		wifiUpButtonContainer = (RelativeLayout) findViewById(R.id.up_button_container);
		wifiRightButtonContainer = (RelativeLayout) findViewById(R.id.right_button_container);
		wifiDownButtonContainer = (RelativeLayout) findViewById(R.id.down_button_container);
		wifiLeftButtonContainer = (RelativeLayout) findViewById(R.id.left_button_container);

		modelName = (TextView) findViewById(R.id.modeltv);

		SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBarChangeListener();

		if (zoomBar != null) {
			zoomBar.setOnSeekBarChangeListener(seekBarListener);
			zoomBar.setVisibility(View.INVISIBLE);
		}
		View.OnClickListener buttonListener = new ButtonListener();

		if (sendCommandButton != null) {
			sendCommandButton.setOnClickListener(buttonListener);
		}

		if (distCommandButton != null) {
			distCommandButton.setOnClickListener(buttonListener);
		}

		if (startLiveImageButton != null) {
			startLiveImageButton.setOnClickListener(buttonListener);
			startLiveImageButton.setVisibility(View.INVISIBLE);
		}

		if (stopLiveImageButton != null) {
			stopLiveImageButton.setOnClickListener(buttonListener);
			stopLiveImageButton.setVisibility(View.INVISIBLE);
		}

		//Init class responsible for motor movement events
		MotorControlsListener motorControlsListener = new MotorControlsListener();

		if (wifiUpButton != null) {
			wifiUpButton.setOnTouchListener(motorControlsListener);
			wifiUpButton.setVisibility(View.INVISIBLE);
		}

		if (wifiRightButton != null) {
			wifiRightButton.setOnTouchListener(motorControlsListener);
			wifiRightButton.setVisibility(View.INVISIBLE);
		}

		if (wifiDownButton != null) {
			wifiDownButton.setOnTouchListener(motorControlsListener);
			wifiDownButton.setVisibility(View.INVISIBLE);
		}

		if (wifiLeftButton != null) {
			wifiLeftButton.setOnTouchListener(motorControlsListener);
			wifiLeftButton.setVisibility(View.INVISIBLE);
		}
		if (wifiUpButtonContainer != null) {
			wifiUpButtonContainer.setOnTouchListener(motorControlsListener);
			wifiUpButtonContainer.setVisibility(View.INVISIBLE);
		}

		if (wifiRightButtonContainer != null) {
			wifiRightButtonContainer.setOnTouchListener(motorControlsListener);
			wifiRightButtonContainer.setVisibility(View.INVISIBLE);
		}

		if (wifiDownButtonContainer != null) {
			wifiDownButtonContainer.setOnTouchListener(motorControlsListener);
			wifiDownButtonContainer.setVisibility(View.INVISIBLE);
		}

		if (wifiLeftButtonContainer != null) {
			wifiLeftButtonContainer.setOnTouchListener(motorControlsListener);
			wifiLeftButtonContainer.setVisibility(View.INVISIBLE);
		}
		clear();

		//Connect /Disconnect dialog
		AlertDialog.Builder alertConnectedBuilder = new AlertDialog.Builder(RndisInformationActivity.this);
		alertConnectedBuilder.setMessage("connection established");
		alertConnectedBuilder.setPositiveButton("Ok", null);

		AlertDialog.Builder alertDisconnectedBuilder = new AlertDialog.Builder(RndisInformationActivity.this);
		alertDisconnectedBuilder.setMessage("lost connection to device");
		alertDisconnectedBuilder.setPositiveButton("Ok", null);

		//--Connect /Disconnect dialog
		alertDialogConnect 		= alertConnectedBuilder.create();
		alertDialogDisconnect 	= alertDisconnectedBuilder.create();

	}

	@Override
	protected void onResume() {
		super.onResume();

		final String METHODTAG ="onResume";
		Log.v(CLASSTAG,METHODTAG+" called.");

		if (!receiverRegistered) {
			receiverRegistered = true;
			registerReceiver(RECEIVER_wifiChange, new IntentFilter("android.net.wifi.STATE_CHANGE"));
		}

		if (currentDevice != null) {
			currentDevice.setConnectionListener(this);
			currentDevice.setReceiveDataListener(this);
			currentDevice.setErrorListener(this);

			status.setText(currentDevice.getConnectionState().toString());

			if (currentDevice.getConnectionState().equals(Device.ConnectionState.connected)) {

				Log.i(CLASSTAG,METHODTAG+" device is connected.");
				this.currentSSIDforAPmode = WifiHelper.getWifiName(getApplicationContext());

				if (currentDevice.getDeviceID().contains("3DD")) {

					if (startLiveImageButton != null) {
						startLiveImageButton.setVisibility(View.VISIBLE);
					}
					if (stopLiveImageButton != null) {
						stopLiveImageButton.setVisibility(View.VISIBLE);
					}

					if (wifiUpButton != null) {
						wifiUpButton.setVisibility(View.VISIBLE);
					}

					if (wifiRightButton != null) {
						wifiRightButton.setVisibility(View.VISIBLE);
					}

					if (wifiDownButton != null) {
						wifiDownButton.setVisibility(View.VISIBLE);
					}

					if (wifiLeftButton != null) {
						wifiLeftButton.setVisibility(View.VISIBLE);
					}

					if (wifiUpButtonContainer != null) {
						wifiUpButtonContainer.setVisibility(View.VISIBLE);
					}

					if (wifiRightButtonContainer != null) {
						wifiRightButtonContainer.setVisibility(View.VISIBLE);
					}

					if (wifiDownButtonContainer != null) {
						wifiDownButtonContainer.setVisibility(View.VISIBLE);
					}

					if (wifiLeftButtonContainer != null) {
						wifiLeftButtonContainer.setVisibility(View.VISIBLE);
					}

					if (zoomBar != null) {
						zoomBar.setVisibility(View.VISIBLE);
					}

					if (zoomBar != null) {
						zoomBar.setVisibility(View.VISIBLE);
					}
					if (zoom_one != null) {
						zoom_one.setVisibility(View.VISIBLE);
					}
					if (zoom_two != null) {
						zoom_two.setVisibility(View.VISIBLE);
					}
					if (zoom_four != null) {
						zoom_four.setVisibility(View.VISIBLE);
					}
					if (zoom_eight != null) {
						zoom_eight.setVisibility(View.VISIBLE);
					}

				} else {
					if (zoom_one != null) {
						zoom_one.setVisibility(View.INVISIBLE);
					}
					if (zoom_two != null) {
						zoom_two.setVisibility(View.INVISIBLE);
					}
					if (zoom_four != null) {
						zoom_four.setVisibility(View.INVISIBLE);
					}
					if (zoom_eight != null) {
						zoom_eight.setVisibility(View.INVISIBLE);
					}
				}
			}

			if (deviceName != null) {
				deviceName.setText(currentDevice.getDeviceID());
			}

			modelName.setText(currentDevice.getModel());

		}

		if (reconnectionHelper != null) {
			reconnectionHelper.setReconnectListener(this);
			reconnectionHelper.setErrorListener(this);
		}

		if (reconnectionIsRunning) {
			status.setText(R.string.reconnecting);
		}

		/*// check if wifi adapter is enabled
		WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
		// if wifi is off do nothing
		if (!wifiManager.isWifiEnabled()) {
			showAlert("Please turn on Wifi.");
		} else {*/
		checkForReconnection();
		/*}
*/
		//Ask for device Infomration (Serial number & software version)
		getDeviceInfo();


	}

	@Override
	protected void onStop() {
		super.onStop();

		final String METHODTAG ="onResume";
		Log.v(CLASSTAG,METHODTAG+" called.");

		if(previewImage != null){
			previewImage.setImageDrawable(null);
		}

		if (receiverRegistered) {
			unregisterReceiver(RECEIVER_wifiChange);
			receiverRegistered = false;
		}

		if (commandDialog != null) {
			commandDialog.dismiss();
		}

		if (customCommandDialog != null) {
			customCommandDialog.dismiss();
		}

		if(currentDevice.getDeviceType().equals(Types.DeviceType.Disto3D)) {
			stopLiveImage();
		}

	}

	/**
	 * Remove all the listeners associated with the currentDevice and the reconection procedure
	 */
	@Override
	protected void onDestroy() {

		super.onDestroy();

		final String METHODTAG ="onResume";
		Log.v(CLASSTAG,METHODTAG+" called.");

		isDestroyed = true;

		if (currentDevice != null) {
			currentDevice.setConnectionListener(null);
			currentDevice.setErrorListener(null);
			currentDevice.setReceiveDataListener(null);
		}
		if (reconnectionHelper != null) {
			reconnectionHelper.setErrorListener(null);
			reconnectionHelper.setReconnectListener(null);
			reconnectionHelper.stopReconnecting();
			reconnectionHelper = null;

			reconnectionIsRunning = false;
		}

		if (setDeviceInfoThread != null){
			setDeviceInfoThread.interrupt();
			setDeviceInfoThread = null;
			setDeviceInfoHandler = null;
		}
	}


	/**
	 * Gets called as soon the connection state of a devices changes.
	 * Show dialog if device gets connected or disconnected.
	 * Change status text.
	 * @param device
	 * @param state
	 */
	@Override
	public void onConnectionStateChanged(Device device, final Device.ConnectionState state) {

		final String METHODTAG = ".onConnectionStateChanged";
		Log.d(CLASSTAG, METHODTAG+": " + device.getDeviceID() + ", state: " + state);

		try {
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					status.setText(state.toString());
				}
			});

			if (state == Device.ConnectionState.connected) {
				this.currentSSIDforAPmode = WifiHelper.getWifiName(getApplicationContext());
				Log.d(CLASSTAG,  METHODTAG+": currentSSIDforAPmode: " + this.currentSSIDforAPmode);

				showConnectedDisconnectedDialog(true);
				//showAlert("Connection established");
			} else if (state == Device.ConnectionState.disconnected) {
				showConnectedDisconnectedDialog(false);
				//showAlert("Lost connection to device");
				checkForReconnection();
			}

		} catch (Exception e) {
			Log.e(CLASSTAG, METHODTAG+": Error in the UI", e);
		}
	}

	/**
	 * Presents a dialog to user after the connection states changed
	 * @param connected
	 * connected = true -> connected
	 * connected = false -> disconnected
	 */
	synchronized void showConnectedDisconnectedDialog(final boolean connected) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (connected) {
					alertDialogConnect.show();
					if(alertDialogDisconnect.isShowing()) {
						alertDialogDisconnect.dismiss();
					}
				} else {
					alertDialogDisconnect.show();
					if(alertDialogDisconnect.isShowing()) {
						alertDialogConnect.dismiss();
					}
				}
			}
		});
	}

	/**
	 * Method gets called after data were received
	 * Extract which information are send by the leica devices to properly update the corresponding ui element (E.g. if a distance measurement is transferred update textfield which corresponds to the distance )
	 * Switch case cover all measurements which can be transferred.
	 * @param receivedData
	 */
	@Override
	public void onAsyncDataReceived(final ReceivedData receivedData) {

		final String METHODTAG = "onAsyncDataReceived";

		if (receivedData != null) {
			try {
				this.runOnUiThread(new Runnable() {
					@Override
					public void run() {


							try {
								ReceivedWifiDataPacket receivedWifiDataPacket = (ReceivedWifiDataPacket) receivedData.dataPacket;
								if(receivedWifiDataPacket != null) {

									String id = receivedWifiDataPacket.dataId;

									switch (id) {

										case Defines.ID_EVPOS: {
											///////////////POSITION MOTOR EVENT////////////////////////////////
											String dataString = receivedWifiDataPacket.getEvPosMotor();
											event.setText("EvPos: " + dataString);

											//Log.d(CLASSTAG, METHODTAG + ": EvPos: Motor: " + dataString);


											///////////////POSITION MOTOR STOPPED RESULT////////////////////////////////
											dataString = receivedWifiDataPacket.getEvPosMotorResult();
											event.setText(event.getText() + dataString);
											//Log.d(CLASSTAG, METHODTAG + ": EvPos: MotorStoppedResult: " + dataString);

//										// check both motors if moving
//										checkMotorsIfMoving();

										}
										break;
										case Defines.ID_EVKEY: {
											///////////////KEY EVENT - CODE////////////////////////////////
											int dataInt = receivedWifiDataPacket.getEvKeyKey();
											event.setText("EvKey: " + String.valueOf(dataInt));

											Log.d(CLASSTAG, METHODTAG + ": EvKey: Key: " + dataInt);

											///////////////KEY EVENT - EVENT////////////////////////////////
											String dataString = receivedWifiDataPacket.getEvKeyEvent();
											event.setText(event.getText() + dataString);
											Log.d(CLASSTAG, METHODTAG + ": EvKey:  Event: " + dataString);

										}
										break;
										case Defines.ID_EVLINE: {
											///////////////LINE EVENT////////////////////////////////
											String dataString = receivedWifiDataPacket.getEvLineLine();
											event.setText("EvLine: " + dataString);
											Log.d(CLASSTAG, METHODTAG + ": EvLine" + dataString);
										}
										break;
										case Defines.ID_EVBAT: {
											/////////////////BATTERY EVENT///////////////////////////////////////
											String dataString = receivedWifiDataPacket.getEvLineBattery();
											event.setText("EvBat: " + dataString);
											Log.d(CLASSTAG, METHODTAG + ": EvBat" + dataString);
										}
										break;
										case Defines.ID_EVLEV: {
											////////////////LEVEL STATE EVENT///////////////////////////////////
											int dataInt = receivedWifiDataPacket.getEvLevel();
											setIstateImage(dataInt);
											event.setText("EvLev: " + String.valueOf(dataInt));
											Log.d(CLASSTAG, METHODTAG + ": IstateImage: " + dataInt);
										}
										break;
										case Defines.ID_EVMSG: {
											///////////////MESSAGE EVENT - SETCLEARBIT////////////////////////////////
											String dataString = receivedWifiDataPacket.getEvMsgAction();
											event.setText("EvMsg: " + dataString);
											Log.d(CLASSTAG, METHODTAG + ": EvMsg: Action: " + dataString);

											///////////////MESSAGE EVENT - FAULTWARNING////////////////////////////////
											dataString = receivedWifiDataPacket.getEvMsgMessage();
											event.setText(event.getText() + dataString);
											Log.d(CLASSTAG, METHODTAG + ": EvMsg: Message:  " + dataString);

											///////////////MESSAGE EVENT - STATE////////////////////////////////
											int dataInt = receivedWifiDataPacket.getEvMsgBit();
											event.setText(event.getText() + String.valueOf(dataInt));
											Log.d(CLASSTAG, METHODTAG + ": EvMsg: Bit:  " + id + ", value: " + dataInt);
										}
										break;
										case Defines.ID_EVCAL: {
											/////////////////CALIBRATION EVENT///////////////////////////////////////
											String dataString = receivedWifiDataPacket.getEvCalResult();
											event.setText("EvCal: " + dataString);
											Log.d(CLASSTAG, METHODTAG + ": EvCal: Message:  " + dataString);
										}
										break;
										case Defines.ID_EVMP: {
											setMeasurePolarData(receivedWifiDataPacket);
										}
										break;
										case Defines.ID_EVMPI: {
											setMeasurePolarData(receivedWifiDataPacket);
											/////////////////IMAGE/////////////////////////////////////////
											//Match for image in different resolutions

											Log.d(CLASSTAG, METHODTAG + ": called with id: " + id);
											Image image = receivedWifiDataPacket.getImage();
											setImage(image);

										}
										break;
										case Defines.ID_LIVEIMAGE: {
											Image image = receivedWifiDataPacket.getImage();
											setImage(image);
										}
										break;
									}
								}
							} catch (IllegalArgumentCheckedException e) {
								Log.e(CLASSTAG, METHODTAG, e);
							}

					}});


			} catch (Exception e) {
				Log.e(CLASSTAG,METHODTAG+": Error in the UI", e);
			}
		} else {
			Log.e(CLASSTAG, METHODTAG+": Error: receivedData object is null.  ",null);
		}
	}

	private void setMeasurePolarData(ReceivedWifiDataPacket receivedWifiDataPacket) throws IllegalArgumentCheckedException{

		final String METHODTAG = "setMeasurePolarData";

		/////////////////DISTANCE///////////////////////////////////////////////////////
		float dataFloat = receivedWifiDataPacket.getDistance();
		if (Float.isNaN(dataFloat) == false) {
			MeasuredValue distanceValue = new MeasuredValue(dataFloat, defaultWifiDistanceUnit);
			distanceValue.convertDistance();

			dist.setText(String.valueOf(distanceValue.getConvertedValueStrNoUnit()));
			distUnit.setText(distanceValue.getUnitStr());
		}
		Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + dataFloat);

		////////////HORIZONTAL ANGLE WITH TILT/////////////////////////////////////////
		dataFloat = receivedWifiDataPacket.getHorizontalAnglewithTilt_hz();
		if (Float.isNaN(dataFloat) == false) {
			MeasuredValue angleHorizontalValue = new MeasuredValue(dataFloat, defaultWifiAngleUnit);
			angleHorizontalValue = MeasurementConverter.convertAngle(angleHorizontalValue);
			horizontalAngle.setText(angleHorizontalValue.getConvertedValueStrNoUnit());
			angleUnit.setText(angleHorizontalValue.getUnitStr());
		}
		if (LeicaSdk.DEBUG){
			Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + dataFloat);}

		/////////////////VERTICAL ANGLE WITH TILT/////////////////////////////////////////
		dataFloat = receivedWifiDataPacket.getVerticalAngleWithTilt_v();
		if (Float.isNaN(dataFloat) == false) {
			MeasuredValue angleVerticalValue = new MeasuredValue(dataFloat, defaultWifiAngleUnit);
			angleVerticalValue = MeasurementConverter.convertAngle(angleVerticalValue);
			verticalAngle.setText(angleVerticalValue.getConvertedValueStrNoUnit());
			angleUnit.setText(angleVerticalValue.getUnitStr());
		}

		Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + dataFloat);

		/////////////////HORIZONTAL ANGLE WITHOUT TILT/////////////////////////////////////////
		dataFloat = receivedWifiDataPacket.getHorizontalAngleWithoutTilt_ni_hz();
		MeasuredValue angleHorizontalNotCorrectedValue = new MeasuredValue(dataFloat, defaultWifiAngleUnit);
		if (Float.isNaN(dataFloat) == false) {
			angleHorizontalNotCorrectedValue.convertAngle();
			horizontalAngleNotCorrected.setText(angleHorizontalNotCorrectedValue.getConvertedValueStrNoUnit());
			angleUnit.setText(angleHorizontalNotCorrectedValue.getUnitStr());
		}

			Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + dataFloat);

		/////////////////VERTICAL ANGLE WITHOUT TILT/////////////////////////////////////////
		dataFloat = receivedWifiDataPacket.getVerticalAngleWithoutTilt_ni_v();
		if (Float.isNaN(dataFloat) == false) {
			MeasuredValue angleVerticalNotCorrectedValue = new MeasuredValue(dataFloat, defaultWifiAngleUnit);
			angleVerticalNotCorrectedValue.convertAngle();
			verticalAngleNotCorrected.setText(angleVerticalNotCorrectedValue.getConvertedValueStrNoUnit());
			angleUnit.setText(angleVerticalNotCorrectedValue.getUnitStr());
		}
	}

	private void setIstateImage(int data) {

		final String METHODTAG = "setIstateImage";
		Log.d(CLASSTAG, METHODTAG + ": called with value: " + data);
		levelImage.invalidate();

		if (data == 0 || data == 1 || data == 2) {
			levelImage.setImageResource(R.drawable.ic_level012);
		}
		if (data == 3 || data == 4) {
			levelImage.setImageResource(R.drawable.ic_level4);
		}
		if (data == 5 || data == 6) {
			levelImage.setImageResource(R.drawable.ic_level356);
		}

	}

	/**
	 *
	 * @param imageData
	 */
	synchronized private void setImage(final byte[] imageData) {
		final String METHODTAG = ".setImage";

		synchronized (setImageInProcess) {

			if (setImageInProcess == Boolean.TRUE) {
				//Log.d(CLASSTAG, METHODTAG+": set image is already in process");
				return;
			}
			setImageInProcess = Boolean.TRUE;

			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					try {
						Bitmap image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
						previewImage.setImageBitmap(image);

						// this runnable will be executed when the imageview has layouted the image. "Runnable provided to post() method will be executed after view measuring and layouting" - https://stackoverflow.com/questions/17606140/how-to-get-when-an-imageview-is-completely-loaded-in-android
						previewImage.postDelayed(new Runnable() {
							@Override
							public void run() {
								setImageInProcess = Boolean.FALSE;

							}
						}, 50);    // without delaying the release of the setImageInProcess, the set image "hangs" more often
					} catch (Exception e) {
						Log.e(CLASSTAG, METHODTAG+": Unknown error setting the image. error. Please verify your source code. ", e);
					}
				}
			});
		}
	}

	/**
	 *
	 * @param image
	 */
	private void setImage(Image image){

		final String METHODTAG = ".setImage";

		try {


			// Crosshair
			LiveImagePixelConverter.Point crosshair = null;

			try {
				crosshair = new LiveImagePixelConverter.Point(
						image.getxCoordinateCrosshair(),
						image.getyCoordinateCrosshair()
				);


				if (image instanceof LiveImage) {
					currentLiveImage = (LiveImage) image;

					if (liveImagePixelConverter == null) {
						// Image size
						Bitmap bitmap = BitmapFactory.decodeByteArray(currentLiveImage.getImageBytes(), 0, currentLiveImage.getImageBytes().length);

						liveImagePixelConverter = new LiveImagePixelConverter(
								Types.DeviceType.Disto3D,
								new LiveImagePixelConverter.Size(
										bitmap.getWidth(),
										bitmap.getHeight()
								)
						);
					}

					// Direction
					LiveImagePixelConverter.SensorDirection sensorDirection = new LiveImagePixelConverter.SensorDirection(
							currentLiveImage.getHorizontalAngleCorrected(),
							currentLiveImage.getVerticalAngleCorrected(),
							currentLiveImage.getHorizontalAngleNotCorrected(),
							currentLiveImage.getVerticalAngleNotCorrected(),
							0
					);

					// Orientation
					LiveImagePixelConverter.VerticalAxisFace orientation;
					if (currentLiveImage.getOrientation() == 1) {
						orientation = LiveImagePixelConverter.VerticalAxisFace.One;
					} else {
						orientation = LiveImagePixelConverter.VerticalAxisFace.Two;
					}
					// zoomFactor
					int zoomFactor = Defines.ID_ZOOM_WIDE;

					switch (zoomBar.getProgress()) {
						case 0:
							zoomFactor = Defines.ID_ZOOM_WIDE;
							break;
						case 1:
							zoomFactor = Defines.ID_ZOOM_NORMAL;
							break;
						case 2:
							zoomFactor = Defines.ID_ZOOM_TELE;
							break;
						case 3:
							zoomFactor = Defines.ID_ZOOM_SUPER;
							break;
					}

					liveImagePixelConverter.UpdateValues(
							sensorDirection,
							orientation,
							zoomFactor,
							crosshair
					);
				}


				byte[] data = image.getImageBytes();
				if (data != null) {
					setImage(data);
					if (crosshair._X > 0 && crosshair._Y > 0) {
						Paint paint = new Paint();
						paint.setAntiAlias(true);
						paint.setColor(Color.RED);

						Bitmap bitmap = ((BitmapDrawable) previewImage.getDrawable()).getBitmap();
						Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
						Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
						Canvas canvas = new Canvas(mutableBitmap);
						canvas.drawLine((float) crosshair._X - 25, (float) crosshair._Y, (float) crosshair._X + 25, (float) crosshair._Y, paint);
						canvas.drawLine((float) crosshair._X, (float) crosshair._Y - 25, (float) crosshair._X, (float) crosshair._Y + 25, paint);

						previewImage.setImageBitmap(mutableBitmap);

					}


						//Log.v(CLASSTAG, METHODTAG + ": IMAGE - called with id: " + id + "data: " + data);
				} else {
					Log.d(CLASSTAG, METHODTAG + ": IMAGE - not available.");
				}
			} catch (WrongDataException e) {
				Log.e(CLASSTAG, METHODTAG + ": Error displaying the Image. Wrong Data Received", e);
			} catch (IllegalArgumentCheckedException e) {
				Log.e(CLASSTAG, METHODTAG + ": Error displaying the Image. Wrong Argument Received ", e);
			}
		}catch(Exception e){
			Log.e(CLASSTAG, METHODTAG + ": Error displaying the Image. ", e);
		}

	}

	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//Data Extraction
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Sending a command will return a Response object.
	 * Which type of response object is dependend of the sended command.
	 * @param response response object holding the data sent by the device
	 */
	public void readDataFromResponseObject(final Response response) {

		final String METHODTAG = ".readDataFromResponseObject";
		Log.v(CLASSTAG, METHODTAG +" called");

		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (response.getError() != null) {

					Log.e(CLASSTAG, METHODTAG+": response error: " + response.getError().getErrorMessage());

					return;
				}

				if (response instanceof ResponseWifiMeasurements) {
					extractDataFromWifiResponseObject((ResponseWifiMeasurements) response);
				} else if (response instanceof ResponseImage) {
					extractDataFromImageResponse((ResponseImage) response);
				} else if (response instanceof ResponseDeviceInfo) {
					extractDataFromDeviceInfoResponse((ResponseDeviceInfo) response);
				} else if (response instanceof ResponseMotorStatus) {
					// ...
				} else if (response instanceof ResponseTemperature) {
					extractDataFromTemperatureResponse((ResponseTemperature) response);
				} else if (response instanceof ResponseFace) {
					extractDataFromFaceResponse((ResponseFace) response);
				} else if (response instanceof ResponseBatteryStatus) {
					extractDataFromBatteryResponse((ResponseBatteryStatus) response);
				} else if (response instanceof ResponsePlain) {
					extractDataFromPlainResponse((ResponsePlain) response);
				}
			}
		});

	}


	/**
	 * The ResponseTemperature class holds all temperature measurements of the sensors
	 * @param response response holding the device temperature data
	 */
	public  void extractDataFromTemperatureResponse(ResponseTemperature response){

		final String METHODTAG = ".extractDataFromTemperatureResponse";
		Log.v(CLASSTAG, METHODTAG +" called");

		if(Defines.defaultFloatValue < response.getTemperatureDistanceMeasurementSensor_Edm()){
			edm_temp.setText(String.valueOf(response.getTemperatureDistanceMeasurementSensor_Edm()));
		}
		if(Defines.defaultFloatValue < response.getTemperatureHorizontalAngleSensor_Hz()){
			hz_temp.setText(String.valueOf(response.getTemperatureHorizontalAngleSensor_Hz()));
		}
		if(Defines.defaultFloatValue < response.getTemperatureVerticalAngleSensor_V()){
			v_temp.setText(String.valueOf(response.getTemperatureVerticalAngleSensor_V()));
		}
		if(Defines.defaultFloatValue < response.getTemperatureBLESensor()){
			ble_temp.setText(String.valueOf(response.getTemperatureBLESensor()));
		}
	}

	/**
	 * The ResponseBatteryStatus class holds information about the battery
	 * @param response response holding the device battery data
	 */
	public void extractDataFromBatteryResponse(ResponseBatteryStatus response){

		final String METHODTAG = ".extractDataFromBatteryResponse";
		Log.v(CLASSTAG, METHODTAG +" called");

		bat_v.setText(String.valueOf(response.getBatteryVoltage()));
		bat_s.setText(String.valueOf(response.getBatteryStatus()));
	}


	/**
	 * The ResponseFace class holds the face for 3D device
	 * @param response response holding the device face data
	 */
	public void extractDataFromFaceResponse(ResponseFace response){

		final String METHODTAG = ".extractDataFromFaceResponse";
		Log.v(CLASSTAG, METHODTAG +" called");

		face.setText(String.valueOf(response.getFace()));
	}

	/**
	 * The DeviceInfo class holds all information about the currently connected device
	 * @param response response holding the DeviceInfo data
	 * @see ResponseDeviceInfo
	 */
	public void extractDataFromDeviceInfoResponse(ResponseDeviceInfo response){

		final String METHODTAG = ".extractDataFromDeviceInfoResponse";
		Log.v(CLASSTAG, METHODTAG +" called");

		if(!response.getIP().equals(Defines.defaultStringValue)){
			ipAddress.setText(response.getIP());
		}

		if(!response.getSerialNumber().equals(Defines.defaultStringValue)){
			serialNumber.setText(response.getSerialNumber());
		}

		if(!response.getSoftwareName().equals(Defines.defaultStringValue)){
			softwareName.setText(response.getSoftwareName());
		}

		if(!response.getSoftwareVersion().equals(Defines.defaultStringValue)){
			softwareVersion.setText(String.valueOf(response.getSoftwareVersion()));
		}

		if(response.getDeviceType() != Defines.defaultIntValue ){
			device.setText(String.valueOf(response.getDeviceType()));
		}

		if(!response.getMacAddress().equals(Defines.defaultStringValue)){
			mac_address.setText(response.getMacAddress());
		}

		if(!response.getWifiModuleVersion().equals(Defines.defaultStringValue)){
			wlan_module_version.setText(response.getWifiModuleVersion());
		}

		if(!response.getWifiESSID().equals(Defines.defaultStringValue)){
			wlan_essid.setText(response.getWifiESSID());
		}

		if(response.getWifiChannelNumber() != Defines.defaultIntValue ){
			wlan_ch.setText(response.getWifiChannelNumber());
		}

		if(response.getWifiFrequency() != Defines.defaultIntValue ){
			wlan_freq.setText(response.getWifiFrequency());
		}

		if(response.getWifiFrequency() != Defines.defaultIntValue ){
			wlan_freq.setText(String.valueOf(response.getWifiFrequency()));
		}

		if(Defines.defaultFloatValue < response.getUserVind()){
			usr_vind.setText(String.valueOf(response.getUserVind()));
		}

		if(Defines.defaultFloatValue < response.getUserCamLasX()){
			user_camlasX.setText(String.valueOf(response.getUserCamLasX()));
		}

		if(Defines.defaultFloatValue < response.getUserCamLasY()){
			user_camlasY.setText(String.valueOf(response.getUserCamLasY()));
		}

		if(Defines.defaultFloatValue < response.getSensitiveMode()){
			isensitive_mode.setText(String.valueOf(response.getSensitiveMode()));
		}
	}

	/**
	 * The ResponseImage class contains all information about the image.
	 * Please note that Live image is handled differently.
	 * @param response response holding image data
	 */
	public void extractDataFromImageResponse(ResponseImage response) {
		final String METHODTAG = ".extractDataFromImageResponse";
		Log.v(CLASSTAG, METHODTAG +" called");

		try{
			this.setImage(response.getImageBytes());
		}catch(IllegalArgumentCheckedException e){
			Log.e(CLASSTAG, METHODTAG,e);
		}

	}

	/**
	 * The ResponseWifiMeasurementExtract contains all measured data
	 * @param response response holding measurement data - wifi Mode
	 */
	public void extractDataFromWifiResponseObject(ResponseWifiMeasurements response){

		final String METHODTAG = "extractDataFromWifiResponseObject";
		Log.v(CLASSTAG, METHODTAG +" called");

		MeasuredValue data = response.getDistanceValue();

		//Distance Measurement
		if(data != null && data.getConvertedValue() != Defines.defaultFloatValue && !data.getUnitStr().equals(Defines.defaultStringValue)){
			dist.setText(data.getConvertedValueStrNoUnit());
			distUnit.setText(data.getUnitStr());
		}
		//Horizontal Angle Measurement
		data = response.getHorizontalAngleWithTilt_HZ();
		if(data != null && data.getConvertedValue() !=  Defines.defaultFloatValue && !data.getUnitStr().equals(Defines.defaultStringValue)){
			horizontalAngle.setText(data.getConvertedValueStrNoUnit());
			angleUnit.setText(data.getUnitStr());
		}

		//Vertical Angle Measurement
		data =  response.getVerticalAngleWithTilt_V();
		if(data != null && data.getConvertedValue() != Defines.defaultFloatValue && !data.getUnitStr().equals(Defines.defaultStringValue)){
			verticalAngle.setText(data.getConvertedValueStrNoUnit());
			angleUnit.setText(data.getUnitStr());
		}

		//Horizontal angle without Tilt correction
		data =  response.getHorizontalAngleWithouthTilt_NI_HZ();
		if(data != null && data.getConvertedValue() != Defines.defaultFloatValue){
			horizontalAngleNotCorrected.setText(data.getConvertedValueStrNoUnit());
		}

		//Vertical angle without Tilt correction
		data = response.getVerticalAngleWithoutTilt_NI_V();
		if(data != null && data.getConvertedValue() != Defines.defaultFloatValue){
			verticalAngleNotCorrected.setText(data.getConvertedValueStrNoUnit());
		}

		//ICross Value: Transverse tilt to horizontal angle
		if(response.getICross() != Defines.defaultFloatValue){
			iCross.setText(String.valueOf(response.getICross()));
		}

		//Ihz Value: Horizontal angle with all corrections for which the tilt applies
		if(response.getIhz() != Defines.defaultFloatValue){
			ihz.setText(String.valueOf(response.getIhz()));
		}

		//ILen Value: Longitudinal tilt to horizontal angle
		if(response.getILen()  != Defines.defaultFloatValue ){
			ilen.setText(String.valueOf(response.getILen()));
		}

		//IState value:
		/*
		 * Level status [0..5]
		 0  tilt plane "probably" up to date (depends on next value 1 or 3)
		 1  Tilt plane was recently redetermined and is identical
		 2  Tilt plane (old) cannot currently be measured (system overload)
		 3  Tilt plane was recently redetermined (value has altered)
		 4  Tilt plane has not yet been determined (after power up, level enabled)
		 5  Tilt measurement deactivated
		 6  Tilt plane "unstable" (possible reason vibrations, oscillations, …)
		 */
		int iState = response.getIState();
		setIstateImage(iState);

	}
	public void extractDataFromPlainResponse(ResponsePlain response) {

		final String METHODTAG = "extractDataFromPlainResponse";
		Log.v(CLASSTAG, METHODTAG +" called");

		deviceResponse.setText(response.getReceivedDataString());
	}

	/**
	 * Show alert message in the UI
	 * @param message Message to be shown to the user
	 */
	public void showAlert(final String message) {

		final String METHODTAG = "extractDataFromWifiResponseObject";
		Log.v(CLASSTAG, METHODTAG +" called");

		if (isDestroyed) {
			return;
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RndisInformationActivity.this);
				alertBuilder.setMessage(message);
				alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (message.equals("Lost connection to device")) {
							isConnectedDialog = false;
						}
					}
				});

				if (message.equals("Lost connection to device")) {
					if (!isConnectedDialog) {
						isConnectedDialog = true;
						alertBuilder.create().show();
					}
				} else {
					alertBuilder.create().show();
				}

			}
		});
	}

	@Override
	public void onError(ErrorObject errorObject, Device device) {

		final String METHODTAG = ".onError";
		Log.e(CLASSTAG, METHODTAG+": "+errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode(),null);

		if (errorObject.getErrorCode() == ErrorDefinitions.EVENTCHANNEL_SOCKET_NOT_CONNECTING_CODE) {
			return;
		}
		if (errorObject.getErrorCode() == ErrorDefinitions.RESPONSECHANNEL_SOCKET_NOT_CONNECTING_CODE) {
			return;
		}
		if (errorObject.getErrorCode() == ErrorDefinitions.RESPONSE_ERROR_RECEIVED_CODE) {

			Log.e(CLASSTAG, METHODTAG +": " + errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode(),null);
			return;

		}

		showAlert(errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode()); // + " "+ errorObject.getErrorDescription(errorObject.getErrorMessage()));
	}

	@Override
	public void onReconnect(Device device) {
		final String METHODTAG = ".onReconnect";
		Log.v(CLASSTAG, METHODTAG+": called");

		reconnectionIsRunning = false;

		currentDevice = device;
		currentDevice.setErrorListener(this);
		currentDevice.setConnectionListener(this);
		currentDevice.setReceiveDataListener(this);

		reconnectionHelper = new ReconnectionHelper(currentDevice, getApplicationContext());
		reconnectionHelper.setReconnectListener(this);
		reconnectionHelper.setErrorListener(this);

		onConnectionStateChanged(currentDevice, currentDevice.getConnectionState());
	}

	synchronized void checkForReconnection() {

		final String METHODTAG = ".checkForReconnection";
		Log.v(CLASSTAG, METHODTAG +": called");

		if (currentDevice == null) {
			return;
		}
		if (currentDevice.getConnectionState() == Device.ConnectionState.connected) {
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {

				if (currentDevice.getConnectionType().equals(Types.ConnectionType.rndis) ) {
					Log.d(CLASSTAG, METHODTAG +": "+ currentDevice.getConnectionType().toString());

					// wait a sec to let adapter work
					WaitAmoment waitAmoment = new WaitAmoment();
					waitAmoment.waitAmoment(1000);

					// get current wifiname the phone is connected to
					String ipAddress = currentDevice.getIP();
					Log.d(CLASSTAG, METHODTAG +": currentIPADDRESS: " + ipAddress);

					if (ipAddress == null) {
						Log.i(CLASSTAG, METHODTAG+": wifi is null");
					} /*else if (!ipAddress.equalsIgnoreCase(currentSSIDforAPmode)) {
						Log.i(CLASSTAG,  METHODTAG+": currentSSIDforAPmode is not the same");
					}*/ else {
						if (!reconnectionIsRunning) {
							reconnectionIsRunning = true;

							if (reconnectionHelper != null) {

								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										status.setText(R.string.reconnecting);
										showAlert("Try to automatically reconnect now.");
									}
								});

								reconnectionHelper.startReconnecting();
							}
						}
					}
				}

			}
		}).start();

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();

		final String METHODTAG = ".onLowMemory";
		Log.v(CLASSTAG, METHODTAG +" called");

		stopLiveImage();
	}


	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//Sending Commands
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Show a dialog in which a user can type in a text and send it as a command.
	 * The custom command method will not return a response object containing the data.
	 */
	private void showCustomCommandDialog() {
		final String METHODTAG = ".showCustomCommandDialog";
		Log.v(CLASSTAG, METHODTAG +" called");

		final EditText input = new EditText(this);
		AlertDialog.Builder customCommandDialogBuilder = new AlertDialog.Builder(this);
		customCommandDialogBuilder.setTitle(R.string.custom_command);
		customCommandDialogBuilder.setView(input);
		customCommandDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					Log.d(CLASSTAG, "Sending Custom Command");
					Response response = currentDevice.sendCustomCommand(input.getText().toString());
					response.waitForData();
					readDataFromResponseObject(response);

				} catch (DeviceException e) {
					Log.e(CLASSTAG, METHODTAG + ": Error sending custom command possible cause: Device Exception Error.", e);

				}catch (Exception e) {
					Log.e(CLASSTAG, METHODTAG + ": Error sending custom command possible cause: Device is off ", e);
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

	/**
	 * show a list of available commands in a dialog
	 */
	public void showCommandDialog() {

		final String METHODTAG = ".showCommandDialog";
		Log.v(CLASSTAG, METHODTAG +" called");

		AlertDialog.Builder comandDialogBuilder = new AlertDialog.Builder(this);
		comandDialogBuilder.setTitle("Select Command");
		comandDialogBuilder.setItems(currentDevice.getAvailableCommands(), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String command = currentDevice.getAvailableCommands()[which];

				if (command.equals(Types.Commands.Custom.name())) {
					showCustomCommandDialog();
				}
				else {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Response response = currentDevice.sendCommand(Types.Commands.valueOf(command));
								response.waitForData();

								readDataFromResponseObject(response);
							} catch (DeviceException e) {
								Log.e(CLASSTAG, METHODTAG+": send command error: ", e);
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

	/**
	 * Listener for the following button events:
	 * 1. Press on send command (Pop up list with all commands appears)
	 * 2. Measure Distance & Turn on the laser
	 * 3. Start live image transfer
	 * 4. Stop live image transfer
	 */
	private class ButtonListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {

			final String METHODTAG = ".ButtonListener.onClick";
			switch (view.getId()) {
				case R.id.send_command_button:
					showCommandDialog();
					break;
				case R.id.dist_command_button:
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								//Measure polar
								final ResponseWifiMeasurements response = (ResponseWifiMeasurements) currentDevice.sendCommand(Types.Commands.MeasurePolar);
								response.waitForData();

								if (response.getError() != null){
									showAlert(response.getError().getErrorMessage());
								}
								else {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											extractDataFromWifiResponseObject(response);
										}
									});
								}
								//Turn laser on
								ResponsePlain responsePlain = (ResponsePlain) currentDevice.sendCommand(Types.Commands.LaserOn);
								responsePlain.waitForData();
								if (responsePlain.getReceivedDataString() != null){
									Log.d(CLASSTAG, METHODTAG+": laser response: " + responsePlain.getReceivedDataString());
								}

							} catch (DeviceException e) {
								Log.e(CLASSTAG, METHODTAG,e);
								showAlert("Error Sending Command. "+ e.getMessage());
							}
						}
					}).start();
					break;
				case R.id.start_live_image_button:
					startLiveImage();
					break;
				case R.id.stop_live_image_button:
					stopLiveImage();
					break;
				default:
					Log.d(CLASSTAG, METHODTAG + ": "+ view.getId() + " not implemented");
			}
		}
	}

	/**
	 * Class is relevant for camera movements.
	 */
	private class CameraMovementListener implements View.OnTouchListener {

		/**
		 * ClassName
		 */
		private final String CLASSTAG = CameraMovementListener.class.getSimpleName();

		/**
		 *
		 */
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {

				final String METHODTAG = ".CameraMovementListener.onTouch";
				Log.v(CLASSTAG, METHODTAG + "X: " + motionEvent.getX() + ", Y: " + motionEvent.getY());

				if (currentDevice instanceof Disto3DDevice) {

					LiveImagePixelConverter.Point touched = new LiveImagePixelConverter.Point(
							motionEvent.getX() * 640 / previewImage.getWidth(),
							motionEvent.getY() * 480 / previewImage.getHeight()
					);
					LiveImagePixelConverter.PolarCoordinates polar = liveImagePixelConverter.ToPolarCoordinates(touched);


					try {



						currentDevice.sendCommandMotorPositionAbsolute(polar._Hz, polar._V, false);

					} catch (DeviceException e) {
						Log.e(CLASSTAG, METHODTAG, e);

					}

					return true;
				}

				return false;

		}

		/**
		 * Convert sensor image coordinates to sensor angles (NiHz, NiV).
		 * This method moves the camver to the given coordinate points.
		 */

	}

	/**
	 * Class is relevant for vertical and horizontal movements of the motor
	 */
	private class MotorControlsListener implements View.OnTouchListener {

		/**
		 * ClassName
		 */
		private final String CLASSTAG = MotorControlsListener.class.getSimpleName();


		/**
		 * Captures the touch event of one of the buttons (left, right, up or down) responsible for the camera movement
		 * @param view
		 * @param motionEvent
		 * @return
		 */
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {

			final String METHODTAG = ".onTouch";
			Log.v(CLASSTAG, METHODTAG + "X: " + motionEvent.getX() + ", Y: " + motionEvent.getY());

			// if its no app 3D device
			if (!(currentDevice instanceof Disto3DDevice)) {
				return false;
			}

			switch (motionEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					return handleDownEvent(view);
				case MotionEvent.ACTION_UP:
					return handleUpEvent(view);
				default:
					return false;
			}
		}

		/**
		 * handles movement of the camera if user presses an arrow button
		 * General sequence:
		 * 1. Send command that sets the velocity of the cammera rotation
		 * 2. Set movement horizontalAngle
		 * 		For Horizontal movement: Move camera around the own center either in a left rotation or right rotation
		 * 		Vertical movement: Move camera up or down
		 * 3. Set horizontalAngle: Either horizontalAngle movement or vertical movement of the camer
		 */
		private boolean handleDownEvent(View view) {

			String METHODTAG = "handleDownEvent" ;
			Log.v(CLASSTAG, METHODTAG + "Called");
			// sending commands "too" fast seems ok for 3DD

 			try {
				int viewId = view.getId();

				if (viewId == R.id.up_button || viewId == R.id.up_button_container) {

					currentDevice.sendCommandMoveMotorUp(50);
					// FOR TESTING
//					currentDevice.sendCommandMotorPositionAbsolute(1.0, 1.0, false);
//					currentDevice.sendCommandMotorPositionRelative(1.0, 1.0, false);

					disableUI(R.id.up_button);

				} else if (viewId == R.id.right_button || viewId == R.id.right_button_container) {

					currentDevice.sendCommandMoveMotorRight(50);
					disableUI(R.id.right_button);

				} else if (viewId == R.id.down_button || viewId == R.id.down_button_container) {

					currentDevice.sendCommandMoveMotorDown(50);
					disableUI(R.id.down_button);

				} else if (viewId == R.id.left_button || viewId == R.id.left_button_container) {

					currentDevice.sendCommandMoveMotorLeft(50);
					disableUI(R.id.left_button);

				} else {
					return false;
				}
			} catch (DeviceException e) {
				Log.e(CLASSTAG, METHODTAG, e);
			}
			return true;
		}

		/**
		 * Handles if the user releases button.
		 * Release of button stops camera movement.
		 * @param view
		 * @return
		 */
		private boolean handleUpEvent(View view) {

			// FOR TESTING
//			if (1 == 1){
//				return false;
//			}

			String METHODTAG = "handleUpEvent" ;
			Log.v(CLASSTAG, METHODTAG + "Called");

			int viewId = view.getId();
			try {
				if (viewId == R.id.up_button || viewId == R.id.up_button_container) {
					currentDevice.sendCommandPositionStopVertical();
					enableUI();

				} else if (viewId == R.id.right_button || viewId == R.id.right_button_container) {
					currentDevice.sendCommandPositionStopHorizontal();
					enableUI();

				} else if (viewId == R.id.down_button || viewId == R.id.down_button_container) {
					currentDevice.sendCommandPositionStopVertical();
					enableUI();

				} else if (viewId == R.id.left_button || viewId == R.id.left_button_container) {
					currentDevice.sendCommandPositionStopHorizontal();
					enableUI();

				} else {
					return false;
				}
			}catch (DeviceException e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	private void disableUI(int button) {

	}

	private void enableUI() {


	}

	/**
	 * SeekBarChangeListener allows the user to select a zoom level
	 * ImageZoomWide, ImagezoomNormal, ImageZoomtele, ImageZoomSuper
	 */
	private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

		/**
		 * ClassName
		 */
		private final String CLASSTAG = SeekBarChangeListener.class.getSimpleName();

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

			final String METHODTAG = ".onProgressChanged";

			Response response = null;
			try {
				Log.v(CLASSTAG, METHODTAG + "Progress: "+progress);
				switch (progress) {
					case 0:
						response = currentDevice.sendCommand(Types.Commands.ImageZoomWide);

						break;
					case 1:
						response = currentDevice.sendCommand(Types.Commands.ImageZoomNormal);
						break;
					case 2:
						response = currentDevice.sendCommand(Types.Commands.ImageZoomTele);
						break;
					case 3:
						response = currentDevice.sendCommand(Types.Commands.ImageZoomSuper);
						break;
					default:
						Log.d(CLASSTAG, METHODTAG + ": Invalid zoom level");
						break;
				}

				if (response != null){
					response.waitForData();
				}

			} catch (DeviceException e) {
				Log.e(CLASSTAG, METHODTAG + ": "+ e.getMessage(),e);
			}
		}

		/**
		 * Method needs to be implemented but has no functionality in this use case
		 */
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			return;
		}

		/**
		 * Method needs to be implemented but has no functionality in this use case
		 */
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			return;
		}
	}


	/**
	 * Start live image transfer.
	 */
	private void startLiveImage() {

		final String METHODTAG = ".startLiveImage";
		Log.v(CLASSTAG, METHODTAG +" called");

		// set zoom to specific level
		zoomBar.setProgress(0);
		try {
			Response response = currentDevice.sendCommand(Types.Commands.ImageZoomWide);
			response.waitForData();
		} catch (DeviceException e) {
			Log.e(CLASSTAG, METHODTAG, e);
		}

		try {
			currentDevice.connectLiveChannel(Device.LiveImageSpeed.FAST);
		} catch (DeviceException e) {
			Log.e(CLASSTAG,METHODTAG+"error caused by: ",e);
		}

		if(this.previewImage != null){
			this.previewImage.setOnTouchListener(new CameraMovementListener());
		}

	}

	/**
	 * Stop live image transfer.
	 */
	private void stopLiveImage() {
		final String METHODTAG = ".stopLiveImage";
		Log.v(CLASSTAG, METHODTAG +" called");


		try {
			currentDevice.disconnectLiveChannel();
		} catch (DeviceException e) {
			Log.e(CLASSTAG,METHODTAG+"error caused by: ",e);
		}

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(previewImage != null){
					previewImage.setImageDrawable(null);
					previewImage.setOnTouchListener(null);
				}
			}
		});

	}

	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//End of Live Image
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++



	/**
	 * Fetch device information.
	 * Sets serial number and software version.
	 */
	void getDeviceInfo() {

		final String METHODTAG = ".getDeviceInfo";
		Log.d(CLASSTAG, METHODTAG +" called");

		try {

			if(setDeviceInfoThread == null) {
				setDeviceInfoThread = new HandlerThread("setDeviceHandler", HandlerThread.MAX_PRIORITY);
				setDeviceInfoThread.start();
				setDeviceInfoHandler = new Handler(setDeviceInfoThread.getLooper());
			}

			//Get Serial Number
			setDeviceInfoHandler.post(new Runnable() {
				@Override
				public void run() {
					final Response response;
					try {
						response = currentDevice.sendCommand(Types.Commands.GetSerialNumber);
						response.waitForData();

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								readDataFromResponseObject(response);

							}
						});

					} catch (DeviceException e) {
						Log.e(CLASSTAG, METHODTAG+": send command error: ", e);
						showAlert("Error Sending Command. "+ e.getMessage());
					} catch (Exception e) {
						Log.e(CLASSTAG, METHODTAG+": send command error: ", e);
						showAlert("Error Sending Command. Turn On Live Image Again.");
					}
				}
			});

			//Get Software Version
			setDeviceInfoHandler.post(new Runnable() {
				@Override
				public void run() {
					final Response response;
					try {
						response = currentDevice.sendCommand(Types.Commands.GetSoftwareVersion);
						response.waitForData();

						runOnUiThread(new Runnable() {
							@Override
							public void run() {

								readDataFromResponseObject(response);
							}
						});

					} catch (DeviceException e) {
						Log.e(CLASSTAG, METHODTAG, e);
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setCurrentDevice(Device currentDevice, Context context) {

		final String METHODTAG = "setCurrentDevice";
		Log.d(CLASSTAG, METHODTAG +" called");

		RndisInformationActivity.currentDevice = currentDevice;
		reconnectionHelper = new ReconnectionHelper(currentDevice, context);
	}

	/**
	 * Clears all textfields
	 */
	private void clear() {

		final String METHODTAG = "clear";
		Log.d(CLASSTAG, METHODTAG +" called");

		dist.setText(R.string.default_value);
		distUnit.setText(R.string.default_value);
		verticalAngle.setText(R.string.default_value);
		angleUnit.setText("");
		horizontalAngle.setText(R.string.default_value);
		horizontalAngleNotCorrected.setText(R.string.default_value);
		verticalAngleNotCorrected.setText(R.string.default_value);
		ipAddress.setText(R.string.default_value);
		htime.setText(R.string.default_value);
		softwareName.setText(R.string.default_value);
		equipment.setText(R.string.default_value);
		face.setText(R.string.default_value);
		mot_while.setText(R.string.default_value);
		hz_temp.setText(R.string.default_value);
		v_temp.setText(R.string.default_value);
		ble_temp.setText(R.string.default_value);
		edm_temp.setText(R.string.default_value);
		bat_v.setText(R.string.default_value);
		bat_s.setText(R.string.default_value);
		event.setText(R.string.default_value);
		led_se.setText(R.string.default_value);
		led_w.setText(R.string.default_value);
		ihz.setText(R.string.default_value);
		ilen.setText(R.string.default_value);
		iCross.setText(R.string.default_value);
		mac_address.setText(R.string.default_value);
		wlan_module_version.setText(R.string.default_value);
		wlan_ch.setText(R.string.default_value);
		wlan_freq.setText(R.string.default_value);
		wlan_essid.setText(R.string.default_value);
		usr_vind.setText(R.string.default_value);
		user_camlasX.setText(R.string.default_value);
		user_camlasY.setText(R.string.default_value);
		isensitive_mode.setText(R.string.default_value);
	}
}