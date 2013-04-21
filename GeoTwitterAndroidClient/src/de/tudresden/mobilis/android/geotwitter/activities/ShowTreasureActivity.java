package de.tudresden.mobilis.android.geotwitter.activities;


import java.io.UnsupportedEncodingException;

import org.jivesoftware.smack.util.Base64;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
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
	TextView textView_Distance;
	ImageView photo;
	ImageButton imageButton_Map;
	TreasureContent content;



	Singleton mSingleton;
	Treasure treasure;
	ActionBar ab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_treasure);
		mSingleton = Singleton.getInstance();
	//	mSingleton.mMXAController = MXAController.get();
	//	mSingleton.mMXAController.connectMXA(getApplication().getApplicationContext(), mSingleton);
		Bundle extras = getIntent().getExtras();
		treasure = (Treasure) extras.get("TreasureSelected");
		UIInitialization();
	//	content = mSingleton.db.getTreasureContent(treasure.getTreasureID());
	//	if(content==null){
			
	//	}else{
	//		refreshTreasureContent();
	//	}

	}
	
	@Override
	public void onResume(){
		super.onResume();
		mSingleton.registerHandler(onReceivedShowTreasure);
		mSingleton.OutStub.sendXMPPBean(new getTreasureContentRequest(treasure.getTreasureID()));
	}
	
	@Override
	public void onPause(){
		super.onPause();
		mSingleton.unRegisterHandler(onReceivedShowTreasure);
	}

	private Handler onReceivedShowTreasure = new Handler(){
		@Override
		public void handleMessage(Message msg){
			Log.i("ShowTreasureActivity", "Message came!" );

			if (msg.obj instanceof getTreasureContentResponse){
				Log.i("ShowTreasureActivity", "Message is response!" );
				Log.i("ShowTreasureActivity","Animation canceled!" );
				getTreasureContentResponse response = (getTreasureContentResponse)msg.obj;
				try{
				/*	byte[] byteArray1;;
					try {
						byteArray1 = response.getContent().getContent().getBytes("ISO-8859-1");
						Bitmap bmp1 = BitmapFactory.decodeByteArray(byteArray1, 0, byteArray1.length);
						photo.setImageBitmap(bmp1);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					Toast.makeText(getApplicationContext(), response.getContent().getContent(), Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					Toast.makeText(getApplicationContext(), "Unable to download picture!", Toast.LENGTH_SHORT).show();
				}
			}
			if(msg.obj instanceof String){
				if(msg.obj.equals("DatabaseUpdated")){
					textView_Distance.setText(mSingleton.distanceCalculation(treasure));
				}
			}
		}
	};

	
	public void UIInitialization(){
		image = (ImageView)findViewById(R.id.imageView_Content);
		textView_Name = (TextView)findViewById(R.id.textView_Name);
		textView_Date = (TextView)findViewById(R.id.textView_Date);
		textView_Author = (TextView)findViewById(R.id.textView_Author);
		textView_Description = (TextView)findViewById(R.id.textView_Description);
		textView_Distance = (TextView)findViewById(R.id.textView_Distance);
		imageButton_Map = (ImageButton)findViewById(R.id.imageButton_Map);
		textView_Name.setText(treasure.getName());
		textView_Author.setText(treasure.getAuthor());
		textView_Date.setText(treasure.getDate());
		textView_Description.setText(treasure.getDescription());
		textView_Distance.setText(mSingleton.distanceCalculation(treasure));
		
		imageButton_Map.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ShowTreasureActivity.this, MapActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("TreasureToPointOn", treasure);
				startActivity(intent);
				finish();
			}
		});
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
			mSingleton.unRegisterHandler(onReceivedShowTreasure);
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
