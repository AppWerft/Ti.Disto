# Ti.Disto

Axway Titaniums module for connecting to Disto devices via bluetooth.

## Usage concept

```javascript
const Disto = require("de.appwerft.disto");

if (Disto.verifyPermissions()) {
	Disto.addLicence("1Xj1z6thybdW/O+Jc6XG2ExVzYuY3GF4h+");
	Disto.enableBLE();
	Disto.init();
	Disto.findAvailableDevices(onFound);
}

function onFound(e) {
	const Devices = e.devices;
	/// UI for select one
	Disto.connect(Devices[0],onConnect);
}

function onConnect(e) {
	if (e.success) {
		const Device = e.device;
		const options= {};
		Device.readData(options.onReady);
	}
}

function onReady(e) {
	console.log(e.data);
}

```