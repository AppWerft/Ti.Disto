package ch.leica.sdk.Devices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.Types.ConnectionType;
import ch.leica.sdk.Types.DeviceType;
import ch.leica.sdk.Utilities.RndisPingHelper;
import ch.leica.sdk.Utilities.WaitAmoment;
import ch.leica.sdk.Utilities.WifiHelper;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.WifiCommand;
import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.connection.APConnectionManager;
import ch.leica.sdk.connection.BaseConnectionManager;
import ch.leica.sdk.connection.HSConnectionManager;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.ArrayList;
import java.util.List;

public class DistoDevice extends Device {
	public DistoDevice(Context paramContext, String paramString) {
		super(paramContext, Types.ConnectionType.wifiHotspot);
		deviceID = (paramString + "+++" + getConnectionType().toString());
		deviceName = paramString;
		c();
	}

	public DistoDevice(Context paramContext, String paramString1,
			String paramString2, Types.ConnectionType paramConnectionType) {
		super(paramContext, paramConnectionType);
		deviceID = (paramString1 + "+++" + paramString2);
		deviceName = paramString1;
		deviceIP = paramString2;
		c();
	}

	public DistoDevice(Context paramContext, String paramString1,
			String paramString2) {
		super(paramContext, Types.ConnectionType.wifiAP);
		deviceID = (paramString1 + "+++" + paramString2);
		deviceName = paramString1;
		deviceIP = paramString2;
		c();
	}

	private void c() {
		deviceType = Types.DeviceType.Disto;
	}

	protected void assignConnectionManager()
  {
    Types.ConnectionType localConnectionType = getConnectionType();
    switch (5.a[localConnectionType.ordinal()])
    {
    case 1: 
      connectionManager = new HSConnectionManager((WifiManager)context.getApplicationContext().getSystemService("wifi"), context);
      break;
    case 2: 
      connectionManager = new APConnectionManager((WifiManager)context.getApplicationContext().getSystemService("wifi"), context);
      break;
    case 3: 
      connectionManager.setConnectionParameters(new Object[] { deviceName });
    }
  }

	public String[] getAvailableCommands() {
		String[] arrayOfString = { Types.Commands.Custom.name(),
				Types.Commands.MeasureAngle.name(),
				Types.Commands.MeasurePolar.name(),
				Types.Commands.LaserOn.name(), Types.Commands.LaserOff.name(),
				Types.Commands.GetIPAdress.name(),
				Types.Commands.GetSoftwareName.name(),
				Types.Commands.GetSoftwareVersion.name(),
				Types.Commands.GetSerialNumber.name(),
				Types.Commands.GetBattery.name(),
				Types.Commands.GetTemperature.name(),
				Types.Commands.MeasureIncline.name(),
				Types.Commands.LevelModeEnable.name(),
				Types.Commands.LevelModeDisable.name(),
				Types.Commands.LevelModeLock.name(),
				Types.Commands.LevelModeUnlock.name() };
		return arrayOfString;
	}

	public boolean getModelValue() {
		boolean bool = true;
		modelName = "S910";
		return bool;
	}

	public Response sendCommand(String paramString, long paramLong)
			throws DeviceException {
		if (paramString == null) {
			throw new DeviceException("command is null");
		}
		WifiCommand localWifiCommand = null;
		localWifiCommand = new WifiCommand(paramString);
		return processCommand(localWifiCommand, paramLong);
	}

	public Response sendCommand(Types.Commands paramCommands,
			List<String> paramList) throws DeviceException {
		if (paramCommands == null) {
			throw new DeviceException("command is null");
		}
		if (paramList == null) {
			throw new DeviceException("parameters are null");
		}
		return processCommand(new WifiCommand(paramCommands, paramList), 12000L);
	}

	public Response sendCommand(Types.Commands paramCommands)
			throws DeviceException {
		return sendCommand(paramCommands, new ArrayList());
	}

	protected void setConnectionParameters()
  {
    Types.ConnectionType localConnectionType = getConnectionType();
    switch (5.a[localConnectionType.ordinal()])
    {
    case 2: 
      connectionManager.setConnectionParameters(new Object[] { deviceName, deviceIP });
      break;
    case 1: 
      connectionManager.setConnectionParameters(new Object[] { deviceName });
    }
  }

	public void saveResponseData(ReceivedData paramReceivedData,
			ErrorObject paramErrorObject) {
		super.saveResponseData(paramReceivedData, paramErrorObject);
		synchronized (i) {
			if (paramReceivedData == null) {
				Logs.log(Logs.LogTypes.codeerror, "receivedData is null");
			} else {
				responseHelper.a(paramReceivedData, i);
			}
		}
	}

	public void onConnected(BaseConnectionManager paramBaseConnectionManager) {
		g = false;
		connectionState = Device.ConnectionState.connected;
		Logs.log(Logs.LogTypes.debug, "state changed to connected");
		DeviceManager.getInstance(context).a(this);
		Logs.log(Logs.LogTypes.debug,
				"is ap or hotspot - connect to event channel and response channel now !");
		responseHandler.postDelayed(new Runnable() {
			public void run() {
				String str = "192.168.87.81";
				Types.ConnectionType localConnectionType = getConnectionType();
				if ((localConnectionType == Types.ConnectionType.wifiAP)
						|| (localConnectionType == Types.ConnectionType.rndis)) {
					str = deviceIP;
				}
				connectionManager.connectToResponseChannel(str);
			}
		}, 300L);
		eventHandler.postDelayed(new Runnable() {
			public void run() {
				String str = "192.168.87.81";
				Types.ConnectionType localConnectionType = getConnectionType();
				if ((localConnectionType == Types.ConnectionType.wifiAP)
						|| (localConnectionType == Types.ConnectionType.rndis)) {
					str = deviceIP;
				}
				connectionManager.connectEventChannel(str);
			}
		}, 600L);
		WaitAmoment localWaitAmoment = new WaitAmoment();
		localWaitAmoment.waitAmoment(3000L);
		if (!getModelValue()) {
			modelName = "D810";
		}
		if (connectionListener != null) {
			connectionListener.onConnectionStateChanged(this,
					Device.ConnectionState.connected);
		} else {
			Logs.log(Logs.LogTypes.codeerror, "listener is null");
		}
		if (getConnectionType() == Types.ConnectionType.rndis) {
			RndisPingHelper.startTask(this);
		}
	}

	public void registerReceivers() {
		int i = 1;
		if (d != null) {
			i = 0;
		}
		if (i == 1) {
			d = new BroadcastReceiver() {
				public void onReceive(Context paramAnonymousContext,
						Intent paramAnonymousIntent) {
					int i = 1;
					String str = WifiHelper.getWifiName(paramAnonymousContext);
					Logs.log(Logs.LogTypes.verbose, "wifiName: " + str);
					if (!getConnectionType().equals(Types.ConnectionType.rndis)) {
						if (str == null) {
							Logs.log(Logs.LogTypes.debug, "wifiName is null");
							disconnect();
							i = 0;
						}
						if (i == 1) {
							Types.ConnectionType localConnectionType = getConnectionType();
							if (localConnectionType == Types.ConnectionType.wifiHotspot) {
								if (!str.equalsIgnoreCase(getDeviceName())) {
									Logs.log(Logs.LogTypes.debug,
											"wifiName does not equal devicename. "
													+ str + " vs "
													+ getDeviceName()
													+ " will call disconnect()");
									disconnect();
								}
							} else if (localConnectionType == Types.ConnectionType.wifiAP) {
								WifiManager localWifiManager = (WifiManager) paramAnonymousContext
										.getApplicationContext()
										.getSystemService("wifi");
								WaitAmoment localWaitAmoment = new WaitAmoment();
								localWaitAmoment.waitAmoment(1000L);
								if (!localWifiManager.isWifiEnabled()) {
									Logs.log(Logs.LogTypes.debug,
											"wifiName is off");
									disconnect();
								}
							}
						}
					}
				}
			};
			IntentFilter localIntentFilter = new IntentFilter(
					"android.net.wifi.STATE_CHANGE");
			context.registerReceiver(d, localIntentFilter);
			Logs.log(Logs.LogTypes.debug, "wifiChangeReceiver registered");
		}
	}

	public void connect() {
		int i = 1;
		if (context == null) {
			Logs.log(Logs.LogTypes.codeerror, "Context must not be null");
			i = 0;
		}
		if (getConnectionState() == Device.ConnectionState.connected) {
			Logs.log(Logs.LogTypes.debug, "Device is already connected");
			if (connectionListener != null) {
				connectionListener.onConnectionStateChanged(this,
						getConnectionState());
			}
			i = 0;
		}
		if (i == 1) {
			b();
			connectionHandler.post(new Runnable() {
				public void run() {
					setupConnectionManager();
					registerReceivers();
					if (getConnectionType() == Types.ConnectionType.wifiHotspot) {
						String str = WifiHelper.getWifiName(context);
						if (str == null) {
							Logs.log(Logs.LogTypes.codeerror,
									"wifiName is null");
							ErrorObject.sendErrorIncorrectSSID(c,
									DistoDevice.this);
							return;
						}
						if (!str.equalsIgnoreCase(getDeviceName())) {
							Logs.log(Logs.LogTypes.codeerror,
									"wifiName does not equal devicename. "
											+ str + " vs " + getDeviceName());
							ErrorObject.sendErrorIncorrectSSID(c,
									DistoDevice.this);
							return;
						}
					}
					connectionManager.connect();
				}
			});
		}
	}
}
