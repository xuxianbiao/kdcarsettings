package com.kandi.settings.fragment.setting.manager;

import com.kandi.settings.R;
import com.kandi.settings.dialog.ConfirmDialog;
import com.kandi.settings.driver.ConfigDriver32MgnChargerSetup;
import com.kandi.settings.driver.DriverServiceManger;
import com.kandi.settings.event.ParamFreshEvent;
import com.kandi.settings.utils.SharedPreferencesUtils;

import de.greenrobot.event.EventBus;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * 充电机管理
 * 
 * @author illidan
 *
 */
public class ChargerFragment extends Fragment {

	View view;

	private EditText chongdianjizuigaochongdiandianya;
	private EditText chongdianjizuigaochongdiandianliu;

	private ToggleButton teamBuyBaoDiTog;

	private Button controlnormalbtn2;

	ConfigDriver32MgnChargerSetup model;
	
	ConfirmDialog confirmDialog = null;
	//均衡使能配置
	private static final String CONFIG_JHSN = "setting_entry_juhengshineng";
	//最高电压
	private static final String CONFIG_MAX_U = "setting_entry_max_dianya";
	//最高电流
	private static final String CONFIG_MAX_I = "setting_entry_max_dianliu";
	
	SharedPreferences sh;
	
	private View findViewById(int id) {
		return view.findViewById(id);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fg_charger, null);
		sh = SharedPreferencesUtils.getSharedPreferences(getActivity());
		initView();
		initEvent();
		return view;
	}

	private void initView() {
		chongdianjizuigaochongdiandianya = (EditText) findViewById(R.id.chongdianjizuigaochongdiandianya);
		chongdianjizuigaochongdiandianliu = (EditText) findViewById(R.id.chongdianjizuigaochongdiandianliu);
		controlnormalbtn2 = (Button) findViewById(R.id.controlnormalbtn2);
		controlnormalbtn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(confirmDialog == null){
					confirmDialog = new ConfirmDialog(getActivity(), new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(saveModel()){
								Toast.makeText(getActivity(), getResources().getString(R.string.save_success), Toast.LENGTH_SHORT).show();								
							}
							Editor ed = sh.edit();
							ed.putBoolean(CONFIG_JHSN, teamBuyBaoDiTog.isChecked());
							ed.commit();
						}
					});
				}
				confirmDialog.show();
			}
		});
		teamBuyBaoDiTog = (ToggleButton) findViewById(R.id.teamBuyBaoDiTog);
		teamBuyBaoDiTog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						// if(model != null) {
						// model.setBalanceEnabled(arg1);
						// }
						chongdianjizuigaochongdiandianya.setEnabled(!arg1);
						chongdianjizuigaochongdiandianliu.setEnabled(!arg1);
					}
				});
		
		teamBuyBaoDiTog.setChecked(sh.getBoolean(CONFIG_JHSN, false));
	}
	
	private void initEvent() {
		EventBus.getDefault().register(this, "onParamFreshCharger",ParamFreshEvent.class);
	}
	
	public void onParamFreshCharger(ParamFreshEvent event){
		chongdianjizuigaochongdiandianya.setEnabled(true);
		chongdianjizuigaochongdiandianliu.setEnabled(true);
		refreshPannel();
	}

	/**
	 * 将屏幕界面控件数据存入模型
	 */
	private boolean saveModel() {
		model = DriverServiceManger.getInstance().getConfigDriver32MgnChargerSetup();

		if (model != null) {
			// TODO:将屏幕界面控件数据存入模型
			model.setMaxChargingVotage(Float
					.parseFloat(chongdianjizuigaochongdiandianya.getText().toString()));
			model.setMaxChargingAmp(Float
					.parseFloat(chongdianjizuigaochongdiandianliu.getText().toString()));
			model.setBalanceEnabled(teamBuyBaoDiTog.isChecked());

			int ret = -1;
			try {
				ret = model.commitBattaryChargingParam();
				chongdianjizuigaochongdiandianya.setEnabled(false);
				chongdianjizuigaochongdiandianliu.setEnabled(false);
			} catch (RemoteException e) {
				e.printStackTrace();
				return false;
			}

			if (ret != 0) {
				// TODO: 弹对话框通知用户存储失败
				Toast.makeText(getActivity(), this.getString(R.string.warning_set_error), Toast.LENGTH_LONG)
						.show();
				// 重新刷新屏幕
				refreshPannel();
			}
		} else {
			Toast.makeText(getActivity(), this.getString(R.string.warning_service), Toast.LENGTH_LONG)
					.show();
			Log.e("KDSERVICE","SystemSettingActivity3_1.saveModel() service is null");
		}
		return false;
	}

	/**
	 * 将模型数据更新到屏幕
	 */
	private void refreshPannel() {
		model = DriverServiceManger.getInstance()
				.getConfigDriver32MgnChargerSetup();

		if (model != null) {
			try {
				model.retrieveBattaryChargingParam();
			} catch (RemoteException e) {
				e.printStackTrace();
				return;
			}

			// TODO: ...将模型数据更新到屏幕...
			chongdianjizuigaochongdiandianliu.setText(model.getMaxChargingAmp()
					+ "");
			chongdianjizuigaochongdiandianya.setText(model
					.getMaxChargingVotage() + "");
			teamBuyBaoDiTog.setChecked(model.isBalanceEnabled());
			// TODO: ...将模型数据更新到屏幕...

		} else {
			Toast.makeText(getActivity(), this.getString(R.string.warning_service), Toast.LENGTH_LONG).show();
			Log.e("KDSERVICE",
					"SystemSettingActivity3_2.refreshPannel() service is null");
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
