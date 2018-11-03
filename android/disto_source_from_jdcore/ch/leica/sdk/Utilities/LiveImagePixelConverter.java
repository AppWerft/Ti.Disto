package ch.leica.sdk.Utilities;

import android.support.annotation.VisibleForTesting;
import ch.leica.sdk.Types.DeviceType;

public class LiveImagePixelConverter
{
  private double a;
  private double b;
  private double c;
  private Vector d;
  private Vector e;
  private Size f;
  @VisibleForTesting
  public Point _crosshairPosition;
  private Vector g;
  private int h;
  private int i;
  
  public double GetOpticsConstant(Types.DeviceType paramDeviceType)
  {
    double d1 = 0.0D;
    switch (1.a[paramDeviceType.ordinal()])
    {
    case 1: 
      d1 = 1800.0D;
      break;
    case 2: 
      d1 = -800.0D;
    }
    return d1;
  }
  
  public LiveImagePixelConverter(Types.DeviceType paramDeviceType, Size paramSize)
  {
    f = paramSize;
    a = GetOpticsConstant(paramDeviceType);
  }
  
  public void UpdateValues(SensorDirection paramSensorDirection, VerticalAxisFace paramVerticalAxisFace, int paramInt, Point paramPoint)
  {
    int j = 0;
    switch (paramInt)
    {
    case 7010: 
      j = 1;
      break;
    case 7011: 
      j = 2;
      break;
    case 7012: 
      j = 4;
      break;
    case 7013: 
      j = 8;
    }
    double d1 = f._Width / 640.0D;
    b = (a * j * d1);
    c = (a * j * d1);
    _crosshairPosition = paramPoint;
    double d2 = Math.sin(_NiV);
    double d3 = Math.cos(_NiV);
    double d4 = Math.sin(_NiHz);
    double d5 = Math.cos(_NiHz);
    g = new Vector(d2 * d4, d2 * d5, d3);
    d = new Vector(d5, -d4, 0.0D);
    e = new Vector(-d3 * d4, -d3 * d5, d2);
    switch (1.b[paramVerticalAxisFace.ordinal()])
    {
    case 1: 
      h = 1;
      i = -1;
      break;
    case 2: 
      h = -1;
      i = 1;
    }
  }
  
  public PolarCoordinates ToPolarCoordinates(Point paramPoint)
  {
    double d1 = h * (_X - _crosshairPosition._X);
    double d2 = i * (_Y - _crosshairPosition._Y);
    double d3 = d1 / b;
    double d4 = d2 / c;
    Vector localVector = d.mult(d3).add(e.mult(d4)).add(g.mult(Math.sqrt(1.0D - d3 * d3 - d4 * d4)));
    double d5 = Math.atan2(Math.sqrt(_X * _X + _Y * _Y), _Z);
    double d6 = Math.atan2(_X, _Y);
    return new PolarCoordinates(d6, d5);
  }
  
  public Point ToImagePoint(PolarCoordinates paramPolarCoordinates)
  {
    Point localPoint = new Point(NaN.0D, NaN.0D);
    Vector localVector = a(paramPolarCoordinates);
    if (g.scalar(localVector) > 0.0D)
    {
      double d1 = _crosshairPosition._X + h * (b * d.scalar(localVector));
      double d2 = _crosshairPosition._Y + i * (c * e.scalar(localVector));
      localPoint = new Point(d1, d2);
    }
    return localPoint;
  }
  
  private Vector a(PolarCoordinates paramPolarCoordinates)
  {
    double d1 = Math.sin(_V);
    double d2 = Math.cos(_V);
    double d3 = Math.sin(_Hz);
    double d4 = Math.cos(_Hz);
    return new Vector(d1 * d3, d1 * d4, d2);
  }
  
  public static class Vector
  {
    public double _X;
    public double _Y;
    public double _Z;
    
    public Vector(double paramDouble1, double paramDouble2, double paramDouble3)
    {
      _X = paramDouble1;
      _Y = paramDouble2;
      _Z = paramDouble3;
    }
    
    public Vector mult(double paramDouble)
    {
      return new Vector(_X * paramDouble, _Y * paramDouble, _Z * paramDouble);
    }
    
    public Vector add(Vector paramVector)
    {
      return new Vector(_X + _X, _Y + _Y, _Z + _Z);
    }
    
    public double scalar(Vector paramVector)
    {
      return _X * _X + _Y * _Y + _Z * _Z;
    }
  }
  
  public static class Point
  {
    public double _X;
    public double _Y;
    
    public Point(double paramDouble1, double paramDouble2)
    {
      _X = paramDouble1;
      _Y = paramDouble2;
    }
  }
  
  public static class Size
  {
    public double _Width;
    public double _Heigth;
    
    public Size(double paramDouble1, double paramDouble2)
    {
      _Width = paramDouble1;
      _Heigth = paramDouble2;
    }
  }
  
  public static class SensorDirection
  {
    public double _Hz;
    public double _V;
    public double _NiHz;
    public double _NiV;
    public int _CompensatorState;
    
    public SensorDirection(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt)
    {
      _Hz = paramDouble1;
      _V = paramDouble2;
      _NiHz = paramDouble3;
      _NiV = paramDouble4;
      _CompensatorState = paramInt;
    }
  }
  
  public static class PolarCoordinates
  {
    public double _Hz;
    public double _V;
    
    @VisibleForTesting
    public PolarCoordinates(double paramDouble1, double paramDouble2)
    {
      _Hz = paramDouble1;
      _V = paramDouble2;
    }
  }
  
  public static enum VerticalAxisFace
  {
    private VerticalAxisFace() {}
  }
}
