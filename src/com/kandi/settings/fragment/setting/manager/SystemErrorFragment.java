package com.kandi.settings.fragment.setting.manager;

import com.kandi.settings.R;
import com.kandi.settings.driver.ConfigDriver3xMgnMiscSetup;
import com.kandi.settings.driver.DriverServiceManger;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 系统故障
 * @author illidan
 *
 */
public class SystemErrorFragment extends Fragment {

	View view;
	
	private LinearLayout errorlist;
	private Button guzhangclear;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fg_system_error, null);
		initViews();
		return view;
	}
	
	private void initViews() {
		guzhangclear = (Button) view.findViewById(R.id.guzhangclear);
		guzhangclear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ConfigDriver3xMgnMiscSetup model = DriverServiceManger.getInstance().getConfigDriver3xMgnMiscSetup();
				if(model != null) {
					try {
						model.cleanSystemError();
						refreshPannel();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					Toast.makeText(getActivity(), getResources().getString(R.string.warning_service), Toast.LENGTH_LONG).show();
					Log.e("KDSERVICE", "SystemSettingActivity3_1 service is null");
				}
			}
		});
		errorlist = (LinearLayout) view.findViewById(R.id.errorlist);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshPannel();
	}
	
	/**
	 * 将模型数据更新到屏幕
	 */
	public void refreshPannel() {
		ConfigDriver3xMgnMiscSetup model = DriverServiceManger.getInstance().getConfigDriver3xMgnMiscSetup();

		if(model != null) {
			try {
				String[] vals = model.getSystemError();
				//errortxt.setText(warningInfo);
				for(int i=0;i<vals.length;i++){
					if(TextUtils.isEmpty(vals[i])){
						continue;
					}
					TextView tw = new TextView(getActivity());
					tw.setText(vals[i]);
					tw.setTextColor(0xffa0a0a0);
					tw.setTextSize(18);
					tw.setPadding(0, 0, 0, 15);
					errorlist.addView(tw);
				}
			}catch (RemoteException e) {
				e.printStackTrace();
				return;
			}
			
			//TODO: ...将模型数据更新到屏幕...
	
		}
		else {
			Toast.makeText(getActivity(), getResources().getString(R.string.warning_service), Toast.LENGTH_LONG).show();
			Log.e("KDSERVICE", "SystemSettingActivity3_7.refreshPannel() service is null");
		}
	}
}
