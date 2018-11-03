package ch.leica.sdk.commands.response;

import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.commands.MeasuredValue;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.util.ArrayList;
import java.util.List;

public class Response
{
  Types.Commands a;
  Boolean b = Boolean.valueOf(false);
  ErrorObject c;
  List<ReceivedData> d;
  private String e = "";
  
  public Response(Types.Commands paramCommands)
  {
    a = paramCommands;
    d = new ArrayList();
  }
  
  public void addReceivedData(ReceivedData paramReceivedData)
  {
    d.add(paramReceivedData);
  }
  
  public void setDataString(String paramString) {}
  
  public Types.Commands getCommand()
  {
    return a;
  }
  
  public ErrorObject getError()
  {
    return c;
  }
  
  public List<ReceivedData> getReceivedData()
  {
    return d;
  }
  
  public void setError(ErrorObject paramErrorObject)
  {
    c = paramErrorObject;
  }
  
  public void setCommand(Types.Commands paramCommands)
  {
    a = paramCommands;
  }
  
  public void setAngleInclination(float paramFloat)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAngleInclination(float paramFloat, short paramShort)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAngleInclination(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAngleDirection(float paramFloat)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAngleDirection(float paramFloat, short paramShort)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAngleDirection(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAngleHorizontal_Hz(float paramFloat)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAngleHorizontal_Hz(float paramFloat, short paramShort)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAngleHorizontal_Hz(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAngleVertical_Ve(float paramFloat)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAngleVertical_Ve(float paramFloat, short paramShort)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAngleVertical_Ve(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAccelerationSensitivity(short paramShort)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAcceleration_X(short paramShort)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAcceleration_X(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAcceleration_Y(short paramShort)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAcceleration_Y(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAcceleration_Z(short paramShort)
    throws IllegalArgumentCheckedException
  {}
  
  public void setAcceleration_Z(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setMagnetometer_X(short paramShort)
    throws IllegalArgumentCheckedException
  {}
  
  public void setMagnetometer_X(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setMagnetometer_Y(short paramShort)
    throws IllegalArgumentCheckedException
  {}
  
  public void setMagnetometer_Y(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setMagnetometer_Z(short paramShort)
    throws IllegalArgumentCheckedException
  {}
  
  public void setMagnetometer_Z(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setRotation_X(short paramShort)
    throws IllegalArgumentCheckedException
  {}
  
  public void setRotation_X(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setRotation_Y(short paramShort)
    throws IllegalArgumentCheckedException
  {}
  
  public void setRotation_Y(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setRotation_Z(short paramShort)
    throws IllegalArgumentCheckedException
  {}
  
  public void setRotation_Z(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setquaternion_W(float paramFloat)
    throws IllegalArgumentCheckedException
  {}
  
  public void setquaternion_W(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setquaternion_X(float paramFloat)
    throws IllegalArgumentCheckedException
  {}
  
  public void setquaternion_X(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setquaternion_Y(float paramFloat)
    throws IllegalArgumentCheckedException
  {}
  
  public void setquaternion_Y(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setquaternion_Z(float paramFloat)
    throws IllegalArgumentCheckedException
  {}
  
  public void setquaternion_Z(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {}
  
  public void setDistance(float paramFloat)
    throws IllegalArgumentCheckedException
  {}
  
  public void setDistance(float paramFloat, short paramShort)
    throws IllegalArgumentCheckedException
  {}
  
  public void setHorizontalAngleWithTilt_HZ(float paramFloat)
    throws IllegalArgumentCheckedException
  {}
  
  public void setHorizontalAngleWithouthTilt_NI_HZ(float paramFloat)
    throws IllegalArgumentCheckedException
  {}
  
  public void setVerticalAngleWithTilt_V(float paramFloat)
    throws IllegalArgumentCheckedException
  {}
  
  public void setVerticalAngleWithouthTilt_NI_V(float paramFloat)
    throws IllegalArgumentCheckedException
  {}
  
  public void setBatteryStatus(int paramInt) {}
  
  public void setBatteryVoltage(float paramFloat) {}
  
  public void setFace(int paramInt) {}
  
  public void setICross(float paramFloat) {}
  
  public void setILen(float paramFloat) {}
  
  public void setIhz(float paramFloat) {}
  
  public void setIP(String paramString) {}
  
  public void setMacAddress(String paramString) {}
  
  public void setEquipmentNumber(String paramString) {}
  
  public void setSerialNumber(String paramString) {}
  
  public void setSoftwareName(String paramString) {}
  
  public void setSoftwareVersion(String paramString) {}
  
  public void setWifiChannelNumber(int paramInt) {}
  
  public void setWifiESSID(String paramString) {}
  
  public void setWifiFrequency(int paramInt) {}
  
  public void setWifiModuleVersion(String paramString) {}
  
  public void setDeviceType(int paramInt) {}
  
  public void setSensitiveMode(float paramFloat) {}
  
  public void setUserCamLasX(float paramFloat) {}
  
  public void setUserCamLasY(float paramFloat) {}
  
  public void setUserVind(float paramFloat) {}
  
  public void setxCoordinateCrosshair(short paramShort) {}
  
  public void setyCoordinateCrosshair(short paramShort) {}
  
  public void setImageBytes(byte[] paramArrayOfByte) {}
  
  public void setLEDSystemError(int paramInt) {}
  
  public void setLEDWarnings(int paramInt) {}
  
  public void setMotorStatusHorizontalAxis(int paramInt) {}
  
  public void setMotorStatusVerticalAxis(int paramInt) {}
  
  public void setTemperatureBLESensor(float paramFloat) {}
  
  public void setTemperatureDistanceMeasurementSensor_Edm(float paramFloat) {}
  
  public void setTemperatureHorizontalAngleSensor_Hz(float paramFloat) {}
  
  public void setTemperatureVerticalAngleSensor_V(float paramFloat) {}
  
  public void setIsUpdateMode(int paramInt) {}
  
  public void setHorizontalAngleWithInclination(float paramFloat) {}
  
  public void setVerticalAngleWithInclination(float paramFloat) {}
  
  public void setiState(short paramShort) {}
  
  public void validateError(String paramString)
  {
    String str1 = "No data received from the device";
    if (paramString != null)
    {
      e = paramString.trim();
      String[] arrayOfString = paramString.trim().split("@E_");
      if (arrayOfString.length > 1)
      {
        String str2 = arrayOfString[1];
        switch (str2)
        {
        case "FLASH_WR": 
          setError(new ErrorObject(5001, a(5001)));
          break;
        case "CRC": 
          setError(new ErrorObject(5002, a(5002)));
          break;
        case "COM": 
          setError(new ErrorObject(5003, a(5003)));
          break;
        case "OUT_OF_MEMORY": 
          setError(new ErrorObject(5004, a(5004)));
          break;
        case "SETUP": 
          setError(new ErrorObject(5005, a(5005)));
          break;
        case "BUFFER_OVERFLOW": 
          setError(new ErrorObject(5006, a(5006)));
          break;
        case "UNKNOWN_CMD": 
          setError(new ErrorObject(5007, a(5007)));
          break;
        case "FTA_IN_SWD_MODE": 
          setError(new ErrorObject(5110, a(5110)));
          break;
        case "NOT_AVAILABLE": 
          setError(new ErrorObject(5120, a(5120)));
          break;
        case "LOW_BATTERY": 
          setError(new ErrorObject(5130, a(5130)));
          break;
        default: 
          setError(new ErrorObject(5150, paramString));
        }
      }
    }
    else
    {
      setError(new ErrorObject(5152, str1));
    }
  }
  
  private String a(int paramInt)
  {
    return e + " - " + ErrorObject.getErrorDescription(Integer.valueOf(paramInt));
  }
  
  public void waitForData()
  {
    Logs.log(Logs.LogTypes.verbose, " Called.");
    while (b.booleanValue() == true) {
      try
      {
        Thread.sleep(10L);
      }
      catch (InterruptedException localInterruptedException)
      {
        Logs.log(Logs.LogTypes.exception, "Error in thread Sleep.", localInterruptedException);
      }
    }
  }
  
  public void setWaitingForData(Boolean paramBoolean)
  {
    b = paramBoolean;
  }
}
