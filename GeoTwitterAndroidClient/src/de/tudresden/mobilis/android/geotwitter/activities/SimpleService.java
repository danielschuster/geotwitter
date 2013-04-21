package de.tudresden.mobilis.android.geotwitter.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IXMPPIQCallback;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPIQ;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.mobilis.android.geotwitter.beans.LocationBean;
import de.tudresden.mobilis.android.geotwitter.beans.Treasure;
import de.tudresden.mobilis.android.geotwitter.beans.pushNewTreasure;
import de.tudresden.mobilis.android.geotwitter.beans.updateLocation;
import de.tudresden.mobilis.android.geotwitter.engine.DatabaseHandler;
import de.tudresden.mobilis.android.geotwitter.engine.Parser;
import de.tudresden.mobilis.android.geotwitter.engine.Singleton;

/**
 * This class extends service if used for receiving broadcast notification messages
 * @author MARCHELLO
 *
 */

public class SimpleService extends Service implements LocationListener, MXAListener{

	NotificationManager nm = null;
	Singleton mSingleton = null;
	Timer timer = new Timer();


	@Override
	public void onLocationChanged(Location location) {
		mSingleton.currentLocation = location;	

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
		mSingleton = Singleton.getInstance();
	//	mSingleton.mMXAController = MXAController.get();
	//	mSingleton.mMXAController.connectMXA(getApplication().getBaseContext(), this);

		locationListenerInitialization();
	}

	public void locationListenerInitialization(){
		LocationManager aLocationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria c=new Criteria();
		c.setAccuracy(Criteria.ACCURACY_LOW);
		String providerName=aLocationManager.getBestProvider(c,true);
		aLocationManager.requestLocationUpdates(providerName, 0, 0, this);
		mSingleton.currentLocation = aLocationManager.getLastKnownLocation(providerName);
		TimerTask firstTask = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

					if(mSingleton.currentLocation!=null){
				
					LocationBean bean = new LocationBean();
										bean.setLatitude(mSingleton.currentLocation.getLatitude());
										bean.setLongitude(mSingleton.currentLocation.getLongitude());
					//bean.setLatitude(1.0);
					//bean.setLongitude(1.0);
					mSingleton.OutStub.sendXMPPBean(new updateLocation(bean));
				}

			}
		};
		timer.schedule(firstTask, 0,5000);

	}



	public IXMPPIQCallback IQCallback = new IXMPPIQCallback.Stub(){

		public void processIQ(XMPPIQ arg0) throws RemoteException {
			// TODO Auto-generated method stub
			XMPPBean b = Parser.convertXMPPIQToBean(arg0);
			if(b instanceof pushNewTreasure){
				pushNewTreasure response = (pushNewTreasure)b;
				generateNotification(response.getTreasure());
			}
		}
	};


	/**
	 * Generates notification depending on kind of received bean
	 * @param b
	 */
	private void generateNotification(Treasure treasure) {
		int icon = R.drawable.ic_stat_gcm;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		String title = "Auditorium Notification";
		String message = treasure.getName();
		Notification notification = new Notification(icon, message, when);
		Intent notificationIntent = new Intent(getApplicationContext(), ShowTreasureActivity.class);
		notificationIntent.putExtra("TreasureSelected", treasure);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
				Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent =
				PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
		notification.setLatestEventInfo(getApplicationContext(), title, message , intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify("OK",0, notification);

	}

	@Override
	public void onMXAConnected() {
		// TODO Auto-generated method stub
		try{
			mSingleton.getIXMPPService().registerIQCallback(IQCallback, pushNewTreasure.CHILD_ELEMENT, pushNewTreasure.NAMESPACE);
			Toast.makeText(getApplicationContext(), "MXA connected!", Toast.LENGTH_SHORT).show();
		}
		catch(Exception e)
		{

		}


	}

	@Override
	public void onMXADisconnected() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "MXA disconnected!", Toast.LENGTH_SHORT).show();

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
