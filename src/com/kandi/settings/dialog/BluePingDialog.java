package com.kandi.settings.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kandi.settings.Configs;
import com.kandi.settings.R;
import com.kandi.settings.utils.SharedPreferencesUtils;

public class BluePingDialog extends Dialog {

	private TextView txt_bluename_change;
	private TextView txt_devicename;
	private EditText device_name;
	private Button paramsubmit;
	private Button paramcancel;
	Context context;

	public BluePingDialog(Context context,DialogDismissListener listener) {
		super(context,R.style.my_dialog);
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluedialog_confirm);
		context = getContext();
		txt_bluename_change = (TextView) findViewById(R.id.txt_bluename_change);
		txt_bluename_change.setText(context.getResources().getString(
				R.string.blueping_change));
		txt_devicename = (TextView) findViewById(R.id.txt_devicename);
		txt_devicename.setText(context.getResources().getString(
				R.string.input_new_ping));
		device_name = (EditText) findViewById(R.id.device_name);
		device_name.setInputType(InputType.TYPE_CLASS_NUMBER);
		paramsubmit = (Button) findViewById(R.id.paramsubmit);
		paramsubmit.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String blueping = SharedPreferencesUtils.getSharedPreferences(getContext()).getString(Configs.SHARED_BLUE_PING,
						Configs.DEFUALT_BLUE_PING);
				if(!"".equals(device_name.getText().toString()) && !blueping.equals(device_name.getText().toString())){
					Editor edt = SharedPreferencesUtils.getEditor(getContext());
					edt.putString(Configs.SHARED_BLUE_PING, device_name.getText().toString());
					edt.commit();
					if(listener != null){
						listener.onDismiss(device_name.getText().toString(), 0, null);
					}
					device_name.setText("");
				}
				dismiss();
			}
		});

		paramcancel = (Button) findViewById(R.id.paramcancel);
		paramcancel.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	private DialogDismissListener listener;

}
