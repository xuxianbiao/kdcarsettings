package com.kandi.settings.fragment.setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kandi.settings.R;
import com.kandi.settings.fragment.SysSetBuletoothSetFragment;
import com.kandi.settings.fragment.SysSetDisplaySetFragment;
import com.kandi.settings.fragment.SysSetSoundSetFragment;
import com.kandi.settings.fragment.SysSetTimeSetFragment;
import com.kandi.settings.fragment.SysSetUpdateSetFragment;
import com.kandi.settings.fragment.SysSetWifiSetFragment;
import com.kandi.settings.utils.FragmentHolder;
import com.kandi.settings.widgets.SettingLeftTabsGroupLayout;

public class SystemSetFragment extends Fragment {
	private FragmentHolder fh;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.set_sysset, container, false);
		initview(view);
		return view;
	}

	private void initview(View view) {
		fh = new FragmentHolder(getFragmentManager());
		SettingLeftTabsGroupLayout groupTabsLayout = (SettingLeftTabsGroupLayout) view
				.findViewById(R.id.left_nav);
		final String[] tabs = getResources().getStringArray(
				R.array.set_sysset_tabs);
		final Fragment[] fragments = { new SysSetDisplaySetFragment(),
				/*new SysSetSoundSetFragment(),*/ new SysSetWifiSetFragment(),
				new SysSetBuletoothSetFragment(),
				new SysSetUpdateSetFragment(), new SysSetTimeSetFragment() };
		groupTabsLayout
				.setTabClickListener(new SettingLeftTabsGroupLayout.TabClickListener() {

					@Override
					public void onItemClick(View v, int position) {
						if (!fh.isAdded(tabs[position])) {
							fh.addFragment(tabs[position], fragments[position]);
							fh.addAndShow(R.id.sysset_container, tabs[position]);
						} else {
							fh.hideOthersAndShow(tabs[position]);
						}
					}
				});
		groupTabsLayout.addChildTabs(tabs);
	}
}
