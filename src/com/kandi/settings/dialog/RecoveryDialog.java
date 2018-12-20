package com.kandi.settings.dialog;

import com.kandi.settings.R;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class RecoveryDialog extends Dialog {
	
	private Button paramsubmit;
	private Button paramcancel;
	TextView content;
	
	public RecoveryDialog(Context context) {
		super(context,R.style.my_dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_confirm_);
		paramsubmit = (Button) findViewById(R.id.paramsubmit);
		content = (TextView) findViewById(R.id.txt_content);
		setContentText(getContext().getString(R.string.is_recovery));
		paramsubmit.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getContext().sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
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

	public void setContentText(String txt){
		if(content != null){
			content.setText(txt);
		}
	}
	
	
}
