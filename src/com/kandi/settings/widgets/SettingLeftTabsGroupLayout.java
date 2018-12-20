package com.kandi.settings.widgets;

import com.kandi.settings.R;
import com.kandi.settings.utils.DpUtils;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
/**
 * 设置栏左侧tab自定义管理控件
 * @author illidan
 *
 */
public class SettingLeftTabsGroupLayout extends ScrollView {

	LinearLayout linearLayout = null;
	
	private static final int ITEM_BACKGROUD_NULL = R.drawable.set_shoots_button_bg;
	private static final int ITEM_BACKGROUD = R.drawable.set_shoots_button_bg_pressed;
	private static final int ITEM_CENTER_BACKGROUD_ON = R.drawable.set_shoots_tap_btn_on;
	private static final int ITEM_CENTER_BACKGROUD_OFF = R.drawable.set_shoots_tap_btn_off;
	
	private static final int ITEM_TEXT_COLOR_OFF = Color.parseColor("#C5CBCD");
	private static final int ITEM_TEXT_COLOR_ON = Color.WHITE;
	private int ITEM_TEXT_SIZE = 30;
	
	private int nowSelect = 0;
	
	public SettingLeftTabsGroupLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initViews(context);
		ITEM_TEXT_SIZE = DpUtils.sp2px(getResources(), 18);
	}
	
	public SettingLeftTabsGroupLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public SettingLeftTabsGroupLayout(Context context) {
		this(context, null, 0);
	}

	private void initViews(Context context){
		linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		addView(linearLayout);
	}
	
	//给非第一项用的布局特征
	android.widget.LinearLayout.LayoutParams itemLP = new android.widget.LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	//给第一项用的布局特征
	android.widget.LinearLayout.LayoutParams itemLP2 = new android.widget.LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	
	/**
	 * 添加一个tab选项
	 * @param set
	 */
	private View addChildTab(String set,int index){
		LinearLayout tabLe= new LinearLayout(getContext());
		tabLe.setBackgroundResource(ITEM_BACKGROUD_NULL);
		tabLe.setGravity(Gravity.CENTER);
		Button button = new Button(getContext());
		button.setBackgroundResource(ITEM_CENTER_BACKGROUD_OFF);
		button.setText(set);
		button.setTextColor(ITEM_TEXT_COLOR_OFF);
		button.setTextSize(ITEM_TEXT_SIZE);
		button.setClickable(false);
		button.setGravity(Gravity.CENTER_HORIZONTAL);
		button.setPadding(0, 16, 0, 0);
		if(index == 0){
			itemLP2.topMargin = 5;
			linearLayout.addView(tabLe,itemLP2);
		}else{
			itemLP.topMargin = -20;						
			linearLayout.addView(tabLe,itemLP);
		}
		tabLe.addView(button);
		setOnTabClickListenr(tabLe, index);
		return tabLe;
	}
	
	/**
	 * 添加多个选项
	 * @param sets 选项内容
	 * @param defualtSelectIndex 默认选择第几项
	 */
	public void addChildTabs(String[] sets,int defualtSelectIndex){
		if(sets != null && sets.length > 0){
			linearLayout.removeAllViews();
			for(int i = 0 ; i<sets.length ; i++){
				View v = addChildTab(sets[i],i);
				if(i == defualtSelectIndex){
					setTabSelectState(v, true);
					nowSelect = defualtSelectIndex;
					if(clickListener != null){
						clickListener.onItemClick(v, defualtSelectIndex);
					}
				}
			}
		}
	}
	
	/**
	 * 添加多个选项,默认选择第一项
	 * @param sets 选项内容
	 * @param 
	 */
	public void addChildTabs(String[] sets){
		addChildTabs(sets,0);
	}

	private void setOnTabClickListenr(View view,final int index){
		view.setClickable(true);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(nowSelect == index){
					//
					return;
				}
				setTabSelectState(v, true);
				View last = linearLayout.getChildAt(nowSelect);
				if(last != null){
					setTabSelectState(last, false);					
				}
				nowSelect = index;
				if(clickListener != null){
					clickListener.onItemClick(v, index);
				}
			}
		});
	}
	
	private void setTabSelectState(View v ,boolean selected){
		if(v instanceof LinearLayout){
			LinearLayout l = ((LinearLayout)v);
			View item = l.getChildAt(0);
			if(item != null && item instanceof Button){
				item.setBackgroundResource(selected ? ITEM_CENTER_BACKGROUD_ON : ITEM_CENTER_BACKGROUD_OFF);
				((Button)item).setTextColor(selected ? ITEM_TEXT_COLOR_ON : ITEM_TEXT_COLOR_OFF);
			}
			if(selected){
				l.setBackgroundResource(ITEM_BACKGROUD);
			}else{
				l.setBackgroundResource(ITEM_BACKGROUD_NULL);
			}
		}
	}
	
	private TabClickListener clickListener;
	
	public void setTabClickListener(TabClickListener tabClickListener){
		this.clickListener = tabClickListener;
	}
	
	public void removeTabClickListener(){
		this.clickListener = null;
	}
	
	/**
	 * 获取上次点击的是哪一项
	 * @return
	 */
	public int getLastSelect(){
		return nowSelect;
	}
	
	/**
	 * 按钮点击回调
	 * @author illidan
	 *
	 */
	public interface TabClickListener{
		public void onItemClick(View v,int position);
	}
}
