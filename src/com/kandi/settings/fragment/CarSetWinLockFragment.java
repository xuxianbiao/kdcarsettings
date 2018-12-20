package com.kandi.settings.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.kandi.settings.R;
import com.kandi.settings.driver.ConfigDriver1xCarCtrlSetup;
import com.kandi.settings.driver.DriverServiceManger;

public class CarSetWinLockFragment extends Fragment {
	private Activity mactivity;
	private ToggleButton teamBuyBaoDiTog;
	ConfigDriver1xCarCtrlSetup model;
	
	@Override
	public void onAttach(Activity activity) {
		mactivity = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.set_carset_winlock, container,
				false);
		initview(view);
		return view;
	}

	private void initview(View view) {
		teamBuyBaoDiTog = (ToggleButton) view
				.findViewById(R.id.teamBuyBaoDiTog);
		refreshPannel();
		teamBuyBaoDiTog
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						if(model!=null){
							model.setTouchCtrlOn(arg1);
						}else{
							Toast.makeText(mactivity, getResources().getString(R.string.warning_service), Toast.LENGTH_SHORT).show();
						}
						
					}
				});
	}

	/**
	 * 将模型数据更新到屏幕
	 */
	private void refreshPannel() {
		model = DriverServiceManger.getInstance()
				.getConfigDriver1xCarCtrlSetup();

		if (model != null) {
			teamBuyBaoDiTog.setChecked(model.isTouchCtrlOn());

			// TODO: ...将模型数据更新到屏幕...

		} else {
			Toast.makeText(mactivity, getResources().getString(R.string.warning_service), Toast.LENGTH_SHORT).show();
		}
	}

}
