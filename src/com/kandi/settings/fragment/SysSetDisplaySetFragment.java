package com.kandi.settings.fragment;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.kandi.settings.R;
import com.kandi.settings.activitys.WindowActivity;
import com.kandi.settings.driver.SystemSettingDriver2;
import com.kandi.settings.utils.LightnessControl;
import com.kandi.settings.utils.Utils;

public class SysSetDisplaySetFragment extends Fragment {
	private Activity mactivity;

	private ToggleButton teamBuyBaoDiTog;
	private SeekBar seekBar;
	private SystemSettingDriver2 ssd2 = SystemSettingDriver2.getInstance();

	private Button lang_ch;
	private Button lang_en;

	private AlertDialog cameralockDialog;
	
	private ImageView iconLight1;
	private ImageView iconLight2;

	@Override
	public void onAttach(Activity activity) {
		mactivity = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.set_sysset_displayset, container,
				false);
		initview(view);
		updateLanguegeBtn();
		return view;
	}

	private void initview(View view) {
		iconLight1 = (ImageView) view.findViewById(R.id.iconLight1);
		iconLight2 = (ImageView) view.findViewById(R.id.iconLight2);
		lang_ch = (Button) view.findViewById(R.id.lang_ch);
		lang_ch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Locale locale = getResources().getConfiguration().locale;
		        String language = locale.getLanguage();
		        if(language.endsWith("zh")){
		        	return;
		        }
				cameralockDialog = new AlertDialog.Builder(mactivity).create();
				cameralockDialog.show();
				cameralockDialog.getWindow().setContentView(
						R.layout.langalert_layout);
				cameralockDialog.getWindow().findViewById(R.id.yesbtn)
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								lang_ch.setBackgroundResource(R.drawable.set_shoots_tap_btn_on);
								lang_en.setBackgroundResource(R.drawable.set_shoots_tap_btn_off);
								try {
//									PresistentData.getInstance().setLang("ch");
									// TODO:将系统语言切换为中文
									Locale currentLocale = new Locale("zh",
											"CN");
									Utils.updateLanguage(currentLocale);
									
									// changeLan("ch");
									cameralockDialog.dismiss();
									restartApp(getActivity(), WindowActivity.class);
								} catch (Exception e) {
									e.printStackTrace();
									cameralockDialog.dismiss();
								}
							}
						});
				cameralockDialog.getWindow().findViewById(R.id.cancelbtn)
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cameralockDialog.dismiss();
							}
						});

			}
		});
		lang_en = (Button) view.findViewById(R.id.lang_en);
		lang_en.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Locale locale = getResources().getConfiguration().locale;
		        String language = locale.getLanguage();
		        if(language.endsWith("en")){
		        	return;
		        }
				cameralockDialog = new AlertDialog.Builder(mactivity).create();
				cameralockDialog.show();
				cameralockDialog.getWindow().setContentView(
						R.layout.langalert_layout);
				cameralockDialog.getWindow().findViewById(R.id.yesbtn)
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								lang_en.setBackgroundResource(R.drawable.set_shoots_tap_btn_on);
								lang_ch.setBackgroundResource(R.drawable.set_shoots_tap_btn_off);
								try {
//									PresistentData.getInstance().setLang("en");
									// TODO将系统语言切换为英文
									Utils.updateLanguage(Locale.ENGLISH);
									// changeLan("en");
									cameralockDialog.dismiss();
									restartApp(getActivity(), WindowActivity.class);
								} catch (Exception e) {
									e.printStackTrace();
									cameralockDialog.dismiss();
								}
							}
						});
				cameralockDialog.getWindow().findViewById(R.id.cancelbtn)
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cameralockDialog.dismiss();
							}
						});

			}
		});
//		if (PresistentData.getInstance().getLang().equals("en")) {
//			lang_en.setBackgroundResource(R.drawable.set_shoots_tap_btn_on);
//			lang_ch.setBackgroundResource(R.drawable.set_shoots_tap_btn_off);
//		} else {
//			lang_en.setBackgroundResource(R.drawable.set_shoots_tap_btn_off);
//			lang_ch.setBackgroundResource(R.drawable.set_shoots_tap_btn_on);
//		}

		seekBar = (SeekBar) view.findViewById(R.id.seekBar);
		seekBar.setProgress(LightnessControl.GetLightness(mactivity));
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				LightnessControl.SetLightness(mactivity, arg1);
			}
		});
		teamBuyBaoDiTog = (ToggleButton) view.findViewById(R.id.teamBuyBaoDiTog);
		teamBuyBaoDiTog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					LightnessControl.startAutoBrightness(mactivity);
					enableLightSeekbar(false);
				}else{
					LightnessControl.stopAutoBrightness(mactivity);
					enableLightSeekbar(true);
				}
				
				
			}
		});
		teamBuyBaoDiTog.setChecked(LightnessControl.isAutoBrightness(mactivity));

	}
	
	@Override
	public void onResume() {
		if(LightnessControl.isAutoBrightness(mactivity)) {
			enableLightSeekbar(false);
			teamBuyBaoDiTog.setChecked(true);
		}
		else {
			enableLightSeekbar(true);
			teamBuyBaoDiTog.setChecked(false);
		}
		super.onResume();
	}
	
	void enableLightSeekbar(boolean isEnable) {

		seekBar.setEnabled(isEnable);

		if(isEnable) {
			iconLight1.setImageResource(R.drawable.set_shoots_liangdutiaojie_icon_01);
			iconLight2.setImageResource(R.drawable.set_shoots_liangdutiaojie_icon_02);
		}
		else {
			iconLight1.setImageResource(R.drawable.set_shoots_liangdutiaojie_icon_01_disabled);
			iconLight2.setImageResource(R.drawable.set_shoots_liangdutiaojie_icon_02_disabled);
		}
	}

	private void changeLan(String language_code) {
		Resources res = mactivity.getResources();
		// Change locale settings in the app.
		DisplayMetrics dm = res.getDisplayMetrics();
		android.content.res.Configuration conf = res.getConfiguration();
		conf.locale = new Locale(language_code.toLowerCase());
		res.updateConfiguration(conf, dm);
	}
	
	private void updateLanguegeBtn(){
		Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh")){
        	lang_ch.setBackgroundResource(R.drawable.set_shoots_tap_btn_on);
			lang_en.setBackgroundResource(R.drawable.set_shoots_tap_btn_off);
        } else {
        	lang_ch.setBackgroundResource(R.drawable.set_shoots_tap_btn_off);
			lang_en.setBackgroundResource(R.drawable.set_shoots_tap_btn_on);
        }
	}

	@Override
	public void onDestroy() {
		if(cameralockDialog != null){
			cameralockDialog.dismiss();
			cameralockDialog = null;
		}
		super.onDestroy();
	}
	
	@Override
	public void onPause() {
		if(cameralockDialog != null){
			cameralockDialog.dismiss();
			cameralockDialog = null;
		}
		super.onPause();
	}
	
	public static void restartApp(Activity activity, Class<?> homeClass) {
        Intent intent = new Intent(activity, homeClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        // 杀掉进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

}
