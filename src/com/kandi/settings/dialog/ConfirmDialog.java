package com.kandi.settings.dialog;

import com.kandi.settings.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmDialog extends Dialog {
	
	private Button paramsubmit;
	private Button paramcancel;
	TextView content;
	android.view.View.OnClickListener okClickListener;
	
	public ConfirmDialog(Context context,android.view.View.OnClickListener okClickListener) {
		super(context,R.style.my_dialog);
		this.okClickListener = okClickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_confirm_);
		paramsubmit = (Button) findViewById(R.id.paramsubmit);
		content = (TextView) findViewById(R.id.txt_content);
		paramsubmit.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(okClickListener != null){
					okClickListener.onClick(v);
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

	public void setContentText(String txt){
		if(content != null){
			content.setText(txt);
		}
	}
	
	
}
