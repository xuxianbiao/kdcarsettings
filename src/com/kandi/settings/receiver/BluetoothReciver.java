package com.kandi.settings.receiver;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.IKdBtService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.driverlayer.kdos_driverServer.BlueDriver;
import com.kandi.settings.Configs;
import com.kandi.settings.KdsApplication;
import com.kandi.settings.R;
import com.kandi.settings.activitys.WindowActivity;
import com.kandi.settings.event.ParamFreshEvent;
import com.kandi.settings.event.PlaySimCallEndEvent;
import com.kandi.settings.fragment.OneKeyServiceFragment;
import com.kandi.settings.utils.SharedPreferencesUtils;

import de.greenrobot.event.EventBus;

public class BluetoothReciver extends BroadcastReceiver{

	public final static String ACTION_REQUEST_PINCODE = "com.kangdi.BroadCast.RequestPinCode";
	public final static String ACTION_REQUEST_AIR = "android.intent.action.BOOT_COMPLETED";
	public final static String ACTION_PARAMRETURN = "com.kangdi.paramreturn";
	public final static String ACTION_SIM_CALL_START = "com.kangdi.BroadCast.SimCallStart";//SIM卡接通广播
	public final static String ACTION_SIM_CALL_END = "com.kangdi.BroadCast.PhoneState";//SIM卡挂断广播
//	public final static String ACTION_4GADUIO_PATH = "com.kangdi.BroadCast.open4GAduioPath";//4G通话打开声道
	ParamFreshEvent event;
	BlueDriver bluedriver = KdsApplication.bluedriver;
	private AudioManager mAudioManager;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ACTION_REQUEST_PINCODE)){
			IKdBtService btservice = IKdBtService.Stub.asInterface(ServiceManager.getService("bt"));
			String blueping = SharedPreferencesUtils.getSharedPreferences(
					context).getString(Configs.SHARED_BLUE_PING,
					Configs.DEFUALT_BLUE_PING);
    		try {
    			if(btservice.btPinCode(blueping)==0){
    				Toast.makeText(context, context.getString(R.string.blue_conn_success), Toast.LENGTH_LONG);
    			}else{
    				Toast.makeText(context, context.getString(R.string.blue_conn_fail), Toast.LENGTH_LONG);
    			}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
		
		if(intent.getAction().equals(ACTION_REQUEST_AIR)){
			KdsApplication.getInstance();
		}
		
		if(intent.getAction().equals(ACTION_PARAMRETURN)){
			event = new ParamFreshEvent();
			event.type = ParamFreshEvent.PARAMFRESH_STATE;
			EventBus.getDefault().post(event);
		}
		
//		if(intent.getAction().equals(ACTION_4GADUIO_PATH)){
//			mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//			if("TEST-KDV1P5-MB-VER-1_0_0".equals(SystemProperties.get("sys.kd.hardwareversion"))){
//				if(mAudioManager.requestAudioFocus(afPhoneChangeListener, 10,
//	    				AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
//	        	}
//			}
//		}
		
		if(intent.getAction().equals(ACTION_SIM_CALL_START)){
			//Log.i("ACTION_SIM_CALL_START", "SIM接通事件");
			Intent dtmf = new Intent();
			dtmf.setAction("android.intent.action.KandiProcessDtmf");
			dtmf.putExtra("DTMFCode", '0');
			context.sendBroadcast(dtmf);
			mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			OneKeyServiceFragment.CALLSTATE = false;
			String[] param = new String[]{context.getString(R.string.onekeynum),"0"};
			if(bluedriver!=null){
				try {
//					if("vwcsy001_M_v1_4".equals(SystemProperties.get("sys.kd.hardwareversion","vwcsy001_M_v1_4"))){
//					}
					if(mAudioManager.requestAudioFocus(afPhoneChangeListener, 10,
							AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
					}
					bluedriver.startCountService(param);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			if(!isTopActivity("WindowActivity") || WindowActivity.nowKey != Configs.KEY_TAB_ONE_SERVICE){
				Intent callintent = new Intent(context, WindowActivity.class);
				callintent.putExtra("intent_key_tab", 1);
				callintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(callintent);
			}
		}
		
		if(intent.getAction().equals(ACTION_SIM_CALL_END)){
			int state = intent.getIntExtra("state", 1);
			switch(state){
			//接收到挂断事件，做一些处理
			case 9:		
				mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				simHangup();
				break;
			}
		}
		
		if(intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)){//系统sim action不同
    		TelephonyManager telMgr =  (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
    		String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
    		int state = telMgr.getCallState();
    		switch (state) {
    		case TelephonyManager.CALL_STATE_RINGING:
    			Log.i("RingCallReceiver", "[Broadcast]等待接电话=" + phoneNumber);
    			break;
    		case TelephonyManager.CALL_STATE_IDLE:
    			Log.i("RingCallReceiver", "[Broadcast]电话挂断=" + phoneNumber);
    			mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    			if("vwcsy001_M_v1_4".equals(SystemProperties.get("sys.kd.hardwareversion","vwcsy001_M_v1_4"))){
    				simHangup();
    			}
    			break;
    		case TelephonyManager.CALL_STATE_OFFHOOK:
    			Log.i("RingCallReceiver", "[Broadcast]通话中=" + phoneNumber);
    			break;
    		} 
    	}
	}
	
	private boolean isTopActivity(String name)
    {
        boolean isTop = false;
        ActivityManager am = (ActivityManager) KdsApplication.getInstance().getSystemService("activity");
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if (cn.getClassName().contains(name))
        {
            isTop = true;
        }
        return isTop;
    }
	
	public void simHangup(){
		OneKeyServiceFragment.CALLSTATE = false;
		int[] data = new int[1];
		try {
			if(bluedriver!=null){
				String[] state2 = new String[]{"0"};
				bluedriver.getCountState(state2,data);
				if(data[0] == 0){
					PlaySimCallEndEvent event = new PlaySimCallEndEvent();
					event.type = PlaySimCallEndEvent.SIM_NOSTATE;
					EventBus.getDefault().post(event);
				}
				if (mAudioManager.abandonAudioFocus(afPhoneChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				}
				bluedriver.stopCountService(state2);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * SIMPHONE监听器
	 */
	public static OnAudioFocusChangeListener afPhoneChangeListener = new OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
			}

		}
	};
}
