package com.kandi.settings.fragment.setting.manager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.kandi.settings.R;
import com.kandi.settings.dialog.ConfirmDialog;
import com.kandi.settings.driver.ConfigDriver33MgnBcuSetup;
import com.kandi.settings.driver.ConfigDriver33MgnBcuSetup.eBcuProtocol;
import com.kandi.settings.driver.DriverServiceManger;
import com.kandi.settings.event.ParamFreshEvent;

import de.greenrobot.event.EventBus;
/**
 * BUC管理
 * @author illidan
 *
 */
public class BCUFragment extends Fragment implements View.OnClickListener{
	View view;
	private View findViewById(int id) {
		return view.findViewById(id);
	}
	
	ConfirmDialog confirmDialog;
	private CheckBox radioButton1;
	private CheckBox radioButton2;
	private CheckBox radioButton3;
	
	/**RTC低功耗使能*/
	private ToggleButton teamBuyBaoDiTog1;
	/**RTC低功耗CAN使能*/
	private ToggleButton teamBuyBaoDiTog2;
	/**电流环自动校准使能*/
	private ToggleButton teamBuyBaoDiTog3;
	/**均衡使能*/
	private ToggleButton teamBuyBaoDiTog4;
	/**清除电子粘连标志*/
	private ToggleButton teamBuyBaoDiTog5;
	
	/**保存*/
	private Button controlnormalbtn2;
	/**电池粘连检测周期*/
	private EditText dianchizhanlianjiancezhouqi;
	/**电流环选型*/
	private EditText dianliuhuanxuanxing;
	/**最小电池数目*/
	private EditText zuixiaodianchishumu;
	/**绝缘报警值*/
	private EditText insulationwval;
	/**绝缘故障值*/
	private EditText insulationeval;
	
	private ConfigDriver33MgnBcuSetup model;

	SharedPreferences sh;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fg_bcu, null);
		initViews();
		initEvent();
		return view;
	}
	
	private void initViews(){
		dianchizhanlianjiancezhouqi = (EditText) findViewById(R.id.dianchizhanlianjiancezhouqi);
		dianliuhuanxuanxing = (EditText) findViewById(R.id.dianliuhuanxuanxing);
		zuixiaodianchishumu = (EditText) findViewById(R.id.zuixiaodianchishumu);
		insulationwval = (EditText) findViewById(R.id.insulationwval);
		insulationeval = (EditText) findViewById(R.id.insulationeval);
		
		controlnormalbtn2 = (Button) findViewById(R.id.controlnormalbtn2);
		controlnormalbtn2.setOnClickListener(this);
		radioButton1 = (CheckBox) findViewById(R.id.radioButton1);
		radioButton2 = (CheckBox) findViewById(R.id.radioButton2);
		radioButton3 = (CheckBox) findViewById(R.id.radioButton3);
		
		teamBuyBaoDiTog1 = (ToggleButton) findViewById(R.id.teamBuyBaoDiTog1);
		teamBuyBaoDiTog2 = (ToggleButton) findViewById(R.id.teamBuyBaoDiTog2);
		teamBuyBaoDiTog3 = (ToggleButton) findViewById(R.id.teamBuyBaoDiTog3);
		teamBuyBaoDiTog4 = (ToggleButton) findViewById(R.id.teamBuyBaoDiTog4);
		teamBuyBaoDiTog5 = (ToggleButton) findViewById(R.id.teamBuyBaoDiTog5);
	}
	
	private void initEvent() {
		EventBus.getDefault().register(this, "onParamFreshBCU",ParamFreshEvent.class);
	}
	
	public void onParamFreshBCU(ParamFreshEvent event){
		dianchizhanlianjiancezhouqi.setEnabled(true);
		dianliuhuanxuanxing.setEnabled(true);
		zuixiaodianchishumu.setEnabled(true);
		insulationwval.setEnabled(true);
		insulationeval.setEnabled(true);
		refreshPannel();
	}
	
	/**
	 * 将模型数据更新到屏幕
	 */
	private void refreshPannel() {
		model = DriverServiceManger.getInstance().getConfigDriver33MgnBcuSetup();

		if(model != null) {
			try {
				model.retrieveBcuParam();
			}catch (RemoteException e) {
				e.printStackTrace();
				return;
			}
			dianchizhanlianjiancezhouqi.setText(model.getBattStickTestCycle()+"");
			dianliuhuanxuanxing.setText(model.getCurrencyLoopType()+"");
			zuixiaodianchishumu.setText(model.getMinBattaryQty()+"");
			insulationwval.setText(model.getInsulationwVal()+"");
			insulationeval.setText(model.getInsulationeVal()+"");
			
			radioButton1.setChecked(model.isBcuProtocolEnabled(eBcuProtocol.PROTOCAL_WANXIANG));
			radioButton2.setChecked(model.isBcuProtocolEnabled(eBcuProtocol.PROTOCAL_DAYOU));
			radioButton3.setChecked(model.isBcuProtocolEnabled(eBcuProtocol.PROTOCAL_KANGDI));
			
			teamBuyBaoDiTog1.setChecked(model.isRtcLowPowerEnabled());
			teamBuyBaoDiTog2.setChecked(model.isRtcLowPowerCanEnabled());
			teamBuyBaoDiTog3.setChecked(model.isCurrencyLoopAutoCalEnabled());
			teamBuyBaoDiTog4.setChecked(model.isBalanceEnabled());
			teamBuyBaoDiTog5.setChecked(model.isClearBatterEnabled());
			
			//TODO: ...将模型数据更新到屏幕...
	
		}
		else {
			Toast.makeText(getActivity(), this.getString(R.string.warning_service), Toast.LENGTH_LONG).show();
			Log.e("KDSERVICE", "SystemSettingActivity3_3.refreshPannel() service is null");
		}
	}
	
	
	public void saveModel() {
		model = DriverServiceManger.getInstance().getConfigDriver33MgnBcuSetup();

		if(model != null) {
			//TODO:将屏幕界面控件数据存入模型
			model.setBattStickTestCycle(Integer.parseInt(dianchizhanlianjiancezhouqi.getText()+""));
			model.setCurrencyLoopType(Integer.parseInt(dianliuhuanxuanxing.getText()+""));
			model.setMinBattaryQty(Integer.parseInt(zuixiaodianchishumu.getText()+""));
			model.setInsulationwVal(Integer.parseInt(insulationwval.getText()+""));
			model.setInsulationeVal(Integer.parseInt(insulationeval.getText()+""));
			int CheckBox_val = 0;
			if(radioButton1.isChecked()){
				CheckBox_val += 4;
			}
			if(radioButton2.isChecked()){
				CheckBox_val += 2;
			}
			if(radioButton3.isChecked()){
				CheckBox_val += 1;
			}
			model.setBcuProtocolEnabled(CheckBox_val);
			model.setRtcLowPowerEnabled(teamBuyBaoDiTog1.isChecked());
			model.setRtcLowPowerCanEnabled(teamBuyBaoDiTog2.isChecked());
			model.setCurrencyLoopAutoCalEnabled(teamBuyBaoDiTog3.isChecked());
			model.setBalanceEnabled(teamBuyBaoDiTog4.isChecked());
			model.setClearBatterEnabled(teamBuyBaoDiTog5.isChecked());
			
			int ret = -1;
			try {
				ret = model.commitBcuParam();
				dianchizhanlianjiancezhouqi.setEnabled(false);
				dianliuhuanxuanxing.setEnabled(false);
				zuixiaodianchishumu.setEnabled(false);
				insulationwval.setEnabled(false);
				insulationeval.setEnabled(false);
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
			//Toast.makeText(getActivity(), "充电机设置错误！", Toast.LENGTH_LONG).show();
			Toast.makeText(getActivity(), this.getString(R.string.warning_service), Toast.LENGTH_LONG).show();
			Log.e("KDSERVICE", "SystemSettingActivity3_1.saveModel() service is null");
		}
	}

	@Override
	public void onClick(View v) {
		
			switch (v.getId()) {
			/*case R.id.controlnormalbtn:
				try {
					int res = model.resetBCURelayStick();
					if(res==-1){
						Toast.makeText(getActivity(), "充电机设置错误！", Toast.LENGTH_LONG).show();
					}else{
						
					}
				} catch (RemoteException e) {
					e.printStackTrace();
					Toast.makeText(getActivity(), "充电机设置错误！", Toast.LENGTH_LONG).show();
				}
				break;*/
			case R.id.controlnormalbtn2:
				
				if(confirmDialog == null){
					confirmDialog = new ConfirmDialog(getActivity(), new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if(TextUtils.isEmpty(dianchizhanlianjiancezhouqi.getText().toString()) || 
									TextUtils.isEmpty(dianliuhuanxuanxing.getText().toString())||
									TextUtils.isEmpty(zuixiaodianchishumu.getText().toString()) ||
									TextUtils.isEmpty(insulationwval.getText().toString())||
									TextUtils.isEmpty(insulationeval.getText().toString())){
								Toast.makeText(getActivity(), getResources().getString(R.string.warning_param_imperfect), Toast.LENGTH_LONG).show();
							}else{
								saveModel();								
							}
						}
					});
				}
				confirmDialog.show();
				break;

			default:
				break;
			}
		
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshPannel();
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
