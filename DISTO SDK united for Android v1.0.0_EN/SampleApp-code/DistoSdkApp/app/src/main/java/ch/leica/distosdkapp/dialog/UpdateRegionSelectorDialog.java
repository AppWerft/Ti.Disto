package ch.leica.distosdkapp.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import ch.leica.distosdkapp.R;
import ch.leica.distosdkapp.YetiActivityUpdateProcessHelper;
import ch.leica.distosdkapp.YetiInformationActivity;



public class UpdateRegionSelectorDialog extends Dialog implements
		android.view.View.OnClickListener {

	public interface IUpdateRegion {
		void selectUpdateTarget(YetiActivityUpdateProcessHelper.UpdateRegion updateRegion);
	}

	private IUpdateRegion updateTargetListener;

	private YetiInformationActivity activity;
	private Dialog dialog;
	public Button updateDevice, updateComponent, updateBoth, cancel;
	private TextView messageTxtView;
	private String messageStr;

	private String txt_updateStr;
	private TextView txt_updateView;



	public UpdateRegionSelectorDialog(YetiInformationActivity activity, IUpdateRegion updateTargetListener) {
		super(activity);

		this.activity = activity;
		this.updateTargetListener = updateTargetListener;
		this.setCancelable(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.update_region_selector_dialog);
		updateDevice = (Button) findViewById(R.id.btn_update_device);
		updateComponent = (Button) findViewById(R.id.btn_update_component);
		updateBoth = (Button) findViewById(R.id.btn_update_both);
		cancel = (Button) findViewById(R.id.btn_cancel);
		messageTxtView = (TextView) findViewById(R.id.txt_info);
		messageTxtView.setText(messageStr);
		messageTxtView.setMovementMethod(new ScrollingMovementMethod());

		txt_updateView = (TextView) findViewById(R.id.txt_update);
		txt_updateView.setText(txt_updateStr);

		updateDevice.setOnClickListener(this);
		updateComponent.setOnClickListener(this);
		updateBoth.setOnClickListener(this);
		cancel.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {


		switch (v.getId()) {
			case R.id.btn_update_device:
				this.updateTargetListener.selectUpdateTarget(YetiActivityUpdateProcessHelper.UpdateRegion.device);

				break;
			case R.id.btn_update_component:
				this.updateTargetListener.selectUpdateTarget(YetiActivityUpdateProcessHelper.UpdateRegion.components);
				break;
			case R.id.btn_update_both:
				this.updateTargetListener.selectUpdateTarget(YetiActivityUpdateProcessHelper.UpdateRegion.both);
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

	public void setTxt_updateStr(String txt_updateStr) {
		this.txt_updateStr = txt_updateStr;
	}

}