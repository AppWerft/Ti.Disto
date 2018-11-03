package ch.leica.sdk.update.FirmwareUpdate;

import ch.leica.sdk.ErrorHandling.UpdateException;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.SerialSplitResult;

public class SerialRange
{
  SerialSplitResult a;
  SerialSplitResult b;
  int c;
  boolean d;
  
  public SerialRange() {}
  
  public SerialRange(String paramString1, String paramString2)
    throws UpdateException
  {
    Logs.log(Logs.LogTypes.debug, " from: " + paramString1 + " to: " + paramString2);
    a = splitSerial(paramString2);
    b = splitSerial(paramString1);
    Logs.log(Logs.LogTypes.debug, "after split From: TYPE=" + b.splitType + ", DATE=" + b.splitDate + ", NUMBER=" + b.splitNumber);
    if (b.splitType != a.splitType)
    {
      Logs.log(Logs.LogTypes.debug, "Type mismatch. From and To types are different");
      throw new UpdateException("Type mismatch. From and To types are different");
    }
    c = b.splitType;
    d = false;
    if ((Integer.valueOf(paramString1).intValue() == 0) && (Integer.valueOf(paramString2).intValue() == 0))
    {
      d = true;
      Logs.log(Logs.LogTypes.debug, "ValidationIgnoreServerCheat is now true");
    }
  }
  
  public SerialSplitResult splitSerial(String paramString)
  {
    while (paramString.length() < 10)
    {
      localObject = "0" + paramString;
      paramString = (String)localObject;
    }
    Object localObject = new SerialSplitResult();
    splitType = Integer.valueOf(paramString.substring(0, 2)).intValue();
    splitDate = Integer.valueOf(paramString.substring(2, 6)).intValue();
    splitNumber = Integer.valueOf(paramString.substring(6, 10)).intValue();
    return localObject;
  }
  
  public boolean isValid(String paramString)
  {
    if (d == true) {
      return true;
    }
    SerialSplitResult localSerialSplitResult = splitSerial(paramString);
    Logs.log(Logs.LogTypes.debug, "Test: SplitType=" + splitType + ", Date=" + splitDate + ", Number=" + splitNumber);
    boolean bool = false;
    if ((splitType == c) && (a(b.splitDate, a.splitDate, splitDate)))
    {
      bool = true;
      if ((splitDate == b.splitDate) && (!a(b.splitNumber, b.splitNumber + 4999, splitNumber)))
      {
        Logs.log(Logs.LogTypes.debug, "from failed");
        bool = false;
      }
      if ((splitDate == a.splitDate) && (a(a.splitNumber + 1, a.splitNumber + 4999, splitNumber)))
      {
        Logs.log(Logs.LogTypes.debug, "to failed");
        bool = false;
      }
    }
    return bool;
  }
  
  private boolean a(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 < paramInt1) {
      paramInt2 += 10000;
    }
    if (paramInt3 < paramInt1) {
      paramInt3 += 10000;
    }
    return (paramInt3 >= paramInt1) && (paramInt3 <= paramInt2);
  }
}
