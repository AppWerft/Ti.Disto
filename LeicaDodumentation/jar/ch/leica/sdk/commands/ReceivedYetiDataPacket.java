package ch.leica.sdk.commands;

import ch.leica.sdk.ErrorHandling.WrongDataException;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;

public class ReceivedYetiDataPacket extends ReceivedDataPacket {
	private String a = "";
	private YetiBasicMeasurements b = null;
	private YetiP2P c = null;
	private YetiQuaternion d = null;
	private YetiAccelerationAndRotation e = null;
	private YetiMagnetometer f = null;
	private YetiDistocomData g = null;
	private byte[] h;

	ReceivedYetiDataPacket(String paramString, byte[] paramArrayOfByte) {
		super(paramString);
		h = paramArrayOfByte;
		a();
	}

	ReceivedYetiDataPacket(String paramString1, String paramString2) {
		dataId = paramString1;
		a = paramString2;
		a();
	}

	public YetiBasicMeasurements getBasicMeasurements()
			throws WrongDataException {
		if (b != null) {
			return b;
		}
		throw new WrongDataException("Basic Measurements has not been set");
	}

	public void setBasicMeasurements(byte[] paramArrayOfByte) {
		b = new YetiBasicMeasurements(paramArrayOfByte);
	}

	public YetiP2P getP2P() throws WrongDataException {
		if (c != null) {
			return c;
		}
		throw new WrongDataException("P2P Measurements has not been set");
	}

	public void setP2P(byte[] paramArrayOfByte) {
		c = new YetiP2P(paramArrayOfByte);
	}

	public YetiQuaternion getQuaternion() throws WrongDataException {
		if (d != null) {
			return d;
		}
		throw new WrongDataException("Quaternion has not been set");
	}

	public void setQuaternion(byte[] paramArrayOfByte) {
		d = new YetiQuaternion(paramArrayOfByte);
	}

	public YetiAccelerationAndRotation getAccelerationAndRotation()
			throws WrongDataException {
		if (e != null) {
			return e;
		}
		throw new WrongDataException(
				"Acceleration and Rotation has not been set");
	}

	public void setAccelerationAndRotation(byte[] paramArrayOfByte) {
		e = new YetiAccelerationAndRotation(paramArrayOfByte);
	}

	public YetiMagnetometer getMagnetometer() throws WrongDataException {
		if (f != null) {
			return f;
		}
		throw new WrongDataException("Magnetometer has not been set");
	}

	public void setMagnetometer(byte[] paramArrayOfByte) {
		f = new YetiMagnetometer(paramArrayOfByte);
	}

	public String getDistocomReceivedMessage() {
		return a;
	}

	public YetiDistocomData getDistocom() throws WrongDataException {
		if (g != null) {
			return g;
		}
		throw new WrongDataException("Distocom has not been set");
	}

	public void setYetiDistocomData(String paramString) {
		g = new YetiDistocomData(paramString);
	}

	private void a() {
		switch (dataId) {
		case "IMU_BASIC_MEASUREMENTS":
			setBasicMeasurements(h);
			break;
		case "IMU_P2P":
			setP2P(h);
			break;
		case "IMU_QUATERNION":
			setQuaternion(h);
			break;
		case "IMU_ACELERATION_AND_ROTATION":
			setAccelerationAndRotation(h);
			break;
		case "IMU_MAGNETOMETER":
			setMagnetometer(h);
			break;
		case "IMU_DISTOCOM_EVENT":
		case "IMU_DISTOCOM_TRANSMIT":
			setYetiDistocomData(a);
			break;
		default:
			Logs.log(Logs.LogTypes.debug,
					"Error setting the YetiDataPacket received information.");
		}
	}

	public class YetiDistocomData {
		public String dataId = "";
		private String g = "";
		private int h = 55537;
		private String i = "";
		String a = ":";
		String b = "inSwdMode";
		String c = "";
		String d = "";
		String e = "";

		public YetiDistocomData(String paramString) {
			Logs.log(Logs.LogTypes.debug, "Distocom data Received: "
					+ paramString);
			g = paramString;
			String[] arrayOfString = g.split(a);
			c = arrayOfString[0].trim();
			if (arrayOfString.length >= 2) {
				d = arrayOfString[1].trim();
			}
			if (arrayOfString.length >= 3) {
				e = arrayOfString[2].trim();
			}
			if (c.startsWith(b)) {
				dataId = "IS_UPDATE_MODE";
				Logs.log(Logs.LogTypes.debug, "inSWDMODE String: " + d);
				int j = Integer.parseInt(d);
				setInSWDMode(j);
			}
		}

		public int getInSWDMode() {
			return h;
		}

		public String getRawString() {
			return g;
		}

		public void setInSWDMode(int paramInt) {
			h = paramInt;
		}
	}

	public class YetiMagnetometer {
		private int f = 0;
		private int g = f + 4;
		private int h = g + 4;
		private int i = h + 4;
		float a = -9999.0F;
		float b = -9999.0F;
		float c = -9999.0F;
		short d = 55537;

		public YetiMagnetometer(byte[] paramArrayOfByte) {
			Logs.log(Logs.LogTypes.debug, " magnetometer_X: " + a
					+ " magnetometer_Y: " + b + " magnetometer_Z: " + c
					+ " timestampAndFlags: " + d);
		}

		public float getMagnetometer_X() {
			return a;
		}

		public float getMagnetometer_Y() {
			return b;
		}

		public float getMagnetometer_Z() {
			return c;
		}

		public short getTimestampAndFlags() {
			return d;
		}
	}

	public class YetiAccelerationAndRotation {
		private int b = 0;
		private int c = b + 4;
		private int d = c + 2;
		private int e = d + 2;
		private int f = e + 2;
		private int g = f + 2;
		private int h = g + 2;
		private int i = h + 2;
		private int j = i + 2;
		private short k = 55537;
		private short l = 55537;
		private short m = 55537;
		private short n = 55537;
		private short o = 55537;
		private short p = 55537;
		private short q = 55537;
		private short r = 55537;
		private float s = -9999.0F;

		public YetiAccelerationAndRotation(byte[] paramArrayOfByte) {
			Logs.log(Logs.LogTypes.debug, " rotationSensitivity: " + s
					+ " acceleration_X: " + k + " acceleration_Y: " + l
					+ " acceleration_Z: " + m + " accSensitivity: " + n
					+ " rotation_X: " + o + " rotation_Y: " + p
					+ " rotation_Z: " + q + " timestampAndFlags: " + r);
		}

		public short getAcceleration_X() {
			return k;
		}

		public short getAcceleration_Y() {
			return l;
		}

		public short getAcceleration_Z() {
			return m;
		}

		public short getAccSensitivity() {
			return n;
		}

		public short getRotation_X() {
			return o;
		}

		public short getRotation_Y() {
			return p;
		}

		public short getRotation_Z() {
			return q;
		}

		public float getRotationSensitivity() {
			return s;
		}

		public short getTimestampAndFlags() {
			return r;
		}
	}

	public class YetiQuaternion {
		private int b = 0;
		private int c = b + 4;
		private int d = c + 4;
		private int e = d + 4;
		private int f = e + 4;
		private float g = -9999.0F;
		private float h = -9999.0F;
		private float i = -9999.0F;
		private float j = -9999.0F;
		private short k = 55537;

		public YetiQuaternion(byte[] paramArrayOfByte) {
			Logs.log(Logs.LogTypes.debug, " quaternion_X: " + g
					+ " quaternion_Y: " + h + " quaternion_Z: " + i
					+ " quaternion_W: " + j + " timestampAndFlags: " + k);
		}

		public float getQuaternion_X() {
			return g;
		}

		public float getQuaternion_Y() {
			return h;
		}

		public float getQuaternion_Z() {
			return i;
		}

		public float getQuaternion_W() {
			return j;
		}

		public short getTimestampAndFlags() {
			return k;
		}
	}

	public class YetiP2P {
		private int b = 0;
		private int c = b + 4;
		private int d = c + 4;
		private int e = d + 2;
		private float f = -9999.0F;
		private float g = -9999.0F;
		private short h = 55537;
		private short i = 55537;

		public YetiP2P(byte[] paramArrayOfByte) {
			Logs.log(Logs.LogTypes.debug, " hzAngle: " + f + " veAngle: " + g
					+ " inclinationStatus: " + h + " timestampAndFlags: " + i);
		}

		public float getHzAngle() {
			return f;
		}

		public float getVeAngle() {
			return g;
		}

		public short getInclinationStatus() {
			return h;
		}

		public short getTimestampAndFlags() {
			return i;
		}
	}

	public class YetiBasicMeasurements {
		private int b = 0;
		private int c = b + 4;
		private int d = c + 4;
		private int e = d + 4;
		private int f = e + 2;
		private int g = f + 2;
		private int h = g + 2;
		private float i = -9999.0F;
		private float j = -9999.0F;
		private float k = -9999.0F;
		private short l = 55537;
		private short m = 55537;
		private short n = 55537;
		private short o = 55537;

		public YetiBasicMeasurements(byte[] paramArrayOfByte) {
			Logs.log(Logs.LogTypes.debug, " distance: " + i + " inclination: "
					+ j + " direction: " + k + " timestampAndFlags: " + o);
		}

		public float getDistance() {
			return i;
		}

		public float getInclination() {
			return j;
		}

		public float getDirection() {
			return k;
		}

		public short getDistanceUnit() {
			return l;
		}

		public short getInclinationUnit() {
			return m;
		}

		public short getDirectionUnit() {
			return n;
		}

		public short getTimestampAndFlags() {
			return o;
		}
	}
}
