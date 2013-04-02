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
import java.util.HashMap;
import java.util.List;

import de.tudresden.mobilis.services.geotwitter.beans.Treasure;



public class Database {
	

	
	public static HashMap<Integer,Treasure> TDB =  new HashMap<Integer,Treasure>();
//	public static HashMap<String,String> ContentDB =  new HashMap<String,String>();
	public static HashMap<String,User> ODB =  new HashMap<String,User>();
	
	
	
	
	
	

	

}
