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
### getVersion(): String
### init();
### addLicence(String)
### verifyPermissions(): boolean
### findAvailableDevices(Callback)
### getConnectedDevices(): Device[]

## Methods of device

### getDeviceID(): String
### getDeviceName(): String
### getModel(): String
### connect()
### disconnect()
### unpairDevice();