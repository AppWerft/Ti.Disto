package de.appwerft.disto;

import java.util.ArrayList;
import java.util.List;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollObject;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;

import ch.leica.sdk.Defines;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.ErrorHandling.WrongDataException;
import ch.leica.sdk.commands.MeasuredValue;
import ch.leica.sdk.commands.MeasurementConverter;
import ch.leica.sdk.commands.ReceivedBleDataPacket;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.ReceivedWifiDataPacket;
import ch.leica.sdk.commands.ReceivedYetiDataPacket;
import ch.leica.sdk.connection.ble.BleCharacteristic;

public class MessageDispatcher {
	KrollObject krollObject;
	private final static String LCAT = TidistoModule.LCAT;
	private KrollFunction deviceCallback = null;
	private KrollFunction dataCallback = null;
	private KrollFunction errorCallback = null;
	static int defaultDirectionAngleUnit = MeasurementConverter
			.getDefaultDirectionAngleUnit();

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

	public void dispatchData(ReceivedData receivedData) {

		KrollDict payload = new KrollDict();
		KrollDict event = new KrollDict();
		ReceivedYetiDataPacket receivedYetiDataPacket = null;
		ReceivedBleDataPacket receivedBleDataPacket = null;
		if (receivedData.dataPacket instanceof ReceivedYetiDataPacket) {
			receivedYetiDataPacket = (ReceivedYetiDataPacket) receivedData.dataPacket;
		} else { // Model Information can come in a BleDataPacket
			receivedBleDataPacket = (ReceivedBleDataPacket) receivedData.dataPacket;
			Log.d(LCAT,
					"Async Data modelName"
							+ receivedBleDataPacket.getModelName());
			payload.put("model", receivedBleDataPacket.getModelName());
			payload.put("batteryLevel", receivedBleDataPacket.getBatteryLevel());
			payload.put("firmwareRevision",
					receivedBleDataPacket.getFirmwareRevision());
			payload.put("hardwareRevision",
					receivedBleDataPacket.getHardwareRevision());

		}
		if (receivedYetiDataPacket != null) {
			try {
				String id = receivedYetiDataPacket.dataId;
				Log.i(LCAT, "receivedYetiDataPacket.dataId = " + id);
				payload.put("type", id);
				switch (id) {
				case Defines.ID_IMU_BASIC_MEASUREMENTS: {
					MeasuredValue distanceValue;
					MeasuredValue inclinationValue;
					MeasuredValue directionValue;
					ReceivedYetiDataPacket.YetiBasicMeasurements data = receivedYetiDataPacket
							.getBasicMeasurements();
					distanceValue = new MeasuredValue(data.getDistance());
					distanceValue.setUnit(data.getDistanceUnit());
					distanceValue = MeasurementConverter
							.convertDistance(distanceValue);
					payload.put(
							"distance",
							getValue(distanceValue.getUnitStr(),
									distanceValue.getConvertedValueStrNoUnit()));
					inclinationValue = new MeasuredValue(data.getInclination());
					inclinationValue.setUnit(data.getInclinationUnit());
					inclinationValue = MeasurementConverter
							.convertAngle(inclinationValue);
					payload.put(
							"inclination",
							getValue(inclinationValue.getUnitStr(),
									inclinationValue
											.getConvertedValueStrNoUnit()));
					directionValue = new MeasuredValue(data.getDirection());
					directionValue.setUnit(data.getDirectionUnit());
					directionValue = MeasurementConverter
							.convertAngle(directionValue);
					payload.put(
							"direction",
							getValue(directionValue.getUnitStr(),
									directionValue.getConvertedValueStrNoUnit()));
				}
					break;
				case Defines.ID_IMU_P2P: {
					MeasuredValue VeP2PValue;
					MeasuredValue HzP2PValue;
					ReceivedYetiDataPacket.YetiP2P data = receivedYetiDataPacket
							.getP2P();
					HzP2PValue = new MeasuredValue(data.getHzAngle());
					HzP2PValue.setUnit(defaultDirectionAngleUnit);
					HzP2PValue = MeasurementConverter.convertAngle(HzP2PValue);
					payload.put("HZAngle",
							HzP2PValue.getConvertedValueStrNoUnit());
					VeP2PValue = new MeasuredValue(data.getVeAngle());
					VeP2PValue.setUnit(defaultDirectionAngleUnit);
					VeP2PValue = MeasurementConverter.convertAngle(VeP2PValue);
					payload.put("VeAngle",
							VeP2PValue.getConvertedValueStrNoUnit());
					payload.put("InclinationStatus",
							String.valueOf(data.getInclinationStatus()));
					payload.put("timestamp_P2P_Measurements",
							String.valueOf(data.getTimestampAndFlags()));
				}
					break;

				case Defines.ID_IMU_QUATERNION: {
					ReceivedYetiDataPacket.YetiQuaternion data = receivedYetiDataPacket
							.getQuaternion();
					payload.put("Quaternion_X",
							String.valueOf(data.getQuaternion_X()));
					payload.put("Quaternion_Y",
							String.valueOf(data.getQuaternion_Y()));
					payload.put("Quaternion_Z",
							String.valueOf(data.getQuaternion_Z()));
					payload.put("Quaternion_W",
							String.valueOf(data.getQuaternion_W()));
					payload.put("timestamp",
							String.valueOf(data.getTimestampAndFlags()));
				}
					break;
				case Defines.ID_IMU_ACELERATION_AND_ROTATION: {
					ReceivedYetiDataPacket.YetiAccelerationAndRotation data = receivedYetiDataPacket
							.getAccelerationAndRotation();
					payload.put("Acceleration_X",
							String.valueOf(data.getAcceleration_X()));
					payload.put("Acceleration_Y",
							String.valueOf(data.getAcceleration_Y()));
					payload.put("Acceleration_Z",
							String.valueOf(data.getAcceleration_Z()));
					payload.put("AccSensitivity",
							String.valueOf(data.getAccSensitivity()));
					payload.put("Rotation_X",
							String.valueOf(data.getRotation_X()));
					payload.put("Rotation_Y",
							String.valueOf(data.getRotation_Y()));
					payload.put("Rotation_Z",
							String.valueOf(data.getRotation_Z()));
					payload.put("RotationSensitivity",
							String.valueOf(data.getRotationSensitivity()));
					payload.put("timestamp",
							String.valueOf(data.getTimestampAndFlags()));
				}
					break;
				case Defines.ID_IMU_MAGNETOMETER: {
					ReceivedYetiDataPacket.YetiMagnetometer data;
					data = receivedYetiDataPacket.getMagnetometer();
					payload.put("Magnetometer_X",
							String.valueOf(data.getMagnetometer_X()));
					payload.put("Magnetometer_Y",
							String.valueOf(data.getMagnetometer_Y()));
					payload.put("Magnetometer_Z",
							String.valueOf(data.getMagnetometer_Z()));
					payload.put("timestamp",
							String.valueOf(data.getTimestampAndFlags()));
				}
					break;
				case Defines.ID_IMU_DISTOCOM_TRANSMIT: {
					String data = receivedYetiDataPacket
							.getDistocomReceivedMessage();
					data = data.trim();
					if (data != null && data.isEmpty() == false) {
						payload.put("distoCOMResponse", data);
					}
				}
					break;
				case Defines.ID_IMU_DISTOCOM_EVENT: {
					String data = receivedYetiDataPacket
							.getDistocomReceivedMessage();
					payload.put("distoCOMEvent", data);
				}
					break;
				default: {
					Log.w(LCAT, "Error setting data\n"+ receivedYetiDataPacket.response.toString());
				}
					break;
				}
				event.put("data", payload);
			} catch (IllegalArgumentCheckedException e) {
				Log.e(LCAT, e.getMessage());
				event.put("error", e.getMessage());
			} catch (WrongDataException e) {
				Log.e(LCAT, e.getMessage());
				event.put("error", e.getMessage());
			} catch (Exception e) {
				Log.e(LCAT, e.getMessage());
				event.put("error", e.getMessage());
			} finally {
				if (dataCallback != null) {
					event.put("success", true);
					dataCallback.call(krollObject, event);

				} else
					Log.w(LCAT, "dataCallback missing");
			}

		} else Log.w(LCAT, "ReceivedDataPacket was null");

	}

	private static KrollDict getValue(String u, String v) {
		KrollDict res = new KrollDict();
		res.put("value", v);
		res.put("unit", u);
		return res;
	}

	public void dispatchDevice(KrollDict event) {
		/*
		 * List<KrollDict> charList = new ArrayList<KrollDict>(); try { for
		 * (BleCharacteristic characteristic : currentDevice
		 * .getAllCharacteristics()) { KrollDict c = new KrollDict();
		 * c.put("id", characteristic.getId()); c.put("value",
		 * characteristic.getStrValue()); charList.add(c); }
		 * event.put("characteristics", charList.toArray()); } catch
		 * (DeviceException e) { e.printStackTrace(); }
		 */
		if (deviceCallback != null) {
			deviceCallback.call(krollObject, event);
		} else
			Log.w(LCAT, "Missing deviceCallback 'onconnect'");
	}
}
