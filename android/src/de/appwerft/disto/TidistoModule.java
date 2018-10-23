/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2017 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package de.appwerft.disto;

import java.io.IOException;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.json.JSONException;

import android.content.Context;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.Device.ConnectionState;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.ErrorHandling.PermissionException;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Listeners.ErrorListener;

@Kroll.module(name = "Tidisto", id = "de.appwerft.disto")
public class TidistoModule extends KrollModule implements DeviceManager.FoundAvailableDeviceListener, Device.ConnectionListener, ErrorListener {

	// Standard Debugging variables
	private static final String LCAT = "TiDisto";

	DeviceManager deviceManager;
	
	private KrollFunction Callback;
    boolean findDevicesRunning = false;
    /**
     * Current selected device
     */
    Device currentDevice;

    	
	// You can define constants with @Kroll.constant, for example:
	// @Kroll.constant public static final String EXTERNAL_NAME = value;

	public TidistoModule() {
		super();
	}

	@Kroll.method
	public String getVersion() {
		return LeicaSdk.getVersion();
	}
	@Kroll.method
	public void init() {
		if (LeicaSdk.isInit == false) {

            // this "commands.json" file can be named differently. it only has to exist in the assets folder
            LeicaSdk.InitObject initObject = new LeicaSdk.InitObject("commands.json");

            try {
            	
                LeicaSdk.init(TiApplication.getInstance().getApplicationContext(), initObject);
                //boolean distoWifi, boolean distoBle, boolean yeti, boolean disto3DD
                LeicaSdk.setScanConfig(false, true, false, false);
                LeicaSdk.setLicenses(new AppLicenses().keys);

            } catch (JSONException e) {
              Log.d(LCAT,e.getMessage());

            } catch (IllegalArgumentCheckedException e) {
            	Log.d(LCAT,e.getMessage());


            } catch (IOException e) {
            	Log.d(LCAT,e.getMessage());
               
            }
            
		}
		Context ctx = TiApplication.getInstance().getApplicationContext();
		deviceManager = DeviceManager.getInstance(ctx);
        deviceManager.setFoundAvailableDeviceListener(this);
        deviceManager.setErrorListener(this);

	}

	@Override
	public void onError(ErrorObject arg0, Device arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionStateChanged(Device arg0, ConnectionState arg1) {
		// TODO Auto-generated method stub
		
	}

	 @Override
	    public void onAvailableDeviceFound(final Device device) {

	        final String METHODTAG = ".onAvailableDeviceFound";
	       // stopFindingDevices();

	    //    uiHelper.setLog(this, log, "DeviceId found: " + device.getDeviceID() + ", deviceName: " + device.getDeviceName());
	        //new Thread
	     //   Log.i(CLASSTAG, METHODTAG + "DeviceId found: "  + device.getDeviceID() + ", deviceName: " + device.getDeviceName());

	        //Call this to avoid interference in Bluetooth operations


	        if (device == null) {
	            Log.i(METHODTAG, "device not found");
	            return;
	        }

	        currentDevice = device;
	       // goToInfoScreen(device);

	}
	 
	 private void dispatchMessage(KrollDict dict) {
		 if (Callback != null) {
			 Callback.call(getKrollObject(), dict);
		 }
		 
	 }

}
