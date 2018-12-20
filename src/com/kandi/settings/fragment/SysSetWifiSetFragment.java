package com.kandi.settings.fragment;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.kandi.settings.Configs;
import com.kandi.settings.R;
import com.kandi.settings.utils.SharedPreferencesUtils;
import com.kandi.settings.utils.WifiAdmin;

public class SysSetWifiSetFragment extends Fragment {
	private Activity mactivity;

	private WifiAdmin wifiAdmin;
	WifiConfiguration cur_config;
	// 事件处理线程
	WifiThread m_wifithread;

	private TextView connect_notice_txt;
	private TextView set_nearwlan_txt;

	private TableLayout Apwifilistview;
	private TableLayout Freewifilistview;
	private TableLayout already_connect;

	private String connect_wifi_SSID; // 已连接wifi名称
	private int connect_wifi_ID; // 已连接的wifi的网络ID

	private List<ScanResult> list;
	private List<WifiConfiguration> mWifiConfigurations;

	// 文字点击效果
	final int active = Color.rgb(0x1B, 0x71, 0x81); // 有效状态
	final int defualtcolor = Color.rgb(0xff, 0xff, 0xff);// 默认状态
	Drawable sys_defualtcolor; // 系统默认颜色

	private TextView m_setap_btn;
	private TextView m_setwifi_btn;

	// AP和WIFI模式下的布局文件
	private View ApLayout_include;
	private View WifiLayout_include;

	// 下划线
	private ImageView undorline_wifiap;
	private ImageView undorline_wifiwlan;
	private ImageView wifi_info_bt; // 连接状态的wifi信息按钮

	private ToggleButton teamPersonApTog; // 热点开启
	private ToggleButton teamFreeWlanTog; // 无线开启

	private boolean frist_init = false;

	private Dialog wifiPassDialog;
	private Dialog wifiInfoDialog;
	private Dialog wifiInfoDelDialog;

	private Dialog wifiApDialog;

	private TextView txt_app;
	private TextView txt_wifiap;
	private EditText pswEdit;
	private TextView cancelButton;
	private TextView okButton;

	private TextView scan_ip, scan_netmask, scan_gateway;
	private Button scan_back, delete_cur_connect;
	private Button delscan_back, deldelete_cur_connect;

	private TextView controlpowertxt, hotspotdoc;
	private TableLayout dev_connected;

	private TextView set_wifi_name_txt, set_wifi_password_txt;

	private SharedPreferences sh;

	private RelativeLayout wifiap_name_layout, wifiap_pass_layout;

	private String wifiapname, wifiappass;

	@Override
	public void onAttach(Activity activity) {
		mactivity = activity;
		sh = SharedPreferencesUtils.getSharedPreferences(activity);
		wifiapname = sh.getString(Configs.WIFI_AP_NAME, "EV_Ap");
		wifiappass = sh.getString(Configs.WIFI_AP_PASS, "0123456789");
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.set_sysset_wifiset, container,
				false);

		wifiAdmin = new WifiAdmin(mactivity);
		m_wifithread = new WifiThread();
		m_wifithread.start();

		initview(view);

		return view;
	}

	private void initview(final View view) {
		// 获取布局文件资源
		ApLayout_include = view.findViewById(R.id.ap_layout);
		WifiLayout_include = view.findViewById(R.id.free_layout);

		// 下划线资源获取
		undorline_wifiap = (ImageView) view.findViewById(R.id.undorline_wifiap);
		undorline_wifiwlan = (ImageView) view
				.findViewById(R.id.undorline_wifiwlan);
		// 个人热点开关
		// 初始化wifi ap热点界面相关
		initWifiAp(view);
		InitWifi(view);

		teamPersonApTog
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						if (arg1) {
							// 开启个人共享热点
							wifiAdmin.openWifi();
							wifiAdmin.mWifiManager.setWifiEnabled(false);
							controlpowertxt.setVisibility(View.GONE);
							hotspotdoc.setVisibility(View.VISIBLE);
							//dev_connected.setVisibility(View.VISIBLE);
							wifiAdmin.startWifiAp(wifiapname, wifiappass);
							wifiap_name_layout.setVisibility(View.VISIBLE);
							wifiap_pass_layout.setVisibility(View.VISIBLE);
							closeWlanConnet();
							teamPersonApTog.setEnabled(false);
							teamFreeWlanTog.setEnabled(false);
							m_handler.postDelayed(new Runnable() {
								
								@Override
								public void run() {
									teamPersonApTog.setEnabled(true);
									teamFreeWlanTog.setEnabled(true);
								}
							}, 2000);
						} else {
							// 个人热点未开启
							controlpowertxt.setVisibility(View.VISIBLE);
							hotspotdoc.setVisibility(View.GONE);
							//dev_connected.setVisibility(View.GONE);
							wifiap_name_layout.setVisibility(View.INVISIBLE);
							wifiap_pass_layout.setVisibility(View.INVISIBLE);
							wifiAdmin.closeWifiAp();
							teamPersonApTog.setEnabled(false);
							teamFreeWlanTog.setEnabled(false);
							m_handler.postDelayed(new Runnable() {
								
								@Override
								public void run() {
									teamPersonApTog.setEnabled(true);
									teamFreeWlanTog.setEnabled(true);
								}
							}, 2000);
						}
					}
				});
		// 上面的部分是个人热点设置，下面的部分是wifi连接设置

		m_setap_btn = (TextView) view.findViewById(R.id.m_setap_btn);
		m_setap_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (ApLayout_include.getVisibility() == View.GONE) {
					ApLayout_include.setVisibility(View.VISIBLE);
					WifiLayout_include.setVisibility(View.GONE);
					// 字体颜色和下划线设置
					m_setap_btn.setTextColor(active);
					undorline_wifiap.setVisibility(View.VISIBLE);

					m_setwifi_btn.setTextColor(defualtcolor);
					undorline_wifiwlan.setVisibility(View.INVISIBLE);
				}
			}
		});

		m_setwifi_btn = (TextView) view.findViewById(R.id.m_setwifi_btn);
		m_setwifi_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (WifiLayout_include.getVisibility() == View.GONE) {
					WifiLayout_include.setVisibility(View.VISIBLE);
					ApLayout_include.setVisibility(View.GONE);
					// 字体颜色和下划线设置
					m_setwifi_btn.setTextColor(active);
					undorline_wifiwlan.setVisibility(View.VISIBLE);

					m_setap_btn.setTextColor(defualtcolor);
					undorline_wifiap.setVisibility(View.INVISIBLE);
					InitWifi(view);
				}
			}
		});

	}
	
	Handler m_handler = new Handler();

	private void initWifiAp(final View view) {
		set_wifi_name_txt = (TextView) view
				.findViewById(R.id.set_wifi_name_txt);
		set_wifi_password_txt = (TextView) view
				.findViewById(R.id.set_wifi_password_txt);
		set_wifi_name_txt.setText(getResources().getString(
				R.string.wifi_ap_name)
				+ wifiapname);
		set_wifi_password_txt.setText(getResources().getString(
				R.string.wifi_ap_pass)
				+ wifiappass);

		wifiap_name_layout = (RelativeLayout) view
				.findViewById(R.id.wifiap_name_layout);
		wifiap_pass_layout = (RelativeLayout) view
				.findViewById(R.id.wifiap_pass_layout);
		wifiap_name_layout.setBackgroundResource(R.drawable.wifiinfo_bg_selector);
		wifiap_name_layout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showWifiApDialog(0);
			}
		});
		wifiap_pass_layout.setBackgroundResource(R.drawable.wifiinfo_bg_selector);
		wifiap_pass_layout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showWifiApDialog(1);
			}
		});
		
		teamPersonApTog = (ToggleButton) view.findViewById(R.id.teamAPOpenTog);
		controlpowertxt = (TextView) view.findViewById(R.id.controlpowertxt);
		hotspotdoc = (TextView) view.findViewById(R.id.hotspotdoc);
		dev_connected = (TableLayout) view.findViewById(R.id.dev_connected);
		if(wifiAdmin.isWifiApEnabled(wifiAdmin.mWifiManager)){
			controlpowertxt.setVisibility(View.GONE);
			hotspotdoc.setVisibility(View.VISIBLE);
			//dev_connected.setVisibility(View.VISIBLE);
			wifiAdmin.startWifiAp(wifiapname, wifiappass);
			wifiap_name_layout.setVisibility(View.VISIBLE);
			wifiap_pass_layout.setVisibility(View.VISIBLE);
			teamPersonApTog.performClick();
		}
	}

	/*
	 * 无线WIFI事件处理线程
	 */
	class WifiThread extends Thread {
		private final int MSG_WIFI_OPEN = 200;
		private final int MSG_WLAN_SCAN = 201;
		private boolean m_WifiWlan_Open;
		private int scan_time;
		final int Time_Cycle = 3;

		public WifiThread() {
			m_WifiWlan_Open = false;
			scan_time = Time_Cycle;
		}

		public void run() {
			while (true) {
				if (mactivity.isFinishing()) {
					break;
				}
				if (m_WifiWlan_Open) {
					if (!wifiAdmin.mWifiManager.isWifiEnabled()) {
					} else {
						if(scanstatus){
							m_hadler.sendEmptyMessage(MSG_WLAN_SCAN);
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		Handler m_hadler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_WIFI_OPEN:
					if (m_WifiWlan_Open) {
						wifiAdmin.closeWifiAp();
						closeWifiAP();
						wifiAdmin.openWifi();
						connect_notice_txt.setVisibility(View.GONE);
						teamFreeWlanTog.setEnabled(false);
						teamPersonApTog.setEnabled(false);
						m_handler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								teamFreeWlanTog.setEnabled(true);
								teamPersonApTog.setEnabled(true);
							}
						}, 2000);
					} else {
						wifiAdmin.closeWifi();
						set_nearwlan_txt.setVisibility(View.GONE);
						Freewifilistview.setVisibility(View.GONE);
						connect_notice_txt.setText(getResources().getString(
								R.string.wifi_wifi_notice_close));
						connect_notice_txt.setVisibility(View.VISIBLE);
						already_connect.setVisibility(View.GONE);
						teamFreeWlanTog.setEnabled(false);
						teamPersonApTog.setEnabled(false);
						m_handler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								teamFreeWlanTog.setEnabled(true);
								teamPersonApTog.setEnabled(true);
							}
						}, 2000);
					}
					break;
				case MSG_WLAN_SCAN:
					if (already_connect.getVisibility() == View.GONE) {
						if (wifiAdmin.wifiIsConnect(mactivity)) {
							already_connect.setVisibility(View.VISIBLE);
							TextView tv;
							TableRow tablerow = (TableRow) already_connect
									.getChildAt(0);
							tv = (TextView) tablerow.getChildAt(0);
							String str = wifiAdmin.mWifiManager
									.getConnectionInfo().getSSID();
							if (str.indexOf("\"") == 0)
								str = str.substring(1, str.length());
							if (str.lastIndexOf("\"") == (str.length() - 1))
								str = str.substring(0, str.length() - 1);
							tv.setText(str);
							connect_wifi_SSID = str;
							connect_wifi_ID = wifiAdmin.mWifiManager
									.getConnectionInfo().getNetworkId();
							tv = (TextView) tablerow.getChildAt(1);
							tv.setText(getResources().getString(
									R.string.wifi_wifi_notice_connected));
							connect_notice_txt.setVisibility(View.VISIBLE);
							connect_notice_txt.setText(getResources()
									.getString(R.string.wifi_wifi_notice_open));
						} else {
							connect_notice_txt.setVisibility(View.GONE);
						}
					} else {
						if (!wifiAdmin.wifiIsConnect(mactivity)) {
							already_connect.setVisibility(View.GONE);
							connect_wifi_SSID = "";
						}
					}
					scan_time++;
					if (scan_time >= Time_Cycle) {
						scan_time = 0;
						getWifiList();
					} else {
						list.clear();
						break;
					}
					if (list == null) {
						break;
					}
					Init_WifiList();
					break;
				default:
					break;
				}
			}
		};

	}

	private void getWifiList() {
		wifiAdmin.startScan();
		list = wifiAdmin.getWifiList();
		mWifiConfigurations = wifiAdmin.getConfiguration();
		if (list != null) {
			for (ScanResult mScanResult : list) {
				System.out.println(mScanResult.BSSID + " " + mScanResult.SSID
						+ " " + mScanResult.capabilities + " "
						+ mScanResult.frequency + " " + mScanResult.level);
			}
		}
	}

	/*
	 * 列表附近可以连接的WIFI网络 作者：漂流中
	 */
	private void Init_WifiList() {
		if (set_nearwlan_txt.getVisibility() == View.GONE) {
			set_nearwlan_txt.setVisibility(View.VISIBLE);
		}
		if (Freewifilistview.getVisibility() == View.GONE) {
			Freewifilistview.setVisibility(View.VISIBLE);
		}
		if (Freewifilistview != null && Freewifilistview.getChildCount() > 0) {
			Freewifilistview.removeAllViews();
		}
		for (int row = 0; row < list.size(); row++) {
			final TableRow m_sert = new TableRow(mactivity);
			if (list.get(row).SSID.equals(connect_wifi_SSID)) {
				continue;
			}
			for (int col = 0; col < 3; col++) { // 名称，连接状态，信息三列
				if (col == 0) {
					TextView tvName = new TextView(mactivity);
					if(list.get(row).SSID.length()>20){
						tvName.setText(list.get(row).SSID.substring(0, 15)+"...");
					}else{
						tvName.setText(list.get(row).SSID);
					}
					tvName.setTextColor(Color.rgb(0xff, 0xff, 0xff));
					tvName.setGravity(Gravity.CENTER);
					tvName.setTextSize(14);
					m_sert.addView(tvName);
				} else if (col == 1) {
					TextView tvState = new TextView(mactivity);
					if (list.get(row).SSID.equals(connect_wifi_SSID)) {
						tvState.setText(getResources().getString(
								R.string.wifi_wifi_notice_connected));
					} else {
						tvState.setText(getResources().getString(
								R.string.wifi_wifi_notice_notconnect));
					}
					tvState.setTextColor(Color.rgb(0xff, 0xff, 0xff));
					tvState.setGravity(Gravity.CENTER);
					tvState.setTextSize(14);
					tvState.setPadding(20, 0, 0, 0);
					m_sert.addView(tvState);

				} else if (col == 2) {
					ImageView wifi_level = new ImageView(mactivity);
					if (list.get(row).level < 0 && list.get(row).level > -50) {
						wifi_level
								.setImageResource(R.drawable.home_top_btn5_05);
					} else if (list.get(row).level < -50
							&& list.get(row).level > -70) {
						wifi_level
								.setImageResource(R.drawable.home_top_btn5_04);
					} else if (list.get(row).level < -70
							&& list.get(row).level > -80) {
						wifi_level
								.setImageResource(R.drawable.home_top_btn5_03);
					} else if (list.get(row).level < -80
							&& list.get(row).level > -90) {
						wifi_level
								.setImageResource(R.drawable.home_top_btn5_02);
					} else {
						wifi_level
								.setImageResource(R.drawable.home_top_btn5_01);
					}
					m_sert.addView(wifi_level);
					continue;
				}
			}
			// m_sert为其中一行
			m_sert.setBackgroundResource(R.drawable.wifiinfo_bg_selector);
			Freewifilistview.addView(m_sert);
			final int count = row;
			m_sert.setOnClickListener(new TableRowClick(list.get(row).SSID,
					list.get(row).capabilities) {
				@Override
				public void onClick(View arg0) {
					// TODO 此处需要增加对于wifi加密类型的判断，如果没有加密直接连 如果加密了 弹出密码框
					// TODO 此处需要在开始连接wifi后将文字“断开”调整为“连接中”
					if (GetSecurity(Capabilities) == 0) {
						ConnectedWIfi(SSID, Capabilities, "");
						Log.e("justin GetSecurity 0", SSID + ":" + Capabilities);
						TextView stateTxt = (TextView) m_sert.getChildAt(1);
						stateTxt.setText(getResources().getString(R.string.connecting));
					} else {
						if(wifiAdmin.connetionConfiguration(SSID)){
							TextView stateTxt = (TextView) m_sert.getChildAt(1);
							stateTxt.setText(getResources().getString(R.string.connecting));
						}else{
							showPassDialog(SSID, Capabilities, count);
						}
					}
				}
			});
			m_sert.setOnLongClickListener(new TableRowLongClick(list.get(row).SSID,
					list.get(row).capabilities) {
				
				@Override
				public boolean onLongClick(View v) {
					int index = wifiAdmin.connetionConfigurationIndex(SSID);
					if(index >= 0){
						showWifiDelDialog(SSID,index);
					}
					return false;
				}
			});
		}
	}

	private void InitWifi(final View view) {

		if (frist_init) {
			return;
		} else {
			frist_init = true;
		}

		// 列表资源
		Freewifilistview = (TableLayout) view.findViewById(R.id.list_wlan);
		connect_notice_txt = (TextView) view
				.findViewById(R.id.connect_notice_txt);
		set_nearwlan_txt = (TextView) view.findViewById(R.id.set_nearwlan_txt);

		already_connect = (TableLayout) view.findViewById(R.id.already_connect);
		sys_defualtcolor = already_connect.getBackground();
		already_connect.setBackgroundResource(R.drawable.wifiinfo_bg_selector);
		already_connect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				wifi_info_bt.performClick();
			}
		});

		wifi_info_bt = (ImageView) view.findViewById(R.id.wifi_info_bt);
		wifi_info_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String connect_wifi_Ip;
				String connect_wifi_NetMask;
				String connect_wifi_GateWay;
				connect_wifi_Ip = Formatter
						.formatIpAddress(wifiAdmin.mWifiManager
								.getConnectionInfo().getIpAddress());
				connect_wifi_NetMask = Formatter
						.formatIpAddress(wifiAdmin.mWifiManager.getDhcpInfo().netmask);
				connect_wifi_GateWay = Formatter
						.formatIpAddress(wifiAdmin.mWifiManager.getDhcpInfo().gateway);
				// 显示wifi网络信息
				showWifiInfoDialog(connect_wifi_Ip, connect_wifi_NetMask,
						connect_wifi_GateWay);
			}
		});

		teamFreeWlanTog = (ToggleButton) view
				.findViewById(R.id.teamWifiOpenDiTog);
		if (wifiAdmin.mWifiManager.isWifiEnabled()) {
			teamFreeWlanTog.performClick();
			m_wifithread.m_WifiWlan_Open = true;
			connect_notice_txt.setText(getResources().getString(
					R.string.wifi_wifi_notice_open));
			connect_notice_txt.setVisibility(View.GONE);
		}
		teamFreeWlanTog
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						if (arg1) {
							m_wifithread.m_WifiWlan_Open = true;
						} else {
							m_wifithread.m_WifiWlan_Open = false;
						}
						m_wifithread.m_hadler
								.sendEmptyMessage(m_wifithread.MSG_WIFI_OPEN);
					}
				});
	}

	/**
	 * 弹出wifi密码确认dialog
	 */
	private void showPassDialog(final String SSID, final String type,
			final int row) {
		if (wifiPassDialog == null) {
			wifiPassDialog = new Dialog(mactivity, R.style.my_dialog);
			wifiPassDialog.setContentView(R.layout.wifipass_dialog);

			pswEdit = (EditText) wifiPassDialog.findViewById(R.id.pswEdit);
			cancelButton = (TextView) wifiPassDialog
					.findViewById(R.id.cancelButton);
			okButton = (TextView) wifiPassDialog.findViewById(R.id.okButton);

			cancelButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View source) {
					wifiPassDialog.dismiss();
					wifiPassDialog = null;
				}
			});

			okButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View source) {
					ConnectedWIfi(SSID, type, pswEdit.getText().toString());
					pswEdit.setText(null);
					wifiPassDialog.dismiss();
					wifiPassDialog = null;
					// 将断开调整为正在连接中
					TableRow clickRow = (TableRow) Freewifilistview
							.getChildAt(row);
					if(clickRow != null){
						TextView stateTxt = (TextView) clickRow.getChildAt(1);
						stateTxt.setText(getResources().getString(R.string.connecting));
					}
				}
			});

		}
		wifiPassDialog.show();
	}

	/**
	 * 弹出wifi信息dialog
	 */
	private void showWifiInfoDialog(String ip, String netmask, String gateway) {
		wifiInfoDialog = new Dialog(mactivity, R.style.my_dialog);
		wifiInfoDialog.setContentView(R.layout.wifiinfo_dialog);
		
		TextView scan_wifi_name = (TextView) wifiInfoDialog.findViewById(R.id.scan_wifi_name);
		String str = wifiAdmin.mWifiManager
				.getConnectionInfo().getSSID();
		if (str.indexOf("\"") == 0)
			str = str.substring(1, str.length());
		if (str.lastIndexOf("\"") == (str.length() - 1))
			str = str.substring(0, str.length() - 1);
		scan_wifi_name.setText(str.length()>20?str.substring(0, 15)+"...":str);
		
		scan_ip = (TextView) wifiInfoDialog.findViewById(R.id.scan_ip);
		scan_netmask = (TextView) wifiInfoDialog
				.findViewById(R.id.scan_netmask);
		scan_gateway = (TextView) wifiInfoDialog
				.findViewById(R.id.scan_gateway);
		
		scan_ip.setText(ip);
		scan_netmask.setText(netmask);
		scan_gateway.setText(gateway);
		
		delete_cur_connect = (Button) wifiInfoDialog
				.findViewById(R.id.delete_cur_connect);
		scan_back = (Button) wifiInfoDialog.findViewById(R.id.scan_back);
		
		scan_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View source) {
				wifiInfoDialog.dismiss();
				wifiInfoDialog = null;
			}
		});
		
		delete_cur_connect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View source) {
				wifiInfoDialog.dismiss();
				wifiInfoDialog = null;
				wifiAdmin.disConnectionWifi(connect_wifi_ID);
				wifiAdmin.removeConfig(connect_wifi_ID);
			}
		});
		wifiInfoDialog.show();
	}
	
	/**
	 * 弹出wifi信息dialog
	 */
	private void showWifiDelDialog(final String SSID,final int index) {
		wifiInfoDelDialog = new Dialog(mactivity, R.style.my_dialog);
		wifiInfoDelDialog.setContentView(R.layout.wifiinfo_deldialog);
		
		TextView scan_wifi_name = (TextView) wifiInfoDelDialog.findViewById(R.id.scan_wifi_name);
		String str = SSID;
		if (str.indexOf("\"") == 0)
			str = str.substring(1, str.length());
		if (str.lastIndexOf("\"") == (str.length() - 1))
			str = str.substring(0, str.length() - 1);
		scan_wifi_name.setText(str.length()>20?str.substring(0, 15)+"...":str);
		
		deldelete_cur_connect = (Button) wifiInfoDelDialog
				.findViewById(R.id.delete_cur_connect);
		delscan_back = (Button) wifiInfoDelDialog.findViewById(R.id.scan_back);
		
		delscan_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View source) {
				wifiInfoDelDialog.dismiss();
				wifiInfoDelDialog = null;
			}
		});
		
		deldelete_cur_connect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View source) {
				wifiInfoDelDialog.dismiss();
				wifiInfoDelDialog = null;
				if(index >= 0){
					wifiAdmin.mWifiManager.disableNetwork(index);
					wifiAdmin.removeConfig(index);
				}
			}
		});
		wifiInfoDelDialog.show();
	}

	// wifi连接
	private boolean ConnectedWIfi(String SSID, String type, String password) {
		if (!wifiAdmin.mWifiManager.isWifiEnabled()) {
			return false;
		}
		// 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
		// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
		while (wifiAdmin.mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
			try {
				// 为了避免程序一直while循环，让它睡个100毫秒在检测……
				Thread.currentThread();
				Thread.sleep(100);
			} catch (InterruptedException ie) {
			}
		}
		// 查看该网络是否已经配置过
		for (WifiConfiguration existingConfig : mWifiConfigurations) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				wifiAdmin.removeConfig(connect_wifi_ID);
				// return true;
			}
		}
		// WIFI根据加密方式不同进行连接
		cur_config = wifiAdmin.CreateWifiInfo(SSID, password, type);
		if (cur_config != null) {
			return true;
		} else {
			return false;
		}
	}

	class TableRowClick implements OnClickListener {
		String SSID;
		String Capabilities;

		TableRowClick(String bssid, String capabilities) {
			SSID = bssid;
			Capabilities = capabilities;
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

		}

	}
	
	class TableRowLongClick implements OnLongClickListener {
		String SSID;
		String Capabilities;

		TableRowLongClick(String bssid, String capabilities) {
			SSID = bssid;
			Capabilities = capabilities;
		}

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	// 判断wifi加密方式
	private int GetSecurity(String capabilities) {
		if (capabilities.contains("WEP")) {
			return 1;
		} else if (capabilities.contains("PSK")) {
			return 2;
		} else if (capabilities.contains("EAP")) {
			return 3;
		}
		return 0;
	}

	/**
	 * 弹出wifi ap用户名 密码设置dialog type 0 apname设置 1 appass设置
	 */
	private void showWifiApDialog(final int type) {
		if (wifiApDialog == null) {
			wifiApDialog = new Dialog(mactivity, R.style.my_dialog);
			wifiApDialog.setContentView(R.layout.wifiap_dialog);
		}
		txt_app = (TextView) wifiApDialog.findViewById(R.id.txt_app);
		txt_wifiap = (TextView) wifiApDialog.findViewById(R.id.txt_wifiap);
		pswEdit = (EditText) wifiApDialog.findViewById(R.id.pswEdit);
//		pswEdit.addTextChangedListener(textWatcher);
		if (type == 0) {
			pswEdit.setText(wifiapname);
			txt_wifiap.setText(getString(R.string.wifi_ap_name_change));
			txt_app.setText(R.string.wifi_ap_app);
			pswEdit.setHint(getString(R.string.input_name));
		} else {
			pswEdit.setText(wifiappass);
			txt_wifiap.setText(getString(R.string.wifipass_dialog_inputpass));
			txt_app.setText(R.string.wifipass_dialog_pass);
			pswEdit.setHint(getString(R.string.input_pwd));
		}
		pswEdit.setSelection(pswEdit.getText().length());

		cancelButton = (TextView) wifiApDialog.findViewById(R.id.cancelButton);
		okButton = (TextView) wifiApDialog.findViewById(R.id.okButton);

		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View source) {
				wifiApDialog.dismiss();
			}
		});

		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View source) {
				String inputStr = pswEdit.getText().toString();
				if(!"".equals(inputStr)){
					if (type == 0) {
						wifiapname = inputStr;
						int length = calculateLength(inputStr);
						if(length>32){
							Toast.makeText(getActivity(), String.format(getString(R.string.error_toolong),length), Toast.LENGTH_SHORT).show();
							return;
						}
						set_wifi_name_txt.setText(getResources().getString(
								R.string.wifi_ap_name)
								+ wifiapname);
						Editor editer = sh.edit();
						editer.putString(Configs.WIFI_AP_NAME, wifiapname);
						editer.commit();
						teamPersonApTog.performClick();
						teamPersonApTog.setEnabled(false);
						m_wifithread.m_hadler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								teamPersonApTog.setEnabled(true);
								teamPersonApTog.performClick();
							}
						}, 1000);
					} else {
						if(inputStr.length()>=8){
							wifiappass = inputStr;
							if(contain(inputStr)){
								Toast.makeText(getActivity(), getString(R.string.error_type), Toast.LENGTH_SHORT).show();
								return;
							}
							set_wifi_password_txt.setText(getResources().getString(
									R.string.wifi_ap_pass)
									+ wifiappass);
							sh.edit().putString(Configs.WIFI_AP_PASS, wifiappass).commit();
							teamPersonApTog.performClick();
							teamPersonApTog.setEnabled(false);
							m_wifithread.m_hadler.postDelayed(new Runnable() {
								
								@Override
								public void run() {
									teamPersonApTog.setEnabled(true);
									teamPersonApTog.performClick();
								}
							}, 1000);
						}else{
							return;
						}
					}
					wifiApDialog.dismiss();
				}
			}
		});
		wifiApDialog.show();
	}
	
	public void restartWifiAp(){
		if(wifiAdmin.isWifiApEnabled(wifiAdmin.mWifiManager)){
			wifiAdmin.startWifiAp(wifiapname, wifiappass);
		}
	}
	
	public void closeWlanConnet(){
		
//		if(m_wifithread.m_WifiWlan_Open)
		if(teamFreeWlanTog == null ||!teamFreeWlanTog.isChecked()){
			return;
		}
		{
			teamFreeWlanTog.setChecked(false);

			m_wifithread.m_WifiWlan_Open = false;
			m_wifithread.m_hadler
				.sendEmptyMessage(m_wifithread.MSG_WIFI_OPEN);
		}
	}
	
	public void closeWifiAP(){
		if(teamPersonApTog == null ||!teamPersonApTog.isChecked()){
			return;
		}
		
		teamPersonApTog.setChecked(false);

			controlpowertxt.setVisibility(View.VISIBLE);
			hotspotdoc.setVisibility(View.GONE);
			dev_connected.setVisibility(View.GONE);
//			wifiAdmin.closeWifiAp();
	}

	@Override
	public void onDestroy() {
		if(wifiPassDialog != null){
			wifiPassDialog.dismiss();
			wifiPassDialog = null;
		}
		if(wifiInfoDialog != null){
			wifiInfoDialog.dismiss();
			wifiInfoDialog = null;
		}
		if(wifiInfoDelDialog != null){
			wifiInfoDelDialog.dismiss();
			wifiInfoDelDialog = null;
		}
		if(wifiApDialog != null){
			wifiApDialog.dismiss();
			wifiApDialog = null;
		}
		super.onDestroy();
	}
	
	boolean scanstatus = false;
	
	@Override
	public void onPause() {
		scanstatus = false;
		if(wifiPassDialog != null){
			wifiPassDialog.dismiss();
			wifiPassDialog = null;
		}
		if(wifiInfoDialog != null){
			wifiInfoDialog.dismiss();
			wifiInfoDialog = null;
		}
		if(wifiApDialog != null){
			wifiApDialog.dismiss();
			wifiApDialog = null;
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		scanstatus = true;
	}
	
	private TextWatcher textWatcher = new TextWatcher() {
		private int editStart;
		private int editEnd;
		private int maxLen = 20; // the max byte
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			editStart = pswEdit.getSelectionStart();
			editEnd = pswEdit.getSelectionEnd();
			// 先去掉监听器，否则会出现栈溢出
			pswEdit.removeTextChangedListener(textWatcher);
			if (!TextUtils.isEmpty(pswEdit.getText())) {
				String etstring = pswEdit.getText().toString().trim();
				while (calculateLength(s.toString()) > maxLen) {
					s.delete(editStart - 1, editEnd);
					editStart--;
					editEnd--;
				}
			}

			pswEdit.setText(s);
			pswEdit.setSelection(editStart);

			// 恢复监听器
			pswEdit.addTextChangedListener(textWatcher);
		}
	};
	
	private int calculateLength(String etstring) {
		char[] ch = etstring.toCharArray();

		int varlength = 0;
		for (int i = 0; i < ch.length; i++) {
			// changed by zyf 0825 , bug 6918，加入中文标点范围 ， TODO 标点范围有待具体化
			if ((ch[i] >= 0x2E80 && ch[i] <= 0xFE4F) || (ch[i] >= 0xA13F && ch[i] <= 0xAA40) || ch[i] >= 0x80) { // 中文字符范围0x4e00 0x9fbb
				varlength = varlength + 2;
			} else {
				varlength++;
			}
		}
		return varlength;
	}
	
	private boolean contain(String etstring) {
		char[] ch = etstring.toCharArray();

		int varlength = 0;
		for (int i = 0; i < ch.length; i++) {
			// changed by zyf 0825 , bug 6918，加入中文标点范围 ， TODO 标点范围有待具体化
			if ((ch[i] >= 0x2E80 && ch[i] <= 0xFE4F) || (ch[i] >= 0xA13F && ch[i] <= 0xAA40) || ch[i] >= 0x80) { // 中文字符范围0x4e00 0x9fbb
				return true;
			}
		}
		return false;
	}
	
}
