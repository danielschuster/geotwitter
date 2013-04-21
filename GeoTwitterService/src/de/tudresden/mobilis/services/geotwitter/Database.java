/**
 * @author MARCHELLO, Marian Seliuchenko, egur2006@yandex.ru
 * Class is a holder for HashMaps which are used instead of Database
 * QDB contains question objects mapped to their indexes
 * ADB contains answer list objects mapped to question indexes
 * UDB contains user objects mapped to their email addresses
 * JID contains jids(mapped to email addresses) of the users that are online
 */

package de.tudresden.mobilis.services.geotwitter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.helper.DoubleKeyMap;
import de.tudresden.mobilis.services.geotwitter.beans.LocationBean;
import de.tudresden.mobilis.services.geotwitter.beans.Treasure;
import de.tudresden.mobilis.services.geotwitter.beans.TreasureContent;



public class Database {
	
	public static final double MAX_LONGITUDE = 49.925;
	public static final double MIN_LONGITUDE = 49.750;
	public static final double MAX_LATITUDE = 24.201;
	public static final double MIN_LATITUDE = 23.850;
	public static final double STEP_LONGITUDE = 0.0045;
	public static final double STEP_LATITUDE = 0.007;



	private HashMap<Integer,Treasure> TDB =  new HashMap<Integer,Treasure>();
	private HashMap<Integer,TreasureContent> TCDB = new HashMap<Integer,TreasureContent>();
	private HashMap<String,User> ODB =  new HashMap<String,User>();

	public Database(){}

	public int addTreasure(Treasure treasure){

		try{
			Random r = new Random();
			int key;
			do{
				key = r.nextInt();
			}while(TDB.containsKey(key)||(key<0));
			treasure.setTreasureID(key);
			TDB.put(key, treasure);
			return key;
		}catch(Exception e){
			return -1;
		}	
	}
	
	public int addTreasureContent(TreasureContent content, int key){
			TCDB.put(key, content);
			return key;
		
	}

	public List<Treasure> getTreasureList(){
		
	/*	if((location.getLongitude()>MIN_LONGITUDE)&&(location.getLongitude()<MAX_LONGITUDE)
				&&(location.getLatitude()>MIN_LATITUDE)&&(location.getLatitude()<MAX_LATITUDE)){
			
		}*/
		List<Treasure> treasures = new ArrayList<Treasure>(TDB.values());
		return treasures;
	}
	
	
	public TreasureContent getTreasureContent(int key){
		if (TCDB.containsKey(key)){
			return TCDB.get(key);
		}
		return null;
	}

	public void clearOnlineUserList(){
		synchronized(ODB){
			Iterator<User> iterator = ODB.values().iterator();
			Date current = new Date();
			while(iterator.hasNext()){
				User user = iterator.next();
				if(current.after(user.time)){
					if(Math.abs(current.getMinutes()-user.time.getMinutes())>1){
						ODB.remove(user.jid);
					}

				}
			}
		}
	}
		
	public void addUserToOnlineList(String jid){
		synchronized(ODB){
			if(!ODB.containsKey(jid)){
				User user = new User();
				user.jid = jid;
				user.time = new Date();
				ODB.put(jid, user);
			}
		}
	}
	
	public List<User> getOnlineUserList(){
		synchronized(ODB){
			return new ArrayList<User>(ODB.values());
		}
		
	}


}
