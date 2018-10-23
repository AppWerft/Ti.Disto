package leica.ch.quickstartapp;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.Reconnection.ReconnectionHelper;
import ch.leica.sdk.Types;
import ch.leica.sdk.commands.response.ResponsePlain;

public class InformationActivityData {

    public Device device;
    public DeviceManager deviceManager;

    public ReconnectionHelper reconnectionHelper;



    public InformationActivityData(Device device, ReconnectionHelper reconnectionHelper, DeviceManager deviceManager){
        super();


        this.device = device;
        this.reconnectionHelper = reconnectionHelper;
        this.deviceManager = deviceManager;
    }



}
