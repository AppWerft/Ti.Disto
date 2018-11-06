// This is a test harness for your module
// You should do something interesting in this harness
// to test out the module and to provide instructions
// to users on how to use it by example.

// open a single window
var win = Ti.UI.createWindow({
	backgroundColor : 'white'
});

win.addEventListener('open', function() {
	const PERMISSIONS = ['android.permission.ACCESS_FINE_LOCATION', 'android.permission.ACCESS_COARSE_LOCATION'];
	if (Ti.Android.hasPermission(PERMISSIONS[0]) && Ti.Android.hasPermission(PERMISSIONS[1]))
		handleDisto();
	else
		(Ti.Android.requestPermissions(PERMISSIONS, function(e) {
				if (e.success)
					handleDisto();
			}));

	function handleDisto() {
		const DISTO = require('de.appwerft.disto');
		
		// import commands.json, importing key from tiapp.xml
		DISTO.setLogLevel(DISTO.DEBUG).init();

		
		const DeviceManager = DISTO.createDeviceManager({
			lifecycleContainer : win
		});
		DeviceManager.onFound = function(e) {
			console.log(e);
		};
		DeviceManager.findAvailableDevices();
		
		$.addEventListener('click', function() {
			DeviceManager.stopFindingDevices();
			DeviceManager.findAvailableDevices();
		});
	}

	function onAvailableDeviceFound(e) {
	}

});

win.open();
