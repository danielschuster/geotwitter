package de.tudresden.mobilis.android.geotwitter.beans;

import org.xmlpull.v1.XmlPullParser;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

public class TreasureContent implements XMPPInfo {

	private int treasureID = Integer.MIN_VALUE;
	private String content = null;


	public TreasureContent( int treasureID, String content ) {
		super();
		this.treasureID = treasureID;
		this.content = content;
	}

	public TreasureContent(){}



	@Override
	public void fromXML( XmlPullParser parser ) throws Exception {
		boolean done = false;
			
		do {
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				
				if (tagName.equals(getChildElement())) {
					parser.next();
				}
				else if (tagName.equals( "treasureID" ) ) {
					this.treasureID = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "content" ) ) {
					this.content = parser.nextText();
				}
				else
					parser.next();
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals(getChildElement()))
					done = true;
				else
					parser.next();
				break;
			case XmlPullParser.END_DOCUMENT:
				done = true;
				break;
			default:
				parser.next();
			}
		} while (!done);
	}

	public static final String CHILD_ELEMENT = "TreasureContent";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "http://mobilis.services/GeoTwitterService#type:TreasureContent";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<treasureID>" )
			.append( this.treasureID )
			.append( "</treasureID>" );

		sb.append( "<content>" )
			.append( this.content )
			.append( "</content>" );

		return sb.toString();
	}



	public int getTreasureID() {
		return this.treasureID;
	}

	public void setTreasureID( int treasureID ) {
		this.treasureID = treasureID;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent( String content ) {
		this.content = content;
	}

}