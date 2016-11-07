package br.com.compras.app.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class WiFiCommunicationHelper {
	
	public static boolean isWiFiEnable(Context context) {

		WifiManager wiFiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    
		return wiFiManager.isWifiEnabled();
	}
	
	public static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    
	    // if no network is available networkInfo will be null otherwise check if we are connected
	    if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
}
