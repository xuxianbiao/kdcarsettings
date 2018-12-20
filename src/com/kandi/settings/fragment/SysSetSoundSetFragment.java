package com.kandi.settings.fragment;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kandi.settings.R;
import com.kandi.settings.widgets.VerticalSeekBar;

public class SysSetSoundSetFragment extends Fragment implements VerticalSeekBar.OnSeekBarChangeListener{
	private Activity activity;
	private VerticalSeekBar voiceSeekBar3;
	private VerticalSeekBar voiceSeekBar2;
	private VerticalSeekBar voiceSeekBar1;
	
	private AudioManager audiomanage;
	private int currentVolume; 
	private int voiceVolume;
	private int ringVolume;
	
	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.set_sysset_soundset, container, false);
		initview(view);
		return view;
	}
	
	private void initview(View view){
		voiceSeekBar1 = (VerticalSeekBar) view.findViewById(R.id.verticalSeekBar);
		voiceSeekBar1.setOnSeekBarChangeListener(this);
		voiceSeekBar2 = (VerticalSeekBar) view.findViewById(R.id.verticalSeekBar2);
		voiceSeekBar2.setOnSeekBarChangeListener(this);
		audiomanage = (AudioManager)activity.getSystemService(Context.AUDIO_SERVICE); 
		voiceSeekBar3 = (VerticalSeekBar) view.findViewById(R.id.verticalSeekBar3);
		currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  
		voiceSeekBar3.setProgress(100/15*currentVolume);
		voiceSeekBar3.setOnSeekBarChangeListener(this);
		
		currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
        voiceVolume = audiomanage.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        ringVolume = audiomanage.getStreamVolume(AudioManager.STREAM_RING);
		System.out.println("currentVolume~~~~~~~~"+(100/15)*currentVolume);
		voiceSeekBar3.setProgress(100/15*currentVolume);
		voiceSeekBar1.setProgress(100/15*voiceVolume);
		voiceSeekBar2.setProgress(100/15*ringVolume);
	}

	@Override
	public void onProgressChanged(VerticalSeekBar vBar, int progress,
			boolean fromUser) {
		switch (vBar.getId()) {
		case R.id.verticalSeekBar3:
			audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, progress/(100/15), 0);   
			break;
		case R.id.verticalSeekBar:
			audiomanage.setStreamVolume(AudioManager.STREAM_VOICE_CALL, progress/(100/15), 0);   
			break;
		case R.id.verticalSeekBar2:
			audiomanage.setStreamVolume(AudioManager.STREAM_RING, progress/(100/15), 0);   
			break;
		default:
			break;
		} 
	}

	@Override
	public void onStartTrackingTouch(VerticalSeekBar vBar) {
		
	}

	@Override
	public void onStopTrackingTouch(VerticalSeekBar vBar) {
		
	}
	
	
}
