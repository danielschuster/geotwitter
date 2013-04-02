package de.tudresden.mobilis.android.geotwitter.activities;


import android.app.Activity;
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
import de.tudresden.mobilis.android.geotwitter.beans.getTreasureContentRequest;
import de.tudresden.mobilis.android.geotwitter.beans.getTreasureContentResponse;
import de.tudresden.mobilis.android.geotwitter.engine.Singleton;

public class ShowTreasureActivity extends Activity {
	
	ImageView image;
	TextView textView_Name;
	TextView textView_Date;
	TextView textView_Author;
	
	Singleton mSingleton;
	Treasure treasure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_treasure);
		mSingleton = Singleton.getInstance();
		mSingleton.registerHandler(onReceived);
		Bundle extras = getIntent().getExtras();
		treasure = (Treasure) extras.get("TreasureSelected");
		UIInitialization();
		mSingleton.OutStub.sendXMPPBean(new getTreasureContentRequest(treasure.getDatabaseID()));
	}
	
	private Handler onReceived = new Handler(){
		@Override
		public void handleMessage(Message msg){
			Log.i("ShowTreasureActivity", "Message came!" );

			if (msg.obj instanceof getTreasureContentResponse){
				Log.i("ShowTreasureActivity", "Message is response!" );
				Log.i("ShowTreasureActivity","Animation canceled!" );
				getTreasureContentResponse response = (getTreasureContentResponse)msg.obj;
				Toast.makeText(getApplicationContext(), "Content donwloaded successfully!", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	public void UIInitialization(){
		image = (ImageView)findViewById(R.id.imageView_Content);
		textView_Name = (TextView)findViewById(R.id.textView_Name);
		textView_Date = (TextView)findViewById(R.id.textView_Date);
		textView_Author = (TextView)findViewById(R.id.textView_Author);
		textView_Name.setText(treasure.getName());
		textView_Author.setText(treasure.getAuthor());
		textView_Date.setText(treasure.getDate());
		
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
