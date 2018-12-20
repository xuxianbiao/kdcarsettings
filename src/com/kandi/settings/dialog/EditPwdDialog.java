package com.kandi.settings.dialog;

import com.kandi.settings.Configs;
import com.kandi.settings.R;
import com.kandi.settings.SetbtnListener;
import com.kandi.settings.utils.SharedPreferencesUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditPwdDialog extends Dialog {

	private EditText pwd_txt;
	private Button pwdsubmit;
	private Button pwdcancel;
	Context context;

	public EditPwdDialog(Context context,DialogDismissListener listener) {
		super(context,R.style.my_dialog);
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_pwd_save);
		context = getContext();

		pwd_txt = (EditText) findViewById(R.id.pwd_txt);

		pwdsubmit = (Button) findViewById(R.id.pwdsubmit);
		pwdsubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String pw = pwd_txt.getText().toString();
				if(!TextUtils.isEmpty(pw) && pw.length() > 5 ){
					Editor edt = SharedPreferencesUtils.getEditor(getContext());
					edt.putString(Configs.SHARED_PASSWORD, pw);
					edt.commit();
					Toast.makeText(getContext(), context.getResources().getString(R.string.change_success), Toast.LENGTH_SHORT).show();
					if(listener != null){
						listener.onDismiss(pw, 0, null);
					}
					dismiss();
				}else{
					Toast.makeText(getContext(), context.getResources().getString(R.string.pwd_format_fail), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		pwdcancel = (Button) findViewById(R.id.pwdcancel);
		
		pwdcancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	private DialogDismissListener listener;

}
