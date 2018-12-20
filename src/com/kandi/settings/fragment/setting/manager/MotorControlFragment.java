package com.kandi.settings.fragment.setting.manager;

import com.kandi.settings.R;
import com.kandi.settings.dialog.ConfirmDialog;
import com.kandi.settings.driver.ConfigDriver34MgnMotorCtrlSetup;
import com.kandi.settings.driver.DriverServiceManger;
import com.kandi.settings.event.ParamFreshEvent;

import de.greenrobot.event.EventBus;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * 电机控制管理
 * @author illidan
 *
 */
public class MotorControlFragment extends Fragment {
	View view;
	private View findViewById(int id) {
		return view.findViewById(id);
	}
	
	ConfirmDialog confirmDialog;
	
	//保存
	private Button controlnormalbtn2;
	
	private EditText caogaozhuansuxianding;
	private EditText taisuzhuanjuxianding;
	private EditText soc;
	
	private ConfigDriver34MgnMotorCtrlSetup model;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fg_motor_control, null);
		initViews();
		initEvent();
		return view;
	}

	private void initViews(){
		caogaozhuansuxianding = (EditText) findViewById(R.id.caogaozhuansuxianding);
		taisuzhuanjuxianding = (EditText) findViewById(R.id.taisuzhuanjuxianding);
		soc = (EditText) findViewById(R.id.soc);
		controlnormalbtn2 = (Button) findViewById(R.id.controlnormalbtn2);
		controlnormalbtn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(confirmDialog == null){
					confirmDialog = new ConfirmDialog(getActivity(), new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							saveModel();
						}
					});
				}
				confirmDialog.show();
			}
		});
	}
	
	private void initEvent() {
		EventBus.getDefault().register(this, "onParamFreshMotor",ParamFreshEvent.class);
	}
	
	public void onParamFreshMotor(ParamFreshEvent event){
		caogaozhuansuxianding.setEnabled(true);
		taisuzhuanjuxianding.setEnabled(true);
		soc.setEnabled(true);
		refreshPannel();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refreshPannel();
	}
	
	/**
	 * 将模型数据更新到屏幕
	 */
	private void refreshPannel() {
		model = DriverServiceManger.getInstance().getConfigDriver34MgnMotorCtrlSetup();

		if(model != null) {
			try {
				model.retrieveBcuParam();
			}catch (RemoteException e) {
				e.printStackTrace();
				return;
			}

			//TODO: ...将模型数据更新到屏幕...
			//TODO: ...将模型数据更新到屏幕...
			
			caogaozhuansuxianding.setText(model.getMaxRpmLimit()+"");
			taisuzhuanjuxianding.setText(model.getIdleTorqueLimit()+"");
			soc.setText(model.getSOC()+"");
			
			//TODO: ...将模型数据更新到屏幕...
			//TODO: ...将模型数据更新到屏幕...

		}
		else {
			Toast.makeText(getActivity(), this.getString(R.string.warning_service), Toast.LENGTH_LONG).show();
			Log.e("KDSERVICE", "SystemSettingActivity3_4.refreshPannel() service is null");
		}
	}
	
	/**
	 * 将屏幕界面控件数据存入模型
	 */
	private void saveModel() {
		model = DriverServiceManger.getInstance().getConfigDriver34MgnMotorCtrlSetup();

		if(model != null) {
			
			//TODO:将屏幕界面控件数据存入模型
			
			model.setMaxRpmLimit(Integer.parseInt(caogaozhuansuxianding.getText()+""));
			model.setIdleTorqueLimit(Integer.parseInt(taisuzhuanjuxianding.getText()+""));
			model.setSOC(Float.parseFloat(soc.getText()+""));
			
			int ret = -1;
			try {
				ret = model.commitBcuParam();
				caogaozhuansuxianding.setEnabled(false);
				taisuzhuanjuxianding.setEnabled(false);
				soc.setEnabled(false);
				Toast.makeText(getActivity(), this.getString(R.string.save_success), Toast.LENGTH_SHORT).show();
			}catch (RemoteException e) {
				e.printStackTrace();
				return;
			}
	
			if(ret != 0) {
				
				//TODO: 弹对话框通知用户存储失败
				
				Toast.makeText(getActivity(), this.getString(R.string.warning_set_error), Toast.LENGTH_LONG).show();
				//重新刷新屏幕
				refreshPannel();
			}
		}
		else {
			Toast.makeText(getActivity(), this.getString(R.string.warning_service), Toast.LENGTH_LONG).show();
			Log.e("KDSERVICE", "SystemSettingActivity3_1.saveModel() service is null");
		}
	}

	@Override
	public void onDestroy() {
		if(confirmDialog != null){
			confirmDialog.dismiss();
			confirmDialog = null;
		}
		super.onDestroy();
		EventBus.getDefault().unregister(this, ParamFreshEvent.class);
	}
	
	@Override
	public void onPause() {
		if(confirmDialog != null){
			confirmDialog.dismiss();
			confirmDialog = null;
		}
		super.onPause();
	}
	
}
