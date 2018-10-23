package ch.leica.distosdkapp;

import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;
import ch.leica.sdk.Reconnection.ReconnectionHelper;

public class InformationActivityData {

	public Device device;
	public DeviceManager deviceManager;

	public ReconnectionHelper reconnectionHelper;
	public boolean isSearchingEnabled = true;

	public InformationActivityData(Device device, ReconnectionHelper reconnectionHelper, DeviceManager deviceManager) {
		super();

		this.device = device;
		this.reconnectionHelper = reconnectionHelper;
		this.deviceManager = deviceManager;
	}

}
