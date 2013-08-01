package de.tudresden.mobilis.android.geotwitter.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.mobilis.android.geotwitter.adapters.ListTreasureAdapter;
import de.tudresden.mobilis.android.geotwitter.beans.Treasure;
import de.tudresden.mobilis.android.geotwitter.engine.GeoTwitterManager;

/**
 * 
 * @author Marian Seliuchenko
 *
 */
public class TreasuresListActivity extends ListActivity {


	ListTreasureAdapter listAdapter;
	List<Treasure> treasures;
	
	GeoTwitterManager mGeoTwitterManager;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.treasures_list_layout);
		mGeoTwitterManager = GeoTwitterManager.getInstance();
		if(savedInstanceState==null){
			mGeoTwitterManager.mMXAController = MXAController.get();
			mGeoTwitterManager.mMXAController.connectMXA(getApplication().getApplicationContext(), mGeoTwitterManager);
			
			mGeoTwitterManager.createDatabase(getApplicationContext());
			mGeoTwitterManager.openDatabase().clearTables();
			startService(new Intent(TreasuresListActivity.this,GeoTwitterService.class));
			UIInitialization();
		}

	}
	
	public void UIInitialization(){
		treasures = new ArrayList<Treasure>();
		refreshList();
	}
	
	
	
	

	private Handler onReceived = new Handler(){
		@Override
		public void handleMessage(Message msg){

			if (msg.obj instanceof String){
				if(msg.obj.equals("DatabaseUpdated")){
					refreshList();	

				}
			}
		}
	};

	public void refreshList(){
		listAdapter = new ListTreasureAdapter(getApplicationContext(),mGeoTwitterManager.openDatabase().getTreasuresList() , R.layout.element_list_treasure);
		this.setListAdapter(listAdapter);
		listAdapter.notifyDataSetChanged();

	}
	
	

	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		super.onListItemClick(l, v, position, id);
		Treasure treasureSelected = (Treasure) getListAdapter().getItem(position);
		Intent showQuestionActivityIntent = new Intent(TreasuresListActivity.this,ShowTreasureActivity.class);
		showQuestionActivityIntent.putExtra("TreasureSelected", treasureSelected);
		startActivity(showQuestionActivityIntent);
	}

	@Override
	public void onResume(){
		super.onResume();
		mGeoTwitterManager.registerHandler(onReceived);
		refreshList();

	}

	@Override
	public void onPause(){
		super.onPause();
		mGeoTwitterManager.unRegisterHandler(onReceived);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean("RestartedDueToConfigurationChanges", true);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		stopService(new Intent(TreasuresListActivity.this,GeoTwitterService.class));
		//mGeoTwitterManager.openDatabase().close();
	}

	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_treasure:
			startActivity(new Intent(TreasuresListActivity.this,CreateTreasureActivity.class));
			return true;
		case R.id.item_Search:
			
			return true;
		case R.id.item_ViewSwitch:
			startActivity(new Intent(TreasuresListActivity.this,MapActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			return true;
		default: 
			return super.onOptionsItemSelected(item);
		}

	} 


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_public, menu);
		return true;
	}
}
