package com.kandi.settings.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kandi.settings.Configs;
import com.kandi.settings.R;
import com.kandi.settings.utils.SharedPreferencesUtils;

public class LoginDialog extends Dialog {

	private Button zg;
	private Button zg1;
	private Button subbtn;
	private EditText usernameet;
	private EditText pwdet;
	private LinearLayout oldview;
	private LinearLayout newview;
	
	String pw;
	String truePassword;
	String adminPassword;
	Context context;
	
	boolean isLoginClick = false;
	boolean isShow = false;
	
	public LoginDialog(Context context,OnLoginResult loginResult,boolean isShow) {
		super(context,R.style.my_dialog);
		this.loginResult = loginResult;
		this.isShow = isShow;
		adminPassword = SharedPreferencesUtils.getSharedPreferences(context).getString(Configs.SHARED_PASSWORD, Configs.DEFUALT_ADMIN_PASSWORD);
		truePassword = SharedPreferencesUtils.getSharedPreferences(context).getString(Configs.SHARED_PASSWORD, Configs.DEFUALT_PASSWORD);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.login);
		context = getContext();
		oldview = (LinearLayout) findViewById(R.id.oldview);
		newview = (LinearLayout) findViewById(R.id.newview);
		if(isShow){
			oldview.setVisibility(View.VISIBLE);
			newview.setVisibility(View.GONE);
		}else{
			oldview.setVisibility(View.GONE);
			newview.setVisibility(View.VISIBLE);
		}
		usernameet = (EditText) findViewById(R.id.usernameet);
		pwdet = (EditText) findViewById(R.id.pwdet);
		subbtn = (Button) findViewById(R.id.subbtn);
		zg = (Button) findViewById(R.id.zg);
		zg1 = (Button) findViewById(R.id.zg1);
		
		subbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//取消
				dismiss();
			}
		});
		
		zg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 登陆
				isLoginClick = false;
				String userName = usernameet.getText().toString();
				pw = pwdet.getText().toString();
				if(TextUtils.isEmpty(pw)){
					Toast.makeText(getContext(), context.getResources().getString(R.string.pwd_not_null), Toast.LENGTH_SHORT).show();
				}else{
					if(truePassword.equals(pw)||adminPassword.equals(pw)){
						isLoginClick = true;
						pwdet.setText("");
						dismiss();						
					}else{
						Toast.makeText(getContext(), context.getResources().getString(R.string.pwd_is_wrong), Toast.LENGTH_SHORT).show();						
					}
				}
			}
		});
		
		zg1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//取消
				dismiss();
			}
		});
		
		usernameet.setText(Configs.MANAGER_USER_NAME);
	}
	
	
	
	@Override
	protected void onStart() {
		isLoginClick  = false;
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		//触发登陆结果
		if(loginResult != null){
			loginResult.result(isLoginClick && (truePassword.equals(pw)||adminPassword.equals(pw)));
		}
	}
	
	public void removeLoginResult(){
		this.loginResult = null;
	}
	
	private OnLoginResult loginResult;
	
	public interface OnLoginResult{
		
		public void result(boolean isSuccess);
	}
}
