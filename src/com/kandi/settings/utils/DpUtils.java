package com.kandi.settings.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

public class DpUtils {
	public static int screanWidth = 0;
	public static int screanHigth = 0;
	
	public int getScreanWidth(Resources res){
		if(screanWidth == 0){
			DisplayMetrics dm = res.getDisplayMetrics();
			screanWidth = dm.widthPixels;
		}
		return screanWidth;
	}
	
	public int getScreanHigth(Resources res){
		if(screanHigth == 0){
			DisplayMetrics dm = res.getDisplayMetrics();
			screanHigth = dm.heightPixels;
		}
		return screanHigth;
	}
	
	public static int dp2px(Resources res,int dp){
		DisplayMetrics dm = res.getDisplayMetrics();
		int px = (int) (dp*dm.density + 0.5f);
		return px;
	}
	
	public static int sp2px(Resources res,int sp){
		DisplayMetrics dm = res.getDisplayMetrics();
		int px = (int) (sp*dm.scaledDensity + 0.5f);
		return px;
	}
}
