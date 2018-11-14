const LeicaSDK = require('de.appwerft.disto');
var deviceList = Ti.UI.createTableView();

const Dialog = Ti.UI.createAlertDialog({
	androidView : deviceList,
	buttonNames : [],
});

//
module.exports = function() {
	$.add(Ti.UI.createLabel({
		top : 20,
		font : {
			fontSize : 24
		},
		text : ""
	}));
	$.add(Ti.UI.createButton({
		bottom : 0,
		visible : false,
		title : "Start tracking",
		height : 50,
		width : Ti.UI.FILL
	}));

	const PERMISSIONS = ['android.permission.ACCESS_FINE_LOCATION', 'android.permission.ACCESS_COARSE_LOCATION'];
	if (Ti.Android.hasPermission(PERMISSIONS[0]) && Ti.Android.hasPermission(PERMISSIONS[1]))
		handleDisto();
	else
		(Ti.Android.requestPermissions(PERMISSIONS, function(e) {
				if (e.success)
					handleDisto();
			}));

	function handleDisto() {
		LeicaSDK.enableBluetooth().setLogLevel(LeicaSDK.DEBUG).init();

		LeicaSDK.Devicemanager.findAvailableDevices({
			onfound : function(e) {
				$.title = e.id;
				LeicaSDK.Devicemanager.stopFindingDevices();
				const currentDevice = e.device;
				currentDevice.connect({
					onconnect : function(device) {
						$.title = device.model;
						$.children[1].visible = true;
						$.children[1].addEventListener('click', function() {
							currentDevice.startTracking(function(e) {
								console.log(e);
							});
						});
					},
					ondata : function(e) {
						if (e.success) {
							$.children[0].text = JSON.stringify(e.data, null, 2);

						}
					}
				});

				//deviceList.appendRow(require("devicerow")(e));
			},
			onerror : function() {
			}
		});
	}

};
