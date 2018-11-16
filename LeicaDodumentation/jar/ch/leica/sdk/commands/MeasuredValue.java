package ch.leica.sdk.commands;

import android.support.annotation.VisibleForTesting;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;

public final class MeasuredValue
{
  private double a = -9999.0D;
  private double b = -9999.0D;
  private String c = "";
  private String d = "";
  private int e = 55537;
  private String f = "";
  private final String g = "Wrong formatted Number";
  
  public MeasuredValue()
    throws IllegalArgumentCheckedException
  {}
  
  public double getOriginalValue()
  {
    return a;
  }
  
  public void setOriginalValue(double paramDouble)
  {
    a = paramDouble;
  }
  
  public double getConvertedValue()
  {
    return b;
  }
  
  public void setConvertedValue(double paramDouble)
  {
    b = paramDouble;
  }
  
  public String getConvertedValueStr()
  {
    return c;
  }
  
  public void setConvertedValueStr(String paramString)
  {
    c = paramString;
  }
  
  public String getConvertedValueStrNoUnit()
  {
    return d;
  }
  
  public void setConvertedValueStrNoUnit(String paramString)
  {
    d = paramString;
  }
  
  public int getUnit()
  {
    return e;
  }
  
  public String getUnitStr()
  {
    return f;
  }
  
  public void setUnitStr(String paramString)
  {
    f = paramString;
  }
  
  public MeasuredValue(String paramString)
    throws IllegalArgumentCheckedException
  {
    try
    {
      if (paramString != null) {
        a = a(paramString);
      } else {
        throw new IllegalArgumentCheckedException("Null Value");
      }
    }
    catch (Exception localException)
    {
      throw new IllegalArgumentCheckedException("Wrong formatted Number", localException);
    }
  }
  
  public MeasuredValue(double paramDouble)
    throws IllegalArgumentCheckedException
  {
    try
    {
      a = paramDouble;
    }
    catch (Exception localException)
    {
      throw new IllegalArgumentCheckedException("Wrong formatted Number", localException);
    }
  }
  
  public MeasuredValue(double paramDouble, short paramShort)
    throws IllegalArgumentCheckedException
  {
    try
    {
      a = paramDouble;
      e = paramShort;
    }
    catch (Exception localException)
    {
      throw new IllegalArgumentCheckedException("Wrong formatted Number", localException);
    }
  }
  
  private float a(String paramString)
    throws IllegalArgumentCheckedException
  {
    try
    {
      return Float.valueOf(paramString).floatValue();
    }
    catch (Exception localException)
    {
      throw new IllegalArgumentCheckedException("Wrong formatted Number", localException);
    }
  }
  
  public double convertDistance()
    throws IllegalArgumentCheckedException
  {
    try
    {
      setConvertedValue(MeasurementConverter.convertDistance(this).getConvertedValue());
      return b;
    }
    catch (IllegalArgumentCheckedException localIllegalArgumentCheckedException)
    {
      b = -9999.0D;
      throw new IllegalArgumentCheckedException("Error converting distance: ", localIllegalArgumentCheckedException);
    }
  }
  
  public double convertAngle()
    throws IllegalArgumentCheckedException
  {
    try
    {
      setConvertedValue(MeasurementConverter.convertAngle(this).getConvertedValue());
      return b;
    }
    catch (IllegalArgumentCheckedException localIllegalArgumentCheckedException)
    {
      throw new IllegalArgumentCheckedException("Error converting angle: ", localIllegalArgumentCheckedException);
    }
  }
  
  @VisibleForTesting
  public double convertQuaternion()
    throws IllegalArgumentCheckedException
  {
    return b;
  }
  
  @VisibleForTesting
  public double convertAcceleration()
    throws IllegalArgumentCheckedException
  {
    return b;
  }
  
  @VisibleForTesting
  public double convertMagnetometer()
    throws IllegalArgumentCheckedException
  {
    return b;
  }
  
  @VisibleForTesting
  public double convertRotation()
    throws IllegalArgumentCheckedException
  {
    return b;
  }
  
  public void setUnit(int paramInt)
  {
    e = paramInt;
  }
}
