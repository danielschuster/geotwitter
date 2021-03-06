package de.tudresden.mobilis.android.geotwitter.beans;

import org.xmlpull.v1.XmlPullParser;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class createTreasureResponse extends XMPPBean {

	private int errortype = Integer.MIN_VALUE;


	public createTreasureResponse( int errortype ) {
		super();
		this.errortype = errortype;

		this.setType( XMPPBean.TYPE_RESULT );
	}

	public createTreasureResponse(){
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
				else if (tagName.equals( "errortype" ) ) {
					this.errortype = Integer.parseInt( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "createTreasureResponse";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilis:iq:createtreasure";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		createTreasureResponse clone = new createTreasureResponse( errortype );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<errortype>" )
			.append( this.errortype )
			.append( "</errortype>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public int getErrortype() {
		return this.errortype;
	}

	public void setErrortype( int errortype ) {
		this.errortype = errortype;
	}

}