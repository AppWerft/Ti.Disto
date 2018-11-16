package ch.leica.sdk.commands;

import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;

public final class MeasurementConverter
{
  public MeasurementConverter() {}
  
  public static MeasuredValue convertDistance(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {
    if (paramMeasuredValue != null)
    {
      double d = paramMeasuredValue.getOriginalValue();
      try
      {
        if (paramMeasuredValue.getOriginalValue() == -9999.0D) {
          throw new IllegalArgumentCheckedException("Distance has not been set, it can not be converted");
        }
        int i = 0;
        int j = 0;
        if (paramMeasuredValue.getUnit() == -9999.0D) {
          throw new IllegalArgumentCheckedException("Distance Unit has not been correctly set. ");
        }
        Logs.log(Logs.LogTypes.debug, "DISTANCE: " + paramMeasuredValue.getOriginalValue() + " UNIT: " + paramMeasuredValue.getUnit());
        if ((paramMeasuredValue.getUnit() >= 100) && (paramMeasuredValue.getUnit() < 1000))
        {
          i = 1;
          paramMeasuredValue.setUnit(paramMeasuredValue.getUnit() - 100);
          if ((paramMeasuredValue.getUnit() == 9) || (paramMeasuredValue.getUnit() == 13) || (paramMeasuredValue.getUnit() == 12) || (paramMeasuredValue.getUnit() == 11) || (paramMeasuredValue.getUnit() == 10) || (paramMeasuredValue.getUnit() == 4) || (paramMeasuredValue.getUnit() == 8) || (paramMeasuredValue.getUnit() == 7) || (paramMeasuredValue.getUnit() == 6) || (paramMeasuredValue.getUnit() == 5))
          {
            paramMeasuredValue.setUnit(4);
            paramMeasuredValue.setOriginalValue(paramMeasuredValue.getOriginalValue() * 3.28083989501312D);
          }
          else if (paramMeasuredValue.getUnit() == 3)
          {
            paramMeasuredValue.setUnit(0);
          }
          else if (paramMeasuredValue.getUnit() == 14)
          {
            paramMeasuredValue.setOriginalValue(paramMeasuredValue.getOriginalValue() * 3.28083989501312D / 3.0D);
          }
        }
        else if (paramMeasuredValue.getUnit() >= 1000)
        {
          j = 1;
          paramMeasuredValue.setUnit(paramMeasuredValue.getUnit() - 1000);
          if ((paramMeasuredValue.getUnit() == 9) || (paramMeasuredValue.getUnit() == 13) || (paramMeasuredValue.getUnit() == 12) || (paramMeasuredValue.getUnit() == 11) || (paramMeasuredValue.getUnit() == 10) || (paramMeasuredValue.getUnit() == 4) || (paramMeasuredValue.getUnit() == 8) || (paramMeasuredValue.getUnit() == 7) || (paramMeasuredValue.getUnit() == 6) || (paramMeasuredValue.getUnit() == 5))
          {
            paramMeasuredValue.setUnit(4);
            paramMeasuredValue.setOriginalValue(paramMeasuredValue.getOriginalValue() * 3.28083989501312D * 3.28083989501312D);
          }
          else if (paramMeasuredValue.getUnit() == 3)
          {
            paramMeasuredValue.setUnit(0);
          }
          else if (paramMeasuredValue.getUnit() == 14)
          {
            paramMeasuredValue.setOriginalValue(paramMeasuredValue.getOriginalValue() * 3.28083989501312D / 3.0D * 3.28083989501312D / 3.0D);
          }
        }
        paramMeasuredValue.setConvertedValue(paramMeasuredValue.getOriginalValue() + 1.0E-5D);
        switch (paramMeasuredValue.getUnit())
        {
        case -1: 
        case 0: 
          paramMeasuredValue.setConvertedValueStr(String.format("%.3f m", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.3f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setUnitStr("m");
          break;
        case 2: 
          paramMeasuredValue.setConvertedValueStr(String.format("%.2f m", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.2f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setUnitStr("m");
          break;
        case 3: 
          paramMeasuredValue.setConvertedValue(paramMeasuredValue.getConvertedValue() * 1000.0D);
          paramMeasuredValue.setConvertedValueStr(String.format("%.1f mm", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.1f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setUnitStr("mm");
          break;
        case 1: 
          paramMeasuredValue.setConvertedValueStr(String.format("%.4f m", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.4f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setUnitStr("m");
          break;
        case 4: 
          paramMeasuredValue.setConvertedValue(paramMeasuredValue.getConvertedValue() * 3.28083989501312D);
          paramMeasuredValue.setConvertedValueStr(String.format("%.2f ft", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.2f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setUnitStr("ft");
          break;
        case 5: 
        case 6: 
        case 7: 
        case 8: 
          paramMeasuredValue.setConvertedValue(paramMeasuredValue.getConvertedValue() * 1000.0D);
          paramMeasuredValue.setConvertedValueStr(a(paramMeasuredValue.getConvertedValue(), paramMeasuredValue.getUnit()));
          paramMeasuredValue.setConvertedValueStrNoUnit(paramMeasuredValue.getConvertedValueStr().replace(" in", ""));
          paramMeasuredValue.setUnitStr("ft");
          break;
        case 9: 
          paramMeasuredValue.setConvertedValue(paramMeasuredValue.getConvertedValue() * 39.370079040527344D);
          paramMeasuredValue.setConvertedValueStr(String.format("%.2f in", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.2f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setUnitStr("in");
          break;
        case 10: 
        case 11: 
        case 12: 
        case 13: 
          paramMeasuredValue.setConvertedValueStr(a(paramMeasuredValue.getConvertedValue() * 1000.0D, paramMeasuredValue.getUnit()));
          paramMeasuredValue.setConvertedValueStrNoUnit(paramMeasuredValue.getConvertedValueStr().replace(" in", ""));
          paramMeasuredValue.setUnitStr("in");
          break;
        case 14: 
          paramMeasuredValue.setConvertedValue(paramMeasuredValue.getConvertedValue() * 3.28083989501312D / 3.0D);
          paramMeasuredValue.setConvertedValueStr(String.format("%.3f yd", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.3f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setUnitStr("yd");
          break;
        default: 
          paramMeasuredValue.setConvertedValueStr(String.format("%.3f m", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.3f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setUnitStr("yd");
        }
        String str;
        if (i != 0)
        {
          str = paramMeasuredValue.getUnitStr() + "²";
          paramMeasuredValue.setConvertedValueStr(paramMeasuredValue.getConvertedValueStrNoUnit() + " " + str);
          paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.3f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setUnitStr(str);
        }
        if (j != 0)
        {
          str = paramMeasuredValue.getUnitStr() + "³";
          paramMeasuredValue.setConvertedValueStr(paramMeasuredValue.getConvertedValueStrNoUnit() + " " + str);
          paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.3f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
          paramMeasuredValue.setUnitStr(str);
        }
        paramMeasuredValue.setOriginalValue(d);
      }
      catch (Exception localException)
      {
        Logs.log(Logs.LogTypes.exception, "Please verify code. Unexpected Error in Measurement", localException);
        paramMeasuredValue.setOriginalValue(-9999.0D);
        paramMeasuredValue.setConvertedValue(-9999.0D);
        paramMeasuredValue.setConvertedValueStr("Error");
        throw new IllegalArgumentCheckedException("Error converting Measurement. ", localException);
      }
    }
    else
    {
      throw new IllegalArgumentCheckedException("Parameter distance is null.");
    }
    return paramMeasuredValue;
  }
  
  public static MeasuredValue convertAngle(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {
    if (paramMeasuredValue.getUnit() != -10)
    {
      float f1 = 1.4710286F;
      float f2 = 1.3258177F;
      double d1 = 0.05D;
      if (paramMeasuredValue != null)
      {
        if (paramMeasuredValue.getOriginalValue() == -9999.0D) {
          throw new IllegalArgumentCheckedException("Angle has not been set, it can not be converted");
        }
        Logs.log(Logs.LogTypes.debug, "Angle: " + paramMeasuredValue.getOriginalValue() + " UNIT: " + paramMeasuredValue.getUnit());
        double d2 = a(Double.valueOf(paramMeasuredValue.getOriginalValue())).doubleValue();
        try
        {
          double d3;
          switch (paramMeasuredValue.getUnit())
          {
          case 6: 
            paramMeasuredValue.setConvertedValue(Math.round(d2 / d1) * d1);
            paramMeasuredValue.setConvertedValueStr(String.format("%.2f°", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
            paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.2f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
            paramMeasuredValue.setUnitStr("°");
            break;
          case 2: 
            if (paramMeasuredValue.getOriginalValue() < 0.0D) {
              d2 = 360.0D + d2;
            }
            paramMeasuredValue.setConvertedValue(Math.round(d2 / d1) * d1);
            paramMeasuredValue.setConvertedValueStr(String.format("%.2f°", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
            paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.2f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
            paramMeasuredValue.setUnitStr("°");
            break;
          case 0: 
            if ((paramMeasuredValue.getOriginalValue() > 1.5707963267948966D) && (paramMeasuredValue.getOriginalValue() < 3.141592653589793D)) {
              d2 = -90.0D + (d2 - 90.0D);
            } else if ((paramMeasuredValue.getOriginalValue() > -3.141592653589793D) && (paramMeasuredValue.getOriginalValue() < -1.5707963267948966D)) {
              d2 = 180.0D + d2;
            }
            paramMeasuredValue.setConvertedValue(Math.round(d2 / d1) * d1);
            paramMeasuredValue.setConvertedValueStr(String.format("%.2f°", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
            paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.2f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
            paramMeasuredValue.setUnitStr("°");
            break;
          case 1: 
            paramMeasuredValue.setConvertedValue(Math.round(d2 / d1) * d1);
            paramMeasuredValue.setConvertedValueStr(String.format("%.2f°", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
            paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.2f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
            paramMeasuredValue.setUnitStr("°");
            break;
          case 3: 
            d3 = a(paramMeasuredValue.getOriginalValue());
            if (Math.abs(d3) > f1) {
              if (d3 > 0.0D) {
                d3 -= 1.5707963267948966D;
              } else {
                d3 += 1.5707963267948966D;
              }
            }
            paramMeasuredValue.setConvertedValue(Math.tan(d3) * 100.0D);
            paramMeasuredValue.setConvertedValueStr(String.format("%.2f %%", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
            paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.2f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
            paramMeasuredValue.setUnitStr("%");
            break;
          case 4: 
            d3 = a(paramMeasuredValue.getOriginalValue());
            if (Math.abs(d3) > 0.7853981633974483D) {
              if (d3 > 0.0D) {
                d3 -= 1.5707963267948966D;
              } else {
                d3 += 1.5707963267948966D;
              }
            }
            paramMeasuredValue.setConvertedValue(Math.tan(d3) * 1000.0D);
            paramMeasuredValue.setConvertedValueStr(String.format("%.1f mm/m", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
            paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.1f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
            paramMeasuredValue.setUnitStr("mm/m");
            break;
          case 5: 
            d3 = a(paramMeasuredValue.getOriginalValue());
            if (Math.abs(d3) > f2) {
              if (d3 > 0.0D) {
                d3 -= 1.5707963267948966D;
              } else {
                d3 += 1.5707963267948966D;
              }
            }
            paramMeasuredValue.setConvertedValue(Math.tan(d3) * 12.0D);
            paramMeasuredValue.setConvertedValueStr(String.format("%.2f in/ft", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
            paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.2f", new Object[] { Double.valueOf(paramMeasuredValue.getConvertedValue()) }));
            paramMeasuredValue.setUnitStr("in/ft");
            break;
          default: 
            paramMeasuredValue.setConvertedValueStr(String.format("%.3f", new Object[] { Double.valueOf(paramMeasuredValue.getOriginalValue()) }));
            paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.3f", new Object[] { Double.valueOf(paramMeasuredValue.getOriginalValue()) }));
          }
        }
        catch (Exception localException)
        {
          Logs.log(Logs.LogTypes.exception, "Please verify code. Unexpected Error in Measurement", localException);
          paramMeasuredValue.setOriginalValue(-9999.0D);
          paramMeasuredValue.setConvertedValue(-9999.0D);
          paramMeasuredValue.setConvertedValueStr("Error");
          throw new IllegalArgumentCheckedException("Error converting Measurement. ", localException);
        }
      }
      else
      {
        throw new IllegalArgumentCheckedException("Parameter distance is null.");
      }
    }
    else
    {
      paramMeasuredValue.setConvertedValue(paramMeasuredValue.getOriginalValue());
      paramMeasuredValue.setConvertedValueStr(String.format("%.3f Radians", new Object[] { Double.valueOf(paramMeasuredValue.getOriginalValue()) }));
      paramMeasuredValue.setConvertedValueStrNoUnit(String.format("%.3f", new Object[] { Double.valueOf(paramMeasuredValue.getOriginalValue()) }));
      paramMeasuredValue.setUnitStr("Radians");
    }
    return paramMeasuredValue;
  }
  
  public static MeasuredValue convertQuaternion(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {
    if (paramMeasuredValue != null) {
      return paramMeasuredValue;
    }
    throw new IllegalArgumentCheckedException("Parameter distance is null.");
  }
  
  public static MeasuredValue convertAcceleration(MeasuredValue paramMeasuredValue)
    throws IllegalArgumentCheckedException
  {
    if (paramMeasuredValue != null) {
      return paramMeasuredValue;
    }
    throw new IllegalArgumentCheckedException("Parameter distance is null.");
  }
  
  public static short getDefaultDirectionAngleUnit()
  {
    return 1;
  }
  
  public static short getDefaultWifiAngleUnit()
  {
    return 1;
  }
  
  public static short getDefaultWifiDistanceUnit()
  {
    return 0;
  }
  
  private static double a(double paramDouble)
  {
    if (paramDouble >= 0.0D)
    {
      if (paramDouble > 1.5707963267948966D) {
        paramDouble -= 3.141592653589793D;
      }
    }
    else if (paramDouble < -1.5707963267948966D) {
      paramDouble += 3.141592653589793D;
    }
    return paramDouble;
  }
  
  private static String a(double paramDouble, int paramInt)
  {
    double d1 = paramDouble * 0.0032808399D;
    long l2 = d1;
    double d2 = Math.abs(l2 - d1);
    double d3 = d2 * 12.0D;
    long l3 = d3;
    double d4 = Math.abs(l3 - d3);
    long l1 = (d4 * 32.0D + 0.5D);
    Logs.log(Logs.LogTypes.debug, "ulFractions:" + l1 + " restInches: " + d4);
    long l4 = l1;
    for (long l5 = 32L; (l4 % 2L == 0L) && (l4 != 0L); l5 /= 2L) {
      l4 = (l4 / 2L + 0.5D);
    }
    if (paramInt == 8) {
      while (l5 > 4L)
      {
        l4 = (l4 / 2L + 0.5D);
        l5 /= 2L;
      }
    }
    if (paramInt == 7) {
      while (l5 > 8L)
      {
        l4 = (l4 / 2L + 0.5D);
        l5 /= 2L;
      }
    }
    if (paramInt == 6) {
      while (l5 > 16L)
      {
        l4 = (l4 / 2L + 0.5D);
        l5 /= 2L;
      }
    }
    if (paramInt == 5) {
      while (l5 > 32L)
      {
        l4 = (l4 / 2L + 0.5D);
        l5 /= 2L;
      }
    }
    while ((l4 % 2L == 0L) && (l4 != 0L))
    {
      l4 /= 2L;
      l5 /= 2L;
    }
    if (paramDouble < 0.0D)
    {
      l2 = -l2;
      l3 = -l3;
    }
    if ((l5 == 1L) && (l4 == 1L))
    {
      l4 = 0L;
      l5 = 0L;
      l3 += 1L;
    }
    String str = "";
    if ((paramInt == 8) || (paramInt == 7) || (paramInt == 6) || (paramInt == 5))
    {
      if (paramDouble < 0.0D) {
        str = String.format("-%d' %d\"", new Object[] { Long.valueOf(Math.abs(l2)), Long.valueOf(Math.abs(l3)) });
      } else {
        str = String.format("%d' %d\"", new Object[] { Long.valueOf(Math.abs(l2)), Long.valueOf(Math.abs(l3)) });
      }
      if (l4 != 0L) {
        str = str + String.format(" %d/%d", new Object[] { Long.valueOf(l4), Long.valueOf(l5) });
      }
    }
    else
    {
      long l6 = Math.abs(l2) * 12L + Math.abs(l3);
      if (paramDouble < 0.0D) {
        str = String.format("-%d", new Object[] { Long.valueOf(l6) });
      } else {
        str = String.format("%d", new Object[] { Long.valueOf(l6) });
      }
      if (l4 != 0L) {
        str = str + String.format(" %d/%d", new Object[] { Long.valueOf(l4), Long.valueOf(l5) });
      }
      str = str + " in";
    }
    return str;
  }
  
  private static Double a(Double paramDouble)
  {
    return Double.valueOf(Math.toDegrees(paramDouble.doubleValue()));
  }
}
