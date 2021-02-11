package io.github.mooeypoo.timelyactions.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;

import io.github.mooeypoo.timelyactions.store.items.DatabaseItem;

public class Database {
	private static DateTimeFormatter LDT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	private JavaPlugin plugin;
	private String jdbcConnString;

	public Database(JavaPlugin plugin) {
		this.plugin = plugin;
		this.jdbcConnString = "jdbc:sqlite:"+ this.plugin.getDataFolder() +"/records.db";
	}

	public void initialize() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(this.jdbcConnString);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 seconds

			// Create table if they don't exist
			statement.executeUpdate(
				"CREATE TABLE IF NOT EXISTS `player_records` ( "
				+ "`player` TEXT NOT NULL , "
				+ "`interval` TEXT NOT NULL , "
				+ "`last_run` TEXT NULL) "
			);
			statement.executeUpdate(
				"CREATE UNIQUE INDEX IF NOT EXISTS idx_player_interval ON player_records(player,interval)"
			);
		} catch (SQLException e) {
			this.plugin.getLogger().warning("Error initializing database: " + e.getMessage());
		} finally {
		    if (connection != null) {
		        try {
		        	connection.close();
		        } catch (SQLException e) { /* ignored */}
		    }
		}
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
				String intervalName = rs.getString("interval");
				String lastRun = rs.getString("last_run");
				LocalDateTime lastRunTime = LocalDateTime.parse(lastRun, LDT_FORMATTER);
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
				String intervalName = rs.getString("interval");
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
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 seconds

			// Use sqlite's REPLACE
			sql = "REPLACE INTO player_records(player, interval, last_run) VALUES(?, ?, ?)";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, playerName);
			pstmt.setString(2, intervalName);
			pstmt.setString(3, lastRun);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			this.plugin.getLogger().warning("Error fetching player records: " + e.getMessage());
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
