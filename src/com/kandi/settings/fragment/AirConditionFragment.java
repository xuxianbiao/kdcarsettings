package com.kandi.settings.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kandi.settings.R;
import com.kandi.settings.adapter.ModeWheelViewAdapter;
import com.kandi.settings.adapter.TempWheelAdapter;
import com.kandi.settings.driver.AirConditionDriver;
import com.kandi.settings.driver.DriverServiceManger;
import com.kandi.settings.widgets.AirNumberView;
import com.kandi.settings.widgets.ImageStateButton;
import com.kandi.settings.widgets.TosAdapterView;
import com.kandi.settings.widgets.WheelView;

/**
 * 绌鸿皟鐣岄潰
 * @author justin
 *
 */
public class AirConditionFragment extends Fragment implements OnClickListener{
	private View loadingView;
	private AirNumberView anv;

	protected static final int UPDATE_TEXT = 0;
	protected static final int UPDATE_PANNEL = 1;
	protected static final int AIR_DISABLED = -1;
	protected static final int RERESH_WHEELS  = 100;
	protected static final int SET_TEMPWHEELS = 101;
	protected static final int SET_WINDWHEELS = 102;
	
	int eventCount=0;
	int debug=0;
	private Timer timer = null;
	private TimerTask task;
	private TimerTask taskDelaySetWind;
	private TimerTask taskDelaySetTemp;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
			case UPDATE_TEXT:
				refreshACDisplay();
				break;
			
			case UPDATE_PANNEL:
				Log.d("AirConditionActivity","AIR-MSG(#" +eventCount+"): what="+msg.what);
				refreshPannelStatus();
				eventCount++;
				break;
			case AIR_DISABLED:
				setLoadingView(true); 	//disable
				break;
			case SET_TEMPWHEELS:
				setPresetTemp();
				break;
			case SET_WINDWHEELS:
				setPresetWind();
				break;
			case RERESH_WHEELS:
				refreshWheels();
				setLoadingView(false);
				break;
			}
		}
	};

	private List<String> tempWheelPosList = new ArrayList<String>();
	private List<String> windWheelPosList = new ArrayList<String>();
	private WheelView tempwv;
	private WheelView modewv;
	private TempWheelAdapter tempWvAdapter;
	private ModeWheelViewAdapter windWvAdapter;

	
	private TosAdapterView.OnItemSelectedListener mListener = new TosAdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(TosAdapterView<?> parent, View view, int pos, long id) {

//        	ToastUtil.showDbgToast(AirConditionFragment.this.getActivity(), "娓╁害閫変腑锛�+pos);
        	wantTempPosition = pos;
        	delaySetPresetTemp();
//        	setPresetTemp();
        }
        @Override
        public void onNothingSelected(TosAdapterView<?> parent) {
        }
    };
    
    private TosAdapterView.OnItemSelectedListener modeListener = new TosAdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(TosAdapterView<?> parent, View view, int pos, long id) {
//        	ToastUtil.showDbgToast(AirConditionFragment.this.getActivity(), "椋庨噺閫変腑锛�+pos);
        	watWindPosition = pos;
//        	setPresetWind();
        	delaySetPresetWind();

        	Log.d("OnItemSelectedListener", "wind wheel selected: "+pos);
        }
        @Override
        public void onNothingSelected(TosAdapterView<?> parent) {
        }
    };
    
	void setTempWheelPostion(int position, boolean isUserEvent) {
		if(position <0) {
			position = 0;
		}
		else if(position >= tempWheelPosList.size()) {
			position = tempWheelPosList.size() - 1;
		}

		tempwv.setSelection(position, true,isUserEvent);
		wantTempPosition = position;
	}

	View mainView;
	private View findViewById(int bgview2) {
		return mainView.findViewById(bgview2);
	}
	
	void setWindWheelPostion(int position, boolean isUserEvent) {
		if(position <0) {
			position = 0;
		}
		else if(position >= windWheelPosList.size()) {
			position = windWheelPosList.size() - 1;
		}

		modewv.setSelection(position, true,isUserEvent);
		watWindPosition = position;
	}
	
	@Override
	public void onResume() {
		this.refreshPannelStatus();
		///this.refreshCurrentTemp();
		//start timer only when the active resume
		if(timer == null) {
			initTimer();
		}		
		
		super.onResume();
	}
	@Override
	public void onPause() {
		//stop timer
		if(timer != null) {
			timer.cancel();
			timer = null;
		}
		super.onPause();
	}

	private LinearLayout mainview;
	
	private void initViewBg(){
		mainview = (LinearLayout) findViewById(R.id.mainview);
		mainview.setOnClickListener(null);
		anv = (AirNumberView) findViewById(R.id.anv);
//		anv.setTemperature(-12);
	}
	
	private void initTimer(){
		timer = new Timer();
	    task = new TimerTask() {
			@Override
			public void run() {
				try{
					mHandler.sendEmptyMessage(UPDATE_TEXT);
					//Log.d("AirConditionActivity","TimerTask.run()");
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		};
		//鑾峰彇杞﹀唴褰撳墠娓╁害
		timer.schedule(task, 3000, 3000);	//瀹氭椂鍣�000 ms
	}
	
	BaseReceiver baseReceiver;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mainView = inflater.inflate(R.layout.m_air_layout, null);
		this.initViewBg();
		this.initView();
		this.initTimer(); //init timer for full life cycle
//		refreshWheels();//鍒蜂竴涓嬮閲忔俯搴︾殑鏍囧噯
		refreshPannelStatus();
		if(baseReceiver == null){
			baseReceiver = new BaseReceiver();
			IntentFilter kdIntentFilter = new IntentFilter();  
			kdIntentFilter.addAction("com.driverlayer.kdos_driverserver");
			getActivity().registerReceiver(baseReceiver, kdIntentFilter);			
		}
		
		return mainView;
	}
	
	@Override
	public void onDestroyView() {
		if(baseReceiver != null){
			getActivity().unregisterReceiver(baseReceiver);
			baseReceiver = null;
		}
		super.onDestroyView();
	}
	
	ImageStateButton btnPtcPower;
	ImageStateButton btnAcPower;
	ImageStateButton btnAirPower;
	ImageButton btnTempUp;
	ImageButton btnTempDown;
	ImageButton btnWindUp;
	ImageButton btnWindDown;
	//ImageInterlockSwitchButtonSet btnSetBlowMode;
	ImageStateButton btnSetBlowMode[];
	//ImageInterlockSwitchButtonSet btnSetWindCycle;
	ImageStateButton btnAirExternalCycle;
	ImageStateButton btnAirInternalCycle;
	
	ImageStateButton btnDemistFrontWin;

	ImageView imageAirIconFan;
	ImageView imageAirIconPTC;
	ImageView imageAirIconAC;

	ImageButton imageDisableMaskTempWheel;
	ImageButton imageDisableMaskWindWheel;
	ImageButton imageDisableMaskWindMode;
	ImageButton imageDisableMaskAirExternalCycle;
	ImageButton imageDisableMaskAirInternalCycle;
	private ImageView iv_ptc_light;
	private ImageView iv_ac_light;
	private ImageView iv_on_light;

	private void setLoadingView(boolean isLoading) {
		if(isLoading){
			if(loadingView.getVisibility() != View.VISIBLE){
				loadingView.setVisibility(View.VISIBLE);
			}
		}else{
			if(loadingView.getVisibility() != View.GONE){
				loadingView.setVisibility(View.GONE);
			}
		}
	}

	private void initView() {
		loadingView = (View)findViewById(R.id.loadingView);
		setLoadingView(false);
		
		//temp wheel range: 18~32 degree
		tempWheelPosList.add("18");
		tempWheelPosList.add("19");
		tempWheelPosList.add("20");
		tempWheelPosList.add("21");
		tempWheelPosList.add("22");
		tempWheelPosList.add("23");
		tempWheelPosList.add("24");
		tempWheelPosList.add("25");
		tempWheelPosList.add("26");
		tempWheelPosList.add("27");
		tempWheelPosList.add("28");
		tempWheelPosList.add("29");
		tempWheelPosList.add("30");
		tempWheelPosList.add("31");
		tempWheelPosList.add("32");
//		tempWheelPosList.add("33");
		//tempWheelPosList.add("34");	//INFO:闇�眰鍙樻洿2015-07-01锛屾祴璇曞彂鐜扮┖璋冩敮鎸佹俯搴﹁寖鍥�8-33搴�

		//wind wheel range 1~8 level
		windWheelPosList.add("1");
		windWheelPosList.add("2");
		windWheelPosList.add("3");
		windWheelPosList.add("4");
		windWheelPosList.add("5");
		windWheelPosList.add("6");
		windWheelPosList.add("7");
		windWheelPosList.add("8");

		tempWvAdapter = new TempWheelAdapter(tempWheelPosList, getActivity());
		windWvAdapter = new ModeWheelViewAdapter(windWheelPosList, getActivity());

		tempwv = (WheelView) findViewById(R.id.tempwv);
		tempwv.setScrollCycle(false);
		tempwv.setAdapter(tempWvAdapter);
		tempwv.setOnItemSelectedListener(mListener);
		
		modewv = (WheelView) findViewById(R.id.modewv);
		modewv.setScrollCycle(false);
		modewv.setAdapter(windWvAdapter);
		modewv.setOnItemSelectedListener(modeListener);
		
		imageAirIconFan = (ImageView)this.findViewById(R.id.imageAirIconFan);
		imageAirIconPTC = (ImageView)this.findViewById(R.id.imageAirIconPTC);
		imageAirIconAC = (ImageView)this.findViewById(R.id.imageAirIconAC);
		imageAirIconFan.setVisibility(View.GONE);
		imageAirIconPTC.setVisibility(View.GONE);
		imageAirIconAC.setVisibility(View.GONE);
		
		btnPtcPower = new ImageStateButton(this.getResources(), 
				(ImageButton)this.findViewById(R.id.btnPtcPower),
				R.drawable.air_btn_ptc_off,		//off,
				R.drawable.air_btn_ptc_on,		//on,
				R.drawable.air_btn_ptc_off_pressed,		//off_pressed,
				R.drawable.air_btn_ptc_on_pressed,		//on_pressed,
				R.drawable.air_btn_ptc_disabled,		//disabled
				R.drawable.air_btn_ptc_disabled,		//disabled
				false
				);
		btnPtcPower.setOnClickListener(this);

		btnAcPower = new ImageStateButton(this.getResources(), 
				(ImageButton)this.findViewById(R.id.btnAcPower),
				R.drawable.air_btn_ac_off,		//off,
				R.drawable.air_btn_ac_on,		//on,
				R.drawable.air_btn_ac_off_pressed,		//off_pressed,
				R.drawable.air_btn_ac_on_pressed,		//on_pressed,
				R.drawable.air_btn_ac_disabled,		//disabled
				R.drawable.air_btn_ac_disabled,		//disabled
				false
				);
		btnAcPower.setOnClickListener(this);
		
		btnAirPower = new ImageStateButton(this.getResources(), 
				(ImageButton)this.findViewById(R.id.btnWindPower),
				R.drawable.air_btn_temperature_off,	//off,
				R.drawable.air_btn_temperature_on,	//on,
				R.drawable.air_btn_temperature_off_pressed,	//off_pressed,
				R.drawable.air_btn_temperature_on_pressed,	//on_pressed,
				R.drawable.air_btn_temperature_disabled,	//disabled
				R.drawable.air_btn_temperature_disabled,	//disabled
				false
				);
		btnAirPower.setOnClickListener(this);
		
		btnTempUp = (ImageButton)this.findViewById(R.id.btnTempUp);
		btnTempUp.setOnClickListener(this);
		btnTempDown = (ImageButton)this.findViewById(R.id.btnTempDown);
		btnTempDown.setOnClickListener(this);
		btnWindUp = (ImageButton)this.findViewById(R.id.btnWindUp);
		btnWindUp.setOnClickListener(this);
		btnWindDown = (ImageButton)this.findViewById(R.id.btnWindDown);
		btnWindDown.setOnClickListener(this);
		
		tempwv.setUnselectedAlpha(0.4f);
		tempwv.setSoundEffectsEnabled(true);
		btnTempUp.setSoundEffectsEnabled(false);
		btnTempDown.setSoundEffectsEnabled(false);

		modewv.setUnselectedAlpha(0.3f);
		modewv.setSoundEffectsEnabled(true);
		btnWindUp.setSoundEffectsEnabled(false);
		btnWindDown.setSoundEffectsEnabled(false);
		
		iv_ptc_light = (ImageView)findViewById(R.id.iv_ptc_light);
		iv_ac_light = (ImageView)findViewById(R.id.iv_ac_light);
		iv_on_light = (ImageView)findViewById(R.id.iv_on_light);
		btnSetBlowMode = new ImageStateButton[5];
		btnSetBlowMode[0] = new ImageStateButton(this.getResources(), 
				(ImageButton)this.findViewById(R.id.btnWinBlowMode1),
				R.drawable.air_btn_airmode_head_off,
				R.drawable.air_btn_airmode_head_on,		
				R.drawable.air_btn_airmode_head_off_press,
				R.drawable.air_btn_airmode_head_on_press,	
				R.drawable.air_btn_airmode_head_off,	//disable
				R.drawable.air_btn_airmode_head_off,	//disable
				false
				);
		btnSetBlowMode[0].setOnClickListener(this);

		btnSetBlowMode[1] = new ImageStateButton(this.getResources(), 
				(ImageButton)this.findViewById(R.id.btnWinBlowMode2),
				R.drawable.air_btn_airmode_head_and_foot_off,
				R.drawable.air_btn_airmode_head_and_foot_on,		
				R.drawable.air_btn_airmode_head_and_foot_off_press,
				R.drawable.air_btn_airmode_head_and_foot_on_press,	
				R.drawable.air_btn_airmode_head_and_foot_off,	//disable
				R.drawable.air_btn_airmode_head_and_foot_off,	//disable
				false
				);
		btnSetBlowMode[1].setOnClickListener(this);

		btnSetBlowMode[2] = new ImageStateButton(this.getResources(), 
				(ImageButton)this.findViewById(R.id.btnWinBlowMode3),
				R.drawable.air_btn_airmode_foot_off,
				R.drawable.air_btn_airmode_foot_on,		
				R.drawable.air_btn_airmode_foot_off_press,
				R.drawable.air_btn_airmode_foot_on_press,	
				R.drawable.air_btn_airmode_foot_off,	//disable
				R.drawable.air_btn_airmode_foot_off,	//disable
				false
				);
		btnSetBlowMode[2].setOnClickListener(this);

		btnSetBlowMode[3] = new ImageStateButton(this.getResources(), 
				(ImageButton)this.findViewById(R.id.btnWinBlowMode4),
				R.drawable.air_btn_airmode_win_foot_off,
				R.drawable.air_btn_airmode_win_foot_on,		
				R.drawable.air_btn_airmode_win_foot_off_press,
				R.drawable.air_btn_airmode_win_foot_on_press,	
				R.drawable.air_btn_airmode_win_foot_off,	//disable
				R.drawable.air_btn_airmode_win_foot_off,	//disable
				false
				);
		btnSetBlowMode[3].setOnClickListener(this);
		
		btnSetBlowMode[4] = new ImageStateButton(this.getResources(), 
				(ImageButton)this.findViewById(R.id.btnWinBlowMode5),
				R.drawable.air_btn_airmode_front_off,
				R.drawable.air_btn_airmode_front_on,		
				R.drawable.air_btn_airmode_front_off_press,
				R.drawable.air_btn_airmode_front_on_press,	
				R.drawable.air_btn_airmode_front_off,	//disable
				R.drawable.air_btn_airmode_front_off,	//disable
				false
				);
		btnSetBlowMode[4].setOnClickListener(this);
		
		btnAirExternalCycle = new ImageStateButton(this.getResources(), 
				(ImageButton)this.findViewById(R.id.btnAirExternalCycle),
				R.drawable.air_btn_cycle_external_circulation_off,
				R.drawable.air_btn_cycle_external_circulation_on,		
				R.drawable.air_btn_cycle_external_circulation_off_press,
				R.drawable.air_btn_cycle_external_circulation_on_press,	
				R.drawable.air_btn_cycle_external_circulation_off,	//disable
				R.drawable.air_btn_cycle_external_circulation_off,	//disable
				false
				);
		btnAirExternalCycle.setOnClickListener(this);
		btnAirInternalCycle = new ImageStateButton(this.getResources(), 
				(ImageButton)this.findViewById(R.id.btnAirInternalCycle),
				R.drawable.air_btn_cycle_internal_circulation_off,
				R.drawable.air_btn_cycle_internal_circulation_on,		
				R.drawable.air_btn_cycle_internal_circulation_off_press,
				R.drawable.air_btn_cycle_internal_circulation_on_press,	
				R.drawable.air_btn_cycle_internal_circulation_off,	//disable
				R.drawable.air_btn_cycle_internal_circulation_off,	//disable
				false
				);
		btnAirInternalCycle.setOnClickListener(this);

		imageDisableMaskTempWheel = (ImageButton)this.findViewById(R.id.imageDisableMaskTempWheel);
		imageDisableMaskWindWheel = (ImageButton)this.findViewById(R.id.imageDisableMaskWindWheel);
		imageDisableMaskWindMode = (ImageButton)this.findViewById(R.id.imageDisableMaskWindMode);
		imageDisableMaskAirExternalCycle = (ImageButton)this.findViewById(R.id.imageDisableMaskAirExternalCycle);
		imageDisableMaskAirInternalCycle = (ImageButton)this.findViewById(R.id.imageDisableMaskAirInternalCycle);
		
		imageDisableMaskTempWheel.setVisibility(View.GONE);
		imageDisableMaskWindWheel.setVisibility(View.GONE);
		imageDisableMaskWindMode.setVisibility(View.GONE);
		imageDisableMaskAirExternalCycle.setVisibility(View.GONE);
		imageDisableMaskAirInternalCycle.setVisibility(View.GONE);
		
	}
	
	@Override
	public void onClick(View v) {
		AirConditionDriver airPannelDrv = DriverServiceManger.getInstance()
				.getAirConditionDriver();
		if (airPannelDrv != null) {
			int pos;
			int ret=0;
			try {
				switch (v.getId()) {
				case R.id.btnPtcPower:
					ret = airPannelDrv
							.setPtcPowerOn(!this.btnPtcPower.isSwitchOn());
					
//					 if(airPannelDrv.isPtcPowerOn()) 
					 {
//					 airPannelDrv.setWindPowerOn(true);
					 btnAirPower.switchOn(true, false);
					 iv_on_light.setVisibility(View.VISIBLE);
					 btnPtcPower.switchOn(!btnPtcPower.isSwitchOn(), false);
						if(btnPtcPower.isSwitchOn())
						{
							iv_ptc_light.setVisibility(View.VISIBLE);
						}
						else
						{
							iv_ptc_light.setVisibility(View.INVISIBLE);
						}
					 }
//					enablePannelByPower();
					break;
				case R.id.btnAcPower:
					ret = airPannelDrv
							.setACPowerOn(!this.btnAcPower.isSwitchOn());
					
					// if(airPannelDrv.isACPowerOn()) {
					// airPannelDrv.setWindPowerOn(true);
					 btnAirPower.switchOn(true, false);
					 iv_on_light.setVisibility(View.VISIBLE);
					 btnAcPower.switchOn(!btnAcPower.isSwitchOn(), false);
					 if(btnAcPower.isSwitchOn())
						{
						 iv_ac_light.setVisibility(View.VISIBLE);
						}
						else
						{
							iv_ac_light.setVisibility(View.INVISIBLE);
						}
					// }
					///enablePannelByPower();
					break;
				case R.id.btnWindPower:
					ret = airPannelDrv
							.setWindPowerOn(!this.btnAirPower.isSwitchOn());
//					if (!airPannelDrv.isWindPowerOn()) {
//						airPannelDrv.setACPowerOn(false);
						this.btnAirPower.switchOn(!this.btnAirPower.isSwitchOn(),false);
						 if(btnAirPower.isSwitchOn())
							{
							 iv_on_light.setVisibility(View.VISIBLE);
							}
							else
							{
								iv_on_light.setVisibility(View.INVISIBLE);
							}
//					}
					///enablePannelByPower();
					break;
				case R.id.btnTempUp:
					pos = tempwv.getSelectedItemPosition() + 1;
					//setTempWheelPostion(pos, false);
					setTempWheelPostion(pos, true);
					//airPannelDrv.setPresetTemp(tempwv.getSelectedItemPosition()+1);	//1~16 level (18~34 degree)						
					break;
				case R.id.btnTempDown:
					pos = tempwv.getSelectedItemPosition() - 1;
					//setTempWheelPostion(pos, false);
					setTempWheelPostion(pos, true);
					//airPannelDrv.setPresetTemp(tempwv.getSelectedItemPosition()+1);//1~16 level (18~34 degree)
					break;
				case R.id.btnWindUp:
					pos = modewv.getSelectedItemPosition() + 1;
					setWindWheelPostion(pos, true);
        			//airPannelDrv.setWindSpeed(modewv.getSelectedItemPosition()+1); //1~8 level
					break;
				case R.id.btnWindDown:
					pos = modewv.getSelectedItemPosition() - 1;
					setWindWheelPostion(pos, true);
        			//airPannelDrv.setWindSpeed(modewv.getSelectedItemPosition()+1); //1~8 level
					break;
				case R.id.btnWinBlowMode1:
					ret = airPannelDrv
							.setAirBlowMode(AirConditionDriver.eAirBlowMode.BLOW_HEAD);
					setWindBlowButton(0);
					break;
				case R.id.btnWinBlowMode2:
					ret = airPannelDrv
							.setAirBlowMode(AirConditionDriver.eAirBlowMode.BLOW_HEAD_FOOT);
					setWindBlowButton(1);
					break;
				case R.id.btnWinBlowMode3:
					ret = airPannelDrv
							.setAirBlowMode(AirConditionDriver.eAirBlowMode.BLOW_FOOT);
					setWindBlowButton(2);
					break;
				case R.id.btnWinBlowMode4:
					ret = airPannelDrv
							.setAirBlowMode(AirConditionDriver.eAirBlowMode.BLOW_FOOT_DEMIST);
					
					setWindBlowButton(3);
					break;
				case R.id.btnWinBlowMode5:
					ret = airPannelDrv
							.setAirBlowMode(AirConditionDriver.eAirBlowMode.BLOW_DEMIST);
					setWindBlowButton(4);
					break;
				case R.id.btnAirExternalCycle:
					ret = airPannelDrv.setInternalCycle(false);
					setAirCycleButton(false);
					break;
				case R.id.btnAirInternalCycle:
					ret = airPannelDrv.setInternalCycle(true);
					setAirCycleButton(true);
					break;
				default:
					return;
				}

				//ret = airPannelDrv.commitACInfo();
				if (ret != 0) {
					//Toast.makeText(this, "璀﹀憡锛氱┖璋冭缃敊璇� code=" + ret,
							//Toast.LENGTH_LONG).show();
					Log.e("KDSERVICE",
							"AirConditionActivity.onClick() airPannelDrv.commitACInfo()="
									+ ret);

					refreshPannelStatus(); // 璁剧疆澶辫触鍒欏埛鏂伴潰鏉垮紑鍏�
				}
				;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			//ToastUtil.showToast(this.getApplicationContext(), "璀﹀憡锛氬悗鍙版湇鍔℃湭鍚姩!(002)", Toast.LENGTH_LONG);
			Log.e("KDSERVICE", "AirConditionActivity.onClick() service is null");
		}
	}

	private void setWindBlowButton(int mode){
		for(int i=0; i<btnSetBlowMode.length; i++){
			if(i == mode){
				btnSetBlowMode[i].switchOn(true, false);
			} else {
				btnSetBlowMode[i].switchOn(false, false);
			}
		}
	}
	
	private void setAirCycleButton(boolean isInternal){
		if(isInternal){
			btnAirInternalCycle.switchOn(true, false);
			btnAirExternalCycle.switchOn(false, false);
		} else {
			btnAirInternalCycle.switchOn(false, false);
			btnAirExternalCycle.switchOn(true, false);
		}
	}
	
	public void refreshACDisplay() {
		//TODO: 瀹氭椂鍣�s鍒锋柊鏄剧ず娓╁害
		AirConditionDriver airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
		if(airPannelDrv != null) {
			
			try {
				setLoadingView(airPannelDrv.retreveACInfo() != 0); 
			}catch (RemoteException e) {
				e.printStackTrace();
				return;
			}

			int nInternalTemperature = airPannelDrv.getInsideTemp();	//杞﹀唴娓╁害

			anv.setTemperature(nInternalTemperature);

			//refresh power icon
			imageAirIconFan.setVisibility(
					(airPannelDrv.isWindPowerOn() && !airPannelDrv.isPtcPowerOn() && !airPannelDrv.isACPowerOn())
					?View.VISIBLE:View.GONE);
			imageAirIconPTC.setVisibility(airPannelDrv.isPtcPowerOn()?View.VISIBLE:View.GONE);
			imageAirIconAC.setVisibility(airPannelDrv.isACPowerOn()?View.VISIBLE:View.GONE);
			if(airPannelDrv.isPtcPowerOn() != this.btnPtcPower.isSwitchOn()) {
				this.btnPtcPower.switchOn(airPannelDrv.isPtcPowerOn(),false);
				if(airPannelDrv.isPtcPowerOn())
				{
					iv_ptc_light.setVisibility(View.VISIBLE);
				}
				else
				{
					iv_ptc_light.setVisibility(View.INVISIBLE);
				}
				
			}
			if(airPannelDrv.isACPowerOn() != this.btnAcPower.isSwitchOn()) {
				this.btnAcPower.switchOn(airPannelDrv.isACPowerOn(),false);
				if(airPannelDrv.isACPowerOn())
				{
					iv_ac_light.setVisibility(View.VISIBLE);
				}
				else
				{
					iv_ac_light.setVisibility(View.INVISIBLE);
				}
			}
			if(airPannelDrv.isWindPowerOn() != this.btnAirPower.isSwitchOn()) {
				this.btnAirPower.switchOn(airPannelDrv.isWindPowerOn(),false);
				if(airPannelDrv.isWindPowerOn())
				{
					iv_on_light.setVisibility(View.VISIBLE);
				}
				else
				{
					iv_on_light.setVisibility(View.INVISIBLE);
				}
				
			}
			
		}
		else {
			//ToastUtil.showToast(this.getApplicationContext(), "璀﹀憡锛氬悗鍙版湇鍔℃湭鍚姩!(002)", Toast.LENGTH_LONG);
			Log.e("KDSERVICE", "AirConditionActivity.refreshPannelStatus() service is null");
		}
	}
	
	/**
	 * 寤惰繜璁剧疆婊氳疆锛岄槻姝㈡粴杞儻鎬ц浆鍔ㄦ椂鍙楀埌搴曞眰椹卞姩浜嬩欢骞叉壈
	 */
//	public void refreshWheelDelay() {
//
//		//寤惰繜璁剧疆婊氳疆锛岄槻姝㈡粴杞儻鎬ц浆鍔ㄦ椂鍙楀埌搴曞眰椹卞姩浜嬩欢骞叉壈
//		if(timer !=null) {
//			if(taskWheelRefesh!=null) {
//				taskWheelRefesh.cancel();
//			}
//			taskWheelRefesh = new TimerTask() {
//				@Override
//				public void run() {
//					mHandler.sendEmptyMessage(RERESH_WHEELS);
//					this.cancel();
//				}
//		    };
//			timer.schedule(taskWheelRefesh, 1000, 1000);	//瀹氭椂鍣�000 ms
//		}
//	}

	//褰撳墠婊氳疆璁剧疆鐨勬俯搴﹀拰椋庨噺
	int wantTempPosition = 0;
	int watWindPosition = 0;
	
//	int waitTempBack = 0;
//	int waitWindBack = 0;
	int waitWheelBack = 0;
	boolean isWaitWheelBack = false;
	
	int dbgCount;
	public void setPresetTemp()  {
		AirConditionDriver airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
		if(airPannelDrv!=null) {
			int level = wantTempPosition+1;
//			ToastUtil.showDbgToast(AirConditionFragment.this.getActivity(), "璁剧疆娓╁害锛�+level);
			try {
				airPannelDrv.setPresetTemp(level);	//1~16 level (18~34 degree)
				waitWheelBack++;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			//ToastUtil.showDbgToast(this.getApplicationContext(), "DBG-SET-Wheel(#"+(dbgCount++)+"):temp="+level);
		}
	}

	public void setPresetWind()  {
		AirConditionDriver airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();
		if(airPannelDrv!=null) {
			int level = watWindPosition+1;
//			ToastUtil.showDbgToast(AirConditionFragment.this.getActivity(), "璁剧疆椋庨噺锛�+level);
			Log.i("AC", "setPresetWind");
			try {
				airPannelDrv.setWindSpeed(level); //1~8 level
				waitWheelBack++;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 寤惰繜璁剧疆娓╁害锛岄槻姝㈡粴杞儻鎬ц浆鍔ㄦ椂鍙楀埌搴曞眰椹卞姩浜嬩欢骞叉壈
	 */
	public void delaySetPresetTemp()  {
		if(timer !=null) {
			isWaitWheelBack = true;
			if(taskDelaySetTemp!=null) {
				taskDelaySetTemp.cancel();
			}
			taskDelaySetTemp = new TimerTask() {
				@Override
				public void run() {
//					mHandler.sendEmptyMessage(SET_TEMPWHEELS);
					setPresetTemp();
					this.cancel();
				}
		    };
			timer.schedule(taskDelaySetTemp, 800);	//瀹氭椂鍣�00 ms
		}
	}
	
	/**
	 * 寤惰繜璁剧疆椋庨噺锛岄槻姝㈡粴杞儻鎬ц浆鍔ㄦ椂鍙楀埌搴曞眰椹卞姩浜嬩欢骞叉壈
	 */
	public void delaySetPresetWind()  {
		
		if(timer !=null) {
			isWaitWheelBack = true;
			if(taskDelaySetWind!=null) {
				taskDelaySetWind.cancel();
			}
			taskDelaySetWind = new TimerTask() {
				@Override
				public void run() {
//					mHandler.sendEmptyMessage(SET_WINDWHEELS);
					setPresetWind();
					this.cancel();
				}
		    };
			timer.schedule(taskDelaySetWind, 800);	//瀹氭椂鍣�00 ms
		}
	}


	
	int dbgCountW;
	boolean isWheelsRefeshAgain = false;
	int countWheelsRefeshRetry = 0;
	
	public void refreshWheels() {
		if(isWaitWheelBack){
			if(waitWheelBack > 0){
				waitWheelBack--;
				if(waitWheelBack == 0){
					isWaitWheelBack = false;
				}
			}
				
//			ToastUtil.showDbgToast(AirConditionFragment.this.getActivity(), "鍒锋柊婊氳疆锛屾嫆缁濓紒锛�);
			return;

		}
			
		AirConditionDriver airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();

		if(airPannelDrv == null) { return;}

		try {
			airPannelDrv.retreveACInfo();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//鏇存柊娓╁害璁剧疆婊氳疆
		int nPresetTemp = airPannelDrv.getPresetTemp();
		int tempWheelPostion = nPresetTemp - 1;	//18~32 degree
		int selectedTempPos = tempwv.getSelectedItemPosition();
		if(tempWheelPostion != selectedTempPos) {
			setTempWheelPostion(tempWheelPostion, false);
		}
		
		//鏇存柊椋庨噺璁剧疆婊氳疆
		int nPresetWindLevel = airPannelDrv.getWindSpeed();
		int windWheelPostion = nPresetWindLevel - 1;	//1锝� level
		int selectedWindPos = modewv.getSelectedItemPosition();
		if(windWheelPostion != selectedWindPos) {
			setWindWheelPostion(windWheelPostion, false);
		}
//		ToastUtil.showDbgToast(AirConditionFragment.this.getActivity(), "鍒锋柊婊氳疆锛屾俯搴︼細"+tempWheelPostion + " 椋庨噺锛�+windWheelPostion);
		//璁剧疆鍚庢鏌ユ槸鍚﹁缃垚鍔�
		int selectedTempPos2 = tempwv.getSelectedItemPosition();
		int selectedWindPos2 = modewv.getSelectedItemPosition();
		
		String msg = "Refresh-WHEELS(#"+(dbgCountW++)
				+"): tempPos=("+tempWheelPostion+"->"+ tempwv.getSelectedItemPosition()
				+"), windPos=("+windWheelPostion+"->"+selectedWindPos2+")";
		
		
		if(	((tempWheelPostion == selectedTempPos2) && (windWheelPostion == selectedWindPos2))
				|| (countWheelsRefeshRetry==0)) { //褰撴俯搴︺�椋庨噺璁剧疆姝ｇ‘锛屾垨鑰呴噸璇曟鏁拌秴鏃跺垯鏀惧純閲嶈瘯
			
			/*if(taskDelayRefeshWheels!=null) {
        		taskDelayRefeshWheels.cancel();
        		taskDelayRefeshWheels=null;
        	}*/
			//ToastUtil.showDbgToast(this.getApplicationContext(), msg);
			Log.i("wheelsDbg",msg);
			countWheelsRefeshRetry = 2;	//棰勮涓嬫閲嶈瘯娆℃暟
		} 
		else {
			msg = msg+", Retry #"+countWheelsRefeshRetry;
			//ToastUtil.showDbgToast(this.getApplicationContext(), msg);
			Log.i("wheelsDbg",msg);
			countWheelsRefeshRetry--;	//閲嶈瘯娆℃暟閫掑噺
			
			if(countWheelsRefeshRetry==0) {	//閲嶈瘯澶氭鏃犳晥锛屽垯reset wheel
				if(tempWheelPostion != selectedTempPos2) {
//					if(tempWvAdapter != null){
//						tempWvAdapter.notifyDataSetChanged();
//					}
					setTempWheelPostion(tempWheelPostion, false);
					Log.e("wheelsDbg","### reset Temp wheel, positon="+tempWheelPostion);
						}
				if(windWheelPostion != selectedWindPos2) {
					//modewv.setAdapter(windWvAdapter);	//reset wheel
//					if(windWvAdapter != null){
//						windWvAdapter.notifyDataSetChanged();
//					}
					setWindWheelPostion(windWheelPostion, false);
					Log.e("wheelsDbg","### reset Wind wheel, positon="+windWheelPostion);
				}
			}
			
			//NOTE: Wheel鎺т欢bug锛氬伓灏旈潪鐢ㄦ埛鎬佽缃粴杞け璐ワ紝鍐嶆璁剧疆鍙垚鍔燂紱鏈夋椂璁剧疆澶辫触鍚庡啀涔熸棤娉曡缃垚鍔燂紝姝ゆ椂閫氳繃setAdapter()杩涜閲嶇疆鍚庨棶棰樿В鍐炽�
			
		}	
	}
	
	
	public void refreshPannelStatus() {
		AirConditionDriver airPannelDrv = DriverServiceManger.getInstance().getAirConditionDriver();

		if(airPannelDrv != null) {
			try {
				if(airPannelDrv.retreveACInfo() != 0) {
					setLoadingView(true); 
				}
			}catch (RemoteException e) {
				e.printStackTrace();
				return;
			}		

			//鐢垫簮寮�叧
			if(airPannelDrv.isPtcPowerOn() != this.btnPtcPower.isSwitchOn()) {
				this.btnPtcPower.switchOn(airPannelDrv.isPtcPowerOn(),false);
				if(airPannelDrv.isPtcPowerOn())
				{
					iv_ptc_light.setVisibility(View.VISIBLE);
				}
				else
				{
					iv_ptc_light.setVisibility(View.INVISIBLE);
				}
				
			}
			if(airPannelDrv.isACPowerOn() != this.btnAcPower.isSwitchOn()) {
				this.btnAcPower.switchOn(airPannelDrv.isACPowerOn(),false);
				if(airPannelDrv.isACPowerOn())
				{
					iv_ac_light.setVisibility(View.VISIBLE);
				}
				else
				{
					iv_ac_light.setVisibility(View.INVISIBLE);
				}
			}
			if(airPannelDrv.isWindPowerOn() != this.btnAirPower.isSwitchOn()) {
				this.btnAirPower.switchOn(airPannelDrv.isWindPowerOn(),false);
				if(airPannelDrv.isWindPowerOn())
				{
					iv_on_light.setVisibility(View.VISIBLE);
				}
				else
				{
					iv_on_light.setVisibility(View.INVISIBLE);
				}
				
			}
			///enablePannelByPower();

			//婊氳疆鐩稿叧璁剧疆
			//*
			//delayRefreshWheels();	//寤惰繜鍒锋柊婊氳疆
			/*
			 * /			
			 */
			refreshWheels();		//绔嬪嵆鍒锋柊婊氳疆
			//*/
			
			//鍚归妯″紡
			switchBtnSetBlowMode(airPannelDrv.getAirBlowMode().ordinal()-1);

//			if(airPannelDrv.getAirBlowMode().getMask() != (btnSetBlowMode.getOnButton()+1)) {
//				switch(airPannelDrv.getAirBlowMode()) {
//				case BLOW_HEAD:
//					this.btnSetBlowMode.switchOnButton(0, false);
//					break;
//				case BLOW_HEAD_FOOT:
//					this.btnSetBlowMode.switchOnButton(1, false);
//					break;
//				case BLOW_FOOT:
//					this.btnSetBlowMode.switchOnButton(2, false);
//					break;
//				case BLOW_FOOT_DEMIST:
//					this.btnSetBlowMode.switchOnButton(3, false);
//					break;
//				case BLOW_DEMIST:
//					this.btnSetBlowMode.switchOnButton(4, false);
//					break;
//				case BLOW_UNDEF:
//				default:
//					this.btnSetBlowMode.switchOnButton(-1, false);		//switch off all buttons
//				}
//			}
			
			//杩涢妯″紡
			btnAirExternalCycle.switchOn(!airPannelDrv.isInternalCycle(),false);
			btnAirInternalCycle.switchOn(airPannelDrv.isInternalCycle(),false);
			
			
			//refresh temperature display
			int nInternalTemperature = airPannelDrv.getInsideTemp();	//杞﹀唴娓╁害
			//int nExternalTemperature = airPannelDrv.getOutsideTemp();	//杞﹀娓╁害
			//int nHumidity = airPannelDrv.getHumidity();					//婀垮害
			anv.setTemperature(nInternalTemperature);

			//refresh power icon
			imageAirIconFan.setVisibility(
					(airPannelDrv.isWindPowerOn() && !airPannelDrv.isPtcPowerOn() && !airPannelDrv.isACPowerOn())
					?View.VISIBLE:View.GONE);
			imageAirIconPTC.setVisibility(airPannelDrv.isPtcPowerOn()?View.VISIBLE:View.GONE);
			imageAirIconAC.setVisibility(airPannelDrv.isACPowerOn()?View.VISIBLE:View.GONE);

//			if(airPannelDrv.isACPowerOn()) {
//				setImgAirIcon(2);	//cooling
//			}
//			else if(airPannelDrv.isPtcPowerOn()) {
//				setImgAirIcon(3);	//heating
//			}
//			else if(airPannelDrv.isWindPowerOn()) {
//				setImgAirIcon(1);	//fan
//			}
//			else {
//				setImgAirIcon(0);	//off
//			}
			
			//if(true) {
//			String msg = "PTC="+ airPannelDrv.isPtcPowerOn()+", AC="+airPannelDrv.isACPowerOn()+", PWR="+ airPannelDrv.isWindPowerOn()+
//						", mode="+airPannelDrv.getAirBlowMode()+", cyc="+airPannelDrv.isInternalCycle()+
//						", setT="+airPannelDrv.getPresetTemp()+", setWind="+airPannelDrv.getWindSpeed()+
//						", temp="+airPannelDrv.getInsideTemp();
//	
			//Toast.makeText(this, "AIR-SET:" + msg, Toast.LENGTH_LONG).show();
			///ToastUtil.showDbgToast(this.getApplicationContext(), "AIR-SET:" + msg);

			//}

			Log.d("ACCondition", "PTC="+ airPannelDrv.isPtcPowerOn()+", AC="+airPannelDrv.isACPowerOn()+", PWR="+ airPannelDrv.isWindPowerOn()+
					", mode="+airPannelDrv.getAirBlowMode()+", cyc="+airPannelDrv.isInternalCycle()+
					", setT="+airPannelDrv.getPresetTemp()+", setWind="+airPannelDrv.getWindSpeed()+
					", temp="+airPannelDrv.getInsideTemp());

		}
		else {
			mHandler.sendEmptyMessageDelayed(UPDATE_PANNEL, 1000);
			//ToastUtil.showToast(this.getApplicationContext(), "璀﹀憡锛氬悗鍙版湇鍔℃湭鍚姩!(002)", Toast.LENGTH_LONG);
			Log.e("KDSERVICE", "AirConditionActivity.refreshPannelStatus() service is null");
		}
	}
	
	void switchBtnSetBlowMode(int index) {
		for(int i=0; i<btnSetBlowMode.length;i++) {
			btnSetBlowMode[i].switchOn(i==index, false);
		}
	}
	
	
	
	@Override
	public void onDestroy() {
		if(timer != null) {
			timer.cancel();
			timer = null;
		}
		super.onDestroy();
	}
	
	/**
	 * 鍩虹被骞挎挱鎺ユ敹
	 * */
	private class BaseReceiver extends BroadcastReceiver{  
        @Override  
        public void onReceive(Context context, Intent intent) { 
        	Bundle bundle = intent.getExtras();
	    	Set<String> keySet = bundle.keySet();
		   	for(String key : keySet) {
		   		
		        //*  绌鸿皟闈㈡澘    **  key+1       **  0:浣胯兘锛�:绂佺敤               **  boolean                       *
		   		if(key.compareTo("KD_CAST_EVENT1") != 0) {
		   			continue;
		   		}
		   		if(bundle.getBoolean(key))
		   		{
		   			mHandler.sendEmptyMessage(AIR_DISABLED);
		   		}
		   		else {
		   			mHandler.sendEmptyMessage(UPDATE_PANNEL);	
		   		}
		   	}
		   	refreshWheels();
        }
    }
	
}
