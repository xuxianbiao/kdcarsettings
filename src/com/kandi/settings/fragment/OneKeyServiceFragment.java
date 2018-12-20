package com.kandi.settings.fragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IKdAudioControlService;
import android.os.IQyPhoneService;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.driverlayer.kdos_driverServer.BlueDriver;
import com.kandi.settings.KdsApplication;
import com.kandi.settings.R;
import com.kandi.settings.event.PlaySimCallEndEvent;

import de.greenrobot.event.EventBus;
/**
 * 一键服务Fragment
 * @author xuxb
 *
 */
public class OneKeyServiceFragment extends Fragment {

	private TextView converse_time,hotlinenum;
	private Button btn_call_answer;
	private Button btn_call_hangup;
	private int[] state = new int[1];
	private BlueDriver bluedriver = KdsApplication.bluedriver;
	String numbers = "";
	String calltimes = "";
	String phonename = "";
	public static boolean CALLSTATE;
	Context context;
	
	IKdAudioControlService audioservice;
	TelephonyManager telMgr;
	String[] btorsim = new String[]{"0"};
	private IQyPhoneService mIQyPhoneService = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.onekeycall, container, false);
		context = inflater.getContext();
		audioservice = IKdAudioControlService.Stub.asInterface(ServiceManager.getService("audioCtrl"));
		//获取电话服务
		if(SystemProperties.get(getString(R.string.about_hardprop)).contains(getString(R.string.about_QYv1_5))){
			mIQyPhoneService = IQyPhoneService.Stub.asInterface(ServiceManager.getService("kdphone"));
		}
		this.initView(view);
		initEvent();
		return view;
	}
	
	private void initEvent() {
		EventBus.getDefault().register(this, "onPlaySimCallEnd",PlaySimCallEndEvent.class);
	}
	
	public void onPlaySimCallEnd(PlaySimCallEndEvent event){
		mhandler.sendEmptyMessage(104);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		telMgr = (TelephonyManager) getActivity().getSystemService(Service.TELEPHONY_SERVICE);
//		if(!(telMgr.getSimState()==TelephonyManager.SIM_STATE_READY)){
//			mhandler.sendEmptyMessage(102);
//		}
		mhandler.sendEmptyMessage(106);
		if(CALLSTATE){
			mhandler.sendEmptyMessage(103);
		}
	}

	private boolean flag = false;
	@Override
	public void onDestroy() {
		flag = true;
		super.onDestroy();
	}

	private void initView(View view) {
		converse_time = (TextView) view.findViewById(R.id.converse_time);
		hotlinenum = (TextView) view.findViewById(R.id.hotlinenum);
		btn_call_answer = (Button) view.findViewById(R.id.btn_call_answer);
		btn_call_answer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String hardversion = SystemProperties.get(getString(R.string.about_hardprop));
				if(hardversion.contains(getString(R.string.about_QYv1_5))){
					boolean result = false;
					try{
						//拨号
						result = mIQyPhoneService.startCall(getString(R.string.onekeyrealnum));
					}catch(RemoteException e){
						e.printStackTrace();
					}
					if(result){
						mhandler.sendEmptyMessage(103);
						CALLSTATE = true;
					}
				}else if(getString(R.string.about_QYv1_4).equals(hardversion)){
					if(telMgr.getSimState()==TelephonyManager.SIM_STATE_READY){
						if(telMgr.getCallState() == TelephonyManager.CALL_STATE_IDLE){
							simCardCall(getString(R.string.onekeyrealnum));
							mhandler.sendEmptyMessage(103);
							CALLSTATE = true;
						}
					}
				}
			}
		});
		btn_call_hangup = (Button) view.findViewById(R.id.btn_call_hangup);
		btn_call_hangup.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String hardversion = SystemProperties.get(getString(R.string.about_hardprop));
				if(hardversion.contains(getString(R.string.about_QYv1_5))){
					try{
						//挂断电话
						mIQyPhoneService.endCall();
					}catch(RemoteException e){
						e.printStackTrace();
					}
					bluedriver = KdsApplication.bluedriver;
					CALLSTATE = false;
				}else if(getString(R.string.about_QYv1_4).equals(hardversion)){
					if("1".equals(SystemProperties.get("gsm.current.phone-type"))){
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								endCall(context);
							}
						}).start();
						bluedriver = KdsApplication.bluedriver;
						CALLSTATE = false;
					}
				}
				try {
					if(bluedriver!=null){
						state = new int[1];
						bluedriver.getCountState(btorsim,state);
						if(state[0]==0){
							mhandler.sendEmptyMessage(104);
						}
					}else{
						mhandler.sendEmptyMessage(104);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		CountTimeView();
	}
	
	private void CountTimeView(){
		new Thread(new Runnable() {
	    	
	    	@Override
	    	public void run() {
	    		try {
	    			state = new int[1];
	    			while (true) {
	    				try {
	    					Thread.sleep(100);
	    				} catch (InterruptedException e) {
	    					e.printStackTrace();
	    				}
	    				if(flag){
	    					break;
	    				}
	    				if(bluedriver != null){
	    					bluedriver.getCountState(btorsim,state);
	    					while (state[0] == 1) {
	    						try {
	    							Thread.sleep(400);
	    						} catch (InterruptedException e) {
	    							e.printStackTrace();
	    						}
	    						String[] data = new String[2];
	    						bluedriver.getCountData(data);
	    						calltimes = data[1];
	    						if("".equals(calltimes)){
	    							mhandler.sendEmptyMessage(100);
	    						}else{
	    							mhandler.sendEmptyMessage(101);
	    						}
	    						bluedriver.getCountState(btorsim,state);
	    						if(state[0]==0){
	    							mhandler.sendEmptyMessage(104);
	    							break;
	    						}
	    					}
	    				}
	    				bluedriver = KdsApplication.bluedriver;
	    			}
	    		} catch (RemoteException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    }).start();
	}
	
	Handler mhandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				if(!(telMgr.getSimState()==TelephonyManager.SIM_STATE_READY)){
					mhandler.sendEmptyMessage(102);
				}else{
					converse_time.setText(calltimes);
				}
				btn_call_answer.setVisibility(View.VISIBLE);
				btn_call_hangup.setVisibility(View.GONE);
				btn_call_answer.setEnabled(false);
				break;
			case 101:
				if(isAdded()){
				    converse_time.setText(getString(R.string.callingtime)+calltimes);
				}
				btn_call_answer.setVisibility(View.GONE);
				btn_call_hangup.setVisibility(View.VISIBLE);
				break;
			case 102:
				btn_call_answer.setEnabled(false);
				if(isAdded()){
					converse_time.setText(getString(R.string.sim_unin));
				}
				break;
			case 103:
				converse_time.setText(getString(R.string.calling));
				btn_call_answer.setVisibility(View.GONE);
				btn_call_hangup.setVisibility(View.VISIBLE);
				break;
			case 104:
				if(isAdded()){
					converse_time.setText(getString(R.string.call_end));
				}
				btn_call_answer.setVisibility(View.VISIBLE);
				btn_call_hangup.setVisibility(View.GONE);
				if(!(telMgr.getSimState()==TelephonyManager.SIM_STATE_READY)){
					mhandler.sendEmptyMessage(102);
				}else{
					btn_call_answer.setEnabled(false);
					mhandler.sendEmptyMessageDelayed(105, 3000);
				}
				break;
			case 105:
				if(isAdded()){
					btn_call_answer.setEnabled(true);
					converse_time.setText("");
				}
				break;
			case 106:
				btn_call_answer.setEnabled(false);
				if(isAdded()){
					hotlinenum.setText(getString(R.string.unhotline));
					converse_time.setText("");
				}
				break;
			}
		}
		
	};
	
	private void simCardCall(String phone){
		if(phone != null && !phone.equals("")){
			Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone));
	        startActivity(intent);
		}
	}
	
	public void endCall(Context context) {
		try {
			Object telephonyObject = getTelephonyObject(context);
			if (null != telephonyObject) {
				Class telephonyClass = telephonyObject.getClass();
				Method endCallMethod = telephonyClass.getMethod("endCall");
				endCallMethod.setAccessible(true);
				endCallMethod.invoke(telephonyObject);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private Object getTelephonyObject(Context context) {
		Object telephonyObject = null;
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			Class telManager = telephonyManager.getClass();
			Method getITelephony = telManager.getDeclaredMethod("getITelephony");
			getITelephony.setAccessible(true);
			telephonyObject = getITelephony.invoke(telephonyManager);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return telephonyObject;
	}

}
