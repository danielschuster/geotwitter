package de.tudresden.mobilis.android.geotwitter.beans;

import org.xmlpull.v1.XmlPullParser;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

import java.util.List;import java.util.ArrayList;

public class getTreasureContentResponse extends XMPPBean {

	private List< Byte > content = new ArrayList< Byte >();


	public getTreasureContentResponse( List< Byte > content ) {
		super();
		for ( byte entity : content ) {
			this.content.add( entity );
		}

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
				else if (tagName.equals( "content" ) ) {
					content.add( Byte.parseByte( parser.nextText() ) );
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

		for( byte entry : this.content ) {
			sb.append( "<content>" );
			sb.append( entry );
			sb.append( "</content>" );
		}

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public List< Byte > getContent() {
		return this.content;
	}

	public void setContent( List< Byte > content ) {
		this.content = content;
	}

}