package io.github.mooeypoo.timelyactions.database;

import static java.util.Collections.emptyList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		try (
				Connection connection = DriverManager.getConnection(this.jdbcConnString);
			 	Statement statement = connection.createStatement()
		) {
			statement.setQueryTimeout(30);  // set timeout to 30 seconds

			// Create table if they don't exist
			String sql = "SELECT * FROM player_records WHERE player=?";
			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				pstmt.setString(1, playerName);
				try (ResultSet rs = pstmt.executeQuery()) {
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
				}
			}
		} catch (SQLException e) {
			this.plugin.getLogger().warning("Error fetching player records: " + e.getMessage());
		}
		return null;
	}

	public List<DatabaseItem> getAllPlayerRecords() {
		try (Connection connection = DriverManager.getConnection(this.jdbcConnString);) {

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 seconds

			ArrayList<DatabaseItem> list = new ArrayList<>();
			// Create table if they don't exist
			try (ResultSet rs = statement.executeQuery("SELECT * FROM player_records")) {
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
			}
			return list;
		} catch (SQLException e) {
			this.plugin.getLogger().warning("Error fetching player records: " + e.getMessage());
			return emptyList();
		}
	}
	
	public void savePlayerRecord(String playerName, String intervalName, String lastRun) {
		// TODO: Batch inserts
		try (Connection connection = DriverManager.getConnection(this.jdbcConnString)) {

			try (PreparedStatement searchPs = connection.prepareStatement("SELECT * FROM player_records WHERE player=? AND interval_name=?")) {
				searchPs.setString(1, playerName);
				searchPs.setString(2, intervalName);
				try (ResultSet rs = searchPs.executeQuery()) {
					if (!rs.next()) {
						// Record doesn't exist; insert
						try (PreparedStatement pstmt = connection.prepareStatement(
								"INSERT INTO player_records(player, interval_name, last_run) VALUES(?, ?, ?)"))
						{
							pstmt.setString(1, playerName);
							pstmt.setString(2, intervalName);
							pstmt.setString(3, lastRun);
							// Use sqlite's REPLACE
							pstmt.executeUpdate();
						}
					} else {
						// Record already exists; update
						try (PreparedStatement pstmt = connection.prepareStatement(
								"UPDATE player_records SET last_run=? WHERE player=? AND interval_name=?")) {
							pstmt.setString(1, lastRun);
							pstmt.setString(2, playerName);
							pstmt.setString(3, intervalName);
							// Use sqlite's REPLACE
							pstmt.executeUpdate();
						}
					}
				}

			}
		} catch (SQLException e) {
			this.plugin.getLogger().warning("Error adding player records: " + e.getMessage());
		}
	}
}
