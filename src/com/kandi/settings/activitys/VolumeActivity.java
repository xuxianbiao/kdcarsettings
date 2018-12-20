package com.kandi.settings.activitys;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kandi.settings.KdsApplication;
import com.kandi.settings.R;
import com.kandi.settings.SetbtnListener;
import com.kandi.settings.fragment.VolumeSetOffFragment;
import com.kandi.settings.fragment.VolumeSetOnFragment;
import com.kandi.settings.utils.FragmentHolder;

public class VolumeActivity extends Fragment implements SetbtnListener {
	private View buttom_shadow;
	private FrameLayout mainview;
	private FragmentHolder fh;
	private final String SHOWSETON = "showseton";
	private final String SHOWSETOFF = "showsetoff";
	public static VolumeActivity instance;

	private void initView(View view) {
		instance = this;
		fh = new FragmentHolder(getChildFragmentManager());
		fillFragment(0);
		buttom_shadow = (View) view.findViewById(R.id.buttom_shadow);
		buttom_shadow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String[] infos = getActivityAtSecond();
				if(infos != null){
					//启动上一个activity
					gotoActivity(infos[0], infos[1]);
				}else{
					if(KdsApplication.getInstance().getWindowActvity() != null && !KdsApplication.getInstance().getWindowActvity().isDestroyed()){
						KdsApplication.getInstance().getWindowActvity().finish();
						KdsApplication.getInstance().setWindowActvity(null);
					}
				}
			}
		});
		mainview = (FrameLayout) view.findViewById(R.id.mainview);
		mainview.setOnClickListener(null);
	}

	// 将不同的fragment填充到frame中
	private void fillFragment(int type) {
		switch (type) {
		case 0:
			if (!fh.isAdded(SHOWSETOFF)) {
				fh.addFragment(SHOWSETOFF, new VolumeSetOffFragment());
				fh.addAndShow(R.id.mainview, SHOWSETOFF);
			} else {
				fh.hideOthersAndShow(SHOWSETOFF);
				fh.getFragment(SHOWSETOFF).onResume();
			}
			break;
		case 1:
			if (!fh.isAdded(SHOWSETON)) {
				fh.addFragment(SHOWSETON, new VolumeSetOnFragment());
				fh.addAndShow(R.id.mainview, SHOWSETON);
			} else {
				fh.hideOthersAndShow(SHOWSETON);
				fh.getFragment(SHOWSETON).onResume();
			}
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.voice_layout, container, false);
		this.initView(view);
		return view;
	}

	//回调函数fragment中set按钮被点击后由此处处理
	@Override
	public void OnSetbtnClicked(int position) {
		if(position == 0){
			fillFragment(1);
			return;
		}
		if(position == 1){
			fillFragment(0);
			return;
		}
	}
	
	private String[] getActivityAtSecond() {
		try {
			ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName cn = am.getRunningTasks(2).get(1).topActivity;
			String[] infos = new String[2];
			infos[0] = cn.getPackageName();
			infos[1] = cn.getClassName();
			return infos;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
    }
	
	void gotoActivity(String pkg,String activityName) {
        Intent homeIntent = new Intent();
        homeIntent = new Intent();
        homeIntent.setComponent(new ComponentName(pkg, activityName));
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(homeIntent);
        } catch (Exception e) {
            Log.e("KandiSystemUiService", e.toString());
        }
    }
	
	/**
	 * 窗口外部阴影点击事件，需要可重写
	 * @return true 表示消费事件，不做关闭窗口处理了
	 */
	public boolean onShadowCilck(){
		return false;
	}

}
