# Ti.Disto

Axway Titaniums module for connecting to Disto devices via bluetooth.

## Usage concept

```javascript
const Disto = require("de.appwerft.disto");

!Dist.isBluetoothAvailable() && Dist.enableBLE();

if (Disto.verifyPermissions() == true) {
	Disto.addLicence("1Xj1z6thybdW/O+Jc6XG2ExVzYuY3GF4h+")
		.addScanConfig(Disto.distoBle) //optional
		.enableBLE()  //optional
		.setDebugging(Disto.VERBOSE) //optional
		.findAvailableDevices() 
}
const DialogSelectDevice = require('dialog')();
DialogSelectDevice.shwo();
DialogSelectDevice.onSelect() {
		DialogSelectDevice.hide();
		Device.registerListeners({
		onAsyncDataReceived : onAsyncDataReceived,
		onError : onError,
		onConnectionStateChanged : onConnectionStateChanged
	});

}

Disto.onAvailableDeviceFound(function(e) {
	Disto.stopFindingDevices();
	const Device = e.device;
	console.log("DeviceId: "  + Device.getDeviceId());
	console.log("DeviceName: " + Device.getDeviceName();
	DialogSelectDevice.addDevice(Device);
};

```
### Constants

- DEVICE\_TYPE\_BLE
- DEVICE\_TYPE\_DISTO
- DEVICE\_TYPE\_YETI
- DEVICE\_CONNECTION\_STATE\_CONNECTED
- DEVICE\_CONNECTION\_STATE\_DISCONNECTED
- DEVICE\_STATE\_NORMAL
- DEVICE\_STATE\_UPDATE
- CONNECTION\_TYPE\_WIFI\_AP
- CONNECTION\_TYPE\_WIFI\_HOTSPOT


## Methods of module
### isBluetoothAvailable(): boolean
### enableBLE()
Works only if BLUETOOTH_ADMIN permission is granted.
### getVersion(): String
### init();
Reads the `command.json` in modules assets folder.
### addLicence(String)
### verifyPermissions(): boolean
### findAvailableDevices(Callback)
Starts the scan. In every callback you will receive a device.
### getConnectedDevices(): Device[]
Return a list of device. For every device you can the methods below:	

## Methods of device

### getDeviceID(): String
### getDeviceName(): String
### getModel(): String
### connect()
### disconnect()
### unpairDevice();