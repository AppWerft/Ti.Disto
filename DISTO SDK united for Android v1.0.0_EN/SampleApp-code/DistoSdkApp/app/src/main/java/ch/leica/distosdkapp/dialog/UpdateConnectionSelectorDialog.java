package ch.leica.distosdkapp.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import ch.leica.distosdkapp.R;
import ch.leica.distosdkapp.YetiActivityUpdateProcessHelper;
import ch.leica.distosdkapp.YetiInformationActivity;



public class UpdateConnectionSelectorDialog extends Dialog implements
		View.OnClickListener {

	public interface IUpdateConnection {
		void selectUpdateConnection(YetiActivityUpdateProcessHelper.UpdateConn updateConn);
	}

	private UpdateConnectionSelectorDialog.IUpdateConnection updateTargetListener;

	private YetiInformationActivity activity;
	private Dialog dialog;
	public Button updateOnline, updateOffline, cancel;
	private TextView messageTxtView;
	private String messageStr;



	public UpdateConnectionSelectorDialog(YetiInformationActivity activity, IUpdateConnection updateTargetListener) {
		super(activity);

		this.activity = activity;
		this.updateTargetListener = updateTargetListener;
		this.setCancelable(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.update_connection_selector_dialog);

		updateOnline = (Button) findViewById(R.id.btn_update_online);
		updateOffline = (Button) findViewById(R.id.btn_update_offline);
		cancel = (Button) findViewById(R.id.btn_cancel);

		messageTxtView = (TextView) findViewById(R.id.txt_info);
		messageTxtView.setText(messageStr);

		updateOnline.setOnClickListener(this);
		updateOffline.setOnClickListener(this);
		cancel.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {


		switch (v.getId()) {
			case R.id.btn_update_online:
				this.updateTargetListener.selectUpdateConnection(YetiActivityUpdateProcessHelper.UpdateConn.online);
				break;
			case R.id.btn_update_offline:
				this.updateTargetListener.selectUpdateConnection(YetiActivityUpdateProcessHelper.UpdateConn.offline);
				break;
			case R.id.btn_cancel:
				dismiss();
				break;
			default:
				break;
		}
		dismiss();
	}


	public void setMessage(String message){

		this.messageStr = message;
	}
}