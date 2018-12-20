package com.kandi.settings.fragment;

import com.kandi.settings.Configs;
import com.kandi.settings.R;
import com.kandi.settings.SetbtnListener;
import com.kandi.settings.activitys.VolumeActivity;
import com.kandi.settings.utils.SharedPreferencesUtils;
import com.kandi.settings.widgets.VerticalSeekBar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 声音调整界面只可以调节音量时的界面
 * @author justin
 *
 */
public class VolumeSetOffFragment extends Fragment {
	
	private final static int STEP = 15;
	private final int LONG_UP_ACTION_MSG=1000,LONG_DOWN_ACTION_MSG=1001;
	private boolean longPressFlag=false;
	private Context mcontext;
	private RelativeLayout bgview;
	private VerticalSeekBar verticalSeekBar;
	private AudioManager audiomanage;
	private int maxVolume, currentVolume; 
	private FrameLayout mainview;
	private ImageButton voice;
	private ImageButton set;
	private Button upVolume,downVolume;
	private boolean isVoiceOn = false;
	private int mainVolume;
	private int balanceVolume;
	private int bassVolume;
	SharedPreferences sh;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.voice_layout_setoff, container, false);
		mcontext = inflater.getContext();
		System.out.println("onCreateView currentVolume~~~~~~~~"+(100*mainVolume/STEP));
		sh = SharedPreferencesUtils.getSharedPreferences(getActivity());
		isVoiceOn = sh.getBoolean(Configs.KEY_VOL_SWITCH, true);
		mainVolume = sh.getInt(Configs.KEY_VOL_VALUE, 0);
		initView(view);
		return view;
	}
	
	private void initView(View view){
		audiomanage = (AudioManager)mcontext.getSystemService(Context.AUDIO_SERVICE); 
		voice = (ImageButton) view.findViewById(R.id.voice);
		verticalSeekBar = (VerticalSeekBar) view.findViewById(R.id.verticalSeekBar);
		
		voice.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!isVoiceOn){
					isVoiceOn = true;
					if(mainVolume == 0){
						mainVolume = 4;
					}
					verticalSeekBar.setProgress(100*mainVolume/STEP);
					audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, verticalSeekBar.getProgress()*STEP/100, 0);    
	                currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
//	                voice.setImageResource(R.drawable.volume_03_on_l);
	                setVoiceIcon(currentVolume);
				}else{
					isVoiceOn = false;
					mainVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
					System.out.println("voice off");
					verticalSeekBar.setProgress(0);
					audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);    
	                currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
//	                voice.setImageResource(R.drawable.volume_03_off);
	                setVoiceIcon(0);
	                
	                Editor edt = SharedPreferencesUtils.getEditor(getActivity());
	        		edt.putInt(Configs.KEY_VOL_VALUE, mainVolume);
	        		edt.commit();
				}
				
				
				Editor edt = SharedPreferencesUtils.getEditor(getActivity());
				edt.putBoolean(Configs.KEY_VOL_SWITCH, isVoiceOn);
				edt.commit();
			}
		});
		
		currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  
		System.out.println("currentVolume~~~~~~~~"+(100*currentVolume/15));
		verticalSeekBar.setProgress(100*currentVolume/STEP);
		verticalSeekBar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(VerticalSeekBar vBar) {
				if(verticalSeekBar.getProgress() >0){
					isVoiceOn = true;
//					voice.setImageResource(R.drawable.volume_03_on_l);			
					setVoiceIcon(currentVolume);
					Editor edt = SharedPreferencesUtils.getEditor(getActivity());
					edt.putBoolean(Configs.KEY_VOL_SWITCH, isVoiceOn);
					edt.commit();
				} else if(verticalSeekBar.getProgress() ==0){
					isVoiceOn = false;
//					voice.setImageResource(R.drawable.volume_03_on_l);			
					setVoiceIcon(0);
					Editor edt = SharedPreferencesUtils.getEditor(getActivity());
					edt.putBoolean(Configs.KEY_VOL_SWITCH, isVoiceOn);
					edt.commit();
				}
			}
			
			@Override
			public void onStartTrackingTouch(VerticalSeekBar vBar) {
				
			}
			
			@Override
			public void onProgressChanged(VerticalSeekBar vBar, int progress,
					boolean fromUser) {
				audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, progress*STEP/100, 0);    
                currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  
                setVoiceIcon(currentVolume);
			}
		});
		
		set = (ImageButton) view.findViewById(R.id.set);
		set.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				VolumeActivity.instance.OnSetbtnClicked(0);
			}
		});
		
		upVolume = (Button) view.findViewById(R.id.volume_up);
		upVolume.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				switch(arg1.getAction()){
				case MotionEvent.ACTION_DOWN:
					if(verticalSeekBar.getProgress()<100){
						verticalSeekBar.setProgress(verticalSeekBar.getProgress()+1);
					}
					break;
				case MotionEvent.ACTION_UP:
					longPressFlag = false;
					if(verticalSeekBar.getProgress() >0){
						isVoiceOn = true;
						Editor edt = SharedPreferencesUtils.getEditor(getActivity());
						edt.putBoolean(Configs.KEY_VOL_SWITCH, isVoiceOn);
						edt.commit();
					}else{
						isVoiceOn = false;
						Editor edt = SharedPreferencesUtils.getEditor(getActivity());
						edt.putBoolean(Configs.KEY_VOL_SWITCH, isVoiceOn);
						edt.commit();
					}
					break;
				default:
					break;
				}
				return false;
			}
		} );
		
		upVolume.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				longPressFlag = true;
				handler.sendEmptyMessageDelayed(LONG_UP_ACTION_MSG, 100);
				return false;
			}
		});
		
		downVolume = (Button) view.findViewById(R.id.volume_down);
		downVolume.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				switch(arg1.getAction()){
				case MotionEvent.ACTION_DOWN:
					if(verticalSeekBar.getProgress()>0){
						verticalSeekBar.setProgress(verticalSeekBar.getProgress()-1);
					}
					break;
				case MotionEvent.ACTION_UP:
					longPressFlag = false;
					break;
				default:
					break;
				}
				return false;
			}
		} );
		
		downVolume.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				longPressFlag = true;
				handler.sendEmptyMessageDelayed(LONG_DOWN_ACTION_MSG, 100);
				return false;
			}
		});
		
		if(isVoiceOn){
//			voice.setImageResource(R.drawable.volume_03_off_l);
			setVoiceIcon(mainVolume);
		}else{
//			voice.setImageResource(R.drawable.volume_03_off);
			setVoiceIcon(0);
		}
	}
	//实现声音缓升和缓降功能
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LONG_UP_ACTION_MSG:
				if(longPressFlag){
					if(verticalSeekBar.getProgress()<100){
						verticalSeekBar.setProgress(verticalSeekBar.getProgress()+1);
					}else{
						longPressFlag = false;
						break;
					}
					handler.sendEmptyMessageDelayed(LONG_UP_ACTION_MSG, 100);
				}
				break;
			case LONG_DOWN_ACTION_MSG:
				if(longPressFlag){
					if(verticalSeekBar.getProgress()>0){
						verticalSeekBar.setProgress(verticalSeekBar.getProgress()-1);
					}else{
						longPressFlag = false;
						break;
					}
					handler.sendEmptyMessageDelayed(LONG_DOWN_ACTION_MSG, 100);
				}
				break;
			}
		}

	};
	
	@Override
	public void onResume() {
		super.onResume();
		isVoiceOn = sh.getBoolean(Configs.KEY_VOL_SWITCH, true);		
		if(audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC)>0){
			mainVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
			isVoiceOn = true;
		}else{
			isVoiceOn = false;
		}
		System.out.println("onResume currentVolume~~~~~~~~"+(100*mainVolume/STEP));
		if(!isVoiceOn){
			verticalSeekBar.setProgress(0);	
			setVoiceIcon(0);
		}else{
			verticalSeekBar.setProgress(100*mainVolume/STEP);
			setVoiceIcon(mainVolume);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private void setVoiceIcon(int level){
		Log.i("Volume", "level:"+level);
		if(voice != null){
			if(level == 0){
				voice.setImageResource(R.drawable.volume0_btn_selector);
			}else if(level >=1 && level <=4){
				voice.setImageResource(R.drawable.volume1_btn_selector);
			}else if(level >=5 && level <=9){
				voice.setImageResource(R.drawable.volume2_btn_selector);
			}else if(level >=10){
				voice.setImageResource(R.drawable.volume3_btn_selector);
			}
		}
	}
	
}
