# Ti.Disto

Axway Titaniums module for connecting to Disto devices via bluetooth.

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
Reads the `command.json` in modules assets folder. You can use a String paramter for filename. `commands.json` is default.
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
### toogleTracking()
### unpair()

### sendCustomCommand(String cmd, Callback)

