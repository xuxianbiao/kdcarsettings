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
import android.os.IFmService;
import android.os.IKdAudioControlService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * 声音调整界面高低音、声道都可调节时的页面
 * @author justin
 *
 */
public class VolumeSetOnFragment extends Fragment {
	
	private Context mcontext;
	private RelativeLayout bgview;
	private VerticalSeekBar verticalSeekBar;
	private VerticalSeekBar verticalSeekBar2;
	private VerticalSeekBar verticalSeekBar3;
	private AudioManager audiomanage;
	private int maxVolume, currentVolume;//currentVolume2,currentVolume3; 
	private ImageButton voice;
	private ImageButton voice2;
	private ImageButton voice3;
	private ImageButton set;
	private boolean isVoiceOn = false;
//	private boolean isVoice2Off = false;
//	private boolean isVoice3Off = false;
	private int mainVolume;
	private int balanceVolume;
	private int bassVolume;
	private SetbtnListener mCallback;
	SharedPreferences sh;
	private IKdAudioControlService m_advancevoice = IKdAudioControlService.Stub.asInterface(ServiceManager.getService("audioCtrl"));;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.voice_layout_seton, container, false);
		mcontext = inflater.getContext();
		sh = SharedPreferencesUtils.getSharedPreferences(getActivity());
		isVoiceOn = sh.getBoolean(Configs.KEY_VOL_SWITCH, true);
		mainVolume = sh.getInt(Configs.KEY_VOL_VALUE, 0);
//		balanceVolume = sh.getInt(Configs.KEY_VOL_BALANCE, 50*14/100); 
//		bassVolume = sh.getInt(Configs.KEY_VOL_BASS, 50*254/100); 
		try {
			balanceVolume = m_advancevoice.getSurroundWidth() * 2;
			bassVolume = m_advancevoice.getBassLevel() * 2;//65~125即130~250
		} catch (RemoteException e) {
			e.printStackTrace();
		} 
		initView(view);
		return view;
	}
	
	private void initView(View view){
		audiomanage = (AudioManager)mcontext.getSystemService(Context.AUDIO_SERVICE); 
		
		voice = (ImageButton) view.findViewById(R.id.voice);
		verticalSeekBar = (VerticalSeekBar) view.findViewById(R.id.verticalSeekBar);
		voice2 = (ImageButton) view.findViewById(R.id.voice2);
		verticalSeekBar2 = (VerticalSeekBar) view.findViewById(R.id.verticalSeekBar2);
		voice3 = (ImageButton) view.findViewById(R.id.voice3);
		verticalSeekBar3 = (VerticalSeekBar) view.findViewById(R.id.verticalSeekBar3);
		//高低音调节处理开始
		voice.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				verticalSeekBar.setProgress(50);
				bassVolume = 190;
				
				try {
					m_advancevoice.setBassLevel(bassVolume/2);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
//                Editor edt = SharedPreferencesUtils.getEditor(getActivity());
//        		edt.putInt(Configs.KEY_VOL_BASS, bassVolume);
//        		edt.commit();
			}
		});
//		currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC); 
		verticalSeekBar.setProgress(100*(bassVolume - 130)/120);
		verticalSeekBar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(VerticalSeekBar vBar) {
				try {
					m_advancevoice.setBassLevel(bassVolume/2);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				// TODO:
//                Editor edt = SharedPreferencesUtils.getEditor(getActivity());
//        		edt.putInt(Configs.KEY_VOL_BASS, bassVolume);
//        		edt.commit();
			}
			
			@Override
			public void onStartTrackingTouch(VerticalSeekBar vBar) {
				
			}
			
			@Override
			public void onProgressChanged(VerticalSeekBar vBar, int progress,
					boolean fromUser) {
				bassVolume = verticalSeekBar.getProgress()*120/100+130;  
			}
		});
		
		//平衡音调节处理开始
		voice2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				verticalSeekBar2.setProgress(50);
				balanceVolume = 50*14/100;
				
				try {
					m_advancevoice.setSurroundWidth(balanceVolume/2);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
//                Editor edt = SharedPreferencesUtils.getEditor(getActivity());
//        		edt.putInt(Configs.KEY_VOL_BALANCE, balanceVolume);
//        		edt.commit();
			}
		});
//		currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC); 
		verticalSeekBar2.setProgress(100*balanceVolume/14);
		verticalSeekBar2.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(VerticalSeekBar vBar) {
				try {
					m_advancevoice.setSurroundWidth(balanceVolume/2);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				// TODO:
//                Editor edt = SharedPreferencesUtils.getEditor(getActivity());
//        		edt.putInt(Configs.KEY_VOL_BALANCE, balanceVolume);
//        		edt.commit();
			}
			
			@Override
			public void onStartTrackingTouch(VerticalSeekBar vBar) {
				
			}
			
			@Override
			public void onProgressChanged(VerticalSeekBar vBar, int progress,
					boolean fromUser) {
				balanceVolume = verticalSeekBar2.getProgress()*14/100; 
			}
		});

		//音量调节处理开始
		voice3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!isVoiceOn){
					isVoiceOn = true;
					if(mainVolume == 0){
						mainVolume = 4;
					}
					verticalSeekBar3.setProgress(100*mainVolume/15);
					audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, verticalSeekBar3.getProgress()*15/100, 0);    
	                currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
//	                voice3.setImageResource(R.drawable.volume_03_on_l);
	                setVoiceIcon(currentVolume);
				}else{
					isVoiceOn = false;
					mainVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
					System.out.println("voice off");
					verticalSeekBar3.setProgress(0);
					audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);    
	                currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
//	                voice3.setImageResource(R.drawable.volume_03_off);
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
		
		mainVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  
		System.out.println("currentVolume~~~~~~~~"+(100*mainVolume/15));
		verticalSeekBar3.setProgress(100*mainVolume/15);
		verticalSeekBar3.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(VerticalSeekBar vBar) {
				if(verticalSeekBar3.getProgress() >0){
					isVoiceOn = true;
//					voice3.setImageResource(R.drawable.volume_03_on_l);
					setVoiceIcon(currentVolume);
					Editor edt = SharedPreferencesUtils.getEditor(getActivity());
					edt.putBoolean(Configs.KEY_VOL_SWITCH, isVoiceOn);
					edt.commit();
				} else if(verticalSeekBar3.getProgress() ==0){
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
				audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, progress*15/100, 0);    
				currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  
				setVoiceIcon(currentVolume);
			}
		});
		
		//设置点击处理
		set = (ImageButton) view.findViewById(R.id.set);
		set.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				VolumeActivity.instance.OnSetbtnClicked(1);
			}
		});
		
	}
	
	private void setVoiceIcon(int level){
		Log.i("Volume", "level:"+level);
		if(voice != null){
			if(level == 0){
				voice3.setImageResource(R.drawable.volume0_btn_selector);
			}else if(level >=1 && level <=4){
				voice3.setImageResource(R.drawable.volume1_btn_selector);
			}else if(level >=5 && level <=9){
				voice3.setImageResource(R.drawable.volume2_btn_selector);
			}else if(level >=10){
				voice3.setImageResource(R.drawable.volume3_btn_selector);
			}
		}
	}
	
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
		// TODO: balanceVolume = audiomanage.
		// TODO: bassVolume = audiomanage.
		System.out.println("currentVolume~~~~~~~~"+(100*mainVolume/15));
		if(!isVoiceOn){
			verticalSeekBar3.setProgress(0);
//			voice3.setImageResource(R.drawable.volume_03_off);
			setVoiceIcon(0);
		}else{
			verticalSeekBar3.setProgress((100*mainVolume/15));
//			voice3.setImageResource(R.drawable.volume_03_off_l);
			setVoiceIcon(mainVolume);
		}
		verticalSeekBar2.setProgress(100*balanceVolume/14);
		verticalSeekBar.setProgress(100*(bassVolume-130)/120);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}
