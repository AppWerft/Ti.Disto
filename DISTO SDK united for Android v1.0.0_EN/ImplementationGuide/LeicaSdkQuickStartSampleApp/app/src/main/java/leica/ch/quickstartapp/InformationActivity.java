package leica.ch.quickstartapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ch.leica.sdk.Devices.BleDevice;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.YetiDevice;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Listeners.ReceivedDataListener;
import ch.leica.sdk.Types;
import ch.leica.sdk.commands.ReceivedBleDataPacket;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.ReceivedDataPacket;
import ch.leica.sdk.commands.ReceivedWifiDataPacket;
import ch.leica.sdk.commands.ReceivedYetiDataPacket;
import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.commands.response.ResponsePlain;

public class InformationActivity extends AppCompatActivity implements Device.ConnectionListener, ErrorListener, ReceivedDataListener {

    private InformationActivity.ButtonListener bl = new InformationActivity.ButtonListener();

    private Button sendCommandBt;

    private Button connectBt;
    private Button disconnectBt;
    private TextView log;
    private TextView title;

    HandlerThread sendCustomCommandThread;
    Handler sendCustomCommandHandler;

    private Device currentDevice;
    private UIHelper uiHelper;
    private AlertDialog customCommandDialog;
    private AlertDialog commandDialog;

    private final String CLASSTAG = InformationActivity.class.getSimpleName();

    private InformationActivityData informationActivityData;

    private TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        log = (TextView) findViewById(R.id.logsText);
        log.setMovementMethod(new ScrollingMovementMethod());

        title = (TextView) findViewById(R.id.title);

        sendCommandBt = (Button) findViewById(R.id.send_command_button);
        sendCommandBt.setOnClickListener(bl);

        connectBt = (Button) findViewById(R.id.connect_button);
        connectBt.setOnClickListener(bl);

        disconnectBt = (Button) findViewById(R.id.disconnect_button);
        disconnectBt.setOnClickListener(bl);

        uiHelper = new UIHelper();

        informationActivityData = Clipboard.INSTANCE.getInformationActivityData();

        if (informationActivityData != null) {
            Device device = informationActivityData.device;
            if (device != null) {
                currentDevice = device;
                currentDevice.setConnectionListener(this);
                currentDevice.setErrorListener(this);
                currentDevice.setReceiveDataListener(this);
            }
        }
        commandDialog = null;
        customCommandDialog = null;

        version = (TextView) findViewById(R.id.sdkVersion);
        version.setText(String.format("Version: %s", LeicaSdk.getVersion()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        final String METHODTAG = ".onResume";


        uiHelper.setTitle(InformationActivity.this,title,currentDevice.getDeviceID());
        stopFindingDevices();

        toogleBtn(true);
    }

    /**
     * show a dialog in which a user can type in a text and send it as a command
     */
    void showCustomCommandDialog() {


        final String METHODTAG = ".sCCDialog";
        final EditText input = new EditText(this);
        AlertDialog.Builder customCommandDialogBuilder = new AlertDialog.Builder(this);
        customCommandDialogBuilder.setTitle("Custom_command");
        customCommandDialogBuilder.setView(input);
        customCommandDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
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
                                    uiHelper.setLog(InformationActivity.this, log, METHODTAG + " - Error: "+response.getError().getErrorCode()+" "+response.getError().getErrorMessage());
                                }
                                uiHelper.setLog(InformationActivity.this, log, METHODTAG + " - "+((ResponsePlain) response).getReceivedDataString());
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
        customCommandDialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        customCommandDialog = customCommandDialogBuilder.create();
        customCommandDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(currentDevice!=null && currentDevice.getConnectionState()!= Device.ConnectionState.disconnected){
            final Thread disconnectThread;

            disconnectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    currentDevice.disconnect();
                    Log.i(CLASSTAG, "onStop" + "Disconnected Device model: " + currentDevice.modelName +
                            " deviceId: " + currentDevice.getDeviceID());
                }
            });
            disconnectThread.start();
        }

        if (commandDialog != null) {
            commandDialog.dismiss();
        }

        if (customCommandDialog != null) {
            customCommandDialog.dismiss();
        }

        InformationActivityData data = Clipboard.INSTANCE.getInformationActivityData();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        InformationActivityData data = Clipboard.INSTANCE.getInformationActivityData();
        data.device = currentDevice;


        final String METHODTAG = ".onDestroy";


        //Unregister the activity for changes in the connection
        if (currentDevice != null) {
            currentDevice.setReceiveDataListener(null);
            currentDevice.setConnectionListener(null);
            currentDevice.setErrorListener(null);
            currentDevice = null;
        }

        if (sendCustomCommandThread != null) {
            sendCustomCommandThread.interrupt();
            sendCustomCommandThread = null;
            sendCustomCommandHandler = null;
        }

    }

    @Override
    public void onConnectionStateChanged(final Device device, final Device.ConnectionState connectionState) {


        final String METHODTAG = ".onConnectionStateChanged";
        Log.i(CLASSTAG, METHODTAG + ": " + device.getDeviceID() + ", state: " + connectionState);

        try {


            if (connectionState == Device.ConnectionState.disconnected) {

                uiHelper.setLog(InformationActivity.this, log, "disconnected from device.");
                toogleBtn(true);
                return;
            }

            if(connectionState == Device.ConnectionState.connected){

                uiHelper.setLog(InformationActivity.this, log, "connected to device.");
                // start bt connection
                try {
                    if (currentDevice != null && currentDevice instanceof BleDevice ) {
                        currentDevice.startBTConnection(new Device.BTConnectionCallback() {
                            @Override
                            public void onFinished() {
                                Log.d(METHODTAG, "NOW YOU CAN SEND COMMANDS TO THE DEVICE");
                                uiHelper.setLog(InformationActivity.this, log, "NOW YOU CAN SEND COMMANDS TO THE DEVICE");


                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                toogleBtn(false);
                return;
            }



        } catch (Exception e) {
            Log.e(CLASSTAG, METHODTAG, e);
        }

    }

    @Override
    public void onError(ErrorObject errorObject, Device device) {
        uiHelper.setLog(this, log, "onError" + ": " + errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());
    }

    @Override
    public void onAsyncDataReceived(ReceivedData receivedData) {

        if(receivedData.dataPacket instanceof ReceivedWifiDataPacket){
            getWifiInformation((ReceivedWifiDataPacket) receivedData.dataPacket);
        }else if(receivedData.dataPacket instanceof ReceivedYetiDataPacket){
            getYetiInformation((ReceivedYetiDataPacket) receivedData.dataPacket);
        }else if(receivedData.dataPacket instanceof ReceivedBleDataPacket){
            getBleInformation((ReceivedBleDataPacket) receivedData.dataPacket);
        }


    }

    private void getWifiInformation(ReceivedWifiDataPacket dataPacket) {

        try {
            uiHelper.setMeasurements(InformationActivity.this, log, "HorizontalAnglewithTilt_hz: ", dataPacket.getHorizontalAnglewithTilt_hz());
            uiHelper.setMeasurements(InformationActivity.this, log, "getVerticalAngleWithTilt_v: ", dataPacket.getVerticalAngleWithTilt_v());
            uiHelper.setMeasurements(InformationActivity.this, log, "getHorizontalAngleWithoutTilt_ni_hz: ", dataPacket.getHorizontalAngleWithoutTilt_ni_hz());
            uiHelper.setMeasurements(InformationActivity.this, log, "getVerticalAngleWithoutTilt_ni_v: ", dataPacket.getVerticalAngleWithoutTilt_ni_v());
            uiHelper.setMeasurements(InformationActivity.this, log, "getDistance: ", dataPacket.getDistance());
            uiHelper.setMeasurements(InformationActivity.this, log, "getHz_temp: ", dataPacket.getHz_temp());
            uiHelper.setMeasurements(InformationActivity.this, log, "getV_temp: ", dataPacket.getV_temp());
            uiHelper.setMeasurements(InformationActivity.this, log, "getEdm_temp: ", dataPacket.getEdm_temp());
            uiHelper.setMeasurements(InformationActivity.this, log, "getBle_temp: ", dataPacket.getBle_temp());


            uiHelper.setMeasurements(InformationActivity.this, log, "IpAddress: ", dataPacket.getIpAddress());
            uiHelper.setMeasurements(InformationActivity.this, log, "SerialNumber: ", dataPacket.getSerialNumber());
            uiHelper.setMeasurements(InformationActivity.this, log, "SoftwareName: ", dataPacket.getSoftwareName());
            uiHelper.setMeasurements(InformationActivity.this, log, "SoftwareVersion: ", dataPacket.getSoftwareVersion());
            uiHelper.setMeasurements(InformationActivity.this, log, "Mac: ", dataPacket.getMac());
            uiHelper.setMeasurements(InformationActivity.this, log, "WlanVersions: ", dataPacket.getWlanVersions());
            uiHelper.setMeasurements(InformationActivity.this, log, "WlanFreq: ", dataPacket.getWlanFreq());
            uiHelper.setMeasurements(InformationActivity.this, log, "getWlanESSID: ", dataPacket.getWlanESSID());
            uiHelper.setMeasurements(InformationActivity.this, log, "getEquipment: ", dataPacket.getEquipment());
            uiHelper.setMeasurements(InformationActivity.this, log, "WlanFreq: ", dataPacket.getWlanFreq());

            uiHelper.setMeasurements(InformationActivity.this, log, "getBat_v: ", dataPacket.getBat_v());
            uiHelper.setMeasurements(InformationActivity.this, log, "getiHz: ", dataPacket.getiHz());
            uiHelper.setMeasurements(InformationActivity.this, log, "getiCross: ", dataPacket.getiCross());
            uiHelper.setMeasurements(InformationActivity.this, log, "getiLen: ", dataPacket.getiLen());
            uiHelper.setMeasurements(InformationActivity.this, log, "getUsr_vind: ", dataPacket.getUsr_vind());
            uiHelper.setMeasurements(InformationActivity.this, log, "getUser_camlasX: ", dataPacket.getUser_camlasX());
            uiHelper.setMeasurements(InformationActivity.this, log, "getUser_camlasY: ", dataPacket.getUser_camlasY());
            uiHelper.setMeasurements(InformationActivity.this, log, "getDeviceType: ", dataPacket.getDeviceType());
            uiHelper.setMeasurements(InformationActivity.this, log, "getFace: ", dataPacket.getMotWhile());
            uiHelper.setMeasurements(InformationActivity.this, log, "getMotWhile: ", dataPacket.getUser_camlasY());
            uiHelper.setMeasurements(InformationActivity.this, log, "getBat_s: ", dataPacket.getBat_s());
            uiHelper.setMeasurements(InformationActivity.this, log, "getLedSE: ", dataPacket.getLedSE());
            uiHelper.setMeasurements(InformationActivity.this, log, "getLedW: ", dataPacket.getLedW());
            uiHelper.setMeasurements(InformationActivity.this, log, "getWlanCH: ", dataPacket.getWlanCH());
            uiHelper.setMeasurements(InformationActivity.this, log, "getiSensitiveMode: ", dataPacket.getiSensitiveMode());
            uiHelper.setMeasurements(InformationActivity.this, log, "getLevel_iState: ", dataPacket.getLevel_iState());

        }catch(Exception e){
            uiHelper.setLog(InformationActivity.this, log,  e.getMessage());
        }
    }

    private void getBleInformation(ReceivedBleDataPacket dataPacket) {

        try {
            uiHelper.setMeasurements(InformationActivity.this, log, "getDistance: ", dataPacket.getDistance());
            uiHelper.setMeasurements(InformationActivity.this, log, "getDistanceUnit: ", dataPacket.getDistanceUnit());
            uiHelper.setMeasurements(InformationActivity.this, log, "getInclination: ", dataPacket.getInclination());
            uiHelper.setMeasurements(InformationActivity.this, log, "getInclinationUnit: ", dataPacket.getInclinationUnit());
            uiHelper.setMeasurements(InformationActivity.this, log, "getDirection: ", dataPacket.getDirection());
            uiHelper.setMeasurements(InformationActivity.this, log, "getDirectionUnit: ", dataPacket.getDirectionUnit());



        }catch(Exception e){
            uiHelper.setLog(InformationActivity.this, log,  e.getMessage());
        }
    }

    private void getYetiInformation(ReceivedYetiDataPacket dataPacket) {

        try {
            uiHelper.setMeasurements(InformationActivity.this, log, "getDistance: ", dataPacket.getBasicMeasurements().getDistance());
            uiHelper.setMeasurements(InformationActivity.this, log, "getDistanceUnit: ", dataPacket.getBasicMeasurements().getDistanceUnit());
            uiHelper.setMeasurements(InformationActivity.this, log, "getInclination: ", dataPacket.getBasicMeasurements().getInclination());
            uiHelper.setMeasurements(InformationActivity.this, log, "getInclinationUnit: ", dataPacket.getBasicMeasurements().getInclinationUnit());
            uiHelper.setMeasurements(InformationActivity.this, log, "getDirection: ",  dataPacket.getBasicMeasurements().getDirection());
            uiHelper.setMeasurements(InformationActivity.this, log, "getDirectionUnit: ",  dataPacket.getBasicMeasurements().getDirectionUnit());
            uiHelper.setMeasurements(InformationActivity.this, log, "getV_temp: ", dataPacket.getBasicMeasurements().getTimestampAndFlags());

            uiHelper.setMeasurements(InformationActivity.this, log, "getHzAngle: ", dataPacket.getP2P().getHzAngle());
            uiHelper.setMeasurements(InformationActivity.this, log, "getVeAngle: ", dataPacket.getP2P().getVeAngle());
            uiHelper.setMeasurements(InformationActivity.this, log, "getInclinationStatus: ", dataPacket.getP2P().getInclinationStatus());
            uiHelper.setMeasurements(InformationActivity.this, log, "getTimestampAndFlags: ", dataPacket.getP2P().getTimestampAndFlags());

            uiHelper.setMeasurements(InformationActivity.this, log, "getQuaternion_X: ", dataPacket.getQuaternion().getQuaternion_X());
            uiHelper.setMeasurements(InformationActivity.this, log, "getQuaternion_Y: ", dataPacket.getQuaternion().getQuaternion_Y());
            uiHelper.setMeasurements(InformationActivity.this, log, "getQuaternion_Z: ", dataPacket.getQuaternion().getQuaternion_Z());
            uiHelper.setMeasurements(InformationActivity.this, log, "getTimestampAndFlags: ", dataPacket.getQuaternion().getTimestampAndFlags());

            uiHelper.setMeasurements(InformationActivity.this, log, "getMagnetometer_X: ", dataPacket.getMagnetometer().getMagnetometer_X());
            uiHelper.setMeasurements(InformationActivity.this, log, "getMagnetometer_Y: ", dataPacket.getMagnetometer().getMagnetometer_Y());
            uiHelper.setMeasurements(InformationActivity.this, log, "getMagnetometer_Z: ", dataPacket.getMagnetometer().getMagnetometer_Z());
            uiHelper.setMeasurements(InformationActivity.this, log, "getTimestampAndFlags: ", dataPacket.getMagnetometer().getTimestampAndFlags());

            uiHelper.setMeasurements(InformationActivity.this, log, "getAcceleration_X: ", dataPacket.getAccelerationAndRotation().getAcceleration_X());
            uiHelper.setMeasurements(InformationActivity.this, log, "getAcceleration_Y: ", dataPacket.getAccelerationAndRotation().getAcceleration_Y());
            uiHelper.setMeasurements(InformationActivity.this, log, "getAcceleration_Z: ", dataPacket.getAccelerationAndRotation().getAcceleration_Z());
            uiHelper.setMeasurements(InformationActivity.this, log, "getAccSensitivity: ", dataPacket.getAccelerationAndRotation().getAccSensitivity());
            uiHelper.setMeasurements(InformationActivity.this, log, "getRotation_X: ", dataPacket.getAccelerationAndRotation().getRotation_X());
            uiHelper.setMeasurements(InformationActivity.this, log, "getRotation_Y: ", dataPacket.getAccelerationAndRotation().getRotation_Y());
            uiHelper.setMeasurements(InformationActivity.this, log, "getRotation_Z: ", dataPacket.getAccelerationAndRotation().getRotation_Z());
            uiHelper.setMeasurements(InformationActivity.this, log, "getRotationSensitivity: ", dataPacket.getAccelerationAndRotation().getRotationSensitivity());
            uiHelper.setMeasurements(InformationActivity.this, log, "getTimestampAndFlags: ", dataPacket.getAccelerationAndRotation().getTimestampAndFlags());

            uiHelper.setMeasurements(InformationActivity.this, log, "getDistocomReceivedMessage: ", dataPacket.getDistocomReceivedMessage());
            uiHelper.setMeasurements(InformationActivity.this, log, "getDistocom: ", dataPacket.getDistocom().getRawString());


        }catch(Exception e){
            uiHelper.setLog(InformationActivity.this, log,  e.getMessage());
        }
    }

    /**
     * Button Listener
     */
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            final String METHODTAG = ".ButtonListener.onClick";

            switch (v.getId()) {

                case R.id.send_command_button: {
                    showCommandDialog();
                }
                break;
                case R.id.disconnect_button: {
                    // Pause the bluetooth connection
                    if (currentDevice != null && currentDevice instanceof BleDevice ) {
                        uiHelper.setLog(InformationActivity.this,log,"disconnecting ...");
                        try {
                            currentDevice.pauseBTConnection(new Device.BTConnectionCallback() {
                                @Override
                                public void onFinished() {
                                    Log.d("onStop", "NOW Notifications are deactivated in the device");
                                    uiHelper.setLog(InformationActivity.this,log,"NOW Notifications are deactivated in the device");
                                    currentDevice.disconnect();
                                }
                            });
                        } catch (DeviceException e) {
                            e.printStackTrace();
                        }
                    }else if(currentDevice != null){
                        currentDevice.disconnect();
                    }

                }
                break;
                case R.id.connect_button: {
                    uiHelper.setLog(InformationActivity.this,log,"Trying to connect ...");
                    currentDevice.connect();
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
     * stop finding devices
     */
    void stopFindingDevices() {
        final String METHODTAG = ".stopFindingAvailableDevices";

        Log.i(
                CLASSTAG,
                METHODTAG + ": Stop find Devices Task and set BroadcastReceivers to Null"
        );


        //findDevicesRunning = false;
        informationActivityData.deviceManager.stopFindingDevices();
    }

    private void toogleBtn(final boolean connectRequired){
        runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if(connectRequired == true) {
                        connectBt.setEnabled(true);
                        connectBt.setVisibility(View.VISIBLE);
                        disconnectBt.setEnabled(false);
                        disconnectBt.setVisibility(View.INVISIBLE);
                        sendCommandBt.setEnabled(false);
                        sendCommandBt.setVisibility(View.INVISIBLE);
                    }else{
                        connectBt.setEnabled(false);
                        connectBt.setVisibility(View.INVISIBLE);
                        disconnectBt.setEnabled(true);
                        disconnectBt.setVisibility(View.VISIBLE);
                        sendCommandBt.setEnabled(true);
                        sendCommandBt.setVisibility(View.VISIBLE);
                    }
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

                                //readDataFromResponseObject(response);
                            } catch (DeviceException e) {
                                Log.e(CLASSTAG, METHODTAG + ": send command error: ", e);
                                uiHelper.setLog(InformationActivity.this, log, "Error Sending Command. " + e.getMessage());

                            }
                        }

                    }).start();
                }
            }
        });
        commandDialog = comandDialogBuilder.create();
        commandDialog.show();
    }

}
