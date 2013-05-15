package de.tudresden.mobilis.android.geotwitter.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.MessageItems;
import de.tudresden.mobilis.android.geotwitter.activities.R;
import de.tudresden.mobilis.android.geotwitter.engine.GeoTwitterManager;

/**
 * 
 * @author Marian Seliuchenko
 * Adapter for ListView in ChatActivity for messages
 *
 */

public class ChatMessageListAdapter extends SimpleCursorAdapter {

	public ChatMessageListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		mGeoTwitterManager = GeoTwitterManager.getInstance();
	}
	GeoTwitterManager mGeoTwitterManager;
	
	

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		super.bindView(view, context, cursor);
		String body = cursor.getString(cursor.getColumnIndex(MessageItems.BODY));
		String sender = cursor.getString(cursor.getColumnIndex(MessageItems.SENDER));		
		ImageView icon = (ImageView)view.findViewById(R.id.imageView_ItemIcon);
		if(sender.equals(mGeoTwitterManager.getChatHelper().getJID())){
			icon.setImageBitmap(BitmapFactory.decodeResource(view.getResources(), R.drawable.message_out));
		}else{
			icon.setImageBitmap(BitmapFactory.decodeResource(view.getResources(), R.drawable.message_in));
		}
		sender = sender.substring(sender.lastIndexOf('/')+1);
		TextView tv_body = (TextView)view.findViewById(R.id.textView_Name);
		TextView tv_sender = (TextView)view.findViewById(R.id.textView_AuthorDate);
		tv_body.setText(body);
		tv_sender.setText(sender);
	}


}


