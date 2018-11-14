//const FirebaseCore = require('firebase.core');

//FirebaseCore.configure('google-services.json');

//const FirebaseAuth = require('firebase.auth');

var $ = Ti.UI.createWindow({
	title : 'LeicaSDK Test',
	backgroundColor:'orange',
	exitOnClose : true
});
$.addEventListener('open',require('disto'));
$.open();
