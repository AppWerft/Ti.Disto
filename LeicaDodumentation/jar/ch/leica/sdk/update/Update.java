package ch.leica.sdk.update;

import android.content.Context;
import ch.leica.sdk.Devices.Device.ConnectionState;
import ch.leica.sdk.Devices.Device.DeviceState;
import ch.leica.sdk.Devices.Device.UpdateDeviceListener;
import ch.leica.sdk.Devices.YetiDevice;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.ReceivedYetiDataPacket;
import ch.leica.sdk.commands.response.ResponsePlain;
import ch.leica.sdk.commands.response.ResponseUpdate;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareBinary;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareComponent;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareComponentVersion;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareProduct;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareProductVersion;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareUpdate;
import ch.leica.sdk.update.FirmwareUpdate.FirmwareDownloader;
import ch.leica.sdk.update.FirmwareUpdate.FirmwareDownloader.FirmwareProductCallback;
import ch.leica.sdk.update.FirmwareUpdate.FirmwareDownloader.FirmwareUpdateCallback;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class Update
{
  private Context a;
  private FirmwareDownloader b;
  private String c = "";
  private String d = "";
  private String e = "";
  private String f = "";
  private String g = "";
  private String h = "";
  public FirmwareUpdate firmwareUpdate = null;
  public FirmwareProduct firmwareProduct = null;
  public UpdateFirmwareDeviceHelper updateFirmwareDeviceHelper;
  
  public Update(Context paramContext)
  {
    a = paramContext;
    updateFirmwareDeviceHelper = new UpdateFirmwareDeviceHelper();
  }
  
  private void a(ResponseUpdate paramResponseUpdate)
  {
    List localList = paramResponseUpdate.getReceivedData();
    if ((localList == null) || (localList.size() < 1)) {
      return;
    }
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      ReceivedData localReceivedData = (ReceivedData)localIterator.next();
      if (dataPacket.getClass().isInstance(ReceivedYetiDataPacket.class)) {
        paramResponseUpdate.validateError(((ReceivedYetiDataPacket)dataPacket).getDistocomReceivedMessage());
      }
    }
  }
  
  private String a(FirmwareProduct paramFirmwareProduct)
  {
    return "fwProduct.BrandIdentifier: " + paramFirmwareProduct.getBrandIdentifier() + "\nfwProduct.getBrandName: " + paramFirmwareProduct.getBrandName() + "\nfwProduct.getIdentifier: " + paramFirmwareProduct.getIdentifier() + "\nfwProduct.getName: " + paramFirmwareProduct.getName() + "\nfwProduct.getVersionCommand: " + paramFirmwareProduct.getVersionCommand();
  }
  
  public ResponsePlain getValue(String paramString, YetiDevice paramYetiDevice)
  {
    ResponsePlain localResponsePlain = new ResponsePlain(Types.Commands.Custom);
    Object localObject;
    if ((paramString == null) || (paramString.isEmpty()))
    {
      localObject = new ErrorObject(7030, "No value was retrieved from the device.");
      localResponsePlain.setError((ErrorObject)localObject);
      return localResponsePlain;
    }
    try
    {
      localResponsePlain = (ResponsePlain)paramYetiDevice.sendCustomCommand(paramString, paramYetiDevice.getTIMEOUT_NORMAL());
      localResponsePlain.waitForData();
      localResponsePlain.validateError(localResponsePlain.getReceivedDataString());
      if (localResponsePlain.getError() != null)
      {
        Logs.log(Logs.LogTypes.debug, " Additional Info: Command: " + paramString);
        return localResponsePlain;
      }
      localObject = localResponsePlain.getResponseSegments(":");
      localResponsePlain.setDataString(localObject[(localObject.length - 1)]);
      if (localResponsePlain.getReceivedDataString().isEmpty())
      {
        localResponsePlain.setError(new ErrorObject(7030, "No value was retrieved from the device."));
        return localResponsePlain;
      }
    }
    catch (DeviceException localDeviceException)
    {
      Logs.log(Logs.LogTypes.debug, " Additional Info: Command: " + paramString + "Exception: " + localDeviceException.getMessage());
      localResponsePlain.setError(new ErrorObject(17005, localDeviceException.getMessage()));
    }
    return localResponsePlain;
  }
  
  private FirmwareUpdate a(YetiDevice paramYetiDevice, FirmwareProduct paramFirmwareProduct, FirmwareUpdate paramFirmwareUpdate)
    throws DeviceException
  {
    return a(paramYetiDevice, paramFirmwareProduct, paramFirmwareUpdate, "next");
  }
  
  private FirmwareUpdate b(YetiDevice paramYetiDevice, FirmwareProduct paramFirmwareProduct, FirmwareUpdate paramFirmwareUpdate)
    throws DeviceException
  {
    return a(paramYetiDevice, paramFirmwareProduct, paramFirmwareUpdate, "current");
  }
  
  private FirmwareUpdate a(YetiDevice paramYetiDevice, FirmwareProduct paramFirmwareProduct, FirmwareUpdate paramFirmwareUpdate, String paramString)
    throws DeviceException
  {
    final CountDownLatch localCountDownLatch = new CountDownLatch(1);
    int i = 0;
    ArrayList localArrayList = new ArrayList();
    if (firmwareUpdate == null)
    {
      Logs.log(Logs.LogTypes.codeerror, "This should not happen.");
      return null;
    }
    if ((paramFirmwareProduct.getComponents() == null) || (paramFirmwareProduct.getComponents().size() < 1)) {
      return firmwareUpdate;
    }
    Object localObject = paramFirmwareProduct.getComponents().iterator();
    while (((Iterator)localObject).hasNext())
    {
      FirmwareComponent localFirmwareComponent1 = (FirmwareComponent)((Iterator)localObject).next();
      if (!localArrayList.contains(localFirmwareComponent1.getIdentifier()))
      {
        localArrayList.add(localFirmwareComponent1.getIdentifier());
        FirmwareComponentVersion localFirmwareComponentVersion = null;
        int j = 0;
        int k = 0;
        ResponsePlain localResponsePlain1 = getValue(localFirmwareComponent1.getSerialCommand(), paramYetiDevice);
        if (localResponsePlain1 != null)
        {
          if (localResponsePlain1.getError() == null)
          {
            g = localResponsePlain1.getReceivedDataString();
          }
          else if (localResponsePlain1.getError().getErrorCode() == 5110)
          {
            k = 1;
            Logs.log(Logs.LogTypes.debug, "Component Update: No valid serial number found. ");
          }
          else
          {
            firmwareUpdate.errors.add(localResponsePlain1.getError());
            Logs.log(Logs.LogTypes.debug, "ERROR CODE: " + localResponsePlain1.getError().getErrorCode() + "ERROR MESSAGE: " + localResponsePlain1.getError().getErrorMessage());
          }
        }
        else
        {
          firmwareUpdate.errors.add(new ErrorObject(7602, "Component Serial Response is null"));
          continue;
        }
        ResponsePlain localResponsePlain2 = getValue(localFirmwareComponent1.getVersionCommand(), paramYetiDevice);
        if (localResponsePlain2 != null)
        {
          if (localResponsePlain2.getError() == null)
          {
            h = localResponsePlain2.getReceivedDataString();
          }
          else if (localResponsePlain2.getError().getErrorCode() == 5120)
          {
            j = 1;
            Logs.log(Logs.LogTypes.debug, "Component Update: There was No Version found but a valid serial, the las valid version is going to be used.");
          }
          else
          {
            firmwareUpdate.errors.add(localResponsePlain2.getError());
            Logs.log(Logs.LogTypes.debug, "ERROR CODE: " + localResponsePlain2.getError().getErrorCode() + "ERROR MESSAGE: " + localResponsePlain2.getError().getErrorMessage());
          }
        }
        else
        {
          firmwareUpdate.errors.add(new ErrorObject(7604, "Component Serial Response is null"));
          continue;
        }
        FirmwareComponent localFirmwareComponent2 = null;
        if (k == 1)
        {
          localFirmwareComponent2 = paramFirmwareProduct.getFittingComponentNoSerial(localFirmwareComponent1);
          localFirmwareComponentVersion = localFirmwareComponent2.getLastAvailableComponentVersion();
        }
        else
        {
          Logs.log(Logs.LogTypes.debug, " --- now looking for fitting component");
          localFirmwareComponent2 = paramFirmwareProduct.getFittingComponentWithSerial(localFirmwareComponent1, g);
          if (localFirmwareComponent2 == null)
          {
            Logs.log(Logs.LogTypes.debug, " --- error. no fitting component found");
            firmwareUpdate.errors.add(new ErrorObject(7610, "No fitting component found. "));
            continue;
          }
          if (j == 1)
          {
            localFirmwareComponentVersion = localFirmwareComponent2.getLastAvailableComponentVersion();
          }
          else if ("current".equalsIgnoreCase(paramString))
          {
            Logs.log(Logs.LogTypes.debug, " --- now looking for current version");
            localFirmwareComponentVersion = localFirmwareComponent2.getCurrentComponentVersion(h);
          }
          else if ("next".equalsIgnoreCase(paramString))
          {
            Logs.log(Logs.LogTypes.debug, "--- now looking for next version");
            localFirmwareComponentVersion = localFirmwareComponent2.getNextComponentVersion(h);
          }
        }
        if (localFirmwareComponentVersion == null)
        {
          firmwareUpdate.errors.add(new ErrorObject(7612, "No Last Version was found for the component, continue with the next component."));
          Logs.log(Logs.LogTypes.debug, "--- error. no version found for next version.");
        }
        else
        {
          i = 1;
          b.getAvailableFirmwareUpdatesForComponents(localFirmwareComponent2, localFirmwareComponentVersion, paramFirmwareUpdate, new FirmwareDownloader.FirmwareUpdateCallback()
          {
            public void firmwareUpdateResult(FirmwareUpdate paramAnonymousFirmwareUpdate, ErrorObject paramAnonymousErrorObject)
            {
              int i = 0;
              if (paramAnonymousErrorObject != null)
              {
                firmwareUpdate = new FirmwareUpdate(firmwareProduct.getBrandIdentifier(), firmwareProduct.getIdentifier(), null);
                firmwareUpdate.errors.add(paramAnonymousErrorObject);
                localCountDownLatch.countDown();
              }
              else
              {
                if ((paramAnonymousFirmwareUpdate.getBinaries() != null) && (!paramAnonymousFirmwareUpdate.getBinaries().isEmpty()))
                {
                  i = 1;
                }
                else if (paramAnonymousFirmwareUpdate.getComponents() != null)
                {
                  Iterator localIterator = paramAnonymousFirmwareUpdate.getComponents().iterator();
                  while (localIterator.hasNext())
                  {
                    FirmwareComponent localFirmwareComponent = (FirmwareComponent)localIterator.next();
                    if ((localFirmwareComponent.getBinaries() != null) && (!localFirmwareComponent.getBinaries().isEmpty())) {
                      i = 1;
                    }
                  }
                }
                if (i == 1)
                {
                  Logs.log(Logs.LogTypes.debug, "firmware Components");
                  firmwareUpdate = paramAnonymousFirmwareUpdate;
                  Logs.log(Logs.LogTypes.debug, "firmwareUpdateLatch Countdown.");
                  localCountDownLatch.countDown();
                }
                else
                {
                  firmwareUpdate = null;
                  Logs.log(Logs.LogTypes.debug, "There are no App, neither component binaries to update.");
                  Logs.log(Logs.LogTypes.debug, "firmwareUpdateLatch Countdown.");
                  localCountDownLatch.countDown();
                }
              }
            }
          });
        }
      }
    }
    if (i == 0) {
      localCountDownLatch.countDown();
    }
    localObject = a(localCountDownLatch);
    if (localObject == null) {
      return firmwareUpdate;
    }
    firmwareUpdate = new FirmwareUpdate();
    firmwareUpdate.errors.add(localObject);
    return firmwareUpdate;
  }
  
  public synchronized FirmwareUpdate getAvailableFirmwareUpdateForDevice(YetiDevice paramYetiDevice)
    throws DeviceException
  {
    String str = "next";
    return a(paramYetiDevice, str);
  }
  
  public synchronized FirmwareUpdate getCurrentFirmwareUpdateForDevice(YetiDevice paramYetiDevice)
    throws DeviceException
  {
    String str = "current";
    return a(paramYetiDevice, str);
  }
  
  private synchronized FirmwareUpdate a(final YetiDevice paramYetiDevice, final String paramString)
    throws DeviceException
  {
    String str1 = "getBrand";
    String str2 = "getID";
    String str3 = "getSerial app";
    c = "";
    d = "";
    e = "";
    final CountDownLatch localCountDownLatch = new CountDownLatch(1);
    b = new FirmwareDownloader(a);
    ResponsePlain localResponsePlain1 = getValue(str1, paramYetiDevice);
    if ((localResponsePlain1 != null) && (localResponsePlain1.getError() == null))
    {
      c = localResponsePlain1.getReceivedDataString();
    }
    else
    {
      firmwareUpdate = new FirmwareUpdate();
      firmwareUpdate.errors.add(new ErrorObject(7622, "No productBrand received from DISTO Device"));
      return firmwareUpdate;
    }
    ResponsePlain localResponsePlain2 = getValue(str2, paramYetiDevice);
    if ((localResponsePlain2 != null) && (localResponsePlain2.getError() == null))
    {
      d = localResponsePlain2.getReceivedDataString();
    }
    else
    {
      firmwareUpdate = new FirmwareUpdate();
      firmwareUpdate.errors.add(new ErrorObject(7624, "No productId received from DISTO Device"));
      return firmwareUpdate;
    }
    ResponsePlain localResponsePlain3 = getValue(str3, paramYetiDevice);
    if ((localResponsePlain3 != null) && (localResponsePlain3.getError() == null) && (updateFirmwareDeviceHelper.isNumeric(localResponsePlain3.getReceivedDataString())))
    {
      f = localResponsePlain3.getReceivedDataString();
    }
    else
    {
      firmwareUpdate = new FirmwareUpdate(c, d, null);
      firmwareUpdate.errors.add(new ErrorObject(7626, "No Serial received from DISTO Device"));
      return firmwareUpdate;
    }
    b.getFirmwareInformation(c, d, f, new FirmwareDownloader.FirmwareProductCallback()
    {
      public void firmwareProductResult(final FirmwareProduct paramAnonymousFirmwareProduct, ErrorObject paramAnonymousErrorObject)
      {
        if (paramAnonymousErrorObject == null)
        {
          firmwareProduct = paramAnonymousFirmwareProduct;
          Logs.log(Logs.LogTypes.debug, Update.a(Update.this, paramAnonymousFirmwareProduct));
          ResponsePlain localResponsePlain = getValue(paramAnonymousFirmwareProduct.getVersionCommand(), paramYetiDevice);
          if ((localResponsePlain != null) && (localResponsePlain.getError() == null))
          {
            Update.a(Update.this, localResponsePlain.getReceivedDataString());
          }
          else
          {
            firmwareUpdate = new FirmwareUpdate(firmwareProduct.getBrandIdentifier(), Update.a(Update.this), null);
            firmwareUpdate.productInfoJSon = paramAnonymousFirmwareProduct.getProductJSON();
            firmwareUpdate.errors.add(new ErrorObject(7628, "No current Product Version retrieved from DISTO Device"));
            localCountDownLatch.countDown();
          }
          FirmwareProductVersion localFirmwareProductVersion1 = null;
          if ("next".equalsIgnoreCase(paramString)) {
            localFirmwareProductVersion1 = firmwareProduct.getNextProductVersion(Update.b(Update.this));
          } else if ("current".equalsIgnoreCase(paramString)) {
            localFirmwareProductVersion1 = firmwareProduct.getCurrentProductVersion(Update.b(Update.this));
          } else {
            localFirmwareProductVersion1 = null;
          }
          FirmwareProductVersion localFirmwareProductVersion2 = localFirmwareProductVersion1;
          if (localFirmwareProductVersion2 != null)
          {
            Update.c(Update.this).getFirmwareUpdateWithBinaries(paramAnonymousFirmwareProduct, localFirmwareProductVersion2, new FirmwareDownloader.FirmwareUpdateCallback()
            {
              public void firmwareUpdateResult(FirmwareUpdate paramAnonymous2FirmwareUpdate, ErrorObject paramAnonymous2ErrorObject)
              {
                if (paramAnonymous2ErrorObject == null)
                {
                  firmwareUpdate = paramAnonymous2FirmwareUpdate;
                  if (paramAnonymousFirmwareProduct != null) {
                    productInfoJSon = paramAnonymousFirmwareProduct.getProductJSON();
                  }
                }
                else
                {
                  firmwareUpdate = new FirmwareUpdate(firmwareProduct.getBrandIdentifier(), firmwareProduct.getIdentifier(), null);
                  if (paramAnonymousFirmwareProduct != null) {
                    productInfoJSon = paramAnonymousFirmwareProduct.getProductJSON();
                  }
                  firmwareUpdate.errors.add(paramAnonymous2ErrorObject);
                }
                b.countDown();
              }
            });
          }
          else
          {
            firmwareUpdate = new FirmwareUpdate(firmwareProduct.getBrandIdentifier(), Update.a(Update.this), null);
            if (paramAnonymousFirmwareProduct != null) {
              firmwareUpdate.productInfoJSon = paramAnonymousFirmwareProduct.getProductJSON();
            }
            firmwareUpdate.errors.add(firmwareProduct.getErrorObject());
            localCountDownLatch.countDown();
          }
        }
        else
        {
          firmwareUpdate = new FirmwareUpdate();
          if (paramAnonymousFirmwareProduct != null) {
            firmwareUpdate.productInfoJSon = paramAnonymousFirmwareProduct.getProductJSON();
          }
          firmwareUpdate.errors.add(paramAnonymousErrorObject);
          localCountDownLatch.countDown();
        }
      }
    });
    ErrorObject localErrorObject = a(localCountDownLatch);
    if (localErrorObject == null)
    {
      if (firmwareUpdate == null)
      {
        firmwareUpdate = new FirmwareUpdate(e, firmwareProduct.getBrandIdentifier(), firmwareProduct.getName(), firmwareProduct.getIdentifier(), new ArrayList());
        if (firmwareProduct != null) {
          firmwareUpdate.productInfoJSon = firmwareProduct.getProductJSON();
        }
      }
      if (firmwareUpdate.isValid() == true) {
        if ("current".equalsIgnoreCase(paramString))
        {
          firmwareUpdate = b(paramYetiDevice, firmwareProduct, firmwareUpdate);
        }
        else if ("next".equalsIgnoreCase(paramString))
        {
          firmwareUpdate = a(paramYetiDevice, firmwareProduct, firmwareUpdate);
        }
        else
        {
          Logs.log(Logs.LogTypes.codeerror, "This should not happen");
          return null;
        }
      }
    }
    else
    {
      firmwareUpdate = new FirmwareUpdate();
      firmwareUpdate.errors.add(localErrorObject);
      return firmwareUpdate;
    }
    firmwareUpdate.forCurrentVersion = e;
    return firmwareUpdate;
  }
  
  private ErrorObject a(CountDownLatch paramCountDownLatch)
    throws DeviceException
  {
    boolean bool = false;
    int i = 120000;
    try
    {
      bool = paramCountDownLatch.await(i, TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new DeviceException(localInterruptedException.getMessage());
    }
    Logs.log(Logs.LogTypes.verbose, "awaiting: " + i);
    if (!bool)
    {
      Logs.log(Logs.LogTypes.debug, " Unsuccessful Update: Allotted time for Updating Firmware reached.");
      ErrorObject localErrorObject = new ErrorObject(7040, "Allotted time for Updating Firmware reached.");
      return localErrorObject;
    }
    return null;
  }
  
  private ResponseUpdate b(ResponseUpdate paramResponseUpdate)
  {
    boolean bool = paramResponseUpdate.isCallSuccessful();
    if (!bool) {
      if (paramResponseUpdate.getError() != null)
      {
        Logs.log(Logs.LogTypes.debug, "Error code: " + paramResponseUpdate.getError().getErrorCode() + "Error Message: " + paramResponseUpdate.getError().getErrorCode());
      }
      else
      {
        paramResponseUpdate.setError(new ErrorObject(17005, paramResponseUpdate.getDataString()));
        Logs.log(Logs.LogTypes.debug, "Error: " + paramResponseUpdate.getDataString());
      }
    }
    return paramResponseUpdate;
  }
  
  private ResponseUpdate a(FirmwareBinary paramFirmwareBinary, YetiDevice paramYetiDevice, Device.UpdateDeviceListener paramUpdateDeviceListener)
  {
    ResponseUpdate localResponseUpdate = new ResponseUpdate(Types.Commands.Custom);
    byte[] arrayOfByte = null;
    if (paramFirmwareBinary == null)
    {
      Logs.log(Logs.LogTypes.debug, "Error Code: 7102 Message: No binaries were found");
      localResponseUpdate.setError(new ErrorObject(7102, "No binaries were found"));
      return localResponseUpdate;
    }
    if ((paramFirmwareBinary.getCommand() == null) || (paramFirmwareBinary.getCommand().isEmpty() == true))
    {
      Logs.log(Logs.LogTypes.debug, "Error Code: 7105 Message: binary.command or binary.data is null");
      localResponseUpdate.setError(new ErrorObject(7105, "binary.command or binary.data is null"));
      return localResponseUpdate;
    }
    arrayOfByte = paramFirmwareBinary.getData();
    if ((arrayOfByte == null) || (arrayOfByte.length < 1))
    {
      localResponseUpdate.setError(new ErrorObject(7104, "Empty binary"));
      return localResponseUpdate;
    }
    Logs.log(Logs.LogTypes.debug, "binary.Command: " + paramFirmwareBinary.getCommand() + "binary.Offset: " + paramFirmwareBinary.getOffset() + "binary.Length: " + paramFirmwareBinary.getData().length);
    try
    {
      boolean bool = paramYetiDevice.isInUpdateMode();
      if (!bool)
      {
        Logs.log(Logs.LogTypes.debug, "Device is not in update mode.");
        localResponseUpdate = (ResponseUpdate)paramYetiDevice.sendCommand(Types.Commands.UpdateSetFlag, paramYetiDevice.getTIMEOUT_NORMAL());
        localResponseUpdate.waitForData();
        a(localResponseUpdate);
        b(localResponseUpdate);
        if (localResponseUpdate.getError() != null) {
          return localResponseUpdate;
        }
        localResponseUpdate = (ResponseUpdate)paramYetiDevice.sendCommand(Types.Commands.ResetDevice, paramYetiDevice.getTIMEOUT_NORMAL());
        localResponseUpdate.waitForData();
        a(localResponseUpdate);
        if (localResponseUpdate.getError() != null) {
          return localResponseUpdate;
        }
        try
        {
          Thread.sleep(500L);
        }
        catch (InterruptedException localInterruptedException)
        {
          localInterruptedException.printStackTrace();
        }
      }
      else
      {
        Logs.log(Logs.LogTypes.debug, " --- now sending getUpdateStatus and wait for data");
        localObject1 = (ResponsePlain)paramYetiDevice.sendCustomCommand("getUpdateStatus", paramYetiDevice.getTIMEOUT_NORMAL());
        ((ResponsePlain)localObject1).waitForData();
        ((ResponsePlain)localObject1).validateError(((ResponsePlain)localObject1).getReceivedDataString());
        Logs.log(Logs.LogTypes.debug, " --- finished wait for data for getUpdateStatus.");
        if (((ResponsePlain)localObject1).getError() != null)
        {
          Logs.log(Logs.LogTypes.debug, " --- error received for getUpdateStatus: ");
          localResponseUpdate.setError(((ResponsePlain)localObject1).getError());
          return localResponseUpdate;
        }
        Logs.log(Logs.LogTypes.debug, "--- getUpdateStatus response: " + ((ResponsePlain)localObject1).getReceivedDataString());
        localObject2 = ((ResponsePlain)localObject1).getResponseSegments(":");
        if ((!"getUpdateStatus".equals(localObject2[0])) || (!"!".equals(localObject2[1]))) {
          localResponseUpdate.setError(new ErrorObject(7210, "Wrong Update Status"));
        }
      }
      Object localObject1 = new UpdateDataHelper(arrayOfByte);
      ((UpdateDataHelper)localObject1).setOffset(paramFirmwareBinary.getOffset());
      Object localObject2 = Integer.toHexString(((UpdateDataHelper)localObject1).getRealFilesize()).toUpperCase();
      String str1 = "update start_" + paramFirmwareBinary.getCommand() + " " + (String)localObject2;
      ResponsePlain localResponsePlain = (ResponsePlain)paramYetiDevice.sendCustomCommand(str1, paramYetiDevice.getTIMEOUT_NORMAL());
      localResponsePlain.waitForData();
      localResponsePlain.validateError(localResponsePlain.getReceivedDataString());
      if (localResponsePlain.getError() != null)
      {
        localResponseUpdate.setError(localResponsePlain.getError());
        return localResponseUpdate;
      }
      String[] arrayOfString = localResponsePlain.getResponseSegments(":");
      if (!"!".equals(arrayOfString[1]))
      {
        localResponseUpdate.setError(new ErrorObject(7201, "start_Update command not returned successful Start Update Failed. "));
        return localResponseUpdate;
      }
      String str2;
      if (arrayOfString[2] != null)
      {
        str2 = arrayOfString[2];
      }
      else
      {
        localResponseUpdate.setError(new ErrorObject(7201, "start_Update command not returned successful Blocksize is null. "));
        return localResponseUpdate;
      }
      int i = -1;
      try
      {
        i = Integer.parseInt(str2, 16);
        Logs.log(Logs.LogTypes.debug, " Blocksize String: " + str2 + "Blocksize Integer: " + i);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        localResponseUpdate.setError(new ErrorObject(7220, "Error generating Hex Value"));
        return localResponseUpdate;
      }
      ((UpdateDataHelper)localObject1).setBlockSize(i);
      UpdateDataHelper.UpdateData localUpdateData = ((UpdateDataHelper)localObject1).getNextData();
      long l1 = ((UpdateDataHelper)localObject1).getRealFilesize();
      long l2 = 0L;
      while (localUpdateData != null)
      {
        localResponseUpdate = a(localUpdateData, paramYetiDevice);
        if (localResponseUpdate.getError() != null) {
          return localResponseUpdate;
        }
        if (paramUpdateDeviceListener != null) {
          paramUpdateDeviceListener.onProgress(l2, l1);
        }
        Logs.log(Logs.LogTypes.debug, "packet send successfully, offset: " + localUpdateData.getOffsetStr());
        l2 += localUpdateData.getBlock().length;
        localUpdateData = ((UpdateDataHelper)localObject1).getNextData();
      }
      String str3 = ((UpdateDataHelper)localObject1).getCrcWholeFileData();
      Logs.log(Logs.LogTypes.debug, "whole CRC Str: " + str3);
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(str3);
      localResponseUpdate = (ResponseUpdate)paramYetiDevice.sendCommand(Types.Commands.UpdateChecksum, paramYetiDevice.getTIMEOUT_NORMAL(), localArrayList);
      localResponseUpdate.waitForData();
      a(localResponseUpdate);
      b(localResponseUpdate);
      if (localResponseUpdate.getError() != null) {
        return localResponseUpdate;
      }
    }
    catch (DeviceException localDeviceException)
    {
      Logs.log(Logs.LogTypes.exception, "Caused by: ", localDeviceException);
      localResponseUpdate.setError(new ErrorObject(7001, localDeviceException.getMessage()));
    }
    return localResponseUpdate;
  }
  
  private ResponseUpdate a(UpdateDataHelper.UpdateData paramUpdateData, YetiDevice paramYetiDevice)
    throws DeviceException
  {
    String str1 = paramUpdateData.getOffsetStr();
    String str2 = paramUpdateData.getCrcStr();
    String str3 = paramUpdateData.getDataLengthStr();
    Logs.log(Logs.LogTypes.debug, "offsetHEX: " + str1 + " CRCHex: " + str2 + " dataLenghtHex: " + str3);
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(str1);
    localArrayList.add(str2);
    localArrayList.add(str3);
    ResponseUpdate localResponseUpdate = (ResponseUpdate)paramYetiDevice.sendCommand(Types.Commands.UpdateWrite, paramYetiDevice.getTIMEOUT_NORMAL(), localArrayList);
    localResponseUpdate.waitForData();
    b(localResponseUpdate);
    if (localResponseUpdate.getError() != null) {
      return localResponseUpdate;
    }
    localResponseUpdate = (ResponseUpdate)paramYetiDevice.sendCommand(Types.Commands.SendData, paramYetiDevice.getTIMEOUT_LONG(), paramUpdateData.getBlock());
    localResponseUpdate.waitForData();
    b(localResponseUpdate);
    if (localResponseUpdate.getError() != null) {
      return localResponseUpdate;
    }
    return localResponseUpdate;
  }
  
  public ResponseUpdate activate(YetiDevice paramYetiDevice)
  {
    ResponseUpdate localResponseUpdate = new ResponseUpdate(Types.Commands.Custom);
    try
    {
      localResponseUpdate = (ResponseUpdate)paramYetiDevice.sendCommand(Types.Commands.UpdateActivate, paramYetiDevice.getTIMEOUT_NORMAL());
    }
    catch (DeviceException localDeviceException)
    {
      localResponseUpdate.setError(new ErrorObject(17005, localDeviceException.getMessage()));
      return localResponseUpdate;
    }
    localResponseUpdate.waitForData();
    b(localResponseUpdate);
    if (localResponseUpdate.getError() != null) {
      return localResponseUpdate;
    }
    Logs.log(Logs.LogTypes.debug, "Update was Successful!");
    paramYetiDevice.setCurrentState(Device.DeviceState.normal);
    return localResponseUpdate;
  }
  
  public synchronized ResponseUpdate updateDeviceFirmwares(FirmwareUpdate paramFirmwareUpdate, YetiDevice paramYetiDevice, Device.UpdateDeviceListener paramUpdateDeviceListener)
    throws DeviceException
  {
    ResponseUpdate localResponseUpdate = new ResponseUpdate(Types.Commands.Custom);
    if (paramYetiDevice.getConnectionState() != Device.ConnectionState.connected) {
      throw new DeviceException(new ErrorObject(3002, "Device is not connected. "));
    }
    if (paramFirmwareUpdate == null)
    {
      localResponseUpdate.setError(new ErrorObject(7100, "UpdateFirmware Object is null"));
      return localResponseUpdate;
    }
    if (((paramFirmwareUpdate.getBinaries() == null) || (paramFirmwareUpdate.getBinaries().isEmpty())) && ((paramFirmwareUpdate.getComponents() == null) || (paramFirmwareUpdate.getComponents().isEmpty())))
    {
      localResponseUpdate.setError(new ErrorObject(7102, "No binaries were found"));
      return localResponseUpdate;
    }
    int i = 0;
    Iterator localIterator1;
    Object localObject;
    if ((paramFirmwareUpdate.getBinaries() != null) && (paramFirmwareUpdate.getBinaries().size() > 0))
    {
      Logs.log(Logs.LogTypes.debug, "Number of Device Binaries to Update: " + paramFirmwareUpdate.getBinaries());
      localIterator1 = paramFirmwareUpdate.getBinaries().iterator();
      while (localIterator1.hasNext())
      {
        localObject = (FirmwareBinary)localIterator1.next();
        paramUpdateDeviceListener.onFirmwareUpdateStarted(((FirmwareBinary)localObject).getCommand(), paramFirmwareUpdate.getVersion());
        i = 1;
        localResponseUpdate = a((FirmwareBinary)localObject, paramYetiDevice, paramUpdateDeviceListener);
        if (localResponseUpdate.getError() != null)
        {
          errors.add(localResponseUpdate.getError());
          break;
        }
      }
    }
    if ((paramFirmwareUpdate.getComponents() != null) && (paramFirmwareUpdate.getComponents().size() > 0))
    {
      localIterator1 = paramFirmwareUpdate.getComponents().iterator();
      while (localIterator1.hasNext())
      {
        localObject = (FirmwareComponent)localIterator1.next();
        Logs.log(Logs.LogTypes.debug, "Number of component Binaries to Update: " + ((FirmwareComponent)localObject).getBinaries());
        boolean bool = updateFirmwareDeviceHelper.isComponentConnected(paramYetiDevice, ((FirmwareComponent)localObject).getSerialCommand(), ((FirmwareComponent)localObject).getVersionCommand());
        if (!bool)
        {
          Logs.log(Logs.LogTypes.debug, "Component is not currently connected: " + ((FirmwareComponent)localObject).getBinaries());
        }
        else
        {
          Iterator localIterator2 = ((FirmwareComponent)localObject).getBinaries().iterator();
          while (localIterator2.hasNext())
          {
            FirmwareBinary localFirmwareBinary = (FirmwareBinary)localIterator2.next();
            paramUpdateDeviceListener.onFirmwareUpdateStarted(localFirmwareBinary.getCommand(), ((FirmwareComponent)localObject).getCurrentVersion());
            i = 1;
            localResponseUpdate = a(localFirmwareBinary, paramYetiDevice, paramUpdateDeviceListener);
            if (localResponseUpdate.getError() != null)
            {
              errors.add(localResponseUpdate.getError());
              break;
            }
          }
        }
      }
    }
    if (localResponseUpdate.getError() == null) {
      if (i == 1) {
        activate(paramYetiDevice);
      } else {
        localResponseUpdate.setError(new ErrorObject(5160, "There was no data for update. "));
      }
    }
    return localResponseUpdate;
  }
}
