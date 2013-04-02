package de.tudresden.mobilis.android.geotwitter.activities;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.mobilis.android.geotwitter.beans.Treasure;
import de.tudresden.mobilis.android.geotwitter.beans.createTreasureRequest;
import de.tudresden.mobilis.android.geotwitter.beans.createTreasureResponse;
import de.tudresden.mobilis.android.geotwitter.engine.Const;
import de.tudresden.mobilis.android.geotwitter.engine.Singleton;

public class CreateTreasureActivity extends Activity {

	Button buttonSubmit;
	Button buttonCancel;
	EditText editName;
	EditText editDescription;
	Singleton mSingleton;

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
				default:{
					mSingleton.unRegisterHandler(onReceived);
					CreateTreasureActivity.this.finish();
				}
				
				}

			}
		}
	};

	public void UIInitialization(){
		buttonSubmit = (Button)findViewById(R.id.buttonSubmit);
		buttonCancel = (Button)findViewById(R.id.buttonCancel);
		editName = (EditText)findViewById(R.id.editName);
		editDescription = (EditText)findViewById(R.id.editDescription);
		buttonSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!editName.getText().toString().equals("")&&!editDescription.getText().toString().equals("")){



					try {
						Treasure treasure = new Treasure();
						treasure.setAuthor(mSingleton.mMXAController.getXMPPService().getUsername());
						treasure.setName(editName.getText().toString());
						treasure.setDate(getDateData());
						treasure.setDescription(editDescription.getText().toString());
						createTreasureRequest request = new createTreasureRequest(treasure);
						mSingleton.OutStub.sendXMPPBean(request);

					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});

		buttonCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSingleton.unRegisterHandler(onReceived);
				finish();
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
