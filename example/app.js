const DISTO = require('de.appwerft.disto');

var win = Ti.UI.createWindow();
win.addEventListener('open', function() {
	const PERMISSIONS = ['android.permission.ACCESS_FINE_LOCATION', 'android.permission.ACCESS_COARSE_LOCATION'];
	if (Ti.Android.hasPermission(PERMISSIONS[0]) && Ti.Android.hasPermission(PERMISSIONS[1]))
		handleDisto();
	else
		(Ti.Android.requestPermissions(PERMISSIONS, function(e) {
				if (e.success)
					handleDisto();
			}));

	// starting DISTO after successful permission stuff:
	function handleDisto() {

		// import commands.json, importing key from tiapp.xml
		DISTO.setLogLevel(DISTO.DEBUG).init();
		// creating DeviceManager
		const DeviceManager = DISTO.createDeviceManager({
			lifecycleContainer : win
		});
		// registering callback
		DeviceManager.onFound = function(e) {
			console.log(e);
		};
		// start:
		DeviceManager.findAvailableDevices();
		// restart by click
		$.addEventListener('click', function() {
			DeviceManager.stopFindingDevices();
			DeviceManager.findAvailableDevices();
		});
	}

	function onAvailableDeviceFound(e) {
	}

});

win.open();
