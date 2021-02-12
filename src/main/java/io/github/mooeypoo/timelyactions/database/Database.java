package io.github.mooeypoo.timelyactions.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.plugin.java.JavaPlugin;

public class Database {
	protected static DateTimeFormatter LDT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	protected JavaPlugin plugin;
	protected String jdbcConnString;
	protected String filename;

	public Database(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	protected void setFilename(String filename) {
		this.filename = filename;
		this.jdbcConnString = "jdbc:sqlite:"+ this.plugin.getDataFolder() +"/" + this.filename;
	}

	/**
	 * Initialize tables
	 */
	protected void initializeTables(String createTableSql, String createIndexSql) {
		if (this.filename == null) {
			return;
		}

		Connection connection = null;
		try {
			connection = DriverManager.getConnection(this.jdbcConnString);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 seconds

			// Create table if they don't exist
			statement.executeUpdate(createTableSql);
			statement.executeUpdate(createIndexSql);
		} catch (SQLException e) {
			this.plugin.getLogger().warning(String.format("Error initializing database(%s): %s", this.filename, e.getMessage()));
		} finally {
		    if (connection != null) {
		        try {
		        	connection.close();
		        } catch (SQLException e) { /* ignored */}
		    }
		}
	}
	
	public String getStringFromLocalDate(LocalDateTime ldt) {
		return ldt.format(LDT_FORMATTER);
	}

	public LocalDateTime getLocalDateFromString(String str) {
		return LocalDateTime.parse(str, LDT_FORMATTER);
	}
}
