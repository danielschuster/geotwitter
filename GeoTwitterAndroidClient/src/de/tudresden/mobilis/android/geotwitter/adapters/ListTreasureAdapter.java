package de.tudresden.mobilis.android.geotwitter.adapters;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import de.tudresden.mobilis.android.geotwitter.activities.R;
import de.tudresden.mobilis.android.geotwitter.beans.Treasure;
import de.tudresden.mobilis.android.geotwitter.engine.GeoTwitterManager;
/**
 * 
 * @author Marian Seliuchenko
 * 
 */

public class ListTreasureAdapter extends BaseAdapter
{    

	List<Treasure> treasuresList = new ArrayList<Treasure>();
	Context context = null;
	int layoutId;
	GeoTwitterManager mGeoTwitterManager;
	
	public ListTreasureAdapter(Context context, List<Treasure> items, int layoutId){
		this.context = context;
		this.treasuresList = items;
		this.layoutId = layoutId;
		mGeoTwitterManager = GeoTwitterManager.getInstance();
	}
	
	public int getCount(){
		return treasuresList.size();
	}

	public Object getItem(int position){
		return treasuresList.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		if (convertView == null){
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(layoutId,parent, false);
		}

		TextView textViewName = (TextView) view.findViewById(R.id.textView_Name);
		TextView textViewAuthor  = (TextView) view.findViewById(R.id.textView_Author);
		TextView textViewDate = (TextView) view.findViewById(R.id.textView_Date);
		TextView textViewDistance = (TextView) view.findViewById(R.id.textView_Distance);
		Treasure treasureInList = treasuresList.get(position);
		textViewName.setText(treasureInList.getName());
		textViewAuthor.setText(treasureInList.getAuthor());
		textViewDate.setText(treasureInList.getDate());
		textViewDistance.setText(mGeoTwitterManager.distanceCalculation(treasureInList));
		return view;
	}
}





