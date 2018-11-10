package de.appwerft.disto;

import java.util.ArrayList;
import java.util.List;

import org.appcelerator.kroll.KrollDict;

import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.commands.ReceivedBleDataPacket;
import ch.leica.sdk.commands.ReceivedWifiDataPacket;
import ch.leica.sdk.commands.ReceivedYetiDataPacket;
import ch.leica.sdk.connection.ble.BleCharacteristic;

public class KrollDictExporter {
	
	public KrollDictExporter () {}
	public static KrollDict getBleInformation(ReceivedBleDataPacket dataPacket) {
		KrollDict data = new KrollDict();
		KrollDict event = new KrollDict();
		return event;
		
	}
	public static KrollDict getWifiInformation(ReceivedWifiDataPacket dataPacket) {
		KrollDict data = new KrollDict();
		KrollDict event = new KrollDict();
		return event;
		
	}
	public static KrollDict getYetiInformation(ReceivedYetiDataPacket dataPacket) {
		KrollDict data = new KrollDict();
		KrollDict event = new KrollDict();
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
			data.put("Rotation_X: ", dataPacket.getAccelerationAndRotation()
					.getRotation_X());
			data.put("Rotation_Y: ", dataPacket.getAccelerationAndRotation()
					.getRotation_Y());
			data.put("Rotation_Z: ", dataPacket.getAccelerationAndRotation()
					.getRotation_Z());
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
		}
		return event;
	}
	public static KrollDict DeviceToKrollDict(Device currentDevice) {
		KrollDict event = new KrollDict();
		List<KrollDict> charList = new ArrayList<KrollDict>();
		try {
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
		}
		event.put("model", currentDevice.getModel());
		event.put("device", currentDevice.getDeviceType().name());
		event.put("commands", currentDevice.getAvailableCommands());
		event.put("type", currentDevice.getConnectionType().name());
		return event;
	}
}
