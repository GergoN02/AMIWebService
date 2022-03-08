package uk.ac.mmu.advprog.hackathon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.w3c.dom.Document;

/**
 * Handles database access from within your web service
 * 
 * @author Gergo Nemeth, 20055710
 */
public class DB implements AutoCloseable {

	// allows us to easily change the database used
	private static final String JDBC_CONNECTION_STRING = "jdbc:sqlite:./data/AMI.db";

	// allows us to re-use the connection between queries if desired
	private Connection connection = null;

	/**
	 * Creates an instance of the DB object and connects to the database
	 */
	public DB() {
		try {
			connection = DriverManager.getConnection(JDBC_CONNECTION_STRING);
		} catch (SQLException sqle) {
			error(sqle);
		}
	}

	/**
	 * Returns the number of entries in the database, by counting rows
	 * 
	 * @return The number of entries in the database, or -1 if empty
	 */
	public int getNumberOfEntries() {
		int result = -1;
		try {
			Statement s = connection.createStatement();
			ResultSet results = s.executeQuery("SELECT COUNT(*) AS count FROM ami_data");
			while (results.next()) { // will only execute once, because SELECT COUNT(*) returns just 1 number
				result = results.getInt(results.findColumn("count"));
			}
		} catch (SQLException sqle) {
			error(sqle);

		}
		return result;
	}

	/**
	 * Queries Database for the last value of signal with provided ID
	 * @param signal_id The signal to be queried
	 * @return String value of signal's last value
	 */
	public String getLastSignalValue(String signal_id) {
		String res = "no results";

		try {
			PreparedStatement ps = connection
					.prepareStatement("SELECT signal_value FROM ami_data\r\n" + "WHERE signal_id = ?\r\n"
							+ "AND NOT signal_value = \"OFF\"\r\n" + "AND NOT signal_value = \"NR\"\r\n"
							+ "AND NOT signal_value = \"BLNK\"\r\n" + "ORDER BY datetime DESC\r\n" + "LIMIT 1;\r\n");

			ps.setString(1, signal_id);

			ResultSet resSet = ps.executeQuery();
			res = resSet.getString("signal_value");

			return res;

		} catch (SQLException sqle) {
			// TODO Auto-generated catch block
			error(sqle);
		}

		return res;
	}

	/**
	 * Returns frequency of signal values on a select motorway
	 * @param motorway Motorway ID to be queried
	 * @return JSONArray of key-value frequency pairs
	 */
	public JSONArray getFrequency(String motorway) {
		JSONArray res = null;

		HashMap<String, String> freqMap = new HashMap<>();

		try {
			PreparedStatement ps = connection.prepareStatement(
					"SELECT COUNT(signal_value) AS frequency, signal_value FROM ami_data WHERE signal_id LIKE ? GROUP BY signal_value ORDER BY frequency DESC;");

			ps.setString(1, motorway + "%");

			ResultSet resSet = ps.executeQuery();

			while (resSet.next()) {

				String sigValue = resSet.getString("signal_value");
				String frequency = resSet.getString("frequency");

				freqMap.put(sigValue, frequency);
			}

			res = Parser.frequencyToJSONArray(freqMap);
			return res;

		} catch (SQLException sqle) {
			error(sqle);
		}
		return res;
	}

	/**
	 * Gets the types of groups of signals
	 * @return String formatted XML Document
	 */
	public String getGroups() {

		try {
			Statement s = connection.createStatement();
			ResultSet resSet = s.executeQuery("SELECT DISTINCT signal_group FROM ami_data;");

			ArrayList<String> groups = new ArrayList<String>();

			while (resSet.next()) {
				groups.add(resSet.getString("signal_group"));
			}

			Document res = Parser.makeXMLGroups(groups);
			return Parser.formatDocument(res);

		} catch (SQLException sqle) {
			error(sqle);
		}
		return null;
	}

	/**
	 * Gets displayed signals in a specific signal group, at a specified time
	 * @param group Signal group as String
	 * @param time Requested time as String
	 * @return String fromatted XML Document
	 */
	public String getSignalAtTime(String group, String time) {

		try {

			PreparedStatement ps = connection.prepareStatement(
					"SELECT datetime, signal_id, signal_value FROM ami_data "
					+ "WHERE signal_group = ? AND datetime < ? AND (datetime, signal_id) "
					+ "IN ( SELECT MAX(datetime) AS datetime, signal_id FROM ami_data "
					+ "WHERE signal_group = ? AND datetime < ? GROUP BY signal_id ) ORDER BY signal_id;");

			ps.setString(1, group);
			ps.setString(2, time);
			ps.setString(3, group);
			ps.setString(4, time);

			ResultSet resSet = ps.executeQuery();
			

			ArrayList<Signal> signals = new ArrayList<>();

			while (resSet.next()) {

				String ID = resSet.getString("signal_id");
				String DateSet = resSet.getString("datetime");
				String Value = resSet.getString("signal_value");

				signals.add(new Signal(ID, DateSet, Value));
			}


			Document res = Parser.makeXMLSignals(signals);
			return Parser.formatDocument(res);

		} catch (SQLException sqle) {
			error(sqle);
		}
		return null;

	}

	/**
	 * Closes the connection to the database, required by AutoCloseable interface.
	 */
	@Override
	public void close() {
		try {
			if (!connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException sqle) {
			error(sqle);
		}
	}

	/**
	 * Prints out the details of the SQL error that has occurred, and exits the
	 * programme
	 * 
	 * @param sqle Exception representing the error that occurred
	 */
	private void error(SQLException sqle) {
		System.err.println("Problem Opening Database! " + sqle.getClass().getName());
		sqle.printStackTrace();
		System.exit(1);
	}
}
