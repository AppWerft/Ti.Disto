package ch.leica.sdk.Devices;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.PermissionException;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.connection.APConnectionManager;
import ch.leica.sdk.connection.BaseConnectionManager.ScanDevicesListener;
import ch.leica.sdk.connection.HSConnectionManager;
import ch.leica.sdk.connection.RndisConnectionManager;
import ch.leica.sdk.connection.ble.BleConnectionManager;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceManager implements ErrorListener,
		BaseConnectionManager.ScanDevicesListener {
	private static DeviceManager e;
	private Context f;
	private List<Device> g = new ArrayList();
	private ConcurrentHashMap<String, Device> h;
	private ConcurrentHashMap<String, Device> i;
	private WifiManager j;
	private BluetoothManager k;
	APConnectionManager a;
	BleConnectionManager b;
	HSConnectionManager c;
	RndisConnectionManager d;
	private Handler l;
	private Runnable m;
	private Handler n;
	private Runnable o;
	private Handler p;
	private Runnable q;
	private Handler r;
	private Runnable s;
	private ErrorListener t;
	private FoundAvailableDeviceListener u;

	private DeviceManager(Context paramContext) throws IllegalArgumentException {
		if (paramContext == null) {
			throw new IllegalArgumentException("context is null");
		}
		f = paramContext;
		j = ((WifiManager) f.getApplicationContext().getSystemService("wifi"));
		k = ((BluetoothManager) f.getApplicationContext().getSystemService(
				"bluetooth"));
		b();
		c();
		h = new ConcurrentHashMap();
		i = new ConcurrentHashMap();
		Logs.log(Logs.LogTypes.debug, "DeviceManager created and registered ");
	}

	private boolean a() {
		return k.getAdapter().getState() == 12;
	}

	public synchronized void onBluetoothDeviceFound(String paramString,
			BluetoothDevice paramBluetoothDevice, boolean paramBoolean1,
			boolean paramBoolean2) {
		if (!a()) {
			return;
		}
		Logs.log(Logs.LogTypes.verbose, "called with " + paramString
				+ ", hasDistoService: " + paramBoolean1);
		if ((paramString != null) && (paramBluetoothDevice != null)) {
			String str = paramString + "+++"
					+ paramBluetoothDevice.getAddress();
			synchronized (g) {
				Iterator localIterator = g.iterator();
				while (localIterator.hasNext()) {
					Device localDevice = (Device) localIterator.next();
					if (localDevice.getDeviceID().equalsIgnoreCase(str)) {
						return;
					}
				}
			}
			if (!paramBoolean2) {
				Logs.log(Logs.LogTypes.debug,
						"checkNewAvailableDeviceForBle: by APP");
				a(h, paramBluetoothDevice, str, paramString, paramBoolean1);
			} else {
				Logs.log(Logs.LogTypes.debug,
						"checkNewAvailableDeviceForBle: byACL");
				a(i, paramBluetoothDevice, str, paramString, paramBoolean1);
			}
		} else {
			Logs.log(Logs.LogTypes.debug,
					"Error finding Bluetooth device. DeviceName or bluetoothObject is null");
		}
	}

	private ConcurrentHashMap<String, Device> a(
			ConcurrentHashMap<String, Device> paramConcurrentHashMap,
			BluetoothDevice paramBluetoothDevice, String paramString1,
			String paramString2, boolean paramBoolean) {
		synchronized (paramConcurrentHashMap) {
			int i1 = 0;
			Iterator localIterator = paramConcurrentHashMap.entrySet()
					.iterator();
			Object localObject1;
			while (localIterator.hasNext()) {
				localObject1 = (Map.Entry) localIterator.next();
				String str = (String) ((Map.Entry) localObject1).getKey();
				if (str.contains(paramBluetoothDevice.getAddress())) {
					i1 = 1;
				}
			}
			if ((!paramConcurrentHashMap.containsKey(paramString1))
					&& (i1 == 0)) {
				localObject1 = null;
				int i2 = 0;
				if (LeicaSdk.isYetiName(paramString2)) {
					if (LeicaSdk.scanConfig.isYeti()) {
						i2 = 1;
						localObject1 = new YetiDevice(f, paramString2,
								paramBluetoothDevice, paramBoolean);
					} else {
						Logs.log(Logs.LogTypes.verbose, "Device Skipped: "
								+ paramString2);
					}
				} else if (LeicaSdk.isDistoGenericName(paramString2)) {
					if (LeicaSdk.scanConfig.isDistoBle()) {
						i2 = 1;
						localObject1 = new BleDevice(f, paramString2,
								paramBluetoothDevice, paramBoolean);
					} else {
						Logs.log(Logs.LogTypes.verbose, "Device Skipped: "
								+ paramString2);
					}
				} else {
					Logs.log(Logs.LogTypes.verbose,
							"deviceName does not fit the naming filter: "
									+ paramString2);
					return paramConcurrentHashMap;
				}
				if (i2 == 1) {
					paramConcurrentHashMap
							.put(((Device) localObject1).getDeviceID(),
									localObject1);
					c((Device) localObject1);
					Logs.log(Logs.LogTypes.verbose, "does not contain key "
							+ paramString1
							+ "New BLE Device Found - creating device: "
							+ paramString2);
				}
			} else if (paramBoolean == true) {
				if (paramConcurrentHashMap.get(paramString1) != null) {
					gethasDistoServiceBeforeConnection = paramBoolean;
					Logs.log(Logs.LogTypes.verbose,
							"set hasDistoServiceBeforeConnection: "
									+ paramBoolean);
				} else {
					Logs.log(Logs.LogTypes.debug,
							"Error finding Bluetooth device. DeviceName or bluetoothObject is null");
				}
			}
		}
		return paramConcurrentHashMap;
	}

	public void onBluetoothDeviceACLDisconnected(String paramString) {
		if (i.containsKey(paramString) == true) {
			i.remove(paramString);
			Logs.log(Logs.LogTypes.debug,
					"Device removed from availableDevices_ACL");
		}
	}

	public synchronized void onHotspotDeviceFound(String paramString) {
		if (paramString != null) {
			String str = paramString + "+++" + "wifiHotspot";
			Object localObject = g.iterator();
			while (((Iterator) localObject).hasNext()) {
				Device localDevice = (Device) ((Iterator) localObject).next();
				if (localDevice.getDeviceID().equalsIgnoreCase(str)) {
					return;
				}
			}
			if (!h.containsKey(str)) {
				localObject = null;
				int i1 = 0;
				if (LeicaSdk.isDistoGenericName(paramString)) {
					if (LeicaSdk.scanConfig.isDistoWifi()) {
						i1 = 1;
						localObject = new DistoDevice(f, paramString);
					} else {
						Logs.log(Logs.LogTypes.verbose, "Device Skipped: "
								+ paramString);
					}
				} else if (LeicaSdk.isDisto3DName(paramString)) {
					if (LeicaSdk.scanConfig.isDisto3DD()) {
						i1 = 1;
						localObject = new Disto3DDevice(f, paramString);
					} else {
						Logs.log(Logs.LogTypes.verbose, "Device Skipped: "
								+ paramString);
					}
				} else {
					Logs.log(Logs.LogTypes.verbose,
							"deviceName does not fit the naming filter: "
									+ paramString);
					return;
				}
				if (i1 == 1) {
					h.put(((Device) localObject).getDeviceID(), localObject);
					c((Device) localObject);
					Logs.log(Logs.LogTypes.debug,
							"New HOTSPOT Device Found - creating device: "
									+ paramString);
				}
			}
		} else {
			Logs.log(Logs.LogTypes.debug,
					"Error finding Hotspot device. DeviceName is null");
		}
	}

	public synchronized void onApDeviceFound(String paramString1,
			String paramString2) {
		if ((paramString1 != null) && (paramString2 != null)) {
			String str = paramString1 + "+++" + paramString2;
			Object localObject = g.iterator();
			while (((Iterator) localObject).hasNext()) {
				Device localDevice = (Device) ((Iterator) localObject).next();
				if (localDevice.getDeviceID().equalsIgnoreCase(str)) {
					return;
				}
			}
			if (!h.containsKey(str)) {
				localObject = null;
				int i1 = 0;
				if (LeicaSdk.isDistoGenericName(paramString1)) {
					if (LeicaSdk.scanConfig.isDistoWifi()) {
						i1 = 1;
						localObject = new DistoDevice(f, paramString1,
								paramString2);
					} else {
						Logs.log(Logs.LogTypes.verbose, "Device Skipped: "
								+ paramString1);
					}
				} else {
					Logs.log(Logs.LogTypes.verbose,
							"deviceName does not fit the naming filter: "
									+ paramString1);
					return;
				}
				if (i1 == 1) {
					h.put(((Device) localObject).getDeviceID(), localObject);
					c((Device) localObject);
					Logs.log(Logs.LogTypes.debug,
							"New AP Device Found - creating device: "
									+ paramString1);
				}
			}
		} else {
			Logs.log(Logs.LogTypes.debug,
					"Error finding AP device. DeviceName or ip is null");
		}
	}

	public void onRndisDeviceFound(String paramString1, String paramString2) {
		if ((paramString1 != null) && (paramString2 != null)) {
			String str = paramString1 + "+++" + paramString2;
			Object localObject = g.iterator();
			while (((Iterator) localObject).hasNext()) {
				Device localDevice = (Device) ((Iterator) localObject).next();
				if (localDevice.getDeviceID().equalsIgnoreCase(str)) {
					return;
				}
			}
			if (!h.containsKey(str)) {
				if (LeicaSdk.isDisto3DName(paramString1)) {
					localObject = null;
					if (LeicaSdk.scanConfig.isDisto3DD()) {
						localObject = new Disto3DDevice(f, paramString1,
								paramString2);
						h.put(((Device) localObject).getDeviceID(), localObject);
						c((Device) localObject);
						Logs.log(Logs.LogTypes.debug,
								"New Rndis 3dd device found");
					} else {
						Logs.log(Logs.LogTypes.verbose, "Device Skipped: "
								+ paramString1);
					}
				} else {
					Logs.log(Logs.LogTypes.verbose,
							"deviceName does not fit the naming filter: "
									+ paramString1);
				}
			}
		}
	}

	public void onError(ErrorObject paramErrorObject, Device paramDevice) {
		if (t != null) {
			t.onError(paramErrorObject, paramDevice);
		} else {
			Logs.log(Logs.LogTypes.debug, "listener is null");
		}
	}

	public boolean checkBluetoothAvailibilty() {
		return b.checkConnectionMethodsAvailable();
	}

	public boolean checkWifiAvailibilty() {
		return c.checkConnectionMethodsAvailable();
	}

	public void enableBLE() {
		b.enableFunctionality();
	}

	public void findAvailableDevices(Context paramContext)
			throws PermissionException {
		if (LeicaSdk.scanConfig != null) {
			int i5;
			try {
				PackageInfo localPackageInfo = paramContext.getPackageManager()
						.getPackageInfo(paramContext.getPackageName(), 4096);
				if ((localPackageInfo != null)
						&& (requestedPermissions != null)) {
					int i1 = 0;
					int i2 = 0;
					int i3 = 0;
					int i4 = 0;
					i5 = 0;
					int i6 = 0;
					for (String str : requestedPermissions) {
						if ("android.permission.ACCESS_COARSE_LOCATION"
								.equalsIgnoreCase(str)) {
							i1 = 1;
						}
						if ("android.permission.ACCESS_WIFI_STATE"
								.equalsIgnoreCase(str)) {
							i2 = 1;
						}
						if ("android.permission.CHANGE_WIFI_STATE"
								.equalsIgnoreCase(str)) {
							i3 = 1;
						}
						if ("android.permission.INTERNET".equalsIgnoreCase(str)) {
							i4 = 1;
						}
						if ("android.permission.BLUETOOTH"
								.equalsIgnoreCase(str)) {
							i5 = 1;
						}
						if ("android.permission.BLUETOOTH_ADMIN"
								.equalsIgnoreCase(str)) {
							i6 = 1;
						}
					}
					if (i1 == 0) {
						throw new PermissionException(
								"Permission denied: ACCESS_COARSE_LOCATION");
					}
					if (i2 == 0) {
						throw new PermissionException(
								"Permission denied: ACCESS_WIFI_STATE");
					}
					if (i3 == 0) {
						throw new PermissionException(
								"Permission denied: CHANGE_WIFI_STATE");
					}
					if (i4 == 0) {
						throw new PermissionException(
								"Permission denied: INTERNET");
					}
					if (i5 == 0) {
						throw new PermissionException(
								"Permission denied: BLUETOOTH");
					}
					if (i6 == 0) {
						throw new PermissionException(
								"Permission denied: BLUETOOTH_ADMIN");
					}
				} else {
					throw new PermissionException("no permission found");
				}
			} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
				Logs.log(Logs.LogTypes.exception, "Error Caused by: ",
						localNameNotFoundException);
			}
			stopFindingDevices();
			h.clear();
			ArrayList localArrayList = new ArrayList();
			Iterator localIterator1 = i.entrySet().iterator();
			Object localObject2;
			while (localIterator1.hasNext()) {
				localObject1 = (Map.Entry) localIterator1.next();
				localObject2 = (String) ((Map.Entry) localObject1).getKey();
				BleDevice localBleDevice = (BleDevice) ((Map.Entry) localObject1)
						.getValue();
				i5 = 0;
				Iterator localIterator2 = g.iterator();
				while (localIterator2.hasNext()) {
		          Device device = (Device)localIterator2.next();
		          if (((Device)device).getDeviceID().contains((CharSequence)localObject2)) {
		            i5 = 1;
		            break;
		          }
		        }
				if (i5 == 0) {
					localArrayList.add(localBleDevice);
				}
			}
			Object localObject1 = localArrayList.iterator();
			while (((Iterator) localObject1).hasNext()) {
				localObject2 = (Device) ((Iterator) localObject1).next();
				c((Device) localObject2);
			}
			try {
				if ((LeicaSdk.scanConfig.isWifiAdapterOn())
						&& (c.checkConnectionMethodsAvailable())) {
					Logs.log(Logs.LogTypes.debug,
							"Hotspot findAvailableDevices");
					n.post(o);
				}
				if ((LeicaSdk.scanConfig.isWifiAdapterOn())
						&& (a.checkConnectionMethodsAvailable())) {
					Logs.log(Logs.LogTypes.debug, "AP findAvailableDevices");
					l.post(m);
				}
				if ((LeicaSdk.scanConfig.isBleAdapterOn())
						&& (b.checkConnectionMethodsAvailable())) {
					Logs.log(Logs.LogTypes.debug, "BLE findAvailableDevices");
					p.post(q);
				}
				if (d.checkConnectionMethodsAvailable()) {
					Logs.log(Logs.LogTypes.debug, "Rndis findAvailableDevices");
					r.post(s);
				}
			} catch (Exception localException) {
				Logs.log(Logs.LogTypes.exception, "Error caused by: ",
						localException);
			}
		} else {
			throw new PermissionException(
					"Parameter array Permissions is null ");
		}
	}

	public List<Device> getConnectedDevices() {
		ArrayList localArrayList = new ArrayList();
		Iterator localIterator = g.iterator();
		while (localIterator.hasNext()) {
			Device localDevice = (Device) localIterator.next();
			localArrayList.add(localDevice);
		}
		return localArrayList;
	}

	public ErrorListener getErrorListener() {
		return t;
	}

	public static synchronized DeviceManager getInstance(Context paramContext) {
		if (e == null) {
			e = new DeviceManager(paramContext);
		} else {
			ef = paramContext;
		}
		return e;
	}

	public void registerReceivers(Context paramContext) {
		if (c != null) {
			c.registerReceivers(paramContext);
		}
		if (b != null) {
			b.registerReceivers(paramContext);
		}
	}

	public void setErrorListener(ErrorListener paramErrorListener) {
		t = paramErrorListener;
	}

	public void setFoundAvailableDeviceListener(
			FoundAvailableDeviceListener paramFoundAvailableDeviceListener) {
		u = paramFoundAvailableDeviceListener;
	}

	public void stopFindingDevices() {
		if (p != null) {
			b.stopDiscovery();
		}
		if (n != null) {
			c.stopScan();
			c.stopDiscovery();
		}
		if (l != null) {
			a.stopScan();
			a.stopDiscovery();
		}
		if (r != null) {
			d.stopScan();
		}
	}

	public void unregisterReceivers() {
		if (c != null) {
			c.unregisterReceivers();
		}
		if (b != null) {
			b.unregisterReceivers();
		}
		Iterator localIterator = g.iterator();
		while (localIterator.hasNext()) {
			Device localDevice = (Device) localIterator.next();
			localDevice.unregisterReceivers();
		}
	}

	void a(Device paramDevice) {
		if (paramDevice != null) {
			synchronized (g) {
				Iterator localIterator = g.iterator();
				while (localIterator.hasNext()) {
					Device localDevice = (Device) localIterator.next();
					if (localDevice.getDeviceID().equalsIgnoreCase(
							paramDevice.getDeviceID())) {
						Logs.log(Logs.LogTypes.verbose,
								"device already in list");
						return;
					}
				}
				g.add(paramDevice);
				Logs.log(Logs.LogTypes.debug, "ConnectedDevices: "
						+ paramDevice.getDeviceID()
						+ " ConnectedDevices.size: " + g.size());
			}
		}
	}

	void b(Device paramDevice) {
		synchronized (g) {
			paramDevice.unregisterReceivers();
			g.remove(paramDevice);
			Logs.log(Logs.LogTypes.debug,
					"ConnectedDevices: " + paramDevice.getDeviceID()
							+ "Device connectedDevices.size: " + g.size());
		}
	}

	private synchronized void c(Device paramDevice) {
		AvailableDeviceFilter localAvailableDeviceFilter = new AvailableDeviceFilter();
		if (!localAvailableDeviceFilter.isDeviceAllowed(paramDevice)) {
			Logs.log(Logs.LogTypes.debug,
					"available device found, but its not allowed. deviceId: "
							+ paramDevice.getDeviceID());
			return;
		}
		if (paramDevice != null) {
			synchronized (h) {
				if (u != null) {
					Logs.log(Logs.LogTypes.debug,
							"available device found, tell listener now");
					u.onAvailableDeviceFound(paramDevice);
				} else {
					Logs.log(Logs.LogTypes.debug,
							"available device found, but no listener is set");
				}
			}
		} else {
			Logs.log(Logs.LogTypes.debug,
					"error notifying listener, Device is null");
		}
	}

	private void b() {
		c = new HSConnectionManager(j, f);
		c.scanDevicesListener = this;
		c.setErrorListener(this);
		a = new APConnectionManager(j, f);
		a.scanDevicesListener = this;
		a.setErrorListener(this);
		b = new BleConnectionManager(k, f);
		b.setContext(f);
		b.scanDevicesListener = this;
		b.setErrorListener(this);
		d = new RndisConnectionManager(f);
		d.scanDevicesListener = this;
		d.setErrorListener(this);
	}

	private void c() {
		HandlerThread localHandlerThread1 = new HandlerThread(
				"hsFindDevicesThread_" + System.currentTimeMillis());
		localHandlerThread1.start();
		n = new Handler(localHandlerThread1.getLooper());
		o = new Runnable() {
			public void run() {
				c.findAvailableDevices();
			}
		};
		HandlerThread localHandlerThread2 = new HandlerThread(
				"apFindDevicesThread_" + System.currentTimeMillis());
		localHandlerThread2.start();
		l = new Handler(localHandlerThread2.getLooper());
		m = new Runnable() {
			public void run() {
				a.findAvailableDevices();
			}
		};
		HandlerThread localHandlerThread3 = new HandlerThread(
				"bleFindDevicesThread_" + System.currentTimeMillis());
		localHandlerThread3.start();
		p = new Handler(localHandlerThread3.getLooper());
		q = new Runnable() {
			public void run() {
				b.findAvailableDevices();
			}
		};
		HandlerThread localHandlerThread4 = new HandlerThread(
				"rndisFindDeviceThread_" + System.currentTimeMillis());
		localHandlerThread4.start();
		r = new Handler(localHandlerThread4.getLooper());
		s = new Runnable() {
			public void run() {
				d.findAvailableDevices();
			}
		};
	}

	public boolean checkGPSAvailability() {
		LocationManager localLocationManager = (LocationManager) f
				.getSystemService("location");
		return localLocationManager.isProviderEnabled("gps");
	}

	public void enableWifi() {
		c.enableFunctionality();
	}

	public void reset() {
		if (i != null) {
			i.clear();
		}
	}

	public static abstract interface FoundAvailableDeviceListener {
		public abstract void onAvailableDeviceFound(Device paramDevice);
	}

	
}
