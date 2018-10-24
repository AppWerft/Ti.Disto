# Ti.Disto

Axway Titaniums module for connecting to Disto devices via bluetooth.

## Usage concept

```javascript
const Disto = require("de.appwerft.disto");

!Dist.enableBLE() && Dist.enableBLE();

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

### Dist.enableBLE()
### enableBLE()
### getVersion()
### init();
### addLicence()
### isBluetoothAvailable()
### verifyPermissions()

## Methods of device

### getDeviceID()
### getDeviceName()
### getModel()
### connect()
### disconnect()
### unpairDevice();