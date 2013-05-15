package de.tudresden.mobilis.android.geotwitter.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import de.tudresden.mobilis.android.geotwitter.beans.LocationBean;
import de.tudresden.mobilis.android.geotwitter.beans.updateLocation;
import de.tudresden.mobilis.android.geotwitter.engine.GeoTwitterManager;

/**
 * This class extends service if used for receiving broadcast notification messages
 * @author Marian Seliuchenko
 *
 */

public class GeoTwitterService extends Service implements LocationListener{

	NotificationManager nm = null;
	GeoTwitterManager mGeoTwitterManager = null;
	Timer timer = new Timer();


	@Override
	public void onLocationChanged(Location location) {
		mGeoTwitterManager.currentLocation = location;	
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i("OnProviderDisabled", "OnProviderDisabled");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i("onProviderEnabled", "onProviderEnabled");
	}

	@Override
	public void onStatusChanged(String provider, int status,
			Bundle extras) {
		Log.i("onStatusChanged", "onStatusChanged");

	}


	@Override
	public void onCreate() {
		super.onCreate();
		mGeoTwitterManager = GeoTwitterManager.getInstance();
		locationListenerInitialization();
	}

	public void locationListenerInitialization(){
		LocationManager aLocationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria c=new Criteria();
		c.setAccuracy(Criteria.ACCURACY_LOW);
		String providerName=aLocationManager.getBestProvider(c,true);
		aLocationManager.requestLocationUpdates(providerName, 0, 0, this);
		mGeoTwitterManager.currentLocation = aLocationManager.getLastKnownLocation(providerName);
		
		TimerTask firstTask = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

					if(mGeoTwitterManager.currentLocation!=null){
					LocationBean bean = new LocationBean();
					bean.setLatitude(mGeoTwitterManager.currentLocation.getLatitude());
					bean.setLongitude(mGeoTwitterManager.currentLocation.getLongitude());
					Log.i("Service", String.valueOf(mGeoTwitterManager.currentLocation.getLatitude()));
					Log.i("Service", String.valueOf(mGeoTwitterManager.currentLocation.getLongitude()));
					mGeoTwitterManager.OutStub.sendXMPPBean(new updateLocation(bean));
				}

			}
		};
		timer.schedule(firstTask, 0,5000);

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		try{
			timer.cancel();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}


}
