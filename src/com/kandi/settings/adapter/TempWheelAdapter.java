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

public class TempWheelAdapter extends BaseAdapter {
    int mHeight = 54;	//item height
    int mPadding = 13;	//item padding top or bottom
    private Context context;
    private List<String> dataList;
    public TempWheelAdapter(List<String> dataList, Context context) 
    { 
        super(); 
        this.context = context;
        this.dataList = dataList;
        
    } 
    public TempWheelAdapter() {
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
				imageView.setImageResource(R.drawable.air_temperature_wendu_18);
				break;
			case 1:
				imageView.setImageResource(R.drawable.air_temperature_wendu_19);
				break;
			case 2:
				imageView.setImageResource(R.drawable.air_temperature_wendu_20);
				break;
			case 3:
				imageView.setImageResource(R.drawable.air_temperature_wendu_21);
				break;
			case 4:
				imageView.setImageResource(R.drawable.air_temperature_wendu_22);
				break;
			case 5:
				imageView.setImageResource(R.drawable.air_temperature_wendu_23);
				break;
			case 6:
				imageView.setImageResource(R.drawable.air_temperature_wendu_24);
				break;
			case 7:
				imageView.setImageResource(R.drawable.air_temperature_wendu_25);
				break;
			case 8:
				imageView.setImageResource(R.drawable.air_temperature_wendu_26);
				break;
			case 9:
				imageView.setImageResource(R.drawable.air_temperature_wendu_27);
				break;
			case 10:
				imageView.setImageResource(R.drawable.air_temperature_wendu_28);
				break;
			case 11:
				imageView.setImageResource(R.drawable.air_temperature_wendu_29);
				break;
			case 12:
				imageView.setImageResource(R.drawable.air_temperature_wendu_30);
				break;
			case 13:
				imageView.setImageResource(R.drawable.air_temperature_wendu_31);
				break;
			case 14:
				imageView.setImageResource(R.drawable.air_temperature_wendu_32);
				break;
			case 15:
				imageView.setImageResource(R.drawable.air_temperature_wendu_33);
				break;
			case 16:
				imageView.setImageResource(R.drawable.air_temperature_wendu_34);
				break;
				
			default:
				imageView.setImageResource(R.drawable.air_temperature_wendu_18);
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
