package de.appwerft.disto;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.connection.BaseConnectionManager;

public class TestContext extends ContextWrapper implements DeviceManager.FoundAvailableDeviceListener, Device.ConnectionListener, ErrorListener, BaseConnectionManager.ScanDevicesListener {

        TestContext(Context base) {
            super(base);
        }


        public void onError(final ErrorObject errorObject, final Device device) {
            final String METHODTAG = ".onError";

            Log.i("AAA", METHODTAG + ": " + errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());

        }

        @Override
        public void onAvailableDeviceFound(final Device device) {

            final String METHODTAG = ".onAvailableDeviceFound";
//        stopFindingDevices();

//        uiHelper.setLog(this, log, "DeviceId found: " + device.getDeviceID() + ", deviceName: " + device.getDeviceName());
            //new Thread
            Log.i("BBBB", METHODTAG + "DeviceId found: "  + device.getDeviceID() + ", deviceName: " + device.getDeviceName());

            //Call this to avoid interference in Bluetooth operations


            if (device == null) {
                Log.i(METHODTAG, "device not found");
                return;
            }

//        currentDevice = device;
//        goToInfoScreen(device);

        }

        @Override
        public void onConnectionStateChanged(Device device, Device.ConnectionState state) {
            Log.i("BBBB", "GGG");

        }



    @Override
    public void onApDeviceFound(String arg0, String arg1) {
        // TODO Auto-generated method stub
        Log.i( "TTTT", "onApDeviceFound");

    }

    @Override
    public void onBluetoothDeviceACLDisconnected(String arg0) {
        // TODO Auto-generated method stub
        Log.i( "RRRR", "onBluetoothDeviceACLDisconnected");

    }

    @Override
    public void onBluetoothDeviceFound(String arg0, BluetoothDevice device,
                                       boolean arg2, boolean arg3) {
        Log.i( "FFFF", "onBluetoothDeviceFound");

    }

    @Override
    public void onHotspotDeviceFound(String arg0) {
        // TODO Auto-generated method stub
        Log.i( "EEEE", "EEEEEE");

    }

    @Override
    public void onRndisDeviceFound(String arg0, String arg1) {
        // TODO Auto-generated method stub
        Log.i( "onRndisDeviceFound", "onRndisDeviceFound");

    }
}
