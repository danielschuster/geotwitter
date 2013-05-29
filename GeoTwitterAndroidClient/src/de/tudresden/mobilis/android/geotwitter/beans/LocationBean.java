package de.tudresden.mobilis.android.geotwitter.beans;

import org.xmlpull.v1.XmlPullParser;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

public class LocationBean implements XMPPInfo {

	private double longitude = Double.MIN_VALUE;
	private double latitude = Double.MIN_VALUE;


	public LocationBean( double longitude, double latitude ) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public LocationBean(){}



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
				else if (tagName.equals( "longitude" ) ) {
					this.longitude = Double.parseDouble( parser.nextText() );
				}
				else if (tagName.equals( "latitude" ) ) {
					this.latitude = Double.parseDouble( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "LocationBean";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "http://mobilis.services/GeoTwitterService#type:LocationBean";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<longitude>" )
			.append( this.longitude )
			.append( "</longitude>" );

		sb.append( "<latitude>" )
			.append( this.latitude )
			.append( "</latitude>" );

		return sb.toString();
	}



	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude( double longitude ) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude( double latitude ) {
		this.latitude = latitude;
	}

}