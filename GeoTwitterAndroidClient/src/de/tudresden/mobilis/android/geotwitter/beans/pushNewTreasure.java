package de.tudresden.mobilis.android.geotwitter.beans;

import org.xmlpull.v1.XmlPullParser;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

import java.util.List;import java.util.ArrayList;

public class pushNewTreasure extends XMPPBean {

	private Treasure Treasure = new Treasure();


	public pushNewTreasure( Treasure Treasure ) {
		super();
		this.Treasure = Treasure;

		this.setType( XMPPBean.TYPE_RESULT );
	}

	public pushNewTreasure(){
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
					this.Treasure.fromXML( parser );
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

	public static final String CHILD_ELEMENT = "pushNewTreasure";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilis:iq:pushnewtreasure";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		pushNewTreasure clone = new pushNewTreasure( Treasure );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<" + this.Treasure.getChildElement() + ">" )
			.append( this.Treasure.toXML() )
			.append( "</" + this.Treasure.getChildElement() + ">" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public Treasure getTreasure() {
		return this.Treasure;
	}

	public void setTreasure( Treasure Treasure ) {
		this.Treasure = Treasure;
	}

}