package ch.leica.sdk.Devices;

import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.ErrorHandling.WrongDataException;
import ch.leica.sdk.commands.Image;
import ch.leica.sdk.commands.MeasurementConverter;
import ch.leica.sdk.commands.ReceivedBleDataPacket;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.ReceivedDataPacket;
import ch.leica.sdk.commands.ReceivedWifiDataPacket;
import ch.leica.sdk.commands.ReceivedYetiDataPacket;
import ch.leica.sdk.commands.ReceivedYetiDataPacket.YetiDistocomData;
import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;

public class ResponseHelper
{
  private float a = -9999.0F;
  private float b = -9999.0F;
  private float c = -9999.0F;
  private String d = "";
  
  public ResponseHelper() {}
  
  void a(ReceivedData paramReceivedData, Response paramResponse)
  {
    if (paramResponse == null)
    {
      Logs.log(Logs.LogTypes.debug, "response is null");
      return;
    }
    if (paramReceivedData == null)
    {
      Logs.log(Logs.LogTypes.debug, "receivedData is null");
      return;
    }
    if (dataPacket == null)
    {
      Logs.log(Logs.LogTypes.debug, "receivedData.dataPackets is null");
      return;
    }
    paramResponse.addReceivedData(paramReceivedData);
    if (dataPacket != null)
    {
      ReceivedWifiDataPacket localReceivedWifiDataPacket = (ReceivedWifiDataPacket)dataPacket;
      paramResponse.setDataString(response);
      try
      {
        Logs.log(Logs.LogTypes.debug, "WifiDataPacket ID: " + dataId);
        String str = "";
        float f = -9999.0F;
        int i = 55537;
        short s = 55537;
        Image localImage = null;
        str = localReceivedWifiDataPacket.getIpAddress();
        if (!str.equals(""))
        {
          paramResponse.setIP(str);
          Logs.log(Logs.LogTypes.debug, "IP_ADDRESS: " + str);
        }
        f = localReceivedWifiDataPacket.gethTime();
        if (f != -9999.0F)
        {
          paramResponse.setDataString(String.valueOf(f));
          Logs.log(Logs.LogTypes.debug, "ID_HTIME: " + f);
        }
        i = localReceivedWifiDataPacket.getDeviceType();
        if (i != 55537)
        {
          paramResponse.setDeviceType(i);
          Logs.log(Logs.LogTypes.debug, "DEVICE_TYPE: " + i);
        }
        str = localReceivedWifiDataPacket.getSerialNumber();
        if (!str.equals(""))
        {
          paramResponse.setSerialNumber(str);
          Logs.log(Logs.LogTypes.debug, "SERIAL_NUMBER: " + str);
        }
        str = localReceivedWifiDataPacket.getSoftwareName();
        if (!str.equals(""))
        {
          paramResponse.setSoftwareName(str);
          Logs.log(Logs.LogTypes.debug, "SOFTWARE_NAME: " + str);
        }
        str = localReceivedWifiDataPacket.getSoftwareVersion();
        if (!str.equals(""))
        {
          paramResponse.setSoftwareVersion(str);
          Logs.log(Logs.LogTypes.debug, "SOFTWARE_VERSION: " + str);
        }
        f = localReceivedWifiDataPacket.getEquipment();
        if (f != -9999.0F)
        {
          paramResponse.setEquipmentNumber(String.valueOf(f));
          Logs.log(Logs.LogTypes.debug, "EQUIPMENT_NUMBER: " + f);
        }
        f = localReceivedWifiDataPacket.getDistance();
        if (f != -9999.0F)
        {
          if (!Float.isNaN(f)) {
            paramResponse.setDistance(f);
          } else {
            paramResponse.setDistance(-9999.0F);
          }
          Logs.log(Logs.LogTypes.debug, "DISTANCE: " + f);
        }
        f = localReceivedWifiDataPacket.getHorizontalAnglewithTilt_hz();
        if (f != -9999.0F)
        {
          if (!Float.isNaN(f)) {
            paramResponse.setHorizontalAngleWithTilt_HZ(f);
          }
          Logs.log(Logs.LogTypes.debug, "HZ: " + f);
        }
        f = localReceivedWifiDataPacket.getVerticalAngleWithTilt_v();
        if (f != -9999.0F)
        {
          if (!Float.isNaN(f)) {
            paramResponse.setVerticalAngleWithTilt_V(f);
          }
          Logs.log(Logs.LogTypes.debug, "V: " + f);
        }
        f = localReceivedWifiDataPacket.getHorizontalAngleWithoutTilt_ni_hz();
        if (f != -9999.0F)
        {
          if (!Float.isNaN(f)) {
            paramResponse.setHorizontalAngleWithouthTilt_NI_HZ(f);
          } else {
            paramResponse.setHorizontalAngleWithouthTilt_NI_HZ(-9999.0F);
          }
          Logs.log(Logs.LogTypes.debug, "NI_HZ: " + f);
        }
        f = localReceivedWifiDataPacket.getVerticalAngleWithoutTilt_ni_v();
        if (f != -9999.0F)
        {
          if (!Float.isNaN(f)) {
            paramResponse.setVerticalAngleWithouthTilt_NI_V(f);
          } else {
            paramResponse.setVerticalAngleWithouthTilt_NI_V(-9999.0F);
          }
          Logs.log(Logs.LogTypes.debug, "NI_V: " + f);
        }
        i = localReceivedWifiDataPacket.getFace();
        if (i != 55537)
        {
          paramResponse.setFace(i);
          Logs.log(Logs.LogTypes.debug, "FACE: " + i);
        }
        i = localReceivedWifiDataPacket.getMotWhile();
        if (i != 55537)
        {
          paramResponse.setDataString(String.valueOf(i));
          Logs.log(Logs.LogTypes.debug, "MOT_WHILE: " + i);
        }
        f = localReceivedWifiDataPacket.getHz_temp();
        if (f != -9999.0F)
        {
          paramResponse.setTemperatureHorizontalAngleSensor_Hz(f);
          Logs.log(Logs.LogTypes.debug, "HZ-TEMP: " + f);
        }
        f = localReceivedWifiDataPacket.getV_temp();
        if (f != -9999.0F)
        {
          paramResponse.setTemperatureVerticalAngleSensor_V(f);
          Logs.log(Logs.LogTypes.debug, "V-TEMP: " + f);
        }
        f = localReceivedWifiDataPacket.getEdm_temp();
        if (f != -9999.0F)
        {
          paramResponse.setTemperatureDistanceMeasurementSensor_Edm(f);
          Logs.log(Logs.LogTypes.debug, "EDM_TEMP" + f);
        }
        f = localReceivedWifiDataPacket.getBle_temp();
        if (f != -9999.0F)
        {
          paramResponse.setTemperatureBLESensor(f);
          Logs.log(Logs.LogTypes.debug, "BLE-TEMP: " + f);
        }
        f = localReceivedWifiDataPacket.getBat_v();
        if (f != -9999.0F)
        {
          paramResponse.setBatteryVoltage(f);
          Logs.log(Logs.LogTypes.debug, "BAT_V: " + f);
        }
        i = localReceivedWifiDataPacket.getBat_s();
        if (i != 55537)
        {
          paramResponse.setBatteryStatus(i);
          Logs.log(Logs.LogTypes.debug, "BAT_S: " + i);
        }
        i = localReceivedWifiDataPacket.getLedSE();
        if (i != 55537)
        {
          paramResponse.setLEDSystemError(i);
          Logs.log(Logs.LogTypes.debug, "LED_SE: " + i);
        }
        i = localReceivedWifiDataPacket.getLedW();
        if (i != 55537)
        {
          paramResponse.setLEDWarnings(i);
          Logs.log(Logs.LogTypes.debug, "LED_W: " + i);
        }
        f = localReceivedWifiDataPacket.getiHz();
        if (f != -9999.0F)
        {
          paramResponse.setIhz(f);
          Logs.log(Logs.LogTypes.debug, "IHZ: " + f);
        }
        f = localReceivedWifiDataPacket.getiLen();
        if (f != -9999.0F)
        {
          paramResponse.setILen(f);
          Logs.log(Logs.LogTypes.debug, "ILEN: " + f);
        }
        f = localReceivedWifiDataPacket.getiCross();
        if (f != -9999.0F)
        {
          paramResponse.setICross(f);
          Logs.log(Logs.LogTypes.debug, "ICROSS: " + f);
        }
        s = localReceivedWifiDataPacket.getLevel_iState();
        if (s != 55537)
        {
          paramResponse.setiState(s);
          Logs.log(Logs.LogTypes.debug, "ISTATE: " + s);
        }
        str = localReceivedWifiDataPacket.getMac();
        if (!str.equals(""))
        {
          paramResponse.setMacAddress(str);
          Logs.log(Logs.LogTypes.debug, "WLAN_MAC: " + str);
        }
        str = localReceivedWifiDataPacket.getWlanVersions();
        if (!str.equals(""))
        {
          paramResponse.setWifiModuleVersion(str);
          Logs.log(Logs.LogTypes.debug, "WLAN_VERSIONS: " + str);
        }
        i = localReceivedWifiDataPacket.getWlanCH();
        if (i != 55537)
        {
          paramResponse.setWifiChannelNumber(i);
          Logs.log(Logs.LogTypes.debug, "WLAN_CH: " + i);
        }
        i = localReceivedWifiDataPacket.getWlanFreq();
        if (i != 55537)
        {
          paramResponse.setWifiFrequency(i);
          Logs.log(Logs.LogTypes.debug, "WLAN_FREQ: " + str);
        }
        str = localReceivedWifiDataPacket.getWlanESSID();
        if (!str.equals(""))
        {
          paramResponse.setWifiESSID(str);
          Logs.log(Logs.LogTypes.debug, "WLAN_ESSID: " + str);
        }
        localImage = localReceivedWifiDataPacket.getImage();
        if (localImage != null)
        {
          Logs.log(Logs.LogTypes.debug, "Set data in image response object");
          paramResponse.setxCoordinateCrosshair(localImage.getxCoordinateCrosshair());
          paramResponse.setyCoordinateCrosshair(localImage.getyCoordinateCrosshair());
          paramResponse.setImageBytes(localImage.getImageBytes());
        }
        f = localReceivedWifiDataPacket.getUsr_vind();
        if (f != -9999.0F)
        {
          paramResponse.setUserVind(f);
          Logs.log(Logs.LogTypes.debug, "USR_VIND: " + f);
        }
        f = localReceivedWifiDataPacket.getiSensitiveMode();
        if (f != -9999.0F)
        {
          paramResponse.setSensitiveMode(f);
          Logs.log(Logs.LogTypes.debug, "ISENSITIVEMODE: " + f);
        }
        f = localReceivedWifiDataPacket.getUser_camlasX();
        if (f != -9999.0F)
        {
          paramResponse.setUserCamLasX(f);
          Logs.log(Logs.LogTypes.debug, "CAMLAS_X: " + f);
        }
        f = localReceivedWifiDataPacket.getUser_camlasY();
        if (f != -9999.0F)
        {
          paramResponse.setUserCamLasY(f);
          Logs.log(Logs.LogTypes.debug, "CAMLAS_Y: " + f);
        }
        i = localReceivedWifiDataPacket.getMotorStatusH();
        if (i != 55537)
        {
          paramResponse.setMotorStatusHorizontalAxis(i);
          Logs.log(Logs.LogTypes.debug, "MotorStatusH: " + i);
        }
        i = localReceivedWifiDataPacket.getMotorStatusV();
        if (i != 55537)
        {
          paramResponse.setMotorStatusVerticalAxis(i);
          Logs.log(Logs.LogTypes.debug, "MotorStatusV: " + i);
        }
      }
      catch (IllegalArgumentCheckedException localIllegalArgumentCheckedException)
      {
        Logs.log(Logs.LogTypes.exception, "Error caused by: ", localIllegalArgumentCheckedException);
      }
      catch (WrongDataException localWrongDataException)
      {
        Logs.log(Logs.LogTypes.exception, "Invalid Received Data. Error caused by: ", localWrongDataException);
      }
    }
  }
  
  void b(ReceivedData paramReceivedData, Response paramResponse)
  {
    if (paramResponse == null)
    {
      Logs.log(Logs.LogTypes.debug, "response is null");
      return;
    }
    if (paramReceivedData == null)
    {
      Logs.log(Logs.LogTypes.debug, "receivedData is null");
      return;
    }
    if (dataPacket == null)
    {
      Logs.log(Logs.LogTypes.debug, "receivedData.dataPackets is null");
      return;
    }
    paramResponse.addReceivedData(paramReceivedData);
    Logs.log(Logs.LogTypes.debug, "ReceivedData Added: (ble) " + dataPacket.toString());
    if (dataPacket != null) {
      try
      {
        String str1 = dataPacket.dataId;
        ReceivedBleDataPacket localReceivedBleDataPacket;
        Object localObject;
        switch (str1)
        {
        case "DS_MODEL_NAME": 
          localReceivedBleDataPacket = (ReceivedBleDataPacket)dataPacket;
          localObject = localReceivedBleDataPacket.getModelName();
          paramResponse.setDataString((String)localObject);
          break;
        case "DI_MODEL_NUMBER": 
          localReceivedBleDataPacket = (ReceivedBleDataPacket)dataPacket;
          localObject = localReceivedBleDataPacket.getModelName();
          paramResponse.setDataString((String)localObject);
          break;
        case "DI_FIRMWARE_REVISION": 
          localReceivedBleDataPacket = (ReceivedBleDataPacket)dataPacket;
          localObject = localReceivedBleDataPacket.getFirmwareRevision();
          paramResponse.setDataString((String)localObject);
          break;
        case "DI_HARDWARE_REVISION": 
          localReceivedBleDataPacket = (ReceivedBleDataPacket)dataPacket;
          localObject = localReceivedBleDataPacket.getHardwareRevision();
          paramResponse.setDataString((String)localObject);
          break;
        case "DI_MANUFACTURER_NAME_STRING": 
          localReceivedBleDataPacket = (ReceivedBleDataPacket)dataPacket;
          localObject = localReceivedBleDataPacket.getManufacturerNameString();
          paramResponse.setDataString((String)localObject);
          break;
        case "DI_SERIAL_NUMBER": 
          localReceivedBleDataPacket = (ReceivedBleDataPacket)dataPacket;
          localObject = localReceivedBleDataPacket.getSerialNumber();
          paramResponse.setDataString((String)localObject);
          break;
        case "DS_DISTANCE": 
          localReceivedBleDataPacket = (ReceivedBleDataPacket)dataPacket;
          localObject = Float.valueOf(localReceivedBleDataPacket.getDistance());
          a = ((Float)localObject).floatValue();
          Logs.log(Logs.LogTypes.debug, "called with id: " + str1 + ", value: " + localObject);
          break;
        case "DS_DISTANCE_UNIT": 
          localReceivedBleDataPacket = (ReceivedBleDataPacket)dataPacket;
          short s1 = localReceivedBleDataPacket.getDistanceUnit();
          Logs.log(Logs.LogTypes.debug, "called with id: " + str1 + ", value: " + s1);
          if (a != -9999.0F)
          {
            paramResponse.setDistance(a, s1);
          }
          else
          {
            Logs.log(Logs.LogTypes.debug, "An error ocurred there is no distance value to be converted");
            paramResponse.setError(new ErrorObject(3506, "The Unit was received before the command"));
          }
          break;
        case "DS_INCLINATION": 
          localReceivedBleDataPacket = (ReceivedBleDataPacket)dataPacket;
          Float localFloat1 = Float.valueOf(localReceivedBleDataPacket.getInclination());
          b = localFloat1.floatValue();
          Logs.log(Logs.LogTypes.debug, "called with id: " + str1 + ", value: " + localFloat1);
          break;
        case "DS_INCLINATION_UNIT": 
          localReceivedBleDataPacket = (ReceivedBleDataPacket)dataPacket;
          short s2 = localReceivedBleDataPacket.getInclinationUnit();
          if (b != -9999.0F)
          {
            paramResponse.setAngleInclination(b, s2);
          }
          else
          {
            Logs.log(Logs.LogTypes.debug, "An error ocurred there is no inclination angle value to be converted");
            paramResponse.setError(new ErrorObject(3506, "The Unit was received before the command"));
          }
          break;
        case "DS_DIRECTION": 
          localReceivedBleDataPacket = (ReceivedBleDataPacket)dataPacket;
          Float localFloat2 = Float.valueOf(localReceivedBleDataPacket.getDirection());
          c = localFloat2.floatValue();
          paramResponse.setAngleDirection(c, MeasurementConverter.getDefaultDirectionAngleUnit());
          Logs.log(Logs.LogTypes.debug, "called with id: " + str1 + ", value: " + localFloat2);
          break;
        case "DS_DIRECTION_UNIT": 
          localReceivedBleDataPacket = (ReceivedBleDataPacket)dataPacket;
          short s3 = localReceivedBleDataPacket.getDirectionUnit();
          if (c != -9999.0F)
          {
            paramResponse.setAngleDirection(c, s3);
          }
          else
          {
            Logs.log(Logs.LogTypes.debug, "An error ocurred there is no inclination angle value to be converted");
            paramResponse.setError(new ErrorObject(3506, "The Unit was received before the command"));
          }
          break;
        }
      }
      catch (Exception localException)
      {
        Logs.log(Logs.LogTypes.exception, "An error ocurred creating the response", localException);
      }
    }
  }
  
  void c(ReceivedData paramReceivedData, Response paramResponse)
  {
    if (paramResponse == null)
    {
      Logs.log(Logs.LogTypes.debug, "response is null");
      return;
    }
    if (paramReceivedData == null)
    {
      Logs.log(Logs.LogTypes.debug, "receivedData is null");
      return;
    }
    if (dataPacket == null)
    {
      Logs.log(Logs.LogTypes.debug, "receivedData.dataPackets is null");
      return;
    }
    paramResponse.addReceivedData(paramReceivedData);
    Logs.log(Logs.LogTypes.debug, "ReceivedData Added: (Yeti) " + dataPacket.toString());
    if (dataPacket != null) {
      try
      {
        ReceivedYetiDataPacket localReceivedYetiDataPacket = (ReceivedYetiDataPacket)dataPacket;
        if ((dataId.equals("IMU_DISTOCOM_TRANSMIT")) || (dataId.equals("IMU_DISTOCOM_EVENT")))
        {
          ReceivedYetiDataPacket.YetiDistocomData localYetiDistocomData = localReceivedYetiDataPacket.getDistocom();
          String str1 = dataId;
          Logs.log(Logs.LogTypes.debug, "YetiDataPacket with id: " + str1);
          switch (str1)
          {
          case "IS_UPDATE_MODE": 
            int j = localYetiDistocomData.getInSWDMode();
            paramResponse.setIsUpdateMode(j);
            Logs.log(Logs.LogTypes.debug, "called with id: " + str1 + ", value: " + j);
          }
          d += localYetiDistocomData.getRawString();
          paramResponse.setDataString(d);
        }
        else
        {
          Logs.log(Logs.LogTypes.debug, "An error ocurred there is no valid distocom Response");
          paramResponse.setError(new ErrorObject(2702, "Error received from device. "));
        }
      }
      catch (Exception localException)
      {
        Logs.log(Logs.LogTypes.exception, "Error caused by: ", localException);
      }
    }
  }
  
  public void setDistocomData(String paramString)
  {
    d = paramString;
  }
}
