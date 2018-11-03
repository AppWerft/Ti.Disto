package ch.leica.sdk.commands;

import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.Types.Commands;
import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class CommandsParser
{
  public CommandsParser(InputStream paramInputStream)
    throws JSONException, IllegalArgumentCheckedException, IOException
  {
    a(paramInputStream);
  }
  
  private void a(InputStream paramInputStream)
    throws JSONException, IllegalArgumentCheckedException, IOException
  {
    try
    {
      BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream, "UTF-8"), 8);
      StringBuilder localStringBuilder = new StringBuilder();
      String str = null;
      while ((str = localBufferedReader.readLine()) != null) {
        localStringBuilder.append(str + "\n");
      }
      a(localStringBuilder.toString());
      if (WifiCommand.getCommand(Types.Commands.Terminator) == null) {
        throw new IllegalArgumentCheckedException("The terminator command is missing for Wifi. ");
      }
    }
    catch (IOException localIOException)
    {
      Logs.log(Logs.LogTypes.exception, "There was an error parsing the file", localIOException);
      throw localIOException;
    }
    catch (JSONException localJSONException)
    {
      Logs.log(Logs.LogTypes.exception, "There was an error parsing the file, Verify the commands commands.json for completeness", localJSONException);
      throw localJSONException;
    }
    catch (Exception localException)
    {
      Logs.log(Logs.LogTypes.exception, "error", localException);
      throw new IllegalArgumentCheckedException("Exception: ", localException);
    }
  }
  
  private void a(String paramString)
    throws JSONException, IllegalArgumentCheckedException
  {
    String str1 = "";
    String str2 = "";
    String str3 = "";
    String str4 = "";
    String str5 = "";
    try
    {
      if (paramString != null)
      {
        String str6 = "commands";
        String str7 = "protocol";
        String str8 = "wifi";
        String str9 = "ble";
        String str10 = "name";
        String str11 = "value";
        String str12 = "parameter1";
        String str13 = "parameter2";
        JSONObject localJSONObject1 = new JSONObject(paramString);
        JSONArray localJSONArray = localJSONObject1.getJSONArray(str6);
        for (int i = 0; i < localJSONArray.length(); i++)
        {
          JSONObject localJSONObject2 = localJSONArray.getJSONObject(i);
          try
          {
            str1 = localJSONObject2.getString(str10);
            str2 = localJSONObject2.getString(str11);
            str3 = localJSONObject2.getString(str12);
            str4 = localJSONObject2.getString(str13);
            str5 = localJSONObject2.getString(str7);
            Logs.log(Logs.LogTypes.debug, "Name: " + str1 + " Value: " + str2 + " currentParameter1: " + str3 + " currentParameter2: " + str4);
          }
          catch (Exception localException)
          {
            Logs.log(Logs.LogTypes.exception, "Failed in Name: " + str1);
            throw new IllegalArgumentException("Failed to get Json data Failed in Name:" + str1, localException);
          }
          if (str5.equals(str8)) {
            WifiCommand.setCommand(Types.Commands.valueOf(str1), str2, str3, str4);
          } else if (str5.equals(str9)) {
            BLECommand.setCommand(Types.Commands.valueOf(str1), str2, str3, str4);
          } else if (str5.equals("error")) {
            ErrorObject.setErrors(str1, str2);
          }
        }
      }
    }
    catch (IllegalArgumentCheckedException localIllegalArgumentCheckedException)
    {
      Logs.log(Logs.LogTypes.exception, "failed in: " + str1, localIllegalArgumentCheckedException);
      throw localIllegalArgumentCheckedException;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      Logs.log(Logs.LogTypes.exception, "failed in: " + str1 + " - There is a missing Types.Command value or an extra value in the commands.json file", localIllegalArgumentException);
      throw new IllegalArgumentCheckedException("There is a missing Types.Command value", localIllegalArgumentException);
    }
  }
}
