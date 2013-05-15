package de.tudresden.mobilis.android.geotwitter.activities;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.RosterItems;
import de.tudresden.mobilis.android.geotwitter.beans.Treasure;
import de.tudresden.mobilis.android.geotwitter.beans.TreasureContent;
import de.tudresden.mobilis.android.geotwitter.beans.getTreasureContentRequest;
import de.tudresden.mobilis.android.geotwitter.beans.getTreasureContentResponse;
import de.tudresden.mobilis.android.geotwitter.engine.GeoTwitterManager;
import de.tudresden.mobilis.android.geotwitter.helper.CameraHelper;

/**
 * 
 * @author Marian Seliuchenko
 *
 */

public class ShowTreasureActivity extends Activity {

	ImageView image;
	TextView textView_Name;
	TextView textView_Date;
	TextView textView_Author;
	TextView textView_Description;
	TextView textView_Distance;
	TextView textView_Address;
	ImageView photo;
	ImageButton imageButton_Map;
	ImageButton imageButton_AuthorState;
	
	TreasureContent content;
	private Cursor cursor;
	String userMode;
	GeoTwitterManager mGeoTwitterManager;
	Treasure treasure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_treasure);
		mGeoTwitterManager = GeoTwitterManager.getInstance();

		if(savedInstanceState==null){
			Bundle extras = getIntent().getExtras();
			treasure = (Treasure) extras.get("TreasureSelected");
		}else{
			if(savedInstanceState.containsKey("treasure")){
				this.treasure = (Treasure)savedInstanceState.get("treasure");
			}
		}
		UIInitialization();
		TreasureContent tc = mGeoTwitterManager.openDatabase().getTreasureContent(treasure.getTreasureID());
		if(tc==null){
			mGeoTwitterManager.OutStub.sendXMPPBean(new getTreasureContentRequest(treasure.getTreasureID()));
		}else{
			refreshImage(tc.getContent());
		}
	}


	private Handler onReceivedShowTreasure = new Handler(){
		@Override
		public void handleMessage(Message msg){


			if (msg.obj instanceof getTreasureContentResponse){
				getTreasureContentResponse response = (getTreasureContentResponse)msg.obj;
				if(!response.getContent().getContent().equals("")){
					if(refreshImage(response.getContent().getContent())){
						mGeoTwitterManager.openDatabase().addTreasureContent(response.getContent());	
					}
				}
			}
			if(msg.obj instanceof String){
				if(msg.obj.equals("DatabaseUpdated")){
					textView_Distance.setText(mGeoTwitterManager.distanceCalculation(treasure));
				}
			}
		}
	};

	public boolean refreshImage(String str){
		try{
			image.setImageBitmap(CameraHelper.convertStringToBitmap(str));
			return true;
		}catch(Exception e){
			Toast.makeText(getApplicationContext(), "Unable to show picture!", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		return false;
	}


	public void UIInitialization(){
		image = (ImageView)findViewById(R.id.imageView_Content);
		textView_Name = (TextView)findViewById(R.id.textView_Name);
		textView_Date = (TextView)findViewById(R.id.textView_DateTD);
		textView_Author = (TextView)findViewById(R.id.textView_AuthorTD);
		textView_Description = (TextView)findViewById(R.id.textView_Description);
		textView_Distance = (TextView)findViewById(R.id.textView_Distance);
		imageButton_Map = (ImageButton)findViewById(R.id.imageButton_Map);
		textView_Name.setText(treasure.getName());
		String author = treasure.getAuthor();
		Log.i("ShowTreasure", author);
		textView_Author.setText(author);
		String date = treasure.getDate();
		Log.i("ShowTreasure", date);
		textView_Date.setText(date);
		textView_Description.setText(treasure.getDescription());
		if(mGeoTwitterManager.getCurrentLocation()!=null){
			textView_Distance.setText(mGeoTwitterManager.distanceCalculation(treasure));
		}else{
			textView_Distance.setText("Your current location is unavailable!");
		}
		textView_Address = (TextView)findViewById(R.id.textView_Address);
		textView_Address.setText(mGeoTwitterManager.ConvertPointToLocation(getApplicationContext(), 
				new LatLng(treasure.getLocation().getLatitude(),treasure.getLocation().getLongitude())));
		imageButton_AuthorState = (ImageButton)findViewById(R.id.ImageButton_AuthorState);
	imageButton_AuthorState.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(userMode.equals(RosterItems.MODE_AVAILABLE)){
					startActivity(new Intent(ShowTreasureActivity.this,ChatActivity.class).putExtra("partnerJID", "mobilis@marchello-pc/"+treasure.getAuthor()));
					finish();
				}else{
					Toast.makeText(getApplicationContext(), "User is not available at the moment!", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		imageButton_Map.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getActionMasked()==MotionEvent.ACTION_DOWN){
					imageButton_Map.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_map_transp_down));
				}
				if(event.getActionMasked()==MotionEvent.ACTION_UP){
					imageButton_Map.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_map_transp));
					Intent intent = new Intent(ShowTreasureActivity.this, MapActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("TreasureToPointOn", treasure);
					startActivity(intent);
					finish();
				}
				return true;
			}
		});
	}
	
	
	class UserStateObserver extends ContentObserver{
		public UserStateObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			onCursorChanged.sendEmptyMessage(0);

		}

	}

	private Handler onCursorChanged = new Handler(){
		@Override
		public void handleMessage(Message msg){
			refreshUserStateList(treasure.getAuthor());
		}
	};
	
	private void refreshUserStateList(String resource) {
		cursor= getContentResolver().query(RosterItems.CONTENT_URI,null, null,
				null, RosterItems.XMPP_ID+" ASC, "+RosterItems.RESSOURCE+" ASC");
				startManagingCursor(cursor);
		if(cursor!=null){
			if(cursor.moveToFirst()){
				do{
					if(cursor.getString(cursor.getColumnIndex(RosterItems.RESSOURCE)).equals(treasure.getAuthor())){
						Log.i("SHOW", "SPIVPALO: "+cursor.getString(cursor.getColumnIndex(RosterItems.RESSOURCE)));
						Log.i("SHOW", "SPIVPALO: "+cursor.getString(cursor.getColumnIndex(RosterItems.PRESENCE_MODE)));
						userMode=cursor.getString(cursor.getColumnIndex(RosterItems.PRESENCE_MODE));
						
						if (userMode.equals(RosterItems.MODE_UNAVAILABLE))
							imageButton_AuthorState.setImageResource(R.drawable.offline);
						else if (userMode.equals(RosterItems.MODE_AVAILABLE))
							imageButton_AuthorState.setImageResource(R.drawable.available);
						else if (userMode.equals(RosterItems.MODE_CHAT))
							imageButton_AuthorState.setImageResource(R.drawable.chat);
						else if (userMode.equals(RosterItems.MODE_AWAY))
							imageButton_AuthorState.setImageResource(R.drawable.away);
						else if (userMode.equals(RosterItems.MODE_DO_NOT_DISTURB))
							imageButton_AuthorState.setImageResource(R.drawable.busy);
						else if (userMode.equals(RosterItems.MODE_EXTENDED_AWAY))
							imageButton_AuthorState.setImageResource(R.drawable.extended_away);
						break;
					}else{
						userMode = RosterItems.MODE_UNAVAILABLE;
						imageButton_AuthorState.setImageResource(R.drawable.offline);
					}
				}while(cursor.moveToNext());
			}
		}
	}
	
	
	@Override
	public void onResume(){
		super.onResume();
		mGeoTwitterManager.registerHandler(onReceivedShowTreasure);
		refreshUserStateList(treasure.getAuthor());
		UserStateObserver mObserver = new UserStateObserver(onCursorChanged);
		getContentResolver().registerContentObserver(RosterItems.CONTENT_URI, true, mObserver);
		startManagingCursor(cursor);

	}

	@Override
	public void onPause(){
		super.onPause();
		mGeoTwitterManager.unRegisterHandler(onReceivedShowTreasure);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean("RestartedDueToConfigurationChanges", true);
		outState.putSerializable("treasure", treasure);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if ((keyCode == KeyEvent.KEYCODE_BACK))
		{
			mGeoTwitterManager.unRegisterHandler(onReceivedShowTreasure);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_show_treasure, menu);
		return true;
	}

}
