package de.tudresden.mobilis.services.geotwitter.beans;

import org.xmlpull.v1.XmlPullParser;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

import java.util.List;import java.util.ArrayList;

public class getTreasureContentResponse extends XMPPBean {

	private TreasureContent content = new TreasureContent();


	public getTreasureContentResponse( TreasureContent content ) {
		super();
		this.content = content;

		this.setType( XMPPBean.TYPE_RESULT );
	}

	public getTreasureContentResponse(){
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
				else if (tagName.equals( TreasureContent.CHILD_ELEMENT ) ) {
					this.content.fromXML( parser );
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

	public static final String CHILD_ELEMENT = "getTreasureContentResponse";

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
		getTreasureContentResponse clone = new getTreasureContentResponse( content );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<" + this.content.getChildElement() + ">" )
			.append( this.content.toXML() )
			.append( "</" + this.content.getChildElement() + ">" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public TreasureContent getContent() {
		return this.content;
	}

	public void setContent( TreasureContent content ) {
		this.content = content;
	}

}