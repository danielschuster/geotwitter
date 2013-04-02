package de.tudresden.mobilis.services.geotwitter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.tudresden.mobilis.services.geotwitter.beans.*;

public class BeanProcessor {

	public BeanProcessor(){}

	public createTreasureResponse processCreateTreasureRequest(createTreasureRequest bean){
		
		Random r = new Random();
		int key;
		do{
			key = r.nextInt();
		}while(Database.TDB.containsKey(key)||(key<0));
		Treasure treasure = bean.getTreasure();
		treasure.setDatabaseID(key);
		Database.TDB.put(key, treasure);
		return new createTreasureResponse(key);
	}

	public getTreasureContentResponse processgetTreasureContentRequest(getTreasureContentRequest request) {
		

			return new getTreasureContentResponse();
		
		
	}

	public sendTreasureList processUpdateLocation(updateLocation request) {
		
		
		List<Treasure> treasures = new ArrayList<Treasure>(Database.TDB.values());
		return new sendTreasureList(treasures);
	}

}
