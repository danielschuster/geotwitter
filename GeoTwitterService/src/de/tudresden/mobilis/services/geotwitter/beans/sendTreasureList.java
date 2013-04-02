package de.tudresden.mobilis.services.geotwitter.beans;

import org.xmlpull.v1.XmlPullParser;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

import java.util.List;import java.util.ArrayList;

public class sendTreasureList extends XMPPBean {

	private List< Treasure > treasureList = new ArrayList< Treasure >();


	public sendTreasureList( List< Treasure > treasureList ) {
		super();
		for ( Treasure entity : treasureList ) {
			this.treasureList.add( entity );
		}

		this.setType( XMPPBean.TYPE_RESULT );
	}

	public sendTreasureList(){
		this.setType( XMPPBean.TYPE_RESULT );
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
				else if (tagName.equals( Treasure.CHILD_ELEMENT ) ) {
					Treasure entity = new Treasure();

					entity.fromXML( parser );
					this.treasureList.add( entity );
					
					parser.next();
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

	public static final String CHILD_ELEMENT = "sendTreasureList";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilis:iq:updatelocation";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		sendTreasureList clone = new sendTreasureList( treasureList );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		for( Treasure entry : treasureList ) {
			sb.append( "<" + Treasure.CHILD_ELEMENT + ">" );
			sb.append( entry.toXML() );
			sb.append( "</" + Treasure.CHILD_ELEMENT + ">" );
		}

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public List< Treasure > getTreasureList() {
		return this.treasureList;
	}

	public void setTreasureList( List< Treasure > treasureList ) {
		this.treasureList = treasureList;
	}

}