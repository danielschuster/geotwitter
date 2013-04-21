package de.tudresden.mobilis.android.geotwitter.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.mobilis.android.geotwitter.adapters.ListTreasureAdapter;
import de.tudresden.mobilis.android.geotwitter.beans.Treasure;
import de.tudresden.mobilis.android.geotwitter.engine.DatabaseHandler;
import de.tudresden.mobilis.android.geotwitter.engine.Singleton;
import android.location.Geocoder;

public class TreasuresListActivity extends ListActivity {


	ListTreasureAdapter listAdapter;
	List<Treasure> treasures;
	Singleton mSingleton;
	ActionBar ab;
	LinearLayout screen;
	ViewFlipper flipper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.treasures_list_layout);
		mSingleton = Singleton.getInstance();
		mSingleton.mMXAController = MXAController.get();
		mSingleton.mMXAController.connectMXA(getApplication().getApplicationContext(), mSingleton);
		mSingleton.createDatabase(getApplicationContext());
		mSingleton.openDatabase().clearTreasures();
		startService(new Intent(TreasuresListActivity.this,SimpleService.class));
		UIInitialization();
/*	
		ab = getActionBar();
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayShowTitleEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		ab.setHomeButtonEnabled(true);
		ab.setIcon(R.drawable.ic_treasure);
		ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_background));
		ab.setTitle("Treasures around");
		 */

	}


	private Handler onReceived = new Handler(){
		@Override
		public void handleMessage(Message msg){

			if (msg.obj instanceof String){
				if(msg.obj.equals("DatabaseUpdated")){
					refreshList();	
					Log.i("TreasureListActivity", "LIST REFRESHED!!!");
				}
			}
		}
	};

	public void refreshList(){
		

		listAdapter = new ListTreasureAdapter(getApplicationContext(),mSingleton.openDatabase().getAllTreasures() , R.layout.element_list_treasure);
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
		mSingleton.registerHandler(onReceived);
	}

	@Override
	public void onPause(){
		super.onPause();
		mSingleton.unRegisterHandler(onReceived);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		stopService(new Intent(TreasuresListActivity.this,SimpleService.class));
		mSingleton.openDatabase().close();
	}

	public void UIInitialization(){
		treasures = new ArrayList<Treasure>();
		listAdapter = new ListTreasureAdapter(getApplicationContext(), treasures, R.layout.element_list_treasure);
		this.setListAdapter(listAdapter);
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_treasure:
			startActivity(new Intent(TreasuresListActivity.this,CreateTreasureActivity.class));
			return true;

		case R.id.item_Search:
			Toast.makeText(getApplicationContext(), "Searching!", Toast.LENGTH_SHORT).show();
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
