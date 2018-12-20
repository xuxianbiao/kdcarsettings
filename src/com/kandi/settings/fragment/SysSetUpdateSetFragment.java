package com.kandi.settings.fragment;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kandi.settings.R;
import com.kandi.settings.driver.SystemSettingDriver2;

public class SysSetUpdateSetFragment extends Fragment {
	private SystemSettingDriver2 ssd2 = SystemSettingDriver2.getInstance();
	private Activity mactivity;
	
	private Button up_install_ui_btn;
	private Button up_install_ser_btn;
	
	private Button Online_checkFota;

	private View zaixian_include;
	private View lixian_include;
	// 下划线
	private ImageView undorline_zaixian;
	private ImageView undorline_lixian;
	// 在线升级控件
	private TextView m_upzaixian_btn;
	// 离线升级控件
	private TextView m_uplixian_btn;
	// 文字点击效果
	final int active = Color.rgb(0x1B, 0x71, 0x81); // 有效状态
	final int defualtcolor = Color.rgb(0xff, 0xff, 0xff);// 默认状态
	
	@Override
	public void onAttach(Activity activity) {
		mactivity = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.set_sysset_updateset, container,
				false);
		initview(view);
		return view;
	}

	private void initview(View view) {
		// 动态布局资源获取
		zaixian_include = view.findViewById(R.id.zaixian_layout);
		lixian_include = view.findViewById(R.id.lixian_layout);
		// 下划线资源获取
		undorline_zaixian = (ImageView) view
				.findViewById(R.id.undorline_zaixian);
		undorline_lixian = (ImageView) view.findViewById(R.id.undorline_lixian);
		// 在线升级监听
		m_upzaixian_btn = (TextView) view.findViewById(R.id.m_upzaixian_btn);
		m_upzaixian_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (zaixian_include.getVisibility() == View.GONE) {
					zaixian_include.setVisibility(View.VISIBLE);
					lixian_include.setVisibility(View.GONE);
					// 字体颜色和下划线设置
					m_upzaixian_btn.setTextColor(active);
					undorline_zaixian.setVisibility(View.VISIBLE);

					m_uplixian_btn.setTextColor(defualtcolor);
					undorline_lixian.setVisibility(View.INVISIBLE);
				}
			}

		});

		// 离线升级监听
		m_uplixian_btn = (TextView) view.findViewById(R.id.m_uplixian_btn);
		m_uplixian_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (lixian_include.getVisibility() == View.GONE) {
					lixian_include.setVisibility(View.VISIBLE);
					zaixian_include.setVisibility(View.GONE);

					// 字体颜色和下划线设置
					m_uplixian_btn.setTextColor(active);
					undorline_lixian.setVisibility(View.VISIBLE);

					m_upzaixian_btn.setTextColor(defualtcolor);
					undorline_zaixian.setVisibility(View.INVISIBLE);
				}
			}
		});
		// 安装UI软件
		up_install_ui_btn = (Button) view.findViewById(R.id.up_ui_btn);

		up_install_ui_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				File apkfile = new File(getResources().getString(
						R.string.Vwcs_LocalInstallUI_Path));
				if (!apkfile.exists()) {
					Toast.makeText(mactivity,
							"UI Apk Not Found!", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(
						Uri.fromFile(new File(apkfile.toString())),
						"application/vnd.android.package-archive");
				startActivity(intent);
			}
		});

		// 安装Ser软件
		up_install_ser_btn = (Button) view.findViewById(R.id.up_ser_btn);
		up_install_ser_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				File apkfile = new File(getResources().getString(
						R.string.Vwcs_LocalInstallSER_Path));
				if (!apkfile.exists()) {
					Toast.makeText(mactivity,
							"Ser Apk Not Found!", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
						"application/vnd.android.package-archive");
				startActivity(i);
			}
		});
		
		Online_checkFota = (Button) view.findViewById(R.id.check_up_btn);
		Online_checkFota.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("com.aliyun.fota","com.aliyun.fota.FotaUpdateInfo");
                try {
                    startActivity(intent);
                    Log.d("Fota UpCheck",intent.toString());
                } catch (Exception e) {
                    Log.e("Fota UpCheckt", "Unable to start activity " + intent.toString());
                }
			}
		});
	}

}
