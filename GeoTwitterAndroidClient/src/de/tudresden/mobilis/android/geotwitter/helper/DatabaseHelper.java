package de.tudresden.mobilis.android.geotwitter.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.tudresden.mobilis.android.geotwitter.beans.LocationBean;
import de.tudresden.mobilis.android.geotwitter.beans.Treasure;
import de.tudresden.mobilis.android.geotwitter.beans.TreasureContent;

/**
 * 
 * @author Marian Seliuchenko
 *
 */

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "TreasureManager";

	// Contacts table name
	private static final String TABLE_TREASURES = "treasures";
	private static final String TABLE_CONTENTS = "contents";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_AUTHOR = "author";
	private static final String KEY_DATE = "date";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_LONGITUDE = "longitude";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_CONTENT = "content";
	
	private static List<Treasure> tempTreasureList = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}



	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_TREASURES_TABLE = "CREATE TABLE IF NOT EXISTS " +TABLE_TREASURES + "("+
				KEY_ID + " INT NULL, " + 
				KEY_NAME + " TEXT, "+
				KEY_AUTHOR + " TEXT, " + 
				KEY_DATE + " TEXT, " + 
				KEY_DESCRIPTION + " TEXT, "+ 
				KEY_LONGITUDE + " TEXT, " + 
				KEY_LATITUDE + " TEXT, " + 
				" PRIMARY KEY ("+KEY_ID+") )";
		db.execSQL(CREATE_TREASURES_TABLE);

		String CREATE_CONTENTS_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_CONTENTS + "("+
				KEY_ID + " INT," + 
				KEY_CONTENT + " TEXT," +
				"PRIMARY KEY ("+KEY_ID+") )";
		db.execSQL(CREATE_CONTENTS_TABLE);
	//	db.close();

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TREASURES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENTS);
		onCreate(db);

	}

	public void addTreasure(Treasure treasure) {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "INSERT OR IGNORE INTO "+TABLE_TREASURES+"("+
				KEY_ID+","+
				KEY_NAME+","+
				KEY_AUTHOR+","+
				KEY_DATE+","+
				KEY_DESCRIPTION+","+
				KEY_LONGITUDE+","+
				KEY_LATITUDE+") VALUES ('"+
				treasure.getTreasureID()+"','"+
				treasure.getName()+"','"+
				treasure.getAuthor()+"','"+
				treasure.getDate()+"','"+
				treasure.getDescription()+"','"+
				String.valueOf(treasure.getLocation().getLongitude())+"','"+
				String.valueOf(treasure.getLocation().getLatitude())+"')";
		db.execSQL(query);

		db.close(); // Closing database connection
	}

	public void addTreasure(List<Treasure> treasureList) {

		tempTreasureList = treasureList;
		SQLiteDatabase db = this.getWritableDatabase();
		Iterator<Treasure> iterator = treasureList.iterator();
		Treasure treasure = null;
		while(iterator.hasNext()){
			treasure = iterator.next();
			this.addTreasure(treasure);

		}
		db.close(); // Closing database connection
	}
	
	public List<Treasure> getTreasuresList(){
		if (tempTreasureList!=null){
			return this.tempTreasureList;
		}else{
			return getAllTreasures();
		}
	}

	// Getting single contact
	public Treasure getTreasure(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_TREASURES, new String[] { KEY_ID,
				KEY_NAME, KEY_AUTHOR, KEY_DATE, KEY_DESCRIPTION, KEY_LONGITUDE, KEY_LATITUDE}, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Treasure treasure = new Treasure(
				cursor.getString(1),
				cursor.getString(2),
				cursor.getString(3),
				cursor.getString(4),
				new LocationBean(Double.parseDouble(cursor.getString(5)), Double.parseDouble(cursor.getString(6))),
				Integer.parseInt(cursor.getString(0)));
		cursor.close();
		db.close();
		return treasure;
	}

	// Getting All Contacts
	public List<Treasure> getAllTreasures() {
		List<Treasure> treasureList = new ArrayList<Treasure>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TREASURES;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Treasure treasure = new Treasure();
				treasure.setTreasureID(Integer.parseInt(cursor.getString(0)));
				treasure.setName(cursor.getString(1));
				treasure.setAuthor(cursor.getString(2));
				treasure.setDate(cursor.getString(3));
				treasure.setDescription(cursor.getString(4));
				treasure.setLocation(new LocationBean(Double.parseDouble(cursor.getString(5)), Double.parseDouble(cursor.getString(6))));
				// Adding contact to list
				treasureList.add(treasure);
			} while (cursor.moveToNext());
		}

		// return contact list
		cursor.close();
		db.close();
		return treasureList;
	}


	public void addTreasureContent(TreasureContent content) {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "INSERT OR IGNORE INTO "+TABLE_CONTENTS+"("+
				KEY_ID+","+
				KEY_CONTENT+") VALUES ('"+
				content.getTreasureID()+"','"+
				content.getContent()+"');";
		db.execSQL(query);
		db.close(); // Closing database connection
		
	}

	// Getting single contact
	public TreasureContent getTreasureContent(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM "+TABLE_CONTENTS+" WHERE "+KEY_ID+" = '"+String.valueOf(id)+"';";
		Cursor cursor = db.rawQuery(query, null);
		TreasureContent result = null;
		if(cursor.moveToFirst()){
			result = new TreasureContent(cursor.getInt(0), cursor.getString(1));
			Log.i("DB", "TREASURE CONTENT GOT!");
		}
		cursor.close();
		db.close();
		return result;
	}



	public void clearTables() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TREASURES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENTS);
		onCreate(db);
		db.close();

	}

}
