package com.kandi.settings.fragment;

import com.kandi.settings.R;
import com.kandi.settings.dialog.LoginDialog;
import com.kandi.settings.fragment.setting.AboutFragment;
import com.kandi.settings.fragment.setting.CarSetFragment;
import com.kandi.settings.fragment.setting.ManagerSetFragment;
import com.kandi.settings.fragment.setting.SystemSetFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
/**
 * 主系统设置界面
 * @author illidan
 *
 */
public class SettingsFragment extends Fragment {
	
	private ViewPager viewPager;
	private int currIndex = 0;//0车辆设置，1系统设置，2管理设置，3关于
	private View[] tabViews;
	private Fragment[] fragments;
	private ImageView cursorView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view  = inflater.inflate(R.layout.fg_settings, null);
		viewPager = (ViewPager) view.findViewById(R.id.viewpager);
		tabViews = new View[4];
		tabViews[0] = view.findViewById(R.id.set_shoots_title_01);
		tabViews[1] = view.findViewById(R.id.set_shoots_title_02);
		tabViews[2] = view.findViewById(R.id.set_shoots_title_03);
		tabViews[3] = view.findViewById(R.id.set_shoots_title_04);
		cursorView = (ImageView) view.findViewById(R.id.switch_tab_image);
		
		for(int i =0;i<tabViews.length;i++){
			tabViews[i].setOnClickListener(clickListener);
		}
		
		viewPager.setAdapter(new MyFragmentPagerAdapter(getFragmentManager()));
		viewPager.setOnPageChangeListener(changeListener);
		tabViews[currIndex].setSelected(true);
		cursorView.setMaxWidth(90);
		return view;
	}
	
	OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.set_shoots_title_01:
				viewPager.setCurrentItem(0, true);
				break;
			case R.id.set_shoots_title_02:
				viewPager.setCurrentItem(1, true);
				break;
			case R.id.set_shoots_title_03:
				viewPager.setCurrentItem(2, true);
				break;
			case R.id.set_shoots_title_04:
				viewPager.setCurrentItem(3, true);
				break;
			}
		}
	};
	
	boolean isLogined = false;
	private LoginDialog loginDialog = null;
	
	OnPageChangeListener changeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method 
			freshTabViews(currIndex, arg0);
			currIndex = arg0;
			
			if(arg0 == 2){
				//管理设置需要输入密码才能玩
				if(!isLogined){
					if(loginDialog == null){
						loginDialog = new LoginDialog(getActivity(), new LoginDialog.OnLoginResult() {
							
							@Override
							public void result(boolean isSuccess) {
								isLogined = isSuccess;
								if(!isSuccess){
									//没有登陆成功，直接回到第一个界面
									viewPager.setCurrentItem(0, false);
								}
							}
						},false);
					}
					loginDialog.show();
				}
			}else{
				isLogined = false;
			}
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	};
	
	//刷新指示栏状态
	private void freshTabViews(int last,int now){
		tabViews[now].setSelected(true);
		tabViews[last].setSelected(false);
		
		FrameLayout.LayoutParams lp = (LayoutParams) cursorView.getLayoutParams();
		if(now != 3){
			lp.leftMargin = (int) tabViews[now].getLeft()-26;
		}else{
			lp.leftMargin = (int) tabViews[now].getLeft();
		}
		Log.i("hdt", tabViews[now].getLeft()+"   "+tabViews[now].getX());
		cursorView.setLayoutParams(lp);
	}
	
	class MyFragmentPagerAdapter extends FragmentStatePagerAdapter{

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
			fragments = new Fragment[4];
		}

		@Override
		public Fragment getItem(int arg0) {
			return getFragment(arg0);
		}

		@Override
		public int getCount() {
			return 4;
		}
		
		private Fragment getFragment(int position){
			if(position>=0 && position<=3){
				Fragment f = fragments[position];
				if(f == null){
					//懒加载
					switch (position) {
					case 0:
						f = new CarSetFragment();
						break;
					case 1:
						f = new SystemSetFragment();
						break;
					case 2:
						f = new ManagerSetFragment();
						break;
					case 3:
						f = new AboutFragment();
						break;
					}
					fragments[position] = f;
					return f;
				}else{
					return f;
				}
			}
			return new Fragment();
		}
	}
	
	@Override
	public void onDestroy() {
		if(loginDialog != null){
			loginDialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public void onPause() {
		if(loginDialog != null){
			loginDialog.dismiss();
		}
		super.onPause();
	}
	
}
