package com.kandi.settings.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IKdBtService;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kandi.settings.Configs;
import com.kandi.settings.R;
import com.kandi.settings.dialog.BlueDeviceDialog;
import com.kandi.settings.dialog.BluePingDialog;
import com.kandi.settings.dialog.DialogDismissListener;
import com.kandi.settings.utils.SharedPreferencesUtils;

public class SysSetBuletoothSetFragment extends Fragment {

	private TextView txt_set_blue;
	private TextView txt_set_blue_ping;
	private ImageButton btn_set_blue;
	private ImageButton btn_set_blue_ping;
	private RelativeLayout set_blue_layout;
	private RelativeLayout set_blue_ping_layout;
	final int active=Color.rgb(26, 128, 172); //有效状态
	IKdBtService btservice;
	Context context;
	private BlueDeviceDialog bluedialog = null;
	private BluePingDialog bluepingdialog = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.set_sysset_bluetoothset,
				container, false);
		 btservice =
		 IKdBtService.Stub.asInterface(ServiceManager.getService("bt"));
		context = inflater.getContext();
		initview(view);
		return view;
	}

	private void initview(View view) {
		txt_set_blue = (TextView) view.findViewById(R.id.txt_set_blue);
		txt_set_blue_ping = (TextView) view
				.findViewById(R.id.txt_set_blue_ping);
		
		set_blue_layout = (RelativeLayout) view.findViewById(R.id.set_blue_layout);
		set_blue_layout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				RelativeLayout layout = (RelativeLayout)arg0;
				if(arg1.getAction()==MotionEvent.ACTION_DOWN){
					layout.setBackgroundColor(active);
				}else if(arg1.getAction()==MotionEvent.ACTION_UP){
					btn_set_blue.performClick();
					layout.setBackground(null);
				}
				return true;
			}
		});
		
		set_blue_ping_layout = (RelativeLayout) view.findViewById(R.id.set_blue_ping_layout);
		set_blue_ping_layout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				RelativeLayout layout = (RelativeLayout)arg0;
				if(arg1.getAction()==MotionEvent.ACTION_DOWN){
					layout.setBackgroundColor(active);
				}else if(arg1.getAction()==MotionEvent.ACTION_UP){
					btn_set_blue_ping.performClick();
					layout.setBackground(null);
				}
				return true;
			}
		});
//		new Thread(new Runnable() {
//			public void run() {
//				if (btservice != null) {
//					try {
//						String bluetoothname = SharedPreferencesUtils.getSharedPreferences(
//								getActivity()).getString(Configs.SHARED_BLUE_STATE,
//										Configs.DEFUALT_BLUE_STATE);
//						btservice.btSetLocalName(bluetoothname);
//					} catch (RemoteException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}).start();
		btn_set_blue = (ImageButton) view.findViewById(R.id.btn_set_blue);
		btn_set_blue.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (bluedialog == null) {
					bluedialog = new BlueDeviceDialog(getActivity(),new DialogDismissListener() {
						
						@Override
						public void onDismiss(String info, int what, Object obj) {
		                    handler.sendEmptyMessage(101);//发送message信息  
						}
					});
				}
				bluedialog.show();
			}
		});

		btn_set_blue_ping = (ImageButton) view
				.findViewById(R.id.btn_set_blue_ping);
		btn_set_blue_ping.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (bluepingdialog == null) {
					bluepingdialog = new BluePingDialog(getActivity(),new DialogDismissListener() {
						
						@Override
						public void onDismiss(String info, int what, Object obj) {
							txt_set_blue_ping.setText(info);
						}
					});
				}
				bluepingdialog.show();
			}
		});
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.refreshView();
	}

	private void refreshView() {
		handler.sendEmptyMessage(101);
		String blueping = SharedPreferencesUtils.getSharedPreferences(
				getActivity()).getString(Configs.SHARED_BLUE_PING,
				Configs.DEFUALT_BLUE_PING);
		if (blueping != null) {
			txt_set_blue_ping.setText(blueping);
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				Toast.makeText(getActivity(), getResources().getString(R.string.warning_blue_error), Toast.LENGTH_LONG).show();
				break;
			case 101:
				if (btservice != null) {
					String[] name = new String[6];
					try {
						if (btservice.btGetLocalName(name) == 0) {
							if (!"".equals(name[0])) {
			                    txt_set_blue.setText(name[0]);
							}
						}
						name = null;
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}

	};

	@Override
	public void onDestroy() {
		if(bluedialog != null){
			bluedialog.dismiss();
			bluedialog = null;
		}
		if(bluepingdialog != null){
			bluepingdialog.dismiss();
			bluepingdialog = null;
		}
		super.onDestroy();
	}
	
	@Override
	public void onPause() {
		if(bluedialog != null){
			bluedialog.dismiss();
			bluedialog = null;
		}
		if(bluepingdialog != null){
			bluepingdialog.dismiss();
			bluepingdialog = null;
		}
		super.onPause();
	}

}
