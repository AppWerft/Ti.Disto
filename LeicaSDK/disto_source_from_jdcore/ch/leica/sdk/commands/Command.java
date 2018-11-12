package ch.leica.sdk.commands;

import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.commands.response.ResponseBLEMeasurements;
import ch.leica.sdk.commands.response.ResponseBatteryStatus;
import ch.leica.sdk.commands.response.ResponseDeviceInfo;
import ch.leica.sdk.commands.response.ResponseFace;
import ch.leica.sdk.commands.response.ResponseImage;
import ch.leica.sdk.commands.response.ResponseMotorStatus;
import ch.leica.sdk.commands.response.ResponsePlain;
import ch.leica.sdk.commands.response.ResponseTemperature;
import ch.leica.sdk.commands.response.ResponseUpdate;
import ch.leica.sdk.commands.response.ResponseWifiMeasurements;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.Iterator;
import java.util.List;

public abstract class Command {
	protected String payload;
	private byte[] a;
	protected Types.Commands commandValue;
	protected ReceivedData receivedData;
	public boolean hasTerminator = false;

	public Command() {
	}

	public Boolean hasCommandResponse() {
		return Boolean.valueOf(true);
	}

	public abstract void preparePayload(Types.Commands paramCommands,
			List<String> paramList);

	public abstract void preparePayload(String paramString);

	public static boolean isCommandCorrect(String paramString1,
			String paramString2, String paramString3) {
		boolean bool = false;
		if ((paramString1 != null)
				&& (paramString2 != null)
				&& (paramString3 != null)
				&& (((paramString1.matches("^[\\p{Alnum}|_]+$"))
						&& (paramString2.matches("^[\\p{Alnum}|_]*$")) && (paramString3
							.matches("^[\\p{Alnum}|_\t]*$"))) || ("\r\n"
						.equals(paramString1)))) {
			bool = true;
		}
		return bool;
	}

	public static boolean isParameterCorrect(String paramString) {
		boolean bool = false;
		if (paramString != null) {
			bool = true;
		}
		return bool;
	}

	public Types.Commands getCommandValue() {
		return commandValue;
	}

	public ReceivedData getReceivedData() {
		return receivedData;
	}

	public void preparePayload(byte[] paramArrayOfByte) {
		if (paramArrayOfByte != null) {
			a = paramArrayOfByte;
		} else {
			Logs.log(Logs.LogTypes.codeerror, "Caused by: null command");
			a = null;
		}
	}

	public void setPayload(String paramString, boolean paramBoolean,
			List<String> paramList) {
		if (paramString != null) {
			payload = paramString;
			Object localObject;
			if (paramList != null) {
				localObject = paramList.iterator();
				while (((Iterator) localObject).hasNext()) {
					String str = (String) ((Iterator) localObject).next();
					if ((str != null) && (!str.isEmpty())
							&& (isParameterCorrect(str))) {
						payload = (payload + " " + str);
					}
				}
			}
			if (paramBoolean == true) {
				localObject = WifiCommand.getCommand(Types.Commands.Terminator);
				if (localObject != null) {
					if (!payload.endsWith((String) localObject)) {
						payload += (String) localObject;
					}
				} else {
					Logs.log(Logs.LogTypes.codeerror,
							"Caused by: Terminator not Existing");
					payload = "";
				}
			}
		} else {
			Logs.log(Logs.LogTypes.codeerror, "Caused by: Null command");
			payload = "";
		}
		Logs.log(Logs.LogTypes.debug, "payload: " + payload);
		a = payload.getBytes();
	}

	public String getPayload() {
		if (payload == null) {
			if (getCommandValue().equals(Types.Commands.UpdateWrite)) {
				Logs.log(Logs.LogTypes.codeerror, " Payload is NULL");
			}
			payload = "";
		}
		return payload;
	}

	public byte[] getBytePayload() {
		return a;
	}

	public Response getResponseForCommand()
  {
    if (getCommandValue() == null) {
      return null;
    }
    switch (1.a[getCommandValue().ordinal()])
    {
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    case 16: 
    case 17: 
    case 18: 
    case 19: 
    case 20: 
    case 21: 
    case 22: 
    case 23: 
    case 24: 
    case 25: 
    case 26: 
    case 27: 
    case 28: 
    case 29: 
    case 30: 
    case 31: 
    case 32: 
    case 33: 
    case 34: 
    case 35: 
    case 36: 
    case 37: 
    case 38: 
    case 39: 
    case 40: 
    case 41: 
    case 42: 
    case 43: 
    case 44: 
    case 45: 
    case 46: 
    case 47: 
    case 48: 
    case 49: 
    case 50: 
    case 51: 
    case 52: 
    case 53: 
    case 54: 
    case 55: 
    case 56: 
    case 57: 
    case 58: 
    case 59: 
    case 60: 
    case 61: 
    case 62: 
    case 63: 
    case 64: 
    case 65: 
    case 66: 
    case 67: 
    case 68: 
    case 69: 
    case 70: 
    case 71: 
    case 72: 
    case 73: 
    case 74: 
    case 75: 
    case 76: 
    case 77: 
    case 78: 
    case 79: 
    case 80: 
    case 81: 
    case 82: 
    case 83: 
      return new ResponsePlain(getCommandValue());
    case 84: 
    case 85: 
    case 86: 
    case 87: 
    case 88: 
    case 89: 
    case 90: 
    case 91: 
    case 92: 
    case 93: 
    case 94: 
    case 95: 
    case 96: 
    case 97: 
    case 98: 
      return new ResponseDeviceInfo(getCommandValue());
    case 99: 
    case 100: 
    case 101: 
    case 102: 
      return new ResponseTemperature(getCommandValue());
    case 103: 
    case 104: 
    case 105: 
      return new ResponseWifiMeasurements(getCommandValue());
    case 106: 
      return new ResponseBatteryStatus(getCommandValue());
    case 107: 
    case 108: 
    case 109: 
    case 110: 
      return new ResponseMotorStatus(getCommandValue());
    case 111: 
    case 112: 
    case 113: 
    case 114: 
    case 115: 
      return new ResponseImage(getCommandValue());
    case 116: 
      return new ResponseFace(getCommandValue());
    case 117: 
      return new ResponseBLEMeasurements(getCommandValue());
    case 118: 
    case 119: 
    case 120: 
    case 121: 
    case 122: 
    case 123: 
    case 124: 
      return new ResponseUpdate(getCommandValue());
    }
    return new ResponsePlain(getCommandValue());
  }

	public boolean isUpdateCommand()
  {
    if (getCommandValue() == null) {
      return false;
    }
    switch (1.a[getCommandValue().ordinal()])
    {
    case 69: 
    case 70: 
    case 71: 
    case 72: 
    case 73: 
    case 74: 
    case 75: 
    case 76: 
    case 77: 
    case 78: 
    case 79: 
    case 118: 
    case 119: 
    case 120: 
    case 121: 
    case 122: 
    case 123: 
    case 124: 
      return true;
    }
    return false;
  }

	static class a {
		private String a;
		private String b = "";
		private String c = "";

		public a(String paramString1, String paramString2, String paramString3)
				throws IllegalArgumentCheckedException {
			if (paramString1 != null) {
				a = paramString1;
				if (paramString2 != null) {
					b = paramString2;
					if (paramString3 != null) {
						c = paramString3;
					} else {
						Logs.log(Logs.LogTypes.debug, "Parameter1 is null");
					}
				} else {
					Logs.log(Logs.LogTypes.debug,
							"Parameter1 and Parameter2 are null");
				}
			} else {
				Logs.log(Logs.LogTypes.debug, "Command Value is null");
				throw new IllegalArgumentCheckedException(
						"value parameter is null, command can not be created");
			}
		}

		public String a() {
			String str = "";
			if (c().equals("") == true) {
				str = c();
			} else if (d().equals("") == true) {
				str = c();
			} else {
				str = c() + " " + d();
			}
			return str;
		}

		public String b() {
			return a;
		}

		public String c() {
			return b;
		}

		public String d() {
			return c;
		}
	}
}
