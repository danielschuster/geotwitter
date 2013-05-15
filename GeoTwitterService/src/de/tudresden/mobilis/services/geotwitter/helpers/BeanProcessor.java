package de.tudresden.mobilis.services.geotwitter.helpers;

import de.tudresden.mobilis.services.geotwitter.GeoTwitter;
import de.tudresden.mobilis.services.geotwitter.beans.TreasureContent;
import de.tudresden.mobilis.services.geotwitter.beans.createTreasureRequest;
import de.tudresden.mobilis.services.geotwitter.beans.createTreasureResponse;
import de.tudresden.mobilis.services.geotwitter.beans.getTreasureContentRequest;
import de.tudresden.mobilis.services.geotwitter.beans.getTreasureContentResponse;
import de.tudresden.mobilis.services.geotwitter.beans.sendTreasureList;
import de.tudresden.mobilis.services.geotwitter.beans.updateLocation;

/**
 * 
 * @author Marian Seliuchenko
 *
 */

public class BeanProcessor {

	public BeanProcessor(GeoTwitter geoTwitter){
		this.geoTwitter = geoTwitter;
		this.DB = geoTwitter.getDB();
	}

	GeoTwitter geoTwitter;
	SqlHelper DB;



	public createTreasureResponse processCreateTreasureRequest(createTreasureRequest bean){

		int response = DB.addTreasure(bean.getTreasure());
		TreasureContent tc = bean.getContent();
		tc.setTreasureID(response);
		DB.addTreasureContent(tc);
		System.out.println("INSERT TREASURE: " + String.valueOf(response));
		return new createTreasureResponse(response);
	}


	public getTreasureContentResponse processgetTreasureContentRequest(getTreasureContentRequest request) {
		return new getTreasureContentResponse(DB.getTreasureContent(request.getTreasureID()));

	}

	public sendTreasureList processUpdateLocation(updateLocation request) {
		return new sendTreasureList(DB.getTreasureList());
	}



}
