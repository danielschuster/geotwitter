package de.tudresden.mobilis.android.geotwitter.activities;

import java.util.ArrayList;
import java.util.List;

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
import de.tudresden.mobilis.android.geotwitter.beans.sendTreasureList;
import de.tudresden.mobilis.android.geotwitter.engine.Singleton;

public class TreasuresListActivity extends ListActivity {


	ListTreasureAdapter listAdapter;
	List<Treasure> treasures;
	Singleton mSingleton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_treasures_list);
		mSingleton = Singleton.getInstance();
		mSingleton.mMXAController = MXAController.get();
		mSingleton.mMXAController.connectMXA(getApplication().getApplicationContext(), mSingleton);
		mSingleton.registerHandler(onReceived);
		startService(new Intent(TreasuresListActivity.this,SimpleService.class));
		UIInitialization();
	}

	private Handler onReceived = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if (msg.obj instanceof sendTreasureList){
				sendTreasureList response = (sendTreasureList)msg.obj;
				treasures = response.getTreasureList();
				refreshList();
			}
		}
	};

	public void refreshList(){
		listAdapter = new ListTreasureAdapter(getApplicationContext(), treasures, R.layout.element_list_treasure);
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
