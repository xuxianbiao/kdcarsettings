package com.kandi.settings.activitys;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.kandi.settings.Configs;
import com.kandi.settings.KdsApplication;
import com.kandi.settings.R;
import com.kandi.settings.driver.ConfigDriver1xCarCtrlSetup;
import com.kandi.settings.driver.DriverServiceManger;
import com.kandi.settings.driver.ConfigDriver1xCarCtrlSetup.eDrvVMode;
import com.kandi.settings.fragment.AirConditionFragment;
import com.kandi.settings.fragment.OneKeyServiceFragment;
import com.kandi.settings.fragment.SettingsFragment;
import com.kandi.settings.utils.FragmentHolder;

public class WindowActivity extends FragmentActivity {
	
	public static int tab_key = Configs.KEY_TAB_SETTINGS;
	FragmentHolder fh = null;
	private static final String KEY_ONE= "one_key";
	private static final String KEY_SET= "set_key";
	private static final String KEY_AIR= "air_key";
	private static final String KEY_VOL= "vol_key";
	public static int nowKey = 0;
	View frame_content;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_base);
//		super.findViewById(R.id.buttom_shadow).getBackground().setAlpha(160);
		super.findViewById(R.id.buttom_shadow).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!onShadowCilck()){
					String[] infos = getActivityAtSecond();
					if(infos != null){
						//鍚姩涓婁竴涓猘ctivity
						gotoActivity(infos[0], infos[1]);
						nowKey = 0;
					}else{
						finish();
					}
				}
			}
		});
		frame_content = findViewById(R.id.father_content_view);
		
		Intent intent = getIntent();
		tab_key = intent.getIntExtra(Configs.INTENT_KEY_TAB, Configs.KEY_TAB_SETTINGS);
		
		fh = new FragmentHolder(getSupportFragmentManager());

		setFragmentFromKey(tab_key);
		KdsApplication.getInstance().setWindowActvity(this);
		mhandler.sendEmptyMessageDelayed(ISHOTMODE, 400);
	}
	
	private final int ISHOTMODE = 1000;
	
	Handler mhandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ISHOTMODE:
				if(KdsApplication.getInstance().isHotMode()){
					ConfigDriver1xCarCtrlSetup model = DriverServiceManger.getInstance()
							.getConfigDriver1xCarCtrlSetup();
					if(model!=null){
						try {
							model.setDrvMode(eDrvVMode.DRVMODE_TEMPER);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
				mhandler.sendEmptyMessageDelayed(ISHOTMODE, 400);
				break;

			default:
				break;
			}
		}
		
	};
	
	@Override
	protected void onDestroy() {
		KdsApplication.getInstance().setWindowActvity(null);
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		tab_key = intent.getIntExtra(Configs.INTENT_KEY_TAB, Configs.KEY_TAB_SETTINGS);
		//閲嶅鍚姩
		if(isActive && nowKey == tab_key){
			String[] infos = getActivityAtSecond();
			if(infos != null){
				//鍚姩涓婁竴涓猘ctivity
				gotoActivity(infos[0], infos[1]);
				nowKey = 0;
			}else{
				finish();
			}
		}else{
			setFragmentFromKey(tab_key);
		}
	}
	
	private String[] getActivityAtSecond() {
		try {
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			ComponentName cn = am.getRunningTasks(2).get(1).topActivity;
			String[] infos = new String[2];
			infos[0] = cn.getPackageName();
			infos[1] = cn.getClassName();
			return infos;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
    }
	
	void gotoActivity(String pkg,String activityName) {
        Intent homeIntent = new Intent();
        homeIntent = new Intent();
        homeIntent.setComponent(new ComponentName(pkg, activityName));
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(homeIntent);
        } catch (Exception e) {
            Log.e("KandiSystemUiService", e.toString());
        }
    }
	
	boolean isActive = false;
	
	@Override
	protected void onStart() {
		super.onStart();
		isActive = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		isActive = false;
	}

	private void setFragmentFromKey(int key){
		if(key == Configs.KEY_TAB_SETTINGS){
			if(!fh.isAdded(KEY_SET)){
				fh.addFragment(KEY_SET, new SettingsFragment());
				fh.addAndShow(R.id.father_content_view, KEY_SET);
			}else{
				fh.hideOthersAndShow(KEY_SET);
			}
			frame_content.setBackgroundResource(R.drawable.shoots_bg_01);
		}else if(key == Configs.KEY_TAB_ONE_SERVICE){
			if(!fh.isAdded(KEY_ONE)){
				fh.addFragment(KEY_ONE, new OneKeyServiceFragment());
				fh.addAndShow(R.id.father_content_view, KEY_ONE);
			}else{
				fh.hideOthersAndShow(KEY_ONE);
			}
			frame_content.setBackground(null);
		}else if(key == Configs.KEY_TAB_KONGTIAO){
			if(!fh.isAdded(KEY_AIR)){
				fh.addFragment(KEY_AIR, new AirConditionFragment());
				fh.addAndShow(R.id.father_content_view, KEY_AIR);
			}else{
				fh.hideOthersAndShow(KEY_AIR);		
			}
			frame_content.setBackground(null);
		}else if(key == Configs.KEY_TAB_YINLIANG){
			if(!fh.isAdded(KEY_VOL)){
				fh.addFragment(KEY_VOL, new VolumeActivity());
				fh.addAndShow(R.id.father_vol_view, KEY_VOL);
			}else{
				fh.hideOthersAndShow(KEY_VOL);		
			}
			frame_content.setBackground(null);
		}
		nowKey = key;
	}
	
	/**
	 * 绐楀彛澶栭儴闃村奖鐐瑰嚮浜嬩欢锛岄渶瑕佸彲閲嶅啓
	 * @return true 琛ㄧず娑堣垂浜嬩欢锛屼笉鍋氬叧闂獥鍙ｅ鐞嗕簡
	 */
	public boolean onShadowCilck(){
		return false;
	}
		
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		nowKey = savedInstanceState.getInt("position");
		setFragmentFromKey(nowKey);
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//璁板綍褰撳墠鐨刾osition
		outState.putInt("position", nowKey);
	}
}
