package ch.leica.sdk.update.FirmwareUpdate;

import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Decompress
{
  private String a;
  private String b;
  private String c;
  
  public Decompress(String paramString1, String paramString2)
  {
    a = paramString1;
    b = paramString2;
    c = (paramString2 + "_unzipped");
    a(c);
  }
  
  public void unzip()
    throws IOException
  {
    try
    {
      FileInputStream localFileInputStream = new FileInputStream(b + "/" + a);
      Object localObject1 = null;
      try
      {
        ZipInputStream localZipInputStream = new ZipInputStream(localFileInputStream);
        Object localObject2 = null;
        try
        {
          ZipEntry localZipEntry = null;
          while ((localZipEntry = localZipInputStream.getNextEntry()) != null)
          {
            Logs.log(Logs.LogTypes.debug, "Decompress  -  Unzipping: " + b + "/" + localZipEntry.getName());
            if (localZipEntry.isDirectory())
            {
              a(localZipEntry.getName());
            }
            else if (localZipEntry.getName().contains("/"))
            {
              localZipInputStream.closeEntry();
            }
            else
            {
              File localFile1 = new File(c);
              a(localFile1);
              File localFile2 = new File(localFile1, localZipEntry.getName());
              try
              {
                FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);
                Object localObject3 = null;
                try
                {
                  byte[] arrayOfByte = new byte['က'];
                  Logs.log(Logs.LogTypes.debug, "Unzipping to : " + localFile2.getAbsolutePath());
                  int i;
                  while ((i = localZipInputStream.read(arrayOfByte)) != -1) {
                    localFileOutputStream.write(arrayOfByte, 0, i);
                  }
                }
                catch (Throwable localThrowable6)
                {
                  localObject3 = localThrowable6;
                  throw localThrowable6;
                }
                finally
                {
                  if (localFileOutputStream != null) {
                    if (localObject3 != null) {
                      try {}catch (Throwable localThrowable7)
                      {
                        localObject3.addSuppressed(localThrowable7);
                      }
                    }
                  }
                }
              }
              finally
              {
                localZipInputStream.closeEntry();
              }
            }
          }
          localZipInputStream.close();
        }
        catch (Throwable localThrowable4)
        {
          localObject2 = localThrowable4;
          throw localThrowable4;
        }
        finally {}
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localFileInputStream != null) {
          if (localObject1 != null) {
            try
            {
              localFileInputStream.close();
            }
            catch (Throwable localThrowable9)
            {
              localObject1.addSuppressed(localThrowable9);
            }
          } else {
            localFileInputStream.close();
          }
        }
      }
    }
    catch (IOException localIOException)
    {
      Logs.log(Logs.LogTypes.exception, "Decompress  -  unzip: ", localIOException);
      throw localIOException;
    }
  }
  
  private void a(String paramString)
  {
    File localFile = new File(b + paramString);
    if (!localFile.isDirectory()) {
      localFile.mkdirs();
    }
  }
  
  private void a(File paramFile)
  {
    String str = paramFile.getAbsolutePath();
    File localFile = new File(str);
    if (!localFile.isDirectory()) {
      localFile.mkdirs();
    }
  }
  
  public String getOutputFolderUnZipped()
  {
    return c;
  }
}
