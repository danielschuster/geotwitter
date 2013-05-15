package de.tudresden.mobilis.android.geotwitter.helper;

/**
 * 
 * @author Marian Seliuchenko
 *
 */

public class ChatHelper {
	
	public ChatHelper(){
	}
	
	private String nickName = null;
	private String myJID = null;
	
	public void setNickName(String nickName){
		this.nickName = nickName;
	}
	
	public String getNickName(){
		return this.nickName;
	}
	
	public void setJID(String JID){
		this.myJID = JID;
	}
	
	public String getJID(){
		return this.myJID;
	}

}
