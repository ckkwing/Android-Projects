package com.jecfbagsx.android.gifmanage;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.util.Log;

public class ConnectManager {
	private static final String TAG = "ConnectManager";
	private static ConnectManager instance = null;

	private ConnectManager() {
	};

	public static synchronized ConnectManager getInstance() {
		if (instance == null)
			instance = new ConnectManager();
		return instance;
	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				if (intf == null)
					continue;
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (inetAddress == null)
						continue;
					if (!inetAddress.isLoopbackAddress()) {
						Log.i(TAG,
								"Got localhost ip address is successful, IP = "
										+ inetAddress.getHostAddress()
												.toString());
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(TAG, ex.toString());
		}
		Log.i(TAG, "Got localhost ip address is failed...");
		return null;
	}
}
