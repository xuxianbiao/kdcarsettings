package com.kandi.settings.fragment.setting;


import com.kandi.settings.R;
import com.kandi.settings.fragment.CarSetDriveSetFragment;
import com.kandi.settings.fragment.CarSetWinLockFragment;
import com.kandi.settings.utils.FragmentHolder;
import com.kandi.settings.widgets.SettingLeftTabsGroupLayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CarSetFragment extends Fragment{
	private FragmentHolder fh;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.set_carset, container, false);
		initview(view);
		return view;
	}
	
	private void initview(View view){
		fh = new FragmentHolder(getFragmentManager());
		SettingLeftTabsGroupLayout groupTabsLayout = (SettingLeftTabsGroupLayout) view.findViewById(R.id.left_nav);
		final String[] tabs = getResources().getStringArray(R.array.set_carset_tabs);
		final Fragment[] fragments = {new CarSetDriveSetFragment()/*,new CarSetWinLockFragment()*/};
		groupTabsLayout.setTabClickListener(new SettingLeftTabsGroupLayout.TabClickListener() {
			
			@Override
			public void onItemClick(View v, int position) {
				if(!fh.isAdded(tabs[position])){
					fh.addFragment(tabs[position], fragments[position]);
					fh.addAndShow(R.id.carset_container, tabs[position]);
				}else{
					fh.hideOthersAndShow(tabs[position]);
				}
			}
		});
		groupTabsLayout.addChildTabs(tabs);
	}
	
}
