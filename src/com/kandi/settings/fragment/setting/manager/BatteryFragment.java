package com.kandi.settings.fragment.setting.manager;

import java.text.DecimalFormat;
import com.kandi.settings.R;
import com.kandi.settings.driver.ConfigDriver31MgnBattarySetup;
import com.kandi.settings.driver.DriverServiceManger;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 电池管理
 * @author illidan
 *
 */
public class BatteryFragment extends Fragment {

	View view;
	
	private EditText charger_lowtemp;
	private EditText charger_lowcell;
	private EditText charger_lowcurr;
	private EditText charger_normaltemp;
	private EditText charger_normalcell;
	private EditText charger_normalcurr;
	private EditText charger_hightemp;
	private EditText charger_highcell;
	private EditText charger_highcurr;
	
	private EditText charger_stopvol;
	private EditText charger_stopcellvol;
	private EditText charger_stoptemp;
	private EditText charger_stopcurr;
	private EditText reduce_cell;
	private EditText reduce_time;
	
	private EditText dis_lowtemp;
	private EditText dis_lowcellw;
	private EditText dis_lowcelle;
	private EditText dis_normaltemp;
	private EditText dis_normalcellw;
	private EditText dis_normalcelle;
	private EditText dis_hightemp;
	private EditText dis_highcellw;
	private EditText dis_highcelle;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fg_battery, null);
		
		initViews();
		
		new Thread(){

			@Override
			public void run() {
				super.run();
				ConfigDriver31MgnBattarySetup model = DriverServiceManger.getInstance().getConfigDriver31MgnBattarySetup();
				if(model != null) {
					try {
						model.retrieveBattaryParam();
						Message m = Message.obtain();
						m.obj = model;
						h.sendMessage(m);
					}catch (RemoteException e) {
						e.printStackTrace();
						return;
					}
				}else{
					h.sendEmptyMessage(0);
				}
			}
			
		}.start();
		
		return view;
	}
	
	private View findViewById(int id){
		return view.findViewById(id);
	}
	
	private void initViews(){
		charger_lowtemp = (EditText) findViewById(R.id.charger_lowtemp);
		charger_lowcell = (EditText) findViewById(R.id.charger_lowcell);
		charger_lowcurr = (EditText) findViewById(R.id.charger_lowcurr);
		charger_normaltemp = (EditText) findViewById(R.id.charger_normaltemp);
		charger_normalcell = (EditText) findViewById(R.id.charger_normalcell);
		charger_normalcurr = (EditText) findViewById(R.id.charger_normalcurr);
		charger_hightemp = (EditText) findViewById(R.id.charger_hightemp);
		charger_highcell = (EditText) findViewById(R.id.charger_highcell);
		charger_highcurr = (EditText) findViewById(R.id.charger_highcurr);
		
		charger_stopvol = (EditText) findViewById(R.id.charger_stopvol);
		charger_stopcellvol = (EditText) findViewById(R.id.charger_stopcellvol);
		charger_stoptemp = (EditText) findViewById(R.id.charger_stoptemp);
		charger_stopcurr = (EditText) findViewById(R.id.charger_stopcurr);
		reduce_cell = (EditText) findViewById(R.id.reduce_cell);
		reduce_time = (EditText) findViewById(R.id.reduce_time);
		
		dis_lowtemp = (EditText) findViewById(R.id.dis_lowtemp);
		dis_lowcellw = (EditText) findViewById(R.id.dis_lowcellw);
		dis_lowcelle = (EditText) findViewById(R.id.dis_lowcelle);
		dis_normaltemp = (EditText) findViewById(R.id.dis_normaltemp);
		dis_normalcellw = (EditText) findViewById(R.id.dis_normalcellw);
		dis_normalcelle = (EditText) findViewById(R.id.dis_normalcelle);
		dis_hightemp = (EditText) findViewById(R.id.dis_hightemp);
		dis_highcellw = (EditText) findViewById(R.id.dis_highcellw);
		dis_highcelle = (EditText) findViewById(R.id.dis_highcelle);
	}
	
	Handler h = new Handler(Looper.getMainLooper()){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.obj != null && msg.obj instanceof ConfigDriver31MgnBattarySetup){
				refreshPannel((ConfigDriver31MgnBattarySetup)msg.obj);
			}else{
				refreshPannel(null);				
			}
		}
		
	};
	
	/**
	 * 将模型数据更新到屏幕
	 */
	private void refreshPannel(ConfigDriver31MgnBattarySetup model) {
		if(model != null) {
			DecimalFormat fnum = new DecimalFormat("#0.0");
			charger_lowtemp.setText(model.getChargerLowTemp()+this.getString(R.string.unit_degree));
			charger_lowcell.setText(model.getChargerLowCell()+this.getString(R.string.unit_mv));
			charger_lowcurr.setText(fnum.format(model.getChargerLowCurr()*0.1)+this.getString(R.string.unit_a));
			charger_normaltemp.setText(model.getChargerNormalTemp()+this.getString(R.string.unit_degree));
			charger_normalcell.setText(model.getChargerNormalCell()+this.getString(R.string.unit_mv));
			charger_normalcurr.setText(fnum.format(model.getChargerNormalCurr()*0.1)+this.getString(R.string.unit_a));
			charger_hightemp.setText(model.getChargerHighTemp()+this.getString(R.string.unit_degree));
			charger_highcell.setText(model.getChargerHighCell()+this.getString(R.string.unit_mv));
			charger_highcurr.setText(fnum.format(model.getChargerHighCurr()*0.1)+this.getString(R.string.unit_a));
			
			charger_stopvol.setText(fnum.format(model.getChargerStopVol()*0.1)+this.getString(R.string.unit_v));
			charger_stopcellvol.setText(model.getChargerStopCellVol()+this.getString(R.string.unit_mv));
			charger_stoptemp.setText(model.getChargerStopTemp()+this.getString(R.string.unit_degree));
			charger_stopcurr.setText(fnum.format(model.getChargerStopCurr()*0.1)+this.getString(R.string.unit_a));
			reduce_cell.setText(model.getReduceCell()+this.getString(R.string.unit_mv));
			reduce_time.setText(model.getReduceTime()+this.getString(R.string.unit_second));
			
			dis_lowtemp.setText(model.getDischargerLowTemp()+this.getString(R.string.unit_degree));
			dis_lowcellw.setText(model.getDischargerLowCellw()+this.getString(R.string.unit_mv));
			dis_lowcelle.setText(model.getDischargerLowCelle()+this.getString(R.string.unit_mv));
			dis_normaltemp.setText(model.getDischargerNormalTemp()+this.getString(R.string.unit_degree));
			dis_normalcellw.setText(model.getDischargerNormalCellw()+this.getString(R.string.unit_mv));
			dis_normalcelle.setText(model.getDischargerNormalCelle()+this.getString(R.string.unit_mv));
			dis_hightemp.setText(model.getDischargerHighTemp()+this.getString(R.string.unit_degree));
			dis_highcellw.setText(model.getDischargerHighCellw()+this.getString(R.string.unit_mv));
			dis_highcelle.setText(model.getDischargerHighCelle()+this.getString(R.string.unit_mv));
			//TODO: ...将模型数据更新到屏幕...
		}else {
			Toast.makeText(getActivity(), this.getString(R.string.warning_service), Toast.LENGTH_LONG).show();
			Log.e("KDSERVICE", "SystemSettingActivity3_1.refreshPannel() service is null");
		}
	}
}
