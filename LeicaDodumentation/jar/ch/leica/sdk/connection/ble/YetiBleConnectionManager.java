package ch.leica.sdk.connection.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build.VERSION;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.connection.BaseConnectionManager.ConnectionState;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;

public class YetiBleConnectionManager extends BleConnectionManager {
	public YetiBleConnectionManager(BluetoothManager paramBluetoothManager,
			Context paramContext) {
		super(paramBluetoothManager, paramContext);
	}

	protected void connectToDevice() {
		if (retryPairing < RETRYPAIRINGLIMIT) {
			synchronized (this) {
				Logs.log(Logs.LogTypes.debug, "connecting to device");
				setState(BaseConnectionManager.ConnectionState.connecting, true);
				BluetoothDevice localBluetoothDevice = getBluetoothDevice();
				boolean bool = false;
				if (localBluetoothDevice != null) {
					if (!noPairingNeeded) {
						Logs.log(Logs.LogTypes.debug,
								"check if pairing needed!");
						if (Build.VERSION.SDK_INT >= 21) {
							if (localBluetoothDevice.getBondState() != 12) {
								Logs.log(
										Logs.LogTypes.debug,
										"bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED ! so pairing needed!");
								if (retryPairing == 0) {
									bool = pairAndBond();
								} else {
									unBond();
									bool = pairAndBond();
								}
							} else if (retryPairing == 0) {
								bool = true;
							} else {
								bool = pairAndBond();
							}
						} else {
							unBond();
							bool = true;
						}
					} else {
						Logs.log(Logs.LogTypes.debug, "no pairing needed!");
						bool = true;
					}
					Logs.log(Logs.LogTypes.debug, "result of isPaired: " + bool);
					if ((currentBluetoothGatt == null) && (bool == true)) {
						Logs.log(Logs.LogTypes.debug, "connect gatt");
						CALLBACK_gatt = new BleManagerGattCallback(this,
								currentBluetoothGatt, discoverServicesDelay);
						CALLBACK_gatt.isInitSequence = true;
						synchronized (lock) {
							currentBluetoothGatt = localBluetoothDevice
									.connectGatt(context, false, CALLBACK_gatt);
						}
					} else {
						Logs.log(Logs.LogTypes.debug,
								"connection not successfully prepared. ");
						retryPairing += 1;
						if (localBluetoothDevice.getBondState() == 12) {
							Logs.log(Logs.LogTypes.debug,
									"Device still bonded after failing to connect");
							unBond();
						}
						pairingTimeDelay += pairingTimeDelayDelta;
						try {
							Thread.sleep(pairingTimeDelay);
						} catch (InterruptedException localInterruptedException) {
							Logs.log(Logs.LogTypes.debug,
									"InterruptedException: ",
									localInterruptedException);
						}
						connectToDevice();
					}
				} else {
					Logs.log(Logs.LogTypes.exception,
							"Bluetooth Device is null. Connection can not be established.");
				}
			}
		} else {
			setState(BaseConnectionManager.ConnectionState.disconnected, true);
			Logs.log(Logs.LogTypes.codeerror,
					"Connection Killed unable to bond the device. ");
			if (errorListener != null) {
				ErrorObject.sendErrorBluetoothDeviceUnableToPair(errorListener,
						this);
			}
		}
	}
}
