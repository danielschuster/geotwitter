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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.mobilis.android.geotwitter.adapters.ListTreasureAdapter;
import de.tudresden.mobilis.android.geotwitter.beans.Treasure;
import de.tudresden.mobilis.android.geotwitter.engine.DatabaseHandler;
import de.tudresden.mobilis.android.geotwitter.engine.Singleton;

public class TreasuresListActivity extends ListActivity {


	ListTreasureAdapter listAdapter;
	List<Treasure> treasures;
	Singleton mSingleton;
	ActionBar ab;
	String mode = "List";
	LinearLayout screen;
	ViewFlipper flipper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.treasures_list_layout);
		mSingleton = Singleton.getInstance();
		mSingleton.mMXAController = MXAController.get();
		mSingleton.mMXAController.connectMXA(getApplication().getApplicationContext(), mSingleton);
		mSingleton.registerHandler(onReceived);
		mSingleton.db = new DatabaseHandler(getApplicationContext());
		mSingleton.db.clearTreasures();
		mSingleton.db.clearTreasureContents();
		startService(new Intent(TreasuresListActivity.this,SimpleService.class));
		UIInitialization();
	/*	treasures = new ArrayList<Treasure>();
		Treasure treasure = new Treasure();
		treasure.setName("Beer Bar!!!");
		treasure.setAuthor("Marian Seliuchenko");
		treasure.setDate("14.56 - 15.04.2013");
		treasure.setDescription("I just found this bar while was walking with my girlfriredn!!! I liked it from the beginning especially the waitress )) kidding!! :) haha!! This is the best place to hang out with ur friends at late night on friday!! dont miss the chance and visit it because its getting very crowded so u might not find free place if come to late!!!! hope u enjoy it!!!  ");
		treasure.setDatabaseID(12);
		treasure.setLocation(new LocationBean(1, 1));
		treasure.setContent(new ArrayList<Byte>());
		treasures.add(treasure);
		//mSingleton.db.addContact(treasure);
		treasure.setDatabaseID(13);
		//mSingleton.db.addContact(treasure);
		treasure.setDatabaseID(100);
		mSingleton.db.addContact(treasure);*/
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
				refreshList();
			}
		}
	};

	public void refreshList(){
		listAdapter = new ListTreasureAdapter(getApplicationContext(), mSingleton.db.getAllTreasures(), R.layout.element_list_treasure);
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
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		stopService(new Intent(TreasuresListActivity.this,SimpleService.class));
		mSingleton.db.close();
		mSingleton.db = null;
		
	}

	@Override
	public void onPause(){
		super.onPause();
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
	    		if(mode.equals("List")){
	    			item.setIcon(getResources().getDrawable(R.drawable.ic_map_transp));
	    			mode = "Map";
	    		}else
	    		{
	    			item.setIcon(getResources().getDrawable(R.drawable.ic_list));
	    			mode = "List";
	    		}
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
