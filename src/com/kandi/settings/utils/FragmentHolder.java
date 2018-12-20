package com.kandi.settings.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class FragmentHolder {

	private FragmentManager fgManager;
	private Map<String, Fragment> fragments;

	public FragmentHolder(FragmentManager fragmentManager) {
		if (fragments == null) {
			fragments = new HashMap<String, Fragment>();
		}
		fgManager = fragmentManager;
	}

	public boolean addFragment(String _key, Fragment _value) {
		if (_value != null && _key != null && !fragments.containsKey(_key)) {
			fragments.put(_key, _value);
			return true;
		}
		return false;
	}

	public boolean isAdded(String _key) {
		return fragments.containsKey(_key);
	}

	public void addAndShow(int ResId, String _key) {
		if(fgManager != null){
			hideOthersAndShow(_key);
			fgManager.beginTransaction()
					.add(ResId, fragments.get(_key)).commit();
		}
	}

	public void setArg(String _key, Bundle bundle) {
		fragments.get(_key).setArguments(bundle);
	}

	public void hideOthersAndShow(String _key) {
		if(fgManager != null){
			Iterator<Entry<String, Fragment>> it = fragments.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Fragment> entry = (Map.Entry<String, Fragment>) it
						.next();
				if (entry.getKey() != _key) {
					fgManager.beginTransaction()
							.hide(entry.getValue()).commit();
				} else {
					fgManager.beginTransaction()
							.show(entry.getValue()).commit();
				}
			}
		}
	}

	
	public Fragment getFragment(String _key){
		return fragments.get(_key);
	}
}
