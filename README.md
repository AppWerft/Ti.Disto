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

## Usage concept

```javascript
const LeicaSDK = require("de.appwerft.disto");

if (LeicaSDK.verifyPermissions() == true) {
	LeicaSDK
		.setLogLevel(LeicaSDK.DEBUG)
		.enableBluetooth()
		.init("commands.json")  // don't forget commands.json in Resources folder!
	LeicaSDK.Devicemanager.findAvailableDevices({
		onfound : function(e) {
			const currentDevive = e.device;
			currentDevice.connect({
				ondata : function(data) {
					console.log(data);
				},
				onconnect : function() {
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


## Methods of module

### isBluetoothAvailable(): boolean
### isBluetoothEnabled(): boolean
### enableBluetooth()
###  disableBluetooth()

Works only if BLUETOOTH_ADMIN permission is granted.
### getVersion(): String
### init();
Reads the `commands.json` in modules assets folder. You can use a String paramter for filename. `commands.json` is default.
### verifyPermissions(): boolean


##Methods of DeviceManager

DeviceManager ist available under `LeicaSDK.Devicemanager`

### findAvailableDevices({})

#### Properties

- onfound: Function


### getConnectedDevices(): Device[]
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
