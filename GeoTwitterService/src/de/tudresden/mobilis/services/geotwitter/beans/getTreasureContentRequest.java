package de.tudresden.mobilis.services.geotwitter.beans;

import org.xmlpull.v1.XmlPullParser;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class getTreasureContentRequest extends XMPPBean {

	private int treasureID = Integer.MIN_VALUE;


	public getTreasureContentRequest( int treasureID ) {
		super();
		this.treasureID = treasureID;

		this.setType( XMPPBean.TYPE_SET );
	}

	public getTreasureContentRequest(){
		this.setType( XMPPBean.TYPE_SET );
	}


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
				else if (tagName.equals("error")) {
					parser = parseErrorAttributes(parser);
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

	public static final String CHILD_ELEMENT = "getTreasureContentRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilis:iq:gettreasurecontent";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		getTreasureContentRequest clone = new getTreasureContentRequest( treasureID );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<treasureID>" )
			.append( this.treasureID )
			.append( "</treasureID>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public int getTreasureID() {
		return this.treasureID;
	}

	public void setTreasureID( int treasureID ) {
		this.treasureID = treasureID;
	}

}