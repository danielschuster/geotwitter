package de.tudresden.mobilis.android.geotwitter.engine;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.location.Geocoder;
import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IXMPPIQCallback;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPIQ;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.mobilis.android.geotwitter.beans.IGeoTwitterServiceOutgoing;
import de.tudresden.mobilis.android.geotwitter.beans.Treasure;
import de.tudresden.mobilis.android.geotwitter.beans.createTreasureResponse;
import de.tudresden.mobilis.android.geotwitter.beans.getTreasureContentResponse;
import de.tudresden.mobilis.android.geotwitter.beans.sendTreasureList;

public class Singleton implements MXAListener {

	private static Singleton instance;
	private Singleton(){
		
	}

	private static ArrayList<Handler> handlerList = new ArrayList<Handler>();

	public void registerHandler(Handler handler){
		if(!handlerList.contains(handler)){
			handlerList.add(handler);
		}
	}

	public void unRegisterHandler(Handler handler){
		if(handlerList.contains(handler)){
			handlerList.remove(handler);
		}
	}

	public void sendToAllHandlers(Message msg){
		Iterator<Handler> it = handlerList.iterator();
		while (it.hasNext()){
			Handler h = it.next(); 
			h.sendMessage(msg);
			Log.i("Singleton", h.toString());
		}
	}


	public static MXAController mMXAController = null;
	public static IXMPPService xmppS = null;
	public static IXMPPService xmppC = null;
	private boolean isXMPPConnected = false;
	public static Location currentLocation;
	private static  DatabaseHandler db = null;

	public boolean isXMPPConnected(){
		if(isXMPPConnected==true){
			isXMPPConnected=false;
			return true;
		}
		return false;
	}
	
	public synchronized DatabaseHandler openDatabase(){
		if(db!=null)
			return db;
		return null;	
	}
	
	public void createDatabase(Context context){
		db = new DatabaseHandler(context);
	}
	
	public LatLng getCurrentLocation(){
		if(currentLocation!=null)
			return new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
		return null;
	}



	public static synchronized Singleton getInstance(){
		if(instance==null){
			return new Singleton();
		}
		return instance;
	}

	public IXMPPService getIXMPPService(){
		return xmppS;
	}





	public IXMPPIQCallback IQCallback = new IXMPPIQCallback.Stub(){

		public void processIQ(XMPPIQ arg0) throws RemoteException {
			// TODO Auto-generated method stub
			Log.i("Singleton.Callback", "IQ to bean starting...");
			XMPPBean b = Parser.convertXMPPIQToBean(arg0);




			if(b instanceof createTreasureResponse){
				createTreasureResponse response = (createTreasureResponse)b;
				Message m1 = new Message();
				m1.obj = response;
				sendToAllHandlers(m1);
			}


			if(b instanceof getTreasureContentResponse){
				getTreasureContentResponse response = (getTreasureContentResponse)b;
				Log.i("Singleton!!!!", response.getContent().getContent());
				Message m1 = new Message();
				m1.obj = response;
				sendToAllHandlers(m1);
				
			}


			if(b instanceof sendTreasureList){
				sendTreasureList response = (sendTreasureList)b;
				db.clearTreasures();
				for(Treasure treasure:response.getTreasureList()){
					db.addTreasure(treasure);
				}
				Message m1 = new Message();
				m1.obj = new String("DatabaseUpdated");
				sendToAllHandlers(m1);
			}
			
		
		}
		
	
	};
	
	public String distanceCalculation(Treasure treasure){
		Location treasureLocation = new Location("TreasureLocation");
		treasureLocation.setLatitude(treasure.getLocation().getLatitude());
		treasureLocation.setLongitude(treasure.getLocation().getLongitude());
		float calculated = currentLocation.distanceTo(treasureLocation);
	//	int meters = Math.round(calculated);
		if(calculated<1000){
			return Math.round(calculated)+" m";
		}else{
			return String.format("%.2f", calculated/1000)+" km";
		}
	
	}
	
	

	public IGeoTwitterServiceOutgoing OutStub = new IGeoTwitterServiceOutgoing(){

		public void sendXMPPBean(XMPPBean out) {
			// TODO Auto-generated method stub
			try {
				Messenger ack = null;
				Messenger ack2 = null;
				out.setFrom("mobilis@marchello-pc/Android");
				out.setTo("mobilis@marchello-pc/GeoTwitterService_v1#1");
				out.setType(XMPPBean.TYPE_SET);
				Log.i("Singleton", "Sending out...");
				if(xmppS==null){
					Log.i("Singleton", "xmppS = NULL");	
				}
				xmppS.sendIQ(ack, ack2, 1,Parser.beanToIQ(out, true));
				Log.i("Singleton", "Sent!");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	};




	public void onMXAConnected() {
		// TODO Auto-generated method stub
		xmppS = mMXAController.getXMPPService();
		
		
		try{
			xmppS.connect(new Messenger(xmppServiceConnectionHandler));
			registerCallbacks();
			Message m = new Message();
			m.what = -99;
			sendToAllHandlers(m);

		}catch(RemoteException e){
			e.printStackTrace();
		}

	}

	public void registerCallbacks(){
		try{

			//write equal lines for all beans you want to receive
			xmppS.registerIQCallback(IQCallback, createTreasureResponse.CHILD_ELEMENT, createTreasureResponse.NAMESPACE);
			xmppS.registerIQCallback(IQCallback, getTreasureContentResponse.CHILD_ELEMENT, getTreasureContentResponse.NAMESPACE);
			xmppS.registerIQCallback(IQCallback, sendTreasureList.CHILD_ELEMENT, sendTreasureList.NAMESPACE);
			Log.i("Singleton", "Callback registered!");
		}
		catch(Exception e)
		{

		}

	}

	public void onMXADisconnected() {
		Message m = new Message();
		m.what = -100;
		sendToAllHandlers(m);
		Log.i("Singleton", "DISCONNECTED!");
	}

	private Handler xmppServiceConnectionHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			Message m = new Message();
			m.what = -88;
			sendToAllHandlers(m);
		}
	};
	
	

}
