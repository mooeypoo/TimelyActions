package io.github.mooeypoo.timelyactions.database;

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


public class LogDatabase extends Database {

	public LogDatabase(JavaPlugin plugin) {
		super(plugin);
		this.setFilename("logs.db");
	}

	public void initialize() {
		this.initializeTables(
			// Create table
			"CREATE TABLE IF NOT EXISTS `logs` ( "
				+ "`player` VARCHAR(255) NOT NULL , "
				+ "`interval_name` VARCHAR(255) NOT NULL , "
				+ "`run_time` VARCHAR(255) NULL) ",
				""
		);
	}

	public void add(String playerName, String intervalName, LocalDateTime runtime) {
		try (
				Connection connection = DriverManager.getConnection(this.jdbcConnString);
				Statement statement = connection.createStatement()
		) {
			statement.setQueryTimeout(30);  // set timeout to 30 seconds

			// Use sqlite's REPLACE
			String sql = "INSERT INTO logs(player, interval_name, run_time) VALUES(?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(sql);){
				pstmt.setString(1, playerName);
				pstmt.setString(2, intervalName);
				pstmt.setString(3, this.getStringFromLocalDate(runtime));
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			this.plugin.getLogger().warning("Error adding log records: " + e.getMessage());
		}
	}

	public List<LogItem> getLogsForPlayer(String playerName) {
		return this.getLogsForPlayer(playerName, 5);
	}

	public List<LogItem> getLogsForPlayerInterval(String playerName, String interval) {
		return this.getLogsForPlayerInterval(playerName, interval, 5);
	}

	public List<LogItem> getLogsForPlayerInterval(String playerName, String interval, Integer limit) {
		try (Connection connection = DriverManager.getConnection(this.jdbcConnString);) {
			// Use sqlite's REPLACE
			String sql = "SELECT * FROM logs WHERE player=? AND interval_name=? ORDER BY run_time DESC LIMIT " + limit;
			ArrayList<LogItem> list = new ArrayList<>();
			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				pstmt.setString(1, playerName);
				pstmt.setString(2, interval);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					String intervalName = rs.getString("interval_name");
					String lastRun = rs.getString("run_time");
					LocalDateTime runTime = LocalDateTime.parse(lastRun, LDT_FORMATTER);
					list.add(new LogItem(
							playerName,
							intervalName,
							runTime
					));
				}
			}
			return list;
		} catch (SQLException e) {
			this.plugin.getLogger().warning("Error fetching log records: " + e.getMessage());
			return Collections.emptyList();
		}
	}

	public List<LogItem> getLogsForPlayer(String playerName, Integer limit) {
		try (
				Connection connection = DriverManager.getConnection(this.jdbcConnString);
				PreparedStatement pstmt = connection.prepareStatement(
						"SELECT * FROM logs WHERE player=? ORDER BY run_time DESC LIMIT " + limit
				)
		) {
			// Use sqlite's REPLACE
			pstmt.setString(1, playerName);
			try (ResultSet rs = pstmt.executeQuery()) {
				ArrayList<LogItem> list = new ArrayList<>();
				while (rs.next()) {
					String intervalName = rs.getString("interval_name");
					String lastRun = rs.getString("run_time");
					LocalDateTime runTime = LocalDateTime.parse(lastRun, LDT_FORMATTER);
					list.add(new LogItem(
							playerName,
							intervalName,
							runTime
					));
				}
				return list;
			}
		} catch (SQLException e) {
			this.plugin.getLogger().warning("Error fetching log records: " + e.getMessage());
			return Collections.emptyList();
		}
	}
}
