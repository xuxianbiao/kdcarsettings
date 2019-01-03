package com.kandi.settings.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kandi.settings.Configs;
import com.kandi.settings.R;
import com.kandi.settings.dialog.DateDialog;
import com.kandi.settings.dialog.DateFormatDialog;
import com.kandi.settings.dialog.DateTimeDialog;
import com.kandi.settings.dialog.DialogDismissListener;
import com.kandi.settings.driver.SystemSettingDriver2;
import com.kandi.settings.utils.SharedPreferencesUtils;

public class SysSetTimeSetFragment extends Fragment {
	SimpleDateFormat timeformate;
	SimpleDateFormat dateFormat;
	java.text.DateFormat timestate;
	
	//设置日期控件
	private TextView  set_date_txt;
	//设置时间控件
	private TextView  set_time_txt;
	//设置日期格式控件
	private TextView  set_dateformat_txt;
	//文字点击效果
	final int active=Color.rgb(26, 128, 172); //有效状态
	int defualtcolor;
	private String DateForStr;
	
	private SystemSettingDriver2 ssd2 = SystemSettingDriver2.getInstance();
	
	private final int AUTOCHANGESTATE = 1;
	
	private final int TIMETOSTATRE = 2;
	
	private ImageButton set_date_btn;
	private ImageButton set_time_btn;
	private ImageButton set_dateformat_btn;
	private ToggleButton teamNetTimeDiTog;
	private ToggleButton teamTimeFormatDiTog;
	
	private Activity mactivity;
	
	private DateDialog dateDialog;
	private DateTimeDialog timeDialog;
	private DateFormatDialog dateFormatDialog;
	
	private SharedPreferences sh;
	
	private RelativeLayout set_date_parent,set_time_parent,dataformat_layout;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		mactivity = activity;
		super.onAttach(activity);
		sh = SharedPreferencesUtils.getSharedPreferences(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.set_sysset_timeset, container, false);
		initview(view);
		// TODO 刷新界面用
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				Timer timer = new Timer();
//				timer.scheduleAtFixedRate(new TimerTask() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						handle.sendEmptyMessage(0);
//					}
//				}, 1, 60000);
//			}
//		}).start();
		registBroadCastReceiver();
		return view;
	}
	
	private void initview(View view){
		String datapattern = ((SimpleDateFormat)android.text.format.DateFormat.getDateFormat(mactivity)).toPattern();
		if(datapattern.equals("M/d/y")){
			datapattern = "MM/dd/yyyy";
		}else if(datapattern.equals("y-M-d")){
			datapattern = "yyyy-MM-dd";
		}
		dateFormat = new SimpleDateFormat(datapattern);
		
		set_date_parent = (RelativeLayout)view.findViewById(R.id.set_date_parent);

		set_time_parent = (RelativeLayout)view.findViewById(R.id.set_time_parent);
		
		dataformat_layout = (RelativeLayout)view.findViewById(R.id.dataformat_layout);
		
		dataformat_layout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				RelativeLayout layout = (RelativeLayout)arg0;
				if(arg1.getAction()==MotionEvent.ACTION_DOWN){
					layout.setBackgroundColor(active);
				}else if(arg1.getAction()==MotionEvent.ACTION_UP){
					set_dateformat_btn.performClick();
					layout.setBackground(null);
				}
				return true;
			}
		});
		
		String str;
		str = dateFormat.format(new java.util.Date()); 
		//设置日期事件监听
		set_date_txt = (TextView)view.findViewById(R.id.set_date_txt);
		set_date_txt.setText(getResources().getString(R.string.set_system_date)+" "+str);
		defualtcolor = set_date_txt.getTextColors().getDefaultColor();
		set_date_btn = (ImageButton)view.findViewById(R.id.set_date_btn);
//		set_date_parent.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				set_date_txt.setTextColor(active);
////				Intent intent = new Intent(mactivity, DateActivity.class);
////				startActivityForResult(intent, mactivity.RESULT_CANCELED);
//				if(dateDialog == null){
//					dateDialog = new DateDialog(getActivity(), new DialogDismissListener() {
//						
//						@Override
//						public void onDismiss(String info, int what, Object obj) {
//							SimpleDateFormat Cur_Time = new SimpleDateFormat(Settings.System.getString(mactivity.getContentResolver(),Settings.System.DATE_FORMAT));
//							Calendar calendar = (Calendar) obj;
//							set_date_txt.setText(getResources().getString(R.string.set_system_date)+" "+Cur_Time.format(calendar.getTime()));
//						}
//					});
//				}
//				dateDialog.show();
//				
//			}
//			
//		});
		
		//设置时间格式事件监听
		set_time_txt = (TextView)view.findViewById(R.id.set_time_txt);
		set_time_btn = (ImageButton)view.findViewById(R.id.set_time_btn);
//		set_time_parent.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				set_time_txt.setTextColor(active);
//				/*Intent intent = new Intent(mactivity, DateTimeActivity.class);
//				startActivityForResult(intent, mactivity.RESULT_CANCELED);*/
//				if(timeDialog == null){
//					timeDialog = new DateTimeDialog(getActivity(), new DialogDismissListener() {
//						
//						@Override
//						public void onDismiss(String info, int what, Object obj) {
//							SimpleDateFormat Cur_Time = new SimpleDateFormat(sh.getString(Configs.KEY_TIME_FORMAT, "HH:mm"));
//							Calendar calendar = (Calendar) obj;
//							set_time_txt.setText(getResources().getString(R.string.set_system_time)+" "+Cur_Time.format(calendar.getTime()));
//						}
//					});
//				}
//				timeDialog.show();
//			}
//			
//		});
		//设置日期格式事件监听

		DateForStr = dateFormat.format(new java.util.Date());
		set_dateformat_txt = (TextView)view.findViewById(R.id.set_dateformat_txt);
		set_dateformat_txt.setText(getResources().getString(R.string.set_system_dateformat)+" "+DateForStr);
		set_dateformat_btn = (ImageButton)view.findViewById(R.id.set_dateformat_btn);
		set_dateformat_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				set_dateformat_txt.setTextColor(active);
//				Intent intent = new Intent(mactivity, SetDateForActivity.class);
//				startActivityForResult(intent, mactivity.RESULT_CANCELED);
				if(dateFormatDialog==null){
					dateFormatDialog = new DateFormatDialog(getActivity(), new DialogDismissListener() {
						
						@Override
						public void onDismiss(String info, int what, Object obj) {
							SimpleDateFormat sdf = new SimpleDateFormat(info);
							set_dateformat_txt.setText(getResources().getString(R.string.set_system_dateformat)+" "+sdf.format(new Date()));
							set_date_txt.setText(getResources().getString(R.string.set_system_date)+" "+sdf.format(new Date()));
						}
					});
				}
				dateFormatDialog.show();
				
			}
			
		});
		
		teamNetTimeDiTog = (ToggleButton) view.findViewById(R.id.teamNetTimeDiTog);
		String autotimestate = Settings.Global.getString(mactivity.getContentResolver(), Settings.Global.AUTO_TIME);
		if("1".equals(autotimestate)){
			teamNetTimeDiTog.setChecked(true);
			enableDateTimeSet(false);
		}else{
			teamNetTimeDiTog.setChecked(false);
			enableDateTimeSet(true);
		}
		teamNetTimeDiTog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				//设置调整后重新调整界面
				enableDateTimeSet(!arg1);
				if(arg1){
					Settings.Global.putString(mactivity.getContentResolver(), Settings.Global.AUTO_TIME, "1");
				}else{
					Settings.Global.putString(mactivity.getContentResolver(), Settings.Global.AUTO_TIME, "0");
				}
				Intent mIntent = new Intent(ACTION_AUTO_TIME_CHANGED);  
				getActivity().sendBroadcast(mIntent);
			}
		});
		
		teamTimeFormatDiTog = (ToggleButton) view.findViewById(R.id.teamTimeFormatTog);
		//boolean timeforstate = android.text.format.DateFormat.is24HourFormat(mactivity.getApplicationContext());
		String timeforstate = android.provider.Settings.System.getString(getActivity().getContentResolver(), Settings.System.TIME_12_24);
		if(getString(R.string.format24).equals(timeforstate)){
			teamTimeFormatDiTog.setChecked(true);
		}else{
			android.provider.Settings.System.putString(getActivity().getContentResolver(), Settings.System.TIME_12_24, getString(R.string.format12));
			teamTimeFormatDiTog.setChecked(false);
		}
		timestate = android.text.format.DateFormat.getTimeFormat(mactivity);
		set_time_txt.setText(getResources().getString(R.string.set_system_time)+" "+timestate.format(new java.util.Date()));
		teamTimeFormatDiTog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				ContentResolver cv = getActivity().getContentResolver();
				if(arg1){
					//Settings.System.putString(mactivity.getContentResolver(),Settings.System.TIME_12_24,"24");
					android.provider.Settings.System.putString(cv, Settings.System.TIME_12_24, getString(R.string.format24));
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		        	String dateStr = sdf.format(new java.util.Date());
					set_time_txt.setText(getResources().getString(R.string.set_system_time)+" "+dateStr);
				}else{
					//Settings.System.putString(mactivity.getContentResolver(),Settings.System.TIME_12_24,"12");
					android.provider.Settings.System.putString(cv, Settings.System.TIME_12_24, getString(R.string.format12));
					SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm");
		        	String dateStr = sdf.format(new java.util.Date());
					set_time_txt.setText(getResources().getString(R.string.set_system_time)+" "+dateStr);
				}
				
				Editor ed = sh.edit();
				ed.putString(Configs.KEY_TIME_FORMAT, arg1 ? "HH:mm":"a hh:mm");
				ed.commit();
			}
		});
	}
	

	//设置完成结果读取接口
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(Activity.RESULT_CANCELED == resultCode){
		
			
		}else if(Activity.MODE_APPEND == resultCode){
			Bundle bundle = null;
			if(data!=null&&(bundle=data.getExtras())!=null){
//				timeformate = new SimpleDateFormat(bundle.getString("Result"));
//				String redata = timeformate.format(new java.util.Date());
				String redata = dateFormat.format(new java.util.Date());
				set_date_txt.setText(getResources().getString(R.string.set_system_date)+" "+redata);
				set_dateformat_txt.setText(getResources().getString(R.string.set_system_dateformat)+" "+redata);
			}
		}else if(808 == resultCode){
			new Thread() {
	            @Override
	            public void run() {
	            	handle.sendEmptyMessage(0);
	            }
	        }.start();
		}
		set_date_txt.setTextColor(defualtcolor);
		set_time_txt.setTextColor(defualtcolor);
//		set_dateformat_txt.setTextColor(defualtcolor);
	}
	
	private Handler handle = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				RefreshView();
				break;
			case AUTOCHANGESTATE:
				AutoRefresh();
				break;
			case TIMETOSTATRE:
				
				break;
			}
			return true;
		}

	});
	
	private void RefreshView(){
		String redata = dateFormat.format(new java.util.Date());
		String str = timestate.format(new java.util.Date());
		set_date_txt.setText(getResources().getString(R.string.set_system_date)+" "+redata);
		set_time_txt.setText(getResources().getString(R.string.set_system_time)+" "+str);
		set_dateformat_txt.setText(getResources().getString(R.string.set_system_dateformat)+" "+redata);
	}
	
	private void AutoRefresh(){
//		Settings.Global.putString(mactivity.getContentResolver(), Settings.Global.AUTO_TIME, "1");
		if(getActivity() != null){
			Date date = new Date();
			ContentResolver cv = getActivity().getContentResolver();
			String strTimeFormat = android.provider.Settings.System.getString(cv,
					android.provider.Settings.System.TIME_12_24);
			if(strTimeFormat != null || !"".equals(strTimeFormat)) {
				if(strTimeFormat.equals(getString(R.string.format24))){
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
					String dateStr = sdf.format(date);
					set_time_txt.setText(getResources().getString(R.string.set_system_time)+" "+dateStr);
				}else{
					SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm");
					String dateStr = sdf.format(date);
					set_time_txt.setText(getResources().getString(R.string.set_system_time)+" "+dateStr);
				}
				String datapattern = ((SimpleDateFormat)android.text.format.DateFormat.getDateFormat(mactivity)).toPattern();
				if(datapattern.equals("M/d/y")){
					datapattern = "MM/dd/yyyy";
				}else if(datapattern.equals("y-M-d")){
					datapattern = "yyyy-MM-dd";
				}
				dateFormat = new SimpleDateFormat(datapattern);
				String redata = dateFormat.format(date);
				set_date_txt.setText(getResources().getString(R.string.set_system_date)+" "+redata);
				set_dateformat_txt.setText(getResources().getString(R.string.set_system_dateformat)+" "+redata);
			}else{
				android.provider.Settings.System.putString(cv,android.provider.Settings.System.TIME_12_24,getString(R.string.format12));
			}
		}
	}
	
	/**
	 * 根据“自动确定时间设置”调节
	 * 是否允许设置日期、时间
	 * @param flag=true表示关闭自动时间获取
	 */
	private void enableDateTimeSet(boolean flag){
		if(flag){
			set_date_parent.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					RelativeLayout layout = (RelativeLayout)arg0;
					if(arg1.getAction()==MotionEvent.ACTION_DOWN){
						layout.setBackgroundColor(active);
					}else if(arg1.getAction()==MotionEvent.ACTION_UP){
						dateDialog = new DateDialog(getActivity(), new DialogDismissListener() {
							@Override
							public void onDismiss(String info, int what, Object obj) {
								//SimpleDateFormat Cur_Time = new SimpleDateFormat(sh.getString(Configs.KEY_DATE_FORMAT,"yyyy-MM-dd"));
								SimpleDateFormat Cur_Time = new SimpleDateFormat(Settings.System.getString(getActivity().getContentResolver(), Settings.System.DATE_FORMAT));
								Calendar calendar = (Calendar) obj;
								set_date_txt.setText(getResources().getString(R.string.set_system_date)+" "+Cur_Time.format(calendar.getTime()));
								set_dateformat_txt.setText(getResources().getString(R.string.set_system_dateformat)+" "+Cur_Time.format(calendar.getTime()));
								long when = calendar.getTimeInMillis();
								SystemClock.setCurrentTimeMillis(when);
							}
						});
						dateDialog.show();
						layout.setBackground(null);
					}
					return true;
				}
			});

			set_date_txt.setTextColor(getResources().getColor(R.color.white));
			set_date_btn.setImageResource(R.drawable.point_style);
			set_time_parent.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					RelativeLayout layout = (RelativeLayout)arg0;
					if(arg1.getAction()==MotionEvent.ACTION_DOWN){
						layout.setBackgroundColor(active);
					}else if(arg1.getAction()==MotionEvent.ACTION_UP){
						timeDialog = new DateTimeDialog(getActivity(), new DialogDismissListener() {
							
							@Override
							public void onDismiss(String info, int what, Object obj) {
								SimpleDateFormat Cur_Time = new SimpleDateFormat(sh.getString(Configs.KEY_TIME_FORMAT, "HH:mm"));
								Calendar calendar = (Calendar) obj;
								set_time_txt.setText(getResources().getString(R.string.set_system_time)+" "+Cur_Time.format(calendar.getTime()));
								long when = calendar.getTimeInMillis();
								SystemClock.setCurrentTimeMillis(when);
							}
						});
						timeDialog.show();
						layout.setBackground(null);
					}
					return true;
				}
				
			});
			set_time_txt.setTextColor(getResources().getColor(R.color.white));
			set_time_btn.setImageResource(R.drawable.point_style);
		}else{
			set_date_parent.setOnTouchListener(null);
			set_date_txt.setTextColor(getResources().getColor(R.color.grey));
			set_date_btn.setImageResource(R.drawable.entry_disabled);
			set_time_parent.setOnTouchListener(null);
			set_time_txt.setTextColor(getResources().getColor(R.color.grey));
			set_time_btn.setImageResource(R.drawable.entry_disabled);
		}
	}
	
	
	
	@Override
	public void onDestroy() {
		unregistBroadCastReceiver();
		if(dateFormatDialog != null){
			dateFormatDialog.dismiss();
			dateFormatDialog = null;
		}
		if(dateDialog != null){
			dateDialog.dismiss();
			dateDialog = null;
		}
		if(timeDialog != null){
			timeDialog.dismiss();
			timeDialog = null;
		}
		super.onDestroy();
	}
	
	@Override
	public void onPause() {
		if(dateFormatDialog != null){
			dateFormatDialog.dismiss();
			dateFormatDialog = null;
		}
		if(dateDialog != null){
			dateDialog.dismiss();
			dateDialog = null;
		}
		if(timeDialog != null){
			timeDialog.dismiss();
			timeDialog = null;
		}
		super.onPause();
	}

	private AutoTimeChanageReceiver autoTimeChanageReceiver;
	public final static String ACTION_AUTO_TIME_CHANGED = "com.kangdi.BroadCast.AutoTimeChange";
	public class AutoTimeChanageReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
				handle.sendEmptyMessage(AUTOCHANGESTATE);
			}
			if (intent.getAction().equals(ACTION_AUTO_TIME_CHANGED)) {
				handle.sendEmptyMessageDelayed(AUTOCHANGESTATE, 3000);
			}
		}
	}
	
	private void registBroadCastReceiver() {
        if (null == autoTimeChanageReceiver) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_AUTO_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIME_TICK);
            autoTimeChanageReceiver = new AutoTimeChanageReceiver();
            getActivity().registerReceiver(autoTimeChanageReceiver, filter);
        }
    }

    private void unregistBroadCastReceiver() {
        if (null != autoTimeChanageReceiver) {
        	getActivity().unregisterReceiver(autoTimeChanageReceiver);
        	autoTimeChanageReceiver = null;
        }
    }
	
}
