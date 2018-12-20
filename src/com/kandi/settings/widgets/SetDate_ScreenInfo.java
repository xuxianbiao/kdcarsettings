package com.kandi.settings.widgets;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * 锟矫碉拷锟斤拷幕锟斤拷锟斤拷芏鹊锟�
 * @author 
 * @email chenshi011@163.com
 *
 */
public class SetDate_ScreenInfo {
	 private Activity activity;
	 /** 锟斤拷幕锟斤拷龋锟斤拷锟斤拷兀锟*/
	 private int width;
	 /**锟斤拷幕锟竭度ｏ拷锟斤拷锟截ｏ拷*/
	 private int height;
	 /**锟斤拷幕锟杰度ｏ拷0.75 / 1.0 / 1.5锟斤拷*/
	 private float density;
	 /**锟斤拷幕锟杰讹拷DPI锟斤拷120 / 160 / 240锟斤拷*/
	 private int densityDpi;
	 public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public float getDensity() {
		return density;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	public int getDensityDpi() {
		return densityDpi;
	}

	public void setDensityDpi(int densityDpi) {
		this.densityDpi = densityDpi;
	}

	public SetDate_ScreenInfo(Activity activity){
		 this.activity = activity;
		 ini();
	 }
	 
	 private void ini(){
		 DisplayMetrics metric = new DisplayMetrics();
		 activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
	     width = metric.widthPixels;  
	     height = metric.heightPixels; 
	     density = metric.density;  
	     densityDpi = metric.densityDpi;  
	 }
	 
	
}
