package com.kandi.settings.fragment.setting;

import java.util.ArrayList;
import com.kandi.settings.R;
import com.kandi.settings.fragment.setting.manager.BCUFragment;
import com.kandi.settings.fragment.setting.manager.BatteryFragment;
import com.kandi.settings.fragment.setting.manager.ChargerFragment;
import com.kandi.settings.fragment.setting.manager.MotorControlFragment;
import com.kandi.settings.fragment.setting.manager.PasswordUpdateFragment;
import com.kandi.settings.fragment.setting.manager.SystemErrorFragment;
import com.kandi.settings.fragment.setting.manager.SystemWarmingFragment;
import com.kandi.settings.utils.FragmentHolder;
import com.kandi.settings.widgets.SettingLeftTabsGroupLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ManagerSetFragment extends Fragment {

	//管理设置下面的7项
	FragmentHolder fragmentHolder = null;
	String[] tabs = null;
	ArrayList<Class<? extends Fragment>> fragmentClazzs = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragmentHolder = new FragmentHolder(getFragmentManager());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fg_manager_settings, null);
		initFragments();
		SettingLeftTabsGroupLayout groupTabsLayout = (SettingLeftTabsGroupLayout) view.findViewById(R.id.tabs_layout);
		tabs = getResources().getStringArray(R.array.manager_tabs);
		groupTabsLayout.setTabClickListener(new SettingLeftTabsGroupLayout.TabClickListener() {
			
			@Override
			public void onItemClick(View v, int position) {
				if(!fragmentHolder.isAdded(tabs[position])){
					try {
						fragmentHolder.addFragment(tabs[position], fragmentClazzs.get(position).newInstance());
						fragmentHolder.addAndShow(R.id.content_frame, tabs[position]);
					} catch (java.lang.InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}else{
					fragmentHolder.hideOthersAndShow(tabs[position]);
				}
			}
		});
		groupTabsLayout.addChildTabs(tabs);
		return view;
	}
	
	private void initFragments(){
		fragmentClazzs = new ArrayList<Class<? extends Fragment>>(10);
		fragmentClazzs.add(BatteryFragment.class);
		fragmentClazzs.add(ChargerFragment.class);
		fragmentClazzs.add(BCUFragment.class);
		fragmentClazzs.add(MotorControlFragment.class);
		fragmentClazzs.add(SystemWarmingFragment.class);
		fragmentClazzs.add(SystemErrorFragment.class);
		fragmentClazzs.add(PasswordUpdateFragment.class);
	}
}
