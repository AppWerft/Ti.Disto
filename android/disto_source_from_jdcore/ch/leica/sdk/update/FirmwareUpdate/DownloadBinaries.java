package ch.leica.sdk.update.FirmwareUpdate;

import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareBinary;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DownloadBinaries
{
  private List<FirmwareBinary> a = new ArrayList();
  private List<String> b = new ArrayList();
  private ErrorObject c;
  
  public DownloadBinaries(byte[] paramArrayOfByte, File paramFile, String paramString)
  {
    Logs.log(Logs.LogTypes.debug, "After Volley HTTP RequestFile Length: " + paramArrayOfByte.length);
    File localFile1 = null;
    if (paramArrayOfByte.length > 0)
    {
      try
      {
        String str1 = paramFile.getAbsolutePath();
        File localFile2 = new File(str1);
        a(paramFile);
        if (!localFile2.isDirectory()) {
          localFile2.mkdirs();
        }
        localFile1 = new File(paramFile, paramString);
        try
        {
          localObject1 = new FileOutputStream(localFile1);
          localObject2 = null;
          try
          {
            ((OutputStream)localObject1).write(paramArrayOfByte);
            Logs.log(Logs.LogTypes.debug, "Saved Zipped FileLength: " + localFile1.length());
            Logs.log(Logs.LogTypes.debug, "Download complete.");
            ((OutputStream)localObject1).flush();
          }
          catch (Throwable localThrowable2)
          {
            localObject2 = localThrowable2;
            throw localThrowable2;
          }
          finally
          {
            if (localObject1 != null) {
              if (localObject2 != null) {
                try
                {
                  ((OutputStream)localObject1).close();
                }
                catch (Throwable localThrowable3)
                {
                  ((Throwable)localObject2).addSuppressed(localThrowable3);
                }
              } else {
                ((OutputStream)localObject1).close();
              }
            }
          }
        }
        finally
        {
          Logs.log(Logs.LogTypes.debug, "Download closed.");
        }
        Object localObject1 = new Decompress(localFile1.getName(), paramFile.getAbsolutePath());
        a(new File(((Decompress)localObject1).getOutputFolderUnZipped()));
        ((Decompress)localObject1).unzip();
        Object localObject2 = new File(((Decompress)localObject1).getOutputFolderUnZipped());
        for (String str2 : ((File)localObject2).list())
        {
          Logs.log(Logs.LogTypes.debug, "file found: " + str2);
          String[] arrayOfString2 = a(str2);
          for (String str3 : arrayOfString2) {
            Logs.log(Logs.LogTypes.debug, str3 + " " + arrayOfString2.length);
          }
          if (!str2.contains(".zip"))
          {
            Object localObject6;
            Object localObject7;
            if (arrayOfString2.length == 3)
            {
              Logs.log(Logs.LogTypes.debug, "FirmwareBinary fileName: " + paramFile.getAbsolutePath() + "/" + str2);
              ??? = new File((File)localObject2, str2);
              Logs.log(Logs.LogTypes.debug, "UnZippedFile: " + str2 + " File Length: " + ((File)???).length());
              localObject6 = null;
              localObject7 = new byte[(int)((File)???).length()];
              try
              {
                localObject6 = new FileInputStream((File)???);
                ((FileInputStream)localObject6).read((byte[])localObject7);
                ((FileInputStream)localObject6).close();
                Logs.log(Logs.LogTypes.debug, "UnZippedbyteArray: " + str2 + " ByteArray Length: " + localObject7.length);
              }
              catch (Exception localException2)
              {
                localException2.printStackTrace();
              }
              Logs.log(Logs.LogTypes.debug, "Filesize after DeCompress: " + ((File)???).length() + " File: " + paramFile + str2 + " Size: (bytes binary file) " + localObject7.length);
              a.add(new FirmwareBinary(Integer.parseInt(arrayOfString2[0], 16), arrayOfString2[1], Integer.parseInt(arrayOfString2[2], 16), (byte[])localObject7));
            }
            else if (arrayOfString2.length == 1)
            {
              Logs.log(Logs.LogTypes.debug, "others fileName: " + paramFile.getAbsolutePath() + "/" + str2);
              try
              {
                ??? = new File((File)localObject2, str2);
                FileReader localFileReader = new FileReader((File)???);
                Object localObject8 = null;
                try
                {
                  localObject6 = new BufferedReader(localFileReader);
                  localObject7 = "";
                  String str4 = null;
                  while ((str4 = ((BufferedReader)localObject6).readLine()) != null) {
                    localObject7 = (String)localObject7 + "\n" + str4;
                  }
                  ((BufferedReader)localObject6).close();
                  Logs.log(Logs.LogTypes.debug, "File Content: " + (String)localObject7);
                  b.add(localObject7);
                }
                catch (Throwable localThrowable5)
                {
                  localObject8 = localThrowable5;
                  throw localThrowable5;
                }
                finally
                {
                  if (localFileReader != null) {
                    if (localObject8 != null) {
                      try
                      {
                        localFileReader.close();
                      }
                      catch (Throwable localThrowable6)
                      {
                        localObject8.addSuppressed(localThrowable6);
                      }
                    } else {
                      localFileReader.close();
                    }
                  }
                }
              }
              catch (Exception localException1)
              {
                Logs.log(Logs.LogTypes.exception, "Unzipped file error. " + paramFile.getAbsolutePath() + "/" + str2, localException1);
                c = new ErrorObject(7110, localException1.getMessage());
              }
            }
          }
        }
      }
      catch (IOException localIOException)
      {
        Logs.log(Logs.LogTypes.exception, "ERROR: ", localIOException);
        c = new ErrorObject(7111, "Files not found inside the Zip File");
      }
    }
    else
    {
      Logs.log(Logs.LogTypes.debug, "Retrieved File has no bytes. ");
      c = new ErrorObject(7112, "Retrieved File has no bytes.");
    }
    if (localFile1 != null) {
      localFile1.delete();
    }
  }
  
  private void a(File paramFile)
  {
    File[] arrayOfFile1 = paramFile.listFiles();
    if (arrayOfFile1 != null) {
      for (File localFile : arrayOfFile1) {
        if (localFile.isDirectory()) {
          a(localFile);
        } else {
          localFile.delete();
        }
      }
    }
    paramFile.delete();
  }
  
  private String[] a(String paramString)
  {
    String[] arrayOfString = paramString.split("_");
    return arrayOfString;
  }
  
  public List<FirmwareBinary> getBinaries()
  {
    return a;
  }
  
  public List<String> getOtherFiles()
  {
    return b;
  }
  
  public ErrorObject getError()
  {
    return c;
  }
}
