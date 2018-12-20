package com.kandi.settings.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.IKdBtService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kandi.settings.R;

public class BlueDeviceDialog extends Dialog {

	private TextView txt_bluename_change;
	private TextView txt_devicename;
	private EditText device_name;
	private Button paramsubmit;
	private Button paramcancel;
	Context context;
	IKdBtService btservice = IKdBtService.Stub.asInterface(ServiceManager
			.getService("bt"));

	public BlueDeviceDialog(Context context,DialogDismissListener listener) {
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
				R.string.bluename_change));
		txt_devicename = (TextView) findViewById(R.id.txt_devicename);
		txt_devicename.setText(context.getResources().getString(
				R.string.input_new_name));
		device_name = (EditText) findViewById(R.id.device_name);
		paramsubmit = (Button) findViewById(R.id.paramsubmit);
		paramsubmit.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final String name = device_name.getText().toString();
				if (!"".equals(name)) {
					new Thread(new Runnable() {
						public void run() {
							if (btservice != null) {
								try {
									if (btservice.btSetLocalName(name) == 0) {
										if(listener != null){
											listener.onDismiss(name, 0, null);
										}
										dismiss();
									}
								} catch (RemoteException e) {
									e.printStackTrace();
								}
							}
						}
					}).start();
				}
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
