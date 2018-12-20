package com.kandi.settings.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.kandi.settings.KdsApplication;
import com.kandi.settings.R;
import com.kandi.settings.dialog.LoginDialog;
import com.kandi.settings.driver.ConfigDriver1xCarCtrlSetup;
import com.kandi.settings.driver.ConfigDriver1xCarCtrlSetup.eDrvVMode;
import com.kandi.settings.driver.ConfigDriver1xCarCtrlSetup.ePowerAssist;
import com.kandi.settings.driver.DriverServiceManger;

public class CarSetDriveSetFragment extends Fragment implements OnClickListener {
	private Activity mactivity;
	private ConfigDriver1xCarCtrlSetup model;
	// 杞�
	private Button controllightbtn;
	// 姝ｅ父
	private Button controlnormalbtn;
	// 閲�
	private Button controlrightbtn;
	// 杩愬姩
	private Button controllightbtn2;
	// 鑺傝兘
	private Button controlnormalbtn2;
	// 鏆磋簛
	private Button controltemperbtn2;
	
	private final int ReFresh_ACTION_MSG=1000;
	
	@Override
	public void onAttach(Activity activity) {
		mactivity = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.set_carset_driveset, container,
				false);
		initview(view);
		return view;
	}
	
	Handler mhandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ReFresh_ACTION_MSG:
				refreshPannel();
				break;
			}
		}
		
	};

	private void initview(View view) {
		controllightbtn2 = (Button) view.findViewById(R.id.controllightbtn2);
		controlnormalbtn2 = (Button) view.findViewById(R.id.controlnormalbtn2);
		controltemperbtn2 = (Button) view.findViewById(R.id.controltemperbtn2);
		controlnormalbtn = (Button) view.findViewById(R.id.controlnormalbtn);
		controllightbtn = (Button) view.findViewById(R.id.controllightbtn);
		controlrightbtn = (Button) view.findViewById(R.id.controlrightbtn);
		refreshPannel();
		controllightbtn2.setOnClickListener(this);
		controlnormalbtn2.setOnClickListener(this);
		controltemperbtn2.setOnClickListener(this);
		controllightbtn.setOnClickListener(this);
		controlnormalbtn.setOnClickListener(this);
		controlrightbtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.controllightbtn:
			try {
				if (model != null) {
					model.setPowerAssistedSteering(ePowerAssist.ASSISTED_LOW);
				} else {
					Toast.makeText(mactivity, getResources().getString(R.string.warning_service),
							Toast.LENGTH_SHORT).show();
				}
				int res = 0;
				if (res == 0) {
					controllightbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_01_on);
					controlnormalbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn3_02_off);
					controlrightbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_02_off);
				} else {
					// controllightbtn.setBackgroundResource(R.drawable.set_shoots_control_btn2_02_off);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		case R.id.controlnormalbtn:
			try {
				// int res = ssd1.zhengchang();
				if (model != null) {
					model.setPowerAssistedSteering(ePowerAssist.ASSISTED_MIDDLE);
				} else {
					Toast.makeText(mactivity, getResources().getString(R.string.warning_service),
							Toast.LENGTH_SHORT).show();
				}

				int res = 0;
				if (res == 0) {
					controllightbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_01_off);
					controlnormalbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn3_02_on);
					controlrightbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_02_off);
				} else {
					// controlnormalbtn.setBackgroundResource(R.drawable.set_shoots_control_btn2_02_off);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		case R.id.controlrightbtn:
			try {
				if (model != null) {
					model.setPowerAssistedSteering(ePowerAssist.ASSISTED_HIGH);
				} else {
					Toast.makeText(mactivity, getResources().getString(R.string.warning_service),
							Toast.LENGTH_SHORT).show();
				}

				// int res = ssd1.zhong();
				int res = 0;
				if (res == 0) {
					controllightbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_01_off);
					controlnormalbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn3_02_off);
					controlrightbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_02_on);
				} else {
					// controlrightbtn.setBackgroundResource(R.drawable.set_shoots_control_btn2_02_off);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		case R.id.controllightbtn2:
			try {
				// int res = ssd1.yundong();
				int res = 0;
				if (model != null) {
					model.setDrvMode(eDrvVMode.DRVMODE_SPORT);
				}else{
					Toast.makeText(mactivity, getResources().getString(R.string.warning_service),
							Toast.LENGTH_SHORT).show();
				}

				if (res == 0) {
					controllightbtn2
							.setBackgroundResource(R.drawable.set_shoots_control_btn3_02_on);
					controlnormalbtn2
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_01_off);
					controltemperbtn2
							.setBackgroundResource(R.drawable.set_shoots_tap_btn_off);
				} else {
					// controllightbtn2.setBackgroundResource(R.drawable.set_shoots_control_btn2_02_off);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		case R.id.controlnormalbtn2:
			try {
				// int res = ssd1.jieneng();
				if(model!=null){
					model.setDrvMode(eDrvVMode.DRVMODE_ECO);
				}else{
					Toast.makeText(mactivity, getResources().getString(R.string.warning_service),
							Toast.LENGTH_SHORT).show();
				}
				
				int res = 0;
				if (res == 0) {
					controlnormalbtn2
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_01_on);
					controllightbtn2
							.setBackgroundResource(R.drawable.set_shoots_control_btn3_02_off);
					controltemperbtn2
							.setBackgroundResource(R.drawable.set_shoots_tap_btn_off);
				} else {
					// controlnormalbtn2.setBackgroundResource(R.drawable.set_shoots_control_btn2_02_off);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		case R.id.controltemperbtn2:
			if(!isLogined){
				if(loginDialog == null){
					loginDialog = new LoginDialog(getActivity(), new LoginDialog.OnLoginResult() {
						
						@Override
						public void result(boolean isSuccess) {
							isLogined = isSuccess;
							if(isSuccess){
								if(!KdsApplication.getInstance().isHotMode()){
									try {
										if(model!=null){
											model.setDrvMode(eDrvVMode.DRVMODE_TEMPER);//璁剧疆鏆磋簛妯″紡
										}else{
											Toast.makeText(mactivity, getResources().getString(R.string.warning_service),
													Toast.LENGTH_SHORT).show();
										}
										
										int res = 0;
										if (res == 0) {
											controlnormalbtn2
											.setBackgroundResource(R.drawable.set_shoots_control_btn2_01_off);
											controllightbtn2
											.setBackgroundResource(R.drawable.set_shoots_control_btn3_02_off);
											controltemperbtn2
											.setBackgroundResource(R.drawable.set_shoots_tap_btn_on);
										} else {
											// controlnormalbtn2.setBackgroundResource(R.drawable.set_shoots_control_btn2_02_off);
										}
									} catch (RemoteException e) {
										e.printStackTrace();
									}
									KdsApplication.getInstance().setHotMode(true);
								}else{
									try {
										// int res = ssd1.yundong();
										int res = 0;
										if (model != null) {
											model.setDrvMode(eDrvVMode.DRVMODE_SPORT);
										}else{
											Toast.makeText(mactivity, getResources().getString(R.string.warning_service),
													Toast.LENGTH_SHORT).show();
										}

										if (res == 0) {
											controllightbtn2
													.setBackgroundResource(R.drawable.set_shoots_control_btn3_02_on);
											controlnormalbtn2
													.setBackgroundResource(R.drawable.set_shoots_control_btn2_01_off);
											controltemperbtn2
													.setBackgroundResource(R.drawable.set_shoots_tap_btn_off);
										} else {
											// controllightbtn2.setBackgroundResource(R.drawable.set_shoots_control_btn2_02_off);
										}
									} catch (RemoteException e) {
										e.printStackTrace();
									}
									KdsApplication.getInstance().setHotMode(false);
								}
//								Editor edt = SharedPreferencesUtils.getEditor(getActivity());
//								edt.putBoolean(Configs.DEFUALT_HOTMODE, isHotMode);
//								edt.commit();
								isLogined = false;
							}
						}
					},true);
				}
				loginDialog.show();
				break;
			}
			break;

		default:
			break;
		}
	}
	
	boolean isLogined = false;
	private LoginDialog loginDialog = null;

	/**
	 * 灏嗘ā鍨嬫暟鎹洿鏂板埌灞忓箷
	 */
	private void refreshPannel() {
		model = DriverServiceManger.getInstance()
				.getConfigDriver1xCarCtrlSetup();

		if (model != null) {
			try {
				switch (model.getDrvMode()) {
				case DRVMODE_SPORT:
					controllightbtn2
							.setBackgroundResource(R.drawable.set_shoots_control_btn3_02_on);
					controlnormalbtn2
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_01_off);
					controltemperbtn2
							.setBackgroundResource(R.drawable.set_shoots_tap_btn_off);
					break;
				case DRVMODE_ECO:
					controllightbtn2
							.setBackgroundResource(R.drawable.set_shoots_control_btn3_02_off);
					controlnormalbtn2
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_01_on);
					controltemperbtn2
							.setBackgroundResource(R.drawable.set_shoots_tap_btn_off);
					break;
				case DRVMODE_TEMPER:
					controllightbtn2
							.setBackgroundResource(R.drawable.set_shoots_control_btn3_02_off);
					controlnormalbtn2
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_01_off);
					controltemperbtn2
							.setBackgroundResource(R.drawable.set_shoots_tap_btn_on);
					break;
				default:
					break;
				}

				switch (model.getPowerAssistedSteering()) {
				case ASSISTED_HIGH:
					controllightbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_01_off);
					controlnormalbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn3_02_off);
					controlrightbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_02_on);
					break;
				case ASSISTED_MIDDLE:
					controllightbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_01_off);
					controlnormalbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn3_02_on);
					controlrightbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_02_off);
					break;
				case ASSISTED_LOW:
					controllightbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_01_on);
					controlnormalbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn3_02_off);
					controlrightbtn
							.setBackgroundResource(R.drawable.set_shoots_control_btn2_02_off);
					break;

				default:
					break;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
				return;
			}

			// TODO: ...灏嗘ā鍨嬫暟鎹洿鏂板埌灞忓箷...
			if(KdsApplication.getInstance().isHotMode()){
				try {
					if(model!=null){
						model.setDrvMode(eDrvVMode.DRVMODE_TEMPER);//璁剧疆鏆磋簛妯″紡
					}else{
						Toast.makeText(mactivity, getResources().getString(R.string.warning_service),
								Toast.LENGTH_SHORT).show();
					}
					
					int res = 0;
					if (res == 0) {
						controlnormalbtn2
						.setBackgroundResource(R.drawable.set_shoots_control_btn2_01_off);
						controllightbtn2
						.setBackgroundResource(R.drawable.set_shoots_control_btn3_02_off);
						controltemperbtn2
						.setBackgroundResource(R.drawable.set_shoots_tap_btn_on);
					} else {
						// controlnormalbtn2.setBackgroundResource(R.drawable.set_shoots_control_btn2_02_off);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		} else {
			Toast.makeText(mactivity, getResources().getString(R.string.warning_service), Toast.LENGTH_SHORT)
					.show();
			Log.e("KDSERVICE",
					"SystemSettingActivity1_1.refreshPannel() service is null");
		}
	}

	@Override
	public void onDestroy() {
		if(loginDialog != null){
			loginDialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public void onPause() {
		if(loginDialog != null){
			loginDialog.dismiss();
		}
		super.onPause();
	}
	
	

}
