package de.tudresden.mobilis.android.geotwitter.beans;

import org.xmlpull.v1.XmlPullParser;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

import java.util.List;import java.util.ArrayList;

public class Treasure implements XMPPInfo {

	private String name = null;
	private String author = null;
	private String date = null;
	private String description = null;
	private LocationBean location = new LocationBean();
	private int databaseID = Integer.MIN_VALUE;
	private List< Byte > content = new ArrayList< Byte >();


	public Treasure( String name, String author, String date, String description, LocationBean location, int databaseID, List< Byte > content ) {
		super();
		this.name = name;
		this.author = author;
		this.date = date;
		this.description = description;
		this.location = location;
		this.databaseID = databaseID;
		for ( byte entity : content ) {
			this.content.add( entity );
		}
	}

	public Treasure(){}



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
				else if (tagName.equals( "name" ) ) {
					this.name = parser.nextText();
				}
				else if (tagName.equals( "author" ) ) {
					this.author = parser.nextText();
				}
				else if (tagName.equals( "date" ) ) {
					this.date = parser.nextText();
				}
				else if (tagName.equals( "description" ) ) {
					this.description = parser.nextText();
				}
				else if (tagName.equals( LocationBean.CHILD_ELEMENT ) ) {
					this.location.fromXML( parser );
				}
				else if (tagName.equals( "databaseID" ) ) {
					this.databaseID = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "content" ) ) {
					content.add( Byte.parseByte( parser.nextText() ) );
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

	public static final String CHILD_ELEMENT = "Treasure";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "http://mobilis.services/GeoTwitterService#type:Treasure";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<name>" )
			.append( this.name )
			.append( "</name>" );

		sb.append( "<author>" )
			.append( this.author )
			.append( "</author>" );

		sb.append( "<date>" )
			.append( this.date )
			.append( "</date>" );

		sb.append( "<description>" )
			.append( this.description )
			.append( "</description>" );

		sb.append( "<" + this.location.getChildElement() + ">" )
			.append( this.location.toXML() )
			.append( "</" + this.location.getChildElement() + ">" );

		sb.append( "<databaseID>" )
			.append( this.databaseID )
			.append( "</databaseID>" );

		for( byte entry : this.content ) {
			sb.append( "<content>" );
			sb.append( entry );
			sb.append( "</content>" );
		}

		return sb.toString();
	}



	public String getName() {
		return this.name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor( String author ) {
		this.author = author;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate( String date ) {
		this.date = date;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	public LocationBean getLocation() {
		return this.location;
	}

	public void setLocation( LocationBean location ) {
		this.location = location;
	}

	public int getDatabaseID() {
		return this.databaseID;
	}

	public void setDatabaseID( int databaseID ) {
		this.databaseID = databaseID;
	}

	public List< Byte > getContent() {
		return this.content;
	}

	public void setContent( List< Byte > content ) {
		this.content = content;
	}

}