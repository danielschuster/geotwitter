package de.tudresden.mobilis.android.geotwitter.beans;

import org.xmlpull.v1.XmlPullParser;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class updateLocation extends XMPPBean {

	private LocationBean locationBean = new LocationBean();


	public updateLocation( LocationBean locationBean ) {
		super();
		this.locationBean = locationBean;

		this.setType( XMPPBean.TYPE_SET );
	}

	public updateLocation(){
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
				else if (tagName.equals( LocationBean.CHILD_ELEMENT ) ) {
					this.locationBean.fromXML( parser );
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

	public static final String CHILD_ELEMENT = "updateLocation";

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
		updateLocation clone = new updateLocation( locationBean );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<" + this.locationBean.getChildElement() + ">" )
			.append( this.locationBean.toXML() )
			.append( "</" + this.locationBean.getChildElement() + ">" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public LocationBean getLocationBean() {
		return this.locationBean;
	}

	public void setLocationBean( LocationBean locationBean ) {
		this.locationBean = locationBean;
	}

}