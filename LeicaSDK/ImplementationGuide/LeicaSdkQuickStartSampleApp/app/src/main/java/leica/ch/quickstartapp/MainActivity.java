package leica.ch.quickstartapp;


import android.Manifest;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;

import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.ErrorHandling.PermissionException;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Listeners.ErrorListener;

public class MainActivity extends AppCompatActivity implements DeviceManager.FoundAvailableDeviceListener, Device.ConnectionListener, ErrorListener {


    // to handle info dialog at app start and only at app start
    // has to be static, otherwise the alert will be displayed more than one time
    static boolean searchInfoShown = false;
    /**
     * ClassName
     */
    private final String CLASSTAG = MainActivity.class.getSimpleName();


    //ui-alterts to present errors to users
    AlertDialog connectingDialog = null;

    // for finding and connecting to a device
    DeviceManager deviceManager;
    boolean findDevicesRunning = false;
    /**
     * Current selected device
     */
    Device currentDevice;

    private TextView version;

    private TextView log;

    private UIHelper uiHelper = new UIHelper();

    private Button findDevices_button ;
    private ButtonListener bl = new ButtonListener();


    /**
     * called when a valid Leica device is found
     *
     * @param device the device
     */
    @Override
    public void onAvailableDeviceFound(final Device device) {

        final String METHODTAG = ".onAvailableDeviceFound";
        stopFindingDevices();

        uiHelper.setLog(this, log, "DeviceId found: " + device.getDeviceID() + ", deviceName: " + device.getDeviceName());
        //new Thread
        Log.i(CLASSTAG, METHODTAG + "DeviceId found: "  + device.getDeviceID() + ", deviceName: " + device.getDeviceName());

        //Call this to avoid interference in Bluetooth operations


        if (device == null) {
            Log.i(METHODTAG, "device not found");
            return;
        }

        currentDevice = device;
        goToInfoScreen(device);

    }

    /**
     * when closing main activity, disconnect from all devices
     */
    @Override
    public void onBackPressed() {

        final String METHODTAG = ".onBackPressed";

        Log.i(CLASSTAG, METHODTAG + "You are about to close the app. That results in disconnecting from all devices. Do you want to close the app?");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (deviceManager != null) {
            // Disconnect from all the connected devices
            Log.i(CLASSTAG, METHODTAG + "To disconnect Devices count: " + deviceManager.getConnectedDevices().size());

        }

        finish();
    }

    /**
     * Called when the connection state of a device changed
     *
     * @param device currently connected device
     * @param state  current device connection state
     */
    @Override
    public void onConnectionStateChanged(Device device, Device.ConnectionState state) {

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

        Log.i(CLASSTAG, METHODTAG + ": " + errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());
        uiHelper.setLog(this, log, METHODTAG + ": " + errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    private AlertDialog mainAlertDialog;
    /**
     * Show alert message
     */
    public void showAlert(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertBuilder.setMessage(message);
                    alertBuilder.setPositiveButton("Ok", null);
                    mainAlertDialog = alertBuilder.create();
                    mainAlertDialog.show();
                } catch (Exception e) {
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

        super.onCreate(savedInstanceState);
        final String METHODTAG = ".onCreate";
        setContentView(R.layout.activity_main);
        version = (TextView) findViewById(R.id.sdkVersion);
        log = (TextView) findViewById(R.id.logsText);
        log.setMovementMethod(new ScrollingMovementMethod());
        findDevices_button = (Button) findViewById(R.id.findDevices_button);
        findDevices_button.setOnClickListener(bl);

        this.init();

        Log.d(CLASSTAG, METHODTAG + ": onCreate Finished.");
        uiHelper.setLog(this, log, METHODTAG + ": onCreate Finished.");

        // immediately start finding devices when activity resumes
        findAvailableDevices();

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

        if (LeicaSdk.isInit == false) {

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

                Log.e(
                        CLASSTAG,
                        METHODTAG + ": Error in the data of the JSON File, closing the application",
                        e
                );

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

        version.setText(String.format("Version: %s", LeicaSdk.getVersion()));

        deviceManager = DeviceManager.getInstance(this);
        deviceManager.setFoundAvailableDeviceListener(this);
        deviceManager.setErrorListener(this);

        Log.d(CLASSTAG, METHODTAG + ": Activity initialization was finished completely");

    }

    @Override
    protected void onResume() {
        super.onResume();

        final String METHODTAG = ".onResume";

        InformationActivityData informationActivityData = Clipboard.INSTANCE.getInformationActivityData();
        if (informationActivityData != null) {
            Device device = informationActivityData.device;
            if (device != null) {
                currentDevice = device;
                currentDevice.setConnectionListener(this);
                currentDevice.setErrorListener(this);
            }
        }


        // only show info dialog once
        if (searchInfoShown == false) {
            searchInfoShown = true;
            showAlert("Finding Devices started. Please wait. Please turn on the bluetooth, wifi and gps adapter to find all possible devices in reach.");
        }




        Log.i(CLASSTAG, METHODTAG + ": Activity onResume Completed");
        uiHelper.setLog(this, log, METHODTAG + ": onResume Finished.");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        InformationActivityData data = Clipboard.INSTANCE.getInformationActivityData();
        data.device = currentDevice;

        final String METHODTAG = ".onDestroy";
        Log.d(METHODTAG, "called");
        // stop finding devices when activity finishes
        // stop the timer for finding devices

        stopFindingDevices();

        //dismiss all the dialogs that may be activated
        if (connectingDialog != null) {
            connectingDialog.dismiss();
        }

        Log.i(CLASSTAG, METHODTAG + ": Activity destroyed");
        uiHelper.setLog(this, log, METHODTAG + ": onDestroy Finished.");
    }


    @Override
    protected void onStop() {
        super.onStop();

    }


    /**
     * start looking for available devices
     * - first clears the UI and show only connected devices
     * - setups deviceManager and start finding devices process
     * - setups timer for restarting finding process (after 25 seconds start a new search iteration)
     */
    void findAvailableDevices() {

        InformationActivityData data = Clipboard.INSTANCE.getInformationActivityData();

        final String METHODTAG = ".findAvailableDevices";

        uiHelper.setLog(this, log, METHODTAG + ": called.");
        Log.d(CLASSTAG, METHODTAG + ": Called");


        findDevicesRunning = true;


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

    }

    /**
     * Switch activity
     * BTLE devices go to BLEInformationActivty
     * BTLE Yeti go to YetiInformationActivty
     * Wifi devices go to WifiInformationActivity
     */
    void goToInfoScreen(final Device device) {

        final String METHODTAG = ".goToInfoScreen";

        Log.d(CLASSTAG, METHODTAG + ": Called");


        // set the current device object for the next activity
        //Clipboard singleton = Clipboard.INSTANCE;
        //singleton.setDevice(device);
        //Launch the YetiInformationActivity

        // need this because ble and yeti info activty uses the clipboard.
        Clipboard singleton = Clipboard.INSTANCE;
        singleton.setInformationActivityData(new InformationActivityData(device, null, deviceManager));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                android.app.AlertDialog alertDialog;
                android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                alertBuilder.setMessage("Device Found: "+device.getDeviceID());
                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent informationIntent = new Intent(MainActivity.this, InformationActivity.class);
                        startActivity(informationIntent);
                        if(mainAlertDialog!=null) {
                            mainAlertDialog.dismiss();
                        }
                    }
                });

                alertDialog = alertBuilder.create();
                alertDialog.show();

            }
        });




        // forget current device
        this.currentDevice = null;
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
                Log.e(CLASSTAG, METHODTAG + "Permissions error: ", e);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {

                LeicaSdk.scanConfig.setWifiAdapterOn(true);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
                    && getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {

                LeicaSdk.scanConfig.setBleAdapterOn(true);
            }

            LocationManager lm = (LocationManager) getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);
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

                        AlertDialog.Builder locationServicesDialog = new AlertDialog.Builder(MainActivity.this);
                        locationServicesDialog.setMessage("Please activate Location Services.");
                        locationServicesDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                MainActivity.this.startActivity(myIntent);

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

        uiHelper.setLog(this, log, METHODTAG + ": verify Permissions Finished.");
        return permissions;
    }


    /**
     * Button Listener
     */
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            final String METHODTAG = ".ButtonListener.onClick";

            switch (v.getId()) {

                case R.id.findDevices_button: {
                    try {
                        if(deviceManager != null) {
                            deviceManager.findAvailableDevices(MainActivity.this.getApplicationContext());
                        }
                    } catch (PermissionException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case R.id.disconnect_button: {
                    currentDevice.disconnect();
                }
                break;
                case R.id.connect_button: {
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


}