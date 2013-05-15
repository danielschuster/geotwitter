package de.tudresden.mobilis.android.geotwitter.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import de.tudresden.mobilis.android.geotwitter.beans.LocationBean;
import de.tudresden.mobilis.android.geotwitter.beans.Treasure;
import de.tudresden.mobilis.android.geotwitter.beans.TreasureContent;
import de.tudresden.mobilis.android.geotwitter.beans.createTreasureRequest;
import de.tudresden.mobilis.android.geotwitter.beans.createTreasureResponse;
import de.tudresden.mobilis.android.geotwitter.engine.GeoTwitterManager;
import de.tudresden.mobilis.android.geotwitter.helper.CameraHelper;
import de.tudresden.mobilis.android.geotwitter.utils.Constants;

/**
 * 
 * @author Marian Seliuchenko
 *
 */

public class CreateTreasureActivity extends Activity {

	private static final int CAMERA_REQUEST = 1888; 

	Button buttonCreateTreasure;
	ImageButton imageButton_CreateTreasure;
	ImageButton imageButton_Cancel;
	EditText editName;
	EditText editDescription;
	ImageView imageView_Photocamera;
	
	Bitmap photoFromCamera = null;
	CameraHelper cameraHandler = null;
	GeoTwitterManager mGeoTwitterManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_createtreasure);
		mGeoTwitterManager = GeoTwitterManager.getInstance();	
		cameraHandler = new CameraHelper();
		if(savedInstanceState!=null){
			if(savedInstanceState.containsKey("photoFormCamera")){
				photoFromCamera = (Bitmap)savedInstanceState.getParcelable("photoFormCamera");
				imageView_Photocamera.setImageBitmap(photoFromCamera);
			}
		}
		UIInitialization();
	}

	private Handler onReceived = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if (msg.obj instanceof createTreasureResponse){
				createTreasureResponse response = (createTreasureResponse)msg.obj;
				switch(response.getErrortype()){
				case Constants.TREASURE_SUBMISSION_FAIL:{
					Toast.makeText(getApplicationContext(), "Error submiting treasure!", Toast.LENGTH_SHORT).show();
					break;
				}
				case Constants.TREASURE_SUBMISSION_SUCCESS:{
					Toast.makeText(getApplicationContext(), "Treasure has been posted!", Toast.LENGTH_SHORT).show();
					break;
				}
				default:{
					mGeoTwitterManager.unRegisterHandler(onReceived);
					CreateTreasureActivity.this.finish();
				}

				}

			}
		}
	};
	
	


	public void UIInitialization(){
		editName = (EditText)findViewById(R.id.editName);
		editDescription = (EditText)findViewById(R.id.editText_Description);
		imageView_Photocamera = (ImageView)findViewById(R.id.imageView_Photocamera);
		registerForContextMenu(imageView_Photocamera);
		imageButton_CreateTreasure = (ImageButton)findViewById(R.id.imageButton_CreateTreasure);
		imageButton_CreateTreasure.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!editName.getText().toString().equals("")){ 
					String photoFromCameraString = "";
					if(photoFromCamera==null){
						photoFromCameraString = "";
					}else{
						photoFromCameraString = cameraHandler.convertBitmapToString(photoFromCamera);
					}

					Treasure treasure = new Treasure();
					treasure.setAuthor(mGeoTwitterManager.getChatHelper().getNickName());
					treasure.setName(editName.getText().toString());
					treasure.setDate(getDateData());
					treasure.setDescription(editDescription.getText().toString());
					treasure.setLocation(new LocationBean(mGeoTwitterManager.currentLocation.getLongitude(),mGeoTwitterManager.currentLocation.getLatitude()));
					TreasureContent content = new TreasureContent(0, photoFromCameraString);
					createTreasureRequest request = new createTreasureRequest(treasure,content);
					mGeoTwitterManager.OutStub.sendXMPPBean(request);
					
				}else{
					Toast.makeText(getApplicationContext(), "Please give Your treasure a name!", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		photoFromCamera = cameraHandler.setPic(imageView_Photocamera);
		if(photoFromCamera!=null){
			imageView_Photocamera.setImageBitmap(photoFromCamera);
		}
		
	}

	public String getDateData(){
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		String date=today.format("%d.%m.%Y at %H:%M:%S");
		return date;
	}
	
	@Override  
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
		super.onCreateContextMenu(menu, v, menuInfo);  
		menu.setHeaderTitle("What to do with image...");  
		menu.add(0, v.getId(), 0, "Capture new");  
		menu.add(0, v.getId(), 0, "Remove");  
	}

	@Override  
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getTitle().equals("Capture new")){
			startActivityForResult(cameraHandler.prepareTakePictureIntent(), CAMERA_REQUEST);
		}
		if(item.getTitle().equals("Remove")){
			imageView_Photocamera.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.photocamera));
		}
		return true;  
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
			mGeoTwitterManager.unRegisterHandler(onReceived);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(photoFromCamera!=null){
			outState.putParcelable("photoFromCamera", photoFromCamera);
		}
	};
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mGeoTwitterManager.registerHandler(onReceived);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mGeoTwitterManager.unRegisterHandler(onReceived);
	}

}
