package de.tudresden.mobilis.android.geotwitter.engine;

import java.util.ArrayList;
import java.util.List;

import de.tudresden.mobilis.android.geotwitter.beans.LocationBean;
import de.tudresden.mobilis.android.geotwitter.beans.Treasure;
import de.tudresden.mobilis.android.geotwitter.beans.TreasureContent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	
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
    
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_TREASURES_TABLE = "CREATE TABLE " + TABLE_TREASURES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_AUTHOR + " TEXT," + KEY_DATE + " TEXT," + KEY_DESCRIPTION + " TEXT," 
                + KEY_LONGITUDE + " TEXT," + KEY_LATITUDE + " TEXT," + ")";
        db.execSQL(CREATE_TREASURES_TABLE);
        
        String CREATE_CONTENTS_TABLE = "CREATE TABLE " + TABLE_CONTENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CONTENT + " TEXT," + ")";
        db.execSQL(CREATE_CONTENTS_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TREASURES);
        // Create tables again
        onCreate(db);
		
	}
	
	public void addTreasure(Treasure treasure) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_ID, treasure.getTreasureID());
	    values.put(KEY_NAME, treasure.getName()); // Contact Name
	    values.put(KEY_AUTHOR, treasure.getAuthor()); // Contact Phone Number
	    values.put(KEY_DATE, treasure.getDate());
	    values.put(KEY_DESCRIPTION, treasure.getDescription());
	    values.put(KEY_LONGITUDE, String.valueOf(treasure.getLocation().getLongitude()));
	    values.put(KEY_LATITUDE, String.valueOf(treasure.getLocation().getLatitude()));
	    // Inserting Row
	    db.insert(TABLE_TREASURES, null, values);
	    db.close(); // Closing database connection
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
	 
	    SQLiteDatabase db = this.getWritableDatabase();
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
	
	public void clearTreasures(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TREASURES, null, null);
	}
	 
	// Getting contacts Count
	public int getTreasuresCount() {
		String countQuery = "SELECT  * FROM " + TABLE_TREASURES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
	}
	/*// Updating single contact
	public int updateContact(Treasure treasure) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_ID, treasure.getTreasureID());
	    values.put(KEY_NAME, treasure.getName()); // Contact Name
	    values.put(KEY_AUTHOR, treasure.getAuthor()); // Contact Phone Number
	    values.put(KEY_DATE, treasure.getDate());
	    values.put(KEY_DESCRIPTION, treasure.getDescription());
	    values.put(KEY_LONGITUDE, treasure.getLocation().getLongitude());
	    values.put(KEY_LATITUDE, treasure.getLocation().getLatitude());
	    // updating row
	    return db.update(TABLE_TREASURES, values, KEY_ID + " = ?",
	            new String[] { String.valueOf(treasure.getTreasureID()) });
	}
	 
	/*Deleting single contact
	public void deleteTreasure(Treasure treasure) {
		SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_TREASURES, KEY_ID + " = ?",
	            new String[] { String.valueOf(contact.getID()) });
	    db.close();
	}*/
	
	
	public void addTreasureContent(TreasureContent content) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_ID, content.getTreasureID());
	    values.put(KEY_CONTENT, content.getContent()); // Contact Name
	    // Inserting Row
	    db.insert(TABLE_CONTENTS, null, values);
	    db.close(); // Closing database connection
	}
	 
	// Getting single contact
	public TreasureContent getTreasureContent(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		 
	    Cursor cursor = db.query(TABLE_CONTENTS, new String[] { KEY_ID,
	            KEY_CONTENT}, KEY_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	    TreasureContent content = new TreasureContent(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
	    cursor.close();
	    db.close();
	    return content;
	}
	 
	public void clearTreasureContents(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONTENTS, null, null);
	}
}
