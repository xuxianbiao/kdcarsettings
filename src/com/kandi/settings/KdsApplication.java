package com.kandi.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.ebookdroid.core.cache.CacheManager;
import org.ebookdroid.core.log.EmergencyHandler;
import org.ebookdroid.core.log.LogContext;
import org.ebookdroid.core.settings.SettingsManager;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.IBinder;

import com.driverlayer.kdos_driverServer.BlueDriver;
import com.driverlayer.kdos_driverServer.IECarDriver;
import com.kandi.settings.activitys.WindowActivity;
import com.kandi.settings.driver.DriverServiceManger;
import com.kandi.settings.model.PresistentData;
import com.kandi.settings.utils.CrashHandler;

public class KdsApplication extends Application {
	private static final String TAG = "kangdiSettings_voice";
	public WindowActivity windowActvity = null;
    private static KdsApplication mApplication = null;
    public static BlueDriver bluedriver;
    final int Restart_time=5;//如果检查到服务程序崩溃，5秒后将重启服务
    int TimeOut_Count=0;
    public static final LogContext LCTX = LogContext.ROOT;
    public static Context context;
    public static String APP_VERSION;
    public static String APP_PACKAGE;
    public static File EXT_STORAGE;
    public boolean isHotMode = false;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		DriverServiceManger.getInstance().startService(this);
		PresistentData.initInstance(this);
		mApplication = this;
		//捕获全局异常
		CrashHandler handler = CrashHandler.getInstance();
		handler.init(getApplicationContext());
		Thread.setDefaultUncaughtExceptionHandler(handler);
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				IECarDriver model = DriverServiceManger.getInstance().R_service;
				if (model == null) {
					TimeOut_Count++;
		        	if(TimeOut_Count>=Restart_time){
			            restartKdService();
			            TimeOut_Count = 0;
		        	}
				}
			}
		}, 15000, 1000);
		
		/**pdf集成*/
		this.init();

        EmergencyHandler.init(this);
        LogContext.init(this);
        SettingsManager.init(this);
        CacheManager.init(this);
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                if(!new File(getString(R.string.pdf_path)).exists()){
                    copyFileToSdcard();
                }else{
					try {
						InputStream in;
						in = getResources().openRawResource(R.raw.instructions_geely);
						int lenght = in.available();
						if(new File(getString(R.string.pdf_path)).length()!=lenght){
							copyFileToSdcard();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
                }
            }
        }).start();
        Intent service = new Intent("com.kangdi.InitService");
		bindService(service, conn, Context.BIND_AUTO_CREATE);
	}
	
	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			bluedriver = BlueDriver.Stub.asInterface(arg1);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			bluedriver = null;
		}

	};
	
    public static KdsApplication getInstance() {
        return mApplication;
    }
    
    /**
	 * 当后台服务未启动时，尝试重新启动KD后台服务
	 * 
	 * @return false:后台服务启动中; true:后台服务已重启成功，无需重启
	 */
	public boolean restartKdService() {
		if (!DriverServiceManger.getInstance().isServiceRunning()) {
			DriverServiceManger.getInstance().startService(this);
			return false;
		} else {
			return true;
		}
	}
	
	protected void init() {
        context = getApplicationContext();

        final PackageManager pm = getPackageManager();
        try {
            final PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            APP_VERSION = pi.versionName;
            APP_PACKAGE = pi.packageName;
            EXT_STORAGE = Environment.getExternalStorageDirectory();
            
        } catch (final NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void copyFileToSdcard() {
    	InputStream inputstream;
    	inputstream = getResources().openRawResource(R.raw.instructions_geely);
        byte[] buffer = new byte[1024];
        int count = 0;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(getString(R.string.pdf_path)));
            while ((count = inputstream.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public boolean isHotMode() {
		return isHotMode;
	}

	public void setHotMode(boolean isHotMode) {
		this.isHotMode = isHotMode;
	}

	public WindowActivity getWindowActvity() {
		return windowActvity;
	}

	public void setWindowActvity(WindowActivity windowActvity) {
		this.windowActvity = windowActvity;
	}
}
