package com.kandi.settings.fragment.setting;

import java.io.File;

import org.ebookdroid.pdfdroid.PdfViewerActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kandi.settings.R;
import com.kandi.settings.dialog.RecoveryDialog;
import com.kandi.settings.driver.ConfigDriver1xCarCtrlSetup;
import com.kandi.settings.driver.DriverServiceManger;
import com.kandi.settings.utils.ACacheUtil;
import com.kandi.settings.utils.ToastUtil;


public class AboutFragment extends Fragment {

	final int Onclick_Num=3;
	private int click_vwcs=0;
	private int click_systemui=0;
	private int click_home=0;
	private int click_blue=0;
	final int Onclick_Numblue=5;
	final int Onclick_NumSetting=5;
	private int click_setting=0;
	RecoveryDialog recoveryDialog;
	TextView txt_about_pdf;
	ACacheUtil acache;
	ConfigDriver1xCarCtrlSetup model;
	TextView txt_carvin;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		model = DriverServiceManger.getInstance().getConfigDriver1xCarCtrlSetup();
		View view = inflater.inflate(R.layout.fg_about, null);
		TextView about_vwcs = (TextView) view.findViewById(R.id.txt_about_vwcs);
		TextView about_systemui = (TextView) view.findViewById(R.id.txt_about_systemui);
		TextView about_setting = (TextView) view.findViewById(R.id.txt_about_setting);
		TextView about_home = (TextView) view.findViewById(R.id.txt_about_home);
		TextView about_ringcall = (TextView) view.findViewById(R.id.txt_about_ringcall);
		TextView txt_about_blue = (TextView) view.findViewById(R.id.txt_about_blue);
		txt_carvin = (TextView) view.findViewById(R.id.txt_carvin);
		TextView about_os = (TextView) view.findViewById(R.id.txt_about_os);
		TextView txt_about_hard = (TextView) view.findViewById(R.id.txt_about_hard);
		txt_about_pdf = (TextView) view.findViewById(R.id.txt_about_pdf);
		txt_about_pdf.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				File file = new File(getString(R.string.pdf_path));
				if(file.exists()){
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
					intent.setClass(getActivity(), PdfViewerActivity.class);
					startActivity(intent);
				}
			}
			
		});
		txt_about_pdf.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					txt_about_pdf.setTextColor(getResources().getColor(R.color.about_blue));
				} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
					txt_about_pdf.setTextColor(getResources().getColor(R.color.about_white));
				}
				return false;
			}
		});
		
		//显示vwcs_ser、systemUI、home、setting版本读取
		String str = DriverServiceManger.getInstance().getVersion();
		about_vwcs.setText(getString(R.string.about_driver)+" "+(str==null?getString(R.string.service_version_unknow):str));
		about_systemui.setText(getString(R.string.about_systemui)+" ");
		about_setting.setText(getString(R.string.about_carsetting)+" "+getString(R.string.version_code));
		about_home.setText(getString(R.string.about_home)+" "+GetVersion("com.kandi.home"));
		about_ringcall.setText(getString(R.string.about_ringcall)+" "+GetVersion("com.kangdi.ringcall"));
		about_systemui.setText(getString(R.string.about_systemui)+" "+GetVersion("com.kandi.systemui"));
		txt_about_blue.setText(getString(R.string.about_bluetooth)+" "+SystemProperties.get("sys.kd.btversion",""));
		if(model != null){
			txt_carvin.setText(getString(R.string.about_carvin)+" "+model.getCarVin());
		}
		txt_carvin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				model = DriverServiceManger.getInstance().getConfigDriver1xCarCtrlSetup();
				if(model != null){
					model.requestCarVin();
				}
			}
		});
		about_os.setText(getString(R.string.about_os) + " "+android.os.Build.MODEL + "," + android.os.Build.VERSION.SDK_INT + ","+ android.os.Build.VERSION.RELEASE+","+ SystemProperties.get("ro.yunos.build.version",
                Build.VERSION.RELEASE));
		String hardversion = SystemProperties.get("sys.kd.hardwareversion","");
		if("".equals(hardversion)){
			txt_about_hard.setVisibility(View.GONE);
		}else if(hardversion.contains(getString(R.string.about_QYv1_5))){
			txt_about_hard.setText(getString(R.string.about_hardversion)+" "+hardversion);
		}else if(getString(R.string.about_QYv1_4).equals(hardversion)){
			txt_about_hard.setText(getString(R.string.about_hardversion)+" "+hardversion);
		}
		about_vwcs.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				click_vwcs++;
				if(click_vwcs>=Onclick_Num){
					click_vwcs=0;
					Intent intent = new Intent(Intent.ACTION_MAIN);
		            intent.setClassName("acer.log","acer.log.tab_host_main");
		            try {
		            	startActivity(intent);
		            }catch (Exception e){
		            	ToastUtil.showDbgToast(AboutFragment.this.getActivity(),"Unable to start activity"+intent.toString());
		            }
				}else{
					ToastUtil.showDbgToast(AboutFragment.this.getActivity(),getResources().getString(R.string.point_hint)+(Onclick_Num-click_vwcs)+getResources().getString(R.string.count_in));
				}
			}
			
		});
		
		about_systemui.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				click_systemui++;
				if(click_systemui>=Onclick_Num){
					click_systemui=0;
					Intent intent = new Intent(Intent.ACTION_MAIN);
		            intent.setClassName("com.android.apr","com.android.apr.android.AprConfigActivity");
		            try {
		            	startActivity(intent);
		            }catch (Exception e){
		            	ToastUtil.showDbgToast(AboutFragment.this.getActivity(),"Unable to start activity"+intent.toString());
		            }
				}else{
					ToastUtil.showDbgToast(AboutFragment.this.getActivity(),getResources().getString(R.string.point_hint)+(Onclick_Num-click_systemui)+getResources().getString(R.string.count_in_apr));
				}
			}
			
		});
		
		about_setting.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				click_setting++;
				if(click_setting>=Onclick_NumSetting){
					click_setting=0;
					if(recoveryDialog == null){
						recoveryDialog = new RecoveryDialog(getActivity());
					}
					recoveryDialog.show();
					//getActivity().sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
				}
			}
			
		});
		
		about_home.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				click_home++;
				if(click_home>=Onclick_Num){
					click_home=0;
		            ToastUtil.showToast(AboutFragment.this.getActivity(), getResources().getString(R.string.uuid) + SystemProperties.get("ro.aliyun.clouduuid"), Toast.LENGTH_LONG);
				}
			}
			
		});
		
		txt_about_blue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				click_blue++;
				if(click_blue>=Onclick_Numblue){
					click_blue=0;
		            ToastUtil.showToast(AboutFragment.this.getActivity(), getResources().getString(R.string.blue_hardware) + SystemProperties.get("sys.kd.hardwareversion"), Toast.LENGTH_LONG);
		            acache = ACacheUtil.get();
		            acache.clear();
				}
			}
		});
		
		return view;
	}
	
	private String GetVersion(String packagename){
	    try {
	    	//PackageManager packageManager = this.getActivity().getPackageManager();
	        PackageInfo info = this.getActivity().getPackageManager().getPackageInfo(packagename, 0);
	        String version = info.versionName;
	        return version;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	@Override
	public void onResume() {
		super.onResume();
		mhandler.sendEmptyMessageDelayed(0,1000);
	}
	
	Handler mhandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if(isAdded()){
					model = DriverServiceManger.getInstance().getConfigDriver1xCarCtrlSetup();
					if(model != null){
						txt_carvin.setText(getString(R.string.about_carvin)+" "+model.getCarVin());
					}
					txt_about_pdf.setTextColor(getResources().getColor(R.color.about_blue));
					mhandler.sendEmptyMessageDelayed(1, 1000);
				}
				break;

			case 1:
				if(isAdded()){
					txt_about_pdf.setTextColor(getResources().getColor(R.color.about_white));
				}
				break;
			}
		}
		
	};
	@Override
	public void onDestroy() {
		if(recoveryDialog != null){
			recoveryDialog.dismiss();
			recoveryDialog = null;
		}
		super.onDestroy();
	}
	
}
