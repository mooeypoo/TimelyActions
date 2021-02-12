package io.github.mooeypoo.timelyactions.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

//import java.time.LocalDate;
//import java.time.Month;
//import java.time.format.TextStyle;
//import java.util.Locale;

import org.bukkit.plugin.java.JavaPlugin;


public class LogDatabase extends Database {

	public LogDatabase(JavaPlugin plugin) {
		super(plugin);
		this.setFilename("logs.db");
//		Month month = LocalDate.now().getMonth();
//		this.setFilename(String.format(
//			"logs-%s.db",
//			month.getDisplayName(TextStyle.FULL, Locale.US)
//		));
	}

	public void initialize() {
		this.initializeTables(
			// Create table
			"CREATE TABLE IF NOT EXISTS `logs` ( "
				+ "`player` TEXT NOT NULL , "
				+ "`interval` TEXT NOT NULL , "
				+ "`run_time` TEXT NULL) ",
			// Create unique index
			"CREATE INDEX IF NOT EXISTS "
			+ "idx_player "
			+ "ON player_records(player)"
		);
	}

	public void add(String playerName, String intervalName, LocalDateTime runtime) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		String sql;
		try {
			connection = DriverManager.getConnection(this.jdbcConnString);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 seconds

			// Use sqlite's REPLACE
			sql = "REPLACE INTO logs(player, interval, run_time) VALUES(?, ?, ?)";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, playerName);
			pstmt.setString(2, intervalName);
			pstmt.setString(3, this.getStringFromLocalDate(runtime));
			pstmt.executeUpdate();
		} catch (SQLException e) {
			this.plugin.getLogger().warning("Error adding log records: " + e.getMessage());
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

	public ArrayList<LogItem> getLogsForPlayer(String playerName) {
		return this.getLogsForPlayer(playerName, 5);
	}

	public ArrayList<LogItem> getLogsForPlayerInterval(String playerName, String interval) {
		return this.getLogsForPlayerInterval(playerName, interval, 5);
	}

	public ArrayList<LogItem> getLogsForPlayerInterval(String playerName, String interval, Integer limit) {
		ArrayList<LogItem> list = new ArrayList<LogItem>();
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = DriverManager.getConnection(this.jdbcConnString);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 seconds

			// Use sqlite's REPLACE
			String sql = "GET * FROM logs WHERE player=? AND interval=? ORDER BY run_time DESC LIMIT=?";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, playerName);
			pstmt.setString(1, interval);
			pstmt.setInt(2, limit);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String intervalName = rs.getString("interval");
				String lastRun = rs.getString("last_run");
				LocalDateTime runTime = LocalDateTime.parse(lastRun, LDT_FORMATTER);
				list.add(new LogItem(
					playerName,
					intervalName,
					runTime
				));
			}
		} catch (SQLException e) {
			this.plugin.getLogger().warning("Error fetching log records: " + e.getMessage());
		} finally {
		    if (connection != null) {
		        try {
		        	connection.close();
		        } catch (SQLException e) { /* ignored */}
		    }
		}
		return list;
	}

	public ArrayList<LogItem> getLogsForPlayer(String playerName, Integer limit) {
		ArrayList<LogItem> list = new ArrayList<LogItem>();
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = DriverManager.getConnection(this.jdbcConnString);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 seconds

			// Use sqlite's REPLACE
			String sql = "GET * FROM logs WHERE player=? ORDER BY run_time DESC LIMIT=?";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, playerName);
			pstmt.setInt(2, limit);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String intervalName = rs.getString("interval");
				String lastRun = rs.getString("last_run");
				LocalDateTime runTime = LocalDateTime.parse(lastRun, LDT_FORMATTER);
				list.add(new LogItem(
					playerName,
					intervalName,
					runTime
				));
			}
		} catch (SQLException e) {
			this.plugin.getLogger().warning("Error fetching log records: " + e.getMessage());
		} finally {
		    if (connection != null) {
		        try {
		        	connection.close();
		        } catch (SQLException e) { /* ignored */}
		    }
		}
		return list;
	}
}
