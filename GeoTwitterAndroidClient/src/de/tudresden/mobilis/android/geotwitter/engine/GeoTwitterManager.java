package de.tudresden.mobilis.android.geotwitter.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

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
import de.tudresden.mobilis.android.geotwitter.helper.ChatHelper;
import de.tudresden.mobilis.android.geotwitter.helper.DatabaseHelper;
import de.tudresden.mobilis.android.geotwitter.helper.Parser;

/**
 * GeoTwitterManager
 * @author Marian Seliuchenko
 * Email: egur2006@yandex.ru
 * Created 15.05.2013
 *
 */

public class GeoTwitterManager implements MXAListener {

	private static GeoTwitterManager instance;
	private GeoTwitterManager(){
		
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
	private static IXMPPService xmppS = null;
	private boolean isXMPPConnected = false;
	public static Location currentLocation;
	private static  DatabaseHelper db = null;
	public static ChatHelper chatHelper = null;

	public boolean isXMPPConnected(){
		if(isXMPPConnected==true){
			isXMPPConnected=false;
			return true;
		}
		return false;
	}
	
	public synchronized DatabaseHelper openDatabase(){
		if(db!=null)
			return db;
		return null;	
	}
	
	public synchronized ChatHelper getChat(){
		if(chatHelper!=null)
			return chatHelper;
		return null;	
	}
	
	
	public void createDatabase(Context context){
		db = new DatabaseHelper(context);
	}
	
	public void initChatHelper(String JID){
		chatHelper = new ChatHelper();
		chatHelper.setJID(JID);
		chatHelper.setNickName(JID.substring(21));
	}
	
	



	public static synchronized GeoTwitterManager getInstance(){
		if(instance==null){
			return new GeoTwitterManager();
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
				db.addTreasure(response.getTreasureList());
				Message m1 = new Message();
				m1.obj = new String("DatabaseUpdated");
				sendToAllHandlers(m1);
			}
			
		
		}
		
	
	};
	
	
	
	

	public IGeoTwitterServiceOutgoing OutStub = new IGeoTwitterServiceOutgoing(){

		public void sendXMPPBean(XMPPBean out) {
			try {
				Messenger ack = null;
				Messenger ack2 = null;
				out.setFrom(chatHelper.getJID());
				out.setTo("mobilis@marchello-pc/GeoTwitterService_v1#1");
				out.setType(XMPPBean.TYPE_SET);
				xmppS.sendIQ(ack, ack2, 1,Parser.beanToIQ(out, true));
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
	
	public ChatHelper getChatHelper(){
		return chatHelper;
	}

	private Handler xmppServiceConnectionHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			try {
				initChatHelper(getIXMPPService().getUsername());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			Message m = new Message();
			m.what = -88;
			sendToAllHandlers(m);
		}
	};

	
	public LatLng getCurrentLocation(){
		if(currentLocation!=null)
			return new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
		return null;
	}
	
	public String distanceCalculation(Treasure treasure){
		Location treasureLocation = new Location("TreasureLocation");		
		treasureLocation.setLatitude(treasure.getLocation().getLatitude());
		treasureLocation.setLongitude(treasure.getLocation().getLongitude());
		float calculated = currentLocation.distanceTo(treasureLocation);
		if(calculated<1000){
			return Math.round(calculated)+" m";
		}else{
			return String.format("%.2f", calculated/1000)+" km";
		}
	}

	public String ConvertPointToLocation(Context context, LatLng point) {   
	    String address = "";
	    Geocoder geoCoder = new Geocoder(
	        context, Locale.getDefault());
	    try {
	      List<Address> addresses = geoCoder.getFromLocation(
	        point.latitude, 
	        point.longitude, 1);
	 
	      if (addresses.size() > 0) {
	        for (int index = 0; 
		index < addresses.get(0).getMaxAddressLineIndex(); index++)
	          address += addresses.get(0).getAddressLine(index) + " ";
	      }
	    }
	    catch (IOException e) {        
	      e.printStackTrace();
	    }   
	    
	    return address;
	  } 
	
	

}
