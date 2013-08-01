package de.tudresden.mobilis.android.geotwitter.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.MessageItems;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPMessage;
import de.tudresden.mobilis.android.geotwitter.adapters.ChatMessageListAdapter;
import de.tudresden.mobilis.android.geotwitter.engine.GeoTwitterManager;

public class ChatActivity extends Activity{

	Button button_Send;
	TextView textView_PartnerResource;
	EditText editText_MessageToSend;
	ImageView imageView_PartnerState;
	ListView listView_Messages;
	
	
	String PartnerJID;
	GeoTwitterManager mGeoTwitterManager;
	Cursor msgCursor;
	public final String TAG = "ChatActivity";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_activity);
		if(getIntent().getExtras()!=null){
			if(getIntent().getExtras().containsKey("partnerJID")){
				this.PartnerJID = getIntent().getExtras().getString("partnerJID");
				Log.i(TAG, this.PartnerJID + "= PARTNER ID");
				
			}
		}
		mGeoTwitterManager = GeoTwitterManager.getInstance();
		UIInitialization();
		refreshMessageList();
	}

	public void UIInitialization(){
		button_Send = (Button)findViewById(R.id.button_Send);
		button_Send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					if (mGeoTwitterManager.getIXMPPService() != null && editText_MessageToSend.getText().toString()!="") {
						XMPPMessage msg = new XMPPMessage(mGeoTwitterManager.getChatHelper().getJID(),PartnerJID , editText_MessageToSend.getText().toString(), XMPPMessage.TYPE_CHAT);
						mGeoTwitterManager.getIXMPPService().sendMessage(new Messenger(new Handler()), 0, msg);
						editText_MessageToSend.setText("");
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		editText_MessageToSend = (EditText)findViewById(R.id.editText_MessageToSend);
		imageView_PartnerState = (ImageView)findViewById(R.id.imageView_PartnerState);
		listView_Messages = (ListView)findViewById(R.id.listView_Messages);
		textView_PartnerResource = (TextView)findViewById(R.id.textView_PartnerResource);
		textView_PartnerResource.setText(PartnerJID.substring(PartnerJID.lastIndexOf('/')+1));
		
	}
	
	private void refreshMessageList() {
		StringBuilder sb= new StringBuilder();
		sb.append("((("+MessageItems.SENDER+"='"+this.PartnerJID
				+"' AND "
				+MessageItems.RECIPIENT+"='"+mGeoTwitterManager.getChatHelper().getJID()
				+"') OR"+
				"("+MessageItems.SENDER+"='"+mGeoTwitterManager.getChatHelper().getJID()
				+"' AND "
				+MessageItems.RECIPIENT+"='"+PartnerJID+"')) AND "+MessageItems.BODY+"<>'') ");
		String helper=sb.toString();
		msgCursor = getContentResolver().query(MessageItems.contentUri,
				new String[]{MessageItems._ID,MessageItems.SENDER,MessageItems.RECIPIENT,MessageItems.BODY},helper, null, MessageItems.DATE_SENT);
		startManagingCursor(msgCursor);

		if(msgCursor!=null){
			ChatMessageListAdapter adapter = new ChatMessageListAdapter(this,
					R.layout.element_list_chatroom,
					msgCursor,
					new String[] { MessageItems.BODY, MessageItems.SENDER },new int[]{R.id.textView_Name, R.id.textView_AuthorDate});
			listView_Messages.setAdapter(adapter);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_chat_activity2, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK))
		{
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	

}
