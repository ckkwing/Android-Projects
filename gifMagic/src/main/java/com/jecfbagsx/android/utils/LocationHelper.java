package com.jecfbagsx.android.utils;

import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class LocationHelper {

	private LocationManager m_locationManager = null;
	private LocationProvider m_locationProvider = null;
	private Location m_location = null;
	private Criteria m_criteria = null;
	private String m_currentProvider = "";
	private Context m_context = null;
	private boolean m_isStart = false;
	public static final int ID_OPENNETWORKCALLBACK = 100;

	private static LocationHelper m_instance = null;

	public static synchronized LocationHelper getInstance() {
		if (m_instance == null) {
			m_instance = new LocationHelper();
		}
		return m_instance;
	}

	private LocationListener m_locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			m_currentProvider = provider;
			if (m_currentProvider != null && m_currentProvider.length() > 0) {
				m_location = m_locationManager
						.getLastKnownLocation(m_currentProvider);
			}
			start();
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			m_location = null;
			m_currentProvider = "";
//			stop();
		}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			update(location);
		}
	};
	
	public LocationHelper()
	{
		
	}

	public LocationHelper(Context context) {
		// TODO Auto-generated constructor stub
		
		if (m_currentProvider != null && m_currentProvider.length() > 0) {
			m_location = m_locationManager
					.getLastKnownLocation(m_currentProvider);
		} else {
			openNetwork();
		}
	}
	
	public void init(Context context) {
		m_context = context;
		m_locationManager = (LocationManager) m_context
				.getSystemService(Context.LOCATION_SERVICE);
		getProvider();
	}
	
	public void uninit()
	{
		
	}

	public boolean getIsNetworkAvailable() {
		return m_locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
				|| m_locationManager
						.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
	}

	private void getProvider() {
		if (null == m_criteria) {
			m_criteria = new Criteria();
			m_criteria.setAccuracy(Criteria.ACCURACY_FINE);
			m_criteria.setAltitudeRequired(false);
			m_criteria.setBearingRequired(false);
			m_criteria.setCostAllowed(true);
			m_criteria.setPowerRequirement(Criteria.POWER_LOW);
		}

		m_currentProvider = m_locationManager.getBestProvider(m_criteria, true);
	}

	public void openNetwork() {
		Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
		((Activity) m_context).startActivityForResult(intent,ID_OPENNETWORKCALLBACK);
	}

	public Location getLocation() {
		return m_location;
	}
	
//	public  GeoPoint getGeoByLocation(Location location) {  
//        GeoPoint gp = null;  
//        try {  
//            if (location != null) {  
//                double geoLatitude = location.getLatitude() * 1E6;  
//                double geoLongitude = location.getLongitude() * 1E6;  
//                gp = new GeoPoint((int) geoLatitude, (int) geoLongitude);  
//            }  
//        } catch (Exception e) {  
//            e.printStackTrace();  
//        }  
//        return gp;  
//    }
//	
//	public GeoPoint getGeo() {
//		return getGeoByLocation(m_location);
//	}
	
//	public  Address getAddressbyGeoPoint(Context cntext, GeoPoint gp) {  
//        Address result = null;  
//        try {  
//            if (gp != null) {  
//                Geocoder gc = new Geocoder(cntext);  
//                 
//                double geoLatitude = (int) gp.getLatitudeE6() / 1E6;  
//                double geoLongitude = (int) gp.getLongitudeE6() / 1E6;  
//                  
//                List<Address> lstAddress = gc.getFromLocation(geoLatitude,  
//                        geoLongitude, 1);  
//                if (lstAddress.size() > 0) {  
//                    result = lstAddress.get(0);  
//                }  
//            }  
//        } catch (Exception e) {  
//            e.printStackTrace();  
//        }  
//        return result;  
//    }
//	
//	public Address getAddress()
//	{
//		return getAddressbyGeoPoint(m_context, getGeo());
//	}
	
	public Address getAddress(Context context) {
		Address result = null;
		try {

			if ( null != m_location)
			{
				Geocoder gc = new Geocoder(context);
				
				double geoLatitude = m_location.getLatitude();
				double geoLongitude = m_location.getLongitude();

				List<Address> lstAddress = gc.getFromLocation(geoLatitude,
						geoLongitude, 1);
				if (lstAddress.size() > 0) {
					result = lstAddress.get(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void start() {
		if (m_isStart)
			return;
		getProvider();
		if (m_locationManager != null && m_currentProvider != null
				&& m_currentProvider.length() > 0) {
			m_locationManager.requestLocationUpdates(m_currentProvider, 1000,
					0, m_locationListener);
			m_isStart = true;
		}
	}

	public void stop() {
		if (!m_isStart)
			return;
		if (m_locationManager != null && m_currentProvider != null
				&& m_currentProvider.length() > 0) {
			m_locationManager.removeUpdates(m_locationListener);
			m_isStart = false;
		}
	}

	private void update(Location location) {
		m_location = location;
	}

}
