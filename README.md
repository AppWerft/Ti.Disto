# Ti.Disto

<img src="https://lasers.leica-geosystems.com/eu/sites/lasers.leica-geosystems.com.eu/files/leica_media/images/disto/x3-rotateimg.jpg" />
Axway Titaniums module for connecting to Disto devices via bluetooth. The official SDK supports Bluetooth, Yeti, USB and Wifi. This module supports at the time only yeti. This is the Leica-Name for BT.


## Prerequisites

### Permissions in manifest:

```xml
 <uses-permission android:name="android.permission.BLUETOOTH" />
 <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
 <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### command.json

[This file](https://raw.githubusercontent.com/AppWerft/Ti.Disto/master/android/assets/commands.json) contains all BT-commands and will imported from Titaniums `Resources`-folder.

### Licence key
The SDK need for work a licence key. Currently you can use the public known key (also part of this module) or you can an own one. In this case you can put it into tiapp.xml:

```xml
<property 
	name="DISTO_KEY" 
	type="String">1Xj1z6thybdW/O+Jc6XG2ExVzYuY3GF4h+</property>
```


## Usage concept

```javascript
const LeicaSDK = require("de.appwerft.disto");

if (LeicaSDK.verifyPermissions() == true) {
	LeicaSDK.setLogLevel(LeicaSDK.DEBUG);
	LeicaSDK.Bluetooth.enable();
	LeicaSDK.init("commands.json")  // don't forget commands.json in Resources folder!

	LeicaSDK.Devicemanager.findAvailableDevices({
		onfound : (e) => {
			const currentDevive = e.device;
			currentDevice.connect({
				ondata : (data) => {
					console.log(data);
				},
				onconnect : () => {
					currentDevice.startTracking();
				}
			});
		}
	});
}



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

## Bluetooth availability

```js
const BT = require("de.appwerft.disto").Bluetooth;
var state = BT.getAvailability();
```

The result can be:

*   NOTAVAILABLE
*   DISABLED
*   ENABLED

In case two you can enable BT by:

```js
import Dist from "de.appwerft.disto";
let Bluetooth = Dist.Bluetooth;
let Devicemanager = Dist.Devicemanager;

// maybe it is possible to import in one line …

BT.enableBluetooth({
	onsuccess : handleDisto, // result is name and address of BT on device
	onerror : e => {}
}); 
function handleDisto(e) {  // will hoisted ;-)
	Object.key(e).forEach(k => {console.log(k + "=" + e[k];)})
}

```

This opens a system dialog and the user can grant (or not)


## Methods of Bluetooth module

### getAvailability(): int
Possible results are:

*   NOTAVAILABLE
*   DISABLED
*   ENABLED

This constants are part of sub module Bluetooth.


### isAvailable(): boolean
If false then you cannot use this device.

### isEnabled(): boolean
If false you can start `enable()`

### enable()
This method has an object with to callbacks: `onsuccess` and `onerror`.

Alternatively you can overwrite the onSuccess property:

```js
Bluetooth.onSuccess = (e) => {
	// Disto work
};

```

###  disable()


## Methods of module 

### getVersion(): String

### init();
Reads the `commands.json` in modules assets folder. You can use a String paramter for filename. `commands.json` is default.
### verifyPermissions(): boolean

### setTimeout 

### setlogLevel

```js
import Disto from 'de.appwerft.disto';
Disto.setLogLevel(Disto.DEBUG).setTimeout(10000).init();

```
## Methods of DeviceManager

DeviceManager ist available under `LeicaSDK.Devicemanager`

### Devicemanager.findAvailableDevices({})

#### Properties

- onfound: Function


### Devicemanager.getConnectedDevices(): Device[]
Return a list of device. For every device you can the methods below:

## Methods of device

### getDeviceID(): String
### getDeviceName(): String
### connect()
### disconnect()
### isInUpdateMode(): boolean
### getConnectionState(): {name:,code:}
### getAvailableCommands(): String[]
### getDistance()

Start one measurement.
### startTracking()

Start continously measurement
### stopTracking()

no comment
### toggleTracking()
### unpair()

### sendCustomCommand(String cmd, Callback)

## Roadmap

As mentioned this module only works for Yeti-protocol. On request we can extend the module for usage with other protocols.