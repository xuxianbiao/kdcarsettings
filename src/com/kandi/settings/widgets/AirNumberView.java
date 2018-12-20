package com.kandi.settings.widgets;

import com.kandi.settings.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class AirNumberView extends RelativeLayout{
	private LayoutInflater inflater; 
	private ImageButton num0;
	private ImageButton num1;
	private ImageButton num2;
	private void initView(Context context){
		inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.air_no_view, null);
		this.addView(view);
		num0 = (ImageButton) view.findViewById(R.id.num0);
		num1 = (ImageButton) view.findViewById(R.id.num1);
		num2 = (ImageButton) view.findViewById(R.id.num2);
		
	}
	public AirNumberView(Context context) {
		super(context);
		 this.initView(context);
	}
	public AirNumberView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.initView(context);
	}
	public AirNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.initView(context);
	}
	private void setNo(int no,ImageButton v){
		switch (no) {
		case 0:
			v.setImageResource(R.drawable.air_temperature_now_0);
			break;
		case 1:
			v.setImageResource(R.drawable.air_temperature_now_1);
			break;
		case 2:
			v.setImageResource(R.drawable.air_temperature_now_2);
			break;
		case 3:
			v.setImageResource(R.drawable.air_temperature_now_3);
			break;
		case 4:
			v.setImageResource(R.drawable.air_temperature_now_4);
			break;
		case 5:
			v.setImageResource(R.drawable.air_temperature_now_5);
			break;
		case 6:
			v.setImageResource(R.drawable.air_temperature_now_6);
			break;
		case 7:
			v.setImageResource(R.drawable.air_temperature_now_7);
			break;
		case 8:
			v.setImageResource(R.drawable.air_temperature_now_8);
			break;
		case 9:
			v.setImageResource(R.drawable.air_temperature_now_9);
			break;
		default:
			break;
		}
	}
	//设置温度数字
	public void setTemperature(int temp){
		num0.setVisibility(View.INVISIBLE);
		num1.setVisibility(View.INVISIBLE);
		num2.setVisibility(View.INVISIBLE);
		String tempStr = temp+"";	
		if(tempStr.startsWith("-")){
			num0.setVisibility(View.VISIBLE);
			num0.setImageResource(R.drawable.air_temperature_now_fu);
			
			if(tempStr.length()==3){
				num1.setVisibility(View.VISIBLE);
				num2.setVisibility(View.VISIBLE);
				int tenNo = Integer.parseInt(tempStr.subSequence(1, 2)+"");
				int oneNo = Integer.parseInt(tempStr.subSequence(2, 3)+"");
				this.setNo(tenNo, num1);
				this.setNo(oneNo, num2);
			}else{
				num2.setVisibility(View.VISIBLE);
				num1.setVisibility(View.GONE);						
				int oneNo = Integer.parseInt(tempStr.subSequence(1, 2)+"");
				this.setNo(oneNo, num2);
			}
		}else{
			num0.setVisibility(View.INVISIBLE);
			if(tempStr.length()==2){
				num1.setVisibility(View.VISIBLE);
				num2.setVisibility(View.VISIBLE);
				int tenNo = Integer.parseInt(tempStr.subSequence(0, 1)+"");
				int oneNo = Integer.parseInt(tempStr.subSequence(1, 2)+"");
				this.setNo(tenNo, num1);
				this.setNo(oneNo, num2);
			}else{
				num2.setVisibility(View.VISIBLE);
				int oneNo = Integer.parseInt(tempStr.subSequence(0, 1)+"");
				this.setNo(oneNo, num2);
			}
		}
	}
}
