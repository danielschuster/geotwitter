package de.tudresden.mobilis.services.geotwitter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.tudresden.mobilis.services.geotwitter.beans.*;

public class BeanProcessor {

	public BeanProcessor(Database db){
		this.DB = db;
	}
	
	Database DB;
	
	

	public createTreasureResponse processCreateTreasureRequest(createTreasureRequest bean){
			
		int response = DB.addTreasure(bean.getTreasure());
		DB.addTreasureContent(bean.getContent(), response);
		return new createTreasureResponse(response);
	}
	

	public getTreasureContentResponse processgetTreasureContentRequest(getTreasureContentRequest request) {
			return new getTreasureContentResponse(DB.getTreasureContent(request.getTreasureID()));
	}

	public sendTreasureList processUpdateLocation(updateLocation request) {
		return new sendTreasureList(DB.getTreasureList());
	}

}
