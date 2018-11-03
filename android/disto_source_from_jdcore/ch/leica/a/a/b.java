package ch.leica.a.a;

public class b
{
  public b() {}
  
  public byte[] a(char[] paramArrayOfChar1, char[] paramArrayOfChar2, byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte1 = new byte[paramArrayOfChar1.length];
    for (int i = 0; i < paramArrayOfChar1.length; i++) {
      arrayOfByte1[i] = ((byte)paramArrayOfChar1[i]);
    }
    byte[] arrayOfByte2 = new byte[paramArrayOfChar2.length];
    for (int j = 0; j < paramArrayOfChar2.length; j++) {
      arrayOfByte2[j] = ((byte)paramArrayOfChar2[j]);
    }
    j = arrayOfByte1.length;
    if (arrayOfByte2.length < arrayOfByte1.length) {
      j = arrayOfByte2.length;
    }
    byte[] arrayOfByte3 = new byte[j];
    for (int k = 0; k < j; k++) {
      arrayOfByte3[k] = ((byte)(arrayOfByte1[k] ^ arrayOfByte2[k]));
    }
    byte[] arrayOfByte4 = paramArrayOfByte;
    a(arrayOfByte4, arrayOfByte3);
    byte[] arrayOfByte5 = arrayOfByte4;
    return arrayOfByte5;
  }
  
  public int a(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    for (int i = 0; i < paramArrayOfByte1.length; i++)
    {
      int j = i % paramArrayOfByte2.length;
      paramArrayOfByte1[i] = ((byte)(paramArrayOfByte1[i] ^ paramArrayOfByte2[j]));
    }
    return i;
  }
}
