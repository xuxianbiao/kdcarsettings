package com.kandi.settings.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kandi.settings.R;
import com.kandi.settings.utils.Utils;
import com.kandi.settings.widgets.TosGallery;

public class ModeWheelViewAdapter extends BaseAdapter {
    int mHeight = 66;	//item height
    int mPadding = 9;	//item padding top or bottom
    private Context context;
    private List<String> dataList;
    public ModeWheelViewAdapter(List<String> dataList, Context context) 
    { 
        super(); 
        this.context = context;
        this.dataList = dataList;
        
    } 
    public ModeWheelViewAdapter() {
        mHeight = (int) Utils.pixelToDp(context, mHeight);
    }

    @Override
    public int getCount() {
        return (null != dataList) ? dataList.size() : 0;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txtView = null;
        ImageView imageView = null;
        if (null == convertView) {
        	convertView = new ImageView(context);
//            convertView = new TextView(WheelViewPasswordActivity.this);
        	convertView.setLayoutParams(new TosGallery.LayoutParams(-1, mHeight + mPadding*2));
        	convertView.setPadding(IGNORE_ITEM_VIEW_TYPE, mPadding, IGNORE_ITEM_VIEW_TYPE, mPadding);

//            txtView = (TextView) convertView;
//            txtView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
//            txtView.setTextColor(Color.BLACK);
//            txtView.setGravity(Gravity.CENTER);
            
            imageView = (ImageView) convertView;
            switch (position) {
			case 0:
				imageView.setImageResource(R.drawable.air_btn_bg_show_wind_icon_01);
				break;
			case 1:
				imageView.setImageResource(R.drawable.air_btn_bg_show_wind_icon_02);
				break;
			case 2:
				imageView.setImageResource(R.drawable.air_btn_bg_show_wind_icon_03);
				break;
			case 3:
				imageView.setImageResource(R.drawable.air_btn_bg_show_wind_icon_04);
				break;
			case 4:
				imageView.setImageResource(R.drawable.air_btn_bg_show_wind_icon_05);
				break;
			case 5:
				imageView.setImageResource(R.drawable.air_btn_bg_show_wind_icon_06);
				break;
			case 6:
				imageView.setImageResource(R.drawable.air_btn_bg_show_wind_icon_07);
				break;
			case 7:
				imageView.setImageResource(R.drawable.air_btn_bg_show_wind_icon_08);
				break;

			default:
				imageView.setImageResource(R.drawable.air_btn_bg_show_wind_icon_01);
				break;
			}
        }

//        String text = String.valueOf(mData[position]);
//        if (null == txtView) {
//            txtView = (TextView) convertView;
//        }
//
//        txtView.setText(text);

        return convertView;
    }
}
