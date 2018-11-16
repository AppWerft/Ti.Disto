package ch.leica.distosdkapp;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareBinary;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareComponent;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareUpdate;

public class OfflineFirmwareUpdateHelper {
	static final String TAG = "OfflFwUp";

	private void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { //some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

	public FirmwareUpdate getNextFirmwareUpdate(String deviceId, String currentSwVersion, Context context) {
		if (deviceId == null
				|| deviceId.length() < 1
				|| currentSwVersion == null
				|| currentSwVersion.length() < 1
				|| context == null) {
			return null;
		}

		// check folder if already existing
		String subFolderName = deviceId.replace(" ", "");
		File externalCacheDir = context.getExternalCacheDir();
		File destinationFolder = new File(externalCacheDir + "/" + subFolderName + "/" + currentSwVersion + "/");

		// if folder does not exist
		if (destinationFolder.isDirectory() == false
				|| destinationFolder.exists() == false) {
			Log.i(TAG, "folder does not exist: " + destinationFolder.getAbsolutePath());
			return null;
		}

		File file = new File(destinationFolder, "data.json");
		if (file.exists() == false) {
			Log.i(TAG, "data.json does not exist: " + file.getAbsolutePath());
			return null;
		}

		// read json string
		//Read text from file
		StringBuilder text = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		FirmwareUpdate fwUpdateResult = new FirmwareUpdate();
		fwUpdateResult.isValid = true;
		try {

			JSONObject fwUpdateJson = new JSONObject(text.toString());

			try {
				fwUpdateResult.version = fwUpdateJson.getString("version");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			fwUpdateResult.brandIdentifier = fwUpdateJson.getString("brandIdentifier");
			fwUpdateResult.productIdentifier = fwUpdateJson.getString("productIdentifier");

			try {
				fwUpdateResult.name = fwUpdateJson.getString("productName");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// binaries
			JSONArray binariesJson = fwUpdateJson.getJSONArray("binaries");
			if (binariesJson != null) {
				for (int i = 0; i < binariesJson.length(); i++) {
					JSONObject binaryJson = (JSONObject) binariesJson.get(i);
					int orderNumber = binaryJson.getInt("orderNumber");
					String command = binaryJson.getString("command");
					int offset = binaryJson.getInt("offset");
					String dataString = binaryJson.getString("data");
					byte[] data = Base64.decode(dataString, Base64.DEFAULT);

					FirmwareBinary binary = new FirmwareBinary(orderNumber, command, offset, data);
					fwUpdateResult.binaries.add(binary);
				}
			}

			// components
			JSONArray componentsJson = fwUpdateJson.getJSONArray("components");
			if (componentsJson != null) {
				for (int i = 0; i < componentsJson.length(); i++) {
					JSONObject componentJson = componentsJson.getJSONObject(i);
					FirmwareComponent component = new FirmwareComponent();
					component.name = componentJson.getString("name");
					component.identifier = componentJson.getString("identifier");
					component.currentVersion = componentJson.getString("version");
					component.versionCommand = componentJson.getString("versionCommand");
					component.serialCommand = componentJson.getString("serialCommand");
					// binaries
					JSONArray componentBinariesJson = componentJson.getJSONArray("binaries");
					if (componentBinariesJson != null) {
						for (int j = 0; j < componentBinariesJson.length(); j++) {
							JSONObject binaryJson = (JSONObject) componentBinariesJson.get(j);
							int orderNumber = binaryJson.getInt("orderNumber");
							String command = binaryJson.getString("command");
							int offset = binaryJson.getInt("offset");
							String dataString = binaryJson.getString("data");
							byte[] data = Base64.decode(dataString, Base64.DEFAULT);

							FirmwareBinary binary = new FirmwareBinary(orderNumber, command, offset, data);
							component.binaries.add(binary);
						}
					}
					fwUpdateResult.components.add(component);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return fwUpdateResult;
	}

	public String saveNextFirmwareUpdate(FirmwareUpdate fwUpdate,
										 String deviceId,
										 String currentSwVersion,
										 Context context) {
		if (fwUpdate == null
				|| deviceId == null
				|| deviceId.length() < 1
				//|| currentSwVersion == null
				//|| currentSwVersion.length() < 1
				|| context == null) {
			return null;
		}

		// check firmware update
		//if (fwUpdate.version == null
		//		|| fwUpdate.version.length() < 1){
		//	return null;
		//}
		if ((fwUpdate.binaries == null || fwUpdate.binaries.size() < 1)
				&& (fwUpdate.components == null || fwUpdate.components.size() < 1)
				) {
			return null;
		}

		// check folder if already existing
		String subFolderName = deviceId.replace(" ", "");
		File externalCacheDir = context.getExternalCacheDir();
		File destinationFolder = new File(externalCacheDir + "/" + subFolderName + "/" + currentSwVersion + "/");

		// if folder already exists
		if (destinationFolder.isDirectory() && destinationFolder.exists()) {
			Log.i(TAG, "destinationFolder already exists: " + destinationFolder.getAbsolutePath());
			deleteFolder(destinationFolder);
		}

		// create a json from the firmware update object and save the json
		JSONObject fwUpdateJson = new JSONObject();
		try {
			fwUpdateJson.put("version", fwUpdate.version);
			fwUpdateJson.put("brandIdentifier", fwUpdate.brandIdentifier);
			fwUpdateJson.put("productIdentifier", fwUpdate.productIdentifier);
			fwUpdateJson.put("productName", fwUpdate.name);
			// binaries
			JSONArray binariesJsonArray = new JSONArray();
			for (FirmwareBinary binary : fwUpdate.binaries) {
				JSONObject binaryJson = new JSONObject();
				binaryJson.put("orderNumber", binary.orderNumber);
				binaryJson.put("command", binary.command);
				binaryJson.put("offset", binary.offset);
				String dataString = Base64.encodeToString(binary.data, Base64.DEFAULT);
				binaryJson.put("data", dataString);

				binariesJsonArray.put(binaryJson);
			}
			fwUpdateJson.put("binaries", binariesJsonArray);

			// components
			JSONArray componentsJsonArray = new JSONArray();
			for (FirmwareComponent component : fwUpdate.components) {
				JSONObject componentJson = new JSONObject();
				componentJson.put("name", component.name);
				componentJson.put("identifier", component.identifier);
				componentJson.put("version", component.currentVersion);
				componentJson.put("versionCommand", component.versionCommand);
				componentJson.put("serialCommand", component.serialCommand);
				// binaries
				JSONArray componentBinariesJsonArray = new JSONArray();
				for (FirmwareBinary binary : component.binaries) {
					JSONObject binaryJson = new JSONObject();
					binaryJson.put("orderNumber", binary.orderNumber);
					binaryJson.put("command", binary.command);
					binaryJson.put("offset", binary.offset);
					String dataString = Base64.encodeToString(binary.data, Base64.DEFAULT);
					binaryJson.put("data", dataString);

					componentBinariesJsonArray.put(binaryJson);
				}
				componentJson.put("binaries", componentBinariesJsonArray);

				componentsJsonArray.put(componentJson);
			}
			fwUpdateJson.put("components", componentsJsonArray);

		} catch (Exception e) {
			e.printStackTrace();
			fwUpdateJson = null;
			return null;
		}

		// make folder
		destinationFolder.mkdirs();

		// save JSON
		String jsonString = fwUpdateJson.toString();
		File file = new File(destinationFolder, "data.json");
		try {
			file.createNewFile();
			FileOutputStream fOut = new FileOutputStream(file);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(jsonString);
			myOutWriter.close();
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return jsonString;
	}



}
