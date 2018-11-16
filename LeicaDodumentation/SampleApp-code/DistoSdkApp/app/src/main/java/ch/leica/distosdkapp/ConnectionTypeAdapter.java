package ch.leica.distosdkapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Types;

/**
 * Responsible for adding new devices to list in search device activity
 */
public class ConnectionTypeAdapter extends BaseAdapter {

	LayoutInflater inflater;
	private List<Device> distos;

	public ConnectionTypeAdapter(Context applicationContext, List<Device> distos) {
		this.distos = distos;
		inflater = LayoutInflater.from(applicationContext);
	}

	@Override
	public int getCount() {
		return distos.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * Set icon and name for each device in the device list.
	 *
	 * @param position position
	 * @param view     view
	 * @param parent   parent
	 * @return View
	 */
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		view = inflater.inflate(R.layout.listview_connectiontype, null);


		if (distos != null && distos.size() > 0) {
			Device device = distos.get(position);

			TextView disto = (TextView) view.findViewById(R.id.distoDeviceID);
			ImageView icon = (ImageView) view.findViewById(R.id.icon);

			disto.setText(device.getDeviceName());


			if (device.getConnectionType() == Types.ConnectionType.ble) {

				if (device.getConnectionState() == Device.ConnectionState.connected) {
					icon.setImageResource(R.drawable.btle_connected);
				} else {
					icon.setImageResource(R.drawable.btle_not_connected);
				}

			} else if (device.getConnectionType() == Types.ConnectionType.wifiAP) {


				if (device.getConnectionState() == Device.ConnectionState.connected) {
					icon.setImageResource(R.drawable.wifi_connected);
				} else {
					icon.setImageResource(R.drawable.wifi_not_connected);
				}

			} else if (device.getConnectionType() == Types.ConnectionType.wifiHotspot) {

				if (device.getConnectionState() == Device.ConnectionState.connected) {
					icon.setImageResource(R.drawable.hs_connected);
				} else {
					icon.setImageResource(R.drawable.hs_not_connected);
				}


			} else if (device.getConnectionType() == Types.ConnectionType.rndis) {
				if (device.getConnectionState() == Device.ConnectionState.connected) {
					icon.setImageResource(R.drawable.rndis_connected);
				} else {
					icon.setImageResource(R.drawable.rndis_not_connected);
				}
			}
			return view;


		} else {
			TextView disto = (TextView) view.findViewById(R.id.distoDeviceID);
			disto.setText(R.string.listview_no_matches);
			return view;
		}

	}

	public void setNewDeviceList(List<Device> distos) {
		this.distos = distos;
	}
}
