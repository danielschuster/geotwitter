package de.tudresden.mobilis.android.geotwitter.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.tudresden.mobilis.android.geotwitter.beans.Treasure;
import de.tudresden.mobilis.android.geotwitter.engine.GeoTwitterManager;

/**
 * 
 * @author Marian Seliuchenko
 *
 */


public class MapActivity extends FragmentActivity{
	
	private GoogleMap googleMap;
	GeoTwitterManager mGeoTwitterManager;
	List<Treasure> treasureList;
	private LocationManager locationManager;
	LatLng centerPoint = null;
	locationListener myLocationListener = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		mGeoTwitterManager = GeoTwitterManager.getInstance();
		//Toast.makeText(getApplicationContext(), ConvertPointToLocation(coordinates), Toast.LENGTH_SHORT).show();
		centerPoint = mGeoTwitterManager.getCurrentLocation();
		MapInitialization();
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
			if(bundle.containsKey("TreasureToPointOn")){
				drawTreasure((Treasure)bundle.get("TreasureToPointOn"));
			}
		}else{
			drawAllTreasures();
		}

	}
	
	public void MapInitialization(){
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

		if(status!=ConnectionResult.SUCCESS){
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
			dialog.show();
		}else{
			SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
			googleMap = fm.getMap();
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			googleMap.setMyLocationEnabled(true);
		}
		googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

			private final View view = getLayoutInflater().inflate(R.layout.marker_info_window, null);
			private Treasure treasure = null;

			@Override
			public View getInfoWindow(Marker arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) {
				// TODO Auto-generated method stub
				TextView title = (TextView)view.findViewById(R.id.textView_MarkerTitle);
				TextView description = (TextView)view.findViewById(R.id.textView_MarkerDescription);
				treasure = mGeoTwitterManager.openDatabase().getTreasure(Integer.parseInt(marker.getSnippet()));
				title.setText(treasure.getName());
				description.setText(treasure.getDescription());
				return view;
			}
		});
		googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				// TODO Auto-generated method stub
				Treasure treasure = mGeoTwitterManager.openDatabase().getTreasure(Integer.parseInt(marker.getSnippet()));
				Intent intent = new Intent(MapActivity.this, ShowTreasureActivity.class);
				intent.putExtra("TreasureSelected", treasure);
				startActivity(intent);
			}
		});
		
	}
	


	

	public void drawAllTreasures(){
		treasureList = new ArrayList<Treasure>(mGeoTwitterManager.openDatabase().getAllTreasures());
		Iterator<Treasure> it = treasureList.iterator();
		Treasure treasure = null;
		while(it.hasNext()){
			treasure = it.next();
			MarkerOptions mo = new MarkerOptions();
			mo.position(new LatLng(treasure.getLocation().getLatitude(),treasure.getLocation().getLongitude()));
			mo.title(treasure.getName());
			mo.snippet(String.valueOf(treasure.getTreasureID()));
			googleMap.addMarker(mo);
			
		}
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(centerPoint));
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
	}
	
	public void drawTreasure(Treasure treasure){
		googleMap.clear();
		MarkerOptions mo = new MarkerOptions();
		mo.position(new LatLng(treasure.getLocation().getLatitude(),treasure.getLocation().getLongitude()));
		mo.title(treasure.getName());
		mo.snippet(String.valueOf(treasure.getTreasureID()));
		googleMap.addMarker(mo);
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(mo.getPosition()));
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_treasure:
			startActivity(new Intent(MapActivity.this,CreateTreasureActivity.class));
			return true;
		case R.id.item_Search:
			Toast.makeText(getApplicationContext(), "Searching!", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.item_ViewSwitch:
			item.setIcon(getResources().getDrawable(R.drawable.ic_map_transp));
			startActivity(new Intent(MapActivity.this,TreasuresListActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			return true;
		default: 
			return super.onOptionsItemSelected(item);
		}

	} 


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_map, menu);

		return true;
	}

	
	@Override
	protected void onNewIntent(Intent intent){
		Toast.makeText(getApplicationContext(), "Analazying!!!!", Toast.LENGTH_LONG).show();
		Bundle bundle = intent.getExtras();
		if(bundle!=null){
			if(bundle.containsKey("TreasureToPointOn")){
				Treasure treasure = (Treasure) intent.getExtras().get("TreasureToPointOn");
				LatLng position = new LatLng(treasure.getLocation().getLatitude(),treasure.getLocation().getLongitude());
				googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
			}
		}
		
	}

	@Override
	public void onResume(){
		super.onResume();
	/*	locationManager =(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		myLocationListener = new locationListener();
		Criteria c=new Criteria();
		c.setAccuracy(Criteria.ACCURACY_LOW);
		String providerName=locationManager.getBestProvider(c,true);
		locationManager.requestLocationUpdates(providerName, 0, 0, myLocationListener);*/

	}
	
	@Override
	public void onPause(){
		super.onPause();
		//locationManager.removeUpdates(myLocationListener);
	}

	class locationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			googleMap.addMarker(new MarkerOptions()
			.position(new LatLng(location.getLatitude(),location.getLongitude()))
			.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))));

		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}


	}
	

	
	public String ConvertPointToLocation(LatLng point) {   
	    String address = "";
	    Geocoder geoCoder = new Geocoder(
	        getBaseContext(), Locale.getDefault());
	    try {
	      List<Address> addresses = geoCoder.getFromLocation(
	        point.latitude, 
	        point.longitude, 1);
	 
	      if (addresses.size() > 0) {
	        for (int index = 0; 
		index < addresses.get(0).getMaxAddressLineIndex(); index++)
	          address += addresses.get(0).getAddressLine(index) + " ";
	      }
	    }
	    catch (IOException e) {        
	      e.printStackTrace();
	    }   
	    
	    return address;
	  } 


}
