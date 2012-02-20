package com.bubbles.location;


import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public class MyLocation {
	private static final int LOC_UPDATE = 0;
	//CONSTANTS
	final long initialUpdateInterval = 60000L;
	final long updateInterval = 300000L; //5min
	final long onInterval = 30000L;
	final float gpsTolerance = 100;
	
	protected LocationManager locationManager;
	protected String provider;
	public static double lon, lat;
	public long mUpdateInterval;
	protected GeoUpdateListener gul;
	protected GPSUpdateListener gpsul;
	
	private static Handler downloadSignalHandler = null;
	
	protected float gpsAcc;
	protected float otherAcc;


	private boolean looper1 = true;
	Criteria criteria;


	public static double getLon() {
		return lon;
	}

	public static double getLat() {
		return lat;
	}

	public MyLocation(Activity mActivity /*, LocationListener mLocList*/){
		
        mUpdateInterval = updateInterval;
		
		// Get the location manager
		locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the locatioin provider -> use
		// default
		criteria = new Criteria();
		
		provider = locationManager.getBestProvider(criteria, true);

		gul = new GeoUpdateListener();
		gpsul = new GPSUpdateListener();

		if(provider != null)
			locationManager.requestLocationUpdates(provider, 5000, 1, gul);
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, gpsul);

		float lastAcc = 1000000;
		for(String aProvider : locationManager.getAllProviders()){
			Location lastKnown = locationManager.getLastKnownLocation(aProvider);
			if(lastKnown != null){
				if(lastKnown.getAccuracy()<lastAcc){
					lat = lastKnown.getLatitude();
					lon = lastKnown.getLongitude();
					Log.d("Last Known Lat from: ", aProvider + Double.toString(lat));
					Log.d("Last Known Lon from: ", aProvider + Double.toString(lon));
				}
			}
		}
		if(lat == 0 || lon == 0) {
			lat = 39.952381111111111;
			lon = -75.19052611111111;
		}
				
		Log.d("My Location Timer: ", "Timer Start");

		if(onInterval < updateInterval){

			Timer geoUpdateTimer = new Timer();
			geoUpdateTimer.scheduleAtFixedRate(new GeoTimerStop(), initialUpdateInterval, updateInterval);
		
			Timer geoUpdateTimer2 = new Timer();
			geoUpdateTimer2.scheduleAtFixedRate(new GeoTimerStart(), updateInterval-onInterval+initialUpdateInterval, updateInterval);
		}
	}
	
	class GeoTimerStart extends TimerTask {
		public void run() {
			if(looper1){
				Looper.prepare();
				looper1 = false;
			}
			provider = locationManager.getBestProvider(criteria, true);
			if(provider!=null)
				locationManager.requestLocationUpdates(provider, 5000, 1, gul);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, gpsul);
	    }
	}
	
	class GeoTimerStop extends TimerTask {
		public void run() {
		    if(downloadSignalHandler != null){
	            Message updateMessage = Message.obtain();
	            updateMessage.what = LOC_UPDATE;
	            downloadSignalHandler.obtainMessage(LOC_UPDATE, null).sendToTarget();
		    }
			stopUpdates();
	    }
	  }
	
	public class GPSUpdateListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			lat = (location.getLatitude());
			lon = (location.getLongitude());
			gpsAcc = (location.getAccuracy());

			Log.d("Update lat from: ", "GPS "+ Double.toString(lat));
			Log.d("Update Lon from: ", "GPS " + Double.toString(lon));
			Log.d("Update Accuracy from: ", " GPS "+ Double.toString(location.getAccuracy()));

			if(gpsAcc <= otherAcc && gpsAcc < gpsTolerance){
				stopUpdates();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
}
	
	public class GeoUpdateListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			lat = (location.getLatitude());
			lon = (location.getLongitude());
			otherAcc = (location.getAccuracy());

			Log.d("Update lat from: ", provider+ Double.toString(lat));
			Log.d("Update Lon from: ", provider+ Double.toString(lon));
			Log.d("Update Accuracy from: ", provider+ Double.toString(location.getAccuracy()));
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
	
	public void stopUpdates(){
	    locationManager.removeUpdates(gul);
	    locationManager.removeUpdates(gpsul);

	    	
	}
	
	public static void go(){
		 if(downloadSignalHandler != null){
	            Message updateMessage = Message.obtain();
	            updateMessage.what = LOC_UPDATE;
	            downloadSignalHandler.obtainMessage(LOC_UPDATE, null).sendToTarget();
		    }
	}
	
	public static void setHandler(Handler downloadSignalHandler){
		MyLocation.downloadSignalHandler = downloadSignalHandler;
	}
}

