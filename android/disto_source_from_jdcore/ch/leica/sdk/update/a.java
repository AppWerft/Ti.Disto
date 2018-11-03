package ch.leica.sdk.update;

import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;

final class a
{
  public static int a(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = paramInt2;
    for (int j = 0; j < paramInt1; j++)
    {
      i ^= paramArrayOfByte[j] << 24;
      for (int k = 8; k > 0; k--)
      {
        int m = i & 0x80000000;
        if (m != 0) {
          i = i << 1 ^ 0x4C11DB7;
        } else {
          i <<= 1;
        }
      }
    }
    Logs.log(Logs.LogTypes.debug, "Remainder: " + i);
    return i & 0xFFFFFFFF;
  }
}
