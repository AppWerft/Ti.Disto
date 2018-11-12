package de.appwerft.disto;

import java.util.ArrayList;
import java.util.List;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollObject;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;

import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.commands.ReceivedBleDataPacket;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.ReceivedWifiDataPacket;
import ch.leica.sdk.commands.ReceivedYetiDataPacket;
import ch.leica.sdk.connection.ble.BleCharacteristic;

public class MessageDispatcher {
	KrollObject krollObject;
	private final static String LCAT= TidistoModule.LCAT;
	private KrollFunction deviceCallback = null;
	private KrollFunction dataCallback = null;
	private KrollFunction errorCallback = null;
	public MessageDispatcher() {
	}
	
	public MessageDispatcher(KrollProxy proxy) {
		krollObject = proxy.getKrollObject();
	}
	
	public void registerCallbacks(KrollDict o) {
		if (o.containsKeyAndNotNull("onconnect")) {
			deviceCallback = (KrollFunction) o.get("onconnect");
		}
		if (o.containsKeyAndNotNull("ondata")) {
			dataCallback = (KrollFunction) o.get("ondata");
		}
		if (o.containsKeyAndNotNull("onerror")) {
			errorCallback = (KrollFunction) o.get("onerror");
}
	}
	public void dispatchError(ErrorObject errorObject) {
		KrollDict event = new KrollDict();
		event.put("device", this);
		event.put("message", errorObject.getErrorMessage());
		event.put("code", errorObject.getErrorCode());
		if (errorCallback != null) {
			errorCallback.call(krollObject, event);
		}
	}
	public void dispatchData(
			 ReceivedData receivedData) {
		KrollDict data = new KrollDict();
		KrollDict event = new KrollDict();
		if (receivedData.dataPacket instanceof ReceivedYetiDataPacket) {
			ReceivedYetiDataPacket dataPacket = (ReceivedYetiDataPacket) receivedData.dataPacket;
			try {
				data.put("Distance: ", dataPacket.getBasicMeasurements()
						.getDistance());
				data.put("DistanceUnit: ", dataPacket.getBasicMeasurements()
						.getDistanceUnit());
				data.put("Inclination: ", dataPacket.getBasicMeasurements()
						.getInclination());
				data.put("InclinationUnit: ", dataPacket.getBasicMeasurements()
						.getInclinationUnit());
				data.put("Direction: ", dataPacket.getBasicMeasurements()
						.getDirection());
				data.put("DirectionUnit: ", dataPacket.getBasicMeasurements()
						.getDirectionUnit());
				data.put("V_temp: ", dataPacket.getBasicMeasurements()
						.getTimestampAndFlags());
				data.put("HzAngle: ", dataPacket.getP2P().getHzAngle());
				data.put("VeAngle: ", dataPacket.getP2P().getVeAngle());
				data.put("InclinationStatus: ", dataPacket.getP2P()
						.getInclinationStatus());
				data.put("TimestampAndFlags: ", dataPacket.getP2P()
						.getTimestampAndFlags());
				data.put("Quaternion_X: ", dataPacket.getQuaternion()
						.getQuaternion_X());
				data.put("Quaternion_Y: ", dataPacket.getQuaternion()
						.getQuaternion_Y());
				data.put("Quaternion_Z: ", dataPacket.getQuaternion()
						.getQuaternion_Z());
				data.put("TimestampAndFlags: ", dataPacket.getQuaternion()
						.getTimestampAndFlags());
				data.put("Magnetometer_X: ", dataPacket.getMagnetometer()
						.getMagnetometer_X());
				data.put("Magnetometer_Y: ", dataPacket.getMagnetometer()
						.getMagnetometer_Y());
				data.put("Magnetometer_Z: ", dataPacket.getMagnetometer()
						.getMagnetometer_Z());
				data.put("TimestampAndFlags: ", dataPacket.getMagnetometer()
						.getTimestampAndFlags());
				data.put("Acceleration_X: ", dataPacket
						.getAccelerationAndRotation().getAcceleration_X());
				data.put("Acceleration_Y: ", dataPacket
						.getAccelerationAndRotation().getAcceleration_Y());
				data.put("Acceleration_Z: ", dataPacket
						.getAccelerationAndRotation().getAcceleration_Z());
				data.put("AccSensitivity: ", dataPacket
						.getAccelerationAndRotation().getAccSensitivity());
				data.put("Rotation_X: ", dataPacket
						.getAccelerationAndRotation().getRotation_X());
				data.put("Rotation_Y: ", dataPacket
						.getAccelerationAndRotation().getRotation_Y());
				data.put("Rotation_Z: ", dataPacket
						.getAccelerationAndRotation().getRotation_Z());
				data.put("RotationSensitivity: ", dataPacket
						.getAccelerationAndRotation().getRotationSensitivity());
				data.put("TimestampAndFlags: ", dataPacket
						.getAccelerationAndRotation().getTimestampAndFlags());
				data.put("DistocomReceivedMessage: ",
						dataPacket.getDistocomReceivedMessage());
				data.put("Distocom: ", dataPacket.getDistocom().getRawString());

				event.put("data", data);
				event.put("success", true);
			} catch (Exception e) {
				event.put("success", false);
				event.put("error", e.getMessage());
			} finally {
				if (dataCallback != null) {
					dataCallback.callAsync(krollObject, event);
				}
			}
		}
	}

	public void dispatchDevice(
			 KrollDict event) {
		List<KrollDict> charList = new ArrayList<KrollDict>();
		/*try {
			for (BleCharacteristic characteristic : currentDevice
					.getAllCharacteristics()) {
				KrollDict c = new KrollDict();
				c.put("id", characteristic.getId());
				c.put("value", characteristic.getStrValue());
				charList.add(c);
			}
			event.put("characteristics", charList.toArray());
		} catch (DeviceException e) {
			e.printStackTrace();
		}*/
		if (deviceCallback != null) {
			deviceCallback.call(krollObject, event);
		}else Log.w(LCAT,"Missing deviceCallback 'onconnect'");
	}
}
