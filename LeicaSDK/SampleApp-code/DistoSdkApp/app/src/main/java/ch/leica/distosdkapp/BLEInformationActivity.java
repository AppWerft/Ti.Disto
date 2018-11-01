package ch.leica.distosdkapp;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ch.leica.sdk.Defines;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;

import ch.leica.sdk.ErrorHandling.DeviceException;

import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.ErrorHandling.PermissionException;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Listeners.ReceivedDataListener;
import ch.leica.sdk.Reconnection.ReconnectionHelper;
import ch.leica.sdk.Types;

import ch.leica.sdk.Utilities.WaitAmoment;
import ch.leica.sdk.commands.MeasuredValue;
import ch.leica.sdk.commands.MeasurementConverter;
import ch.leica.sdk.commands.ReceivedBleDataPacket;
import ch.leica.sdk.commands.ReceivedData;

import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.commands.response.ResponseBLEMeasurements;
import ch.leica.sdk.connection.ble.BleCharacteristic;



/**
 * UI to diplay bluetooth device information.
 * Excluding Yeti.
 */
public class BLEInformationActivity extends AppCompatActivity implements ReceivedDataListener, Device.ConnectionListener, ErrorListener, ReconnectionHelper.ReconnectListener {

    /**
     * ClassName
     */
    private final String CLASSTAG = BLEInformationActivity.class.getSimpleName();

    private Device currentDevice;
    private ReconnectionHelper reconnectionHelper;

    static int defaultDirectionAngleUnit = MeasurementConverter.getDefaultDirectionAngleUnit();

    //UI - Status of the connection
    TextView status;

    //Textfields present information to user
    TextView distance;
    TextView distanceUnit;

    TextView inclinationLabel;
    TextView inclinationUnitLabel;
    TextView inclination;
    TextView inclinationUnit;

    TextView directionLabel;
    TextView directionUnitLabel;
    TextView direction;
    TextView directionUnit;

    TextView modelName;
    TextView deviceName;

    //Buttons implementing functionality of leica device
    Button sendCommand; //
    Button dist; //Sends "measure distance"
    Button startTracking; //Sends "start tracking"
    Button stopTracking; //Sends "stop tracking"
    Button read; //Read bluetooth data
    Button clear; //Clear textfields on smartphone and on leica device

    AlertDialog commandDialog;
    AlertDialog customCommandDialog;
    AlertDialog lostConnectionAlert;
    boolean lostConnectionAlertIsShown = false;

    ButtonListener bl = new ButtonListener();

    boolean isDestroyed = false;
    boolean deviceIsInTrackingMode = false;
    boolean turnOnBluetoothDialogIsShown = false;

    //Measurement Values received from the device
    MeasuredValue distanceValue;
    MeasuredValue inclinationValue;
    MeasuredValue directionValue;

    Boolean receiverRegistered = false;
    boolean reconnectionIsRunning = false;

    private AlertDialog alertDialogConnect;
    private AlertDialog alertDialogDisconnect;

    private boolean hasDistanceMeasurement = false;



    // listen to changes to the bluetooth adapter
    BroadcastReceiver bluetoothAdapterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String METHODTAG =".bluetoothAdapterReceiver.receive()";
            Log.d(CLASSTAG, METHODTAG);
            checkForReconnection();
        }
    };



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

        setContentView(R.layout.activity_ble_information);

        //Initialize all UI Fields
        status = (TextView) findViewById(R.id.status);
        distance = (TextView) findViewById(R.id.distance);
        distanceUnit = (TextView) findViewById(R.id.distanceUnit);
        inclinationLabel = (TextView) findViewById(R.id.inclinationLabel);
        inclination = (TextView) findViewById(R.id.inclination);
        inclinationUnitLabel = (TextView) findViewById(R.id.inclinationUnitLabel);
        inclinationUnit = (TextView) findViewById(R.id.inclinationUnit);
        directionLabel = (TextView) findViewById(R.id.directionLabel);
        direction = (TextView) findViewById(R.id.direction);
        directionUnitLabel = (TextView) findViewById(R.id.directionUnitLabel);
        directionUnit = (TextView) findViewById(R.id.directionUnit);
        modelName = (TextView) findViewById(R.id.modelName);
        deviceName = (TextView) findViewById(R.id.deviceName);

        dist = (Button) findViewById(R.id.dist);
        dist.setOnClickListener(bl);
        sendCommand = (Button) findViewById(R.id.sendcommand);
        sendCommand.setOnClickListener(bl);
        startTracking = (Button) findViewById(R.id.startTracking);
        startTracking.setOnClickListener(bl);
        stopTracking = (Button) findViewById(R.id.stopTracking);
        stopTracking.setOnClickListener(bl);
        read = (Button) findViewById(R.id.read);
        read.setOnClickListener(bl);
        clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(bl);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(BLEInformationActivity.this);
        alertBuilder.setMessage(R.string.lostConnection);
        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                lostConnectionAlertIsShown = false;
            }
        });
        lostConnectionAlert = alertBuilder.create();
        lostConnectionAlert.setCancelable(false);


        AlertDialog.Builder alertConnectedBuilder = new android.app.AlertDialog.Builder(BLEInformationActivity.this);
        alertConnectedBuilder.setMessage("connection established");
        alertConnectedBuilder.setPositiveButton("Ok", null);

        AlertDialog.Builder alertDisconnectedBuilder = new android.app.AlertDialog.Builder(BLEInformationActivity.this);
        alertDisconnectedBuilder.setMessage("lost connection to device");
        alertDisconnectedBuilder.setPositiveButton("Ok", null);

        alertDialogConnect 		= alertConnectedBuilder.create();
        alertDialogDisconnect 	= alertDisconnectedBuilder.create();

        // set values when activity got recreated
        if (currentDevice != null) {
            deviceName.setText(currentDevice.getDeviceName());
            modelName.setText(currentDevice.getModel());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        currentDevice = Clipboard.INSTANCE.getInformationActivityData().device;

        final String METHODTAG = "onResume";

        // Register activity for bluetooth adapter changes
        if (!receiverRegistered) {
            receiverRegistered = true;
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(bluetoothAdapterReceiver, filter);
        }

        // setup according to device
        if(currentDevice != null){
            currentDevice.setConnectionListener(this);
            currentDevice.setReceiveDataListener(this);
            currentDevice.setErrorListener(this);

            status.setText(currentDevice.getConnectionState().toString());

            if(currentDevice.getConnectionState().equals(Device.ConnectionState.connected)){
                deviceName.setText(currentDevice.getDeviceName());

                // Ask for model number and serial number, this will result async
                String model = currentDevice.getModel();
                if(model.isEmpty() == false) {
                    setUI(model);
                }

            }

            try {
                if (currentDevice != null) {
                    for (BleCharacteristic bGC : currentDevice.getAllCharacteristics()) {
                        Log.d(CLASSTAG, METHODTAG + " ALL Characteristics UI:strValue:" + bGC.getStrValue());
                    }
                }
            } catch (DeviceException e) {
                Log.d(CLASSTAG, METHODTAG+" Error getting the list of characteristics");
            }

        }

        // Register activity for reconnection
        if (reconnectionHelper != null){
            reconnectionHelper.setErrorListener(this);
            reconnectionHelper.setReconnectListener(this);
        }

        if (reconnectionIsRunning){
            status.setText(R.string.reconnecting);
        }

        // start bt connection

        try {
            if(currentDevice != null) {
                currentDevice.startBTConnection(new Device.BTConnectionCallback() {
                    @Override
                    public void onFinished() {
                        Log.d(METHODTAG, "NOW YOU CAN SEND COMMANDS TO THE DEVICE");
                    }
                });
            }
        } catch (DeviceException e) {
            e.printStackTrace();
        }
        Log.d(CLASSTAG, METHODTAG + " AvailableCommands: "+showAvailableCommands());

        this.stopFindingDevices();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unregister activity for adapter changes
        if (receiverRegistered) {
            unregisterReceiver(bluetoothAdapterReceiver);
            receiverRegistered = false;
        }

        // Dismiss opened dialogs
        if(commandDialog != null) {
            commandDialog.dismiss();
        }

        if(customCommandDialog != null) {
            customCommandDialog.dismiss();
        }

        // Pause the bluetooth connection
        if(currentDevice != null){
            try {
                currentDevice.pauseBTConnection(new Device.BTConnectionCallback() {
                    @Override
                    public void onFinished() {
                        Log.d("onStop", "NOW Notifications are deactivated in the device");
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

        //unregister activity for connection changes
        if (currentDevice != null){
            currentDevice.setReceiveDataListener(null);
            currentDevice.setConnectionListener(null);
            currentDevice.setErrorListener(null);
            currentDevice = null;
        }
        //unregister activity for reconnection
        if (reconnectionHelper != null){
            reconnectionHelper.setErrorListener(null);
            reconnectionHelper.setReconnectListener(null);
            reconnectionHelper.stopReconnecting();
            reconnectionHelper = null;
            reconnectionIsRunning = false;
        }

//        if (currentDevice != null){
//            //Disconnect the device
//            currentDevice.disconnect();
//            Log.d(CLASSTAG, METHODTAG + "Disconnected Device: "+currentDevice.modelName);
//        }
    }

    /**
     * Show the corresponding UI elements for each of the models
     * Different Leica models support different BTLE functionality.
     * @param deviceModel Device Model
     */
    private void setUI( final String deviceModel ){

        final String METHODTAG = ".setUI";
        Log.d(CLASSTAG,METHODTAG + " Available Commands: "+this.showAvailableCommands());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String METHODTAG = ".setUI";
                Log.d(CLASSTAG, METHODTAG+" ALL deviceModel: "+deviceModel);
                if(deviceModel != null) {

                    modelName.setText(deviceModel);


                    //Show only the available elements for the models D110, LDM, D1, D2
                    if (deviceModel.equals("D110") || deviceModel.startsWith("D1") || deviceModel.startsWith("D2") || deviceModel.equals("D210")) {
                        if (inclinationLabel != null) {
                            inclinationLabel.setVisibility(View.INVISIBLE);
                        }
                        if (inclination != null) {
                            inclination.setVisibility(View.INVISIBLE);
                        }
                        if (inclinationUnitLabel != null) {
                            inclinationUnitLabel.setVisibility(View.INVISIBLE);
                        }
                        if (inclinationUnit != null) {
                            inclinationUnit.setVisibility(View.INVISIBLE);
                        }
                        if (directionLabel != null) {
                            directionLabel.setVisibility(View.INVISIBLE);
                        }
                        if (direction != null) {
                            direction.setVisibility(View.INVISIBLE);
                        }
                        if (directionUnitLabel != null) {
                            directionUnitLabel.setVisibility(View.INVISIBLE);
                        }
                        if (directionUnit != null) {
                            directionUnit.setVisibility(View.INVISIBLE);
                        }

                    }
                    //Show only the available elements for the models D810
                    else if (deviceModel.equals("D810")) {
                        modelName.setText(deviceModel);
                    }

                    //Show only the available elements for the models D510, LD520
                    else if (deviceModel.equals("D510") || deviceModel.equals("0")) {

                        if (dist != null) {
                            dist.setVisibility(View.INVISIBLE);
                        }
                        if (dist != null) {
                            dist.setVisibility(View.INVISIBLE);
                        }
                        if (sendCommand != null) {
                            sendCommand.setVisibility(View.INVISIBLE);
                        }
                        if (startTracking != null) {
                            startTracking.setVisibility(View.INVISIBLE);
                        }
                        if (stopTracking != null) {
                            stopTracking.setVisibility(View.INVISIBLE);
                        }
                        if (read != null) {
                            read.setVisibility(View.INVISIBLE);
                        }
                        if (clear != null) {
                            clear.setVisibility(View.INVISIBLE);
                        }
                        if (deviceModel.equals("D510") || (deviceModel.equals("0") && currentDevice != null && currentDevice.getDeviceName().startsWith("DISTO")) ) {

                            String usedName = deviceModel;
                            /*if (usedName.equalsIgnoreCase("0")){
                                usedName = "D510";
                            }*/
                            modelName.setText(usedName);
                        }

                        // D510 model does not send orientatio data
                        if (directionLabel != null) {
                            directionLabel.setVisibility(View.INVISIBLE);
                        }
                        if (direction != null) {
                            direction.setVisibility(View.INVISIBLE);
                        }
                        if (directionUnitLabel != null) {
                            directionUnitLabel.setVisibility(View.INVISIBLE);
                        }
                        if (directionUnit != null) {
                            directionUnit.setVisibility(View.INVISIBLE);
                        }
                    }
                }else{
                    Log.d(CLASSTAG, METHODTAG +": parameter deviceModel is null");
                }
            }
        });

    }

    private String showAvailableCommands() {
        String commandListStr = "";
        if(currentDevice!=null) {
            for (String commandStr : currentDevice.getAvailableCommands()) {
                commandListStr = commandListStr + ", " + commandStr;
            }

        }
        return commandListStr;
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

				if (!reconnectionIsRunning && reconnectionHelper != null) {
					reconnectionIsRunning = true;
					reconnectionHelper.startReconnecting();

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							status.setText(R.string.reconnecting);
						}
					});
				}

			}
		}).start();
	}


    /**
     * show a list of available commands in a dialog
     */
    public void showCommandDialog() {

        final String METHODTAG = ".showCommandDialog";

        if (currentDevice == null){
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder comandDialogBuilder = new AlertDialog.Builder(BLEInformationActivity.this);
                comandDialogBuilder.setTitle("Select Command");

                comandDialogBuilder.setItems(currentDevice.getAvailableCommands(), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String command = currentDevice.getAvailableCommands()[which];
                        Log.d(CLASSTAG,METHODTAG + command);

                        if(command.equals(Types.Commands.Custom.name())) {
                            showCustomCommandDialog();
                        } else {

                            new Thread(new Runnable(){
                                @Override
                                public void run() {
                                    try {
                                        Response response = currentDevice.sendCommand(Types.Commands.valueOf(command));
                                        response.waitForData();
                                        readDataFromResponseObject(response);
                                    } catch (DeviceException e) {
                                        Log.e(CLASSTAG, METHODTAG +": send command error: ", e);
                                        showAlert("Error Sending Command. "+ e.getMessage());
                                    }
                                }

                            }).start();

                        }
                    }
                });

                commandDialog = comandDialogBuilder.create();
                commandDialog.show();
            }
        });

    }

    /**
     * show a dialog in which a user can type in a text and send it as a command
     */
    void showCustomCommandDialog() {

        if (currentDevice == null){
            return;
        }

        final String METHODTAG = ".showCustomCommandDialog";
        final EditText input = new EditText(this);
        AlertDialog.Builder customCommandDialogBuilder = new AlertDialog.Builder(this);
        customCommandDialogBuilder.setTitle(R.string.custom_command);
        customCommandDialogBuilder.setView(input);
        customCommandDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // send any string to device
                try {
                    currentDevice.sendCustomCommand(input.getText().toString());
                } catch (DeviceException e) {
                    Log.e(CLASSTAG, METHODTAG +": Error showCustomCommandDialog ", e);
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
     * set all textviews to zeros
     */
    void clear() {
        // set the current state as text
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                distance.setText(R.string.default_value);
                distanceUnit.setText(R.string.default_value);
                inclination.setText(R.string.default_value);
                inclinationUnit.setText(R.string.default_value);
                direction.setText(R.string.default_value);
                directionUnit.setText(R.string.default_value);
            }
        });
    }

    /**
     * Check if the device is disconnected, if it is disconnected launch the reconnection functiono
     *
     * @param device the device on which the connection state changed
     * @param state the current connection state. If state is disconnected, the device object is not valid anymore. No connection can be established with this object any more.
     */
    @Override
    public void onConnectionStateChanged(Device device, final Device.ConnectionState state) {

        final String METHODTAG = ".onConnectionStateChanged";
        Log.d(CLASSTAG, METHODTAG +": " + device.getDeviceID() + ", state: " + state);

        try {
            // set the current state as text
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText(state.toString());
                }
            });

            // if disconnected, try reconnecting
            if (state == Device.ConnectionState.disconnected){

                showConnectedDisconnectedDialog(false);
                checkForReconnection();
                return;
            }
            else if(state == Device.ConnectionState.connected){
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        status.setText("Connected");
                        stopFindingDevices();
                        // if connected ask for model. this will result in onDataReceived()
                        setUI(currentDevice.getModel());

                    }
                });
            }

            showConnectedDisconnectedDialog(true);
        }catch(Exception e){
            Log.e(CLASSTAG, METHODTAG, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAsyncDataReceived(final ReceivedData receivedData) {

        final String METHODTAG = ".onAsyncDataReceived";

        if (receivedData != null) {
            try {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //loop over all the elements in the received data packet
                        // Set each element in the corresponding UI Element

                            try {
                                ReceivedBleDataPacket receivedBleDataPacket = (ReceivedBleDataPacket)receivedData.dataPacket;
                                if(receivedBleDataPacket != null) {
                                    String id = receivedBleDataPacket.dataId;

                                    switch (id) {

                                        //show the Firmware revision in Device Interface
                                        case Defines.ID_DI_FIRMWARE_REVISION: {
                                            String data = receivedBleDataPacket.getFirmwareRevision();

                                            Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);

                                        }
                                        break;
                                        //show the Hardware revision in Device Interface
                                        case Defines.ID_DI_HARDWARE_REVISION: {
                                            String data = receivedBleDataPacket.getHardwareRevision();

                                            Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);

                                        }
                                        break;
                                        //Get the Firmware revision from Device Interface
                                        case Defines.ID_DI_MANUFACTURER_NAME_STRING: {
                                            String data = receivedBleDataPacket.getManufacturerNameString();

                                            Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);

                                        }
                                        break;
                                        //show the Firmware revision from Device Interface
                                        case Defines.ID_DI_SERIAL_NUMBER: {
                                            String data = receivedBleDataPacket.getSerialNumber();

                                            // only for below android 5 or if devicename is "too short" to have a serial number
                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || (currentDevice != null && currentDevice.getDeviceName().length() <= 10)) {
                                                deviceName.setText(deviceName.getText() + " " + data);
                                            }
                                            Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);

                                        }
                                        break;
                                        //Distance Measurement
                                        case Defines.ID_DS_DISTANCE: {
                                            if (deviceIsInTrackingMode == false) {
                                                //clears only UI
                                                distance.setText(R.string.blank_value);
                                                distanceUnit.setText(R.string.blank_value);
                                                inclination.setText(R.string.blank_value);
                                                inclinationUnit.setText(R.string.blank_value);
                                                direction.setText(R.string.blank_value);
                                                directionUnit.setText(R.string.blank_value);
                                            }
                                            float data = receivedBleDataPacket.getDistance();    // distance is always float
                                            distanceValue = new MeasuredValue(data);    // save the measured value, a unit may come in the next data packet
                                            Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);
                                            hasDistanceMeasurement = true;

                                        }
                                        break;
                                        //Assign units to the distance measured Value object and do the conversion
                                        case Defines.ID_DS_DISTANCE_UNIT: {

                                            if (distanceValue != null) {
                                                short data = receivedBleDataPacket.getDistanceUnit();    // unit is always short
                                                distanceValue.setUnit(data);
                                                distanceValue.convertDistance();

                                                distance.setText(distanceValue.getConvertedValueStrNoUnit());
                                                distanceUnit.setText(distanceValue.getUnitStr());

                                                Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);
                                                hasDistanceMeasurement = true;

                                            }
                                        }
                                        break;
                                        //Inclination Angle Measurement
                                        case Defines.ID_DS_INCLINATION: {
                                            if (deviceIsInTrackingMode == false) {
                                                //Clears only UI
                                                inclination.setText(R.string.blank_value);
                                                inclinationUnit.setText(R.string.blank_value);
                                            }
                                            float data = receivedBleDataPacket.getInclination();
                                            inclinationValue = new MeasuredValue(data);
                                            Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);

                                        }
                                        break;
                                        //Assign units to the Inclination Angle  measured Value object and do the conversion
                                        case Defines.ID_DS_INCLINATION_UNIT: {
                                            if (deviceIsInTrackingMode == false) {
                                                //Clears only UI
                                                inclination.setText(R.string.blank_value);
                                                inclinationUnit.setText(R.string.blank_value);
                                            }
                                            if (inclinationValue != null) {
                                                short data = receivedBleDataPacket.getInclinationUnit();
                                                inclinationValue.setUnit(data);
                                                inclinationValue.convertAngle();
                                                inclination.setText(inclinationValue.getConvertedValueStrNoUnit());
                                                inclinationUnit.setText(inclinationValue.getUnitStr());
                                                Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);

                                            }

                                            if (hasDistanceMeasurement == false) {
                                                distance.setText(R.string.blank_value);
                                                distanceUnit.setText(R.string.blank_value);
                                            }

                                        }
                                        break;
                                        //Direction Angle Measurement
                                        case Defines.ID_DS_DIRECTION: {
                                            if (deviceIsInTrackingMode == false) {
                                                //Clears only UI
                                                direction.setText(R.string.blank_value);
                                                directionUnit.setText(R.string.blank_value);
                                            }
                                            float data = receivedBleDataPacket.getDirection();
                                            directionValue = new MeasuredValue(data);

                                            directionValue.setUnit(defaultDirectionAngleUnit);
                                            directionValue.convertAngle();
                                            direction.setText(directionValue.getConvertedValueStrNoUnit());
                                            directionUnit.setText(directionValue.getUnitStr());
                                            Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);

                                            if (hasDistanceMeasurement == false) {
                                                distance.setText(R.string.blank_value);
                                                distanceUnit.setText(R.string.blank_value);
                                            }


                                        }
                                        break;
                                        //Assign units to the Direction Angle  measured Value object and do the conversion
                                        case Defines.ID_DS_DIRECTION_UNIT: {
                                            if (deviceIsInTrackingMode == false) {
                                                //Clears only UI
                                                direction.setText(R.string.blank_value);
                                                directionUnit.setText(R.string.blank_value);
                                            }
                                            if (directionValue != null) {
                                                short data = receivedBleDataPacket.getDirectionUnit();
                                                directionValue.setUnit(data);
                                                directionValue.convertAngle();
                                                direction.setText(directionValue.getConvertedValueStrNoUnit());
                                                directionUnit.setText(directionValue.getUnitStr());
                                                Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);
                                            }

                                            if (hasDistanceMeasurement == false) {
                                                distance.setText(R.string.blank_value);
                                                distanceUnit.setText(R.string.blank_value);
                                            }
                                        }
                                        break;
                                    }
                                }
                            } catch (IllegalArgumentCheckedException e) {
                                Log.e(CLASSTAG, METHODTAG+": Error onAsyncDataReceived ", e);
                            }

                    }
                });
            } catch (Exception e) {
                Log.e(CLASSTAG, METHODTAG+": Error onAsyncDataReceived ", e);
            }
        }else{
            Log.d(CLASSTAG, METHODTAG+": Error onAsyncDataReceived: receivedData object is null  ");
        }

    }

    /**
     *
     * @throws DeviceException
     */
    void sendClearCommand() throws DeviceException {
        final String METHODTAG = ".sendClearCommand";

        if (currentDevice == null){
            return;
        }

        try {
            currentDevice.sendCommand(Types.Commands.Clear);

            if(deviceIsInTrackingMode == true) {
                currentDevice.sendCommand(Types.Commands.StopTracking);
                deviceIsInTrackingMode = false;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e(CLASSTAG, METHODTAG+ ": Error in thread sleep. ", e);

                }
            }
            clear();
        } catch (DeviceException e) {
            Log.e(CLASSTAG, METHODTAG +":  Error sending the command", e);
            throw e;

        }

    }

    /**
     *
     * @throws DeviceException
     */
    void sendDistanceCommand() throws DeviceException {
        final String METHODTAG = ".sendDistanceCommand";

        if (currentDevice == null){
            return;
        }

        //If device is not in tracking mode, command distance is sent
        if (deviceIsInTrackingMode == true) {
            return;
        }


        try {

            final ResponseBLEMeasurements response = (ResponseBLEMeasurements) currentDevice.sendCommand(Types.Commands.Distance);


            if(response != null) {
                response.waitForData();

                if (response.getError() != null) {
                    Log.e(CLASSTAG, METHODTAG + ": response has error: " + response.getError().getErrorMessage());
                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            MeasuredValue data = response.getDistanceValue();
                            try {
                                distance.setText(data.getConvertedValueStrNoUnit());
                                distanceUnit.setText(data.getUnitStr());


                                data = response.getAngleInclination();
                                //Inclination value will not be returned for models D110, D1
                                //If inclination is not enabled in D510 and S910 it will not be transferred
                                if (data != null) {
                                    inclination.setText(data.getConvertedValueStrNoUnit());
                                    inclinationUnit.setText(data.getUnitStr());
                                }

                                data = response.getAngleDirection();
                                //Direction value will not be returned for models D510, D110, D1
                                //If compass is not enabled in S910 it will not be transferred
                                if (data != null) {
                                    direction.setText(data.getConvertedValueStrNoUnit());
                                    directionUnit.setText(data.getUnitStr());
                                }
                            } catch (Exception e) {
                                Log.e(CLASSTAG, METHODTAG + ": Error sending the distance ( g ) command", e);
                            }

                        }
                    });
                }

            }else{
                Log.d(METHODTAG, "Response = null. Command could not be sent");
            }

            // IMPORTANT: when sending laserOn afterwards directly, the s910 has problems and some distance commands will not be executed/app receives no data
            //  currentDevice.sendCommand(Types.Commands.LaserOn);


        } catch (DeviceException e) {
            Log.e(CLASSTAG, METHODTAG +": Error sending the o command", e);
            throw e;
        } catch(Exception e){
            Log.e(CLASSTAG, METHODTAG +": Device is not available", e);
            throw e;
        }


    }

    /**
     * Defines the behavior of the buttons in the Activity
     */
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            final String METHODTAG = ".ButtonListener.onClick";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (currentDevice == null){
                            return;
                        }

                        switch (v.getId()) {
                            case R.id.clear:
                                sendClearCommand();
                                break;
                            case R.id.dist:
                                sendDistanceCommand();
                                break;
                            case R.id.sendcommand:
                                showCommandDialog();
                                break;
                            case R.id.startTracking:
                                currentDevice.sendCommand(Types.Commands.StartTracking);
                                deviceIsInTrackingMode = true;
                                break;
                            case R.id.stopTracking:
                                currentDevice.sendCommand(Types.Commands.StopTracking);
                                deviceIsInTrackingMode = false;
                                break;
                            case R.id.read:
                                currentDevice.readAllBleCharacteristics(new Device.BTConnectionCallback() {
                                    @Override
                                    public void onFinished() {
                                        Log.d(CLASSTAG, METHODTAG + "Characteristics were read successfully, ready to perform another task");
                                    }
                                });
                                break;
                            default:
                                Log.e(CLASSTAG, METHODTAG+": Error in ButtonListener.onClick");
                                break;
                        }
                    } catch (DeviceException e) {
                        Log.e(CLASSTAG, METHODTAG+": Error sending the command", e);
                        showAlert("Error Sending Command. "+ e.getMessage());
                    }
                }
            }).start();
        }
    }


    synchronized void showConnectedDisconnectedDialog(final boolean connected) {
        final String METHODTAG = ".showConnectedDisconnectedDialog";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
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
                }catch(Exception e){
                    Log.e(CLASSTAG, METHODTAG + "DialogError. Exception: " + e.getMessage());
                }
            }
        });

    }


    /**
     * Show alert messages
     * @param message message shown in the UI
     */
    public void showAlert(final String message){
        if (isDestroyed){
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(BLEInformationActivity.this);
                alertBuilder.setMessage(message);
                alertBuilder.setPositiveButton("Ok", null);
                alertBuilder.create().show();
            }
        });
    }

    /**
     * Show bluetooth turnOn dialog
     */
    synchronized void showBluetoothTurnOn(){
        final String METHODTAG = ".showBluetoothTurnOn";

        if(turnOnBluetoothDialogIsShown){
            Log.d(CLASSTAG, METHODTAG +": dialog is already shown");
            return;
        }

        turnOnBluetoothDialogIsShown = true;
        Log.d(CLASSTAG, METHODTAG +": turnOnBluetoothDialogIsShown is true");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            try {
                AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BLEInformationActivity.this);
                builder.setMessage("Bluetooth has to be turned on.");
                builder.setCancelable(false);
                builder.setPositiveButton("Turn it on", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        turnOnBluetoothDialogIsShown = false;
                        DeviceManager.getInstance(getApplicationContext()).enableBLE();
                    }
                });
                builder.create().show();
                Log.d(CLASSTAG, METHODTAG + ": SHOW");
            }catch(Exception e){
                Log.e(CLASSTAG, METHODTAG +": " ,e);
            }
            }
        });
    }

    /**
     * Get object error and show it in the UI
     * @param errorObject errorObject send by the Sdk
     */
    @Override
    public void onError(ErrorObject errorObject, Device device) {

        final String METHODTAG = ".onError";
        Log.e(CLASSTAG, METHODTAG +": "+ errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());

        showAlert(errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());
    }


    /**
     * On reconnect the newly connected device will be set as currentDevice.
     * And a new ReconnectionHelper will be created for the new device object.
     * @param device previously connected device
     */
    @Override
    public void onReconnect(Device device) {
        final String METHODTAG = ".onReconnect";
        Log.d(CLASSTAG, METHODTAG);

        if (currentDevice == null){
            return;
        }

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


    public void readDataFromResponseObject(Response response)  {

        final String METHODTAG = ".readDataFromResponseObject";

        if (response.getError() != null){

            Log.e(CLASSTAG, METHODTAG +": response error: " + response.getError().getErrorMessage());

            return;
        }

        if(response instanceof ResponseBLEMeasurements){
            this.extractDataFromBLEResponseObject((ResponseBLEMeasurements) response);
        }
    }

    /**
     * The ResponseWifiMeasurementExtract contains all measured data
     * @param response BLE Response
     */
    public void extractDataFromBLEResponseObject(final ResponseBLEMeasurements response){


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                MeasuredValue data = response.getDistanceValue();

                //Distance Measurement
                if(data != null && data.getConvertedValue() != Defines.defaultFloatValue && !data.getUnitStr().equals(Defines.defaultStringValue)){
                    distance.setText(data.getConvertedValueStrNoUnit());
                    distanceUnit.setText(data.getUnitStr());
                }

                //Inclination Angle Measurement
                data = response.getAngleInclination();
                if(data != null && data.getConvertedValue() !=  Defines.defaultFloatValue && !data.getUnitStr().equals(Defines.defaultStringValue)){
                    inclination.setText(data.getConvertedValueStrNoUnit());
                    inclinationUnit.setText(data.getUnitStr());
                }

                //Direction Angle Measurement
                data = response.getAngleDirection();
                if(data != null && data.getConvertedValue() !=  Defines.defaultFloatValue && !data.getUnitStr().equals(Defines.defaultStringValue)){
                    direction.setText(data.getConvertedValueStrNoUnit());
                    directionUnit.setText(data.getUnitStr());
                }
            }

        });

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