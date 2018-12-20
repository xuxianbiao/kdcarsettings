package com.kandi.settings.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

import com.kandi.settings.widgets.MyTimerCheck;

public class WifiAdmin {
	public final int SECURITY_NONE = 0;
    public final int SECURITY_WEP = 1;
    public final int SECURITY_PSK = 2;
    public final int SECURITY_EAP = 3;
	private final String TAG = "WifiAdmin";
	public WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	private List<ScanResult> mTempWifiList;
	private List<WifiConfiguration> mWifiConfigurations;
	WifiLock mWifiLock;
    private List<ScanResult> mWifiList  = new ArrayList<ScanResult>();
    private List<String> mScanHisResult = new ArrayList<String>();
	public WifiAdmin(Context context) {
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
	}

	public void openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	public void closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	public int checkState() {
		return mWifiManager.getWifiState();
	}

	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	public void releaseWifiLock() {
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	public void createWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("test");
	}

	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfigurations;
	}

	public boolean connetionConfiguration(String SSID) {
		for(int i=0;i<mWifiConfigurations.size();i++){
			if(("\""+SSID+"\"").equals(mWifiConfigurations.get(i).SSID)){
				mWifiManager.enableNetwork(mWifiConfigurations.get(i).networkId,true);
				return true;
			}
		}
		return false;
	}
	
	public int connetionConfigurationIndex(String SSID) {
		for(int i=0;i<mWifiConfigurations.size();i++){
			if(("\""+SSID+"\"").equals(mWifiConfigurations.get(i).SSID)){
				return mWifiConfigurations.get(i).networkId;
			}
		}
		return -1;
	}

	public void startScan() {
		mWifiManager.startScan();
		mTempWifiList = mWifiManager.getScanResults();
		mWifiConfigurations = mWifiManager.getConfiguredNetworks();
	}

	public List<ScanResult> getWifiList() {
        if (mTempWifiList != null) {
        	mWifiList.clear();
        	mScanHisResult.clear();
            for (ScanResult result : mTempWifiList) {
                // Ignore hidden and ad-hoc networks.
                if (result.SSID == null || result.SSID.length() == 0 ||
                        result.capabilities.contains("[IBSS]") || mScanHisResult.contains(result.SSID)) {
                    continue;
                }
                mScanHisResult.add(result.SSID);
                mWifiList.add(result);
            }
        }
	    
		return mWifiList;
	}

	public StringBuffer lookUpScan() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mWifiList.size(); i++) {
			sb.append("Index_" + new Integer(i + 1).toString() + ":");
			sb.append((mWifiList.get(i)).toString()).append("\n");
		}
		return sb;
	}

	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	public int getIpAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	public int getNetWordId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	public void addNetWork(WifiConfiguration configuration) {
		int wcgId = mWifiManager.addNetwork(configuration);
		mWifiManager.enableNetwork(wcgId, true);
		mWifiManager.saveConfiguration();
	}

	public void disConnectionWifi(int netId) {
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}

	public void removeConfig(int netId) {
		mWifiManager.removeNetwork(netId);
		mWifiManager.saveConfiguration();
	}

	public boolean wifiIsConnect(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected()) {
			return true;
		}
		return false;
	}

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

	public WifiConfiguration CreateWifiInfo(String SSID, String Password,
			String Type) {
		int sec = GetSecurity(Type);
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = isExsits(SSID, mWifiManager);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}

		if (sec == SECURITY_NONE) // WIFICIPHER_NOPASS
		{
//			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//			config.wepTxKeyIndex = 0;
		}
		if (sec == SECURITY_WEP) // WIFICIPHER_WEP
		{
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.wepTxKeyIndex = 0;

			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			config.wepKeys[0] = "\"" + Password + "\"";
			config.status = WifiConfiguration.Status.ENABLED;
		}
		if (sec == SECURITY_PSK) // WIFICIPHER_WPA
		{
			config.preSharedKey = "\"" + Password + "\"";
			/*config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);*/
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			/*config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;*/
		}
		addNetWork(config);
		return config;
	}
	
	/**
	 * 判断wifi是否存在
	 * 
	 * @param SSID
	 * @param wifiManager
	 * @return
	 */
	private static WifiConfiguration isExsits(String SSID, WifiManager wifiManager) {
		List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	private String mSSID = "";
	private String mPasswd = "";

	public void startWifiAp(String ssid, String passwd) {
		mSSID = ssid;
		mPasswd = passwd;

		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}

		stratWifiAp();

		MyTimerCheck timerCheck = new MyTimerCheck() {

			@Override
			public void doTimerCheckWork() {
				// TODO Auto-generated method stub

				if (isWifiApEnabled(mWifiManager)) {
					Log.v(TAG, "Wifi enabled success!");
					this.exit();
				} else {
					Log.v(TAG, "Wifi enabled failed!");
				}
			}

			@Override
			public void doTimeOutWork() {
				// TODO Auto-generated method stub
				this.exit();
			}
		};
		timerCheck.start(15, 1000);

	}

	public void stratWifiAp() {
		Method method1 = null;
		try {
			method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
					WifiConfiguration.class, boolean.class);
			WifiConfiguration netConfig = new WifiConfiguration();

			netConfig.SSID = mSSID;
			netConfig.preSharedKey = mPasswd;

			netConfig.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			netConfig.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			netConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			netConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			netConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
			netConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);

			method1.invoke(mWifiManager, netConfig, true);

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeWifiAp() {
		if (isWifiApEnabled(mWifiManager)) {
			try {
				Method method = mWifiManager.getClass().getMethod(
						"getWifiApConfiguration");
				method.setAccessible(true);

				WifiConfiguration config = (WifiConfiguration) method
						.invoke(mWifiManager);

				Method method2 = mWifiManager.getClass().getMethod(
						"setWifiApEnabled", WifiConfiguration.class,
						boolean.class);
				method2.invoke(mWifiManager, config, false);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean isWifiApEnabled(WifiManager mWifiManager) {
		try {
			Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
			method.setAccessible(true);
			return (Boolean) method.invoke(mWifiManager);

		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
