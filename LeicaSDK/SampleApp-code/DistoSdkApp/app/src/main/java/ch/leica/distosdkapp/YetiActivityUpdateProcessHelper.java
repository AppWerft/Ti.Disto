package ch.leica.distosdkapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.util.Log;
import android.view.View;

import java.util.Iterator;
import java.util.List;

import ch.leica.distosdkapp.dialog.UpdateConnectionSelectorDialog;
import ch.leica.distosdkapp.dialog.UpdateRegionSelectorDialog;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.YetiDevice;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorDefinitions;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.Types;
import ch.leica.sdk.commands.response.ResponsePlain;
import ch.leica.sdk.commands.response.ResponseUpdate;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareBinary;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareComponent;
import ch.leica.sdk.update.FirmwareUpdate.DataClasses.FirmwareUpdate;
import ch.leica.sdk.update.UpdateFirmwareDeviceHelper;

public class YetiActivityUpdateProcessHelper {
	final static String CLASSTAG = "fwUpdateProcessHelper";

	/**
	 * Region to be updated, connection mode used to be updated
	 */
	public enum UpdateRegion{
		device,
		components,
		both
	}

	/**
	 * Region to be updated, connection mode used to be updated
	 */
	public enum UpdateConn{
		online,
		offline
	}


	/**
	 *
	 * @param currentDevice
	 * @param storagePermission
	 * @param yetiInformationActivity
	 */
	void startUpdateProcess(final YetiDevice currentDevice,
							Boolean storagePermission,
							final YetiInformationActivity yetiInformationActivity) {


		final String METHODTAG = ".startUpdateProcess()";


		if (currentDevice == null) {
			return;
		}
		if (currentDevice.getConnectionState().equals(Device.ConnectionState.connected) == false) {
			Log.e("Error", METHODTAG + ": device is not connected. cannot update fw");
			return;
		}
		if (storagePermission == false) {
			Log.e("Error", METHODTAG + ": No Storage permissions, unable to run the update.");
			return;
		}



		new Thread(new Runnable() {
			@Override
			public void run() {


				boolean hasOnlineUpdate = false;

				boolean fwAvailableOffline = false;
				boolean fwAvailableOnline = false;

				try {
					// show update alert
					yetiInformationActivity.showUpdateProgressDialog("Update Device:");

					// try to get fw update from disk first
					String currentSwVersion = null;
					// get current sw version from device
					try {
						ResponsePlain responsePlain =
								(ResponsePlain) currentDevice.sendCommand(Types.Commands.GetSoftwareVersionAPPDistocom);

						responsePlain.waitForData();
						currentSwVersion = responsePlain.getReceivedDataString();
					} catch (DeviceException e) {
						e.printStackTrace();
					}
					if (currentSwVersion != null) {
						OfflineFirmwareUpdateHelper offlineFirmwareUpdateHelper = new OfflineFirmwareUpdateHelper();
						final FirmwareUpdate fwUpdateOnDisk =
								offlineFirmwareUpdateHelper.getNextFirmwareUpdate(
										currentDevice.getDeviceID(),
										currentSwVersion,
										yetiInformationActivity.getApplicationContext()
								);

						if (fwUpdateOnDisk != null) {

							fwAvailableOffline = true;

						}

						// no firmware update found on disk, so try get fw update
						// get available firmware update
						final FirmwareUpdate firmwareUpdate = currentDevice.getAvailableFirmwareUpdate();

						if (firmwareUpdate != null ) {
							if((firmwareUpdate.binaries!=null && firmwareUpdate.binaries.size()>0)
									|| (firmwareUpdate.components != null  &&  firmwareUpdate.components.size() >0)){

								fwAvailableOnline = true;
								hasOnlineUpdate = true;
							}
							else{
								Log.i("startUpdateProcess", "There is no available online update");

								hasOnlineUpdate = false;
							}

						}

						if(fwAvailableOffline == true && fwAvailableOnline == true){

							yetiInformationActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									UpdateConnectionSelectorDialog dialog =
											new UpdateConnectionSelectorDialog(
													yetiInformationActivity,
													new UpdateConnectionSelectorDialog.IUpdateConnection() {
														@Override
														public void selectUpdateConnection(final UpdateConn updateType) {
															new Thread(new Runnable() {
																@Override
																public void run() {
																	if(updateType.equals(UpdateConn.offline)){
																		// firmware update on disk found
																		handleAvailableFirmwareUpdate(
																				fwUpdateOnDisk,
																				"offline",
																				yetiInformationActivity,
																				currentDevice
																		);

																	}else if(updateType.equals(UpdateConn.online)){
																		// firmware update online
																		handleAvailableFirmwareUpdate(
																				firmwareUpdate,
																				"online",
																				yetiInformationActivity,
																				currentDevice
																		);
																	}
																}
															}).start();
														}
													}
											);


									dialog.setCanceledOnTouchOutside(false);

									String message = "";
									message = message + "Which kind of update do you want? \n";



									dialog.setMessage(message);
									dialog.show();
								}
							});


						}else if(fwAvailableOffline == true){
							// firmware update on disk found
							handleAvailableFirmwareUpdate(
									fwUpdateOnDisk,
									"offline",
									yetiInformationActivity,
									currentDevice
							);

						}else if (fwAvailableOnline == true){
							// firmware update online
							handleAvailableFirmwareUpdate(
									firmwareUpdate,
									"online",
									yetiInformationActivity,
									currentDevice
							);
						}

						if(hasOnlineUpdate == false) {
							yetiInformationActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {

									String message = "";
									if (firmwareUpdate != null) {
										for (ErrorObject error : firmwareUpdate.errors) {
											message = message + error.getErrorCode() + " : " + error.getErrorMessage();
										}

										AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(yetiInformationActivity);
										dialogBuilder.setTitle("Error");
										dialogBuilder.setMessage(message);
										dialogBuilder.setCancelable(true);
										Dialog ErrorDialog = dialogBuilder.create();
										ErrorDialog.show();
									}
								}
							});
						}
					}

				} catch (DeviceException e) {

					yetiInformationActivity.showUpdateMessages("Not Able to get the Device State.");
					Log.e(CLASSTAG, METHODTAG + ": Not Able to get the Device State.", e);

				} finally {
					yetiInformationActivity.dismissUpdateProgressDialog();
				}
			}
		}).start();
	}


	void handleAvailableFirmwareUpdate(final FirmwareUpdate fwUpdate,
									   final String offlineFoundIndicator,
									   final YetiInformationActivity yetiInformationActivity,
									   final YetiDevice currentDevice) {

		final UpdateFirmwareDeviceHelper updateFirmwareDeviceHelper = new UpdateFirmwareDeviceHelper();

		if (fwUpdate == null) {
			yetiInformationActivity.dismissUpdateProgressDialog();
			yetiInformationActivity.onError(
					new ErrorObject(
							ErrorDefinitions.UPDATE_UNMAPPED_ERROR_CODE,
							ErrorDefinitions.UPDATE_UNMAPPED_ERROR_MESSAGE
					),
					null
			);
			Log.w("FirmwareUpdate", "This should never happen");
			return;
		}

		if ((fwUpdate.getBinaries() == null || fwUpdate.getBinaries().size() < 1)
				&& (fwUpdate.getComponents() == null || fwUpdate.getComponents().size() < 1)) {

			yetiInformationActivity.dismissUpdateProgressDialog();
			for (ErrorObject error : fwUpdate.errors) {
				yetiInformationActivity.onError(
						new ErrorObject(
								error.getErrorCode(),
								error.getErrorMessage()
						),
						null
				);
			}
			return;
		}

		// save it on disk
		OfflineFirmwareUpdateHelper offlineFirmwareUpdateHelper = new OfflineFirmwareUpdateHelper();
		String currentSwVersion = fwUpdate.forCurrentVersion;
		// get current sw version from device
		try {
			ResponsePlain responsePlain =
					(ResponsePlain) currentDevice.sendCommand(Types.Commands.GetSoftwareVersionAPPDistocom);
			responsePlain.waitForData();
			currentSwVersion = responsePlain.getReceivedDataString();
		} catch (DeviceException e) {
			e.printStackTrace();
		}
		// SAVE IT
		//TODO: verify why this version returns null when only component is available
		String jsonResult =
				offlineFirmwareUpdateHelper.saveNextFirmwareUpdate(
						fwUpdate,
						currentDevice.getDeviceID(),
						currentSwVersion,
						yetiInformationActivity.getApplicationContext()
				);

		if (jsonResult == null) {
			Log.e("fwUpdate", "save fwUpdate object failed");
			yetiInformationActivity.dismissUpdateProgressDialog();
			yetiInformationActivity.onError(
					new ErrorObject(
							ErrorDefinitions.UPDATE_UNABLE_TO_SAVEDATA_CODE,
							ErrorDefinitions.UPDATE_UNABLE_TO_SAVEDATA_MESSAGE
					),
					null
			);

			//return;//TODO:add again
		}

		final FirmwareUpdate fwUpdateToUse = fwUpdate;
		yetiInformationActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				// dismiss other alert
				yetiInformationActivity.dismissUpdateProgressDialog();

				String deviceUpdatename = "";
				String deviceUpdateNextVersion = "";
				boolean hasDeviceUpdate = false;
				boolean hasComponentsUpdate = false;

				if(fwUpdateToUse.getName() == null){
					deviceUpdatename = "Not Available.";
				}else{
					deviceUpdatename = fwUpdateToUse.getName();
				}
				if(fwUpdateToUse.getVersion() == null){
					deviceUpdateNextVersion = "Not Available.";
				}else{
					deviceUpdateNextVersion = fwUpdateToUse.getVersion();
				}

				if(fwUpdateToUse.getBinaries()!=null && fwUpdateToUse.getBinaries().size()>0){
					hasDeviceUpdate = true;
				}
				boolean isComponentConnected = false;
				if(fwUpdateToUse.getComponents()!=null && fwUpdateToUse.getComponents().size()>0){


					List<FirmwareComponent> fwComponents = fwUpdateToUse.getComponents();
					Iterator<FirmwareComponent> iterator = fwComponents.iterator();
					while (iterator.hasNext()) {
						FirmwareComponent fwComponent = iterator.next(); // must be called before you can call i.remove()

						isComponentConnected = updateFirmwareDeviceHelper.
								isComponentConnected(
										currentDevice,
										fwComponent.getSerialCommand(),
										fwComponent.getVersionCommand()
								);

						if(isComponentConnected == false){
							iterator.remove();
						}

					}
					if(fwComponents.size()>0){
						hasComponentsUpdate = true;
					}

				}


				if(offlineFoundIndicator.equals("online")) {
					UpdateRegionSelectorDialog dialog =
							new UpdateRegionSelectorDialog(
									yetiInformationActivity,
									new UpdateRegionSelectorDialog.IUpdateRegion() {
										@Override
										public void selectUpdateTarget(final UpdateRegion updateRegion) {

											if (updateRegion == UpdateRegion.device) {
												fwUpdateToUse.components = null;
											} else if (updateRegion == UpdateRegion.components) {
												fwUpdateToUse.binaries = null;
											}

											launchUpdate(
													currentDevice,
													fwUpdateToUse,
													yetiInformationActivity
											);

										}
									});

					dialog.setCanceledOnTouchOutside(false);

					String message = "";
					message = message + "YETI Brand: " + fwUpdateToUse.getBrandIdentifier();
					message = message + " (" + offlineFoundIndicator + ")" + "\n";

					if (hasDeviceUpdate == true) {
						message = message + " Firmware found for Device \n" +
								"\n Name: " + deviceUpdatename +
								"\n Next Version: " + deviceUpdateNextVersion + "\n";

						for (FirmwareBinary fwBinary : fwUpdateToUse.getBinaries()) {
							message = message + "Files: " + fwBinary.getCommand() + "\n";
						}
					}

					if (hasComponentsUpdate == true) {
						message = message + "\n Firmware found for COMPONENTS \n";
						for (FirmwareComponent fwComponent : fwUpdateToUse.getComponents()) {
							message = message + "Component: " + fwComponent.getIdentifier() + "\n"
									+ "Name: " + fwComponent.getName() + "\n"
									+ "Version: " + fwComponent.getCurrentVersion() + "\n";
						}
					}

					dialog.setMessage(message);
					dialog.setTxt_updateStr("Update ?");
					dialog.show();

					if (hasDeviceUpdate == false) {
						dialog.updateDevice.setEnabled(false);
						dialog.updateDevice.setVisibility(View.INVISIBLE);
						dialog.updateBoth.setEnabled(false);
						dialog.updateBoth.setVisibility(View.INVISIBLE);

					}
					if (hasComponentsUpdate == false) {
						dialog.updateComponent.setEnabled(false);
						dialog.updateComponent.setVisibility(View.INVISIBLE);
						dialog.updateBoth.setEnabled(false);
						dialog.updateBoth.setVisibility(View.INVISIBLE);
					}
				}else{
					launchUpdate(
							currentDevice,
							fwUpdateToUse,
							yetiInformationActivity
					);
				}
			}
		});
	}


	void launchUpdate(final YetiDevice currentDevice,
						   final FirmwareUpdate fwUpdateToUse,
						   final YetiInformationActivity yetiInformationActivity)  {


		// do the update in background
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// dismiss other alert
					yetiInformationActivity.dismissUpdateProgressDialog();
					// show update alert
					yetiInformationActivity.showUpdateProgressDialog("Updating Device: ");

					ResponseUpdate responseUpdate =
							currentDevice.updateDeviceFirmwareWithFirmwareUpdate(
									fwUpdateToUse,
									yetiInformationActivity
							);

					if (responseUpdate == null) {

						yetiInformationActivity.onError(
								new ErrorObject(
										ErrorDefinitions.UPDATE_FIRMWARE_FAIL_CODE,
										ErrorDefinitions.UPDATE_FIRMWARE_FAIL_MESSAGE

								),
								null
						);
						yetiInformationActivity.dismissUpdateProgressDialog();

					} else if (responseUpdate.getError() != null) {

						yetiInformationActivity.onError(responseUpdate.getError(), null);
						yetiInformationActivity.dismissUpdateProgressDialog();
						return;


					} else {
						yetiInformationActivity.dismissUpdateProgressDialog();
						yetiInformationActivity.showAlert("Update Successful");

					}
				} catch (DeviceException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}




	void startReinstallProcess(final YetiDevice currentDevice,
							   final YetiInformationActivity yetiInformationActivity,
							   Boolean storagePermission) {

		final String METHODTAG = ".startReinstallProcess()";

		if (currentDevice == null) {
			return;
		}
		if (currentDevice.getConnectionState().equals(Device.ConnectionState.connected) == false) {
			Log.e("Error", METHODTAG + ": device is not connected. cannot update fw");
			return;
		}
		if (storagePermission == false) {
			Log.e("Error", METHODTAG + ": No Storage permissions, unable to run the update.");
			return;
		}



		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					// dismiss other alert
					yetiInformationActivity.dismissUpdateProgressDialog();
					// show update alert
					yetiInformationActivity.showUpdateProgressDialog("Reinstall Device:");

					// get available firmware update
					final FirmwareUpdate firmwareUpdate = currentDevice.getReinstallFirmware();

					if (firmwareUpdate == null) {
						yetiInformationActivity.dismissUpdateProgressDialog();
						yetiInformationActivity.onError(
								new ErrorObject(
										ErrorDefinitions.UPDATE_UNMAPPED_ERROR_CODE,
										ErrorDefinitions.UPDATE_UNMAPPED_ERROR_MESSAGE
								)
								, null
						);
						Log.w("FirmwareUpdate", "This should never happen");
						return;
					}
/*
*/

					handleAvailableFirmwareReinstall(
							firmwareUpdate,
							yetiInformationActivity,
							currentDevice
					);


				} catch (DeviceException e) {
					yetiInformationActivity.showUpdateMessages("Not Able to get the Device State.");
					Log.e(CLASSTAG, METHODTAG + ": Not Able to get the Device State.", e);

				} finally {
					yetiInformationActivity.dismissUpdateProgressDialog();
				}

			}
		}).start();

	}

	void handleAvailableFirmwareReinstall(final FirmwareUpdate fwUpdate,
									   final YetiInformationActivity yetiInformationActivity,
									   final YetiDevice currentDevice) {

		final UpdateFirmwareDeviceHelper updateFirmwareDeviceHelper = new UpdateFirmwareDeviceHelper();

		if (fwUpdate == null) {
			yetiInformationActivity.dismissUpdateProgressDialog();
			yetiInformationActivity.onError(
					new ErrorObject(
							ErrorDefinitions.UPDATE_UNMAPPED_ERROR_CODE,
							ErrorDefinitions.UPDATE_UNMAPPED_ERROR_MESSAGE
					),
					null
			);
			Log.w("FirmwareUpdate", "This should never happen");
			return;
		}

		if ((fwUpdate.getBinaries() == null || fwUpdate.getBinaries().size() < 1)
				&& (fwUpdate.getComponents() == null || fwUpdate.getComponents().size() < 1)) {

			yetiInformationActivity.dismissUpdateProgressDialog();
			for (ErrorObject error : fwUpdate.errors) {
				yetiInformationActivity.onError(
						new ErrorObject(
								error.getErrorCode(),
								error.getErrorMessage()
						),
						null
				);
			}
			return;
		}


		String currentSwVersion = fwUpdate.forCurrentVersion;
		// get current sw version from device
		try {
			ResponsePlain responsePlain =
					(ResponsePlain) currentDevice.sendCommand(Types.Commands.GetSoftwareVersionAPPDistocom);
			responsePlain.waitForData();
			currentSwVersion = responsePlain.getReceivedDataString();
		} catch (DeviceException e) {
			e.printStackTrace();
		}


		final FirmwareUpdate fwUpdateToUse = fwUpdate;
		yetiInformationActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				// dismiss other alert
				yetiInformationActivity.dismissUpdateProgressDialog();

				String deviceUpdatename = "";
				String deviceUpdateNextVersion = "";
				boolean hasDeviceUpdate = false;
				boolean hasComponentsUpdate = false;

				if (fwUpdateToUse.getName() == null) {
					deviceUpdatename = "Not Available.";
				} else {
					deviceUpdatename = fwUpdateToUse.getName();
				}
				if (fwUpdateToUse.getVersion() == null) {
					deviceUpdateNextVersion = "Not Available.";
				} else {
					deviceUpdateNextVersion = fwUpdateToUse.getVersion();
				}

				if (fwUpdateToUse.getBinaries() != null && fwUpdateToUse.getBinaries().size() > 0) {
					hasDeviceUpdate = true;
				}
				boolean isComponentConnected = false;
				if (fwUpdateToUse.getComponents() != null && fwUpdateToUse.getComponents().size() > 0) {


					List<FirmwareComponent> fwComponents = fwUpdateToUse.getComponents();
					Iterator<FirmwareComponent> iterator = fwComponents.iterator();
					while (iterator.hasNext()) {
						FirmwareComponent fwComponent = iterator.next(); // must be called before you can call i.remove()

						isComponentConnected = updateFirmwareDeviceHelper.
								isComponentConnected(
										currentDevice,
										fwComponent.getSerialCommand(),
										fwComponent.getVersionCommand()
								);

						if (isComponentConnected == false) {
							iterator.remove();
						}

					}
					if (fwComponents.size() > 0) {
						hasComponentsUpdate = true;
					}

				}

				UpdateRegionSelectorDialog dialog =
						new UpdateRegionSelectorDialog(
								yetiInformationActivity,
								new UpdateRegionSelectorDialog.IUpdateRegion() {
									@Override
									public void selectUpdateTarget(final UpdateRegion updateRegion) {

										if (updateRegion == UpdateRegion.device) {
											fwUpdateToUse.components = null;
										} else if (updateRegion == UpdateRegion.components) {
											fwUpdateToUse.binaries = null;
										}

										launchUpdate(
												currentDevice,
												fwUpdateToUse,
												yetiInformationActivity
										);

									}
								});

				dialog.setCanceledOnTouchOutside(false);

				String message = "";
				message = message + "YETI Brand: " + fwUpdateToUse.getBrandIdentifier();
				message = message + " ( Online )" + "\n";

				if (hasDeviceUpdate == true) {
					message = message + " Firmware found for Device \n" +
							"\n Name: " + deviceUpdatename +
							"\n Current Version: " + deviceUpdateNextVersion + "\n";

					for (FirmwareBinary fwBinary : fwUpdateToUse.getBinaries()) {
						message = message + "Files: " + fwBinary.getCommand() + "\n";
					}
				}

				if (hasComponentsUpdate == true) {
					message = message + "\n Firmware found for COMPONENTS \n";
					for (FirmwareComponent fwComponent : fwUpdateToUse.getComponents()) {
						message = message + "Component: " + fwComponent.getIdentifier() + "\n"
								+ "Name: " + fwComponent.getName() + "\n"
								+ "Version: " + fwComponent.getCurrentVersion() + "\n";
					}
				}

				dialog.setMessage(message);
				dialog.setTxt_updateStr("Reinstall ?");
				dialog.show();

				if (hasDeviceUpdate == false) {
					dialog.updateDevice.setEnabled(false);
					dialog.updateDevice.setVisibility(View.INVISIBLE);
					dialog.updateBoth.setEnabled(false);
					dialog.updateBoth.setVisibility(View.INVISIBLE);

				}
				if (hasComponentsUpdate == false) {
					dialog.updateComponent.setEnabled(false);
					dialog.updateComponent.setVisibility(View.INVISIBLE);
					dialog.updateBoth.setEnabled(false);
					dialog.updateBoth.setVisibility(View.INVISIBLE);
				}

			}
		});
	}
}