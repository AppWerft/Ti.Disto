# Ti.Disto

Axway Titaniums module for connecting to Disto devices via bluetooth.

## Usage concept

```javascript
const Disto = require("de.appwerft.disto");

!Dist.isBluetoothAvailable() && Dist.enableBLE();

if (Disto.verifyPermissions() == true) {
	Disto.addLicence("1Xj1z6thybdW/O+Jc6XG2ExVzYuY3GF4h+");
	Disto.addScanConfig(Disto.distoBle);
	Disto.init();
	Disto.getVersion();
	Disto.isBluetoothAvailable();
	Dist.enableBLE();
	Disto.findAvailableDevices(onAvailableDeviceFound)
	Disto.stopFindingDevices();
}

function onAvailableDeviceFound(e) {
	Disto.stopFindingDevices();
	const Device = e.device;
	console.log("DeviceId: "  + Device.getDeviceId());
	console.log("DeviceName: " + Device.getDeviceName();
	Device.registerListeners({
		onAsyncDataReceived : onAsyncDataReceived,
		onError : onError,
		onConnectionStateChanged : onConnectionStateChanged
	});
}


```

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