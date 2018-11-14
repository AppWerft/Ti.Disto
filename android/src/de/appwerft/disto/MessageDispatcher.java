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

		KrollDict krolldata = new KrollDict();
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
			krolldata.put("model", receivedBleDataPacket.getModelName());
			krolldata.put("batteryLevel", receivedBleDataPacket.getBatteryLevel());
			krolldata.put("firmwareRevision", receivedBleDataPacket.getFirmwareRevision());
			krolldata.put("hardwareRevision", receivedBleDataPacket.getHardwareRevision());
			
			
			
		}
		if (receivedYetiDataPacket != null) {
			try {
				String id = receivedYetiDataPacket.dataId;
				Log.i(LCAT, "receivedYetiDataPacket.dataId = " + id);
				krolldata.put("type", id);
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
					krolldata
							.put("distance",
									getValue(
											distanceValue.getUnitStr(),
											distanceValue
													.getConvertedValueStrNoUnit()));
					inclinationValue = new MeasuredValue(data.getInclination());
					inclinationValue.setUnit(data.getInclinationUnit());
					inclinationValue = MeasurementConverter
							.convertAngle(inclinationValue);
					krolldata.put(
							"inclination",
							getValue(inclinationValue.getUnitStr(),
									inclinationValue
											.getConvertedValueStrNoUnit()));
					directionValue = new MeasuredValue(data.getDirection());
					directionValue.setUnit(data.getDirectionUnit());
					directionValue = MeasurementConverter
							.convertAngle(directionValue);
					krolldata
							.put("direction",
									getValue(
											directionValue.getUnitStr(),
											directionValue
													.getConvertedValueStrNoUnit()));
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
					krolldata.put("HZAngle",
							HzP2PValue.getConvertedValueStrNoUnit());
					VeP2PValue = new MeasuredValue(data.getVeAngle());
					VeP2PValue.setUnit(defaultDirectionAngleUnit);
					VeP2PValue = MeasurementConverter.convertAngle(VeP2PValue);
					krolldata.put("VeAngle",
							VeP2PValue.getConvertedValueStrNoUnit());
					krolldata.put("InclinationStatus",
							String.valueOf(data.getInclinationStatus()));
					krolldata.put("timestamp_P2P_Measurements",
							String.valueOf(data.getTimestampAndFlags()));
				}
					break;

				case Defines.ID_IMU_QUATERNION: {
					ReceivedYetiDataPacket.YetiQuaternion data = receivedYetiDataPacket
							.getQuaternion();
					krolldata.put("Quaternion_X",
							String.valueOf(data.getQuaternion_X()));
					krolldata.put("Quaternion_Y",
							String.valueOf(data.getQuaternion_Y()));
					krolldata.put("Quaternion_Z",
							String.valueOf(data.getQuaternion_Z()));
					krolldata.put("Quaternion_W",
							String.valueOf(data.getQuaternion_W()));
					krolldata.put("timestamp",
							String.valueOf(data.getTimestampAndFlags()));
				}
					break;
				case Defines.ID_IMU_ACELERATION_AND_ROTATION: {
					ReceivedYetiDataPacket.YetiAccelerationAndRotation data = receivedYetiDataPacket
							.getAccelerationAndRotation();
					krolldata.put("Acceleration_X",
							String.valueOf(data.getAcceleration_X()));
					krolldata.put("Acceleration_Y",
							String.valueOf(data.getAcceleration_Y()));
					krolldata.put("Acceleration_Z",
							String.valueOf(data.getAcceleration_Z()));
					krolldata.put("AccSensitivity",
							String.valueOf(data.getAccSensitivity()));
					krolldata.put("Rotation_X",
							String.valueOf(data.getRotation_X()));
					krolldata.put("Rotation_Y",
							String.valueOf(data.getRotation_Y()));
					krolldata.put("Rotation_Z",
							String.valueOf(data.getRotation_Z()));
					krolldata.put("RotationSensitivity",
							String.valueOf(data.getRotationSensitivity()));
					krolldata.put("timestamp",
							String.valueOf(data.getTimestampAndFlags()));
				}
					break;
				case Defines.ID_IMU_MAGNETOMETER: {
					ReceivedYetiDataPacket.YetiMagnetometer data;
					data = receivedYetiDataPacket.getMagnetometer();
					krolldata.put("Magnetometer_X",
							String.valueOf(data.getMagnetometer_X()));
					krolldata.put("Magnetometer_Y",
							String.valueOf(data.getMagnetometer_Y()));
					krolldata.put("Magnetometer_Z",
							String.valueOf(data.getMagnetometer_Z()));
					krolldata.put("timestamp",
							String.valueOf(data.getTimestampAndFlags()));
				}
					break;
				case Defines.ID_IMU_DISTOCOM_TRANSMIT: {
					String data = receivedYetiDataPacket
							.getDistocomReceivedMessage();
					data = data.trim();
					if (data != null && data.isEmpty() == false) {
						krolldata.put("distoCOMResponse", data);
					}
				}
					break;
				case Defines.ID_IMU_DISTOCOM_EVENT: {
					String data = receivedYetiDataPacket
							.getDistocomReceivedMessage();
					krolldata.put("distoCOMEvent", data);
				}
					break;
				default: {
					Log.d(LCAT, "Error setting data");
				}
					break;
				}
				event.put("data", krolldata);
			} catch (IllegalArgumentCheckedException e) {
				Log.e(LCAT,e.getMessage());
				event.put("error", e.getMessage());
			} catch (WrongDataException e) {
				Log.e(LCAT,e.getMessage());
				event.put("error", e.getMessage());
			} catch (Exception e) {
				Log.e(LCAT,e.getMessage());
				event.put("error", e.getMessage());
			} finally{
				if (dataCallback != null) {
					event.put("success", true);
					dataCallback.call(krollObject, event);

				} else
					Log.w(LCAT, "dataCallback missing");
			}

			
		}

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
