package de.tudresden.mobilis.android.geotwitter.activities;


import org.jivesoftware.smack.util.Base64;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.tudresden.mobilis.android.geotwitter.beans.Treasure;
import de.tudresden.mobilis.android.geotwitter.beans.TreasureContent;
import de.tudresden.mobilis.android.geotwitter.beans.getTreasureContentRequest;
import de.tudresden.mobilis.android.geotwitter.beans.getTreasureContentResponse;
import de.tudresden.mobilis.android.geotwitter.engine.Singleton;

public class ShowTreasureActivity extends Activity {

	ImageView image;
	TextView textView_Name;
	TextView textView_Date;
	TextView textView_Author;
	TextView textView_Description;
	ImageView photo;
	TreasureContent content;



	Singleton mSingleton;
	Treasure treasure;
	ActionBar ab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_treasure);
		mSingleton = Singleton.getInstance();
		mSingleton.registerHandler(onReceived);
		Bundle extras = getIntent().getExtras();
		treasure = (Treasure) extras.get("TreasureSelected");
		UIInitialization();
		content = mSingleton.db.getTreasureContent(treasure.getTreasureID());
		if(content==null){
			mSingleton.OutStub.sendXMPPBean(new getTreasureContentRequest(treasure.getTreasureID()));
		}else{
			refreshTreasureContent();
		}

	}

	private Handler onReceived = new Handler(){
		@Override
		public void handleMessage(Message msg){
			Log.i("ShowTreasureActivity", "Message came!" );

			if (msg.obj instanceof getTreasureContentResponse){
				Log.i("ShowTreasureActivity", "Message is response!" );
				Log.i("ShowTreasureActivity","Animation canceled!" );
				getTreasureContentResponse response = (getTreasureContentResponse)msg.obj;
				try{
					content = response.getContent();
					refreshTreasureContent();
				}catch(Exception e){
					Toast.makeText(getApplicationContext(), "Unable to download picture!", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	public void refreshTreasureContent(){
		byte[] bitmaparray = Base64.decode(content.getContent());
		photo.setImageDrawable(getDrawableFromBytes(bitmaparray));
	}

	public Drawable getDrawableFromBytes(byte[] imageBytes) {

		if (imageBytes != null)
			return new BitmapDrawable(BitmapFactory.decodeByteArray(imageBytes,
					0, imageBytes.length));
		else
			return null;
	}

	public void UIInitialization(){
		image = (ImageView)findViewById(R.id.imageView_Content);
		textView_Name = (TextView)findViewById(R.id.textView_Name);
		textView_Date = (TextView)findViewById(R.id.textView_Date);
		textView_Author = (TextView)findViewById(R.id.textView_Author);
		textView_Description = (TextView)findViewById(R.id.textView_Description);
		textView_Name.setText(treasure.getName());
		textView_Author.setText(treasure.getAuthor());
		textView_Date.setText(treasure.getDate());
		textView_Description.setText(treasure.getDescription());
		/*	
		ab = getActionBar();
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayShowTitleEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		ab.setHomeButtonEnabled(true);
		ab.setIcon(R.drawable.ic_treasure);
		ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_background));
		ab.setTitle("Treasure details");
		 */	
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if ((keyCode == KeyEvent.KEYCODE_BACK))
		{
			mSingleton.unRegisterHandler(onReceived);
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
