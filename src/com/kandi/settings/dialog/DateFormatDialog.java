package com.kandi.settings.dialog;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.kandi.settings.R;

public class DateFormatDialog extends Dialog{
	private final String sYYYYMMDD = "yyyy-MM-dd";
	private final String sMMDDYYYY = "MM-dd-yyyy";
	private final String sDDMMYYYY = "dd-MM-yyyy";

	private RadioGroup dateforgroup;
	private RadioButton datefor1;
	private RadioButton datefor2;
	private RadioButton datefor3;
	private Button return_bt;
	
	private String temp = "";
	java.text.DateFormat dateFormat;
	private Activity activity;
	private DialogDismissListener listener;

	public DateFormatDialog(Activity activity,DialogDismissListener listener) {
		super(activity,R.style.my_dialog);
		this.activity = activity;
		this.listener = listener;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_sys_dateformat);
		dateforgroup = (RadioGroup) findViewById(R.id.dateforgroup);
		datefor1 = (RadioButton) findViewById(R.id.datefor1);
		datefor2 = (RadioButton) findViewById(R.id.datefor2);
		datefor3 = (RadioButton) findViewById(R.id.datefor3);
		dateFormat = android.text.format.DateFormat.getDateFormat(activity);
		SimpleDateFormat simpledateformat;
		simpledateformat = (SimpleDateFormat) dateFormat;
		String datestr = simpledateformat.toPattern();
		if(!TextUtils.isEmpty(datestr) && datestr.contains("/")){
			datestr = datestr.replaceAll("/", "-");
		}
		if("M-d-y".equals(datestr)){
			datefor1.setChecked(false);
			datefor2.setChecked(true);
			datefor3.setChecked(false);
			temp=sMMDDYYYY;
		}else if("y-M-d".equals(datestr)){
			datefor1.setChecked(true);
			datefor2.setChecked(false);
			datefor3.setChecked(false);
			temp=sYYYYMMDD;
		}else if(sYYYYMMDD.equals(datestr)){
			datefor1.setChecked(true);
			datefor2.setChecked(false);
			datefor3.setChecked(false);
			temp=datestr;
		}else if(sMMDDYYYY.equals(datestr)){
			datefor1.setChecked(false);
			datefor2.setChecked(true);
			datefor3.setChecked(false);
			temp=datestr;
		}else if(sDDMMYYYY.equals(datestr)){
			datefor1.setChecked(false);
			datefor2.setChecked(false);
			datefor3.setChecked(true);
			temp=datestr;
		}
		
//		Date date = new Date();
//		String sfm = dateFormat.format(date);
//		sfm = sfm.replace("/", "-");
//		String sfm1= new SimpleDateFormat(sYYYYMMDD).format(date);
//		String sfm2= new SimpleDateFormat(sMMDDYYYY).format(date);
//		String sfm3= new SimpleDateFormat(sDDMMYYYY).format(date);
//
//		datefor1.setChecked(sfm.equals(sfm1));
//		datefor2.setChecked(sfm.equals(sfm2));
//		datefor3.setChecked(sfm.equals(sfm3));
		
		dateforgroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(datefor1.getId()==checkedId){
					temp=sYYYYMMDD; //"yyyy-MM-dd";
				}else if(datefor2.getId()==checkedId){
					temp=sMMDDYYYY; //"MM-dd-yyyy";
				}else if(datefor3.getId()==checkedId){
					temp=sDDMMYYYY; //"dd-MM-yyyy";
				}
			}
		});
		return_bt = (Button) findViewById(R.id.timeset_return);
		return_bt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if("".equals(temp)){
					temp = "yyyy-MM-dd";
				}
				Settings.System.putString(activity.getContentResolver(),Settings.System.DATE_FORMAT, temp);
				
				if(listener != null){
					listener.onDismiss(temp, 0, null);
				}
				dismiss();
			}
		});
	}
	
}
