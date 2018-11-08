package de.appwerft.disto;

import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;
import android.app.Activity;



public class DummyActivity extends Activity  implements
DeviceManager.FoundAvailableDeviceListener{

	@Override
	public void onAvailableDeviceFound(Device arg0) {
		// TODO Auto-generated method stub
		
	}

}
