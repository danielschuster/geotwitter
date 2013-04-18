package de.tudresden.mobilis.android.geotwitter.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import org.jivesoftware.smack.util.Base64;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.mobilis.android.geotwitter.beans.Treasure;
import de.tudresden.mobilis.android.geotwitter.beans.TreasureContent;
import de.tudresden.mobilis.android.geotwitter.beans.createTreasureRequest;
import de.tudresden.mobilis.android.geotwitter.beans.createTreasureResponse;
import de.tudresden.mobilis.android.geotwitter.engine.Const;
import de.tudresden.mobilis.android.geotwitter.engine.Singleton;

public class CreateTreasureActivity extends Activity {

	Button buttonCreateTreasure;
	EditText editName;
	EditText editDescription;
	Singleton mSingleton;
	ImageView photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_treasure);
		mSingleton = Singleton.getInstance();
		mSingleton.mMXAController = MXAController.get();
		mSingleton.mMXAController.connectMXA(getApplication().getApplicationContext(), mSingleton);
		mSingleton.registerHandler(onReceived);
		UIInitialization();
	}

	private Handler onReceived = new Handler(){
		@Override
		public void handleMessage(Message msg){
			Log.i("CreateTreasureActivity", "Message came!" );
			if (msg.obj instanceof createTreasureResponse){
				createTreasureResponse response = (createTreasureResponse)msg.obj;
				switch(response.getErrortype()){
				case Const.TREASURE_SUBMISSION_FAIL:{
					Toast.makeText(getApplicationContext(), "Error submiting treasure!", Toast.LENGTH_SHORT).show();
					break;
				}
				case Const.TREASURE_SUBMISSION_SUCCESS:{
					Toast.makeText(getApplicationContext(), "Treasure has been posted!", Toast.LENGTH_SHORT).show();
					break;
				}
				default:{
					mSingleton.unRegisterHandler(onReceived);
					CreateTreasureActivity.this.finish();
				}
				
				}

			}
		}
	};

	public void UIInitialization(){
		buttonCreateTreasure = (Button)findViewById(R.id.button_CreateTreasure);
		editName = (EditText)findViewById(R.id.editName);
		editDescription = (EditText)findViewById(R.id.editText_Description);
		photo = (ImageView)findViewById(R.id.imageView_photo);
		buttonCreateTreasure.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!editName.getText().toString().equals("")&&!editDescription.getText().toString().equals("")){



					try {
						
						Bitmap bitmap = ((BitmapDrawable)photo.getDrawable()).getBitmap();
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
						byte[] bitmapdata = stream.toByteArray();
						String photoString = Base64.encodeBytes(bitmapdata);
						
						Treasure treasure = new Treasure();
						treasure.setAuthor(mSingleton.mMXAController.getXMPPService().getUsername());
						treasure.setName(editName.getText().toString());
						treasure.setDate(getDateData());
						treasure.setDescription(editDescription.getText().toString());
						TreasureContent content = new TreasureContent(0, photoString);
						createTreasureRequest request = new createTreasureRequest(treasure,content);
						mSingleton.OutStub.sendXMPPBean(request);

					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
	}
	
	public String getDateData(){
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		String date=today.format("%d.%m.%Y at %H:%M:%S");
		return date;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_create_treasure, menu);
		return true;
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

}
