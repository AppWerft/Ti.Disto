# Ti.Disto

Axway Titaniums module for connecting to Disto devices via bluetooth.

## Usage concept

```javascript
const Disto = require("de.appwerft.disto");

if (Disto.verifyPermissions()) {
	Disto.addLicence("1Xj1z6thybdW/O+Jc6XG2ExVzYuY3GF4h+");
	Disto.addScanConfig(Disto.distoBle);
	Disto.init();
	Disto.findAvailableDevices(onAvailableDeviceFound);
}

function onAvailableDeviceFound(e) {
	Disto.stopFindingDevices();
	const Device = e.device;
	console.log("DeviceId: "  + Device.getDeviceId());
	console.log("DeviceName: " + Device.getDeviceName()	);
}

function onConnect(e) {
	
}

function onReady(e) {
}

```