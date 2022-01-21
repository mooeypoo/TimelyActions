package io.github.mooeypoo.timelyactions.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;

import io.github.mooeypoo.timelyactions.store.items.DatabaseItem;

public class RecordsDatabase extends Database {

	public RecordsDatabase(JavaPlugin plugin) {
		super(plugin);
		
		this.setFilename("records.db");
	}

	public void initialize() {
		this.initializeTables(
			// Create table
			"CREATE TABLE IF NOT EXISTS `player_records` ("
			+ "  `player` varchar(255) NOT NULL,"
			+ "  `interval_name` varchar(255) NOT NULL,"
			+ "  `last_run` varchar(255) NOT NULL,"
			+ "  PRIMARY KEY(`player`,`interval_name`)"
			+ ")",
			""
		);
	}

	public DatabaseItem getSpecificPlayerRecords(String playerName) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = DriverManager.getConnection(this.jdbcConnString);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 seconds

			// Create table if they don't exist
			String sql = "SELECT * FROM player_records WHERE player=?";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, playerName);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				String intervalName = rs.getString("interval_name");
				String lastRun = rs.getString("last_run");
				LocalDateTime lastRunTime = this.getLocalDateFromString(lastRun);
				return new DatabaseItem(
					playerName,
					intervalName,
					lastRunTime
				);
			}
		} catch (SQLException e) {
			this.plugin.getLogger().warning("Error fetching player records: " + e.getMessage());
		} finally {
		    if (connection != null) {
		        try {
		        	connection.close();
		        } catch (SQLException e) { /* ignored */}
		    }
		}
		return null;
	}

	public ArrayList<DatabaseItem> getAllPlayerRecords() {
		ArrayList<DatabaseItem> list = new ArrayList<DatabaseItem>();
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = DriverManager.getConnection(this.jdbcConnString);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 seconds

			// Create table if they don't exist
			rs = statement.executeQuery(
				"SELECT * FROM player_records"
			);
			while (rs.next()) {
				String playerName = rs.getString("player");
				String intervalName = rs.getString("interval_name");
				String lastRun = rs.getString("last_run");
				LocalDateTime lastRunTime = LocalDateTime.parse(lastRun, LDT_FORMATTER);
				list.add(new DatabaseItem(
					playerName,
					intervalName,
					lastRunTime
				));
			}
		} catch (SQLException e) {
			this.plugin.getLogger().warning("Error fetching player records: " + e.getMessage());
		} finally {
		    if (connection != null) {
		        try {
		        	connection.close();
		        } catch (SQLException e) { /* ignored */}
		    }
		}
		return list;
	}
	
	public void savePlayerRecord(String playerName, String intervalName, String lastRun) {
		// TODO: Batch inserts
		Connection connection = null;
		PreparedStatement pstmt = null;
		String sql;
		try {
			connection = DriverManager.getConnection(this.jdbcConnString);
			sql = "SELECT * FROM player_records WHERE player=? AND interval_name=?";
			PreparedStatement searchPs = connection.prepareStatement(sql);
			searchPs.setString(1, playerName);
			searchPs.setString(2, intervalName);
			ResultSet rs = searchPs.executeQuery();
			if (rs.next() == false) {
				// Record doesn't exist; insert
				sql = "INSERT INTO player_records(player, interval_name, last_run) VALUES(?, ?, ?)";
				pstmt = connection.prepareStatement(sql);
				pstmt.setString(1, playerName);
				pstmt.setString(2, intervalName);
				pstmt.setString(3, lastRun);
			} else {
				// Record already exists; update
				sql = "UPDATE player_records SET last_run=? WHERE player=? AND interval_name=?";
				pstmt = connection.prepareStatement(sql);
				pstmt.setString(1, lastRun);
				pstmt.setString(2, playerName);
				pstmt.setString(3, intervalName);
			}

			// Use sqlite's REPLACE
			pstmt.executeUpdate();
		} catch (SQLException e) {
			this.plugin.getLogger().warning("Error adding player records: " + e.getMessage());
		} finally {
		    if (connection != null) {
		        try {
		        	connection.close();
		        } catch (SQLException e) { /* ignored */}
		    }
		    if (pstmt != null) {
		        try {
		        	pstmt.close();
		        } catch (SQLException e) { /* ignored */}
		    }
		}
	}
}
