/*******************************************************************************
 * Copyright (C) 2010 Technische Universität Dresden
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 ******************************************************************************/
package de.tudresden.mobilis.services.geotwitter.helpers;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jivesoftware.smack.util.Base64;

import de.tudresden.mobilis.services.geotwitter.beans.LocationBean;
import de.tudresden.mobilis.services.geotwitter.beans.Treasure;
import de.tudresden.mobilis.services.geotwitter.beans.TreasureContent;


/**
 * The Class SqlHelper provides functions to query data of 
 * XHunt specific tables out of the Mobilis-DB. It is also 
 * possible to export the whole data for an area into an xml file.
 */
public class SqlHelper {

	/** The SQL connection class. */
	private Connection mMysqlConnection;

	/** The current SQL statement. */
	private Statement mStatement = null;

	/** The current prepared SQL statement. */
	private PreparedStatement mPreparedStatement = null;

	/** The current result set of an SQL action. */
	private ResultSet mResultSet = null;

	/** The server address of the database. */
	private String mServerAddress = "localhost";

	/** The server port of the database. */
	private String mServerPort = "3306";

	/** The name for the XHunt database. */
	private String mDbName = "geotwitter";

	/** The username to log in into the database. */
	private String mDbUsername = "mobilis";

	/** The password to log in into the database. */
	private String mDbPassword = "mobilis";

	/** The name of the TABLE_AREA. */
	private static final String TABLE_TREASURE_LIST = "TreasureList";
	/** The name of the COLUMN_ID. */
	/** The name of the COLUMN_NAME. */
	private static final String COLUMN_ID = "ID";
	private static final String COLUMN_TREASURE_ID = "TreasureID";
	private static final String COLUMN_TREASURE_NAME = "Name";
	private static final String COLUMN_TREASURE_LONGITUDE = "Longitude";
	private static final String COLUMN_TREASURE_LATITUDE = "Latitude";
	private static final String COLUMN_TREASURE_AUTHOR = "Author";
	private static final String COLUMN_TREASURE_DATE = "Date";
	private static final String COLUMN_TREASURE_RATE = "Rate";
	private static final String COLUMN_TREASURE_DESCRIPTION = "Description";
	/** The name of the COLUMN_DESCRIPTION. */

	private static final String TABLE_CONTENT_LIST = "ContentTable";
	/** The name of the COLUMN_ID. */
	/** The name of the COLUMN_NAME. */
	private static final String COLUMN_CONTENT_ID = "ID";
	private static final String COLUMN_CONTENT = "Content";

	private static final String TABLE_ONLINE_USER_LIST = "OnlineJIDTable";
	/** The name of the COLUMN_ID. */
	/** The name of the COLUMN_NAME. */
	private static final String COLUMN_USER_ID = "ID";
	private static final String COLUMN_USER_JID = "JID";
	private static final String COLUMN_USER_TIME = "Time";
	


	/** The class specific Logger object. */
	private final static Logger LOGGER = Logger.getLogger(SqlHelper.class.getCanonicalName());


	/**
	 * Instantiates a new SqlHelper.
	 */
	public SqlHelper() {
		loadDbDriver();
		this.createDbStructure();
	}

	/**
	 * Check if tables are well defined. This is necessary to modify 
	 * the data in the right tables. If one query of a table fails, 
	 * the function is returning false.
	 *
	 * @return true, if structure is well defined
	 */
	public boolean checkDbStructure(){
		boolean isStructureOk = false;

		try {
			mMysqlConnection = DriverManager
					.getConnection(getConnectionURI());

			mPreparedStatement = mMysqlConnection
					.prepareStatement("select ID, Name, Description, Version from " + mDbName + "." + TABLE_TREASURE_LIST);			
			mPreparedStatement.executeQuery();

			isStructureOk = true;			
		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
			isStructureOk = false;
		}

		return isStructureOk;
	}

	/**
	 * Disconnect the database connection.
	 */
	public void disconnect(){
		try {
			if (mMysqlConnection != null) {
				mMysqlConnection.close();
			}
		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
		}
	}

	/**
	 * Flush/release the current SQL statement and result set objects.
	 */
	private void flush() {
		try {
			if (mResultSet != null) {
				mResultSet.close();
			}

			if (mStatement != null) {
				mStatement.close();
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Creates the necessary database structure. Use {@link checkDbStructure} to 
	 * verify the correct structure of the database.
	 *
	 * @return true, if creation was successfuly
	 */
	public boolean createDbStructure(){
		boolean isStructureCreated = false;
		try {
			mMysqlConnection = DriverManager
					.getConnection(getConnectionURI());

			mPreparedStatement = mMysqlConnection
					.prepareStatement("CREATE  TABLE IF NOT EXISTS " + mDbName + "." + TABLE_TREASURE_LIST + " (" +
							COLUMN_ID + " INT NOT NULL auto_increment," +
							COLUMN_TREASURE_NAME + " VARCHAR(255) NULL ," +
							COLUMN_TREASURE_LONGITUDE + " DOUBLE NULL ," +
							COLUMN_TREASURE_LATITUDE + " DOUBLE NULL ," +
							COLUMN_TREASURE_DESCRIPTION + " MEDIUMTEXT NULL ," +
							COLUMN_TREASURE_RATE + " INT NULL ," +
							COLUMN_TREASURE_AUTHOR + " VARCHAR(255) NULL ," +
							COLUMN_TREASURE_DATE + " VARCHAR(255) NULL ," +
							" PRIMARY KEY (" + COLUMN_ID + ") )");
			mPreparedStatement.executeUpdate();

			mPreparedStatement = mMysqlConnection
					.prepareStatement("CREATE  TABLE IF NOT EXISTS " + mDbName + "." + TABLE_CONTENT_LIST + " (" +
							COLUMN_CONTENT_ID + " INT NULL," +
							COLUMN_CONTENT + " BLOB NULL ," +
							" PRIMARY KEY (" + COLUMN_ID + ") )");
			mPreparedStatement.executeUpdate();

			mPreparedStatement = mMysqlConnection
					.prepareStatement("CREATE  TABLE IF NOT EXISTS " + mDbName + "." + TABLE_ONLINE_USER_LIST + " (" +
							COLUMN_USER_ID + " INT NULL," +
							COLUMN_USER_JID + " VARCHAR(50) NULL ," +
							COLUMN_USER_TIME + " BIGINT NULL ," +
							" PRIMARY KEY (" + COLUMN_USER_JID + ") )");
			mPreparedStatement.executeUpdate();

			isStructureCreated = true;
		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
			isStructureCreated = false;
		}

		return isStructureCreated;
	}


	public int addTreasure(Treasure treasure){
		int treasureid = 0;
		try {
			mMysqlConnection = DriverManager
					.getConnection(getConnectionURI());
			String query = "INSERT INTO "+TABLE_TREASURE_LIST+"("+
					COLUMN_ID+","+
					COLUMN_TREASURE_NAME+","+
					COLUMN_TREASURE_LONGITUDE+","+
					COLUMN_TREASURE_LATITUDE+","+
					COLUMN_TREASURE_DESCRIPTION+","+
					COLUMN_TREASURE_AUTHOR+","+
					COLUMN_TREASURE_DATE+","+
					COLUMN_TREASURE_RATE+") VALUES (?,?,?,?,?,?,?,?)";


			mPreparedStatement = mMysqlConnection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
			mPreparedStatement.setInt(1, 0);
			mPreparedStatement.setString(2, treasure.getName());
			mPreparedStatement.setDouble(3, treasure.getLocation().getLongitude());
			mPreparedStatement.setDouble(4, treasure.getLocation().getLatitude());
			mPreparedStatement.setString(5, treasure.getDescription());
			mPreparedStatement.setString(6, treasure.getAuthor());
			mPreparedStatement.setString(7, treasure.getDate());
			mPreparedStatement.setInt(8, 0);
			mPreparedStatement.executeUpdate();

			ResultSet rs = mPreparedStatement.getGeneratedKeys();
			if(rs.next()){
				treasureid = rs.getInt(1);
			}
			disconnect();

		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());

		}

		return treasureid;
	}

	public boolean setUserOnline(String jid){

		try {
			mMysqlConnection = DriverManager
					.getConnection(getConnectionURI());
			long time = System.currentTimeMillis();
			String query = "INSERT INTO "+TABLE_ONLINE_USER_LIST+
					"("+COLUMN_USER_ID+","+COLUMN_USER_JID+","+COLUMN_USER_TIME+") "+
					"VALUES ( '"+0+"','"+jid+"','"+time+"' ) ON DUPLICATE KEY UPDATE "+
					COLUMN_USER_TIME+" = '"+time+"';";
			
			mPreparedStatement = mMysqlConnection.prepareStatement(query);
			mPreparedStatement.executeUpdate();
			System.out.println("Updated: "+jid+" at "+String.valueOf(time));
			disconnect();
			return true;			

		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());

		}
		disconnect();
		return false;
	}

	public boolean setUserOffline(String jid){

		try {
			mMysqlConnection = DriverManager
					.getConnection(getConnectionURI());
			String query = "DELETE FROM "+TABLE_ONLINE_USER_LIST+" WHERE "+COLUMN_USER_JID+" = '"+jid+"';";
			mPreparedStatement = mMysqlConnection.prepareStatement(query);
			mPreparedStatement.executeUpdate();
			disconnect();
			return true;			

		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());

		}
		disconnect();
		return false;
	}
	
	public boolean isUserOnline(String jid){
		try {
			mMysqlConnection = DriverManager
					.getConnection(getConnectionURI());
			String query = "SELECT FROM "+TABLE_ONLINE_USER_LIST+" WHERE "+COLUMN_USER_JID+" = '"+jid+"';";


			mPreparedStatement = mMysqlConnection.prepareStatement(query);
			
			ResultSet rs = mPreparedStatement.executeQuery();
			if(rs.next()){
				System.out.println("User is ONLINE!!!");
				return true;
				
			}
			System.out.println("User is OFFLINE!!!");
			disconnect();
			return false;			

		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());

		}
		disconnect();
		return false;
		
	}
	
	public void UpdateOnlineUserList(){
		try {
			mMysqlConnection = DriverManager
					.getConnection(getConnectionURI());
			String query = "SELECT "+COLUMN_USER_JID+","+COLUMN_USER_TIME+" FROM "+TABLE_ONLINE_USER_LIST;


			mPreparedStatement = mMysqlConnection.prepareStatement(query);
			
			ResultSet rs = mPreparedStatement.executeQuery();

			while(rs.next()){
				String s = rs.getString(COLUMN_USER_TIME);
				System.out.println("Cheking users state . . .:" + rs.getString(COLUMN_USER_JID));
				long time1 = rs.getLong(COLUMN_USER_TIME);
				long time2 = System.currentTimeMillis();
				if(time2-time1>60000)
				{
					this.setUserOffline(rs.getString(COLUMN_USER_JID));
					System.out.println(rs.getString(COLUMN_USER_JID)+"USER REMOVED!");
				}	
			}

		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());

		}
		disconnect();
		
	}

	public int addTreasureContent(TreasureContent treasureContent){
		int treasureid = 0;
		try {
			mMysqlConnection = DriverManager
					.getConnection(getConnectionURI());
			String query = "INSERT INTO "+TABLE_CONTENT_LIST+"("+
					COLUMN_CONTENT_ID+","+
					COLUMN_CONTENT+") VALUES (?,?)";


			mPreparedStatement = mMysqlConnection.prepareStatement(query);
			System.out.println("STRING: " + treasureContent.getContent());
			InputStream is = new ByteArrayInputStream(Base64.decode(treasureContent.getContent()));
			System.out.println("STREAM: " + is);
			mPreparedStatement.setInt(1, treasureContent.getTreasureID());
			mPreparedStatement.setBlob(2, is);
			treasureid = mPreparedStatement.executeUpdate();
			treasureid = treasureContent.getTreasureID();



		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());

		}
		disconnect();
		return treasureid;
	}

	public TreasureContent getTreasureContent(int treasureID){
		TreasureContent treasureContent = null;
		try {
			mMysqlConnection = DriverManager
					.getConnection(getConnectionURI());
			String query = "SELECT "+COLUMN_CONTENT+" FROM "+TABLE_CONTENT_LIST+" WHERE "+COLUMN_CONTENT_ID+" = "+String.valueOf(treasureID);



			mPreparedStatement = mMysqlConnection.prepareStatement(query);
			ResultSet rs = mPreparedStatement.executeQuery();
			while(rs.next()){

				BufferedReader br = new BufferedReader(new InputStreamReader(rs.getBinaryStream(COLUMN_CONTENT))); 
				String s="";
				InputStream is = rs.getBlob(COLUMN_CONTENT).getBinaryStream();
				System.out.println("READ IS: " + is);
				int length = (int) rs.getBlob(COLUMN_CONTENT).length();
				byte[] buffer = new byte[length];
				try {
					length = is.read(buffer);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				s = Base64.encodeBytes(buffer);
				System.out.println("S encoded: " + s);
				treasureContent = new TreasureContent(treasureID, s);
				System.out.println("TREASURE CONTENT ID: " + String.valueOf(treasureContent.getTreasureID()));
				System.out.println("TREASURE CONTENT CONT: " + s);

			}

		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());

		}
		disconnect();
		return treasureContent;
	}


	public List<Treasure> getTreasureList(){
		List<Treasure> treasureList = new ArrayList<Treasure>();
		try {
			mMysqlConnection = DriverManager
					.getConnection(getConnectionURI());

			String query = "SELECT * FROM "+TABLE_TREASURE_LIST;
			mPreparedStatement = mMysqlConnection.prepareStatement(query);
			System.out.println("Statement prepared!");
			ResultSet rs = mPreparedStatement.executeQuery();
			System.out.println("RESULT GOT!:");
			while(rs.next()){
				Treasure treasure = new Treasure();
				treasure.setTreasureID(rs.getInt(1));
				treasure.setName(rs.getString(COLUMN_TREASURE_NAME));
				treasure.setLocation(new LocationBean(
						rs.getDouble(COLUMN_TREASURE_LONGITUDE), 
						rs.getDouble(COLUMN_TREASURE_LATITUDE)));
				treasure.setDescription(rs.getString(COLUMN_TREASURE_DESCRIPTION));
				treasure.setAuthor(rs.getString(COLUMN_TREASURE_AUTHOR));
				treasure.setDate(rs.getString(COLUMN_TREASURE_DATE));
				treasureList.add(treasure);
				//	System.out.println("TREASURE: "+treasure.getName());


			}

		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
			return null;
		}
		disconnect();
		return treasureList;
	}


	/**
	 * Load database driver for MySQL.
	 */
	private void loadDbDriver(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
		}
	}

	/**
	 * Test the database connection to verify the settings.
	 *
	 * @return true, if connection and the settings are correct
	 */
	public boolean testConnection(){
		boolean connected = false;
		try {			
			mMysqlConnection = DriverManager
					.getConnection(getConnectionURI());

			connected = true;
		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
		}

		return connected;
	}	

	/**
	 * Gets the URI of the database connection.
	 *
	 * @return the database connection URI
	 */
	private String getConnectionURI(){
		return "jdbc:mysql://"
				+ mServerAddress + ":" + mServerPort
				+ "/" + mDbName + "?"
				+ "user=" + mDbUsername + "&password=" + mDbPassword;
	}

	/**
	 * Sets the SQL connection data.
	 *
	 * @param serverAddress the address of the database server
	 * @param serverPort the port of the database server
	 * @param dbName the name of the XHunt database
	 * @param dbUsername the username to log in into the database
	 * @param dbPassword the password to log in into the database
	 */
	public void setSqlConnectionData(String serverAddress, String serverPort, String dbName,
			String dbUsername, String dbPassword) {
		this.mServerAddress = serverAddress;
		this.mServerPort = serverPort;
		this.mDbName = dbName;
		this.mDbUsername = dbUsername;
		this.mDbPassword = dbPassword;
	}


	/**
	 * Gets the server address.
	 *
	 * @return the server address
	 */
	public String getServerAddress() {
		return mServerAddress;
	}

	/**
	 * Sets the server address.
	 *
	 * @param mDbServerAddress the new server address
	 */
	public void setServerAddress(String mDbServerAddress) {
		this.mServerAddress = mDbServerAddress;
	}

	/**
	 * Gets the server port.
	 *
	 * @return the server port
	 */
	public String getServerPort() {
		return mServerPort;
	}

	/**
	 * Sets the server port.
	 *
	 * @param mServerPort the new server port
	 */
	public void setServerPort(String mServerPort) {
		this.mServerPort = mServerPort;
	}

	/**
	 * Gets the database name.
	 *
	 * @return the database name
	 */
	public String getDbName() {
		return mDbName;
	}

	/**
	 * Sets the database name.
	 *
	 * @param mDbName the new database name
	 */
	public void setDbName(String mDbName) {
		this.mDbName = mDbName;
	}

	/**
	 * Gets the database username.
	 *
	 * @return the database username
	 */
	public String getDbUsername() {
		return mDbUsername;
	}

	/**
	 * Sets the database username.
	 *
	 * @param mDbUsername the new database username
	 */
	public void setDbUsername(String mDbUsername) {
		this.mDbUsername = mDbUsername;
	}

	/**
	 * Gets the database password.
	 *
	 * @return the database password
	 */
	public String getDbPassword() {
		return mDbPassword;
	}

	/**
	 * Sets the database password.
	 *
	 * @param mDbPassword the new database password
	 */
	public void setDbPassword(String mDbPassword) {
		this.mDbPassword = mDbPassword;
	}	

}
