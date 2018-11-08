# Ti.Disto

Axway Titaniums module for connecting to Disto devices via bluetooth.

## Usage concept

```javascript
const Disto = require("de.appwerft.disto");

!Dist.isBluetoothAvailable() && Dist.enableBLE();

if (Disto.verifyPermissions() == true) {
	Disto
		.setLogLevel(Disto.DEBUG)
		.enableDebugging() //optional
		.setTimeout(2000) //optional
		.init() 
}
const Dm = Disto.createDeviceManager();

Dm.findAvailableDevices({
	onfound : function(e) {
		console.log(e);
	},
	yeti : true
});

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
### verifyPermissions(): boolean

### createDeviceManager()

##Methods of DeviceManager 

### findAvailableDevices({})

#### Properties
- yeti: true
- onfound: Function 


### getConnectedDevices(): Device[]
Return a list of device. For every device you can the methods below:	

## Methods of device

### getDeviceID(): String
### getDeviceName(): String
### connect()
### disconnect()
### getAvailableCommands(): String[]
### sendCommand():
### setLaser(true | false)
### getDistance()
### startTracking()
### stopTracking()
### clear()
### updateActivate()
### updateWrite()
### sendData()
