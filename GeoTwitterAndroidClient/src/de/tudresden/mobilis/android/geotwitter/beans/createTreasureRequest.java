package de.tudresden.mobilis.android.geotwitter.beans;

import org.xmlpull.v1.XmlPullParser;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class createTreasureRequest extends XMPPBean {

	private Treasure treasure = new Treasure();
	private TreasureContent content = new TreasureContent();


	public createTreasureRequest( Treasure treasure, TreasureContent content ) {
		super();
		this.treasure = treasure;
		this.content = content;

		this.setType( XMPPBean.TYPE_SET );
	}

	public createTreasureRequest(){
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
				else if (tagName.equals( Treasure.CHILD_ELEMENT ) ) {
					this.treasure.fromXML( parser );
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

	public static final String CHILD_ELEMENT = "createTreasureRequest";

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
		createTreasureRequest clone = new createTreasureRequest( treasure, content );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<" + this.treasure.getChildElement() + ">" )
			.append( this.treasure.toXML() )
			.append( "</" + this.treasure.getChildElement() + ">" );

		sb.append( "<" + this.content.getChildElement() + ">" )
			.append( this.content.toXML() )
			.append( "</" + this.content.getChildElement() + ">" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public Treasure getTreasure() {
		return this.treasure;
	}

	public void setTreasure( Treasure treasure ) {
		this.treasure = treasure;
	}

	public TreasureContent getContent() {
		return this.content;
	}

	public void setContent( TreasureContent content ) {
		this.content = content;
	}

}