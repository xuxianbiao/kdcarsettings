package com.kandi.settings.fragment.setting.manager;

import java.io.File;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kandi.settings.Configs;
import com.kandi.settings.R;
import com.kandi.settings.dialog.DialogDismissListener;
import com.kandi.settings.dialog.EditPwdDialog;
import com.kandi.settings.dialog.SetFileUpgradeDialog;
import com.kandi.settings.driver.ConfigDriver38MgnUpgrade;
import com.kandi.settings.driver.DriverServiceManger;
import com.kandi.settings.utils.SharedPreferencesUtils;
/**
 * 密码、升级
 * @author illidan
 *
 */
public class PasswordUpdateFragment extends Fragment {
	View view;

	private ImageButton set_pwd_btn;
	
	private TextView vendor_type_txt;
	private TextView software_version_txt;
	private TextView hardware_version_txt;
	private TextView up_status_txt;
	private TextView upgrade_progress_txt;
	
	private Button check_choose_btn;
	private Button check_up_btn;
	private ProgressBar upgrade_progressbar;
	
	String m_FirmwarePath;
	
	EditPwdDialog pwDialog = null;
	
	TextView txtUserName;
	TextView txtPassWord;
	
	SetFileUpgradeDialog fileDialog;
	boolean UpStart=false;
	int up_timeout=0;
	final int Timeout_Num=5;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fg_password_update, null);
		initViews();
		return view;
	}

	
	
	@Override
	public void onResume() {
		super.onResume();
		String truePassword = SharedPreferencesUtils.getSharedPreferences(getActivity()).getString(Configs.SHARED_PASSWORD, Configs.DEFUALT_PASSWORD);
		if(txtPassWord != null){
			txtPassWord.setText(truePassword);			
		}
	}

	private void initViews() {
		m_FirmwarePath = this.getActivity().getResources().getString(R.string.FirmWare_Path);
		set_pwd_btn = (ImageButton) findViewById(R.id.set_pwd_btn);
		set_pwd_btn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(pwDialog == null){
					pwDialog = new EditPwdDialog(getActivity(),new DialogDismissListener() {
						
						@Override
						public void onDismiss(String info, int what, Object obj) {
							txtPassWord.setText(info);
						}
					});
				}
				pwDialog.show();
			}
			
		});
		
		vendor_type_txt = (TextView) findViewById(R.id.vendor_type_txt);
		software_version_txt = (TextView) findViewById(R.id.software_version_txt);
		hardware_version_txt = (TextView) findViewById(R.id.hardware_version_txt);
		up_status_txt = (TextView) findViewById(R.id.up_status_txt);
		upgrade_progress_txt = (TextView) findViewById(R.id.upgrade_progress_txt);
		
		check_choose_btn = (Button) findViewById(R.id.check_choose_btn);
		check_choose_btn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {	
				if(fileDialog == null){
					fileDialog = new SetFileUpgradeDialog(getActivity(), new DialogDismissListener() {
						
						@Override
						public void onDismiss(String info, int what, Object obj) {
							if(!TextUtils.isEmpty(info)){
								//接口回调回来的就是路径
								m_FirmwarePath = info;
							}
						}
					});
				}
				String str = m_FirmwarePath;
				str = str.substring(0, str.lastIndexOf('/')+1);	//获取文件的路径，去掉文件名称
				File f = new File(str);
				if(!f.exists()){
					f.mkdir();
				}
				fileDialog.setFileDir(str);
				fileDialog.show();
			}
			
		});
		check_up_btn = (Button) findViewById(R.id.check_up_btn);
		check_up_btn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ConfigDriver38MgnUpgrade model = DriverServiceManger.getInstance().getConfigDriver38MgnUpgrade();
				if(model == null){
					Toast.makeText(getActivity(), getResources().getString(R.string.warning_service), Toast.LENGTH_LONG).show();
					return;
				}
				Button check_up_btn = (Button)arg0.findViewById(arg0.getId());
				if(check_up_btn.getText().toString().equals(getResources().getString(R.string.updata_start))){
					up_timeout=0;
					UpStart = true;
					check_choose_btn.setEnabled(false);
					try {
						model.getSystemStartUpdataHex(m_FirmwarePath, 0, true);
						m_hadler.sendEmptyMessageDelayed(1001, 500);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}else{
					check_up_btn.setText(R.string.updata_start);
					check_choose_btn.setEnabled(true);
					UpStart = false;
					try {
						model.getSystemStartUpdataHex(m_FirmwarePath, 0, false);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
			
		});
		
		upgrade_progressbar = (ProgressBar) findViewById(R.id.upgrade_progressbar);
		
		txtUserName = (TextView) findViewById(R.id.manager_txt);
		txtPassWord = (TextView) findViewById(R.id.manager_pwd_txt);
		txtUserName.setText(Configs.MANAGER_USER_NAME);
	}
	
	private View findViewById(int managerTxt) {
		return view.findViewById(managerTxt);
	}

	/**
	 * 将模型数据更新到屏幕
	 */
	public void refreshPannel(ConfigDriver38MgnUpgrade model,int state) {
		
		
		if(model != null && UpStart) {
			try {
				model.getSystemUpdataStatus();
				software_version_txt.setText(""+model.getSoftwareVersion());
				hardware_version_txt.setText(""+model.getHardwareVersion());
				
				if(model.getVendorInfo()==1){
					vendor_type_txt.setText(getResources().getString(R.string.vendor_kandi));
				}else{
					vendor_type_txt.setText(getResources().getString(R.string.vendor_unkown));
				}
				
				if(state == 0x55){
					up_status_txt.setText(getResources().getString(R.string.upgrading));
					up_status_txt.setTextColor(Color.WHITE);
					check_up_btn.setText(getResources().getString(R.string.updata_stop));
					check_up_btn.setEnabled(true);
					check_choose_btn.setEnabled(false);
					
				}else if(state == 0xaa){
					up_status_txt.setText(getResources().getString(R.string.upgrade_finish));
					up_status_txt.setTextColor(Color.WHITE);
					check_up_btn.setText(getResources().getString(R.string.updata_start));
					check_up_btn.setEnabled(true);
					check_choose_btn.setEnabled(true);
					UpStart=false;
					upgrade_progress_txt.setText(""+100+getResources().getString(R.string.unit_percent));
					return;
				}else if(state == 0x01){
					up_status_txt.setText(getResources().getString(R.string.start_info_error));
					up_status_txt.setTextColor(Color.RED);
					check_up_btn.setText(getResources().getString(R.string.updata_start));
					check_up_btn.setEnabled(true);
					check_choose_btn.setEnabled(true);
					UpStart=false;
				}else if(state == 0x02){
					up_status_txt.setText(getResources().getString(R.string.upgrade_fail));
					up_status_txt.setTextColor(Color.RED);
					check_up_btn.setText(getResources().getString(R.string.updata_start));
					check_up_btn.setEnabled(true);
					check_choose_btn.setEnabled(true);
					UpStart=false;
				}else{
					up_status_txt.setText(getResources().getString(R.string.upgrade_init));
					up_status_txt.setTextColor(Color.WHITE);
					check_up_btn.setText(getResources().getString(R.string.updata_stop));
					check_up_btn.setEnabled(true);
					check_choose_btn.setEnabled(false);
				}
				//upgrade_progressbar.setProgress(model.getSystemUpdataStatus());
				upgrade_progress_txt.setText(""+model.getSystemUpdataStatus()+getResources().getString(R.string.unit_percent));
			}catch (RemoteException e) {
				e.printStackTrace();
				return;
			}
			//TODO: ...将模型数据更新到屏幕...
		}
		else {
			//Toast.makeText(getActivity(), getResources().getString(R.string.warning_service), Toast.LENGTH_LONG).show();
			Log.e("KDSERVICE", "SystemSettingActivity3_8.refreshPannel() service is null");
		}
	}
		
	@SuppressLint("HandlerLeak")
	Handler m_hadler = new Handler(){
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			ConfigDriver38MgnUpgrade model = DriverServiceManger.getInstance().getConfigDriver38MgnUpgrade();
			try {
				if(model != null){
					model.getSystemUpdataStatus();
					int state = model.getUpgradeState();
					if(state != 0xaa && UpStart){
						//0.5秒刷新一次界面
						m_hadler.sendEmptyMessageDelayed(1001, 500);
					}else if(up_timeout<=Timeout_Num){
						up_timeout++;
						m_hadler.sendEmptyMessageDelayed(1001, 500);
					}
					refreshPannel(model,state);
				}
			} catch (Exception e) {
			}
		}
	};
	@Override
	public void onDestroy() {
		if(pwDialog != null){
			pwDialog.dismiss();
			pwDialog = null;
		}
		if(fileDialog != null){
			fileDialog.dismiss();
			fileDialog = null;
		}
		super.onDestroy();
	}
	
	@Override
	public void onPause() {
		if(pwDialog != null){
			pwDialog.dismiss();
			pwDialog = null;
		}
		if(fileDialog != null){
			fileDialog.dismiss();
			fileDialog = null;
		}
		super.onPause();
	}
	
}
